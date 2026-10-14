Ext.define('CMDBuildUI.model.administration.JobRunner', {
    extend: 'CMDBuildUI.model.base.Base',
    statics: {
        getErrorLevels: function () {
            return [{
                value: 'DEFAULT',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.default
            }, {
                value: 'TRACE',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.trace
            }, {
                value: 'DEBUG',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.debug
            }, {
                value: 'INFO',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.info
            }, {
                value: 'WARN',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.warn
            }, {
                value: 'ERROR',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.error
            }];
        },
        statuses: {
            running: 'running',
            completed: 'completed',
            failed: 'failed'
        },
        getStatuses: function () {
            var data = [{
                value: 'running',
                label: CMDBuildUI.locales.Locales.administration.jobruns.running
            }, {
                value: 'completed',
                label: CMDBuildUI.locales.Locales.administration.jobruns.completed
            }, {
                value: 'failed',
                label: CMDBuildUI.locales.Locales.administration.jobruns.failed
            }];


            return data;
        }
    },
    fields: [{
        name: 'jobCode',
        type: 'string'
    }, {
        name: 'nodeId',
        type: 'string'
    }, {
        name: 'status',
        type: 'string'
    }, {
        name: 'timestamp',
        type: 'string'
    }, {
        name: 'elapsedMillis',
        type: 'string'
    }, {
        name: '_status_description',
        type: 'string',
        calculate: function (data) {
            return CMDBuildUI.locales.Locales.administration.jobruns[data.status];
        }
    }, {
        name: 'errors',
        type: 'auto',
        defaultValue: []
    }, {
        name: 'logs',
        type: 'string'
    }

    ],

    proxy: {
        type: 'baseproxy',
        url: '/jobs/_ANY/runs',
        extraParams: {
            detailed: true
        }
    }
});