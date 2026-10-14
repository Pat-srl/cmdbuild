package org.cmdbuild.dao.postgres.event;

import com.google.common.base.Preconditions;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.event.BeforeCardUpdateEvent;

public class BeforeCardUpdateEventImpl implements BeforeCardUpdateEvent {

    private final Card next;
    private final Card current;

    public BeforeCardUpdateEventImpl(Card current, Card next) {
        this.next = Preconditions.checkNotNull(next);
        this.current = Preconditions.checkNotNull(current);
    }

    @Override
    public Card getCurrentCard() {
        return current;
    }

    @Override
    public Card getNextCard() {
        return next;
    }

    @Override
    public String toString() {
        return "BeforeCardUpdateEvent{" + "card=" + current + '}';
    }

}
