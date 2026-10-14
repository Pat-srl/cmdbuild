Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.classes.ClassesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-groupsandpermissions-tabitems-permissions-tabitems-classes-classes',    
    formulas: {
        hierachicalViewHidden: {
            bind: '{actions.view}',
            get: function (isView) {
                return !isView;
            }
        },

        hierarchicalView: {
            bind: '{grantHierarchicalView.classes}',
            get: function (mode) {
                return mode;
            },
            set: function (value) {
                // debugger;
                this.get('grantHierarchicalView').classes = value;
            }
        }
    }
});