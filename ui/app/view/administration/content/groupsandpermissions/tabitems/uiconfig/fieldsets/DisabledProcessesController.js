Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.uiconfig.fieldsets.DisabledProcessesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-tabitems-uiconfig-fieldsets-disabledprocesses',

    onBeforeCheckChange: function (checkbox, rowIndex, checked, record, e, eOpts) {
        return checked;
    },

    onCheckChange: function (check, rowIndex, checked, record, e, eOpts) {
        var vm = check.lookupViewModel();
        var name = record.get('name');
        if (check.dataIndex === 'none') {
            vm.set(Ext.String.format('theGroup.{0}_read', name), false);
            vm.set(Ext.String.format('theGroup.{0}_write', name), false);
            record.set('none', true);
            record.set('read', false);
            record.set('write', false);
        } else if (check.dataIndex === 'read') {
            vm.set(Ext.String.format('theGroup.{0}_read', name), true);
            vm.set(Ext.String.format('theGroup.{0}_write', name), false);
            record.set('none', false);
            record.set('read', true);
            record.set('write', false);
        }
    },

    onCheckboxWriteChange: function (checkbox, value) {
        if (value) {
            var vm = checkbox.lookupViewModel(),
                record = vm.get('record'),
                name = record.get('name');
            vm.set(Ext.String.format('theGroup.{0}_read', name), true);
            vm.set(Ext.String.format('theGroup.{0}_write', name), true);
            record.set('none', false);
            record.set('read', false);
            record.set('write', true);
        }
    }

});