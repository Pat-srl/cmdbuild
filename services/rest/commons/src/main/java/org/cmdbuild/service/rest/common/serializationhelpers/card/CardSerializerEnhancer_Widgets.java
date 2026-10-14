/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.common.serializationhelpers.card;

import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.services.serialization.DataSerializerEnhancer;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Decorator to add serialization of widgets for a card.
 *
 * @author afelice
 */
public class CardSerializerEnhancer_Widgets extends DataSerializerEnhancer<Card> {

    protected final CardWsSerializationHelperv3 helper;

    public CardSerializerEnhancer_Widgets(boolean condition, CardWsSerializationHelperv3 helper) {
        super(condition);
        this.helper = checkNotNull(helper);
    }

    @Override
    public void apply(FluentMap<String, Object> serialization, Card card) {
        serialization.accept(helper.serializeWidgets(card));
    }

}
