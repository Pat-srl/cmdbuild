Ext.define('CMDBuildUI.view.dashboards.parameters.Parameters', {
    extend: 'Ext.panel.Panel',

    requires: ['CMDBuildUI.view.dashboards.parameters.ParametersController', 'CMDBuildUI.view.dashboards.Mixin'],

    alias: 'widget.dashboards-parameters-parameters',
    controller: 'dashboards-parameters-parameters',

    ui: 'managementdashboard',

    title: CMDBuildUI.locales.Locales.dashboards.filters,
    localized: {
        title: 'CMDBuildUI.locales.Locales.dashboards.filters'
    },

    config: {
        /**
         * @cfg {Array} parameters
         */
        parameters: []
    },

    collapsible: true,
    margin: '15 0 0 15',

    tools: []
});
