/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.utils;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.cache.LocalCacheServiceImpl;
import org.cmdbuild.dao.ConfigurableDataSource;
import org.cmdbuild.dao.config.DummyConfigurableDataSource;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import org.cmdbuild.dao.config.inner.FunctionCardRepository;
import org.cmdbuild.dao.config.inner.FunctionCardRepositoryImpl;
import org.cmdbuild.dao.config.inner.FunctionCodeRepository;
import org.cmdbuild.dao.config.inner.FunctionCodeRepositoryImpl;
import org.cmdbuild.dao.config.inner.FunctionManager;
import org.cmdbuild.dao.config.inner.FunctionManagerImpl;
import org.cmdbuild.dao.config.inner.PatchCardRepository;
import org.cmdbuild.dao.config.inner.PatchCardRepositoryImpl;
import org.cmdbuild.dao.config.inner.PatchFileRepository;
import org.cmdbuild.dao.config.inner.PatchFileRepositoryImpl;
import org.cmdbuild.dao.config.inner.PatchManager;
import org.cmdbuild.dao.config.inner.PatchManagerImpl;
import org.cmdbuild.dao.config.inner.PatchService;
import org.cmdbuild.dao.config.inner.PatchServiceImpl;
import org.cmdbuild.debuginfo.DummyBuildInfoService;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.EventBusUtils.logExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatchManagerUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void applyPatches(DatabaseCreator databaseCreator, File sqlDir) {
        buildAndInitPatchManager(databaseCreator, sqlDir).applyPendingPatchesAndFunctions();
    }

    public static PatchService buildAndInitPatchManager(DatabaseCreator databaseCreator, File sqlDir) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(databaseCreator.getCmdbuildDataSource());
        PatchFileRepository patchFileRepository = new PatchFileRepositoryImpl(buildPatchFileDirs(sqlDir));
        PatchCardRepository patchCardRepository = new PatchCardRepositoryImpl(jdbcTemplate);
        ConfigurableDataSource dataSource = new DummyConfigurableDataSource(databaseCreator);
        FunctionCardRepository functionCardRepository = new FunctionCardRepositoryImpl(jdbcTemplate, DummyBuildInfoService.INSTANCE);
        FunctionCodeRepository functionCodeRepository = getFunctionCodeRepository(sqlDir);
        FunctionManager functionManager = new FunctionManagerImpl(functionCodeRepository, functionCardRepository, jdbcTemplate);
        PatchManager patchManager = new PatchManagerImpl(dataSource, patchCardRepository, patchFileRepository);
        EventBus eventBus = new EventBus(logExceptions(LOGGER));
        return new PatchServiceImpl(patchManager, dataSource, functionManager, eventBus, () -> eventBus);
    }

    public static FunctionCodeRepository getFunctionCodeRepository(File sqlDir) {
        return new FunctionCodeRepositoryImpl(new File(sqlDir, "functions"), new LocalCacheServiceImpl());
    }

    public static List<Pair<File, String>> buildPatchFileDirs(File sqlDir) {
        checkNotNull(sqlDir);
        List<Pair<File, String>> dirs = list();
        dirs.add(Pair.of(new File(sqlDir, "patches"), null));
        list(new File(sqlDir, "patches-ext")).stream().filter(File::exists).forEach((ext) -> {
            list(ext.listFiles()).stream().filter(File::isDirectory).forEach((d) -> dirs.add(Pair.of(d, d.getName())));
        });
        return dirs;
    }

}
