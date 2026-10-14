Ext.define('CMDBuildUI.view.administration.home.widgets.spaceusage.TablesGridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-home-widgets-spaceusage-tablesgrid',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.administration.home.widgets.spaceusage.TablesGrid} view 
     */
    onAfterRender: function (view) {
        var vm = this.getViewModel();
        vm.bind({
            bindTo: '{showLoader}'
        }, function (showLoader) {
            CMDBuildUI.util.Utilities.showLoader(showLoader, view);
        });
    }


});