Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.actions.ActionFieldset', {
    extend: 'Ext.form.FieldSet',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.actions.ActionFieldsetModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-actions-actionfieldset',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-actions-actionfieldset'
    },
    ui: 'administration-formpagination',
    bind: {
        hidden: '{isProcess || (isClass && isPrototype)}'
    },
    title: CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.enabledactions,
    localized: {
        title: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.enabledactions'
    },
    items: [{
        xtype: 'checkboxgroup',
        columns: 1,
        vertical: true,
        items: [{
            boxLabel: CMDBuildUI.locales.Locales.administration.common.actions.create,
            localized: {
                boxLabel: 'CMDBuildUI.locales.Locales.administration.common.actions.create'
            },
            itemId: '_can_create',
            hidden: true,
            bind: {
                value: '{grant._can_create}',
                readOnly: '{actions.view}',
                hidden: '{isView}'
            }
        }, {
            boxLabel: CMDBuildUI.locales.Locales.administration.common.actions.update,
            localized: {
                boxLabel: 'CMDBuildUI.locales.Locales.administration.common.actions.update'
            },
            itemId: '_can_update',
            hidden: true,
            bind: {
                value: '{grant._can_update}',
                readOnly: '{actions.view}',
                hidden: '{isView}'
            }
        }, {
            boxLabel: CMDBuildUI.locales.Locales.administration.common.actions['delete'],
            localized: {
                boxLabel: 'CMDBuildUI.locales.Locales.administration.common.actions.delete'
            },
            itemId: '_can_delete',
            hidden: true,
            bind: {
                value: '{grant._can_delete}',
                readOnly: '{actions.view}',
                hidden: '{isView}'
            }
        }, {
            boxLabel: CMDBuildUI.locales.Locales.administration.common.actions.clone,
            localized: {
                boxLabel: 'CMDBuildUI.locales.Locales.administration.common.actions.clone'
            },
            itemId: '_can_clone',
            hidden: true,
            bind: {
                value: '{grant._can_clone}',
                readOnly: '{actions.view}',
                hidden: '{isView}'
            }
        }, {
            boxLabel: CMDBuildUI.locales.Locales.administration.common.actions.relationchart,
            localized: {
                boxLabel: 'CMDBuildUI.locales.Locales.administration.common.actions.relationchart'
            },
            itemId: '_relgraph_access',
            hidden: true,
            bind: {
                value: '{grant._relgraph_access}',
                readOnly: '{actions.view}',
                hidden: '{isView}'
            }
        }, {
            boxLabel: CMDBuildUI.locales.Locales.administration.common.actions.print,
            localized: {
                boxLabel: 'CMDBuildUI.locales.Locales.administration.common.actions.print'
            },
            itemId: '_can_print',
            bind: {
                value: '{grant._can_print}',
                readOnly: '{actions.view}'
            }
        }]
    }]
});