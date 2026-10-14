Ext.define('CMDBuildUI.model.dms.DMSModel', {
    extend: 'CMDBuildUI.model.base.Base',
    requires: [       
        'CMDBuildUI.validator.TrimPresence'
    ],
    mixins: [
        'CMDBuildUI.mixins.model.Filter',

        'CMDBuildUI.mixins.model.Attribute',
        'CMDBuildUI.mixins.model.Hierarchy',
        'CMDBuildUI.mixins.model.FormTrigger'
    ],
    statics: {
        modeltypes: {
            standard: 'standard'
        },
        checkCount: {
            no_check: 'no_check',
            at_least_number: 'at_least_number',
            exactly_number: 'exactly_number',
            max_number: 'max_number'
        },
        masterParentClass: 'DmsModel'
    },
    fields: [{
        name: 'name',
        type: 'string',
        critical: true
    }, {
        name: 'description',
        type: 'string',
        critical: true
    }, {
        name: 'parent',
        type: 'string',
        critical: true,
        defaultValue: 'DmsModel' // TODO: define prototype name
    }, {
        name: 'prototype',
        type: 'boolean',
        critical: true
    }, {
        name: 'active',
        type: 'boolean',
        critical: true,
        defaultValue: true
    }, {
        name: 'formTriggers',
        type: 'auto',
        critical: true
    }, {
        name: 'widgets',
        type: 'auto',
        critical: true
    }, {
        name: 'attributeGroups',
        type: 'auto',
        critical: true,
        defaultValue: []
    }, {
        name: 'multitenantMode',
        type: 'string',
        critical: true,
        defaultValue: 'never' // values ca be: never, always, mixed
    }, {
        name: '_icon',
        type: 'number',
        critical: true,
        persistent: true
    }, {
        name: '_iconPath',
        type: 'string',
        calculate: function (data) {
            if (data._icon) {
                return Ext.String.format('{0}/uploads/{1}/image.png', CMDBuildUI.util.Config.baseUrl, data._icon);
            } else {
                return null;
            }
        }
    }, {
        name: 'noteInlineClosed',
        type: 'boolean',
        defaultValue: false,
        critical: true
    }, {
        name: 'formStructure',
        type: 'auto',
        critical: true
    }, {
        name: 'allowedExtensions',
        type: 'string',
        defaultValue: '',
        critical: true
    }, {
        name: 'checkCount',
        type: 'string',
        defaultValue: 'no_check', // no_check, at_least_number, exactly_number, max_number
        critical: true,
        convert: function (value) {
            return value ? value : this.defaultValue; //FIXME: Seems that default value doesn't work. Had to do this for having the default value whne null from server
        }
    }, {
        name: 'checkCountNumber',
        type: 'number', // ignored if checkCount = no_check
        defaultValue: 0,
        critical: true
    }, {
        name: 'type', // hidden but required property
        type: 'string',
        critical: true,
        defaultValue: 'standard'
    }, {
        name: 'validationRule',
        type: 'string',
        critical: true
    }, {
        name: 'help',
        type: 'string',
        critical: true
    }, {
        name: 'maxFileSize',
        type: 'string',
        critical: true,
        defaultValue: null
    }],

    hasMany: [{
        model: 'CMDBuildUI.model.Attribute',
        name: 'attributes'
    }, {
        model: 'CMDBuildUI.model.AttributeOrder',
        name: 'defaultOrder',
        associationKey: 'defaultOrder'
    }, {
        model: 'CMDBuildUI.model.FormTrigger',
        name: 'formTriggers',
        associationKey: 'formTriggers'
    }, {
        model: 'CMDBuildUI.model.WidgetDefinition',
        name: 'widgets',
        associationKey: 'widgets'
    }, {
        model: 'CMDBuildUI.model.AttributeGrouping',
        name: 'attributeGroups'
    }, {
        model: 'CMDBuildUI.model.importexports.Template',
        name: 'importExportTemplates'
    }],

    validators: {
        name: [
            'trimpresence'
        ],
        description: ['trimpresence']
    },

    proxy: {
        url: '/dms/models',
        type: 'baseproxy',
        reader: {
            type: 'json'
        }
    },

    objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
    source: 'dms.DMSModels',

    /**
     * Get translated description
     * @param {Boolean} [force] default null (if true return always the translation even if exist,
     *  otherwise if viewContext is 'admin' return the original description)
     * @return {String} The translated description if exists. Otherwise the description.
     */
    getTranslatedDescription: function (force) {
        if (!force && CMDBuildUI.util.Ajax.getViewContext() === 'admin') {
            return this.get("description");
        }
        return this.get("_description_translation") || this.get("description");
    },

    /**
     * Get object for menu
     * @return {String}
     */
    getObjectTypeForMenu: function () {
        return this.get('name');
    },

    getObjectParent: function () {
        return Ext.getStore("dms.DMSModels").getById(this.get('parent'));
    },

    getObjectStore: function () {
        return Ext.getStore("dms.DMSModels");
    },

    /**
     * @return {String} domains url 
     */
    getAttributesUrl: function () {
        return CMDBuildUI.util.api.DMS.getModelAttributes(this.get("name"));
    }
});