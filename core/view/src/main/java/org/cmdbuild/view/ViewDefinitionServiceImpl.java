package org.cmdbuild.view;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.inject.Provider;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_PRINT;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_SEARCH;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_VIEWS_MODIFY;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_VIEWS_VIEW;
import static org.cmdbuild.auth.role.RolePrivilege.RP_VIEW_ALL_READ;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.cleanup.ViewType;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.view.ViewBase.ATTR_SHARED;
import static org.cmdbuild.view.ViewBase.ATTR_USER_ID;
import org.cmdbuild.view.join.inner.JoinViewQueryService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ViewDefinitionServiceImpl implements ViewDefinitionService {

    private final DaoService dao;
    private final OperationUserSupplier userStore;
    private final Provider<JoinViewQueryService> queryService;
    private final Holder<List<View>> sharedViewCache;

    public ViewDefinitionServiceImpl(DaoService dao, OperationUserSupplier userStore, CacheService cacheService, Provider<JoinViewQueryService> queryService) {
        this.dao = checkNotNull(dao);
        this.userStore = checkNotNull(userStore);
        this.queryService = checkNotNull(queryService);
        sharedViewCache = cacheService.newHolder("view_all");
    }

    private void invalidateCache() {
        sharedViewCache.invalidate();
    }

    @Override
    public List<View> getAllSharedViews() {
        return sharedViewCache.get(this::doGetAllSharedViews);
    }

    @Override
    public List<View> getNonSharedViewsForCurrentUser() {
        return dao.selectAll().from(ViewImpl.class).where(ATTR_SHARED, EQ, false).where(ATTR_USER_ID, EQ, userStore.getUser().getId()).asList();
    }

    @Override
    public List<View> getViewsForCurrentUser() {
        return list(getAllSharedViews()).withOnly(this::isAccessibleByCurrentUser).with(getNonSharedViewsForCurrentUser());
    }

    @Override
    public List<View> getActiveViewsForCurrentUser() {
        return getViewsForCurrentUser().stream().filter(this::isActive).filter(v -> v.getSourceClass() == null ? true : dao.getClasse(v.getSourceClass()).isActive()).collect(toList());
    }

    @Override
    public boolean isActiveAndUserAccessibleByName(String name) {
        return isAccessibleByCurrentUser(getSharedByName(name)) && isActive(getSharedByName(name));
    }

    @Override
    public boolean canPrint(View view) {
        OperationUser user = userStore.getUser();
        if (view.isShared()) {
            return user.hasPrivileges(p -> p.hasPrivileges(RP_ADMIN_VIEWS_VIEW) || (p.hasReadAccess(view) && userStore.getPrivileges().getPrivilegesForObject(view).getMinPrivilegesForAllRecords().getUiPrivileges().contains(GP_PRINT)));
        } else {
            return equal(view.getUserId(), user.getId());
        }
    }

    @Override
    public boolean canSearch(View view, boolean isSearchEnabled) {
        OperationUser user = userStore.getUser();

        if (view.isShared()) {
            return user.hasPrivileges(p -> p.hasPrivileges(RP_ADMIN_VIEWS_VIEW) || (p.hasReadAccess(view) && isSearchEnabled ? userStore.getPrivileges().getPrivilegesForObject(view).getMinPrivilegesForAllRecords().getUiPrivileges().contains(GP_SEARCH) : false));
        } else {
            return equal(view.getUserId(), user.getId());
        }
    }

    @Override
    public List<View> getForCurrentUserByType(ViewType type) {
        return getViewsForCurrentUser().stream().filter((v) -> v.isOfType(type)).collect(toList());
    }

    @Override
    public View getForCurrentUserById(long id) {
        return getViewsForCurrentUser().stream().filter((v) -> v.getId() == id).collect(onlyElement("view not found for id = %s", id));
    }

    @Override
    public View getSharedForCurrentUserByNameOrId(String nameOrId) {
        checkNotBlank(nameOrId);
        return getAllSharedViews().stream().filter(this::isAccessibleByCurrentUser).filter((v) -> v.isShared()
                ? (equal(v.getName(), nameOrId) || equal(toStringNotBlank(v.getId()), nameOrId))
                : equal(toStringNotBlank(v.getId()), nameOrId)).collect(onlyElement("view not found for nameOrId =< %s >", nameOrId));
    }

    @Override
    public View getSharedByName(String name) {
        checkNotBlank(name);
        return getAllSharedViews().stream().filter((v) -> equal(v.getName(), name)).collect(onlyElement("view not found for name =< %s >", name));
    }

    @Override
    public View createForCurrentUser(View view) {
        if (view.isShared()) {
            userStore.checkPrivileges(p -> p.hasPrivileges(RP_ADMIN_VIEWS_MODIFY));
        } else {
            view = ViewImpl.copyOf(view).withUserId(userStore.getUser().getId()).build();
        }
        return create(view);
    }

    @Override
    public View updateForCurrentUser(View view) {
        View current = view.isShared() ? getSharedByName(view.getName()) : getForCurrentUserById(view.getId());
        checkCanModify(current);
        view = ViewImpl.copyOf(view).withShared(current.isShared()).withUserId(current.getUserId()).withId(current.getId()).withName(current.getName()).build();
        validateConfig(view);
        view = dao.update(view);
        invalidateCache();
        return view;
    }

    @Override
    public View create(View view) {
        checkArgument(view.getId() == null);
        validateConfig(view);
        view = dao.create(view);
        invalidateCache();
        return view;
    }

    @Override
    public void delete(long id) {
        checkCanModify(getForCurrentUserById(id));
        dao.delete(ViewImpl.class, id);
        invalidateCache();
    }

    @Override
    public View getById(long id) {
        return getAllSharedViews().stream().filter(v -> equal(id, v.getId())).collect(onlyElement("view not found for id = %s", id));
    }

    private void checkCanModify(View view) {
        if (view.isShared()) {
            userStore.checkPrivileges(p -> p.hasPrivileges(RP_ADMIN_VIEWS_MODIFY) || p.hasWriteAccess(view));//TODO check this (modify)
        } else {
            userStore.checkPrivileges((u, p) -> p.hasPrivileges(RP_ADMIN_VIEWS_MODIFY) || equal(u.getId(), view.getUserId()));
        }
    }

    private boolean isAccessibleByCurrentUser(View view) {
        OperationUser user = userStore.getUser();
        if (view.isShared()) {
            return user.hasPrivileges(p -> p.hasPrivileges(RP_VIEW_ALL_READ) || p.hasReadAccess(view));
        } else {
            return equal(view.getUserId(), user.getId());
        }
    }

    private boolean isActive(View view) {
        return view.isActive() && (view.isOfType(ViewType.VT_FILTER) ? isClassActive(view.getSourceClass()) : true);
    }

    private List<View> doGetAllSharedViews() {
        return dao.selectAll().from(ViewImpl.class).where(ATTR_SHARED, EQ, true).asList();
    }

    private boolean isClassActive(String classId) {
        return dao.getClasse(classId).isActive();
    }

    private void validateConfig(View view) {
        switch (view.getType()) {
            case VT_JOIN ->
                queryService.get().validateViewConfig(view);
            //TODO validate others
        }
    }

}
