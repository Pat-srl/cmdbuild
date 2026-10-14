/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.domain.tree;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.cmdbuild.common.beans.CardIdAndClassName;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.services.serialization.domain.tree.DomainNodeData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 *
 * @author afelice
 */
class DomainPath {

    List<DomainNodeData> pathData = list();
    static final Comparator<CardIdAndClassName> CARD_COMPARATOR
            = Comparator.comparing(CardIdAndClassName::getClassName)
                    .thenComparing(CardIdAndClassName::getId);

    /**
     * Checks if a {@link Card} is already added in current path while searching
     * for a <i>root</i>.
     */
    Set<CardIdAndClassName> antiLoop = new TreeSet<>(CARD_COMPARATOR);

    DomainPath(DomainNodeData rootNode) {
        pathData.add(rootNode);
    }

    /**
     * Copy constructor.
     *
     * @param origPath
     */
    DomainPath(DomainPath origPath) {
        this.pathData = list(origPath.pathData);
    }

    void add(DomainNodeData currentNode) {
        pathData.add(currentNode);
    }

    boolean isEmpty() {
        return pathData.isEmpty();
    }

    DomainNodeData get(int i) {
        return pathData.get(i);
    }

    DomainNodeData getLast() {
        return pathData.get(pathData.size() - 1);
    }

    int size() {
        return pathData.size();
    }

    void removeLast() {
        pathData.remove(pathData.size() - 1);
    }

    /**
     * To check if a loop of {@link Card}s in current path.
     *
     * @param card
     * @return
     */
    boolean isAlreadyTracedInPath(Card card) {
        return antiLoop.contains(card);
    }

    /**
     * Updates information useful to check loop of {@link Card}s in current
     * path.
     *
     * @param currentCard
     */
    void traceInPath(Card currentCard) {
        antiLoop.add(currentCard);
    }

    @Override
    public String toString() {
        return "DomainPath{%s}".formatted(LoggerHelper.buildLog_Detailed(this));
    }

}
