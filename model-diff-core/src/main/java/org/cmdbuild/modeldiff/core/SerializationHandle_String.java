/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.core;

/**
 * Plain JSON string (using a {@link StringWriter}).
 *
 * @author afelice
 */
public class SerializationHandle_String implements SerializationHandle {

    private final String serialization;

    public SerializationHandle_String(String serialization) {
        this.serialization = serialization;
    }

    @Override
    public String getSerializationInfo() {
        return serialization;
    }
}
