/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.core.serializer;

/**
 * Creates a <i>visitable</i> for a supported value.
 *
 * @author afelice
 */
public interface SerializerVisitableFactory {

    /**
     * From raw value type to <i>Visitable</i>.
     *
     * @param fieldName null if no enwrapping field name, for example for
     * compound values (map item or list item);
     * @param value
     * @return
     */
    SerializerVisitable create(String fieldName, Object value);
}
