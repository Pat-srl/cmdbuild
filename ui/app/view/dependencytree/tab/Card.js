Ext.define('CMDBuildUI.view.dependencytree.tab.Card', {
    extend: 'Ext.panel.Panel',

    requires: ['CMDBuildUI.view.dependencytree.tab.CardController'],

    alias: 'widget.dependencytree-tab-card',
    controller: 'dependencytree-tab-card',

    layout: {
        type: 'vbox',
        align: 'stretch'
    }
});
