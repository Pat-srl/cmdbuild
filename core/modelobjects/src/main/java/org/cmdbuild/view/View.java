package org.cmdbuild.view;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import org.cmdbuild.view.join.JoinViewConfig;

public interface View extends ViewBase {

    @Nullable
    String getSourceClass();

    @Nullable
    String getSourceFunction();

    @Nullable
    String getFilter();

    @Nullable
    JoinViewConfig getJoinConfig();

    default JoinViewConfig getJoinConfigNotNull() {
        return checkNotNull(getJoinConfig());
    }

}
