/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.services.serialization.attribute.file;

import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.services.serialization.CardAttributeSerializerAdapter;
import org.cmdbuild.services.serialization.SerializationPrefixMode;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.cmdbuild.dms.DmsService.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * Serializes:
 * <ul>
 * <li>document, with <code>_author_description</code>;
 * <li>(possibly present) related card (id) in <code>card</code> and related
 * attributes;
 * <li>(if active) category, with further details on permissions and
 * translations.
 * </ul>
 *
 * @author afelice
 */
public class CardAttributeFileWsSerializationHelper extends CardAttributeFileBasicSerializer {

    private static final CmCollectionUtils.FluentSet<String> UNWANTED_DOCUMENT_CARD_ATTRIBUTES = set(DOCUMENT_ATTR_DOCUMENTID, DOCUMENT_ATTR_CARD, DOCUMENT_ATTR_VERSION);

    private final CardAttributeSerializerAdapter attributeSerializer;

    /**
     * Serializes:
     * <ul>
     * <li>document, with <code>_author_description</code>;
     * <li>(possibly present) related card (id) in <code>card</code> and related
     * attributes;
     * <li>(if active) category, with further details on permissions and
     * translations.
     * </ul>
     *
     * @param attributeSerializer Handle recursion when serializing Card
     *                            attributes.
     */
    public CardAttributeFileWsSerializationHelper(CardAttributeSerializerAdapter attributeSerializer) {
        this.attributeSerializer = attributeSerializer;
    }

    @Override
    public FluentMap<String, Object> serialize(CardAttributeFileSerializationData data) {
        FluentMap<String, Object> wsSpecificResult = map();
        wsSpecificResult = wsSpecificResult
                .with(serializeRelatedCard(data, data.cardData));

        String attributeName = data.attributeName;

        SerializationPrefixMode prefixMode = data.getPrefixMode();
        switch (prefixMode) {
            case SPM_ANONYMOUS_SERIALIZATION -> {
            }
            case SPM_PACKED -> {
                // Prefix all keys with _<attributeName>_
                wsSpecificResult = underscorePrefixKeysWith(attributeName, wsSpecificResult);
            }
            case SPM_JSON -> {
                wsSpecificResult = asMapStr(attributeName, wsSpecificResult);
            }
            default -> throw new UnsupportedOperationException(format("unhandled prefix mode =<%s>", prefixMode));
        }


        return super.serialize(data).with(wsSpecificResult);
    }

    @Override
    protected CmMapUtils.FluentMap<String, Object> serializeDocument(CardAttributeFileSerializationData data) {
        return super.serializeDocument(data).with(
                "_author_description", data.documentAuthorDescription
        );
    }

    @Override
    protected FluentMap<String, Object> serializeCategory(CardAttributeFileSerializationData data) {
        CmMapUtils.FluentMap<String, Object> result = map();

        if (data.category != null && data.category.isActive()) {
            result.put(
                    "_category", data.category.getId(),
                    "_category_name", data.category.getCode(),
                    "_category_description", data.category.getDescription(),
                    "_category_description_translation", data.categoryDescriptionTranslation
            );
        } else {
            result.put(
                    "_category", null,
                    "_category_name", null,
                    "_category_description", null,
                    "_category_description_translation", null
            );
        }
        result.put(
                "_can_update", data.categoryCanUpdate,
                "_can_delete", data.categoryCanDelete
        );

        return super.serializeCategory(data).with(result);
    }

    protected CmMapUtils.FluentMap<String, Object> serializeRelatedCard(CardAttributeFileSerializationData data, Map<String, Object> cardData) {
        CmMapUtils.FluentMap<String, Object> result = map();

        if (data.document.hasMetadata()) {
            Card fileAttributeRelatedCard = data.cardWithPermissions; // May have Size, Notes, Hash, Filename attributes
            result.put("_card", fileAttributeRelatedCard.getId());

            // Serialize attributes of Card attached with FILE attribute, using values from cardData
            // As in CardWsSerializationHelperv3.serializeCard() + addCardValuesAndDescriptionsAndExtras_3args
            List<Attribute> attributes = fileAttributeRelatedCard.getType().getServiceAttributes();
            attributes.removeIf(a -> UNWANTED_DOCUMENT_CARD_ATTRIBUTES.contains(a.getName()));
            attributes.stream().forEach(a -> result.with(attributeSerializer.serialize(a, cardData)));
        }

        return result;
    }

}
