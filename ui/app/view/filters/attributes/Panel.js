
Ext.define('CMDBuildUI.view.filters.attributes.Panel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.filters.attributes.PanelController',
        'CMDBuildUI.view.filters.attributes.PanelModel'
    ],

    alias: 'widget.filters-attributes-panel',
    controller: 'filters-attributes-panel',
    viewModel: {
        type: 'filters-attributes-panel'
    },

    title: CMDBuildUI.locales.Locales.filters.attributes,

    localized: {
        title: 'CMDBuildUI.locales.Locales.filters.attributes'
    },

    config: {
        /**
         * @cfg {Boolean} allowInputParameter
         */
        allowInputParameter: true,

        /**
        * @cfg {Boolean} allowCurrentUser
        */
        allowCurrentUser: false,

        /**
         * @cfg {Boolean} allowCurrentGroup
         */
        allowCurrentGroup: false,

        /**
         * @cfg {boolean} allowArbitraryAttributeName
         */
        allowArbitraryAttributeName: false,

        /**
         * @cfg {boolean} onlyOneLevel
         */
        onlyOneLevel: false,

        /**
         * @cfg {boolean} useTextFieldForValue
         */
        useTextFieldForValue: false
    },

    scrollable: true,
    layout: {
        type: 'anchor',
        reserveScrollbar: true
    },

    items: [{
        xtype: 'filters-attributes-block',
        operator: CMDBuildUI.util.helper.FiltersHelper.blocksoperators.and,
        itemId: 'mainblock'
    }],

    /**
     * @return {Object}
     */
    getAttributesData: function () {
        return this.down('#mainblock').getFilterData();
    },

    privates: {
        _fieldsetsreferences: []
    }
});
