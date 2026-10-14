/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Objects;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class CardIdAndClassNameImpl extends IdAndDescriptionImpl implements CardIdAndClassName {

    public CardIdAndClassNameImpl(String className, long cardId, @Nullable String description, @Nullable String code) {
        super(className, cardId, description, code);
    }

    public CardIdAndClassNameImpl(String className, Long cardId) {
        this(className, cardId, null, null);
    }

    @Override
    public String getClassName() {
        return getTypeName();
    }

    @Override
    public String toString() {
        return "CardIdAndClassName{" + "className=" + getClassName() + ", cardId=" + getId() + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.getId());
        hash = 67 * hash + Objects.hashCode(this.getClassName());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CardIdAndClassName)) {
            return false;
        }
        final CardIdAndClassName other = (CardIdAndClassName) obj;
        if (!Objects.equals(this.getClassName(), other.getClassName())) {
            return false;
        }
        if (!Objects.equals(this.getId(), other.getId())) {
            return false;
        }
        return true;
    }

    public static CardIdAndClassName copyOf(CardIdAndClassName card) {
        return new CardIdAndClassNameImpl(card.getClassName(), card.getId(), card.getDescription(), card.getCode());
    }

    public static CardIdAndClassName card(String className, Long cardId) {
        return new CardIdAndClassNameImpl(className, cardId);
    }

    public static CardIdAndClassName parse(String expr) {
        return checkNotNull(CardIdAndClassNameUtils.parseCardIdAndClassName(checkNotBlank(expr)));
    }

}
