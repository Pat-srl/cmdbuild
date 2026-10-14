package org.cmdbuild.minions;

import org.cmdbuild.minions.MinionStatus;
import static com.google.common.base.Objects.equal;
import static org.cmdbuild.minions.MinionStatus.MS_DISABLED;
import static org.cmdbuild.minions.MinionStatus.MS_ERROR;
import static org.cmdbuild.minions.MinionStatus.MS_NOTRUNNING;
import static org.cmdbuild.minions.MinionStatus.MS_READY;

public interface MinionComponentCommons {

    String getName();

    default String getDescription() {
        return getName();
    }

    default String getConfigEnabler() {
        return null;
    }

    MinionRuntimeStatus getRuntimeStatus();

    boolean isEnabled();

    default MinionStatus getStatus() {
        return buildStatus(getRuntimeStatus(), isEnabled());
    }

    default boolean isReady() {
        return equal(getStatus(), MS_READY);
    }

    static MinionStatus buildStatus(MinionRuntimeStatus runtimeStatus, boolean enabled) {
        return switch (runtimeStatus) {
            case MRS_READY ->
                MS_READY;
            case MRS_ERROR ->
                MS_ERROR;
            case MRS_NOTRUNNING ->
                enabled ? MS_NOTRUNNING : MS_DISABLED;
        };

    }
}
