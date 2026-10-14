/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.plugin.config.dao;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang.reflect.FieldUtils;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.plugin.SystemLoadingPluginConfigEvent;
import org.cmdbuild.plugin.config.PluginConfigAnnotationProcessorService;
import org.cmdbuild.plugin.config.api.PluginConfigValue;
import static org.cmdbuild.plugin.config.api.PluginConfigValue.NULL;
import static org.cmdbuild.plugin.config.utils.PluginConfigUtils.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElementOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNullOrNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 *
 * @author ataboga
 */
@Component
public class PluginConfigRepositoryImpl implements PluginConfigRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final PluginConfigAnnotationProcessorService pluginConfigAnnotationProcessorService;
    private final EventBus eventBus;

    public static final DaoQueryOptions PLUGIN_CONFIG_ATTR_MAPPING = DaoQueryOptionsImpl.emptyOptions().mapAttrNames(map(
            "plugin", "Code",
            "key", "Key",
            "value", "Value",
            "access", "Access"
    ));

    public List<PluginConfigData> allDefaultPluginConfigs = list();

    public PluginConfigRepositoryImpl(DaoService dao, PluginConfigAnnotationProcessorService pluginConfigAnnotationProcessorService, EventBusService eventBusService) {
        this.dao = checkNotNull(dao);
        this.pluginConfigAnnotationProcessorService = checkNotNull(pluginConfigAnnotationProcessorService);
        this.eventBus = eventBusService.getSystemEventBus();
        eventBus.register(new Object() {
            @Subscribe
            public void handlePluginConfigs(SystemLoadingPluginConfigEvent event) {
                logger.info("load plugin config from db");
                updatePluginConfigs();
            }
        });
    }

    private synchronized void updatePluginConfigs() {
        allDefaultPluginConfigs.clear();
        try {
            pluginConfigAnnotationProcessorService.getPluginConfigBeans().forEach(bean -> {
                ReflectionUtils.doWithFields(bean.getClass(), field -> {
                    if (field.isAnnotationPresent(PluginConfigValue.class)) {
                        String pluginName = checkNotNull(getPluginNameFromBean(bean.getClass()), "plugin name for bean =< %s > not set", bean.getClass().getName());
                        String pluginKey = checkNotNull(getPluginConfigExtendedKey(field), "key =< %s > for plugin =< %s > not set", field.getName(), pluginName);
                        PluginConfigData pluginConfig = getValueByPluginAndKeyOrNull(pluginName, pluginKey);
                        if (pluginConfig == null) {
                            String value = field.getAnnotation(PluginConfigValue.class).defaultValue();
                            if (Objects.equals(value, NULL)) {
                                value = null;
                            }
                            pluginConfig = PluginConfigData.builder()
                                    .withPluginName(pluginName)
                                    .withKey(pluginKey)
                                    .withValue(value)
                                    .withAccess(parseEnum(getPluginConfigAccess(field), PluginConfigAccess.class))
                                    .build();
                            allDefaultPluginConfigs.add(pluginConfig);
                        }
                        FieldUtils.writeField(field, bean, convert(pluginConfig.getValue(), field.getGenericType()), true);
                    }
                });
            });
        } catch (Exception ex) {
            logger.error("error getting configurations from plugin beans", ex);
        }

    }

    @Override
    public List<PluginConfigData> getAll() {
        return getAllByPlugin(null);
    }

    @Override
    public List<PluginConfigData> getAllByPlugin(@Nullable String plugin) {
        if (isNotBlank(plugin)) {
            return list(allDefaultPluginConfigs).filter(equal(PluginConfigData::getPluginName, plugin)).with(dao.selectAll().from(PluginConfigData.class).where(ATTR_CODE, EQ, plugin).withOptions(PLUGIN_CONFIG_ATTR_MAPPING).asList());
        }
        return list(allDefaultPluginConfigs).with(dao.selectAll().from(PluginConfigData.class).withOptions(PLUGIN_CONFIG_ATTR_MAPPING).asList());
    }

    @Nullable
    @Override
    public PluginConfigData getValueByPluginAndKeyOrNull(String plugin, String key) {
        return firstNotNullOrNull(
                dao.selectAll().from(PluginConfigData.class)
                        .where(ATTR_CODE, EQ, plugin)
                        .where("Key", EQ, key)
                        .withOptions(PLUGIN_CONFIG_ATTR_MAPPING).getOneOrNull(),
                list(allDefaultPluginConfigs).filter(equal(PluginConfigData::getPluginName, plugin)).filter(equal(PluginConfigData::getKey, key)).collect(onlyElementOrNull())
        );
    }

    @Override
    public PluginConfigData getValueByPluginAndKey(String plugin, String key) {
        return checkNotNull(getValueByPluginAndKeyOrNull(plugin, key), "config =< %s > not found for plugin =< %s >", plugin, key);
    }

    @Override
    public PluginConfigData create(PluginConfigData pluginConfigData) {
        PluginConfigData config = dao.create(pluginConfigData);
        eventBus.post(new SystemLoadingPluginConfigEvent(config.getPluginName(), config.getKey()));
        return config;
    }

    @Override
    public PluginConfigData update(PluginConfigData pluginConfigData) {
        PluginConfigData config = dao.update(pluginConfigData);
        eventBus.post(new SystemLoadingPluginConfigEvent(config.getPluginName(), config.getKey()));
        return config;
    }

    @Override
    public void delete(PluginConfigData pluginConfigData) {
        dao.delete(pluginConfigData);
        eventBus.post(new SystemLoadingPluginConfigEvent(pluginConfigData.getPluginName(), pluginConfigData.getKey()));
    }
}
