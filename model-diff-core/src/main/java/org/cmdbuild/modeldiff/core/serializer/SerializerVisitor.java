/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.core.serializer;

import java.util.List;
import java.util.Map;

/**
 * Visitor, specific serialization for each of the supported type of value.
 *
 * @author afelice
 */
public interface SerializerVisitor {

    void visitNull(String fieldName);

    void visit(String fieldName, Number value);

    void visit(String fieldName, Boolean value);

    void visit(String fieldName, String value);

    void visit(String fieldName, Enum<?> value);

    void visit(String fieldName, Map<String, Object> value);

    void visit(String fieldName, List value);

    SerializerVisitableFactory getVisitableFactory();
} // end SerializerVisitor interface
