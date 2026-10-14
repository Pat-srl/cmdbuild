/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema;

import org.cmdbuild.modeldiff.core.SerializationHandle_String;

/**
 *
 * @author afelice
 */
public interface SchemaCollector {

    public SchemaConfiguration collectSchema(String id, String mnemoniName);

    public SerializationHandle_String compareSchema(SchemaConfiguration leftSchema, String rightMnemonicName);

    public SerializationHandle_String compareSchema(SchemaConfiguration leftSchema, SchemaConfiguration rightSchema);

    public SchemaConfiguration applySchemaDiff(SerializationHandle_String diffData);
}
