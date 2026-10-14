Ext.define('CMDBuildUI.model.dashboards.Component', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        componentOrientation: {
            horizontal: 'horizontal',
            vertical: 'vertical'
        },
        getComponentOrientations: function () {
            return [
                {
                    value: CMDBuildUI.model.dashboards.Component.componentOrientation.horizontal,
                    label: CMDBuildUI.locales.Locales.administration.dashboards.horizontal
                },
                {
                    value: CMDBuildUI.model.dashboards.Component.componentOrientation.vertical,
                    label: CMDBuildUI.locales.Locales.administration.dashboards.vertical
                }
            ];
        },
        dataSourceTypes: {
            funktion: 'function',
            klass: 'class',
            view: 'view',
            text: 'text',
            process: 'process'
        },
        getDataSourceTypes: function () {
            return [
                {
                    value: CMDBuildUI.model.dashboards.Component.dataSourceTypes.funktion,
                    label: CMDBuildUI.locales.Locales.administration.common.labels.funktion
                },
                {
                    value: CMDBuildUI.model.dashboards.Component.dataSourceTypes.klass,
                    label: CMDBuildUI.locales.Locales.administration.schedules.klass
                },
                {
                    value: CMDBuildUI.model.dashboards.Component.dataSourceTypes.view,
                    label: CMDBuildUI.locales.Locales.administration.localizations.view
                }
            ];
        },
        componentType: {
            bar: 'bar',
            pareto: 'pareto',
            gauge: 'gauge',
            line: 'line',
            pie: 'pie',
            table: 'table',
            text: 'text',
            list: 'list',
            tile: 'tile',
            calendar: 'calendar',
            search: 'search'
        },
        getComponentTypes: function (type, excludedTypes) {
            var types = [
                {
                    label: CMDBuildUI.locales.Locales.administration.dashboards.bar,
                    value: CMDBuildUI.model.dashboards.Component.componentType.bar,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('chart-bar', 'regular')
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.dashboards.pareto,
                    value: CMDBuildUI.model.dashboards.Component.componentType.pareto,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('chart-bar', 'regular')
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.dashboards.pie,
                    value: CMDBuildUI.model.dashboards.Component.componentType.pie,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('chart-pie', 'solid')
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.dashboards.gauge,
                    value: CMDBuildUI.model.dashboards.Component.componentType.gauge,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('tachometer-alt', 'solid')
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.dashboards.line,
                    value: CMDBuildUI.model.dashboards.Component.componentType.line,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('chart-line', 'solid')
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.dashboards.table,
                    value: CMDBuildUI.model.dashboards.Component.componentType.table,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid')
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.dashboards.text,
                    value: CMDBuildUI.model.dashboards.Component.componentType.text,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('file-alt', 'regular')
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.dashboards.list,
                    value: CMDBuildUI.model.dashboards.Component.componentType.list,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('list', 'solid')
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.dashboards.tilegroup,
                    value: CMDBuildUI.model.dashboards.Component.componentType.tile,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('th-large', 'solid')
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.dashboards.calendar.title,
                    value: CMDBuildUI.model.dashboards.Component.componentType.calendar,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('calendar', 'solid')
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.dashboards.search,
                    value: CMDBuildUI.model.dashboards.Component.componentType.search,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('search', 'solid')
                }
            ];

            if (excludedTypes && excludedTypes.length) {
                types = Ext.Array.filter(types, function (_type) {
                    return !Ext.Array.contains(excludedTypes, _type.value);
                });
            }

            if (!type) {
                return types;
            }
            return Ext.Array.findBy(types, function (_type) {
                return _type.value === type;
            });
        }
    },

    fields: [
        {
            name: 'name',
            description: CMDBuildUI.locales.Locales.administration.common.labels.name,
            localized: {
                description: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
            },
            type: 'string',
            critical: true,
            showInGrid: true
        },
        {
            name: 'description',
            description: CMDBuildUI.locales.Locales.administration.common.labels.description,
            localized: {
                description: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
            },
            type: 'string',
            critical: true,
            showInGrid: true
        },
        {
            name: 'active',
            description: CMDBuildUI.locales.Locales.administration.common.labels.active,
            localized: {
                description: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
            },
            type: 'boolean',
            critical: true,
            defaultValue: true,
            showInGrid: true
        },
        {
            name: 'dataSourceType', // used in table type
            type: 'string',
            critical: true,
            defaultValue: 'function' // function|class
        },
        {
            name: 'dataSourceFilter', // used in table type
            type: 'string',
            critical: true,
            defaultValue: ''
        },
        {
            name: 'dataSourceLimit', // used in table type
            type: 'number',
            critical: true,
            defaultValue: 0 // 0 == infinite
        },
        {
            name: 'dataSourceName',
            type: 'string',
            critical: true
        },
        {
            name: 'type',
            type: 'string', // enum componentType
            critical: true
        },
        {
            name: 'categoryAxisField',
            type: 'string',
            critical: true
        },
        {
            name: 'categoryAxisLabel',
            type: 'string',
            critical: true
        },
        {
            name: 'valueAxisLabel',
            type: 'string',
            critical: true
        },
        {
            name: 'valueAxisFields',
            type: 'auto',
            critical: true,
            defaultValue: [] // string
        },
        {
            name: 'componentOrientation',
            type: 'string',
            critical: true,
            defaultValue: 'horizontal'
        },
        {
            name: 'autoLoad',
            type: 'boolean',
            critical: true
        },
        {
            name: 'legend',
            type: 'boolean',
            critical: true
        },
        {
            name: 'height',
            type: 'string',
            critical: true,
            defaultValue: null
        },
        {
            name: 'maximum',
            type: 'number',
            critical: true
        },
        {
            name: 'minimum',
            type: 'number',
            critical: true
        },
        {
            name: 'steps',
            type: 'number',
            critical: true
        },
        {
            name: 'dataSourceParameters',
            type: 'auto',
            critical: true
        },
        {
            name: 'fgcolor',
            type: 'string',
            critical: true
        },
        {
            name: 'bgcolor',
            type: 'string',
            critical: true
        },
        {
            name: 'singleSeriesField',
            type: 'string',
            critical: true
        },
        {
            name: 'labelField',
            type: 'string',
            critical: true
        },
        {
            name: 'linkToClass',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: 'seeAllElements',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: 'titleField',
            type: 'string',
            critical: true
        },
        {
            name: 'descriptionField',
            type: 'string',
            critical: true
        },
        {
            name: 'metaField',
            type: 'string',
            critical: true
        },
        {
            name: 'tagField',
            type: 'string',
            critical: true
        },
        {
            name: 'listSearchFilter',
            type: 'string',
            critical: true
        },
        {
            name: 'emptyListMessage',
            type: 'string',
            critical: true,
            defaultValue: ''
        },
        {
            name: 'tiles',
            type: 'auto',
            critical: true,
            defaultValue: []
        },
        { name: 'allowViewDay', type: 'boolean', critical: true, defaultValue: true },
        { name: 'allowViewWeek', type: 'boolean', critical: true, defaultValue: true },
        { name: 'allowViewMonth', type: 'boolean', critical: true, defaultValue: true },
        { name: 'allowViewAgenda', type: 'boolean', critical: true, defaultValue: true },
        { name: 'allowPrevNextYear', type: 'boolean', critical: true, defaultValue: true },
        { name: 'defaultView', type: 'string', critical: true, defaultValue: 'month' },
        { name: 'eventDatesUseTime', type: 'boolean', critical: true, defaultValue: false },
        { name: 'eventStartDateAttribute', type: 'string', critical: true },
        { name: 'eventEndDateAttribute', type: 'string', critical: true },
        { name: 'eventTitleAttribute', type: 'string', critical: true },
        { name: 'eventTypeAttribute', type: 'string', critical: true },
        { name: 'eventTypeLookup', type: 'string', critical: true },
        { name: 'eventLookupValueField', type: 'string', critical: true, defaultValue: '_id' },
        {
            name: 'logo',
            type: 'string',
            critical: true,
            defaultValue: null
        }
    ],

    hasMany: [
        {
            model: 'CMDBuildUI.model.dashboards.DataSourceParameter',
            name: 'dataSourceParameters',
            associationKey: 'dataSourceParameters'
        }
    ],

    proxy: {
        type: 'memory'
    },

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
     * Get source attributes
     *
     * @return {Ext.Deferred}
     */
    getSourceAttributes: function () {
        var deferred = new Ext.Deferred();
        // get source parameters
        var sourcetype = this.get('dataSourceType'),
            sourcename = this.get('dataSourceName');
        var sourceobj,
            isloadingfn = false;

        // if is view
        if (sourcetype === CMDBuildUI.model.dashboards.Component.dataSourceTypes.view) {
            var v = CMDBuildUI.util.helper.ModelHelper.getViewFromName(sourcename);
            if (v.get('sourceClassName')) {
                sourceobj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(v.get('sourceClassName'));
            } else {
                sourcetype = CMDBuildUI.model.dashboards.Component.dataSourceTypes.funktion;
                sourcename = v.get('sourceFunction');
            }
        } else if (sourcetype === CMDBuildUI.model.dashboards.Component.dataSourceTypes.klass && sourcename) {
            sourceobj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(sourcename);
        }

        if (!sourceobj && sourcetype === CMDBuildUI.model.dashboards.Component.dataSourceTypes.funktion && sourcename) {
            var fnstore = Ext.getStore('Functions');
            sourceobj = fnstore.findRecord('name', sourcename);
            // load single function if not already loaded
            if (!sourceobj) {
                isloadingfn = true;
                fnstore.loadSingleFunction(sourcename).then(
                    function (fn) {
                        fn.getAttributes().then(function (attributes) {
                            deferred.resolve(attributes);
                        });
                    },
                    function () {
                        deferred.reject();
                    }
                );
            }
        }

        // if source object is not empty and is not loading the function
        // get source attributes
        if (sourceobj) {
            sourceobj.getAttributes().then(function (attributes) {
                deferred.resolve(attributes);
            });
        } else if (!isloadingfn) {
            deferred.reject();
        }
        return deferred.promise;
    },

    /**
     * @return {Object}
     */
    getSourceStoreParameters: function () {
        var sourcetype = this.get('dataSourceType'),
            sourcename = this.get('dataSourceName'),
            sourcefilter,
            proxyurl;

        if (sourcetype === CMDBuildUI.model.dashboards.Component.dataSourceTypes.view) {
            var v = CMDBuildUI.util.helper.ModelHelper.getViewFromName(sourcename);
            if (v.get('sourceClassName')) {
                sourcename = v.get('sourceClassName');
                sourcetype = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(sourcename);
                sourcefilter = v.get('filter');
            } else {
                sourcename = v.get('sourceFunction');
                sourcetype = CMDBuildUI.model.dashboards.Component.dataSourceTypes.funktion;
            }
        } else if (this.get('dataSourceFilter')) {
            sourcefilter = this.get('dataSourceFilter');
        }

        if (sourcetype === CMDBuildUI.model.dashboards.Component.dataSourceTypes.klass && sourcename) {
            sourcetype = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(sourcename);
        }

        switch (sourcetype) {
            case CMDBuildUI.model.dashboards.Component.dataSourceTypes.klass:
                proxyurl = CMDBuildUI.util.api.Classes.getCardsUrl(sourcename);
                break;
            case CMDBuildUI.model.dashboards.Component.dataSourceTypes.process:
                proxyurl = CMDBuildUI.util.api.Processes.getInstancesUrl(sourcename);
                break;
            case CMDBuildUI.model.dashboards.Component.dataSourceTypes.funktion:
                proxyurl = CMDBuildUI.util.api.Functions.getOutputsUrlByFunctionName(sourcename);
                break;
        }
        return {
            proxyurl: proxyurl,
            filter: sourcefilter
        };
    },

    copyForClone: function () {
        var newRecord = this.clone();
        newRecord.set('_id', CMDBuildUI.util.Utilities.generateUUID());
        newRecord.set('name', Ext.String.format('{0}_{1}', newRecord.get('name'), 'copy'));
        newRecord.set('description', Ext.String.format('{0}_{1}', newRecord.get('description'), 'copy'));
        newRecord.set('dataSourceParameters', this.getAssociatedData().dataSourceParameters);
        newRecord._dataSourceParameters = this._dataSourceParameters;
        newRecord.crudState = 'C';
        newRecord.phantom = true;
        newRecord.isClone = true;
        delete newRecord.crudStateWas;
        delete newRecord.previousValues;
        delete newRecord.modified;

        return newRecord;
    },

    getTilesStore: function () {
        if (!this._tilesStore) {
            this._tilesStore = Ext.create('Ext.data.Store', {
                fields: [
                    '_id',
                    'label',
                    'icon_font',
                    'valueField',
                    'unit',
                    'fgcolor',
                    'bgcolor',
                    'colortype',
                    'fgcolorField',
                    'bgcolorField',
                    'goto',
                    'scope'
                ],
                data: this.get('tiles') || [],
                proxy: { type: 'memory' }
            });
        }
        return this._tilesStore;
    }
});
