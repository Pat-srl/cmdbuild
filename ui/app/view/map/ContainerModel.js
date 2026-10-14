Ext.define('CMDBuildUI.view.map.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.map-container',

    data: {
        objectId: null,
        theObject: null,
        settingsMap: {
            geometryDelete: false,
            selectAll: false
        },
        drawmode: false,
        gisNavigation: null,
        thematismId: null,
        theThematism: null,
        advancedfilter: null,
        zoom: 1,
        highlightselected: true,
        last_load: null
    },

    formulas: {
        theObjectType: {
            bind: {
                objectType: '{objectType}',
                objectTypeName: '{objectTypeName}'
            },
            get: function (data) {
                if (data.objectType && data.objectTypeName) {
                    switch (data.objectType) {
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                            return CMDBuildUI.util.helper.ModelHelper.getClassFromName(data.objectTypeName);
                    }
                }
            }
        },

        theObjectCalculation: {
            bind: {
                objectType: '{objectType}',
                objectTypeName: '{objectTypeName}',
                objectId: '{objectId}'
            },
            get: function (data) {
                if (data.objectType && data.objectTypeName) {
                    const me = this,
                        view = this.getView();

                    if (!view.mask.id) {
                        view.mask = CMDBuildUI.util.Utilities.addLoadMask(view);
                    }

                    if (data.objectId) {
                        switch (data.objectType) {
                            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                                const modelName = CMDBuildUI.util.helper.ModelHelper.getModelName(CMDBuildUI.util.helper.ModelHelper.objecttypes.klass, data.objectTypeName),
                                    theObject = Ext.create(modelName, {
                                        _id: data.objectId
                                    });

                                if (!Ext.isEmpty(this.get("last_load"))) {
                                    this.get("last_load").abort();
                                }

                                this.set("last_load", theObject.load({
                                    params: {
                                        includeModel: true,
                                        includeWidgets: false,
                                        includeStats: false
                                    },
                                    callback: function (record, operation, success) {
                                        if (success) {
                                            if (view && !view.destroyed) {
                                                me.set("theObject", theObject);
                                                theObject.getGeoValues();
                                            }
                                            me.set("last_load", null);
                                            CMDBuildUI.util.Utilities.removeLoadMask(view.mask);
                                        }
                                    }
                                }));
                                break;
                            default:
                                CMDBuildUI.util.Logger.log(
                                    Ext.String.format('Object Type not implemented: {0}', data.objectType),
                                    CMDBuildUI.util.Logger.levels.debug);
                                break;
                        }
                    } else {
                        this.set("theObject", null);
                        CMDBuildUI.util.Utilities.removeLoadMask(view.mask);
                    }
                }
            }
        },

        changeObjectId: {
            bind: '{objectId}',
            get: function (objectId) {
                const objectTypeName = this.get("objectTypeName");

                if (objectTypeName) {
                    const url = CMDBuildUI.util.Navigation.getClassBaseUrl(objectTypeName, objectId);
                    Ext.util.History.add(url);
                }
            }
        },

        updateTheThematism: {
            bind: {
                objectType: '{objectType}',
                objectTypeName: '{objectTypeName}',
                thematismId: '{thematismId}'
            },
            get: function (data) {
                if (data.objectType && data.objectTypeName) {
                    if (Ext.isEmpty(data.thematismId)) {
                        this.set("theThematism", null);
                    } else {
                        //get the class instance
                        const theKlass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(data.objectTypeName),
                            view = this.getView();

                        //load the thematisms
                        theKlass.getThematisms().then(function (thematisms) {
                            if (view && !view.destroyed) {
                                //find the target thematism
                                const theThematism = thematisms.getById(data.thematismId);

                                //sets the configuration in the view
                                theThematism.calculateResults(function () {
                                    this.set("theThematism", theThematism);
                                }, this);
                            }
                        }, Ext.emptyFn, Ext.emptyFn, this);
                    }
                }
            }
        },

        updateHighlightselected: {
            bind: '{highlightselected}',
            get: function () {
                const view = this.getView().getViewMap(),
                    olMap = view.getOlMap();
                if (olMap) {
                    view.getController().ol_interaction_select_refresh(olMap);
                }
            }
        },

        updateZoom: {
            bind: '{zoom}',
            get: function (zoom) {
                const olMap = this.getView().getViewMap().getOlMap(),
                    minZoom = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.minZoom),
                    maxZoom = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.maxZoom);
                var zoomValue = minZoom;

                if (Ext.isNumber(zoom)) {
                    zoomValue = Math.max(minZoom, zoom);
                    zoomValue = Math.min(maxZoom, zoomValue);
                }
                this.set("zoom", zoomValue);

                if (olMap && olMap.getView().getZoom() != zoomValue) {
                    olMap.getView().setZoom(zoomValue);
                }
            }
        }
    },

    stores: {
        shapeFeatureStore: {
            model: 'CMDBuildUI.model.gis.GeoLayer',
            proxy: {
                type: 'memory'
            },
            grouper: {
                property: 'ollayername'
            }
        },

        featureStore: {
            model: 'CMDBuildUI.model.gis.GeoValue',
            proxy: {
                type: 'memory'
            },
            data: [],
            grouper: {
                property: 'ollayername'
            }
        },

        attachNavTreeCollection: {
            proxy: {
                type: 'memory'
            },
            data: []
        },

        layerStore: {
            type: 'chained',
            source: '{geoAttributesStore}',
            filters: [],
            listeners: {
                filterchange: 'onFilterChange',
                update: 'onLayerStoreUpdate'
            }
        }
    }

});
