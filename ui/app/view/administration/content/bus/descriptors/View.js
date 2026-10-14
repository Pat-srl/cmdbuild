Ext.define('CMDBuildUI.view.administration.content.bus.descriptors.View', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.bus.descriptors.ViewController',
        'CMDBuildUI.view.administration.content.bus.descriptors.ViewModel',
        'CMDBuildUI.view.administration.content.bus.descriptors.TabPanel'
    ],

    alias: 'widget.administration-content-bus-descriptors-view',
    controller: 'administration-content-bus-descriptors-view',
    viewModel: {
        type: 'administration-content-bus-descriptors-view'
    },

    config: {
        objectTypeName: null,
        showAddButton: true,
        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
        title: null
    },
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    layout: 'border',
    items: [{
        xtype: 'administration-content-bus-descriptors-tabpanel',
        region: 'center',
        hidden: true,
        bind: {
            hidden: '{hideForm}'
        }
    }],
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.administration.bus.addbusdescriptor,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.bus.addbusdescriptor'
            },
            ui: 'administration-action-small',
            itemId: 'addbtn',
            autoEl: {
                'data-testid': 'administration-content-bus-descriptors-addBusDescriptorBtn'
            },
            bind: {
                disabled: '{!toolAction._canAdd}'
            }
        }, {
            xtype: 'admin-globalsearchfield',
            objectType: 'busdescriptors'
        }, {
            xtype: 'tbfill'
        }, {
            xtype: 'tbtext',
            dock: 'right',
            hidden: true,
            bind: {
                hidden: '{!theDescriptor.description}',
                html: '{typeLabel}: <b data-testid="administration-content-bus-descriptors-typeName">{theDescriptor.description}</b>'
            }
        }]
    }],

    initComponent: function () {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.busdescriptors);
        this.callParent(arguments);
    }
});