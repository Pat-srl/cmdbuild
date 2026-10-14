/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.cache;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.Iterables;
import static java.lang.Long.min;
import static java.lang.Math.toIntExact;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import jakarta.annotation.Nullable;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.common.utils.PositionOf;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.config.api.ConfigListener;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.utils.CmSorterUtils.serializeSorter;
import static org.cmdbuild.dao.utils.PositionOfUtils.buildPositionOf;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.SorterElement;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CardCacheServiceImpl implements CardCacheService, MinionComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CoreConfiguration config;

    private final MinionHandler minionHandler;

    private final Map<Long, Card> cardsById = new ConcurrentHashMap<>();
    private final Map<String, CachedCards> cardsByClass = new ConcurrentHashMap<>();

    public CardCacheServiceImpl(CoreConfiguration coreConfiguration) {
        this.config = checkNotNull(coreConfiguration);
        this.minionHandler = MinionHandlerImpl.builder()
                .withName("Card Cache Service")
                .withConfigEnabler("org.cmdbuild.core.cardcache.enabled")
                .withEnabledChecker(config::isCardCacheEnabled)
                .reloadOnConfigs(CoreConfiguration.class)//TODO
                .withHidden(true)
                .build();
    }

    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    @ConfigListener(CoreConfiguration.class)
    public void handleConfigChange() {//TODO improve this
        invalidateAll();
    }

    private void invalidateAll() {
        logger.debug("clear card cache");
        cardsById.clear();
        cardsByClass.clear();
    }

    @Override
    public Card getCard(long id, Supplier<Card> loader) {
        if (config.isCardCacheEnabled()) {
            Card card = cardsById.get(id);
            if (card != null) {
                return card;
            } else {
                card = checkNotNull(loader.get(), "invalid null value returned by loader callback");
                logger.debug("loaded card = {}", card);
                cardsById.put(id, card);
                return card;
            }
        } else {
            return loader.get();
        }
    }

    @Override
    public void createCard(Card card) {
        if (config.isCardCacheEnabled()) {
            logger.debug("create card = {}", card);
            cardsById.put(card.getId(), card);//TODO write lock ??
            card.getType().getAncestorsAndSelf().forEach(typeName -> {
                CachedCards cards = cardsByClass.get(typeName);
                if (cards != null) {
                    cards.createCard(card);
                }
            });
        }
    }

    @Override
    public void updateCard(Card card) {
        if (config.isCardCacheEnabled()) {
            logger.debug("update card = {}", card);
            cardsById.put(card.getId(), card);//TODO write lock ??
            card.getType().getAncestorsAndSelf().forEach(typeName -> {
                CachedCards cards = cardsByClass.get(typeName);
                if (cards != null) {
                    cards.updateCard(card);
                }
            });
        }
    }

    @Override
    public void deleteCard(Card card) {
        if (config.isCardCacheEnabled()) {
            logger.debug("delete card = {}", card);
            cardsById.remove(card.getId());//TODO write lock ??
            card.getType().getAncestorsAndSelf().forEach(typeName -> {
                CachedCards cards = cardsByClass.get(typeName);
                if (cards != null) {
                    cards.deleteCard(card.getId());
                }
            });
        }
    }

    @Override
    public PagedElements<Card> getCards(Classe classe, DaoQueryOptions queryOptions, String userAccessPrivilegesHash, Supplier<PagedElements<Card>> loader) {
        if (!config.isCardCacheEnabled()) {
            return loader.get();
//        } else if (queryOptions.hasAttrs() || !queryOptions.getFilter().isNoop() || !queryOptions.getSorter().isSortBy(ATTR_BEGINDATE, ASC) || !queryOptions.hasLimit()) {
        } else if (queryOptions.hasAttrs() || !queryOptions.getFilter().isNoop()) {
            logger.debug("cache miss: unable to use cache for query from class = {} with options = {}", classe, queryOptions);
            return loader.get();
        } else {
            CachedCards cards = cardsByClass.get(classe.getName());
            if (cards == null) {
                cardsByClass.put(classe.getName(), cards = new CachedCards());
            }
            return cards.getCards(queryOptions, userAccessPrivilegesHash, loader);
        }
    }

    private final class CachedCards {

        private final Map<String, PagedElements<Card>> cardsByQueryOptionsHash = new ConcurrentHashMap<>();

        public PagedElements<Card> getCards(DaoQueryOptions queryOptions, String userAccessPrivilegesHash, Supplier<PagedElements<Card>> loader) {
            String queryOptionsHash = userAccessPrivilegesHash + "|" + serializeSorter(queryOptions.getSorter());
            PagedElements<Card> cards = cardsByQueryOptionsHash.get(queryOptionsHash);
            if (cards == null) {
                cards = loader.get();
                tryPut(queryOptions, queryOptionsHash, PagedElements.empty(), cards);
                return cards;
            } else {
                if (queryOptions.isPaged()) {
                    if (queryOptions.hasPositionOf()) {
                        long cardId = queryOptions.getPositionOf();
                        int index = Iterables.indexOf(cards, c -> equal(c.getId(), cardId));
                        if (index == -1) {
                            PagedElements<Card> res = loader.get();
                            tryPut(queryOptions, queryOptionsHash, cards, res);
                            return res;
                        } else {
                            PositionOf positionOf = buildPositionOf((long) index, queryOptions);
                            PagedElements<Card> res = getCards(DaoQueryOptionsImpl.copyOf(queryOptions).withOffset(positionOf.getActualOffset()).withPositionOf(null, null).build(), userAccessPrivilegesHash, loader);
                            return res.withPositionOf(positionOf);
                        }
                    } else {
                        if (cards.size() >= min(queryOptions.getOffset() + queryOptions.getLimit(), cards.totalSize())) {
                            return paged(cards.elements().subList(toIntExact(min(queryOptions.getOffset(), cards.totalSize())), toIntExact(min(queryOptions.getOffset() + queryOptions.getLimit(), cards.totalSize()))), cards.totalSize());
                        } else {
                            PagedElements<Card> res = loader.get();
                            tryPut(queryOptions, queryOptionsHash, cards, res);
                            return res;
                        }
                    }
                } else {
                    if (cards.size() == cards.totalSize()) {
                        return cards;
                    } else {
                        PagedElements<Card> res = loader.get();
                        tryPut(queryOptions, queryOptionsHash, cards, res);
                        return res;
                    }
                }
            }
        }

        public void createCard(Card card) {
//            cardsByUserAccessPrivilegesHash.forEach((k, cards) -> {//TODO this is not actually corrected
//                List<Card> list = list(card).with(cards.elements());
//                Collections.sort(list, (a, b) -> b.getBeginDate().compareTo(a.getBeginDate()));//TODO improve this
//                cardsByUserAccessPrivilegesHash.put(k, paged(list, cards.totalSize() + 1));
//            });
            cardsByQueryOptionsHash.clear();//TODO improve this, apply filter in-memory and add card to lists where possible
        }

        public void updateCard(Card card) {//TODO write lock ??
            cardsByQueryOptionsHash.forEach((k, cards) -> {//TODO this is buggy, will bear incorrect results if update change record visibility or sort order; should fix it
                List<Card> list = cards.elements().stream().map(c -> c.getId() == (long) card.getId() ? card : c).collect(toImmutableList());
                cardsByQueryOptionsHash.put(k, paged(list, cards.totalSize()));
            });
        }

        public void deleteCard(long cardId) {//TODO write lock ??
            cardsByQueryOptionsHash.forEach((k, cards) -> {
                List<Card> list = list(cards.elements()).without(c -> equal(c.getId(), cardId));//TODO improve this
                if (list.size() < cards.size()) {
                    cardsByQueryOptionsHash.put(k, paged(list, cards.totalSize() - 1));
                }
            });
        }

        private void tryPut(DaoQueryOptions queryOptions, String queryOptionsHash, PagedElements<Card> currentCards, PagedElements<Card> newCards) {
            long offset = newCards.hasPositionOf() ? newCards.getPositionOf().getActualOffset() : queryOptions.getOffset();
            logger.debug("loaded cards = {} for options =< {} > offset = {}", newCards, queryOptionsHash, offset);
            if (offset <= currentCards.size() && offset + newCards.size() <= config.getCardCacheMaxRecordsPerClass()) {
                PagedElements<Card> cards = paged(list(currentCards.elements().subList(0, toIntExact(offset))).with(newCards.elements()), newCards.totalSize());//TODO more efficent list concat
                logger.debug("new stored cards = {}", cards);
                cardsByQueryOptionsHash.put(queryOptionsHash, cards);//TODO write lock ??
            } else {
                logger.debug("unable to cache records, skip cache update");
            }
            newCards.forEach(card -> {
                cardsById.put(card.getId(), card);//TODO write lock ??                
            });
        }

    }

    public static Comparator<Card> buildComparator(CmdbSorter sorter) {
        checkNotNull(sorter);
        if (sorter.isNoop()) {
            return (a, b) -> 0;
        } else {
            return (a, b) -> {
                for (SorterElement element : sorter.getElements()) {
                    Object one = a.get(element.getProperty()), two = b.get(element.getProperty());
                    int order = getOrder(one, two, a.getType().getAttribute(element.getProperty())) * element.getDirectionMultiplier();
                    if (order != 0) {
                        return order;
                    }
                }
                return 0;
            };
        }
    }

    public static int getOrder(@Nullable Object one, @Nullable Object two, Attribute attribute) {
        // null values sort as if larger than any non-null value (postgres default)
        if (one == null && two == null) {
            return 0;
        } else if (one == null) {
            return 1;
        } else if (two == null) {
            return -1;
        } else {
            return switch (attribute.getType().getName()) {
                case BOOLEAN, CHAR, DATE, DECIMAL, DOUBLE, FLOAT, INTEGER, INTERVAL, LONG, STRING, TEXT, LINK, TIME, TIMESTAMP ->
                    ((Comparable) one).compareTo(two);
                case FOREIGNKEY, REFERENCE, LOOKUP, FILE ->
                    nullToEmpty(((IdAndDescription) one).getDescription()).compareTo(nullToEmpty(((IdAndDescription) two).getDescription()));
                default ->
                    throw unsupported("unsupported ordering for attribute = {}", attribute);
            };
        }
    }

}
