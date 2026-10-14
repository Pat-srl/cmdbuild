/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import java.nio.charset.Charset;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.preferences")
public class PreferencesConfigurationImpl implements PreferencesConfiguration {

    @ConfigValue(key = "preferredOfficeSuite", description = "preferred office suite (`msoffice` or `default`)", defaultValue = "default")
    private PreferredOfficeSuite preferredOfficeSuite;

    @ConfigValue(key = "preferredFileCharset", description = "preferred file charset", defaultValue = "UTF-8")
    private String preferredFileCharset;

    @ConfigValue(key = "preferredCsvSeparator", description = "preferred csv separator", defaultValue = ";")
    private String preferredCsvSeparator;

    @Override
    public PreferredOfficeSuite getPreferredOfficeSuite() {
        return checkNotNull(preferredOfficeSuite);
    }

    @Override
    public String getPreferredFileCharset() {
        return Charset.forName(checkNotBlank(preferredFileCharset)).name();
    }

    @Override
    public String getPreferredCsvSeparator() {
        return checkNotNull(emptyToNull(preferredCsvSeparator));
    }
}
