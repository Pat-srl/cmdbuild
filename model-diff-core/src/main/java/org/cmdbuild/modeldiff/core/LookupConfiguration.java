/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import org.cmdbuild.lookup.LookupAccessType;
import org.cmdbuild.lookup.LookupSpeciality;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

/**
 *
 * @author afelice
 */
public class LookupConfiguration {

    private final String name;
    @JsonProperty("_id")
    private final String id;
    public String parent;
    
    private LookupSpeciality speciality;
    private LookupAccessType accessType;
    
    protected final List<LookupValueConfiguration> values; 
        
    public LookupConfiguration(String name) {
        this(name, list());
    }
    
    public LookupConfiguration(String name, List<? extends LookupValueConfiguration> values) {
        this.name = checkNotNull(name);
        this.id = this.name; // See LookupTypeWs.toResponse
        checkArgument(!CmCollectionUtils.isNullOrEmpty(values), "empty map found for lookup =< %s >", name);
        this.values = list();
        values.forEach(lvConf -> this.values.add(lvConf));
    }

    public String getName() {
        return name;
    }
    
    public String getId() {
        return id;
    }    

    public List<LookupValueConfiguration> getValues() {
        return values;
    }

    public void addValue(LookupValueConfiguration value) {
        values.add(value);
    }
    
    @JsonIgnore
    public LookupSpeciality getSpeciality() {
        return speciality;
    }

    @JsonProperty("speciality")
    public String getSpecialityStr() {
        return serializeEnum(speciality);
    }

    public void setSpeciality(LookupSpeciality speciality) {
        this.speciality = speciality;
    }

    // Wan't be serialized
//    @JsonProperty("speciality")
//    public void setSpeciality(String specialityStr) {
//        this.speciality = parseEnumOrDefault(typeStr, IconType.NONE);
//    }    
    
    @JsonIgnore
    public LookupAccessType getAccessType() {
        return accessType;
    }

    @JsonProperty("accessType")
    public String getAccessTypeStr() {
        return serializeEnum(accessType);
    }

    public void setAccessType(LookupAccessType accessType) {
        this.accessType = accessType;
    }
     
 
}
