package org.cmdbuild.navtree;

import jakarta.annotation.Nullable;

public interface NavTreeInfo {

    String getName();

    String getDescription();

    @Nullable
    String getIconFont();
}
