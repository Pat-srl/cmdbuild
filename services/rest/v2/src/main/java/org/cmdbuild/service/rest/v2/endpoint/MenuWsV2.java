package org.cmdbuild.service.rest.v2.endpoint;

import com.google.common.base.Splitter;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dashboard.DashboardService;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.menu.Menu;
import static org.cmdbuild.menu.MenuConstants.MENU_ITEM_TYPE_SERIALIZATION_MAP;
import org.cmdbuild.menu.MenuItemType;
import org.cmdbuild.menu.MenuService;
import org.cmdbuild.menu.MenuTreeNode;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.translation.TranslationService;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmStringUtils.emptyToNull;
import org.cmdbuild.view.ViewService;

@Path("menu/")
@Produces(APPLICATION_JSON)
public class MenuWsV2 {

    private final DaoService dao;
    private final MenuService menuService;
    private final ReportService reportService;
    private final DashboardService dashboardService;
    private final ViewService viewService;
    private final GisService gisService;
    private final TranslationService translationService;

    private AtomicInteger index;

    public MenuWsV2(DaoService dao, MenuService menuService, ReportService reportService, DashboardService dashboardService, ViewService viewService, GisService gisService, TranslationService translationService) {
        this.dao = checkNotNull(dao);
        this.menuService = checkNotNull(menuService);
        this.reportService = checkNotNull(reportService);
        this.dashboardService = checkNotNull(dashboardService);
        this.viewService = checkNotNull(viewService);
        this.gisService = checkNotNull(gisService);
        this.translationService = checkNotNull(translationService);
    }

    @GET
    @Path(EMPTY)
    public Object read() {
        index = new AtomicInteger(-1);
        Menu menuForCurrentUser = menuService.getMenuForCurrentUser();
        return map("success", true, "data", serializeMenuTree(menuForCurrentUser), "meta", map());
    }

    public FluentMap serializeMenuTree(Menu menu) {
        return serializeMenuItemAndChilds(menu, menu.getRootNode());
    }

    private FluentMap serializeMenuItemAndChilds(Menu menu, MenuTreeNode item) {
        return serializeMenuItem(menu, item).with("index", index.incrementAndGet()).with("children", item.getChildren().stream().map((i) -> serializeMenuItemAndChilds(menu, i).with("index", index.incrementAndGet())).collect(toList()));
    }

    private FluentMap serializeMenuItem(Menu menu, MenuTreeNode item) {
        return map(
                "specificTypeValues", "",
                "objectDescription", item.getActualDescription(),
                "_objectDescription_translation", item.hasOwnDescription() ? translationService.translateMenuitemDescription(item.getCode(), menu.getCode(), item.getDescription()) : getTargetDescriptionTranslation(item.getType(), item.getTarget(), item.getTargetDescription()),
                "menuType", checkNotNull(MENU_ITEM_TYPE_SERIALIZATION_MAP.get(item.getType())),
                "referencedElementId", emptyToNull(item.getTarget()) == null ? 0 : item.getTarget(),
                "uuid", item.getCode()
        ).skipNullValues().with("referencedClassName", emptyToNull(item.getTarget()), "objectType", emptyToNull(item.getTarget()))
                .accept(e -> {
                    if (MENU_ITEM_TYPE_SERIALIZATION_MAP.get(item.getType()).contains("report")) {
                        e.put("objectId", reportService.getByCode(item.getTarget()).getId());
                    } else if (MENU_ITEM_TYPE_SERIALIZATION_MAP.get(item.getType()).equals("dashboard")) {
                        e.put("objectId", dashboardService.getForUserByIdOrCode(item.getTarget()).getId());
                    } else if (MENU_ITEM_TYPE_SERIALIZATION_MAP.get(item.getType()).equals("view")) {
                        e.put("objectId", viewService.getForCurrentUserByNameOrId(item.getTarget()).getId());
                    }
                }).then();
    }

    @Nullable
    private String getTargetDescriptionTranslation(MenuItemType type, String code, @Nullable String description) {
        switch (type) {
            case CLASS:
            case PROCESS:
                return translationService.translateClassDescription(dao.getClasse(code));
            case CUSTOM_PAGE:
                return translationService.translateCustomPageDesciption(code, description);
            case DASHBOARD:
                return translationService.translateDashboardDescription(code, description);
            case NAVTREE:
                return translationService.translateNavtreeDescription(code, description);
            case REPORT_CSV:
            case REPORT_ODT:
            case REPORT_PDF:
            case REPORT_RTF:
            case REPORT_XML:
                return translationService.translateReportDescription(code, description);
            case VIEW:
                return translationService.translateViewDescription(code, description);
            case GEOATTRIBUTE:
                return translationService.translateGisAttributeDescription(gisService.getGisAttributeWithCurrentUserByClassAndNameOrId(Splitter.on(".").splitToList(code).get(0), Splitter.on(".").splitToList(code).get(1)));
            default:
                return description;
        }
    }

}
