Ext.define('CMDBuildUI.model.processes.Process', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: ['CMDBuildUI.validator.TrimPresence'],

    statics: {
        flowstatus: {
            field: 'FlowStatus',
            lookuptype: 'FlowStatus'
        },
        masterParentClass: CMDBuildUI.util.helper.ModelHelper.systemClasses.process
    },

    mixins: [
        'CMDBuildUI.mixins.model.Filter',
        'CMDBuildUI.mixins.model.Domain',
        'CMDBuildUI.mixins.model.Attribute',
        'CMDBuildUI.mixins.model.Hierarchy',
        'CMDBuildUI.mixins.model.FormTrigger',
        'CMDBuildUI.mixins.model.ImportExportTemplates'
    ],
    isProcess: true,
    fields: [
        {
            name: 'name',
            type: 'string',
            critical: true
        },
        {
            name: 'description',
            type: 'string',
            critical: true
        },
        {
            name: 'parent',
            type: 'string',
            critical: true,
            defaultValue: CMDBuildUI.util.helper.ModelHelper.systemClasses.process
        },
        {
            name: 'prototype',
            type: 'boolean',
            critical: true
        },
        {
            name: 'flowStatusAttr',
            type: 'string',
            critical: true
        },
        {
            name: 'attributeGroups',
            type: 'auto',
            critical: true,
            defaultValue: []
        },
        {
            name: 'autoValue',
            type: 'string',
            critical: true
        },
        {
            name: 'messageAttr',
            type: 'string',
            critical: true
        },
        {
            name: 'enableSaveButton',
            type: 'boolean',
            critical: true
        },
        {
            name: 'hideSaveButton',
            type: 'boolean',
            calculate: function (data) {
                return !data.enableSaveButton;
            }
        },
        {
            name: 'defaultOrder',
            type: 'auto',
            critical: true
        },
        {
            name: 'formTriggers',
            type: 'auto',
            critical: true
        },
        {
            name: 'contextMenuItems',
            type: 'auto',
            critical: true
        },
        {
            name: 'widgets',
            type: 'auto',
            critical: true,
            defaultValue: []
        },
        {
            name: 'multitenantMode',
            type: 'string',
            critical: true,
            defaultValue: 'never' // values ca be: never, always, mixed
        },
        {
            name: 'active',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        {
            name: 'dmsCategory',
            critical: true,
            type: 'string'
        },
        {
            name: 'noteInline',
            type: 'boolean',
            critical: true
        },
        {
            name: 'noteInlineClosed',
            type: 'boolean',
            critical: true
        },
        {
            name: 'stoppableByUser',
            type: 'boolean',
            critical: true
        },
        {
            name: 'type',
            type: 'string',
            defaultValue: 'standard',
            critical: true
        },
        {
            name: '_icon',
            type: 'number',
            critical: true,
            persistent: true
        },
        {
            name: '_iconPath',
            type: 'string',
            calculate: function (data) {
                if (data._icon) {
                    return Ext.String.format('{0}/uploads/{1}/image.png', CMDBuildUI.util.Config.baseUrl, data._icon);
                } else {
                    return null;
                }
            }
        },
        {
            name: 'icon_font',
            type: 'string',
            critical: true
        },
        {
            name: 'icon_description',
            type: 'string',
            persist: false
        },
        {
            dame: 'domainOrder',
            type: 'auto',
            defaultValue: [],
            critical: true
        },
        {
            name: 'defaultFilter',
            type: 'string',
            critical: true
        },
        {
            name: 'help',
            type: 'string',
            critical: true
        },
        {
            name: 'uiRouting_mode',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'uiRouting_target',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'uiRouting_custom',
            type: 'auto',
            persist: true,
            critical: true
        },
        {
            name: 'barcodeSearchAttr',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'barcodeSearchRegex',
            type: 'string',
            persist: true,
            critical: true
        }
    ],

    hasMany: [
        {
            model: 'CMDBuildUI.model.Attribute',
            name: 'attributes'
        },
        {
            model: 'CMDBuildUI.model.domains.ObjectDomain',
            name: 'domains'
        },
        {
            model: 'CMDBuildUI.model.domains.FkDomain',
            name: 'fkdomains'
        },
        {
            model: 'CMDBuildUI.model.process.ProcessVersion',
            name: 'versions'
        },
        {
            model: 'CMDBuildUI.model.AttributeOrder',
            name: 'defaultOrder',
            associationKey: 'defaultOrder'
        },
        {
            model: 'CMDBuildUI.model.FormTrigger',
            name: 'formTriggers',
            associationKey: 'formTriggers'
        },
        {
            model: 'CMDBuildUI.model.ContextMenuItem',
            name: 'contextMenuItems',
            associationKey: 'contextMenuItems'
        },
        {
            model: 'CMDBuildUI.model.WidgetDefinition',
            name: 'widgets',
            associationKey: 'widgets'
        },
        {
            model: 'CMDBuildUI.model.base.Filter',
            name: 'filters'
        },
        {
            model: 'CMDBuildUI.model.AttributeGrouping',
            name: 'attributeGroups'
        },
        {
            model: 'CMDBuildUI.model.processes.Activity',
            name: 'activities'
        },
        {
            model: 'CMDBuildUI.model.importexports.Template',
            name: 'importExportTemplates'
        }
    ],
    validators: {
        name: ['trimpresence'],
        description: ['trimpresence']
    },

    proxy: {
        url: '/processes/',
        type: 'baseproxy'
    },

    objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
    source: 'processes.Processes',

    /**
     * Get translated description
     * @param {Boolean} [force] default null (if true return always the translation even if exist,
     *  otherwise if viewContext is 'admin' return the original description)
     * @return {String} The translated description if exists. Otherwise the description.
     */
    getTranslatedDescription: function (force) {
        if (!force && CMDBuildUI.util.Ajax.getViewContext() === 'admin') {
            return this.get('description');
        }
        return this.get('_description_translation') || this.get('description');
    },

    /**
     * Get object for menu
     * @return {String}
     */
    getObjectTypeForMenu: function () {
        return this.get('name');
    },

    /**
     * Load activities relation
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     * @return {Ext.Deferred} The promise has as parameters the attributes store and a boolean field.
     */
    getActivities: function (force) {
        var deferred = new Ext.Deferred();
        var activities = this.activities();
        var processName = this.get('name');

        if (!activities.isLoaded() || force) {
            activities.setProxy({
                type: 'baseproxy',
                url: Ext.String.format('{0}/processes/{1}/activities', CMDBuildUI.util.Config.baseUrl, processName)
            });

            activities.load({
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve(activities, true);
                    }
                }
            });
        } else {
            deferred.resolve(activities, true);
        }

        return deferred.promise;
    },

    /**
     * @return {String}
     */
    getObjectParent: function () {
        return CMDBuildUI.util.helper.ModelHelper.getProcessFromName(this.get('parent'));
    },

    getObjectStore: function () {
        return Ext.getStore('processes.Processes');
    },

    /**
     * @return {String} attributes url
     */
    getAttributesUrl: function () {
        return CMDBuildUI.util.api.Processes.getAttributes(this.get('name'));
    },

    /**
     * @return {String} domains url
     */
    getDomainsUrl: function () {
        return CMDBuildUI.util.api.Processes.getDomains(this.get('name'));
    },

    /**
     * @return {String} import/export templates url
     */
    getImportExportTemplatesUrl: function () {
        return CMDBuildUI.util.api.Classes.getImportExportTemplatesUrl(this.get('name'));
    }
});
