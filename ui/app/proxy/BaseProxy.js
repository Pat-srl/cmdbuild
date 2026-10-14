Ext.define('CMDBuildUI.proxy.BaseProxy', {
    extend: "Ext.data.proxy.Rest",
    alias: 'proxy.baseproxy',

    statics: {
        filter: {
            query: '_query',
            ecql: '_ecql'
        }
    },
    timeout: CMDBuildUI.util.Config.ajaxTimeout || 15000,
    reader: {
        type: 'json',
        rootProperty: 'data',
        metaProperty: 'meta',
        totalProperty: 'meta.total'
    },
    withCredentials: true,

    getBaseUrl: function () {
        return CMDBuildUI.util.Config.baseUrl;
    },

    // @override
    getUrl: function () {
        var url = this.callParent(arguments);
        if (url && url.indexOf("http://") === -1 && url.indexOf("https://") === -1) {
            // initialize url
            url = this.getBaseUrl() + url;
        }
        return url;
    }
});