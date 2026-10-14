
Ext.define('CMDBuildUI.view.joinviews.items.View', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.joinviews.items.ViewController'
    ],

    mixins: [
        'CMDBuildUI.mixins.DetailsTabPanel'
    ],

    alias: 'widget.joinviews-items-view',
    controller: 'joinviews-items-view',

    ui: 'managementlighttabpanel',
    tabPosition: 'top',
    tabRotation: 0
});
