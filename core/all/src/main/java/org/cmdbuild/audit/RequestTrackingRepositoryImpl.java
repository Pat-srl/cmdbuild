/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.audit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.reverse;
import static java.lang.String.format;
import java.time.ZonedDateTime;
import java.util.List;
import static org.cmdbuild.utils.date.CmDateUtils.toSqlTimestamp;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.NOT_MATCHES_REGEXP;
import static org.cmdbuild.data.filter.SorterElementDirection.ASC;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class RequestTrackingRepositoryImpl implements RequestTrackingWritableRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final JdbcTemplate jdbcTemplate;

    public RequestTrackingRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
        jdbcTemplate = dao.getJdbcTemplate();
    }

    @Override
    public void cleanupRequestTableForMaxAge(int maxRecordAgeToKeepSeconds) {
        logger.debug("cleanup request tracking records older than {} secs", maxRecordAgeToKeepSeconds);
        int res = jdbcTemplate.update(format("DELETE FROM \"_Request\" WHERE \"Timestamp\" < NOW() - interval '%s seconds'", maxRecordAgeToKeepSeconds));
        if (res > 0) {
            logger.info("removed {} request tracking records that where older than {} secs", res, maxRecordAgeToKeepSeconds);
        } else {
            logger.debug("no request tracking record removed");
        }
    }

    @Override
    public void cleanupRequestTableForMaxRecords(int maxRecordsToKeep) {
        logger.debug("cleanup request tracking records, max records to keep = {}", maxRecordsToKeep);
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM \"_Request\"", Integer.class);
        if (count <= maxRecordsToKeep) {
            logger.debug("current record count = {}, skip cleanup", count);
        } else {
            int res = jdbcTemplate.update("DELETE FROM \"_Request\" WHERE \"Id\" IN (SELECT \"Id\" FROM  \"_Request\" ORDER BY \"Timestamp\" ASC LIMIT ((SELECT COUNT(*) FROM \"_Request\")-?))", maxRecordsToKeep);
            logger.info("removed {} records", res);
        }
    }

    @Override
    public void dropAll() {
        logger.info("drop add request tracking data from db");
        int res = jdbcTemplate.update("TRUNCATE TABLE \"_Request\"");
        logger.info("removed {} records", res);
    }

    @Override
    public List<RequestInfo> getRequests(ZonedDateTime from, ZonedDateTime to) {
        throw new UnsupportedOperationException("TODO"); // TODO
    }

    @Override
    public List<RequestInfo> getRequestsSince(ZonedDateTime from) {
        logger.debug("get request since = {}", from);
        return dao.selectAll().from(RequestDataImpl.class)
                .whereExpr("\"Timestamp\" > ?", toSqlTimestamp(from))
                .orderBy("Timestamp", ASC)
                .asList(RequestInfo.class);
    }

    @Override
    public List<RequestInfo> getLastRequests(long limit) {
        List<RequestInfo> list = dao.selectAll().from(RequestDataImpl.class)
                .orderBy("Timestamp", DESC)
                .limit(limit)
                .asList(RequestInfo.class);
        return list(reverse(list));
    }

    @Override
    public List<RequestInfo> getErrorsSince(ZonedDateTime from) {
        return dao.selectAll().from(RequestDataImpl.class)
                .whereExpr("\"Timestamp\" > ?", toSqlTimestamp(from))
                .where("StatusCode", NOT_MATCHES_REGEXP, "^2..")
                .orderBy("Timestamp", ASC)
                .asList(RequestInfo.class);
    }

    @Override
    public List<RequestInfo> getLastErrors(long limit) {
        List<RequestInfo> list = dao.selectAll().from(RequestDataImpl.class)
                .where("StatusCode", NOT_MATCHES_REGEXP, "^2..")
                .orderBy("Timestamp", DESC)
                .limit(limit)
                .asList(RequestInfo.class);
        return list(reverse(list));
    }

    @Override
    public RequestData getRequest(String requestId) {
        logger.debug("get request for requestId = {}", requestId);
        checkNotBlank(requestId);
        return dao.selectAll().from(RequestDataImpl.class).whereExpr("\"RequestId\" ILIKE ?", requestId + "%").getOne();
    }

    @Override
    public void create(RequestData data) {
        dao.createOnly(data);
    }

    @Override
    public void update(RequestData data) {
        RequestData current = dao.selectAll().from(RequestDataImpl.class).where("RequestId", EQ, data.getRequestId()).getOneOrNull();
        if (current == null) {//this may happen douring a reconfigure/importdb operation
            dao.createOnly(data);
        } else {
            data = RequestDataImpl.copyOf(data).withId(current.getId()).build();
            dao.updateOnly(data);
        }
    }

}
