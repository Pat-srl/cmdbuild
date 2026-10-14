
Ext.define('CMDBuildUI.view.relations.masterdetail.TabPanel',{
    extend: 'Ext.tab.Panel',

    requires: [
        'CMDBuildUI.view.relations.masterdetail.TabPanelController',
        'CMDBuildUI.view.relations.masterdetail.TabPanelModel'
    ],

    alias: 'widget.relations-masterdetail-tabpanel',
    controller: 'relations-masterdetail-tabpanel',
    viewModel: {
        type: 'relations-masterdetail-tabpanel'
    },

    ui: 'light',

    autoScroll: true,
    tabPosition: "right",
    tabRotation: 0,

    config: {
        /**
         * @cfg {readOnly} 
         * 
         * Set to `true` to shwow details tabs in read-only mode
         */
        readOnly: false
    }

});
