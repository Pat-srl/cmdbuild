Ext.define('CMDBuildUI.model.importexports.GateAttribute', {
    extend: 'Ext.data.Model',
    statics: {
        relativelocation: 'relative_location'
    },
    fields: [{
        // the Attribute.name 
        name: 'attribute',
        type: 'string',
        persist: true,
        critical: true
    }, {
        // theAttribute.description
        name: 'columnName',
        type: 'string',
        persist: true,
        critical: true
    }, {
        // theAttribute.description
        name: '_attribute_description',
        type: 'string'
    }, {
        // values can be default|description|code|id
        // editable only if theAttribute.type is lookup|reference
        name: 'mode',
        type: 'string',
        persist: true,
        critical: true,
        convert: function (value, record) {
            if (record.get('columnName') === 'CM_RELATIVE_LOCATION') {
                return CMDBuildUI.model.importexports.GateAttribute.relativelocation;
            }
            return value;
        }
    }, {
        // if theAttribute.type is reference, input type should be "combo" 
        // otherwise input type should be hidden        
        name: 'relative_location',
        type: 'string',
        persist: true,
        critical: true
    }, {
        // it is needed by reorder grig
        name: 'index',
        type: 'number',
        persist: false,
        critical: false
    }],
    proxy: {
        type: 'memory'
    }
});