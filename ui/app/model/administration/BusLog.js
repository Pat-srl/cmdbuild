Ext.define('CMDBuildUI.model.administration.BusLog', {
    extend: 'CMDBuildUI.model.base.Base',
    statics: {
        statuses: {
            draft: 'draft',
            queued: 'queued',
            processing: 'processing',
            processed: 'processed',
            error: 'error',
            failed: 'failed',
            completed: 'completed'
        },
        getStatuses: function () {
            var data = [{
                value: 'draft',
                label: CMDBuildUI.locales.Locales.administration.busmessages.draft
            }, {
                value: 'queued',
                label: CMDBuildUI.locales.Locales.administration.busmessages.queued
            }, {
                value: 'processing',
                label: CMDBuildUI.locales.Locales.administration.busmessages.processing
            }, {
                value: 'processed',
                label: CMDBuildUI.locales.Locales.administration.busmessages.processed
            }, {
                value: 'error',
                label: CMDBuildUI.locales.Locales.administration.busmessages.error
            }, {
                value: 'failed',
                label: CMDBuildUI.locales.Locales.administration.busmessages.failed
            }, {
                value: 'completed',
                label: CMDBuildUI.locales.Locales.administration.busmessages.completed
            }];

            return data;
        }
    },
    fields: [{
        name: 'messageId',
        type: 'string'
    }, {
        name: 'nodeId',
        type: 'string'
    }, {
        name: 'queue',
        type: 'string'
    }, {
        name: 'status',
        type: 'string'
    }, {
        name: 'timestamp',
        type: 'string'
    }, {
        name: 'beginDate',
        type: 'string'
    }, {
        name: '_status_description',
        type: 'string',
        calculate: function (data) {
            return CMDBuildUI.locales.Locales.administration.busmessages[data.status];
        }
    }

    ],

    proxy: {
        type: 'baseproxy',
        url: '/etl/messages'
    }
});