/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.plugin.config;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import static org.cmdbuild.plugin.config.dao.PluginConfigAccess.PCA_PUBLIC;
import org.cmdbuild.plugin.config.dao.PluginConfigData;
import org.cmdbuild.plugin.config.dao.PluginConfigRepository;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public class PluginConfigServiceImpl implements PluginConfigService {

    private final PluginConfigRepository pluginConfigRepository;

    public PluginConfigServiceImpl(PluginConfigRepository pluginConfigRepository) {
        this.pluginConfigRepository = checkNotNull(pluginConfigRepository);
    }

    @Override
    public List<Map<String, String>> getAll() {
        return list(pluginConfigRepository.getAll()).map(pc -> mapOf(String.class, String.class).with("plugin", pc.getPluginName()).with(serializePluginConfig(pc)));
    }

    @Override
    public List<Map<String, String>> getAllByPlugin(String plugin) {
        return list(pluginConfigRepository.getAllByPlugin(plugin))
                .map(this::serializePluginConfig);
    }

    @Override
    public String getValueByPluginAndKey(String plugin, String key) {
        return pluginConfigRepository.getValueByPluginAndKey(plugin, key).getValue();
    }

    @Override
    public PluginConfigData getConfigByPluginAndKeyOrNull(String plugin, String key) {
        return pluginConfigRepository.getValueByPluginAndKeyOrNull(plugin, key);
    }

    @Override
    public List<Map<String, String>> getAllPublicByPlugin(String plugin) {
        return list(pluginConfigRepository.getAllByPlugin(plugin))
                .filter(equal(PluginConfigData::getAccess, PCA_PUBLIC))
                .map(this::serializePluginConfig);
    }

    @Override
    public String getPublicValueByPluginAndKey(String plugin, String key) {
        PluginConfigData config = pluginConfigRepository.getValueByPluginAndKey(plugin, key);
        checkArgument(Objects.equals(config.getAccess(), PCA_PUBLIC), "config =< %s > is not public", key);
        return config.getValue();
    }

    @Override
    public PluginConfigData createConfig(PluginConfigData config) {
        return pluginConfigRepository.create(config);
    }

    @Override
    public List<PluginConfigData> createOrUpdateConfigs(List<PluginConfigData> configs) {
        return listOf(PluginConfigData.class).accept(cu_configs -> {
            configs.forEach(config -> {
                PluginConfigData pluginConfigData = pluginConfigRepository.getValueByPluginAndKeyOrNull(config.getPluginName(), config.getKey());
                cu_configs.add(pluginConfigData == null || pluginConfigData.getId() == null ? createConfig(config) : updateConfig(config));
            });
        });
    }

    @Override
    public PluginConfigData updateConfig(PluginConfigData config) {
        Long configId = pluginConfigRepository.getValueByPluginAndKey(config.getPluginName(), config.getKey()).getId();
        if (configId == null) {
            return createConfig(config); // config does not exist on db (default config from PluginConfigValue
        }
        config = PluginConfigData.copyOf(config).withId(configId).build();
        return pluginConfigRepository.update(config);
    }

    @Override
    public void deleteConfig(String plugin, String key) {
        PluginConfigData config = pluginConfigRepository.getValueByPluginAndKey(plugin, key);
        pluginConfigRepository.delete(config);
    }

    public Map<String, String> serializePluginConfig(PluginConfigData pluginConfig) {
        return map("key", pluginConfig.getKey(),
                "value", pluginConfig.getValue(),
                "access", pluginConfig.getAccess()
        );
    }
}
