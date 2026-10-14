package org.cmdbuild.view;

import static com.google.common.base.Objects.equal;
import java.util.List;
import org.cmdbuild.cleanup.ViewType;
import org.cmdbuild.view.View;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;

public interface ViewDefinitionService {

    List<View> getAllSharedViews();

    View getSharedByName(String name);

    List<View> getNonSharedViewsForCurrentUser();

    List<View> getViewsForCurrentUser();

    List<View> getActiveViewsForCurrentUser();

    List<View> getForCurrentUserByType(ViewType type);

    View getById(long id);

    View getForCurrentUserById(long id);

    View getSharedForCurrentUserByNameOrId(String nameOrId);

    View createForCurrentUser(View view);

    View updateForCurrentUser(View view);

    View create(View view);

    void delete(long id);

    boolean isActiveAndUserAccessibleByName(String name);

    boolean canPrint(View view);

    boolean canSearch(View view, boolean isSearchEnabled);

    default View getForCurrentUserByNameOrId(String nameOrId) {
        checkNotBlank(nameOrId);
        return getViewsForCurrentUser().stream().filter(v -> equal(nameOrId, toStringNotBlank(v.getId())) || equal(nameOrId, v.getName())).collect(onlyElement("view not found for name or id =< %s >", nameOrId));
    }

}
