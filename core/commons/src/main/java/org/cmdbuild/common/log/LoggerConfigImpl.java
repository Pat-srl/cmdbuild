/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.log;

import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;

public class LoggerConfigImpl implements LoggerConfig {

    private final String category, description, level, module;

    public LoggerConfigImpl(String category, String level) {
        this(category, null, level);
    }

    public LoggerConfigImpl(String category, @Nullable String description, String level) {
        this(category, description, level, null);
    }

    public LoggerConfigImpl(String category, @Nullable String description, String level, @Nullable String module) {
        this.category = trimAndCheckNotBlank(category);
        this.description = nullToEmpty(description);
        this.level = trimAndCheckNotBlank(level).toUpperCase();
        this.module = trimToNull(module);
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getLevel() {
        return level;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Nullable
    @Override
    public String getModule() {
        return module;
    }

    @Override
    public String toString() {
        return "LoggerConfig{" + "category=" + category + ", level=" + level + '}';
    }

}
