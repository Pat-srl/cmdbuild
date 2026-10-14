Ext.define('CMDBuildUI.view.administration.content.gis.gismenus.MainPanel', {
    extend: 'Ext.container.Container',
    alias: 'widget.administration-content-gismenus-mainpanel',
    controller: 'administration-content-gismenus-mainpanel',

    requires: [
        'CMDBuildUI.view.administration.content.gis.gismenus.MainPanelController',
        'CMDBuildUI.util.MenuStoreBuilder'
    ],

    viewModel: {},
    itemId: 'administration-content-gismenus-mainpanel',
    ui: 'administration-tabandtools',
    layout: 'border',
    items: [{
        region: 'center',
        xtype: 'container',
        width: "100%",
        height: "100%",
        layout: {
            type: 'hbox',
            align: 'strech'
        },
        items: [{
            xtype: 'panel',
            layout: 'container',
            scrollable: 'y',
            width: "100%",
            height: "100%",
            cls: 'container-border-1',
            flex: 1,

            items: [{
                xtype: 'administration-content-gismenus-treepanels-destinationpanel'
            }]
        }, {
            xtype: 'panel',
            layout: 'container',
            align: 'middle',
            shrinkWrap: false,
            height: "100%",
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: [{
                xtype: 'button',
                margin: '0 0 0 10',
                height: "100%",
                reference: 'removeItemBtn',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-circle-right', 'solid') + ' fa-2x',
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
            }]

        }, {
            xtype: 'panel',
            layout: 'container',
            scrollable: 'y',
            shrinkWrap: false,
            width: "100%",
            height: "100%",
            cls: 'container-border-1',
            flex: 1,
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: [{
                xtype: 'fieldcontainer',
                margin: '0 0 0 10',
                layout: {
                    type: 'hbox',
                    align: 'end'
                },
                items: [{
                    margin: 10,
                    flex: 1,
                    xtype: 'textfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.menus.fieldlabels.newfolder, // New folder
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.menus.fieldlabels.newfolder'
                    },
                    reference: 'newFolderName',
                    bind: '{newFolderName}',
                    listeners: {
                        change: function (field, value) {
                            field.lookupViewModel().set('canAddNewFolder', field.ownerCt.items.items[0].isDirty());
                        }
                    }
                }, {
                    xtype: 'button',
                    margin: '10 0 10 0',
                    reference: 'addFolderBtn',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus-circle', 'solid') + ' fa-2x',
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
                        'data-testid': 'administration-gismenus-addfolder-btn'
                    }
                }]
            }, {
                xtype: 'administration-content-gismenus-treepanels-originpanel'
            }]
        }]
    }, {
        xtype: 'toolbar',
        ui: 'footer',
        region: 'south',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(false, {
            bind: {
                disabled: '{!theMenu.group}'
            }
        })
    }]
});