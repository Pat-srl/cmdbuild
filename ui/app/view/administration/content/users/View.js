(function () {
    var elementId = 'CMDBuildAdministrationContentUsersView';
    Ext.define('CMDBuildUI.view.administration.content.users.View', {
        extend: 'Ext.panel.Panel',

        alias: 'widget.administration-content-users-view',

        requires: [
            'CMDBuildUI.view.administration.content.users.ViewController',
            'CMDBuildUI.view.administration.content.users.ViewModel'
        ],

        controller: 'administration-content-users-view',
        viewModel: {
            type: 'administration-content-users-view'
        },
        id: elementId,
        statics: {
            elementId: elementId
        },
        loadMask: true,
        defaults: {
            textAlign: 'left',
            scrollable: true
        },
        layout: 'border',
        items: [{
            xtype: 'administration-content-users-grid',
            region: 'center',
            bind: {
                hidden: '{isGridHidden}'
            }
        }],

        listeners: {
            afterlayout: function (panel) {
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            }
        },

        initComponent: function () {
            var vm = this.getViewModel();
            vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.users);
            this.callParent(arguments);
        }
    });
})();