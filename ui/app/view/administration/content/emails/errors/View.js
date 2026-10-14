Ext.define('CMDBuildUI.view.administration.content.emails.errors.View', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.errors.ViewModel'
    ],
    alias: 'widget.administration-content-emails-errors-view',
    viewModel: {
        type: 'administration-content-emails-errors-view'
    },
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    layout: 'border',
    items: [{
        xtype: 'administration-content-emails-errors-grid', region: 'center'
    }],
    listeners: {
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },

    initComponent: function () {
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.emails.emailerrrors);
        this.callParent(arguments);
    }
});