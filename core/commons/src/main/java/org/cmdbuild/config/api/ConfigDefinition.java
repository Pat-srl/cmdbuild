/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.config.api.ConfigDefinition.ModularConfigDefinition.MCD_MODULE;
import static org.cmdbuild.config.api.ConfigDefinition.ModularConfigDefinition.MCD_OWNER;
import static org.cmdbuild.config.api.ConfigLocation.CL_DEFAULT;
import static org.cmdbuild.config.api.ConfigLocation.CL_FILE_ONLY;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface ConfigDefinition {

    String getKey();

    @Nullable
    String getDefaultValue();

    @Nullable
    String getEnumValues();

    String getDescription();

    boolean isProtected();

    boolean isExperimental();

    ConfigLocation getLocation();

    ConfigCategory getCategory();

    @Nullable
    String getModuleNamespace();

    ModularConfigDefinition getModular();

    default String getModulePrefix() {
        return getModulePrefix(getModuleNamespace());
    }

    default String getModuleType() {
        return getModuleType(getModuleNamespace());
    }

    default String getModuleSuffix() {
        Matcher matcher = Pattern.compile(format("^%s[.](.+)$", Pattern.quote(checkNotBlank(getModuleNamespace())))).matcher(checkNotBlank(getKey()));
        checkArgument(matcher.matches());
        return checkNotBlank(matcher.group(1));
    }

    default boolean isModule() {
        return equal(getModular(), MCD_MODULE);
    }

    default boolean isModuleOwner() {
        return equal(getModular(), MCD_OWNER);
    }

    default boolean isLocationFileOnly() {
        return equal(getLocation(), CL_FILE_ONLY);
    }

    default boolean isLocationDefault() {
        return equal(getLocation(), CL_DEFAULT);
    }

    default boolean hasDescription() {
        return isNotBlank(getDescription());
    }

    enum ModularConfigDefinition {
        MCD_OWNER, MCD_MODULE, MCD_NONE
    }

    static String getModulePrefix(String namespace) {
        Matcher matcher = Pattern.compile("^(.+)[.]([^.]+)$").matcher(checkNotBlank(namespace));
        checkArgument(matcher.matches());
        return checkNotBlank(matcher.group(1));
    }

    static String getModuleType(String namespace) {
        Matcher matcher = Pattern.compile("^(.+)[.]([^.]+)$").matcher(checkNotBlank(namespace));
        checkArgument(matcher.matches());
        return checkNotBlank(matcher.group(2));
    }
}
