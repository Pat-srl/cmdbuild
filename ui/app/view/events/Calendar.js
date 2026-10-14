
Ext.define('CMDBuildUI.view.events.Calendar',{
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.events.CalendarController'
    ],
    alias: 'widget.events-calendar',
    controller: 'events-calendar',

    layout: 'fit'
});
