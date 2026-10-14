/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.eventbus.Subscribe;
import static java.lang.String.format;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.cmdbuild.config.api.ConfigBeanRepository.ConfigServiceHelper;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigDefinition;
import org.cmdbuild.config.api.ConfigListener;
import org.cmdbuild.config.api.ConfigService;
import org.cmdbuild.config.api.ConfigUpdateEvent;
import org.cmdbuild.config.api.ConfigValue;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.config.api.NamespacedConfigService;
import static org.cmdbuild.config.utils.ConfigDefinitionUtils.parseBeanForConfigDefinitions;
import static org.cmdbuild.config.utils.ConfigUtils.addNamespaceToKey;
import org.cmdbuild.utils.encode.CmPackUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.getEnumParamsFromType;
import static org.cmdbuild.utils.lang.CmConvertUtils.getFirstTypeArgOfParametrizedType;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmReflectionUtils.executeMethod;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;
import static org.cmdbuild.utils.url.CmUrlUtils.decodeUrlParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
public class ConfigAnnotationProcessorService implements BeanPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConfigListenerBeansRepositoryImpl configListenerBeansRepository;
    private final ConfigBeanRepositoryImpl repository;

    public ConfigAnnotationProcessorService(ConfigBeanRepositoryImpl repository, ConfigListenerBeansRepositoryImpl configListenerBeansRepository) {
        this.repository = checkNotNull(repository);
        this.configListenerBeansRepository = checkNotNull(configListenerBeansRepository);
        logger.debug("ready");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(ConfigComponent.class)) {
            repository.addBean(new ConfigServiceHelperImpl(bean, beanName));
        }
        scanForConfigListenerMethods(bean);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private void scanForConfigListenerMethods(Object bean) {
        list(bean.getClass().getMethods()).stream().filter(m -> m.getAnnotation(ConfigListener.class) != null).forEach(m -> {
            configureConfigListener(bean, m);
        });
    }

    private void configureConfigListener(Object bean, Method method) {
        checkArgument(method.getParameterCount() == 0, "unsupported ConfigListener annotation usage on method = %s with non-null param count", method);
        configListenerBeansRepository.addBean(new ConfigListenerHelperImpl(format("%s.%s", method.getDeclaringClass().getName(), method.getName()), method.getAnnotation(ConfigListener.class), () -> executeMethod(bean, method)));
    }

    private static class ConfigListenerHelperImpl implements ConfigListenerBean {

        private final String description;
        private final ConfigListener annotation;
        private final Runnable callback;

        public ConfigListenerHelperImpl(String description, ConfigListener annotation, Runnable callback) {
            this.description = checkNotBlank(description);
            this.annotation = checkNotNull(annotation);
            this.callback = checkNotNull(callback);
        }

        @Override
        public ConfigListener getAnnotation() {
            return annotation;
        }

        @Override
        public void notifyUpdate() {
            callback.run();
        }

        @Override
        public String toString() {
            return "ConfigListenerHelperImpl{" + "description=" + description + '}';
        }

    }

    private class ConfigServiceHelperImpl implements ConfigServiceHelper {

        private final Object bean;
        private final String beanName;
        private final Class type;
        private final ConfigComponent annotation;
        private final String namespace;

        private final Map<String, Field> fieldsToAutowire = map();
        private final Map<String, Method> methodsToAutowire = map();

        private NamespacedConfigService localConfigService;

        public ConfigServiceHelperImpl(Object bean, String beanName) {
            this.bean = checkNotNull(bean);
            this.beanName = checkNotNull(beanName);
            this.type = bean.getClass();
            this.annotation = (ConfigComponent) type.getAnnotation(ConfigComponent.class);
            this.namespace = checkNotBlank(annotation.value());
        }

        @Override
        public void processBean(GlobalConfigService configAccessService) {
            try {
                logger.trace("processing bean = {} {} with namespace =< {} >", beanName, bean, namespace);
                localConfigService = configAccessService.getConfig(namespace);
                parseMethodsAndFieldsAndLoadDefaults();
                if (annotation.module() != true) {
                    loadConfigs();//load defaults
                    localConfigService.getEventBus().register(new Object() {

                        @Subscribe
                        public void handleConfigUpdateEvent(ConfigUpdateEvent event) {
                            loadConfigs();
                        }
                    });
                    localConfigService.getEventBus().register(bean);
                }
            } catch (Exception e) {
                throw runtime(e, "error processing bean = %s %s with namespace =< %s >", beanName, bean, namespace);
            }
        }

        @Override
        public String getNamespace() {
            return namespace;
        }

        @Override
        public Class getType() {
            return type;
        }

        @Override
        public ConfigComponent getAnnotation() {
            return annotation;
        }

        @Override
        public Object getBean() {
            return bean;
        }

        private void parseMethodsAndFieldsAndLoadDefaults() {
            parseBeanForConfigDefinitions(bean.getClass(), localConfigService::addDefinition, (d, f) -> {
                logger.debug("autowiring field = {}.{} for key = {}", beanName, f.getName(), d.getKey());
                fieldsToAutowire.put(d.getKey(), f);
            }, (d, m) -> {
                logger.debug("autowiring method = {}.{} for key = {}", beanName, m.getName(), d.getKey());
                methodsToAutowire.put(d.getKey(), m);
            });
            ReflectionUtils.doWithFields(bean.getClass(), (field) -> {
                if (field.isAnnotationPresent(ConfigService.class)) {
                    logger.debug("autowiring local config service field = {}.{} for namespace = {}", beanName, field.getName(), namespace);
                    FieldUtils.writeField(field, bean, localConfigService, true);
                }
            });
        }

        private void loadConfigs() {
            try {
                fieldsToAutowire.entrySet().forEach(entry -> setConfigForField(entry.getKey(), entry.getKey(), entry.getValue(), bean));
                methodsToAutowire.entrySet().forEach(entry -> {
                    Method method = entry.getValue();
                    String key = entry.getKey();
                    Object value = localConfigService.getOrDefault(key, method.getParameterTypes()[0]);
                    value = unpackIfPacked(value);
                    logger.debug("invoke config method = {}.{} with value = {} for key = {}", beanName, method.getName(), value, key);
                    try {
                        method.invoke(bean, value);
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    } catch (InvocationTargetException ex) {
                        logger.error("error setting config value for method = " + method + " and value = " + value, ex.getCause());
                    }
                });
            } catch (Exception ex) {
                logger.error("error setting config value", ex);
            }
        }

        private void setConfigForField(String key, String defaultKey, Field field, Object bean) {
            try {
                String modular = field.getAnnotation(ConfigValue.class).modular();
                Object value;
                if (isBlank(modular)) {
                    value = localConfigService.getOrDefault(key, defaultKey, field.getType());
                } else {
                    value = localConfigService.getOrDefault(key, defaultKey, List.class);
                }
                doSetConfigForField(key, field, value, bean);
            } catch (Exception ex) {
                logger.error("error setting config value for key =< {} > field = {}", key, field, ex);
            }
        }

        private void setConfigForField(String key, Field field, Object value, Object bean) {
            try {
                doSetConfigForField(key, field, value, bean);
            } catch (Exception ex) {
                logger.error("error setting config value for key =< {} > field = {} value =< {} >", key, field, value, ex);
            }
        }

        private void doSetConfigForField(String key, Field field, Object value, Object bean) throws Exception {
            String modular = field.getAnnotation(ConfigValue.class).modular();
            value = unpackIfPacked(value);
            logger.debug("set config field = {}.{} to value =< {} > for key =< {} >", bean, field.getName(), value, key);
            if (value instanceof Set set) {
                value = ImmutableSet.copyOf(set);
            } else if (value instanceof Iterable iterable) {
                value = ImmutableList.copyOf(iterable);
            }
            if (isNotBlank(modular)) {
                value = ImmutableList.copyOf(loadModule((List<String>) value, modular, field.getGenericType()));
            }
            value = convert(value, field.getGenericType());
            try {
                FieldUtils.writeField(field, bean, value, true);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Nullable
        private Object unpackIfPacked(@Nullable Object value) {
            if (value instanceof String string) {
                value = CmPackUtils.unpackIfPacked(string);
            }
            return value;
        }

        private List<?> loadModule(@Nullable List<String> value, String modular, Type genericType) throws ReflectiveOperationException {
            logger.debug("load config modules for key =< {} > value = {}", modular, value);
            if (isNullOrEmpty(value)) {
                return emptyList();
            } else {
                String namespacedModular = addNamespaceToKey(namespace, checkNotBlank(modular));
                logger.debug("module config namespace =< {} >", namespacedModular);
                Class<?> modelSuperType = getFirstTypeArgOfParametrizedType(genericType);
                Map<String, Class> models = repository.getConfigHelpers().stream().filter(c -> modelSuperType.isInstance(c.getBean()) && c.getAnnotation().module() == true && equal(ConfigDefinition.getModulePrefix(c.getNamespace()), namespacedModular))
                        .collect(toImmutableMap((c) -> ConfigDefinition.getModuleType(c.getNamespace()), c -> c.getType()));
                checkArgument(!models.isEmpty());
                return list(value).map(rethrowFunction(v -> {
                    Matcher matcher = Pattern.compile("^([^\\[]+)\\[([^\\]]+)\\]$").matcher(v);
                    Map<String, String> configs;
                    String code;
                    if (matcher.matches()) {
                        code = checkNotBlank(matcher.group(1));
                        configs = decodeUrlParams(checkNotBlank(matcher.group(2)));
                    } else {
                        code = v;
                        configs = emptyMap();
                    }
                    String itemConfigPrefix = format("%s.%s.", namespacedModular, code);
                    String modelCode = models.size() == 1 ? getOnlyElement(models.keySet()) : firstNotBlank(localConfigService.getStringOrDefault(itemConfigPrefix + "type"), code);
                    logger.debug("load config module element with code =< {} > type =< {} >", code, modelCode);
                    Class model = checkNotNull(models.get(modelCode), "invalid model type =< %s > for modular config element =< %s >", modelCode, namespacedModular);
                    String defaultConfigPrefix = format("%s.%s.", namespacedModular, modelCode);
                    Object item = model.getConstructor(String.class).newInstance(code);
                    parseBeanForConfigDefinitions(model, d -> {
                    }, (d, f) -> {
                        String innerKey = itemConfigPrefix + d.getKey(), defaultKey = defaultConfigPrefix + d.getKey();
                        logger.debug("load config value for module =< {}.{} > key =< {} >", namespacedModular, code, innerKey);
                        if (configs.containsKey(d.getKey())) {
                            setConfigForField(innerKey, f, configs.get(d.getKey()), item);
                        } else {
                            setConfigForField(innerKey, defaultKey, f, item);
                        }
                    }, (d, m) -> {
                        throw new UnsupportedOperationException();
                    });
                    return item;
                }));
            }
        }

    }

}
