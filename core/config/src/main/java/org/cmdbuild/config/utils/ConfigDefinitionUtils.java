/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.utils;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.Ordering;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigDefinition;
import static org.cmdbuild.config.api.ConfigDefinition.ModularConfigDefinition.MCD_MODULE;
import static org.cmdbuild.config.api.ConfigDefinition.ModularConfigDefinition.MCD_OWNER;
import org.cmdbuild.config.api.ConfigDefinitionImpl;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.utils.ConfigUtils.addNamespaceToKey;
import static org.cmdbuild.config.utils.ConfigUtils.stripOptionalNamespaceFromKey;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.getEnumParamsFromType;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ReflectionUtils;

public class ConfigDefinitionUtils {

    public static Pair<String, String> parseConfigPropLine(String value) {
        Matcher matcher = Pattern.compile("^\\s*([^\\s=]+)\\s*=\\s*(.*)$").matcher(checkNotBlank(value));
        checkArgument(matcher.matches());
        return Pair.of(matcher.group(1), matcher.group(2));
    }

    public static List<ConfigDefinition> getAllConfigDefinitionsFromClasspath() {
        return doGetConfigDefinitionsFromClasspath(true);
    }

    public static List<ConfigDefinition> getConfigDefinitionsFromClasspath() {
        return doGetConfigDefinitionsFromClasspath(false);
    }

    public static void parseBeanForConfigDefinitions(Class classe, Consumer<ConfigDefinition> configDefinitionCallback) {
        parseBeanForConfigDefinitions(classe, configDefinitionCallback, (x, y) -> {
        }, (x, y) -> {
        });
    }

    public static void parseBeanForConfigDefinitions(Class classe, Consumer<ConfigDefinition> configDefinitionCallback, BiConsumer<ConfigDefinition, Field> fieldCallback, BiConsumer<ConfigDefinition, Method> methodCallback) {
        ConfigComponent configComponent = (ConfigComponent) classe.getAnnotation(ConfigComponent.class);
        ReflectionUtils.doWithFields(classe, (field) -> {
            if (field.isAnnotationPresent(ConfigValue.class)) {
                ConfigValue fieldAnnotation = field.getAnnotation(ConfigValue.class);
                ConfigDefinition configDefinition = buildConfigDefinitionFromAnnotation(fieldAnnotation, configComponent, field.getType());
                configDefinitionCallback.accept(configDefinition);
                fieldCallback.accept(configDefinition, field);
            }
        });
        ReflectionUtils.doWithMethods(classe, (method) -> {
            if (method.isAnnotationPresent(ConfigValue.class)) {
                checkArgument(method.getParameterCount() == 1, "config value methods must have one and only one parameter; invalid method = %s", method);
                ConfigValue methodAnnotation = method.getAnnotation(ConfigValue.class);
                ConfigDefinition configDefinition = buildConfigDefinitionFromAnnotation(methodAnnotation, configComponent, method.getReturnType());
                configDefinitionCallback.accept(configDefinition);
                methodCallback.accept(configDefinition, method);
            }
        });
    }

    private static ConfigDefinition buildConfigDefinitionFromAnnotation(ConfigValue annotation, ConfigComponent configComponent, Class<?> type) {
        String key = firstNotBlank(nullIfEqualToNullConst(annotation.key()), nullIfEqualToNullConst(annotation.value()));
        key = stripOptionalNamespaceFromKey(configComponent.value(), key);
        return ConfigDefinitionImpl.builder()
                .withKey(key)
                .withDescription(annotation.description())
                .withDefaultValue(nullIfEqualToNullConst(annotation.defaultValue()))
                .withEnumValues(getEnumParamsFromType(type))
                .withProtected(annotation.isProtected())
                .withExperimental(annotation.experimental())
                .withLocation(annotation.location())
                .withCategory(annotation.category())
                .accept(b -> {
                    if (configComponent.module()) {
                        b.withModular(MCD_MODULE).withModuleNamespace(configComponent.value());
                    } else if (isNotBlank(annotation.modular())) {
                        b.withModular(MCD_OWNER).withModuleNamespace(annotation.modular());
                    }
                })
                .build();
    }

    private static List<ConfigDefinition> doGetConfigDefinitionsFromClasspath(boolean includeExperimental) {
        List<ConfigDefinition> list = list();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ConfigComponent.class));
        scanner.findCandidateComponents("org.cmdbuild").stream().forEach(bc -> {
            try {
                Class c = Class.forName(bc.getBeanClassName());
                ConfigComponent configComponent = (ConfigComponent) c.getAnnotation(ConfigComponent.class);
                if (configComponent != null) {
                    String namespace = checkNotBlank(configComponent.value());
                    parseBeanForConfigDefinitions(c, (d) -> list.add(ConfigDefinitionImpl.copyOf(d).accept(b -> {
                        if (!configComponent.module() == true) {
                            b.withKey(addNamespaceToKey(namespace, d.getKey())).withModuleNamespace(addNamespaceToKey(namespace, d.getModuleNamespace()));
                        }
                    }).build()));
                }
            } catch (ClassNotFoundException | NoClassDefFoundError ex) {
                throw runtime(ex, "error loading class for name = %s", bc.getBeanClassName());
            }
        });
        if (!includeExperimental) {
            list.removeIf(ConfigDefinition::isExperimental);//TODO make this configurable (?)
        }
        list.sort(Ordering.natural().onResultOf(ConfigDefinition::getKey));
        checkArgument(!list.isEmpty(), "error retrieving config definitions from classpath: config definitions not found");
        return list;
    }

    @Nullable
    private static String nullIfEqualToNullConst(@Nullable String value) {
        return (value == null || ConfigValue.NULL.equals(value)) ? null : value;
    }

}
