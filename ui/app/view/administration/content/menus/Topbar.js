Ext.define('CMDBuildUI.view.administration.content.menus.Topbar', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.menus.TopbarController',
        'CMDBuildUI.view.administration.content.menus.TopbarModel'
    ],

    alias: 'widget.administration-content-menus-topbar',
    controller: 'administration-content-menus-topbar',
    viewModel: {
        type: 'administration-content-menus-topbar'
    },

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.menus.texts.add,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.menus.texts.add'
        },
        ui: 'administration-action-small',
        reference: 'addBtn',
        itemId: 'addBtn',
        autoEl: {
            'data-testid': 'administration-menu-toolbar-addMenuBtn'
        },
        bind: {
            disabled: '{!toolAction._canAdd}'
        }
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'tbtext',
        dock: 'right',
        bind: {
            hidden: '{!theMenuName}',
            html: CMDBuildUI.locales.Locales.administration.menus.singular + ': <b data-testid="administration-menus-toolbar-menuName">{theMenuName}</b>'
        }
    }]
});