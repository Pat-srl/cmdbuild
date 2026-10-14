Ext.define('CMDBuildUI.store.classes.Cards', {
    extend: 'Ext.data.BufferedStore',
    // extend: 'Ext.data.Store',

    requires: [

    ],

    alias: 'store.classes-cards',

    pageSize: 50,
    leadingBufferZone: 100,
    remoteFilter: true,
    remoteSort: true,

    sum: function (attribute) {
        var deferred = new Ext.Deferred();
        var advancedFilter = this.getAdvancedFilter();

        var query = JSON.stringify({ // as suggested in #3350
            "aggregate": [{
                "attribute": attribute,
                "operation": "sum"
            }]
        });

        Ext.Ajax.request({
            //classes/MyClass/statistics/sum?attributeId=MyAttr&filter=<relationFilter> 
            url: Ext.String.format("{0}/classes/{1}/stats", this.getProxy().getBaseUrl(), this.getModel().objectTypeName),
            method: 'GET',
            params: {
                select: query,
                filter: advancedFilter.encode() //TODO: change when filters would be supported
            },
            success: function (response) {
                var res = JSON.parse(response.responseText);
                var value = res.data.aggregate[0].result;
                deferred.resolve([attribute, value]);
            },
            error: function (response) {
                deferred.resolve([attribute, 0]);
            }
        });

        return deferred.promise;
    }
});