Ext.define('CMDBuildUI.view.administration.components.splitstring.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.components.splitstring.GridController',
        'CMDBuildUI.view.administration.components.splitstring.GridModel'
    ],

    alias: 'widget.administration-components-splitstring-grid',
    controller: 'administration-components-splitstring-grid',
    viewModel: {
        type: 'administration-components-splitstring-grid'
    },

    /*      -------------------------  EXAMPLE: -----------------------------

        1) Create button in your View.

        2) Create popup with returnString listener in your controller
            xtype: 'administration-components-splitstring-grid'
            pass theMessage and theDivisor

        onPopupButtonBtnClick: function (item, event, eOpts) {
            var content = {
                xtype: 'administration-components-splitstring-grid',
                viewModel: {
                    data: {
                        theMessage: item.lookupViewModel().get('message'),
                        theDivisor: '.'
                    }
                }
            };


            // custom panel listeners
            var listeners = {
                returnString: function (result, eOpts) {
                    item.lookupViewModel().set('message', result);
                    CMDBuildUI.util.Utilities.closePopup('administration-content-localizations-imports-view');
                },
                close: function (panel, eOpts) {
                    CMDBuildUI.util.Utilities.closePopup('administration-content-localizations-imports-view');
                }
            };

            var popUp = CMDBuildUI.util.Utilities.openPopup(
                'administration-content-localizations-imports-view',
                'Split String',
                content,
                listeners, {
                    ui: 'administration-actionpanel',
                    width: '50%',
                    height: '50%'
                }
            );

        3) Put message in your viewModel

            message: 'aaaaaaaa.aaaaaaa.bbbbbbb.cccccc'
*/
    scrollable: true,
    forceFit: true,
    viewConfig: {
        markDirty: false
    },

    columns: [
        {
            text: CMDBuildUI.locales.Locales.administration.common.strings.string,
            dataIndex: 'substring',
            align: 'left',
            width: '40%',
            editor: {
                xtype: 'textfield',
                allowBlank: false
            }
        },
        {
            xtype: 'actioncolumn',
            bind: {
                hidden: '{actions.view}'
            },
            minWidth: 54,
            maxWidth: 54,
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
        store: '{splitStringStore}'
    },

    plugins: {
        pluginId: 'cellediting',
        ptype: 'cellediting',
        clicksToEdit: 1,
        listeners: {
            beforeedit: function (editor, context) {
                if (editor.view.lookupViewModel().get('actions.view')) {
                    return false;
                }
            }
        }
    },

    tbar: [
        {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.administration.emails.addrow,
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
            ui: 'administration-action-small',
            handler: 'onAddRowBtnClick',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.emails.addrow'
            },
            autoEl: {
                'data-testid': 'administration-components-splitString-grid-addrow'
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
            listeners: {
                click: 'onCancelBtnClick'
            },
            autoEl: {
                'data-testid': 'administration-components-splitString-grid-cancel'
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.common.actions.ok,
            ui: 'administration-action-small',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.ok'
            },
            listeners: {
                click: 'onOkBtnClick'
            },
            autoEl: {
                'data-testid': 'administration-components-splitString-grid-ok'
            }
        }
    ]
});
