/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.dao.beans;

import static java.lang.String.format;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwnerImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainImpl.DomainImplBuilder;
import static org.cmdbuild.dao.entrytype.DomainMetadata.IN_CLASS_REFERENCE_FILTERS;
import static org.cmdbuild.dao.entrytype.DomainMetadata.IN_CLASS_REFERENCE_FILTERS_LEGACY;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author afelice
 */
public class DomainImplTest {

    private final static String FLOOR_FILTER = "from Floor where Id in (/(select \"Id\" from \"Floor\" where \"Building\" = 0{client:Building.Id})/)";

    private final static String A_KNOWN_DOMAIN_LOOKUP_ATTRIBUTE_NAME = "MyLookupAttr";
    private final static String A_KNOWN_LOOKUP_NAME = "MyLookup";

    private final static String A_KNOWN_DOMAIN_ATTRIBUTE_FILTER = "from LookUp where Id in (/(select \"Id\" from \"LookUp\" where \"Code\" in {0{client:wantedValues}})/)";

    /**
     * Test of withMetadata method, for domain filter on class, of class
     * DomainImpl.
     */
    @Test
    public void testWithMetadata_domainClassReferenceFilters() {
        System.out.println("withMetadata_domainClassReferenceFilters");

        //arrange:
        Classe floor = mockBuildClass("Floor");
        Classe room = mockBuildClass("Room");

        //act:
        Domain instance = new DomainImplBuilder()
                .withName("FloorRoom")
                .withId(1L)
                .withClass1(floor) // source
                .withClass2(room) // target
                .withMetadata(DomainMetadataImpl.builder()
                        .withSourceFilter(FLOOR_FILTER)
                        .build())
                .build();

        //assert:
        assertEquals(FLOOR_FILTER, instance.getSourceFilter());
    }

    /**
     * Issue
     * <a href="http://gitlab.example.com/cmdbuild/cmdbuild/-/issues/7819">#7819
     * -- Error on domain with filter creation</a>: supportare ancora il nome
     * legacy "cm_class_reference_filters" nel metadata del Domain, per i
     * trigger usati da OpenMAINT.
     */
    @Test
    public void testWithMetadata_classAttributeReferenceFilters_legacyMetadataName_7819() {
        System.out.println("withMetadata_classAttributeReferenceFilters_legacyMetadataName_7819");

        //arrange:
        String aKnownSourceCqlFilter = "\"from Employee where Id in (/( SELECT \\\"Id\\\" FROM \\\"Employee\\\" WHERE \\\"State\\\" = _cm3_lookup(''Employee - State'', ''Active'') AND \\\"Status\\\" = ''A'')/)\"";
        String aKnownSourceCqlFilter_Unescaped = "from Employee where Id in (/( SELECT \"Id\" FROM \"Employee\" WHERE \"State\" = _cm3_lookup(''Employee - State'', ''Active'') AND \"Status\" = ''A'')/)";
        String aKnownTargetCqlFilter = "\"from Office where Id in (/( SELECT \\\"Id\\\" FROM \\\"Office\\\" WHERE \\\"State\\\" = _cm3_lookup(''Office - State'', ''Active'') AND \\\"Status\\\" = ''A'')/)\"";
        String aKnownTargetCqlFilter_Unescaped = "from Office where Id in (/( SELECT \"Id\" FROM \"Office\" WHERE \"State\" = _cm3_lookup(''Office - State'', ''Active'') AND \"Status\" = ''A'')/)";
        String aKnownStoredCqlFilter = format("{\"sourceFilter\":%s,\"targetFilter\":%s}", aKnownSourceCqlFilter, aKnownTargetCqlFilter);
        Classe floor = mockBuildClass("Floor");
        Classe room = mockBuildClass("Room");

        //act:
        Domain instance1 = new DomainImplBuilder()
                .withName("FloorRoom")
                .withId(1L)
                .withClass1(floor) // source
                .withClass2(room) // target
                .withAttribute(buildDomainAttribute(A_KNOWN_DOMAIN_LOOKUP_ATTRIBUTE_NAME, A_KNOWN_LOOKUP_NAME))
                .withMetadata(new DomainMetadataImpl(map(IN_CLASS_REFERENCE_FILTERS_LEGACY, aKnownStoredCqlFilter)))
                .build();

        //assert:
        assertEquals(aKnownSourceCqlFilter_Unescaped, instance1.getSourceFilter());
        assertEquals(aKnownTargetCqlFilter_Unescaped, instance1.getTargetFilter());

        Domain instance2 = new DomainImplBuilder()
                .withName("FloorRoom")
                .withId(2L)
                .withClass1(floor) // source
                .withClass2(room) // target
                .withAttribute(buildDomainAttribute(A_KNOWN_DOMAIN_LOOKUP_ATTRIBUTE_NAME, A_KNOWN_LOOKUP_NAME))
                .withMetadata(new DomainMetadataImpl(map(IN_CLASS_REFERENCE_FILTERS, aKnownStoredCqlFilter)))
                .build();

        //assert:
        assertEquals(aKnownSourceCqlFilter_Unescaped, instance2.getSourceFilter());
        assertEquals(aKnownTargetCqlFilter_Unescaped, instance2.getTargetFilter());
    }

    private static ClasseImpl mockBuildClass(String classeName) {
        return ClasseImpl.builder()
                .withName(classeName)
                .build();
    }

    private AttributeWithoutOwner buildDomainAttribute(String attribName, String lookupName) {
        return AttributeWithoutOwnerImpl.builder().withName(attribName).withType(new LookupAttributeType(lookupName)).build();
    }

}
