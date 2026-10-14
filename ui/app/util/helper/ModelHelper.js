/**
 * @file CMDBuildUI.util.helper.ModelHelper
 * @module CMDBuildUI.util.helper.ModelHelper
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.helper.ModelHelper', {
    singleton: true,

    requires: [
        'CMDBuildUI.proxy.AttributesProxy' //TODO: Remove when attributes are fixed in CMDBuild
    ],

    /**
     * @constant {Object} objecttypes Available object types.
     * @property {String} calendar
     * @property {String} custompage
     * @property {String} dashboard
     * @property {String} dmsmodel
     * @property {String} domain
     * @property {String} event
     * @property {String} klass
     * @property {String} navtreecontent
     * @property {String} process
     * @property {String} report
     * @property {String} view
     *
     */
    objecttypes: {
        calendar: 'calendar',
        custompage: 'custompage',
        dashboard: 'dashboard',
        homepage: 'homepage',
        dependencytree: 'dependencytree',
        dmsmodel: 'dmsmodel',
        domain: 'domain',
        event: 'event',
        klass: 'class',
        navtreecontent: 'navtreecontent',
        process: 'process',
        report: 'report',
        view: 'view'
    },

    /**
     * @constant {Object} cmdbuildtypes All CMDBuild attributes types managed by the application.
     * @property {String} activity
     * @property {String} bigint
     * @property {String} boolean
     * @property {String} char
     * @property {String} date
     * @property {String} datetime
     * @property {String} decimal
     * @property {String} double
     * @property {String} foreignkey
     * @property {String} integer
     * @property {String} ipaddress
     * @property {String} link
     * @property {String} lookup
     * @property {String} lookuparray
     * @property {String} reference
     * @property {String} string
     * @property {String} text
     * @property {String} time
     * @property {String} tenant
     */
    cmdbuildtypes: {
        activity: 'activity',
        bigint: 'long',
        boolean: 'boolean',
        char: 'char',
        date: 'date',
        datetime: 'dateTime',
        decimal: 'decimal',
        double: 'double',
        file: 'file',
        foreignkey: 'foreignKey',
        formula: 'formula',
        integer: 'integer',
        ipaddress: 'ipAddress',
        link: 'link',
        lookup: 'lookup',
        lookuparray: 'lookupArray',
        reference: 'reference',
        string: 'string',
        text: 'text',
        time: 'time',
        tenant: 'tenant',
        entrytype: 'entrytype'
    },

    /**
     * @constant {object} extraFields All fields with uncommon behaviour
     * @property {String} notes
     */
    extraFields: {
        notes: 'Notes',
        idTenant: 'IdTenant'
    },

    /**
     * @constant {String[]} ignoredFields Attributes to ignore in model generation.
     */
    ignoredFields: ['Id', 'Notes', 'IdTenant', 'FlowStatus', 'IdClass'],

    /**
     * @constant {object} systemClasses All system classes
     * @property {String} class
     * @property {String} process
     */
    systemClasses: {
        class: 'Class',
        process: 'Activity'
    },

    /**
     * Return the model name for given type and name.
     *
     * @param {String} type Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties.
     * @param {String} name Class/Process/View/... name.
     *
     * @returns  {String} Model name.
     *
     * @example
     * var modelName = CMDBuildUI.util.helper.ModelHelper.getModelName(
     *      CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
     *      'Employee'
     * );
     */
    getModelName: function (type, name) {
        if (type === CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar) {
            switch (name) {
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.event:
                    return 'CMDBuildUI.model.calendar.Event';
            }
        }
        return 'CMDBuildUI.model.' + type + '.' + name;
    },

    /**
     * Returns existing model by model name.
     *
     * @param {String} modelName Model name
     *
     * @returns {Ext.data.Model} Existing CMDBuild model.
     *
     * @example
     * var modelName = CMDBuildUI.util.helper.ModelHelper.getModelFromName(
     *      'Employee'
     * );
     * var model = CMDBuildUI.util.helper.ModelHelper.getModelFromName(modelName);
     */
    getModelFromName: function (modelName) {
        return Ext.ClassManager.get(modelName);
    },

    /**
     * Returns model by type and name. If it doesn't exist this method will create it.
     *
     * @param {String} type Object type. One of {@link module:CMDBuildUI.util.helper.ModelHelper#objecttypes CMDBuildUI.util.helper.ModelHelper.objecttypes} properties.
     * @param {String} name Class/Process/View/... name.
     *
     * @returns {Ext.promise.Promise<Ext.data.Model>} Resolve method has as argument an
     *      instance of Ext.data.Model. Reject method has as argument
     *      a {String} containing error message.
     *
     * @example
     * CMDBuildUI.util.helper.ModelHelper.getModel(
     *      CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
     *      'Employee'
     * ).then(function (model) {
     *      view.add(
     *          CMDBuildUI.util.helper.FormHelper.renderForm(model, {
     *              mode: CMDBuildUI.util.helper.FormHelper.formmodes.create
     *          })
     *      );
     * });
     */
    getModel: function (type, name) {
        var deferred = new Ext.Deferred();
        var me = this;

        // get model name
        var modelName = this.getModelName(type, name);

        // returns model if already exists
        if (Ext.ClassManager.isCreated(modelName)) {
            var model = CMDBuildUI.util.helper.ModelHelper.getModelFromName(modelName);
            deferred.resolve(model);
            return deferred.promise;
        }

        // get url
        var baseUrl = this.getBaseUrl(type, name);

        /**
         * Define load callback
         *
         * @param {Ext.data.Store} store
         * @param {CMDBuildUI.model.Attribute[]} records
         * @param {Boolean} successful
         * @param {Ext.data.operation.Read} operation
         * @param {Object} eOpts
         */
        function onStoreLoaded(store, records, successful, operation, eOpts) {
            if (successful) {
                var fields = [];
                store.sort([
                    {
                        property: 'index',
                        direction: 'ASC'
                    }
                ]);
                Ext.Array.each(store.getData().getRange(), function (attribute, index) {
                    if (
                        attribute.get('active') &&
                        !Ext.Array.contains(CMDBuildUI.util.helper.ModelHelper.ignoredFields, attribute.get('name'))
                    ) {
                        // override mandatory property when create process instance model
                        // because mandatory fields are defined by activities
                        if (type === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                            attribute.set('mandatory', false);
                        }
                        // create field definition
                        var field = me.getModelFieldFromAttribute(attribute);
                        if (field) {
                            // add field
                            Ext.Array.push(fields, field);
                        }
                    }
                });

                // define new model
                if (!Ext.ClassManager.isCreated(modelName)) {
                    Ext.define(
                        modelName,
                        Ext.applyIf(me.getModelDefinition(type, baseUrl, fields), {
                            statics: {
                                objectType: type,
                                objectTypeName: name
                            }
                        })
                    );
                    CMDBuildUI.util.Logger.log('Create new model: ' + modelName, CMDBuildUI.util.Logger.levels.debug);
                }

                var model = Ext.ClassManager.get(modelName);
                deferred.resolve(model);
            } else {
                // execute failure callback
                deferred.reject('Base url not defined.');
            }
        }

        var item = this.getObjectFromName(name, type);
        if (item) {
            if (!baseUrl || Ext.String.endsWith(baseUrl, 'null')) {
                // TODO: verificare correttezza del baseUrl.endsWith('null')
                deferred.reject('Base url not defined.');
            } else if (item && item.getAttributes) {
                item.getAttributes().then(function (attributes) {
                    onStoreLoaded(attributes, attributes.getRange(), true);
                });
            } else {
                CMDBuildUI.util.Logger.log(
                    Ext.String.format('Get attributes method not implemented for type: {0}', type),
                    CMDBuildUI.util.Logger.levels.warn
                );
                // create new store
                var attributesStore = Ext.create('Ext.data.Store', {
                    model: 'CMDBuildUI.model.Attribute',

                    proxy: {
                        url: baseUrl + '/attributes',
                        type: 'baseproxy'
                    },
                    pageSize: 0, // disable pagination
                    autoDestroy: true,

                    sorters: ['index'],

                    listeners: {
                        load: onStoreLoaded
                    }
                });
                attributesStore.load();
            }
        } else {
            CMDBuildUI.util.Logger.log(
                Ext.String.format('Element not found. Type: {0} - Name: {1}', type, name),
                CMDBuildUI.util.Logger.levels.error
            );
            deferred.reject();
        }
        // check if the model exists

        return deferred.promise;
    },

    /**
     * Get the base url for give type and name.
     *
     * @private
     *
     * @param {String} type Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties.
     * @param {String} name Class/Process/View/... name.
     *
     * @returns {String} Base url the model definition.
     */
    getBaseUrl: function (type, name) {
        var url;
        switch (type) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                url = '/classes/' + name;
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel:
                url = '/dms/models/' + name;
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                url = '/processes/' + name;
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.view:
                url = '/views/' + name;
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.report:
                url = '/reports/' + name;
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.domain:
                url = '/domains/' + name;
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar:
                url = '/events';
                break;
            default:
                CMDBuildUI.util.Logger.log('Warning!', 'Type ' + type + ' non recognized in createModel function!');
        }
        return url;
    },

    /**
     * Returns the model definition
     *
     * @private
     *
     * @param {String} type Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties.
     * @param {String} baseUrl Model base url
     * @param {Ext.field.Field[]} fields Fields array
     *
     * @returns {Object} Model definition
     */
    getModelDefinition: function (type, baseUrl, fields) {
        var modelname, endpint, proxytype, extraparams, hasmany;
        switch (type) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                modelname = 'CMDBuildUI.model.classes.Card';
                endpint = 'cards';
                proxytype = 'baseproxy';
                hasmany = [
                    {
                        type: 'CMDBuildUI.model.WidgetDefinition',
                        name: 'widgets',
                        associationKey: '_widgets'
                    },
                    {
                        type: 'CMDBuildUI.model.calendar.Sequence',
                        name: 'sequences',
                        associationKey: '_sequences'
                    },
                    {
                        type: 'CMDBuildUI.model.gis.GeoValue',
                        name: 'geovalues',
                        associationKey: '_geovalues'
                    },
                    {
                        type: 'CMDBuildUI.model.gis.GeoLayer',
                        name: 'geolayers',
                        associationKey: '_geolayers'
                    }
                ];
                extraparams = {
                    onlyGridAttrs: true
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                modelname = 'CMDBuildUI.model.processes.Instance';
                endpint = 'instances';
                proxytype = 'baseproxy';
                extraparams = {
                    include_tasklist: true,
                    onlyGridAttrs: true
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.view:
                modelname = 'CMDBuildUI.model.views.ViewItem';
                endpint = 'cards';
                proxytype = 'baseproxy';
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.domain:
                modelname = 'CMDBuildUI.model.domains.Domain';
                endpint = '';
                proxytype = 'baseproxy';
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.report:
                modelname = 'CMDBuildUI.model.reports.ReportItem';
                endpint = '';
                proxytype = 'baseproxy';
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel:
                modelname = 'CMDBuildUI.model.dms.DMSAttachment';
                proxytype = 'baseproxy';
                hasmany = [
                    {
                        type: 'CMDBuildUI.model.WidgetDefinition',
                        name: 'widgets',
                        associationKey: '_widgets'
                    },
                    {
                        type: 'CMDBuildUI.model.calendar.Sequence',
                        name: 'sequences',
                        associationKey: '_sequences'
                    }
                ];
                break;
            default:
                CMDBuildUI.util.Logger.log('Warning!', 'Type ' + type + ' non recognized in createModel function!');
        }
        var proxy = {
            url: Ext.String.format('{0}/{1}', baseUrl, endpint),
            type: proxytype,
            writer: {
                writeAllFields: true
            }
        };
        if (extraparams) {
            proxy.extraParams = extraparams;
        }
        return {
            extend: modelname,
            fields: fields,
            proxy: proxy,
            hasMany: hasmany
        };
    },

    /**
     * Get the base url for lists.
     *
     * @param {String} type Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties.
     * @param {String} name Class/Process/View/... name.
     *
     * @returns {String} The url to retrieve the list for specified type.
     *
     * @example
     * var store = {
     *      proxy: {
     *          type: 'baseproxy',
     *          url: CMDBuildUI.util.helper.ModelHelper.getListBaseUrl(
     *              CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
     *              'Employee'
     *          )
     *      },
     *      autoLoad: true,
     *      autoDestroy: true
     * }
     */
    getListBaseUrl: function (type, name) {
        var url = CMDBuildUI.util.helper.ModelHelper.getBaseUrl(type, name);
        switch (type) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                url += '/cards/';
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                url += '/instances/';
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.view:
                url += '/cards/';
                break;
            default:
                CMDBuildUI.util.Logger.log('Warning!', 'Type ' + type + ' non recognized in createModel function!');
        }
        return url;
    },

    /**
     * Return the name for Notes model.
     *
     * @private
     *
     * @param {String} type Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties.
     * @param {String} name Class/Process/View/... name.
     *
     * @returns  {String} The name of notes model
     */
    getNotesModelName: function (type, name) {
        return 'CMDBuildUI.model.notes.' + type + '.' + name;
    },

    /**
     * Return the model for notes by type and name.
     *
     * @param {String} type Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties.
     * @param {String} name Class/Process/View/... name.
     *
     * @returns {Ext.data.Model} Notes model.
     *
     * @example
     * var model = CMDBuildUI.util.helper.ModelHelper.getNotesModel(
     *      CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
     *      'Employee'
     * );
     */
    getNotesModel: function (type, name, callback) {
        // get model name
        var modelName = this.getNotesModelName(type, name);

        // check if the model exists
        if (!Ext.ClassManager.isCreated(modelName)) {
            // get url
            var baseUrl = this.getBaseUrl(type, name);
            if (!baseUrl) {
                return null;
            }
            var field = this.getModelField({
                cmdbuildtype: 'text',
                defaultValue: null,
                description: 'Notes',
                hidden: false,
                mandatory: false,
                name: 'Notes',
                type: 'text',
                writable: true,
                metadata: {
                    editorType: 'HTML'
                }
            });

            var fields = [field];

            if (type == 'process') {
                fields.push({
                    name: '_activity',
                    critical: true
                });
            }

            // define new model
            Ext.define(modelName, {
                extend: 'CMDBuildUI.model.base.Base',
                statics: {
                    objectType: type,
                    objectTypeName: name
                },
                fields: fields,
                proxy: {
                    url: this.getListBaseUrl(type, name),
                    type: 'baseproxy'
                }
            });
            CMDBuildUI.util.Logger.log('Create new model: ' + modelName, CMDBuildUI.util.Logger.levels.debug);
        }

        var model = Ext.ClassManager.get(modelName);
        return model;
    },

    /**
     * Return the name for History model.
     *
     * @param {String} type Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties.
     * @param {String} name Class/Process/View/... name.
     *
     * @returns  {String} History model name.
     */
    getHistoryModelName: function (type, name) {
        return 'CMDBuildUI.model.history.' + type + '.' + name;
    },

    /**
     * Return the model for history by type and name.
     *
     * @param {String} type Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties.
     * @param {String} name Class/Process/View/... name.
     *
     * @returns {Ext.data.Model} History model.
     *
     * @example
     * var model = CMDBuildUI.util.helper.ModelHelper.getHistoryModel(
     *      CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
     *      'Employee'
     * );
     */
    getHistoryModel: function (type, name) {
        var modelName = CMDBuildUI.util.helper.ModelHelper.getHistoryModelName(type, name);
        // check if the model exists
        if (!Ext.ClassManager.isCreated(modelName)) {
            var parentModelName = CMDBuildUI.util.helper.ModelHelper.getModelName(type, name);
            // define new model
            Ext.define(modelName, {
                extend: parentModelName,
                statics: {
                    objectType: type,
                    objectTypeName: name
                },
                fields: CMDBuildUI.model.History.fields,
                proxy: {} // override proxy
            });
            CMDBuildUI.util.Logger.log('Create new model: ' + modelName, CMDBuildUI.util.Logger.levels.debug);
        }

        // return model
        return Ext.ClassManager.get(modelName);
    },

    /**
     * Get model field definition from CMDBuild attribute.
     *
     * @param {CMDBuildUI.model.Attribute} attribute CMDBuild attribute definition.
     *
     * @returns {Ext.data.field.Field} Model field definition.
     *
     * @example
     * var fields = [];
     * attributes.forEach(function (attribute) {
     *      fields.push(CMDBuildUI.util.helper.ModelHelper.getModelFieldFromAttribute(attribute));
     * });
     */
    getModelFieldFromAttribute: function (attribute) {
        var fielddef = this.getModelField(attribute.getData()),
            mainfield = fielddef;
        if (fielddef) {
            if (Ext.isArray(fielddef)) {
                mainfield = fielddef[0];
            }
            // get translated description
            mainfield.attributeconf.description_localized = attribute.getTranslatedDescription();
        }
        return fielddef;
    },

    /**
     * Get model field definition from CMDBuild attribute
     *
     * @private
     *
     * @param {CMDBuildUI.model.Attribute} attribute CMDBuild attribute definition.
     *
     * @returns {Ext.data.field.Field} Model field definition.
     */
    getModelField: function (attribute) {
        var fieldname = CMDBuildUI.util.Utilities.stringRemoveSpecialCharacters(attribute.name);
        var field = {
            name: fieldname,
            cmdbuildtype: attribute.type,
            attributename: attribute.name,
            description: attribute.description || attribute.name,
            mandatory: attribute.mandatory,
            defaultValue: attribute.defaultValue || null,
            // writable: attribute.writable,
            mode: attribute.mode,
            hidden: attribute.hidden,
            writable: attribute.writable,
            attributeconf: attribute,
            allowNull: true,
            validators: []
        };

        if (fieldname !== attribute.name) {
            field.mapping = function (data) {
                return data[attribute.name];
            };
        }

        if (attribute.mandatory) {
            field.validators.push('presence');
        }

        // get field type
        switch (attribute.type.toLowerCase()) {
            /**
             * Boolean field
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean.toLowerCase():
                var defaultValue = attribute.defaultValue;
                if (defaultValue && typeof defaultValue == 'string') {
                    field.defaultValue = defaultValue === 'true';
                }
                field.type = 'boolean';
                break;
            /**
             * Date fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date.toLowerCase():
                field.type = 'date';
                field.dateWriteFormat = 'c';
                field.dateReadFormat = 'c';
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime.toLowerCase():
                field.type = 'date';
                field.dateWriteFormat = 'c';
                field.dateReadFormat = 'c';
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time.toLowerCase():
                field.type = 'string';
                break;
            /**
             * IP field
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress.toLowerCase():
                field.type = 'string';
                break;
            /**
             * Numeric fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal.toLowerCase():
                field.type = 'number';
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double.toLowerCase():
                field.type = 'number';
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint.toLowerCase():
                field.type = 'string';
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer.toLowerCase():
                field.type = 'integer';
                break;
            /**
             * Relation fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase():
                field.type = 'integer';
                CMDBuildUI.model.lookups.LookupType.loadLookupValues(field.attributeconf.lookupType);
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray.toLowerCase():
                field.type = 'auto';
                field.defaultValue = [];
                CMDBuildUI.model.lookups.LookupType.loadLookupValues(field.attributeconf.lookupType);
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase():
                field.type = 'integer';
                if (!field.attributeconf.targetType) {
                    field.attributeconf.targetType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(
                        field.attributeconf.targetClass
                    );
                }
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey.toLowerCase():
                field.type = 'integer';
                if (!field.attributeconf.targetType) {
                    field.attributeconf.targetType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(
                        field.attributeconf.targetClass
                    );
                }
                break;
            /**
             * File field
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.file.toLowerCase():
                field.type = 'string';
                break;
            /**
             * Text fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char.toLowerCase():
                field.type = 'string';
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string.toLowerCase():
                field.type = 'string';
                if (
                    field.attributeconf.password &&
                    field.attributeconf.showPassword === CMDBuildUI.model.Attribute.showPassword.never
                ) {
                    field.serialize = function (value, record) {
                        if (value) {
                            return value;
                        }
                    };
                }
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text.toLowerCase():
                field.type = 'string';
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula.toLowerCase():
                field.type = 'string';
                field.persist = false;
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.link.toLowerCase():
                field.type = 'string';
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.entrytype.toLowerCase():
                field.type = 'string';
                break;
            default:
                CMDBuildUI.util.Logger.log(
                    'No type specified for CMDBuild type ' + attribute.type,
                    CMDBuildUI.util.Logger.levels.warn
                );
                return;
        }

        return field;
    },

    /**
     * Returns the object type for given Class/Process/View/... name.
     *
     * @param {String} objectTypeName Class/Process/View/... name.
     *
     * @returns {String}
     *
     * @example
     * var objectTypeName = 'Employee';
     * var type = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(objectTypeName);
     */
    getObjectTypeByName: function (objectTypeName) {
        // return class if objectTypeName is a class
        if (this.getClassFromName(objectTypeName)) {
            return CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
        }
        // return process if objectTypeName is a process
        if (this.getProcessFromName(objectTypeName)) {
            return CMDBuildUI.util.helper.ModelHelper.objecttypes.process;
        }
        // return class if objectTypeName is a DMSModel
        if (this.getDMSModelFromName(objectTypeName)) {
            return CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel;
        }
        // return domain if objectTypeName is a domain
        if (this.getDomainFromName(objectTypeName)) {
            return CMDBuildUI.util.helper.ModelHelper.objecttypes.domain;
        }
        // return report if objectTypeName is a report
        if (this.getReportFromName(objectTypeName)) {
            return CMDBuildUI.util.helper.ModelHelper.objecttypes.report;
        }
        if (this.getViewFromName(objectTypeName)) {
            return CMDBuildUI.util.helper.ModelHelper.objecttypes.view;
        }
        // return calendar if objectTypeName is event
        if (objectTypeName === CMDBuildUI.util.helper.ModelHelper.objecttypes.event) {
            return CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar;
        }
    },

    /**
     * Returns Class object by class name.
     *
     * @param {String} className Class name.
     *
     * @returns {CMDBuildUI.model.classes.Class} Class object
     *
     * @example
     * var item = CMDBuildUI.util.helper.ModelHelper.getClassFromName('Employee');
     */
    getClassFromName: function (className) {
        return Ext.getStore('classes.Classes').getById(className);
    },

    /**
     * Returns Domain object from domain name.
     *
     * @param {String} domainName Domain name.
     *
     * @returns {CMDBuildUI.model.domains.Domain} Domain object
     *
     * @example
     * var item = CMDBuildUI.util.helper.ModelHelper.getDomainFromName('AssigneeCI');
     */
    getDomainFromName: function (domainName) {
        return Ext.getStore('domains.Domains').getById(domainName);
    },

    /**
     * Returns Process object from process name.
     *
     * @param {String} processName Process name.
     *
     * @returns {CMDBuildUI.model.processes.Process} Process object.
     *
     * @example
     * var item = CMDBuildUI.util.helper.ModelHelper.getProcessFromName('IncidentMgt');
     */
    getProcessFromName: function (processName) {
        return Ext.getStore('processes.Processes').getById(processName);
    },

    /**
     * Returns DMSModel object from DMSModel name.
     *
     * @param {String} modelName DMSModel name.
     *
     * @returns {CMDBuildUI.model.dms.DMSModel} DMSModel object.
     *
     * @example
     * var item = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName('BaseDocument');
     */
    getDMSModelFromName: function (modelName) {
        return Ext.getStore('dms.DMSModels').getById(modelName);
    },

    /**
     * Returns View object from view name.
     *
     * @param {String} viewName View name.
     *
     * @returns {CMDBuildUI.model.views.View} View object.
     *
     * @example
     * var item = CMDBuildUI.util.helper.ModelHelper.getViewFromName('ExpiringWarranty');
     */
    getViewFromName: function (viewName) {
        return Ext.getStore('views.Views').getById(viewName);
    },

    /**
     * Returns Report object from report name.
     *
     * @param {String} reportName Report name.
     *
     * @returns {CMDBuildUI.model.reports.Report} Report object.
     *
     * @example
     * var item = CMDBuildUI.util.helper.ModelHelper.getReportFromName('ActiveTicketsInDate');
     */
    getReportFromName: function (reportName) {
        return Ext.getStore('reports.Reports').findRecord('code', reportName);
    },

    /**
     * Returns Dashbaord object from dashboard name.
     *
     * @param {String} dashboardName Dashboard name.
     *
     * @returns {CMDBuildUI.model.dashboards.Dashboard} Dashboard object.
     *
     * @example
     * var item = CMDBuildUI.util.helper.ModelHelper.getDashboardFromName('ServiceDeskActiveTickets');
     */
    getDashboardFromName: function (dashboardName) {
        return Ext.getStore('dashboards.Dashboards').findRecord('name', dashboardName);
    },

    /**
     * Returns Homepage object from homepage name.
     *
     * @param {String} homepageName Homepage name.
     *
     * @returns {CMDBuildUI.model.homepages.Homepage} Homepage object.
     *
     * @example
     * var item = CMDBuildUI.util.helper.ModelHelper.getHomepageFromName('ServiceDeskActiveTickets');
     */
    getHomepageFromName: function (homepageName) {
        return Ext.getStore('homepages.Homepages').findRecord('name', homepageName);
    },

    /**
     * Returns Custom Page object from custom page name.
     *
     * @param {String} customPageName Custom Page name.
     *
     * @returns {CMDBuildUI.model.custompages.CustomPage} Custom Page object.
     *
     * @example
     * var item = CMDBuildUI.util.helper.ModelHelper.getCustomPageFromName('ticketssummary');
     */
    getCustomPageFromName: function (customPageName) {
        return Ext.getStore('custompages.CustomPages').findRecord('name', customPageName);
    },

    /**
     * Returns Function object from function name.
     *
     * @param {String} functionName Function name.
     *
     * @returns {CMDBuildUI.model.Function} Function object.
     */
    getFunctionFromName: function (functionName) {
        return Ext.getStore('Functions').findRecord('name', functionName);
    },

    /**
     * Returns Navigation content object from name.
     *
     * @param {String} navContent Nav tree name.
     *
     * @returns {CMDBuildUI.model.reports.Report} Report object.
     *
     * @example
     * var item = CMDBuildUI.util.helper.ModelHelper.getNavigationContentFromName('MyNavContent');
     */
    getNavtreeFromName: function (navContent) {
        return Ext.getStore('menu.NavigationTrees').findRecord('_id', navContent);
    },

    /**
     * Returns Calendar object
     *
     * @returns {CMDBuildUI.model.calendar.Calendar} Calendar object.
     */
    getCalendar: function () {
        if (!CMDBuildUI.util.helper.ModelHelper._calendarinstance) {
            var dmsCategoryTypeName = CMDBuildUI.util.helper.AttachmentsHelper.getCategoryTypeName(),
                dmsCategory = CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(dmsCategoryTypeName);
            CMDBuildUI.util.helper.ModelHelper._calendarinstance = Ext.create('CMDBuildUI.model.calendar.Calendar', {
                dmsCategory: dmsCategoryTypeName,
                dmsCategories: dmsCategory.values().getRange()
            });
        }
        return CMDBuildUI.util.helper.ModelHelper._calendarinstance;
    },

    /**
     * Returns Class/Process/View/... object for given name.
     *
     * @param {String} typeName Class/Process/View/... name.
     * @param {String} [type] Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties. If null it will be calculated.
     *
     * @returns {Ext.data.Model} The model, if exists, of given name.
     *
     * @example
     * var employee = CMDBuildUI.util.helper.ModelHelper.getObjectFromName('Employee');
     */
    getObjectFromName: function (typeName, type) {
        type = type || CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(typeName);

        switch (type) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                return CMDBuildUI.util.helper.ModelHelper.getClassFromName(typeName);
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                return CMDBuildUI.util.helper.ModelHelper.getProcessFromName(typeName);
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel:
                return CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(typeName);
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.view:
                return CMDBuildUI.util.helper.ModelHelper.getViewFromName(typeName);
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.report:
                return CMDBuildUI.util.helper.ModelHelper.getReportFromName(typeName);
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.domain:
                return CMDBuildUI.util.helper.ModelHelper.getDomainFromName(typeName);
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.custompage:
                return CMDBuildUI.util.helper.ModelHelper.getCustomPageFromName(typeName);
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.dashboard:
                return CMDBuildUI.util.helper.ModelHelper.getDashboardFromName(typeName);
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar:
                return CMDBuildUI.util.helper.ModelHelper.getCalendar();
            case CMDBuildUI.model.menu.MenuItem.types.navtree:
                return CMDBuildUI.util.helper.ModelHelper.getNavtreeFromName(typeName);
        }
    },

    /**
     * Returns Class object description.
     *
     * @param {String} className Class name.
     *
     * @returns {String} Class description
     *
     * @example
     * var description = CMDBuildUI.util.helper.ModelHelper.getClassDescription("Employee");
     */
    getClassDescription: function (className) {
        var klass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(className);
        return klass ? klass.getTranslatedDescription() : null;
    },

    /**
     * Returns Process object description.
     *
     * @param {String} processName Process name
     *
     * @returns {String} Process description
     *
     * @example
     * var description = CMDBuildUI.util.helper.ModelHelper.getProcessDescription("IncidentMgt");
     */
    getProcessDescription: function (processName) {
        var processes = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(processName);
        return processes ? processes.getTranslatedDescription() : null;
    },

    /**
     * Returns DMSModel object description.
     *
     * @param {String} modelName DMSModel name.
     *
     * @returns {String} DMSModel description
     *
     * @example
     * var description = CMDBuildUI.util.helper.ModelHelper.getDMSModelDescription("BaseDocument");
     */
    getDMSModelDescription: function (modelName) {
        var model = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(modelName);
        return model ? model.getTranslatedDescription() : null;
    },

    /**
     * Returns View object description.
     *
     * @param {String} viewName View name.
     *
     * @returns {String} View description.
     *
     * @example
     * var description = CMDBuildUI.util.helper.ModelHelper.getViewDescription("ExpiringWarranty");
     */
    getViewDescription: function (viewName) {
        var view = CMDBuildUI.util.helper.ModelHelper.getViewFromName(viewName);
        return view ? view.getTranslatedDescription() : null;
    },

    /**
     * Returns Report object description.
     *
     * @param {String} reportName Report name.
     *
     * @returns {String} Report description.
     *
     * @example
     * var description = CMDBuildUI.util.helper.ModelHelper.getReportDescription("ActiveTicketsInDate");
     */
    getReportDescription: function (reportName) {
        var report = CMDBuildUI.util.helper.ModelHelper.getReportFromName(reportName);
        return report ? report.getTranslatedDescription() : null;
    },

    /**
     * Returns Dashboard object description.
     *
     * @param {String} dashboarName Dashboard name.
     *
     * @returns {String} Dashboard description
     *
     * @example
     * var description = CMDBuildUI.util.helper.ModelHelper.getDashboardDescription("ServiceDeskActiveTickets");
     */
    getDashboardDescription: function (dashboarName) {
        var dashboard = CMDBuildUI.util.helper.ModelHelper.getDashboardFromName(dashboarName);
        return dashboard ? dashboard.getTranslatedDescription() : null;
    },

    /**
     * Returns object description. Usefull when type is not known.
     *
     * @param {String} typeName Class/Process/View/... name.
     * @param {String} [type] Object type. One of CMDBuildUI.util.helper.ModelHelper.objecttypes properties. If null it will be calculated.
     *
     * @returns {String} Object description.
     *
     * @example
     * var description = CMDBuildUI.util.helper.ModelHelper.getObjectDescription("Employee");
     */
    getObjectDescription: function (typeName, type) {
        var type = type || CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(typeName);
        switch (type) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                return CMDBuildUI.util.helper.ModelHelper.getClassDescription(typeName);
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                return CMDBuildUI.util.helper.ModelHelper.getProcessDescription(typeName);
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel:
                return CMDBuildUI.util.helper.ModelHelper.getDMSModelDescription(typeName);
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.view:
                return CMDBuildUI.util.helper.ModelHelper.getViewDescription(typeName);
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.report:
                return CMDBuildUI.util.helper.ModelHelper.getReportDescription(typeName);
        }
    },

    /**
     * Returns the object type description.
     *
     * @param {String} type The object type. One of `CMDBuildUI.util.helper.ModelHelper.objecttypes`.
     * @returns {String}
     */
    getTypeDescription: function (type) {
        switch (type) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                return CMDBuildUI.locales.Locales.menu.classes;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                return CMDBuildUI.locales.Locales.menu.processes;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.view:
                return CMDBuildUI.locales.Locales.menu.views;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.report:
                return CMDBuildUI.locales.Locales.menu.reports;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.dashboard:
                return CMDBuildUI.locales.Locales.menu.dashboards;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.custompage:
                return CMDBuildUI.locales.Locales.menu.custompages;
        }
    },

    bulkComboSettingsData: function () {
        return [
            {
                value: true,
                label: CMDBuildUI.locales.Locales.administration.systemconfig.enabled
            },
            {
                value: false,
                label: CMDBuildUI.locales.Locales.administration.systemconfig.disabled
            }
        ];
    },

    bulkComboPermissionsData: function () {
        return [
            {
                value: 'null',
                label: CMDBuildUI.locales.Locales.administration.common.labels.default
            },
            {
                value: 'true',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.enabled
            },
            {
                value: 'false',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.disabled
            }
        ];
    },

    /**
     * Returns the field name for url field.
     *
     * @param {String} fieldname
     * @returns {String}
     */
    getUrlFieldNameForLinkField: function (fieldname) {
        return Ext.String.format('_{0}_url', fieldname);
    },

    /**
     * Returns the field name for label field.
     *
     * @param {String} fieldname
     * @returns {String}
     */
    getLabelFieldNameForLinkField: function (fieldname) {
        return Ext.String.format('_{0}_label', fieldname);
    },

    /**
     * Returns the search filters for a given target.
     *
     * @param {String} target
     * @returns
     */
    getSearchFiltersByTarget: function (target) {
        var searchFiltersStore = Ext.getStore('searchfilters.Searchfilters');
        if (!target || !searchFiltersStore) {
            return [];
        }
        return Ext.Array.filter(searchFiltersStore.getRange(), function (filterRecord) {
            return filterRecord.get('target') === target;
        });
    }
});
