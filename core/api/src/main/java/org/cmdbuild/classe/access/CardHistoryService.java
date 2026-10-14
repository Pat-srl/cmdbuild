/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import com.google.common.base.Splitter;
import com.google.common.collect.ComparisonChain;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;

public interface CardHistoryService {

    PagedElements<Card> getHistory(String classId, long cardId, DaoQueryOptions queryOptions);

    PagedElements<DatabaseRecord> getHistoryElements(String classId, long cardId, DaoQueryOptions queryOptions, List<HistoryElement> types);

    Card getHistoryRecord(String classId, long recordId);

    CMRelation getRelationHistoryRecord(String domainId, long recordId);

    /**
     * Changes in history based on the value of a field.
     *
     * @param classId
     * @param cardId
     * @param queryOptions
     * @param types specifies if
     * @return
     */
    List<Card> getHistoryElementsOnlyChanges(String classId, Long cardId, DaoQueryOptions queryOptions, List<HistoryElement> types);

    default <T extends DatabaseRecord> void sortHistoryRecords(List<T> list) {
        list.sort((a, b) -> ComparisonChain.start().compareFalseFirst(a.hasEndDate(), b.hasEndDate()).compare(b.getBeginDate(), a.getBeginDate()).compare(b.getId(), a.getId()).result());
    }

    default List<Card> getHistoryElementsOnlyChanges(String classId, Long cardId, DaoQueryOptions queryOptions) {
        return getHistoryElementsOnlyChanges(classId, cardId, queryOptions, list(HistoryElement.HE_CARDS));
    }

    default List<HistoryElement> fetchHistoryTypes(String types) {
        return Splitter.on(",").splitToList(types).stream().map(e -> parseEnumOrNull(e, CardHistoryService.HistoryElement.class)).collect(toList());
    }

    default String getHistoryTypeFromRecord(DatabaseRecord record) {
        if (record.getType().isDomain()) {
            CMRelation rel = (CMRelation) record;
            if (rel.get("_isReference") != null && rel.get("_isReference").equals(true)) {
                return "reference";
            } else {
                return "relation";
            }
        } else if (record.getType().isClasse()) {
            return record.getUser().startsWith("system") ? "system" : "card";
        }
        return null;
    }

    public enum HistoryElement {
        HE_CARDS, HE_REFERENCES, HE_RELATIONS, HE_SYSTEM
    }

}
