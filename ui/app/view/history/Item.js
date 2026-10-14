
Ext.define('CMDBuildUI.view.history.Item', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.history.ItemController',
        'CMDBuildUI.view.history.ItemModel'
    ],

    alias: 'widget.history-item',
    controller: 'history-item',
    viewModel: {
        type: 'history-item'
    },

    bind: {
        title: '{title}'
    },

    config: {
        objectId: null,
        historyType: null,
        type: null
    },

    baseCls: Ext.baseCSSPrefix + 'historyitem',

    layout: 'fit',

    fieldDefaults: {
        labelAlign: 'top'
    }
});
