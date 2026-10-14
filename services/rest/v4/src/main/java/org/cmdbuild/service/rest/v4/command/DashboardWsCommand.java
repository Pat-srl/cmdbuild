/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import org.cmdbuild.dashboard.DashboardData;
import org.cmdbuild.dashboard.DashboardService;
import org.cmdbuild.service.rest.v4.model.WsDashboardData;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Component
public class DashboardWsCommand {

    private final DashboardService dashboardService;

    public DashboardWsCommand(DashboardService dashboardService) {
        this.dashboardService = checkNotNull(dashboardService);
    }

    public DashboardData doReadOne(String idOrCode, Function<String, DashboardData> function) {
        return function.apply(idOrCode);
    }

    public List<DashboardData> doGetAll(Supplier<List<DashboardData>> funtion) {
        return funtion.get();
    }

    public DashboardData doCreate(WsDashboardData data) {
        DashboardData dashboard = data.toDashboard().build();
        return dashboardService.create(dashboard);
    }

    public DashboardData doUpdate(Long id, WsDashboardData data) {
        DashboardData dashboard = data.toDashboard().withId(id).build();
        return dashboardService.update(dashboard);
    }

    public void doDelete(Long id) {
        dashboardService.delete(id);
    }
}
