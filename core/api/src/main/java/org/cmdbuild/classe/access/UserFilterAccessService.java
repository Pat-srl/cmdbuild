package org.cmdbuild.classe.access;

import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.data.filter.CmdbFilter;

public interface UserFilterAccessService {

    void checkUserFilterAccess(CmdbFilter filter);

    default void checkUserFilterAccess(DaoQueryOptions query) {
        checkUserFilterAccess(query.getFilter());
    }

}
