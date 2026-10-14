/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.easyupload.EasyuploadItem;
import org.cmdbuild.easyupload.EasyuploadService;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class ResourcesWsCommand {

    private final EasyuploadService easyuploadService;
    private final CoreConfiguration coreConfiguration;

    public ResourcesWsCommand(EasyuploadService easyuploadService, CoreConfiguration coreConfiguration) {
        this.easyuploadService = checkNotNull(easyuploadService);
        this.coreConfiguration = checkNotNull(coreConfiguration);
    }

    public EasyuploadItem doDownloadCompanyLogo() {
        return easyuploadService.getById(coreConfiguration.getCompanyLogoUploadsId());
    }
}
