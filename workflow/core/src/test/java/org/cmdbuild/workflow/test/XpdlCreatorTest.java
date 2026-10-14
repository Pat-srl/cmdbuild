/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.workflow.test;

import java.io.IOException;
import static java.lang.String.format;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.cmdbuild.dao.beans.ClassMetadataImpl.ClassMetadataImplBuilder;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import static org.cmdbuild.dao.entrytype.ClassMetadata.CLASS_SPECIALITY;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.ProcessImpl;
import org.cmdbuild.workflow.xpdl.XpdlDocumentHelper;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author ataboga
 */
public class XpdlCreatorTest {

    private static final String A_EXP_XPDL_TEMPLATE_FILENAME = "IncidentMgt_template.xpdl";

    @Test
    public void testCreateXpdl() throws IOException {
        System.out.println("createXpdl");

        //arrange:
        Classe incidentMgt = mockBuildProcessClasse("IncidentMgt", 100L);
        Attribute code = mockBuildAttribute(incidentMgt, "Code", new StringAttributeType());
        Attribute description = mockBuildAttribute(incidentMgt, "Description", new StringAttributeType());
        Attribute flowStatus = mockBuildAttribute(incidentMgt, "FlowStatus", new LookupAttributeType("LookupType"));
        Attribute creationTimestamp = mockBuildAttribute(incidentMgt, "CreationTimestamp", new DateTimeAttributeType());
        incidentMgt = mockAddAttributes(incidentMgt, code, description, flowStatus, creationTimestamp);
        Process process = mockBuildProcess(incidentMgt);
        String expResult = loadTestFile(A_EXP_XPDL_TEMPLATE_FILENAME);

        //act:
        XpdlDocumentHelper result = new XpdlDocumentHelper().createXpdlTemplateForProcess(process, list("SuperUser", "Guest", "Connectors"));

        //assert:
        assertEquals(expResult, new String(result.xpdlByteArray(), StandardCharsets.UTF_8));
    }

    private Classe mockBuildProcessClasse(String processName, long processId) {
        return ClasseImpl.builder()
                .withId(processId)
                .withName(processName)
                .withMetadata(
                        new ClassMetadataImplBuilder(map(CLASS_SPECIALITY, "process")).build()
                )
                .build();
    }

    private Classe mockAddAttributes(Classe classe, Attribute... attributes) {
        return ClasseImpl.copyOf(classe).withAttributes(list(attributes)).build();
    }

    private Attribute mockBuildAttribute(Classe owner, String attributeName, CardAttributeType attributeType) {
        return AttributeImpl.builder()
                .withOwner(owner)
                .withName(attributeName)
                .withType(attributeType)
                .build();
    }

    private Process mockBuildProcess(Classe innerClasse) {
        return ProcessImpl.builder().withInner(innerClasse).build();
    }

    private String loadTestFile(String testResourceFilename) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream(format("/%s", testResourceFilename)), Charset.defaultCharset());
    }
}
