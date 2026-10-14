/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.lookup;

import jakarta.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.common.beans.LookupValue;

public interface LookupValueData extends LookupValue, LookupConfig {

    String getNotes();

    Integer getIndex();

    boolean isActive();

    @Nullable
    Long getParentId();

    LookupConfig getConfig();

    long getTypeId();

    @Override
    public default Set<String> getDmsAllowedExtensions() {
        return getConfig().getDmsAllowedExtensions();
    }

    @Override
    public default Integer getMaxFileSize() {
        return getConfig().getMaxFileSize();
    }

    @Override
    @Nullable
    public default DmsAttachmentCountCheck getDmsCheckCount() {
        return getConfig().getDmsCheckCount();
    }

    @Override
    @Nullable
    public default Integer getDmsCheckCountNumber() {
        return getConfig().getDmsCheckCountNumber();
    }

    @Override
    @Nullable
    public default String getDmsModelClass() {
        return getConfig().getDmsModelClass();
    }

    @Override
    public default boolean isDefault() {
        return getConfig().isDefault();
    }

    @Override
    public default IconType getIconType() {
        return getConfig().getIconType();
    }

    @Override
    @Nullable
    public default String getTextColor() {
        return getConfig().getTextColor();
    }

    @Override
    @Nullable
    public default String getIconImage() {
        return getConfig().getIconImage();
    }

    @Override
    @Nullable
    public default String getIconFont() {
        return getConfig().getIconFont();
    }

    @Override
    @Nullable
    public default String getIconColor() {
        return getConfig().getIconColor();
    }

    @Override
    @Nullable
    public default boolean isHideText() { return getConfig().isHideText();}

    @Override
    public default Map<String, String> asMap() {
        return getConfig().asMap();
    }

    default boolean hasParent() {
        return getParentId() != null;
    }

}
