/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.common.serializationhelpers.card;

import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.services.serialization.DataSerializer;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.dao.constants.SystemAttributes.*;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * Serialize a card, converting attribute values in string, map and arrays that
 * can be handled to create a JSON.
 *
 * @author afelice
 */
public class CardSerializer implements DataSerializer<Card> {

    protected final CardWsSerializationHelperv3 helper;

    public CardSerializer(CardWsSerializationHelperv3 helper) {
        this.helper = checkNotNull(helper);
    }

    /**
     * <b>Beware</b>: invoked {@link CardWsSerializationHelperv3#serializeCard(org.cmdbuild.dao.beans.Card, org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3.ExtendedCardOptions...)
     * }
     * is implemented invoking
     * <code>card.getType().getAliasToAttributeMap()</code>, so the card
     * serialization has <code>null</code> for (in case) given {@link Card} have
     * only a subset of selected attributes. Invoke {@link #serialize(org.cmdbuild.dao.beans.Card,
     * <any>) }
     * instead.
     *
     * @param card
     * @return
     */
    @Override
    public FluentMap<String, Object> serialize(Card card) {
        return helper.serializeCard(card);
    }

    /**
     * Card serialization, supporting only part of attributes selected.
     *
     * @param card
     * @param selectedAttrs
     * @return
     */
    public FluentMap<String, Object> serialize(Card card, Set<String> selectedAttrs) {
        return helper.serializeCard(card, DaoQueryOptionsImpl.builder()
                .withAttrs(selectedAttrs)
                .build());
    }

    public CardWsSerializationHelperv3 getHelper() {
        return helper;
    }

    public static FluentMap<String, Object> cardToMap(Card card) {
        return map(
                SYSTEM_ATTRIBUTE_ALIASES.get(ATTR_ID), card.getId(),
                SYSTEM_ATTRIBUTE_ALIASES.get(ATTR_IDCLASS), card.getTypeName(),
                SYSTEM_ATTRIBUTE_ALIASES.get(ATTR_USER), card.getUser(),
                SYSTEM_ATTRIBUTE_ALIASES.get(ATTR_BEGINDATE), toIsoDateTime(card.getBeginDate()),
                ATTR_CODE, card.getCode(),
                ATTR_DESCRIPTION, card.getDescription()
        );
    }

}
