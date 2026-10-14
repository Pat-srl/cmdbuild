Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.processes.ProcessesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-groupsandpermissions-tabitems-permissions-tabitems-processes-processes',
    formulas: {
        hierachicalViewHidden: {
            bind: '{actions.view}',
            get: function (isView) {
                return !isView;
            }
        },
        hierarchicalView: {
            bind: '{grantHierarchicalView.processes}',
            get: function (mode) {
                return mode;
            },
            set: function (value) {
                this.get('grantHierarchicalView').processes = value;
            }
        }
    }
});
