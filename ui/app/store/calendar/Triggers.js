Ext.define('CMDBuildUI.store.calendar.Triggers', {
    extend: 'Ext.data.BufferedStore',

    requires: [
        'Ext.data.BufferedStore',
        'CMDBuildUI.model.calendar.Trigger'
    ],

    alias: 'store.calendar-triggers',

    model: 'CMDBuildUI.model.calendar.Trigger',

    sorters: ['description'],

    proxy: {
        url: '/calendar/triggers',
        type: 'baseproxy',
        extraParams: {
            detailed: true
        }
    },
    pageSize: 100,
    remoteFilter: true,
    remoteSort: true,
    autoLoad: false
});