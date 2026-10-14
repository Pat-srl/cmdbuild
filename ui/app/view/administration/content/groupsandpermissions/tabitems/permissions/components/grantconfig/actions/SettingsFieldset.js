Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.actions.SettingsFieldset', {
    extend: 'Ext.form.FieldSet',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.actions.SettingsFieldsetController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.actions.SettingsFieldsetModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-actions-settingsfieldset',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-actions-settingsfieldset',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-actions-settingsfieldset'
    },
    ui: 'administration-formpagination',
    bind: {
        title: '{settingsOrActionsTitle}'
    },
    items: [{
        xtype: 'fieldcontainer',
        fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.cardbulkedit,
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.cardbulkedit'
        },
        hidden: true,
        bind: {
            hidden: '{isProcess || isView}'
        },
        items: [{
            itemId: '_can_bulk_update_value',
            xtype: 'combobox',
            displayField: 'label',
            valueField: 'value',
            bind: {
                value: '{_can_bulk_update_value}',
                store: '{canBulkUpdateStore}',
                disabled: '{!actions.edit}'
            }
        }]
    }, {
        xtype: 'fieldcontainer',
        fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.cardbulkdeletion,
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.cardbulkdeletion'
        },
        hidden: true,
        bind: {
            hidden: '{isProcess || isView}'
        },
        items: [{
            itemId: '_can_bulk_delete_value',
            xtype: 'combobox',
            displayField: 'label',
            valueField: 'value',
            bind: {
                value: '{_can_bulk_delete_value}',
                store: '{canBulkDeleteStore}',
                disabled: '{!actions.edit}'
            }
        }]
    }, {
        xtype: 'fieldcontainer',
        hidden: true,
        fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.enableattachmenttoclosedactivities,
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.enableattachmenttoclosedactivities'
        },
        bind: {
            hidden: '{isClass || isView}'
        },
        items: [{
            xtype: 'combobox',
            name: 'chk_group',
            reference: 'chk_group',
            displayField: 'label',
            valueField: 'value',
            bind: {
                value: '{_can_fc_attachment_value}',
                store: '{canAttachmentStore}',
                disabled: '{!actions.edit}'
            }
        }]
    }, {
        xtype: 'fieldcontainer',
        hidden: true,
        fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.bulkabort,
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.bulkabort'
        },
        bind: {
            hidden: '{isClass || isView}'
        },
        items: [{
            xtype: 'combobox',
            displayField: 'label',
            valueField: 'value',
            bind: {
                value: '{_can_bulk_abort_value}',
                store: '{canBulkAbortStore}',
                disabled: '{!actions.edit}'
            }
        }]
    }, {
        xtype: 'fieldcontainer',
        fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.disablesearchfieldingrids,
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.disablesearchfieldingrids'
        },
        items: [{
            xtype: 'combobox',
            itemId: 'fulltext',
            valueField: 'value',
            displayField: 'label',
            allowBlank: false,
            forceSelection: true,
            disabled: true,
            bind: {
                value: '{_can_search_value}',
                store: '{fulltextStore}',
                disabled: '{actions.view}'
            }
        }]
    }]

});