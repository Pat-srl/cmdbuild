(function () {

    var elementId = 'CMDBuildAdministrationContentProcessView';
    Ext.define('CMDBuildUI.view.administration.content.processes.View', {
        extend: 'Ext.container.Container',

        requires: [
            'CMDBuildUI.view.administration.content.processes.ViewController',
            'CMDBuildUI.view.administration.content.processes.ViewModel'
        ],

        alias: 'widget.administration-content-processes-view',
        controller: 'administration-content-processes-view',
        viewModel: {
            type: 'administration-content-processes-view'
        },
        statics: {
            elementId: elementId
        },
        id: elementId,
        config: {
            objectTypeName: null,
            allowFilter: true,
            showAddButton: true,
            action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
            title: null
        },
        defaults: {
            textAlign: 'left',
            scrollable: true
        },
        layout: 'border',
        style: 'background-color:#fff',
        items: [{
            xtype: 'administration-content-processes-topbar',
            region: 'north'
        }, {
            xtype: 'administration-content-processes-tabpanel',
            region: 'center',
            hidden: true,
            bind: {
                hidden: '{!theProcess._id}'
            }
        }],

        initComponent: function () {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
            var vm = this.getViewModel();           
            vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.processes);
            this.callParent(arguments);
        },
        listeners: {
            afterlayout: function (panel) {
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            }
        }
    });
})();