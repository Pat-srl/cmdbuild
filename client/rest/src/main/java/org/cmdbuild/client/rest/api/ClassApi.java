/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import java.util.List;
import org.cmdbuild.client.rest.model.AttributeData;
import org.cmdbuild.client.rest.model.AttributeRequestData;
import org.cmdbuild.client.rest.model.ClassData;
import org.cmdbuild.client.rest.model.GeoAttributeData;
import org.cmdbuild.dao.entrytype.ClassDefinition;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;

public interface ClassApi {

    ClassData getById(String classeId);

    String getRawJsonById(String classId);

    String getProcessRawJsonById(String processId);

    List<ClassData> getAll();

    ClassData create(ClassData data);

    ClassData create(ClassDefinition data);

    ClassApiWithClassData update(ClassData data);

    ClassApiWithClassData update(String classId, String rawJsonData);

    ClassApi deleteById(String classeId);

    List<AttributeData> getAttributes(String classId);

    ClassApiWithAttrData createAttr(String classId, AttributeRequestData data);

    ClassApiWithAttrData updateAttr(String classId, AttributeRequestData data);

    List<GeoAttributeData> getGeoAttributes(String classId);

    GeoAttributeData getGeoAttribute(String classId, Object geoAttributeId);

    GeoAttributeData createGeoAttr(String classId, GeoAttributeData data);

    boolean deleteGeoAttr(String classId, Object geoAttributeId);

    ClassApi deleteAttr(String classId, String attrId);

    AttributeData getAttr(String classId, String attrId);

    default ClassData create(String classId) {
        return create(ClassDefinitionImpl.build(classId));
    }

    interface ClassApiWithClassData {

        ClassApi then();

        ClassData getClasse();
    }

    interface ClassApiWithAttrData {

        ClassApi then();

        AttributeData getAttr();
    }
}
