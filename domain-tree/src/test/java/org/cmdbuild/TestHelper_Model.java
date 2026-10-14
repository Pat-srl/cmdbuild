/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.ClassMetadataImpl;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.dao.beans.RelationDirection;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwnerImpl;
import org.cmdbuild.dao.entrytype.ClassMetadata;
import static org.cmdbuild.dao.entrytype.ClassMetadata.CLASS_SPECIALITY;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainCardinality;
import org.cmdbuild.dao.entrytype.DomainImpl;
import org.cmdbuild.dao.entrytype.attributetype.LongAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.model.ProcessImpl;
import static org.junit.Assert.assertTrue;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author afelice
 */
public class TestHelper_Model {

    /**
     * Create a <code>standard</code> {@link Classe}.
     *
     * @param classeName
     * @return
     */
    public static Classe mockBuildClasse(String classeName) {
        final List<String> ancestors = emptyList();
        final List<AttributeWithoutOwner> attributes = emptyList();
        return mockBuildClasse(classeName, ancestors, attributes);
    }

    /**
     * Create a <code>standard</code> {@link Classe}, with given attributes.
     *
     * @param classeName
     * @param attributes
     * @return
     */
    public static Classe mockBuildClasse(String classeName, List<AttributeWithoutOwner> attributes) {
        final List<String> ancestors = emptyList();
        return mockBuildClasse(classeName, ancestors, attributes);
    }

    /**
     * Create a Subclass, that is a  <code>standard</code> {@link Classe} with
     * ancestor(s).
     *
     * @param classeName
     * @param ancestors
     * @param attributes attributes of <code>Card</code>s are derived from this
     * ones.
     *
     * @return
     */
    public static Classe mockBuildClasse(String classeName, List<String> ancestors, List<AttributeWithoutOwner> attributes) {
        ClasseImpl.ClasseBuilder builder = ClasseImpl.builder()
                .withName(classeName)
                .withId(UniqueTestIdUtils.id()) // Without this the attribute related filter is categorized as EMBEDDED instead of CLASS_ATTRIBUTE
                .withAttributes(attributes);

        if (ancestors != null) {
            builder.withAncestors(ancestors);
        }

//        builder.withMetadata(new ClassMetadataImpl(metadataSerialization));

        return builder.build();
    }

    /**
     * Attribute with type String
     *
     * @param attributeName
     * @return
     */
    public static AttributeWithoutOwner mockBuildAttributeWithoutOwner(final String attributeName) {
        return AttributeWithoutOwnerImpl.builder()
                .withName(attributeName)
                .withType(new StringAttributeType())
                .build();
    }

    /**
     * Attribute Id (card id, serialized as <code>"_id"</code> later on)
     *
     * @return
     */
    public static AttributeWithoutOwner mockBuildAttributeId() {
        return AttributeWithoutOwnerImpl.builder()
                .withName(ATTR_ID)
                .withType(LongAttributeType.INSTANCE)
                .build();
    }

    /**
     * Attribute <code>IdClasse</code> (card type, serialized as
     * <code>"_type"</code> later on)
     *
     * @return
     */
    public static AttributeWithoutOwner mockBuildAttributeIdClasse() {
        return mockBuildAttributeWithoutOwner(ATTR_IDCLASS);
    }

// ** DOMAIN **
    /**
     * Builds domain <code>N:1</code>.
     *
     * @param domainName
     * @param mockUniqueId
     * @param sourceClass
     * @param targetClass
     * @return
     */
    public static Domain mockBuildDomain_N1(String domainName, Long mockUniqueId, Classe sourceClass, Classe targetClass) {
        // Create domain RFCChangeManagertestFetchEcql
        // "_id": "RFCChangeManager",
        // "name": "RFCChangeManager",
        // "description": "RFCChangeManager",
        // "source": "RequestForChange1",
        // "sources": [
        //    "RequestForChange1"
        // ],
        // "sourceProcess": true,
        // "destination": "Employee1",
        // "destinations": [
        //   "Employee1"
        // ],
        // "cardinality": "N:1",
        DomainImpl.DomainImplBuilder domainBuilder = DomainImpl.builder()
                .withName(domainName)
                .withId(mockUniqueId)
                .withClass1(sourceClass) // RequestForChange1
                .withClass2(targetClass); // Employee1
        domainBuilder.withMetadata(DomainMetadataImpl.builder()
                .withCardinality(DomainCardinality.MANY_TO_ONE)
                .withIsActive(true)
                .build()
        );

        Domain directDomain = domainBuilder.build();
        return directDomain;
    }

