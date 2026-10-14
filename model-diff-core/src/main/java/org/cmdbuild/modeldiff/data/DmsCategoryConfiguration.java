/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import org.cmdbuild.lookup.DmsAttachmentCountCheck;
import org.cmdbuild.lookup.LookupConfig;
import org.cmdbuild.modeldiff.core.LookupValueConfiguration;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

/**
 *
 * @author afelice
 */
public class DmsCategoryConfiguration extends LookupValueConfiguration {
        
    private String modelClass;    
    private String allowedExtensions;
    private Integer maxFileSize;    
    private DmsAttachmentCountCheck checkCount;    
    private Integer checkCountNumber;    
    
    
    public DmsCategoryConfiguration(long id, String categoryName, String code, String description) {
        super(id, categoryName, code, description);
    }    

    public String getModelClass() {
        return modelClass;
    }

    public String getAllowedExtensions() {
        return allowedExtensions;
    }

    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    public DmsAttachmentCountCheck getCheckCount() {        
        return checkCount;
    }    
    
    @JsonProperty("checkCount")
    public String getCheckCountStr() {        
        return serializeEnum(checkCount);
    }

    public Integer getCheckCountNumber() {
        return checkCountNumber;
    }    

    /**
     * see {@link LookupValueWsCommons#toResponse(LookupValue)}, when <code>lookupType.isDmsCategorySpeciality()</code>.
     * @param config 
     */
    @Override
    public void applyConfig(LookupConfig config) {
        super.applyConfig(config);
        
        // handle DMS Category, 
        modelClass = config.getDmsModelClass();
        allowedExtensions = Joiner.on(",").join(nullToEmpty(config.getDmsAllowedExtensions()));
        checkCount = config.getDmsCheckCount();
        checkCountNumber = config.getDmsCheckCountNumber();
        maxFileSize = config.getMaxFileSize();
    }
    
}
