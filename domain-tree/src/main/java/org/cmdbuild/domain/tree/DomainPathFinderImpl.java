/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.domain.tree;

import com.google.common.base.Joiner;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.common.beans.CardIdAndClassName;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.STANDARD_CLASS_INFO_ATTRIBUTES;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainCardinality;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_MANY;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_ONE;
import static org.cmdbuild.dao.entrytype.DomainCardinality.ONE_TO_MANY;
import static org.cmdbuild.dao.entrytype.DomainCardinality.ONE_TO_ONE;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import static org.cmdbuild.domain.tree.LoggerHelper.buildLog;
import static org.cmdbuild.domain.tree.LoggerHelper.buildLog_Filter;
import static org.cmdbuild.domain.tree.LoggerHelper.buildLog_FilteredOut;
import static org.cmdbuild.domain.tree.LoggerHelper.buildLog_Short;
import org.cmdbuild.services.serialization.domain.tree.CardData;
import org.cmdbuild.services.serialization.domain.tree.DomainNodeData;
import org.cmdbuild.services.serialization.domain.tree.DomainTreeData;
import org.cmdbuild.sync.CardSync;
import org.cmdbuild.sync.ClasseSync;
import org.cmdbuild.sync.DomainSync;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author afelice
 */
@Component
public class DomainPathFinderImpl implements DomainPathFinder {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    final CardSync cardSync;
    final ClasseSync classeSync;
    final DomainSync domainSync;

    final DomainTreeBuilder domainTreeBuilder;

    public DomainPathFinderImpl(CardSync cardSync,
            ClasseSync classeSync, DomainSync domainSync) {
        this.cardSync = checkNotNull(cardSync);
        this.classeSync = checkNotNull(classeSync);
        this.domainSync = checkNotNull(domainSync);

        this.domainTreeBuilder = new DomainTreeBuilder(classeSync);
    }

    @Override
    public List<Card> findPathToRoot(DomainTreeData domainTreeData, CardData leafCardData) {
        List<Card> result = list();

        // Raises DomainTreeException if given domain tree:
        // - has no nodes;
        // - has no single root;
        // - has multiple roots.
        DomainTree domainTree = domainTreeBuilder.buildDomainTree(domainTreeData, leafCardData.className);

        if (!domainTree.hasPaths()) {
            String errMsg = "%s has not suitable paths for %s".formatted(buildLog(domainTreeData), buildLog(leafCardData));
            logger.error(errMsg);
            throw new DomainTreeException(errMsg);
        }

        List<DomainPath> suitablePaths = domainTree.getPaths(); // Not empty paths to check
        logger.debug("%s - checking [%d] paths to root =< %s > from leaf %s".formatted(buildLog(domainTreeData), suitablePaths.size(), domainTree.rootNodeClasseName, buildLog(leafCardData)));
        Classe leafCardClasse = classeSync.read(leafCardData.className); // cached in ClasseRepositoryImpl
        logger.debug("%s - loading leaf card %s".formatted(buildLog(domainTree), buildLog_Short(leafCardData)));
        // @todo AFE TBC va considerato il filter anche qui, che sarebbe [TBC] RoomCI.filter nel json input?
        Card leafCard = cardSync.read(leafCardClasse, toLong(leafCardData.id));
        //loadCard(domainTree, leafCardClasse, leafCardData.Id, leafCardData.ClasseFilterStr, "leaf")
        if (leafCard == null) {
            String errMsg = "%s - not found %s".formatted(buildLog(domainTree), buildLog(leafCardData));
            logger.error(errMsg);
            throw new DomainTreeException(errMsg);
        }
        CardRelationsFinder cardRelationsFinder = new CardRelationsFinder(domainSync, logger); // chache for cards related to given one
        for (DomainPath curSuitablePath : suitablePaths) {
            logger.debug("%s - checking %s to root =< %s > from leaf %s".formatted(buildLog(domainTreeData), buildLog(curSuitablePath), domainTree.rootNodeClasseName, buildLog_Short(leafCard)));

            // (Possibly) Re-testing leaf card for if leaf filter found in this path
            DomainNodeData curPathLeafNode = curSuitablePath.getLast();
            if (!isBlank(curPathLeafNode.asLeafFilter) && loadCard(domainTree, leafCardClasse, leafCard.getId(), curPathLeafNode.asLeafFilter, "leaf") == null) {
                logger.debug("%s - not found suitable path to root =< %s > from (*filtered out*) leaf %s in %s".formatted(buildLog(domainTreeData), domainTree.rootNodeClasseName, buildLog_Short(leafCard), buildLog(curSuitablePath)));
                continue; // check next path
            }

            List<Card> curFoundPath = findPathToRoot(domainTree, curSuitablePath, leafCard, cardRelationsFinder);
            if (curFoundPath.isEmpty()) {
                logger.debug("%s - not found suitable path to root =< %s > from leaf %s in %s".formatted(buildLog(domainTreeData), domainTree.rootNodeClasseName, buildLog_Short(leafCard), buildLog(curSuitablePath)));
            } else {
                // A path found
                logger.debug("%s - *found* path to root =< %s > from leaf %s in %s: %s".formatted(buildLog(domainTreeData), domainTree.rootNodeClasseName, buildLog_Short(leafCard), buildLog(curSuitablePath), LoggerHelper.buildLog_Short(curFoundPath)));
                result = curFoundPath;
                break; // exit
            }
        }

        if (result.isEmpty()) {
            logger.debug("%s - not found any resulting path to root =< %s > from leaf %s".formatted(buildLog(domainTreeData), domainTree.rootNodeClasseName, buildLog(leafCard)));
        }

        return result;
    }

