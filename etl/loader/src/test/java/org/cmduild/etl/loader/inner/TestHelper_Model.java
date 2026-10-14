package org.cmduild.etl.loader.inner;

import static java.util.Collections.emptyList;
import java.util.List;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainCardinality;
import org.cmdbuild.dao.entrytype.DomainImpl;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import static org.junit.Assert.fail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author afelice
 */
public class TestHelper_Model {

    /**
     * Create a simple class
     *
     * @param classeName
     * @return
     */
    public static Classe mockBuildClasse(String classeName) {
        final List<String> ancestors = emptyList();
        final List<Attribute> attributes = emptyList();
        return mockBuildClasse(classeName, ancestors, attributes);
    }

    /**
     * Create a simple class
     *
     * @param classeName
     * @param attributes
     * @return
     */
    public static Classe mockBuildClasse(String classeName, List<Attribute> attributes) {
        final List<String> ancestors = emptyList();
        return mockBuildClasse(classeName, ancestors, attributes);
    }

    /**
     * Create a Subclass
     *
     * @param classeName
     * @param ancestors
     * @param attributes attributes of <code>Card</code>s are derived from this
     * ones.
     * @return
     */
    public static Classe mockBuildClasse(String classeName, List<String> ancestors, List<Attribute> attributes) {
        ClasseImpl.ClasseBuilder builder = ClasseImpl.builder()
                .withName(classeName)
                .withId(Long.valueOf(UniqueTestIdUtils.tuid())) // Without this the attribute related filter is categorized as EMBEDDED instead of CLASS_ATTRIBUTE
                .withAttributes(attributes);

        if (!ancestors.isEmpty()) {
            builder.withAncestors(ancestors);
        }

        return builder.build();
    }

//    /**
//     * Generates a direct domain source -> target.
//     *
//     * Handles uniqueness of instances of {@link Class} and {@link Domain} to be
//     * used with real {@link DaoService} using the given
//     * <code>mockUniqueId</code>.
//     *
//     * @param sourceClassName
//     * @param targetClassName
//     * @param domainName
//     * @param mockUniqueId
//     * @return sourceClasse, targetClasse, Domain
//     */
//    private Triple<Classe, Classe, Domain> mockBuildDomainDirect(String sourceClassName, String targetClassName,
//            String domainName,
//            int mockUniqueId) {
//        return mockBuildDomainDirect(sourceClassName, targetClassName, domainName, emptyMap(), mockUniqueId);
//    }
//    /**
//     * Generates a direct domain source -> target.
//     *
//     * Handles uniqueness of instances of {@link Class} and {@link Domain} to be
//     * used with real {@link DaoService} using the given
//     * <code>mockUniqueId</code>.
//     *
//     * @param sourceClassName
//     * @param targetClassName
//     * @param domainName
//     * @param classReferenceFilters
//     * @param mockUniqueId
//     * @return
//     */
//    private Triple<Classe, Classe, Domain> mockBuildDomainDirect(String sourceClassName, String targetClassName,
//            String domainName, int mockUniqueId) {
//        Classe sourceClass = createClasse(sourceClassName + mockUniqueId),
//                targetClass = createClasse(targetClassName + mockUniqueId);
//
//        Domain directDomain = mockBuildDomainDirect(domainName, mockUniqueId, sourceClass, targetClass);
//
//        return Triple.of(sourceClass, targetClass, directDomain);
//    } 
    public static Domain mockBuildDomainDirect(String domainName, String mockUniqueId, Classe sourceClass, Classe targetClass) {
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
                .withName(domainName + mockUniqueId)
                .withClass1(sourceClass) // RequestForChange1
                .withClass2(targetClass); // Employee1
        domainBuilder.withMetadata(b -> b.withCardinality(DomainCardinality.MANY_TO_ONE));

        Domain directDomain = domainBuilder.build();
        return directDomain;
    }

    /**
     * Attribute with type String
     *
     * @param attributeName
     * @param ownerClass
     * @return
     */
    public static Attribute mockBuildAttribute(final String attributeName, final Classe ownerClass) {
        return AttributeImpl.builder()
                .withOwner(ownerClass)
                .withName(attributeName)
                .withType(new StringAttributeType())
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

    public static Domain mockBuildDomainInverse(Classe sourceClass, Classe targetClass,
            String domainName, String mockUniqueId) {
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

        domainBuilder.withMetadata(b -> b.withCardinality(DomainCardinality.ONE_TO_MANY));
        Domain domain = domainBuilder.build();

        return domain;
    }

    public static void overrideType_Relation(Attribute attribute, Domain domain, final RelationDirection relationDirection) {
        setFieldValue(attribute, "type", new ReferenceAttributeType(domain, relationDirection));
    }

    /**
     * Overwrite private field (through Reflection)
     *
     * @param <T> type of field value
     * @param obj object to apply overwriting of field value on.
     * @param fieldName name of field in object.
     * @param value the new value to assign to the field.
     */
    public static <T> void setFieldValue(Object obj, String fieldName, T value) {

        // Obtain class
        Class<?> theObjClass = obj.getClass();

        java.lang.reflect.Field privateField = null;
        try {
            // get private field by name
            privateField = theObjClass.getDeclaredField(fieldName);

            // set field as accessible
            privateField.setAccessible(true);

            privateField.set(obj, value);
        } catch (IllegalAccessException | NoSuchFieldException exc) {
            fail("test - error accessing field =< %s.%s >, %s".formatted(theObjClass.getName(), fieldName, exc));
        } finally {
            // Restore accessibility
            if (privateField != null) {
                privateField.setAccessible(false);
            }
        }
    } // end setFieldValue method    

} // end TestHelper_Model class

/**
 * Duplicated here from module cmdbuild-test-framework to not import all that
 * module only to use this class.
 *
 * @author afelice
 */
class UniqueTestIdUtils {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static int i = 0;

    public static void prepareTuid() {
        i++;
    }

    /**
     * @return test unique id (to prefix names and stuff)
     */
    public static String tuid() {
        return Integer.toString(i, 32);
    }

    /**
     * @param id
     * @return param + test unique id
     */
    public static String tuid(String id) {
        return id + tuid();//StringUtils.capitalizeFirstLetter(tuid());
    }

} // end UniqueTestIdUtils class
