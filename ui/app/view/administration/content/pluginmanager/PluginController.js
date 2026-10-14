Ext.define('CMDBuildUI.view.administration.content.pluginmanager.PluginController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-pluginmanager-plugin',
    mixins: ['CMDBuildUI.view.administration.content.pluginmanager.Mixin'],

    control: {
        '#': {
            afterlayout: 'onAfterLayout',
            beforerender: 'onBeforeRender'
        },
        '#applyPatchesBtn': {
            click: 'onApplyPatchesBtnClick'
        },
        '#patchesBtn': {
            click: 'onPatchesBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#reloadButton': {
            click: 'onReloadBtnClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.pluginmanager.View} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();
        const thePlugin = vm.get('thePlugin');
        const configs = thePlugin ? thePlugin.get('configs') : {};
        const custompage = thePlugin ? thePlugin.get('custompage') : null;
        const hasPatches = thePlugin ? thePlugin.get('_hasPatches') : false;

        vm.set('isPatchesAvailable', hasPatches);

        if (thePlugin) {
            vm.set('pluginInfo', this.buildPluginInfo(thePlugin));
            vm.set('pluginStatus', this.buildPluginStatus(thePlugin));
        }

        if (!hasPatches) {
            if (custompage) {
                vm.set('hideButtons', true);
                view.add({
                    xtype: 'fieldset',
                    collapsible: true,
                    scrollable: true,
                    layout: 'fit',
                    flex: 1,
                    ui: 'administration-formpagination',
                    style: {
                        'padding-bottom': 0
                    },
                    title: CMDBuildUI.locales.Locales.administration.plugin.pluginconfig,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.plugin.pluginconfig'
                    },
                    items: [
                        {
                            xtype: custompage.alias.replace('widget.', '')
                        }
                    ]
                });
            } else {
                const columns = this.buildPluginConfigFields(configs);
                if (columns.left.length > 0 || columns.right.length > 0) {
                    view.add({
                        xtype: 'fieldset',
                        collapsible: true,
                        layout: 'hbox',
                        scrollable: true,
                        flex: 1,
                        ui: 'administration-formpagination',
                        title: CMDBuildUI.locales.Locales.administration.plugin.pluginconfig,
                        localized: {
                            title: 'CMDBuildUI.locales.Locales.administration.plugin.pluginconfig'
                        },
                        items: [
                            {
                                flex: 1,
                                padding: '0 20 0 0',
                                items: columns.left
                            },
                            {
                                flex: 1,
                                padding: '0 20 0 0',
                                items: columns.right
                            }
                        ]
                    });
                }
            }
        }
    },

    /**
     *
     * @param {Ext.form.Panel} panel
     * @param {Ext.layout.form.Panel} layout
     * @param {Object} eOpts
     */
    onAfterLayout: function (panel, layout, eOpts) {
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, event, eOpts) {
        const vm = this.getViewModel();
        vm.set('recalculateConfigs', true);
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, event, eOpts) {
        const vm = this.getViewModel();
        const configData = [];
        const thePlugin = vm.get('thePlugin');

        Ext.Object.each(vm.get('pluginConfigs'), function (key, value, myself) {
            const correctKey = key.replaceAll('__DOT__', '.');
            configData.push({
                access: vm.get('configsAttributes')[correctKey].access,
                key: correctKey,
                value: value
            });
        });

        // Prevent sending empty data
        if (!Ext.Object.isEmpty(configData)) {
            const pluginView = button.up('administration-content-pluginmanager-plugin');
            CMDBuildUI.util.Utilities.showLoader(true, pluginView);
            Ext.Ajax.request({
                method: 'PUT',
                url: Ext.String.format(
                    '{0}/plugin/{1}/config/_MANY',
                    CMDBuildUI.util.Config.baseUrl,
                    thePlugin.get('service') || thePlugin.get('_id')
                ),
                jsonData: configData,
                success: function (response, eOpts) {
                    const store = Ext.getStore('pluginmanager.Plugins');
                    const record = store.getById(thePlugin.getId());
                    Ext.Array.forEach(configData, function (item, index, allitems) {
                        record.get('configs')[item.key] = item.value;
                    });
                    CMDBuildUI.util.Utilities.showLoader(false, pluginView);
                    vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                },
                failure: function () {
                    CMDBuildUI.util.Utilities.showLoader(false, pluginView);
                }
            });
        } else {
            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onApplyPatchesBtnClick: function (button, event, eOpts) {
        this.onOpenPatchesPopup(true);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onPatchesBtnClick: function (button, event, eOpts) {
        this.onOpenPatchesPopup();
    },

    privates: {
        /**
         *
         * @param {Boolean} showOnlyApplyPatches
         */
        onOpenPatchesPopup: function (showOnlyApplyPatches) {
            const vm = this.getViewModel();
            const content = {
                xtype: 'panel',
                items: [
                    {
                        xtype: 'container',
                        layout: 'hbox',
                        items: [
                            {
                                xtype: 'tbfill'
                            },
                            {
                                xtype: 'checkbox',
                                itemId: 'showOnlyApplyPatchesCheckbox',
                                padding: '10 20',
                                boxLabel: CMDBuildUI.locales.Locales.administration.plugin.showonlyapplypatches,
                                localized: {
                                    boxLabel: 'CMDBuildUI.locales.Locales.administration.plugin.showonlyapplypatches'
                                },
                                checked: showOnlyApplyPatches,
                                listeners: {
                                    change: function (field, newValue, oldValue, eOpts) {
                                        const store = field.lookupViewModel().get('patchesStore');
                                        if (newValue) {
                                            store.filterBy(function (record) {
                                                return !record.get('date');
                                            });
                                        } else {
                                            store.clearFilter();
                                        }
                                    }
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'grid',
                        ui: 'cmdbuildgrouping',
                        scrollable: true,
                        forceFit: true,
                        features: [
                            {
                                ftype: 'grouping',
                                collapsible: true,
                                enableGroupingMenu: false,
                                groupHeaderTpl: [
                                    '{name} ({children:this.childrenNumber})',
                                    {
                                        childrenNumber: function (children) {
                                            return children.length;
                                        }
                                    }
                                ]
                            }
                        ],
                        bind: {
                            store: '{patchesStore}'
                        },
                        columns: [
                            {
                                text: CMDBuildUI.locales.Locales.administration.common.labels.name,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                                },
                                dataIndex: '_id'
                            },
                            {
                                text: CMDBuildUI.locales.Locales.administration.plugin.appliedon,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.plugin.appliedon'
                                },
                                dataIndex: 'date',
                                renderer: function (value) {
                                    return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value);
                                }
                            }
                        ]
                    }
                ],
                dockedItems: {
                    xtype: 'toolbar',
                    itemId: 'bottomToolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        {
                            xtype: 'tbfill'
                        },
                        {
                            xtype: 'button',
                            itemId: 'cancelBtn',
                            text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.common.actions.cancel'
                            },
                            ui: 'administration-secondary-action-small',
                            listeners: {
                                click: function (button, event, eOpts) {
                                    popup.close();
                                }
                            }
                        },
                        {
                            xtype: 'button',
                            itemId: 'applyPatches',
                            text: CMDBuildUI.locales.Locales.administration.plugin.applypatches,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.plugin.applypatches'
                            },
                            ui: 'administration-action-small',
                            bind: {
                                disabled: '{!isPatchesAvailable}'
                            },
                            listeners: {
                                click: function (button, event, eOpts) {
                                    const pluginName = vm.get('thePlugin').get('name');
                                    const cancelBtn = button.up('#bottomToolbar').down('#cancelBtn');
                                    button.showSpinner = true;
                                    CMDBuildUI.util.Utilities.disableFormButtons([button, cancelBtn]);

                                    Ext.Ajax.request({
                                        method: 'POST',
                                        url: Ext.String.format(
                                            '{0}/system/plugins/{1}/patch',
                                            CMDBuildUI.util.Config.baseUrl,
                                            pluginName
                                        ),
                                        callback: function (options, success, response) {
                                            CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
                                                const plugins = Ext.getStore('pluginmanager.Plugins').getRange();
                                                const plugin = Ext.Array.findBy(plugins, function (item, index) {
                                                    return item.get('name') == pluginName;
                                                });
                                                const popupViewModel = button.lookupViewModel();
                                                const store = popupViewModel.get('patchesStore');
                                                const currentUrl = Ext.History.getToken();
                                                const me = this;
                                                CMDBuildUI.util.Utilities.enableFormButtons([button, cancelBtn]);

                                                if (success) {
                                                    CMDBuildUI.util.Notifier.showInfoMessage(
                                                        CMDBuildUI.locales.Locales.administration.plugin.patchesapplied
                                                    );
                                                    vm.set('thePlugin', plugin);
                                                    vm.set('isPatchesAvailable', plugin.get('_hasPatches'));
                                                    vm.set('recalculateConfigs', true);
                                                    button.disable();
                                                } else {
                                                    CMDBuildUI.util.Notifier.showErrorMessage(
                                                        CMDBuildUI.locales.Locales.administration.plugin
                                                            .patchesapplyfailed,
                                                        null,
                                                        'default',
                                                        CMDBuildUI.locales.Locales.administration.plugin
                                                            .patchesapplyfailedmessage
                                                    );
                                                }

                                                button
                                                    .up('#plugin-manager-patches')
                                                    .down('#showOnlyApplyPatchesCheckbox')
                                                    .setValue(false);
                                                popupViewModel.set('isPatchesAvailable', plugin.get('_hasPatches'));
                                                store.removeAll();
                                                store.add(plugin.get('patches'));

                                                CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy(
                                                    'href',
                                                    currentUrl,
                                                    me
                                                );
                                            });
                                        }
                                    });
                                }
                            }
                        }
                    ]
                }
            };

            const patchesStore = {
                autoDestroy: true,
                proxy: {
                    type: 'memory'
                },
                data: vm.get('thePlugin').get('patches'),
                sorters: {
                    property: 'date',
                    direction: 'DESC'
                },
                grouper: {
                    groupFn: function (item) {
                        return item.get('date')
                            ? CMDBuildUI.locales.Locales.administration.plugin.applied
                            : CMDBuildUI.locales.Locales.administration.plugin.tobeapplied;
                    }
                }
            };

            if (showOnlyApplyPatches) {
                patchesStore.filters = function (record) {
                    return !record.get('date');
                };
            }

            const popup = CMDBuildUI.util.Utilities.openPopup(
                'plugin-manager-patches',
                CMDBuildUI.locales.Locales.administration.plugin.pluginpatcheslist,
                content,
                null,
                {
                    ui: 'administration',
                    width: '60%',
                    height: '60%',
                    viewModel: {
                        data: {
                            isPatchesAvailable: vm.get('isPatchesAvailable')
                        },
                        stores: {
                            patchesStore: patchesStore
                        }
                    }
                }
            );
        }
    }
});
