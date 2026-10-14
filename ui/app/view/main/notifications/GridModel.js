Ext.define('CMDBuildUI.view.main.notifications.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.main-notifications-grid',

    stores: {
        notifications: {
            type: 'buffered',
            model: 'CMDBuildUI.model.messages.Notification',
            sorters: [{
                property: 'timestamp',
                direction: 'DESC'
            }],
            advancedFilter: {
                attributes: {
                    sourceType: {
                        operator: CMDBuildUI.util.helper.FiltersHelper.operators.equal,
                        value: ['system']
                    }
                }
            },
            autoLoad: '{isAuthenticated}',
            autoDestroy: false,
            leadingBufferZone: 100,
            pageSize: 50
        }
    }

});
