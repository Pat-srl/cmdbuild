Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.tabsconfiggrid.TabsConfig', {
    extend: 'Ext.form.FieldSet',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.tabsconfiggrid.TabsConfigController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.tabsconfiggrid.TabsConfigModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-tabsconfiggrid-tabsconfig',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-tabsconfiggrid-tabsconfig',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-tabsconfiggrid-tabsconfig'
    },
    ui: 'administration-formpagination',
    title: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.tabs,
    localized: {
        title: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.tabs'
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
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.tab,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.tab'
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
            text: CMDBuildUI.locales.Locales.administration.common.strings.hidden,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.strings.hidden'
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
            renderer: function (value, cell, record) {
                var vm = this.lookupViewModel();
                if (vm.get('actions.view') || vm.get('grant.objectType') === CMDBuildUI.util.helper.ModelHelper.objecttypes.process || record.get('name') === '_history_access') {
                    cell.tdCls = 'x-item-disabled';
                }

                if (Ext.isEmpty(value)) {
                    return Ext.String.format("<span class=\"x-grid-checkcolumn\"></span>");
                }
                var klass = '';
                if (value) {
                    klass = 'x-grid-checkcolumn-checked';
                }
                return Ext.String.format("<span class=\"{0} x-grid-checkcolumn \"></span>", klass);
            },
            listeners: {
                beforecheckchange: 'onBeforeCheckChange',
                checkchange: 'onCheckChange'
            }
        }]
    }]
});