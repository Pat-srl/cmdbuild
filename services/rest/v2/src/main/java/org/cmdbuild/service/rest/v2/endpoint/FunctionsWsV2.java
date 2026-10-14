package org.cmdbuild.service.rest.v2.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.uniqueIndex;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PARAMETERS;
import org.cmdbuild.service.rest.v2.serializationhelpers.CardWsSerializationHelperV2;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Path("functions/")
@Produces(APPLICATION_JSON)
public class FunctionsWsV2 {

    private final DaoService dao;
    private final CardWsSerializationHelperV2 helper;

    public FunctionsWsV2(DaoService dao, CardWsSerializationHelperV2 helper) {
        this.dao = checkNotNull(dao);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path(EMPTY)
    public Object readMany() {
        List<StoredFunction> list = dao.getAllFunctions();
        return map("data", list.stream().map(this::serializeResponse).collect(toList()), "meta", map("total", list.size()));
    }

    @GET
    @Path("{functionId}/")
    public Object readOne(@PathParam("functionId") Long functionId) {
        StoredFunction function = dao.getFunctionById(functionId);
        return map("data", serializeResponse(function), "meta", map());
    }

    @GET
    @Path("{functionId}/parameters/")
    public Object readInputParameters(@PathParam("functionId") Long functionId) {
        StoredFunction function = dao.getFunctionById(functionId);
        Collection<Attribute> parameters = function.getInputParameters();
        return map("data", serializeResponseParameters(parameters), "meta", map());
    }

    @GET
    @Path("{functionId}/attributes/")
    public Object readOutputParameters(@PathParam("functionId") Long functionId) {
        StoredFunction function = dao.getFunctionById(functionId);
        Collection<Attribute> oParameters = function.getOutputParameters();
        return map("data", serializeResponseParameters(oParameters), "meta", map("total", oParameters.size()));
    }

    @GET
    @Path("{functionId}/outputs/")
    public Object call(@PathParam("functionId") Long functionId, @QueryParam(PARAMETERS) String inputs, @QueryParam("model") String model) {
        StoredFunction function = dao.getFunctionById(functionId);

        Map<String, Object> params = isBlank(inputs) ? emptyMap() : fromJson(inputs, MAP_OF_OBJECTS);
        List<Object> inputParams = function.getInputParameters().stream().map(Attribute::getName).map(params::get).collect(toList());

        List<Attribute> outputParams = isBlank(model)
                ? function.getOutputParameters()
                : parseOutputParameters(function, model);

        List rows = dao.selectFunction(function, inputParams, outputParams).run().stream().map((r) -> r.asMap())
                .map((source) -> map().accept((map) -> outputParams.forEach((p) -> helper.addCardValuesAndDescriptionsAndExtras(p.getName(), p.getType(), p.getMetadata(), source::get, map::put)))).collect(toList());

        return map("data", rows, "meta", map("total", rows.size()));
    }

    private CmMapUtils.FluentMap<String, Object> serializeResponse(StoredFunction input) {
        return map(
                "name", input.getName(),
                "description", input.getName(),
                "metadata", input.getMetadataExt(),
                "_id", input.getId());
    }

    private List<Attribute> parseOutputParameters(StoredFunction function, String modelStr) {
        WsFunctionOutputModel model = CmJsonUtils.fromJson(modelStr, WsFunctionOutputModel.class);
        Map<String, WsFunctionOutputParameter> customParams = uniqueIndex(model.output, (p) -> p.name);
        return function.getOutputParameters().stream().map((param) -> {
            WsFunctionOutputParameter customParam = customParams.get(param.getName());
            if (customParam != null) {
                param = AttributeImpl.builder().withOwner(function).withName(param.getName()).withType(customParam.type).withMeta(param.getMetadata()).build();
            }
            return param;

        }).collect(toList());
    }

    private static class WsFunctionOutputModel {

        private final List<WsFunctionOutputParameter> output;

        public WsFunctionOutputModel(@JsonProperty("output") List<WsFunctionOutputParameter> output) {
            this.output = checkNotNull(output);
        }

    }

    private static class WsFunctionOutputParameter {

        private final String name;
        private final CardAttributeType type;

        public WsFunctionOutputParameter(@JsonProperty("name") String name, @JsonProperty("type") String typeStr, @JsonProperty("fkTarget") String fkTarget, @JsonProperty("lookupType") String lookupType) {
            this.name = checkNotBlank(name);
            AttributeTypeName typeName = AttributeTypeName.valueOf(typeStr.toUpperCase());
            type = switch (typeName) {
                case FOREIGNKEY ->
                    new ForeignKeyAttributeType(fkTarget);
                case LOOKUP ->
                    new LookupAttributeType(lookupType);
                default ->
                    throw unsupported("unsupported param type = %s", typeStr);
            };
        }

    }

    private Object serializeResponseParameters(Collection<Attribute> parameters) {
        return response(parameters.stream().map(this::serializeParam).collect(toList()));
    }

    private Object serializeParam(Attribute param) {
        return map(
                "name", param.getName(),
                "type", AttributeTypeConversionService.serializeAttributeType(param.getType().getName()),
                "_id", param.getName());
    }

    private StoredFunction getFunction(String idOrName) {
        checkNotBlank(idOrName);
        if (NumberUtils.isCreatable(idOrName)) {
            return dao.getFunctionById(Long.parseLong(idOrName));
        } else {
            return dao.getFunctionByName(idOrName);
        }
    }

}
