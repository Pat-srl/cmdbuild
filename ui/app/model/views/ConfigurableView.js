Ext.define('CMDBuildUI.model.views.ConfigurableView', {
    extend: 'CMDBuildUI.model.views.View',

    statics: {
        userpemissions: {
            default: 'default',
            restrict: 'restrict',
            ignore: 'ignore'
        }
    },

    fields: [{
        name: 'filter',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'masterClass',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'masterClassAlias',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'active',
        type: 'boolean',
        persist: true,
        critical: true,
        defaultValue: true
    }, {
        name: 'join',
        type: 'auto',
        persist: true,
        critical: true,
        defaultValue: []
    }, {
        name: 'attributes',
        type: 'auto',
        persist: true,
        critical: true,
        defaultValue: []
    }, {
        name: 'attributeGroups',
        type: 'auto',
        persist: true,
        critical: true,
        defaultValue: []
    }, {
        name: 'sorter',
        type: 'auto',
        persist: true,
        critical: true,
        defaultValue: {}
    }, {
        name: 'formStructure',
        type: 'auto',
        persist: true,
        critical: true
    }, {
        name: 'contextMenuItems',
        type: 'auto',
        critical: true,
        persist: true
    }, {
        name: 'privilegeMode',
        persist: true,
        critical: true,
        defaultValue: 'default'
    }],

    hasMany: [{
        model: 'CMDBuildUI.model.views.JoinViewJoin',
        name: 'join',
        getterName: 'joinWith'
    }, {
        model: 'CMDBuildUI.model.views.JoinViewAttribute',
        name: 'attributes'
    }, {
        model: 'CMDBuildUI.model.AttributeGrouping',
        name: 'attributeGroups'
    }, {
        model: 'CMDBuildUI.model.AttributeOrder',
        name: 'sorter'
    }, {
        model: 'CMDBuildUI.model.Attribute',
        name: 'viewAttributes'
    }, {
        model: 'CMDBuildUI.model.ContextMenuItem',
        name: 'contextMenuItems',
        associationKey: 'contextMenuItems'
    }],

    attributesStoreName: 'viewAttributes',
    /**
     * @return {String} domains url 
     */
    getAttributesUrl: function () {
        return CMDBuildUI.util.api.Views.getAttributesUrl(this.get("name"));
    }

});