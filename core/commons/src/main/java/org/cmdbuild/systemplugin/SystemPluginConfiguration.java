/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.systemplugin;

import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public interface SystemPluginConfiguration {

    public boolean isVersionCheckEnabled();
}
