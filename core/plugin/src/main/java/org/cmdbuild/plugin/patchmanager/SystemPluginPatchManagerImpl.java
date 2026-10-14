/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.plugin.patchmanager;

import static java.lang.String.format;
import java.nio.charset.StandardCharsets;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import java.util.List;
import java.util.Objects;
import static java.util.stream.Collectors.joining;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.dao.config.inner.Patch;
import org.cmdbuild.dao.config.inner.PatchImpl;
import org.cmdbuild.dao.config.inner.PatchInfo;
import org.cmdbuild.dao.config.inner.PatchManager;
import org.cmdbuild.modeldiff.core.SerializationHandle_String;
import org.cmdbuild.modeldiff.schema.SchemaCollector;
import static org.cmdbuild.plugin.patchmanager.SystemPluginPathType.SPPT_SCHEMA;
import static org.cmdbuild.plugin.patchmanager.SystemPluginPathType.SPPT_SQL;
import static org.cmdbuild.plugin.patchmanager.SystemPluginPathType.SPPT_TRANSLATION;
import org.cmdbuild.systemplugin.SystemPlugin;
import org.cmdbuild.systemplugin.SystemPluginService;
import org.cmdbuild.translation.TranslationService;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.lang.CmCollectionUtils.getOnlyElementOrNull;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public class SystemPluginPatchManagerImpl implements SystemPluginPatchManager {

    private final static String NOT_SQL_PARAM = "-- PARAMS: NOT_SQL=true\n\n";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final PatchManager patchManager;
    private final SchemaCollector schemaCollector;
    private final TranslationService translationService;
    private final CacheService cacheService;

    public SystemPluginPatchManagerImpl(SystemPluginService systemPluginService, PatchManager patchManager, List<SchemaCollector> schemaCollector, TranslationService translationService, CacheService cacheService) {
        this.patchManager = checkNotNull(patchManager);
        this.schemaCollector = getOnlyElementOrNull(schemaCollector);
        this.translationService = checkNotNull(translationService);
        this.cacheService = checkNotNull(cacheService);
    }

    @Override
    public void applyPatches(SystemPlugin plugin) {
        if (hasPatches(plugin)) {
            checkPatches(plugin);
            getPatchesOnFile(plugin).forEach(patch -> {
                if (patch.isNotSql()) {
                    String patchContent = StringUtils.remove(patch.getContent(), NOT_SQL_PARAM);
                    switch (getPatchType(patch.getVersion())) {
                        case SPPT_SCHEMA ->
                            schemaCollector.applySchemaDiff(new SerializationHandle_String(patchContent));
                        case SPPT_TRANSLATION -> {
                            translationService.importHelper().importTranslations(newDataHandler(patchContent, "text/csv"));
                        }
                    }
                }
                patchManager.applyPatchAndStore(patch);
            });
            cacheService.invalidateAll();
        }
    }

    @Override
    public List<Patch> getPatchesOnDb(SystemPlugin plugin) {
        return list(patchManager.getAllPatches()).filter(p -> p.hasPatchOnDb() && p.getCategory().equals(getCategoryPatch(plugin))).map(PatchInfo::getPatch);
    }

    @Override
    public List<Patch> getPatchesOnFile(SystemPlugin plugin) {
        return list(plugin.getResources("patches", "sql", "json", "csv").entrySet()).filter(e -> !list(getPatchesOnDb(plugin)).map(Patch::getVersion).contains(e.getKey())).map(entry -> {
            return PatchImpl.builder()
                    .withVersion(entry.getKey())
                    .withDescription(entry.getKey())
                    .withCategory(getCategoryPatch(plugin))
                    .accept(p -> {
                        String patchContent = new String(entry.getValue(), StandardCharsets.UTF_8);
                        if (!Objects.equals(getPatchType(entry.getKey()), SPPT_SQL)) {
                            patchContent = NOT_SQL_PARAM + patchContent;
                        }
                        p.withContent(patchContent);
                    })
                    .build();
        }).sorted(comparing(Patch::getComparableVersion, naturalOrder()));
    }

    @Override
    public boolean hasPatches(SystemPlugin plugin) {
        return !getPatchesOnFile(plugin).isEmpty();
    }

    private String getCategoryPatch(SystemPlugin plugin) {
        return format("plugin_%s", firstNotNull(plugin.getService(), plugin.getName()));
    }

    private SystemPluginPathType getPatchType(String filename) {
        return switch (FilenameUtils.getExtension(filename)) {
            case "sql" ->
                SPPT_SQL;
            case "json" ->
                SPPT_SCHEMA;
            case "csv" ->
                SPPT_TRANSLATION;
            default ->
                throw unsupported("this plugin patch =< %s > is not supported", filename);
        };
    }

    private void checkPatches(SystemPlugin plugin) {
        List<Patch> schemaJson = list(getPatchesOnFile(plugin)).filter(patch -> patch.isNotSql() && Objects.equals(getPatchType(patch.getVersion()), SPPT_SCHEMA));
        checkArgument(schemaJson.isEmpty() || schemaCollector != null, "CMO 901: cannot apply patches because model diff schema plugin is not available\n\npatches =< %s >", schemaJson.stream().map(Patch::getVersion).collect(joining(", ")));
    }
}
