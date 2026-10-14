package org.cmdbuild.bim.utils;

import org.apache.commons.lang3.StringUtils;

public class BimConstants {

    public static final String BIM_PROJECT_POID_NOT_YET_ASSIGNED = "BIM_PROJECT_POID_NOT_YET_ASSIGNED",
            IFC_FORMAT_IFC2X3TC1 = "ifc2x3tc1",
            IFC_FORMAT_IFC4 = "ifc4",
            IFC_FORMAT_DEFAULT = IFC_FORMAT_IFC2X3TC1;//TODO check this

    public static final String IFC_CONTENT_TYPE = "application/ifc";

    // IFC constants
    public static final String IFC_GLOBALID = "GlobalId";
    public static final String GLOBALID_ATTRIBUTE = IFC_GLOBALID;
    public static final String FK_COLUMN_NAME = "Master";

    // BimServer constants
    public static final String INVALID_ID = "-1";

    public static boolean isValidId(final String stringId) {
        return !StringUtils.isEmpty(stringId) && !stringId.equals(INVALID_ID);
    }

    private BimConstants() {
        throw new AssertionError();
    }
}
