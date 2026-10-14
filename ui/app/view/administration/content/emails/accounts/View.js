
Ext.define('CMDBuildUI.view.administration.content.emails.accounts.View',{
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.accounts.ViewController',
        'CMDBuildUI.view.administration.content.emails.accounts.ViewModel'
    ],

    alias: 'widget.administration-content-emails-accounts-view',
    controller: 'administration-content-emails-accounts-view',
    viewModel: {
        type: 'administration-content-emails-accounts-view'
    },

    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    layout: 'border',
    items: [
        { xtype: 'administration-content-emails-accounts-topbar', region: 'north' },
        { xtype: 'administration-content-emails-accounts-grid', region: 'center' } //,bind: {hidden:'{isGridHidden}' }
    ],

    listeners: {
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },

    initComponent: function () {
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.emails.emailaccounts);
        this.callParent(arguments);
    }
});
