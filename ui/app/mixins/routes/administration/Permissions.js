Ext.define('CMDBuildUI.mixins.routes.administration.Permissions', {
    mixinId: 'administrationroutes-permissions-mixin',

    imports: ['CMDBuildUI.util.helper.SessionHelper', 'CMDBuildUI.model.users.Session'],

    onCheckAdministrationSession: function (action) {
        var me = this;
        CMDBuildUI.util.helper.SessionHelper.checkSessionValidity().then(
            function (session) {
                action.resume();
            },
            function (session) {
                action.stop();
                me.redirectTo('login', true);
            }
        );
    },

    adminAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_access, action);
    },
    adminClassAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_classes_view, action);
    },
    adminClassAdd: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_classes_modify, action);
    },
    adminLookupTypeAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_lookups_view, action);
    },
    adminLookupAdd: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_lookups_modify, action);
    },
    adminDMSAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(
            this.adminRolePrivileges.admin_dms_view,
            action,
            CMDBuildUI.model.Configuration.dms.enabled
        );
    },
    adminDMSAdd: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(
            this.adminRolePrivileges.admin_dms_modify,
            action,
            CMDBuildUI.model.Configuration.dms.enabled
        );
    },
    adminDomainAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_domains_view, action);
    },
    adminDomainAdd: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_domains_modify, action);
    },

    adminMenuAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_menus_view, action);
    },
    adminHomepagesAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_homepages_view, action);
    },
    adminProcessAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(
            this.adminRolePrivileges.admin_processes_view,
            action,
            CMDBuildUI.model.Configuration.processes.enabled
        );
    },
    adminProcessAdd: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(
            this.adminRolePrivileges.admin_processes_modify,
            action,
            CMDBuildUI.model.Configuration.processes.enabled
        );
    },
    adminReportAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_reports_view, action);
    },
    adminDashboardAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_dashboards_view, action);
    },
    adminUiComponentAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_uicomponents_view, action);
    },
    adminRoleAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_roles_view, action);
    },
    adminTaskAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_jobs_view, action);
    },
    adminUserAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_users_view, action);
    },
    adminSetupAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_sysconfig_view, action);
    },
    adminEmailAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_email_view, action);
    },
    adminLocalizationAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_localization_view, action);
    },
    adminNavTreeAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_navtrees_view, action);
    },
    adminSearchFilterAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_searchfilters_view, action);
    },
    adminViewAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_views_view, action);
    },
    adminScheduleAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_calendar_view, action);
    },
    adminBimAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(
            this.adminRolePrivileges.admin_bim_view,
            action,
            CMDBuildUI.model.Configuration.bim.enabled
        );
    },
    adminGisExternalServicesAccess: function () {
        CMDBuildUI.util.Utilities.showLoader(true);
        this.adminGisAccess(arguments[0]);
        new Ext.util.DelayedTask(function () {
            CMDBuildUI.util.Utilities.showLoader(false);
        }).delay(150);
    },
    adminGisAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(
            this.adminRolePrivileges.admin_gis_view,
            action,
            CMDBuildUI.model.Configuration.gis.enabled
        );
    },
    adminGisMenuAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(
            [this.adminRolePrivileges.admin_gis_view, this.adminRolePrivileges.admin_menus_view],
            action,
            CMDBuildUI.model.Configuration.gis.enabled,
            true
        );
    },
    adminImportExportAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_etl_view, action);
    },
    adminBusAccess: function () {
        this.onAdminRouteChange();
        var action = arguments[arguments.length - 1];
        this.validateAdminRoutePermission(this.adminRolePrivileges.admin_etl_view, action);
    },

    privates: {
        adminRolePrivileges: CMDBuildUI.model.users.Session.adminRolePrivileges,

        continue: function (action) {
            var token = Ext.History.currentToken;
            CMDBuildUI.util.administration.MenuStoreBuilder.selectNode('href', token);
            action.resume();
        },
        /**
         *
         * @param {String} privilege
         * @param {*} action
         * @param {String} serviceToCheck
         * @param {Boolean} multiplePermissions
         */
        validateAdminRoutePermission: function (privilege, action, serviceToCheck, multiplePermissions) {
            var me = this;
            var vm = me.getViewModel();
            adminCan = function (theSession, privilege, serviceToCheck, multiplePermissions) {
                var rolePrivilege = theSession.adminCan(privilege, serviceToCheck, multiplePermissions);
                if (rolePrivilege) {
                    CMDBuildUI.util.Logger.log(
                        Ext.String.format('rolePrivilege {0} allowed', privilege),
                        CMDBuildUI.util.Logger.levels.debug
                    );
                    me.continue(action);
                } else {
                    // TODO: show error message to user?
                    CMDBuildUI.util.Logger.log(
                        Ext.String.format('rolePrivilege {0} not allowed', privilege),
                        CMDBuildUI.util.Logger.levels.error
                    );
                    me.redirectToManagement();
                }
            };
            if (vm.get('theSession') && vm.get('theSession').adminCan) {
                adminCan(vm.get('theSession'), privilege, serviceToCheck, multiplePermissions);
            } else {
                vm.bind(
                    {
                        bindTo: {
                            theSession: '{theSession}'
                        },
                        single: true
                    },
                    function (data) {
                        adminCan(data.theSession, privilege, serviceToCheck, multiplePermissions);
                    }
                );
            }
        },
        onAdminRouteChange: function () {
            CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
        },
        redirectToManagement: function () {
            this.redirectTo('gotomamagement', true);
        }
    }
});
