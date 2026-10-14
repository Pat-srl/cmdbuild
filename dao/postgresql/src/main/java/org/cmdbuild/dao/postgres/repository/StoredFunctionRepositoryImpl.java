package org.cmdbuild.dao.postgres.repository;

import com.google.common.eventbus.Subscribe;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import org.cmdbuild.dao.beans.FunctionMetadataImpl;
import org.cmdbuild.dao.driver.repository.StoredFunctionModifiedEvent;
import org.cmdbuild.dao.driver.repository.StoredFunctionRepository;
import org.cmdbuild.dao.entrytype.AttrEditorType;
import org.cmdbuild.dao.entrytype.AttributeMetadata;
import static org.cmdbuild.dao.entrytype.TextContentSecurity.TCS_HTML_ALL;
import static org.cmdbuild.dao.entrytype.TextContentSecurity.TCS_HTML_SAFE;
import static org.cmdbuild.dao.entrytype.TextContentSecurity.TCS_PLAINTEXT;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.DECIMAL;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.STRING;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DecimalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dao.function.FunctionMetadata;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.function.StoredFunctionImpl;
import org.cmdbuild.dao.function.StoredFunctionImpl.FunctionBuilder;
import static org.cmdbuild.dao.postgres.utils.CommentUtils.FUNCTION_COMMENT_TO_METADATA_MAPPING;
import static org.cmdbuild.dao.postgres.utils.CommentUtils.parseCommentFromFeatures;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.createAttributeType;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.isVoidSqlType;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.INNER;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElementOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.notNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
@Qualifier(INNER)
public class StoredFunctionRepositoryImpl implements StoredFunctionRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String DEFAULT_SCHEMA = "public", SEPARATOR = "|", KEY_VALUE_SEPARATOR = ": ";

    private final JdbcTemplate jdbcTemplate;

    private final Holder<List<StoredFunction>> allFunctionsCache;
    private final CmCache<Optional<StoredFunction>> functionsCacheByName;

    public StoredFunctionRepositoryImpl(CacheService cacheService, @Qualifier(SYSTEM_LEVEL_ONE) JdbcTemplate jdbcTemplate, EventBusService busService) {
        this.jdbcTemplate = checkNotNull(jdbcTemplate);

        allFunctionsCache = cacheService.newHolder("dao_all_functions", CacheConfig.SYSTEM_OBJECTS);
        functionsCacheByName = cacheService.newCache("dao_functions_by_name", CacheConfig.SYSTEM_OBJECTS);
        busService.getDaoEventBus().register(new Object() {

            @Subscribe
            public void handleStoredFunctionModifiedEvent(StoredFunctionModifiedEvent event) {
                invalidateCache();
            }
        });
    }

    private void invalidateCache() {
        allFunctionsCache.invalidate();
        functionsCacheByName.invalidateAll();
    }

    @Override
    public List<StoredFunction> getAllFunctions() {
        logger.debug("getting all functions");
        return allFunctionsCache.get(this::doFindAllFunctions);
    }

    @Override
    @Nullable
    public StoredFunction getFunctionOrNull(@Nullable String name) {
        if (isBlank(name)) {
            return null;
        } else {
            return functionsCacheByName.get(name, () -> Optional.ofNullable(doGetFunctionOrNull(name))).orElse(null);
        }
    }

    @Nullable
    private StoredFunction doGetFunctionOrNull(String name) {
        return getAllFunctions().stream().filter(equal(StoredFunction::getName, name)).collect(onlyElementOrNull());
    }

    private List<StoredFunction> doFindAllFunctions() {
        List<StoredFunction> functionList = list(jdbcTemplate.query("SELECT * FROM _cm3_function_list_detailed()", new RowMapper<StoredFunctionImpl>() {
            @Override
            public StoredFunctionImpl mapRow(ResultSet rs, int rowNum) throws SQLException {
                String name = "<unknown>";
                try {
                    name = rs.getString("function_name");
                    logger.trace("processing function {}", name);
                    Long id = rs.getLong("function_id");
                    boolean returnsSet = rs.getBoolean("returns_set");
                    FunctionMetadata meta = new FunctionMetadataImpl(parseCommentFromFeatures(fromJson(rs.getString("features"), MAP_OF_STRINGS), FUNCTION_COMMENT_TO_METADATA_MAPPING));
                    return StoredFunctionImpl.builder()
                            .withName(name)
                            .withId(id)
                            .withReturnSet(returnsSet)
                            .withMetadata(meta)
                            .accept((builder) -> addParameters(rs, builder))
                            .build();
                } catch (Exception ex) {
                    logger.error(marker(), "error processing function = {} ", name, ex);
                    return null;
                }
            }

            private void addParameters(ResultSet rs, FunctionBuilder builder) {
                try {
                    String[] argIo = (String[]) rs.getArray("arg_io").getArray();
                    String[] argNames = (String[]) rs.getArray("arg_names").getArray();
                    String[] argTypes = (String[]) rs.getArray("arg_types").getArray();
                    checkArgument(argIo.length == argNames.length && argNames.length == argTypes.length, "arg io, names, types mismatch!");
                    FunctionMetadata meta = builder.getFunctionMetadata();
                    for (int i = 0; i < argIo.length; ++i) {
                        logger.trace("processing function param name = {}, type = {}, io = {}", argNames[i], argTypes[i], argIo[i]);
                        String name = checkNotBlank(argNames[i], "arg name is null"),
                                sqlTypeName = checkNotBlank(argTypes[i], "arg type is null");
                        try {
                            AttributeMetadata attributeMetadata = AttributeMetadataImpl.builder()
                                    .withDescription(meta.getDescription(name))
                                    .withShowInGrid(meta.isBaseDsp(name))
                                    .withTextContentSecurity(meta.getTextContentSecurity(name))
                                    .accept(m -> {
                                        switch (firstNotNull(meta.getTextContentSecurity(name), TCS_PLAINTEXT)) {
                                            case TCS_HTML_ALL, TCS_HTML_SAFE ->
                                                m.withEditorType(AttrEditorType.ET_HTML);
                                        }
                                        map(meta.getCustomMetadata()).filterMapKeys(format("attribute.%s.", name)).forEach(m::withMetadata);
                                    })
                                    .build();
                            String io = checkNotBlank(argIo[i], "arg io is null").toLowerCase();
                            if (isVoidSqlType(sqlTypeName)) {
                                checkArgument(Objects.equals(io, "o"), "invalid void input attribute");
                                logger.trace("skip void output attribute");
                            } else {
                                CardAttributeType<?> type = createAttributeType(sqlTypeName);
                                if (type.isOfType(DECIMAL) && !type.as(DecimalAttributeType.class).hasPrecisionOrScale()) {
                                    type = new DecimalAttributeType(meta.getAttrPrecision(name), meta.getAttrScale(name));
                                } else if (type.isOfType(STRING) && !type.as(StringAttributeType.class).hasLength()) {
                                    type = new StringAttributeType(meta.getAttrLength(name));
                                }
                                switch (io) {
                                    case "i" -> {
                                        logger.trace("add input param name = {} type = {}", name, sqlTypeName);
                                        builder.withInputParameter(name, type);
                                    }
                                    case "o" -> {
                                        if (!isVoidSqlType(sqlTypeName)) {
                                            logger.trace("add output param name = {} type = {}", name, sqlTypeName);
                                            builder.withOutputParameter(name, type, attributeMetadata);
                                        }
                                    }
                                    case "io" -> {
                                        logger.trace("add input/output param name = {} type = {}", name, sqlTypeName);
                                        builder.withInputParameter(name, type);
                                        builder.withOutputParameter(name, type, attributeMetadata);
                                    }
                                    default ->
                                        throw new IllegalArgumentException("unsupported io param = " + io);
                                }
                            }
                        } catch (Exception ex) {
                            throw new DaoException(ex, "error processing function param = %s for function = %s", name, builder.getName());
                        }
                    }
                } catch (SQLException ex) {
                    throw new DaoException(ex);
                }
            }
        })).filter(notNull()).map(StoredFunction.class::cast);
        return functionList;
    }

}
