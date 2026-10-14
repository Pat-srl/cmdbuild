Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.uiconfig.fieldsets.DisabledProcesses', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-groupsandpermissions-tabitems-uiconfig-fieldsets-disabledprocesses',
    controller: 'administration-content-groupsandpermissions-tabitems-uiconfig-fieldsets-disabledprocesses',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-uiconfig-fieldsets-disabledprocesses'
    },
    ui: 'administration-formpagination',
    items: [{
        xtype: 'fieldset',
        ui: 'administration-formpagination',
        title: CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.managementprocesstabs,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.managementprocesstabs'
        },
        collapsible: true,
        items: [{
            xtype: 'grid',
            bind: {
                store: '{disabledProcessTabsStore}'
            },
            userCls: 'readonly-as-disabled',
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
                xtype: 'widgetcolumn',
                injectCheckbox: false,
                hideable: false,
                headerCheckbox: false,
                sortable: true,
                menuDisabled: true,
                widget: {
                    xtype: 'checkbox',
                    align: 'center',
                    margin: 'auto',
                    bind: {
                        readOnly: '{!actions.edit || record.name != "_rp_flow_tab_attachment_access"}',
                        value: '{record.write}'
                    },
                    listeners: {
                        beforechange: 'onBeforeCheckChange',
                        change: 'onCheckboxWriteChange'
                    }
                }

            }]
        }]
    }]
});