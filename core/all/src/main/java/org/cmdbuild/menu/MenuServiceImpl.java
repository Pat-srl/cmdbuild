package org.cmdbuild.menu;

import com.google.common.base.Splitter;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.user.OperationUserStore;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dashboard.DashboardService;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.homepage.HomepageService;
import org.cmdbuild.navtree.NavTreeService;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.custompage.CustomPageService;
import org.cmdbuild.view.ViewDefinitionService;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.menu.MenuConstants.DEFAULT_MENU_GROUP_NAME;
import static org.cmdbuild.menu.MenuItemType.ROOT;
import static org.cmdbuild.ui.TargetDevice.TD_DEFAULT;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNullOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.notNull;

@Component
public class MenuServiceImpl implements MenuService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final MenuRepository menuRepository;
    private final ViewDefinitionService viewService;
    private final CustomPageService customPageService;
    private final OperationUserStore userStore;
    private final ReportService reportService;
    private final UserClassService userClassService;
    private final DashboardService dashboardService;
    private final NavTreeService navTreeService;
    private final WorkflowConfiguration workflowConfiguration;
    private final GisService gisService;
    private final HomepageService homepageService;

    public MenuServiceImpl(
            DaoService dao,
            MenuRepository menuRepository,
            ViewDefinitionService viewService,
            CustomPageService customPageService,
            OperationUserStore userStore,
            ReportService reportService,
            UserClassService userClassService,
            DashboardService dashboardService,
            NavTreeService navTreeService,
            WorkflowConfiguration workflowConfiguration,
            GisService gisService,
            HomepageService homepageService
            ) {
        this.dao = checkNotNull(dao);
        this.menuRepository = checkNotNull(menuRepository);
        this.viewService = checkNotNull(viewService);
        this.customPageService = checkNotNull(customPageService);
        this.userStore = checkNotNull(userStore);
        this.reportService = checkNotNull(reportService);
        this.userClassService = checkNotNull(userClassService);
        this.dashboardService = checkNotNull(dashboardService);
        this.navTreeService = checkNotNull(navTreeService);
        this.workflowConfiguration = checkNotNull(workflowConfiguration);
        this.gisService = checkNotNull(gisService);
        this.homepageService = checkNotNull(homepageService);
    }

    @Nullable
    @Override
    public Menu getMenuByIdOrNull(long menuId) {
        MenuData data = menuRepository.getMenuDataByIdOrNull(menuId);
        return data == null ? null : toMenu(data);
    }

    @Nullable
    @Override
    public Menu getGisMenu() {
        MenuData gisMenu = menuRepository.getMenuDataByCodeOrNull("gismenu");
        if (gisMenu != null) {
            return toMenu(gisMenu);
        } else {
            return null;
        }
    }

    @Override
    public List<MenuInfo> getAllMenuInfos() {
        return menuRepository.getAllMenuInfos();
    }

    @Override
    public Menu create(String groupName, MenuTreeNode menu, TargetDevice targetDevice, MenuType menuType) {
        return toMenu(menuRepository.createMenuData(MenuDataImpl.builder()
                .withGroupName(groupName)
                .withMenuRootNode(toJsonMenu(menu))
                .withTargetDevice(targetDevice)
                .withType(menuType)
                .build()));
    }

    @Override
    public Menu update(long menuId, MenuTreeNode menu, TargetDevice targetDevice) {
        MenuData data = menuRepository.getMenuDataById(menuId);
        return toMenu(menuRepository.updateMenuData(MenuDataImpl.copyOf(data)
                .withMenuRootNode(toJsonMenu(menu))
                .withTargetDevice(targetDevice)
                .build()));
    }

    @Override
    public Menu getMenuForCurrentUser() {
        OperationUser user = userStore.getUser();
        String defaultGroup = user.getDefaultGroupNameOrNull();
        Menu menu = isBlank(defaultGroup) ? null : getMenuForGroupOrNull(defaultGroup, user.getTargetDevice());
        if (menu == null) {
            menu = getMenuForGroupOrNull(DEFAULT_MENU_GROUP_NAME, user.getTargetDevice());
        }
        if (menu == null) {
            menu = create(defaultGroup, MenuTreeNodeImpl.builder()
                    .withType(ROOT)
                    .withDescription("ROOT")
                    .build(), user.getTargetDevice(), MenuType.MT_NAVMENU);
        }
        List<MenuTreeNode> nodes = filterMenuForUser(menu.getRootNode().getChildren(), user.getTargetDevice());
        MenuTreeNodeImpl rootNode = MenuTreeNodeImpl.copyOf(menu.getRootNode()).withChildren(nodes).build();
        return new MenuImpl(menu.getId(), menu.getCode(), rootNode, "current", menu.getTargetDevice(), menu.getType());
    }

    @Override
    public Menu getGisMenuForUserAndClass(String forClass) {
        Menu menu = getGisMenu();
        if (menu == null) {
            return null;
        }
        return menu;
//        List<MenuTreeNode> nodes = filterGisMenuForUserAndClass(menu.getRootNode().getChildren(), gisService.getGisAttributesVisibleFromClass(forClass), userClassService.getUserClass(forClass));
//        MenuTreeNodeImpl rootNode = MenuTreeNodeImpl.copyOf(menu.getRootNode()).withChildren(nodes).build();
//        return new MenuImpl(menu.getId(), menu.getCode(), rootNode, "current", menu.getTargetDevice(), menu.getType());
    }

    @Override
    public void delete(long menuId) {
        menuRepository.delete(menuId);
    }

    @Nullable
    private Menu getMenuForGroupOrNull(String groupName, TargetDevice preferredDevice) {
        MenuData data = firstNotNullOrNull(menuRepository.getMenuDataForGroupOrNull(groupName, preferredDevice), menuRepository.getMenuDataForGroupOrNull(groupName, TD_DEFAULT));
        return data == null ? null : toMenu(data);
    }

    private Menu toMenu(MenuData data) {
        return new MenuImpl(data.getId(), data.getCode(), toMenuItem(data.getMenuRootNode()), data.getGroupName(), data.getTargetDevice(), data.getType());
    }

    private MenuJsonRootNode toJsonMenu(MenuTreeNode menu) {
        checkArgument(Objects.equals(menu.getType(), ROOT));
        return new MenuJsonRootNodeImpl(toJsonMenuNodes(menu.getChildren()));
    }

    private MenuJsonNode toJsonMenuNode(Pair<MenuTreeNode, Integer> pair) {
        MenuTreeNode node = pair.getLeft();
        return new MenuJsonNodeImpl(node.getType(),
                node.getTarget(),
                node.getDescription(),
                node.getCode(),
                toJsonMenuNodes(node.getChildren()));
    }

    private List<MenuJsonNode> toJsonMenuNodes(List<MenuTreeNode> list) {
        return IntStream.range(0, list.size()).mapToObj((i) -> Pair.of(list.get(i), i + 1)).map(this::toJsonMenuNode).collect(toList());
    }

    private List<MenuTreeNode> filterGisMenuForUserAndClass(List<MenuTreeNode> menu, List<GisAttribute> visibleGisAttributes, Classe forClass) {
        return menu.stream().filter((m) -> {
            switch (m.getType()) {
                case FOLDER:
                    return true;
                case GEOATTRIBUTE:
                    String geoAttributeClassName = Splitter.on(".").splitToList(m.getTarget()).get(0);
                    String geoAttributeName = Splitter.on(".").splitToList(m.getTarget()).get(1);
                    for (int i = 0; i < visibleGisAttributes.size(); i++) {
                        if (visibleGisAttributes.get(i).getOwnerClassName().equals(geoAttributeClassName) && visibleGisAttributes.get(i).getLayerName().equals(geoAttributeName)) {
                            if (forClass.getName().equals(geoAttributeClassName)) {
                                return forClass.hasGisAttributeReadPermission(geoAttributeName);
                            } else {
                                return userClassService.getUserClass(visibleGisAttributes.get(i).getOwnerClassName()).hasGisAttributeReadPermission(geoAttributeName);
                            }
                        }
                    }
                default:
                    return false;
            }
        }).map((n) -> {
            if (n.getChildren().isEmpty()) {
                return n;
            } else {
                return MenuTreeNodeImpl.copyOf(n).withChildren(filterGisMenuForUserAndClass(n.getChildren(), visibleGisAttributes, forClass)).build();
            }
        }).collect(toList());
    }

    private List<MenuTreeNode> filterMenuForUser(List<MenuTreeNode> menu, TargetDevice targetDevice) {
        return menu.stream()
                .filter((m) -> {
                    try {
                        return switch (m.getType()) {
                            case FOLDER, ROOT ->
                                true;
                            case NAVTREE, DEPENDENCYTREE ->
                                userClassService.isActiveAndUserCanRead(navTreeService.getTree(m.getTarget()).getData().getTargetClassName());
                            case CLASS ->
                                userClassService.isActiveAndUserCanRead(m.getTarget());
                            case PROCESS -> {
                                if (workflowConfiguration.isEnabled()) {
                                    yield userClassService.isActiveAndUserCanRead(m.getTarget());
                                } else {
                                    yield false;
                                }
                            }
                            case REPORT_CSV, REPORT_PDF, REPORT_ODT, REPORT_RTF, REPORT_XML ->
                                reportService.isActiveAndAccessibleByCode(m.getTarget());
                            case DASHBOARD ->
                                dashboardService.isActiveAndAccessibleByCode(m.getTarget());
                            case VIEW ->
                                viewService.isActiveAndUserAccessibleByName(m.getTarget());
                            case CUSTOM_PAGE -> {
                                if (customPageService.getByName(m.getTarget()).getTargetDevices().contains(targetDevice)) {
                                    yield customPageService.isActiveAndAccessibleByName(m.getTarget());
                                } else {
                                    yield false;
                                }
                            }
                            case HOMEPAGE ->
                                homepageService.isActiveAndAccessibleByCode(m.getTarget());
                            default ->
                                throw unsupported("unsupported menu type = %s", m.getType());
                        };
                    } catch (Exception ex) {
                        logger.error(marker(), "error processing menu record = {}", m, ex);
                        return false;
                    }

                }).map((n) -> {
            if (n.getChildren().isEmpty()) {
                return n;
            } else {
                return MenuTreeNodeImpl.copyOf(n).withChildren(filterMenuForUser(n.getChildren(), targetDevice)).build();
            }
        }).collect(toList());
    }

    private MenuTreeNode toMenuItem(MenuJsonRootNode root) {
        return MenuTreeNodeImpl.buildRoot(root.getMenuNodes().stream().map(this::convertMenuElementToMenuItemBuilderOrNullIfError).filter(notNull()).collect(toList()));
    }

    @Nullable
    private MenuTreeNode convertMenuElementToMenuItemBuilderOrNullIfError(MenuJsonNode menuElement) {
        try {
            return convertMenuElementToMenuItemBuilder(menuElement);
        } catch (Exception e) {
            logger.error(marker(), "Error converting MenuItem from element = {}", menuElement, e);
            return null;
        }
    }

    private MenuTreeNode convertMenuElementToMenuItemBuilder(MenuJsonNode record) {
        return MenuTreeNodeImpl.builder()
                .withCode(record.getCode())
                .withType(record.getMenuType())
                .withDescription(record.getDescription())
                .accept((b) -> {
                    switch (record.getMenuType()) {
                        case CLASS, PROCESS ->
                            b.withTarget(record.getTarget()).withTargetDescription(dao.getClasse(record.getTarget()).getDescription());
                        case REPORT_CSV, REPORT_PDF, REPORT_ODT, REPORT_RTF, REPORT_XML ->
                            b.withTarget(record.getTarget()).withTargetDescription(reportService.getByCode(record.getTarget()).getDescription());
                        case CUSTOM_PAGE ->
                            b.withTarget(record.getTarget()).withTargetDescription(customPageService.getByName(record.getTarget()).getDescription());
                        case VIEW ->
                            b.withTarget(record.getTarget()).withTargetDescription(viewService.getSharedByName(record.getTarget()).getDescription());
                        case DASHBOARD ->
                            b.withTarget(record.getTarget()).withTargetDescription(dashboardService.getByCode(record.getTarget()).getDescription());
                        case HOMEPAGE ->
                            b.withTarget(record.getTarget()).withTargetDescription(homepageService.getByCode(record.getTarget()).getDescription());
                        case NAVTREE, DEPENDENCYTREE ->
                            b.withTarget(record.getTarget()).withTargetDescription(navTreeService.getTree(record.getTarget()).getDescription());
                        case GEOATTRIBUTE -> {
                            List<String> splittedTarget = Splitter.on(".").splitToList(record.getTarget());
                            b.withTarget(record.getTarget()).withTargetDescription(gisService.getGisAttributeWithCurrentUserByClassAndNameOrId(splittedTarget.get(0), splittedTarget.get(1)).getDescription());
                        }
                        case FOLDER, ROOT, SYSTEM_FOLDER -> {
                            //no target
                        }
                        default ->
                            throw unsupported("unsupported menu item type = %s", record.getMenuType());
                    }
                })
                .withChildren(record.getChildren().stream().map(this::convertMenuElementToMenuItemBuilderOrNullIfError).filter(notNull()).collect(toList()))
                .build();
    }

}
