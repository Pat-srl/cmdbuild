Ext.define('CMDBuildUI.view.administration.home.widgets.modelsstats.ModelsStatsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-home-widgets-modelsstats-modelsstats',

    data: {
        data: []
    },

    stores: {
        modelsStats: {
            proxy: 'memory',
            sorters: ['index'],
            fields: [
                {
                    type: 'string',
                    name: 'type'
                },
                {
                    type: 'integer',
                    name: 'count'
                },
                {
                    type: 'string',
                    name: 'description'
                },
                {
                    type: 'integer',
                    name: 'index'
                }
            ],
            data: '{data}'
        }
    }
});
