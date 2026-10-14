Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.dmsprivilegesgrid.DmsPrivilegesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-dmsprivilegesgrid-dmsprivileges',

    onBeforeCheckChange: function (checkbox, rowIndex, checked, record, e, eOpts) {
        return checked;
    },

    onCheckChange: function (check, rowIndex, checked, record, e, eOpts) {
        var vm = check.lookupViewModel();
        var value = check.dataIndex;
        vm.get('grant.dmsPrivileges')[record.get('name')] = value;
        // trick for fire grid events
        vm.get('grant').set('lastUpdate', new Date().toTimeString());
        record.set('default', value === 'default');
        record.set('none', value === 'none');
        record.set('read', value === 'read');
        record.set('write', value === 'write');
    }
});