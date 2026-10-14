Ext.define('CMDBuildUI.view.dependencytree.tab.Gerarchic', {
    extend: 'Ext.panel.Panel',

    requires: ['CMDBuildUI.view.dependencytree.tab.GerarchicController'],

    alias: 'widget.dependencytree-tab-gerarchic',
    controller: 'dependencytree-tab-gerarchic',

    mixins: ['CMDBuildUI.view.dependencytree.Mixin'],

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'component',
            itemId: 'textNoCard',
            padding: 15,
            bind: {
                hidden: '{!disableTabCard || isGroupNode}'
            }
        },
        {
            html: '<svg style="width: 100%"; id="gerarchicTree"></svg>',
            flex: 1,
            scrollable: true
        }
    ]
});
