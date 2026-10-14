/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import org.cmdbuild.api.ApiConverterService;
import org.cmdbuild.api.fluent.Card;
import org.cmdbuild.api.fluent.Relation;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.WorkflowTypeConverter;
import org.springframework.stereotype.Component;

@Component
public class ApiConverterServiceImpl implements ApiConverterService {

    private final WorkflowTypeConverter typeConverter;
    private final DaoService dao;

    public ApiConverterServiceImpl(WorkflowTypeConverter typeConverter, DaoService dao) {
        this.typeConverter = checkNotNull(typeConverter);
        this.dao = checkNotNull(dao);
    }

    @Override
    public Card daoCardToApiCard(org.cmdbuild.dao.beans.Card card) {
        Map<String, Object> map = map(card.getAllValuesAsMap());
        card.getAllValuesAsMap().forEach((key, value) -> {
            if (card.hasAttribute(key)) {
                value = typeConverter.cardValueToFlowValue(value, card.getType().getAttribute(key));
            }
            map.put(key, value);
        });
        return new org.cmdbuild.api.fluent.CardImpl(card.getClassName(), card.getId(), map);
    }

    @Override
    public org.cmdbuild.dao.beans.Card apiCardToDaoCard(org.cmdbuild.api.fluent.Card card) {
        return CardImpl.builder()
                .withType(dao.getClasse(card.getClassName()))
                .withAttributes(card.getAttributes())
                .withId(card.getId())
                .build();
    }

    @Override
    public Relation daoRelationToApiRelation(org.cmdbuild.dao.beans.CMRelation relation) {
        Map<String, Object> map = map(relation.getAllValuesAsMap());
        relation.getAllValuesAsMap().forEach((key, value) -> {
            if (relation.hasAttribute(key)) {
                value = typeConverter.cardValueToFlowValue(value, relation.getType().getAttribute(key));
            }
            map.put(key, value);
        });
        return new org.cmdbuild.api.fluent.RelationImpl(
                relation.getDomainWithThisRelationDirection().getName(),
                new org.cmdbuild.api.fluent.CardImpl(relation.getSourceClassName(), relation.getSourceId()),
                new org.cmdbuild.api.fluent.CardImpl(relation.getTargetClassName(), relation.getTargetId()),
                map);
    }
}
