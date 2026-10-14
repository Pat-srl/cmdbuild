Ext.define('CMDBuildUI.view.administration.content.emails.queue.View', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.queue.ViewController',
        'CMDBuildUI.view.administration.content.emails.queue.ViewModel'
    ],
    alias: 'widget.administration-content-emails-queue-view',
    controller: 'administration-content-emails-queue-view',
    viewModel: {
        type: 'administration-content-emails-queue-view'
    },
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    layout: 'border',
    items: [{
        xtype: 'administration-content-emails-queue-grid', region: 'center'
    }],
    listeners: {
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },

    initComponent: function () {
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.emails.emailqueue);
        this.callParent(arguments);
    }
});