    /**
     *
     * @param domainTree
     * @param curSuitablePath not empty paths to check, from given
     * <i>destinationCard</i>
     * @param destinationCard
     * @param cardRelationsFinder cache for {@link CMRelation} for each
     * {@link Card} treated till now. Useful when returning in the same Card in
     * different paths.
     * @return
     */
    private List<Card> findPathToRoot(DomainTree domainTree,
            DomainPath curSuitablePath,
            Card destinationCard,
            CardRelationsFinder cardRelationsFinder) {
        List<Card> resultCardInversePath = list();

        if (inverseDfs(resultCardInversePath,
                curSuitablePath, curSuitablePath.size() - 1,
                destinationCard,
                domainTree, cardRelationsFinder)) {

            Collections.reverse(resultCardInversePath);
            return resultCardInversePath;
        }

        return Collections.emptyList();
    }

    /**
     * Find (if any) inverse path from given <code>currentCard</code> to a root
     * in given path <code>curSuitablePath</code>.
     *
     * <p>
     * Based on <i>filter</i> defined in related parent {@link DomainNodeData}
     * to filter (parent) suitable {@link Card}s through the <i>root</i>.
     *
     * @param resultCardInversePath current inverse path from <i>leaf card</i>
     * to a <i>root card</i>.
     * @param curSuitablePath
     * @param curLevel from end to start, represents a suitable path from a
     * <i>leaf card</i> to a <i>root</i>.
     * @param currentCard
     * @param domainTree
     * @param cardRelationsFinder cache for {@link CMRelation} for each
     * {@link Card} treated till now. Useful when returning in the same Card in
     * different paths.
     * @return
     */
    private boolean inverseDfs(List<Card> resultCardInversePath,
            DomainPath curSuitablePath, int curLevel,
            Card currentCard,
            DomainTree domainTree,
            CardRelationsFinder cardRelationsFinder) {

        // Check if loop of Cards in path
        if (curSuitablePath.isAlreadyTracedInPath(currentCard)) {
            // A loop of Cards detected. Try with another domain tree branch
            logger.warn("%s, %s - found a loop of cards through %s".formatted(buildLog(domainTree), buildLog(curSuitablePath), buildLog(currentCard)));
            return false;
        }
        resultCardInversePath.add(currentCard);
        curSuitablePath.traceInPath(currentCard);

        if (curLevel == 0) {
            // All suitable path walked: root found
            return true;
        }

        // Find reverse related (parent) Cards, (possibly) applying Node filter
        DomainNodeData prevLevelDomainNodeData = curSuitablePath.get(curLevel);
        Card curParentCard = findParent(currentCard, prevLevelDomainNodeData,
                domainTree, cardRelationsFinder);
        if (curParentCard == null) {
            logger.debug("%s, %s - not found any resulting related card through Domain =< %s > (target Classe =< %s >)%s for %s".formatted(buildLog(domainTree),
                    buildLog(curSuitablePath), prevLevelDomainNodeData.domain, prevLevelDomainNodeData.targetClass, buildLog_FilteredOut(prevLevelDomainNodeData.cqlFilter), buildLog_Short(currentCard)));
            return false;
        }

        // Some parent Card found.
        // Process the parent following its path
        if (inverseDfs(resultCardInversePath, //recursive call for each parent Card
                curSuitablePath, curLevel - 1, // Go up one level searching the path root
                curParentCard,
                domainTree,
                cardRelationsFinder)) {

            return true;
        }

        resultCardInversePath.remove(resultCardInversePath.size() - 1);
        return false;
    }

