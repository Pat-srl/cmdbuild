/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_PROCESS_PLAN_ID_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_PROCESS_XPDL_MD5_SERIALIZATION;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;

/**
 * Represents {@link Process} model configuration.
 *
 * @author afelice
 */
public class ProcessConfiguration extends ClasseConfiguration {

    @JsonCreator
    public ProcessConfiguration() {
        super();
    }    
    
    /**
     *
     * @param name {@link Process} name.
     */
    public ProcessConfiguration(String name) {
        super(name);
    }

    @Override
    @JsonProperty("process")
    public void setCmdbSerialization(Map<String, Object> cmdbSerialization) {
        super.setCmdbSerialization(cmdbSerialization);
    }

    @Override
    @JsonProperty("process")
    public Map<String, Object> getCmdbSerialization() {
        return super.getCmdbSerialization();
    }

    public static String fetchPlanId(Map<String, Object> cmdbSerialization) {
        return convertToString(cmdbSerialization.get(ATTR_PROCESS_PLAN_ID_SERIALIZATION));
    }

    private static String convertToString(Object value) {
        return convert(value, String.class);
    }

    public void addXpdlMd5(String xpdlMd5) {
        getCmdbSerialization().put(ATTR_PROCESS_XPDL_MD5_SERIALIZATION, xpdlMd5);
    }

    public String fetchXpdlMd5() {
        return convertToString(getCmdbSerialization().get(ATTR_PROCESS_XPDL_MD5_SERIALIZATION));
    }

}
