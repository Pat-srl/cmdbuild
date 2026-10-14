Ext.define('CMDBuildUI.view.administration.content.bus.messages.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-bus-messages-grid',
    data: {
        storeautoload: true
    },
    formulas: {
        statuses: function () {
            return CMDBuildUI.model.administration.BusLog.getStatuses();
        }
    },
    stores: {
        statusesStore: {
            proxy: {
                type: 'memory'
            },
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{statuses}',
            sorters: ['label'],
            autoDestroy: true
        },

        messagesStore: {
            model: 'CMDBuildUI.model.administration.BusLog',
            type: 'buffered',
            proxy: {
                type: 'baseproxy',
                url: '/etl/messages'
            },
            sorters: [{
                property: 'timestamp',
                direction: 'DESC'
            }],
            pageSize: 50,
            leadingBufferZone: 100,
            remoteFilter: true,
            remoteSort: true,
            autoLoad: '{storeautoload}',
            autoDestroy: true
        }
    }
});