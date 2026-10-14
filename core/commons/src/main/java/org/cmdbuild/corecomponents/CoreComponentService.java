/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.corecomponents;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public interface CoreComponentService {

    @Nullable
    CoreComponent getComponentOrNull(String code);

    List<CoreComponent> getComponents();

    CoreComponent createComponent(CoreComponent component);

    CoreComponent updateComponent(CoreComponent component);

    void deleteComponent(String code);

    default CoreComponent getComponent(String code) {
        return checkNotNull(getComponentOrNull(code), "component not found for code =< %s >", code);
    }

    default List<CoreComponent> getComponentsByType(CoreComponentType type) {
        checkNotNull(type);
        return list(getComponents()).filter(c -> equal(c.getType(), type));
    }

    default List<CoreComponent> getActiveComponentsByType(CoreComponentType type) {
        return list(getComponentsByType(type)).filter(CoreComponent::isActive);
    }

    default CoreComponent getActiveComponent(String code) {
        CoreComponent component = getComponent(code);
        checkArgument(component.isActive(), "no active component found for code =< %s >", code);
        return component;
    }

}
