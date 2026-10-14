package org.cmdbuild.homepage;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;

public interface HomepageService {

    List<HomepageData> getAll();

    HomepageData getForCurrentUser();

    HomepageData getActiveForCurrentUser();

    HomepageData getOne(long id);

    HomepageData getOneForUser(long id);

    HomepageData getByCode(String code);

    HomepageData getForUserByName(String code);

    HomepageData create(HomepageData data);

    HomepageData update(HomepageData data);

    void delete(long id);

    boolean isActiveAndAccessibleByCode(String code);

    default HomepageData getByIdOrCode(String idOrCode) {
        if (NumberUtils.isCreatable(idOrCode)) {
            return getOne(toLong(idOrCode));
        } else {
            return getByCode(idOrCode);
        }
    }

    default HomepageData getForUserByIdOrCode(String idOrCode) {
        if (NumberUtils.isCreatable(idOrCode)) {
            return getOneForUser(toLong(idOrCode));
        } else {
            return getForUserByName(idOrCode);
        }
    }

}