    /**
     *
     * @param aTargetCard
     * @param aDomainTargetNodeData
     * @param domainTree
     * @return <code>null</code> if no parent {@link Card} found belonging to
     * {@link Classe} defined by input <code>aDomainTargetNodeData</code>.
     * @param cardRelationsFinder cache for {@link CMRelation} for each
     * {@link Card} treated till now. Useful when returning in the same Card in
     * different paths.
     */
    private Card findParent(Card aTargetCard, DomainNodeData aDomainTargetNodeData,
            DomainTree domainTree, CardRelationsFinder cardRelationsFinder) {
        String toCheckDomainName = aDomainTargetNodeData.domain;
        logger.debug("%s - inspecting domain =< %s > searching related cards for %s".formatted(buildLog(domainTree), toCheckDomainName, buildLog_Short(aTargetCard)));

        List<CMRelation> relationsForCard = cardRelationsFinder.fetchRelatedCards(aTargetCard, domainTree);
        logger.debug("%s - found [%d] (*global*) related cards for %s".formatted(buildLog(domainTree), relationsForCard.size(), buildLog_Short(aTargetCard)));

        Optional<CMRelation> foundParentOpt = relationsForCard.stream()
                .filter(r -> toCheckDomainName.equals(r.getType().getName()))
                .findAny();

        if (foundParentOpt.isEmpty()) {
            return null;
        }

        // A parent found
        CMRelation foundParentRelation = foundParentOpt.get();

        // Check domain cardinality vs Reference attribute direction
        Domain foundDomain = foundParentRelation.getDomainWithThisRelationDirection();
        DomainCardinality foundDomainCardinality = foundDomain.getCardinality();
        if (foundDomainCardinality == MANY_TO_MANY
                || foundDomainCardinality == ONE_TO_ONE) {
            logger.warn("%s - found a unsupported Domain =< %s > with cardinality =< %s >".formatted(buildLog(domainTree), foundDomain.getName(), buildLog(foundDomainCardinality)));
            return null;
        }
        switch (foundDomainCardinality) {
            case MANY_TO_ONE -> {
                if (foundParentRelation.isDirect()) {
                    logger.warn("%s - found a unsupported (direct reference) Domain =< %s > with cardinality =< %s >".formatted(buildLog(domainTree), foundDomain.getName(), buildLog(foundParentRelation.getType().getCardinality())));
                    return null;
                }
            }
            case ONE_TO_MANY -> {
                if (foundParentRelation.isDirect()) {
                    logger.warn("%s - found a unsupported (inverse reference) Domain =< %s > with cardinality =< %s >".formatted(buildLog(domainTree), foundDomain.getName(), buildLog(foundParentRelation.getType().getCardinality())));
                    return null;
                }
            }
            default ->
                throw new AssertionError("Usupported cardinality =< %s > in domain =< %s > at this point".formatted(serializeEnum(foundDomainCardinality), foundDomain));
        }

        Classe parentClasse = getParentClasse(foundDomain);

        Long parentCardId = getParentCardId(foundParentRelation);
        logger.debug("%s - found related parent card %s for %s".formatted(buildLog(domainTree), buildLog_Short(parentClasse.getName(), parentCardId), buildLog_Short(aTargetCard)));

        // (possibly) Fetch parent Classe filter
        String parentClasseFilterStr = aDomainTargetNodeData.cqlFilter;

        // @todo AFE TBC se filter è vuoto, emulare la Card senza caricarla: mancano però Code e Description (c'è solo _id e _type)
        // Returns null if parent Card doesn't match given classeFilterStr
        Card parentCard = loadCard(domainTree, parentClasse, parentCardId, parentClasseFilterStr, "parent");

        return parentCard;
    }

