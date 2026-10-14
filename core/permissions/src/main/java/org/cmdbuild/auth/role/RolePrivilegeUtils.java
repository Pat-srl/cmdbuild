/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.role;

import static com.google.common.base.Predicates.compose;
import static java.lang.String.format;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;
import static org.cmdbuild.auth.role.RolePrivilege.*;
import static org.cmdbuild.auth.role.RoleType.ROLE_TYPE_MAPPING;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.enumToString;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.cmdbuild.utils.privileges.PrivilegeProcessor;
import org.cmdbuild.utils.privileges.PrivilegeProcessorImpl;

public class RolePrivilegeUtils {

    private static final PrivilegeProcessor<RolePrivilege> ROLE_PRIVILEGE_PROCESSOR = PrivilegeProcessorImpl.<RolePrivilege>builder().withPrivilegeImplicationMap(map(
            RP_ADMIN_ALL, set(
                    RP_ADMIN_ALL_READONLY, RP_SYSTEM_ACCESS, RP_DATA_ALL_WRITE, RP_DATA_ALL_TENANT, RP_PROCESS_ALL_EXEC,
                    RP_IMPERSONATE_ALL, RP_ETLGATE_ALL, RP_CQL_ALL,
                    RP_ADMIN_CLASSES_MODIFY, RP_ADMIN_PROCESSES_MODIFY, RP_ADMIN_DOMAINS_MODIFY, RP_ADMIN_LOOKUPS_MODIFY,
                    RP_ADMIN_VIEWS_MODIFY, RP_ADMIN_DASHBOARDS_MODIFY, RP_ADMIN_REPORTS_MODIFY, RP_ADMIN_MENUS_MODIFY,
                    RP_ADMIN_NAVTREES_MODIFY, RP_ADMIN_ROLES_MODIFY, RP_ADMIN_USERS_MODIFY, RP_ADMIN_JOBS_MODIFY,
                    RP_ADMIN_EMAIL_MODIFY, RP_ADMIN_GIS_MODIFY, RP_ADMIN_BIM_MODIFY, RP_ADMIN_LOCALIZATION_MODIFY,
                    RP_ADMIN_SYSCONFIG_MODIFY, RP_ADMIN_SEARCHFILTERS_MODIFY, RP_ADMIN_DMS_MODIFY,
                    RP_ADMIN_UICOMPONENTS_MODIFY, RP_ADMIN_CORECOMPONENTS_MODIFY, RP_ADMIN_CALENDAR_MODIFY,
                    RP_ADMIN_ETL_MODIFY, RP_ADMIN_OFFLINE_MODIFY, RP_ADMIN_HOMEPAGES_MODIFY),
            RP_ADMIN_ALL_READONLY, set(RP_ADMIN_ACCESS, RP_DATA_ALL_READ, RP_REPORT_ALL_READ, RP_VIEW_ALL_READ,
                    RP_ADMIN_CLASSES_VIEW, RP_ADMIN_PROCESSES_VIEW, RP_ADMIN_DOMAINS_VIEW, RP_ADMIN_LOOKUPS_VIEW,
                    RP_ADMIN_VIEWS_VIEW, RP_ADMIN_DASHBOARDS_VIEW, RP_ADMIN_REPORTS_VIEW, RP_ADMIN_MENUS_VIEW,
                    RP_ADMIN_NAVTREES_VIEW, RP_ADMIN_ROLES_VIEW, RP_ADMIN_USERS_VIEW, RP_ADMIN_JOBS_VIEW,
                    RP_ADMIN_EMAIL_VIEW, RP_ADMIN_GIS_VIEW, RP_ADMIN_BIM_VIEW, RP_ADMIN_LOCALIZATION_VIEW,
                    RP_ADMIN_SYSCONFIG_VIEW, RP_ADMIN_SEARCHFILTERS_VIEW, RP_ADMIN_DMS_VIEW, RP_ADMIN_UICOMPONENTS_VIEW,
                    RP_ADMIN_CORECOMPONENTS_VIEW, RP_ADMIN_CALENDAR_VIEW, RP_ADMIN_ETL_VIEW, RP_ADMIN_OFFLINE_VIEW,
                    RP_ADMIN_HOMEPAGES_VIEW),
            RP_BASE_ALL, set(RP_BASE_ACCESS, RP_BULKUPDATE_ACCESS, RP_CHAT_ACCESS, RP_MOBILE_APP_ACCESS,
                    RP_CHANGEPASSWORD_ACCESS, RP_CLASS_ACCESS, RP_CUSTOMPAGES_ACCESS, RP_DASHBOARD_ACCESS,
                    RP_DATAVIEW_ACCESS, RP_EXPORTCSV_ACCESS, RP_IMPORTCSV_ACCESS, RP_PROCESS_ACCESS, RP_REPORT_ACCESS,
                    RP_CALENDAR_ACCESS, RP_CALENDAR_EVENT_CREATE, RP_GIS_ACCESS, RP_BIM_ACCESS, RP_RELGRAPH_ACCESS,
                    RP_CARD_TAB_ATTACHMENT_ACCESS_WRITE, RP_CARD_TAB_DETAIL_ACCESS_WRITE, RP_CARD_TAB_EMAIL_ACCESS_WRITE,
                    RP_CARD_TAB_HISTORY_ACCESS_WRITE, RP_CARD_TAB_NOTE_ACCESS_WRITE, RP_CARD_TAB_RELATION_ACCESS_WRITE,
                    RP_CARD_TAB_SCHEDULE_ACCESS_WRITE, RP_FLOW_TAB_ATTACHMENT_ACCESS_WRITE,
                    RP_FLOW_TAB_DETAIL_ACCESS_WRITE, RP_FLOW_TAB_EMAIL_ACCESS_WRITE, RP_FLOW_TAB_HISTORY_ACCESS_WRITE,
                    RP_FLOW_TAB_NOTE_ACCESS_WRITE, RP_FLOW_TAB_RELATION_ACCESS_WRITE, RP_ETLGATE_ACCESS,
                    RP_HOMEPAGE_ACCESS),
            RP_CARD_TAB_ATTACHMENT_ACCESS_WRITE, set(RP_CARD_TAB_ATTACHMENT_ACCESS_READ),
            RP_CARD_TAB_DETAIL_ACCESS_WRITE, set(RP_CARD_TAB_DETAIL_ACCESS_READ),
            RP_CARD_TAB_EMAIL_ACCESS_WRITE, set(RP_CARD_TAB_EMAIL_ACCESS_READ),
            RP_CARD_TAB_HISTORY_ACCESS_WRITE, set(RP_CARD_TAB_HISTORY_ACCESS_READ),
            RP_CARD_TAB_NOTE_ACCESS_WRITE, set(RP_CARD_TAB_NOTE_ACCESS_READ),
            RP_CARD_TAB_RELATION_ACCESS_WRITE, set(RP_CARD_TAB_RELATION_ACCESS_READ),
            RP_CARD_TAB_SCHEDULE_ACCESS_WRITE, set(RP_CARD_TAB_SCHEDULE_ACCESS_READ),
            RP_FLOW_TAB_ATTACHMENT_ACCESS_WRITE, set(RP_FLOW_TAB_ATTACHMENT_ACCESS_READ),
            RP_FLOW_TAB_DETAIL_ACCESS_WRITE, set(RP_FLOW_TAB_DETAIL_ACCESS_READ),
            RP_FLOW_TAB_EMAIL_ACCESS_WRITE, set(RP_FLOW_TAB_EMAIL_ACCESS_READ),
            RP_FLOW_TAB_HISTORY_ACCESS_WRITE, set(RP_FLOW_TAB_HISTORY_ACCESS_READ),
            RP_FLOW_TAB_NOTE_ACCESS_WRITE, set(RP_FLOW_TAB_NOTE_ACCESS_READ),
            RP_FLOW_TAB_RELATION_ACCESS_WRITE, set(RP_FLOW_TAB_RELATION_ACCESS_READ),
            RP_ETLGATE_ALL, set(RP_ETLGATE_ACCESS),
            RP_CALENDAR_EVENT_CREATE, set(RP_CALENDAR_ACCESS),
            RP_DATA_ALL_WRITE, set(RP_DATA_ALL_READ),
            RP_ADMIN_CLASSES_MODIFY, set(RP_ADMIN_CLASSES_VIEW),
            RP_ADMIN_PROCESSES_MODIFY, set(RP_ADMIN_PROCESSES_VIEW),
            RP_ADMIN_DOMAINS_MODIFY, set(RP_ADMIN_DOMAINS_VIEW),
            RP_ADMIN_LOOKUPS_MODIFY, set(RP_ADMIN_LOOKUPS_VIEW),
            RP_ADMIN_VIEWS_MODIFY, set(RP_ADMIN_VIEWS_VIEW),
            RP_ADMIN_DASHBOARDS_MODIFY, set(RP_ADMIN_DASHBOARDS_VIEW),
            RP_ADMIN_REPORTS_MODIFY, set(RP_ADMIN_REPORTS_VIEW),
            RP_ADMIN_MENUS_MODIFY, set(RP_ADMIN_MENUS_VIEW, RP_ADMIN_NAVTREES_MODIFY),
            RP_ADMIN_NAVTREES_MODIFY, set(RP_ADMIN_NAVTREES_VIEW, RP_DEPENDENCYTREE_ACCESS),
            RP_ADMIN_ROLES_MODIFY, set(RP_ADMIN_ROLES_VIEW),
            RP_ADMIN_USERS_MODIFY, set(RP_ADMIN_USERS_VIEW),
            RP_ADMIN_JOBS_MODIFY, set(RP_ADMIN_JOBS_VIEW),
            RP_ADMIN_EMAIL_MODIFY, set(RP_ADMIN_EMAIL_VIEW),
            RP_ADMIN_GIS_MODIFY, set(RP_ADMIN_GIS_VIEW, RP_ADMIN_NAVTREES_MODIFY),//TODO check nav tree access
            RP_ADMIN_BIM_MODIFY, set(RP_ADMIN_BIM_VIEW, RP_ADMIN_NAVTREES_MODIFY),//TODO check nav tree access
            RP_ADMIN_SEARCHFILTERS_MODIFY, set(RP_ADMIN_SEARCHFILTERS_VIEW),
            RP_ADMIN_DMS_MODIFY, set(RP_ADMIN_DMS_VIEW),
            RP_ADMIN_UICOMPONENTS_MODIFY, set(RP_ADMIN_UICOMPONENTS_VIEW),
            RP_ADMIN_CORECOMPONENTS_MODIFY, set(RP_ADMIN_CORECOMPONENTS_VIEW),
            RP_ADMIN_CALENDAR_MODIFY, set(RP_ADMIN_CALENDAR_VIEW),
            RP_ADMIN_ETL_MODIFY, set(RP_ADMIN_ETL_VIEW),
            RP_ADMIN_OFFLINE_MODIFY, set(RP_ADMIN_OFFLINE_VIEW),
            RP_ADMIN_LOCALIZATION_MODIFY, set(RP_ADMIN_LOCALIZATION_VIEW),
            RP_ADMIN_SYSCONFIG_MODIFY, set(RP_ADMIN_SYSCONFIG_VIEW),
            RP_ADMIN_CLASSES_VIEW, set(RP_ADMIN_DOMAINS_VIEW, RP_ADMIN_CALENDAR_VIEW, RP_ADMIN_LOOKUPS_VIEW, RP_ADMIN_ETL_VIEW, RP_ADMIN_CORECOMPONENTS_VIEW),
            RP_ADMIN_PROCESSES_VIEW, set(RP_ADMIN_JOBS_VIEW, RP_ADMIN_DOMAINS_VIEW, RP_ADMIN_LOOKUPS_VIEW, RP_ADMIN_ETL_VIEW),
            RP_ADMIN_DOMAINS_VIEW, set(RP_ADMIN_CLASSES_VIEW, RP_ADMIN_DOMAINS_VIEW, RP_ADMIN_LOOKUPS_VIEW),
            RP_ADMIN_VIEWS_VIEW, set(RP_ADMIN_CLASSES_VIEW, RP_ADMIN_PROCESSES_VIEW),
            RP_ADMIN_DASHBOARDS_VIEW, set(RP_ADMIN_CLASSES_VIEW, RP_ADMIN_PROCESSES_VIEW),
            RP_ADMIN_MENUS_VIEW, set(RP_ADMIN_CLASSES_VIEW, RP_ADMIN_PROCESSES_VIEW, RP_ADMIN_VIEWS_VIEW, RP_ADMIN_DASHBOARDS_VIEW, RP_ADMIN_REPORTS_VIEW, RP_ADMIN_UICOMPONENTS_VIEW, RP_ADMIN_ROLES_VIEW, RP_ADMIN_HOMEPAGES_VIEW),
            RP_ADMIN_NAVTREES_VIEW, set(RP_ADMIN_CLASSES_VIEW, RP_ADMIN_DOMAINS_VIEW, RP_ADMIN_LOOKUPS_VIEW),
            RP_ADMIN_ROLES_VIEW, set(RP_ADMIN_ETL_VIEW, RP_ADMIN_UICOMPONENTS_VIEW, RP_ADMIN_DMS_VIEW, RP_ADMIN_USERS_VIEW, RP_ADMIN_CLASSES_VIEW, RP_ADMIN_PROCESSES_VIEW, RP_ADMIN_VIEWS_VIEW, RP_ADMIN_DASHBOARDS_VIEW, RP_ADMIN_REPORTS_VIEW, RP_ADMIN_HOMEPAGES_VIEW),
            RP_ADMIN_USERS_VIEW, set(RP_ADMIN_ROLES_VIEW),
            RP_ADMIN_JOBS_VIEW, set(RP_ADMIN_EMAIL_VIEW, RP_ADMIN_PROCESSES_VIEW, RP_ADMIN_ETL_VIEW),
            RP_ADMIN_ETL_VIEW, set(RP_ADMIN_CLASSES_VIEW, RP_ADMIN_PROCESSES_VIEW, RP_ADMIN_EMAIL_VIEW),
            RP_ADMIN_OFFLINE_VIEW, set(RP_ADMIN_CLASSES_VIEW, RP_ADMIN_PROCESSES_VIEW, RP_ADMIN_VIEWS_VIEW),
            RP_ADMIN_LOCALIZATION_VIEW, set(RP_ADMIN_MENUS_VIEW, RP_ADMIN_ROLES_VIEW),
            RP_ADMIN_GIS_VIEW, set(RP_ADMIN_CLASSES_VIEW, RP_ADMIN_DOMAINS_VIEW),
            RP_ADMIN_SEARCHFILTERS_VIEW, set(RP_ADMIN_CLASSES_VIEW, RP_ADMIN_PROCESSES_VIEW),
            RP_ADMIN_HOMEPAGES_MODIFY, set(RP_ADMIN_HOMEPAGES_VIEW),
            RP_ADMIN_HOMEPAGES_VIEW, set(RP_ADMIN_CLASSES_VIEW, RP_ADMIN_PROCESSES_VIEW)
    )).build();

