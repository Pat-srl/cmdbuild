Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.tabsconfiggrid.TabsConfigController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-tabsconfiggrid-tabsconfig',

    onBeforeCheckChange: function (checkbox, rowIndex, checked, record, e, eOpts) {
        return checked;
    },

    onCheckChange: function (check, rowIndex, checked, record, e, eOpts) {
        var vm = check.lookupViewModel();
        var value = check.dataIndex === 'write' ? 'true' : check.dataIndex === 'read' ? 'false' : '';
        var name = record.get('name');
        switch (check.dataIndex) {
            case 'none':
                vm.set(Ext.String.format('grant.{0}_read', name), 'false');
                vm.set(Ext.String.format('grant.{0}_write', name), 'false');
                record.set('none', true);
                record.set('read', false);
                record.set('write', false);
                record.set('default', false);
                break;
            case 'read':
                vm.set(Ext.String.format('grant.{0}_read', name), 'true');
                vm.set(Ext.String.format('grant.{0}_write', name), 'false');
                record.set('none', false);
                record.set('read', true);
                record.set('write', false);
                record.set('default', false);
                break;
            case 'write':
                vm.set(Ext.String.format('grant.{0}_read', name), 'true');
                vm.set(Ext.String.format('grant.{0}_write', name), 'true');
                record.set('none', false);
                record.set('read', false);
                record.set('write', true);
                record.set('default', false);
                break;
            default:
                vm.set(Ext.String.format('grant.{0}_read', name), undefined);
                vm.set(Ext.String.format('grant.{0}_write', name), undefined);
                record.set('none', false);
                record.set('read', false);
                record.set('write', false);
                record.set('default', true);
                break;
        }
    }

});