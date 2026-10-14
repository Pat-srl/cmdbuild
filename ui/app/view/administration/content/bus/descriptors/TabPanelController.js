Ext.define('CMDBuildUI.view.administration.content.bus.descriptors.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-bus-descriptors-tabpanel',
    requires: [
        'CMDBuildUI.view.administration.content.bus.descriptors.tabitems.properties.View',
        'CMDBuildUI.util.administration.helper.TabPanelHelper'
    ],

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChage'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.bus.descriptors.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
        tabPanelHelper.addTab(
            view,
            'properties',
            CMDBuildUI.locales.Locales.administration.common.strings.properties,
            [
                {
                    xtype: 'administration-content-bus-descriptors-tabitems-properties-view',
                    autoScroll: true
                }
            ],
            0,
            {
                disabled: '{disabledTabs.properties}'
            }
        );

        vm.set('activeTab', vm.get('activeTabs.busdescriptors'));
    },

    /**
     * @param {CMDBuildUI.view.administration.content.bus.descriptors.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage(
            'activeTabs.busdescriptors',
            this,
            view,
            newtab,
            oldtab,
            eOpts
        );
    }
});
