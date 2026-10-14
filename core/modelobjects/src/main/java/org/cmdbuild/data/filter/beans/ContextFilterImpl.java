/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.beans;

import static com.google.common.base.Preconditions.checkArgument;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.common.beans.CardIdAndClassName;
import org.cmdbuild.common.beans.CardIdAndClassNameImpl;
import org.cmdbuild.data.filter.ContextFilter;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ContextFilterImpl implements ContextFilter {

    private final String className, cardId;

    public ContextFilterImpl(CardIdAndClassName card) {
        this.className = card.getClassName();
        this.cardId = card.getId().toString();
    }

    public ContextFilterImpl(String className, long cardId) {
        this(new CardIdAndClassNameImpl(className, cardId));
    }

    public ContextFilterImpl(String className, String recordId) {
        this.className = checkNotBlank(className);
        this.cardId = checkNotBlank(recordId);
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public long getId() {
        checkArgument(NumberUtils.isCreatable(cardId), "this card id has not been mapped to a regular card id ( record id =< %s > )", cardId);
        return toLong(cardId);
    }

    @Override
    public String getRecordId() {
        return cardId;
    }

    @Override
    public String toString() {
        return "ContextFilter{" + "className=" + className + ", cardId=" + cardId + '}';
    }

}
