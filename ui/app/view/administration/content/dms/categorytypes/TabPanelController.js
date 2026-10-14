Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dms-dmscategorytypes-tabpanel',

    control: {
        '#': {
            beforerender: "onBeforeRender",
            tabchange: 'onTabChage'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.dms.dmscategorytypes.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {

        var vm = this.getViewModel();
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
        tabPanelHelper.addTab(view, "properties", CMDBuildUI.locales.Locales.administration.common.strings.properties, [{
            xtype: 'administration-content-dms-dmscategorytypes-tabitems-type-properties',
            objectTypeName: vm.get("objectTypeName"),
            objectId: vm.get("objectId"),
            autoScroll: true
        }], 0, { disabled: '{disabledTabs.list}' });

        tabPanelHelper.addTab(view, "attributes", CMDBuildUI.locales.Locales.administration.lookuptypes.strings.values, [{
            xtype: 'administration-content-dms-dmscategorytypes-tabitems-values-values',
            objectTypeName: vm.get("objectTypeName"),
            objectId: vm.get("objectId"),

            parentDMSCategoriesStore: vm.get("parentDMSCategoriesStore")

        }], 1, { disabled: '{disabledTabs.values}' });
        tabPanelHelper.addTab(view, "assignedon", CMDBuildUI.locales.Locales.administration.dmscategories.assignedon, [{
            xtype: 'administration-content-dms-categorytypes-tabitems-assignedon-view',
            objectTypeName: vm.get("objectTypeName"),
            objectId: vm.get("objectId")
        }], 2, { disabled: '{disabledTabs.assignedon}' });
        
    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage('activeTabs.dmscategories', this, view, newtab, oldtab, eOpts);
    }
});