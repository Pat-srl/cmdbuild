/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Suppliers;
import java.io.File;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.function.Supplier;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.dao.sql.utils.SqlFunction;
import static org.cmdbuild.dao.sql.utils.SqlFunctionUtils.readSqlFunctionsFromDir;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class FunctionCodeRepositoryImpl implements FunctionCodeRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Supplier<File> functionDirSupplier;
    private final Holder<List<SqlFunction>> availableFunctions;

    @Autowired
    public FunctionCodeRepositoryImpl(DirectoryService directoryService, @Qualifier(SYSTEM_LEVEL_ONE) CacheService cacheService) {
        this(() -> directoryService.hasWebappDirectory() ? new File(directoryService.getWebappDirectory(), "WEB-INF/sql/functions") : null, cacheService);
    }

    public FunctionCodeRepositoryImpl(File functionDir, CacheService cacheService) {
        this(Suppliers.ofInstance(checkNotNull(functionDir)), cacheService);
    }

    private FunctionCodeRepositoryImpl(Supplier<File> functionDir, CacheService cacheService) {
        functionDirSupplier = checkNotNull(functionDir);
        availableFunctions = cacheService.newHolder("dao.available_function_code");
    }

    @Override
    public List<SqlFunction> getAvailableFunctions() {
        return availableFunctions.get(this::doGetAvailableFunctions);
    }

    private List<SqlFunction> doGetAvailableFunctions() {
        File functionDir = functionDirSupplier.get();
        if (functionDir != null) {
            return readSqlFunctionsFromDir(functionDir);
        } else {
            logger.warn("no filesystem available, no function code available");
            return emptyList();
        }
    }

}
