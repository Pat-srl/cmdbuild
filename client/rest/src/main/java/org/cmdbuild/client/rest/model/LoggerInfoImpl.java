package org.cmdbuild.client.rest.model;

import org.cmdbuild.utils.lang.CmPreconditions;

public class LoggerInfoImpl implements LoggerInfo {

    private final String category;
    private final String level;

    public LoggerInfoImpl(String category, String level) {
        this.category = CmPreconditions.trimAndCheckNotBlank(category);
        this.level = CmPreconditions.trimAndCheckNotBlank(level);
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
    public String toString() {
        return "SimpleLogger{" + "category=" + category + ", level=" + level + '}';
    }

}
