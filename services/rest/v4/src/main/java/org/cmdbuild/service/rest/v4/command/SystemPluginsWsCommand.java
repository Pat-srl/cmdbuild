/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import jakarta.activation.DataSource;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.cmdbuild.service.rest.v4.wshelpers.SystemPluginHelper;
import org.cmdbuild.systemplugin.SystemPlugin;
import org.cmdbuild.systemplugin.SystemPluginService;
import org.cmdbuild.systemplugin.UploadSystemPluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author ataboga
 */
@Component
public class SystemPluginsWsCommand {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SystemPluginService pluginService;
    private final UploadSystemPluginService uploadPluginService;
    private final SystemPluginHelper pluginHelper;

    public SystemPluginsWsCommand(SystemPluginService pluginService, UploadSystemPluginService uploadPluginService, SystemPluginHelper pluginHelper) {
        this.pluginService = checkNotNull(pluginService);
        this.uploadPluginService = checkNotNull(uploadPluginService);
        this.pluginHelper = checkNotNull(pluginHelper);
    }

    public List<SystemPlugin> readAll() {
        return pluginService.getSystemPlugins();
    }

    public SystemPlugin read(String pluginCode) {
        return pluginService.getSystemPlugin(pluginCode);
    }

    public void deploy(List<Attachment> parts) {
        List<DataSource> dataFiles = list(parts).map(a -> newDataSource(() -> a.getDataHandler().getInputStream(), a.getContentType().toString(), a.getContentDisposition().getFilename()));
        uploadPluginService.deploySystemPlugins(dataFiles);
    }

    public void applyPatches(String pluginCode) {
        pluginHelper.applyPatches(pluginService.getSystemPlugin(pluginCode));
    }
}
