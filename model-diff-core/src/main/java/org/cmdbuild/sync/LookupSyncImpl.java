/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.lookup.LookupValueImpl;
import org.cmdbuild.lookup.LookupValueImpl.LookupBuilder;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_ID_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_VALUE_PARENT_ID_SERIALIZATION;
import org.cmdbuild.service.rest.common.serializationhelpers.LookupSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.WsLookupType;
import static org.cmdbuild.service.rest.common.serializationhelpers.WsLookupType.isProtected;
import org.cmdbuild.service.rest.common.serializationhelpers.WsLookupValue;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.stereotype.Component;

/**
 * <b>Note</b>: <i>update</i> and <i>deactivate</i> of {@link LookupType} is not
 * available in CMDBuild {@link LookupService}.
 *
 *
 * @author afelice
 */
@Component
public class LookupSyncImpl implements LookupSync {

    private final LookupService lookupService;
    private final ObjectTranslationService translationService;
    private final LookupSerializationHelper serializationHelper;

    public LookupSyncImpl(LookupService lookupService, ObjectTranslationService translationService,
            LookupSerializationHelper serializationHelper) {
        this.lookupService = checkNotNull(lookupService);
        this.translationService = checkNotNull(translationService);
        this.serializationHelper = checkNotNull(serializationHelper);
    }

    /**
     *
     * @param filterStr blank to return all {@link LookupType}s
     * @return all {@link LookupType} except <i>dms categories</i>. For <i>dms
     * categories</i>, use {@link DmsSync#readDmsCategories(java.lang.String) }.
     */
    @Override
    public List<LookupType> readTypes(String filterStr) {
        // Symmetrical to DmsSync.readDmsCategories()
        return lookupService.getAllTypes(filterStr).stream().filter(LookupType::isDefaultSpeciality).collect(toList());
    }

    @Override
    public LookupType readType(Long lookupTypeId) {
        return lookupService.getLookupType(lookupTypeId);
    }

    @Override
    public LookupType readType(String lookupTypeName) {
        return lookupService.getLookupType(lookupTypeName);
    }

    @Override
    public List<LookupValue> readValues(String lookupTypeName) {
        return lookupService.getAllLookup(lookupTypeName).elements();
    }

    @Override
    public LookupValue readValue(String lookupTypeName, String lookupValueCode) {
        return lookupService.getLookupByTypeAndCodeOrNull(lookupTypeName, lookupValueCode);
    }

    /**
     * As in DmsSynchImpl.fetchDmsCategoryDescrTranslations().
     *
     * @param lookupTypeName
     * @param valuesList
     * @return
     */
    @Override
    public Map<String, String> fetchLookupDescrTranslations(String lookupTypeName, List<LookupValue> valuesList) {
        return valuesList.stream()
                .collect(
                        Collectors.toMap(
                                LookupValue::getCode,
                                v -> translationService.translateLookupDescriptionSafe(lookupTypeName, v.getCode(), v.getDescription())
                        )
                );
    }

    /**
     * Adds a {@link LookupType}.
     *
     * @param lookupTypeName
     * @param lookupTypeCmdbSerialization
     * @return
     */
    @Override
    public LookupType addType(String lookupTypeName, Map<String, Object> lookupTypeCmdbSerialization) {
        final LookupType lookupType = buildType(lookupTypeCmdbSerialization);
        if (isProtected(lookupTypeCmdbSerialization)) {
            // A protected LookupType
            return lookupService.createLookupType_Protected(lookupType);
        }

        // A normal LookupType
        return lookupService.createLookupType(lookupType);
    }

    /**
     * Removes a {@link LookupType} and all related {@link LookupValue}.
     *
     * @param lookupType
     * @return
     */
    @Override
    public void removeType(LookupType lookupType) {
        lookupService.deleteLookupType(lookupType.getName());
    }

    /**
     * Adds a {@link LookupValue}.
     *
     * <p>
     * <b>Note</b>: <i>lookup value</i>
     *
     * @param lookupValue already built value using
     * {@link #buildValue(java.util.Map, org.cmdbuild.lookup.LookupType, com.fasterxml.jackson.databind.ObjectMapper)};
     * <code>id</code> shouldn't be set, otherwise an error is raised.
     * @return
     */
    @Override
    public LookupValue addValue(LookupValue lookupValue) {
        // As in LookupValueWsCommons.doCreate()        
        // LookupValue lookup = lookupService.createOrUpdateLookup(wsLookupValue.buildLookup().withType(lookupType).build());        

        if (lookupValue.hasId()) {
            throw runtime("trying to add a new LookupValue =< %s > for LookupType =< %s > but id is set; expected null.", lookupValue.getId(), lookupValue.getLookupType());
        }

        // @todo AFE add LookupValue description translation: _description_translation        
        return lookupService.createOrUpdateLookup(lookupValue);
    }

