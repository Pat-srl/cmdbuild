/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

/**
 *
 * @author afelice
 */
public interface LookupSync {

    List<LookupType> readTypes(String filterStr);

    default List<LookupType> readTypes() {
        return readTypes("");
    }

    LookupType readType(Long lookupTypeId);
    
    LookupType readType(String lookupTypeName);
       
    List<LookupValue> readValues(String lookupTypeName);

    LookupValue readValue(String lookupTypeName, String lookupValueCode);

    Map<String, String> fetchLookupDescrTranslations(String lookupTypeName, List<LookupValue> valuesList);
    
    LookupType addType(String lookupTypeName, Map<String, Object> lookupTypeCmdbSerialization);
    
    void removeType(LookupType lookupType);
    
    LookupValue addValue(LookupValue lookupValue);
    
    LookupValue updateValue(LookupValue lookupValue);
    
    LookupValue deactivateValue(LookupValue lookupValue);
    
    void removeValue(LookupValue lookupValue);
        
    FluentMap<String, Object> serializeLookupTypeProps(LookupType lookupType);    
    
    FluentMap<String, Object> serializeLookupValueProps(LookupValue lookupValue);    
    
    LookupType buildType(Map<String, Object> cmdbTypePropsSerialization);
    
    LookupValue buildValue(Map<String, Object> cmdbValuePropsSerialization, LookupType ownerType, ObjectMapper objectMapper);
}
    
