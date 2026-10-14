/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.ecql.utils;

import static java.util.Arrays.asList;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwnerImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainImpl;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.ecql.EcqlId;
import org.cmdbuild.ecql.EcqlSource;
import static org.cmdbuild.report.ReportConst.DUMMY_REPORT_PARAM_OWNER_CODE;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.utils.lang.CmNullableUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author afelice
 */
public class EcqlUtilsTest {
    
    private static final String A_DOMAIN_NAME = "FloorRoom";
    private static final String A_TARGET_DOMAIN_CLASS = "Room";
    private static final String A_SOURCE_DOMAIN_CLASS = "Floor";

    // EcqlId{source=DOMAIN, id=[FloorRoom, sourceFilter]}
    private final static String EXP_GENERATED_ROOM_DOMAIN_FILTER_ECQL_ID = "e98o52dg4fam1dy6cp0t83nwafyptxpcr8lg0z4h6vr23";

    private final static String EXP_GENERATED_CLASS_ATTRIBUTE_FILTER_ECQL_ID = "v1qdfbmqmd2nevspoosxe7ssoa8olfsnxo9tn";

    private final static String EXP_GENERATED_REPORT_ATTRIBUTE_FILTER_ECQL_ID = "11ynclsyckrruu9oe2axww7yqx2bxfy4xi5p6j";

    private final static String A_KNOWN_LOOKUP_ATTRIBUTE_NAME = "MyLookupAttr";
    private final static String A_KNOWN_LOOKUP_NAME = "MyLookup";
    private final static String A_KNOWN_DOMAIN_ATTRIBUTE_FILTER = "from LookUp where Id in (/(select \"Id\" from \"LookUp\" where \"Code\" in {0{client:wantedValues}})/)";
    // EcqlId{source=DOMAIN_ATTRUIBUTE, id=[FloorRoom,MyLookupAttr]}
    private final static String EXP_GENERATED_DOMAIN_ATTRIBUTE_FILTER_ECQL_ID = "bqp0gdu06fhjw3tyjxyndgqr6h7z4efodwclp69ynqmrf";

    @Test
    public void testResolveEcqlXa1() {
        String ecql1 = "from Hardware where Id in (/(select \"IdObj2\" from \"Map_AssetMgtCI\" where \"IdObj1\"={xa:process} and \"Status\"='A')/";
        String ecql2 = EcqlUtils.resolveEcqlXa(ecql1, map("process", 12345678l));
        assertEquals("from Hardware where Id in (/(select \"IdObj2\" from \"Map_AssetMgtCI\" where \"IdObj1\"=12345678 and \"Status\"='A')/", ecql2);
    }

    @Test
    public void testResolveEcqlXa2() {
        String ecql1 = "from Hardware where Id in (/(select \"IdObj2\" from \"Map_AssetMgtCI\" where \"IdObj1\"='{xa:process}' and \"Status\"='A')/";
        String ecql2 = EcqlUtils.resolveEcqlXa(ecql1, map("process", "test"));
        assertEquals("from Hardware where Id in (/(select \"IdObj2\" from \"Map_AssetMgtCI\" where \"IdObj1\"='test' and \"Status\"='A')/", ecql2);
    }    
    
    @Test
    public void testBuildDomainEcqlId() {
        System.out.println("buildDomainEcqlId");

        //arrange:
        Classe room = createClasse(A_TARGET_DOMAIN_CLASS);
        Classe floor = createClasse(A_SOURCE_DOMAIN_CLASS);
        Domain floorRoomDomain = DomainImpl.builder()
                .withName(A_DOMAIN_NAME)
                .withClass1(floor) // source
                .withClass2(room) // target
                .build();

        //act:
        String encodedId = EcqlUtils.buildDomainEcqlId(floorRoomDomain, Domain.DOMAIN_SOURCE_FILTER_SIDE);
        //assert:
        assertEquals(EXP_GENERATED_ROOM_DOMAIN_FILTER_ECQL_ID, encodedId);
    }

    @Test
    public void testParseDomainEcqlId() {
        System.out.println("parseDomainEcqlId");

        //act:
        EcqlId ecqlId = EcqlUtils.parseEcqlId(EXP_GENERATED_ROOM_DOMAIN_FILTER_ECQL_ID);

        //assert:
        assertEquals(EcqlSource.DOMAIN, ecqlId.getSource());
        assertEquals(asList(A_DOMAIN_NAME, Domain.DOMAIN_SOURCE_FILTER_SIDE), ecqlId.getId());
    }

    @Test
    public void testBuildClassAttributeEcqlId() {
        System.out.println("buildClassAttributeEcqlId");

        //arrange:
        Classe room = createClasse(A_TARGET_DOMAIN_CLASS);
        Attribute aClassAttribute = buildClassAttribute_Lookup(A_KNOWN_LOOKUP_ATTRIBUTE_NAME, room, A_KNOWN_LOOKUP_NAME);
        Classe floor = createClasse(A_SOURCE_DOMAIN_CLASS);
        Domain floorRoomDomain = DomainImpl.builder()
                .withName(A_DOMAIN_NAME)
                .withClass1(floor) // source
                .withClass2(room) // target
                .build();

        //act:
        String encodedId = EcqlUtils.buildAttrEcqlId(aClassAttribute);

        //assert:
        assertEquals(EXP_GENERATED_CLASS_ATTRIBUTE_FILTER_ECQL_ID, encodedId);
    }

