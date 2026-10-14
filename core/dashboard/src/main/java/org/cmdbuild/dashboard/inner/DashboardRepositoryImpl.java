/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dashboard.inner;

import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dashboard.DashboardData;
import org.cmdbuild.dashboard.DashboardRepository;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Component
public class DashboardRepositoryImpl implements DashboardRepository {

    private final DaoService dao;

    public DashboardRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public List<DashboardData> getAll() {
        return dao.selectAll().from(DashboardData.class).asList();
    }

    @Override
    public DashboardData getDashboardById(long id) {
        return dao.getById(DashboardData.class, id);
    }

    @Override
    public DashboardData createDashboard(DashboardData dashboard) {
        return dao.create(dashboard);
    }

    @Override
    public DashboardData updateDashboard(DashboardData dashboard) {
        return dao.update(dashboard);
    }

    @Override
    public void deleteDashboard(long id) {
        dao.delete(DashboardData.class, id);
    }

    @Override
    public DashboardData getDashboardByCode(String code) {
        return dao.selectAll().from(DashboardData.class).where(ATTR_CODE, EQ, checkNotBlank(code)).getOne();
    }

}
