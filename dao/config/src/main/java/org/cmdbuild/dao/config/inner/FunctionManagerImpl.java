/*
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Maps.uniqueIndex;
import static java.lang.String.format;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.dao.DaoException;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.dao.sql.utils.SqlFunction;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class FunctionManagerImpl implements FunctionManager {

    private final static String SYSTEM_FUNCTION_CATEGORY = "system";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final FunctionCodeRepository codeRepository;
    private final FunctionCardRepository cardRepository;
    private final JdbcTemplate jdbcTemplate;

    public FunctionManagerImpl(FunctionCodeRepository codeRepository, FunctionCardRepository cardRepository, @Qualifier(SYSTEM_LEVEL_ONE) JdbcTemplate jdbcTemplate) {
        this.codeRepository = checkNotNull(codeRepository);
        this.cardRepository = checkNotNull(cardRepository);
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
    }

    @Override
    public void upgradeFunctionsWithPatches(Collection<Patch> patchesOnDb) {
        List<SqlFunction> availableFunctions = getPendingFunctions().stream().filter(f -> patchesOnDb.stream().anyMatch(p -> p.getComparableVersion().compareTo(f.getRequiredPatchVersionAsComparableVersion()) >= 0)).collect(toList()); //TODO use category+version (?)
        if (!availableFunctions.isEmpty()) {
            logger.debug("found {} functions available for upgrade", availableFunctions.size());
            availableFunctions.forEach(this::upgradeFunction);
        }
    }

    @Override
    public boolean hasPendingFunctions() {
        return !getPendingFunctions().isEmpty();
    }

    private void upgradeFunction(SqlFunction function) {
        checkNotNull(function);
        logger.info("upgrade function = {}", function.getSignature());
        try {
            String sql = buildFunctionUpgradeSqlScript(function);
            jdbcTemplate.execute(sql);
            cardRepository.update(SYSTEM_FUNCTION_CATEGORY, function);
        } catch (Exception ex) {
            throw new DaoException(ex, "error upgrading function = %s", function.getSignature());
        }
    }

    private List<SqlFunction> getPendingFunctions() {
        List<SqlFunction> availableFunctions = codeRepository.getAvailableFunctions();
        Map<String, SqlFunction> availableFunctionsByHash = uniqueIndex(availableFunctions, SqlFunction::getHash);
        Map<String, SqlFunction> appliedFunctions = map(transformValues(cardRepository.getFunctions(SYSTEM_FUNCTION_CATEGORY), f -> firstNotNull(availableFunctionsByHash.get(f.getHash()), f))); //TODO improve this (handling of multiple function versions with different version requirements)
        return availableFunctions.stream().filter(f -> {
            SqlFunction current = appliedFunctions.get(f.getSignature());
            if (current == null) {
                return true;
            } else {
                return !equal(f.getHash(), current.getHash()) && f.getRequiredPatchVersionAsComparableVersion().compareTo(current.getRequiredPatchVersionAsComparableVersion()) >= 0;
            }
        }).collect(toList());
    }

    public static String buildFunctionUpgradeSqlScript(SqlFunction function) {
        return format("\nDO $_cm_function_upgrade_$ BEGIN\n\n%s\n\nEXCEPTION WHEN invalid_function_definition THEN\nDROP FUNCTION IF EXISTS %s;\n\n%s\n\nEND $_cm_function_upgrade_$ LANGUAGE PLPGSQL;\nCOMMENT ON FUNCTION %s IS %s;\n",
                function.getFunctionDefinition(), function.getSignature(), function.getFunctionDefinition(), function.getSignature(), systemToSqlExpr(nullToEmpty(function.getComment())));
    }

}
