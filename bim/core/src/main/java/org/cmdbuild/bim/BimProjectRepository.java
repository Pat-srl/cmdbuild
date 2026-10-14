/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import java.util.Collection;
import jakarta.annotation.Nullable;

public interface BimProjectRepository {

    Collection<BimProject> getAllProjects();

    BimProject getProjectById(long id);

    @Nullable
    BimProjectIfc getProjectIfcByProjectId(String projectId);
    
    void deleteProjectIfcByProjectId(String projectId);

    void deleteProjectById(long id);

    BimProject createProject(BimProject bimProject);

    BimProject updateProject(BimProject bimProject);

}