    private Card loadCard(DomainTree domainTree, Classe classe, Long cardId, String classeFilterStr, String contextMsg) {
        logger.debug("%s - loading (%s) card %s%s".formatted(buildLog(domainTree), contextMsg, buildLog_Short(classe.getName(), cardId), buildLog_Filter(classeFilterStr)));
        // Returns null if parent Card doesn't match given classeFilterStr
        Card resultCard = cardSync.read(classe, cardId, classeFilterStr, STANDARD_CLASS_INFO_ATTRIBUTES); // Only attributes (Id, IdClass, Code, Description)
        return resultCard;
    }

    private Optional<ReferenceAttributeType> getReferenceAttr(Classe aClasse, String domainName) {
        // See Classe.hasReferenceForDomain()
        return aClasse.getAllAttributes().stream()
                .filter(a -> a.isOfType(REFERENCE))
                .map(Attribute::getType)
                .map(ReferenceAttributeType.class::cast)
                .filter(a -> domainName.equals(a.getDomainName()))
                .findAny();
    }

    private Classe getParentClasse(Domain aDomain) {
        DomainCardinality aDomainCardinality = aDomain.getCardinality();
        if (aDomainCardinality == MANY_TO_ONE) {
            return aDomain.getTargetClass();
        }

        return aDomain.getSourceClass();
    }

    private Long getParentCardId(CMRelation aRelation) {
        DomainCardinality aDomainCardinality = aRelation.getDomainWithThisRelationDirection().getCardinality();
        if (aDomainCardinality == MANY_TO_ONE) {
            return aRelation.getTargetId();
        }

        return aRelation.getSourceId();
    }

    /**
     * Dual of {@link #getParentClasse(org.cmdbuild.dao.entrytype.Domain) }.
     *
     * @param aDomain
     * @return
     */
    private Classe getChildClasse(Domain aDomain) {
        DomainCardinality aDomainCardinality = aDomain.getCardinality();
        if (aDomainCardinality == MANY_TO_ONE) {
            return aDomain.getSourceClass();
        }

        return aDomain.getTargetClass();
    }
} // end DomainPathFinderImpl class

/**
 * Implements a temporary <i>cache</i> for current {@link DomainPathFinderImpl#findPathToRoot(org.cmdbuild.services.serialization.domain.tree.DomainTreeData, org.cmdbuild.services.serialization.domain.tree.CardData)
 * } invocation: each invocation may interest a lot of {@link Card}s, but
 * different branches in the {@link DomainTree} regard different set of
 * {@link Card}, so a global <i>cache</i>
 * wouldn't be useful.
 *
 * @author afelice
 */
class CardRelationsFinder {

    static final Comparator<CardIdAndClassName> CARD_COMPARATOR
            = Comparator.comparing(CardIdAndClassName::getClassName)
                    .thenComparing(CardIdAndClassName::getId);

    final Logger logger;
    final DomainSync domainSync;
    final TreeMap<Card, List<CMRelation>> tracedCardRelations = new TreeMap<>(CARD_COMPARATOR);

