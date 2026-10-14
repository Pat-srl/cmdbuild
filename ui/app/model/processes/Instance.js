Ext.define('CMDBuildUI.model.processes.Instance', {
    extend: 'CMDBuildUI.model.base.Base',

    mixins: [
        'CMDBuildUI.mixins.model.Lock',
        'CMDBuildUI.mixins.model.Emails'
    ],

    fields: [{
        name: '_type',
        type: 'string'
    }, {
        name: '_tenant',
        type: 'string'
    }, {
        name: '_name',
        type: 'string'
    }, {
        name: '_activity',
        type: 'string',
        critical: true
    }, {
        name: '_flowstatus',
        type: 'integer',
        hidden: true,
        cmdbuildtype: '',
        mapping: 'FlowStatus',
        metadata: {}
    }, {
        name: '_model',
        persist: false
    }],

    isProcessInstance: true,

    /**
     * @return {Numeric|String} Record id. The same value returned by this.getId() function.
     */
    getRecordId: function () {
        return this.getId();
    },
    /**
     * @return {Numeric|String} Record type. The same value returned by this.get("_type") function.
     */
    getRecordType: function () {
        return this.get("_type");
    },

    /**
     * Override load method to add "includeModel" parameter in request.
     *
     * @param {Object} [options] Options to pass to the proxy.
     *
     * @return {Ext.data.operation.Read} The read operation.
     */
    load: function(options) {
        options = Ext.apply(options || {}, {
            params: {
                includeModel: true,
                includeStats: true
            }
        });
        this.callParent([options]);
    },

    /**
     * @return {Object}
     */
    getOverridesFromPermissions: function() {
        var overrides = {};
        if (this.get("_model")) {
            this.get("_model").attributes.forEach(function(attr) {
                overrides[attr._id] = {
                    writable: attr.writable,
                    hidden: attr.hidden
                };
            });
        }
        return overrides;
    }
});
