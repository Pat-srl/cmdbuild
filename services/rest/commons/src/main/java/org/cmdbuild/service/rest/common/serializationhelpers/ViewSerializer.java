/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.cmdbuild.auth.role.Role;
import static org.cmdbuild.cleanup.ViewType.VT_JOIN;
import org.cmdbuild.contextmenu.ContextMenuItem;
import static org.cmdbuild.dao.utils.CmFilterUtils.serializeFilter;
import static org.cmdbuild.dao.utils.CmSorterUtils.serializeSorter;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.view.View;
import org.cmdbuild.view.join.JoinViewConfig;
import org.springframework.stereotype.Component;

import static org.cmdbuild.auth.grant.GrantData.GDCP_PRINT;
import static org.cmdbuild.auth.grant.GrantData.GDCP_SEARCH;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.config.UiConfiguration;
import org.cmdbuild.contextmenu.ContextMenuService;
import org.cmdbuild.formstructure.FormStructureService;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.cmdbuild.view.ViewService;

/**
 *
 * @author afelice
 */
@Component
public class ViewSerializer {

    private final ViewService viewService;
    private final ObjectTranslationService translationService;
    private final RoleRepository roleRepository;
    private final FormStructureService formStructureService;
    private final OperationUserSupplier userStore;
    private final ContextMenuService contextMenuService;
    private final ContextMenuSerializationHelper contextMenuSerializationHelper;
    private final UiConfiguration uiConfiguration;

    public ViewSerializer(
            ViewService viewService,
            ObjectTranslationService translationService,
            RoleRepository roleRepository,
            FormStructureService formStructureService,
            OperationUserSupplier userStore,
            ContextMenuService contextMenuService,
            ContextMenuSerializationHelper contextMenuSerializationHelper,
            UiConfiguration uiConfiguration) {
        this.viewService = checkNotNull(viewService);
        this.translationService = checkNotNull(translationService);
        this.roleRepository = checkNotNull(roleRepository);
        this.formStructureService = checkNotNull(formStructureService);
        this.userStore = checkNotNull(userStore);
        this.contextMenuService = checkNotNull(contextMenuService);
        this.contextMenuSerializationHelper = checkNotNull(contextMenuSerializationHelper);
        this.uiConfiguration = checkNotNull(uiConfiguration);
    }

    /**
     * Was in <code>ViewWs</code>.
     *
     * @param view
     * @return
     */
    public FluentMap<String, Object> serializeView(View view) {
        FluentMap<String, Object> result = map(
                "_id", view.isShared() ? view.getName() : view.getId(),//TODO check constraint, access, etc
                "name", view.getName(),
                "type", serializeEnum(view.getType()).toUpperCase(),
                "description", view.getDescription(),
                "_description_translation", translationService.translateViewDescription(view.getName(), view.getDescription()),
                "_description_plural_translation", translationService.translateViewDescriptionPlural(view.getName(), view.getDescription()),
                "filter", view.getFilter(),
                "sourceClassName", view.getSourceClass(),
                "sourceFunction", view.getSourceFunction(),
                "active", view.isActive(),
                "shared", view.isShared(),
                "icon_font", view.getIconFont()
        );

        return result.accept(m -> {
            Role currentUserRole = roleRepository.getByNameOrIdOrNull(userStore.getCurrentGroup());

            m.put(format("_can_%s", GDCP_PRINT), viewService.canPrint(view));
            m.put(format("_can_%s", GDCP_SEARCH), viewService.canSearch(view, currentUserRole != null && currentUserRole.getConfig().getFullTextSearch() != null ? currentUserRole.getConfig().getFullTextSearch() : uiConfiguration.isFullTextSearchEnabled()));
        });
    }

    /**
     * Was in <code>ViewWs</code>.
     *
     * @param view
     * @return
     */
    public FluentMap<String, Object> serializeDetailedView(View view) {
        return serializeView(view).accept(m -> {
            if (view.isOfType(VT_JOIN)) {
                AtomicInteger attributeGroupIndex = new AtomicInteger(0);
                JoinViewConfig c = view.getJoinConfigNotNull();
                List<ContextMenuItem> contextMenuItems = contextMenuService.getContextMenuItemsForView(view);
                m.put(
                        "masterClass", c.getMasterClass(),
                        "masterClassAlias", c.getMasterClassAlias(),
                        "sorter", fromJson(serializeSorter(c.getSorter()), JsonNode.class),
                        "filter", serializeFilter(c.getFilter()),
                        "contextMenuItems", contextMenuItems == null ? null : contextMenuSerializationHelper.contextMenuItemsToResponse(contextMenuItems, view.getName()),
                        "privilegeMode", serializeEnum(c.getPrivilegeMode()),
                        "join", list(c.getJoinElements()).map(j -> map(
                        "source", j.getSource(),
                        "domain", j.getDomain(),
                        "targetType", j.getTargetType(),
                        "domainAlias", j.getDomainAlias(),
                        "targetAlias", j.getTargetAlias(),
                        "direction", serializeEnum(j.getDirection()),
                        "joinType", serializeEnum(j.getJoinType())
                )),
                        "attributes", list(c.getAttributes()).map(a -> map(
                        "expr", a.getExpr(),
                        "name", a.getName(),
                        "description", a.getDescription(),
                        "_description_translation", translationService.translateViewAttributeDescription(view.getName(), a.getName(), a.getDescription()),
                        "group", a.getGroup(),
                        "showInGrid", a.getShowInGrid(),
                        "showInReducedGrid", a.getShowInReducedGrid()
                )),
                        "attributeGroups", list(c.getAttributeGroups()).map(g -> map(
                        "name", g.getName(),
                        "_id", g.getName(),
                        "index", attributeGroupIndex.incrementAndGet(),
                        "defaultDisplayMode", g.getDefaultDisplayMode(),
                        "description", g.getDescription(),
                        "_description_translation", translationService.translateViewAttributeGroupDescription(view.getName(), g.getName(), g.getDescription())
                )));
            }
            Optional.ofNullable(formStructureService.getFormForViewOrNull(view)).ifPresent(f -> {
                m.put("formStructure", fromJson(f.getData(), JsonNode.class));
            });
        });
    }

}
