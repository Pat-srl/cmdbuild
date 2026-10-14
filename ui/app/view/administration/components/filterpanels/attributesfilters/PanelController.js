Ext.define('CMDBuildUI.view.administration.components.filterpanels.attributesfiltrs.PanelController', {
    extend: 'CMDBuildUI.view.filters.attributes.PanelController',
    alias: 'controller.administration-components-filterpanels-attributes-panel',
    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.filters.attributes.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        view.setTitle(null);
        this.callParent(arguments);
    },

    /**
     *
     * @param {CMDBuildUI.view.filters.attributes.Panel} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        view.down('#attributecombo').forceSelection = view.getAllowArbitraryAttributeName();
    }

});