    CardRelationsFinder(DomainSync domainSync, Logger logger) {
        this.domainSync = checkNotNull(domainSync);
        this.logger = logger;
    }

    List<CMRelation> fetchRelatedCards(Card aTargetCard, DomainTree domainTree) {
        // TBC: se serve la Classe, classeSync.readClasse() , tanto è cachata i ClasseRepositoryImpl
        if (tracedCardRelations.containsKey(aTargetCard)) {
            logger.debug("%s - retrieved from relations cache for %s".formatted(buildLog(domainTree), buildLog_Short(aTargetCard)));
            return tracedCardRelations.get(aTargetCard);
        }

        logger.debug("%s - loading related cards for %s".formatted(buildLog(domainTree), buildLog_Short(aTargetCard)));
        List<CMRelation> relationsForCard = domainSync.readRelationsForCard(aTargetCard);
        tracedCardRelations.put(aTargetCard, relationsForCard);

        return relationsForCard;
    }

} // end class CardRelationsFinder class

class LoggerHelper {

    static String buildLog(DomainTreeData domainTreeData) {
        return "Domain tree =< %s > (id =< %s >)".formatted(domainTreeData.name, domainTreeData.id);
    }

    static String buildLog(DomainTree domainTree) {
        return "Domain tree =< %s > (id =< %s >)%s".formatted(domainTree.name, domainTree.id, domainTree.leafNodeClasseName == null ? "" : ", leaf classe =< " + domainTree.leafNodeClasseName + " >");
    }

    static String buildLog(Card aCard) {
        return "card =< %s >, Classe =< %s > (description =< %s >)".formatted(aCard.getId(), aCard.getClassName(), aCard.getDescription());
    }

    static String buildLog(CardData aCardData) {
        return "card =< %s >, Classe =< %s > (description =< %s >)".formatted(aCardData.id, aCardData.className, aCardData.description);
    }

    static String buildLog_Short(CardData aCardData) {
        return buildLog_Short(aCardData.className, toLong(aCardData.id));
    }

    static String buildLog_Short(CardIdAndClassName aCard) {
        return buildLog_Short(aCard.getClassName(), aCard.getId());
    }

    static String buildLog_Short(String classeName, Long cardId) {
        return "%s.%s".formatted(classeName, cardId);
    }

    static String buildLog_Short(List<Card> cards) {
        return Joiner.on(", ").join(cards.stream().map(LoggerHelper::buildLog_Short).collect(toList()));
    }

    static String buildLog(DomainPath aPath) {
        return "path =< %s >".formatted(Joiner.on(" -> ").join(aPath.pathData.stream().map(p -> p.targetClass).collect(toList())));
    }

    static String buildLog_Detailed(DomainPath aPath) {
        return "path =< %s >; already traced cards: =< %s >".formatted(
                Joiner.on(" -> ").join(aPath.pathData.stream().map(p -> p.targetClass).collect(toList()),
                        Joiner.on(",").join(aPath.antiLoop.stream().map(LoggerHelper::buildLog_Short).collect(toList()))
                ));
    }

    static String buildLog_Filter(String cqlFilterStr) {
        return isBlank(cqlFilterStr) ? "" : " (cql Filter =< %s >)".formatted(cqlFilterStr);
    }

    static String buildLog_FilteredOut(String cqlFilterStr) {
        return isBlank(cqlFilterStr) ? "" : " (or no related card matches given cql Filter =< %s >)".formatted(cqlFilterStr);
    }

    static String buildLog(DomainCardinality foundDomainCardinality) {
        return switch (foundDomainCardinality) {
            case ONE_TO_ONE ->
                "1:1";
            case ONE_TO_MANY ->
                "1:N";
            case MANY_TO_ONE ->
                "N:1";
            case MANY_TO_MANY ->
                "N:N";
            default ->
                throw new AssertionError("Unsupported domain cardinality =< %s >".formatted(foundDomainCardinality));
        };
    }
} // end LoggerHelper class
