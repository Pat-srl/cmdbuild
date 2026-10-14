Ext.define('CMDBuildUI.view.administration.content.gis.gismenus.ViewModel', {
    extend: 'Ext.app.ViewModel',
    requires: [
        'CMDBuildUI.util.api.Groups',
        'CMDBuildUI.util.MenuStoreBuilder'
    ],
    alias: 'viewmodel.administration-content-gismenus-view',

    data: {
        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
        actions: {
            edit: false,
            add: false,
            view: true
        },        
        newFolderName: '',
        canAddNewFolder: false,
        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },

    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_menus_modify && theSession.rolePrivileges.admin_gis_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },

        titleManager: {           
            get: function () {                
                this.getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.gismenu);
            }
        }

    },    

    setCurrentAction: function (action) {
        this.set('actions.edit', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        this.set('actions.add', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
        this.set('actions.view', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        this.set('action', action);
    },

    setExistingMenus: function (value) {
        this.set('existingMenu', value);
    }
});