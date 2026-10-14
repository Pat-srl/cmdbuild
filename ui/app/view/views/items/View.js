
Ext.define('CMDBuildUI.view.views.items.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.views.items.ViewController',
        'CMDBuildUI.view.views.items.ViewModel',

        'CMDBuildUI.util.helper.FormHelper'
    ],

    alias: 'widget.views-items-view',
    controller: 'views-items-view',
    viewModel: {
        type: 'views-items-view'
    },

    autoScroll: true,

    fieldDefaults: {
        labelAlign: 'top'
    }

});
