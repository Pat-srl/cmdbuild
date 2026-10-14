Ext.define('CMDBuildUI.util.administration.MenuStoreBuilder', {
    singleton: true,
    mixins: ['Ext.mixin.Observable'],
    requires: ['CMDBuildUI.util.administration.helper.ApiHelper'],
    initialized: false,
    initialize: function (callback, ignoreInitialized) {
        var me = this;
        CMDBuildUI.util.helper.Configurations.loadSystemConfs().then(
            function () {
                CMDBuildUI.util.helper.UserPreferences.load();
                CMDBuildUI.util.Logger.log(
                    'MenuStoreBuilder loadSystemConfs done',
                    CMDBuildUI.util.Logger.levels.debug
                );
                if (ignoreInitialized && me.initialized) {
                    CMDBuildUI.util.Logger.log(
                        'MenuStoreBuilder ignore initialize menu',
                        CMDBuildUI.util.Logger.levels.debug
                    );
                    return;
                } else {
                    CMDBuildUI.util.Logger.log(
                        'MenuStoreBuilder all stores are initialized',
                        CMDBuildUI.util.Logger.levels.debug
                    );
                    me.onReloadAdminstrationMenu(callback);
                }
            },
            function () {
                CMDBuildUI.util.Logger.log(
                    'MenuStoreBuilder error load system initialize menu',
                    CMDBuildUI.util.Logger.levels.debug
                );
            }
        );
    },

    /**
     *
     */
    onReloadAdminstrationMenu: function (callback) {
        var me = this;
        var store = Ext.getStore('administration.MenuAdministration');
        var childrens = [];
        me.initialized = true;
        var count = 0;
        var promises = [];
        if (me.adminCan(me.adminRolePrivileges.admin_classes_view)) {
            promises.push(
                me.appendClassesAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'classes'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu Classes NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_processes_view)) {
            promises.push(
                me.appendProcessesAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'processes'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu Processes NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_domains_view)) {
            promises.push(
                me.appendDomainsAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'domains'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu Domains NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_lookups_view)) {
            promises.push(
                me.appendLookupTypesAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'lookups'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu LookTypes NOT LOADED!');
                    }
                )
            );
        }

        if (me.adminCan(me.adminRolePrivileges.admin_views_view)) {
            promises.push(
                me.appendViewsAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'views'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu Views NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_searchfilters_view)) {
            promises.push(
                me.appendSearchFiltersAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'searchfilters'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu Search filter NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_dashboards_view)) {
            promises.push(
                me.appendDashboardsAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'dashboards'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu Dashboard NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_reports_view)) {
            promises.push(
                me.appendReportsAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'reports'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu Report NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminRolePrivileges.admin_homepages_view) {
            promises.push(
                me.appendHomepageAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'homepage'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu Homepage NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_menus_view)) {
            promises.push(
                me.appendMenuAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'menu'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu Menu NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_uicomponents_view)) {
            promises.push(
                me.appendCustomPagesAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'custom pages'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu custom page NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_uicomponents_view)) {
            promises.push(
                me.appendCustomComponentsAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'custom components'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu custom component NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_dms_view)) {
            promises.push(
                me.appendDmsAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'DMS'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu LookTypes NOT LOADED!');
                    }
                )
            );
        }
        if (
            me.adminCan(me.adminRolePrivileges.admin_navtrees_view) &&
            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.relgraph.enabled)
        ) {
            promises.push(
                me.appendNavigationTreeAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'navigation trees'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu Navigation tree NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_navtrees_view)) {
            promises.push(
                me.appendDependencyTreeAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'dependency trees'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu dependency tree NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_roles_view)) {
            promises.push(
                me.appendGroupsAndPermissionsAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'groups and permissions'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu role and permission NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_users_view)) {
            promises.push(
                me.appendUsersAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'users'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu users NOT LOADED!');
                    }
                )
            );
        }

        if (me.adminCan(me.adminRolePrivileges.admin_email_view)) {
            promises.push(
                me.appendEmailsAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'emails'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu emails NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_etl_view)) {
            promises.push(
                me.appendImportExportMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'import export'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu settings NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_jobs_view)) {
            promises.push(
                me.appendTasksAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'tasks'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu tasks NOT LOADED!');
                    }
                )
            );
        }

        if (me.adminCan(me.adminRolePrivileges.admin_access)) {
            promises.push(
                me.appendPluginManagerMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'plugin manager'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu plugin manager NOT LOADED!');
                    }
                )
            );
            promises.push(
                me.appendAiMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'ai'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu ai NOT LOADED!');
                    }
                )
            );
        }

        if (me.adminCan(me.adminRolePrivileges.admin_etl_view)) {
            promises.push(
                me.appendBusAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'bus'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu Bus NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_calendar_view)) {
            promises.push(
                me.appendScheduleAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'schedules'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu schedule NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_gis_view)) {
            promises.push(
                me.appendGisAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'gis'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu gis NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_bim_view)) {
            promises.push(
                me.appendBimAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'bim'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu bim NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_localization_view)) {
            promises.push(
                me.appendLanguagesMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'localizations'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu languages NOT LOADED!');
                    }
                )
            );
        }
        if (me.adminCan(me.adminRolePrivileges.admin_sysconfig_view)) {
            promises.push(
                me.appendSystemManagementAdminMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'system management'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu system management NOT LOADED!');
                    }
                )
            );
        }

        if (me.adminCan(me.adminRolePrivileges.admin_sysconfig_view)) {
            promises.push(
                me.appendSettingsMenu(count++).then(
                    function (data) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format('Menu {0} - {1} loaded', data.index, 'settings'),
                            CMDBuildUI.util.Logger.levels.debug
                        );
                        childrens[data.index] = data.menu;
                    },
                    function () {
                        CMDBuildUI.util.Msg.alert('Error', 'Menu settings NOT LOADED!');
                    }
                )
            );
        }
        Ext.Promise.all(promises).then(function () {
            // add home
            Ext.Array.insert(childrens, 0, [
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.home,
                    // index: 0,
                    objecttype: 'home',
                    leaf: true,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('home', 'solid'),
                    href: 'administration/home',
                    text: CMDBuildUI.locales.Locales.administration.home.home
                }
            ]);

            store.beginUpdate();
            store.removeAll();
            store.setRoot({
                expanded: true
            });
            store.getRoot().appendChild(childrens);
            store.endUpdate();
            me.onMenuStoreReady(store, callback);
        });
    },

    /**
     * Create new folder for homepages in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendHomepageAdminMenu: function (index) {
        const me = this;
        const deferred = new Ext.Deferred();

        Ext.Promise.all([CMDBuildUI.util.Stores.loadHomepagesStore(), CMDBuildUI.util.Stores.loadGroupsStore()]).then(
            function (results) {
                const items = results[0];
                const groups = results[1];

                items.forEach(function (item) {
                    if (item.get('name') === '_default') {
                        item.set('description', CMDBuildUI.locales.Locales.administration.common.strings['default']);
                    } else {
                        const currentGroup = Ext.Array.findBy(groups, function (group) {
                            return group.get('name') === item.get('name');
                        });
                        if (currentGroup) {
                            item.set('description', currentGroup.get('description'));
                        } else {
                            CMDBuildUI.util.Logger.log(
                                Ext.String.format(
                                    'Group {0} not found for homepage {1}',
                                    item.get('name'),
                                    item.getId()
                                ),
                                CMDBuildUI.util.Logger.levels.warn
                            );
                        }
                    }
                });

                let children = me.getRecordsAsSubmenu(
                    items.sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.homepage,
                    ''
                );

                children.push({
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: children.length,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.settings,
                    leaf: true,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('wrench', 'solid'),
                    href: 'administration/setup/homepages',
                    rowCls: !me.canViewSettings() ? 'disabled' : '',
                    selectable: me.canViewSettings()
                });

                const homepagesMenu = {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.folder,
                    index: index,
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.homepage,
                    text: CMDBuildUI.locales.Locales.administration.navigation.homepages,
                    href: 'administration/homepages',
                    leaf: false,
                    children: children
                };

                deferred.resolve({
                    index: index,
                    menu: homepagesMenu
                });
            }
        );

        return deferred.promise;
    },

    /**
     * 0 - Create classes tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendClassesAdminMenu: function (index) {
        var me = this;
        var deferred = new Ext.Deferred();

        CMDBuildUI.util.Stores.loadClassesStore().then(function (items) {
            var simples = {
                menutype: 'folder',
                objecttype: 'Simples',
                text: CMDBuildUI.locales.Locales.administration.navigation.simples,
                href: 'administration/classes_empty',
                leaf: false,
                index: 1,
                children: me.getRecordsAsSubmenu(
                    items
                        .filter(function (rec, id) {
                            return rec.get('type') === 'simple';
                        })
                        .sort(me.sortByText),
                    CMDBuildUI.model.menu.MenuItem.types.klass,
                    ''
                )
            };

            var standard = {
                menutype: 'folder',
                objecttype: 'Standard',
                text: CMDBuildUI.locales.Locales.administration.navigation.standard,
                href: 'administration/classes_empty',
                leaf: false,
                index: 0,
                children: me.getRecordsAsSubmenu(
                    items
                        .filter(function (rec, id) {
                            return rec.get('prototype') === true || rec.get('parent').length > 0;
                        })
                        .sort(me.sortByDescription),
                    CMDBuildUI.model.menu.MenuItem.types.klass,
                    CMDBuildUI.util.helper.ModelHelper.systemClasses.class
                )
            };

            //TODO: check configuration
            var classesMenu = {
                menutype: 'folder',
                objecttype: CMDBuildUI.util.helper.ModelHelper.systemClasses.class,
                index: index,
                href: 'administration/classes_empty',
                text: CMDBuildUI.locales.Locales.administration.navigation.classes,
                leaf: false,
                children: [standard, simples],
                alternativeHref: ['administration/classes']
            };
            deferred.resolve({
                index: index,
                menu: classesMenu
            });
        });

        return deferred.promise;
    },
    /**
     * 1 - Create Process tree
     */
    appendProcessesAdminMenu: function (index) {
        var me = this;
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Stores.loadProcessesStore().then(function (items) {
            var children;
            if (items) {
                children = me.getRecordsAsSubmenu(
                    items
                        .filter(function (rec, id) {
                            return rec.get('prototype') === true || rec.get('parent').length > 0;
                        })
                        .sort(me.sortByDescription),
                    CMDBuildUI.model.menu.MenuItem.types.process,
                    CMDBuildUI.util.helper.ModelHelper.systemClasses.process
                );
            }
            var workflowDisabled = !CMDBuildUI.util.helper.Configurations.get(
                CMDBuildUI.model.Configuration.processes.enabled
            );
            var processesMenu = {
                rowCls: workflowDisabled ? 'disabled' : '',
                menutype: 'folder',
                objecttype: CMDBuildUI.model.administration.MenuItem.types.process,
                text: CMDBuildUI.locales.Locales.administration.navigation.processes,
                leaf: workflowDisabled && !children,
                index: index,
                selectable: !workflowDisabled,
                href: 'administration/processes_empty',
                alternativeHref: ['administration/processes']
            };

            processesMenu.children = children;
            deferred.resolve({
                index: index,
                menu: processesMenu
            });
        });

        return deferred.promise;
    },

    /**
     * 2 - Create new folder for domains in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendDomainsAdminMenu: function (index) {
        var me = this;
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Stores.loadDomainsStore().then(function (items) {
            var domainsMenu = {
                menutype: 'folder',
                href: 'administration/domains_empty',
                index: 2,
                objecttype: 'domain',
                text: CMDBuildUI.locales.Locales.administration.navigation.domains,
                leaf: false,
                children: me.getRecordsAsSubmenu(
                    items.sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.domain,
                    ''
                ),
                alternativeHref: ['administration/domains']
            };

            // append menu item to the store if has children
            deferred.resolve({
                index: index,
                menu: domainsMenu
            });
        });
        return deferred.promise;
    },

    /**
     * 3 - Create LookType tree
     */
    appendLookupTypesAdminMenu: function (index) {
        var me = this;
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Stores.loadLookupTypesStore().then(function (items) {
            function setAsPrototype(_item) {
                for (var i in items) {
                    if (items[i].get('name') === _item) {
                        items[i].set('prototype', true);
                    }
                }
            }

            function threeGenerator(_items) {
                for (var item in _items) {
                    if (_items[item].get('parent').length) {
                        setAsPrototype(_items[item].get('parent'));
                    }
                }
                return _items;
            }
            items = threeGenerator(items);
            var lokupTypesMenu = {
                menutype: 'folder',
                index: index,
                objecttype: 'lookuptype',
                text: CMDBuildUI.locales.Locales.administration.navigation.lookuptypes,
                leaf: false,
                href: 'administration/lookup_types_empty',
                children: me.getRecordsAsSubmenu(
                    items.filter(function (item) {
                        return item.get('accessType') === CMDBuildUI.model.lookups.LookupType.accessTypes._default;
                    }),
                    CMDBuildUI.model.administration.MenuItem.types.lookuptype,
                    ''
                )
            };

            // append menu item to the store if has children
            deferred.resolve({
                index: index,
                menu: lokupTypesMenu
            });
        });
        return deferred.promise;
    },
    appendDmsAdminMenu: function (index) {
        var me = this;
        var deferred = new Ext.Deferred();
        var dmsEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled);
        Ext.Promise.all([
            CMDBuildUI.util.Stores.loadDmsModelsStore(),
            CMDBuildUI.util.Stores.loadDmsCategoryTypesStore()
        ]).then(function (data) {
            var models = data[0],
                categoryTypes = data[1];
            var dmsModelsMenu = {
                menutype: 'folder',
                index: 1,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.dmsmodel,
                text: CMDBuildUI.locales.Locales.administration.navigation.dmsmodels,
                leaf: false,
                href: 'administration/dmsmodels_empty',
                children: me.getRecordsAsSubmenu(
                    models,
                    CMDBuildUI.model.administration.MenuItem.types.dmsmodel,
                    CMDBuildUI.model.dms.DMSModel.masterParentClass,
                    {
                        rowCls: !dmsEnabled ? 'disabled' : '',
                        selectable: dmsEnabled
                    }
                ),
                rowCls: !dmsEnabled ? 'disabled' : '',
                selectable: dmsEnabled
            };

            function setAsPrototype(item) {
                for (var i in categoryTypes) {
                    if (categoryTypes[i].get('name') === item) {
                        categoryTypes[i].set('prototype', true);
                    }
                }
            }

            function threeGenerator(categoryTypes) {
                for (var item in categoryTypes) {
                    if (categoryTypes[item].get('parent').length) {
                        setAsPrototype(categoryTypes[item].get('parent'));
                    }
                }
                return categoryTypes;
            }
            categoryTypes = threeGenerator(categoryTypes);
            var dmsCategoriesMenu = {
                menutype: 'folder',
                index: 0,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.dmscategory,
                text: CMDBuildUI.locales.Locales.administration.navigation.dmscategories,
                leaf: false,
                href: 'administration/dmscategories_empty',
                children: me.getRecordsAsSubmenu(
                    categoryTypes,
                    CMDBuildUI.model.administration.MenuItem.types.dmscategory,
                    '',
                    {
                        rowCls: !dmsEnabled ? 'disabled' : '',
                        selectable: dmsEnabled
                    }
                ),
                rowCls: !dmsEnabled ? 'disabled' : '',
                selectable: dmsEnabled
            };
            var settingsMenu = {
                menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                index: 2,
                objecttype: 'setup',
                leaf: true,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('wrench', 'solid'),
                href: 'administration/setup/documentmanagementsystem',
                text: CMDBuildUI.locales.Locales.administration.navigation.settings,
                rowCls: !me.canViewSettings() ? 'disabled' : '',
                selectable: me.canViewSettings()
            };
            // append menu item to the store if has children
            deferred.resolve({
                index: index,
                menu: {
                    menutype: 'folder',
                    index: index,
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.dmsmodel,
                    text: CMDBuildUI.locales.Locales.administration.navigation.dms,
                    leaf: false,
                    href: 'administration/dms',
                    children: [dmsCategoriesMenu, dmsModelsMenu, settingsMenu],
                    rowCls: !me.canViewSettings() && !dmsEnabled ? 'disabled' : '',
                    selectable: me.canViewSettings() || dmsEnabled
                }
            });
        });
        return deferred.promise;
    },

    /**
     * 4 - Create new folder for domains in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendViewsAdminMenu: function (index) {
        var me = this;
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Stores.loadViewsStore().then(function (items) {
            var filter = {
                menutype: 'folder',
                objecttype: CMDBuildUI.model.administration.MenuItem.types.view,
                text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromfilter,
                href: Ext.String.format(
                    'administration/views/_new/{0}/false',
                    CMDBuildUI.model.views.View.types.filter
                ),
                leaf: false,
                index: 0,
                children: me.getRecordsAsSubmenu(
                    items
                        .filter(function (item) {
                            return item.get('type') === CMDBuildUI.model.views.View.types.filter;
                        })
                        .sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.view,
                    ''
                )
            };
            var joind = {
                menutype: 'folder',
                objecttype: CMDBuildUI.model.administration.MenuItem.types.view,
                text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromjoin,
                href: 'administration/joinviews_empty/false',
                leaf: false,
                index: 1,
                children: me.getRecordsAsSubmenu(
                    items
                        .filter(function (item) {
                            return item.get('type') === CMDBuildUI.model.views.View.types.join;
                        })
                        .sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.view,
                    ''
                )
            };

            var sql = {
                menutype: 'folder',
                objecttype: CMDBuildUI.model.administration.MenuItem.types.view,
                text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromsql,
                href: Ext.String.format('administration/views/_new/{0}/false', CMDBuildUI.model.views.View.types.sql),
                leaf: false,
                index: 2,
                children: me.getRecordsAsSubmenu(
                    items
                        .filter(function (item) {
                            return item.get('type') === CMDBuildUI.model.views.View.types.sql;
                        })
                        .sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.view,
                    ''
                )
            };
            var schedule = {
                menutype: 'folder',
                objecttype: CMDBuildUI.model.administration.MenuItem.types.view,
                text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromschedule,
                href: Ext.String.format(
                    'administration/views/_new/{0}/false',
                    CMDBuildUI.model.views.View.types.calendar
                ),
                leaf: false,
                index: 3,
                children: me.getRecordsAsSubmenu(
                    items
                        .filter(function (item) {
                            return item.get('type') === CMDBuildUI.model.views.View.types.calendar;
                        })
                        .sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.view,
                    ''
                )
            };

            var viewsMenu = {
                menutype: 'folder',
                objecttype: CMDBuildUI.model.administration.MenuItem.types.view,
                index: index,
                text: CMDBuildUI.locales.Locales.administration.navigation.views,
                href: Ext.String.format('administration/views/_new', CMDBuildUI.model.views.View.types.filter),
                leaf: false,
                children: [filter, joind, sql, schedule]
            };
            deferred.resolve({
                index: index,
                menu: viewsMenu
            });
        });

        return deferred.promise;
    },

    /**
     * 5 - Create new folder for search filter in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendSearchFiltersAdminMenu: function (index) {
        var me = this;
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Stores.loadSearchfiltersStore().then(function (items) {
            var searchfiltersMenu = {
                menutype: 'folder',
                index: index,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.searchfilter,
                text: CMDBuildUI.locales.Locales.administration.navigation.searchfilters,
                href: 'administration/searchfilters/_new/false',
                leaf: false,
                children: me.getRecordsAsSubmenu(
                    items.sort(me.sortByDescription).sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.searchfilter,
                    ''
                )
            };
            deferred.resolve({
                index: index,
                menu: searchfiltersMenu
            });
        });

        return deferred.promise;
    },
    /**
     * 6 - Create new folder for dashboards in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendDashboardsAdminMenu: function (index) {
        var me = this;
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Stores.loadDashboardsStore().then(function (items) {
            var dashboardsMenu = {
                menutype: 'folder',
                index: index,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.dashboard,
                text: CMDBuildUI.locales.Locales.administration.navigation.dashboards,
                href: 'administration/dashboards',
                leaf: false,
                children: me.getRecordsAsSubmenu(
                    items.sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.dashboard,
                    ''
                )
            };

            deferred.resolve({
                index: index,
                menu: dashboardsMenu
            });
        });

        return deferred.promise;
    },

    /**
     * 7 - Create new folder for custom pages in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendCustomPagesAdminMenu: function (index) {
        var me = this;
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Stores.loadCustomPagesStore().then(function (items) {
            var customPagesDesktopMenu = {
                menutype: 'folder',
                index: index,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.custompage,
                text: CMDBuildUI.locales.Locales.administration.common.labels.desktop,
                leaf: false,
                href: 'administration/custompages_empty/default/false',
                children: me.getRecordsAsSubmenu(
                    items
                        .filter(function (item) {
                            return (
                                item.get('device') === CMDBuildUI.model.custompages.CustomPage.device['default'] ||
                                item.get('device') === 'any'
                            ); // TODO: remove any after #3051
                        })
                        .sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.custompage,
                    ''
                )
            };
            var customPagesMobileMenu = {
                menutype: 'folder',
                index: index,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.custompage,
                text: CMDBuildUI.locales.Locales.administration.common.labels.mobile,
                leaf: false,
                href: 'administration/custompages_empty/mobile/false',
                children: me.getRecordsAsSubmenu(
                    items
                        .filter(function (item) {
                            return item.get('device') === CMDBuildUI.model.custompages.CustomPage.device.mobile;
                        })
                        .sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.custompage,
                    ''
                )
            };
            var customPagesMenu = {
                menutype: 'folder',
                index: index,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.custompage,
                text: CMDBuildUI.locales.Locales.administration.navigation.custompages,
                leaf: false,
                href: 'administration/custompages',
                children: me.getRecordsAsSubmenu(
                    items.sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.custompage,
                    ''
                )
            };
            // append menu item to the store if has children
            deferred.resolve({
                index: index,
                menu: customPagesMenu
            });
        });

        return deferred.promise;
    },

    /**
     * 8 - Create new folder for custom components in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendCustomComponentsAdminMenu: function (index) {
        var me = this;
        var deferred = new Ext.Deferred();
        Ext.Promise.all([
            CMDBuildUI.util.Stores.loadCustomContextMenuStore(),
            CMDBuildUI.util.Stores.loadWidgetsStore(),
            CMDBuildUI.util.Stores.loadScriptsStore()
        ]).then(function (storesData) {
            var contextMenus = storesData[0],
                widgets = storesData[1],
                scripts = storesData[2];

            var contextMenuFolder = {
                menutype: 'folder',
                index: 0,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.contextmenu,
                text: CMDBuildUI.locales.Locales.administration.common.labels.contextmenuitems,
                leaf: false,
                href: 'administration/customcomponents_empty/contextmenu/false',
                children: me.getRecordsAsSubmenu(
                    contextMenus.sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.contextmenu,
                    ''
                )
            };

            var widgetMenuFolder = {
                menutype: 'folder',
                index: 1,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.widget,
                text: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formWidgets,
                leaf: false,
                href: 'administration/customcomponents_empty/widget/false',
                children: me.getRecordsAsSubmenu(
                    widgets.sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.widget,
                    ''
                )
            };

            var scriptMenuFolder = {
                menutype: 'folder',
                index: 1,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.script,
                text: CMDBuildUI.locales.Locales.administration.customcomponents.strings.scripts,
                leaf: false,
                href: 'administration/customcomponents_empty/script/false',
                children: me.getRecordsAsSubmenu(
                    scripts.sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.script,
                    ''
                )
            };
            var customcomponentMenu = {
                menutype: 'folder',
                index: index,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.customcomponent,
                text: CMDBuildUI.locales.Locales.administration.navigation.customcomponents,
                leaf: false,
                href: 'administration/customcomponents_empty',
                children: [contextMenuFolder, widgetMenuFolder, scriptMenuFolder]
            };
            // append menu item to the store if has children
            deferred.resolve({
                index: index,
                menu: customcomponentMenu
            });
        });

        return deferred.promise;
    },

    /**
     * 9 - Create new folder for search filter in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendReportsAdminMenu: function (index) {
        var me = this;
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Stores.loadReportsStore().then(function (items) {
            var menu = {
                menutype: 'folder',
                index: index,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.report,
                text: CMDBuildUI.locales.Locales.administration.navigation.reports,
                leaf: false,
                href: 'administration/reports',
                children: me.getRecordsAsSubmenu(
                    items.sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.report,
                    ''
                )
            };

            // append menu item to the store if has children
            deferred.resolve({
                index: index,
                menu: menu
            });
        });

        return deferred.promise;
    },
    /**
     * 10 - Create Menu tree
     */

    appendMenuAdminMenu: function (index) {
        var me = this;
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Stores.loadAdministrationMenusStore().then(function (items) {
            CMDBuildUI.util.Stores.loadGroupsStore().then(function (groups) {
                CMDBuildUI.util.Stores.loadMenuNavigationTreesStore().then(function (menuNavTrees) {
                    items.forEach(function (item, _index) {
                        if (item.get('description') === '_default') {
                            items[_index].data.description =
                                CMDBuildUI.locales.Locales.administration.common.strings.default;
                        } else {
                            var group = Ext.Array.findBy(groups, function (group) {
                                return group.get('name') === item.get('group');
                            });
                            try {
                                items[_index].data.description = group.get('description');
                            } catch (error) {
                                var msg = Ext.String.format(
                                    'Group {0} not found. Please delete the menu {1}',
                                    items[_index].get('group'),
                                    items[_index].getId()
                                );
                                CMDBuildUI.util.Logger.log(msg, CMDBuildUI.util.Logger.levels.warn);
                            }
                        }
                    });

                    var desktopMenu = {
                        menutype: 'folder',
                        index: index,
                        objecttype: CMDBuildUI.model.administration.MenuItem.types.menu,
                        text: CMDBuildUI.locales.Locales.administration.common.labels.desktop,
                        leaf: false,
                        href: CMDBuildUI.util.administration.helper.ApiHelper.client.getTheMenuUrl(
                            null,
                            CMDBuildUI.model.menu.Menu.device['default']
                        ),
                        children: me.getRecordsAsSubmenu(
                            items
                                .filter(function (item) {
                                    return (
                                        item.get('device') === CMDBuildUI.model.menu.Menu.device['default'] &&
                                        item.get('type') === CMDBuildUI.model.menu.Menu.types.navmenu
                                    );
                                })
                                .sort(me.sortByDescription),
                            CMDBuildUI.model.administration.MenuItem.types.menu,
                            ''
                        )
                    };

                    var navTreeMenu = {
                        menutype: 'folder',
                        index: index,
                        objecttype: CMDBuildUI.model.administration.MenuItem.types.navigationtree,
                        text: CMDBuildUI.locales.Locales.administration.navigation.navigationtrees,
                        href: 'administration/menunavigationtrees_empty/false',
                        leaf: false,
                        children: me.getRecordsAsSubmenu(
                            menuNavTrees.sort(me.sortByDescription),
                            CMDBuildUI.model.administration.MenuItem.types.menunavigationtree,
                            ''
                        )
                    };
                    var menuMenu = {
                        menutype: 'folder',
                        index: index,
                        objecttype: CMDBuildUI.model.administration.MenuItem.types.menu,
                        text: CMDBuildUI.locales.Locales.administration.navigation.menus,
                        href: CMDBuildUI.util.administration.helper.ApiHelper.client.getTheMenuUrl(null, null),
                        leaf: false,
                        children: []
                    };
                    menuMenu.children.push(desktopMenu);
                    if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.mobile.enabled)) {
                        var mobileMenu = {
                            menutype: 'folder',
                            index: index,
                            objecttype: CMDBuildUI.model.administration.MenuItem.types.menu,
                            text: CMDBuildUI.locales.Locales.administration.common.labels.mobile,
                            leaf: false,
                            href: CMDBuildUI.util.administration.helper.ApiHelper.client.getTheMenuUrl(
                                null,
                                CMDBuildUI.model.menu.Menu.device.mobile
                            ),
                            children: me.getRecordsAsSubmenu(
                                items
                                    .filter(function (item) {
                                        return item.get('device') === CMDBuildUI.model.menu.Menu.device.mobile;
                                    })
                                    .sort(me.sortByDescription),
                                CMDBuildUI.model.administration.MenuItem.types.menu,
                                ''
                            )
                        };

                        menuMenu.children.push(mobileMenu);
                    }
                    menuMenu.children.push(navTreeMenu);

                    // append menu item to the store if has children
                    deferred.resolve({
                        index: index,
                        menu: menuMenu
                    });
                });
            });
        });
        return deferred.promise;
    },

    /**
     * 11 - Create new folder for navigation tree in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendNavigationTreeAdminMenu: function (index) {
        var me = this;
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Stores.loadNavigationTreesStore().then(function (items) {
            Ext.Array.forEach(items, function (item) {
                item.set('name', item.get('_id'));
                if (!item.get('description')) {
                    item.set('description', item.get('_id'));
                }
            });
            items = items.filter(function (item) {
                return item.get('_id') !== 'gisnavigation' && item.get('_id') !== 'bimnavigation';
            });

            items.sort(me.sortByDescription);
            var menu = {
                menutype: 'folder',
                index: index,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.navigationtree,
                text: CMDBuildUI.locales.Locales.administration.navigation.navigationtrees,
                href: 'administration/navigationtrees_empty/false',
                leaf: false,
                children: me.getRecordsAsSubmenu(
                    items.sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.navigationtree,
                    ''
                )
            };

            // append menu item to the store if has children
            deferred.resolve({
                index: index,
                menu: menu
            });
        });
        return deferred.promise;
    },

    /**
     * 12 - Create new folder for dependency tree in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendDependencyTreeAdminMenu: function (index) {
        const me = this;
        const deferred = new Ext.Deferred();
        const proxy = Ext.getStore('dependencyTrees.DependencyTrees').getProxy();
        const extraParams = proxy.getExtraParams();
        extraParams.treeMode = 'flat';
        proxy.setExtraParams(extraParams);

        CMDBuildUI.util.Stores.loadDependencyTreesStore().then(function (items) {
            Ext.Array.forEach(items, function (item) {
                item.set('name', item.get('_id'));
                if (!item.get('description')) {
                    item.set('description', item.get('_id'));
                }
            });
            items = items.filter(function (item) {
                return item.get('_id') !== 'gisnavigation' && item.get('_id') !== 'bimnavigation';
            });

            items.sort(me.sortByDescription);
            const menu = {
                menutype: 'folder',
                index: index,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.dependencytree,
                text: CMDBuildUI.locales.Locales.administration.navigation.dependencytrees,
                href: 'administration/dependencytrees_empty/false',
                leaf: false,
                children: me.getRecordsAsSubmenu(
                    items.sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.dependencytree,
                    ''
                )
            };

            // append menu item to the store if has children
            deferred.resolve({
                index: index,
                menu: menu
            });
        });
        return deferred.promise;
    },

    /**
     * 13 - Create new folder for navigation tree in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendGroupsAndPermissionsAdminMenu: function (index) {
        var me = this;
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Stores.loadGroupsStore().then(function (items) {
            var groupsMenu = {
                menutype: 'folder',
                index: index,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.groupsandpermissions,
                text: CMDBuildUI.locales.Locales.administration.navigation.groupsandpermissions,
                href: 'administration/groupsandpermissions_empty',
                leaf: false,
                children: me.getRecordsAsSubmenu(
                    items.sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.groupsandpermissions,
                    ''
                )
            };
            deferred.resolve({
                index: index,
                menu: groupsMenu
            });
        });
        return deferred.promise;
    },

    /**
     * 14 - Create new folder for users in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendUsersAdminMenu: function (index) {
        var deferred = new Ext.Deferred();

        var userItem = {
            menutype: 'user',
            index: 0,
            objecttype: 'user',
            objectDescription: CMDBuildUI.locales.Locales.administration.navigation.users,
            leaf: true,
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('user', 'solid'),
            href: 'administration/users'
        };
        var menu = {
            menutype: 'folder',
            index: index,
            objecttype: 'user',
            text: CMDBuildUI.locales.Locales.administration.navigation.users,
            leaf: false,
            children: [userItem],
            href: 'administration/users_empty'
        };
        deferred.resolve({
            index: index,
            menu: menu
        });

        return deferred.promise;
    },

    /**
     * 15 - Create new folder for users in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendTasksAdminMenu: function (index) {
        var deferred = new Ext.Deferred();

        var workflowEnabled = CMDBuildUI.util.helper.Configurations.get(
            CMDBuildUI.model.Configuration.processes.enabled
        );
        var gisEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.enabled);
        var menu = {
            menutype: 'folder',
            index: index,
            objecttype: CMDBuildUI.model.administration.MenuItem.types.task,
            text: CMDBuildUI.locales.Locales.administration.navigation.taskmanager,
            leaf: false,
            href: 'administration/tasks',
            children: [
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.task,
                    index: 1,
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.task,
                    text: CMDBuildUI.locales.Locales.administration.tasks.texts.reademails,
                    leaf: true,
                    href: 'administration/tasks/emailService'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.task,
                    index: 2,
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.task,
                    text: CMDBuildUI.locales.Locales.administration.tasks.sendemail,
                    leaf: true,
                    href: 'administration/tasks/sendemail'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.task,
                    index: 3,
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.task,
                    text: CMDBuildUI.locales.Locales.administration.importexport.texts.importexportfile,
                    leaf: true,
                    href: 'administration/tasks/import_export'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.task,
                    index: 4,
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.task,
                    text: CMDBuildUI.locales.Locales.administration.importexport.texts.importdatabase,
                    leaf: true,
                    href: 'administration/tasks/etl/database'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.task,
                    index: 5,
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.task,
                    text: CMDBuildUI.locales.Locales.administration.navigation.importgis,
                    leaf: true,
                    href: 'administration/tasks/etl/cad',
                    rowCls: !gisEnabled ? 'disabled' : '',
                    selectable: gisEnabled
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.task,
                    index: 5,
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.task,
                    text: CMDBuildUI.locales.Locales.administration.importexport.texts.importexportifcgatetemplate,
                    leaf: true,
                    href: 'administration/tasks/etl/ifc'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.task,
                    index: 6,
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.task,
                    text: CMDBuildUI.locales.Locales.administration.classes.texts.startworkflow,
                    leaf: true,
                    href: 'administration/tasks/workflow',
                    rowCls: !workflowEnabled ? 'disabled' : '',
                    selectable: workflowEnabled
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.task,
                    index: 7,
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.task,
                    text: CMDBuildUI.locales.Locales.administration.navigation.servicebus,
                    leaf: true,
                    href: 'administration/tasks/trigger'
                }
            ]
        };

        deferred.resolve({
            index: index,
            menu: menu
        });

        return deferred.promise;
    },

    /**
     * 16 - Create new folder for bus descriptors in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendBusAdminMenu: function (index) {
        var me = this;
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Stores.loadBusDescriptorStore().then(function (items) {
            var busMenu = {
                menutype: 'folder',
                index: 0,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.bus,
                text: CMDBuildUI.locales.Locales.administration.navigation.busdescriptors,
                href: CMDBuildUI.util.administration.helper.ApiHelper.client.getBusDescriptorUrl(), // 'administration/bus/descriptors_empty/false
                leaf: false,
                children: me.getRecordsAsSubmenu(
                    items
                        .filter(function (item) {
                            //TODO filter by some property
                            return !Ext.isEmpty(item.get('description'));
                        })
                        .sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.bus,
                    ''
                )
            };

            var busSettingsMenu = {
                menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                index: 1,
                objecttype: 'setup',
                text: CMDBuildUI.locales.Locales.administration.navigation.settings,
                leaf: true,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('wrench', 'solid'),
                href: 'administration/setup/servicebus',
                rowCls: !me.canViewSettings() ? 'disabled' : '',
                selectable: me.canViewSettings()
            };

            var serviceBus = {
                menutype: 'folder',
                index: index,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.bus,
                text: CMDBuildUI.locales.Locales.administration.navigation.servicebus,
                href: 'administration/bus',
                leaf: false,
                children: [busMenu, busSettingsMenu]
            };
            deferred.resolve({
                index: index,
                menu: serviceBus
            });
        });

        return deferred.promise;
    },

    /**
     * 17 - Create new folder for eamils in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendEmailsAdminMenu: function (index) {
        var me = this;
        var deferred = new Ext.Deferred();
        var menu = {
            menutype: 'folder',
            index: index,
            objecttype: CMDBuildUI.model.administration.MenuItem.types.email,
            text: CMDBuildUI.locales.Locales.administration.navigation.notifications,
            leaf: false,
            href: 'administration/email_empty',
            children: [
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.email,
                    index: 1,
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.email,
                    text: CMDBuildUI.locales.Locales.administration.emails.emailaccounts,
                    leaf: true,
                    href: 'administration/email/accounts'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.folder,
                    index: 2,
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.email,
                    text: CMDBuildUI.locales.Locales.administration.emails.templates,
                    leaf: false,
                    href: 'administration/notifications/templates/all',
                    children: [
                        {
                            menutype: CMDBuildUI.model.administration.MenuItem.types.email,
                            index: 0,
                            objecttype: CMDBuildUI.model.administration.MenuItem.types.email,
                            text: CMDBuildUI.locales.Locales.administration.emails.email,
                            leaf: true,
                            href: 'administration/notifications/templates/email'
                        },
                        {
                            menutype: CMDBuildUI.model.administration.MenuItem.types.notification,
                            index: 1,
                            objecttype: CMDBuildUI.model.administration.MenuItem.types.notification,
                            text: CMDBuildUI.locales.Locales.administration.emails.inappnotification,
                            leaf: true,
                            href: 'administration/notifications/templates/inappnotification'
                        },
                        {
                            menutype: CMDBuildUI.model.administration.MenuItem.types.notification,
                            index: 2,
                            objecttype: CMDBuildUI.model.administration.MenuItem.types.notification,
                            text: CMDBuildUI.locales.Locales.administration.emails.mobilenotification,
                            leaf: true,
                            href: 'administration/notifications/templates/mobilenotification'
                        }
                    ]
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.email,
                    index: 3,
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.email,
                    text: CMDBuildUI.locales.Locales.administration.emails.emailsignatures,
                    leaf: true,
                    href: 'administration/email/signatures'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.email,
                    index: 4,
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.email,
                    text: CMDBuildUI.locales.Locales.administration.emails.emailqueue,
                    leaf: true,
                    href: 'administration/email/queue'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.email,
                    index: 5,
                    objecttype: CMDBuildUI.model.administration.MenuItem.types.email,
                    text: CMDBuildUI.locales.Locales.administration.emails.emailerrrors,
                    leaf: true,
                    href: 'administration/email/errors'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 6,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.settings,
                    leaf: true,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('wrench', 'solid'),
                    href: 'administration/setup/notifications',
                    rowCls: !me.canViewSettings() ? 'disabled' : '',
                    selectable: me.canViewSettings()
                }
            ]
        };
        deferred.resolve({
            index: index,
            menu: menu
        });

        return deferred.promise;
    },

    /**
     * 18 - Create new folder for schedule in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendScheduleAdminMenu: function (index) {
        var deferred = new Ext.Deferred();
        var schedulerEnabled = CMDBuildUI.util.helper.Configurations.get(
            CMDBuildUI.model.Configuration.scheduler.enabled
        );
        // only for DEBUG
        // schedulerEnabled = typeof schedulerEnabled === 'undefined' ? true : schedulerEnabled;

        var scheduleItem = {
            menutype: CMDBuildUI.model.administration.MenuItem.types.schedule,
            index: 0,
            objecttype: CMDBuildUI.model.administration.MenuItem.types.schedule,
            objectDescription: CMDBuildUI.locales.Locales.administration.schedules.ruledefinitions,
            leaf: true,
            iconCls: 'cmdbuildicon-stopwatch',
            href: 'administration/schedules/ruledefinitions',
            rowCls: !schedulerEnabled ? 'disabled' : '',
            selectable: schedulerEnabled
        };

        var scheduleSettings = {
            menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
            index: 1,
            objecttype: 'setup',
            text: CMDBuildUI.locales.Locales.administration.navigation.settings,
            leaf: true,
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('wrench', 'solid'),
            href: 'administration/schedules/settings',
            rowCls: !this.canViewSettings() ? 'disabled' : '',
            selectable: this.canViewSettings()
        };
        var menu = {
            menutype: 'folder',
            index: index,
            objecttype: CMDBuildUI.model.administration.MenuItem.types.schedule,
            text: CMDBuildUI.locales.Locales.administration.navigation.schedules,
            leaf: false,
            children: [scheduleItem, scheduleSettings],
            href: 'administration/schedules_empty'
        };
        deferred.resolve({
            index: index,
            menu: menu
        });

        return deferred.promise;
    },

    /**
     * 19 - Create new folder for gis in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendGisAdminMenu: function (index) {
        var me = this;
        var deferred = new Ext.Deferred();
        var gisEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.enabled);
        var canViewMenus = me.adminCan(me.adminRolePrivileges.admin_menus_view);

        CMDBuildUI.util.Stores.loadAdministrationMenusStore().then(function (menus) {
            var layerMenu = menus.filter(function (item) {
                return item.get('type') === CMDBuildUI.model.menu.Menu.types.gismenu;
            });
            var children = [
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.gis,
                    index: 1,
                    objecttype: 'gis',
                    text: CMDBuildUI.locales.Locales.administration.gis.manageicons,
                    leaf: true,
                    href: 'administration/gis/manageicons',
                    rowCls: !gisEnabled ? 'disabled' : '',
                    selectable: gisEnabled
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.gis,
                    index: 2,
                    objecttype: 'gis',
                    text: CMDBuildUI.locales.Locales.administration.gis.externalservices,
                    leaf: true,
                    href: 'administration/gis/externalservices',
                    rowCls: !gisEnabled ? 'disabled' : '',
                    selectable: gisEnabled
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.gis,
                    index: 3,
                    objecttype: 'gis',
                    text: CMDBuildUI.locales.Locales.administration.gis.layersorder,
                    leaf: true,
                    href: 'administration/gis/layersorder',
                    rowCls: !gisEnabled ? 'disabled' : '',
                    selectable: gisEnabled
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.gis,
                    index: 4,
                    objecttype: 'gis',
                    text: CMDBuildUI.locales.Locales.administration.navigation.gismenu,
                    leaf: true,
                    href: Ext.String.format(
                        'administration/gis/gismenu/{0}',
                        layerMenu && layerMenu.length ? layerMenu[0].get('_id') : 'false'
                    ),
                    rowCls: !gisEnabled || !canViewMenus ? 'disabled' : '',
                    selectable: gisEnabled && canViewMenus
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.gis,
                    index: 5,
                    objecttype: 'gis',
                    text: CMDBuildUI.locales.Locales.administration.navigation.gisnavigation,
                    leaf: true,
                    href: 'administration/gis/gisnavigation',
                    rowCls: !gisEnabled ? 'disabled' : '',
                    selectable: gisEnabled
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.gis,
                    index: 6,
                    objecttype: 'gis',
                    text: CMDBuildUI.locales.Locales.administration.gis.thematisms,
                    leaf: true,
                    href: 'administration/gis/thematism',
                    rowCls: !gisEnabled ? 'disabled' : '',
                    selectable: gisEnabled
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 7,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.settings,
                    leaf: true,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('wrench', 'solid'),
                    href: 'administration/setup/gis',
                    rowCls: !me.canViewSettings() ? 'disabled' : '',
                    selectable: me.canViewSettings()
                }
            ];

            var menu = {
                menutype: 'folder',
                index: index,
                objecttype: 'gis',
                text: CMDBuildUI.locales.Locales.administration.navigation.gis,
                href: 'administration/gis_empty',
                children: children,
                rowCls: !me.canViewSettings() && !gisEnabled ? 'disabled' : '',
                selectable: me.canViewSettings() || gisEnabled
            };

            deferred.resolve({
                index: index,
                menu: menu
            });
        });

        return deferred.promise;
    },

    /**
     * 20 - Create new folder for bim in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendBimAdminMenu: function (index) {
        var deferred = new Ext.Deferred();
        var bimEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.bim.enabled);
        var children = [
            {
                menutype: CMDBuildUI.model.administration.MenuItem.types.bim,
                index: 1,
                objecttype: 'bim',
                text: CMDBuildUI.locales.Locales.administration.bim.projects,
                leaf: true,
                href: 'administration/bim/projects',
                rowCls: !bimEnabled ? 'disabled' : '',
                selectable: bimEnabled
            },
            {
                menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                index: 3,
                objecttype: 'setup',
                text: CMDBuildUI.locales.Locales.administration.navigation.settings,
                leaf: true,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('wrench', 'solid'),
                href: 'administration/setup/bim',
                rowCls: !this.canViewSettings() ? 'disabled' : '',
                selectable: this.canViewSettings()
            }
        ];

        var menu = {
            menutype: 'folder',
            index: index,
            objecttype: 'bim',
            text: CMDBuildUI.locales.Locales.administration.navigation.bim,
            href: 'administration/bim_empty',
            children: children,
            rowCls: !this.canViewSettings() && !bimEnabled ? 'disabled' : '',
            selectable: this.canViewSettings() || bimEnabled
        };

        deferred.resolve({
            index: index,
            menu: menu
        });

        return deferred.promise;
    },

    /**
     * 21 - Create new folder for language in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendLanguagesMenu: function (index) {
        var deferred = new Ext.Deferred();
        var canViewSettings = this.adminCan(this.adminRolePrivileges.admin_sysconfig_view);
        var menu = {
            menutype: 'folder',
            index: index,
            objecttype: 'localization',
            text: CMDBuildUI.locales.Locales.administration.navigation.languages,
            leaf: false,
            href: 'administration/localization_empty',
            children: [
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.localization,
                    index: 1,
                    objecttype: 'localization',
                    text: CMDBuildUI.locales.Locales.administration.localizations.configuration,
                    leaf: true,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('globe-americas', 'solid'),
                    href: 'administration/localizations/configuration',
                    rowCls: !canViewSettings ? 'disabled' : '',
                    selectable: canViewSettings
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.localization,
                    index: 2,
                    objecttype: 'localization',
                    text: CMDBuildUI.locales.Locales.administration.localizations.localization,
                    leaf: true,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('globe-americas', 'solid'),
                    href: 'administration/localizations/localization'
                }
            ]
        };

        deferred.resolve({
            index: index,
            menu: menu
        });

        return deferred.promise;
    },

    /**
     * 22 - Create new folder for import export in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendImportExportMenu: function (index) {
        var me = this;
        var deferred = new Ext.Deferred();

        Ext.Promise.all([
            CMDBuildUI.util.Stores.loadImportExportTemplatesStore(),
            CMDBuildUI.util.Stores.loadETLGatesStore()
        ]).then(function (results) {
            var tpls = results[0],
                gates = results[1];

            var datatemplates = {
                menutype: CMDBuildUI.model.administration.MenuItem.types.folder,
                index: 1,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.importexport,
                text: CMDBuildUI.locales.Locales.administration.navigation.datatemplate,
                leaf: false,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('list', 'solid'),
                href: 'administration/importexport/datatemplates_empty/false',
                children: me.getRecordsAsSubmenu(tpls, CMDBuildUI.model.administration.MenuItem.types.importexport, '')
            };

            var databasegates = {
                menutype: 'folder',
                index: 2,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.databasegatetemplate,
                text: CMDBuildUI.locales.Locales.administration.navigation.databasegatetemplate,
                leaf: false,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('list', 'solid'),
                href: CMDBuildUI.util.administration.helper.ApiHelper.client.getGateTemplateUrl(
                    CMDBuildUI.model.importexports.Gate.gateType.database
                )
            };

            var databaseitems = Ext.Array.filter(gates, function (item) {
                return item.get('config').tag === CMDBuildUI.model.importexports.Gate.gateType.database;
            });
            databasegates.children = me.getRecordsAsSubmenu(
                databaseitems.sort(me.sortByDescription),
                CMDBuildUI.model.administration.MenuItem.types.databasegatetemplate,
                ''
            );

            var gisgates = {
                menutype: 'folder',
                index: 3,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.gisgatetemplate,
                text: CMDBuildUI.locales.Locales.administration.navigation.gisgatetemplate,
                leaf: false,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('list', 'solid'),
                href: CMDBuildUI.util.administration.helper.ApiHelper.client.getGateTemplateUrl(
                    CMDBuildUI.model.importexports.Gate.gateType.cad
                )
            };
            var caditems = Ext.Array.filter(gates, function (item) {
                return item.get('config').tag === CMDBuildUI.model.importexports.Gate.gateType.cad;
            });
            gisgates.children = me.getRecordsAsSubmenu(
                caditems.sort(me.sortByDescription),
                CMDBuildUI.model.administration.MenuItem.types.gisgatetemplate,
                ''
            );
            var ifcgates = {
                menutype: 'folder',
                index: 4,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.ifcgatetemplate,
                text: CMDBuildUI.locales.Locales.administration.navigation.ifcgatetemplate,
                leaf: false,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('list', 'solid'),
                href: CMDBuildUI.util.administration.helper.ApiHelper.client.getGateTemplateUrl(
                    CMDBuildUI.model.importexports.Gate.gateType.ifc
                )
            };
            var ifcitems = Ext.Array.filter(gates, function (item) {
                return item.get('config').tag === CMDBuildUI.model.importexports.Gate.gateType.ifc;
            });
            ifcgates.children = me.getRecordsAsSubmenu(
                ifcitems.sort(me.sortByDescription),
                CMDBuildUI.model.administration.MenuItem.types.ifcgatetemplate,
                ''
            );

            var menu = {
                menutype: 'folder',
                index: index,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.importexport,
                text: CMDBuildUI.locales.Locales.administration.navigation.importexports,
                leaf: false,
                href: 'administration/importexport_empty',
                children: [datatemplates, databasegates, gisgates, ifcgates]
            };
            deferred.resolve({
                index: index,
                menu: menu
            });
        });

        return deferred.promise;
    },

    /**
     * 23 - Create new folder for eamils in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendSystemManagementAdminMenu: function (index) {
        var deferred = new Ext.Deferred();
        var menu = {
            menutype: 'folder',
            index: index,
            objecttype: CMDBuildUI.model.administration.MenuItem.types.servermanagement, // TODO: define
            text: CMDBuildUI.locales.Locales.administration.navigation.servermanagement,
            leaf: false,
            href: 'administration/setup/servermanagement', // TODO: define in main controller and routings
            children: [
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 0,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.busmessages,
                    leaf: true,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('th-list', 'solid'),
                    href: 'administration/bus/messages'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 1,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.jobmessages,
                    leaf: true,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('th-list', 'solid'),
                    href: 'administration/tasks/jobruns'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 2,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.systemconfig.logs,
                    leaf: true,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('th-list', 'solid'),
                    href: 'administration/setup/logs'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 3,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.system,
                    leaf: true,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('wrench', 'solid'),
                    href: 'administration/setup/system'
                }
            ]
        };
        deferred.resolve({
            index: index,
            menu: menu
        });

        return deferred.promise;
    },

    /**
     * 24 - Create new folder for configuration in menu tree
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendSettingsMenu: function (index) {
        var deferred = new Ext.Deferred();
        var menu = {
            menutype: 'folder',
            index: index,
            objecttype: 'setup',
            text: CMDBuildUI.locales.Locales.administration.navigation.systemconfig,
            leaf: false,
            href: 'administration/setup_empty',
            children: [
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 1,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.generaloptions,
                    leaf: true,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('wrench', 'solid'),
                    href: 'administration/setup/generaloptions'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 2,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.webhooks,
                    leaf: true,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('wrench', 'solid'),
                    href: 'administration/webhooks'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 3,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.systemconfig.authentication,
                    leaf: true,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('wrench', 'solid'),
                    href: 'administration/setup/authentication'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 4,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.multitenant,
                    leaf: true,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('wrench', 'solid'),
                    href: 'administration/setup/multitenant'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 5,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.workflow,
                    leaf: true,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('wrench', 'solid'),
                    href: 'administration/setup/workflow'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 6,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.common.actions.relationchart,
                    leaf: true,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('wrench', 'solid'),
                    href: 'administration/setup/relationchart'
                },
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 7,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.subscription,
                    leaf: true,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('wrench', 'solid'),
                    href: 'administration/setup/subscription'
                }
            ]
        };
        deferred.resolve({
            index: index,
            menu: menu
        });

        return deferred.promise;
    },

    /**
     * Create new folder for plugin manager in menu tree
     * @param {String} index
     * @returns {Ext.Deferred} Promise object of {CMDBuildUI.model.menu.MenuItem}
     */
    appendPluginManagerMenu: function (index) {
        const me = this;
        const deferred = new Ext.Deferred();
        CMDBuildUI.util.Stores.loadPluginManagerStore().then(function (items) {
            const plugins = {
                menutype: 'folder',
                index: index,
                objecttype: CMDBuildUI.model.administration.MenuItem.types.pluginmanager,
                text: CMDBuildUI.locales.Locales.administration.navigation.plugin,
                href: 'administration/pluginmanager_empty',
                leaf: false,
                children: me.getRecordsAsSubmenu(
                    items.sort(me.sortByDescription),
                    CMDBuildUI.model.administration.MenuItem.types.pluginmanager,
                    ''
                )
            };
            deferred.resolve({
                index: index,
                menu: plugins
            });
        });

        return deferred.promise;
    },

    appendAiMenu: function (index) {
        const me = this;
        const deferred = new Ext.Deferred();

        const menu = {
            menutype: 'folder',
            index: index,
            objecttype: 'setup',
            text: CMDBuildUI.locales.Locales.administration.navigation.ai,
            leaf: false,
            href: 'administration/ai_empty',
            children: [
                {
                    menutype: CMDBuildUI.model.administration.MenuItem.types.setup,
                    index: 1,
                    objecttype: 'setup',
                    text: CMDBuildUI.locales.Locales.administration.navigation.configurations,
                    leaf: true,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('wrench', 'solid'),
                    href: 'administration/setup/ai'
                }
            ]
        };

        deferred.resolve({
            index: index,
            menu: menu
        });

        return deferred.promise;
    },

    selectNode: function (key, value) {
        var navTree = Ext.getCmp('administrationNavigationTree');
        if (navTree) {
            var store = navTree.getStore();
            var vm = navTree.getViewModel();
            var record = store.findNode(key, value);
            // if (!record) {
            //     Ext.Object.eachValue(store.byIdMap, function (node) {
            //         if (node && node.get('alternativeHref') && node.get('alternativeHref').indexOf(value) > -1) {
            //             record = node;
            //             return false;
            //         }
            //     });
            // }
            vm.set('selected', record);
        }
    },

    selectAndRedirectToRecordBy: function (key, value, controller) {
        this.selectNode(key, value);
        controller.redirectTo(value, true);
    },

    changeRecordBy: function (key, value, newDescription, controller) {
        var navTree = Ext.getCmp('administrationNavigationTree');
        var store = navTree.getStore();
        var record = store.findNode(key, value);
        if (record) {
            record.set('text', newDescription);
            var sorted = record.parentNode.childNodes.sort(this.sortByText);
            sorted.forEach(function (item, index) {
                item.set('index', index);
            });
            record.parentNode.childNodes = sorted;
            record.parentNode.data.children = sorted;
            store.sort('index', 'ASC');
        }
    },

    removeRecordBy: function (key, value, nextUrl, controller) {
        var navTree = Ext.getCmp('administrationNavigationTree');
        var vm = navTree.getViewModel();
        var store = navTree.getStore();
        var record = store.findNode(key, value);
        if (record) {
            record.remove();
        }
        if (vm.get('selected') === record) {
            if (controller && nextUrl) {
                controller.redirectTo(nextUrl, true);
            }
            var currentNode = store.findNode('href', Ext.History.getToken());

            if (!currentNode) {
                currentNode = this.getFirstSelectableMenuItem(store.getRootNode().childNodes);
            }
        }

        vm.set('selected', currentNode);
    },

    onMenuStoreReady: function (store, callback) {
        var navTree;
        if (!store) {
            navTree = Ext.getCmp('administrationNavigationTree');
            store = navTree.getStore();
        }
        var currentNode = store.findNode('href', Ext.History.getToken());

        if (!currentNode) {
            currentNode = this.getFirstSelectableMenuItem(store.getRootNode().childNodes);
        }
        if (navTree && currentNode) {
            var vm = navTree.getViewModel();
            vm.set('selected', currentNode);
        }
        if (typeof callback === 'function') {
            callback();
        }
    },

    /**
     * Return the json definition for records as tree.
     * @param {Ext.data.Model[]} records The plain list of items.
     * @param {String} menutype The menu type to use for these records.
     * @param {String} parentmenu The name of the parent item.
     *
     * @returns {Object[]} A list of CMDBuild.model.MenuItem definitions.
     */
    getRecordsAsSubmenu: function (records, menutype, parentname, config) {
        var output = [];
        var me = this;

        var frecords = Ext.Array.filter(records, function (item) {
            return item.getData().hasOwnProperty('parent') && item.getData().parent === parentname;
        });
        switch (menutype) {
            case CMDBuildUI.model.administration.MenuItem.types.domain:
            case CMDBuildUI.model.administration.MenuItem.types.menu:
            case CMDBuildUI.model.administration.MenuItem.types.report:
            case CMDBuildUI.model.administration.MenuItem.types.groupsandpermissions:
            case CMDBuildUI.model.administration.MenuItem.types.custompage:
            case CMDBuildUI.model.administration.MenuItem.types.contextmenu:
            case CMDBuildUI.model.administration.MenuItem.types.widget:
            case CMDBuildUI.model.administration.MenuItem.types.script:
            case CMDBuildUI.model.administration.MenuItem.types.dependencytree:
            case CMDBuildUI.model.administration.MenuItem.types.navigationtree:
            case CMDBuildUI.model.administration.MenuItem.types.menunavigationtree:
            case CMDBuildUI.model.administration.MenuItem.types.searchfilter:
            case CMDBuildUI.model.administration.MenuItem.types.dashboard:
            case CMDBuildUI.model.administration.MenuItem.types.homepage:
            case CMDBuildUI.model.administration.MenuItem.types.view:
            case CMDBuildUI.model.administration.MenuItem.types.importexport:
            case CMDBuildUI.model.administration.MenuItem.types.gisgatetemplate:
            case CMDBuildUI.model.administration.MenuItem.types.ifcgatetemplate:
            case CMDBuildUI.model.administration.MenuItem.types.databasegatetemplate:
            case CMDBuildUI.model.administration.MenuItem.types.bus:
            case CMDBuildUI.model.administration.MenuItem.types.pluginmanager:
                frecords = records;
                break;
            default:
                break;
        }

        for (var i = 0; i < frecords.length; i++) {
            var record = frecords[i].getData();
            var menuitem = Ext.Object.merge(
                {},
                {
                    menutype: menutype,
                    index: i,
                    objecttype: decodeURI(record.name),
                    text: record.description,
                    leaf: true
                },
                config || {}
            );

            switch (menutype) {
                case CMDBuildUI.model.administration.MenuItem.types.klass:
                    menuitem.href = 'administration/classes/' + menuitem.objecttype;
                    if (record.prototype) {
                        menuitem.leaf = false;
                        menuitem.children = me.getRecordsAsSubmenu(records, menutype, record.name);
                    }
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.lookuptype:
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid');
                    if (record.prototype) {
                        menuitem.leaf = false;
                        menuitem.children = me.getRecordsAsSubmenu(records, menutype, record.name);
                    }
                    menuitem.objecttype = CMDBuildUI.util.Utilities.stringToHex(menuitem.objecttype);
                    menuitem.href = 'administration/lookup_types/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.dmsmodel:
                    menuitem.href = 'administration/dmsmodels/' + menuitem.objecttype;
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('file-alt', 'regular');
                    if (record.prototype) {
                        menuitem.leaf = false;
                        menuitem.children = me.getRecordsAsSubmenu(records, menutype, record.name);
                    }
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.dmscategory:
                    menuitem.objecttype = CMDBuildUI.util.Utilities.stringToHex(menuitem.objecttype);
                    menuitem.href = 'administration/dmscategories/' + menuitem.objecttype;
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid');
                    if (record.prototype) {
                        menuitem.leaf = false;
                        menuitem.children = me.getRecordsAsSubmenu(records, menutype, record.name);
                    }
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.domain:
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid');
                    menuitem.href = 'administration/domains/' + menuitem.objecttype;
                    if (record.prototype) {
                        menuitem.leaf = false;
                        menuitem.children = me.getRecordsAsSubmenu(records, menutype, record.name);
                    }
                    break;

                case CMDBuildUI.model.administration.MenuItem.types.groupsandpermissions:
                    menuitem.objecttype = record._id;
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('users', 'solid');
                    menuitem.href = 'administration/groupsandpermissions/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.menu:
                    menuitem.objecttype = record._id; // TODO: use getRecordId() not work!!
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('users', 'solid');
                    menuitem.href = 'administration/menus/' + record.device + '/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.process:
                    menuitem.objecttype = record._id; // TODO: use getRecordId() not work!!
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('cog', 'solid');
                    menuitem.href = 'administration/processes/' + menuitem.objecttype;
                    if (record.prototype) {
                        menuitem.leaf = false;
                        menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('cogs', 'solid');
                        menuitem.children = me.getRecordsAsSubmenu(records, menutype, record.name);
                    }
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.report:
                    menuitem.objecttype = record._id; // TODO: use getRecordId() not work!!
                    menuitem.iconCls =
                        CMDBuildUI.util.helper.IconHelper.getIconId('copy', 'regular') + ' fa-flip-horizontal';
                    menuitem.href = 'administration/reports/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.custompage:
                    menuitem.objecttype = record._id;
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('code', 'solid');
                    menuitem.href = 'administration/custompages/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.dashboard:
                    menuitem.objecttype = record._id;
                    menuitem.href = 'administration/dashboards/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.homepage:
                    menuitem.objecttype = record._id;
                    menuitem.href = 'administration/homepages/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.contextmenu:
                    menuitem.objecttype = record._id;
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('code', 'solid');
                    menuitem.href = 'administration/customcomponents/contextmenu/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.widget:
                    menuitem.objecttype = record._id;
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('code', 'solid');
                    menuitem.href = 'administration/customcomponents/widget/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.script:
                    menuitem.objecttype = record._id;
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('code', 'solid');
                    menuitem.href = 'administration/customcomponents/script/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.dependencytree:
                    menuitem.objecttype = record._id;
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('sitemap', 'solid');
                    menuitem.href = 'administration/dependencytrees/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.navigationtree:
                    menuitem.objecttype = record._id;
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('sitemap', 'solid');
                    menuitem.href = 'administration/navigationtrees/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.menunavigationtree:
                    menuitem.objecttype = record._id;
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('sitemap', 'solid');
                    menuitem.href = 'administration/menunavigationtrees/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.searchfilter:
                    menuitem.objecttype = record.name;
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('binoculars', 'solid');
                    menuitem.href = 'administration/searchfilters/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.view:
                    menuitem.objecttype = record.name;
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('list-alt', 'regular');
                    menuitem.href = 'administration/views/' + menuitem.objecttype;
                    menuitem.viewType = record.type;
                    if (record.type === 'JOIN') {
                        menuitem.href = 'administration/joinviews/' + menuitem.objecttype;
                    }
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.importexport:
                    menuitem.objecttype = record._id;
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('list-alt', 'regular');
                    menuitem.href = 'administration/importexport/datatemplates/' + menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.gisgatetemplate:
                    menuitem.objecttype = record._id;
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('list-alt', 'regular');
                    menuitem.href =
                        'administration/importexport/gatetemplates/' +
                        CMDBuildUI.model.importexports.Gate.gateType.cad +
                        '/' +
                        menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.databasegatetemplate:
                    menuitem.objecttype = record._id;
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('list-alt', 'regular');
                    menuitem.href =
                        'administration/importexport/gatetemplates/' +
                        CMDBuildUI.model.importexports.Gate.gateType.database +
                        '/' +
                        menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.ifcgatetemplate:
                    menuitem.objecttype = record._id;
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('list-alt', 'regular');
                    menuitem.href =
                        'administration/importexport/gatetemplates/' +
                        CMDBuildUI.model.importexports.Gate.gateType.ifc +
                        '/' +
                        menuitem.objecttype;
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.bus:
                    menuitem.objecttype = record._id;
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('cubes', 'solid');
                    menuitem.href = CMDBuildUI.util.administration.helper.ApiHelper.client.getBusDescriptorUrl(
                        menuitem.objecttype
                    );
                    break;
                case CMDBuildUI.model.administration.MenuItem.types.pluginmanager:
                    menuitem.objecttype = record._id;
                    menuitem.iconCls = CMDBuildUI.util.helper.IconHelper.getIconId('puzzle-piece', 'solid');
                    menuitem.href = 'administration/pluginmanager/' + menuitem.objecttype;
                    break;
            }

            output.push(menuitem);
        }
        return output;
    },

    /**
     * Return the json definition for records as tree.
     * @param {Ext.data.Model[]} records The plain list of items.
     * @param {String} menutype The menu type to use for these records.
     *
     * @returns {Object[]} A list of CMDBuild.model.MenuItem definitions.
     */
    getRecordsAsList: function (records, menutype) {
        var output = [];

        for (var i = 0; i < records.length; i++) {
            var record = records[i].getData();
            var menuitem = {
                menutype: menutype,
                index: i,
                objectid: record._id,
                text: record.description,
                leaf: true
            };
            output.push(menuitem);
        }
        return output;
    },

    privates: {
        adminRolePrivileges: CMDBuildUI.model.users.Session.adminRolePrivileges,
        /**
         * @private
         */
        sortByDescription: function (a, b) {
            var isSetupA =
                a.get('menutype') === CMDBuildUI.model.administration.MenuItem.types.setup ||
                a.get('objecttype') === 'setup';
            var isSetupB =
                b.get('menutype') === CMDBuildUI.model.administration.MenuItem.types.setup ||
                b.get('objecttype') === 'setup';

            if (isSetupA && !isSetupB) return 1;
            if (!isSetupA && isSetupB) return -1;

            if (a && a.get('description') && b && b.get('description')) {
                var nameA = a.get('description').toUpperCase();
                var nameB = b.get('description').toUpperCase();
                if (nameA < nameB) return -1;
                if (nameA > nameB) return 1;
                return 0;
            }
            return 0;
        },

        /**
         * @private
         */
        sortByText: function (a, b) {
            var isSetupA =
                a.get('menutype') === CMDBuildUI.model.administration.MenuItem.types.setup ||
                a.get('objecttype') === 'setup';
            var isSetupB =
                b.get('menutype') === CMDBuildUI.model.administration.MenuItem.types.setup ||
                b.get('objecttype') === 'setup';

            if (isSetupA && !isSetupB) return 1;
            if (!isSetupA && isSetupB) return -1;

            if (a.get('text') && b.get('text')) {
                var nameA = a.get('text').toUpperCase();
                var nameB = b.get('text').toUpperCase();
                if (nameA < nameB) return -1;
                if (nameA > nameB) return 1;
                return 0;
            }
            return 0;
        },

        /**
         * @param {CMDBuildUI.model.menu.MenuItem[]} items
         * @return {CMDBuildUI.model.menu.MenuItem} First selectable menu item
         */
        getFirstSelectableMenuItem: function (items) {
            var item;
            var i = 0;
            while (!item && i < items.length) {
                var node = items[i];
                if (node.get('menutype') !== CMDBuildUI.model.menu.MenuItem.types.folder) {
                    item = node;
                } else {
                    item = this.getFirstSelectableMenuItem(node.childNodes);
                }
                i++;
            }
            return item;
        },

        adminCan: function (privilege, multiplePermissions) {
            var me = this;
            var mainContent = Ext.getCmp('CMDBuildMainContent');
            var vm = mainContent.getViewModel();
            var theSession = vm.get('theSession');
            if (multiplePermissions) {
                var multiRolePrivilege = [];
                privilege.forEach(function (item) {
                    multiRolePrivilege.push(me.adminCan(item));
                });
                return multiRolePrivilege.indexOf(false) === -1;
            }
            return !theSession || !theSession.adminCan(privilege) ? false : true;
        },

        canViewSettings: function () {
            return this.adminCan(this.adminRolePrivileges.admin_sysconfig_view);
        }
    }
});
