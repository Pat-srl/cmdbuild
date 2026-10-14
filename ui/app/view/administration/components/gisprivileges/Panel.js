Ext.define('CMDBuildUI.view.administration.components.gisprivileges.Panel', {
    extend: 'Ext.grid.Panel',

    requires: ['CMDBuildUI.view.administration.components.gisprivileges.PanelModel'],

    alias: 'widget.administration-components-gisprivileges-panel',
    viewModel: {
        type: 'administration-components-gisprivileges-panel'
    },

    bind: {
        store: '{geoAttributes}'
    },

    columns: [
        {
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.common.labels.name,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
            },
            dataIndex: 'name',
            align: 'left'
        },
        {
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.common.labels.description,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
            },
            dataIndex: 'description',
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.default,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.default'
            },
            dataIndex: 'default',
            align: 'center',
            xtype: 'checkcolumn',
            injectCheckbox: false,
            headerCheckbox: false,
            hideable: false,
            disabled: true,
            sortable: true,
            menuDisabled: true,
            bind: {
                disabled: '{!canModifyChecks}'
            },
            listeners: {
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    record.set('mode', 'default');
                }
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.none,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.none'
            },
            dataIndex: 'none',
            align: 'center',
            xtype: 'checkcolumn',
            injectCheckbox: false,
            headerCheckbox: false,
            hideable: false,
            disabled: true,
            sortable: true,
            menuDisabled: true,
            bind: {
                disabled: '{!canModifyChecks}'
            },
            listeners: {
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    record.set('mode', 'none');
                }
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.read,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.read'
            },
            dataIndex: 'read',
            align: 'center',
            xtype: 'checkcolumn',
            headerCheckbox: false,
            disabled: true,
            hideable: false,
            menuDisabled: true,
            sortable: true,
            bind: {
                disabled: '{!canModifyChecks}'
            },
            listeners: {
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    record.set('mode', 'read');
                }
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.write,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.write'
            },
            dataIndex: 'write',
            align: 'center',
            xtype: 'checkcolumn',
            headerCheckbox: false,
            disabled: true,
            hideable: false,
            menuDisabled: true,
            sortable: true,
            bind: {
                disabled: '{!canModifyChecks}'
            },
            listeners: {
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    record.set('mode', 'write');
                }
            }
        }
    ]
});
