package org.cmdbuild.auth.grant;

import static java.lang.String.format;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;

public interface PrivilegeSubject {

    final String PS_FILTER = "filter",
            PS_CLASS = "class",
            PS_CUSTOMPAGE = "custompage",
            PS_DASHBOARD = "dashboard",
            PS_HOMEPAGE = "homepage",
            PS_DOMAIN = "domain",
            PS_ETLGATE = "etlgate",
            PS_ETLTEMPLATE = "etltemplate",
            PS_REPORT = "report",
            PS_STOREDFUNCTION = "storedfunction",
            PS_VIEW = "view",
            PS_OFFLINE = "offline",
            PS_DEPENDENCYTREE = "dependencytree";

    String getPrivilegeId();

    default boolean hasInfo() {
        return false;
    }

    default PrivilegeSubjectWithInfo getInfo() {
        return (PrivilegeSubjectWithInfo) this;
    }

    static String privilegeId(String type, Object id) {
        return format("%s:%s", checkNotBlank(type), toStringNotBlank(id));
    }

}
