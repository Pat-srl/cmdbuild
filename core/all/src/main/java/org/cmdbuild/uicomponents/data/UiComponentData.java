package org.cmdbuild.uicomponents.data;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.cmdbuild.ui.TargetDevice;
import static org.cmdbuild.ui.TargetDevice.TD_DEFAULT;
import static org.cmdbuild.ui.TargetDevice.TD_MOBILE;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;

public interface UiComponentData {

    static final String UI_COMPONENT_TABLE_NAME = "_UiComponent";

    @Nullable
    Long getId();

    boolean getActive();

    String getName();

    String getDescription();

    ZonedDateTime getLastUpdated();

    @Nullable
    byte[] getDataDefault();

    @Nullable
    byte[] getDataMobile();

    UiComponentType getType();

    @Nullable
    String getIconFont();

    default Set<TargetDevice> getTargetDevices() {
        return getData().keySet();
    }

    default Map<TargetDevice, byte[]> getData() {
        return checkNotEmpty((Map) map(TD_DEFAULT, getDataDefault(), TD_MOBILE, getDataMobile()).filterValues(Objects::nonNull));
    }

    default boolean isOfType(UiComponentType type) {
        return equal(getType(), type);
    }

    default byte[] getData(TargetDevice targetDevice) {
        return checkNotNull(getData().get(checkNotNull(targetDevice)), "data not found for target device = %s", targetDevice);
    }

    default boolean getSynthesized() {
        return false;
    }
}