    /**
     * Updates an existing {@link LookupValue}.
     *
     * @param lookupValue already built value using
     * {@link #buildValue(java.util.Map, org.cmdbuild.lookup.LookupType, com.fasterxml.jackson.databind.ObjectMapper)};
     * <code>id</code> <b>has to be set to system {@link LookupValue} one</b>,
     * otherwise and error is raised.
     *
     * @return
     */
    @Override
    public LookupValue updateValue(LookupValue lookupValue) {
        // As in LookupValueWsCommons.doUpdate()        
        //LookupValue lookup = lookupService.createOrUpdateLookup(wsLookupValue.buildLookup().withType(lookupType).withId(checkNotNull(lookupId)).build());

        if (!lookupValue.hasId()) {
            throw runtime("trying to update an existent LookupValue =< %s > for LookupType =< %s > but id is null.", lookupValue.getId(), lookupValue.getLookupType());
        }

        // @todo AFE add LookupValue description translation: _description_translation        
        
        return lookupService.createOrUpdateLookup(lookupValue);
    }

    /**
     * Only <i>disables</i> the {@link LookupValue}.
     *
     * @param lookupValue already built value using
     * {@link #buildValue(java.util.Map, org.cmdbuild.lookup.LookupType, com.fasterxml.jackson.databind.ObjectMapper)};
     * <code>id</code> <b>has to be set to system {@link LookupValue} one</b>,
     * otherwise and error is raised.
     *
     * @return
     */
    @Override
    public LookupValue deactivateValue(LookupValue lookupValue) {
        // @todo AFE TBC
//        if (lookupValue.getId() == null) {
//            String lookupTypeName = lookupValue.getLookupType();
//            String lookupValueCode = lookupValue.getCode();
//            lookupValue = lookupService.getLookupByTypeAndCodeOrNull(lookupTypeName, lookupValueCode);
//            if (lookupValue == null) {
//                throw runtime("trying to deactivate a LookupValue with code =< %s > for LookupType =< %s > but not found.", lookupValueCode, lookupTypeName);
//            }
//        }
        LookupValue deactivatedValue = LookupValueImpl.copyOf(lookupValue).withActive(false).build();
        return updateValue(deactivatedValue);
    }    
    
    /**
     * Only <i>disables</i> the {@link LookupValue}.
     *
     * @param lookupValue already built value using
     * {@link #buildValue(java.util.Map, org.cmdbuild.lookup.LookupType, com.fasterxml.jackson.databind.ObjectMapper)};
     * <code>id</code> has to be set, otherwise and error is raised.
     * 
     */
    @Override
    public void removeValue(LookupValue lookupValue) {
        lookupService.deleteLookupValue(lookupValue.getLookupType(), lookupValue.getId());
    }

    @Override
    public FluentMap<String, Object> serializeLookupTypeProps(LookupType lookupType) {
        return WsLookupType.serializeLookupTypeProps(lookupService, lookupType);
    }

    @Override
    public FluentMap<String, Object> serializeLookupValueProps(LookupValue lookupValue) {
        return serializationHelper.serializeLookupValue(lookupValue);
    }        
    
    @Override
    public LookupType buildType(Map<String, Object> cmdbTypePropsSerialization) {
        return WsLookupType.toLookupType(lookupService, cmdbTypePropsSerialization);
    }

    @Override
    public LookupValue buildValue(Map<String, Object> cmdbValuePropsSerialization, LookupType ownerType, ObjectMapper objectMapper) {
        Map<String, Object> cmdbValuePropsSerialization_withParent = map(cmdbValuePropsSerialization).with(ATTR_VALUE_PARENT_ID_SERIALIZATION, ownerType.getParent());
        WsLookupValue wsLookupValue = objectMapper.convertValue(cmdbValuePropsSerialization_withParent, WsLookupValue.class);
        wsLookupValue.buildLookup().withType(ownerType);

        LookupBuilder valueBuilder = wsLookupValue.buildLookup().withType(ownerType);

        if (cmdbValuePropsSerialization.containsKey(ATTR_ID_SERIALIZATION) && cmdbValuePropsSerialization.get(ATTR_ID_SERIALIZATION) != null) {
            valueBuilder.withId(fetchId(cmdbValuePropsSerialization));
        }

        return valueBuilder.build();
    }

    public Long fetchId(Map<String, Object> props) {
        return toLongOrNull(props.get(ATTR_ID_SERIALIZATION));
    }    
    
}
