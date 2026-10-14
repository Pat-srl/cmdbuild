Ext.define('CMDBuildUI.model.lookups.LookupType', {
    imports: [
        'CMDBuildUI.util.Utilities',
        'CMDBuildUI.util.api.Lookups'
    ],
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        /**
         * Get an instance of Lookut type
         * @param {String} type
         * @return {CMDBuildUI.model.lookups.LookupType}
         */
        getLookupTypeFromName: function (type) {
            return Ext.getStore("lookups.LookupTypes").getById(CMDBuildUI.util.Utilities.stringToHex(type));
        },

        /**
         * Load lookup values for given Lookup Type
         *
         * @param {String} type
         * @param {Boolean} force
         */
        loadLookupValues: function (type, force) {
            var lt = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(type);
            if (lt && (force || (!lt.values().isLoaded() && !lt.values().isLoading()))) {
                lt.values().getProxy().setUrl(CMDBuildUI.util.api.Lookups.getLookupValues(type));
                lt.values().load();
                // load values for parent
                if (!Ext.isEmpty(lt.get("parent"))) {
                    CMDBuildUI.model.lookups.LookupType.loadLookupValues(lt.get("parent"));
                }
            }
        },
        accessTypes: {
            _system: 'system',
            _protected: 'protected',
            _default: 'default'
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
        calculate: function (data) {
            return data.accessType === 'system';
        }
    }, {
        name: 'accessType',
        type: 'string'
    }],

    hasMany: [{
        model: 'CMDBuildUI.model.lookups.Lookup',
        name: 'values'
    }],

    /**
     * Load attributes relation
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     * @return {Ext.Deferred} The promise has as parameters the values store and a boolean field.
     */
    getLookupValues: function (force) {
        var deferred = new Ext.Deferred();
        var values = this.values();
        var lookupTypesName = this.get('name');

        if (!values.isLoaded() || force) {

            values.setProxy({
                type: 'baseproxy',
                url: Ext.String.format(CMDBuildUI.util.api.Lookups.getLookupValues(lookupTypesName))
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
        url: CMDBuildUI.util.api.Lookups.getLookupTypes(),
        type: 'baseproxy'
    }
});