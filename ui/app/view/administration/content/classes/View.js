(function () {

    var elementId = 'CMDBuildAdministrationContentClassView';
    Ext.define('CMDBuildUI.view.administration.content.classes.View', {
        extend: 'Ext.container.Container',

        requires: [
            'CMDBuildUI.view.administration.content.classes.ViewController',
            'CMDBuildUI.view.administration.content.classes.ViewModel'
        ],

        alias: 'widget.administration-content-classes-view',
        controller: 'administration-content-classes-view',
        viewModel: {
            type: 'administration-content-classes-view'
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
            xtype: 'administration-content-classes-topbar',
            region: 'north'
        }, {
            xtype: 'administration-content-classes-tabpanel',
            region: 'center',
            hidden: true,
            bind: {
                hidden: '{!theObject._id}'
            }
        }],

        initComponent: function () {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
            var vm = this.getViewModel();
            vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.classes);
            this.callParent(arguments);
        }
    });
})();