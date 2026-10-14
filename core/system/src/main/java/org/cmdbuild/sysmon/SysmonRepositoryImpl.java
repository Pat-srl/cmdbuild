/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.sysmon;

import static com.google.common.base.Preconditions.checkNotNull;
import java.time.ZonedDateTime;
import java.util.List;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.GT;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;
import org.springframework.stereotype.Component;

@Component
public class SysmonRepositoryImpl implements SysmonRepository {

    private final DaoService dao;

    public SysmonRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public void store(SystemStatusLog systemStatusRecord) {
        dao.createOnly(systemStatusRecord);
    }

    @Override
    public List<SystemStatusLog> getLastRecords(int limit) {
        return dao.selectAll().from(SystemStatusLog.class).orderBy(ATTR_BEGINDATE, DESC).limit(limit).asList();
    }

    @Override
    public List<SystemStatusLog> getLastNodeRecords(String node, int limit) {
        return dao.selectAll().from(SystemStatusLog.class).where("Hostname", EQ, node).orderBy(ATTR_BEGINDATE, DESC).limit(limit).asList();
    }

    @Override
    public List<SystemStatusLog> getRecordsSince(String node, ZonedDateTime since) {
        return dao.selectAll().from(SystemStatusLog.class)
                .accept(q -> {
                    if (node != null) {
                        q.where("Hostname", EQ, node);
                    }
                })
                .where(ATTR_BEGINDATE, GT, since).orderBy(ATTR_BEGINDATE).asList();
    }

}
