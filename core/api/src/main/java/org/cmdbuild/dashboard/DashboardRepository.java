/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dashboard;

import java.util.List;
import org.apache.commons.lang3.math.NumberUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;

public interface DashboardRepository {

    List<DashboardData> getAll();

    DashboardData getDashboardById(long id);

    DashboardData getDashboardByCode(String code);

    DashboardData createDashboard(DashboardData dashboard);

    DashboardData updateDashboard(DashboardData dashboard);

    void deleteDashboard(long id);

    default DashboardData getDashboardByIdOrCode(String idOrCode) {
        if (NumberUtils.isCreatable(idOrCode)) {
            return getDashboardById(toLong(idOrCode));
        } else {
            return getDashboardByCode(idOrCode);
        }
    }
}
