Ext.define('CMDBuildUI.view.administration.content.dashboards.common.FormModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dashboards-common-form',

    data: {
        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
        hideToolbar: false
    },

    formulas: {
        hiddenTypeProperty: {
            bind: '{theComponent.type}',
            get: function (type) {
                var defaults = {
                    legend: true,
                    labelField: true,
                    singleSeriesField: true,
                    maximum: true,
                    minimum: true,
                    steps: true,
                    fgcolor: true,
                    bgcolor: true,
                    componentOrientation: true,
                    valueAxisFields: true,
                    valueAxisLabel: true,
                    categoryAxisLabel: true,
                    categoryAxisField: true
                };

                switch (type) {
                    case 'pie':
                        return Ext.apply({}, { legend: false, labelField: false, singleSeriesField: false }, defaults);
                    case 'gauge':
                        return Ext.apply(
                            {},
                            {
                                legend: false,
                                labelField: true,
                                singleSeriesField: false,
                                maximum: false,
                                minimum: false,
                                steps: false,
                                fgcolor: false,
                                bgcolor: false
                            },
                            defaults
                        );
                    case 'bar':
                        return Ext.apply(
                            {},
                            {
                                legend: false,
                                componentOrientation: false,
                                valueAxisFields: false,
                                valueAxisLabel: false,
                                categoryAxisLabel: false,
                                categoryAxisField: false
                            },
                            defaults
                        );
                    case 'pareto':
                        return Ext.apply(
                            {},
                            {
                                legend: false,
                                componentOrientation: true,
                                valueAxisFields: false,
                                valueAxisLabel: false,
                                categoryAxisLabel: false,
                                categoryAxisField: false
                            },
                            defaults
                        );
                    case 'line':
                        return Ext.apply(
                            {},
                            {
                                legend: false,
                                valueAxisFields: false,
                                valueAxisLabel: false,
                                categoryAxisLabel: false,
                                categoryAxisField: false
                            },
                            defaults
                        );
                    case 'tile':
                        return Ext.apply(
                            {},
                            {
                                fgcolor: false,
                                bgcolor: false
                            },
                            defaults
                        );
                    case 'calendar':
                        return Ext.apply(
                            {},
                            {
                                fgcolor: false
                            },
                            defaults
                        );
                    case 'table':
                    case 'text':
                    case 'list':
                    default:
                        return defaults;
                }
            }
        },

        valueAxisFieldsMultiple: {
            bind: '{theComponent.type}',
            get: function (type) {
                return type !== 'pareto';
            }
        },

        valueAxisFieldsSingle: {
            bind: '{theComponent.valueAxisFields}',
            get: function (value) {
                return value && value.length ? value[0] : null;
            },
            set: function (value) {
                this.set('theComponent.valueAxisFields', value ? [value] : []);
            }
        },

        componentTypeDescription: {
            bind: '{theComponent.type}',
            get: function (type) {
                if (!type) return '';
                var typeConfig = CMDBuildUI.model.dashboards.Component.getComponentTypes(type);
                return typeConfig ? typeConfig.label : '';
            }
        },

        componentTypeFieldsetTitle: {
            bind: '{componentTypeDescription}',
            get: function (typeDescription) {
                return Ext.String.format(
                    CMDBuildUI.locales.Locales.administration.homepages.componentnamecomponenttypeproperties,
                    typeDescription || ''
                );
            }
        },

        titleManager: {
            bind: {
                description: '{theComponent.description}',
                action: '{action}',
                typeDescription: '{componentTypeDescription}'
            },
            get: function (data) {
                var description =
                    data.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add
                        ? CMDBuildUI.locales.Locales.administration.homepages.new
                        : data.description;

                var title = Ext.String.format('{0}{1}{2}', data.typeDescription, ' - ', description);

                this.set('theComponent._type_description', data.typeDescription);
                this.getParent().set('title', title);
            }
        },

        actions: {
            bind: '{action}',
            get: function (action) {
                var actionsHelper = CMDBuildUI.util.administration.helper.FormHelper.formActions;
                return {
                    add: action === actionsHelper.add,
                    edit: action === actionsHelper.edit,
                    view: action === actionsHelper.view
                };
            }
        },

        removeFilterBtnDisabled: {
            bind: {
                isView: '{actions.view}',
                filter: '{theComponent.dataSourceFilter}'
            },
            get: function (data) {
                return data.isView || !data.filter;
            }
        },

        listSearchFiltersFilteredData: {
            bind: '{theComponent.dataSourceName}',
            get: function (dataSourceName) {
                return CMDBuildUI.util.helper.ModelHelper.getSearchFiltersByTarget(dataSourceName);
            }
        },

        // --- visibility helpers based on component type ---
        isDataSourceVisible: {
            bind: '{theComponent.type}',
            get: function (type) {
                return type === 'table' || type === 'list';
            }
        },
        isChart: {
            bind: '{theComponent.type}',
            get: function (type) {
                var chartTypes = ['bar', 'gauge', 'line', 'pie'];
                return Ext.Array.contains(chartTypes, type);
            }
        },
        isPieChart: {
            bind: '{theComponent.type}',
            get: function (type) {
                return type === 'pie';
            }
        },
        isText: {
            bind: '{theComponent.type}',
            get: function (type) {
                return type === 'text';
            }
        },
        isTable: {
            bind: '{theComponent.type}',
            get: function (type) {
                return type === 'table';
            }
        },
        isList: {
            bind: '{theComponent.type}',
            get: function (type) {
                return type === 'list';
            }
        },
        isTile: {
            bind: '{theComponent.type}',
            get: function (type) {
                return type === 'tile';
            }
        },
        isCalendar: {
            bind: '{theComponent.type}',
            get: function (type) {
                return type === 'calendar';
            }
        },
        isSearch: {
            bind: '{theComponent.type}',
            get: function (type) {
                return type === 'search';
            }
        },

        // --- Static formulas to populate stores ---
        integerFieldTypes: function () {
            return CMDBuildUI.model.dashboards.DataSourceParameter.getIntegerFieldTypes();
        },
        stringFieldTypes: function () {
            return CMDBuildUI.model.dashboards.DataSourceParameter.getStringFieldTypes();
        },
        dataSourceTypes: function () {
            return CMDBuildUI.model.dashboards.Component.getDataSourceTypes();
        },
        componentOrientations: function () {
            return CMDBuildUI.model.dashboards.Component.getComponentOrientations();
        },
        getAllViews: function () {
            return Ext.getStore('views.Views').getRange();
        }
    },

    stores: {
        dataSourceTypeStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{dataSourceTypes}',
            pageSize: 0,
            proxy: { type: 'memory' }
        },
        listSearchFiltersFilteredStore: {
            model: 'CMDBuildUI.model.base.Filter',
            data: '{listSearchFiltersFilteredData}',
            pageSize: 0,
            proxy: { type: 'memory' }
        },
        getAllViewsStore: {
            model: 'CMDBuildUI.model.views.View',
            data: '{getAllViews}',
            pageSize: 0,
            proxy: { type: 'memory' },
            filters: [
                function (item) {
                    var viewTypes = CMDBuildUI.model.views.View.types;
                    return [viewTypes.sql, viewTypes.filter].indexOf(item.get('type')) > -1;
                }
            ]
        },
        functionsStore: {
            model: 'CMDBuildUI.model.Function',
            sorters: ['description'],
            pageSize: 0,
            autoLoad: true,
            remoteFilter: false
        },
        integerFieldTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{integerFieldTypes}',
            autoLoad: true,
            proxy: { type: 'memory' }
        },
        stringFieldTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{stringFieldTypes}',
            autoLoad: true,
            proxy: { type: 'memory' }
        },
        integerLookupTypesStore: {
            source: 'lookups.LookupTypes'
        },
        componentOrientationStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{componentOrientations}',
            autoLoad: true,
            proxy: { type: 'memory' }
        },

        // --- Filtered attribute stores (chained stores on functionAttributesStore) ---
        functionAttributesStore: {
            storeId: 'functionAttributesStore',
            model: 'CMDBuildUI.model.Attribute',
            proxy: { type: 'memory' }
        },
        functionAttributesStoreMeta: {
            source: '{functionAttributesStore}',
            filters: [
                {
                    filterFn: function (item) {
                        return ['Id', 'IdTenant'].indexOf(item.get('name')) === -1;
                    }
                }
            ]
        },
        functionAttributesStoreNumeric: {
            source: '{functionAttributesStore}',
            filters: [
                {
                    property: 'type',
                    operator: 'in',
                    value: [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal
                    ]
                }
            ]
        },
        functionAttributesStoreString: {
            source: '{functionAttributesStore}',
            filters: [
                {
                    property: 'type',
                    operator: '=',
                    value: 'string'
                }
            ]
        },
        functionAttributesStoreStringTextReferenceLookup: {
            source: '{functionAttributesStore}',
            filters: [
                {
                    property: 'type',
                    operator: 'in',
                    value: ['string', 'text', 'reference', 'lookup']
                }
            ]
        },
        functionAttributesStoreLookup: {
            source: '{functionAttributesStore}',
            filters: [
                {
                    property: 'type',
                    operator: '=',
                    value: 'lookup'
                }
            ]
        },
        functionAttributesStoreDate: {
            source: '{functionAttributesStore}',
            filters: [{ property: 'type', operator: 'in', value: ['date', 'dateTime'] }]
        },

        // Static stores for tile component
        tileColorTypeStore: {
            fields: ['value', 'label'],
            data: [
                ['static', CMDBuildUI.locales.Locales.administration.homepages.tiles.colortypecombo.static],
                ['dynamic', CMDBuildUI.locales.Locales.administration.homepages.tiles.colortypecombo.dynamic]
            ],
            proxy: { type: 'memory' }
        },
        tileScopeStore: {
            fields: ['value', 'label'],
            data: [
                ['all', CMDBuildUI.locales.Locales.administration.homepages.tiles.viewcombo.all],
                ['filtered', CMDBuildUI.locales.Locales.administration.homepages.tiles.viewcombo.filtered],
                ['selected', CMDBuildUI.locales.Locales.administration.homepages.tiles.viewcombo.selected]
            ],
            proxy: { type: 'memory' }
        },

        // Static stores for calendar component
        calendarViewStore: {
            fields: ['value', 'label'],
            data: [
                ['dayGridMonth', CMDBuildUI.locales.Locales.administration.homepages.calendar.month],
                ['listWeek', CMDBuildUI.locales.Locales.administration.homepages.calendar.agenda]
            ],
            proxy: { type: 'memory' }
        },
        calendarLookupValueFieldStore: {
            fields: ['value', 'label'],
            data: [
                ['_id', 'Id'],
                ['code', CMDBuildUI.locales.Locales.administration.homepages.calendar.code]
            ],
            proxy: { type: 'memory' }
        }
    }
});
