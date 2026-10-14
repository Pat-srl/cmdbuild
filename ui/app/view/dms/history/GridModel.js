Ext.define('CMDBuildUI.view.dms.history.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.dms-history-grid',

    data: {
        proxyUrlHistory: undefined
    },

    formulas: {
        proxyUrlHistory: {
            bind: {
                proxyUrl: '{proxyUrl}',
                attachmentId: '{record._id}'
            },
            get: function (data) {
                return Ext.String.format('{0}/{1}/history',
                    data.proxyUrl,
                    data.attachmentId
                );
            }
        }
    },

    stores: {
        attachmentshistory: {
            type: 'attachments',
            autoLoad: true,
            proxy: {
                url: '{proxyUrlHistory}',
                type: 'baseproxy'
            }
        }
    }
});
