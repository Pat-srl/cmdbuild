/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;


import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.utils.lang.CmMapUtils;

import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
public class StoredFunctionSerializer {

    public static CmMapUtils.FluentMap<String, Object> toResponse(StoredFunction input) {
        return map("_id", input.getId(),
                "name", input.getName(),
                "description", input.getName());
    }

    public static Object toDetailedResponse(StoredFunction input, AttributeTypeConversionService attributeTypeConversionService) {
        return toResponse(input).with(
                "tags", input.getTags(),
                "source", input.getSourceClassName(),
                "metadata", input.getMetadataExt(),
                "parameters", attributeTypeConversionService.serializeParametersList(input.getInputParameters()));
    }
}