    public static Map<Object, Object> getAdminPermissionsDependencies() {
        return map().accept(m -> {
            PrivilegeProcessorImpl.copyOf((PrivilegeProcessorImpl<RolePrivilege>) ROLE_PRIVILEGE_PROCESSOR).build().getPrivilegeImplicationMap().forEach((k, v) -> {
                m.put(format("_%s", enumToString(k)), v.stream().map(e -> format("_%s", enumToString(e))).collect(toSet())); // _rp_* for UI
            });
        });
    }

    public static ProcessedRolePrivileges processRolePermissions(RoleType type, Map<RolePrivilege, Boolean> customPermissions) {

        Set<RolePrivilege> allPermissions = set(ROLE_PRIVILEGE_PROCESSOR.expandPrivileges(getPermissionsForRoleType(type)));
        customPermissions = map(customPermissions);

        list(customPermissions.entrySet()).stream().filter(Entry::getValue).filter(compose(allPermissions::contains, Entry::getKey)).map(Entry::getKey).forEach(customPermissions::remove);

        customPermissions.entrySet().stream().filter(Entry::getValue).map(Entry::getKey).forEach(allPermissions::add);

        allPermissions = set(ROLE_PRIVILEGE_PROCESSOR.expandPrivileges(allPermissions));

        list(customPermissions.entrySet()).stream().filter(not(Entry::getValue)).filter(not(compose(allPermissions::contains, Entry::getKey))).map(Entry::getKey).forEach(customPermissions::remove);

        customPermissions.entrySet().stream().filter(not(Entry::getValue)).map(Entry::getKey).forEach(allPermissions::remove); //TODO reverse expand

        return new ProcessedRolePermissionsImpl(allPermissions, customPermissions);
    }

    private static Set<RolePrivilege> getPermissionsForRoleType(RoleType type) {
        return checkNotNull(ROLE_TYPE_MAPPING.get(type), "role type mapping not found for type = %s", type);
    }

    public interface ProcessedRolePrivileges {

        Set<RolePrivilege> getRolePrivileges();

        Map<RolePrivilege, Boolean> getCustomPrivileges();
    }

    private static class ProcessedRolePermissionsImpl implements ProcessedRolePrivileges {

        private final Set<RolePrivilege> allPermissions;
        private final Map<RolePrivilege, Boolean> customPermissions;

        public ProcessedRolePermissionsImpl(Set<RolePrivilege> allPermissions, Map<RolePrivilege, Boolean> customPermissions) {
            this.allPermissions = checkNotNull(allPermissions);
            this.customPermissions = checkNotNull(customPermissions);
        }

        @Override
        public Set<RolePrivilege> getRolePrivileges() {
            return allPermissions;
        }

        @Override
        public Map<RolePrivilege, Boolean> getCustomPrivileges() {
            return customPermissions;
        }

    }

}
