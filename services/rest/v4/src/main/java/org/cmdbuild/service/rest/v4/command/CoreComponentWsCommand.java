/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import org.cmdbuild.corecomponents.CoreComponent;
import org.cmdbuild.corecomponents.CoreComponentService;
import org.cmdbuild.corecomponents.CoreComponentType;
import org.cmdbuild.service.rest.v4.model.WsCoreComponentData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Component
public class CoreComponentWsCommand {

    private final CoreComponentService coreComponentService;

    public CoreComponentWsCommand(CoreComponentService coreComponentService) {
        this.coreComponentService = checkNotNull(coreComponentService);
    }

    public CoreComponent doGet(String code, Function<String, CoreComponent> function) {
        return function.apply(code);
    }

    public List<CoreComponent> doListByType(CoreComponentType type) {
        return coreComponentService.getComponentsByType(type);
    }

    public CoreComponent doCreate(CoreComponentType type, WsCoreComponentData data) {
        return coreComponentService.createComponent(data.toCoreComponent().withType(type).build());
    }

    public CoreComponent doUpdate(String code, WsCoreComponentData data) {
        CoreComponent component = coreComponentService.getComponent(code);
        component = data.toCoreComponent().withType(component.getType()).withCode(component.getCode()).build();
        return coreComponentService.updateComponent(component);
    }

    public void doDelete(String code) {
        coreComponentService.deleteComponent(code);
    }
}
