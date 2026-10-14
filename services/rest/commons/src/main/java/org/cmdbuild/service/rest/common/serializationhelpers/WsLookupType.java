/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Strings.emptyToNull;
import jakarta.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import org.cmdbuild.lookup.LookupAccessType;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupSpeciality;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupTypeImpl;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class WsLookupType {

    public final String name, parent;

    public WsLookupType(LookupService lookupService, LookupType lookupType) {
        this.name = lookupType.getName();
        this.parent = fetchParentName(lookupService, lookupType);
    }
    
    @JsonCreator
    public WsLookupType(@JsonProperty("name") String name, @JsonProperty("parent") @Nullable String parent) {
        this.name = checkNotBlank(name);
        this.parent = emptyToNull(parent);
    }

    public LookupTypeImpl.LookupTypeImplBuilder toLookupType(LookupService lookupService) {
        return LookupTypeImpl.builder()
                .withName(name)
                .withParent(fetchParent(lookupService, parent).map(LookupType::getId).orElse(null));//TODO improve this
    }

    /**
     * Was in <code>LookupTypeWs.createLookupType()</code>.
     * 
     * @param lookupService
     * @param cmdbTypePropsSerialization
     * @return 
     */
    static public LookupType toLookupType(LookupService lookupService, Map<String, Object> cmdbTypePropsSerialization) {
        WsLookupType wsLookupType = new WsLookupType(getTypeName(cmdbTypePropsSerialization), getTypeParentName(cmdbTypePropsSerialization));
        
        return wsLookupType.toLookupType(lookupService)
                .withSpeciality(getTypeSpeciality(cmdbTypePropsSerialization))
                .withAccessType(getTypeAccess(cmdbTypePropsSerialization))
                .build();
    }
    
    static public FluentMap<String, Object> serializeLookupTypeProps(LookupService lookupService, LookupType lookupType) {
        return map(
                "_id", lookupType.getName(),
                "name", lookupType.getName(),
                "parent", fetchParentName(lookupService, lookupType),
                "speciality", serializeEnum(lookupType.getSpeciality()),
                "accessType", serializeEnum(lookupType.getAccessType()));
    }

    static public String getTypeName(Map<String, Object> cmdbTypePropsSerialization) {
        return toStringOrNull(cmdbTypePropsSerialization.get("name"));
    }
    
    static public String getTypeParentName(Map<String, Object> cmdbTypePropsSerialization) {
        return toStringOrNull(cmdbTypePropsSerialization.get("parent"));
    }    
    
    static private String fetchParentName(LookupService lookupService, LookupType lookupType) {
        return fetchParent(lookupService, lookupType.getParent()).map(LookupType::getName).orElse(null); //TODO improve this
    }
    
    /**
     * Used converting from some serialization to a
     * {@link LookupType}.
     * 
     * @param props
     * @return 
     */
    static public LookupSpeciality getTypeSpeciality(Map<String, Object> props) {
        String lookupTypeSpecialityStr = toStringOrNull(props.get("speciality"));
        
        if (lookupTypeSpecialityStr != null) {
            return parseEnum(lookupTypeSpecialityStr, LookupSpeciality.class);
        }
        
        return LookupSpeciality.LS_DEFAULT;
    }

    /**
     * Used converting from some serialization to a
     * {@link LookupType}.
     * 
     * @param props
     * @return 
     */
    static public LookupAccessType getTypeAccess(Map<String, Object> props) {
        String lookupTypeAccessStr = toStringOrNull(props.get("accessType"));
        
        if (lookupTypeAccessStr != null) {
            return parseEnum(lookupTypeAccessStr, LookupAccessType.class);
        }
        
        return LookupAccessType.LT_DEFAULT;
    }        
    
    static private Optional<LookupType> fetchParent(LookupService lookupService, Long parentId) {
        return Optional.ofNullable(parentId).map(l -> lookupService.getLookupType(l));
    }
    
    static private Optional<LookupType> fetchParent(LookupService lookupService, String parentIdStr) {
        return Optional.ofNullable(emptyToNull(parentIdStr)).map(l -> lookupService.getLookupType(l));
    }   
    
    /**
    /**
     * Used by <code>model-diff-schema</code> to handle creation of
     * <code>protected</code> {@link LookupType}s.
     * 
     * @param props
     * @return 
     */
    static public boolean isProtected(Map<String, Object> props) {
        LookupAccessType accessType = getTypeAccess(props);
        
        return accessType == LookupAccessType.LT_PROTECTED;
    }        
        
}
