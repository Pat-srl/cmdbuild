Ext.define('CMDBuildUI.view.administration.content.bus.messages.StatusesPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-bus-messages-statusespanel',

    data: {
        tileinfo: {
            draft: 0,
            queued: 0,
            processing: 0,
            processed: 0,
            error: 0,
            failed: 0,
            completed: 0
        },
        tilehtml: {
            draft: 0,
            queued: 0,
            processing: 0,
            processed: 0,
            error: 0,
            failed: 0,
            completed: 0
        }
    },
    formulas: {

        tileredcls: {
            bind: {
                error: '{tileinfo.error}',
                failed: '{tileinfo.failed}'
            },
            get: function (data) {
                return {
                    error: data.error > 0 ? true : false,
                    failed: data.failed > 0 ? true : false
                };
            }
        }

    }
});