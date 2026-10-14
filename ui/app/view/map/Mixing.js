Ext.define('CMDBuildUI.view.map.Mixing', {
    mixinId: 'map-mixing',

    /**
     * @param {} olMap
     * @param {String} ollayername
     * @returns {ol.layer.Base} the layer with the passed name. undefined if not foud
     */
    getOlLayer: function (olMap, ollayername) {
        const olLayersCollection = olMap ? olMap.getLayers().getArray() : [];

        return Ext.Array.findBy(olLayersCollection, function (item, index, array) {
            return item.get('name') == ollayername;
        });
    },

    /**
     *
     * @param {*} olMap
     * @param {*} ollayername
     */
    getOlLayers: function (olMap, ollayername) {
        const olLayersCollection = olMap ? olMap.getLayers().getArray() : [],
            results = [];

        Ext.Array.forEach(olLayersCollection, function (item, index, array) {
            if (item.get('name') == ollayername) {
                results.push(item);
            }
        });

        return results;
    },

    /**
     *
     * @param {ol.layer.Base} olLayer
     * @returns {ol.layer.Vector} // vector source
     */
    getOlLayerSource: function (olLayer) {
        let vectorSource = olLayer.getSource();
        if (vectorSource instanceof ol.source.Cluster) {
            // cluster needs another getSource
            vectorSource = vectorSource.getSource();
        }

        return vectorSource;
    },

    /**
     * Get a possible cluster of feature and return features
     * Needed if cluster is enabled
     * @param {ol.Feature} feature
     * @return {ol.Feature[] || ol.Feature}
     */
    getOlFeatureFromFeatures: function (olFeatures) {
        if (olFeatures.get('features')) {
            // the feature for a cluster is an array
            return olFeatures.get('features'); // cluster
        } else {
            return olFeatures; // single
        }
    },

    /**
     * Get first feature
     * Needed if cluster is enabled
     * @param {ol.Feature [] || ol.Feature} olFeatures
     * @returns {ol.Feature}
     */
    getFirstOlFeature: function (olFeatures) {
        const features = this.getOlFeatureFromFeatures(olFeatures);
        return Array.isArray(features) ? features[0] : features;
    },

    /**
     * Get info window content
     *
     * @returns {Ext.Deferred} A promise with the info window content.
     */
    getInfoWindowContent: function (geovalue) {
        const deferred = new Ext.Deferred();

        if (geovalue._infowindow !== undefined) {
            deferred.resolve(geovalue._infowindow);
        } else {
            Ext.Ajax.request({
                url: CMDBuildUI.util.api.Map.getGeoValueDetailsUrl(
                    CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                    geovalue.get('_owner_type'),
                    geovalue.get('_owner_id'),
                    geovalue.get('_attr')
                ),
                method: 'GET',
                success: function (response, option) {
                    const data = JSON.parse(response.responseText).data;
                    if (data.infoWindowEnabled) {
                        geovalue._infowindow = '';
                        if (data.infoWindowImage) {
                            geovalue._infowindow = Ext.String.format(
                                '<div><img src="{0}/classes/{1}/cards/{2}/attachments/{3}/image" style="display:none" onload="this.style.display=\'initial\'"/></div>',
                                CMDBuildUI.util.Config.baseUrl,
                                data._owner_type,
                                data._owner_id,
                                data.infoWindowImage
                            );
                        }
                        geovalue._infowindow += Ext.String.format(
                            '<div class="{0}selectable">{1}</div>',
                            Ext.baseCSSPrefix,
                            data.infoWindowContent
                        );
                    } else {
                        geovalue._infowindow = null;
                    }
                    deferred.resolve(geovalue._infowindow);
                }
            });
        }
        return deferred.promise;
    },

    /**
     *
     * @param {*} olMap
     * @param {*} ollayername
     * @param {*} Id
     */
    getOlGeoFeatureById: function (olMap, ollayername, Id) {
        const olLayersCollection = olMap ? olMap.getLayers().getArray() : [];

        return Ext.Array.findBy(olLayersCollection, function (item, index, array) {
            return item.get('id') == Id;
        });
    },

    /**
     *
     * @param {*} olMap
     * @param {CMDBuildUI.model.gis.GeoAttribute} geoattribute
     *
     * @param {*} olMap
     * @param {CMDBuildUI.model.gis.GeoAttribute} geoattribute
     */
    addOlLayer: function (olMap, geoattribute) {
        const me = this;
        const vm = this.getViewModel();
        const controller = me.getController();
        const olLayer = controller.createOlLayer(geoattribute);
        const theThematism = vm.get('theThematism');

        if (olLayer.get('owner_type') == vm.get('objectTypeName')) {
            olLayer.on('change:visible', function (Event) {
                me.getController().ol_interaction_select_refresh(olMap);
            });
        }

        //add the layer
        this.getOlMap().addLayer(olLayer);
        this.clearExtent();

        //adds the thematism if selected
        if (theThematism && geoattribute.get('ollayername') == theThematism.get('ollayername')) {
            this.applyThematism(olMap, geoattribute, theThematism);
        }
    },

    /**
     *
     */
    clearExtent: function () {
        const loadedExtentsRtree = this.getLoadedExtentsRtree();
        loadedExtentsRtree.clear();
    },

    /**
     *
     * @param {*} olMap
     * @param {*} ollayername
     * @param {*} visible
     */
    setVisibleOlLayer: function (olMap, ollayername, visible) {
        const olLayers = this.getOlLayers(olMap, ollayername);

        Ext.Array.forEach(
            olLayers,
            function (item, index, array) {
                item.setCheckedLayer(visible);
            },
            this
        );
    },

    /**
     *
     * @param {*} olMap
     * @param {*} ollayername
     * @param {*} geolayers
     * @param {*} replace
     */
    addOlGeoFeatures: function (olMap, ollayername, geolayers, replace) {
        Ext.Array.forEach(
            geolayers,
            function (geolayer, array, index) {
                var foundOlGeoFeature = Ext.Array.findBy(
                    olMap.getLayers().getArray(),
                    function (olLayers, array, index) {
                        return olLayers.get('id') == geolayer.getId();
                    },
                    this
                );

                if (replace) {
                    olMap.removeLayer(foundOlGeoFeature);
                    foundOlGeoFeature = null;
                }

                if (!foundOlGeoFeature) {
                    const geoFeature = geolayer.getOlGeoFeature();
                    olMap.addLayer(geoFeature);
                }
            },
            this
        );
    },

    /**
     *
     * @param {*} olMap
     * @param {String} ollayername
     * @param {String} geovalue
     * @param {String} type drawmode || checked
     * @param {Boolean} value
     * @param {Boolean} force
     */
    setVisibleOlFeature: function (olMap, ollayername, geovalue, type, value, force) {
        const olLayer = this.getOlLayer(olMap, ollayername);
        const me = this;

        if (olLayer) {
            const source = me.getOlLayerSource(olLayer);
            var olFeature = source.getFeatureById(geovalue.getId());

            if (!olFeature) {
                olFeature = Ext.Array.findBy(source.getFeatures(), function (item, index) {
                    return item.get('_owner_id') == geovalue.get('_owner_id');
                });
            }

            if (olFeature) {
                switch (type) {
                    case 'checked':
                        olFeature.setChecked(value);
                        break;
                    case 'drawmode':
                        olFeature.setDrawmode(value);
                        break;
                }
                me.getController().ol_interaction_select_refresh(olMap, force);
            }
        }
    },

    /**
     *
     * @param {*} olMap
     * @param {*} ollayername
     * @param {CMDBuildUI.model.gis.GeoLayer} geovalue
     * @param {*} type
     * @param {*} value
     */
    setVisibleOlGeoFeature: function (olMap, ollayername, geovalue, type, value) {
        const olGeoFeature = this.getOlGeoFeatureById(olMap, ollayername, geovalue.getId());
        if (olGeoFeature) {
            switch (type) {
                case CMDBuildUI.model.gis.GeoLayer.checked:
                    olGeoFeature.setChecked(value);
                    break;
            }
        }
    },

    /**
     *
     * @param {ol.Map} olMap
     * @param {CMDBuildUI.model.thematisms.Thematism} thematism
     */
    applyThematism: function (olMap, geoattribute, thematism) {
        //move in model;
        geoattribute.set('thematism', thematism);

        const ollayername = geoattribute.get('ollayername');
        const olLayer = this.getOlLayer(olMap, ollayername);

        if (olLayer) {
            this.getController().ol_interaction_select_refresh(olMap);
            olLayer.changed();
        }
    },

    /**
     *
     * @param {Object} configgetOl_interaction_select
     */
    modify: function (config) {
        config = Ext.merge(
            {
                record: null,
                listeners: {
                    modifystart: {
                        fn: Ext.emptyFn,
                        scope: this
                    },
                    modifyend: {
                        fn: Ext.emptyFn,
                        scope: this
                    }
                }
            },
            config
        );

        //compose the layer name
        const drawModifyLayerName = Ext.String.format(
            '{0}_{1}_{2}',
            CMDBuildUI.map.util.Util.DRAWMODIFYLAYERNAME,
            config.record.get('_attr'),
            config.record.get('_owner_type')
        );

        //gets the draw modify layer
        var drawModifyLayer = this.getDrawModifyLayer(drawModifyLayerName);
        if (!drawModifyLayer) {
            drawModifyLayer = this.createDrawModifyLayer(drawModifyLayerName, config.record);
        }

        //hide the feature in the layer
        const me = this;
        const olMap = me.getOlMap();
        const ollayername = config.record.get('ollayername');
        me.setVisibleOlFeature(olMap, ollayername, config.record, 'drawmode', true, true);

        //compose the interaction name
        const modifyInteractionName = Ext.String.format(
            '{0}_{1}_{2}',
            CMDBuildUI.map.util.Util.MODIFYINTERACTIONNAME,
            config.record.get('_attr'),
            config.record.get('_owner_type')
        );

        let modifyInteraction = me.getModifyInteraction(modifyInteractionName);
        if (!modifyInteraction) {
            modifyInteraction = me.createModifyInteraction(modifyInteractionName, {
                source: me.getOlLayerSource(drawModifyLayer)
            });
        }

        //modify start listener
        modifyInteraction.once('modifystart', function () {
            config.listeners.modifystart.fn.call(config.listeners.modifystart.scope);
        });

        //modifyend listener
        modifyInteraction.on('modifyend', function (event) {
            const points = CMDBuildUI.map.util.Util.olCoordinatesToObject(
                event.features.item(0).getGeometry().getType(),
                event.features.item(0).getGeometry().getCoordinates()
            );
            config.listeners.modifyend.fn.call(config.listeners.modifyend.scope, points);
        });
    },

    /**
     *
     * @param {Object} config
     */
    draw: function (config) {
        const me = this;
        config = Ext.merge(
            {
                record: null,
                listeners: {
                    drawend: {
                        fn: Ext.emptyFn,
                        scope: this
                    },
                    drawstart: {
                        fn: Ext.emptyFn,
                        scope: this
                    }
                }
            },
            config
        );

        //compose the layer name
        const drawModifyLayerName = Ext.String.format(
            '{0}_{1}_{2}',
            CMDBuildUI.map.util.Util.DRAWMODIFYLAYERNAME,
            config.record.get('_attr'),
            config.record.get('_owner_type')
        );

        //gets the draw modify layer
        let drawModifyLayer = me.getDrawModifyLayer(drawModifyLayerName);
        if (!drawModifyLayer) {
            drawModifyLayer = me.createDrawModifyLayer(drawModifyLayerName, config.record);
        }

        //draw interaction name
        const drawInteractionName = Ext.String.format(
            '{0}_{1}_{2}',
            CMDBuildUI.map.util.Util.DRAWINTERACTIONNAME,
            config.record.get('_attr'),
            config.record.get('_owner_type')
        );

        let drawInteraction = me.getDrawInteraction(drawInteractionName);
        if (!drawInteraction) {
            drawInteraction = me.createDrawInteraction(drawInteractionName, {
                source: me.getOlLayerSource(drawModifyLayer),
                type: me.format(config.record.get('_type'))
            });
        }

        drawInteraction.setActive(true);
        //drawstart listener
        drawInteraction.once('drawstart', function (event) {
            config.listeners.drawstart.fn.call(config.listeners.drawstart.scope);
        });

        //drawend listener
        drawInteraction.once('drawend', function (event) {
            drawInteraction.setActive(false);
            const points = CMDBuildUI.map.util.Util.olCoordinatesToObject(
                event.feature.getGeometry().getType(),
                event.feature.getGeometry().getCoordinates()
            );
            config.listeners.drawend.fn.call(config.listeners.drawstart.scope, points);
        });
    },

    /**
     *
     * @param {Object} config
     */
    clear: function (config) {
        Ext.merge(
            {
                record: null,
                listeners: {
                    clear: {
                        fn: Ext.emptyFn,
                        scope: this
                    }
                }
            },
            config
        );

        //compose the layer name
        const drawModifyLayerName = Ext.String.format(
            '{0}_{1}_{2}',
            CMDBuildUI.map.util.Util.DRAWMODIFYLAYERNAME,
            config.record.get('_attr'),
            config.record.get('_owner_type')
        );

        //gets the draw modify layer
        var drawModifyLayer = this.getDrawModifyLayer(drawModifyLayerName);
        if (!drawModifyLayer) {
            drawModifyLayer = this.createDrawModifyLayer(drawModifyLayerName, config.record);
        }

        //hide the feature in the layer
        const me = this;
        const olMap = me.getOlMap();
        const ollayername = config.record.get('ollayername');
        const olLayerSource = me.getOlLayerSource(drawModifyLayer);

        me.setVisibleOlFeature(olMap, ollayername, config.record, 'drawmode', true);

        olLayerSource.once('clear', function () {
            config.listeners.clear.fn.call(config.listeners.clear.scope);
        });

        olLayerSource.clear();
    },

    /**
     *
     * @param {Ext.data.Model} record
     *
     * @param {Ext.data.Model} record
     */
    clean: function (record) {
        const attr = record.get('_attr'),
            owner_type = record.get('_owner_type');

        //compose the interaction name
        const modifyInteractionName = Ext.String.format(
            '{0}_{1}_{2}',
            CMDBuildUI.map.util.Util.MODIFYINTERACTIONNAME,
            attr,
            owner_type
        );
        this.removeModifyinteraction(modifyInteractionName);

        //compose the draw interaction name
        const drawInteractionName = Ext.String.format(
            '{0}_{1}_{2}',
            CMDBuildUI.map.util.Util.DRAWINTERACTIONNAME,
            attr,
            owner_type
        );
        this.removeDrawInteraction(drawInteractionName);

        //compose the layer name
        const drawModifyLayerName = Ext.String.format(
            '{0}_{1}_{2}',
            CMDBuildUI.map.util.Util.DRAWMODIFYLAYERNAME,
            attr,
            owner_type
        );
        this.removeDrawModifyLayer(drawModifyLayerName);

        //hide the feature in the layer
        const olMap = this.getOlMap(),
            ollayername = record.get('ollayername');
        this.setVisibleOlFeature(olMap, ollayername, record, 'drawmode', false);
    },

    /**
     *
     * @param {Object} config
     */
    hideshow: function (config) {
        Ext.merge(
            {
                record: null,
                show: null,
                listeners: {
                    clear: {
                        fn: Ext.emptyFn,
                        scope: this
                    },
                    set: {
                        fn: Ext.emptyFn,
                        scope: this
                    }
                }
            },
            config
        );

        //hide the feature in the layer
        const me = this;
        const olMap = me.getOlMap();
        const ollayername = config.record.get('ollayername');
        var olGeoFeature = me.getOlGeoFeatureById(olMap, ollayername, config.record.getId());

        if (!olGeoFeature) {
            olGeoFeature = Ext.Array.findBy(
                olMap.getLayers().getArray(),
                function (layer) {
                    if (layer.type === 'TILE') {
                        return (
                            me.getOlLayerSource(layer).key_.indexOf(config.record.get('_owner_id').toString()) !== -1
                        );
                    }
                },
                me
            );
        }

        olGeoFeature.setChecked(config.show);
        !config.show
            ? config.listeners.clear.fn.call(config.listeners.clear.scope)
            : config.listeners.set.fn.call(config.listeners.set.scope);
    },

    /**
     *
     * @param {String} name the name for the layer
     * @returns {ol.layer.Vector}
     */
    getDrawModifyLayer: function (name) {
        //get the draw layer
        const drawModifyLayer = Ext.Array.findBy(
            this.getOlMap().getLayers().getArray(),
            function (layer, index, array) {
                if (layer.get('name') == name) {
                    return true;
                }
            }
        );

        return drawModifyLayer;
    },

    /**
     *
     * @param {String} name the name for the layer
     * @param {CMDBuildUI.model.gis.GeoValue} geoValue
     * @returns {ol.layer.Vector}
     */
    createDrawModifyLayer: function (name, geoValue) {
        const drawSource = new ol.source.Vector();
        const controller = this.getController();

        if (geoValue.hasValues()) {
            drawSource.addFeature(controller.createOlFeature(null, geoValue));
        }

        const drawModifyLayer = new ol.layer.Vector({
            source: drawSource,
            style: new ol.style.Style({
                image: new ol.style.Circle({
                    fill: new ol.style.Fill({
                        color: 'rgba(255,165,0,1)' //'orange',//[0, 255, 0, 0.5],
                    }),
                    stroke: new ol.style.Stroke({
                        width: 1,
                        color: 'white'
                    }),
                    radius: 10
                }),
                fill: new ol.style.Fill({
                    color: 'rgba(255,165,0,0.5)' //[255, 165, 0, 0.7], //'orange'
                }),
                stroke: new ol.style.Stroke({
                    color: 'white',
                    width: 2
                })
            }),
            zIndex: 5555
        });

        drawModifyLayer.set('name', name);
        this.getOlMap().addLayer(drawModifyLayer);

        return drawModifyLayer;
    },

    /**
     *
     * @param {String} name
     */
    removeDrawModifyLayer: function (name) {
        const drawModifyLayer = this.getDrawModifyLayer(name);
        if (drawModifyLayer) {
            this.getOlMap().removeLayer(drawModifyLayer);
        }
    },

    /**
     *
     * @param {String} name  The name of the draw interaction
     * @returns {ol.interaction.Draw}
     */
    getDrawInteraction: function (name) {
        //get the draw interaction
        const drawInteraction = Ext.Array.findBy(
            this.getOlMap().getInteractions().getArray(),
            function (interaction, index, array) {
                if (interaction.get('name') == name) {
                    return true;
                }
            }
        );

        return drawInteraction;
    },

    /**
     * @param {String} name
     * @param {Object} config
     * @returns {ol.interaction.Draw}
     */
    createDrawInteraction: function (name, config) {
        //creates the interaction object
        const drawInteraction = new ol.interaction.Draw({
            source: config.source,
            type: config.type // style: style //HACK: change drawInteraction style
        });
        drawInteraction.set('name', name);

        //adds the interaction to the map
        this.getOlMap().addInteraction(drawInteraction);

        return drawInteraction;
    },

    /**
     *
     * @param {String} name
     */
    removeDrawInteraction: function (name) {
        const drawInteraction = this.getDrawInteraction(name);
        if (drawInteraction) {
            this.getOlMap().removeInteraction(drawInteraction);
        }
    },

    /**
     *
     * @param {String} name  The name of the modify interaction
     * @returns {ol.interaction.Modify}
     */
    getModifyInteraction: function (name) {
        const modifyInteraction = Ext.Array.findBy(
            this.getOlMap().getInteractions().getArray(),
            function (interaction, index, array) {
                if (interaction.get('name') == name) {
                    return true;
                }
            }
        );

        return modifyInteraction;
    },

    /**
     *
     * @param {*} name
     * @param {*} config
     * @returns {ol.interaction.Modify}
     */
    createModifyInteraction: function (name, config) {
        //creates the interaction object
        const modifyInteraction = new ol.interaction.Modify({
            source: config.source // style: olTmpStyle
        });
        modifyInteraction.set('name', name);

        //adds the interaction to the map
        this.getOlMap().addInteraction(modifyInteraction);

        return modifyInteraction;
    },

    /**
     *
     * @param {String} name
     */
    removeModifyinteraction: function (name) {
        const modifyInteraction = this.getModifyInteraction(name);
        if (modifyInteraction) {
            this.getOlMap().removeInteraction(modifyInteraction);
        }
    },

    /**
     * this function modifyes a string mantaining the same words but setting uppercase the first latter and lowercase the remaining
     * @param {String} inp the input string
     * @return {String} the modified string
     */
    format: function (type) {
        type = type.toLowerCase();

        switch (type) {
            case 'point':
                return 'Point';
            case 'linestring':
                return 'LineString';
            case 'polygon':
                return 'Polygon';
        }
    },

    /**
     *
     * @param {*} olMap
     * @returns
     *
     * @param {*} olMap
     * @returns
     */
    getOl_interaction_select: function (olMap) {
        const collection = olMap.getInteractions();
        for (let i = 0; i < collection.getLength(); i++) {
            const item = collection.item(i);
            if (item instanceof ol.interaction.Select) {
                return item;
            }
        }
    },

    /**
     *
     * @returns {Boolean}
     */
    isMultiSelectionEnabled: function () {
        return this.getListTab().getSelectionModel().getSelectionMode() === 'SIMPLE';
    },

    /**
     *
     * @returns {Ext.panel.Panel}
     */
    getMapContainerView: function () {
        return this.up('map-container');
    },

    /**
     *
     * @returns {Ext.grid.Panel}
     */
    getListTab: function () {
        return this.down('map-tab-cards-list');
    },

    /**
     *
     * @returns {Ext.tree.Panel}
     */
    getNavigationTreeTab: function () {
        return this.down('map-tab-cards-navigationtree');
    },

    /**
     *
     * @returns {Ext.panel.Panel}
     */
    getViewMap: function () {
        return this.down('map-map');
    },

    /**
     *
     * @returns {Ext.tab.Panel}
     */
    getMapTabPanel: function () {
        return this.down('map-tab-tabpanel');
    },

    /**
     *
     * @returns {Ext.panel.Panel}
     */
    getCardView: function () {
        return this.up('classes-cards-grid-container');
    },

    /**
     *
     * @returns {Ext.grid.Panel}
     */
    getCardGridView: function () {
        return this.getCardView().down('classes-cards-grid-grid');
    },

    /**
     * Modify the selection of the record on the map
     * @param {Ext.data.Model} record
     * @param {Ext.data.Model} record
     * @param {Boolean} select
     */
    modifySelection: function (record, select) {
        const selected = [];
        const me = this;
        const mapContainerView = me.getMapContainerView();
        const vm = mapContainerView.getViewModel();
        const viewMap = mapContainerView.getViewMap();
        const olMap = viewMap.getOlMap();
        const objectId = vm.get('objectId');
        const objectTypeName = vm.get('objectTypeName');
        const interaction = viewMap.getOl_interaction_select(olMap);

        if (select) {
            // search inside the layer the feature
            Ext.Array.forEach(olMap.getLayers().getArray(), function (layer) {
                const source = me.getOlLayerSource(layer);
                const features = source.getFeatures ? source.getFeatures() : [];
                Ext.Array.forEach(features, function (feature) {
                    if (feature.values_._geovalue.get('_owner_id') == objectId) {
                        selected.push(feature);
                    }
                });
            });
            if (!Ext.isEmpty(selected)) {
                interaction.dispatchEvent(new ol.interaction.Select.SelectEvent('select', selected, [], {}));
            } else {
                const geoAttributesStore = vm.get('geoAttributesStore');
                // find the geoAttribute
                const geoAttributeNotShowed = geoAttributesStore.getAt(
                    geoAttributesStore.findBy(function (geoattribute) {
                        return (
                            geoattribute.get('owner_type') == record.get('_type') &&
                            geoattribute.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.geometry
                        );
                    })
                );
                // request the postion of the geoattribute to move to map to the feature
                if (geoAttributeNotShowed) {
                    const advancedFilter = vm.get('advancedFilter');
                    const keepZoomAndPositionConfig = CMDBuildUI.util.helper.Configurations.get(
                        CMDBuildUI.model.Configuration.gis.keepZoomAndPosition
                    );
                    let filter = null;
                    if (advancedFilter) {
                        if (!Ext.isEmpty(advancedFilter)) {
                            // http://gitlab.example.com/cmdbuild/cmdbuild/issues/5003
                            // add config term
                            filter = advancedFilter.encode();
                        }
                    }

                    Ext.Ajax.request({
                        url: Ext.String.format(
                            '{0}/classes/_ANY/cards/_ANY/geovalues/center',
                            CMDBuildUI.util.Config.baseUrl
                        ),
                        method: 'GET',
                        params: {
                            attribute: geoAttributeNotShowed.getId(),
                            filter: filter,
                            forOwner: objectTypeName
                        }
                    }).then(function (response) {
                        if (viewMap) {
                            const responseText = JSON.parse(response.responseText);
                            if (responseText && responseText.found) {
                                const data = responseText.data;
                                viewMap.setMapCenter([data.x, data.y]);
                            }
                            if (keepZoomAndPositionConfig && CMDBuildUI.view.map.MapModel.lastZoom) {
                                vm.set('zoom', CMDBuildUI.view.map.MapModel.lastZoom);
                            } else {
                                vm.set('zoom', geoAttributeNotShowed.get('zoomDef'));
                            }

                            vm.set('initialized', true);
                        }
                    });
                }
                me.selectRecordOnTab(record.get('_id'), true);
            }
        } else {
            interaction.dispatchEvent(
                new ol.interaction.Select.SelectEvent('select', selected, !select ? record : [], {})
            );
        }
    },

    /**
     * Manage the selection of the record
     * @param {Ext.data.Model} record
     */
    onSelectItem: function (record) {
        const vm = this.getMapContainerView().getViewModel();
        if (record.get('_id') == vm.get('objectId')) {
            this.modifySelection(record, true);
        } else {
            vm.set('objectId', Ext.num(record.get('_id')));
            if (record.get('_id')) {
                this.modifySelection(record, true);
            }
        }
    },

    privates: {
        /**
         * Remove values of infoWindow parameters
         */
        removeInfoWindow: function () {
            this.infoWindow._current = null;
            this.infoWindow._ownerId = null;
            this.infoWindow.setPosition(undefined);
        },

        /**
         * Calculate coordinates for geometry
         * @param {ol.geom.GeometryType} geometry
         * @returns {Array}
         */
        calculateGeometryCoordinates: function (geometry) {
            var coordinates;
            switch (geometry.getType()) {
                case 'LineString':
                    coordinates = geometry.getCoordinates()[geometry.getCoordinates().length / 2];
                    break;
                case 'Polygon':
                    coordinates = geometry.getInteriorPoint().getCoordinates();
                    break;
                case 'Point':
                    coordinates = geometry.getCoordinates();
            }
            return coordinates;
        },

        /**
         * Select or deselect record on navigation tree, list and grid
         *
         * @param {Number/String} id the id of the record
         * @param {Boolean} select used to select or deselect record
         */
        selectRecordOnTab: function (id, select) {
            const mapContainerView = this.getMapContainerView(),
                listTab = mapContainerView.getListTab(),
                listStore = listTab.getStore(),
                selectionModelList = listTab.getSelectionModel(),
                recordList = listStore.findRecord('_id', id),
                navTreeTab = mapContainerView.getNavigationTreeTab(),
                navTreeStore = navTreeTab.getStore(),
                selectionModelNavTree = navTreeTab.getSelectionModel(),
                recordNavTree = navTreeStore.findRecord('_id', id),
                cardGrid = this.getCardGridView(),
                cardGridStore = cardGrid.getStore(),
                selectionModelCardGrid = cardGrid.getSelectionModel(),
                recordGrid = cardGridStore.findRecord('_id', id);

            if (select) {
                selectionModelList.select(recordList, true, true);
                selectionModelNavTree.select(recordNavTree, true, true);
                selectionModelCardGrid.select(recordGrid, true, true);
            } else {
                selectionModelList.deselect(recordList, true);
                selectionModelNavTree.deselect(recordNavTree, true);
                selectionModelCardGrid.deselect(recordGrid, true);
            }
        }
    }
});
