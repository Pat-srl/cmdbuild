/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.MoreCollectors.toOptional;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import java.util.List;
import org.cmdbuild.common.beans.CardIdAndClassName;
import org.cmdbuild.dao.entrytype.Classe;

public interface BimService extends BimObjectRepository, BimProjectRepository {

    final static String BIM_NAV_TREE = "bimnavigation";

    final static String BIMSERVER_SOURCE_PROJECT_ID = "BIMSERVER_SOURCE_PROJECT_ID";

    boolean isEnabled();

    boolean hasBim(Classe classe);

    DataHandler downloadIfcFile(long projectId, @Nullable String ifcFormat);

    DataHandler downloadXktFile(long projectId);

    BimProject convertIfcProjectToXkt(long projectId);

    BimProject uploadXktFile(long projectId, DataHandler dataHandler, boolean isNewProject);

    BimProjectExt createProjectExt(BimProjectExt bimProject);

    BimProjectExt updateProjectExt(BimProjectExt bimProject);

    BimProjectExt getProjectExt(long projectId);

    List<BimProjectExt> getAllProjectsAndObjects();

    void deleteProject(long id);

    @Nullable
    BimObject getBimObjectForCardOrViaNavTreeOrNull(CardIdAndClassName card);

    default BimProject uploadXktFile(long projectId, DataHandler dataHandler) {
        return uploadXktFile(projectId, dataHandler, true);
    }

    default BimProjectExt createProjectExt(BimProject project, @Nullable CardIdAndClassName card) {
        return createProjectExt(new BimProjectExtImpl(project, card));
    }

    default BimProjectExt updateProjectExt(BimProject project, @Nullable CardIdAndClassName card) {
        return updateProjectExt(new BimProjectExtImpl(project, card));
    }

    BimObject updateBimObject(BimObject bimObject);

    @Nullable
    BimProjectExt getProjectByCodeOrNull(String projectCode);

    @Nullable
    default BimProjectExt getProjectByMasterCardOrNull(CardIdAndClassName card) {
        return getProjectByMasterCardOrNull(card.getId());
    }

    @Nullable
    default BimProjectExt getProjectByMasterCardOrNull(long cardId) {
        return getAllProjectsAndObjects().stream().filter(p -> equal(p.getOwnerIdOrNull(), cardId)).collect(toOptional()).orElse(null);
    }

    default List<BimProjectExt> getProjectsForParent(BimProject parent) {
        return getProjectsForParent(parent.getId());
    }

    default List<BimProjectExt> getProjectsForParent(long parentId) {
        return getAllProjectsAndObjects().stream().filter(p -> equal(p.getParentId(), parentId)).collect(toImmutableList());
    }

}
