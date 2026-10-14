/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.test;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Collections.emptyMap;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import org.cmdbuild.dao.beans.ClassMetadataImpl;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.dao.beans.EntryTypeMetadataImpl;
import org.cmdbuild.dao.beans.FunctionMetadataImpl;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.FormulaType.FT_SCRIPT;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.ClassMetadata;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FORMULA;
import org.cmdbuild.dao.entrytype.attributetype.FormulaAttributeType;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class MetadataBeansTest {

    @Test
    public void testSimpleAttributeMetadata() {
        AttributeMetadataImpl simpleAttributeMetadata = new AttributeMetadataImpl(emptyMap());
    }

    @Test
    public void testSimpleClassMetadata() {
        ClassMetadataImpl simpleClassMetadata = new ClassMetadataImpl(emptyMap());
    }

    @Test
    public void testSimpleDomainMetadata() {
        DomainMetadataImpl simpleDomainMetadata = new DomainMetadataImpl(emptyMap());
    }

    @Test
    public void testSimpleFunctionMetadata() {
        FunctionMetadataImpl simpleFunctionMetadata = new FunctionMetadataImpl(emptyMap());
    }

    @Test
    public void testSimpleEntryTypeMetadata() {
        EntryTypeMetadataImpl simpleEntryTypeMetadata = new EntryTypeMetadataImpl(emptyMap(), emptyMap()) {
        };
    }

    @Test
    public void testVirtualAttributesProcessing() {
        ClassMetadata meta = new ClassMetadataImpl(emptyMap());

        assertTrue(meta.getVirtualAttributes().isEmpty());

        meta = new ClassMetadataImpl(map(
                "cm_virtual_attributes___MyFormula___type", "formula",
                "cm_virtual_attributes___MyFormula___cm_formulaType", "script",
                "cm_virtual_attributes___MyFormula___cm_formulaCode", "MyScript"
        ));

        assertEquals(1, meta.getVirtualAttributes().size());

        AttributeWithoutOwner attribute = getOnlyElement(meta.getVirtualAttributes());

        assertTrue(attribute.isVirtual());
        assertEquals(FORMULA, attribute.getType().getName());
        assertEquals("MyFormula", attribute.getName());
        assertEquals(FT_SCRIPT, attribute.getMetadata().getFormulaType());
        assertEquals("MyScript", attribute.getMetadata().getFormulaCode());
        assertThat(attribute.getType(), instanceOf(FormulaAttributeType.class));
    }

}
