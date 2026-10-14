Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.View', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.administration-content-importexport-datatemplates-view',

    requires: [
        'CMDBuildUI.view.administration.content.importexport.datatemplates.ViewController',
        'CMDBuildUI.view.administration.content.importexport.datatemplates.ViewModel'
    ],

    controller: 'administration-content-importexport-datatemplates-view',
    viewModel: {
        type: 'administration-content-importexport-datatemplates-view'
    },
    itemId: 'administration-content-importexport-datatemplates',
    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    layout: 'border',

    listeners: {
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },

    initComponent: function () {
        if (!CMDBuildUI.util.Stores.loaded.importexporttemplates) {
            CMDBuildUI.util.Stores.loadImportExportTemplatesStore();
        }
        if (!CMDBuildUI.util.Stores.loaded.emailaccounts) {
            CMDBuildUI.util.Stores.loadEmailAccountsStore();
        }
        if (!CMDBuildUI.util.Stores.loaded.emailtemplates) {
            CMDBuildUI.util.Stores.loadEmailTemplatesStore();
        }

        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.importexport.texts.importexportdatatemplates);
        this.callParent(arguments);
    }
});