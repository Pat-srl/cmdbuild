/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.view;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.cmdbuild.cleanup.ViewType;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.EntryType;
import org.springframework.stereotype.Component;

@Component
public class ViewServiceImpl implements ViewService {

    private final ViewAccessService viewAccessService;
    private final ViewDefinitionService viewDefinitionService;

    public ViewServiceImpl(ViewAccessService viewAccessService, ViewDefinitionService viewDefinitionService) {
        this.viewAccessService = checkNotNull(viewAccessService);
        this.viewDefinitionService = checkNotNull(viewDefinitionService);
    }

    @Override
    public View getById(long id) {
        return viewDefinitionService.getById(id);
    }

    @Override
    public Collection<Attribute> getAttributesForView(View view) {
        return viewAccessService.getAttributesForView(view);
    }

    @Override
    public EntryType getEntryTypeForView(View view) {
        return viewAccessService.getEntryTypeForView(view);
    }

    @Override
    public List<View> getAllSharedViews() {
        return viewDefinitionService.getAllSharedViews();
    }

    @Override
    public View getSharedByName(String name) {
        return viewDefinitionService.getSharedByName(name);
    }

    @Override
    public List<View> getNonSharedViewsForCurrentUser() {
        return viewDefinitionService.getNonSharedViewsForCurrentUser();
    }

    @Override
    public List<View> getViewsForCurrentUser() {
        return viewDefinitionService.getViewsForCurrentUser();
    }

    @Override
    public List<View> getForCurrentUserByType(ViewType type) {
        return viewDefinitionService.getForCurrentUserByType(type);
    }

    @Override
    public View getForCurrentUserById(long id) {
        return viewDefinitionService.getForCurrentUserById(id);
    }

    @Override
    public View getSharedForCurrentUserByNameOrId(String name) {
        return viewDefinitionService.getSharedForCurrentUserByNameOrId(name);
    }

    @Override
    public View create(View view) {
        return viewDefinitionService.create(view);
    }

    @Override
    public View createForCurrentUser(View view) {
        return viewDefinitionService.createForCurrentUser(view);
    }

    @Override
    public View updateForCurrentUser(View view) {
        return viewDefinitionService.updateForCurrentUser(view);
    }

    @Override
    public void delete(long id) {
        viewDefinitionService.delete(id);
    }

    @Override
    public boolean isActiveAndUserAccessibleByName(String name) {
        return viewDefinitionService.isActiveAndUserAccessibleByName(name);
    }

    @Override
    public boolean canPrint(View view) {
        return viewDefinitionService.canPrint(view);
    }

    @Override
    public boolean canSearch(View view, boolean isSearchEnabled) {
        return viewDefinitionService.canSearch(view, isSearchEnabled);
    }

    @Override
    public List<View> getActiveViewsForCurrentUser() {
        return viewDefinitionService.getActiveViewsForCurrentUser();
    }

    @Override
    public View getForCurrentUserByNameOrId(String viewId) {
        return viewDefinitionService.getForCurrentUserByNameOrId(viewId);
    }

    @Override
    public Card getCardById(View view, String cardId) {
        return viewAccessService.getCardById(view, cardId);
    }

    @Override
    public PagedElements<Card> getCards(View view, DaoQueryOptions queryOptions) {
        return viewAccessService.getCards(view, queryOptions);
    }

    @Override
    public Card getCardForCurrentUser(View view, String cardId) {
        return viewAccessService.getCardForCurrentUser(view, cardId);
    }

    @Override
    public Card updateUserCard(View view, long cardId, Map<String, Object> values) {
        return viewAccessService.updateUserCard(view, cardId, values);
    }

    @Override
    public Card createUserCard(View view, Map<String, Object> values) {
        return viewAccessService.createUserCard(view, values);
    }

    @Override
    public void deleteUserCard(View view, long cardId) {
        viewAccessService.deleteUserCard(view, cardId);
    }

}
