package org.cmdbuild.dao.postgres.utils;

import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.notNull;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.copyOf;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.isEmpty;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.sql.SQLException;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.common.Constants.BASE_DOMAIN_NAME;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.common.beans.IdAndDescriptionImpl;
import org.cmdbuild.common.beans.LookupValue;
import static org.cmdbuild.dao.DaoConst.BASE_DOMAIN_TABLE_NAME;
import static org.cmdbuild.dao.DaoConst.DOMAIN_PREFIX;
import org.cmdbuild.dao.DaoException;
import static org.cmdbuild.dao.beans.AttributeMetadataImpl.emptyAttributeMetadata;
import org.cmdbuild.dao.beans.LookupValueImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeMetadata;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.EntryTypeType;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUP;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.STRING;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.TEXT;
import org.cmdbuild.dao.entrytype.attributetype.BooleanAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ByteArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ByteaArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.CharAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DecimalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DoubleAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.FileAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.FloatAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.GeometryAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IntegerAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IntervalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IpAddressAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IpType;
import org.cmdbuild.dao.entrytype.attributetype.JsonAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LinkAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LongArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LongAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.RegclassAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TextAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TimeAttributeType;
import org.cmdbuild.dao.function.StoredFunction;
import static org.cmdbuild.dao.postgres.utils.SqlTypeName._int8;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.date.CmDateUtils.isDate;
import static org.cmdbuild.utils.date.CmDateUtils.isDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.isInterval;
import static org.cmdbuild.utils.date.CmDateUtils.isTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDate;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoInterval;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoTimeWithNanos;
import static org.cmdbuild.utils.json.CmJsonUtils.LIST_OF_MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmCollectionUtils.stream;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.extractCmPrimitiveIfAvailable;
import static org.cmdbuild.utils.lang.CmConvertUtils.hasToPrimitiveConversion;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.postgresql.jdbc.PgArray;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlQueryUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final static Set<String> POSTGRES_RESERVED_KEYWORDS = set("A", "ABORT", "ABS", "ABSOLUTE", "ACCESS", "ACTION", "ADA", "ADD", "ADMIN", "AFTER", "AGGREGATE", "ALIAS", "ALL", "ALLOCATE", "ALSO", "ALTER", "ALWAYS", "ANALYSE", "ANALYZE", "AND", "ANY", "ARE", "ARRAY", "AS", "ASC", "ASENSITIVE", "ASSERTION", "ASSIGNMENT", "ASYMMETRIC", "AT", "ATOMIC", "ATTRIBUTE", "ATTRIBUTES", "AUTHORIZATION", "AVG", "BACKWARD", "BEFORE", "BEGIN", "BERNOULLI", "BETWEEN", "BIGINT", "BINARY", "BIT", "BITVAR", "BIT_LENGTH", "BLOB", "BOOLEAN", "BOTH", "BREADTH", "BY", "C", "CACHE", "CALL", "CALLED", "CARDINALITY", "CASCADE", "CASCADED", "CASE", "CAST", "CATALOG", "CATALOG_NAME", "CEIL", "CEILING", "CHAIN", "CHAR", "CHARACTER", "CHARACTERISTICS", "CHARACTERS", "CHARACTER_LENGTH", "CHARACTER_SET_CATALOG", "CHARACTER_SET_NAME", "CHARACTER_SET_SCHEMA", "CHAR_LENGTH", "CHECK", "CHECKED", "CHECKPOINT", "CLASS", "CLASS_ORIGIN", "CLOB", "CLOSE", "CLUSTER", "COALESCE", "COBOL", "COLLATE", "COLLATION", "COLLATION_CATALOG", "COLLATION_NAME", "COLLATION_SCHEMA", "COLLECT", "COLUMN", "COLUMN_NAME", "COMMAND_FUNCTION", "COMMAND_FUNCTION_CODE", "COMMENT", "COMMIT", "COMMITTED", "COMPLETION", "CONDITION", "CONDITION_NUMBER", "CONNECT", "CONNECTION", "CONNECTION_NAME", "CONSTRAINT", "CONSTRAINTS", "CONSTRAINT_CATALOG", "CONSTRAINT_NAME", "CONSTRAINT_SCHEMA", "CONSTRUCTOR", "CONTAINS", "CONTINUE", "CONVERSION", "CONVERT", "COPY", "CORR", "CORRESPONDING", "COUNT", "COVAR_POP", "COVAR_SAMP", "CREATE", "CREATEDB", "CREATEROLE", "CREATEUSER", "CROSS", "CSV", "CUBE", "CUME_DIST", "CURRENT", "CURRENT_DATE", "CURRENT_DEFAULT_TRANSFORM_GROUP", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "CURRENT_USER", "CURSOR", "CURSOR_NAME", "CYCLE", "DATA", "DATABASE", "DATE", "DATETIME_INTERVAL_CODE", "DATETIME_INTERVAL_PRECISION", "DAY", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DEFAULTS", "DEFERRABLE", "DEFERRED", "DEFINED", "DEFINER", "DEGREE", "DELETE", "DELIMITER", "DELIMITERS", "DENSE_RANK", "DEPTH", "DEREF", "DERIVED", "DESC", "DESCRIBE", "DESCRIPTOR", "DESTROY", "DESTRUCTOR", "DETERMINISTIC", "DIAGNOSTICS", "DICTIONARY", "DISABLE", "DISCONNECT", "DISPATCH", "DISTINCT", "DO", "DOMAIN", "DOUBLE", "DROP", "DYNAMIC", "DYNAMIC_FUNCTION", "DYNAMIC_FUNCTION_CODE", "EACH", "ELEMENT", "ELSE", "ENABLE", "ENCODING", "ENCRYPTED", "END", "END-EXEC", "EQUALS", "ESCAPE", "EVERY", "EXCEPT", "EXCEPTION", "EXCLUDE", "EXCLUDING", "EXCLUSIVE", "EXEC", "EXECUTE", "EXISTING", "EXISTS", "EXP", "EXPLAIN", "EXTERNAL", "EXTRACT", "FALSE", "FETCH", "FILTER", "FINAL", "FIRST", "FLOAT", "FLOOR", "FOLLOWING", "FOR", "FORCE", "FOREIGN", "FORTRAN", "FORWARD", "FOUND", "FREE", "FREEZE", "FROM", "FULL", "FUNCTION", "FUSION", "G", "GENERAL", "GENERATED", "GET", "GLOBAL", "GO", "GOTO", "GRANT", "GRANTED", "GREATEST", "GROUP", "GROUPING", "HANDLER", "HAVING", "HEADER", "HIERARCHY", "HOLD", "HOST", "HOUR", "IDENTITY", "IGNORE", "ILIKE", "IMMEDIATE", "IMMUTABLE", "IMPLEMENTATION", "IMPLICIT", "IN", "INCLUDING", "INCREMENT", "INDEX", "INDICATOR", "INFIX", "INHERIT", "INHERITS", "INITIALIZE", "INITIALLY", "INNER", "INOUT", "INPUT", "INSENSITIVE", "INSERT", "INSTANCE", "INSTANTIABLE", "INSTEAD", "INT", "INTEGER", "INTERSECT", "INTERSECTION", "INTERVAL", "INTO", "INVOKER", "IS", "ISNULL", "ISOLATION", "ITERATE", "JOIN", "K", "KEY", "KEY_MEMBER", "KEY_TYPE", "LANCOMPILER", "LANGUAGE", "LARGE", "LAST", "LATERAL", "LEADING", "LEAST", "LEFT", "LENGTH", "LESS", "LEVEL", "LIKE", "LIMIT", "LISTEN", "LN", "LOAD", "LOCAL", "LOCALTIME", "LOCALTIMESTAMP", "LOCATION", "LOCATOR", "LOCK", "LOGIN", "LOWER", "M", "MAP", "MATCH", "MATCHED", "MAX", "MAXVALUE", "MEMBER", "MERGE", "MESSAGE_LENGTH", "MESSAGE_OCTET_LENGTH", "MESSAGE_TEXT", "METHOD", "MIN", "MINUTE", "MINVALUE", "MOD", "MODE", "MODIFIES", "MODIFY", "MODULE", "MONTH", "MORE", "MOVE", "MULTISET", "MUMPS", "NAME", "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NCLOB", "NESTING", "NEW", "NEXT", "NO", "NOCREATEDB", "NOCREATEROLE", "NOCREATEUSER", "NOINHERIT", "NOLOGIN", "NONE", "NORMALIZE", "NORMALIZED", "NOSUPERUSER", "NOT", "NOTHING", "NOTIFY", "NOTNULL", "NOWAIT", "NULL", "NULLABLE", "NULLIF", "NULLS", "NUMBER", "NUMERIC", "OBJECT", "OCTETS", "OCTET_LENGTH", "OF", "OFF", "OFFSET", "OIDS", "OLD", "ON", "ONLY", "OPEN", "OPERATION", "OPERATOR", "OPTION", "OPTIONS", "OR", "ORDER", "ORDERING", "ORDINALITY", "OTHERS", "OUT", "OUTER", "OUTPUT", "OVER", "OVERLAPS", "OVERLAY", "OVERRIDING", "OWNER", "PAD", "PARAMETER", "PARAMETERS", "PARAMETER_MODE", "PARAMETER_NAME", "PARAMETER_ORDINAL_POSITION", "PARAMETER_SPECIFIC_CATALOG", "PARAMETER_SPECIFIC_NAME", "PARAMETER_SPECIFIC_SCHEMA", "PARTIAL", "PARTITION", "PASCAL", "PASSWORD", "PATH", "PERCENTILE_CONT", "PERCENTILE_DISC", "PERCENT_RANK", "PLACING", "PLI", "POSITION", "POSTFIX", "POWER", "PRECEDING", "PRECISION", "PREFIX", "PREORDER", "PREPARE", "PREPARED", "PRESERVE", "PRIMARY", "PRIOR", "PRIVILEGES", "PROCEDURAL", "PROCEDURE", "PUBLIC", "QUOTE", "RANGE", "RANK", "READ", "READS", "REAL", "RECHECK", "RECURSIVE", "REF", "REFERENCES", "REFERENCING", "REGR_AVGX", "REGR_AVGY", "REGR_COUNT", "REGR_INTERCEPT", "REGR_R2", "REGR_SLOPE", "REGR_SXX", "REGR_SXY", "REGR_SYY", "REINDEX", "RELATIVE", "RELEASE", "RENAME", "REPEATABLE", "REPLACE", "RESET", "RESTART", "RESTRICT", "RESULT", "RETURN", "RETURNED_CARDINALITY", "RETURNED_LENGTH", "RETURNED_OCTET_LENGTH", "RETURNED_SQLSTATE", "RETURNS", "REVOKE", "RIGHT", "ROLE", "ROLLBACK", "ROLLUP", "ROUTINE", "ROUTINE_CATALOG", "ROUTINE_NAME", "ROUTINE_SCHEMA", "ROW", "ROWS", "ROW_COUNT", "ROW_NUMBER", "RULE", "SAVEPOINT", "SCALE", "SCHEMA", "SCHEMA_NAME", "SCOPE", "SCOPE_CATALOG", "SCOPE_NAME", "SCOPE_SCHEMA", "SCROLL", "SEARCH", "SECOND", "SECTION", "SECURITY", "SELECT", "SELF", "SENSITIVE", "SEQUENCE", "SERIALIZABLE", "SERVER_NAME", "SESSION", "SESSION_USER", "SET", "SETOF", "SETS", "SHARE", "SHOW", "SIMILAR", "SIMPLE", "SIZE", "SMALLINT", "SOME", "SOURCE", "SPACE", "SPECIFIC", "SPECIFICTYPE", "SPECIFIC_NAME", "SQL", "SQLCODE", "SQLERROR", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQRT", "STABLE", "START", "STATE", "STATEMENT", "STATIC", "STATISTICS", "STDDEV_POP", "STDDEV_SAMP", "STDIN", "STDOUT", "STORAGE", "STRICT", "STRUCTURE", "STYLE", "SUBCLASS_ORIGIN", "SUBLIST", "SUBMULTISET", "SUBSTRING", "SUM", "SUPERUSER", "SYMMETRIC", "SYSID", "SYSTEM", "SYSTEM_USER", "TABLE", "TABLESAMPLE", "TABLESPACE", "TABLE_NAME", "TEMP", "TEMPLATE", "TEMPORARY", "TERMINATE", "THAN", "THEN", "TIES", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TOAST", "TOP_LEVEL_COUNT", "TRAILING", "TRANSACTION", "TRANSACTIONS_COMMITTED", "TRANSACTIONS_ROLLED_BACK", "TRANSACTION_ACTIVE", "TRANSFORM", "TRANSFORMS", "TRANSLATE", "TRANSLATION", "TREAT", "TRIGGER", "TRIGGER_CATALOG", "TRIGGER_NAME", "TRIGGER_SCHEMA", "TRIM", "TRUE", "TRUNCATE", "TRUSTED", "TYPE", "UESCAPE", "UNBOUNDED", "UNCOMMITTED", "UNDER", "UNENCRYPTED", "UNION", "UNIQUE", "UNKNOWN", "UNLISTEN", "UNNAMED", "UNNEST", "UNTIL", "UPDATE", "UPPER", "USAGE", "USER", "USER_DEFINED_TYPE_CATALOG", "USER_DEFINED_TYPE_CODE", "USER_DEFINED_TYPE_NAME", "USER_DEFINED_TYPE_SCHEMA", "USING", "VACUUM", "VALID", "VALIDATOR", "VALUE", "VALUES", "VARCHAR", "VARIABLE", "VARYING", "VAR_POP", "VAR_SAMP", "VERBOSE", "VIEW", "VOLATILE", "WHEN", "WHENEVER", "WHERE", "WIDTH_BUCKET", "WINDOW", "WITH", "WITHIN", "WITHOUT", "WORK", "WRITE", "YEAR", "ZONE").immutable();

    public static final String Q3_MASTER = "Q3_MASTER";

    public static final Set<String> Q3_MARKERS = ImmutableSet.of(Q3_MASTER);

    public static String escapeLikeExpression(@Nullable String expr) {
        if (isBlank(expr)) {
            return expr;
        } else {
            return expr.replaceAll("[_%\\\\]", "\\\\$0");
        }
    }

    public static boolean exprContainsQ3Markers(@Nullable String expr) {
        if (isBlank(expr)) {
            return false;
        } else {
            return Q3_MARKERS.stream().anyMatch(expr::contains);
        }
    }

    public static String entryTypeToSqlExpr(EntryType entryType) {
        return switch (entryType.getEtType()) {
            case ET_CLASS ->
                quoteSqlIdentifier(entryType.getName());
            case ET_DOMAIN ->
                quoteSqlIdentifier(domainNameToSqlTable(entryType.getName()));
            case ET_FUNCTION -> {
                checkArgument(((StoredFunction) entryType).getInputParameters().isEmpty(), "cannot invoke this function like a view, it has parameters; function = %s", entryType);
                yield format("%s()", quoteSqlIdentifier(entryType.getName()));
            }
            default ->
                throw unsupported("unsupported entry type = %s", entryType);
        };
    }

    public static String entryTypeToSqlExpr(String name, EntryTypeType type) {
        return switch (type) {
            case ET_CLASS ->
                quoteSqlIdentifier(name);
            case ET_DOMAIN ->
                quoteSqlIdentifier(domainNameToSqlTable(name));
            default ->
                throw unsupported("unsupported entry type = %s", type);
        };
    }

    public static String quoteSqlIdentifier(String name) {
        checkNotBlank(name);
        if ((name.matches("[a-z_][a-z0-9_]*") && !POSTGRES_RESERVED_KEYWORDS.contains(name.toUpperCase())) || name.matches("^\".*\"$")) {
            return name;
        } else {
            return format("\"%s\"", name.replace("\"", "\"\""));
        }
    }

    public static String functionCallSqlExpr(String functionName, Object... args) {
        return format("%s(%s)", quoteSqlIdentifier(functionName), list(args).stream().map(SqlQueryUtils::systemToSqlExpr).collect(joining(",")));
    }

    public static String sqlTableToDomainName(String sqlDomainTable) {
        return checkNotBlank(sqlDomainTable).replaceAll("\"", "").replaceFirst("^" + Pattern.quote(DOMAIN_PREFIX), "");
    }

    @Nullable
    public static String sqlTableToClassNameOrNull(@Nullable String sqlDomainTable) {
        return isBlank(sqlDomainTable) ? null : sqlTableToClassName(sqlDomainTable);
    }

    public static String sqlTableToClassName(String sqlDomainTable) {
        return checkNotBlank(sqlDomainTable).replaceAll("\"", "");
    }

    public static String sqlTableToEntryTypeName(String tableName, EntryTypeType type) {
        return switch (type) {
            case ET_CLASS ->
                sqlTableToClassName(tableName);
            case ET_DOMAIN ->
                sqlTableToDomainName(tableName);
            default ->
                throw unsupported("unsupported entry type = %s", type);
        };
    }

    public static String domainNameToSqlTable(String domainName) {
        if (equal(domainName, BASE_DOMAIN_NAME)) {
            return BASE_DOMAIN_TABLE_NAME;
        } else {
            return DOMAIN_PREFIX + sqlTableToDomainName(domainName);
        }
    }

    public static String buildReferenceTypeExpr(Classe targetClass, String classExpr, String attrExpr) {
        return format("(SELECT \"IdClass\"::regclass FROM %s WHERE \"Id\" = %s.%s%s)", entryTypeToSqlExpr(targetClass), classExpr, attrExpr, targetClass.isSimpleClass() ? "" : " AND \"Status\" = 'A'");
    }

    public static String buildReferenceCodeExpr(Classe targetClass, String classExpr, String attrExpr) {
        return format("(SELECT \"Code\" FROM %s WHERE \"Id\" = %s.%s%s)", entryTypeToSqlExpr(targetClass), classExpr, attrExpr, targetClass.isSimpleClass() ? "" : " AND \"Status\" = 'A'");
    }

    public static String buildReferenceDescExpr(Classe targetClass, String classExpr, String attrExpr) {
//		return format("_cm3_card_description_get('%s'::regclass,%s)", entryTypeToQuotedSql(targetClass), attrExpr); ignore-tenant function; too slow, replaced from simple subquery below
        return format("(SELECT \"Description\" FROM %s WHERE \"Id\" = %s.%s%s)", entryTypeToSqlExpr(targetClass), classExpr, attrExpr, targetClass.isSimpleClass() ? "" : " AND \"Status\" = 'A'");
    }

    public static String buildReferenceExistsExpr(Classe targetClass, String classExpr, String attrExpr) {
        return format("(EXISTS (SELECT 1 FROM %s WHERE \"Id\" = %s.%s%s) )", entryTypeToSqlExpr(targetClass), classExpr, attrExpr, targetClass.isSimpleClass() ? "" : " AND \"Status\" = 'A'");
    }

    public static String buildFileNameExpr(Classe targetClass, String classExpr, String attrExpr) {
        return format("(SELECT \"FileName\" FROM %s WHERE \"Id\" = %s.%s%s)", entryTypeToSqlExpr(targetClass), classExpr, attrExpr, targetClass.isSimpleClass() ? "" : " AND \"Status\" = 'A'");
    }

    public static String buildLookupCodeExpr(String classExpr, String attrExpr) {
        return format("(SELECT \"Code\" FROM \"LookUp\" WHERE \"Id\" = %s.%s AND \"Status\" = 'A')", classExpr, attrExpr);
    }

    public static String buildLookupDescExpr(String classExpr, String attrExpr) {
        return format("(SELECT \"Description\" FROM \"LookUp\" WHERE \"Id\" = %s.%s AND \"Status\" = 'A')", classExpr, attrExpr);
    }

    public static String buildLookupArrayDescExpr(String classExpr, String attrExpr, String value) {
        return format("(EXISTS (SELECT 1 FROM \"LookUp\" WHERE \"Id\" = ANY(%s.%s) AND \"Status\" = 'A' AND \"Description\" ILIKE %s))", classExpr, attrExpr, value);
    }

    public static String buildLookupInfoExpr(String classExpr, String attrExpr) {
        return format("_cm3_lookup_info(%s.%s)::varchar", classExpr, attrExpr);
    }

    public static String buildDescAttrName(String attr) {
        return format("_%s_description", attr);
    }

    public static String buildCodeAttrName(String attr) {
        return format("_%s_code", attr);
    }

    public static String buildTypeAttrName(String attr) {
        return format("_%s_type", attr);
    }

    public static String buildInfoAttrName(String attr) {
        return format("_%s_info", attr);
    }

    public static String buildReferenceAttrName(String referenceAttr, String domainAttr) {
        return format("_%s_attr_%s", checkNotBlank(referenceAttr), checkNotBlank(domainAttr));
    }

    public static <T> Map<String, Object> parseEntryTypeQueryResponseData(EntryType entryType, List<T> attrs, Function<T, String> attrToAttrName, Function<T, Object> attrToValue) {
        Map<String, Object> map = attrs.stream().collect(toMap(attrToAttrName::apply, (attr) -> {
            Object value, rawValue = attrToValue.apply(attr);
            Attribute attribute = entryType.getAttributeOrNull(attrToAttrName.apply(attr));
            if (attribute != null) {
                // To Handle LookupArray ids returned in Card attributes
                if (attribute.isOfType(AttributeTypeName.LOOKUPARRAY) && rawValue instanceof PgArray rawArrayValue) {
                    Object convRawValue = (Long[]) pgToJavaArray(rawArrayValue);
                    LOGGER.trace("processing attr =< {} > with sql value =< {} > ( {} ), preliminary step for LOOKUPARRAY, converted to value =< {} > ( {} )", attr, rawValue, getClassOfNullable(rawValue).getName(), convRawValue, getClassOfNullable(convRawValue).getName());
                    rawValue = convRawValue;
                }

                value = rawToSystem(attribute.getType(), rawValue);
            } else {
                value = rawValue;
            }
            LOGGER.trace("processing attr =< {} > with sql value =< {} > ( {} ), converted to value =< {} > ( {} )", attr, rawValue, getClassOfNullable(rawValue).getName(), value, getClassOfNullable(value).getName());
            return value;
        }));
        list(map.keySet()).stream().map(entryType::getAttributeOrNull).filter(notNull()).forEach((a) -> {
            switch (a.getType().getName()) {
                case LOOKUP -> {
                    LookupValue lookupValue = (LookupValue) map.get(a.getName());
                    if (lookupValue != null) {
                        String desc = toStringOrNull(map.remove(buildDescAttrName(a.getName())));
                        String code = toStringOrNull(map.remove(buildCodeAttrName(a.getName())));
                        Object value = LookupValueImpl.copyOf(lookupValue).withDescription(desc).withCode(code).build();
                        map.put(a.getName(), value);
                    }
                }
                case LOOKUPARRAY -> {
                    List<LookupValue> list = (List<LookupValue>) map.get(a.getName());
                    List<Map<String, String>> info = Optional.ofNullable(trimToNull(toStringOrNull(map.get(buildInfoAttrName(a.getName()))))).map(i -> fromJson(i, LIST_OF_MAP_OF_STRINGS)).orElse(null);
                    if (list != null && info != null) {
                        checkArgument(list.size() == info.size(), "invalid lookup array info");
                        map.put(a.getName(), list().accept(l -> {
                            for (int i = 0; i < list.size(); i++) {
                                l.add(LookupValueImpl.copyOf(list.get(i)).withDescription(info.get(i).get(ATTR_DESCRIPTION)).withCode(info.get(i).get(ATTR_CODE)).build());
                            }
                        }));
                    }
                }
                case REFERENCE, FOREIGNKEY -> {
                    IdAndDescription idAndDescription = (IdAndDescription) map.get(a.getName());
                    if (idAndDescription != null) {
                        Object value = new IdAndDescriptionImpl(sqlTableToClassNameOrNull(toStringOrNull(map.remove(buildTypeAttrName(a.getName())))), idAndDescription.getId(), toStringOrNull(map.remove(buildDescAttrName(a.getName()))), toStringOrNull(map.remove(buildCodeAttrName(a.getName()))));
                        map.put(a.getName(), value);
                    }
                }
            }
        });
        return map;
    }

    public static String wrapExprWithBrackets(String expr) {
        checkNotBlank(expr);
        if (!isSafelyWrappedWithBrakets(expr)) {
            expr = format("(%s)", expr);
        }
        return expr;
    }

    public static boolean isSafelyWrappedWithBrakets(String expr) { //TODO improve this, parse sql and check
        return expr.trim().matches("[(][^()]+[)]");
    }

    public static SqlType parseSqlType(String sqlTypeString) {
        return parseSqlType(sqlTypeString, emptyAttributeMetadata());
    }

    public static CardAttributeType<?> createAttributeType(String sqlTypeString, AttributeMetadata meta) {
        return sqlTypeToAttributeType(parseSqlType(sqlTypeString, meta));
    }

    public static SqlType parseSqlType(String sqlTypeString, AttributeMetadata meta) {
        checkNotBlank(sqlTypeString);
        checkNotNull(meta);
        Matcher typeMatcher = Pattern.compile("(\\w+)(\\((\\d+(,\\d+)*)\\))?").matcher(sqlTypeString);
        checkArgument(typeMatcher.find());
        SqlTypeName type = SqlTypeName.valueOf(typeMatcher.group(1).toLowerCase());
        List<String> params = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(nullToEmpty(typeMatcher.group(3)));
        SqlType sqlType = new SqlTypeImpl(type, params, meta);
        return sqlType;
    }

    @Nullable
    public static String getSystemToSqlCastOrNull(CardAttributeType attributeType) {
        return SqlQueryUtils.getSystemToSqlCastOrNull(attributeTypeToSqlTypeName(attributeType));
    }

    public static String addSqlCastIfRequired(CardAttributeType attributeType, String expr) {
        String cast = getSystemToSqlCastOrNull(attributeType);
        if (isNotBlank(cast)) {
            expr += "::" + cast;
        }
        return expr;
    }

    public static String getSqlTypeString(CardAttributeType attributeType) {
        return attributeTypeToSqlType(attributeType).toSqlTypeString();
    }

    public static String pgObjectToString(Object value) {
        return ((PGobject) value).getValue().replaceAll("\"", "");
    }

    private static Object wrapJsonForJdbc(@Nullable Object value) {
        try {
            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            jsonObject.setValue(toStringOrNull(value));
            return jsonObject;
        } catch (SQLException ex) {
            throw new DaoException(ex, "error wrapping to postgres json, source value = %s", value);
        }
    }

    public static boolean isVoidSqlType(String sqlTypeString) {
        return "void".equalsIgnoreCase(sqlTypeString);
    }

    public static CardAttributeType<?> createAttributeType(String sqlTypeString) {
        return parseSqlType(sqlTypeString).toAttributeType();
    }

    private static CardAttributeType<?> sqlTypeToAttributeType(SqlType sqlType) {
        try {
            AttributeMetadata meta = sqlType.getMetadata();
            return switch (sqlType.getType()) {
                case bool ->
                    new BooleanAttributeType();
                case date ->
                    new DateAttributeType();
                case float8 ->
                    new DoubleAttributeType();
                case float4 ->
                    FloatAttributeType.INSTANCE;
                case inet -> {
                    String typeValue = meta.get(AttributeMetadata.IP_TYPE);
                    IpType type = parseEnumOrDefault(typeValue, IpType.IPV4);
                    yield new IpAddressAttributeType(type);
                }
                case int4 ->
                    new IntegerAttributeType();
                case int8 -> {
                    if (meta.isLookup()) {
                        yield new LookupAttributeType(meta.getLookupType());
                    } else if (meta.isReference()) {
                        yield new ReferenceAttributeType(meta.getDomain(), meta.getDirection());
                    } else if (meta.isForeignKey()) {
                        yield new ForeignKeyAttributeType(meta.getForeignKeyDestinationClassName(), meta.getCascadeAction());
                    } else if (meta.isFile()) {
                        yield FileAttributeType.INSTANCE;
                    } else {
                        yield LongAttributeType.INSTANCE;
                    }
                }
                case numeric -> {
                    if (sqlType.getParams().size() == 2) {
                        yield new DecimalAttributeType(Integer.valueOf(sqlType.getParams().get(0)), Integer.valueOf(sqlType.getParams().get(1)));
                    } else {
                        yield new DecimalAttributeType();
                    }
                }
                case regclass ->
                    RegclassAttributeType.INSTANCE;
                case text -> {
                    if (meta.hasTextAttributeLanguage()) {
                        yield new TextAttributeType(meta.getTextAttributeLanguage());
                    } else {
                        yield new TextAttributeType();
                    }
                }
                case time ->
                    new TimeAttributeType();
                case timestamp, timestamptz ->
                    new DateTimeAttributeType();
                case interval ->
                    IntervalAttributeType.INSTANCE;
                case varchar -> {
                    if (meta.isGeometry()) {
                        yield GeometryAttributeType.INSTANCE;
                    } else if (meta.isLink()) {
                        yield LinkAttributeType.INSTANCE;
                    } else if (sqlType.hasParams()) {
                        yield new StringAttributeType(Integer.valueOf(getOnlyElement(sqlType.getParams())));
                    } else {
                        yield new StringAttributeType();
                    }
                }
                case _varchar ->
                    new StringArrayAttributeType();
                case _int8 -> {
                    if (meta.isLookup()) {
                        yield new LookupArrayAttributeType(meta.getLookupType());
                    } else {
                        yield LongArrayAttributeType.INSTANCE; //TODO reference array, fk array
                    }
                }
                case jsonb ->
                    JsonAttributeType.INSTANCE;
                case bpchar -> {
                    if (sqlType.getParams().size() == 1) {
                        int value = Integer.parseInt(getOnlyElement(sqlType.getParams()));
                        checkArgument(value > 0);
                        if (value == 1) {
                            yield new CharAttributeType();
                        } else {
                            yield new StringAttributeType(value);
                        }
                    } else {
                        yield new CharAttributeType();
                    }
                }
                case bytea ->
                    new ByteArrayAttributeType();
                case _bytea ->
                    new ByteaArrayAttributeType();
                default ->
                    throw new UnsupportedOperationException(format("unsupported conversion of sql type = %s to attribute type", sqlType.getType()));
            };
        } catch (Exception ex) {
            throw new DaoException(ex, "error converting sql type = %s to attribute type", sqlType);
        }
    }

    public static SqlTypeName attributeTypeToSqlTypeName(CardAttributeType attributeType) {
        try {
            return switch (attributeType.getName()) {
                case BOOLEAN ->
                    SqlTypeName.bool;
                case BYTEARRAY ->
                    SqlTypeName.bytea;
                case CHAR ->
                    SqlTypeName.bpchar;
                case DATE ->
                    SqlTypeName.date;
                case DECIMAL ->
                    SqlTypeName.numeric;
                case DOUBLE ->
                    SqlTypeName.float8;
                case FLOAT ->
                    SqlTypeName.float4;
                case REGCLASS ->
                    SqlTypeName.regclass;
                case INTEGER ->
                    SqlTypeName.int4;
                case FOREIGNKEY, LOOKUP, REFERENCE, LONG, FILE ->
                    SqlTypeName.int8;
                case INET ->
                    SqlTypeName.inet;
                case JSON ->
                    SqlTypeName.jsonb;
                case STRING, GEOMETRY, LINK ->
                    SqlTypeName.varchar;
                case STRINGARRAY ->
                    SqlTypeName._varchar;
                case LONGARRAY, LOOKUPARRAY, REFERENCEARRAY ->
                    SqlTypeName._int8;
                case BYTEAARRAY ->
                    SqlTypeName._bytea;
                case TEXT ->
                    SqlTypeName.text;
                case TIME ->
                    SqlTypeName.time;
                case INTERVAL ->
                    SqlTypeName.interval;
                case TIMESTAMP ->
                    SqlTypeName.timestamptz;
                default ->
                    throw new UnsupportedOperationException(format("unsupported conversion of attribute type = %s to sql type", attributeType.getName()));
            };
        } catch (Exception ex) {
            throw new DaoException(ex, "error converting attribute type = %s to sql type", attributeType);
        }
    }

    public static String attributeTypeToSqlCast(CardAttributeType attributeType) {
        return attributeTypeToSqlTypeName(attributeType).name();
    }

    public static SqlType attributeTypeToSqlType(CardAttributeType attributeType) {
        try {
            return switch (attributeType.getName()) {
                case CHAR ->
                    new SqlTypeImpl(SqlTypeName.bpchar, singletonList("1"));
                case DECIMAL -> {
                    DecimalAttributeType decimalAttributeType = (DecimalAttributeType) attributeType;
                    if (decimalAttributeType.hasPrecisionAndScale()) {
                        yield new SqlTypeImpl(SqlTypeName.numeric, asList(decimalAttributeType.getPrecision().toString(), decimalAttributeType.getScale().toString()));
                    } else {
                        yield new SqlTypeImpl(SqlTypeName.numeric);
                    }
                }
                case STRING -> {
                    StringAttributeType stringAttributeType = (StringAttributeType) attributeType;
                    if (stringAttributeType.hasLength()) {
                        yield new SqlTypeImpl(SqlTypeName.varchar, singletonList(Integer.toString(stringAttributeType.getLength())));
                    } else {
                        yield new SqlTypeImpl(SqlTypeName.varchar);
                    }
                }
                default ->
                    new SqlTypeImpl(attributeTypeToSqlTypeName(attributeType));
            };
        } catch (Exception ex) {
            throw new DaoException(ex, "error converting attribute type = %s to sql type", attributeType);
        }
    }

    private static class SqlTypeImpl implements SqlType {

        private final SqlTypeName type;
        private final List<String> params;
        private final AttributeMetadata metadata;
        private final String sqlCast;

        public SqlTypeImpl(SqlTypeName type) {
            this(type, emptyList(), emptyAttributeMetadata());
        }

        public SqlTypeImpl(SqlTypeName type, List<String> params) {
            this(type, params, emptyAttributeMetadata());
        }

        public SqlTypeImpl(SqlTypeName type, List<String> params, AttributeMetadata metadata) {
            this.type = checkNotNull(type);
            this.metadata = checkNotNull(metadata);
            this.params = copyOf(checkNotNull(params));
            checkArgument(params.stream().allMatch(not(StringUtils::isBlank)), "found invalid blank param in params = %s", params);
            sqlCast = SqlQueryUtils.getSystemToSqlCastOrNull(type);
        }

        @Override
        public SqlTypeName getType() {
            return type;
        }

        @Override
        public List<String> getParams() {
            return params;
        }

        @Override
        public AttributeMetadata getMetadata() {
            return metadata;
        }

        @Override
        public String toSqlTypeString() {
            if (isNullOrEmpty(params)) {
                return type.name();
            } else {
                return format("%s(%s)", type.name(), Joiner.on(",").join(params));
            }
        }

        @Override
        public String getSqlCast() {
            return checkNotBlank(sqlCast);
        }

        @Override
        public boolean hasSqlCast() {
            return sqlCast != null;
        }

        @Override
        public CardAttributeType<?> toAttributeType() {
            return sqlTypeToAttributeType(this);
        }

        @Override
        public String toString() {
            return "SqlTypeImpl{" + "type=" + type + '}';
        }

    }

    @Nullable
    public static String getSystemToSqlCastOrNull(SqlTypeName sqlType) {
        return switch (sqlType) {
            case inet ->
                "inet";
            case _bytea ->
                "bytea[]";
            case regclass ->
                "regclass";
            case time ->
                "varchar";
            default ->
                null;
        };
    }

    public static String systemToSqlExpr(@Nullable Object value, Attribute attribute) {
        if (attribute.isOfType(STRING, TEXT) && attribute.getMetadata().encryptOnDb()) {
            value = Cm3EasyCryptoUtils.encryptValueIfNotEncrypted(toStringOrNull(value));
        }
        return systemToSqlExpr(value, attribute.getType());
    }

    public static String systemToSqlExpr(@Nullable Object value, CardAttributeType attributeType) {
        if (value == null) {
            return "NULL";
        } else {
            return switch (attributeType.getName()) {
                case REGCLASS -> {
                    if (value instanceof EntryType entryType) {
                        value = entryTypeToSqlExpr(entryType);
                    }
                    yield classNameToSqlExpr(toStringNotBlank(value));
                }
                case STRING, CHAR, GEOMETRY, LINK ->
                    systemToSqlExpr(toStringOrNull(value));
                case JSON ->
                    isNullOrBlank(value) ? "NULL" : format("%s::jsonb", systemToSqlExpr(toStringNotBlank(value)));
                case DATE ->
                    systemToSqlExpr(CmDateUtils.toDate(value));
                case TIME ->
                    systemToSqlExpr(CmDateUtils.toTime(value));
                case TIMESTAMP ->
                    systemToSqlExpr(CmDateUtils.toDateTime(value));
                case INTERVAL ->
                    systemToSqlExpr(CmDateUtils.toInterval(value));
                case LOOKUP, REFERENCE, FOREIGNKEY, FILE -> {
                    if (value instanceof IdAndDescription) {
                        value = ltEqZeroToNull(IdAndDescription.class.cast(value).getId());
                    }
                    yield systemToSqlExpr(value);
                }
                case STRINGARRAY ->
                    toSqlArrayExpr((List) convert(value, List.class).stream().map(CmStringUtils::toStringOrNull).collect(toList()), "varchar[]");
                case BYTEARRAY ->
                    byteArrayToSqlExpr(convert(value, byte[].class));
                case BYTEAARRAY ->
                    toSqlArrayExpr((List) list(convert(value, byte[][].class)), "bytea[]");
                case LOOKUPARRAY ->
                    systemToSqlExpr(value, _int8);
                default ->
                    systemToSqlExpr(value);
            };
        }
    }

    public static String byteArrayToSqlExpr(byte[] data) {
        return format("decode('%s','base64')", Base64.encodeBase64String(data));
    }

    @Nullable
    public static Object systemToSql(SqlTypeName type, @Nullable Object value) {
        return switch (type) {
            case regclass ->
                quoteSqlIdentifier(toStringOrNull(value));
            case date ->
                CmDateUtils.toSqlDate(value);
            case time ->
                CmDateUtils.toSqlTime(value);
            case timestamp, timestamptz ->
                CmDateUtils.toSqlTimestamp(value);
            case int8 -> {
                if (value instanceof IdAndDescriptionImpl) {
                    yield IdAndDescriptionImpl.class.cast(value).getId();
                } else {
                    yield value;
                }
            }
            case _varchar ->
                new PostgresArray(CmConvertUtils.convert(value, String[].class));
            case _int8 ->
                new PostgresArray(CmConvertUtils.convert(value, Long[].class));
            case _bytea ->
                new PostgresArray(CmConvertUtils.convert(value, byte[][].class));
            default ->
                value;
        };
    }

    public static String classNameToSqlExpr(String className) {
        return format("%s::regclass", systemToSqlExpr(quoteSqlIdentifier(className)));
    }

    public static String systemToSqlExpr(@Nullable Object value) {
        return systemToSqlExpr(value, (SqlTypeName) null);
    }

    public static String systemToSqlExpr(@Nullable Object value, @Nullable SqlTypeName sqlTypeName) {
        if (value == null) {
            return "NULL";
        } else if (value instanceof String string) {
            if (equal(sqlTypeName, SqlTypeName.regclass)) {
//                return format("_cm3_utils_name_to_regclass(%s)", systemToSqlExpr(toStringNotBlank(value)));
                return format("%s::regclass", systemToSqlExpr(quoteSqlIdentifier(toStringNotBlank(value))));
            }
            return format("'%s'", string.replace("'", "''").replace("\0", ""));
        } else if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Boolean) {
            return value.toString().toUpperCase();
        } else if (isDate(value)) {
            return format("DATE %s", systemToSqlExpr(toIsoDate(value)));
        } else if (isTime(value)) {
            return format("TIME %s", systemToSqlExpr(toIsoTimeWithNanos(value)));
        } else if (isDateTime(value)) {
            return format("TIMESTAMPTZ %s", systemToSqlExpr(toIsoDateTime(value)));
        } else if (isInterval(value)) {
            return format("INTERVAL %s", systemToSqlExpr(toIsoInterval(value)));
        } else if (value instanceof Iterable iterable) {
            String cast;
            if (sqlTypeName != null) {
                cast = sqlTypeNameToCast(sqlTypeName);
            } else if (!isEmpty(iterable) && stream(iterable).allMatch(String.class::isInstance)) {
                cast = "varchar[]";
            } else {
                cast = "";
            }
            return toSqlArrayExpr(iterable, cast, sqlTypeName);
        } else if (value instanceof EntryType entryType) {
            return format("%s::regclass", systemToSqlExpr(entryTypeToSqlExpr(entryType)));
        } else if (value instanceof byte[]) {
            return byteArrayToSqlExpr(convert(value, byte[].class));
        } else if (hasToPrimitiveConversion(value)) {
            return systemToSqlExpr(extractCmPrimitiveIfAvailable(value), sqlTypeName);
        } else if (value.getClass().isEnum()) {
            return systemToSqlExpr(serializeEnum((Enum) value));
        } else {
            throw new IllegalArgumentException(format("unsupported conversion to sql expr of value = %s ( %s )", value, value.getClass().getName()));
        }
    }

    @Nullable
    private static String sqlTypeNameToCast(SqlTypeName sqlTypeName) {
        return switch (sqlTypeName) {
            case _int4 ->
                "integer[]";
            case _int8 ->
                "bigint[]";
            case _varchar ->
                "varchar[]";
            case _bytea ->
                "bytea[]";
            case _regclass ->
                "regclass[]";
            default ->
                throw new UnsupportedOperationException(format("TODO: cast of type = %s not implemented yet", sqlTypeName));//TODO
        };
    }

    private static String toSqlArrayExpr(Iterable list, @Nullable String cast) {
        return toSqlArrayExpr(list, cast, null);
    }

    private static String toSqlArrayExpr(Iterable list, @Nullable String cast, @Nullable SqlTypeName type) {
        SqlTypeName innerType = arrayTypeToElementType(type);
        String expr = format("ARRAY[%s]", stream(list).map(v -> systemToSqlExpr(v, innerType)).collect(joining(",")));
        if (isNotBlank(cast)) {
            expr = format("%s::%s", expr, cast);
        }
        return expr;
    }

    @Nullable
    private static SqlTypeName arrayTypeToElementType(@Nullable SqlTypeName type) {
        return type == null ? null : SqlTypeName.valueOf(type.name().replaceFirst("^_", ""));
    }

    @Nullable
    public static Object sqlToSystem(SqlTypeName type, @Nullable Object value) {
        return switch (type) {
            case date ->
                CmDateUtils.toDate(value);
            case time ->
                CmDateUtils.toTime(value);
            case timestamp, timestamptz ->
                CmDateUtils.toDateTime(value);
            case interval ->
                CmDateUtils.toInterval(value);
            case _varchar -> {
                if (value instanceof PgArray pgArray) {
                    yield (String[]) pgToJavaArray(pgArray);
                } else {
                    yield value;
                }
            }
            case _int8 -> {
                if (value instanceof PgArray pgArray) {
                    yield (Long[]) pgToJavaArray(pgArray);
                } else {
                    yield value;
                }
            }
            case _bytea -> {
                if (value instanceof PgArray pgArray) {
                    Object obj = pgToJavaArray(pgArray);
                    if (Array.getLength(obj) == 0) {
                        yield new byte[0][];
                    } else {
                        yield (byte[][]) obj;
                    }
                } else {
                    yield value;
                }
            }
            default ->
                value;
        };
    }

    private static Object pgToJavaArray(PgArray pgArray) {
        try {
            return pgArray.getArray();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
