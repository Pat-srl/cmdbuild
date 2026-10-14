/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api.inner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.api.LookupApi;
import org.cmdbuild.api.PluginApi;
import org.cmdbuild.api.SchemaCollectorApi;
import org.cmdbuild.api.SqlApi;
import org.cmdbuild.api.SystemApi;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupValueImpl;
import org.cmdbuild.modeldiff.schema.SchemaCollector;
import org.cmdbuild.platform.PlatformService;
import org.cmdbuild.plugin.config.PluginConfigService;
import org.cmdbuild.script.ScriptService;
import org.cmdbuild.system.ReloadService;
import org.cmdbuild.systemplugin.SystemPlugin;
import org.cmdbuild.systemplugin.SystemPluginService;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SystemApiImpl implements SystemApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GlobalConfigService configService;
    private final DaoService dao;
    private final ApplicationContext applicationContext;
    private final CacheService cacheService;
    private final LookupService lookupService;
    private final ReloadService reloadService;
    private final ScriptService scriptService;
    private final PlatformService platformService;
    private final SystemPluginService pluginService;
    private final PluginConfigService pluginConfigService;

    private final SqlApi sqlApi = new SqlApiImpl();
    private final LookupApi lookupApi = new LookupApiImpl();
    private final PluginApi pluginApi = new PluginApiImpl();

    private final SchemaCollectorApi schemaCollectorApi;

    public SystemApiImpl(GlobalConfigService configService, DaoService dao, ApplicationContext applicationContext, CacheService cacheService, LookupService lookupService, ReloadService reloadService, ScriptService scriptService, PlatformService platformService, SystemPluginService pluginService, PluginConfigService pluginConfigService, List<SchemaCollector> schemaCollectors) {
        this.configService = checkNotNull(configService);
        this.dao = checkNotNull(dao);
        this.applicationContext = checkNotNull(applicationContext);
        this.cacheService = checkNotNull(cacheService);
        this.lookupService = checkNotNull(lookupService);
        this.reloadService = checkNotNull(reloadService);
        this.scriptService = checkNotNull(scriptService);
        this.platformService = checkNotNull(platformService);
        this.pluginService = checkNotNull(pluginService);
        this.pluginConfigService = checkNotNull(pluginConfigService);

        if (schemaCollectors.isEmpty()) {
            logger.info("loading **dummy** SchemaCollector");
            schemaCollectorApi = SchemaCollectorApiImpl.DUMMY;
        } else {
            // (there is only one service implementation, indeed...)
            logger.info("loading real SchemaCollector =< {} >", schemaCollectors.get(0).getClass().getName());
            schemaCollectorApi = new SchemaCollectorApiImpl(schemaCollectors.get(0));
        }
    }

    @Override
    public <T> T getService(String name) {
        return (T) applicationContext.getBean(name, Object.class);
    }

    @Override
    public <T> T getService(Class<T> classe) {
        return applicationContext.getBean(classe);
    }

    @Override
    @Nullable
    public String getSystemConfig(String key) {
        return configService.getStringOrDefault(key);
    }

    @Override
    public Map<String, String> getConfig() {
        return configService.getConfigOrDefaultsAsMap();
    }

    @Override
    public void setConfig(Map<String, String> config) {
        configService.putStrings(config);
    }

    @Override
    public SqlApi sql() {
        return sqlApi;
    }

    @Override
    public LookupApi lookup() {
        return lookupApi;
    }

    @Override
    public PluginApi plugin() {
        return pluginApi;
    }

    @Override
    public SchemaCollectorApi schemaCollector() {
        return schemaCollectorApi;
    }

    @Override
    public void reload() {
        reloadService.refreshAndReload();
    }

    @Override
    public void restart() {
        platformService.restartContainer();
    }

    @Override
    public void shutdown() {
        platformService.stopContainer();
    }

    @Override
    public void dropCache(String cache) {
        checkNotBlank(cache);
        List<String> list = cacheService.getAll().keySet().stream().filter(c -> c.startsWith(cache)).collect(toImmutableList());
        checkArgument(!list.isEmpty(), "cache not found for name/prefix =< %s >", cache);
        list.forEach(cacheService::invalidate);
    }

    @Override
    public Object eval(String script, Map<String, Object> data) {
        return scriptService.helper(getClass(), script).executeForOutput(data);
    }

    private class SqlApiImpl implements SqlApi {

        @Override
        public List<Map<String, Object>> query(String query) {
            logger.debug("execute query =< {} >", query);
            List<Map<String, Object>> list = dao.getJdbcTemplate().query(query, (r, n) -> mapOf(String.class, Object.class).accept(rethrowConsumer(m -> {
                for (int i = 1; i <= r.getMetaData().getColumnCount(); i++) {
                    String columnName = r.getMetaData().getColumnLabel(i);
                    Object value = r.getObject(i);
                    m.put(columnName, value);
                }
            })));
            logger.debug("return query result ( {} records )", list.size());
            if (logger.isTraceEnabled()) {
                list.forEach(m -> logger.trace("result record = \n\n{}\n", mapToLoggableString(m)));
            }
            return list;
        }

        @Override
        public void execute(String query) {
            logger.debug("execute query =< {} >", query);
            dao.getJdbcTemplate().execute(query);
        }

    }

    private class LookupApiImpl implements LookupApi {

        @Override
        public boolean exists(String lookupType, String lookupCode) {
            return lookupService.getLookupByTypeAndCodeOrNull(lookupType, lookupCode) != null;
        }

        @Override
        public void create(String lookupType, String code, String description) {
            lookupService.createOrUpdateLookup(LookupValueImpl.builder().withType(lookupService.getLookupType(lookupType)).withCode(code).withDescription(description).build());
        }

    }

    private class PluginApiImpl implements PluginApi {

        @Override
        public Map<String, String> getPlugins() {
            return pluginService.getSystemPlugins().stream().collect(toMap(SystemPlugin::getName, SystemPlugin::getService));
        }

        @Override
        public String getConfig(String plugin, String config) {
            return pluginConfigService.getValueByPluginAndKey(plugin, config);
        }

        @Override
        public Map<String, Object> getConfigs(String plugin) {
            return pluginConfigService.getAllByPlugin(plugin).stream().collect(toMap(p -> p.get("key"), p -> p.get("value")));
        }
    }
}
