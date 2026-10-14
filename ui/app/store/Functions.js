Ext.define('CMDBuildUI.store.Functions', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base',
        'CMDBuildUI.model.Function'
    ],

    alias: 'store.functions',

    model: 'CMDBuildUI.model.Function',

    sorters: ['description'],
    pageSize: 0, // disable pagination
    autoLoad: false,

    /**
     * Load single function into store.
     * @param {String} fn_name 
     * @return {Ext.Deferred}
     */
    loadSingleFunction: function (fn_name) {
        var deferred = new Ext.Deferred();
        var me = this;
        this.getModel().load(fn_name, {
            callback: function (record, operation, success) {
                if (record) {
                    me.add(record);
                }
                deferred.resolve(record, success);
            }
        });
        return deferred;
    },

    /**
     * 
     * @param {String} fnName 
     * @return {Ext.Deferred}
     */
    getFunctionByName: function(fnName) {
        var deferred = new Ext.Deferred();
        
        var fn = this.findRecord("name", fnName);
        // load single function if not already loaded
        if (!fn) {
            isloadingfn = true;
            this.loadSingleFunction(fnName).then(function(fn) {
                deferred.resolve(fn);
            }, function () {
                deferred.reject();
            });
        } else {
            deferred.resolve(fn);
        }
        return deferred.promise;
    }

});