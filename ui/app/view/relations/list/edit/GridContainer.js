Ext.define('CMDBuildUI.view.relations.list.edit.GridContainer', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.relations.list.edit.GridContainerController',
        'CMDBuildUI.view.relations.list.edit.GridContainerModel'
    ],

    alias: 'widget.relations-list-edit-gridcontainer',
    controller: 'relations-list-edit-gridcontainer',
    viewModel: {
        type: 'relations-list-edit-gridcontainer'
    },

    layout: 'border',

    config: {
        /**
         * @cfg {String} originTypeName
         */
        originTypeName: null,

        /**
         * @cfg {Numeric} originId
         */
        originId: null,

        /**
         * @cfg {Boolean} multiSelect
         */
        multiSelect: false
    },

    fbar: [{
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        ui: 'secondary-action',
        itemId: 'cancelbutton',
        autoEl: {
            'data-testid': 'relations-list-edit-gridcontainer-cancel'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.save,
        disabled: true,
        itemId: 'savebutton',
        ui: 'management-primary',
        autoEl: {
            'data-testid': 'relations-list-edit-gridcontainer-save'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        },
        bind: {
            disabled: '{disableSaveButton}'
        }
    }],

    items: [{
        xtype: 'panel',
        region: 'center',
        layout: 'card',
        flex: 1,
        reference: 'gridcontainer'
    }, {
        xtype: 'form',
        flex: 0.4,
        layout: 'column',
        region: 'south',
        scrollable: true,
        resizable: true,
        reference: 'attributesform',
        hidden: true,
        fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
        defaults: {
            xtype: 'fieldcontainer',
            columnWidth: 0.5,
            flex: '0.5',
            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
            layout: 'anchor'
        }
    }],

    onSaveSuccess: Ext.emptyFn
});
