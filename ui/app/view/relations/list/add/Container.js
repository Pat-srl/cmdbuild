Ext.define('CMDBuildUI.view.relations.list.add.Container', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.relations.list.add.ContainerController',
        'CMDBuildUI.view.relations.list.add.ContainerModel'
    ],

    alias: 'widget.relations-list-add-container',
    controller: 'relations-list-add-container',
    viewModel: {
        type: 'relations-list-add-container'
    },

    layout: 'fit',

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

    bind: {
        originId: '{originId}'
    },
    twoWayBindable: ['originId'],

    fbar: [{
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        ui: 'secondary-action',
        itemId: 'cancelbutton',
        autoEl: {
            'data-testid': 'relations-list-add-container-cancel'
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
            'data-testid': 'relations-list-add-container-save'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        },
        bind: {
            disabled: '{!selection || !valid.attrs}'
        }
    }],

    onSaveSuccess: Ext.emptyFn
});
