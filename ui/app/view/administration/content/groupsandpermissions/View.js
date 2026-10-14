(function () {

    var elementId = 'CMDBuildAdministrationContentGroupView';
    Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.View', {
        extend: 'Ext.container.Container',

        requires: [
            'CMDBuildUI.view.administration.content.groupsandpermissions.ViewController',
            'CMDBuildUI.view.administration.content.groupsandpermissions.ViewModel'
        ],
        bind: {

        },
        alias: 'widget.administration-content-groupsandpermissions-view',
        controller: 'administration-content-groupsandpermissions-view',
        viewModel: {
            type: 'administration-content-groupsandpermissions-view'
        },
        statics: {
            elementId: elementId
        },

        id: elementId,
        config: {
            objectTypeName: null,
            allowFilter: true,
            showAddButton: true,
            action: null,
            title: null
        },
        defaults: {
            textAlign: 'left',
            scrollable: true
        },
        layout: 'border',
        style: 'background-color:#fff',
        items: [{
            xtype: 'administration-content-groupsandpermissions-topbar',
            region: 'north'
        }, {
            xtype: 'administration-content-groupsandpermissions-tabpanel',
            region: 'center',
            viewModel: {},
            bind: {
                hidden: '{isFormHidden}'
            }
        }],

        listeners: {
            afterlayout: function (panel) {
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            }
        },
        initComponent: function () {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
            var vm = this.getViewModel();
            vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.groupsandpermissions);
            this.callParent(arguments);
        }
    });
})();