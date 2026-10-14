/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class BimProjectRepositoryImpl implements BimProjectRepository {

    private final DaoService dao;

    public BimProjectRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public Collection<BimProject> getAllProjects() {
        return dao.selectAll().from(BimProject.class).asList();
    }

    @Override
    public BimProject getProjectById(long id) {
        return dao.getById(BimProject.class, id);
    }

    @Override
    @Nullable
    public BimProjectIfc getProjectIfcByProjectId(String projectId) {
        return dao.selectAll().from(BimProjectIfc.class).where("ProjectId", EQ, projectId).getOneOrNull();
    }

    @Override
    public void deleteProjectIfcByProjectId(String projectId) {
        dao.delete(getProjectIfcByProjectId(projectId));
    }

    @Override
    public void deleteProjectById(long id) {
        dao.delete(BimProject.class, id);
    }

    @Override
    public BimProject createProject(BimProject bimProject) {
        return dao.create(bimProject);
    }

    @Override
    public BimProject updateProject(BimProject bimProject) {
        return dao.update(bimProject);
    }

}
