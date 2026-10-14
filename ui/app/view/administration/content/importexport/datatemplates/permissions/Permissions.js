Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.permissions.Permissions', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-importexport-datatemplates-permissions-permissions',
    requires: [
        'CMDBuildUI.view.administration.content.importexport.datatemplates.permissions.PermissionsController',
        'CMDBuildUI.view.administration.content.importexport.datatemplates.permissions.PermissionsModel'
    ],

    controller: 'administration-content-importexport-datatemplates-permissions-permissions',
    viewModel: {
        type: 'administration-content-importexport-datatemplates-permissions-permissions'
    },
    scrollable: 'y',
    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },

    items: [
        {
            xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
            objectType: 'etltemplate',
            gridType: 'object',
            bind: {
                store: '{grantsChainedStore}'
            },
            columns: [
                {
                    flex: 9,
                    text: CMDBuildUI.locales.Locales.administration.common.labels.description,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                    },
                    renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                        if (this.getView().up('grid').gridType === 'object') {
                            var groupsStore = Ext.getStore('groups.Groups');
                            return groupsStore.getById(record.get('role')).get('description');
                        }
                        return value;
                    },
                    dataIndex: '_object_description',
                    align: 'left'
                },
                {
                    flex: 1,
                    text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.none,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.none'
                    },
                    dataIndex: 'modeTypeNone',
                    align: 'center',
                    xtype: 'checkcolumn',
                    injectCheckbox: false,
                    headerCheckbox: false,
                    disabled: true,
                    hideable: false,
                    hidden: true,
                    sortable: true,
                    bind: {
                        hidden: '{hiddenColumns.modeTypeNone}',
                        disabled: '{!actions.edit}',
                        headerCheckbox: '{actions.edit}'
                    },
                    listeners: {
                        headercheckchange: function (columnHeader, checked, e, eOpts) {
                            var view = this.getView();
                            view.up()._headerCheckChange(CMDBuildUI.model.users.Grant.grantType.none);
                        },
                        checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                            record.changeMode(CMDBuildUI.model.users.Grant.grantType.none);
                        }
                    }
                },
                {
                    flex: 1,
                    text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.allow,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.allow'
                    },
                    dataIndex: 'modeTypeAllow',
                    align: 'center',
                    xtype: 'checkcolumn',
                    injectCheckbox: false,
                    headerCheckbox: false,
                    disabled: true,
                    hideable: false,
                    hidden: true,
                    sortable: true,
                    bind: {
                        hidden: '{hiddenColumns.modeTypeAllow}',
                        disabled: '{!actions.edit}',
                        headerCheckbox: '{actions.edit}'
                    },
                    listeners: {
                        headercheckchange: function (columnHeader, checked, e, eOpts) {
                            var view = this.getView();
                            view.up()._headerCheckChange(CMDBuildUI.model.users.Grant.grantType.read);
                        },
                        checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                            record.changeMode(CMDBuildUI.model.users.Grant.grantType.read);
                        }
                    }
                }
            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'components-administration-toolbars-formtoolbar',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: true
            })
        },
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons({ formBind: false })
        }
    ]
});
