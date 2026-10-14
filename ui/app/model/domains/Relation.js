Ext.define('CMDBuildUI.model.domains.Relation', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: '_type',
        type: 'string',
        mapping: 'type',
        critical: true
    }, {
        name: '_destinationId',
        type: 'auto',
        mapping: 'destinationId',
        critical: true
    }, {
        name: '_destinationType',
        type: 'string',
        mapping: 'destinationType',
        critical: true
    }, {
        name: '_destinationIsProcess',
        type: 'boolean',
        persist: false,
        calculate: function (data) {
            return CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(data._destinationType) === CMDBuildUI.util.helper.ModelHelper.objecttypes.process;
        }
    }, {
        name: '_destinationDescription',
        type: 'string',
        mapping: 'destinationDescription'
    }, {
        name: '_destinationCode',
        type: 'string',
        mapping: 'destinationCode'
    }, {
        name: '_sourceId',
        type: 'integer',
        mapping: 'sourceId',
        critical: true
    }, {
        name: '_sourceType',
        type: 'string',
        mapping: 'sourceType',
        critical: true
    }, {
        name: '_is_direct',
        type: 'boolean',
        mapping: 'is_direct',
        critical: true

    }, {
        name: '_beginDate',
        type: 'date',
        critical: false,
        persist: false
    }, {
        name: '_relationAttributes',
        type: 'string',
        persist: false,
        calculate: function (data) {
            var attrs = {};
            for (var k in data) {
                if (!Ext.String.startsWith(k, "_")) {
                    if (data["_" + k + "_description_translation"]) {
                        attrs[k] = data["_" + k + "_description_translation"];
                    } else if (data["_" + k + "_description"]) {
                        attrs[k] = data["_" + k + "_description"];
                    } else if (data["_" + k + "_details"]) {
                        attrs[k] = Ext.Array.pluck(data["_" + k + "_details"], "_description_translation").join(", ");
                    } else {
                        attrs[k] = Ext.isEmpty(data[k]) ? null : data[k];
                    }
                } else if (k.includes("has_value") && !data.hasOwnProperty(k.split("_")[1])) {
                    if (data["_" + k.split("_")[1] + "_has_value"]) {
                        attrs[k.split("_")[1]] = true;
                    }
                }
            }
            return attrs;
        }
    }, {
        name: '_relationAttributesDesc',
        type: 'string',
        persist: false
    }],

    /**
     * This field is not returned by the servers but used for internal purpose
     */
    hasMany: [{
        name: 'nodes',
        model: 'CMDBuildUI.model.domains.Relation'
    }],
    /**
     * @property {proxy.type} For URL generation
     */
    proxy: {
        type: 'baseproxy'
    },
    /**
     * @return {Numeric|String} Record id. The same value returned by this.get("destinationId") function.
     */
    getRecordId: function () {
        return this.get("destinationId");
    },
    /**
     * @return {Numeric|String} Record type. The same value returned by this.get("destinationType") function.
     */
    getRecordType: function () {
        return this.get("destinationType");
    }
});
