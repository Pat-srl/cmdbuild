Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.PermissionsController', {
    requires: ['CMDBuildUI.util.administration.helper.TabPanelHelper'],

    mixins: ['CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.PermissionsMixin'],

    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-tabitems-permissions-permissions',
    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChage'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.groupsandpermissions.TabPanel} view
     */
    onBeforeRender: function (view) {
        var vm = this.getViewModel();
        var currentSubTabIndex =
            this.getView().up('administration-content').getViewModel().get('activeTabs.permissions') || 0;
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
        tabPanelHelper.addTab(
            view,
            'classes',
            CMDBuildUI.locales.Locales.administration.navigation.classes,
            [
                {
                    xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-classes-classes',
                    autoScroll: true
                }
            ],
            0,
            {},
            {
                objectType: CMDBuildUI.model.menu.MenuItem.types.klass
            }
        );

        tabPanelHelper.addTab(
            view,
            'processes',
            CMDBuildUI.locales.Locales.administration.navigation.processes,
            [
                {
                    xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-processes-processes'
                }
            ],
            1,
            {},
            {
                objectType: 'process'
            }
        );

        tabPanelHelper.addTab(
            view,
            'views',
            CMDBuildUI.locales.Locales.administration.navigation.views,
            [
                {
                    xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-views-views'
                }
            ],
            2,
            {},
            {
                objectType: CMDBuildUI.model.menu.MenuItem.types.view
            }
        );

        tabPanelHelper.addTab(
            view,
            'searchFilters',
            CMDBuildUI.locales.Locales.administration.navigation.searchfilters,
            [
                {
                    xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-filters-filters'
                }
            ],
            3,
            {},
            {
                objectType: CMDBuildUI.model.menu.MenuItem.types.searchfilter
            }
        );

        tabPanelHelper.addTab(
            view,
            'dashboards',
            CMDBuildUI.locales.Locales.administration.navigation.dashboards,
            [
                {
                    xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-dashboards-dashboards'
                }
            ],
            4,
            {},
            {
                objectType: CMDBuildUI.model.menu.MenuItem.types.dashboard
            }
        );

        tabPanelHelper.addTab(
            view,
            'reports',
            CMDBuildUI.locales.Locales.administration.navigation.reports,
            [
                {
                    xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-reports-reports'
                }
            ],
            5,
            {},
            {
                objectType: CMDBuildUI.model.menu.MenuItem.types.report
            }
        );

        tabPanelHelper.addTab(
            view,
            'custompages',
            CMDBuildUI.locales.Locales.administration.navigation.custompages,
            [
                {
                    xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-custompages-custompages'
                }
            ],
            6,
            {},
            {
                objectType: CMDBuildUI.model.menu.MenuItem.types.custompage
            }
        );

        tabPanelHelper.addTab(
            view,
            'etltemplate',
            CMDBuildUI.locales.Locales.administration.navigation.importexports,
            [
                {
                    xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-importexports-importexports'
                }
            ],
            7,
            {},
            {
                objectType: CMDBuildUI.model.administration.MenuItem.types.importexport
            }
        );

        tabPanelHelper.addTab(
            view,
            'etlgate',
            CMDBuildUI.locales.Locales.administration.navigation.busdescriptors,
            [
                {
                    xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-servicesbus-servicesbus'
                }
            ],
            8,
            {},
            {
                objectType: 'etlgate'
            }
        );

        tabPanelHelper.addTab(
            view,
            'dependencytree',
            CMDBuildUI.locales.Locales.administration.dependencytrees.description,
            [
                {
                    xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-dependencytree-dependencytree'
                }
            ],
            9,
            {},
            {
                objectType: CMDBuildUI.model.administration.MenuItem.types.dependencytree
            }
        );

        tabPanelHelper.addTab(
            view,
            'other',
            CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.otherpermissions,
            [
                {
                    xtype: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-other-other'
                }
            ],
            10,
            {},
            {
                objectType: CMDBuildUI.model.administration.MenuItem.types.other
            }
        );

        vm.set('activeTab', currentSubTabIndex);
        view.setActiveTab(currentSubTabIndex);
    },

    /**
     * @param {CMDBuildUI.view.administration.content.groupsandpermissions.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        var me = this;
        var vm = this.getViewModel();
        vm.set('objectType', newtab.reference);
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage(
            'activeTabs.permissions',
            this,
            view,
            newtab,
            oldtab,
            eOpts
        );

        var grantsStore = Ext.getStore('groups.Grants');
        var proxyUrl = CMDBuildUI.util.administration.helper.ApiHelper.server.getRoleGrantsUrl(vm.get('theGroup._id'));
        if (vm.get('theGroup').crudState === 'C') {
            return false;
        }
        var chainedStore = vm.getStore('grantsChainedStore');
        me.setCopyButton(newtab, chainedStore);
        Ext.asap(function () {
            if (newtab && !newtab.dertroyed && newtab.reference !== 'other') {
                CMDBuildUI.util.Utilities.showLoader(true, newtab);
            }
        });
        grantsStore.getProxy().setUrl(proxyUrl);
        grantsStore.load(function () {
            if (grantsStore && !grantsStore.destryed) {
                try {
                    chainedStore.clearFilter();
                    chainedStore.addFilter([
                        function (record) {
                            if (newtab.config.objectType !== 'etltemplate') {
                                return record.get('objectType') === newtab.config.objectType;
                            } else {
                                if (['etltemplate', 'etlgate'].indexOf(record.get('objectType')) > -1) {
                                    var originalRecord =
                                        Ext.getStore('importexports.Templates').findRecord(
                                            '_id',
                                            record.get('objectTypeName')
                                        ) ||
                                        Ext.getStore('importexports.Gates').findRecord(
                                            '_id',
                                            record.get('objectTypeName')
                                        );
                                    if (originalRecord) {
                                        return true;
                                    }
                                }
                                return false;
                            }
                        }
                    ]);
                    chainedStore.config = {
                        relatedStore: newtab.config.relatedStore,
                        objectType: newtab.config.objectType,
                        roleId: vm.get('theGroup._id')
                    };
                    if (newtab && !newtab.destroyed && newtab.reference !== 'other') {
                        if (newtab.reference === 'classes' || newtab.reference === 'processes') {
                            newtab.down('grid').setStore(chainedStore);
                            me.setClassesOrProcessStore(newtab);
                        } else {
                            newtab.down('grid').setStore(chainedStore);
                            Ext.asap(function () {
                                CMDBuildUI.util.Utilities.showLoader(false, newtab);
                            });
                        }
                        if (oldtab) {
                            oldtab.down('#searchtext').setValue(null);
                        }
                    }
                } catch (error) {}
            }
        });
    },
    setCopyButton: function (view, currentGrantsStore) {
        var me = this;
        var copyFromButton = view.down('#copyFrom');
        copyFromButton.menu.removeAll();

        Ext.getStore('groups.Groups').load({
            callback: function (items) {
                Ext.Array.forEach(items, function (element, index) {
                    if (
                        copyFromButton &&
                        copyFromButton.menu &&
                        element.get('active') &&
                        !element.get('_rp_data_all_write')
                    ) {
                        copyFromButton.menu.add({
                            text: element.get('description'),
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('users', 'solid'),
                            listeners: {
                                click: function () {
                                    me.cloneFrom(element, view, currentGrantsStore);
                                }
                            }
                        });
                    }
                });
            }
        });
    },
    cloneFrom: function (group, view, currentGrantsStore) {
        var me = this,
            proxyUrl = CMDBuildUI.util.administration.helper.ApiHelper.server.getRoleGrantsUrl(group.get('_id')),
            grantsStore = Ext.create('Ext.data.Store', {
                extend: 'CMDBuildUI.store.Base',
                requires: ['CMDBuildUI.store.Base', 'CMDBuildUI.model.users.Grant'],
                model: 'CMDBuildUI.model.users.Grant',
                pageSize: 0,
                autoLoad: false,
                autoDestroy: true
            });

        grantsStore.getProxy().type = 'baseproxy';
        grantsStore.getModel().getProxy().setUrl(proxyUrl);
        grantsStore.load({
            callback: function (items) {
                var grantsToRemove = currentGrantsStore.getRange().filter(function (item) {
                    return item.get('objectType') === view.config.objectType;
                });

                currentGrantsStore.remove(grantsToRemove);
                currentGrantsStore.add(
                    items.filter(function (item, index) {
                        item.crudState = 'U';
                        return item.get('objectType') === view.config.objectType;
                    })
                );
                me.setClassesOrProcessStore(view);
            }
        });
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel('administration-content-groupsandpermissions-view');
        vm.set('actions.view', false);
        vm.set('actions.edit', true);
        vm.set('actions.add', false);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        if (this.getView().getActiveTab().reference === 'other') {
            this.saveOtherTab(button);
        } else {
            this.saveRegularGrant(button);
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
        var me = this;
        var view = me.getView();
        var vm = view.getViewModel();
        vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        this.redirectTo(Ext.History.getToken(), true);
    },

    privates: {
        saveOtherTab: function (button) {
            button.setDisabled(true);
            Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
            var me = this;
            var view = me.getView();
            var vm = view.getViewModel();
            var otherGrid = view.getActiveTab().down('grid');
            var theGroup = vm.get('theGroup');
            otherGrid.getStore().each(function (item) {
                var value = item.get('mode') === CMDBuildUI.model.users.Grant.grantType.read;
                var objectType = item.get('objectType');
                if (objectType === '_rp_calendar_access') {
                    theGroup.set('_rp_calendar_event_create', item.get('modeTypeWriteOther'));
                }
                theGroup.set(objectType, value);
            });
            delete theGroup.data.system;
            Ext.apply(theGroup.data, theGroup.getAssociatedData());
            theGroup.save({
                success: function (record, operation) {
                    me.redirectTo(Ext.History.getToken(), true);
                }
            });
        },

        saveRegularGrant: function (button) {
            var me = this;
            button.setDisabled(true);
            Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
            var vm = this.getViewModel();
            var store = Ext.getStore('groups.Grants');
            var data = store.getData().items;
            var jsonData = [];
            Ext.Array.forEach(data, function (element) {
                if (element.crudState === 'U') {
                    if (element.get('attributePrivileges') === null) {
                        element.set('attributePrivileges', {});
                    }
                    jsonData.push(element.getData());
                }
            });
            Ext.Ajax.request({
                url: CMDBuildUI.util.administration.helper.ApiHelper.server.getRoleGrantsPostUrl(
                    vm.get('theGroup._id')
                ),
                method: 'POST',
                jsonData: jsonData,
                callback: function () {
                    store.load();
                    me.redirectTo(Ext.History.getToken(), true);
                }
            });
        },

        setClassesOrProcessStore: function (view) {
            var me = this,
                vm = view.lookupViewModel(),
                chainedStore = vm.get('grantsChainedStore'),
                treeStore = vm.get('treeStore'),
                grid = view.down(
                    'administration-content-groupsandpermissions-tabitems-permissions-components-treegrid'
                );

            treeStore.removeAll();
            treeStore.setRootNode({
                id: 'root',
                expanded: true,
                _object_description: 'root',
                children: []
            });

            var root = treeStore.getRootNode();
            root.removeAll();

            if (view.reference === 'classes') {
                CMDBuildUI.util.Stores.loadClassesStore().then(function (items) {
                    var simples = Ext.create('CMDBuildUI.model.users.ClassGrant', {
                        nodetype: 'folder',
                        objecttype: 'Simples',
                        _object_description: CMDBuildUI.locales.Locales.administration.navigation.simples,
                        leaf: false,
                        expanded: true,
                        index: 1,
                        children: me.getRecordsAsSubmenu(
                            items
                                .filter(function (rec, id) {
                                    return rec.get('type') === 'simple';
                                })
                                .sort(me.sortByText),
                            CMDBuildUI.model.menu.MenuItem.types.klass,
                            '',
                            chainedStore,
                            'simple'
                        )
                    });
                    simples.get('children').sort(me.sortByText);

                    var standards = me.getRecordsAsSubmenu(
                        items
                            .filter(function (rec, id) {
                                return rec.get('prototype') === true || rec.get('parent').length > 0;
                            })
                            .sort(me.sortByText),
                        CMDBuildUI.model.menu.MenuItem.types.klass,
                        CMDBuildUI.util.helper.ModelHelper.systemClasses.class,
                        chainedStore,
                        'standard'
                    );
                    var standard = Ext.create('CMDBuildUI.model.users.ClassGrant', {
                        nodetype: 'folder',
                        objecttype: 'Standard',
                        _object_description: CMDBuildUI.locales.Locales.administration.navigation.standard,
                        expanded: true,
                        leaf: false,
                        index: 0,
                        children: standards
                    });
                    if (grid && !grid.destroyed) {
                        root.appendChild(standard);
                        root.appendChild(simples);
                        grid.reconfigure(treeStore);
                    }
                    CMDBuildUI.util.Utilities.showLoader(false, view);
                });
            } else if (view.reference === 'processes') {
                CMDBuildUI.util.Stores.loadProcessesStore().then(function (items) {
                    var children;
                    if (items) {
                        children = me.getRecordsAsSubmenu(
                            items
                                .filter(function (rec, id) {
                                    return rec.get('prototype') === true || rec.get('parent').length > 0;
                                })
                                .sort(me.sortByText),
                            CMDBuildUI.model.menu.MenuItem.types.process,
                            CMDBuildUI.util.helper.ModelHelper.systemClasses.process,
                            chainedStore
                        );
                    }
                    root.appendChild(children);
                    grid.reconfigure(treeStore);
                    CMDBuildUI.util.Utilities.showLoader(false, view);
                });
            }
        },

        getRecordsAsSubmenu: function (records, nodetype, parentname, grants) {
            var output = [];
            var me = this;

            var frecords = Ext.Array.filter(records, function (item) {
                return item.getData().hasOwnProperty('parent') && item.getData().parent === parentname;
            });
            for (var i = 0; i < frecords.length; i++) {
                var record = frecords[i].getData();
                var grantRecord = grants.findRecord('objectTypeName', record.name);
                if (grantRecord) {
                    var menuitem = Ext.Object.merge(
                        {},
                        {
                            nodetype: nodetype,
                            index: i,
                            _object_description: record.description,
                            leaf: true,
                            children: []
                        },
                        grantRecord.getData() || {}
                    );
                    switch (nodetype) {
                        case CMDBuildUI.model.administration.MenuItem.types.klass:
                            if (record.prototype) {
                                menuitem.leaf = false;
                                menuitem.expanded = true;
                                menuitem.expandable = false;
                                menuitem.children = me.getRecordsAsSubmenu(records, nodetype, record.name, grants);
                                menuitem.iconCls = menuitem.children.length
                                    ? CMDBuildUI.model.menu.MenuItem.icons.klassparent
                                    : CMDBuildUI.model.menu.MenuItem.icons.klass;
                            }
                            break;
                        case CMDBuildUI.model.administration.MenuItem.types.process:
                            if (record.prototype) {
                                menuitem.leaf = false;
                                menuitem.expanded = true;
                                menuitem.expandable = false;
                                menuitem.children = me.getRecordsAsSubmenu(records, nodetype, record.name, grants);
                                menuitem.iconCls = menuitem.children.length
                                    ? CMDBuildUI.model.menu.MenuItem.icons.processparent
                                    : CMDBuildUI.model.menu.MenuItem.icons.process;
                            }
                            break;
                    }
                    menuitem = Ext.create('CMDBuildUI.model.users.ClassGrant', menuitem);
                    menuitem.get('children').sort(me.sortByText);
                    output.push(menuitem);
                }
            }
            return output;
        },
        sortByText: function (n1, n2) {
            var i1 = n1.data.description,
                i2 = n2.data.description;
            return i2 < i1 ? 1 : i2 > i1 ? -1 : 0;
        }
    }
});
