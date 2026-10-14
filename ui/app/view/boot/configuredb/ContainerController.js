Ext.define('CMDBuildUI.view.boot.configuredb.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.boot-configuredb-container',

    control: {
        'main-header-logodark': {
            afterrenderer: 'onMainHeaderLogoDarkBeforeRenderer'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.main.header.LogoDark} view
     * @param {Object} eOpts
     */
    onMainHeaderLogoDarkBeforeRenderer: function (view, eOpts) {
        view.center();
    }
});
