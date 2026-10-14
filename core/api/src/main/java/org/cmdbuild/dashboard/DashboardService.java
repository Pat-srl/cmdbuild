package org.cmdbuild.dashboard;

import java.util.List;
import org.apache.commons.lang3.math.NumberUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;

public interface DashboardService {

    List<DashboardData> getAll();

    List<DashboardData> getForCurrentUser();

    List<DashboardData> getActiveForCurrentUser();

    DashboardData getOne(long id);

    DashboardData getOneForUser(long id);

    DashboardData getByCode(String code);

    DashboardData getForUserByName(String code);

    DashboardData create(DashboardData data);

    DashboardData update(DashboardData dashboard);

    void delete(long id);

    boolean isActiveAndAccessibleByCode(String code);

    default DashboardData getByIdOrCode(String idOrCode) {
        if (NumberUtils.isCreatable(idOrCode)) {
            return getOne(toLong(idOrCode));
        } else {
            return getByCode(idOrCode);
        }
    }

    default DashboardData getForUserByIdOrCode(String idOrCode) {
        if (NumberUtils.isCreatable(idOrCode)) {
            return getOneForUser(toLong(idOrCode));
        } else {
            return getForUserByName(idOrCode);
        }
    }

}
