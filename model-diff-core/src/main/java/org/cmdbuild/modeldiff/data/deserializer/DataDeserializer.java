/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.data.deserializer;

/**
 *
 * @author afelice
 * @param <T> serialized data
 * @param <U> data
 * 
 */
public interface DataDeserializer<T, U> {
        
    U deserialize(T serializedData);
}
