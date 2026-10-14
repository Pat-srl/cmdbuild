Ext.define(
    'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.group.fieldsets.LimitedAdminPermissionsFieldsetModel',
    {
        extend: 'Ext.app.ViewModel',
        alias: 'viewmodel.administration-content-groupsandpermissions-tabitems-group-fieldsets-limitedadminpermissionsfieldset',
        data: {
            dependencyNeededKeys: []
        },
        formulas: {
            permissionDependenciesManager: function () {
                var me = this;
                Ext.Ajax.request({
                    url: Ext.String.format('{0}/roles/admin/dependencies', CMDBuildUI.util.Config.baseUrl),
                    method: 'GET',
                    success: function (response) {
                        var res = JSON.parse(response.responseText);
                        if (me && !me.destroyed) {
                            me.set('permissionsDepencies', res.data);
                        }
                    }
                });
            },
            limitedAdminPermissions: {
                bind: {
                    theGroup: '{theGroup}',
                    dependencies: '{permissionsDepencies}'
                },
                get: function (data) {
                    var permissions = data.theGroup,
                        dependencies = data.dependencies;
                    if (permissions)
                        return [
                            {
                                _id: '_rp_admin_classes',
                                description: CMDBuildUI.locales.Locales.administration.navigation.classes,
                                view:
                                    (permissions.get('_rp_admin_classes_view') &&
                                        !permissions.get('_rp_admin_classes_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_classes_modify') || false,
                                dependencies: dependencies._rp_admin_classes_view || []
                            },
                            {
                                _id: '_rp_admin_processes',
                                description: CMDBuildUI.locales.Locales.administration.navigation.processes,
                                view:
                                    (permissions.get('_rp_admin_processes_view') &&
                                        !permissions.get('_rp_admin_processes_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_processes_modify') || false,
                                dependencies: dependencies._rp_admin_processes_view || []
                            },
                            {
                                _id: '_rp_admin_domains',
                                description: CMDBuildUI.locales.Locales.administration.navigation.domains,
                                view:
                                    (permissions.get('_rp_admin_domains_view') &&
                                        !permissions.get('_rp_admin_domains_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_domains_modify') || false,
                                dependencies: dependencies._rp_admin_domains_view || []
                            },
                            {
                                _id: '_rp_admin_lookups',
                                description: CMDBuildUI.locales.Locales.administration.navigation.lookuptypes,
                                view:
                                    (permissions.get('_rp_admin_lookups_view') &&
                                        !permissions.get('_rp_admin_lookups_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_lookups_modify') || false,
                                dependencies: dependencies._rp_admin_domains_view || []
                            },
                            {
                                _id: '_rp_admin_views',
                                description: CMDBuildUI.locales.Locales.administration.navigation.views,
                                view:
                                    (permissions.get('_rp_admin_views_view') &&
                                        !permissions.get('_rp_admin_views_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_views_modify') || false,
                                dependencies: dependencies._rp_admin_views_view || []
                            },
                            {
                                _id: '_rp_admin_searchfilters',
                                description: CMDBuildUI.locales.Locales.administration.navigation.searchfilters,
                                view:
                                    (permissions.get('_rp_admin_searchfilters_view') &&
                                        !permissions.get('_rp_admin_searchfilters_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_searchfilters_modify') || false,
                                dependencies: dependencies._rp_admin_searchfilters_view || []
                            },
                            {
                                _id: '_rp_admin_dashboards',
                                description: CMDBuildUI.locales.Locales.administration.navigation.dashboards,
                                view:
                                    (permissions.get('_rp_admin_dashboards_view') &&
                                        !permissions.get('_rp_admin_dashboards_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_dashboards_modify') || false,
                                dependencies: dependencies._rp_admin_dashboards_view || []
                            },
                            {
                                _id: '_rp_admin_reports',
                                description: CMDBuildUI.locales.Locales.administration.navigation.reports,
                                view:
                                    (permissions.get('_rp_admin_reports_view') &&
                                        !permissions.get('_rp_admin_reports_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_reports_modify') || false,
                                dependencies: dependencies._rp_admin_reports_view || []
                            },
                            {
                                _id: '_rp_admin_menus',
                                description: CMDBuildUI.locales.Locales.administration.navigation.menus,
                                view:
                                    (permissions.get('_rp_admin_menus_view') &&
                                        !permissions.get('_rp_admin_menus_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_menus_modify') || false,
                                dependencies: dependencies._rp_admin_menus_view || []
                            },
                            {
                                _id: '_rp_admin_uicomponents',
                                description: CMDBuildUI.locales.Locales.administration.navigation.customcomponents,
                                view:
                                    (permissions.get('_rp_admin_uicomponents_view') &&
                                        !permissions.get('_rp_admin_menus_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_menus_modify') || false,
                                dependencies: dependencies._rp_admin_uicomponents_view || []
                            },
                            {
                                _id: '_rp_admin_dms',
                                description: CMDBuildUI.locales.Locales.administration.navigation.dms,
                                view:
                                    (permissions.get('_rp_admin_dms_view') &&
                                        !permissions.get('_rp_admin_dms_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_dms_modify') || false,
                                dependencies: dependencies._rp_admin_dms_view || []
                            },
                            {
                                _id: '_rp_admin_navtrees',
                                description: CMDBuildUI.locales.Locales.administration.navigation.navigationtrees,
                                view:
                                    (permissions.get('_rp_admin_navtrees_view') &&
                                        !permissions.get('_rp_admin_navtrees_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_navtrees_modify') || false,
                                dependencies: dependencies._rp_admin_navtrees_view || []
                            },
                            {
                                _id: '_rp_admin_roles',
                                description: CMDBuildUI.locales.Locales.administration.navigation.groupsandpermissions,
                                view:
                                    (permissions.get('_rp_admin_roles_view') &&
                                        !permissions.get('_rp_admin_roles_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_roles_modify') || false,
                                dependencies: dependencies._rp_admin_roles_view || []
                            },
                            {
                                _id: '_rp_admin_users',
                                description: CMDBuildUI.locales.Locales.administration.navigation.users,
                                view:
                                    (permissions.get('_rp_admin_users_view') &&
                                        !permissions.get('_rp_admin_users_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_users_modify') || false,
                                dependencies: dependencies._rp_admin_users_view || []
                            },
                            {
                                _id: '_rp_admin_email',
                                description: CMDBuildUI.locales.Locales.administration.navigation.notifications,
                                view:
                                    (permissions.get('_rp_admin_email_view') &&
                                        !permissions.get('_rp_admin_email_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_email_modify') || false,
                                dependencies: dependencies._rp_admin_email_view || []
                            },
                            {
                                _id: '_rp_admin_etl',
                                description:
                                    CMDBuildUI.locales.Locales.administration.navigation.importexports +
                                    ' - ' +
                                    CMDBuildUI.locales.Locales.administration.navigation.busdescriptors,
                                view:
                                    (permissions.get('_rp_admin_etl_view') &&
                                        !permissions.get('_rp_admin_etl_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_etl_modify') || false,
                                dependencies: dependencies._rp_admin_etl_view || []
                            },
                            {
                                _id: '_rp_admin_jobs',
                                description: CMDBuildUI.locales.Locales.administration.navigation.taskmanager,
                                view:
                                    (permissions.get('_rp_admin_jobs_view') &&
                                        !permissions.get('_rp_admin_jobs_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_jobs_modify') || false,
                                dependencies: dependencies._rp_admin_jobs_view || []
                            },
                            {
                                _id: '_rp_admin_calendar',
                                description: CMDBuildUI.locales.Locales.administration.navigation.schedules,
                                view:
                                    (permissions.get('_rp_admin_calendar_view') &&
                                        !permissions.get('_rp_admin_menus_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_menus_modify') || false,
                                dependencies: dependencies._rp_admin_calendar_view || []
                            },
                            {
                                _id: '_rp_admin_gis',
                                description: CMDBuildUI.locales.Locales.administration.navigation.gis,
                                view:
                                    (permissions.get('_rp_admin_gis_view') &&
                                        !permissions.get('_rp_admin_gis_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_gis_modify') || false,
                                dependencies: dependencies._rp_admin_gis_view || []
                            },
                            {
                                _id: '_rp_admin_bim',
                                description: CMDBuildUI.locales.Locales.administration.navigation.bim,
                                view:
                                    (permissions.get('_rp_admin_bim_view') &&
                                        !permissions.get('_rp_admin_bim_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_bim_modify') || false,
                                dependencies: dependencies._rp_admin_bim_view || []
                            },
                            {
                                _id: '_rp_admin_localization',
                                description: CMDBuildUI.locales.Locales.administration.navigation.languages,
                                view:
                                    (permissions.get('_rp_admin_localization_view') &&
                                        !permissions.get('_rp_admin_localization_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_localization_modify') || false,
                                dependencies: dependencies._rp_admin_localization_view || []
                            },
                            {
                                _id: '_rp_admin_sysconfig',
                                description: CMDBuildUI.locales.Locales.administration.navigation.systemconfig,
                                view:
                                    (permissions.get('_rp_admin_sysconfig_view') &&
                                        !permissions.get('_rp_admin_sysconfig_modify')) ||
                                    false,
                                modify: permissions.get('_rp_admin_sysconfig_modify') || false,
                                dependencies: dependencies._rp_admin_sysconfig_view || []
                            }
                        ];
                }
            }
        },

        stores: {
            limitedPersmissionsStore: {
                fields: [
                    '_id',
                    'description',
                    {
                        name: 'none',
                        calculate: function (data) {
                            return !data.view && !data.modify;
                        }
                    },
                    'view',
                    'modify',
                    'dependencies'
                ],
                proxy: 'memory',
                data: '{limitedAdminPermissions}'
            }
        }
    }
);
