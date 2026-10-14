/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cleanup.inner;

import static com.google.common.collect.MoreCollectors.toOptional;
import static java.lang.String.format;
import java.util.List;
import java.util.Objects;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.cleanup.DatabaseCleanupRulesConfig;
import org.cmdbuild.cleanup.RecordCleanupRule;
import static org.cmdbuild.cleanup.RecordCleanupRule.RecordCleanupRuleMatcher.RM_ELSE;
import static org.cmdbuild.cleanup.RecordCleanupRule.RecordCleanupRuleMatcher.RM_FILTER;
import org.cmdbuild.config.api.ConfigListener;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.postgres.q3.beans.PreparedQueryExt;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import static org.cmdbuild.dao.utils.CmFilterUtils.mergeOrCompose;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.scheduler.ScheduledJob;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_SINGLE_NODE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RecordCleanupHelperServiceImpl {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final DatabaseCleanupRulesConfig config;

    public RecordCleanupHelperServiceImpl(DaoService dao, DatabaseCleanupRulesConfig config) {
        this.dao = checkNotNull(dao);
        this.config = checkNotNull(config);
    }

    @ScheduledJob(value = "0 10 4 * * ?", clusterMode = RUN_ON_SINGLE_NODE)//once per day
    @ConfigListener(DatabaseCleanupRulesConfig.class)
    public void cleanupRecords() {
        logger.info("processing {} cleanup rules", config.getCleanupRules().size());
        config.getCleanupRules().stream().map(RecordCleanupRule::getTarget).distinct().sorted().forEach(t -> {
            logger.debug("processing rules for target =< {} >", t);
            try {
                doCleanupRecords(t, list(config.getCleanupRules()).filter(equal(RecordCleanupRule::getTarget, t)).map(r -> RecordCleanupRuleImpl.copyOf(r).build()));
            } catch (Exception ex) {
                logger.warn(marker(), "error processing cleanup rules for target =< {} >", t, ex);
            }
        });
        logger.debug("record cleanup completed");
    }

    private void doCleanupRecords(String target, List<RecordCleanupRule> rules) {
        RecordCleanupRule elseRule = rules.stream().filter(r -> r.hasMatcher(RM_ELSE)).collect(toOptional()).orElse(null);
        if (elseRule != null) {
            List<RecordCleanupRule> nonElseRules = list(rules).without(elseRule);
            CmdbFilter filter = mergeOrCompose(list(nonElseRules).filter(r -> r.hasMatcher(RM_FILTER)).map(RecordCleanupRule::getFilter));
            rules = list(rules).without(elseRule).with(RecordCleanupRuleImpl.copyOf(elseRule).withFilter(filter).build());
        }
        Classe classe = dao.getAllClasses().stream().filter(c -> c.getName().matches("^_.+") && Objects.equals(c.getName().toLowerCase().replaceAll("[^a-z]", ""), target.toLowerCase().replaceAll("[^a-z]", ""))).collect(onlyElement("invalid cleanup rule target =< %s >", target));
        rules.forEach(rule -> {
            String whereFilter;
            if (!rule.getFilter().isNoop()) {
                PreparedQueryExt query = (PreparedQueryExt) dao.select(ATTR_ID).from(classe).where(rule.getFilter()).includeHistory().build();
                whereFilter = format("\"Id\" IN (SELECT %s FROM (%s) x)", query.getSelectForAttr(ATTR_ID).getAlias(), query.getQuery());
            } else {
                whereFilter = null;
            }
            if (classe.hasHistory()) {
                String whereFilterActive = isBlank(whereFilter) ? "\"Status\" = 'A'" : (whereFilter + " AND \"Status\" = 'A'");
                cleanupRecordsByAgeToKeepSecondsStandard(rule, classe, whereFilterActive);
                cleanupRecordsByMaxRecordsToKeepStandard(rule, classe, whereFilterActive);
                cleanupRecordsByMaxSizeMegsStandard(rule, classe, whereFilterActive);
                whereFilter = isBlank(whereFilter) ? "\"Status\" = 'N'" : (whereFilter + " AND \"Status\" = 'N'");
                cleanupStandardRecords(classe, whereFilter);
            } else {
                cleanupRecordsByAgeToKeepSecondsSimple(rule, classe, whereFilter);
                cleanupRecordsByMaxRecordsToKeepSimple(rule, classe, whereFilter);
                cleanupRecordsByMaxSizeMegsSimple(rule, classe, whereFilter);
            }
        });
    }

    private void cleanupRecordsByAgeToKeepSecondsStandard(RecordCleanupRule rule, Classe classe, String whereFilter) {
        if (isNotNullAndGtZero(rule.getMaxRecordAgeToKeepSeconds())) {
            logger.debug("delete {} records older than {} secs", classe.getName(), rule.getMaxRecordAgeToKeepSeconds());
            String whereExpr = isBlank(whereFilter) ? "" : format(" AND %s", whereFilter),
                    query = format("UPDATE \"%s\" SET \"Status\" = 'N' WHERE \"BeginDate\" < NOW() - interval '%s seconds' %s", classe.getName(), rule.getMaxRecordAgeToKeepSeconds(), whereExpr);
            int res = dao.getJdbcTemplate().update(query);
            if (res > 0) {
                logger.info("deleted {} {} records that where older than {} secs", res, classe.getName(), rule.getMaxRecordAgeToKeepSeconds());
            } else {
                logger.debug("no {} record deleted", classe.getName());
            }
        }
    }

    private void cleanupRecordsByMaxRecordsToKeepStandard(RecordCleanupRule rule, Classe classe, String whereFilter) {
        if (isNotNullAndGtZero(rule.getMaxRecordsToKeep())) {
            logger.debug("delete {} records, keep at most {} records", classe.getName(), rule.getMaxRecordsToKeep());
            long count = dao.selectCount().from(classe).getCount();
            if (count <= rule.getMaxRecordsToKeep()) {
                logger.debug("current {} record count = {}, no need to delete any record", classe.getName(), count);
            } else {
                String whereExpr = isBlank(whereFilter) ? "" : format(" WHERE %s", whereFilter),
                        query = format("UPDATE \"%s\" SET \"Status\" = 'N' WHERE \"Id\" IN (SELECT \"Id\" FROM  \"%s\" %s ORDER BY \"BeginDate\" ASC LIMIT (GREATEST(0, ((SELECT COUNT(*) FROM \"%s\" %s)-%s))))",
                                classe.getName(), classe.getName(), whereExpr, classe.getName(), whereExpr, rule.getMaxRecordsToKeep());
                int res = dao.getJdbcTemplate().update(query);
                logger.info("deleted {} {} records", res, classe.getName());
            }
        }
    }

    private void cleanupRecordsByMaxSizeMegsStandard(RecordCleanupRule rule, Classe classe, String whereFilter) {
        if (isNotNullAndGtZero(rule.getMaxSizeMegs())) {
            long maxSizeBytes = rule.getMaxSizeMegs() * 1024 * 1024;
            logger.debug("delete {} records, keep at most {} bytes", classe.getName(), byteCountToDisplaySize(maxSizeBytes));
            long count = dao.selectCount().from(classe).getCount(), sizeBytes = dao.getJdbcTemplate().queryForObject(format("SELECT pg_total_relation_size(%s)", systemToSqlExpr(classe)), Long.class);
            if (sizeBytes <= maxSizeBytes) {
                logger.debug("current {} table size = {}, no need to delete any record", classe.getName(), byteCountToDisplaySize(sizeBytes));
            } else {
                long toDelete = count - (maxSizeBytes * count * 100 / (sizeBytes * 110));
                String whereExpr = isBlank(whereFilter) ? "" : format(" WHERE %s", whereFilter),
                        query = format("UPDATE \"%s\" SET \"Status\" = 'N' WHERE \"Id\" IN (SELECT \"Id\" FROM  \"%s\" %s ORDER BY \"BeginDate\" ASC LIMIT %s)",
                                classe.getName(), classe.getName(), whereExpr, toDelete);
                int res = dao.getJdbcTemplate().update(query);
                logger.info("deleted {} {} records, final size = {}", res, classe.getName(), byteCountToDisplaySize(dao.getJdbcTemplate().queryForObject(format("SELECT pg_total_relation_size(%s)", systemToSqlExpr(classe)), Long.class)));
            }
        }
    }

    private void cleanupStandardRecords(Classe classe, String whereFilter) {
        int res = dao.getJdbcTemplate().update(format("DELETE FROM \"%s\" WHERE %s", classe.getName(), whereFilter));
        if (res > 0) {
            logger.info("deleted {} {} records (Status = N)", res, classe.getName());
        } else {
            logger.debug("no {} record deleted (Status = N)", classe.getName());
        }
    }

    private void cleanupRecordsByAgeToKeepSecondsSimple(RecordCleanupRule rule, Classe classe, String whereFilter) {
        if (isNotNullAndGtZero(rule.getMaxRecordAgeToKeepSeconds())) {
            logger.debug("cleanup {} records older than {} secs", classe.getName(), rule.getMaxRecordAgeToKeepSeconds());
            String whereExpr = isBlank(whereFilter) ? "" : format(" AND %s", whereFilter),
                    query = format("DELETE FROM \"%s\" WHERE \"BeginDate\" < NOW() - interval '%s seconds' %s", classe.getName(), rule.getMaxRecordAgeToKeepSeconds(), whereExpr);
            int res = dao.getJdbcTemplate().update(query);
            if (res > 0) {
                logger.info("removed {} {} records that where older than {} secs", res, classe.getName(), rule.getMaxRecordAgeToKeepSeconds());
            } else {
                logger.debug("no {} record removed", classe.getName());
            }
        }
    }

    private void cleanupRecordsByMaxRecordsToKeepSimple(RecordCleanupRule rule, Classe classe, String whereFilter) {
        if (isNotNullAndGtZero(rule.getMaxRecordsToKeep())) {
            logger.debug("cleanup {} records, keep at most {} records", classe.getName(), rule.getMaxRecordsToKeep());
            long count = dao.selectCount().from(classe).includeHistory().getCount();
            if (count <= rule.getMaxRecordsToKeep()) {
                logger.debug("current {} record count = {}, no need to remove any record", classe.getName(), count);
            } else {
                String whereExpr = isBlank(whereFilter) ? "" : format(" WHERE %s", whereFilter),
                        query = format("DELETE FROM \"%s\" WHERE \"Id\" IN (SELECT \"Id\" FROM  \"%s\" %s ORDER BY \"BeginDate\" ASC LIMIT (GREATEST(0, ((SELECT COUNT(*) FROM \"%s\" %s)-%s))))",
                                classe.getName(), classe.getName(), whereExpr, classe.getName(), whereExpr, rule.getMaxRecordsToKeep());
                int res = dao.getJdbcTemplate().update(query);
                logger.info("removed {} {} records", res, classe.getName());
            }
        }
    }

    private void cleanupRecordsByMaxSizeMegsSimple(RecordCleanupRule rule, Classe classe, String whereFilter) {
        if (isNotNullAndGtZero(rule.getMaxSizeMegs())) {
            long maxSizeBytes = rule.getMaxSizeMegs() * 1024 * 1024;
            logger.debug("cleanup {} records, keep at most {} bytes", classe.getName(), byteCountToDisplaySize(maxSizeBytes));
            long count = dao.selectCount().from(classe).includeHistory().getCount(), sizeBytes = dao.getJdbcTemplate().queryForObject(format("SELECT pg_total_relation_size(%s)", systemToSqlExpr(classe)), Long.class);
            if (sizeBytes <= maxSizeBytes) {
                logger.debug("current {} table size = {}, no need to remove any record", classe.getName(), byteCountToDisplaySize(sizeBytes));
            } else {
                long toDelete = count - (maxSizeBytes * count * 100 / (sizeBytes * 110));
                String whereExpr = isBlank(whereFilter) ? "" : format(" WHERE %s", whereFilter),
                        query = format("DELETE FROM \"%s\" WHERE \"Id\" IN (SELECT \"Id\" FROM  \"%s\" %s ORDER BY \"BeginDate\" ASC LIMIT %s)",
                                classe.getName(), classe.getName(), whereExpr, toDelete);
                int res = dao.getJdbcTemplate().update(query);
                logger.info("removed {} {} records, final size = {}", res, classe.getName(), byteCountToDisplaySize(dao.getJdbcTemplate().queryForObject(format("SELECT pg_total_relation_size(%s)", systemToSqlExpr(classe)), Long.class)));
            }
        }
    }

}
