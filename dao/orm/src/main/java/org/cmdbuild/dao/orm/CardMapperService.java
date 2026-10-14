/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm;

import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.utils.lang.Builder;
import org.cmdbuild.dao.beans.Card;

public interface CardMapperService {

    Card objectToCard(Object object);

    <T> T cardToObject(Card card);

    <T, B extends Builder<T, B>> CardMapper<T, B> getMapperForModelOrBuilder(Class classe);

    Classe getClasseForModelOrBuilder(Class builderOrBeanClass);

    <T, B extends Builder<T, B>> CardMapper<T, B> getMapperForClasse(Classe classe);

    default <T, B extends Builder<T, B>> CardMapper<T, B> getMapperForModel(Class<T> model) {
        return getMapperForModelOrBuilder(model);
    }

    default <T, B extends Builder<T, B>> CardMapper<T, B> getMapperForBuilder(Class<B> builder) {
        return getMapperForModelOrBuilder(builder);
    }

    default String getCardClassIdFromBuilderClass(Class builderClass) {
        return getMapperForModelOrBuilder(builderClass).getClassId();
    }

    default long getCardId(Object model) {
        return ((CardMapper) getMapperForModel(model.getClass())).getCardId(model);
    }

}
