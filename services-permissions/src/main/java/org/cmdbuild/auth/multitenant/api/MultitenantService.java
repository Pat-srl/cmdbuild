/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.multitenant.api;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext;
import org.cmdbuild.auth.user.LoginUser;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public interface MultitenantService {

    boolean isEnabled();

    UserTenantContext buildUserTenantContext(UserAvailableTenantContext availableTenantContext, @Nullable TenantLoginData tenantLoginData);

    UserAvailableTenantContext getAvailableTenantContextForUser(@Nullable Long userId);

    UserAvailableTenantContext getAdminAvailableTenantContext();

    String getUserTenantIdSqlExpr(String idExpr);

    /**
     * return tenant descriptions for supplied tenants (as a tenantId ->
     * tenantDescription map);
     *
     * TODO: check/handle translation/localization of descriptions
     *
     * WARNING: this method is not guaranteed to return a valid description for
     * each supplied tenant; caller should check and handle the case where there
     * is no valid description for its tenant. In case of missing description,
     * the returned map will not contain the affected tenant id.
     *
     * @param availableTenants
     * @return map of tenantId/tenantDescription
     */
    Map<Long, String> getTenantDescriptions(Iterable<Long> availableTenants);

    Set<Long> getAllActiveTenantIds();

    List<TenantInfo> getAllActiveTenants();

    List<TenantInfo> getAvailableUserTenants(UserAvailableTenantContext tenantContext);

    void enableMultitenantFunctionMode();

    void enableMultitenantClassMode(String tenantClass);

    boolean isUserTenantUpdateEnabled();

    void setUserTenants(long userId, List<Long> tenantIds);

    void disableMultitenant();

    default UserTenantContext buildUserTenantContext(LoginUser authUser, @Nullable TenantLoginData tenantLoginData) {
        return buildUserTenantContext(authUser.getAvailableTenantContext(), tenantLoginData);
    }

    default UserTenantContext buildAdminTenantContext(@Nullable TenantLoginData tenantLoginData) {
        return buildUserTenantContext(getAdminAvailableTenantContext(), tenantLoginData);
    }

    default String getTenantDescription(long tenantId) {
        return checkNotNull(getTenantDescriptions(list(tenantId)).get(tenantId));
    }
}
