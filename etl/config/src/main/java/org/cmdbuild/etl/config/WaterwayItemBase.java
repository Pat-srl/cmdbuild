package org.cmdbuild.etl.config;

import jakarta.annotation.Nullable;

public interface WaterwayItemBase {

    String getCode();

    String getDescription();

    String getNotes();

    WaterwayItemType getType();

    @Nullable
    String getSubtype();

    boolean isEnabled();
}
