/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.view;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.isPaged;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.dao.utils.CmFilterUtils;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.springframework.stereotype.Component;
import static org.cmdbuild.cleanup.ViewType.VT_SQL;
import static org.cmdbuild.cleanup.ViewType.VT_FILTER;
import org.cmdbuild.view.join.inner.JoinViewQueryService;
import org.springframework.context.annotation.Primary;

@Component
@Primary
public class ViewAccessServiceImpl implements ViewAccessService {

    private final DaoService dao;
    private final JoinViewQueryService joinViewService;

    public ViewAccessServiceImpl(DaoService dao, JoinViewQueryService queryService) {
        this.dao = checkNotNull(dao);
        this.joinViewService = checkNotNull(queryService);
    }

    @Override
    public Card getCardById(View view, String cardId) {
        return switch (view.getType()) {
            case VT_FILTER ->
                dao.getCard(view.getSourceClass(), parseLong(cardId));//TODO apply filter   
            case VT_JOIN ->
                joinViewService.getCard(view, cardId);
            default ->
                throw unsupported("unsupported view type = %s", view.getType());
        };
    }

    @Override
    public Card createUserCard(View view, Map<String, Object> values) {
        return switch (view.getType()) {
            case VT_FILTER -> {
                Classe targetClass = dao.getClasse(view.getSourceClass());
                Card card = CardImpl.buildCard(targetClass, values);
                card = dao.create(card);//TODO check filter 
                yield card;
            }
            default ->
                throw unsupported("unsupported view type = %s", view.getType());
        };
    }

    @Override
    public Card updateUserCard(View view, long cardId, Map<String, Object> values) {
        return switch (view.getType()) {
            case VT_FILTER -> {
                Classe classe = dao.getClasse(view.getSourceClass());
                Card card = CardImpl.builder()
                        .withType(classe)
                        .withAttributes(values)
                        .withId(cardId)
                        .build();
                yield dao.update(card);//TODO check filter 
            }
            default ->
                throw unsupported("unsupported view type = %s", view.getType());
        };
    }

    @Override
    public void deleteUserCard(View view, long cardId) {
        switch (view.getType()) {
            case VT_FILTER ->
                dao.delete(view.getSourceClass(), cardId);//TODO check filter
            default ->
                throw unsupported("unsupported view type = %s", view.getType());
        }
    }

    @Override
    public PagedElements<Card> getCards(View view, DaoQueryOptions queryOptions) {//TODO user permission check
        return switch (view.getType()) {
            case VT_FILTER ->
                getCardsForFilterView(view, queryOptions);
            case VT_SQL ->
                getCardsForSqlView(view, queryOptions);
            case VT_JOIN ->
                joinViewService.getCards(view, queryOptions);
            default ->
                throw unsupported("unsupported view type = %s", view.getType());
        };
    }

    @Override
    public Card getCardForCurrentUser(View view, String cardId) {
        return getCardById(view, cardId);//TODO user permission check
//        switch (view.getType()) {
//            case VT_FILTER:
//                return dao.getCard(view.getSourceClass(), parseLong(cardId));//TODO check filter
//            case VT_JOIN:
//            //TODO
//            default:
//                throw unsupported("unsupported view type = %s", view.getType());
//        }
    }

    @Override
    public Collection<Attribute> getAttributesForView(View view) {
        return switch (view.getType()) {
            case VT_FILTER ->
                dao.getClasse(view.getSourceClass()).getAllAttributes();
            case VT_SQL ->
                dao.getFunctionByName(view.getSourceFunction()).getAllAttributes();
            case VT_JOIN ->
                joinViewService.getAttributesForView(view);
            default ->
                throw unsupported("unsupported view type = %s", view.getType());
        };
    }

    @Override
    public EntryType getEntryTypeForView(View view) {
        return switch (view.getType()) {
            case VT_FILTER ->
                dao.getClasse(view.getSourceClass());
            case VT_SQL ->
                dao.getFunctionByName(view.getSourceFunction());
            case VT_JOIN ->
                joinViewService.getEntryTypeForView(view);
            default ->
                throw unsupported("unsupported view type = %s", view.getType());
        };
    }

    private PagedElements<Card> getCardsForFilterView(View view, DaoQueryOptions queryOptions) {
        checkArgument(view.isOfType(VT_FILTER), "cannot get cards from view with type = %s", view.getType());
        CmdbFilter filterParam = queryOptions.getFilter();
//        CmdbSorter sorter = mapSorterAttributeNames(CmdbSorterUtils.fromNullableJson(sort)); TODO map
        CmdbSorter sorter = queryOptions.getSorter();
        long offset = queryOptions.getOffset();
        Long limit = queryOptions.getLimit();
        String classId = view.getSourceClass();
        CmdbFilter viewFilter = CmFilterUtils.parseFilter(view.getFilter());
        CmdbFilter filter = CmFilterUtils.merge(viewFilter, filterParam);
        //TODO duplicate code from CardWs.
        if (queryOptions.hasPositionOf()) {
            long rowNumber = dao.selectRowNumber().where(ATTR_ID, EQ, queryOptions.getPositionOf()).then()
                    .from(classId)
                    .orderBy(sorter)
                    .where(filter)
                    .build().getRowNumberOrNull();
            long positionInPage = rowNumber % limit;
            offset = rowNumber - positionInPage;
        }
        List<Card> cards = dao.selectAll()
                .from(classId)
                .orderBy(sorter)
                .where(filter)
                .paginate(offset, limit)
                .getCards();

        long total;
        if (isPaged(offset, limit)) {
            total = dao.selectCount()
                    .from(classId)
                    .where(filter)
                    .getCount();
        } else {
            total = cards.size();
        }
        return PagedElements.paged(cards, total);
    }

    private PagedElements<Card> getCardsForSqlView(View view, DaoQueryOptions queryOptions) {
        checkArgument(view.isOfType(VT_SQL), "cannot get records from view with type = %s", view.getType());
        CmdbFilter filterParam = queryOptions.getFilter();
//        CmdbSorter sorter = mapSorterAttributeNames(CmdbSorterUtils.fromNullableJson(sort)); TODO map
        CmdbSorter sorter = queryOptions.getSorter();
        long offset = queryOptions.getOffset();
        Long limit = queryOptions.getLimit();
        StoredFunction function = dao.getFunctionByName(view.getSourceFunction());
        //TODO duplicate code  
        if (queryOptions.hasPositionOf()) {
            long rowNumber = dao.selectRowNumber().where(ATTR_ID, EQ, queryOptions.hasPositionOf()).then()
                    .from(function)
                    .orderBy(sorter)
                    .where(filterParam)
                    .build().getRowNumberOrNull();
            long positionInPage = rowNumber % limit;
            offset = rowNumber - positionInPage;
        }
        List<Map<String, Object>> records = dao.selectAll()
                .from(function)
                .orderBy(sorter)
                .where(filterParam)
                .paginate(offset, limit)
                .run().stream().map(r -> r.asMap()).collect(toList());

        long total;
        if (isPaged(offset, limit)) {
            total = dao.selectCount()
                    .from(function)
                    .where(filterParam)
                    .getCount();
        } else {
            total = records.size();
        }
        Classe classe = getDummyClassForView(view);
        return PagedElements.paged(records, total).map(r -> CardImpl.buildCard(classe, r));
    }

    private Classe getDummyClassForView(View view) {
        return ClasseImpl.builder().withName(format("_view_%s", view.getName())).withAttributes(getAttributesForView(view)).build();
    }

}
