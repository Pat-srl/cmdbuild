package org.cmdbuild.classe.access;

import jakarta.annotation.Nullable;
import org.cmdbuild.dao.beans.Card;

public interface CardWithAccess {

    @Nullable
    Card getCard();

    boolean hasAccess();

    default boolean hasCard() {
        return getCard() != null;
    }

}
