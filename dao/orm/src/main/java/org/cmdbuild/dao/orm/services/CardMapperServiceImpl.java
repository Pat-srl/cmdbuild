/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm.services;

import com.google.common.base.Function;
import static com.google.common.base.Objects.equal;
import com.google.common.base.Supplier;
import static com.google.common.collect.Maps.uniqueIndex;
import jakarta.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.util.Arrays.stream;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import static java.util.stream.Collectors.toUnmodifiableList;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.Item;
import org.cmdbuild.dao.beans.ItemImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ALL_RESERVED_ATTRIBUTES;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS;
import org.cmdbuild.dao.driver.repository.CardIdService;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import org.cmdbuild.dao.orm.CardMapper;
import org.cmdbuild.dao.orm.CardMapperConfig;
import org.cmdbuild.dao.orm.CardMapperConfigRepository;
import org.cmdbuild.dao.orm.CardMapperService;
import org.cmdbuild.dao.orm.GetterContext;
import org.cmdbuild.dao.orm.SetterContext;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import static org.cmdbuild.dao.orm.annotations.CardAttr.ANY;
import org.cmdbuild.dao.orm.annotations.CardAttr.EmbeddedReference;
import static org.cmdbuild.dao.orm.annotations.CardAttr.EmbeddedReference.ALWAYS;
import static org.cmdbuild.dao.orm.annotations.CardAttr.EmbeddedReference.MIXED;
import static org.cmdbuild.dao.orm.annotations.CardAttr.EmbeddedReference.NONE;
import static org.cmdbuild.dao.orm.annotations.CardAttr.NO_DEFAULT_VALUE;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.dao.utils.ItemUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.hasJsonBeanAnnotation;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import org.cmdbuild.utils.json.JsonBean;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.getOnlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.cause;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.utils.lang.CmNullableUtils;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.nullIf;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

