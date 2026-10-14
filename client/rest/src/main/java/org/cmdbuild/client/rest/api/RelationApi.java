/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import static java.util.Collections.emptyMap;
import org.cmdbuild.client.rest.model.RelationInfo;
import java.util.List;
import java.util.Map;
import org.cmdbuild.common.beans.CardIdAndClassName;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public interface RelationApi {

    RelationInfo createRelation(String domain, CardIdAndClassName source, CardIdAndClassName destination, Map<String, Object> data);

    List<RelationInfo> getRelationsForCard(CardIdAndClassName card);

    default RelationInfo createRelation(String domain, CardIdAndClassName source, CardIdAndClassName destination) {
        return createRelation(domain, source, destination, emptyMap());
    }

    default RelationInfo createRelation(String domain, CardIdAndClassName source, CardIdAndClassName destination, Object... data) {
        return createRelation(domain, source, destination, map(data));
    }
}
