/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.cardfilter.SharedCardFilterRepository;
import org.cmdbuild.contextmenu.ContextMenuItem;
import org.cmdbuild.contextmenu.ContextMenuService;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dashboard.DashboardData;
import org.cmdbuild.dashboard.DashboardRepository;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.homepage.HomepageData;
import org.cmdbuild.homepage.HomepageRepository;
import org.cmdbuild.lookup.LookupRepository;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.menu.MenuService;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.uicomponents.contextmenu.ContextMenuComponentService;
import org.cmdbuild.uicomponents.custompage.CustomPageService;
import org.cmdbuild.view.View;
import org.cmdbuild.view.ViewService;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.widget.model.WidgetData;
import org.cmdbuild.workflow.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static org.cmdbuild.translation.TranslationUtils.*;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.stream;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Component
public class TranslationObjectsServiceImpl implements TranslationObjectsService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<String> SYSTEM_ATTRIBUTES = list("Id", "IdTenant", "IdClass", "Notes", "FlowStatus");

    private final ClasseRepository classeRepository;
    private final DomainRepository domainRepository;
    private final SharedCardFilterRepository filterRepository;
    private final LookupRepository lookupRepository;
    private final WidgetService widgetService;
    private final MenuService menuService;
    private final ViewService viewService;
    private final ReportService reportService;
    private final DashboardRepository dashboardRepository;
    private final RoleRepository roleRepository;
    private final WorkflowService workflowService;
    private final ContextMenuService contextMenuService;
    private final ContextMenuComponentService contextMenuComponentService;
    private final CustomPageService customPageService;
    private final EmailTemplateService templateService;
    private final HomepageRepository homepageRepository;

    public TranslationObjectsServiceImpl(
            ClasseRepository classeRepository,
            DomainRepository domainRepository,
            SharedCardFilterRepository filterRepository,
            LookupRepository lookupRepository,
            WidgetService widgetService,
            MenuService menuService,
            ViewService viewService,
            ReportService reportService,
            DashboardRepository dashboardRepository,
            RoleRepository roleRepository,
            WorkflowService workflowService,
            ContextMenuService contextMenuService,
            ContextMenuComponentService contextMenuComponentService,
            CustomPageService customPageService,
            EmailTemplateService templateService,
            HomepageRepository homepageRepository
    ) {
        this.classeRepository = checkNotNull(classeRepository);
        this.domainRepository = checkNotNull(domainRepository);
        this.filterRepository = checkNotNull(filterRepository);
        this.lookupRepository = checkNotNull(lookupRepository);
        this.widgetService = checkNotNull(widgetService);
        this.menuService = checkNotNull(menuService);
        this.viewService = checkNotNull(viewService);
        this.reportService = checkNotNull(reportService);
        this.dashboardRepository = checkNotNull(dashboardRepository);
        this.roleRepository = checkNotNull(roleRepository);
        this.workflowService = checkNotNull(workflowService);
        this.contextMenuService = checkNotNull(contextMenuService);
        this.contextMenuComponentService = checkNotNull(contextMenuComponentService);
        this.customPageService = checkNotNull(customPageService);
        this.templateService = checkNotNull(templateService);
        this.homepageRepository = checkNotNull(homepageRepository);
    }

    @Override
    public List<TranslationObject> getAllTranslableObjectsAndDefaults() {
        List<TranslationObject> list = list();
//        contextMenuComponentService.getAll().stream().filter(UiComponentInfo::isActive).forEach(e -> {
//            list.add(new TranslationObjectImpl());
//        });
        customPageService.getAll().stream().filter(c -> c.isActive()).forEach(c -> {
            list.add(new TranslationObjectImpl(customPageDescriptionTranslationCode(c.getName()), c.getDescription()));
        });
        classeRepository.getAllClasses().stream().filter(Classe::isActive).filter(Classe::hasServiceReadPermission).forEach(c -> {
            contextMenuService.getContextMenuItemsForClass(c).stream().filter(ContextMenuItem::isActive).forEach(e -> {
                if (e.getLabel() != null && !e.getLabel().isBlank()) {
                    list.add(new TranslationObjectImpl(contextMenuLabelTranslationCode(c.getName(), e.getLabel()), e.getLabel()));
                }
            });
            list.add(new TranslationObjectImpl(classDescriptionTranslationCode(c), c.getDescription()));
            if (c.getMetadata().getHelpMessage() != null) {
                list.add(new TranslationObjectImpl(classHelpTranslationCode(c), c.getMetadata().getHelpMessage()));
            }
            c.getServiceAttributes().stream().filter(Attribute::isActive).filter(Attribute::hasUiReadPermission).filter(a -> !SYSTEM_ATTRIBUTES.contains(a.getName()))
                    .map(a -> new TranslationObjectImpl(attributeDescriptionTranslationCode(a), a.getDescription())).forEach(list::add);
            c.getAttributeGroups().stream().map(g -> new TranslationObjectImpl(attributeGroupDescriptionTranslationCode(c, g), g.getDescription())).forEach(list::add);
        });
        if (workflowService.isWorkflowEnabled()) {
            workflowService.getAllProcessClasses().stream().filter(p -> p.hasPlan()).forEach(p -> {
                workflowService.getTaskDefinitions(p.getName()).stream().forEach(t -> {
                    list.add(new TranslationObjectImpl(taskDescriptionTranslationCode(p.getName(), t.getId()), t.getDescription()));
                    if (!t.getInstructions().isBlank()) {
                        list.add(new TranslationObjectImpl(taskInstructionsTranslationCode(p.getName(), t.getId()), t.getInstructions()));
                    }
                    t.getWidgets().forEach(w -> list.add(new TranslationObjectImpl(workflowWidgetDescriptionTranslationCode(p.getName(), t.getId(), w.getId()), w.getLabel().replaceAll("\"", ""))));
                });
            });
        }
        domainRepository.getAllDomains().stream().filter(Domain::isActive).filter(Domain::hasServiceReadPermission).forEach(d -> {
            list.add(new TranslationObjectImpl(domainDirectDescriptionTranslationCode(d.getName()), d.getDirectDescription()));
            list.add(new TranslationObjectImpl(domainInverseDescriptionTranslationCode(d.getName()), d.getInverseDescription()));
            list.add(new TranslationObjectImpl(domainMasterDetailDescriptionTranslationCode(d.getName()), d.getMasterDetailDescription()));
            d.getServiceAttributes().stream().filter(Attribute::isActive).filter(Attribute::hasServiceReadPermission).map(a -> new TranslationObjectImpl(attributeDescriptionTranslationCode(a), a.getDescription())).forEach(list::add);
            d.getAttributeGroups().stream().map(g -> new TranslationObjectImpl(attributeGroupDescriptionTranslationCode(d, g), g.getDescription())).forEach(list::add);
        });
        filterRepository.getAllSharedFilters().stream().map(f -> new TranslationObjectImpl(filterDescriptionTranslationCode(f.getOwnerName(), f.getName()), f.getDescription())).forEach(list::add);
        lookupRepository.getAll().stream().filter(LookupValue::isActive).map(l -> new TranslationObjectImpl(lookupDescriptionTranslationCode(l.getType().getName(), l.getCode()), l.getDescription())).forEach(list::add);
        widgetService.getAllWidgets().stream().filter(WidgetData::isActive).map(w -> new TranslationObjectImpl(classWidgetDescriptionTranslationCode(w.getOwner(), w.getId()), w.getLabel())).forEach(list::add);
        dashboardRepository.getAll().stream().filter(DashboardData::isActive).forEach(d -> {
            list.add(new TranslationObjectImpl(dashboardDescriptionTranslationCode(d.getCode()), d.getDescription()));
            //TODO make this better
            stream(fromJson(d.getConfig(), JsonNode.class).get("components")).map(ObjectNode.class::cast).forEach(c -> {
                if (c.hasNonNull("description")) {
                    list.add(new TranslationObjectImpl(dashboardComponentDescriptionTranslationCode(d.getCode(), c.get("_id").asText()), c.get("description").asText()));
                }
                if (c.hasNonNull("valueAxisLabel")) {
                    list.add(new TranslationObjectImpl(dashboardComponentValueAxisLabelTranslationCode(d.getCode(), c.get("_id").asText()), c.get("valueAxisLabel").asText()));
                }
                if (c.hasNonNull("categoryAxisLabel")) {
                    list.add(new TranslationObjectImpl(dashboardComponentCategoryAxisLabelTranslationCode(d.getCode(), c.get("_id").asText()), c.get("categoryAxisLabel").asText()));
                }
                if (c.hasNonNull("dataSourceParameters")) {
                    ArrayNode dataSourceParameters = (ArrayNode) c.get("dataSourceParameters");
                    int i = 0;
                    for (JsonNode dataSourceParameter : dataSourceParameters) {
                        if (dataSourceParameter.hasNonNull("name")) {
                            list.add(new TranslationObjectImpl(dashboardComponentDataSourceParameterTranslationCode(d.getCode(), c.get("_id").asText(), Integer.toString(i++)), dataSourceParameter.get("name").asText()));
                        }
                    }
                }
            });
        });
        homepageRepository.getAll().stream().filter(HomepageData::isActive).forEach(d -> {
            list.add(new TranslationObjectImpl(homepageDescriptionTranslationCode(d.getCode()), d.getDescription()));
            //TODO make this better
            stream(fromJson(d.getConfig(), JsonNode.class).get("components")).map(ObjectNode.class::cast).forEach(c -> {
                if (c.hasNonNull("description")) {
                    list.add(new TranslationObjectImpl(homepageComponentDescriptionTranslationCode(d.getCode(), c.get("_id").asText()), c.get("description").asText()));
                }
                if (c.hasNonNull("valueAxisLabel")) {
                    list.add(new TranslationObjectImpl(homepageComponentValueAxisLabelTranslationCode(d.getCode(), c.get("_id").asText()), c.get("valueAxisLabel").asText()));
                }
                if (c.hasNonNull("categoryAxisLabel")) {
                    list.add(new TranslationObjectImpl(homepageComponentCategoryAxisLabelTranslationCode(d.getCode(), c.get("_id").asText()), c.get("categoryAxisLabel").asText()));
                }
                if (c.hasNonNull("dataSourceParameters")) {
                    ArrayNode dataSourceParameters = (ArrayNode) c.get("dataSourceParameters");
                    int i = 0;
                    for (JsonNode dataSourceParameter : dataSourceParameters) {
                        if (dataSourceParameter.hasNonNull("name")) {
                            list.add(new TranslationObjectImpl(homepageComponentDataSourceParameterTranslationCode(d.getCode(), c.get("_id").asText(), Integer.toString(i++)), dataSourceParameter.get("name").asText()));
                        }
                    }
                }
            });
        });
        menuService.getAllMenus().forEach(m -> {
            m.getRootNode().getDescendantsAndSelf().forEach(e -> {
                list.add(new TranslationObjectImpl(menuitemDescriptionTranslationCode(e.getCode(), m.getCode()), e.getDescription()));
            });
        });
        viewService.getAllSharedViews().stream().filter(View::isActive).map(v -> new TranslationObjectImpl(viewDescriptionTranslationCode(v.getName()), v.getDescription())).forEach(list::add);
        reportService.getAll().stream().filter(ReportInfo::isActive).forEach(r -> {
            list.add(new TranslationObjectImpl(reportDescriptionTranslationCode(r.getCode()), r.getDescription()));
            reportService.getParamsById(r.getId()).stream().map(a -> new TranslationObjectImpl(reportAttributeDescriptionTranslationCode(r.getCode(), a.getName()), a.getDescription())).forEach(list::add);
        });
        roleRepository.getAllGroups().stream().map(r -> new TranslationObjectImpl(roleDescriptionTranslationCode(r.getName()), r.getDescription())).forEach(list::add);
        templateService.getAll().stream().flatMap(t -> list(new TranslationObjectImpl(notificationTemplateSubjectTranslationCode(t.getCode()), t.getSubject()), new TranslationObjectImpl(notificationTemplateBodyTranslationCode(t.getCode()), t.getContent())).stream()).forEach(list::add);
        return list;
    }

    private static class TranslationObjectImpl implements TranslationObject {

        private final String code;
        private final String defaultValue;

        public TranslationObjectImpl(String code, @Nullable String defaultValue) {
            this.code = checkNotBlank(code);
            this.defaultValue = nullToEmpty(defaultValue);
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getDefaultValue() {
            return defaultValue;
        }

        @Override
        public String toString() {
            return "TranslationObjectImpl{" + "code=" + code + ", defaultValue=" + defaultValue + '}';
        }

    }

}
