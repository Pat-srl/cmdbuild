Ext.define('CMDBuildUI.view.dashboards.homepage.ContainerController', {
    extend: 'CMDBuildUI.view.dashboards.common.ContainerControllerBase',
    alias: 'controller.homepage-container',

    onBeforeRender: function (view, eOpts) {
        const me = this;

        const currentHomepage = CMDBuildUI.util.Stores.currentHomepage;
        if (currentHomepage) {
            view.lookupViewModel().set('object', currentHomepage);
        }

        me.callParent(arguments);
    }
});
