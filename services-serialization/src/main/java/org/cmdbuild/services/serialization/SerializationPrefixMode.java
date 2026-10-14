/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.cmdbuild.services.serialization;

/**
 * Different modes of serialization
 *
 * @author afelice
 */
public enum SerializationPrefixMode {
    /**
     * as in CardWsSerializationHelperv3, when in ANONYMOUS_SERIALIZATION_MODE,
     * serializes as simply Map of <code>(&lt;propertyName&gt;, String)</code>
     */
    SPM_ANONYMOUS_SERIALIZATION,
    /**
     * as in CardWsSerializationHelperv3, serializes as packed Map of
     * <code>(_&lt;attributeName&gt;_&lt;propertyName&gt;, String)</code>
     */
    SPM_PACKED,
    /**
     * as in EmailTemplateProcessorServiceImpl, serializes as a Map of
     * <code>(&lt;attributeName&gt;.&lt;propertyName&gt;, String)</code>
     */
    SPM_JSON;
}
