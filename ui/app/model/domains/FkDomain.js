Ext.define('CMDBuildUI.model.domains.FkDomain', {
    extend: 'CMDBuildUI.model.base.Base',


    fields: [{
        name: 'name',
        type: 'string',
        mapping: function (data) {
            return data._id;
        }
    }, {
        name: 'source',
        type: 'string'
    }, {
        name: 'sourceProcess',
        type: 'boolean',
        defaultValue: false
    }, {
        name: 'destination',
        type: 'string'
    }, {
        name: 'destinationProcess',
        type: 'boolean',
        calculate: function (data) {
            return CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(data.destination) === CMDBuildUI.util.helper.ModelHelper.objecttypes.process;
        }
    }, {
        name: 'cardinality',
        type: 'string'
    }, {
        name: 'isMasterDetail',
        type: 'boolean'
    }, {
        name: 'descriptionMasterDetail',
        type: 'string'
    }, {
        name: 'active',
        type: 'boolean',
        defaultValue: true
    }, {
        name: 'fk_attribute_direction',
        type: 'string'
    }, {
        name: 'fk_attribute_name',
        type: 'string'
    }],

    proxy: {
        url: CMDBuildUI.util.api.Domains.getFkDomainsUrl(),
        type: 'baseproxy'
    },

    /**
     * Get get the description of source class or process.
     * 
     * @return {String}
     */
    getSourceDescription: function () {
        return CMDBuildUI.util.helper.ModelHelper.getObjectDescription(this.get('source'));
    },

    /**
     * Get get the description of destination class or process.
     * 
     * @return {String}
     */
    getDestinationDescription: function () {
        return CMDBuildUI.util.helper.ModelHelper.getObjectDescription(this.get('destination'));
    },


    /**
     * Get translation for Master/Detail description.
     * 
     * @return {String}
     */
    getTranslatedDescriptionMasterDetail: function () {
        return this.get("_descriptionMasterDetail_translation") || this.get("descriptionMasterDetail");
    },

    /**
     * Get translation for direct description.
     * 
     * @return {String}
     */
    getTranslatedDescriptionDirect: function () {
        return CMDBuildUI.util.helper.ModelHelper.getObjectDescription(this.get('source'));
    },

    /**
     * Get translation for inverse description.
     * 
     * @return {String}
     */
    getTranslatedDescriptionInverse: function () {
        return CMDBuildUI.util.helper.ModelHelper.getObjectDescription(this.get('destination'));
    }
});