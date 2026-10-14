package org.cmdbuild.dao.postgres.event;

import com.google.common.base.Preconditions;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.event.BeforeCardDeleteEvent;

public class BeforeCardDeleteEventImpl implements BeforeCardDeleteEvent {

    private final Card card;

    public BeforeCardDeleteEventImpl(Card card) {
        this.card = Preconditions.checkNotNull(card);
    }

    @Override
    public Card getCurrentCard() {
        return card;
    }

    @Override
    public String toString() {
        return "BeforeCardDeleteEvent{" + "card=" + card + '}';
    }

}
