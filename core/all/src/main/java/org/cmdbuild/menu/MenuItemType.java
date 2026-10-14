package org.cmdbuild.menu;

public enum MenuItemType {

    CLASS,
    DASHBOARD,
    PROCESS,
    FOLDER,
    SYSTEM_FOLDER,
    REPORT_CSV,
    REPORT_PDF,
    REPORT_ODT,
    REPORT_RTF,
    REPORT_XML,
    VIEW,
    CUSTOM_PAGE,
    ROOT,
    NAVTREE,
    DEPENDENCYTREE,
    GEOATTRIBUTE,
    HOMEPAGE;

    public boolean isReport() {
        return equals(REPORT_CSV) || equals(REPORT_PDF) || equals(REPORT_ODT) || equals(REPORT_RTF) || equals(REPORT_XML);
    }

}
