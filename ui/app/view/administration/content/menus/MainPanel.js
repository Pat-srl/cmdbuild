Ext.define('CMDBuildUI.view.administration.content.menus.MainPanel', {
    extend: 'Ext.container.Container',
    alias: 'widget.administration-content-menus-mainpanel',
    controller: 'administration-content-menus-mainpanel',

    requires: ['CMDBuildUI.view.administration.content.menus.MainPanelController', 'CMDBuildUI.util.MenuStoreBuilder'],

    viewModel: {},
    itemId: 'administration-content-menus-mainpanel',
    ui: 'administration-tabandtools',
    layout: 'border',
    items: [
        {
            region: 'north',
            layout: 'column',
            defaults: {
                margin: '10 10 10 10'
            },

            items: [
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    columnWidth: 0.5,
                    items: [
                        {
                            columnWidth: 1,
                            xtype: 'combobox',
                            reference: 'comboGroup',
                            queryMode: 'local',
                            displayField: 'label',
                            valueField: 'value',
                            forceSelection: true,
                            allowBlank: false,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.group, // Group
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.group'
                            },
                            bind: {
                                store: '{rolesStore}',
                                hidden: '{!actions.add}'
                            },
                            listeners: {
                                change: function (combo, newValue, oldValue, eOpts) {
                                    this.up('administration-content-menu-view')
                                        .getViewModel()
                                        .set('theMenu.group', newValue);
                                },
                                afterrender: function () {
                                    try {
                                        this.isValid();
                                    } catch (error) {}
                                }
                            }
                        },
                        {
                            cls: 'administration-inline-label-textfield',
                            columnWidth: 1,
                            xtype: 'displayfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.group, // Group
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.group'
                            },
                            name: 'group',
                            bind: {
                                value: '{theMenu.name}',
                                hidden: '{actions.add}'
                            },
                            renderer: function (value) {
                                if (value === '_default') {
                                    return CMDBuildUI.locales.Locales.administration.common.strings['default']; // '*Default*';
                                }
                                var groupsStore = Ext.getStore('groups.Groups');
                                var group = groupsStore.findRecord('name', value);
                                if (group) {
                                    return group.get('description');
                                }
                                return value;
                            }
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.device,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.device'
                    },
                    hidden: true,
                    items: [
                        {
                            columnWidth: 1,
                            xtype: 'combobox',
                            reference: 'comboDeviceTypes',
                            itemId: 'comboDeviceTypes',
                            queryMode: 'local',
                            displayField: 'label',
                            valueField: 'value',
                            bind: {
                                store: '{deviceTypesStore}',
                                hidden: '{!actions.add}',
                                value: '{theMenu.device}'
                            }
                        },
                        {
                            cls: 'administration-inline-label-textfield',
                            columnWidth: 1,
                            xtype: 'displayfield',
                            bind: {
                                value: '{theMenu.device}',
                                hidden: '{actions.add}'
                            },
                            renderer: function (value) {
                                // TODO use renderer helper
                                return CMDBuildUI.util.administration.helper.RendererHelper.getMenuTargetDevice(value);
                            }
                        }
                    ]
                }
            ]
        },
        {
            region: 'center',
            xtype: 'container',
            width: '100%',
            height: '100%',
            layout: {
                type: 'hbox',
                align: 'strech'
            },
            items: [
                {
                    xtype: 'panel',
                    layout: 'container',
                    scrollable: 'y',
                    width: '100%',
                    height: '100%',
                    cls: 'container-border-1',
                    flex: 1,

                    items: [
                        {
                            xtype: 'administration-content-menus-treepanels-destinationpanel'
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    layout: 'container',
                    align: 'middle',
                    shrinkWrap: false,
                    height: '100%',
                    hidden: true,
                    bind: {
                        hidden: '{actions.view}'
                    },
                    items: [
                        {
                            xtype: 'button',
                            margin: '0 0 0 10',
                            height: '100%',
                            reference: 'removeItemBtn',
                            iconCls:
                                CMDBuildUI.util.helper.IconHelper.getIconId('arrow-circle-right', 'solid') + ' fa-2x',
                            tooltip: CMDBuildUI.locales.Locales.administration.menus.tooltips.remove, // Remove item
                            localized: {
                                tooltip: 'CMDBuildUI.locales.Locales.administration.menus.tooltips.remove'
                            },
                            config: {
                                theValue: null
                            },
                            listeners: {
                                click: 'onRemoveItemBtnClick'
                            },
                            cls: 'administration-field-container-btn',
                            autoEl: {
                                'data-testid': 'administration-lookupvalue-card-edit-translateBtn'
                            }
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    layout: 'container',
                    scrollable: 'y',
                    shrinkWrap: false,
                    width: '100%',
                    height: '100%',
                    cls: 'container-border-1',
                    flex: 1,
                    hidden: true,
                    bind: {
                        hidden: '{actions.view}'
                    },
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            margin: '0 0 0 10',
                            layout: {
                                type: 'hbox',
                                align: 'end'
                            },
                            items: [
                                {
                                    margin: 10,
                                    flex: 1,
                                    xtype: 'textfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.menus.fieldlabels.newfolder, // New folder
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.menus.fieldlabels.newfolder'
                                    },
                                    reference: 'newFolderName',
                                    bind: '{newFolderName}',
                                    listeners: {
                                        change: function (field, value) {
                                            field
                                                .lookupViewModel()
                                                .set('canAddNewFolder', field.ownerCt.items.items[0].isDirty());
                                        }
                                    }
                                },
                                {
                                    xtype: 'button',
                                    margin: '10 0 10 0',
                                    reference: 'addFolderBtn',
                                    iconCls:
                                        CMDBuildUI.util.helper.IconHelper.getIconId('plus-circle', 'solid') + ' fa-2x',
                                    tooltip: CMDBuildUI.locales.Locales.administration.menus.tooltips.addfolder,
                                    localized: {
                                        tooltip: 'CMDBuildUI.locales.Locales.administration.menus.tooltips.addfolder'
                                    },
                                    allowBlank: false,
                                    config: {
                                        theValue: null
                                    },
                                    bind: {
                                        disabled: '{!canAddNewFolder}'
                                    },
                                    listeners: {
                                        click: 'onAddFolderBtnClick'
                                    },
                                    cls: 'administration-field-container-btn',
                                    autoEl: {
                                        'data-testid': 'administration-menus-addfolder-btn'
                                    }
                                }
                            ]
                        },
                        {
                            xtype: 'administration-content-menus-treepanels-originpanel'
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'toolbar',
            ui: 'footer',
            region: 'south',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(false, {
                bind: {
                    disabled: '{!theMenu.group || hasEmptyFolders}'
                }
            })
        }
    ]
});
