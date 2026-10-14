Ext.define('CMDBuildUI.view.administration.content.webhooks.Panel', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.administration-content-webhooks-panel',
    controller: 'administration-content-webhooks-panel',

    requires: [
        'CMDBuildUI.view.administration.content.webhooks.PanelController'
    ],

    tabPosition: 'top',
    tabRotation: 0,
    cls: 'administration-mainview-tabpanel',
    ui: 'administration-tabandtools',
    scrollable: true,
    forceFit: true,
    layout: 'fit',
    tabBar: {
        hidden: true
    },
    bind: {
        activeTab: '{activeTabs.webhooks}'
    },
    dockedItems: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        dock: 'top',
        padding: '5 10 5 10',
        borderBottom: 0,
        itemId: 'toolbarscontainer',
        style: 'border-bottom-width:0!important',
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({},
            'searchfilter',
            'theViewFilter',
            [{
                xtype: 'button',
                text: CMDBuildUI.locales.Locales.administration.webhooks.addwebhook, // Add view
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.webhooks.addwebhook' // Add view
                },
                ui: 'administration-action-small',
                itemId: 'addBtn',
                autoEl: {
                    'data-testid': 'administration-class-toolbar-addViewBtn'
                },
                bind: {
                    disabled: '{!toolAction._canAdd}'
                }
            }, {
                xtype: 'localsearchfield',
                gridItemId: '#webhookgrid'
            }, {
                xtype: 'tbfill'
            }])
    }],
    initComponent: function () {
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.webhooks);
        this.callParent(arguments);
    }


});
