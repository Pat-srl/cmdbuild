/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.data;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmNullableUtils;

/**
 * Represents a <i>diff</i> on modified <i>data</i>.
 *
 * @author afelice
 */
public class GeneratedDiffData {

    public String name;

    public List<Map<String, Object>> insertedCards;
    public List<Map<String, Object>> removedCards;
    public List<GeneratedDiffData_ChangedCard> changedCards;

    /**
     * Used if adding incrementally {@link Classe} deserialized <i>diff
     * data</i>.
     *
     * @param curClasseDiffData
     * @throws IOException if found <i>dataset</i> name is mismatching.
     */
    public void add(GeneratedDiffData curClasseDiffData) throws IOException {
        checkDataset(curClasseDiffData);

        insertedCards = addComponentData(curClasseDiffData, insertedCards, curClasseDiffData.insertedCards);
        removedCards = addComponentData(curClasseDiffData, removedCards, curClasseDiffData.removedCards);
        changedCards = addComponentData(curClasseDiffData, changedCards, curClasseDiffData.changedCards);
    }

    /**
     * Removes changes that are now empty for some sanitizing operation (like
     * removing newly to add document metadata.
     */
    public void removeEmptyChanges() {
        changedCards = list(changedCards).without(c -> c.changedAttribs.isEmpty() && c.addedAttribs.isEmpty());
    }

    /**
     * Check if diff is empty
     */
    public boolean isEmpty() {
        return insertedCards.isEmpty() && removedCards.isEmpty() && changedCards.isEmpty();
    }

    protected List addComponentData(GeneratedDiffData curClasseData, List thisComponentData, List otherComponentData) {
        List result = thisComponentData;
        if (result == null) {
            result = list();
        }

        if (otherComponentData != null) {
            result.addAll(otherComponentData);
        }

        return result;
    }

    protected void checkDataset(GeneratedDiffData curClasseData) throws IOException {
        if (CmNullableUtils.isBlank(name)) {
            this.name = curClasseData.name;
        } else {
            if (!this.name.equals(curClasseData.name)) {
                throw new IOException("mismatching related dataset, found =< %s > but expected =< %s >".formatted(curClasseData.name, this.name));
            }
        }
    }
}
