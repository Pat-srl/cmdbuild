/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.systemplugin;

import jakarta.activation.DataSource;
import java.util.Collection;

/**
 *
 * @author ataboga
 */
public interface UploadSystemPluginService {

    void deploySystemPlugins(Collection<DataSource> files);
}
