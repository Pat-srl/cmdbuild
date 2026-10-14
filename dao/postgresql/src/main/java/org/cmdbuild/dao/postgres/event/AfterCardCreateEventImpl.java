package org.cmdbuild.dao.postgres.event;

import com.google.common.base.Preconditions;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.event.AfterCardCreateEvent;

public class AfterCardCreateEventImpl implements AfterCardCreateEvent {

    private final Card card;

    public AfterCardCreateEventImpl(Card card) {
        this.card = Preconditions.checkNotNull(card);
    }

    @Override
    public Card getCurrentCard() {
        return card;
    }

    @Override
    public String toString() {
        return "AfterCardCreateEvent{" + "card=" + card + '}';
    }

}
