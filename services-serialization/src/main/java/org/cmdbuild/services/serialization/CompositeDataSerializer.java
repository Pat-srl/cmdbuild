/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.services.serialization;

import org.cmdbuild.utils.lang.CmMapUtils;

import java.util.List;

import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 * Serializes something using:
 * <ol>
 * <li>a single serializer;
 * <li>one or more enhancers.
 * </ol>
 *
 * @param <T>
 * @author afelice
 */
public class CompositeDataSerializer<T> implements DataSerializer<T> {

    private final DataSerializer<T> serializer;
    private final List<DataSerializerEnhancer> enhancers = list();

    /**
     *
     * @param serializer
     */
    public CompositeDataSerializer(DataSerializer<T> serializer) {
        this.serializer = serializer;
    }

    public CompositeDataSerializer addEnhancer(DataSerializerEnhancer enhancer) {
        enhancers.add(enhancer);
        return this;
    }

    @Override
    public CmMapUtils.FluentMap<String, Object> serialize(T data) {
        CmMapUtils.FluentMap<String, Object> serialization = serializer.serialize(data);
        enhancers.forEach(e -> {
            e.enhance(serialization, data);
        });

        return serialization;
    }


}