    @Test
    public void testBuildReportAttributeEcqlId() {
        System.out.println("buildReportAttributeEcqlId");

        //arrange:
        Classe room = createClasse(DUMMY_REPORT_PARAM_OWNER_CODE);
        Attribute aClassAttribute = buildClassAttribute("aReportAttr", room, new StringAttributeType());
        Classe floor = createClasse(A_SOURCE_DOMAIN_CLASS);
        Domain floorRoomDomain = DomainImpl.builder()
                .withName(A_DOMAIN_NAME)
                .withClass1(floor) // source
                .withClass2(room) // target
                .build();

        //act:
        String encodedId = EcqlUtils.buildAttrEcqlId(aClassAttribute);

        //assert:
        assertEquals(EXP_GENERATED_REPORT_ATTRIBUTE_FILTER_ECQL_ID, encodedId);
    }

    @Test
    public void testBuildDomainAttributeEcqlId_Lookup() {
        System.out.println("buildDomainAttributeEcqlId_Lookup");

        //arrange:
        Classe room = createClasse(A_TARGET_DOMAIN_CLASS);
        Classe floor = createClasse(A_SOURCE_DOMAIN_CLASS);
        Domain floorRoomDomain = DomainImpl.builder()
                .withId(12345L)
                .withName(A_DOMAIN_NAME)
                .withClass1(floor) // source
                .withClass2(room) // target
                .withAttribute(
                        buildDomainAttribute_Lookup(A_KNOWN_LOOKUP_ATTRIBUTE_NAME, A_KNOWN_LOOKUP_NAME)
                )
                .build();

        //act:
        String encodedId = EcqlUtils.buildAttrEcqlId(floorRoomDomain.getAttribute(A_KNOWN_LOOKUP_ATTRIBUTE_NAME));

        //assert:
        assertEquals(EXP_GENERATED_DOMAIN_ATTRIBUTE_FILTER_ECQL_ID, encodedId);
    }

    @Test
    public void testBuildDomainAttributeEcqlId_Lookup_FilterInAttribute() {
        System.out.println("buildDomainAttributeEcqlId_Lookup_FilterInAttribute");

        //arrange:
        Classe room = createClasse(A_TARGET_DOMAIN_CLASS);
        Classe floor = createClasse(A_SOURCE_DOMAIN_CLASS);
        Domain floorRoomDomain = DomainImpl.builder()
                .withId(12345L)
                .withName(A_DOMAIN_NAME)
                .withClass1(floor) // source
                .withClass2(room) // target
                .withAttribute(
                        buildDomainAttribute_Lookup(A_KNOWN_LOOKUP_ATTRIBUTE_NAME, A_KNOWN_LOOKUP_NAME)
                )
                .build();

        //act:
        String encodedId = EcqlUtils.buildAttrEcqlId(floorRoomDomain.getAttribute(A_KNOWN_LOOKUP_ATTRIBUTE_NAME));

        //assert:
        assertEquals(EXP_GENERATED_DOMAIN_ATTRIBUTE_FILTER_ECQL_ID, encodedId);
    }

    @Test
    public void testParseDomainAttributeEcqlId() {
        System.out.println("parseDomainAttributeEcqlId");

        //act:
        EcqlId ecqlId = EcqlUtils.parseEcqlId(EXP_GENERATED_DOMAIN_ATTRIBUTE_FILTER_ECQL_ID);

        //assert:
        assertEquals(EcqlSource.DOMAIN_ATTRIBUTE, ecqlId.getSource());
        assertEquals(asList(A_DOMAIN_NAME, A_KNOWN_LOOKUP_ATTRIBUTE_NAME), ecqlId.getId());
    }

    private Classe createClasse(String classeName) {
        return ClasseImpl.builder()
                .withName(classeName)
                .withId(12345L) // Without this the attribute related filter is categorized as EMBEDDED instead of CLASS_ATTRIBUTE
                .build();
    }    

    private AttributeWithoutOwner buildDomainAttribute_Lookup(String attribName, String lookupName) {
        return buildDomainAttribute_Lookup(attribName, lookupName, null);
    }

    private AttributeWithoutOwner buildDomainAttribute_Lookup(String attribName, String lookupName, String filter) {
        AttributeWithoutOwnerImpl.AttributeWithoutOwnerImplBuilder builder = AttributeWithoutOwnerImpl.builder().withName(attribName).withType(new LookupAttributeType(lookupName));
        if (CmNullableUtils.isNotBlank(filter)) {
            builder.withMeta(AttributeMetadataImpl.builder().withFilter(filter).build());
        }

        return builder.build();
    }

    private Attribute buildClassAttribute_Lookup(String attribName, Classe classe, String lookupName) {
        return buildClassAttribute(attribName, classe, new LookupAttributeType(lookupName));
    }

    private Attribute buildClassAttribute(String attribName, Classe classe, CardAttributeType type) {
        return AttributeImpl.builder().withName(attribName).withOwner(classe).withType(type).build();
    }
    
}
