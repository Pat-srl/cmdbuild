/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.common.serializationhelpers.card;

import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3.ExtendedCardOptions;
import org.cmdbuild.services.serialization.DataSerializerEnhancer;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

import java.util.EnumSet;

/**
 * Serialize a card with info.
 *
 * <p>@todo this could be a {@link DataSerializerEnhancer<Card>} as {@link CardSerializerEnhancer_Widgets}
 * if the following code:
 * <pre>
 *     if (extendedCardOptions.contains(INCLUDE_MODEL)) {
 * m.put("_model", classSerializationHelper.buildBasicResponse(card.getType())
 * .with("attributes", list(transform(card.getType().getServiceAttributes(), attributeTypeConversionService::serializeAttributeType))));
 * }
 * </pre>
 * that is in {@link CardWsSerializationHelperv3#serializeCard(org.cmdbuild.dao.beans.Card, org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions, java.util.Set)} is put in
 * <code>apply()</code>. The problem removing such three lines of code from {@link CardWsSerializationHelperv3#serializeCard(org.cmdbuild.dao.beans.Card, org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions, java.util.Set)}
 * is in identifying all the callers that pass {@link ExtendedCardOptions#INCLUDE_MODEL},
 * and let them use this serializer enhancer.
 * <ol>
 * <li>ProcessInstancesWs.read() that has <code>includeModel</code> as query parameter, as in CardWs.readOne();
 * <li>ProcessInstancesWs.readMany() that has <code>includeModel</code> at <code>false</code>, so <b>not used</b>.
 * </ol>
 *
 * @author afelice
 */
public class CardSerializer_WithModel extends CardSerializer {

    public CardSerializer_WithModel(CardWsSerializationHelperv3 helper) {
        super(helper);
    }

    @Override
    public FluentMap<String, Object> serialize(Card card) {
        return helper.serializeCard(card, EnumSet.of(ExtendedCardOptions.INCLUDE_MODEL));
    }
}
