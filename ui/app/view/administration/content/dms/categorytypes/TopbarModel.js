Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.TopbarModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dms-dmscategorytypes-topbar',
    data: {
        toolAction: {
            _canAdd: false
        }
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_dms_modify}'             
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
            }
        }
    }
});