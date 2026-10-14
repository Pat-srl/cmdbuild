Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.Topbar', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.TopbarController'
    ],
    viewModel: {},
    alias: 'widget.administration-content-groupsandpermissions-topbar',
    controller: 'administration-content-groupsandpermissions-topbar',

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.addgroup,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.addgroup'
        },
        ui: 'administration-action-small',
        reference: 'addgroup',
        itemId: 'addgroup',
        autoEl: {
            'data-testid': 'administration-groupandpermission-toolbar-addGroupBtn'
        },
        bind: {
            disabled: '{!toolAction._canAdd}'
        }
    }, {
        xtype: 'admin-globalsearchfield',
        objectType: 'roles'
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'tbtext',
        dock: 'right',
        bind: {
            hidden: '{isFormHidden}',
            html: '{groupLabel}: <b data-testid="administration-groupandpermission-toolbar-groupName">{theGroup.name}</b>'
        }
    }]
});