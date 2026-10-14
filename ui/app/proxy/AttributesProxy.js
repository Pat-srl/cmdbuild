Ext.define('CMDBuildUI.proxy.AttributesProxy', {
    extend: "Ext.data.proxy.Rest",
    alias: 'proxy.cmdbuildattributesproxy',
    timeout: CMDBuildUI.util.Config.ajaxTimeout || 15000,
    reader: {
        type: 'json',
        rootProperty: 'data',
        totalProperty: 'meta.total',
        transform: function (response) {
            Ext.Array.each(response.data, function (attribute, index) {
                attribute.metadata = Ext.merge(attribute.metadata || {}, {
                    precision: attribute.precision,
                    scale: attribute.scale,
                    domain: attribute.domain,
                    targetClass: attribute.targetClass,
                    targetType: attribute.targetType,
                    maxLength: attribute.maxLength,
                    editorType: attribute.editorType,
                    filter: attribute.filter,
                    ecqlFilter: attribute.ecqlFilter,
                    values: attribute.values,
                    lookupType: attribute.lookupType
                });
            });
            return response;
        }
    },


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