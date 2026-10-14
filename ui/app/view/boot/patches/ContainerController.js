Ext.define('CMDBuildUI.view.boot.patches.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bootpatches-container',

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
