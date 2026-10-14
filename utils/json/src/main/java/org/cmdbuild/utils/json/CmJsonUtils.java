/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import static com.google.common.collect.MoreCollectors.onlyElement;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.gson.JsonElement;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import static java.util.Arrays.stream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmJsonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public final static TypeReference<List<Map<String, String>>> LIST_OF_MAP_OF_STRINGS = new TypeReference<List<Map<String, String>>>() {
    };
    public final static TypeReference<List<Map<String, Object>>> LIST_OF_MAP_OF_OBJECTS = new TypeReference<List<Map<String, Object>>>() {
    };
    public final static TypeReference<Map<String, String>> MAP_OF_STRINGS = new TypeReference<Map<String, String>>() {
    };
    public final static TypeReference<List<String>> LIST_OF_STRINGS = new TypeReference<List<String>>() {
    };

    public final static TypeReference<Map<String, Object>> MAP_OF_OBJECTS = new TypeReference<Map<String, Object>>() {
    };

    private final static ObjectMapper OBJECT_MAPPER = createObjectMapper(false), OBJECT_MAPPER_PRETTY = createObjectMapper(true),
            YAML_MAPPER = new ObjectMapper(new YAMLFactory().enable(YAMLGenerator.Feature.MINIMIZE_QUOTES).disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));

    public final static <T> CollectionType collectionType(Class<T> classe) {
        return OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, classe);
    }

    public final static <K, V> MapType mapType(Class<K> classeKey, Class<V> classeValue) {
        return OBJECT_MAPPER.getTypeFactory().constructMapType(Map.class, classeKey, classeValue);
    }

    public static <T> T fromHjson(String json, Class<T> classe) {
        return fromJson(org.hjson.JsonValue.readHjson(json).toString(), classe);
    }

    public static <T> T fromHjson(String json, TypeReference<T> type) {
        return fromJson(org.hjson.JsonValue.readHjson(json).toString(), type);
    }

    public static <T> T fromHjson(byte[] json, Class<T> classe) {
        return fromHjson(new ByteArrayInputStream(json), classe);
    }

    public static <T> T fromHjson(byte[] json, TypeReference<T> type) {
        return fromHjson(new ByteArrayInputStream(json), type);
    }

    public static <T> T fromHjson(InputStream in, Class<T> classe) {
        try {
            return fromJson(org.hjson.JsonValue.readHjson(new InputStreamReader(in)).toString(), classe);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T fromHjson(InputStream in, TypeReference<T> type) {
        try {
            return (T) fromJson(org.hjson.JsonValue.readHjson(new InputStreamReader(in)).toString(), type);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T fromJson(String json, Class<T> classe) {
        try {
            if (hasJsonBeanAnnotation(classe)) {
                classe = getJsonBeanTargetClass(classe);
            }
            return OBJECT_MAPPER.readValue(json, classe);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T, O> T fromJson(String json, Type type) {
        try {
            return (T) OBJECT_MAPPER.readValue(json, new TypeReference<CmJsonUtils>() { //dummy types
                @Override
                public Type getType() {
                    return type;//actual type
                }

            });
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T, O> T fromJson(JsonNode record, Type type) {
        try {
            return (T) OBJECT_MAPPER.readValue(record.traverse(), new TypeReference<CmJsonUtils>() { //dummy types
                @Override
                public Type getType() {
                    return type; //actual type
                }

            });
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T, O> T fromJson(InputStream in, Type type) {
        try {
            return (T) OBJECT_MAPPER.readValue(in, new TypeReference<CmJsonUtils>() { //dummy types
                @Override
                public Type getType() {
                    return type; //actual type
                }

            });
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T fromJson(byte[] json, Class<T> classe) {
        try {
            if (hasJsonBeanAnnotation(classe)) {
                classe = getJsonBeanTargetClass(classe);
            }
            return OBJECT_MAPPER.readValue(json, classe);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T fromJson(byte[] json, TypeReference<T> type) {
        try {
            T value = OBJECT_MAPPER.readValue(json, type);
            return value;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T fromJson(DataSource dataSource, Class<T> classe) {
        try (InputStream in = dataSource.getInputStream()) {
            return fromJson(in, classe);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T fromJson(DataSource dataSource, TypeReference<T> type) {
        try (InputStream in = dataSource.getInputStream()) {
            return fromJson(in, type);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T fromJson(InputStream in, TypeReference<T> type) {
        try {
            return OBJECT_MAPPER.readValue(in, type);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T fromJson(InputStream in, Class<T> classe) {
        try {
            return OBJECT_MAPPER.readValue(in, classe);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T fromJson(JsonNode record, Class<T> classe) {
        try {
            if (hasJsonBeanAnnotation(classe)) {
                classe = getJsonBeanTargetClass(classe);
            }
            return OBJECT_MAPPER.readValue(record.traverse(), classe);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T fromJson(JsonNode record, TypeReference<T> type) {
        try {
            return OBJECT_MAPPER.readValue(record.traverse(), type);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T fromYaml(String yaml, Class<T> classe) {
        try {
            if (hasJsonBeanAnnotation(classe)) {
                classe = getJsonBeanTargetClass(classe);
            }
            return YAML_MAPPER.readValue(yaml, classe);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T fromYaml(String json, TypeReference<T> type) {
        try {
            return YAML_MAPPER.readValue(json, type);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Nullable
    public static String jsonValueToString(@Nullable JsonNode value) {
        if (value == null || value.isNull()) {
            return null;
        } else {
            Validate.isTrue(value.isValueNode(), "invalid json value node = %s", value);
            return value.asText();
        }
    }

    public static String jsonValueToStringOrEmpty(@Nullable JsonNode value) {
        return StringUtils.defaultString(jsonValueToString(value));
    }

    @Nullable
    public static String nullableToJson(@Nullable Object value) {
        return value == null ? null : toJson(value);
    }

    @Nullable
    public static String nullableToJson(@Nullable JsonNode value) {
        return value == null || value.isNull() ? null : toJson(value);
    }

    public static boolean isJson(@Nullable String value) {
        if (isBlank(value) || !(value.trim().startsWith("{") || value.trim().startsWith("["))) {
            return false;
        } else {
            try {
                fromJson(value, JsonNode.class);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    public static boolean isYaml(@Nullable String value) {
        if (isBlank(value)) {
            return false;
        } else {
            try {
                fromYaml(value, JsonNode.class);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    public static String toJson(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String toPrettyJson(Object value) {
        try {
            return OBJECT_MAPPER_PRETTY.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String toYaml(Object value) {
        try {
            return YAML_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Nullable
    public static String toString(@Nullable JsonElement value) {
        if (value == null || value.isJsonNull()) {
            return null;
        } else if (value.isJsonPrimitive()) {
            return value.getAsString();
        } else {
            return value.toString();
        }
    }

    @Nullable
    public static String prettifyIfJson(@Nullable String mayBeJson) {
        if (isNotBlank(mayBeJson) && mayBeJson.matches("^[\\{\\[].*")) {//TODO better json check
            return prettifyJsonSafe(mayBeJson);
        } else {
            return mayBeJson;
        }
    }

    @Nullable
    public static String prettifyJsonSafe(@Nullable String mayBeJson) {
        if (isNotBlank(mayBeJson)) {
            try {
                Object object = fromJson(mayBeJson, Object.class);
                return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            } catch (Exception ex) {
                LOGGER.warn("unable to prettify (maybe) json = {}", mayBeJson);
                LOGGER.debug("unable to prettify json", ex);
                return mayBeJson;
            }
        } else {
            return mayBeJson;
        }
    }

    @Nullable
    public static Object prettifyIfJsonLazy(@Nullable String mayBeJson) {
        return new Object() {

            @Override
            public String toString() {
                return String.valueOf(prettifyIfJson(mayBeJson));
            }

        };
    }

    public static <T> boolean hasJsonBeanAnnotation(Class<T> targetClass) {
        return targetClass.isAnnotationPresent(JsonBean.class)
                || targetClass.isAnnotationPresent(JsonDeserialize.class)
                || stream(targetClass.getConstructors()).anyMatch(c -> Arrays.stream(c.getAnnotations()).anyMatch(JsonCreator.class::isInstance) || Arrays.stream(c.getParameterAnnotations()).flatMap(Arrays::stream).anyMatch(JsonProperty.class::isInstance));
    }

    public static boolean hasJsonBeanAnnotation(Type targetType) {//TODO improve this
        return ((targetType instanceof Class aClass) && hasJsonBeanAnnotation(aClass))
                || ((targetType instanceof ParameterizedType parameterizedType) && hasJsonBeanAnnotation((parameterizedType.getActualTypeArguments()[0])));
    }

    public static boolean hasJsonBeanAnnotation(Method method) {
        return method.getAnnotation(JsonBean.class) != null;
    }

    public static <T> Class<T> getJsonBeanTargetClass(Class<T> targetInterfaceOrMode) {
        JsonBean annotation = targetInterfaceOrMode.getAnnotation(JsonBean.class);
        Class targetClass = annotation == null || (annotation.value() == null || Objects.equals(annotation.value(), Object.class)) ? targetInterfaceOrMode : annotation.value();
        Validate.isTrue(targetInterfaceOrMode.isAssignableFrom(targetClass), "invalid jsonbean class = %s for target interface = %s", targetClass.getName(), targetInterfaceOrMode.getName());
        return targetClass;
    }

    private static ObjectMapper createObjectMapper(boolean pretty) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        if (isNotBlank(System.getProperty("com.fasterxml.jackson.core.StreamReadConstraints.maxStringLen"))) {
            int maxStringLen = Integer.parseInt(System.getProperty("com.fasterxml.jackson.core.StreamReadConstraints.maxStringLen"));
            mapper.getFactory().setStreamReadConstraints(StreamReadConstraints.builder().maxStringLength(maxStringLen).build());
        }

        SimpleModule myModule = new SimpleModule();
        myModule.addSerializer(Enum.class, new MyEnumSerializer());
        myModule.addDeserializer(Enum.class, new MyEnumDeserializer());
        mapper.registerModule(myModule);
        mapper.registerModule(new JavaTimeModule());

        if (pretty) {
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        }
        return mapper;
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    private static class MyEnumSerializer<E extends Enum> extends JsonSerializer<E> {

        @Override
        public void serialize(E value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeString(value.name().toLowerCase().replaceFirst("^[^_]+_(.+)", "$1"));
            }
        }
    }

    private static class MyEnumDeserializer<E extends Enum> extends StdDeserializer<E> implements ContextualDeserializer {

        private final Class<E> targetClass;

        public MyEnumDeserializer() {
            super(Enum.class);
            targetClass = null;
        }

        public MyEnumDeserializer(Class<E> targetClass) {
            super(Enum.class);
            this.targetClass = targetClass;
        }

        @Override
        public E deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            try {
                String value = StringUtils.defaultString(p.getValueAsString()).toLowerCase().trim();
                if (isBlank(value)) {
                    return null;
                } else {
                    List<E> list = EnumUtils.getEnumList(targetClass);
                    return list.stream().filter(e -> Objects.equals(e.name().toLowerCase(), value)).collect(toOptional())
                            .orElseGet(() -> list.stream().filter(e -> Objects.equals(e.name().toLowerCase().replaceFirst("^[^_]+_(.+)", "$1"), value)).collect(onlyElement()));
                }
            } catch (Exception ex) {
                throw new RuntimeException(format("unable to parse enum class %s from value =< %s >", targetClass, p.getValueAsString()), ex);
            }
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) throws JsonMappingException {
            Class<Enum> classe = (Class<Enum>) context.getContextualType().getRawClass();
            return new MyEnumDeserializer(classe);
        }

    }

}
