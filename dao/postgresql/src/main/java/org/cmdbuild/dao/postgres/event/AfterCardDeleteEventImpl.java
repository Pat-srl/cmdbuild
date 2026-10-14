package org.cmdbuild.dao.postgres.event;

import com.google.common.base.Preconditions;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.event.AfterCardDeleteEvent;

public class AfterCardDeleteEventImpl implements AfterCardDeleteEvent {

    private final Card card;

    public AfterCardDeleteEventImpl(Card card) {
        this.card = Preconditions.checkNotNull(card);
    }

    @Override
    public Card getCurrentCard() {
        return card;
    }

    @Override
    public String toString() {
        return "AfterCardDeleteEvent{" + "card=" + card + '}';
    }

}
