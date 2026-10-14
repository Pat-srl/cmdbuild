Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.TabPanelController', {
    extend: 'Ext.app.ViewController',
    requires: [
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.ViewInRow'
    ],
    alias: 'controller.administration-content-importexport-gatetemplates-tabpanel',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChage'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.importexport.gatetemplates.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
        vm.bind(
            {
                bindTo: {
                    theGate: '{theGate}'
                },
                single: true
            },
            function (data) {
                tabPanelHelper.addTab(
                    view,
                    'properties',
                    CMDBuildUI.locales.Locales.administration.common.strings.properties,
                    [
                        {
                            xtype: 'administration-content-importexport-gatetemplates-card-tabitems-properties-view',
                            objectTypeName: vm.get('objectTypeName'),
                            objectId: vm.get('objectId'),
                            autoScroll: true
                        }
                    ],
                    0,
                    {
                        disabled: '{disabledTabs.properties}'
                    }
                );

                if (
                    data.theGate._config &&
                    (data.theGate._config.get('tag') === 'database' || data.theGate._config.get('tag') === 'ifc')
                ) {
                    tabPanelHelper.addTab(
                        view,
                        'templates',
                        CMDBuildUI.locales.Locales.administration.gates.templates,
                        [
                            {
                                xtype: 'administration-content-importexport-gatetemplates-tabitems-templates-grid',
                                objectTypeName: vm.get('objectTypeName'),
                                objectId: vm.get('objectId'),
                                bind: {
                                    store: '{allGateTemplates}'
                                },
                                plugins: [
                                    {
                                        ptype: 'administration-forminrowwidget',
                                        pluginId: 'administration-forminrowwidget',

                                        expandOnDblClick: false,
                                        widget: {
                                            xtype: 'administration-content-importexport-datatemplates-card-viewinrow',
                                            ui: 'administration-tabandtools',
                                            viewModel: {
                                                data: {
                                                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions
                                                        .view,
                                                    actions: {
                                                        view: true,
                                                        edit: false,
                                                        add: false
                                                    }
                                                }
                                            },
                                            bind: {
                                                theGateTemplate: '{selected}'
                                            }
                                        }
                                    }
                                ]
                            }
                        ],
                        1,
                        {
                            disabled: '{disabledTabs.templates}'
                        }
                    );
                } else {
                    tabPanelHelper.addTab(
                        view,
                        'templates',
                        CMDBuildUI.locales.Locales.administration.gates.templates,
                        [
                            {
                                xtype: 'administration-content-importexport-gatetemplates-tabitems-templates-grid',
                                objectTypeName: vm.get('objectTypeName'),
                                objectId: vm.get('objectId'),
                                plugins: [
                                    {
                                        ptype: 'administration-forminrowwidget',
                                        pluginId: 'administration-forminrowwidget',

                                        expandOnDblClick: false,
                                        widget: {
                                            xtype: 'administration-content-importexport-gatetemplates-tabitems-templates-card-viewinrow',
                                            ui: 'administration-tabandtools',
                                            viewModel: {
                                                data: {
                                                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions
                                                        .view,
                                                    actions: {
                                                        view: true,
                                                        edit: false,
                                                        add: false
                                                    },
                                                    typeHidden: true,
                                                    fileFormatHidden: true
                                                }
                                            },
                                            bind: {
                                                theGateTemplate: '{selected}'
                                            },
                                            listeners: {
                                                savesuccess: function (record, requestMethod) {
                                                    var me = this;
                                                    var _vm = me.lookupViewModel();
                                                    var grid = _vm.get('grid');
                                                    var theGate = _vm.get('theGate');
                                                    var handler = theGate.handlers().first();
                                                    handler.addTemplate(record.get('code'));
                                                    theGate.save({
                                                        success: function (gate, gateOperation) {
                                                            var eventToCall =
                                                                requestMethod === 'PUT' ? 'itemupdated' : 'itemcreated';
                                                            handler.getTemplates().then(function (templatesStore) {
                                                                grid.lookupViewModel()
                                                                    .get('allGateTemplates')
                                                                    .setData(templatesStore.getRange());
                                                                grid.getPlugin(
                                                                    'administration-forminrowwidget'
                                                                ).view.fireEventArgs(eventToCall, [grid, record, me]);
                                                            });
                                                        }
                                                    });
                                                },
                                                removetemplate: function (record, grid) {
                                                    var me = this;
                                                    var _vm = this.lookupViewModel();
                                                    grid = _vm.get('grid') || grid;
                                                    var theGate = _vm.get('theGate');
                                                    var handler = theGate.handlers().first();
                                                    handler.removeTemplate(record.get('code'));
                                                    record.erase({
                                                        success: function (_record, operation) {
                                                            theGate.save({
                                                                success: function (gate, gateOperation) {
                                                                    var eventToCall = 'itemremoved';
                                                                    handler
                                                                        .getTemplates()
                                                                        .then(function (templatesStore) {
                                                                            grid.lookupViewModel()
                                                                                .get('allGateTemplates')
                                                                                .setData(templatesStore.getRange());
                                                                            grid.getPlugin(
                                                                                'administration-forminrowwidget'
                                                                            ).view.fireEventArgs(eventToCall, [
                                                                                grid,
                                                                                record,
                                                                                me
                                                                            ]);
                                                                        });
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                ],
                                bind: {
                                    store: '{allGateTemplates}'
                                }
                            }
                        ],
                        1,
                        {
                            disabled: '{disabledTabs.templates}'
                        }
                    );
                }

                tabPanelHelper.addTab(
                    view,
                    'importon',
                    CMDBuildUI.locales.Locales.administration.gates.importon,
                    [
                        {
                            xtype: 'administration-content-importexport-gatetemplates-card-tabitems-importon-view',
                            objectTypeName: vm.get('objectTypeName'),
                            objectId: vm.get('objectId')
                        }
                    ],
                    2,
                    {
                        disabled: '{disabledTabs.importon}'
                    }
                );
                tabPanelHelper.addTab(
                    view,
                    'permissions',
                    CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.permissions,
                    [
                        {
                            xtype: 'administration-content-importexport-gatetemplates-card-tabitems-permissions-permissions',
                            objectTypeName: vm.get('objectTypeName'),
                            objectId: vm.get('objectId')
                        }
                    ],
                    3,
                    {
                        disabled: '{disabledTabs.permissions}'
                    }
                );

                if (vm.get('actions.add')) {
                    vm.set('enabledTab', 'properties');
                }
                view.setActiveTab(vm.get('activeTabs.gatetemplates') || 0);
            }
        );
    },

    /**
     * @param {CMDBuildUI.view.administration.content.importexport.gatetemplates.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        if (newtab) {
            this.getView().lookupViewModel().set('activeTab', newtab.tabIndex);
        }
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage(
            'activeTabs.gatetemplates',
            this,
            view,
            newtab,
            oldtab,
            eOpts
        );
    }
});
