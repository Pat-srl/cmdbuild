Ext.define('CMDBuildUI.view.administration.components.keyvaluegrid.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.components.keyvaluegrid.GridController',
        'CMDBuildUI.view.administration.components.keyvaluegrid.GridModel'
    ],

    alias: 'widget.administration-components-keyvaluegrid-grid',
    controller: 'administration-components-keyvaluegrid-grid',
    viewModel: {
        type: 'administration-components-keyvaluegrid-grid'
    },

    forceFit: true,
    viewConfig: {
        markDirty: false
    },
    columns: [
        {
            text: CMDBuildUI.locales.Locales.administration.emails.key,
            dataIndex: 'key',
            align: 'left',
            width: '40%',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.emails.key'
            },
            editor: {
                xtype: 'textfield',
                allowBlank: false,
                validator: function (value) {
                    return !Ext.String.startsWith(value, 'cm_');
                }
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.emails.value,
            dataIndex: 'value',
            align: 'left',
            width: '40%',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.emails.value'
            },
            editor: {
                xtype: 'textfield'
            }
        },
        {
            xtype: 'actioncolumn',
            bind: {
                hidden: '{actions.view}'
            },
            minWidth: 54,
            maxWidth: 54,
            menuDisabled: true,
            hideable: false,
            align: 'center',
            items: [
                {
                    iconCls:
                        'attachments-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                    getTip: function () {
                        return CMDBuildUI.locales.Locales.administration.emails.remove;
                    },
                    handler: function (grid, rowIndex, colIndex) {
                        var record = grid.getStore().getAt(rowIndex);
                        grid.fireEvent('actiondelete', grid, record, rowIndex, colIndex);
                    }
                }
            ]
        }
    ],

    bind: {
        store: '{keyvaluedataStore}'
    },

    plugins: {
        ptype: 'cellediting',
        clicksToEdit: 1,
        listeners: {
            beforeedit: 'onBeforeEditCell'
        }
    },

    tbar: [
        {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.administration.emails.addrow,
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
            ui: 'administration-action-small',
            hidden: true,
            reference: 'addrowbtn',
            itemid: 'addrowbtn',
            handler: 'onAddRowBtnClick',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.emails.addrow'
            },
            autoEl: {
                'data-testid': 'administration-components-keyvalue-grid-addrow'
            },
            bind: {
                hidden: '{actions.view}'
            }
        }
    ],
    buttons: [
        {
            text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
            ui: 'administration-secondary-action-small',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.cancel'
            },
            hidden: true,
            listeners: {
                click: 'onCancelBtnClick'
            },
            autoEl: {
                'data-testid': 'administration-components-keyvalue-grid-cancel'
            },
            bind: {
                hidden: '{actions.view}'
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.common.actions.close,
            ui: 'administration-secondary-action-small',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.close'
            },
            hidden: true,
            listeners: {
                click: 'onCancelBtnClick'
            },
            autoEl: {
                'data-testid': 'administration-components-keyvalue-grid-close'
            },
            bind: {
                hidden: '{!actions.view}'
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.common.actions.ok,
            reference: 'saverowbtn',
            itemid: 'saverowbtn',
            bind: {
                disabled: '{updateSaveRowBtn}',
                hidden: '{actions.view}'
            },
            hidden: true,
            ui: 'administration-action-small',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.ok'
            },
            listeners: {
                click: 'onSaveBtnClick'
            },
            autoEl: {
                'data-testid': 'administration-components-keyvalue-grid-ok'
            }
        }
    ]
});
