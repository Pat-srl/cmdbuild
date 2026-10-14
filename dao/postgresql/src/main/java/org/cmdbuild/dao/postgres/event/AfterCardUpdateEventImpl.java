package org.cmdbuild.dao.postgres.event;

import com.google.common.base.Preconditions;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.event.AfterCardUpdateEvent;

public class AfterCardUpdateEventImpl implements AfterCardUpdateEvent {

    private final Card previous;
    private final Card current;

    public AfterCardUpdateEventImpl(Card previous, Card current) {
        this.previous = Preconditions.checkNotNull(previous);
        this.current = Preconditions.checkNotNull(current);
    }

    @Override
    public Card getPreviousCard() {
        return previous;
    }

    @Override
    public Card getCurrentCard() {
        return current;
    }

    @Override
    public String toString() {
        return "AfterCardUpdateEvent{" + "card=" + current + '}';
    }

}
