/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api;

import org.cmdbuild.api.fluent.Card;
import org.cmdbuild.api.fluent.Relation;

public interface ApiConverterService {

    org.cmdbuild.dao.beans.Card apiCardToDaoCard(org.cmdbuild.api.fluent.Card input);

    Card daoCardToApiCard(org.cmdbuild.dao.beans.Card card);

    Relation daoRelationToApiRelation(org.cmdbuild.dao.beans.CMRelation relation);

}
