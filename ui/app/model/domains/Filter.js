Ext.define('CMDBuildUI.model.domains.Filter', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'domain',
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: 'destination',
        type: 'string'
    }, {
        name: 'destinationDescription',
        type: 'string'
    }, {
        name: 'mode',
        type: 'string',
        serialize: function (v, record) {
            if (record.get("noone")) {
                v = CMDBuildUI.util.helper.FiltersHelper.relationstypes.noone;
            } else if (record.get("any")) {
                v = CMDBuildUI.util.helper.FiltersHelper.relationstypes.any;
            } else if (record.get("oneof")) {
                v = CMDBuildUI.util.helper.FiltersHelper.relationstypes.oneof;
            } else if (record.get("fromfilter")) {
                v = CMDBuildUI.util.helper.FiltersHelper.relationstypes.fromfilter;
            }
            return v;
        }
    }, {
        name: 'noone',
        type: 'boolean',
        calculate: function (record) {
            return record.mode === CMDBuildUI.util.helper.FiltersHelper.relationstypes.noone;
        }
    }, {
        name: 'any',
        type: 'boolean',
        calculate: function (record) {
            return record.mode === CMDBuildUI.util.helper.FiltersHelper.relationstypes.any;
        }
    }, {
        name: 'oneof',
        type: 'boolean',
        calculate: function (record) {
            return record.mode === CMDBuildUI.util.helper.FiltersHelper.relationstypes.oneof;
        }
    }, {
        name: 'fromfilter',
        type: 'boolean',
        calculate: function (record) {
            return record.mode === CMDBuildUI.util.helper.FiltersHelper.relationstypes.fromfilter;
        }
    }, {
        name: 'cards',
        type: 'auto',
        defaultValue: []
    }]
});
