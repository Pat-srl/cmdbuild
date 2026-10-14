/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Streams.stream;
import static java.lang.String.format;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.client.rest.api.ClassApi;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.model.AttributeData;
import org.cmdbuild.client.rest.model.AttributeDataImpl;
import org.cmdbuild.client.rest.model.AttributeRequestData;
import org.cmdbuild.client.rest.model.ClassData;
import org.cmdbuild.client.rest.model.ClassDataImpl;
import org.cmdbuild.client.rest.model.GeoAttributeData;
import org.cmdbuild.client.rest.model.GeoAttributeDataImpl;
import org.cmdbuild.dao.entrytype.ClassDefinition;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toPrettyJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ClasseApiImpl extends AbstractServiceClientImpl implements ClassApi {

    public ClasseApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public ClassData getById(String classeId) {
        logger.debug("get classe for classeId = {}", classeId);
        JsonNode response = get("classes/" + encodeUrlPath(checkNotBlank(classeId, "classeId cannot be blank"))).asJackson();
        return responseToClassData(response);
    }

    @Override
    public String getRawJsonById(String classId) {
        return toPrettyJson(get("classes/" + encodeUrlPath(checkNotBlank(classId, "classeId cannot be blank"))).asJackson().get("data"));
    }

    @Override
    public String getProcessRawJsonById(String processId) {
        return toPrettyJson(get("processes/" + encodeUrlPath(checkNotBlank(processId, "processId cannot be blank"))).asJackson().get("data"));
    }

    @Override
    public List<ClassData> getAll() {
        logger.debug("get all classes");
        JsonNode response = get("classes/").asJackson();
        return stream(response.get("data")).map(element -> fromJson(element, ClassDataImpl.class)).collect(toList());
    }

    @Override
    public ClassData create(ClassData data) {
        JsonNode response = post("administration/classes/", classDataToRequest(data)).asJackson();
        data = responseToClassData(response);
        return data;
    }

    @Override
    public ClassData create(ClassDefinition data) {
        return create(ClassDataImpl.builder().withDefaults()
                .withName(data.getName())
                .withParentId(data.getParentOrNull())
                .withDescription(data.getMetadata().getDescription())
                .withType(serializeEnum(data.getMetadata().getClassType()))
                .withActive(data.getMetadata().isActive())
                .withSuperclass(data.getMetadata().isSuperclass())
                .build());
    }

    @Override
    public ClassApiWithClassData update(ClassData data) {
        JsonNode response = put("administration/classes/" + encodeUrlPath(data.getName()), classDataToRequest(data)).asJackson();
        data = responseToClassData(response);
        return new ClasseApiWithClassDataImpl(data);
    }

    @Override
    public ClassApiWithClassData update(String classId, String rawJsonData) {
        JsonNode response = put("administration/classes/" + encodeUrlPath(checkNotBlank(classId)), checkNotBlank(rawJsonData)).asJackson();
        ClassData data = responseToClassData(response);
        return new ClasseApiWithClassDataImpl(data);
    }

    @Override
    public ClassApi deleteById(String classeId) {
        delete("administration/classes/" + encodeUrlPath(checkNotBlank(classeId, "classeId cannot be blank")));
        return this;
    }

    protected Object classDataToRequest(ClassData data) {
        return map("name", data.getName(),
                "description", data.getDescription(),
                "prototype", data.isSuperclass(),
                "active", data.isActive(),
                "parent", data.getParentId(),
                "type", data.getType());
    }

    protected ClassData responseToClassData(JsonNode response) {
        return fromJson(response.get("data"), ClassDataImpl.class);
    }

    @Override
    public List<AttributeData> getAttributes(String classId) {
        JsonNode response = get("classes/" + encodeUrlPath(checkNotBlank(classId, "classId cannot be blank")) + "/attributes/").asJackson();
        return stream(response.get("data")).map(element -> fromJson(element, AttributeDataImpl.class)).collect(toList());//TODO deserialize metadata, filter
    }

    @Override
    public ClassApiWithAttrData createAttr(String classId, AttributeRequestData data) {
        JsonNode response = post(format("administration/classes/%s/attributes/", encodeUrlPath(checkNotBlank(classId, "classId cannot be blank"))), attrDataToRequest(data)).asJackson();
        AttributeData responseData = responseToAttrData(response);
        return new ClassApiWithAttrDataImpl(responseData);
    }

    @Override
    public ClassApiWithAttrData updateAttr(String classId, AttributeRequestData data) {
        JsonNode response = put(format("administration/classes/%s/attributes/%s", encodeUrlPath(checkNotBlank(classId, "classId cannot be blank")), encodeUrlPath(data.getName())), attrDataToRequest(data)).asJackson();
        AttributeData responseData = responseToAttrData(response);
        return new ClassApiWithAttrDataImpl(responseData);
    }

    @Override
    public List<GeoAttributeData> getGeoAttributes(String classId) {
        JsonNode response = get("classes/" + encodeUrlPath(checkNotBlank(classId, "classId cannot be blank")) + "/geoattributes/").asJackson();
        return stream(response.get("data")).map(element -> fromJson(element, GeoAttributeDataImpl.class)).collect(toList());
    }

    @Override
    public GeoAttributeData getGeoAttribute(String classId, Object geoAttributeId) {
        JsonNode response = get("classes/" + encodeUrlPath(checkNotBlank(classId, "classId cannot be blank")) + "/geoattributes/" + geoAttributeId).asJackson();
        return fromJson(response.get("data"), GeoAttributeDataImpl.class);
    }

    @Override
    public GeoAttributeData createGeoAttr(String classId, GeoAttributeData data) {
        JsonNode response = post(format("administration/classes/%s/geoattributes/", encodeUrlPath(checkNotBlank(classId, "classId cannot be blank"))), geoAttrDataToRequest(classId, data)).asJackson();
        GeoAttributeData responseData = responseToGeoAttrData(response);
        return responseData;
    }

    @Override
    public boolean deleteGeoAttr(String classId, Object geoAttributeId) {
        JsonNode response = delete(format("administration/classes/%s/geoattributes/%s", encodeUrlPath(checkNotBlank(classId, "classId cannot be blank")), checkNotBlank(geoAttributeId, "attrId cannot be blank"))).asJackson();
        return response.get("success").asBoolean();
    }

    @Override
    public AttributeData getAttr(String classId, String attrId) {
        JsonNode response = get("classes/" + encodeUrlPath(checkNotBlank(classId, "classId cannot be blank")) + "/attributes/" + encodeUrlPath(checkNotBlank(attrId, "attrId cannot be blank"))).asJackson();
        return responseToAttrData(response);
    }

    protected AttributeData responseToAttrData(JsonNode jsonNode) {
//		try {
        return fromJson(jsonNode.get("data"), AttributeDataImpl.class);
//		} catch (IOException ex) {
//			throw runtime(ex);
//		}
    }

    protected GeoAttributeData responseToGeoAttrData(JsonNode jsonNode) {
//		try {
        return fromJson(jsonNode.get("data"), GeoAttributeDataImpl.class);
//		} catch (IOException ex) {
//			throw runtime(ex);
//		}
    }

    public static Object geoAttrDataToRequest(String className, GeoAttributeData data) {
        return map(
                "name", data.getName(),
                "owner_type", className,
                "active", data.isActive(),
                "type", data.getType(),
                "subtype", data.getSubType(),
                "description", data.getDescription(),
                "index", data.getIndex(),
                "visibility", data.getVisibility(),
                "zoomMin", data.getZoomMin(),
                "zoomMax", data.getZoomMax(),
                "zoomDef", data.getZoomDef(),
                "style", data.getStyle())
                .with("infoWindowEnabled", data.isInfoWindowEnabled(),
                        "infoWindowContent", data.getInfoWindowContent(),
                        "infoWindowImage", data.getInfoWindowImage());
    }

    public static Object attrDataToRequest(AttributeRequestData data) {
        return map("type", data.getType(),
                "name", data.getName(),
                "description", data.getDescription(),
                "showInGrid", data.getShowInGrid(),
                "showInReducedGrid", data.getShowInGrid(),//TODO
                "domain", data.getDomainName(),
                "direction", data.getDirection(),
                "unique", data.getUnique(),
                "mandatory", data.getRequired(),
                "active", data.getActive(),
                "index", data.getIndex(),
                "defaultValue", data.getDefaultValue(),
                "group", data.getGroup(),
                "precision", data.getPrecision(),
                "scale", data.getScale(),
                "targetClass", data.getTargetClass(),
                "maxLength", data.getLength(),
                "editorType", data.getEditorType(),
                "lookupType", data.getLookupType(),
                "filter", data.getFilter(),
                "mode", data.getMode().name(),
                "metadata", data.getMetadata(),
                "classOrder", data.getClassOrder(),
                "ipType", data.getIpType(),
                "isMasterDetail", data.isMasterDetail(),
                "masterDetailDescription", data.getMasterDetailDescription(),
                "textContentSecurity", serializeEnum(data.getTextContentSecurity()),
                "unitOfMeasure", data.getUnitOfMeasure(),
                "unitOfMeasureLocation", data.getUnitOfMeasureLocation(),
                "showThousandsSeparator", data.getShowThousandsSeparator(),
                "showLabel", data.getShowLabel(),
                "labelRequired", data.getLabelRequired(),
                "formulaType", data.getFormulaType(),
                "formulaCode", data.getFormulaCode(),
                "password", data.getPassword(),
                "showPassword", data.getShowPassword(),
                "dmsCategory", data.getDmsCategory(),
                "trimEnabled", data.getTrimEnabled());
    }

    @Override
    public ClassApi deleteAttr(String classId, String attrId) {
        delete("administration/classes/" + encodeUrlPath(checkNotBlank(classId, "classId cannot be blank")) + "/attributes/" + encodeUrlPath(checkNotBlank(attrId, "attrId cannot be blank")));
        return this;
    }

    protected class ClasseApiWithClassDataImpl implements ClassApiWithClassData {

        private final ClassData classe;

        public ClasseApiWithClassDataImpl(ClassData classe) {
            this.classe = checkNotNull(classe);
        }

        @Override
        public ClassApi then() {
            return ClasseApiImpl.this;
        }

        @Override
        public ClassData getClasse() {
            return classe;
        }

    }

    protected class ClassApiWithAttrDataImpl implements ClassApiWithAttrData {

        private final AttributeData classe;

        public ClassApiWithAttrDataImpl(AttributeData classe) {
            this.classe = checkNotNull(classe);
        }

        @Override
        public ClassApi then() {
            return ClasseApiImpl.this;
        }

        @Override
        public AttributeData getAttr() {
            return classe;
        }

    }

}
