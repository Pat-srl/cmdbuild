Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.dmsprivilegesgrid.DmsPrivileges', {
    extend: 'Ext.form.FieldSet',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.dmsprivilegesgrid.DmsPrivilegesController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.dmsprivilegesgrid.DmsPrivilegesModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-dmsprivilegesgrid-dmsprivileges',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-dmsprivilegesgrid-dmsprivileges',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-dmsprivilegesgrid-dmsprivileges'
    },
    ui: 'administration-formpagination',
    title: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.attachmentspermissions,
    localized: {
        title: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.attachmentspermissions'
    },
    bind: {
        hidden: '{isPrototype}'
    },
    items: [{
        xtype: 'grid',
        bind: {
            store: '{gridDataStore}'
        },
        viewConfig: {
            markDirty: false
        },
        sealedColumns: false,
        sortableColumns: true,
        enableColumnHide: false,
        enableColumnMove: false,
        enableColumnResize: false,
        menuDisabled: true,
        columns: [{
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.dmscategories.dmscategory,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.dmscategories.dmscategory'
            },
            dataIndex: 'description',
            align: 'left'
        }, {
            width: '75px',
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts['default'],
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.default'
            },
            dataIndex: 'default',
            align: 'center',
            xtype: 'checkcolumn',
            injectCheckbox: false,
            disabled: true,
            hideable: false,
            headerCheckbox: false,
            sortable: true,
            menuDisabled: true,
            bind: {
                disabled: '{!actions.edit}'
            },
            listeners: {
                beforecheckchange: 'onBeforeCheckChange',
                checkchange: 'onCheckChange'
            }
        }, {
            width: '75px',
            text: CMDBuildUI.locales.Locales.administration.common.strings.none,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.strings.none'
            },
            dataIndex: 'none',
            align: 'center',
            xtype: 'checkcolumn',
            injectCheckbox: false,
            disabled: true,
            hideable: false,
            headerCheckbox: false,
            sortable: true,
            menuDisabled: true,
            bind: {
                disabled: '{!actions.edit}'
            },
            listeners: {
                beforecheckchange: 'onBeforeCheckChange',
                checkchange: 'onCheckChange'
            }
        }, {
            width: '75px',
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.read,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.read'
            },
            dataIndex: 'read',
            align: 'center',
            xtype: 'checkcolumn',
            injectCheckbox: false,
            disabled: true,
            hideable: false,
            headerCheckbox: false,
            sortable: true,
            menuDisabled: true,
            bind: {
                disabled: '{!actions.edit}'
            },
            listeners: {
                beforecheckchange: 'onBeforeCheckChange',
                checkchange: 'onCheckChange'
            }
        }, {
            width: '75px',
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.write,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.write'
            },
            dataIndex: 'write',
            align: 'center',
            xtype: 'checkcolumn',
            injectCheckbox: false,
            disabled: true,
            hideable: false,
            headerCheckbox: false,
            sortable: true,
            menuDisabled: true,
            bind: {
                disabled: '{!actions.edit}'
            },
            listeners: {
                beforecheckchange: 'onBeforeCheckChange',
                checkchange: 'onCheckChange'
            }
        }]
    }]
});