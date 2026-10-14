Ext.define('CMDBuildUI.view.administration.content.bus.messages.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-bus-messages-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#refreshBtn': {
            click: 'onRefreshButtonClick'
        }
    },

    /**
    * 
    * @param {CMDBuildUI.view.administration.content.bus.messages.View} view 
    */
    onBeforeRender: function (view) {
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigation.busmessages);
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     */
    onRefreshButtonClick: function (button) {
        var view = this.getView(),
            grid = view.down('administration-content-bus-messages-grid');
        view.down('administration-content-bus-messages-statusespanel').fetchData();
        grid.getStore().load();
    }


});