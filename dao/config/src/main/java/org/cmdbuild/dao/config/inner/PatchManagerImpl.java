/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.Nullable;
import java.io.StringReader;
import static java.lang.String.format;
import java.util.Collections;
import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.joining;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.cmdbuild.dao.ConfigurableDataSource;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.DatasourceConfiguredEvent;
import org.cmdbuild.dao.postgres.services.PostgresDatabaseAdapterService;
import static org.cmdbuild.utils.date.CmDateUtils.toUserDuration;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.hasContent;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmExceptionUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.utils.lang.CmPreconditions;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.cmdbuild.utils.lang.CmPredicatesUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.addLineNumbers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class PatchManagerImpl implements PatchManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConfigurableDataSource dataSource;
    private final PatchCardRepository patchCardRepository;
    private final PatchFileRepository patchFileRepository;

    private List<Patch> patchesOnDb;
    private List<Patch> patchesFromFiles;
    private List<Patch> availableCorePatches;
    private List<Patch> allAvailablePatches;
    private boolean isReady = false;

    public PatchManagerImpl(ConfigurableDataSource dataSource, PatchCardRepository patchCardRepository, PatchFileRepository patchFileRepository) {
        this.dataSource = checkNotNull(dataSource);
        this.patchCardRepository = checkNotNull(patchCardRepository);
        this.patchFileRepository = checkNotNull(patchFileRepository);
        dataSource.getEventBus().register(new Object() {
            @Subscribe
            public void handleDatasourceConfiguredEvent(DatasourceConfiguredEvent event) {
                reset();
            }
        });
    }

    @Override
    public synchronized void reset() {
        isReady = false;
    }

    @Override
    public synchronized boolean hasPendingPatches() {
        init();
        return hasContent(availableCorePatches);
    }

    @Override
    public synchronized List<Patch> getAvailableCorePatches() {
        init();
        return checkNotNull(availableCorePatches, "unable to get available patches: cannot get patches from db");
    }

    @Nullable
    @Override
    public synchronized String getLastPatchOnDbKeyOrNull() {
        init();
        if (CmCollectionUtils.isNullOrEmpty(patchesOnDb)) {
            return null;
        } else {
            return patchesOnDb.stream().sorted(comparing(Patch::getApplyDate, reverseOrder())).findFirst().get().getKey();
        }
    }

    @Override
    public String getPatchSourcesChecksum() {
        init();
        return hash(patchesFromFiles.stream().sorted(comparing(Patch::getKey, naturalOrder())).map(Patch::getContent).collect(joining("\n\n")));
    }

    @Override
    public synchronized List<PatchInfo> getAllPatches() {
        init();
        logger.debug("getAllPatches");
        Map<String, Patch> patchesOnDbByKey = applyOrNull(patchesOnDb, p -> map(p, Patch::getKey).immutable());
        Map<String, Patch> patchesFromFilesByKey = map(patchesFromFiles, Patch::getKey).immutable();
        List<PatchInfo> list = list();
        for (String key : Sets.newHashSet(FluentIterable.concat(patchesFromFilesByKey.keySet(), patchesOnDbByKey == null ? Collections.emptySet() : patchesOnDbByKey.keySet()))) {
            Patch patchOnDb = patchesOnDbByKey == null ? null : patchesOnDbByKey.get(key);
            Patch patchOnFile = patchesFromFilesByKey.get(key);
            list.add(new PatchInfo() {
                @Override
                public Patch getPatchOnDb() {
                    return patchOnDb;
                }

                @Override
                public Patch getPatchOnFile() {
                    return patchOnFile;
                }
            });
        }
        Collections.sort(list, (a, b) -> ComparisonChain.start().compareTrueFirst(Objects.equal(a.getCategory(), PatchService.DEFAULT_CATEGORY), Objects.equal(b.getCategory(), PatchService.DEFAULT_CATEGORY)).compare(a.getCategory(), b.getCategory()).compare(a.getComparableVersion(), b.getComparableVersion()).result());
        return list;
    }

    @Override
    public synchronized void applyPatchAndStore(Patch patch) {
        init();
        try {
            boolean clearSuperuserAfter = false;
            String sql = preparePatchSqlCode(patch);
            String patchInfo = format("%s (%s)", patch.getKey(), abbreviate(patch.getDescription()));
            if (patch.requiresSuperuser() && dataSource.isNotSuperuser()) {
                try {
                    dataSource.setSuperuser();
                    clearSuperuserAfter = true;
                } catch (Exception ex) {
                    logger.warn("unable to set superuser for patch = {} (this patch may fail later for lack of permissions)", patchInfo, ex);
                }
            }
            try {
                applyPatch(sql, patchInfo);
            } finally {
                if (clearSuperuserAfter) {
                    dataSource.setNoSuperuser();
                }
            }
            if (patch.reloadConnectionAfter()) {
                logger.info("patch requires connection reload: reloading inner jdbc connection");
                dataSource.reloadInner();
            }
            patchCardRepository.store(patch);
            doReloadPatches();
        } catch (Exception ex) {
            throw new DaoException(ex, "error processing patch = %s", patch);
        }
    }

    @Override
    public synchronized void rebuildPatchesHash() {
        doReloadPatches();
        rebuildPatchesHashAndPrintInfo();
    }

    private synchronized void init() {
        if (!isReady) {
            doReloadPatches();
            checkPatchesHashAndPrintWarning();
            isReady = true;
        }
    }

    private void doReloadPatches() {
        logger.debug("reloading patches");
        List<Patch> newPatchesOnDb = patchCardRepository.findAll();
        logger.debug("found {} patches on db", newPatchesOnDb.size());
        List<Patch> newPatchesFromFiles = list();
        patchFileRepository.getPatchFiles().stream().sorted((a, b) -> ComparisonChain.start().compare(Strings.nullToEmpty(a.getCategory()), Strings.nullToEmpty(b.getCategory())).compare(a.getFile().getName(), b.getFile().getName()).result()).forEach((pf) -> {
            String category = CmPreconditions.firstNotBlank(pf.getCategory(), PatchService.DEFAULT_CATEGORY);
            logger.debug("processing patch file = {} category = {}", pf.getFile(), category);
            newPatchesFromFiles.add(PatchImpl.builder().withCategory(category).withFile(pf.getFile()).build());
        });
        List<Patch> newAvailablePatches = list();
        Multimap<String, Patch> patchesOnDbByCategory = Multimaps.index(newPatchesOnDb, Patch::getCategory);
        Multimap<String, Patch> patchesFromFilesByCategory = Multimaps.index(newPatchesFromFiles, Patch::getCategory);
        patchesFromFilesByCategory.keySet().stream().sorted((a, b) -> ComparisonChain.start().compareTrueFirst(Objects.equal(a, PatchService.DEFAULT_CATEGORY), Objects.equal(b, PatchService.DEFAULT_CATEGORY)).compare(a, b).result()).collect(Collectors.toList()).forEach((category) -> {
            ComparableVersion lastAppliedPatchVersion = patchesOnDbByCategory.get(category).stream().sorted(comparing(Patch::getComparableVersion, reverseOrder())).findFirst().map((p) -> p.getComparableVersion()).orElse(null);
            logger.debug("last patch for category = {} on db is = {}", category, lastAppliedPatchVersion);
            patchesFromFilesByCategory.get(category).stream().filter((p) -> lastAppliedPatchVersion == null || p.getComparableVersion().compareTo(lastAppliedPatchVersion) > 0 || (newPatchesOnDb.stream().filter((dbp) -> dbp.getVersion().equals(p.getVersion())).findAny().orElse(null) == null && p.forceApplyIfNotExists())).sorted(comparing(Patch::getComparableVersion, naturalOrder())).forEach(newAvailablePatches::add);
        });
        this.patchesOnDb = ImmutableList.copyOf(newPatchesOnDb);
        this.patchesFromFiles = ImmutableList.copyOf(newPatchesFromFiles);
        this.allAvailablePatches = ImmutableList.copyOf(newAvailablePatches);
        this.availableCorePatches = ImmutableList.copyOf(allAvailablePatches.stream().filter(Patch::isCore).collect(Collectors.toList()));
        logger.debug("loaded patches");
    }

    private void checkPatchesHashAndPrintWarning() {
        patchesOnDb.stream().filter(CmPredicatesUtils.and(Patch::hasHash, Patch::isCore)).forEach((patchOnDb) -> {
            Patch patchOnFile = patchesFromFiles.stream().filter((patch) -> Objects.equal(patch.getKey(), patchOnDb.getKey())).findAny().orElse(null);
            if (patchOnFile == null) {
                if (!patchesFromFiles.isEmpty()) {
                    logger.info("patch = {} \"{}\" exist only on db, not found on file", patchOnDb.getKey(), patchOnDb.getDescription());
                }
            } else if (patchOnDb.hasHash()) {
                if (!Objects.equal(patchOnDb.getHash(), patchOnFile.getHash())) {
                    logger.info("hash mismatch for patch = {} \"{}\" (patch applied on db is different from patch file)", patchOnDb.getKey(), patchOnDb.getDescription());
                    if (patchOnDb.hasContent()) {
                        try {
                            List<String> onDb = IOUtils.readLines(new StringReader(patchOnDb.getContent()));
                            List<String> onFile = IOUtils.readLines(new StringReader(patchOnFile.getContent()));
                            com.github.difflib.patch.Patch<String> patch = DiffUtils.diff(onDb, onFile);
                            List<String> udiff = UnifiedDiffUtils.generateUnifiedDiff(String.format("%s-%s_db.sql", patchOnDb.getCategory(), patchOnDb.getVersion()), String.format("%s-%s_patch.sql", patchOnFile.getCategory(), patchOnFile.getVersion()), onDb, patch, 4);
                            logger.debug("patch file differences:\n\n{}\n", Joiner.on("\n").join(udiff));
                        } catch (Exception ex) {
                            logger.error("error building mismatching patch file diff message", ex);
                        }
                    }
                } else {
                    logger.debug("unable to test hash of patch = {} \"{}\", patch hash not found on db", patchOnDb.getKey(), patchOnDb.getDescription());
                }
            }
        });
    }

    private void rebuildPatchesHashAndPrintInfo() {
        patchesOnDb.stream().filter(Patch::hasHash).forEach((patchOnDb) -> {
            Patch patchOnFile = patchesFromFiles.stream().filter((patch) -> Objects.equal(patch.getKey(), patchOnDb.getKey())).findAny().orElse(null);
            if (patchOnFile == null) {
                if (!patchesFromFiles.isEmpty()) {
                    logger.info("patch = {} \"{}\" exist only on db, not found on file", patchOnDb.getKey(), patchOnDb.getDescription());
                }
            } else if (patchOnDb.hasHash()) {
                if (!Objects.equal(patchOnDb.getHash(), patchOnFile.getHash())) {
                    logger.info("hash mismatch for patch = {} \"{}\", rebuilding hash and content", patchOnDb.getKey(), patchOnDb.getDescription());
                    patchCardRepository.rebuildPatch(patchOnFile);
                }
            }
        });
    }

    private void applyPatch(String sql, String patchInfo) {
        logger.info("applying patch {}", patchInfo);
        Stopwatch stopwatch = Stopwatch.createStarted();
        String sqlToRun = String.format("\nSET SESSION %s = 'true';\nSET SESSION %s = '{}';\n\n%s", PostgresDatabaseAdapterService.PG_IGNORE_TENANT_POLICIES, PostgresDatabaseAdapterService.PG_USER_TENANTS, sql);
        CompletableFuture<Void> future = new CompletableFuture<>();
        TransactionTemplate transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource)); //TODO check transaction manages lookup (?)
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    new JdbcTemplate(dataSource).execute(sqlToRun);
                    future.complete(null);
                } catch (Exception e) {
                    logger.debug("error processing patch =< {} >", patchInfo);
                    logger.debug("error processing patch", e);
                    status.setRollbackOnly();
                    future.completeExceptionally(e);
                }
            }
        });
        try {
            future.get();
        } catch (Exception ex) {
            logger.warn("error processing patch =< {} >, failed code =\n\n{}\n", patchInfo, addLineNumbers(sqlToRun));
            throw new DaoException(CmExceptionUtils.inner(ex), "error processing patch =< %s >", patchInfo);
        }
        if (stopwatch.elapsed(TimeUnit.SECONDS) > 10) {
            logger.info("applied patch = {}, elapsed time = {}", patchInfo, toUserDuration(stopwatch.elapsed(TimeUnit.MILLISECONDS)));
        } else {
            logger.debug("applied patch = {}, elapsed time = {}", patchInfo, toUserDuration(stopwatch.elapsed(TimeUnit.MILLISECONDS)));
        }
    }

    private String preparePatchSqlCode(Patch patch) {
        return String.format("DO $$ BEGIN RAISE NOTICE 'apply cmdbuild patch %%', '%s'; END $$ LANGUAGE PLPGSQL;\n\n%s\n\nDO $$ BEGIN RAISE NOTICE 'applied cmdbuild patch %%', '%s'; END $$ LANGUAGE PLPGSQL;\n\n",
                patch.getKey(), patch.isNotSql() ? "SELECT 1;" : checkNotBlank(patch.getContent(), "patch content is blank"), patch.getKey());
    }

}
