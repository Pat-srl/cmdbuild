Ext.define('CMDBuildUI.view.administration.content.tasks.jobruns.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-tasks-jobruns-view',

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
     * @param {CMDBuildUI.view.administration.content.tasks.jobruns.View} view 
     */
    onBeforeRender: function (view) {

        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigation.jobmessages);
    },
    /**
     * 
     * @param {Ect.button.Button} button 
     */
    onRefreshButtonClick: function (button) {
        var view = this.getView(),
            grid = view.down('administration-content-tasks-jobruns-grid');
        view.down('administration-content-tasks-jobruns-statusespanel').fetchData();
        grid.getStore().reload();
    }


});