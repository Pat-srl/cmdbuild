Ext.define('CMDBuildUI.view.administration.content.setup.elements.NotificationsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-notifications',

    stores: {
        defaultEmailDelay: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: [{
                value: '0',
                label: '0'
            }, {
                value: '5',
                label: '5'
            }, {
                value: '10',
                label: '10'
            }, {
                value: '20',
                label: '20'
            }, {
                value: '30',
                label: '30'
            }],
            autoDestroy: true
        }
    }

});