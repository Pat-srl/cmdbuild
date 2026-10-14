
Ext.define('CMDBuildUI.view.bim.xeokit.tab.Properties', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.bim.xeokit.tab.PropertiesController',
        'CMDBuildUI.view.bim.xeokit.tab.PropertiesModel'
    ],

    mixins: [
        'CMDBuildUI.view.bim.xeokit.Mixin'
    ],

    alias: 'widget.bim-xeokit-tab-properties',
    controller: 'bim-xeokit-tab-properties',
    viewModel: {
        type: 'bim-xeokit-tab-properties'
    },

    scrollable: true,

    config: {
        entity: undefined
    },

    /**
     * Set the entity object of view
     * @param {Object} value 
     */
    setEntity: function (value) {
        var vm = this.lookupViewModel();
        if (value) {
            if (value !== vm.get("entity")) {
                vm.set("entity", value);
            }
            vm.set("enabledTabs.properties", true);
        } else {
            vm.set("enabledTabs.properties", false);
        }
        this.callParent(arguments);
    }

});