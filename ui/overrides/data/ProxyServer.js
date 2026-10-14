Ext.define('Override.data.ProxyServer', {
    override: 'Ext.data.proxy.Server',

    getTimeout: function() {
        return CMDBuildUI.util.Config.ajaxTimeout || this.timeout;
    }
});