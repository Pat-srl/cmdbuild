package org.cmdbuild.auth.user;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.toOptional;
import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.config.UserRepositoryConfig;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.multitenant.UserAvailableTenantContextImpl;
import org.cmdbuild.auth.multitenant.api.MultitenantService;
import org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext;
import org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext.TenantActivationPrivileges;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RolePrivilege;
import org.cmdbuild.auth.role.RoleRepository;
import static org.cmdbuild.auth.user.UserData.USER_ATTR_EMAIL;
import static org.cmdbuild.auth.user.UserData.USER_ATTR_USERNAME;
import org.cmdbuild.auth.userrole.UserRole;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.isPaged;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.common.utils.PositionOf;
import org.cmdbuild.config.CoreConfiguration;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ_CASE_INSENSITIVE;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.postgres.utils.SqlQueryUtils;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import static org.cmdbuild.dao.utils.PositionOfUtils.buildPositionOf;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import static org.cmdbuild.data.filter.beans.CmdbFilterImpl.falseFilter;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_MULTIGROUP;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_MULTITENANT_ACTIVATION_PRIVILEGES;
import org.cmdbuild.userconfig.UserConfigService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class UserRepositoryImpl implements UserFilteredRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final OperationUserSupplier operationUser;
    private final UserRepositoryConfig configuration;
    private final CoreConfiguration coreConfig;
    private final DaoService dao;
    private final MultitenantService multitenantService;
    private final RoleRepository groupRepository;
    private final UserConfigService userConfigService;

    private final CmCache<Optional<UserData>> userDataByUsername;
    private final CmCache<List<UserData>> userDataByRoleId;

    public UserRepositoryImpl(CacheService cacheService, OperationUserSupplier operationUser, UserRepositoryConfig configuration, CoreConfiguration coreConfig, DaoService dao, MultitenantService multitenantService, RoleRepository groupRepository, UserConfigService userConfigService) {
        this.operationUser = checkNotNull(operationUser);
        this.configuration = checkNotNull(configuration);
        this.coreConfig = checkNotNull(coreConfig);
        this.dao = checkNotNull(dao);
        this.multitenantService = checkNotNull(multitenantService);
        this.groupRepository = checkNotNull(groupRepository);
        this.userConfigService = checkNotNull(userConfigService);
        userDataByUsername = cacheService.newCache("user_data_by_username");
        userDataByRoleId = cacheService.newCache("user_data_by_roleid");
    }

    private void invalidateCache() {
        userDataByUsername.invalidateAll();
        userDataByRoleId.invalidateAll();
    }

    @Override
    public LoginUser getActiveValidUserOrNull(LoginUserIdentity login) {
        UserData userCard = getActiveUserDataOrNull(login);
        if (userCard == null) {
            return null;
        } else {
            LoginUser user = buildUserFromCard(userCard);
            checkArgument(!user.getRoleInfos().isEmpty(), "invalid login user =< %s > : this user has no valid groups", user.getUsername());
            return user;
        }
    }

    @Override
    public LoginUser getUserByIdOrNull(Long userId) {
        UserData user = dao.selectAll().from(UserData.class).where(ATTR_ID, EQ, userId).getOne();
        return buildUserFromCard(user);
    }

    @Override
    public PagedElements<UserData> getMany(DaoQueryOptions queryOptions) {
        CmdbFilter filter = queryOptions.getFilter();
        CmdbSorter sorter = queryOptions.getSorter();
        long offset = queryOptions.getOffset();

        PositionOf positionOf = null;
        if (queryOptions.hasPositionOf()) {
            Long rowNumber = dao.selectRowNumber().where(ATTR_ID, EQ, queryOptions.getPositionOf()).then()
                    .from(UserData.class)
                    .orderBy(sorter)
                    .where(filter)
                    .accept(addTenantFilter())
                    .build().getRowNumberOrNull();
            positionOf = buildPositionOf(rowNumber, queryOptions);
            offset = positionOf.getActualOffset();
        }

        List<UserData> users = dao.selectAll()
                .from(UserData.class)
                .orderBy(sorter)
                .where(filter)
                .accept(addTenantFilter())
                .paginate(offset, queryOptions.getLimit())
                .asList();

        long total;
        if (isPaged(offset, queryOptions.getLimit())) {
            total = dao.selectCount()
                    .from(UserData.class)
                    .where(filter)
                    .accept(addTenantFilter())
                    .getCount();
        } else {
            total = users.size();
        }
        return new PagedElements<>(users, total, positionOf);
    }

    @Override
    public boolean currentUserCanModify(UserData user) {
        return currentUserCanModify(user, user.hasId() ? list(groupRepository.getActiveUserGroups(user.getId())).map(UserRole::getRole) : emptyList(), user.hasId() ? multitenantService.getAvailableTenantContextForUser(user.getId()).getAvailableTenantIds() : emptyList());
    }

    @Override
    public boolean currentUserCanModify(UserData user, Collection<Role> roles, Collection<Long> tenants) {
        return operationUser.hasPrivileges(p -> p.hasPrivileges(RolePrivilege.RP_ADMIN_ALL)
                || (p.hasPrivileges(RolePrivilege.RP_ADMIN_USERS_MODIFY)
                && !equal(operationUser.getUser().getId(), user.getId()) && operationUser.getUser().getRolePrivileges().containsAll(UserPrivilegesImpl.builder().withGroups(roles).build().getRolePrivileges())
                && (operationUser.ignoreTenantPolicies() || operationUser.getUser().getUserTenantContext().getActiveTenantIds().containsAll(tenants))));
    }

    @Override
    public boolean currentUserCanAddUsersToRole(Role role) {
        return operationUser.hasPrivileges(p -> p.hasPrivileges(RolePrivilege.RP_ADMIN_ALL)
                || (p.hasPrivileges(RolePrivilege.RP_ADMIN_USERS_MODIFY)
                && operationUser.getUser().getRolePrivileges().containsAll(UserPrivilegesImpl.builder().withGroups(role).build().getRolePrivileges())));
    }

    @Override
    public PagedElements<UserData> getAllWithoutRole(long roleId, CmdbFilter filter, CmdbSorter sorter, @Nullable Long offset, @Nullable Long limit) {
        return getAllWithRole(roleId, filter, sorter, offset, limit, false);
    }

    @Override
    public PagedElements<UserData> getAllWithRole(long roleId, CmdbFilter filter, CmdbSorter sorter, @Nullable Long offset, @Nullable Long limit) {
        return getAllWithRole(roleId, filter, sorter, offset, limit, true);
    }

    @Override
    public UserData getUserDataById(long id) {
        return dao.getById(UserData.class, id);
    }

    @Override
    @Nullable
    public UserData getUserDataByUsernameOrNull(String username) {
        return userDataByUsername.get(checkNotBlank(username), () -> Optional.ofNullable(dao.selectAll().from(UserData.class).where(USER_ATTR_USERNAME, EQ, username).getOneOrNull())).orElse(null);
    }

    @Override
    @Nullable
    public List<UserData> getUserDataByRoleId(long roleId) {
        return userDataByRoleId.get(checkNotBlank(roleId), () -> getAllWithRole(roleId));
    }

    @Override
    public UserData create(UserData user) {
        user = dao.create(user);
        invalidateCache();
        return user;
    }

    @Override
    public UserData update(UserData user) {
        if (equal(user.getPassword(), BLANK_PASSWORD)) {
            user = UserDataImpl.copyOf(user)
                    .withPassword("")
                    .build();
        } else if (isBlank(user.getPassword())) {
            UserData current = getUserDataById(user.getId());
            user = UserDataImpl.copyOf(user)
                    .withPassword(current.getPassword())
                    .build();
        }
        user = dao.update(user);
        invalidateCache();
        return user;
    }

    @Nullable
    @Override
    public UserData getActiveUserDataOrNull(LoginUserIdentity login) {
        String attribute = getLoginAttribute(login);
        UserData user = dao.selectAll().from(UserData.class)
                .where("Active", EQ, true)
                .where(attribute, configuration.isCaseInsensitive() ? EQ_CASE_INSENSITIVE : EQ, login.getValue())
                .getOneOrNull();
        logger.debug("search active user with login attribute {} = < {} > (case sensitive = {}), found = {}", attribute, login.getValue(), !configuration.isCaseInsensitive(), user);
        return user;
    }

    @Override
    public UserData getUserDataOrNull(LoginUserIdentity login) {
        String attribute = getLoginAttribute(login);
        return dao.selectAll().from(UserData.class)
                .where(attribute, configuration.isCaseInsensitive() ? EQ_CASE_INSENSITIVE : EQ, login.getValue())
                .getOneOrNull();
    }

    private Consumer<QueryBuilder> addTenantFilter() {
        return q -> {
            if (multitenantService.isEnabled() && operationUser.hasMultitenant()) {
                if (operationUser.getUser().hasAnyTenant()) {
                    q.whereExpr(format("(SELECT array_agg(tenant_id) FROM (%s) x) && %s::bigint[]", multitenantService.getUserTenantIdSqlExpr(format("Q3_MASTER.%s", quoteSqlIdentifier(ATTR_ID))), SqlQueryUtils.systemToSqlExpr(operationUser.getUser().getActiveTenantIds())));
                } else {
                    q.where(falseFilter());
                }
            }
        };
    }

    private PagedElements<UserData> getAllWithRole(long roleId, CmdbFilter filter, CmdbSorter sorter, @Nullable Long offset, @Nullable Long limit, boolean assigned) {
        String query = "EXISTS (SELECT * FROM \"Map_UserRole\" _mur WHERE _mur.\"IdObj1\" = Q3_MASTER.\"Id\" AND _mur.\"IdObj2\" = ? AND _mur.\"Status\" = 'A')";
        if (assigned == false) {
            query = "NOT " + query;
        }
        List<UserData> list = dao.selectAll().from(UserData.class).where(filter)
                .whereExpr(query, roleId)
                .accept(addTenantFilter())
                .orderBy(sorter).paginate(offset, limit).asList();
        if (isPaged(offset, limit)) {
            long count = dao.selectCount().from(UserData.class).where(filter)
                    .whereExpr(query, roleId)
                    .accept(addTenantFilter())
                    .getCount();
            return paged(list, count);
        } else {
            return paged(list);
        }
    }

    private LoginUser buildUserFromCard(UserData user) {
        logger.trace("build user from data = {}", user);
        List<UserRole> groups = groupRepository.getActiveUserGroups(user.getId());
        String defaultGroupName = groups.stream().filter(UserRole::isDefault).collect(toOptional()).map(UserRole::getRole).map(Role::getName).orElse(null);
        Map<String, String> userConfig = userConfigService.getByUsername(user.getUsername());
        UserAvailableTenantContext userAvailableTenantContext = multitenantService.getAvailableTenantContextForUser(user.getId());
        if (isNotBlank(userConfig.get(USER_CONFIG_MULTITENANT_ACTIVATION_PRIVILEGES))) {
            userAvailableTenantContext = UserAvailableTenantContextImpl.copyOf(userAvailableTenantContext).withTenantActivationPrivileges(parseEnum(userConfig.get(USER_CONFIG_MULTITENANT_ACTIVATION_PRIVILEGES), TenantActivationPrivileges.class)).build();
        }
        return LoginUserImpl.builder()
                .withId(user.getId())
                .withUsername(user.getUsername())
                .withEmail(defaultString(user.getEmail()))
                .withDescription(defaultString(user.getDescription()))
                .withDefaultGroupName(defaultGroupName)
                .withActiveStatus(user.isActive())
                .withServiceStatus(user.isService())
                .withMultigroupEnabled(toBooleanOrDefault(userConfig.get(USER_CONFIG_MULTIGROUP), coreConfig.enableMultigroupByDefault()))
                .withAvailableTenantContext(userAvailableTenantContext)
                .accept(b -> {
                    groups.stream().map(UserRole::getRole).forEach(b::addGroup);
                })
                .build();
    }

    private String getLoginAttribute(LoginUserIdentity login) {
        return switch (login.getType()) {
            case LT_EMAIL ->
                USER_ATTR_EMAIL;
            case LT_USERNAME ->
                USER_ATTR_USERNAME;
            case LT_AUTO ->
                switch (configuration.getLoginAttributeMode()) {
                    case LAM_AUTO_DETECT_EMAIL -> {
                        if (login.getValue().matches(".+[@].+")) {
                            yield USER_ATTR_EMAIL;
                        } else {
                            yield USER_ATTR_USERNAME;
                        }
                    }
                    case LAM_EMAIL ->
                        USER_ATTR_EMAIL;
                    case LAM_USERNAME ->
                        USER_ATTR_USERNAME;
                };
            default ->
                throw unsupported("unsupported login type = %s", login.getType());
        };
    }

}
