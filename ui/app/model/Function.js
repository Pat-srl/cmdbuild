Ext.define('CMDBuildUI.model.Function', {
    extend: 'CMDBuildUI.model.base.Base',

    mixins: [
        'CMDBuildUI.mixins.model.Attribute'
    ],

    fields: [{
        name: 'name',
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }],

    idProperty: 'name',

    hasMany: [{
        model: 'CMDBuildUI.model.Attribute',
        name: 'attributes'
    }, {
        model: 'CMDBuildUI.model.Attribute',
        name: 'parameters',
        associationKey: 'parameters'
    }],

    proxy: {
        type: 'baseproxy',
        url: '/functions/',
        extraParams: {
            detailed: true
        }
    },

    /**
     * @return {String} domains url 
     */
    getAttributesUrl: function () {
        return CMDBuildUI.util.api.Functions.getAttributesUrlByFunctionName(this.get('name'));
    },

    /**
     * Load function input parameters
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     * @return {Ext.Deferred} The promise has as parameters the parameters store and a boolean field.
     */
    getParameters: function (force) {
        var deferred = new Ext.Deferred();
        var parameters = this.parameters();
        var functionName = this.get('name');

        if (!parameters.isLoaded() || force) {
            parameters.setProxy({
                type: 'baseproxy',
                url: CMDBuildUI.util.api.Functions.getParametersUrlByFunctionName(functionName)
            });

            parameters.load({
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve(parameters, true);
                    } else {
                        deferred.reject(operation);
                    }
                }
            });
        } else {
            // return promise
            deferred.resolve(parameters, false);
        }
        return deferred.promise;
    },

    /**
     * 
     * @param {Object} params 
     * @param {Object} model 
     * @return {Ext.Deferred}
     */
    getOutputs: function (params, model) {
        var deferred = new Ext.Deferred(),
            paramRequest = {};
        // encode params
        if (!Ext.isEmpty(params)) {
            if (Ext.isObject(params) && !Ext.Object.isEmpty(params)) {
                paramRequest["parameters"] = Ext.JSON.encode(params);
            } else if (Ext.isString(params)) {
                paramRequest["parameters"] = params;
            }
        }
        // encode model
        if (!Ext.isEmpty(model)) {
            if (Ext.isObject(model) && !Ext.Object.isEmpty(model) && !Ext.isEmpty(model.output)) {
                paramRequest["model"] = Ext.JSON.encode(model);
            } else if (Ext.isString(model)) {
                paramRequest["model"] = model;
            }
        }

        // load function results
        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Functions.getOutputsUrlByFunctionName(this.get("name")),
            method: "GET",
            params: paramRequest
        }).then(function (response, opts) {
            var responseJson = Ext.JSON.decode(response.responseText, true);
            deferred.resolve(responseJson.data, responseJson.meta);
        }, function () {
            deferred.reject();
        });
        return deferred.promise;
    }
});