
Ext.define('CMDBuildUI.view.filters.Panel', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.filters.PanelController',
        'CMDBuildUI.view.filters.PanelModel'
    ],

    alias: 'widget.filters-panel',
    controller: 'filters-panel',
    viewModel: {
        type: 'filters-panel'
    },

    config: {
        /**
         * @cfg {Boolean} showAttributesPanel
         */
        showAttributesPanel: true,

        /**
         * @cfg {Boolean} allowInputParameterForAttributes
         */
        allowInputParameterForAttributes: true,

        /**
         * @cfg {Boolean} showRelationsPanel
         */
        showRelationsPanel: true,

        /**
         * @cfg {Boolean} showAttachmentsPanel
         */
        showAttachmentsPanel: true,

        /**
         * @cfg {Boolean} isDms
         */
        isDms: false
    },

    header: false,

    ui: 'managementlighttabpanel',
    deferredRender: false,
    fbar: [{
        xtype: 'button',
        itemId: 'cancelbutton',
        ui: 'secondary-action-small',
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        },
        autoEl: {
            'data-testid': 'filters-panel-cancelbutton'
        }
    }, {
        xtype: 'button',
        itemId: 'savebutton',
        text: CMDBuildUI.locales.Locales.common.actions.saveandapply,
        ui: 'management-primary-outline-small',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.saveandapply'
        },
        autoEl: {
            'data-testid': 'filters-panel-savebutton'
        },
        hidden: true
    }, {
        xtype: 'button',
        itemId: 'applybutton',
        text: CMDBuildUI.locales.Locales.common.actions.apply,
        ui: 'management-primary-small',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.apply'
        },
        autoEl: {
            'data-testid': 'filters-panel-applybutton'
        }
    }]
});
