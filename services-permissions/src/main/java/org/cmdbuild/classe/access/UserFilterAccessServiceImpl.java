package org.cmdbuild.classe.access;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import java.util.Set;
import static org.cmdbuild.auth.role.RolePrivilege.RP_CQL_ALL;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.ecql.EcqlId;
import static org.cmdbuild.ecql.EcqlSource.EMBEDDED;
import org.cmdbuild.ecql.EcqlSourceUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserFilterAccessServiceImpl implements UserFilterAccessService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final OperationUserSupplier user;
    private final DaoService dao;
    private final CoreConfiguration configuration;

    private final Holder<Set<String>> whitelist;

    public UserFilterAccessServiceImpl(OperationUserSupplier user, DaoService dao, CoreConfiguration configuration, CacheService cacheService) {
        this.user = checkNotNull(user);
        this.dao = checkNotNull(dao);
        this.configuration = checkNotNull(configuration);
        whitelist = cacheService.newHolder("user_filter_access_cql_whitelist_cache");
    }

    @Override
    public void checkUserFilterAccess(CmdbFilter filter) {
        if (filter.hasEcqlFilter()) {
            EcqlId ecqlId = EcqlSourceUtils.parseEcqlId(filter.getEcqlFilter().getEcqlId());
            if (ecqlId.hasSource(EMBEDDED)) {
                logger.debug("detected unsafe embedded ecql");
                checkUnsafeCql(ecqlId.getOnlyId());
            }
        }
        if (filter.hasCqlFilter()) {
            checkUnsafeCql(filter.getCqlFilter().getCqlExpression());
        }
    }

    private void checkUnsafeCql(String cqlExpr) {
        switch (configuration.getCqlSecurity()) {
            case CS_ALLOW ->
                logger.debug("allow unsafe cql =< {} > (cql security disabled)", cqlExpr);
            case CS_DENY ->
                throw runtime("access denied: invalid unsafe cql =< %s > (cql access disabled)", cqlExpr);
            case CS_RESTRICT -> {
                checkArgument(user.hasPrivileges(p -> p.hasPrivileges(RP_CQL_ALL)) || whitelist.get(this::doGetCqlWhitelist).contains(cqlExpr), "access denied: invalid unsafe cql =< %s >", cqlExpr);
            }
        }
    }

    private Set<String> doGetCqlWhitelist() {
        return set(dao.selectAll().from("_Templates").whereExpr("(\"Meta\"->>'cm_cql_whitelist')::boolean = TRUE").getCards().stream().map(c -> c.getString("Template")).collect(toImmutableSet()))
                .with(dao.selectAll().from("_SysParam").whereExpr("(\"Meta\"->>'cm_cql_whitelist')::boolean = TRUE").getCards().stream().map(c -> c.getString("Value")).collect(toImmutableSet()))
                .immutable();
    }

}
