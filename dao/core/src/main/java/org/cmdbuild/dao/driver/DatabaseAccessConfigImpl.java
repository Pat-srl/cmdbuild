/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.multitenant.api.UserTenantContext;
import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import org.cmdbuild.auth.user.OperationUser;
import static org.cmdbuild.auth.user.OperationUser.OPERATION_SCOPE;
import static org.cmdbuild.auth.user.OperationUser.USER_ATTR_SESSION_ID;
import org.cmdbuild.common.localization.ContextLanguageHolder;
import static org.cmdbuild.dao.driver.DatabaseAccessUserScope.DA_DEFAULT;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_TWO;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Qualifier(SYSTEM_LEVEL_TWO)
@Primary
public class DatabaseAccessConfigImpl implements DatabaseAccessConfig {

    private final MultitenantConfiguration multitenantConfiguration;
    private final OperationUserSupplier operationUserSupplier;
    private final ContextLanguageHolder contextLanguageHolder;

    public DatabaseAccessConfigImpl(MultitenantConfiguration multitenantConfiguration, OperationUserSupplier operationUserSupplier, ContextLanguageHolder contextLanguageHolder) {
        this.multitenantConfiguration = checkNotNull(multitenantConfiguration);
        this.operationUserSupplier = checkNotNull(operationUserSupplier);
        this.contextLanguageHolder = checkNotNull(contextLanguageHolder);
    }

    @Override
    public MultitenantConfiguration getMultitenantConfiguration() {
        return multitenantConfiguration;
    }

    @Override
    public DatabaseAccessUserContext getUserContext() {
        OperationUser operationUser = operationUserSupplier.getUser();
        UserTenantContext userTenantContext = operationUser.getUserTenantContext();
        return new DatabaseAccessUserContextImpl(
                operationUser.getSponsorUsername(),//TODO check this
                operationUser.getDefaultGroupNameOrNull(),
                operationUser.getParams().get(USER_ATTR_SESSION_ID),
                userTenantContext.ignoreTenantPolicies(),
                userTenantContext.getActiveTenantIds(),
                parseEnumOrDefault(operationUser.getParams().get(OPERATION_SCOPE), DA_DEFAULT),
                contextLanguageHolder.getContextLanguage());
    }

}
