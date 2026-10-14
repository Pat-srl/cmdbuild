Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.View', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.ViewController',
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.ViewModel'
    ],
    alias: 'widget.administration-content-importexport-gatetemplates-view',
    controller: 'administration-content-importexport-gatetemplates-view',
    viewModel: {
        type: 'administration-content-importexport-gatetemplates-view'
    },
    itemId: 'administration-content-importexport-gatetemplate',
    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    layout: 'border',
    items: [{
        xtype: 'administration-content-importexport-gatetemplates-tabpanel',
        region: 'center',
        hidden: true,
        bind: {
            hidden: '{hideForm}'
        }
    }],

    listeners: {
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },
    dockedItems: [{
        xtype: 'administration-content-importexport-gatetemplates-topbar',
        dock: 'top',
        borderBottom: 0,
        itemId: 'toolbarscontainer'
    }]    
});