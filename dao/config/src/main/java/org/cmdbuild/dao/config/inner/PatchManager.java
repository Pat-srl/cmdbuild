/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import static com.google.common.base.Objects.equal;
import java.util.List;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface PatchManager {

    List<Patch> getAvailableCorePatches();

    String getPatchSourcesChecksum();

    @Nullable
    String getLastPatchOnDbKeyOrNull();

    List<PatchInfo> getAllPatches();

    boolean hasPendingPatches();

    void applyPatchAndStore(Patch patch);

    void rebuildPatchesHash();

    default List<Patch> getPatchesOnDb() {
        return getAllPatches().stream().filter(PatchInfo::hasPatchOnDb).map(PatchInfo::getPatchOnDb).collect(toList());
    }

    void reset();

    default PatchInfo getPatchByVersion(String version) {
        checkNotBlank(version);
        return getAllPatches().stream().filter(p -> equal(p.getVersion(), version)).collect(onlyElement("patch not found for version =< %s >", version));
    }

}
