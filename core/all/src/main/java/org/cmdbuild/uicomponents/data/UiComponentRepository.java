package org.cmdbuild.uicomponents.data;

import jakarta.annotation.Nullable;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;

public interface UiComponentRepository {

    List<UiComponentData> getAll();

    UiComponentData create(UiComponentData customPage);

    UiComponentData update(UiComponentData customPage);

    void delete(long id);

    @Nullable
    UiComponentData getByTypeAndNameOrNull(UiComponentType type, String name);

    UiComponentData getById(long id);

    default List<UiComponentData> getAllByType(UiComponentType type) {
        checkNotNull(type);
        return getAll().stream().filter(equal(UiComponentData::getType, type)).collect(toList());
    }

    default UiComponentData getByTypeAndName(UiComponentType type, String name) {
        return checkNotNull(getByTypeAndNameOrNull(type, name), "ui component not found for type = %s name =< %s >", type, name);
    }
}
