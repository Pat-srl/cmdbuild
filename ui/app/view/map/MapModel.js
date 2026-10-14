Ext.define('CMDBuildUI.view.map.MapModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.map-map',

    statics: {
        lastZoom: null, // Keep zoom value when a class is changed
        lastPosition: [0, 0] // Keep postion when a class is changed
    },

    data: {
        initialized: false,
        mapCreated: undefined,
        animationTarget: [0, 0] // coordinates
    },

    formulas: {
        updateFilter: {
            bind: {
                layerStore: '{layerStore}',
                zoom: '{zoom}',
                objectTypeName: '{objectTypeName}',
                drawmode: '{drawmode}',
                mapCreated: '{mapCreated}'
            },
            get: function (data) {
                const zoom = data.zoom;
                if (
                    data.layerStore &&
                    Ext.isNumber(zoom) &&
                    data.objectTypeName &&
                    !data.drawmode &&
                    data.mapCreated &&
                    CMDBuildUI.util.Navigation.getCurrentContext().objectType !==
                        CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent
                ) {
                    data.layerStore.setFilters([
                        {
                            id: CMDBuildUI.view.map.Map.filterZoomMaxId,
                            property: 'zoomMax',
                            operator: '>=',
                            value: zoom
                        },
                        {
                            id: CMDBuildUI.view.map.Map.filterZoomMinId,
                            property: 'zoomMin',
                            operator: '<=',
                            value: zoom
                        },
                        {
                            id: CMDBuildUI.view.map.Map.filterVisibility,
                            filterFn: function (item) {
                                return data.objectTypeName in item.get('visibility');
                            },
                            scope: this
                        }
                    ]);
                }
            }
        },

        updateOlSelectInteractionSelected: {
            bind: {
                mapCreated: '{mapCreated}',
                theObject: '{theObject}',
                layerStore: '{layerStore}',
                initialized: '{initialized}',
                drawmode: '{drawmode}'
            },
            get: function (data) {
                const view = this.getView();
                const controller = view.getController();
                const olMap = view.getOlMap();
                const geometryDelete = this.get('settingsMap.geometryDelete');
                const multiselection = this.get('contextmenu.multiselection.enabled');

                if (data.initialized) {
                    if (data.theObject && data.mapCreated && data.layerStore && !data.drawmode && !geometryDelete) {
                        data.theObject.getGeoValues().then(
                            function (geovalues) {
                                if (!view.destroyed) {
                                    const geoval = [];
                                    Ext.Array.forEach(
                                        geovalues.getRange(),
                                        function (geovalue, index) {
                                            const geovalueklassname = geovalue.get('_owner_type'),
                                                geovalueklass =
                                                    CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                                        geovalueklassname
                                                    ),
                                                geovaluehierarchy = geovalueklass.getHierarchy();

                                            if (geovalueklassname == this.get('objectTypeName')) {
                                                //the geovalue is on a geoattribute defined in the same card class
                                                geoval.push(geovalue);
                                            } else if (
                                                Ext.Array.contains(geovaluehierarchy, this.get('objectTypeName'))
                                            ) {
                                                //the geovalue is on a geoattribute defined in a subClass of the card
                                                geoval.push(geovalue);
                                            }
                                        },
                                        this
                                    );

                                    const idObject = multiselection ? Ext.num(data.theObject.getId()) : null;
                                    controller.ol_interaction_select_select(
                                        olMap,
                                        !Ext.isEmpty(geoval) ? geoval : [],
                                        true, //silent
                                        true, //animate
                                        idObject
                                    );
                                }
                            },
                            Ext.emptyFn,
                            Ext.emptyFn,
                            this
                        );
                    } else {
                        controller.ol_interaction_select_select(olMap, [], true);
                    }
                }
            }
        }
    }
});
