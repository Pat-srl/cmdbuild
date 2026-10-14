Ext.define('CMDBuildUI.view.administration.content.localizations.localization.View', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.localizations.localization.ViewController',
        'CMDBuildUI.view.administration.content.localizations.localization.ViewModel'
    ],

    alias: 'widget.administration-content-localizations-localization-view',
    controller: 'administration-content-localizations-localization-view',
    viewModel: {
        type: 'administration-content-localizations-localization-view'
    },

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true,
        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
        title: null
    },
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    layout: 'border',
    style: 'background-color:#fff',
    items: [{
            xtype: 'administration-content-localizations-localization-topbar',
            region: 'north'
        },
        {
            xtype: 'administration-content-localizations-localization-tabpanel',
            region: 'center',
            viewModel: {}            
        }
    ],

    listeners: {
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },
    initComponent: function () {        
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.localizations.localization);
        this.callParent(arguments);
    }
});