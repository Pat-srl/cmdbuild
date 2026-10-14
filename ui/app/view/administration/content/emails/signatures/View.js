
Ext.define('CMDBuildUI.view.administration.content.emails.signatures.View',{
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.signatures.ViewController',
        'CMDBuildUI.view.administration.content.emails.signatures.ViewModel'
    ],

    alias: 'widget.administration-content-emails-signatures-view',
    controller: 'administration-content-emails-signatures-view',
    viewModel: {
        type: 'administration-content-emails-signatures-view'
    },

    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    layout: 'border',
    items: [
        { xtype: 'administration-content-emails-signatures-topbar', region: 'north' },
        { xtype: 'administration-content-emails-signatures-grid', region: 'center' } //,bind: {hidden:'{isGridHidden}' }
    ],

    listeners: {
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },

    initComponent: function () {
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.emails.emailsignatures);
        this.callParent(arguments);
    }
});
