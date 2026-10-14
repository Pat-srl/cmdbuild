Ext.define('CMDBuildUI.view.administration.content.tasks.card.croneditor.TabPanel', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.administration-content-tasks-card-croneditor-tabpanel',
    controller: 'administration-content-tasks-card-croneditor-tabpanel',

    requires: [
        'CMDBuildUI.view.administration.content.classes.TabPanelController'
    ],

    tabPosition: 'top',
    tabRotation: 0,
    cls: 'administration-mainview-tabpanel',
    ui: 'administration-tabandtools',
    scrollable: true,
    forceFit: true,
    bind: {
        activeTab: '{activeTab}'
    }

});