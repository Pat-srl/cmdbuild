package org.cmdbuild.dao.config.inner;

import com.google.common.eventbus.EventBus;
import java.util.List;
import jakarta.annotation.Nullable;

/**
 * system events: {@link AllPatchAppliedAndDatabaseReadyEvent} and
 * {@link PatchAppliedOnDbEvent}
 */
public interface PatchService {

    final static String DEFAULT_CATEGORY = "core";

    void applyPendingPatchesAndFunctions();

    void applyPendingPatchesAndFunctionsUpTo(String lastPatch);

    void rebuildPatchesHash();

    boolean hasPendingPatchesOrFunctions();

    /**
     * get all available patches (for all categories). Whithin each category,
     * patches are ordered by version asc. Category ordering is by name, with
     * core first.
     */
    List<Patch> getAvailableCorePatches();

    EventBus getEventBus();

    List<PatchInfo> getAllPatches();

    @Nullable
    String getLastPatchOnDbKeyOrNull();

    default boolean isUpdated() {
        return !hasPendingPatchesOrFunctions();
    }

}