@Component
public class CardMapperServiceImpl implements CardMapperService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ClasseRepository classeRepository;
    private final CardIdService cardIdService;

    private final Map<String, CardMapper> mappersByClassName, mappersByObjectClass, mappersByRelatedClass;

    public CardMapperServiceImpl(CardMapperConfigRepository repository, ClasseRepository classeRepository, CardIdService cardIdService, CardMapperLoader loader /* only here for dependency processing */) {
        this.classeRepository = checkNotNull(classeRepository);
        this.cardIdService = checkNotNull(cardIdService);

        logger.info("init card mappers");
        Map<String, CardMapper> byClassName = map(), byObjectClass = map(), byRelatedClass = map();
        repository.getConfigs().stream().map(CardMapperImpl::new).forEach(cardMapper -> {
            logger.debug("register card mapper = {}", cardMapper);
            String classId = cardMapper.getClassId();
            if (isNotBlank(classId)) {
                CardMapper other = byClassName.get(classId);
                if (other == null || (cardMapper.isPrimaryMapper() && !other.isPrimaryMapper())) {
                    byClassName.put(cardMapper.getClassId(), cardMapper);
                } else if (other.isPrimaryMapper() && !cardMapper.isPrimaryMapper()) {
                    //do nothing, keep other
                } else {
                    throw runtime("duplicate card mapper found for class id = %s (remove one or use the @Primary annotation)", classId);
                }
            }
            Collection<Class> classes = getAllClasses(cardMapper.getTargetClass());
            logger.debug("register mapper for target classes = {}", classes);
            classes.forEach((iface) -> {
                checkArgument(byObjectClass.put(iface.getName(), cardMapper) == null, "duplicate mapper found for interface = %s", iface);
                checkArgument(byRelatedClass.put(iface.getName(), cardMapper) == null, "duplicate mapper found for interface = %s", iface);
            });
            checkArgument(byRelatedClass.put(cardMapper.getBuilderClass().getName(), cardMapper) == null, "duplicate mapper found for interface = %s", cardMapper.getBuilderClass());
        });
        mappersByClassName = Map.copyOf(byClassName);
        mappersByObjectClass = Map.copyOf(byObjectClass);
        mappersByRelatedClass = Map.copyOf(byRelatedClass);
        checkArgument(!mappersByClassName.isEmpty(), "card mapper init failed: no card mapper found");
        logger.info("{} card mappers ready", mappersByClassName.size());
    }

    @Override
    public Card objectToCard(Object object) {
        checkNotNull(object, "object instance cannot be null");
        if (object instanceof Builder builder) {
            object = builder.build();
        }
        CardMapper mapper = checkNotNull(mappersByObjectClass.get(object.getClass().getName()), "mapper not found for model =< %s >", object.getClass().getName());
        return mapper.objectToCard(CardImpl.builder(), object).withType(classeRepository.getClasse(mapper.getClassId())).build();
    }

    @Override
    public <T> T cardToObject(Card card) {
        CardMapper<T, ?> mapper = getMapperForClasse(card.getType());
        return mapper.cardToObject(card).build();
    }

    @Override
    public <T, B extends Builder<T, B>> CardMapper<T, B> getMapperForModelOrBuilder(Class model) {
        return checkNotNull(mappersByRelatedClass.get(model.getName()), "mapper not found for model =< %s >", model);
    }

    @Override
    public Classe getClasseForModelOrBuilder(Class builderOrBeanClass) {
        CardMapper mapper = getMapperForModelOrBuilder(builderOrBeanClass);
        String classId = mapper.getClassId();
        return classeRepository.getClasse(classId);
    }

    @Override
    public CardMapper getMapperForClasse(Classe classe) {
        return checkNotNull(mappersByClassName.get(classe.getName()), "mapper not found for type = %s", classe);
    }

    private class CardSetterContext implements SetterContext {

        private final Card card;

        public CardSetterContext(Card card) {
            this.card = checkNotNull(card);
        }

        @Override
        public Item getItem(String type, long id) {
            return ItemUtils.getItem(card, type, id);
        }

    }

    private static Collection<Class> getAllClasses(Class targetClass) {
        Collection<Class> classes = new HashSet<>();
        classes.add(targetClass);
        classes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetClass));
        classes.removeIf((thisClass) -> !thisClass.getPackage().getName().startsWith("org.cmdbuild"));
        return classes;
    }

    private static Class toClass(Type type) {
        if (type instanceof Class aClass) {
            return aClass;
        } else if (type instanceof ParameterizedType parameterizedType) {
            return toClass(parameterizedType.getRawType());
        } else {
            throw new RuntimeException("unable to convert type " + type + " to class");
        }
    }

    private static interface SetterHelper {

        void set(SetterContext context, @Nullable Object sourceValue, Object targetObject);
    }

    private enum NoopSetterHelper implements SetterHelper {

        INSTANCE;

        @Override
        public void set(SetterContext context, Object sourceValue, Object targetObject) {
        }

    }

    private class CardMapperImpl<T, B extends Builder<T, B>> implements CardMapper<T, B> {

        private final Class<T> targetClass;
        private final String className;
        private Class<B> builderClass;
        private Supplier<B> builderSupplier;
        private final Map<String, GetterHelper> getterMethods;
        private final Map<String, SetterHelper> setterMethods;
        private final GetterHelper anyGetter;
        private final SetterHelper anySetter;
        private final boolean primary;

        public CardMapperImpl(CardMapperConfig<T> config) {
            logger.debug("create card mapper for target class = {}", config.getTargetClass());
            this.targetClass = checkNotNull(config.getTargetClass());
            CardMapping classAnnotation = checkNotNull(targetClass.getAnnotation(CardMapping.class));
//		className = checkNotBlank(classAnnotation.value(), "className not found for card mapping annotation in class %s", targetClass);
            className = checkNotNull(classAnnotation.value());
            primary = targetClass.isAnnotationPresent(Primary.class);
            logger.debug("card class name = '{}'", className);
            scanBuilderMethod();
            logger.debug("builder class = {}", builderClass);
            Map<String, GetterHelper> getters = findGetterMethods();
            Map<String, SetterHelper> setters = findSetterMethods(getters);
            checkArgument(getters.size() == setters.size(), "missing card mapper setter methods for attributes = %s while parsing %s", set(getters.keySet()).without(setters.keySet()), targetClass);
            logger.debug("mapped attributes = {}", set(getters.keySet()).with(setters.keySet()).sorted().immutable());
            anyGetter = getters.get(ANY);
            anySetter = setters.get(ANY);
            getterMethods = map(getters).withoutKey(ANY).immutable();
            setterMethods = map(setters).withoutKey(ANY).immutable();
        }

        private void scanBuilderMethod() {
            ReflectionUtils.doWithMethods(targetClass, (method) -> {
                if (Modifier.isStatic(method.getModifiers())) {
                    logger.trace("looking for builder method, check method {}", method);
                    if (returnsCorrectBuilder(method)) {
                        logger.debug("selected builder method {}", method);
                        builderClass = (Class<B>) method.getReturnType();
                        builderSupplier = () -> {
                            try {
                                return checkNotNull((B) method.invoke(null), "error, builder method %s return null", method);
                            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                throw new RuntimeException(ex);
                            }
                        };
                    }
                }
            });
            checkNotNull(builderSupplier, "cannot find builder method in mapped card %s", targetClass);
        }

        private boolean returnsCorrectBuilder(Method method) {
            if (Builder.class.isAssignableFrom(method.getReturnType()) && method.getName().toLowerCase().contains("builder")) {
//			if ((method.getGenericReturnType() instanceof ParameterizedType)
//					&& (((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments().length == 2)
//					&& targetClass.isAssignableFrom((Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0])) {
//				return true;
//			} else {
                Optional<Type> builderType = stream(method.getReturnType().getGenericInterfaces()).filter((type) -> Builder.class.isAssignableFrom(toClass(type))
                        && type instanceof ParameterizedType
                        && ((ParameterizedType) type).getActualTypeArguments().length == 2
                        && targetClass.isAssignableFrom(toClass(((ParameterizedType) type).getActualTypeArguments()[0]))
                ).findAny();
                if (builderType.isPresent()) {
                    return true;
                }
//			}
            }
            return false;
        }

        private Map<String, GetterHelper> findGetterMethods() {
            CmMapUtils.FluentMap<String, GetterHelper> map = map();
            ReflectionUtils.doWithMethods(targetClass, (method) -> {
                if (method.isAnnotationPresent(CardAttr.class)) {
                    CardAttr annotation = method.getAnnotation(CardAttr.class);
                    String key = trimToNull(annotation.value());
                    if (key == null) {
                        key = processGetterMethodName(method);
                    }
                    Object defaultValue = hasDefaultValue(annotation) ? annotation.defaultValue() : null;
                    GetterHelper getterHelper = new GetterHelper(method, key, defaultValue, !annotation.readFromDb(), annotation.embedded());
                    map.put(key, getterHelper);
                }
            });
            return map.immutable();
        }

        private String processGetterMethodName(Method method) {
            return capitalize(method.getName().replaceFirst("^(get|is)", ""));
        }

        private Map<String, SetterHelper> findSetterMethods(Map<String, GetterHelper> getters) {
            CmMapUtils.FluentMap<String, SetterHelper> map = map();
            Map<String, GetterHelper> getterMethodsByMethodProcessedName = uniqueIndex(getters.values(), (entry) -> processGetterMethodName(entry.getMethod()));

            MultiValuedMap<String, Method> candidateSetterMethods = new ArrayListValuedHashMap<>();

            ReflectionUtils.doWithMethods(builderClass, (method) -> {
                if (method.getParameterCount() == 1 || method.isAnnotationPresent(CardAttr.class)) {
                    CardAttr annotation = method.getAnnotation(CardAttr.class);
                    if (annotation == null || !annotation.ignore()) {
                        String key = annotation == null ? null : trimToNull(annotation.value());
                        boolean keyFromAnnotation;
                        if (key == null) {
                            keyFromAnnotation = false;
                            key = capitalize(method.getName().replaceFirst("^(set|with)", ""));
                        } else {
                            keyFromAnnotation = true;
                        }
                        GetterHelper getterHelper;
                        if (keyFromAnnotation) {
                            getterHelper = getters.get(key);
                        } else {
                            getterHelper = getterMethodsByMethodProcessedName.get(key);
                        }
                        if (getterHelper != null && !getterHelper.isIgnoreReadFromOnDb()) {
                            key = getterHelper.getKey();
                            candidateSetterMethods.put(key, method);
                        }
                    }
                }
            });

            candidateSetterMethods.asMap().forEach((key, methods) -> {
                GetterHelper getterHelper = checkNotNull(getters.get(key));
                Method setterMethod = methods.size() == 1 ? getOnlyElement(methods) : methods.stream().filter(m -> equal(getterHelper.getMethod().getReturnType(), m.getParameterTypes()[0]) && equal(m.getReturnType(), builderClass)).collect(onlyElement("unable to find matching setter method for key = %s bean = %s", key, targetClass));
                Object defaultValue = getterHelper.getDefaultValue();
                CardAttr getterAnnotation = getterHelper.getMethod().getAnnotation(CardAttr.class),
                        setterAnnotation = setterMethod.getAnnotation(CardAttr.class);
                if (hasDefaultValue(setterAnnotation)) {
                    defaultValue = setterAnnotation.defaultValue();
                }
                Class targetType = null;
                if (getterHelper.getMethod().isAnnotationPresent(JsonBean.class) && nullIf(((JsonBean) getterHelper.getMethod().getAnnotation(JsonBean.class)).value(), Object.class) != null) {
                    targetType = ((JsonBean) getterHelper.getMethod().getAnnotation(JsonBean.class)).value();
                }
                SetterHelper setterHelper = new SetterHelperImpl(setterMethod, defaultValue, targetType, getterHelper.isJsonBean, getterAnnotation == null ? NONE : getterAnnotation.embedded());
                map.put(key, setterHelper);
            });

            getters.values().stream().filter(GetterHelper::isIgnoreReadFromOnDb).forEach((g) -> {
                map.put(g.getKey(), NoopSetterHelper.INSTANCE);
            });
            return map.immutable();
        }

        @Override
        public Class<T> getTargetClass() {
            return targetClass;
        }

        @Override
        public String getClassId() {
            return className;
        }

        @Override
        public boolean isPrimaryMapper() {
            return primary;
        }

        @Override
        public Long getCardId(T object) {
            try {
                return getValue(object, ATTR_ID, Long.class);
            } catch (Exception ex) {
                throw new DaoException(ex, "unable to retrieve Id attribute from bean = %s", object);
            }
        }

        @Override
        public String toString() {
            return "CardMapperImpl{" + "targetClass=" + targetClass.getName() + ", className=" + className + '}';
        }

        @Nullable
        private <V> V getValue(T object, String key, Class<V> valueClass) {
            GetterHelper getter = checkNotNull(getterMethods.get(key), "getter not found for attribute = %s targetClass = %s", key, targetClass);
            Object sourceValue = getter.get(DummyGetterSetterContext.INSTANCE, object);
            if (sourceValue == null) {
                return null;
            } else {
                V targetValue = convert(sourceValue, valueClass);
                return targetValue;
            }
        }

        @Override
        public CardImpl.CardImplBuilder objectToCard(CardImpl.CardImplBuilder builder, T object) {
            logger.debug("mapping bean = {} to card = {}", object, builder);
            objectToData(builder::withAttribute, object);
            return builder;
        }

        @Override
        public void objectToData(BiConsumer<String, Object> consumer, T object) {
            GetterContextImpl context = new GetterContextImpl();
            if (anyGetter != null) {
                firstNotNull((Map) anyGetter.get(context, object), emptyMap()).forEach(consumer);
            }
            getterMethods.forEach((key, getter) -> {
                logger.trace("copy bean attribute {} to card", key);
                Object value = getter.get(context, object);
                consumer.accept(key, value);
            });
            if (context.hasItems()) {
                Attribute attr = classeRepository.getClasse(className).getAllAttributes().stream().filter(a -> a.isOfType(AttributeTypeName.JSON) && a.getMetadata().isItems()).limit(1).collect(onlyElement("missing items field for target class =< %s >", className)); //TODO improve this
                consumer.accept(attr.getName(), toJson(context.getItems().stream().map(Item::getData).collect(toUnmodifiableList())));
                //TODO handler create+inner reference !!
            }
        }

        @Override
        public B cardToObject(Card card) {
            try {
                return dataToObject(new CardSetterContext(card), (key) -> {
                    Object value;
                    if (isSpecialCardValue(key)) {
                        value = getSpecialCardValue(card, key);
                    } else {
                        value = card.get(key);
                    }
                    return value;
                });
            } catch (Exception ex) {
                throw new DaoException(ex, "error mapping card = %s to model = %s", card, targetClass);
            }
        }

        @Override
        public B dataToObject(Map<String, Object> data) {
            SetterContext context = DummyGetterSetterContext.INSTANCE;
            if (CmNullableUtils.isNotBlank(data.get(ATTR_IDCLASS)) && CmNullableUtils.isNotBlank(data.get(ATTR_ID))) {
                context = new CardSetterContext(CardImpl.buildCard(classeRepository.getClasse(toStringNotBlank(data.get(ATTR_IDCLASS))), data));
            }
            return dataToObject(context, data);
        }

        @Override
        public B dataToObject(Function<String, Object> dataSource) {
            return dataToObject(DummyGetterSetterContext.INSTANCE, dataSource);
        }

        @Override
        public B dataToObject(SetterContext context, Function<String, Object> dataSource) {
            B builder = builderSupplier.get();
            setterMethods.forEach((key, setter) -> {
                Object value = dataSource.apply(key);
                setter.set(context, value, builder);
            });
            return builder;
        }

        @Override
        public B dataToObject(SetterContext setterContext, Map<String, Object> dataSource) {
            B builder = dataToObject(setterContext, dataSource::get);
            if (anySetter != null) {
                anySetter.set(setterContext, map(dataSource).withoutKeys(setterMethods.keySet()).withoutKeys(ALL_RESERVED_ATTRIBUTES), builder); //TODO improve `withoutKeys(ALL_RESERVED_ATTRIBUTES)`
            }
            return builder;
        }

        private boolean isSpecialCardValue(String key) {
            return equal(key, ATTR_ID);
        }

        private Object getSpecialCardValue(Card card, String key) {
            return switch (key) {
                case ATTR_ID ->
                    card.getId();
                default ->
                    throw new UnsupportedOperationException("not found a special card value for name = " + key);
            };
        }

        @Override
        public B sqlToObject(ResultSet resultSet) {
            return dataToObject(DummyGetterSetterContext.INSTANCE, (key) -> {
                try {
                    return resultSet.getObject(key);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }

        @Override
        public Class<B> getBuilderClass() {
            return builderClass;
        }

        private boolean hasDefaultValue(@Nullable CardAttr annotation) {
            return annotation != null && !equal(NO_DEFAULT_VALUE, annotation.defaultValue());
        }

        private class GetterContextImpl implements GetterContext {

            private final List<Item> items = list();

            @Override
            public void addItem(Item item) {
                items.add(checkNotNull(item));
            }

            public List<Item> getItems() {
                return items;
            }

            public boolean hasItems() {
                return !getItems().isEmpty();
            }

        }

        private class GetterHelper {

            private final Method method;
            private final String key;
            private final Object defaultValue;
            private final boolean ignoreReadFromDb, isJsonBean;//TODO improve this, unify getter/setter metadata
            private final EmbeddedReference embedded;

            public GetterHelper(Method method, String key, @Nullable Object defaultValue, boolean ignoreReadFromDb, EmbeddedReference embedded) {
                this.method = checkNotNull(method);
                this.embedded = checkNotNull(embedded);
                this.key = checkNotNull(key);
                this.defaultValue = defaultValue;
                this.ignoreReadFromDb = ignoreReadFromDb;
                this.isJsonBean = hasJsonBeanAnnotation(method.getReturnType()) || hasJsonBeanAnnotation(method) || hasJsonBeanAnnotation(method.getGenericReturnType());//TODO replace this with attribute awareness - check card attribute type, and use that to identify json data
            }

            public boolean isIgnoreReadFromOnDb() {
                return ignoreReadFromDb;
            }

            public Method getMethod() {
                return method;
            }

            public String getKey() {
                return key;
            }

            public Object getDefaultValue() {
                return defaultValue;
            }

            public Object get(GetterContext context, Object targetObject) {
                try {
                    Object value = method.invoke(targetObject);
                    if (value != null && (isJsonBean || hasJsonBeanAnnotation(value.getClass()))) { //TODO replace this with attribute awareness - check card attribute type, and use that to identify json data
                        value = toJson(value);
                    } else if (value != null && value.getClass().isEnum()) {
                        value = serializeEnum((Enum) value);
                    }
                    switch (embedded) {
                        case ALWAYS:
                            List<Object> list = convert(value, List.class);//TODO improve this
                            if (list.isEmpty()) {
                                return list;
                            } else {
                                CardMapper mapper = getMapperForModel(list.iterator().next().getClass());
                                List<Item> items = list.stream().map(i -> ItemImpl.builder()
                                        .accept(b -> mapper.objectToData((k, v) -> b.addData((String) k, v), i))
                                        .withTypeName(mapper.getClassId()).build())
                                        .map(item -> {
                                            if (!item.hasId()) {
                                                item = ItemImpl.copyOf(item).withId(cardIdService.newCardId()).build();
                                            }
                                            return item;
                                        }).collect(toUnmodifiableList());
                                items.forEach(context::addItem);
                                return List.copyOf(list(items).map(Item::getId));
                            }
                        case NONE:
                            return value;
                        case MIXED://TODO mixed
                        default:
                            throw unsupported("unsupported embedded value =< %s >", embedded);
                    }
                } catch (InvocationTargetException ex) {
                    throw cause(ex);
                } catch (IllegalAccessException | IllegalArgumentException ex) {
                    throw runtime(ex);
                }
            }

        }

        private class SetterHelperImpl implements SetterHelper {

            private final Method method;
            private final Object defaultValue;
            private final Class targetType;
            private final boolean isJsonBean;
            private final EmbeddedReference embedded;

            public SetterHelperImpl(Method method, @Nullable Object defaultValue, @Nullable Class targetType, boolean isJsonBean, CardAttr.EmbeddedReference embedded) {
                this.method = checkNotNull(method);
                this.embedded = checkNotNull(embedded);
                this.defaultValue = defaultValue;
                this.targetType = targetType;
                checkArgument(targetType == null || method.getParameterTypes()[0].isAssignableFrom(targetType), "invalid target type = %s for method = %s", targetType, method);
                this.isJsonBean = isJsonBean;
            }

            public Method getMethod() {
                return method;
            }

            @Nullable
            public Object getDefaultValue() {
                return defaultValue;
            }

            @Override
            public void set(SetterContext context, @Nullable Object sourceValue, Object targetObject) {
                if (sourceValue == null) {
                    sourceValue = defaultValue;
                }
                Object param;
                try {
                    switch (embedded) {
                        case ALWAYS: {
                            List<Long> references = convert(sourceValue, List.class);//TODO check this
                            Class type = (Class) ((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[0];
                            CardMapper paramMapper = getMapperForModel(type);
                            param = list(references).map(id -> paramMapper.dataToObject(context, context.getItem(paramMapper.getClassId(), id).getData()).build());
                        }
                        break;
                        case NONE:
                            if (targetType != null) {
                                param = convert(sourceValue, targetType);
                            } else {
                                Type type = method.getGenericParameterTypes()[0];
                                boolean hasJsonBeanAnnotation = hasJsonBeanAnnotation(type);
                                if (isJsonBean && !hasJsonBeanAnnotation) {
                                    param = isNullOrBlank(sourceValue) ? null : fromJson(toStringOrNull(sourceValue), type);
                                } else {
                                    param = convert(sourceValue, type);
                                }
                            }
                            break;
                        case MIXED://TODO mixed
                        default:
                            throw unsupported("unsupported embedded value =< %s >", embedded);
                    }
                } catch (Exception ex) {
                    throw runtime(ex, "error converting value for setter method = %s.%s", method.getDeclaringClass().getSimpleName(), method.getName());
                }
                try {
                    method.invoke(targetObject, param);
                } catch (Exception ex) {
                    throw runtime(ex, "error invoking obj setter = %s.%s with value = '%s' (%s)", method.getDeclaringClass().getSimpleName(), method.getName(), abbreviate(param), getClassOfNullable(param));
                }
            }

        }

    }
}
