package org.cmdbuild.classe.access;

import jakarta.annotation.Nullable;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.utils.lang.Builder;

public class CardWithAccessImpl implements CardWithAccess {

    private final Card card;
    private final boolean hasAccess;

    public CardWithAccessImpl(CardWithAccessImplBuilder builder) {
        this.card = builder.card;
        this.hasAccess = builder.hasAccess;
    }

    @Override
    @Nullable
    public Card getCard() {
        return card;
    }

    @Override
    public boolean hasAccess() {
        return hasAccess;
    }

    public static CardWithAccessImplBuilder builder() {
        return new CardWithAccessImplBuilder();
    }

    public static class CardWithAccessImplBuilder implements Builder<CardWithAccessImpl, CardWithAccessImplBuilder> {

        private Card card;
        private boolean hasAccess;

        public CardWithAccessImplBuilder withCard(Card card) {
            this.card = card;
            return this;
        }

        public CardWithAccessImplBuilder withHasAccess(boolean hasAccess) {
            this.hasAccess = hasAccess;
            return this;
        }

        @Override
        public CardWithAccessImpl build() {
            return new CardWithAccessImpl(this);
        }
    }

}
