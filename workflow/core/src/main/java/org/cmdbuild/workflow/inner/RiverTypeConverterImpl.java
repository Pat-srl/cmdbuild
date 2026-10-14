package org.cmdbuild.workflow.inner;

import static com.google.common.collect.Maps.transformEntries;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.common.Constants;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import static org.cmdbuild.common.Constants.DMS_MODEL_PARENT_CLASS;
import static org.cmdbuild.common.beans.CardIdAndClassNameImpl.card;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.LookupValueImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.lookup.LookupRepository;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.stream;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.workflow.WorkflowTypeConverter;
import static org.cmdbuild.workflow.WorkflowTypeDefaults.defaultBoolean;
import static org.cmdbuild.workflow.WorkflowTypeDefaults.defaultDouble;
import static org.cmdbuild.workflow.WorkflowTypeDefaults.defaultInteger;
import static org.cmdbuild.workflow.WorkflowTypeDefaults.defaultLookup;
import static org.cmdbuild.workflow.WorkflowTypeDefaults.defaultReference;
import static org.cmdbuild.workflow.WorkflowTypeDefaults.defaultString;
import org.cmdbuild.workflow.inner.WorkflowTypesConverter.Lookup;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.WorkflowException;
import org.cmdbuild.workflow.type.LookupType;
import org.cmdbuild.workflow.type.ReferenceType;
import static org.cmdbuild.workflow.type.utils.WorkflowTypeUtils.emptyToNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RiverTypeConverterImpl implements WorkflowTypeConverter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final LookupRepository lookupStore;
    private final WorkflowConfiguration workflowConfiguration;

    private final CmCache<Card> cardCache;

    public RiverTypeConverterImpl(DaoService dao, LookupRepository lookupStore, WorkflowConfiguration workflowConfiguration, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        this.lookupStore = checkNotNull(lookupStore);
        this.workflowConfiguration = checkNotNull(workflowConfiguration);
        cardCache = cacheService.newCache("wf_migration_card_cache");
    }

    @Override
    @Nullable
    public Object cardValueToFlowValue(@Nullable Object value, Attribute attribute) {
        checkNotNull(attribute, "attribute cannot be null");
        return cardValueToFlowValue(value, attribute.getType());
    }

    @Override
    @Nullable
    public Object cardValueToFlowValue(@Nullable Object value, CardAttributeType attributeType) {
        checkNotNull(attributeType, "attribute cannot be null");
        if (value == null) {
            return defaultForNull(attributeType.getName());
        } else {
            return switch (attributeType.getName()) {
                case DATE, TIMESTAMP, TIME ->
                    CmDateUtils.toJavaDate(value);
                case FOREIGNKEY, REFERENCE, FILE -> {
                    IdAndDescription idAndDescription = (IdAndDescription) rawToSystem(attributeType, value);
                    Classe targetClass;
                    targetClass = switch (attributeType.getName()) {
                        case FOREIGNKEY ->
                            dao.getClasse(((ForeignKeyAttributeType) attributeType).getForeignKeyDestinationClassName());
                        case REFERENCE ->
                            dao.getDomain(((ReferenceAttributeType) attributeType).getDomainName()).getReferencedClass(((ReferenceAttributeType) attributeType));
                        case FILE ->
                            dao.getClasse(DMS_MODEL_PARENT_CLASS);//TODO improve this (?)
                        default ->
                            throw new IllegalArgumentException();
                    };
                    yield idAndDescriptionToReferenceType(idAndDescription, targetClass);
                }
                case REFERENCEARRAY ->
                    ((List<ReferenceType>) stream(rawToSystem(attributeType, value)).map((r) -> buildReferenceType(((IdAndDescription) r).getId(), ((ReferenceArrayAttributeType) attributeType).getTargetClassName())).collect(toList())).toArray(ReferenceType[]::new);
                case LOOKUP ->
                    buildLookupType(((IdAndDescription) rawToSystem(attributeType, value)).getId());
                case LOOKUPARRAY ->
                    ((List<LookupType>) stream(rawToSystem(attributeType, value)).map((r) -> buildLookupType(((IdAndDescription) r).getId())).collect(toList())).toArray(LookupType[]::new);
                case DECIMAL, INTEGER, LONG, DOUBLE, FLOAT, STRINGARRAY, BYTEARRAY, BYTEAARRAY, BOOLEAN ->
                    value;//TODO check this
                default ->
                    value.toString();
            };
        }
    }

    @Override
    @Nullable
    public <T> T rawValueToFlowValue(@Nullable Object value, Class<T> javaType) {
        if (isNullOrBlank(value)) {
            return defaultForNull(javaType);
        } else if (isReferenceOrLookup(javaType)) {
            return covertReferenceOrLookupValue(value, javaType);
        } else if (javaType.isInstance(value)) {
            return javaType.cast(value);
        } else {
            return CmConvertUtils.convert(value, javaType);
        }
    }

    @Override
    @Nullable
    public Object flowValueToCardValue(Process classe, String key, @Nullable Object value) {
        return flowValueToCardValue(value);
    }

    @Override
    public Map<String, Object> flowValuesToCardValues(Process classe, Map<String, Object> data) {
        return map(transformEntries(data, (k, v) -> flowValueToCardValue(classe, k, v)));
    }

    @Override
    public Map<String, Object> widgetValuesToFlowValues(Map<String, Object> varsAndWidgetData) {
        Map<String, Object> res = map();
        varsAndWidgetData.forEach((key, value) -> {
            value = widgetValueToFlowValue(value);
            res.put(key, value);
        });
        return res;
    }

    private Object widgetValueToFlowValue(Object value) {
        if (value == null) {
            return null;
        } else if (isReferenceOrLookup(value)) {
            return covertReferenceOrLookupValue(value);
        } else {
            return value;//TODO
        }
//		return convertCMDBuildVariable(attributeType, attributeTypeService.getConverter(attributeType).convertValue(value));
    }

    private boolean isReferenceOrLookup(Class classe) {
        return LookupType.class.equals(classe) || ReferenceType.class.equals(classe) || ReferenceType[].class.equals(classe) || LookupType[].class.equals(classe);
    }

    private boolean isReferenceOrLookup(Object value) {
        return value instanceof Lookup || value instanceof WfReference || value instanceof WfReference[];
    }

    private Object covertReferenceOrLookupValue(Object value) {
        if (value instanceof Lookup) {
            return lookupToLookupType(Lookup.class.cast(value));
        } else if (value instanceof WfReference) {
            return referenceToReferenceType(WfReference.class.cast(value));
        } else if (value instanceof WfReference[] wfReferences) {
            return referencesToReferenceTypes(wfReferences);
        } else {
            throw new IllegalArgumentException(format("object = %s is not refrence or lookup", value));
        }
    }

    private <T> T covertReferenceOrLookupValue(Object value, Class<T> javaType) {
        try {
            if (LookupType.class.equals(javaType)) {
                return javaType.cast(toLookupType(value));
            } else if (LookupType[].class.equals(javaType)) {
                List<LookupType> list = (List) convert(value, List.class).stream().map(this::toLookupType).collect(toList());
                return javaType.cast(list.toArray(LookupType[]::new));
            } else if (ReferenceType.class.equals(javaType)) {
                return javaType.cast(toReferenceType(value));
            } else if (ReferenceType[].class.equals(javaType)) {
                List<ReferenceType> list = (List) convert(value, List.class).stream().map(this::toReferenceType).collect(toList());
                return javaType.cast(list.toArray(ReferenceType[]::new));
            }
            throw new IllegalAccessException("unsupported conversion");
        } catch (Exception ex) {
            throw runtime(ex, "unable to convert value = %s (%s) to type = %s", value, getClassOfNullable(value).getName(), javaType.getName());
        }
    }

    private ReferenceType toReferenceType(Object value) {
        if (value instanceof ReferenceType referenceType) {
            if (referenceType.isNotEmpty() && isBlank(referenceType.getClassName())) {
                return buildReferenceType(referenceType.getId(), null, referenceType.getDescription(), referenceType.getCode());
            } else {
                return referenceType;
            }
        } else if (value instanceof WfReference wfReference) {
            return referenceToReferenceType(wfReference);
        } else if (value instanceof IdAndDescription idAndDescription) {
            return idAndDescriptionToReferenceType(idAndDescription);
        } else {
            return buildReferenceType(convert(value, Long.class));
        }
    }

    private LookupType toLookupType(Object value) {
        if (value instanceof LookupType lookupType) {
            return lookupType;
        } else if (value instanceof Lookup lookup) {
            return lookupToLookupType(lookup);
        } else if (value instanceof LookupValueImpl lookupValueImpl) {
            return lookupValueToLookupType(lookupValueImpl);
        } else {
            return buildLookupType(toLong(value));
        }
    }

    @Nullable
    public Object flowValueToCardValue(@Nullable Object value) {
        if (value instanceof LookupType[] arrayLookupType) {
            return list(arrayLookupType).map(v -> flowValueToCardValue(v));
        } else if (value instanceof LookupType) {
            LookupType lookupType = LookupType.class.cast(value);
            return Optional.ofNullable(emptyToNull(lookupType)).map(LookupType::getId).orElse(null);
        } else if (value instanceof ReferenceType) {
            ReferenceType refeference = ReferenceType.class.cast(value);
            return Optional.ofNullable(emptyToNull(refeference)).map(ReferenceType::getId).orElse(null);
        } else {
            return value;
        }
    }

    private ReferenceType[] referencesToReferenceTypes(WfReference[] references) {
        return list(references).map(this::referenceToReferenceType).collect(toList()).toArray(ReferenceType[]::new);
    }

    private ReferenceType referenceToReferenceType(WfReference reference) {
        return buildReferenceType(reference.getId(), reference.getClassName());
    }

    private ReferenceType idAndDescriptionToReferenceType(IdAndDescription idAndDescription) {
        return idAndDescriptionToReferenceType(idAndDescription, null);
    }

    private ReferenceType idAndDescriptionToReferenceType(IdAndDescription idAndDescription, @Nullable Classe targetClass) {
        if (targetClass == null) {
            targetClass = dao.getClasse(BASE_CLASS_NAME);
        }
        if (idAndDescription.hasType()) {
            Classe actualType = dao.getClasse(idAndDescription.getTypeName());
            checkArgument(actualType.equalToOrDescendantOf(targetClass.getName()), "invalid actual type = %s for attribute target type = %s", actualType, targetClass);
            targetClass = actualType;
        }
        boolean autoload = !idAndDescription.hasType() || targetClass == null || targetClass.isSuperclass();//TODO check this
        if (targetClass.isSuperclass() && idAndDescription.hasId()) {
            try {
                targetClass = dao.getType(card(targetClass.getName(), idAndDescription.getId()));
            } catch (Exception ex) {
                logger.warn(marker(), "error retrieving actual type for reference value = {}", idAndDescription, ex);
            }
        }
        return buildReferenceType(idAndDescription.getId(), targetClass.getName(), idAndDescription.getDescription(), idAndDescription.getCode(), autoload);
    }

    private ReferenceType buildReferenceType(@Nullable Long id) {
        return buildReferenceType(id, null);
    }

    private ReferenceType buildReferenceType(@Nullable Long cardId, @Nullable String classId) {
        return buildReferenceType(cardId, classId, null, null);
    }

    private ReferenceType buildReferenceType(@Nullable Long cardId, @Nullable String classId, @Nullable String description, @Nullable String code) {
        return buildReferenceType(cardId, classId, description, code, true);
    }

    private ReferenceType buildReferenceType(@Nullable Long cardId, @Nullable String classId, @Nullable String description, @Nullable String code, boolean autoload) {
        if (isNotNullAndGtZero(cardId)) {
            try {
                // TODO improve performances (?)
                Classe classe;
                if (autoload && (isBlank(classId) || Objects.equals(classId, BASE_CLASS_NAME) || (description == null && code == null))) {
                    classId = firstNotBlank(classId, Constants.BASE_CLASS_NAME);
                    classe = dao.getClasse(classId);
                    Card card = getCardForReference(classe, cardId, description);
                    classe = card.getType();
                    description = card.getDescription();
                    code = card.getCode();
                    classId = classe.getName();
                }
                return new ReferenceType(classId, cardId, description, code);
            } catch (Exception e) {
//                throw new WorkflowException(e, "error converting reference for id = %s and classId =< %s >", cardId, classId);
                logger.warn(marker(), "error converting reference for id = {} and classId =< {} >", cardId, classId, e);
                return new ReferenceType(classId, cardId);
            }
        } else {
            return (ReferenceType) defaultForNull(AttributeTypeName.REFERENCE);
        }
    }

    private Card getCardForReference(Classe classe, long cardId, @Nullable String description) {
        if (workflowConfiguration.enableCardCacheForReferenceMigration()) {
            return cardCache.get(key(classe.getName(), cardId, nullToEmpty(description)), () -> doGetCardForReference(classe, cardId, description));
        } else {
            return doGetCardForReference(classe, cardId, description);
        }
    }

    private Card doGetCardForReference(Classe classe, long cardId, @Nullable String description) {
//        List<String> attrs = classe.isStandardClass() ? list(ATTR_ID, ATTR_CODE, ATTR_DESCRIPTION) : list(ATTR_ID, ATTR_DESCRIPTION);
//        if (classe.isSuperclass()) {
//            List<Card> cards = dao.select(attrs).from(classe).where(ATTR_ID, EQ, cardId).getCards();
//            checkArgument(!cards.isEmpty(), "reference card not found for class = %s id = %s", classe.getName(), cardId);
//            if (cards.size() == 1) {
//                return getOnlyElement(cards);
//            } else {
//                logger.warn("more than one card found for reference id = {}", cardId);
//                if (isNotBlank(description)) {
//                    cards = dao.select(attrs).from(classe).where(ATTR_ID, EQ, cardId).where(ATTR_DESCRIPTION, EQ, description).getCards();
//                    if (cards.size() == 1) {
//                        return getOnlyElement(cards);
//                    }
//                }
//                throw runtime("reference card not found for class = %s id = %s (more than one card found, unable to select only one)", classe.getName(), cardId);
//            }
//        } else {
//            return dao.select(attrs).from(classe).where(ATTR_ID, EQ, cardId).getCard();
//        }
        return dao.getInfo(classe, cardId); //TODO improve this (???)
    }

    private LookupType lookupToLookupType(@Nullable Lookup lookup) {
        return lookup == null ? defaultLookup() : buildLookupType(lookup.getId());
    }

    private LookupType lookupValueToLookupType(@Nullable LookupValueImpl lookup) {
        return lookup == null ? defaultLookup() : buildLookupType(lookup.getId());
    }

    private LookupType buildLookupType(@Nullable Long lookupId) {
        logger.trace("getting lookup with id = {}", lookupId);
        if (isNotNullAndGtZero(lookupId)) {
            try {
                org.cmdbuild.lookup.LookupValue lookupFromStore = lookupStore.getById(lookupId);
                LookupType lookupType = new LookupType();
                lookupType.setType(lookupFromStore.getType().getName());
                lookupType.setId(nullableObjIdToInt(lookupFromStore.getId()));
                lookupType.setCode(lookupFromStore.getCode());
                lookupType.setDescription(lookupFromStore.getDescription());
                return lookupType;
            } catch (Exception e) {
                throw new WorkflowException(e, "error converting lookup = %s", lookupId);
            }
        } else {
            return defaultLookup();
        }
    }

    private int nullableObjIdToInt(@Nullable Long objId) {
        if (objId == null) {
            return -1;
        } else {
            return objId.intValue();
        }
    }

    @Nullable
    private Object defaultForNull(AttributeTypeName typeName) {
        return switch (typeName) {
            case FOREIGNKEY, REFERENCE ->
                defaultReference();
            case LOOKUP ->
                defaultLookup();
            case STRING, CHAR, TEXT ->
                defaultString();
            case BOOLEAN ->
                defaultBoolean();
            case INTEGER, DECIMAL, DOUBLE, FLOAT ->
                0;
            case LONG ->
                null;
            default ->
                null;
        };
    }

    @Nullable
    private <T> T defaultForNull(Class<T> type) {
        if (Objects.equals(type, ReferenceType.class)) {
            return type.cast(defaultReference());
        } else if (Objects.equals(type, LookupType.class)) {
            return type.cast(defaultLookup());
        } else if (Objects.equals(type, String.class)) {
            return type.cast(defaultString());
        } else if (Objects.equals(type, Boolean.class)) {
            return type.cast(defaultBoolean());
        } else {
            return null;
        }
    }

    @Override
    public <T> T defaultValueForFlowInitialization(Class<T> type) {
        if (Objects.equals(type, Boolean.class)) {
            return type.cast(defaultBoolean());
        } else if (Objects.equals(type, String.class)) {
            return type.cast(defaultString());
        } else if (Objects.equals(type, Long.class)) {
            return type.cast(defaultInteger());
        } else if (Objects.equals(type, Double.class)) {
            return type.cast(defaultDouble());
        } else {
            return defaultForNull(type);
        }
    }

    @Override
    @Nullable
    public Object inflateFlowValueToCardValue(@Nullable Object value) {
        if (value instanceof LookupType lookupT) {
            LookupType lookupType = buildLookupType(lookupT.getId());
            return lookupType.isEmpty() ? null : rawToSystem(new LookupAttributeType(lookupType.getType()), value);
        } else if (value instanceof ReferenceType referenceType) {
            ReferenceType reference = buildReferenceType(referenceType.getId(), referenceType.getClassName());
            return reference.isEmpty() ? null : rawToSystem(new ForeignKeyAttributeType(reference.getClassName()), value);
        } else {
            return value;
        }
    }

}
