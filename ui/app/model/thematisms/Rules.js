Ext.define('CMDBuildUI.model.thematisms.Rules', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        default: {
            attribute: '_defaultattribute'
        }
    },

    fields: [{
        name: 'attribute',
        type: 'auto',
        mapping: function (data) {
            try {
                return data.condition.attribute.simple.attribute;
            } catch (e) {
                return CMDBuildUI.model.thematisms.Rules.default.attribute;
            }
        }
    }, {
        name: 'operator',
        type: 'string',
        mapping: function (data) {
            try {
                return data.condition.attribute.simple.operator;
            } catch (e) {
                return null
            }
        }
    }, {
        name: 'value',
        type: 'auto',
        mapping: function (data) {
            try {
                return data.condition.attribute.simple.value;
            } catch (e) {
                return null
            }

        }
    }, {
        name: 'style',
        type: 'auto',
        mapping: function (data) {
            return data.style;
        }
    }],

    proxy: {
        type: 'memory',
        reader: {
            type: 'json',
            rootProperty: 'users'
        }
    }
});