/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;

/**
 *
 * @author afelice
 */
public interface CardSync {

    Card read(Classe classe, Long cardId);

    Card insert(Classe classe, Map<String, Object> values);

    Card update(Classe classe, Card card);

    void remove(Classe classe, Card card);

    /**
     * Read a single {@link Card}, with the given {@link Attribute}s only,
     * applying a <i>CQL filter</i>.
     *
     * @param classeName
     * @param cardId
     * @param classeFilterStr
     * @param classeSelectedAttrs
     * @return <code>null</code> if {@link Card} with given <code>cardId</code>
     * not existing or
     */
    Card read(Classe classe, Long cardId, String classeFilterStr, List<String> classeSelectedAttrs);
}
