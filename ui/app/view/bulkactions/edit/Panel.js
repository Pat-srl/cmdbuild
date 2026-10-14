Ext.define('CMDBuildUI.view.bulkactions.edit.Panel', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.bulkactions.edit.PanelController',
        'CMDBuildUI.view.bulkactions.edit.PanelModel'
    ],

    alias: 'widget.bulkactions-edit-panel',
    controller: 'bulkactions-edit-panel',
    viewModel: {
        type: 'bulkactions-edit-panel'
    },

    layout: 'border',

    config: {
        objectType: null,
        objectTypeName: null
    },

    items: [{
        xtype: 'panel',
        region: 'north',
        layout: 'fit',
        cls: 'panel-with-gray-background',
        bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding,
        items: [{
            xtype: 'fieldcontainer',
            layout: 'hbox',
            fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
            items: [{
                xtype: 'groupedcombo',
                itemId: 'attributescombo',
                displayField: '_description_translation',
                flex: 1,
                valueField: 'name',
                queryMode: 'local',
                forceSelection: true,
                fieldLabel: CMDBuildUI.locales.Locales.filters.attribute,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.filters.attribute'
                },
                bind: {
                    store: '{attributes}'
                }
            }, {
                xtype: 'component',
                flex: 1
            }]
        }]
    }, {
        region: 'center',
        xtype: 'fieldcontainer',
        scrollable: true,
        reference: 'bulkeditform',
        itemId: 'bulkeditform',
        padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
        fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
        items: [{
            xtype: 'formvalidatorfield',
            itemId: 'validationField',
            showActionButton: true
        }]
    }],

    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        itemId: 'cancelbtn',
        ui: 'secondary-action-small',
        autoEl: {
            'data-testid': 'bulkactions-edit-cancel'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.save,
        formBind: true, //only enabled once the form is valid
        disabled: true,
        itemId: 'savebtn',
        ui: 'management-primary-small',
        autoEl: {
            'data-testid': 'bulkactions-edit-save'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        }
    }]
});