/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.uicomponents;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.transform;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.data.UiComponentType;

public interface UiComponentInfo {

    long getId();

    String getName();

    boolean isActive();

    String getDescription();

    ZonedDateTime getLastUpdated();

    String getExtjsComponentId();

    String getExtjsAlias();

    UiComponentType getType();

    List<UiComponentVersionInfo> getVersions();

    String getIconFont();

    default Set<TargetDevice> getTargetDevices() {
        return EnumSet.copyOf(transform(getVersions(), UiComponentVersionInfo::getTargetDevice));
    }

    default boolean isOfType(UiComponentType type) {
        return equal(getType(), type);
    }

    default boolean allowsTargetDevice(TargetDevice targetDevice) {
        return getTargetDevices().contains(targetDevice);
    }

}
