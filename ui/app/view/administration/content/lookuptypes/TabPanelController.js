Ext.define('CMDBuildUI.view.administration.content.lookuptypes.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-lookuptypes-tabpanel',

    control: {
        '#': {
            beforerender: "onBeforeRender",
            tabchange: 'onTabChange'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.lookuptypes.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.addTab(view, "properties", CMDBuildUI.locales.Locales.administration.lookuptypes.strings.lookuplist, [{
            xtype: 'administration-content-lookuptypes-tabitems-type-properties'
        }], 0, { disabled: '{disabledTabs.list}' });

        CMDBuildUI.util.administration.helper.TabPanelHelper.addTab(view, "attributes", CMDBuildUI.locales.Locales.administration.lookuptypes.strings.values, [{
            xtype: 'administration-content-lookuptypes-tabitems-values-grid-grid'
        }], 1, { disabled: '{disabledTabs.values}' });
    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChange: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage('activeTabs.lookuptypes', this, view, newtab, oldtab, eOpts);
    }
});