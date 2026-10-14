package org.cmdbuild.menu;

import java.util.Map;
import static org.cmdbuild.menu.MenuItemType.CLASS;
import static org.cmdbuild.menu.MenuItemType.CUSTOM_PAGE;
import static org.cmdbuild.menu.MenuItemType.DASHBOARD;
import static org.cmdbuild.menu.MenuItemType.DEPENDENCYTREE;
import static org.cmdbuild.menu.MenuItemType.FOLDER;
import static org.cmdbuild.menu.MenuItemType.GEOATTRIBUTE;
import static org.cmdbuild.menu.MenuItemType.NAVTREE;
import static org.cmdbuild.menu.MenuItemType.PROCESS;
import static org.cmdbuild.menu.MenuItemType.REPORT_CSV;
import static org.cmdbuild.menu.MenuItemType.REPORT_ODT;
import static org.cmdbuild.menu.MenuItemType.REPORT_PDF;
import static org.cmdbuild.menu.MenuItemType.REPORT_RTF;
import static org.cmdbuild.menu.MenuItemType.REPORT_XML;
import static org.cmdbuild.menu.MenuItemType.ROOT;
import static org.cmdbuild.menu.MenuItemType.SYSTEM_FOLDER;
import static org.cmdbuild.menu.MenuItemType.VIEW;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class MenuConstants {

    public final static String TYPE_ATTRIBUTE = "Type";
    public final static String GROUP_NAME_ATTRIBUTE = "GroupName";
    public final static String NUMBER_ATTRIBUTE = "Number";
    public final static String ELEMENT_OBJECT_ID_ATTRIBUTE = "IdElementObj";
    public final static String ELEMENT_CLASS_ATTRIBUTE = "IdElementClass";
    public final static String PARENT_ID_ATTRIBUTE = "IdParent";
    public final static String DEFAULT_MENU_GROUP_NAME = "_default";
    public final static String MENU_CLASS_NAME = "Menu";

    public final static Map<MenuItemType, String> MENU_ITEM_TYPE_SERIALIZATION_MAP = map(
            CLASS, serializeEnum(CLASS),
            GEOATTRIBUTE, serializeEnum(GEOATTRIBUTE),
            DASHBOARD, serializeEnum(DASHBOARD),
            PROCESS, "processclass",
            FOLDER, serializeEnum(FOLDER),
            SYSTEM_FOLDER, "system_folder",
            REPORT_CSV, "reportcsv",
            REPORT_PDF, "reportpdf",
            REPORT_ODT, "reportodt",
            REPORT_RTF, "reportrtf",
            REPORT_XML, "reportxml",
            VIEW, serializeEnum(VIEW),
            CUSTOM_PAGE, "custompage",
            ROOT, serializeEnum(ROOT),
            NAVTREE, serializeEnum(NAVTREE),
            DEPENDENCYTREE, serializeEnum(DEPENDENCYTREE)
    );

}
