Ext.define('CMDBuildUI.view.administration.content.tasks.jobruns.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-tasks-jobruns-grid',
    data: {
        storeautoload: true
    },
    formulas: {
        statuses: function () {
            return CMDBuildUI.model.administration.JobRunner.getStatuses();
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

        jobrunsStore: {
            model: 'CMDBuildUI.model.administration.JobRunner',
            type: 'buffered',
            proxy: {
                type: 'baseproxy',
                url: '/jobs/_ANY/runs',
                extraParams: {
                    detailed: true
                }
            },
            sorters: [{
                property: 'timestamp',
                direction: 'DESC'
            }],
            pageSize: 50,
            remoteFilter: true,
            remoteSort: true,
            autoLoad: '{storeautoload}',
            autoDestroy: true
        }
    }
});