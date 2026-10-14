Ext.define('CMDBuildUI.model.dms.DMSCategoryType', {
    imports: [
        'CMDBuildUI.util.Utilities',
        'CMDBuildUI.util.api.DMS'
    ],
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        /**
         * Get an instance of Category type
         * @param {String} type
         * @return {CMDBuildUI.model.dms.DMSCategoryType}
         */
        getCategoryTypeFromName: function (type) {
            return Ext.getStore("dms.DMSCategoryTypes").getById(CMDBuildUI.util.Utilities.stringToHex(type));
        },

        /**
         * Load category values for given Category Type
         * @param {String} type 
         */
        loadCategoryValues: function (type) {
            var lt = CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(type);
            if (lt) {
                lt.values().getProxy().setUrl(CMDBuildUI.util.api.DMS.getCategoryValues(type));
                lt.values().load();
                // load values for parent
                if (!Ext.isEmpty(lt.get("parent"))) {
                    CMDBuildUI.model.dms.DMSCategoryType.loadCategoryValues(lt.get("parent"));
                }
            }
        },

        checkCount: {
            no_check: 'no_check',
            at_least_number: 'at_least_number',
            exactly_number: 'exactly_number'
        }
    },

    fields: [{
        name: '_id',
        type: 'string',
        persist: true,
        critical: true,
        convert: function (data) {
            return CMDBuildUI.util.Utilities.stringToHex(data);
        }
    }, {
        name: 'name',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'description',
        type: 'string',
        calculate: function (data) {
            return data.name;
        }
    }, {
        name: 'parent',
        type: 'string',
        defaultValue: null
    }, {
        name: '_is_system',
        type: 'boolean',
        calculate: function(data){
            return data.accessType === 'system';
        }
    }, {
        name: 'accessType',
        type: 'string'        
    }],

    hasMany: [{
        model: 'CMDBuildUI.model.dms.DMSCategory',
        name: 'values'
    }],

    /**
     * Load attributes relation
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     * @return {Ext.Deferred} The promise has as parameters the values store and a boolean field.
     */
    getCategoryValues: function (force) {
        var deferred = new Ext.Deferred();
        var values = this.values();
        var lookupTypesName = this.get('name');

        if (!values.isLoaded() || force) {

            values.setProxy({
                type: 'baseproxy',
                url: Ext.String.format(CMDBuildUI.util.api.DMS.getCategoryValues(lookupTypesName))
            });

            values.load({
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve(values, true);
                    } else {
                        deferred.reject();
                    }
                }
            });
        } else {
            // return promise
            deferred.resolve(values, false);
        }
        return deferred.promise;

    },

    proxy: {
        url: CMDBuildUI.util.api.DMS.getCategoryTypes(),
        type: 'baseproxy'
    }
});