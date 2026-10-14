Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.uiconfig.UiConfig', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.uiconfig.UiConfigController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.uiconfig.UiConfigModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-uiconfig-uiconfig',
    controller: 'administration-content-groupsandpermissions-tabitems-uiconfig-uiconfig',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-uiconfig-uiconfig'
    },
    autoScroll: false,
    layout: 'border',
    items: [{
        xtype: 'panel',
        region: 'center',
        autoScroll: 'y',
        viewModel: {},
        items: [{
            xtype: 'administration-content-groupsandpermissions-tabitems-uiconfig-fieldsets-disabledallelements'
        }, {
            xtype: 'administration-content-groupsandpermissions-tabitems-uiconfig-fieldsets-disabledclasses'
        }, {
            xtype: 'administration-content-groupsandpermissions-tabitems-uiconfig-fieldsets-disabledprocesses'
        }, {
            xtype: 'administration-content-groupsandpermissions-tabitems-uiconfig-fieldsets-settings'
        }, {
            xtype: 'administration-content-groupsandpermissions-tabitems-uiconfig-fieldsets-chat'
        }]
    }],
    dockedItems: [{
        dock: 'top',
        xtype: 'container',
        items: [{
            xtype: 'components-administration-toolbars-formtoolbar',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: true
            }, 'groupandpermission', 'theGroup')
        }]
    }, {
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