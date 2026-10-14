Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.users.Users', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.users.UsersController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.users.UsersModel',
        'CMDBuildUI.components.administration.grid.toolbar.FilterSearch'
    ],

    alias: 'widget.administration-content-groupsandpermissions-tabitems-users-users',
    controller: 'administration-content-groupsandpermissions-tabitems-users-users',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-users-users'
    },

    autoScroll: false,
    modelValidation: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    layout: 'border',

    items: [
        {
            xtype: 'components-administration-toolbars-formtoolbar',
            region: 'north',
            bind: {
                hidden: '{actions.view}'
            },
            items: [
                {
                    xtype: 'button',
                    itemId: 'spacer',
                    style: {
                        visibility: 'hidden'
                    }
                }
            ]
        },
        {
            xtype: 'components-administration-toolbars-formtoolbar',
            region: 'north',
            bind: {
                hidden: '{!actions.view}'
            },
            items: [
                {
                    xtype: 'button',
                    itemId: 'spacer',
                    style: {
                        visibility: 'hidden'
                    }
                },
                {
                    xtype: 'tbfill'
                },
                {
                    xtype: 'tool',
                    align: 'right',
                    itemId: 'editBtn',
                    cls: 'administration-tool',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
                    },
                    callback: 'onEditBtnClick',
                    hidden: true,
                    autoEl: {
                        'data-testid': 'administration-groupandpermission-users-tool-editbtn'
                    },
                    bind: {
                        hidden: '{toolbarHiddenButtons.edit}',
                        disabled: '{!toolAction._canUpdate}'
                    }
                }
            ]
        },
        {
            region: 'west',
            width: '50%',

            collapsible: true,
            padding: '0 5 0 0',
            reference: 'usersassignedgrid',
            itemId: 'usersassignedgrid',
            header: false,
            xtype: 'grid',
            ui: 'administration-ligth-panel',
            layout: 'fit',
            sealedColumns: false,
            sortableColumns: true,
            enableColumnHide: false,
            enableColumnMove: false,
            enableColumnResize: false,
            menuDisabled: true,
            stopSelect: true,
            multiSelect: true,
            selModel: {
                pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
            },
            viewConfig: {
                plugins: {
                    pluginId: 'dragdrop',
                    ptype: 'gridviewdragdrop',
                    dragGroup: 'firstGridDDGroup',
                    dropGroup: 'secondGridDDGroup',
                    disabled: true
                },
                listeners: {
                    drop: function (node, data, dropRec, dropPosition) {
                        data.records.forEach(function (element) {
                            element.phantom = true;
                        });
                    }
                }
            },

            showActionColumn: true,
            useDefaultAction: false,
            columns: [
                {
                    text: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.username,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.username'
                    },
                    dataIndex: 'username',
                    align: 'left',
                    flex: 1
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.common.labels.description,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                    },
                    dataIndex: 'description',
                    align: 'left',
                    flex: 1
                }
            ],
            bind: {
                store: '{assignedUser}'
            },
            tbar: {
                xtype: 'pagingtoolbarfiltersearch',
                bind: {
                    store: '{assignedUser}'
                },
                panelTitle: CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.usersassigned,
                emptyMsg: CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.displaynousersmessage,
                displayMsg: CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.displaytotalrecords,
                localized: {
                    panelTitle: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.usersassigned',
                    emptyMsg:
                        'CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.displaynousersmessage',
                    displayMsg:
                        'CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.displaytotalrecords'
                },
                searchField: {
                    emptyText: CMDBuildUI.locales.Locales.administration.groupandpermissions.emptytexts.searchusers,
                    localized: {
                        emptyText:
                            'CMDBuildUI.locales.Locales.administration.groupandpermissions.emptytexts.searchusers'
                    }
                },
                searchColumnIndexes: ['description', 'username', 'email'],
                displayInfo: true,
                margin: 5
            }
        },
        {
            region: 'center',
            width: '50%',
            padding: '0 0 0 5',
            header: false,
            reference: 'usersunassignedgrid',
            itemId: 'usersunassignedgrid',
            xtype: 'grid',
            ui: 'administration-ligth-panel',
            layout: 'fit',
            style: {
                borderLeft: '1px solid #ccc'
            },
            sealedColumns: false,
            sortableColumns: true,
            enableColumnHide: false,
            enableColumnMove: false,
            enableColumnResize: false,
            menuDisabled: true,
            stopSelect: true,
            multiSelect: true,
            viewConfig: {
                plugins: {
                    pluginId: 'dragdrop',
                    ptype: 'gridviewdragdrop',
                    dragGroup: 'secondGridDDGroup',
                    dropGroup: 'firstGridDDGroup'
                },
                listeners: {
                    drop: function (node, data, dropRec, dropPosition) {
                        // var dropOn = dropRec ? ' ' + dropPosition + ' ' + dropRec.get('name') : ' on empty view';
                    }
                }
            },

            showActionColumn: true,
            useDefaultAction: false,
            columns: [
                {
                    text: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.username,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.username'
                    },
                    dataIndex: 'username',
                    align: 'left',
                    flex: 1,
                    sorters: false
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.common.labels.description,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                    },
                    sorters: false,
                    dataIndex: 'description',
                    align: 'left',
                    flex: 1
                }
            ],

            bind: {
                store: '{notAssignedUser}'
            },
            tbar: {
                xtype: 'pagingtoolbarfiltersearch',
                bind: {
                    store: '{notAssignedUser}'
                },
                panelTitle: CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.allusers,
                emptyMsg: CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.displaynousersmessage,
                displayMsg: CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.displaytotalrecords,
                localized: {
                    panelTitle: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.allusers',
                    emptyMsg:
                        'CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.displaynousersmessage',
                    displayMsg:
                        'CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.displaytotalrecords'
                },
                searchField: {
                    emptyText: CMDBuildUI.locales.Locales.administration.groupandpermissions.emptytexts.searchusers,
                    localized: {
                        emptyText:
                            'CMDBuildUI.locales.Locales.administration.groupandpermissions.emptytexts.searchusers'
                    }
                },
                searchColumnIndexes: ['description', 'username', 'email'],
                displayInfo: true,
                margin: 5
            }
        }
    ],

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: [
                {
                    xtype: 'component',
                    flex: 1
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.actions.cancel'
                    },
                    reference: 'groupusers_cancelbtn',
                    ui: 'administration-secondary-action-small',
                    listeners: {
                        click: 'onCancelBtnClick'
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.common.actions.save,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.actions.save'
                    },
                    reference: 'groupusers_savebtn',
                    ui: 'administration-action-small',
                    listeners: {
                        click: 'onSaveBtnClick'
                    }
                }
            ]
        }
    ]
});
