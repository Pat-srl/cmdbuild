package org.cmdbuild.dao.postgres.services;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import jakarta.annotation.Nullable;
import jakarta.inject.Provider;
import static java.lang.String.format;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.DatabaseRecord;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS2;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS_A;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS_D;
import static org.cmdbuild.dao.constants.SystemAttributes.SYSTEM_ATTRIBUTES_NEVER_INSERTED;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_CREATE;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_UPDATE;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.postgres.utils.SqlQueryUtils;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.classNameToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.lookup.LookupRepository;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.stereotype.Component;

@Component
public class EntryUpdateServiceImpl implements EntryUpdateService, EntryUpdateHelperService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Provider<LookupRepository> lookupHelper; //TODO this is ok but not great; if possible refactor lookup store, avoid this lazy dependency here
    private final JdbcTemplate jdbcTemplate;

    public EntryUpdateServiceImpl(JdbcTemplate jdbcTemplate, Provider<LookupRepository> lookupHelper) {
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
        this.lookupHelper = checkNotNull(lookupHelper);
    }

    @Override
    public long executeInsertAndReturnKey(DatabaseRecord entry) {
        return getOnlyElement(new EntryUpdateExecutor(entry).executeInsert());
    }

    @Override
    public void executeUpdate(DatabaseRecord entry) {
        new EntryUpdateExecutor(entry).executeUpdate();
    }

    @Override
    public String buildUpdateExpr(EntryType type, Map<String, Object> values) {
        return new EntryUpdateExecutor(CardImpl.buildCard(type.asClasse(), values)).buildUpdateExpr();
    }

    @Override
    public List<Long> executeBatchInsertAndReturnKeys(List<DatabaseRecord> records) {
        return new EntryUpdateExecutor(records).executeInsert();
    }

    private class EntryUpdateExecutor {

        protected final List<DatabaseRecord> entries;

        protected final List<AttrAndValue> attrs = list();

        protected EntryUpdateExecutor(Iterable<DatabaseRecord> entries) {
            this.entries = ImmutableList.copyOf(entries);
        }

        protected EntryUpdateExecutor(DatabaseRecord entry) {
            this.entries = ImmutableList.of(entry);
        }

        public List<Long> executeInsert() {
            checkArgument(!entries.isEmpty());
            if (entries.size() == 1) {
                try {
                    return jdbcTemplate.execute((StatementCallback<List<Long>>) (statement) -> singletonList(doExecuteInsert(statement, getOnlyElement(entries))));
                } catch (DataIntegrityViolationException ex) {
                    throw new DaoException(ex);
                } catch (Exception ex) {
                    throw new DaoException(ex, "error executing insert for entry = %s", getOnlyElement(entries));
                }
            } else {
                try {
                    return jdbcTemplate.execute((ConnectionCallback<List<Long>>) (connection) -> {
                        connection.setAutoCommit(false);//TODO check this (manual commit/rollback etc)
                        try {
                            List<Long> ids;
                            try (Statement statement = connection.createStatement()) {
                                ids = entries.stream().map(rethrowFunction(entry -> doExecuteInsert(statement, entry))).collect(toImmutableList());
                            }
                            connection.commit();
                            return ids;
                        } catch (Exception ex) {
                            connection.rollback();
                            connection.setAutoCommit(true);
                            throw ex;
                        }
                    });
                } catch (Exception ex) {
                    throw new DaoException(ex, "error executing batch insert of %s entries", entries.size());
                }
            }
        }

        private long doExecuteInsert(Statement statement, DatabaseRecord entry) throws SQLException {
            prepareValues(entry, false);
            String query;
            if (attrs.isEmpty()) {
                query = format("INSERT INTO %s DEFAULT VALUES RETURNING \"Id\"", entryTypeToSqlExpr(entry.getType()));
            } else {
                query = format("INSERT INTO %s (%s) VALUES (%s) RETURNING \"Id\"",
                        entryTypeToSqlExpr(entry.getType()),
                        attrs.stream().map(AttrAndValue::getAttr).map(SqlQueryUtils::quoteSqlIdentifier).collect(joining(", ")),
                        attrs.stream().map(AttrAndValue::getValue).collect(joining(", ")));
            }
            try (ResultSet resultSet = statement.executeQuery(query)) {
                checkArgument(resultSet.next(), "insert query fail to return attr id");
                return checkNotNullAndGtZero(resultSet.getLong(ATTR_ID), "insert query returned invalid attr id");
            }
        }

        public String buildUpdateExpr() {
            prepareValues(getOnlyElement(entries), true);
            return doBuildUpdateExpr();
        }

        public void executeUpdate() {
            entries.forEach(entry -> {
                try {
                    prepareValues(entry, true);
                    if (attrs.isEmpty()) {
                        logger.warn("attrs is empty, skipping update");
                    } else {
                        String sql = format("UPDATE %s SET %s WHERE %s = %s",
                                entryTypeToSqlExpr(entry.getType()),
                                doBuildUpdateExpr(),
                                quoteSqlIdentifier(ATTR_ID), checkNotNull(entry.getId()));
                        jdbcTemplate.update(sql);
                    }
                } catch (DataIntegrityViolationException ex) {
                    throw new DaoException(ex);
                } catch (Exception ex) {
                    throw new DaoException(ex, "error executing update for entry = %s", entry);
                }
            });
        }

        private String doBuildUpdateExpr() {
            return attrs.stream().map((a) -> format("%s = %s", a.getAttr(), a.getValue())).collect(joining(", "));
        }

        private void prepareValues(DatabaseRecord entry, boolean forUpdate) {
            attrs.clear();
            entry.getAllValuesAsMap().forEach((key, rawValue) -> {
                Attribute attribute = entry.getType().getAttributeOrNull(key);
                if (attribute == null) {
                    logger.debug("attribute not found or reserved for type = {} attr = {}, will not save value on db", entry.getType(), key);
                } else {
                    try {
                        if (!attribute.isActive()) {
                            logger.debug("attribute = {} is not active, will not save value on db", entry.getType(), key);
                        } else if (equal(attribute.getName(), ATTR_STATUS)) {
                            if (equal(rawValue, ATTR_STATUS_D)) {
                                addValue(ATTR_STATUS, format("'%s'", ATTR_STATUS_D));
                            } else if (equal(rawValue, ATTR_STATUS_A)) {
                                addValue(ATTR_STATUS, format("'%s'", ATTR_STATUS_A));
                            }
                        } else if ((!attribute.hasCorePermission(AP_UPDATE) && forUpdate) || (!attribute.hasCorePermission(AP_CREATE) && !forUpdate)) {
                            logger.debug("ignore new value for immutable/inactive attribute = {} val = {}", key, rawValue);
                        } else if (SYSTEM_ATTRIBUTES_NEVER_INSERTED.contains(attribute.getName()) || attribute.isVirtual()) {
                            //skip system attrs and virtual attrs
                        } else {
                            addValue(attribute.getName(), attributeValueToSqlExpr(attribute, rawValue));
//							String marker = "?";
//							String cast = getSystemToSqlCastOrNull(attribute.getType());
//							if (isNotBlank(cast)) {
//								marker += "::" + cast;
//							}
//							addValue(attribute.getName(), sqlValue, marker);
                        }
                    } catch (Exception ex) {
                        throw new DaoException(ex, "error preparing value for attribute = %s.%s", attribute.getOwner().getName(), attribute.getName());
                    }
                }
            });

            if (entry instanceof CMRelation relation) {
                relation = relation.getRelationDirect();
                addValue(ATTR_IDOBJ1, relation.getSourceId().toString());
                addValue(ATTR_IDCLASS1, classNameToSqlExpr(relation.getSourceCard().getClassName()));
                addValue(ATTR_IDOBJ2, relation.getTargetId().toString());
                addValue(ATTR_IDCLASS2, classNameToSqlExpr(relation.getTargetCard().getClassName()));
            }
        }

        private String attributeValueToSqlExpr(Attribute attribute, @Nullable Object value) {
            CardAttributeType attributeType = attribute.getType();
            switch (attributeType.getName()) {
                case LOOKUP -> {
                    if (value instanceof IdAndDescription idAndDescription) {
                        Long id = idAndDescription.getId();
                        String code = idAndDescription.getCode();
                        String type = ((LookupAttributeType) attributeType).getLookupTypeName();
                        if (isNullOrLtEqZero(id) && isBlank(code)) {
                            return systemToSqlExpr(null);
                        } else {
                            String codeToTry = code;
                            if (isNotNullAndGtZero(id) && lookupHelper.get().hasLookupWithTypeAndId(type, id)) {
                                return systemToSqlExpr(id);
                            } else {
                                codeToTry = firstNotBlank(codeToTry, String.valueOf(id));
                            }
                            if (isNotBlank(codeToTry)) {
                                if (lookupHelper.get().hasLookupWithTypeAndCode(type, codeToTry)) {
                                    return systemToSqlExpr(lookupHelper.get().getOneByTypeAndCode(type, codeToTry).getId());
                                }
                                if (lookupHelper.get().hasLookupWithTypeAndDescription(type, codeToTry)) {
                                    return systemToSqlExpr(lookupHelper.get().getOneByTypeAndDescription(type, codeToTry).getId());
                                }
                            }
                            throw new DaoException("lookup not found for code =< %s > type = %s", id, code, type);
                        }
                    } else {
                        return systemToSqlExpr(value, attribute);
                    }
                }
                default -> {
                    return systemToSqlExpr(value, attribute);
                }
            }
        }

        protected void addValue(String name, String expr) {
            attrs.add(new AttrAndValue(quoteSqlIdentifier(name), expr));
        }

    }

    private final static class AttrAndValue {

        private final String attr, value;

        public AttrAndValue(String attr, String value) {
            this.attr = checkNotBlank(attr);
            this.value = checkNotBlank(value);
        }

        public String getAttr() {
            return attr;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "AttrAndVauelAndMarker{" + "attr=" + attr + ", value=" + value + '}';
        }

    }

}
