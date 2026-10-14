Ext.define('CMDBuildUI.view.administration.content.tasks.jobruns.StatusesPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-tasks-jobruns-statusespanel',

    data: {
        tileinfo: {
            running: 0,
            completed: 0,
            failed: 0
        }
    },
    formulas: {
        tileredcls: {
            bind: {
                failed: '{tileinfo.failed}'
            },
            get: function (data) {
                return {
                    failed: data.failed > 0 ? true : false
                };
            }
        }

    }
});