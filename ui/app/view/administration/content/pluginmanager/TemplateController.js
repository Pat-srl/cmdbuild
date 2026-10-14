Ext.define('CMDBuildUI.view.administration.content.pluginmanager.TemplateController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-pluginmanager-template',

    control: {
        '#': {
            itemclick: 'onItemClick'
        }
    },

    onItemClick: function (view, record, item, index, e, eOpts) {
        const pluginURL = CMDBuildUI.util.administration.helper.ApiHelper.client.getPluginManagerUrl(
            record.get('name')
        );
        CMDBuildUI.util.Utilities.redirectTo(pluginURL);
    }
});
