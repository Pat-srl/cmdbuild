Ext.define('CMDBuildUI.view.administration.content.menus.TopbarModel', {
    extend: 'Ext.app.ViewModel',
   
    alias: 'viewmodel.administration-content-menus-topbar',

    data: {
        toolAction: {
            _canAdd: false
        }
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_menus_modify}'
            },
            get: function (data) {                           
                this.set('toolAction._canAdd', data.canModify === true);               
            }
        }
    }

});