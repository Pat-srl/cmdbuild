/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.services.serialization;

import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

/**
 * Enhances the serialization of a given input.
 *
 * @param <T> input data
 * @author afelice
 */
public abstract class DataSerializerEnhancer<T> {

    protected boolean enabled;

    public DataSerializerEnhancer(boolean condition) {
        this.enabled = condition;
    }

    /**
     *
     * @param serialization modified by thin method
     * @param input
     */
    public void enhance(FluentMap<String, Object> serialization, T input) {
        if (enabled) {
            apply(serialization, input);
        }
    }

    abstract protected void apply(FluentMap<String, Object> serialization, T input);

}
