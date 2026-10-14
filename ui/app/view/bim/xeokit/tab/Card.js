
Ext.define('CMDBuildUI.view.bim.xeokit.tab.Card', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.bim.xeokit.tab.CardController',
        'CMDBuildUI.view.bim.xeokit.tab.CardModel'
    ],

    mixins: [
        'CMDBuildUI.view.bim.xeokit.Mixin'
    ],

    alias: 'widget.bim-xeokit-tab-card',
    controller: 'bim-xeokit-tab-card',
    viewModel: {
        type: 'bim-xeokit-tab-card'
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
        var vm = this.lookupViewModel(),
            vmContainer = this.getContainer().getViewModel();

        if (value) {
            if (value !== vm.get("entity")) {
                vm.set("entity", value);
            } else {
                vmContainer.set("enabledTabs.card", value.mappingInfo.exists);
            }
        } else {
            vmContainer.set("enabledTabs.card", false);
        }
        this.callParent(arguments);
    }

});