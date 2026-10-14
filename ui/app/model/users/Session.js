Ext.define('CMDBuildUI.model.users.Session', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: ['Ext.data.validator.Presence'],

    statics: {
        temporary_id: 'session_temporary_id',
        adminRolePrivileges: {
            admin_access: 'admin_access',
            admin_all: 'admin_all',
            admin_all_readonly: 'admin_all_readonly',
            admin_bim_modify: 'admin_bim_modify',
            admin_bim_view: 'admin_bim_view',
            admin_calendar_modify: 'admin_calendar_modify',
            admin_calendar_view: 'admin_calendar_view',
            admin_classes_modify: 'admin_classes_modify',
            admin_classes_view: 'admin_classes_view',
            admin_dashboards_modify: 'admin_dashboards_modify',
            admin_dashboards_view: 'admin_dashboards_view',
            admin_domains_modify: 'admin_domains_modify',
            admin_domains_view: 'admin_domains_view',
            admin_email_modify: 'admin_email_modify',
            admin_email_view: 'admin_email_view',
            admin_etl_modify: 'admin_etl_modify',
            admin_etl_view: 'admin_etl_view',
            admin_gis_modify: 'admin_gis_modify',
            admin_gis_view: 'admin_gis_view',
            admin_jobs_modify: 'admin_jobs_modify',
            admin_jobs_view: 'admin_jobs_view',
            admin_localization_modify: 'admin_localization_modify',
            admin_localization_view: 'admin_localization_view',
            admin_lookups_modify: 'admin_lookups_modify',
            admin_lookups_view: 'admin_lookups_view',
            admin_menus_modify: 'admin_menus_modify',
            admin_menus_view: 'admin_menus_view',
            admin_homepages_modify: 'admin_homepages_modify',
            admin_homepages_view: 'admin_homepages_view',
            admin_navtrees_modify: 'admin_navtrees_modify',
            admin_navtrees_view: 'admin_navtrees_view',
            admin_processes_modify: 'admin_processes_modify',
            admin_processes_view: 'admin_processes_view',
            admin_reports_modify: 'admin_reports_modify',
            admin_reports_view: 'admin_reports_view',
            admin_roles_modify: 'admin_roles_modify',
            admin_roles_view: 'admin_roles_view',
            admin_searchfilters_modify: 'admin_searchfilters_modify',
            admin_searchfilters_view: 'admin_searchfilters_view',
            admin_sysconfig_modify: 'admin_sysconfig_modify',
            admin_sysconfig_view: 'admin_sysconfig_view',
            admin_uicomponents_modify: 'admin_uicomponents_modify',
            admin_uicomponents_view: 'admin_uicomponents_view',
            admin_users_modify: 'admin_users_modify',
            admin_users_view: 'admin_users_view',
            admin_views_modify: 'admin_views_modify',
            admin_views_view: 'admin_views_view',
            admin_dms_view: 'admin_dms_view',
            admin_dms_modify: 'admin_dms_modify'
        }
    },

    fields: [
        {
            name: 'userDescription',
            type: 'string'
        },
        {
            name: 'username',
            type: 'string',
            critical: true,
            validators: ['presence']
        },
        {
            name: 'password',
            type: 'string',
            validators: ['presence']
        },
        {
            name: 'role',
            type: 'string',
            critical: true
        },
        {
            name: 'availableRoles'
        },
        {
            name: 'activeTenants',
            critical: true
        },
        {
            name: 'availableTenants'
        },
        {
            name: 'scope',
            type: 'string',
            defaultValue: 'ui'
        },
        {
            name: 'rolePrivileges',
            type: 'auto'
        }
    ],

    proxy: {
        url: '/sessions/',
        type: 'baseproxy',
        extraParams: {
            ext: true
        }
    },

    adminCan: function (privilege, serviceToCheck, multiplePermissions) {
        var me = this,
            rolePrivilege = false,
            privileges = this.get('rolePrivileges');
        if (multiplePermissions) {
            var multiRolePrivilege = [];
            privilege.forEach(function (item) {
                multiRolePrivilege.push(me.adminCan(item, serviceToCheck));
            });
            rolePrivilege = multiRolePrivilege.indexOf(false) === -1;
        } else if (privileges && privileges[CMDBuildUI.model.users.Session.adminRolePrivileges.admin_access]) {
            rolePrivilege = privileges && privileges[privilege];
            if (serviceToCheck) {
                rolePrivilege = rolePrivilege && CMDBuildUI.util.helper.Configurations.get(serviceToCheck);
            }
        }
        return rolePrivilege;
    }
});
