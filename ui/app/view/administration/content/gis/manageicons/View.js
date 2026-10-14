Ext.define('CMDBuildUI.view.administration.content.gis.View', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.gis.ViewController',
        'CMDBuildUI.view.administration.content.gis.ViewModel'
    ],

    alias: 'widget.administration-content-gis-view',
    controller: 'administration-content-gis-view',
    viewModel: {
        type: 'administration-content-gis-view'
    },

    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    layout: 'border',
    items: [{
        xtype: 'administration-content-gis-topbar',
        region: 'north'
    },
    {
        xtype: 'administration-content-gis-grid',
        region: 'center'
    }
    ],

    initComponent: function () {
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.gis.manageicons);
        this.callParent(arguments);
    }
});