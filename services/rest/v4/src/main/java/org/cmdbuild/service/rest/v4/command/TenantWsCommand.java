/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.auth.multitenant.api.MultitenantService;
import org.cmdbuild.auth.multitenant.api.TenantInfo;
import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.services.rest.v4.config.WsTenantConfig;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 * @author ldare
 */
@Component
public class TenantWsCommand {

    private final MultitenantConfiguration multitenantConfiguration;
    private final MultitenantService multitenantService;
    private final OperationUserSupplier operationUserSupplier;
    private final CoreConfiguration coreConfiguration;

    public TenantWsCommand(MultitenantConfiguration multitenantConfiguration, MultitenantService multitenantService, OperationUserSupplier operationUserSupplier,CoreConfiguration coreConfiguration) {
        this.multitenantConfiguration = checkNotNull(multitenantConfiguration);
        this.multitenantService = checkNotNull(multitenantService);
        this.operationUserSupplier = checkNotNull(operationUserSupplier);
        this.coreConfiguration = checkNotNull(coreConfiguration);
    }

    public List<TenantInfo> doGetAll() {
        checkArgument(multitenantConfiguration.isMultitenantEnabled(), "multitenant is not enabled");
        return list(multitenantService.getAllActiveTenants()).filter(t -> operationUserSupplier.getUser().getLoginUser().getAvailableTenantContext().getAvailableTenantIds().contains(t.getId()));
    }

    public void doConfigureMultitenant(WsTenantConfig configData) {
        checkCanEdit();
        switch (configData.multitenantMode) {
            case MTM_DISABLED -> multitenantService.disableMultitenant();
            case MTM_CMDBUILD_CLASS -> multitenantService.enableMultitenantClassMode(configData.tenantClass);
            case MTM_DB_FUNCTION -> multitenantService.enableMultitenantFunctionMode();
            default ->
                    throw new IllegalArgumentException("unsupported multitenant mode = " + configData.multitenantMode);
        }
    }

    private void checkCanEdit() { //TODO duplicate code from system config
        checkArgument(coreConfiguration.allowConfigUpdateViaWs(), "CM_CUSTOM_EXCEPTION: system configuration update is disabled for this instance (demo mode)");//TODO check message
    }
}