    /**
     * Builds domain <code>1:N</code>.
     *
     * @param domainName
     * @param mockUniqueId
     * @param sourceClass
     * @param targetClass
     * @return
     */
    public static Domain mockBuildDomain_1N(String domainName, Long mockUniqueId, Classe sourceClass, Classe targetClass) {
        DomainImpl.DomainImplBuilder domainBuilder = DomainImpl.builder()
                .withName(domainName)
                .withId(mockUniqueId)
                .withClass1(sourceClass) // RequestForChange1
                .withClass2(targetClass); // Employee1
        domainBuilder.withMetadata(DomainMetadataImpl.builder()
                .withCardinality(DomainCardinality.ONE_TO_MANY)
                .withIsActive(true)
                .build()
        );

        Domain directDomain = domainBuilder.build();
        return directDomain;
    }

    /**
     * Builds domain <code>N:N</code>.
     *
     * @param domainName
     * @param mockUniqueId
     * @param sourceClass
     * @param targetClass
     * @return
     */
    public static Domain mockBuildDomain_NN(String domainName, Long mockUniqueId, Classe sourceClass, Classe targetClass) {
        DomainImpl.DomainImplBuilder domainBuilder = DomainImpl.builder()
                .withName(domainName)
                .withId(mockUniqueId)
                .withClass1(sourceClass) // RequestForChange1
                .withClass2(targetClass); // Employee1
        domainBuilder.withMetadata(DomainMetadataImpl.builder()
                .withCardinality(DomainCardinality.MANY_TO_MANY)
                .withIsActive(true)
                .build()
        );

        Domain directDomain = domainBuilder.build();
        return directDomain;
    }

        /**
     * Builds domain <code>1:1</code>.
     *
     * @param domainName
     * @param mockUniqueId
     * @param sourceClass
     * @param targetClass
     * @return
     */
    public static Domain mockBuildDomain_11(String domainName, Long mockUniqueId, Classe sourceClass, Classe targetClass) {
        DomainImpl.DomainImplBuilder domainBuilder = DomainImpl.builder()
                .withName(domainName)
                .withId(mockUniqueId)
                .withClass1(sourceClass) // RequestForChange1
                .withClass2(targetClass); // Employee1
        domainBuilder.withMetadata(DomainMetadataImpl.builder()
                .withCardinality(DomainCardinality.ONE_TO_ONE)
                .withIsActive(true)
                .build()
        );

        Domain directDomain = domainBuilder.build();
        return directDomain;
    }

    /**
     * Attribute with type Reference, to relate Cards.
     *
     * @param attributeName
     * @param domainName
     * @param direction
     * @return
     */
    public static AttributeWithoutOwner mockBuildAttributeReference(final String attributeName,
            final String domainName, final RelationDirection direction) {
        return AttributeWithoutOwnerImpl.builder()
                .withName(attributeName)
                .withType(new ReferenceAttributeType(domainName, direction))
                .build();
    }

    public static Attribute mockBuildAttributeInverse(final String attributeName, Domain floorRoomDomain) {
        // Create Floor reference
        // "classId": "Room1"
        // "domain": "FloorRoom1"
        // "targetClass": "Floor1"
        // "direction": "inverse"
        // "name": "Floor",
        // "type": "reference"
        Classe room = floorRoomDomain.getTargetClass();

        return AttributeImpl.builder()
                .withOwner(room)
                .withName(attributeName)
                .withType(new ReferenceAttributeType(floorRoomDomain, RelationDirection.RD_INVERSE))
                .build();
    }

