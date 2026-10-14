Ext.define('CMDBuildUI.view.thematisms.LauncherModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.thematisms-launcher',
    data: {
        appliedthematism: {
            id: null,
            description: null
        },
        item: null
    },

    formulas: {
        showClearBtn: {
            bind: {
                thematismid: '{appliedthematism.id}'
            },
            get: function (data) {
                return !Ext.isEmpty(data.thematismid);
            }
        }
    }

});
