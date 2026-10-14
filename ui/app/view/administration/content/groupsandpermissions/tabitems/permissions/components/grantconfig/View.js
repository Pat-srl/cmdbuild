Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.ViewController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.ViewModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-view',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-view',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-view'
    },

    config: {
        componentType: null,
        grant: null,
        actions: null
    },
    publishes: ['componentType', 'grant', 'actions'],
    twoWayBindable: ['componentType', 'grant', 'actions'],
    bind: {
        componentType: '{componentType}',
        hidden: '{fieldsetHidden}',
        grant: '{grant}',
        actions: '{actions}'
    },
    scrollable: 'y',
    reference: 'customPrivilegesChecks',

    items: [],

    fbar: [{
        text: CMDBuildUI.locales.Locales.administration.common.actions.ok,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.actions.ok'
        },
        reference: 'savebutton',
        itemId: 'savebutton',
        bind: {
            hidden: '{actions.view}'
        },
        listeners: {
            click: function (button, event, eOpts) {
                CMDBuildUI.util.Utilities.closePopup('popup-grantconfig-config');
            }
        },
        ui: 'administration-action'
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.actions.cancel'
        },
        reference: 'cancelbutton',
        ui: 'administration-secondary-action',
        bind: {
            hidden: '{actions.view}'
        },
        listeners: {
            click: function (button, event, eOpts) {
                var record = this.getViewModel().get('record');
                record.reject();
                CMDBuildUI.util.Utilities.closePopup('popup-grantconfig-config');
            }
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.actions.close,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.actions.close'
        },
        reference: 'closebutton',
        ui: 'administration-secondary-action',
        bind: {
            hidden: '{actions.edit}'
        },
        handler: function (button) {
            CMDBuildUI.util.Utilities.closePopup('popup-grantconfig-config');
        }

    }],

    initComponent: function () {

        var vm = this.getViewModel();
        var actions = this.getActions();
        var grant = this.getGrant();
        //vm.set('actions', actions);        
        vm.set('grant', grant);
        this.callParent(arguments);
        this.add({
            xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-actions-actionfieldset',
            grant: grant,
            actions: actions
        });
        this.add({
            xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-actions-settingsfieldset',
            grant: grant,
            actions: actions
        });
        this.add({
            xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-tabsconfiggrid-tabsconfig',
            componentType: '{componentType}',
            grant: grant,
            actions: actions
        });
        this.add({
            xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-customcomponents-customcomponentsfieldset',
            componentType: 'widget',
            grant: grant,
            actions: actions
        });
        this.add({
            xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-customcomponents-customcomponentsfieldset',
            componentType: 'contextmenu',
            grant: grant,
            actions: actions
        });
        this.add({
            xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-dmsprivilegesgrid-dmsprivileges',
            grant: grant,
            actions: actions
        });
    }

});