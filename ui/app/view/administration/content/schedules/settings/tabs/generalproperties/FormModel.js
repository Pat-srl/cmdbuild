Ext.define('CMDBuildUI.view.administration.content.schedules.settings.tabs.generalproperties.FormModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-schedules-settings-tabs-generalproperties-form',
    data: {    
        enabled: null,    
        actions: {
            add: false,
            view: true,
            edit: false
        }
    },
    formulas: {        
        action: {
            bind: {
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}',
                isView: '{actions.view}'
            },
            get: function (data) {                
                if (data.isEdit) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.isAdd) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {                
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        }
    }

});