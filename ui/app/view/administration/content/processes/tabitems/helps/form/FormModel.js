Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.helps.form.FormModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-processes-tabitems-helps-form-form',
    data: {
        actions: {
            view: true,
            edit: false,
            add: false
        }
    },
    formulas: {
        titleManager: function () {            
            if (this.getParent() && this.getParent().type === 'administration-detailswindow') {
                this.getParent().set('title', this.get('title'));
            }
        },

        action: {
            bind: {
                isView: '{actions.view}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}'
            },
            get: function (data) {
                if (data.isView) {
                    this.set('formModeCls', 'formmode-view');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                } else if (data.isEdit) {
                    this.set('formModeCls', 'formmode-edit');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.isAdd) {
                    this.set('formModeCls', 'formmode-add');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        }
    },
    
    stores: {       
        localizationStore: {
            model: 'CMDBuildUI.model.Translation',
            proxy:{
                type: 'baseproxy',
                url: '/translations'
            },
            autoload: false,
            autoDestroy: true
        }
    }

});