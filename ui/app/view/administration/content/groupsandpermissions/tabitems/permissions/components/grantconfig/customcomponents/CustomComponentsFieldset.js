Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.customcomponents.CustomComponentsFieldset', {
    extend: 'Ext.form.FieldSet',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.customcomponents.CustomComponentsFieldsetModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-customcomponents-customcomponentsfieldset',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-customcomponents-customcomponentsfieldset'
    },
    title: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.tabs,
    localized: {
        title: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.tabs'
    },
    ui: 'administration-formpagination',
    hidden: true,
    bind: {
        hidden: '{fieldsetHidden}',
        title: '{fieldsetTitle}'
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
            bind: {
                text: '{descriptionLabel}'
            },
            dataIndex: 'description',
            align: 'left'
        }, {
            width: '75px',
            text: CMDBuildUI.locales.Locales.administration.common.labels.show,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.show'
            },
            dataIndex: 'show',
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
                headercheckchange: function (columnHeader, checked, e, eOpts) {
                    var vm = columnHeader.lookupViewModel();
                    this.getView().getStore().each(function (record) {
                        vm.set(Ext.String.format('grant._{0}_{1}_access', vm.get('componentType'), record.get('id')), checked);
                        record.set('visible', checked);
                    });
                },
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    var vm = check.lookupViewModel();
                    vm.set(Ext.String.format('grant._{0}_{1}_access', vm.get('componentType'), record.get('id')), checked);
                    record.set('visible', checked);
                }
            }
        }]
    }]
});