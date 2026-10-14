/**
 * @file CMDBuildUI.model.AttributeGrouping
 * @class CMDBuildUI.model.AttributeGrouping
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.model.AttributeGrouping', {
    extend: 'Ext.data.Model',

    statics: {
        /**
         * @static
         *
         * @type {String}
         *
         * @example
         * var nogroup = CMDBuildUI.model.AttributeGrouping.nogroup;
         *
         * @memberof CMDBuildUI.model.AttributeGrouping
         */
        nogroup: '_nogroup',

        /**
         * @static
         *
         * @type {Object}
         * @property {String} open
         * @property {String} closed
         *
         * @example
         * var mode = CMDBuildUI.model.AttributeGrouping.displayMode.open;
         *
         * @memberof CMDBuildUI.model.AttributeGrouping
         */
        displayMode: {
            open: 'open',
            closed: 'closed'
        }
    },
    fields: [
        {
            name: 'name',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'description',
            type: 'string',
            defaultValue: ''
        },
        {
            name: 'index',
            type: 'number',
            defaultValue: null
        },
        {
            name: 'defaultDisplayMode',
            type: 'string',
            defaultValue: 'open'
        }
    ],
    proxy: {
        type: 'memory'
    }
});
