
Ext.define('CMDBuildUI.view.fields.schedulerdate.SequenceContainer', {
    extend: 'Ext.form.Panel',

    alias: 'widget.fields-schedulerdate-sequencecontainer',

    requires: [
        'CMDBuildUI.view.fields.schedulerdate.SequenceContainerController',
        'CMDBuildUI.view.fields.schedulerdate.SequenceContainerModel'
    ],

    controller: 'fields-schedulerdate-sequencecontainer',

    viewModel: {
        type: 'fields-schedulerdate-sequencecontainer'
    },

    autoScroll: true,
    ui: 'managementlighttabpanel',

    fbar: [{
        xtype: 'button',
        itemId: 'cancelbutton',
        ui: 'secondary-action-small',
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        }
    }, {
        xtype: 'button',
        itemId: 'savebutton',
        text: CMDBuildUI.locales.Locales.common.actions.save,
        ui: 'management-primary-small',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        }
    }],

    items: []
});
