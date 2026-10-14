Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-importexport-datatemplates-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        var vm = this.getViewModel();
        vm.set('title', CMDBuildUI.locales.Locales.administration.importexport.texts.importexportdatatemplates);

        if (vm.get('showInMainPanel')) {
            view.add({
                xtype: 'administration-content-importexport-datatemplates-topbar',
                region: 'north'
            });
            if (!vm.get('hideForm')) {
                CMDBuildUI.util.Stores.loadEmailAccountsStore();
                CMDBuildUI.util.Stores.loadEmailTemplatesStore();

                var viewModel = {
                    /**
                     *
                     * @param {Number} currrentTabIndex
                     */
                    toggleEnableTabs: function (currrentTabIndex) {
                        var me = this;
                        var view = me.getView();
                        var tabs = view.items.items;

                        tabs.forEach(function (tab) {
                            if (tab.tabConfig.tabIndex !== currrentTabIndex) {
                                me.set('disabledTabs.' + tab.reference, true);
                            }
                        });
                    }
                };
                if (!vm.get('actions.clone')) {
                    viewModel.links = {
                        theGateTemplate: vm.get('templateId')
                            ? {
                                  type: 'CMDBuildUI.model.importexports.Template',
                                  id: vm.get('templateId')
                              }
                            : {
                                  type: 'CMDBuildUI.model.importexports.Template',
                                  create: true
                              }
                    };
                }
                view.add({
                    xtype: 'tabpanel',
                    itemId: 'tabpanel',
                    tabPosition: 'top',
                    tabRotation: 0,
                    bind: {
                        activeTab: '{activeTabs.importexport}'
                    },
                    controller: {
                        control: {
                            '#': {
                                tabchange: 'onTabChage'
                            }
                        },
                        /**
                         * @param {Ext.tab.Panel} view
                         * @param {Ext.Component} newtab
                         * @param {Ext.Component} oldtab
                         * @param {Object} eOpts
                         */
                        onTabChage: function (view, newtab, oldtab, eOpts) {
                            CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage(
                                'activeTabs.importexport',
                                this,
                                view,
                                newtab,
                                oldtab,
                                eOpts
                            );
                        }
                    },
                    viewModel: viewModel,
                    ui: 'administration-tabandtools',
                    cls: 'administration-mainview-tabpanel',
                    region: 'center',
                    items: [
                        {
                            xtype: 'panel',
                            layout: 'card',
                            autoScroll: true,
                            reference: 'properties',
                            config: {},
                            padding: 0,
                            tabIndex: 0,
                            tabConfig: {
                                tabIndex: 0,
                                title: CMDBuildUI.locales.Locales.administration.common.strings.properties,
                                tooltip: CMDBuildUI.locales.Locales.administration.common.strings.properties,
                                autoEl: {
                                    'data-testid': 'administration-tab-properties'
                                },
                                bind: {
                                    disabled: '{disabledTabs.properties}'
                                }
                            },
                            scrollable: true,
                            viewModel: {},
                            items: [
                                {
                                    xtype: 'administration-content-importexport-datatemplates-card',
                                    viewModel: {
                                        data: {
                                            action: vm.get('action'),
                                            actions: vm.get('actions')
                                        }
                                    }
                                }
                            ]
                        },
                        {
                            xtype: 'panel',
                            layout: 'card',
                            scrollable: true,
                            autoScroll: true,
                            reference: 'permissions',
                            config: {},
                            padding: 0,
                            tabIndex: 1,
                            tabConfig: {
                                tabIndex: 1,
                                title: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.permissions,
                                tooltip:
                                    CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.permissions,
                                autoEl: {
                                    'data-testid': 'administration-tab-permissions'
                                },
                                bind: {
                                    disabled: '{disabledTabs.permissions || !actions.view}'
                                }
                            },

                            viewModel: {},
                            items: [
                                {
                                    xtype: 'administration-content-importexport-datatemplates-permissions-permissions'
                                }
                            ]
                        }
                    ]
                });
            }
        } else {
            view.add({
                xtype: 'administration-content-importexport-datatemplates-grid',
                region: 'center',
                bind: {
                    hidden: '{isGridHidden}'
                }
            });
        }
    }
});
