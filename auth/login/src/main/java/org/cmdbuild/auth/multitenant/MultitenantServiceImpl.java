/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.multitenant;

import static com.google.common.base.Objects.equal;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import static com.google.common.collect.Streams.stream;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.sql.ResultSet;
import java.util.Collections;
import static java.util.Collections.emptySet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.cmdbuild.auth.multitenant.UserAvailableTenantContextImpl.fullAccess;
import static org.cmdbuild.auth.multitenant.UserAvailableTenantContextImpl.minimalAccess;
import org.cmdbuild.auth.multitenant.api.MultitenantService;
import org.cmdbuild.auth.multitenant.api.TenantInfo;
import org.cmdbuild.auth.multitenant.api.TenantLoginData;
import org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext;
import static org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext.TenantActivationPrivileges.TAP_ONE;
import org.cmdbuild.auth.multitenant.api.UserTenantContext;
import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import static org.cmdbuild.auth.multitenant.config.MultitenantConfiguration.IGNORE_TENANT_POLICIES_TENANT_ID;
import static org.cmdbuild.auth.multitenant.config.MultitenantConfiguration.MULTITENANT_CONFIG_PROPERTY_MODE;
import static org.cmdbuild.auth.multitenant.config.MultitenantConfiguration.MULTITENANT_CONFIG_PROPERTY_TENANT_CLASS;
import static org.cmdbuild.auth.multitenant.config.MultitenantConfiguration.MULTITENANT_CONFIG_PROPERTY_TENANT_DOMAIN;
import static org.cmdbuild.auth.multitenant.config.MultitenantMode.MTM_CMDBUILD_CLASS;
import static org.cmdbuild.auth.multitenant.config.MultitenantMode.MTM_DB_FUNCTION;
import static org.cmdbuild.auth.user.UserData.USER_CLASS_NAME;
import org.cmdbuild.cache.CacheService;
import static org.cmdbuild.common.beans.CardIdAndClassNameImpl.card;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.ConfigurableDataSource;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.entrytype.ClassPermissionMode.CPM_RESERVED;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainDefinitionImpl;
import org.cmdbuild.dao.postgres.utils.RelationDirectionQueryHelper;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_ERROR;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_READY;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class MultitenantServiceImpl implements MultitenantService, MinionComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MultitenantConfiguration config;
    private final JdbcTemplate jdbcTemplate;
    private final GlobalConfigService configService;
    private final DaoService dao;
    private final ConfigurableDataSource dataSource;
    private final CacheService cacheService;

    private final MinionHandler minionHandler;

    public MultitenantServiceImpl(MultitenantConfiguration config, @Qualifier(SYSTEM_LEVEL_ONE) JdbcTemplate jdbcTemplate, GlobalConfigService configService, DaoService dao, ConfigurableDataSource dataSource, CacheService cacheService) {
        this.config = checkNotNull(config);
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
        this.configService = checkNotNull(configService);
        this.dao = checkNotNull(dao);
        this.dataSource = checkNotNull(dataSource);
        this.cacheService = checkNotNull(cacheService);
        this.minionHandler = MinionHandlerImpl.builder()
                .withName("Multitenant")
                .withEnabledChecker(this::isEnabled)
                .withStatusChecker(() -> checkConfig() ? MRS_READY : MRS_ERROR)
                .build();
    }

    @Override
    public void start() {
        if (dataSource.isSuperuser()) {
            logger.error(marker(), "CM: postgres jdbc account has `superuser` privileges: multitenant WILL NOT WORK with this configuration! for row level security and multitenant a regular (non-superuser) postgres account must be used");
        }
    }

    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    @Override
    public boolean isEnabled() {
        return config.isMultitenantEnabled();
    }

    @Override
    public boolean isUserTenantUpdateEnabled() {
        return MTM_CMDBUILD_CLASS.equals(config.getMultitenantMode());
    }

    @Override
    public void setUserTenants(long userId, List<Long> newTenants) {
        logger.debug("set user tenants for userId = {} to tenant list = {}", userId, newTenants);
        checkArgument(isUserTenantUpdateEnabled());
        Set<Long> currentTenants = getUserTenantIds(userId);
        Set<Long> tenantsToAdd = set(newTenants).without(currentTenants),
                tenantsToRemove = set(currentTenants).without(newTenants);

        logger.debug("set user tenants for userId = {}, add tenants = {}, remove tenants = {}", userId, tenantsToAdd, tenantsToRemove);

        Domain tenantDomain = getTenantDomain();
        Classe tenantClass = getTenantClass();

        tenantsToAdd.forEach(t -> dao.createRelation(tenantDomain, card(USER_CLASS_NAME, userId), tenantClass.isSuperclass() ? dao.getCard(tenantClass, t) : card(tenantClass.getName(), t))); //TODO improve this (relation query)
        tenantsToRemove.forEach(t -> dao.delete(dao.getRelation(tenantDomain.getName(), userId, t)));
    }

    @Override
    public UserTenantContext buildUserTenantContext(UserAvailableTenantContext availableTenantContext, @Nullable TenantLoginData tenantLoginData) {
        Set<Long> availableTenantIds = availableTenantContext.getAvailableTenantIds(),
                activeTenantIds;//(tenantLoginData == null || tenantLoginData.getActiveTenants() == null) ? availableTenantIds : Sets.intersection(availableTenantIds, tenantLoginData.getActiveTenants());

        if (tenantLoginData == null || tenantLoginData.getActiveTenants() == null) {
            activeTenantIds = availableTenantIds;
        } else {
            activeTenantIds = Sets.intersection(availableTenantIds, tenantLoginData.getActiveTenants());
        }

        if (availableTenantIds.size() == 1 && equal(availableTenantContext.getTenantActivationPrivileges(), TAP_ONE)) {
            activeTenantIds = availableTenantIds;
        } else if (activeTenantIds.size() > 1 && equal(availableTenantContext.getTenantActivationPrivileges(), TAP_ONE)) {
            activeTenantIds = set();
        }

        boolean ignoreTenantPolicies;
        if (availableTenantContext.ignoreTenantPolicies()) {
            if (tenantLoginData == null || tenantLoginData.ignoreTenantPolicies() == null) {
                ignoreTenantPolicies = config.tenantAdminIgnoresTenantByDefault();
            } else {
                ignoreTenantPolicies = tenantLoginData.ignoreTenantPolicies();
            }
        } else {
            ignoreTenantPolicies = false;
        }
        Long defaultTenant = Optional.fromNullable(tenantLoginData == null ? null : tenantLoginData.getDefaultTenant())
                .or(Optional.fromNullable(availableTenantContext.getDefaultTenantId()))
                .or(Optional.fromNullable(activeTenantIds.size() == 1 ? Iterables.getOnlyElement(activeTenantIds) : null))
                .orNull();
        UserTenantContext userTenantContext = new UserTenantContextImpl(ignoreTenantPolicies, activeTenantIds, defaultTenant);
        if (equal(availableTenantContext.getTenantActivationPrivileges(), TAP_ONE)) {
            checkArgument(userTenantContext.getActiveTenantIds().size() <= 1, "cannot activate more than one tenant: permission denied");
        }
        return userTenantContext;
    }

    @Override
    public UserAvailableTenantContext getAvailableTenantContextForUser(Long userId) {
        logger.debug("get tenant for user id = {}", userId);
        checkNotNull(userId);
        logger.debug("multitenant mode = {}", config.getMultitenantMode());
        try {
            Set<Long> tenantIds = getUserTenantIds(checkNotNull(userId));
            logger.debug("tenant ids = {}", tenantIds);
            boolean ignoreTenantPolicies = tenantIds.contains(IGNORE_TENANT_POLICIES_TENANT_ID);
            if (ignoreTenantPolicies) {
                tenantIds = getAllActiveTenantIds();//admin see all active tenants as available
            }
            return UserAvailableTenantContextImpl.builder().withAvailableTenantIds(tenantIds).withIgnoreTenantPolicies(ignoreTenantPolicies).build();
        } catch (Exception ex) {
            logger.error(marker(), "unable to retrieve tenant context for user = {}", userId, ex);
            return minimalAccess();
        }
    }

    private boolean checkConfig() {
        try {
            getUserTenantIds(-1l);
            checkArgument(dataSource.isNotSuperuser(), "jdbc account =< %s > has `superuser` privileges: this is incompatible with multi tenant", dataSource.getDatabaseUser());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public UserAvailableTenantContext getAdminAvailableTenantContext() {
        try {
            return UserAvailableTenantContextImpl.builder().withAvailableTenantIds(getAllActiveTenantIds()).withIgnoreTenantPolicies(true).build();
        } catch (Exception ex) {
            logger.error(marker(), "unable to retrieve tenant context for admin", ex);
            return fullAccess();
        }
    }

    @Override
    public Set<Long> getAllActiveTenantIds() {
        return getUserTenantIds(null);
    }

    @Override
    public void enableMultitenantFunctionMode() {
        if (!equal(config.getMultitenantMode(), MTM_DB_FUNCTION)) {
            checkArgument(config.isMultitenantDisabled(), "cannot change multitenant mode: operation not allowed");
            configService.putString(MULTITENANT_CONFIG_PROPERTY_MODE, serializeEnum(MTM_DB_FUNCTION).toUpperCase());
        }
    }

    @Override
    public void enableMultitenantClassMode(String tenantClassId) { //TODO make this method transactional
        if (equal(config.getMultitenantMode(), MTM_CMDBUILD_CLASS)) {
            checkArgument(equal(tenantClassId, config.getTenantClass()), "cannot change tenant class: operation not allowed");
            logger.warn(marker(), "CM: multitenant mode is already enabled with tenant class = {}", tenantClassId);
        } else {
            checkArgument(config.isMultitenantDisabled(), "cannot change multitenant mode: operation not allowed");
            checkArgument(dataSource.isNotSuperuser(), "CM: postgres is configured with a 'superuser' account; for row level security and multitenant to work a regular (non-superuser) user account is required");

            Classe tenantClass = dao.getClasse(tenantClassId),
                    userClass = dao.getClasse(USER_CLASS_NAME);
            Domain tenantDomain = dao.createDomain(DomainDefinitionImpl.builder()
                    .withSourceClass(userClass)
                    .withTargetClass(tenantClass)
                    .withName(format("%s%s", userClass.getName(), tenantClass.getName()))
                    .withMetadata(DomainMetadataImpl.builder()
                            .withMode(CPM_RESERVED)
                            .withCardinality("N:N")//TODO
                            .withDirectDescription("belongs to tenant")
                            .withInverseDescription("has tenant user")
                            .build())
                    .build());
            String tenantDomainId = tenantDomain.getName();

            dao.getJdbcTemplate().queryForObject("SELECT _cm3_multitenant_tenant_class_trigger_install(_cm3_utils_name_to_regclass(?))", Object.class, tenantClass.getName());

            configService.putStrings(map(MULTITENANT_CONFIG_PROPERTY_MODE, serializeEnum(MTM_CMDBUILD_CLASS).toUpperCase(),
                    MULTITENANT_CONFIG_PROPERTY_TENANT_CLASS, tenantClassId,
                    MULTITENANT_CONFIG_PROPERTY_TENANT_DOMAIN, tenantDomainId
            ));
        }
    }

    @Override
    public void disableMultitenant() {
        if (isEnabled()) {
            dao.getJdbcTemplate().execute("SELECT _cm3_multitenant_disable()");
            cacheService.invalidateAll();//TODO improve this (?)
            configService.reload();//TODO improve this (?)
        }
    }

    @Override
    public String getUserTenantIdSqlExpr(String idExpr) {
        checkNotBlank(idExpr);
        return switch (config.getMultitenantMode()) {
            case MTM_CMDBUILD_CLASS -> {
                Classe tenantClass = getTenantClass();
                Domain tenantDomain = getTenantDomain();
                RelationDirectionQueryHelper helper = RelationDirectionQueryHelper.forDomain(tenantDomain.getThisDomainForTargetClass(tenantClass));
                logger.debug("build tenant query for tenant class = {} domain = {}", tenantClass, tenantDomain);
                yield format("SELECT m.%s tenant_id FROM %s m WHERE m.\"Status\" = 'A' AND m.%s = %s", helper.getTargetCardIdExpr(), entryTypeToSqlExpr(tenantDomain), helper.getSourceCardIdExpr(), idExpr);
            }
            case MTM_DB_FUNCTION -> {
                String functionName = trimAndCheckNotBlank(config.getDbFunction(), "multitenant db function name cannot be null");
                checkArgument(functionName.matches("[a-z0-9_]+"), "unsupported multitenant function name syntax %s (must match /^[a-z0-9_]+$/)", functionName);
                logger.debug("querying tenant function = {} ", functionName);
                yield format("SELECT %s(%s) tenant_id", functionName, idExpr);//TODO check this (alias)
            }
            default ->
                throw unsupported("unsupported multitenant mode = %s", config.getMultitenantMode());
        };
    }

    /**
     *
     * @param userId if null, return all active ids
     * @return
     */
    private Set<Long> getUserTenantIds(@Nullable Long userId) {
        return switch (config.getMultitenantMode()) {
            case MTM_DISABLED ->
                emptySet(); //disabled, do nothing
            case MTM_CMDBUILD_CLASS -> {
                if (userId == null) {
                    yield ImmutableSet.copyOf(jdbcTemplate.queryForList(format("SELECT \"Id\" FROM %s WHERE \"Status\" = 'A'", entryTypeToSqlExpr(getTenantClass())), Long.class));
                } else {
                    yield ImmutableSet.copyOf(jdbcTemplate.queryForList(getUserTenantIdSqlExpr(systemToSqlExpr(userId)), Long.class));
                }
            }
            case MTM_DB_FUNCTION ->
                ImmutableSet.copyOf(jdbcTemplate.queryForList(getUserTenantIdSqlExpr(systemToSqlExpr(firstNonNull(userId, -1l))), Long.class));
        };
    }

    @Override
    public Map<Long, String> getTenantDescriptions(Iterable<Long> tenantIds) {
        logger.debug("get tenant descriptions for tenants = {}", tenantIds);
        if (Iterables.isEmpty(tenantIds)) {
            return Collections.emptyMap();
        } else {
            Map<Long, String> map = map();
            jdbcTemplate.query(format("SELECT \"Id\",\"Description\",\"Code\" FROM \"Class\" WHERE \"Id\" IN (%s)", stream(tenantIds).map((t) -> "?").collect(joining(","))), (ResultSet rs) -> { //TODO translation
                map.put(rs.getLong("Id"), firstNotBlankOrEmpty(rs.getString("Description"), rs.getString("Code")));
            }, list(tenantIds).toArray());
            return map;
        }
    }

    @Override
    public List<TenantInfo> getAllActiveTenants() {
        Set<Long> tenantIds = getAllActiveTenantIds();
        Map<Long, String> tenantDescriptions = getTenantDescriptions(tenantIds);
        return tenantIds.stream().map((id) -> new TenantInfoImpl(id, tenantDescriptions.get(id))).sorted(Ordering.natural().onResultOf(TenantInfo::getDescription)).collect(toList());
    }

    @Override
    public List<TenantInfo> getAvailableUserTenants(UserAvailableTenantContext tenantContext) {
        return getAllActiveTenants().stream().filter((t) -> tenantContext.getAvailableTenantIds().contains(t.getId())).collect(toList());
    }

    private Classe getTenantClass() {
        checkArgument(MTM_CMDBUILD_CLASS.equals(config.getMultitenantMode()));
        return dao.getClasse(trimAndCheckNotBlank(config.getTenantClass()));
    }

    private Domain getTenantDomain() {
        checkArgument(MTM_CMDBUILD_CLASS.equals(config.getMultitenantMode()));
        return dao.getDomain(trimAndCheckNotBlank(config.getTenantDomain()));
    }

    private static class TenantInfoImpl implements TenantInfo {

        private final long id;
        private final String description;

        public TenantInfoImpl(Long id, String description) {
            this.id = id;
            this.description = description;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public String getDescription() {
            return description;
        }

    }

}
