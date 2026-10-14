Ext.define('CMDBuildUI.view.administration.content.dms.models.View', {
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.administration.content.dms.models.ViewController',
        'CMDBuildUI.view.administration.content.dms.models.ViewModel'
    ],

    alias: 'widget.administration-content-dms-models-view',
    controller: 'administration-content-dms-models-view',
    viewModel: {
        type: 'administration-content-dms-models-view'
    },


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
        xtype: 'administration-content-dms-models-topbar',
        region: 'north'
    }, {
        xtype: 'administration-content-dms-models-tabpanel',
        region: 'center',
        hidden: true,
        bind: {
            hidden: '{actions.empty}'
        }
    }],

    initComponent: function () {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var vm = this.getViewModel();
        vm.getParent().set('title', 'DMS model');
        this.callParent(arguments);
    }
});