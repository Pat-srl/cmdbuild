Ext.define('CMDBuildUI.view.history.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.history-grid',

    data: {
        storedata: {
            autoload: false,
            historyTypes: 'cards'
        },
        historyfilter: {
            cards: true,
            system: false,
            references: false,
            relations: false
        },
        onlyModeActive: false,
        disableButtonPrint: false,
        attributesCalled: [],
        printAttributes: [],
        selectedAttributes: [],
        numSelectedAttributes: 0,
        initialValueViewMode: null,
        calendarView: false
    },

    formulas: {

        /**
         * Update store data
         */
        updateStoreData: {
            bind: {
                objecttype: '{objectType}',
                objecttypename: '{objectTypeName}',
                objectid: '{objectId}'
            },
            get: function (data) {
                var vm = this;
                if (data.objecttype && data.objecttypename && data.objectid) {
                    CMDBuildUI.util.helper.ModelHelper.getModel(data.objecttype, data.objecttypename).then(function (model) {
                        // set store model name
                        var historymodel = CMDBuildUI.util.helper.ModelHelper.getHistoryModel(data.objecttype, data.objecttypename);

                        vm.set("storedata.modelname", historymodel.getName());
                        // set store proxy url
                        vm.set("storedata.proxyurl", Ext.String.format("{0}/{1}/history", model.getProxy().getUrl(), data.objectid));
                        // set store auto load
                        vm.set("storedata.autoload", true);
                    });
                    // set isProcess variable
                    vm.set("isProcess", data.objecttype === CMDBuildUI.util.helper.ModelHelper.objecttypes.process);
                }
            }
        },

        /**
         * Load data or set the history filter on store
         */
        objectFilterManager: {
            bind: {
                cardsFilter: '{historyfilter.cards}',
                systemFilter: '{historyfilter.system}',
                referencesFilter: '{historyfilter.references}',
                relationsFilter: '{historyfilter.relations}',
                store: '{objects}',
                onlyModeActive: '{onlyModeActive}'
            },
            get: function (data) {
                this.getView().getController().clearAllWidgetRows();
                if (!data.cardsFilter && !data.systemFilter && !data.referencesFilter && !data.relationsFilter) {
                    this.set('storedata.historyTypes', 'cards');
                    return;
                }

                var historyFilter = [],
                    historyTypes = [];

                Ext.Object.getAllKeys(this.get('historyfilter')).forEach(function (filter) {
                    if (data[Ext.String.format('{0}Filter', filter)]) {
                        Ext.Array.push(historyTypes, filter);
                        if (filter === "cards" && data.onlyModeActive) {
                            filter = filter.slice(0, -1);
                        }
                        Ext.Array.push(historyFilter, filter);
                    }
                });

                this.set('storedata.historyTypes', historyTypes.join(','));
                if (!data.onlyModeActive) {
                    if (this.get('objects')) {
                        this.get('objects').load();
                    }
                } else {
                    // Added filter to the objectsFields store because for each loaded attribute it already contains both card and system data
                    this.get("objectsFields").addFilter({
                        id: 'historyFilter',
                        property: '_historyType',
                        operator: 'in',
                        value: historyFilter
                    });
                }
            }
        },

        /**
         * Return the data for combobox view Mode
         */
        viewModesData: {
            get: function () {
                this.set("initialValueViewMode", CMDBuildUI.view.history.Grid.full);
                return [{
                    value: CMDBuildUI.view.history.Grid.full,
                    description: CMDBuildUI.locales.Locales.history.fulldata
                }, {
                    value: CMDBuildUI.view.history.Grid.only,
                    description: CMDBuildUI.locales.Locales.history.onlychanges
                }];
            }
        },

        /**
         * Return the text for selected attributes button
         */
        textAttributes: {
            bind: '{numSelectedAttributes}',
            get: function (numSelectedAttributes) {
                this.set("disableButtonPrint", this.get("onlyModeActive") && numSelectedAttributes === 0);
                return Ext.String.format(CMDBuildUI.locales.Locales.history.attributes, numSelectedAttributes);
            }
        }

    },

    stores: {
        objects: {
            type: 'history',
            model: '{storedata.modelname}',
            proxy: {
                url: '{storedata.proxyurl}',
                type: 'baseproxy',
                extraParams: {
                    types: '{storedata.historyTypes}'
                }
            },
            autoLoad: '{storedata.autoload}'
        },

        objectsFields: {
            storeId: 'history-fields',
            proxy: 'memory',
            data: [],
            sorters: {
                property: '_beginDate',
                direction: 'DESC'
            }
        },

        viewModes: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{viewModesData}'
        },

        listAttributes: {
            proxy: {
                type: "memory"
            },
            data: [],
            sorters: ['label'],
            grouper: {
                property: 'group'
            }
        }
    }

});