    public static Domain mockBuildDomainInverse(String domainName, Long mockUniqueId, Classe sourceClass, Classe targetClass) {
        // Create domain FloorRoom1
        // "_id": "FloorRoom1",
        // "name": "FloorRoom1",
        // "description": "Floor room",
        // "source": "Floor1",
        // "sources": [
        //     "Floor1"
        // ],
        // "destination": "Room1",
        // "destinations": [
        //     "Room1"
        // ],
        DomainImpl.DomainImplBuilder domainBuilder = DomainImpl.builder()
                .withName(domainName + mockUniqueId)
                .withClass1(sourceClass) // Floor
                .withClass2(targetClass); // Room

        domainBuilder.withMetadata(DomainMetadataImpl.builder()
                .withCardinality(DomainCardinality.ONE_TO_MANY)
                .withIsActive(true)
                .build()
        );
        Domain domain = domainBuilder.build();

        return domain;
    }

    // ** PROCESS **
    /**
     *
     * <b>Note</b>: a <i>base default dms category</i> for {@link Process} will
     * result and so will be written in <i>Model</i> json output.
     *
     * @param name
     * @param innerClasse <i>classe</i> with attributes that will be inherited
     * by the process. Must be with <i>class speciality</i> as
     * <code>PROCESS</code> (in its metadata).
     * @return
     */
    public static org.cmdbuild.workflow.model.Process mockBuildProcess(String name, Classe innerClasse) {
        if (innerClasse.hasDmsCategory()) {
            // Can't add the Dms catebory to the process metadata, even if
            // a Process inherits from a Class. So Dms Category can't be set
            // in Process model object, apparently. An bug in existing code?
            // So a <i>base default dms category</i> will result and be written in
            // <i>Model</i> json output.
        }

        return ProcessImpl.builder()
                .withInner(innerClasse)
                .build();
    }

    /**
     * Create a inner <i>classe</i> for a {@link Process}, that is a
     * {@link Classe} with <code>PROCESS</code> <i>speciality</i>.
     *
     * @param classeName
     * @param processAttributes attributes of <code>Flow</code>s are derived
     * from this ones.
     *
     * @return
     */
    public static Classe mockBuildClasse_Process(String classeName, List<AttributeWithoutOwner> processAttributes) {
        CmMapUtils.FluentMap<String, String> metadata = map(CLASS_SPECIALITY, serializeEnum(ClassMetadata.ClassSpeciality.CS_PROCESS));

        return ClasseImpl.builder()
                .withName(classeName)
                .withId(UniqueTestIdUtils.id()) // Without this the attribute related filter is categorized as EMBEDDED instead of CLASS_ATTRIBUTE
                .withAttributes(processAttributes)
                .withMetadata(new ClassMetadataImpl(metadata)) // Without this, the ProcessImpl constructor will complain
                .build();
    }

// ** CARD **
    /**
     * Generate a card.
     *
     * @param cardId
     * @param classe
     * @param attributes
     * @return
     */
    public static Card mockBuildCard(Long cardId, Classe classe, Map<String, Object> attributes) {
        return CardImpl.builder()
                .withId(cardId).withType(classe)
                .withAttributes(attributes)
                .build();
    }

// ** UTILS **
    /**
     * If a value differs for a key in the two input maps, in the identifying
     * message for the {@link AssertionError} there will be a list of expResult
     * value (<i>left value</i>) and result value (<i>right value</i>).
     *
     *
     * @param expResult
     * @param result
     */
    public static void checkEquals_Map(Map<String, Object> expResult, Map<String, Object> result) {
        MapDifference<String, Object> mapDifference = Maps.difference(expResult, result);
        Map<String, Object> missing = mapDifference.entriesOnlyOnLeft();
        assertTrue(format("Expected but missing: %s", missing), missing.isEmpty());
        Map<String, Object> unexpected = mapDifference.entriesOnlyOnRight();
        assertTrue(format("Actual has unexpected: %s", unexpected), unexpected.isEmpty());
        Map<String, MapDifference.ValueDifference<Object>> differing = mapDifference.entriesDiffering();
        assertTrue(format("Expected and actual differs in: %s", differing),
                differing.isEmpty());
    }

    /**
     * Builds a generics value <code>Answer&lt;X&lt;Y&gt;&gt;</code> that
     * returns given value
     *
     * @param value
     * @return
     */
    public static <T> Answer<T> setupDummyAnswer(T value) {
        return (InvocationOnMock invocation) -> value;
    }

}
