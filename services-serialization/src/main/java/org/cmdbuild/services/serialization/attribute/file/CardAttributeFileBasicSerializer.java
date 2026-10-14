/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.services.serialization.attribute.file;

import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.services.serialization.CardAttributeSerializationHelper;
import org.cmdbuild.services.serialization.SerializationPrefixMode;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

import static java.lang.String.format;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;

/**
 * Serializes:
 * <ul>
 * <li>document;
 * <li>(if active) category.
 * </ul>
 *
 * @author afelice
 */
public class CardAttributeFileBasicSerializer implements CardAttributeSerializationHelper<CardAttributeFileSerializationData> {

    @Override
    public CmMapUtils.FluentMap<String, Object> serialize(CardAttributeFileSerializationData data) {
        FluentMap<String, Object> result = map();

        DocumentInfoAndDetail document = data.document;
        String attributeName = data.attributeName;

        result = result.with(
                        "isDmsServiceOk", data.isDmsServiceOk
                ).with(serializeCategory(data))
                .with(serializeDocument(data));

        SerializationPrefixMode prefixMode = data.getPrefixMode();
        switch (prefixMode) {
            case SPM_ANONYMOUS_SERIALIZATION -> {
            }
            case SPM_PACKED -> {
                // Prefix all keys with _<attributeName>_
                result = underscorePrefixKeysWith(attributeName, result);
                // AttributeName without prefix
                result.with(attributeName, document.getDocumentId());

            }
            case SPM_JSON -> {
                result = serialize_AsJson(result, data);
            }
            default -> throw new UnsupportedOperationException(format("unhandled prefix mode =<%s>", prefixMode));
        }

        return result;
    }

    /**
     * All values as map (<code>&lt;attributeName>&gt;.&lt;key&gt;</code>,
     * <code>&lt;value&gt;</code>)
     *
     * <p>
     * a <code>&lt;attributeName>&gt</code> with the value of <i>dms document
     * id</i> is added.
     *
     * @param cardValuesSerialization
     * @param data
     * @return
     */
    protected FluentMap<String, Object> serialize_AsJson(FluentMap<String, Object> cardValuesSerialization, CardAttributeFileSerializationData data) {
        DocumentInfoAndDetail document = data.document;
        String attributeName = data.attributeName;

        // Connvert given values serialization to <attributeName>.<key>
        cardValuesSerialization = asMapStr(attributeName, cardValuesSerialization);
        // <attributeName> has no prefix
        cardValuesSerialization.with(attributeName, document.getDocumentId());
        return cardValuesSerialization;
    }

    protected CmMapUtils.FluentMap<String, Object> serializeCategory(CardAttributeFileSerializationData data) {
        FluentMap<String, Object> result = map();

        if (data.category != null && data.category.isActive()) {
            Long categoryId = data.category.getId();
            result.put(
                    "category", categoryId,
                    "Category", categoryId // for legacy
            );
        } else {
            result.put(
                    "category", null,
                    "Category", null // for legacy
            );
        }

        return result;
    }

    protected CmMapUtils.FluentMap<String, Object> serializeDocument(CardAttributeFileSerializationData data) {
        return mapOf(String.class, Object.class).with(
                "name", data.document.getFileName(),
                "description", data.document.getDescription(),
                "Description", data.document.getDescription(), // for legacy
                "version", data.document.getVersion(),
                "author", data.document.getAuthor(),
                "created", toIsoDateTime(data.document.getCreated()),
                "modified", toIsoDateTime(data.document.getModified())
        );
    }

}
