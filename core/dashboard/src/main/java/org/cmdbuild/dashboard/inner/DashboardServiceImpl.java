/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dashboard.inner;

import org.cmdbuild.auth.role.RolePrivilege;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.dashboard.DashboardData;
import org.cmdbuild.dashboard.DashboardRepository;
import org.cmdbuild.dashboard.DashboardService;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

@Component
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository repository;
    private final OperationUserSupplier userStore;

    public DashboardServiceImpl(DashboardRepository repository, OperationUserSupplier userStore, CacheService cacheService) {
        this.repository = checkNotNull(repository);
        this.userStore = checkNotNull(userStore);
    }

    @Override
    public List<DashboardData> getAll() {
        return repository.getAll();
    }

    @Override
    public List<DashboardData> getForCurrentUser() {
        return getAll().stream().filter(d -> canRead(d)).collect(toList());
    }

    @Override
    public List<DashboardData> getActiveForCurrentUser() {
        return getForCurrentUser().stream().filter(DashboardData::isActive).collect(toList());
    }

    @Override
    public DashboardData getOne(long id) {
        return repository.getDashboardById(id);
    }

    @Override
    public DashboardData getOneForUser(long id) {
        DashboardData dashboard = getOne(id);
        checkArgument(canRead(dashboard), "unable to access dashboard with id = %s: permission denied", id);
        return dashboard;
    }

    @Override
    public DashboardData update(DashboardData dashboard) {
        return repository.updateDashboard(dashboard);
    }

    @Override
    public void delete(long id) {
        repository.deleteDashboard(id);
    }

    @Override
    public boolean isActiveAndAccessibleByCode(String code) {
        DashboardData dashboard = repository.getDashboardByCode(code);
        return dashboard.isActive() && canRead(dashboard);
    }

    @Override
    public DashboardData getByCode(String code) {
        return repository.getDashboardByCode(code);
    }

    @Override
    public DashboardData getForUserByName(String code) {
        DashboardData dashboard = getByCode(code);
        checkArgument(canRead(dashboard), "unable to access dashboard = %s: permission denied", code);
        return dashboard;
    }

    @Override
    public DashboardData create(DashboardData data) {
        return repository.createDashboard(data);
    }

    private boolean canRead(DashboardData dash) {
        return userStore.hasPrivileges(p -> p.hasPrivileges(RolePrivilege.RP_DATA_ALL_READ) || p.hasReadAccess(dash));
    }

}
