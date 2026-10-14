/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.cardfilter.CardFilterAsDefaultForClass;
import org.cmdbuild.cardfilter.CardFilterAsDefaultForClassImpl;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.cardfilter.StoredFilter;
import org.cmdbuild.cardfilter.StoredFilterImpl.StoredFilterImplBuilder;
import org.cmdbuild.service.rest.v4.model.WsDefaultStoredFilter;
import org.cmdbuild.service.rest.v4.model.WsFilterData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.google.common.base.Objects.equal;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Component
public class ClassFilterWsCommand {

    private final CardFilterService cardFilterService;
    private final OperationUserSupplier operationUserSupplier;

    public ClassFilterWsCommand(CardFilterService cardFilterService, OperationUserSupplier operationUserSupplier) {
        this.cardFilterService = checkNotNull(cardFilterService);
        this.operationUserSupplier = checkNotNull(operationUserSupplier);
    }

    public List<StoredFilter> doReadAll(String classId, Boolean sharedOnly) {
        List<StoredFilter> listStoredFilter;
        if (equal(classId, "_ANY")) {
            listStoredFilter = cardFilterService.readAllSharedFilters();
        } else if (sharedOnly) {
            listStoredFilter = cardFilterService.readSharedForCurrentUser(classId);
        } else {
            listStoredFilter = cardFilterService.readAllForCurrentUser(classId);
        }
        return listStoredFilter;
    }

    public StoredFilter doRead(Long filterId) {
        return cardFilterService.getById(filterId);
    }

    public StoredFilter doCreate(WsFilterData element) {
        return cardFilterService.create(element.toCardFilter().accept(setCurrentUserForNonSharedFiltersVisitor(element)).build());
    }

    public StoredFilter doUpdate(Long filterId, WsFilterData element) {
        return cardFilterService.update(element.toCardFilter().withId(filterId).accept(setCurrentUserForNonSharedFiltersVisitor(element)).build());
    }

    public Stream<CardFilterAsDefaultForClass> doGetDefaultForRoles(Long filterId) {
        return cardFilterService.getDefaultFiltersForFilter(filterId).stream().filter(f -> equal(f.getDefaultForClass(), f.getFilter().getOwnerName()));
    }

    public void doDelete(Long filterId) {
        cardFilterService.delete(filterId);
    }

    public Stream<CardFilterAsDefaultForClass> doUpdateWithPost(Long filterId, List<WsDefaultStoredFilter> roles) {
        StoredFilter filter = cardFilterService.getById(filterId);
        List<CardFilterAsDefaultForClass> listCardFilter = roles.stream().map(r -> new CardFilterAsDefaultForClassImpl(filter, filter.getOwnerName(), r.getId())).collect(toList());
        cardFilterService.setDefaultFiltersForFilterWithMatchingClass(filter.getId(), listCardFilter);
        return doGetDefaultForRoles(filterId);
    }

    private Consumer<StoredFilterImplBuilder> setCurrentUserForNonSharedFiltersVisitor(WsFilterData data) {
        return (b) -> {
            if (!data.isShared()) {
                b.withUserId(operationUserSupplier.getUser().getLoginUser().getId());
            }
        };
    }
}
