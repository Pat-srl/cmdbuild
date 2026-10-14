Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.group.Properties', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.group.PropertiesController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.group.fieldsets.LimitedAdminPermissionsFieldset'
    ],

    alias: 'widget.administration-content-groupsandpermissions-tabitems-group-properties',
    controller: 'administration-content-groupsandpermissions-tabitems-group-properties',
    autoScroll: false,
    modelValidation: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    layout: 'border',
    items: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'north',
        bind: {
            hidden: '{actions.view}'
        },
        items: [{
            xtype: 'button',
            itemId: 'spacer',
            style: {
                "visibility": "hidden"
            }
        }]
    }, {
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'north',
        bind: {
            hidden: '{!actions.view}'
        },
        items: [{
            xtype: 'button',
            itemId: 'spacer',
            style: {
                "visibility": "hidden"
            }
        }, {
            xtype: 'tbfill'
        }, {
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
                'data-testid': 'administration-groupandpermission-group-tool-editbtn'
            },
            bind: {
                hidden: '{toolbarHiddenButtons.edit}',
                disabled: '{!toolAction._canUpdate}'
            }
        }, {
            xtype: 'tool',
            align: 'right',
            itemId: 'disableBtn',
            cls: 'administration-tool',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ban', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.disable,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.disable'
            },
            callback: 'onToggleEnableBtnClick',
            hidden: true,
            autoEl: {
                'data-testid': 'administration-groupandpermission-group-tool-disablebtn'
            },
            bind: {
                hidden: '{toolbarHiddenButtons.disable}',
                disabled: '{!toolAction._canActiveToggle}'
            }
        }, {
            xtype: 'tool',
            align: 'right',
            itemId: 'cloneBtn',
            cls: 'administration-tool',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('clone', 'regular'),
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.clone,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.clone'
            },
            callback: 'onCloneBtnClick',
            hidden: true,
            autoEl: {
                'data-testid': 'administration-groupandpermission-group-tool-clonebtn'
            },
            bind: {
                hidden: '{toolbarHiddenButtons.clone}',
                disabled: '{!toolAction._canAdd}'
            }
        }, {
            xtype: 'tool',
            align: 'right',
            itemId: 'enableBtn',
            hidden: true,
            cls: 'administration-tool',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('check-circle', 'regular'),
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.enable,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.enable'
            },
            callback: 'onToggleEnableBtnClick',
            autoEl: {
                'data-testid': 'administration-groupandpermission-group-tool-enablebtn'
            },
            bind: {
                hidden: '{toolbarHiddenButtons.enable}'
            }
        }]
    }, {
        xtype: 'panel',
        region: 'center',
        scrollable: 'y',
        items: [{
            xtype: 'administration-content-groupsandpermissions-tabitems-group-fieldsets-generaldatafieldset'
        }, {
            xtype: 'administration-content-groupsandpermissions-tabitems-group-fieldsets-limitedadminpermissionsfieldset',
            itemId: 'limitedadminpermissionsfieldset',
            hidden: true,
            bind: {
                hidden: '{theGroup.type != \'admin_limited\'}'
            }
        }]
    }],
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
    }]
});