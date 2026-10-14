Ext.define('CMDBuildUI.view.administration.content.schedules.settings.View', {
    extend: 'Ext.tab.Panel',

    requires: ['CMDBuildUI.view.administration.content.schedules.settings.ViewController'],
    alias: 'widget.administration-content-schedules-settings-view',
    controller: 'administration-content-schedules-settings-view',

    scrollable: true,
    forceFit: true,
    layout: 'fit',

    cls: 'administration-mainview-tabpanel',
    ui: 'administration-tabandtools',

    tabPosition: 'top',
    tabRotation: 0
});
