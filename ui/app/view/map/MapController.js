Ext.define('CMDBuildUI.view.map.MapController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-map',

    listen: {
        global: {
            addDataLayer: 'loadfeatures',
            refreshMap: 'onRefreshBtnClick'
        }
    },

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            labelvisibilitychange: 'onLabelVisibilityChange',
            mapcenterchange: 'onMapCenterChange',
            drawmodechange: 'onDrawmodeChange',
            resize: 'onResize',
            beforedestroy: 'onBeforeDestroy'
        }
    },

    onBeforeDestroy: function (view) {
        // Save the current zoom to statics
        CMDBuildUI.view.map.MapModel.lastZoom = view.getViewModel().get('zoom');
        CMDBuildUI.view.map.MapModel.lastPosition = view.getMapCenter();
    },
    /**
     *
     * @param {*} view
     * @param {*} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const me = this;
        const vm = this.getViewModel();
        let oldThematismValue = null;
        const objectTypeName = vm.get('objectTypeName');
        me.getView().labelSize = CMDBuildUI.util.helper.UserPreferences.get(
            CMDBuildUI.model.users.Preference.preferredMapLabelSize
        );

        vm.bind(
            {
                layerStore: '{layerStore}',
                featuresStore: '{featureStore}'
            },
            function (data) {
                if (data.layerStore && data.featuresStore) {
                    me.createOlMap();
                }
            }
        );

        vm.bind(
            {
                mapCreated: '{mapCreated}',
                theThematism: '{theThematism}',
                layerStore: '{layerStore}'
            },
            function (data) {
                if (data.layerStore && data.mapCreated) {
                    const layerStoreSource = view.getOlLayerSource(data.layerStore);
                    const olMap = view.getOlMap();
                    let layer, index;

                    if (data.theThematism) {
                        index = layerStoreSource.find('ollayername', data.theThematism.get('ollayername'));
                        layer = layerStoreSource.getAt(index);

                        view.applyThematism(olMap, layer, data.theThematism);
                    } else {
                        index = layerStoreSource.find('owner_type', objectTypeName, 0);
                        while (index != -1) {
                            layer = layerStoreSource.getAt(index);

                            if (layer.get('thematism')) {
                                view.applyThematism(olMap, layer);
                            }

                            index = layerStoreSource.find('owner_type', objectTypeName, ++index);
                        }
                    }
                }
            }
        );

        vm.bind(
            {
                mapCreated: '{mapCreated}',
                layerStore: '{layerStore}'
            },
            function (data) {
                if (data.mapCreated && data.layerStore) {
                    const layerStoreSource = data.layerStore.getSource();

                    let geoattributeindex = layerStoreSource.findBy(function (geoattribute) {
                        return (
                            geoattribute.get('owner_type') == objectTypeName &&
                            geoattribute.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.geometry
                        );
                    });

                    if (geoattributeindex == -1) {
                        geoattributeindex = layerStoreSource.findBy(function (geoattribute) {
                            return geoattribute.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.geometry;
                        });
                    }

                    const geoattribute = layerStoreSource.getAt(geoattributeindex);
                    if (geoattribute) {
                        vm.bind(
                            {
                                advancedFilter: '{cards.advancedFilter}'
                            },
                            function (data) {
                                const advancedFilter = data.advancedFilter;
                                let filter = null;
                                if (advancedFilter) {
                                    if (!Ext.isEmpty(advancedFilter)) {
                                        // http://gitlab.example.com/cmdbuild/cmdbuild/issues/5003
                                        // add config term
                                        filter = advancedFilter.encode();
                                    }
                                    advancedFilter.addListener('change', me.onAdvancedFilterChange, me);
                                }

                                vm.bind(
                                    '{objectId}',
                                    function (objectId) {
                                        // Keep zoom and position from the previous class
                                        const keepZoomAndPositionConfig = CMDBuildUI.util.helper.Configurations.get(
                                            CMDBuildUI.model.Configuration.gis.keepZoomAndPosition
                                        );
                                        if (
                                            Ext.isEmpty(objectId) &&
                                            CMDBuildUI.util.Navigation.getCurrentContext().objectType !==
                                                CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent
                                        ) {
                                            Ext.Ajax.request({
                                                url: Ext.String.format(
                                                    '{0}/classes/_ANY/cards/_ANY/geovalues/center',
                                                    CMDBuildUI.util.Config.baseUrl
                                                ),
                                                method: 'GET',
                                                params: {
                                                    attribute: geoattribute.getId(),
                                                    filter: filter,
                                                    forOwner: objectTypeName
                                                }
                                            }).then(function (response) {
                                                if (view) {
                                                    const responseText = JSON.parse(response.responseText);
                                                    if (responseText && responseText.found) {
                                                        const data = responseText.data;
                                                        view.setMapCenter([data.x, data.y]);
                                                    }
                                                    if (
                                                        keepZoomAndPositionConfig &&
                                                        CMDBuildUI.view.map.MapModel.lastZoom
                                                    ) {
                                                        vm.set('zoom', CMDBuildUI.view.map.MapModel.lastZoom);
                                                        view.setMapCenter(CMDBuildUI.view.map.MapModel.lastPosition);
                                                    } else {
                                                        vm.set('zoom', geoattribute.get('zoomDef'));
                                                    }
                                                    vm.set('initialized', true);
                                                }
                                            });
                                        } else {
                                            if (keepZoomAndPositionConfig && CMDBuildUI.view.map.MapModel.lastZoom) {
                                                vm.set('zoom', CMDBuildUI.view.map.MapModel.lastZoom);
                                                view.setMapCenter(CMDBuildUI.view.map.MapModel.lastPosition);
                                            } else {
                                                vm.set('zoom', geoattribute.get('zoomDef'));
                                            }
                                            vm.set('initialized', true);
                                        }
                                    },
                                    me,
                                    {
                                        single: true
                                    }
                                );
                            }
                        );
                    } else {
                        vm.set('initialized', true);
                    }
                }
            }
        );

        // disable cluster when a thematism is applied
        vm.bind(
            {
                theThematism: '{theThematism}'
            },
            function (data) {
                if (data.theThematism || oldThematismValue) {
                    let ollayername;
                    if (data.theThematism) {
                        ollayername = data.theThematism.get('ollayername');
                        oldThematismValue = data.theThematism;
                    } else {
                        ollayername = oldThematismValue.get('ollayername');
                    }
                    const olMap = view.getOlMap();

                    // callback for promise
                    function getFeatures(source) {
                        let deferred = new Ext.Deferred();
                        const features = source.getFeatures();
                        deferred.resolve(features);
                        return deferred.promise;
                    }

                    const oldSource = view.getOlLayerSource(view.getOlLayer(olMap, ollayername));

                    getFeatures(oldSource).then(function (features) {
                        const geoValues = [];
                        Ext.Array.forEach(features, function (feature) {
                            geoValues.push(feature.get('_geovalue'));
                        });
                        me.recreateOlLayer(ollayername);
                        if (view.getOlLayer(olMap, ollayername)) {
                            me.addOlFeatures(olMap, ollayername, geoValues, false);
                        }
                    });
                }
            }
        );
    },

    /**
     *
     * @param {CMDBuildUI.view.map.Map} view
     * @param {String} visibility
     */
    onLabelVisibilityChange: function (view, visibility) {
        this.delayloadfeatures(true);
    },

    /**
     * This function loads the features on the map
     * @param {*} view
     * @param {*} newValue
     * @param {*} oldValue
     */
    onMapCenterChange: function (view, newValue, oldValue) {
        this.delayloadfeatures();
    },

    /**
     *
     * @param {*} view
     * @param {*} newValue
     * @param {*} oldValue
     */
    onDrawmodeChange: function (view, newValue, oldValue) {
        this.delayloadfeatures(true);
    },

    /**
     *
     * @param {*} view
     * @param {*} newValue
     */
    onAdvancedFilterChange: function (newValue) {
        this.getViewModel().set('advancedfilter', newValue);
        this.delayloadfeatures(true);
    },

    /**
     *
     */
    onRefreshBtnClick: function () {
        const me = this;
        const view = me.getView();
        const layerStore = me.getViewModel().get('layerStore');
        const olMap = view.getOlMap();

        layerStore
            .getDataSource()
            .getRange()
            .forEach(function (item, index, array) {
                if (item.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.shape) {
                    //add olLayer, doesn't add if already exist
                    if (view.getOlLayer(olMap, item.get('ollayername'))) {
                        me.removeOlLayer(olMap, item.get('ollayername'));
                    }
                }
            });

        this.delayloadfeatures(true);
    },

    // ------ Layers ---------
    /**
     * @param {CMDBuildUI.model.gis.GeoAttribute} geoattribute
     * @returns {ol.layer.Vector || ol.layer.Tile} //complete with the returned types
     */
    createOlLayer: function (geoattribute) {
        const type = geoattribute.get('type');
        const thematism = geoattribute.get('thematism');
        // cluster enable only with points and without thematism
        const needCluster =
            geoattribute.get('subtype') === CMDBuildUI.model.gis.GeoAttribute.subtype.point &&
            geoattribute.getStyle().get('clusterEnable') &&
            !thematism;
        switch (type) {
            case CMDBuildUI.model.gis.GeoAttribute.type.geometry:
                return this.createOlLayerVector(needCluster, geoattribute);
            case CMDBuildUI.model.gis.GeoAttribute.type.geotiff:
                break;
        }

        CMDBuildUI.util.Logger.log(
            Ext.String.format('olMap layer still not implemented for type {0}', type),
            CMDBuildUI.util.Logger.levels.warn
        );
    },

    /**
     *
     * @param {boolean} needCluster
     * @param {CMDBuildUI.model.gis.GeoAttribute} geoattribute
     * @returns {ol.layer.Vector} the layer vector created
     */
    createOlLayerVector: function (needCluster, geoattribute) {
        const me = this;
        const vm = this.getViewModel();
        const view = this.getView();
        let olLayer;
        // feature container
        const olLayerSource = new ol.source.Vector({
            strategy: ol.loadingstrategy.bbox
        });
        if (needCluster) {
            // cluster source - don't add feature in this source
            const clusterSource = new ol.source.Cluster({
                source: olLayerSource,
                distance: geoattribute.getStyle().get('clusterDistance')
            });
            const layerStore = vm.get('layerStore');
            let styleCache = {}; // cache styles
            let lastThematism = null; // last thematism applied
            let lastLabelVisibility = null; // last labels
            olLayer = new ol.layer.Vector({
                source: clusterSource,
                minZoom: geoattribute.zoomMin,
                maxZoom: geoattribute.zoomMax,
                // style definition for cluster of all features
                style: function (olFeatures, resolution) {
                    // count how many features are visible
                    const visibileFeatures = olFeatures
                        .get('features')
                        .reduce((counter, singleFeature) => (singleFeature.isVisible() ? counter + 1 : counter), 0);
                    if (visibileFeatures > 0) {
                        const clusterSize = olFeatures.get('features') ? olFeatures.get('features').length : 1; // number of features in the cluster
                        let olFeature = olFeatures.get('features')[0];
                        if (olFeature.get('_selected')) {
                            // on selection active
                            if (olFeature.get('visibility').drawmode) {
                                view.removeInfoWindow();
                                return;
                            }
                            const visible = me.isVisibleOlFeature(olFeature);
                            const labelsVisibility = view.labelsVisibility;
                            const selected = olFeature.get('_selected');

                            olFeature = me.createOlFeature(
                                {
                                    showLabel:
                                        labelsVisibility === 'selected' ||
                                        labelsVisibility === 'allclass' ||
                                        labelsVisibility === 'all',
                                    labelSize: view.labelSize
                                },
                                olFeature.get('_geovalue')
                            );

                            if (!visible && !vm.get('contextmenu.multiselection.enabled')) {
                                if (
                                    me.infoWindow._current == olFeature.getId() ||
                                    me.infoWindow._ownerId != olFeature.get('_owner_id')
                                ) {
                                    view.removeInfoWindow();
                                }
                                const featurenotfound =
                                        CMDBuildUI.model.gis.GeoAttribute.getOlLayerVectorStyleRemoved(),
                                    removeFeature = Ext.Array.findBy(
                                        interaction.getFeatures().getArray(),
                                        function (item, index) {
                                            return olFeature.getId() == item.getId();
                                        }
                                    );

                                interaction.getFeatures().remove(removeFeature);
                                return [featurenotfound];
                            } else if (visible && layerStore) {
                                const highlightselected = geoattribute.get('thematism')
                                    ? vm.get('highlightselected')
                                    : true;
                                const basestyle = me.getOlLayerVectorStyle(
                                    olFeature,
                                    resolution,
                                    geoattribute,
                                    visibileFeatures
                                );

                                if (highlightselected && selected) {
                                    //if thematism is applied and highlightselected
                                    const selectionstyle = me.getOlLayerVectorStyleSelected(geoattribute);
                                    //return basestyle.push(selectionstyle);
                                    return Ext.Array.merge(basestyle, selectionstyle);
                                } else {
                                    const dark = !selected ? 1 : 0.5;
                                    //if thematism is not applied or the selected item must not be highlited
                                    basestyle[0].fill_ ? (basestyle[0].fill_.color_[3] = dark) : null;
                                    return basestyle;
                                }
                            }
                        }
                        // if there is a new thematism delete the cache and save the new thematism for update all styles
                        const currentThematism = geoattribute.get('thematism');
                        if (lastThematism !== currentThematism) {
                            styleCache = {};
                            lastThematism = currentThematism;
                        }

                        let style;
                        if (!currentThematism) {
                            // no thematism
                            // update the style if the visibility is changed
                            if (lastLabelVisibility !== view.labelsVisibility) {
                                if (
                                    visibileFeatures === 1 &&
                                    (view.labelsVisibility === 'all' || view.labelsVisibility === 'allClass')
                                ) {
                                    olFeature = olFeature = me.createOlFeature(
                                        {
                                            showLabel: true,
                                            labelSize: view.labelSize
                                        },
                                        olFeature.get('_geovalue')
                                    );
                                }
                                lastLabelVisibility = view.labelsVisibility;
                            } else {
                                // or use the cache
                                style = styleCache[clusterSize]; // use the cache only without thematism
                            }
                            if (!style) {
                                // create the style for clustered features
                                style = me.getOlLayerVectorStyle(olFeature, resolution, geoattribute, visibileFeatures);
                                styleCache[clusterSize] = style;
                            }
                        } else {
                            // with thematism
                            style = me.getOlLayerVectorStyle(olFeature, resolution, geoattribute, visibileFeatures);
                        }
                        return style;
                    } else {
                        // hide the feature
                        return null;
                    }
                },
                zIndex: 1000 - geoattribute.get('index')
            });
        } else {
            olLayer = new ol.layer.Vector({
                source: olLayerSource,
                style: function (olFeature, resolution) {
                    if (olFeature.get('_selected')) {
                        // replace OlFeature
                        olFeature = me.createOlFeature(
                            { showLabel: true, labelSize: view.labelSize },
                            olFeature.get('_geovalue')
                        );
                    }
                    return me.getOlLayerVectorStyle(olFeature, resolution, geoattribute);
                },
                zIndex: 1000 - geoattribute.get('index')
            });
        }

        olLayer.setCheckedLayer = function (checked) {
            this.setVisible(checked);
        }.bind(olLayer);

        olLayer.setCheckedLayer(geoattribute.get('checked'));
        olLayer.set('name', geoattribute.get('ollayername'));
        olLayer.set('text', geoattribute.get('text'));
        olLayer.set('type', geoattribute.get('type'));
        olLayer.set('owner_type', geoattribute.get('owner_type'));
        olLayer.set('geoattribute_id', geoattribute.get('_id'));

        return olLayer;
    },

    /**
     *
     * @param {string} ollayername
     * @returns {void}
     */
    recreateOlLayer(ollayername) {
        const view = this.getView();
        const olMap = view.getOlMap();
        const oldLayer = view.getOlLayer(olMap, ollayername);
        const geoAttribute = this.getViewModel().get('layerStore').getData().getByKey(oldLayer.get('geoattribute_id'));
        this.removeOlLayer(olMap, ollayername);
        view.addOlLayer(olMap, geoAttribute);
    },

    /**
     *
     * @param {ol.Map} olMap
     * @param {string} ollayername
     */
    removeOlLayer: function (olMap, ollayername) {
        const me = this;
        const view = me.getView();
        const infoWindow = view.infoWindow;
        const olLayers = view.getOlLayers(olMap, ollayername);

        Ext.Array.forEach(
            olLayers,
            function (olLayer, index, array) {
                const features = view.getOlLayerSource(olLayer).getFeatures
                    ? view.getOlLayerSource(olLayer).getFeatures()
                    : [];
                Ext.Array.forEach(features, function (feature, ind, arr) {
                    if (feature.getId() == infoWindow._current) {
                        view.removeInfoWindow();
                    }
                });
                olMap.removeLayer(olLayer);
            },
            this
        );
    },

    /**
     * @param {ol.Feature} olFeature
     * @param {Float} resolution shape style call with resolution
     * @param {CMDBuildUI.model.gis.GeoAttribute} geoattribute
     * @param {Integer} clusterSize  number over the icon
     * @returns {[ol.style.Style]} the complete calculated style for the geoattribute
     */
    getOlLayerVectorStyle: function (olFeature, resolution, geoAttribute, clusterSize = 0) {
        const me = this;
        const view = me.getView();
        const style = [];
        const subtype = geoAttribute.get('subtype');
        const thematism = geoAttribute.get('thematism');

        if (!(thematism instanceof CMDBuildUI.model.thematisms.Thematism)) {
            switch (subtype) {
                case CMDBuildUI.model.gis.GeoAttribute.subtype.point:
                    if (olFeature.get('hasBim')) {
                        //adds the bim style
                        style.push(me.getOlLayerVectorBimStyle(geoAttribute));
                        if (geoAttribute.get('_img')) {
                            //if has bim and icon
                            style.push(
                                me.getOlLayerVectorBaseStyle(
                                    geoAttribute,
                                    olFeature.get('label'),
                                    olFeature.get('labelSize')
                                )
                            );
                        }
                    } else {
                        //adds the Base style, defined in geoAttribute
                        if (clusterSize > 1) {
                            style.push(
                                me.getOlLayerVectorBaseStyle(
                                    geoAttribute,
                                    clusterSize.toString(),
                                    olFeature.get('labelSize')
                                )
                            );
                        } else {
                            style.push(
                                me.getOlLayerVectorBaseStyle(
                                    geoAttribute,
                                    olFeature.get('label'),
                                    olFeature.get('labelSize')
                                )
                            );
                        }
                    }
                    break;
                case CMDBuildUI.model.gis.GeoAttribute.subtype.polygon:
                case CMDBuildUI.model.gis.GeoAttribute.subtype.linestring:
                case CMDBuildUI.model.gis.GeoAttribute.subtype.shape:
                    style.push(
                        me.getOlLayerVectorBaseStyle(geoAttribute, olFeature.get('label'), olFeature.get('labelSize'))
                    );
                    break;
            }
        } else {
            //if there is an applied thematism
            const defaultStyle = geoAttribute.getStyle();
            if (olFeature) {
                const firstFeature = view.getFirstOlFeature(olFeature);
                const ownerId = firstFeature.get('_owner_id'); // extract owner id from the first feature
                const result = Ext.Array.findBy(
                    thematism.get('result') || [],
                    function (result) {
                        return result.owner_id == ownerId;
                    },
                    geoAttribute
                );
                if (result) {
                    const resultstyle = result.geostyle;
                    const text =
                        clusterSize > 1
                            ? me.getOlLayerText(clusterSize.toString(), firstFeature.get('labelSize'))
                            : me.getOlLayerText(firstFeature.get('label'), firstFeature.get('labelSize'));
                    switch (subtype) {
                        case CMDBuildUI.model.gis.GeoAttribute.subtype.point:
                            //point thematism
                            style.push(
                                new ol.style.Style({
                                    image: new ol.style.Circle({
                                        fill: resultstyle.getOlFill(),
                                        stroke: defaultStyle.getOlStroke(),
                                        radius: geoAttribute.get('_img')
                                            ? defaultStyle.get('pointRadius') / 2
                                            : defaultStyle.get('pointRadius')
                                    }),
                                    text: text
                                })
                            );
                            break;
                        case CMDBuildUI.model.gis.GeoAttribute.subtype.polygon:
                            //polygon thematism
                            style.push(
                                new ol.style.Style({
                                    fill: resultstyle.getOlFill(),
                                    stroke: defaultStyle.getOlStroke(),
                                    text: text
                                })
                            );
                            break;
                        case CMDBuildUI.model.gis.GeoAttribute.subtype.linestring:
                            //linestring thematism
                            style.push(
                                new ol.style.Style({
                                    stroke: defaultStyle.getOlStroke({
                                        color: resultstyle.getFillColor()
                                    }),
                                    text: text
                                })
                            );
                            break;
                    }
                }
            }
        }
        return style;
    },

    /**
     * @param {CMDBuildUI.model.gis.GeoAttribute} geoattribute
     * @param {*} label
     * @param {*} labelSize
     * @returns
     */
    getOlLayerVectorBaseStyle: function (geoattribute, label, labelSize) {
        const style = geoattribute.getStyle();
        const fill = style.getOlFill();
        const stroke = style.getOlStroke();
        const pointradius = style.get('pointRadius');
        //should define subtype cases
        const _img = geoattribute.get('_img');
        let image, scale;

        if (_img) {
            const coef = _img.width / _img.height;

            if (coef > 1) {
                scale = (pointradius * 2) / _img.width;
            } else {
                scale = (pointradius * 2) / _img.height;
            }

            image = new ol.style.Icon({
                src: _img.src,
                scale: scale
            });
        } else {
            image = new ol.style.Circle({
                fill: fill,
                stroke: stroke,
                radius: pointradius
            });
        }

        const text = this.getOlLayerText(label, labelSize, _img, scale);

        return new ol.style.Style({
            stroke: stroke,
            fill: fill,
            image: image,
            text: text
        });
    },

    /**
     * @param {CMDBuildUI.model.gis.GeoAttribute} geoattribute
     * @returns {[ol.style.Style]}
     * Bim style, red square added for icons images, rectangular point otherwise
     */
    getOlLayerVectorBimStyle: function (geoattribute) {
        const style = geoattribute.getStyle();
        const fill = style.getOlFill();
        const stroke = style.getOlStroke();

        return new ol.style.Style({
            image: new ol.style.RegularShape({
                stroke: geoattribute.get('_img')
                    ? new ol.style.Stroke({
                          width: 2,
                          color: 'red'
                      })
                    : stroke,
                fill: geoattribute.get('_icon') ? null : fill,
                radius: style.get('pointRadius'),
                points: 4,
                angle: Math.PI / 4
            })
        });
    },

    /**
     * @param {CMDBuildUI.model.gis.GeoAttribute} geoattribute
     * @returns {[ol.style.Style]}
     */
    getOlLayerVectorStyleSelected: function (geoattribute) {
        let selectionStyle;

        switch (geoattribute.get('subtype')) {
            case CMDBuildUI.model.gis.GeoAttribute.subtype.linestring:
                selectionStyle = new ol.style.Style({
                    stroke: geoattribute.getStyle().getOlStroke({
                        color: 'rgba(255,165,0,1)'
                    })
                });
                break;
            case CMDBuildUI.model.gis.GeoAttribute.subtype.point:
            case CMDBuildUI.model.gis.GeoAttribute.subtype.polygon:
                selectionStyle = new ol.style.Style({
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
                        color: 'rgba(255,165,0,1)',
                        width: 2
                    })
                });
                break;
        }
        return selectionStyle;
    },

    /**
     *
     * @param {String} label
     * @param {String} labelSize
     * @param {Object} img
     * @param {Number} scale
     * @returns {[ol.style.Text]} the style text to show in map elements
     */
    getOlLayerText: function (label, labelSize, img, scale) {
        labelSize = labelSize ? labelSize : '12px';
        if (label) {
            return new ol.style.Text({
                text: label,
                // font: '600 ' + labelSize + " 'Open Sans', helvetica, arial, verdana, sans-serif",
                textBaseline: 'top',
                fill: new ol.style.Fill({ color: '#005ca9' }),
                stroke: new ol.style.Stroke({
                    color: '#fff',
                    width: 3
                }),
                offsetY: img ? (img.height * scale) / 2 : 0,
                overflow: true
            });
        } else {
            return null;
        }
    },

    // ------- Features -------
    /**
     * This function loads the features on the map
     */
    delayloadfeatures: function (replace) {
        if (!this.getViewModel().get('drawmode')) {
            const t = this.getDelayedTask();
            var args;

            if (Ext.isEmpty(t.id)) {
                //there are not yet pending tasks
                args = [replace];
            } else {
                //there was a task before
                args = replace == true ? [replace] : undefined;
            }

            t.delay(this._delayvalue, this.loadfeatures, this, args);
        }
    },

    /**
     *
     * @param {Boolean} replace
     * @param {String} idLayer
     * @returns
     */
    loadfeatures: function (replace, idLayer) {
        const me = this;
        const view = this.getView();
        const vm = this.getViewModel();

        if (view) {
            const olMapView = view.getOlMap().getView();
            const geometryVisibleattributesId = [];
            const geometryOwningattributesId = [];
            const shapeattributesNames = [];
            const promises = [];
            const shapepromises = [];
            const objectTypeName = vm.get('objectTypeName');
            const geoserverEnabled = CMDBuildUI.util.helper.Configurations.get(
                CMDBuildUI.model.Configuration.gis.geoserverEnabled
            );
            const resolution = olMapView.getResolution();
            const projection = olMapView.getProjection();
            const vmGridContainer = view.getCardView().getViewModel();
            const importDWG = vmGridContainer.get('importDWG');

            // adds load mask
            let mask;
            let extent = olMapView.calculateExtent();

            if (!idLayer && !importDWG && !me.strategy(extent, resolution, projection, replace)) {
                return;
            }
            extent = me.enlargeExtent(extent);

            if (idLayer) {
                const geoattribute = vm.get('layerStore').getById(idLayer);
                if (geoattribute.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.shape && geoserverEnabled) {
                    shapeattributesNames.push(geoattribute.getId());
                } else {
                    geometryVisibleattributesId.push(idLayer);
                }
            } else {
                // gets the geoattributes ids
                vm.get('layerStore')
                    .getRange()
                    .forEach(function (geoattribute, index, array) {
                        if (geoattribute.get('checked')) {
                            if (geoattribute.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.geometry) {
                                const geoattributeklassname = geoattribute.get('owner_type');
                                const geoattributeklass =
                                    CMDBuildUI.util.helper.ModelHelper.getObjectFromName(geoattributeklassname);
                                const geoattributehierarchy = geoattributeklass.getHierarchy();

                                if (Ext.Array.contains(geoattributehierarchy, objectTypeName)) {
                                    //the geoatttribute is defined in the same class or in a subClass of objecttypename
                                    geometryOwningattributesId.push(geoattribute.getId());
                                } else {
                                    //attributes visible by the current objectTypeName
                                    geometryVisibleattributesId.push(geoattribute.getId());
                                }
                            } else if (
                                geoattribute.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.shape &&
                                geoserverEnabled
                            ) {
                                shapeattributesNames.push(geoattribute.getId());
                            }
                        }
                    });
            }

            // makes the appropriate calls for getting the geovalues
            if (!Ext.isEmpty(geometryVisibleattributesId) || !Ext.isEmpty(geometryOwningattributesId)) {
                // get advanced filter and query filter
                const advancedfilter = vm.get('advancedfilter');

                mask = CMDBuildUI.util.Utilities.addLoadMask(view);

                if (advancedfilter && !advancedfilter.isEmpty()) {
                    // get geovalues applying the filter. Only for geoattributes wich are owned by the class on which is applied the filter
                    if (!Ext.isEmpty(geometryOwningattributesId)) {
                        promises.push(
                            me.loadFeaturesStore(
                                {
                                    url: CMDBuildUI.util.api.Classes.getGeoValuesUrl('_ANY', '_ANY'),
                                    params: {
                                        attribute: geometryOwningattributesId,
                                        area: extent.toString(),
                                        limit: 0,
                                        attach_nav_tree: vm.get('gisNavigation') ? true : false,
                                        forOwner: objectTypeName
                                    }
                                },
                                advancedfilter
                            )
                        );
                    }

                    // doesn't need to apply the filter on those class
                    if (!Ext.isEmpty(geometryVisibleattributesId)) {
                        promises.push(
                            me.loadFeaturesStore({
                                url: CMDBuildUI.util.api.Classes.getGeoValuesUrl('_ANY', '_ANY'),
                                params: {
                                    attribute: geometryVisibleattributesId,
                                    area: extent.toString(),
                                    limit: 0,
                                    attach_nav_tree: vm.get('gisNavigation') ? true : false
                                }
                            })
                        );
                    }
                } else {
                    promises.push(
                        me.loadFeaturesStore({
                            url: CMDBuildUI.util.api.Classes.getGeoValuesUrl('_ANY', '_ANY'),
                            params: {
                                attribute: Ext.Array.merge(geometryOwningattributesId, geometryVisibleattributesId),
                                area: extent.toString(),
                                limit: 0,
                                attach_nav_tree: vm.get('gisNavigation') ? true : false
                            }
                        })
                    );
                }
            }

            // makes the appropriate calls for getting the geolayers values
            if (!Ext.isEmpty(shapeattributesNames)) {
                shapepromises.push(
                    me.loadShapeFeatureStore(
                        {
                            url: CMDBuildUI.util.api.Classes.getGeoLayersUrl('_ANY', '_ANY')
                        },
                        new CMDBuildUI.util.AdvancedFilter({
                            attributes: {
                                attribute_id: [
                                    {
                                        attribute: 'attribute_id',
                                        operator: 'IN',
                                        value: Ext.Array.map(shapeattributesNames, function (item, index, array) {
                                            return item;
                                        })
                                    }
                                ]
                            }
                        })
                    )
                );
            }

            if (importDWG) {
                replace = true;
                vmGridContainer.set('importDWG', false);
            }

            if (!Ext.isEmpty(promises)) {
                /**
                 * Manipulates the geovalues setting the right values for visibility, and bim Values.
                 */
                Ext.Deferred.all(promises).then(
                    function (results) {
                        results = Ext.Array.merge.apply(me, results);

                        me.geoValuesVisibility(results);
                        me.geoValuesBim(results);

                        // adds the record in memory store
                        vm.get('featureStore').loadRecords(results, {
                            addRecords: !replace
                        });

                        // adds the geovalues on the map
                        me.onFeaturesLoad(replace);

                        // removes load mask
                        CMDBuildUI.util.Utilities.removeLoadMask(mask);
                    },
                    function () {
                        CMDBuildUI.util.Utilities.removeLoadMask(mask);
                    }
                );
                vm.set('settingsMap.geometryDelete', false);
            }

            if (!Ext.isEmpty(shapepromises)) {
                Ext.Deferred.all(shapepromises).then(function (results) {
                    results = Ext.Array.merge.apply(me, results);

                    me.geoLayerVisibility(results);
                    me.getLayerLayerVisibility(results);

                    // adds the record in view memory store
                    vm.get('shapeFeatureStore').loadRecords(results, {
                        addRecords: !replace
                    });

                    // adds the geovalues on the map
                    me.onShapeFeaturesLoad(replace);
                });
            }
        }
    },

    /**
     *
     * @param {*} olMap
     * @param {String} ollayername
     * @param {[CMDBuildUI.model.gis.GeoValue]} geovalues
     * @param {Boolean} replace If true clears the layer before adding the features
     */
    addOlFeatures: function (olMap, ollayername, geovalues, replace) {
        const me = this;
        const olFeatures = [];
        const view = me.getView();
        const vm = me.getViewModel();
        const labelvisibility = view.labelsVisibility;
        const classObjectypeName = CMDBuildUI.util.helper.ModelHelper.getClassFromName(vm.get('objectTypeName'));
        let parentSuperClassSelected = null;

        geovalues.forEach(function (geovalue) {
            let showLabel = false;
            if (
                labelvisibility === 'all' ||
                (labelvisibility === 'allclass' && geovalue.get('_owner_type') === vm.get('objectTypeName'))
            ) {
                showLabel = true;
            } else if (classObjectypeName.get('prototype') === true && labelvisibility === 'allclass') {
                if (parentSuperClassSelected) {
                    showLabel = true;
                } else if (parentSuperClassSelected === null) {
                    const currentClass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(
                        geovalue.get('_owner_type')
                    );
                    if (Ext.Array.contains(currentClass.getHierarchy(), vm.get('objectTypeName'))) {
                        showLabel = true;
                        parentSuperClassSelected = true;
                    } else {
                        parentSuperClassSelected = false;
                    }
                }
            }

            olFeatures.push(
                me.createOlFeature(
                    {
                        showLabel: showLabel,
                        labelSize: view.labelSize
                    },
                    geovalue
                )
            );
        });

        const olLayer = view.getOlLayer(olMap, ollayername);
        if (olLayer) {
            const source = view.getOlLayerSource(olLayer);
            if (replace) {
                source.clear();
            }

            if (olFeatures.length > 0) {
                source.addFeatures(olFeatures);
            }

            if (replace || olLayer.get('owner_type') == vm.get('objectTypeName')) {
                me.ol_interaction_select_refresh(olMap);
            }
        }
    },

    /**
     *
     * @param {Object} config An object containing feature configuration
     * @param {Boolean} config.showLabel Show label
     * @param {Numeric} config.labelSize Set the size of the label
     * @param {CMDBuildUI.model.gis.GeoValue} geovalue
     * @returns
     */
    createOlFeature: function (config, geovalue) {
        config = config || {};
        let geometry;
        const points = [];
        switch (geovalue.get('_type')) {
            case 'point':
                geometry = new ol.geom.Point([geovalue.get('x'), geovalue.get('y')]);
                break;
            case 'linestring':
                if (geovalue.get('points')) {
                    geovalue.get('points').forEach(function (point) {
                        points.push([point.x, point.y]);
                    });
                }
                geometry = new ol.geom.LineString(points);
                break;
            case 'polygon':
                if (geovalue.get('points')) {
                    geovalue.get('points').forEach(function (point) {
                        points.push([point.x, point.y]);
                    });
                }
                geometry = new ol.geom.Polygon([points]);
                break;
        }

        const feature = new ol.Feature({
            geometry: geometry
        });

        feature.setId(geovalue.getId());
        feature.set('_owner_id', geovalue.get('_owner_id'));
        feature.set('ollayername', geovalue.get('ollayername'));
        if (config.showLabel) {
            feature.set('label', geovalue.get('_owner_description'));
        }
        if (config.labelSize) {
            feature.set('labelSize', config.labelSize + 'px');
        }
        feature.set('visibility', {
            drawmode: false,
            checked: true
        });

        feature.set('hasBim', geovalue.get('hasBim'));
        feature.set('projectId', geovalue.get('projectId'));
        feature.set('_geovalue', geovalue);
        // Keep track if the map already zoomed on the feature
        feature.set('_alreadyZoomed', false);

        feature.setDrawmode = function (drawmode) {
            this.get('visibility').drawmode = drawmode;
            this.updatVisibility();
        }.bind(feature);

        feature.setChecked = function (checked) {
            this.get('visibility').checked = checked;
            this.updatVisibility();
        }.bind(feature);

        feature.updatVisibility = function () {
            if (feature.isVisible()) {
                this.setStyle(undefined);
            } else {
                this.setStyle(new ol.style.Style());
            }
        }.bind(feature);

        feature.isVisible = function () {
            const visibility = this.get('visibility');
            return visibility.checked && !visibility.drawmode ? true : false;
        }.bind(feature);

        feature.setDrawmode(false);
        feature.setChecked(geovalue.get('checked'));
        return feature;
    },

    /**
     *
     * @param {*} olFeature
     * @returns
     */
    isVisibleOlFeature: function (olFeature) {
        const vm = this.getViewModel();
        const layerStore = vm.get('layerStore');
        const ollayername = olFeature.get('ollayername');
        const index = layerStore.find('ollayername', ollayername);

        if (index == -1) {
            //if the layer is filtered
            return false;
        } else {
            const featureStore = vm.get('featureStore'),
                item = layerStore.getAt(index);
            var featureIndex, feature, featurechek;

            if (item.get('checked')) {
                // if the layer is visible

                if (vm.get('advancedfilter') && !vm.get('advancedfilter').isEmpty()) {
                    //if there is a filter applied
                    featureIndex = featureStore.find('_id', olFeature.getId());

                    if (featureIndex == -1) {
                        //if the feature is filtered
                        return false;
                    } else {
                        //if the feature is not filtered
                        feature = featureStore.getAt(featureIndex);
                        featurechek = feature.get('checked');

                        if (featurechek || Ext.isEmpty(featurechek)) {
                            //if the feature is visible; set by the navigation tree;
                            return true;
                        } else {
                            //if the feature is not visible
                            return false;
                        }
                    }
                } else {
                    //if there is not a filter applied
                    featureIndex = featureStore.find('_id', olFeature.getId());

                    if (featureIndex != -1) {
                        //if the feature is in the store
                        feature = featureStore.getAt(featureIndex);
                        featurechek = feature.get('checked');

                        if (featurechek || Ext.isEmpty(featurechek)) {
                            //if the feature is visible; set by the navigation tree
                            return true;
                        } else {
                            //if the feature is not visible
                            return false;
                        }
                    } else {
                        //if the feature is not in the store
                        return false;
                    }
                }
            } else {
                //if the layer is not visible
                return false;
            }
        }
    },

    // ------- End features -------

    /**
     *
     * @param {*} extCmp
     * @param {*} width
     * @param {*} height
     */
    onResize: function (extCmp, width, height) {
        const view = this.getView(),
            map = view.getOlMap();
        if (map) {
            map.setSize([width, height]);
            this.delayloadfeatures();
        }
    },

    /**
     *
     * @param {ol.Map} olMap
     */
    setOl_Interaction_select: function (olMap) {
        const me = this;
        const view = me.getView();
        const olMapView = olMap.getView();
        const mapContainerView = view.getMapContainerView();
        const vm = mapContainerView.getViewModel();
        const oldInteration = view.getOl_interaction_select(olMap);
        const objecttypename = vm.get('objectTypeName');
        const layerStore = vm.get('layerStore');

        if (oldInteration) {
            olMap.removeInteraction(oldInteration);
        }

        const interaction = new ol.interaction.Select({
            multi: false,
            hitTolerance: 2,
            filter: function (featureOrFeatures, layer) {
                // featureOrFeatures can be a cluster of features
                let feature = view.getFirstOlFeature(featureOrFeatures);
                return feature.get('ollayername') === layer.get('name');
            },
            condition: function (Event) {
                if (ol.events.condition.singleClick(Event)) {
                    return !vm.get('drawmode');
                }
            },
            toggleCondition: function (Event) {
                if (ol.events.condition.singleClick(Event) && vm.get('contextmenu.multiselection.enabled')) {
                    return !vm.get('drawmode');
                }
            },
            layers: function (layer) {
                const owner_type = layer.get('owner_type');
                if (owner_type) {
                    const layerklass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(owner_type),
                        layerhierarchy = layerklass.getHierarchy();

                    if (Ext.Array.contains(layerhierarchy, objecttypename)) {
                        //the layer is defined in the same class or in a subClass of objecttypename
                        return true;
                    }
                }
                return false;
            },
            style: function (olFeature, resolution) {
                // --- works ONLY with cluster disabled ---
                if (olFeature.get('visibility').drawmode) {
                    view.removeInfoWindow();
                    return;
                }
                const visible = me.isVisibleOlFeature(olFeature);
                const labelsVisibility = view.labelsVisibility;
                const selected = olFeature.get('_selected');

                const config = {
                    showLabel:
                        (labelsVisibility === 'selected' && selected) ||
                        labelsVisibility === 'allclass' ||
                        labelsVisibility === 'all',
                    labelSize: view.labelSize
                };

                olFeature = me.createOlFeature(config, olFeature.get('_geovalue'));

                if (!visible && !vm.get('contextmenu.multiselection.enabled')) {
                    if (
                        view.infoWindow._current == olFeature.getId() ||
                        view.infoWindow._ownerId != olFeature.get('_owner_id')
                    ) {
                        view.removeInfoWindow();
                    }
                    const featurenotfound = CMDBuildUI.model.gis.GeoAttribute.getOlLayerVectorStyleRemoved(),
                        removeFeature = Ext.Array.findBy(interaction.getFeatures().getArray(), function (item) {
                            return olFeature.getId() == item.getId();
                        });

                    interaction.getFeatures().remove(removeFeature);
                    return [featurenotfound];
                } else if (visible && layerStore) {
                    const ollayername = olFeature.get('ollayername');
                    const index = layerStore.find('ollayername', ollayername);
                    const item = layerStore.getAt(index);
                    const highlightselected = item.get('thematism') ? vm.get('highlightselected') : true;
                    const basestyle = me.getOlLayerVectorStyle(olFeature, resolution, item);

                    if (highlightselected && selected) {
                        //if thematism is applied and highlightselected
                        const selectionstyle = me.getOlLayerVectorStyleSelected(item);
                        return Ext.Array.merge(basestyle, selectionstyle);
                    } else {
                        const dark = !selected ? 1 : 0.5;
                        //if thematism is not applied or the selected item must not be highlited
                        basestyle[0].fill_ ? (basestyle[0].fill_.color_[3] = dark) : null;
                        return basestyle;
                    }
                }
            }
        });

        interaction.on('select', function (event) {
            const mapEvent = event.mapBrowserEvent;
            const selected = event.selected;
            const deselected = event.deselected;
            const multiselection = vm.get('contextmenu.multiselection.enabled');
            const features = interaction.getFeatures();
            const featuresArray = features.getArray();

            if (multiselection) {
                if (Ext.isEmpty(selected) && Ext.isEmpty(deselected)) {
                    const featuresMap = mapEvent ? mapEvent.featuresMap : null,
                        refresh_interaction = mapEvent ? mapEvent.refresh_interaction : false;
                    if (featuresMap) {
                        // Used to select record when it doesn't have features on map
                        view.selectRecordOnTab(featuresMap, true);
                    } else if (!refresh_interaction) {
                        // Used to clear selection on map
                        Ext.Array.forEach(featuresArray, function (item) {
                            item.set('_selected', false);
                        });
                        features.clear();
                        vm.set('objectId', null);
                    }
                    return;
                }
            } else {
                Ext.Array.forEach(Ext.Array.union(featuresArray, deselected), function (item, index, allitems) {
                    item.set('_selected', false);
                });
                features.clear();
                if (Ext.isEmpty(selected) && Ext.isEmpty(deselected)) {
                    view.removeInfoWindow();
                    return;
                }
            }

            if (selected.length != 0) {
                Ext.Array.each(selected, function (olFeatures, index, allitems) {
                    const olFeature = view.getFirstOlFeature(olFeatures);

                    const owner_id = olFeature.get('_owner_id');
                    const sameObjectId = vm.get('objectId') == owner_id ? true : false;

                    if (!olFeature.getStyle()) {
                        olFeature.setStyle(interaction.getStyle());
                    }

                    olFeature.set('_selected', true);
                    if (multiselection) {
                        Ext.Array.forEach(featuresArray, function (item, index, allitems) {
                            if (item.get('_owner_id') == owner_id || (mapEvent && mapEvent.refresh_interaction)) {
                                item.set('_selected', true);
                            }
                        });
                        if (!Ext.Array.contains(featuresArray, olFeature)) {
                            features.push(olFeature);
                        }
                        view.selectRecordOnTab(owner_id, true);
                    } else {
                        features.push(olFeature);
                    }

                    if (mapEvent && !mapEvent.silent && !mapEvent.refresh_interaction) {
                        if (vm.get('objectId') != owner_id) {
                            vm.set('objectId', olFeature ? owner_id : null);
                        }
                    }

                    // Used to center the map on the selected feature
                    if ((olFeature && mapEvent.animate && !mapEvent.selectAll) || sameObjectId) {
                        const geoAttr = layerStore.getDataSource().find('ollayername', olFeature.get('ollayername'));
                        const actualZoom = vm.get('zoom');
                        let zoom;
                        if (geoAttr && (actualZoom < geoAttr.get('zoomMin') || actualZoom > geoAttr.get('zoomMax'))) {
                            zoom = geoAttr.get('zoomDef');
                        }

                        // Prevent zoom on selected feature while features refresh
                        if (olFeature.get('_alreadyZoomed') === false) {
                            me.animationMovement(
                                olMapView,
                                ol.extent.getCenter(olFeature.getGeometry().getExtent()),
                                zoom
                            );
                            olFeature.set('_alreadyZoomed', true);
                        }
                    }

                    // enable info window
                    if (olFeature && !multiselection) {
                        const geovalue = olFeature.get('_geovalue');
                        const olFeatureSelected = event.selected.length;
                        if (olFeatureSelected === 1) {
                            const visible = me.isVisibleOlFeature(olFeature);
                            if (visible) {
                                const coordinates = view.calculateGeometryCoordinates(olFeature.getGeometry());
                                if (view.infoWindow._current == geovalue.getId()) {
                                    view.infoWindow.setPosition(coordinates);
                                } else {
                                    view.infoWindow._current = geovalue.getId();
                                    view.infoWindow._ownerId = geovalue.get('_owner_id');
                                    view.getInfoWindowContent(geovalue).then(function (text) {
                                        if (text) {
                                            view.infoWindowContent.innerHTML = text;
                                            view.infoWindow.setPosition(coordinates);
                                        } else {
                                            view.removeInfoWindow();
                                        }
                                    });
                                }
                            } else {
                                view.removeInfoWindow();
                            }
                        } else if (olFeatureSelected > 1 && view.infoWindow._ownerId != geovalue.get('_owner_id')) {
                            view.removeInfoWindow();
                        }
                    } else {
                        view.removeInfoWindow();
                    }
                });
            } else {
                const record = !Ext.isEmpty(deselected) ? (Ext.isArray(deselected) ? deselected[0] : deselected) : null;
                const id = record.get('_owner_id') || record.get('_id');

                view.removeInfoWindow();
                record.set('_selected', false);

                if (!multiselection) {
                    vm.set('objectId', null);
                } else if (!Ext.isEmpty(id)) {
                    Ext.Array.forEach(Ext.clone(featuresArray), function (item, index, allitems) {
                        if (item.get('_owner_id') == id) {
                            item.set('_selected', false);
                            features.remove(item);
                        }
                    });

                    view.selectRecordOnTab(id, false);
                }
            }
        });

        olMap.addInteraction(interaction);
    },

    /**
     *
     * @param {Ext.data.Model} geovalue
     */
    animateMap: function (geovalue) {
        const me = this;
        const vm = me.getViewModel();
        const layerStore = vm.get('layerStore');
        const typeshapetiff =
            geovalue._type === CMDBuildUI.model.gis.GeoAttribute.subtype.shape ||
            geovalue._type === CMDBuildUI.model.gis.GeoAttribute.subtype.geotiff;
        const center = typeshapetiff ? [geovalue.x, geovalue.y] : me.getCenter(geovalue);
        const ollayername = typeshapetiff
            ? Ext.String.format(
                  '{0}_{1}_{2}',
                  CMDBuildUI.model.gis.GeoAttribute.GEOATTRIBUTE,
                  geovalue.name,
                  geovalue._owner_type
              )
            : geovalue.get('ollayername');
        const layer = layerStore.find('ollayername', ollayername);

        let zoom;

        //gets't the zoom
        if (layer == -1) {
            //if layer is filtered --> the olLayer is not visible
            const layerrecord = me.getView().getOlLayerSource(layerStore).findRecord('ollayername', ollayername);
            if (layerrecord) {
                zoom = layerrecord.get('zoomDef');
            }
        } else {
            //if layer is not filtered --> the layer is visible
            zoom = vm.get('zoom');
        }
        const olMapView = me.getView().getOlMap().getView();

        me.animationMovement(olMapView, center, zoom);
    },

    /**
     *
     * @param {ol.Map} olMap
     * @param {Array} center
     * @param {Number} zoom
     * @example https://openlayers.org/en/v4.6.5/apidoc/ol.View.html#animate
     */
    animationMovement: function (olMap, center, zoom) {
        center = Ext.Array.equals(center, olMap.getCenter())
            ? [center[0] + olMap.getMaxZoom() / olMap.getZoom(), center[1]]
            : center;
        const vm = this.getViewModel();
        const animationTarget = vm.get('animationTarget');
        // animate only if the target is changed
        if (!(olMap.getAnimating() && Ext.Array.equals(center, animationTarget))) {
            olMap.cancelAnimations();
            vm.set('animationTarget', center);
            olMap.animate({
                center: center,
                zoom: zoom,
                duration: 500
            });
        }
    },

    /**
     *
     * @param {ol.Map} olMap
     * @param {CMDBuildUI.model.gis.GeoValue} geovalues
     * @param {boolean} silent
     * @param {boolean} animate
     * @param {*} featuresMap
     */
    ol_interaction_select_select: function (olMap, geovalues, silent, animate, featuresMap) {
        silent = Ext.isEmpty(silent) ? false : silent;

        const me = this;
        const view = this.getView();
        const interaction = view.getOl_interaction_select(olMap);

        if (interaction) {
            const selected = [];
            Ext.Array.forEach(geovalues, function (item, index, array) {
                const olLayer = view.getOlLayer(olMap, item.get('ollayername'));
                const layerSource = olLayer ? olLayer.getSource() : [];
                // layerSource = olLayer ? me.getOlLayerSource() : [];
                if (item.get('x') || item.get('points')) {
                    const featureStore = me.getViewModel().get('featureStore');
                    let featureLayer, feature;
                    if (layerSource instanceof ol.source.Cluster) {
                        allFeatures = layerSource.getFeatures();
                        feature = Ext.Array.findBy(
                            Ext.Array.flatten(
                                Ext.Array.map(allFeatures, function (feature) {
                                    return feature.get('features');
                                })
                            ),
                            function (feature) {
                                return feature.getId() === item.getId();
                            }
                        );
                    } else {
                        featureLayer =
                            layerSource && layerSource.getFeatureById ? layerSource.getFeatureById(item.getId()) : null;
                        feature = featureLayer ? featureLayer : me.createOlFeature({}, item);
                    }

                    if (feature) {
                        feature.set('_alreadyZoomed', false);
                        selected.push(feature);
                    }

                    if (!featureStore.findRecord('_id', item.getId())) {
                        featureStore.add(item);
                    }
                }
            });

            interaction.dispatchEvent(
                new ol.interaction.Select.SelectEvent('select', selected, [], {
                    silent: silent,
                    animate: animate,
                    featuresMap: featuresMap
                })
            );
        }
    },

    /**
     * This function is created to trigger the style calculation.
     * Used for when changing the 'checked' in layerStore records
     * @param {ol.Map} olMap
     * @param {Boolean} force
     */
    ol_interaction_select_refresh: function (olMap, force) {
        const me = this;
        const vm = me.getViewModel();
        const view = me.getView();
        const interaction = view.getOl_interaction_select(olMap);
        const config = {
            silent: false,
            animate: false,
            refresh_interaction: true
        };

        if (interaction) {
            const selected = [];
            const theObject = vm.get('theObject');
            const id = theObject ? theObject.getId() : null;

            if (vm.get('contextmenu.multiselection.enabled')) {
                const recordsSelected = Ext.Array.pluck(
                    view.getCardGridView().getSelectionModel().getSelected().getRange(),
                    'id'
                );

                Ext.Array.forEach(olMap.getLayers().getArray(), function (layer) {
                    const source = layer.getSource(),
                        features = source.getFeatures ? source.getFeatures() : [];
                    Ext.Array.forEach(features, function (item) {
                        if (Ext.Array.contains(recordsSelected, item.values_._geovalue.get('_owner_id').toString())) {
                            selected.push(item);
                        }
                    });
                });
            } else if (theObject) {
                theObject.getGeoValues(force).then(function (geovalues) {
                    Ext.Array.forEach(geovalues.getRange(), function (item) {
                        const olLayer = view.getOlLayer(olMap, item.get('ollayername'));
                        const layerFeatures = olLayer ? olLayer.getSource().getFeatures() : [];
                        Ext.Array.forEach(layerFeatures, function (elem) {
                            if (elem.get('features')) {
                                // is a cluster
                                Ext.Array.forEach(elem.get('features'), function (feature) {
                                    if (feature.get('_owner_id') == id) {
                                        selected.push(feature);
                                    }
                                });
                            } else {
                                if (elem.get('_owner_id') == id) {
                                    selected.push(elem);
                                }
                            }
                        });
                    });

                    if (force) {
                        interaction.dispatchEvent(
                            new ol.interaction.Select.SelectEvent('select', selected, [], config)
                        );
                    }
                });
            }

            Ext.asap(function () {
                interaction.dispatchEvent(new ol.interaction.Select.SelectEvent('select', selected, [], config));
            });
        }
    },

    privates: {
        _delayvalue: 100,

        /**
         * {object} object containing the ol.Overlay and the htmlElement used fo it
         */
        _popupBim: {
            /**
             * saves the information about the last feature wich opened the popup
             */
            lastFeatureId: null,
            /**
             * contains the ol.Overlay object
             */
            overlay: null,
            /**
             * The id of the last project clicked
             */
            lastProjectId: null
        },

        _popupBimEvents: {
            popupHover: null,
            featureHover: null
        },

        /**
         *
         * @returns
         */
        getDelayedTask: function () {
            if (!this._delayedTask) {
                this._delayedTask = new Ext.util.DelayedTask();
            }

            return this._delayedTask;
        },

        /**
         * Map source layer values
         */
        mapSource: {
            default: {
                icon: CMDBuildUI.util.helper.IconHelper.getIconId('map', 'regular'),
                value: 'default'
            },
            satellite: {
                icon: CMDBuildUI.util.helper.IconHelper.getIconId('globe', 'solid'),
                value: 'satellite'
            }
        },

        /**
         *
         * @returns
         */
        getCenter: function (geovalue) {
            switch (geovalue.get('_type')) {
                case 'point':
                case 'geotiff':
                case 'shape':
                    return [geovalue.get('x'), geovalue.get('y')];
                case 'linestring':
                case 'polygon':
                    return [geovalue.get('points')[0].x, geovalue.get('points')[0].y];
            }
        },

        /**
         *
         * @param {*} config
         * @param {*} advancedfilter
         * @returns
         */
        loadFeaturesStore: function (config, advancedfilter) {
            const deferred = new Ext.Deferred(),
                // create temp store
                childrenstore = Ext.create('Ext.data.Store', {
                    proxy: {
                        type: 'baseproxy',
                        model: 'CMDBuildUI.model.gis.GeoValue'
                    },
                    pageSize: 0
                });

            if (advancedfilter) {
                childrenstore.setAdvancedFilter(advancedfilter);
            }

            const vm = this.getViewModel();
            const attach_nav_tree_collection = vm.get('attachNavTreeCollection');
            const gisNavigation = vm.get('gisNavigation');

            childrenstore.load(
                Ext.applyIf(config, {
                    callback: function (records, operation, success) {
                        if (success) {
                            if (operation.getParams()[CMDBuildUI.model.gis.GeoValueTree.attach_nav_tree]) {
                                const attach_nav_items =
                                        operation.getResultSet().metadata[
                                            CMDBuildUI.model.gis.GeoValueTree.nav_tree_items
                                        ],
                                    newItems = [];
                                if (attach_nav_items.length) {
                                    Ext.Array.forEach(attach_nav_items, function (attach_nav_item, index, array) {
                                        attach_nav_item.description = Ext.String.htmlEncode(
                                            attach_nav_item.description
                                        );
                                        const composedId = Ext.String.format(
                                            '{0}-{1}',
                                            attach_nav_item._id,
                                            attach_nav_item.navTreeNodeId
                                        );

                                        if (attach_nav_tree_collection.find('_id_composed', composedId) === -1) {
                                            attach_nav_item._id_composed = composedId;
                                            const newItem = Ext.create(
                                                'CMDBuildUI.model.gis.GeoValueTree',
                                                Ext.applyIf(attach_nav_item, {
                                                    text: attach_nav_item.description,
                                                    _objectid: attach_nav_item._id,
                                                    _objecttypename: attach_nav_item.type,
                                                    leaf: true,
                                                    checked: true
                                                })
                                            );

                                            newItem.setNavTreeNode(
                                                gisNavigation.getNodeRecursive(attach_nav_item.navTreeNodeId)
                                            );
                                            newItems.push(newItem);
                                        }
                                    });

                                    attach_nav_tree_collection.add(newItems);
                                }
                            }
                            deferred.resolve(records);
                        } else {
                            deferred.reject();
                        }

                        Ext.asap(function () {
                            childrenstore.destroy();
                        });
                    }
                })
            );
            return deferred.promise;
        },

        /**
         *
         * @param {*} config
         * @param {*} advancedfilter
         * @returns
         */
        loadShapeFeatureStore: function (config, advancedfilter) {
            const deferred = new Ext.Deferred(),
                // create temp store
                childrenstore = Ext.create('Ext.data.Store', {
                    proxy: {
                        type: 'baseproxy',
                        model: 'CMDBuildUI.model.gis.GeoLayer'
                    },
                    pageSize: 0
                });

            if (advancedfilter) {
                childrenstore.setAdvancedFilter(advancedfilter);
            }

            childrenstore.load(
                Ext.applyIf(config, {
                    callback: function (records, operation, success) {
                        if (success) {
                            deferred.resolve(records);
                        }
                    }
                })
            );

            return deferred.promise;
        },

        /**
         *
         * @param {*} records
         */
        geoValuesVisibility: function (records) {
            if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.navigationTreeEnabled)) {
                const hashMap = this.getView().getMapContainerView().getHashMap();
                if (hashMap) {
                    Ext.Array.each(records, function (record, index, array) {
                        const ownerid = record.get('_owner_id'),
                            c = hashMap.get(ownerid),
                            checked = c == undefined || c == true ? true : false;
                        record.set('checked', checked);
                    });
                }
            }
        },

        /**
         *
         * @param {*} records
         */
        geoLayerVisibility: function (records) {
            const hashMap = this.getView().getMapContainerView().getHashMap();
            if (hashMap) {
                Ext.Array.forEach(records, function (item, index, array) {
                    const ownerid = item.get('_owner_id'),
                        c = hashMap.get(ownerid),
                        checked = c == undefined || c == true ? true : false;
                    item.set('checked', checked);
                });
            }
        },

        /**
         *
         * @param {*} records
         */
        getLayerLayerVisibility: function (records) {
            const layerStore = this.getViewModel().get('layerStore');

            Ext.Array.forEach(records, function (item, index, array) {
                const ollayername = item.get('ollayername'),
                    geoattributeindex = layerStore.findBy(function (item) {
                        return item.get('ollayername') == ollayername;
                    });

                if (geoattributeindex != -1) {
                    const geoattribute = layerStore.getAt(geoattributeindex);
                    item.set(CMDBuildUI.model.gis.GeoLayer.checkedLayer, geoattribute.get('checked'));
                } else {
                    item.set(CMDBuildUI.model.gis.GeoLayer.checkedLayer, false);
                }
            });
        },

        /**
         * This function manipulates the geovalues and sets the correct `hasBim` and `projectId` property if necessary
         * @param {CMDBuildUI.model.gis.GeoValue} records
         */
        geoValuesBim: function (geovalues) {
            if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.bim.enabled)) {
                const store = Ext.getStore('bim.Projects');
                Ext.Array.each(geovalues, function (geovalue, index, array) {
                    if (geovalue.get('_type') == CMDBuildUI.model.gis.GeoAttribute.subtype.point) {
                        const ownerid = geovalue.get('_owner_id'),
                            projectIndex = store.find('ownerCard', ownerid);
                        if (projectIndex != -1) {
                            geovalue.set('hasBim', true);
                            const project = store.getAt(projectIndex);
                            geovalue.set('projectId', project.get('projectId'));
                        }
                    }
                });
            }
        },

        /**
         *
         */
        createOlMap: function () {
            const view = this.getView();
            const vm = this.getViewModel();
            let zoom, center;

            // sets and generates the html div id
            view.setHtml(Ext.String.format('<div id="{0}" style="height: 100%;"></div>', view.getDivMapId()));
            const keepZoomAndPositionConfig = CMDBuildUI.util.helper.Configurations.get(
                CMDBuildUI.model.Configuration.gis.keepZoomAndPosition
            );
            if (keepZoomAndPositionConfig && CMDBuildUI.view.map.MapModel.lastZoom) {
                zoom = CMDBuildUI.view.map.MapModel.lastZoom;
                center = CMDBuildUI.view.map.MapModel.lastPosition;
            } else {
                zoom = vm.get('zoom');
                center = view.getMapCenter();
            }

            const viewConfig = {
                projection: 'EPSG:3857',
                zoom: zoom,
                center: center,
                maxZoom:
                    25 > CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.maxZoom)
                        ? CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.maxZoom)
                        : 25,
                minZoom:
                    2 < CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.minZoom)
                        ? CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.minZoom)
                        : 2
            };
            // Configure map components
            const olView = new ol.View(viewConfig);

            olView.on('change:resolution', function (olEvent) {
                const newZoom = this.getZoom();
                vm.set('zoom', newZoom);
            });

            olView.on('change:center', function (olEvent) {
                const newCenter = this.getCenter();
                view.setMapCenter(newCenter);
            });

            var source;
            switch (CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.preferredMapLayer)) {
                case this.mapSource.satellite.value:
                    source = new ol.source.XYZ({
                        url: 'https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}',
                        maxZoom: 19
                    });
                    break;
                default:
                    source = new ol.source.OSM();
                    break;
            }

            var baseLayer = new ol.layer.Tile({
                source: source
            });
            baseLayer.set('name', CMDBuildUI.model.gis.GeoAttribute.BASETILE);

            const controls = this.createMapControls();
            // add map to container
            // TODO: Resolve the probleme on the opening of map, if i resize the window (of chrome) all works otherwise is wrong
            const olMap = new ol.Map({
                controls: controls,
                target: view.getDivMapId(),
                view: olView,
                layers: [baseLayer],
                overlays: [this.createOlPopup(), this.createOlMarkerPopup()]
            });

            this.setOl_Interaction_select(olMap);

            view.setOlMap(olMap);
            view.setLoadedExtentsRtree(new ol.structs.RBush());
            vm.set('mapCreated', true);
            this.addBimInteraction();
            this.addLongPressEvent();
            this.addContextMenuEvent();
        },

        /**
         * Popup for info window
         * @return {ol.Overlay}
         */
        createOlPopup: function () {
            const view = this.getView();

            const infowindow = document.createElement('div');
            infowindow.id = 'cmdbuildui-olmap-infowindow';
            infowindow.className = 'cmdbuildui-olmap-infowindow';

            const closer = document.createElement('a');
            closer.id = 'cmdbuildui-olmap-infowindow-closer';
            closer.className = 'cmdbuildui-olmap-infowindow-closer';
            closer.href = '#';

            const content = (view.infoWindowContent = document.createElement('div'));
            content.id = 'cmdbuildui-olmap-infowindow-content';
            content.className = 'cmdbuildui-olmap-infowindow-content';

            infowindow.append(closer, content);

            view.getEl().dom.append(infowindow);

            view.infoWindow = new ol.Overlay({
                element: infowindow,
                positioning: 'center-center',
                autoPanAnimation: {
                    duration: 250
                }
            });

            closer.onclick = function () {
                view.infoWindow.setPosition(undefined);
                closer.blur();
                return false;
            };

            return view.infoWindow;
        },

        /**
         * Poup for marker
         * @return {ol.Overlay}
         */
        createOlMarkerPopup: function () {
            const view = this.getView();

            const popupEl = document.createElement('div');
            popupEl.id = 'ol-popup';
            popupEl.className = Ext.baseCSSPrefix + 'ol-popup';

            const contentEl = document.createElement('div');
            contentEl.id = 'popup-content';

            popupEl.appendChild(contentEl);
            view.getEl().dom.append(popupEl);
            view.markerEl = contentEl;

            view.markerWindow = new ol.Overlay({
                element: popupEl,
                positioning: 'center-center',
                autoPanAnimation: {
                    duration: 250
                }
            });

            return view.markerWindow;
        },

        /**
         *
         * @returns
         */
        createMapControls: function () {
            const me = this;
            const vm = this.getViewModel();
            const view = me.getView();

            return [
                new ol.control.Zoom(),
                new ol.control.ScaleLine(),
                new ol.control.MousePosition({
                    projection: 'EPSG:4326',
                    coordinateFormat: function (coord) {
                        //Template for the mousePostiton controller
                        const olMap = view.getOlMap();
                        const olView = olMap.getView();
                        olView.set('coord', coord);

                        const template = Ext.String.format(
                            '{0}: {1} {2}: {3}',
                            CMDBuildUI.locales.Locales.gis.zoom,
                            vm.get('zoom'),
                            CMDBuildUI.locales.Locales.gis.position,
                            '{x} {y}'
                        );
                        return ol.coordinate.format(coord, template, 2);
                    }
                }),
                this.getSearchControl(),
                this.getMapLabelsControl(),
                this.modifySizeLabels(),
                this.getInfoControl(),
                this.modifyMapSource()
            ];
        },

        /**
         *
         */
        addLongPressEvent: function () {
            const view = this.getView();
            const olMap = view.getOlMap();
            const targetElement = Ext.get(olMap.getTargetElement());

            this.mon(targetElement, 'longpress', function (event, node, options, eOpts) {
                if (!view.getViewModel().get('drawmode')) {
                    const idElements = [];

                    //https://openlayers.org/en/v4.6.5/apidoc/ol.Map.html#forEachFeatureAtPixel
                    olMap.forEachFeatureAtPixel(
                        [event.parentEvent.event.layerX, event.parentEvent.event.layerY],
                        function (feature, layer) {
                            idElements.push(feature.get('_owner_id'));
                        }
                    );

                    CMDBuildUI.util.Utilities.openPopup(
                        CMDBuildUI.view.map.longpress.Grid.longpressPopupId,
                        CMDBuildUI.locales.Locales.gis.longpresstitle,
                        {
                            xtype: 'map-longpress-grid',
                            viewModel: {
                                data: {
                                    idElements: idElements
                                }
                            }
                        },
                        null,
                        {
                            width: '45%',
                            height: '45%'
                        }
                    );
                }
            });
        },

        /**
         * Right click event on map
         */
        addContextMenuEvent: function () {
            const view = this.getView();
            const olMap = view.getOlMap();
            const targetElement = Ext.get(olMap.getTargetElement());

            this.mon(targetElement, 'contextmenu', function (event) {
                event.preventDefault();
                const pixel = olMap.getEventPixel(event);
                const coordEPSG_3857 = olMap.getCoordinateFromPixel(pixel);
                const coordEPSG_4326 = ol.proj.toLonLat(coordEPSG_3857);
                items = [
                    {
                        text: CMDBuildUI.locales.Locales.gis.contextmenu.coordinates,
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('map-marker-alt', 'solid'),
                        handler: function () {
                            let popupCmp = view.markerCmp;
                            if (popupCmp) {
                                popupCmp.destroy();
                            }
                            view.markerWindow.setPosition(coordEPSG_3857);
                            popupCmp = Ext.create('Ext.panel.Panel', {
                                cls: Ext.baseCSSPrefix + 'ol-popup-panel',
                                style: {
                                    borderRadius: 10
                                },
                                width: 250,
                                renderTo: view.markerEl,
                                items: [
                                    {
                                        xtype: 'container',
                                        layout: {
                                            type: 'hbox',
                                            align: 'middle'
                                        },
                                        items: [
                                            {
                                                xtype: 'component',
                                                padding: '0 0 0 8',
                                                html: '<strong>' + CMDBuildUI.locales.Locales.gis.position + '</strong>'
                                            },
                                            {
                                                xtype: 'tbfill'
                                            },
                                            {
                                                xtype: 'button',
                                                ui: 'management-neutral-action-small',
                                                cls: Ext.baseCSSPrefix + 'ol-popup-close',
                                                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                                                handler: function () {
                                                    popupCmp.destroy();
                                                    popupCmp = null;
                                                    view.markerWindow.setPosition(undefined);
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        // EPSG 3857 row
                                        xtype: 'container',
                                        layout: {
                                            type: 'hbox',
                                            align: 'middle'
                                        },
                                        margin: '0 8 8 8',
                                        items: [
                                            {
                                                xtype: 'displayfield',
                                                fieldLabel: 'EPSG 3857',
                                                value: coordEPSG_3857,
                                                value: Ext.String.format(
                                                    '{0}\n{1}',
                                                    coordEPSG_3857[0],
                                                    coordEPSG_3857[1]
                                                ),
                                                flex: 1
                                            },
                                            {
                                                xtype: 'tool',
                                                margin: '0 4 0 0',
                                                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('copy', 'solid'),
                                                cls: 'management-tool',
                                                handler: function () {
                                                    CMDBuildUI.util.Utilities.copyToClipboard(
                                                        coordEPSG_3857,
                                                        CMDBuildUI.locales.Locales.gis.contextmenu
                                                            .coordinatescopiedmessage
                                                    );
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        // EPSG 4326 row
                                        xtype: 'container',
                                        layout: {
                                            type: 'hbox',
                                            align: 'middle'
                                        },
                                        margin: '0 8 8 8',
                                        items: [
                                            {
                                                xtype: 'displayfield',
                                                fieldLabel: 'EPSG 4326',
                                                value: coordEPSG_4326,
                                                value: Ext.String.format(
                                                    '{0}\n{1}',
                                                    coordEPSG_4326[0],
                                                    coordEPSG_4326[1]
                                                ),
                                                flex: 1
                                            },
                                            {
                                                xtype: 'tool',
                                                margin: '0 4 0 0',
                                                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('copy', 'solid'),
                                                cls: 'management-tool',
                                                handler: function () {
                                                    CMDBuildUI.util.Utilities.copyToClipboard(
                                                        coordEPSG_4326,
                                                        CMDBuildUI.locales.Locales.gis.contextmenu
                                                            .coordinatescopiedmessage
                                                    );
                                                }
                                            }
                                        ]
                                    }
                                ]
                            });

                            view.markerWindow.setOffset([-48, (popupCmp.getHeight() + 10) * -1]);
                            view.markerCmp = popupCmp;
                        }
                    }
                ];
                const menu_grid = new Ext.menu.Menu({
                    autoEl: {
                        'data-testid': 'map-contextmenu'
                    },
                    items: items,
                    listeners: {
                        hide: function (menu, eOpts) {
                            Ext.asap(function () {
                                menu.destroy();
                            });
                        }
                    }
                });

                event.stopEvent();
                const position = event.getXY();
                position[0] = position[0];
                menu_grid.showAt(position);
            });
        },

        /**
         * https://github.com/openlayers/openlayers/blob/v4.6.5/src/ol/source/vector.js#L742
         * @param {*} extent
         * @param {*} resolution
         * @param {*} projection
         * @param {*} replace
         */
        strategy: function (extent, resolution, projection, replace) {
            const loadedExtentsRtree = this.getView().getLoadedExtentsRtree();

            if (replace) {
                loadedExtentsRtree.clear();
            }

            const extentsToLoad = [extent],
                extentEnlarged = this.enlargeExtent(extent);

            var i, ii, toload;

            for (i = 0, ii = extentsToLoad.length; i < ii; ++i) {
                const extentToLoad = extentsToLoad[i],
                    alreadyLoaded = loadedExtentsRtree.forEachInExtent(
                        extentToLoad,
                        /**
                         * @param {{extent: ol.Extent}} object Object.
                         * @return {boolean} Contains.
                         */
                        function (object) {
                            return ol.extent.containsExtent(object.extent, extentToLoad);
                        }
                    );
                if (!alreadyLoaded) {
                    loadedExtentsRtree.insert(extentEnlarged, {
                        extent: extentEnlarged.slice()
                    });
                    toload = true;
                } else {
                    toload = false;
                }
            }

            return toload;
        },

        /**
         *
         * @param {*} extent
         * @returns
         */
        enlargeExtent: function (extent) {
            const minx = extent[0],
                miny = extent[1],
                maxx = extent[2],
                maxy = extent[3],
                xlength = maxx - minx,
                ylength = maxy - miny;

            return [
                extent[0] - xlength * 0.5,
                extent[1] - ylength * 0.5,
                extent[2] + xlength * 0.5,
                extent[3] + ylength * 0.5
            ];
        },

        /**
         *
         * Callback after loading the features
         * @param {*} replace
         */
        onFeaturesLoad: function (replace) {
            const me = this;
            const view = me.getView();
            const vm = me.getViewModel();
            const olMap = view.getOlMap();
            const featureStore = vm.get('featureStore');

            if (featureStore) {
                if (!replace) {
                    featureStore
                        .getGroups()
                        .getRange()
                        .forEach(function (group) {
                            const ollayername = group.getGroupKey();
                            me.addOlFeatures(olMap, ollayername, group.getRange(), replace);
                        });
                } else {
                    Ext.Array.forEach(
                        vm.get('layerStore').getRange(),
                        function (layer) {
                            if (layer.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.geometry) {
                                const ollayername = layer.get('ollayername');
                                const groups = featureStore.getGroups();

                                if (groups) {
                                    const group = groups.findBy(
                                        function (item, index, array) {
                                            return item.getGroupKey() == ollayername;
                                        },
                                        this,
                                        0
                                    );

                                    let groupRange;
                                    if (group) {
                                        groupRange = group.getRange();
                                    } else {
                                        groupRange = [];
                                    }
                                    me.addOlFeatures(olMap, ollayername, groupRange, replace);
                                } else {
                                    me.addOlFeatures(olMap, ollayername, [], replace);
                                }
                            }
                        },
                        this
                    );
                }
            }
        },

        /**
         *
         * @param {*} replace
         */
        onShapeFeaturesLoad: function (replace) {
            const view = this.getView(),
                shapefeatures = this.getViewModel().get('shapeFeatureStore');

            if (shapefeatures) {
                view.addOlGeoFeatures(view.getOlMap(), '_', shapefeatures.getRange(), replace);
            }
        },

        /**
         * This function sets hover and styling for the features with bim
         * Add's an event handler for the map pointer move
         */
        addBimInteraction: function () {
            this.addBimPopup();
            this.pointermoveMapAssign();
        },

        /**
         * creates the DOM element,the ol.Overlay element and adds it to the map
         */
        addBimPopup: function () {
            const me = this,
                extEl = new Ext.button.Button({
                    text: CMDBuildUI.locales.Locales.bim.showBimCard,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.bim.showBimCard'
                    },
                    renderTo: Ext.getBody(),
                    handler: function () {
                        CMDBuildUI.util.Bim.openBimPopup(
                            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.bim.viewer),
                            me._popupBim.lastProjectId,
                            null
                        );
                    }
                }),
                element = extEl.getEl().dom;

            /**
             * Add controls to the dom elemnt
             */

            element.onmouseenter = function (mouseEvt) {
                me._popupBimEvents.popupHover = true;
            };

            element.onmouseleave = function (mouseEvt) {
                me._popupBimEvents.popupHover = false;

                if (!me._popupBimEvents.featureHover && !me._popupBimEvents.popupHover) {
                    me._popupBim.overlay.setPosition(undefined);
                    me._popupBim.lastFeatureId = null;
                }
            };

            /**
             * creates ol.Overlay
             */
            this._popupBim = {
                overlay: new ol.Overlay({
                    element: element,
                    id: 'bimMapPopup',
                    offset: [0, -25],
                    stopEvent: false
                }),
                element: element
            };
            this._popupBim.overlay.setPosition(undefined);

            const map = this.getView().getOlMap(); // this can be passed as argument of the function avoiding calling the view
            map.addOverlay(this._popupBim.overlay);
        },

        /**
         * Binds a function to pointermove event that it is used to know when open the popup for the bim elements
         */
        pointermoveMapAssign: function () {
            const me = this,
                map = this.getView().getOlMap();

            map.on('pointermove', function (event) {
                const features = map.getFeaturesAtPixel(event.pixel);
                if (!Ext.isEmpty(features)) {
                    const feature = features[0],
                        featureId = feature.getId();

                    if (feature.get('hasBim')) {
                        //HACK: enable popup only for point type
                        if (featureId != me._popupBim.lastFeatureId && !me._popupBimEvents.popupHover) {
                            me._popupBim.lastFeatureId = featureId;
                            me._popupBim.lastProjectId = feature.get('projectId');
                            me._popupBimEvents.featureHover = true;

                            const position = feature.getGeometry().getCoordinates();
                            me._popupBim.overlay.setPosition(position);
                        }
                    }
                } else {
                    me._popupBimEvents.featureHover = false;
                }

                if (!me._popupBimEvents.featureHover && !me._popupBimEvents.popupHover) {
                    me._popupBim.overlay.setPosition(undefined);
                    me._popupBim.lastFeatureId = null;
                }
            });
        },

        /**
         * Return the control for address search
         * @returns {ol.control.Control}
         */
        getSearchControl: function () {
            const me = this;
            const view = this.getView();

            const showHideSearch = function (event) {
                event.preventDefault();
                if (element.className.includes('expanded')) {
                    element.classList.remove('ol-search-expanded');
                    element.classList.add('ol-search-collapsed');
                    removeSearchResults();
                    me.removeSearchMarker();
                } else {
                    element.classList.remove('ol-search-collapsed');
                    element.classList.add('ol-search-expanded');
                }
            };

            const removeSearchResults = function () {
                if (search_icon.list) {
                    search_icon.list.remove();
                }
            };

            const handleKeyDown = function (event) {
                if (event.key == 'Enter') {
                    handleSearch();
                }
            };

            const handleSearch = function () {
                if (!Ext.isEmpty(input.value.trim())) {
                    Ext.Ajax.request({
                        url: Ext.String.format('{0}/search?format=json&q={1}', element.baseUrl, input.value),
                        method: 'GET',
                        withCredentials: false,
                        success: function (response) {
                            const data = JSON.parse(response.responseText);
                            showSearchResults(data);
                        },
                        failure: function () {
                            showSearchResults({
                                message: CMDBuildUI.locales.Locales.gis.extension.errorCall
                            });
                        }
                    });
                }
            };

            const showSearchResults = function (data) {
                removeSearchResults();
                if (!Ext.isEmpty(data)) {
                    createSearchResults(data);
                } else {
                    createSearchResults({
                        message: CMDBuildUI.locales.Locales.gis.extension.noResults
                    });
                }
            };

            const createSearchResults = function (data) {
                search_icon.list = document.createElement('div');
                search_icon.list.className = 'ol-span-container';

                if (Ext.isArray(data)) {
                    Ext.Array.forEach(data, function (address, index, alladdresses) {
                        const button = document.createElement('button');
                        button.className = 'ol-span';
                        button.innerHTML = address.display_name;
                        button.lon = address.lon;
                        button.lat = address.lat;
                        search_icon.list.appendChild(button);
                        button.setAttribute('aria-label', address.display_name);
                        button.setAttribute('title', address.display_name);
                        button.addEventListener('click', addressHandler);
                    });
                } else {
                    const span = document.createElement('span');
                    span.className = 'ol-span';
                    span.innerHTML = data.message;
                    search_icon.list.appendChild(span);
                }
                element.appendChild(search_icon.list);
            };

            const addressHandler = function () {
                const olMapView = view.getOlMap().getView();
                const coordinates = ol.proj.transform([Number(this.lon), Number(this.lat)], 'EPSG:4326', 'EPSG:3857');
                olMapView.setCenter(coordinates);
                olMapView.setZoom(18);
                handleDelete();
                me.addSearchMarker(coordinates);
            };

            const handleDelete = function () {
                removeSearchResults();
                input.value = '';
            };

            const element = document.createElement('div');
            element.className = 'ol-search-collapsed ol-control';
            element.baseUrl = 'https://nominatim.openstreetmap.org';

            const search_icon = document.createElement('button');
            search_icon.className = 'ol-search-icon';
            search_icon.setAttribute('aria-label', CMDBuildUI.locales.Locales.gis.labels.search);
            search_icon.setAttribute('title', CMDBuildUI.locales.Locales.gis.labels.search);
            search_icon.addEventListener('click', showHideSearch);

            const label = document.createElement('label');
            label.textContent = CMDBuildUI.locales.Locales.gis.labels.search;
            label.htmlFor = 'ol-input-id';

            const input = document.createElement('input');
            input.className = 'ol-input-text';
            input.type = 'text';
            input.id = 'ol-input-id';
            input.addEventListener('keydown', handleKeyDown);

            label.appendChild(input);
            document.body.appendChild(label);

            const search_action = document.createElement('button');
            search_action.className = 'ol-search-action';
            search_action.setAttribute('aria-label', CMDBuildUI.locales.Locales.gis.labels.search);
            search_action.setAttribute('title', CMDBuildUI.locales.Locales.gis.labels.search);
            search_action.addEventListener('click', handleSearch);

            const delete_action = document.createElement('button');
            delete_action.className = 'ol-delete-action';
            delete_action.setAttribute('aria-label', CMDBuildUI.locales.Locales.gis.labels.clear);
            delete_action.setAttribute('title', CMDBuildUI.locales.Locales.gis.labels.clear);
            delete_action.addEventListener('click', handleDelete);

            element.append(search_icon, input, search_action, delete_action);

            return new ol.control.Control({
                element: element
            });
        },

        /**
         * Return the control for the feature labels
         * @returns {ol.control.Control}
         */
        getMapLabelsControl: function () {
            const me = this;
            const view = me.getView();
            const vm = this.getViewModel();
            const span = document.createElement('span');
            span.innerHTML = CMDBuildUI.locales.Locales.gis.labels.label + ': ';
            span.id = 'map-labels-description';

            const clickEvent = function () {
                const buttons = this.parentElement.querySelectorAll('button');
                buttons.forEach(btn => {
                    btn.className = '';
                    btn.setAttribute('aria-pressed', 'false');
                });

                this.className = 'active';
                this.setAttribute('aria-pressed', 'true');

                view.labelsVisibility = this.getAttribute('data-labelvisibility');
                view.fireEvent('labelvisibilitychange');
            };

            const b_hidden = document.createElement('button');
            b_hidden.innerHTML = CMDBuildUI.locales.Locales.gis.labels.hidden;
            b_hidden.setAttribute('aria-label', CMDBuildUI.locales.Locales.gis.labels.hiddentitle);
            b_hidden.setAttribute('title', CMDBuildUI.locales.Locales.gis.labels.hiddentitle);
            b_hidden.setAttribute('data-labelvisibility', 'hidden');
            b_hidden.setAttribute('aria-pressed', 'false');
            b_hidden.addEventListener('click', clickEvent);

            const b_current = document.createElement('button');
            b_current.innerHTML = CMDBuildUI.locales.Locales.gis.labels.selected;
            b_current.setAttribute('aria-label', CMDBuildUI.locales.Locales.gis.labels.selectedtitle);
            b_current.setAttribute('title', CMDBuildUI.locales.Locales.gis.labels.selectedtitle);
            b_current.setAttribute('data-labelvisibility', 'selected');
            b_current.setAttribute('aria-pressed', 'false');
            b_current.addEventListener('click', clickEvent);

            const b_all_class = document.createElement('button');
            const theObjectType = vm.get('theObjectType');
            const objectTypeName =
                ' ' + (theObjectType ? theObjectType.get('_description_translation') : vm.get('objectTypeName'));
            b_all_class.textContent = CMDBuildUI.locales.Locales.gis.labels.all + objectTypeName;
            b_all_class.setAttribute(
                'aria-label',
                CMDBuildUI.locales.Locales.gis.labels.alltitleclass + objectTypeName
            );
            b_all_class.setAttribute('title', CMDBuildUI.locales.Locales.gis.labels.alltitleclass + objectTypeName);
            b_all_class.setAttribute('data-labelvisibility', 'allclass');
            b_all_class.setAttribute('aria-pressed', 'false');
            b_all_class.addEventListener('click', clickEvent);

            const b_all = document.createElement('button');
            b_all.innerHTML = CMDBuildUI.locales.Locales.gis.labels.all;
            b_all.setAttribute('aria-label', CMDBuildUI.locales.Locales.gis.labels.alltitle);
            b_all.setAttribute('title', CMDBuildUI.locales.Locales.gis.labels.alltitle);
            b_all.setAttribute('data-labelvisibility', 'all');
            b_all.setAttribute('aria-pressed', 'false');
            b_all.addEventListener('click', clickEvent);

            switch (view.labelsVisibility) {
                case 'hidden':
                    b_hidden.className = 'active';
                    b_hidden.setAttribute('aria-pressed', 'true');
                    break;
                case 'selected':
                    b_current.className = 'active';
                    b_current.setAttribute('aria-pressed', 'true');
                    break;
                case 'allclass':
                    b_all_class.className = 'active';
                    b_all_class.setAttribute('aria-pressed', 'true');
                    break;
                case 'all':
                    b_all.className = 'active';
                    b_all.setAttribute('aria-pressed', 'true');
                    break;
            }

            const element = document.createElement('div');
            element.className = 'ol-labels-control ol-control';
            element.setAttribute('role', 'group');
            element.setAttribute('aria-labelledby', 'map-labels-description');
            element.append(span, b_hidden, b_current, b_all_class, b_all);

            return new ol.control.Control({
                element: element
            });
        },

        /**
         * Return the control to modify label size
         * @returns {ol.control.Control}
         */
        modifySizeLabels: function () {
            const view = this.getView();
            const slider = Ext.create('Ext.slider.Single', {
                labelAlign: 'top',
                floating: true,
                fieldLabel: CMDBuildUI.locales.Locales.gis.menu.font,
                width: 200,
                increment: 1,
                minValue: 8,
                maxValue: 22,
                cls: Ext.baseCSSPrefix + 'map-label-sizeslider',
                value: 13,
                listeners: {
                    change: function (slider, newValue, thumb, eOpts) {
                        // save preference
                        CMDBuildUI.util.helper.UserPreferences.updateMapLabelSize(newValue);
                        view.labelSize = newValue;
                        view.fireEvent('labelvisibilitychange');
                    },
                    blur: function (slider, event, eOpts) {
                        if (
                            !(
                                event.delegatedTarget.className ===
                                CMDBuildUI.util.helper.IconHelper.getIconId('cog', 'solid')
                            )
                        ) {
                            slider.setVisible(false);
                        }
                    }
                }
            });

            const clickEvent = function () {
                if (!slider.isVisible()) {
                    slider.showBy(infooptions, 'br-tr');
                    slider.focus();
                } else {
                    slider.setVisible(false);
                }
            };

            const el = document.createElement('div');
            el.className = 'ol-infowindow-options ol-control';

            const infooptions = document.createElement('button');
            infooptions.className = CMDBuildUI.util.helper.IconHelper.getIconId('cog', 'solid');
            infooptions.setAttribute('aria-label', CMDBuildUI.locales.Locales.gis.menu.options);
            infooptions.setAttribute('title', CMDBuildUI.locales.Locales.gis.menu.options);
            infooptions.setAttribute('data-qtip', CMDBuildUI.locales.Locales.gis.menu.options);
            infooptions.addEventListener('click', clickEvent);

            el.append(infooptions);

            return new ol.control.Control({
                element: el
            });
        },

        /**
         * Return the control for show/hide info window
         * @returns {ol.control.Control}
         */
        getInfoControl: function () {
            const view = this.getView();
            const clickEvent = function () {
                const hidden = view.infoWindow.element.hidden;
                view.infoWindow.element.hidden = !hidden;
                if (hidden) {
                    info_icon.className = 'cmdbuildicon-gis-hideinfo';
                    info_icon.setAttribute('aria-label', CMDBuildUI.locales.Locales.gis.labels.hideinfowindow);
                    info_icon.setAttribute('title', CMDBuildUI.locales.Locales.gis.labels.hideinfowindow);
                    info_icon.setAttribute('data-qtip', CMDBuildUI.locales.Locales.gis.labels.hideinfowindow);
                } else {
                    info_icon.className = 'cmdbuildicon-gis-showinfo';
                    info_icon.setAttribute('aria-label', CMDBuildUI.locales.Locales.gis.labels.showinfowindow);
                    info_icon.setAttribute('title', CMDBuildUI.locales.Locales.gis.labels.showinfowindow);
                    info_icon.setAttribute('data-qtip', CMDBuildUI.locales.Locales.gis.labels.showinfowindow);
                }
            };

            const div = document.createElement('div');
            div.className = 'ol-infowindow ol-control';

            const info_icon = document.createElement('button');
            info_icon.className = 'cmdbuildicon-gis-hideinfo';
            info_icon.setAttribute('aria-label', CMDBuildUI.locales.Locales.gis.labels.hideinfowindow);
            info_icon.setAttribute('title', CMDBuildUI.locales.Locales.gis.labels.hideinfowindow);
            info_icon.setAttribute('data-qtip', CMDBuildUI.locales.Locales.gis.labels.hideinfowindow);
            info_icon.addEventListener('click', clickEvent);

            div.append(info_icon);

            return new ol.control.Control({
                element: div
            });
        },

        /**
         * Return the control for modify map source
         * @returns {ol.control.Control}
         */
        modifyMapSource: function () {
            const view = this.getView();
            const mapSource = this.mapSource;
            let preferredMapLayer = CMDBuildUI.util.helper.UserPreferences.get(
                CMDBuildUI.model.users.Preference.preferredMapLayer
            );

            const clickEvent = function () {
                const sourceMap = Ext.Array.findBy(
                    view.getOlMap().getLayers().getArray(),
                    function (item, index, allitems) {
                        return item.get('name') == CMDBuildUI.model.gis.GeoAttribute.BASETILE;
                    }
                );

                switch (preferredMapLayer) {
                    case mapSource.satellite.value:
                        this.className = mapSource.satellite.icon;
                        this.setAttribute('aria-label', CMDBuildUI.locales.Locales.gis.sourceMap.satellite);
                        this.setAttribute('title', CMDBuildUI.locales.Locales.gis.sourceMap.satellite);
                        this.setAttribute('data-qtip', CMDBuildUI.locales.Locales.gis.sourceMap.satellite);
                        preferredMapLayer = mapSource.default.value;
                        sourceMap.setSource(new ol.source.OSM());
                        break;
                    default:
                        this.className = mapSource.default.icon;
                        this.setAttribute('aria-label', CMDBuildUI.locales.Locales.gis.sourceMap.default);
                        this.setAttribute('title', CMDBuildUI.locales.Locales.gis.sourceMap.default);
                        this.setAttribute('data-qtip', CMDBuildUI.locales.Locales.gis.sourceMap.default);
                        preferredMapLayer = mapSource.satellite.value;
                        sourceMap.setSource(
                            new ol.source.XYZ({
                                url: 'https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}',
                                maxZoom: 19
                            })
                        );
                        break;
                }

                CMDBuildUI.util.helper.UserPreferences.updateMapLayer(preferredMapLayer);
            };

            const div = document.createElement('div');
            div.className = 'ol-mapSource ol-control';

            const mapSource_icon = document.createElement('button');
            switch (preferredMapLayer) {
                case mapSource.satellite.value:
                    mapSource_icon.className = mapSource.default.icon;
                    mapSource_icon.setAttribute('aria-label', CMDBuildUI.locales.Locales.gis.sourceMap.default);
                    mapSource_icon.setAttribute('title', CMDBuildUI.locales.Locales.gis.sourceMap.default);
                    mapSource_icon.setAttribute('data-qtip', CMDBuildUI.locales.Locales.gis.sourceMap.default);
                    break;
                default:
                    mapSource_icon.className = mapSource.satellite.icon;
                    mapSource_icon.setAttribute('aria-label', CMDBuildUI.locales.Locales.gis.sourceMap.satellite);
                    mapSource_icon.setAttribute('title', CMDBuildUI.locales.Locales.gis.sourceMap.satellite);
                    mapSource_icon.setAttribute('data-qtip', CMDBuildUI.locales.Locales.gis.sourceMap.satellite);
                    break;
            }
            mapSource_icon.addEventListener('click', clickEvent);

            div.append(mapSource_icon);

            return new ol.control.Control({
                element: div
            });
        },

        /**
         * Adds a search result marker on the map at the given coordinates.
         * @param {ol.Coordinate} coordinates
         */
        addSearchMarker: function (coordinates) {
            const view = this.getView();
            this.removeSearchMarker();

            const markerEl = document.createElement('div');
            markerEl.className = Ext.baseCSSPrefix + 'ol-search-marker';
            markerEl.innerHTML =
                '<div class="' + CMDBuildUI.util.helper.IconHelper.getIconId('map-marker-alt', 'solid') + '"></div>';
            view.getEl().dom.append(markerEl);

            const marker = new ol.Overlay({
                element: markerEl,
                positioning: 'bottom-center',
                stopEvent: false
            });
            marker.setPosition(coordinates);
            view.getOlMap().addOverlay(marker);
            view._searchMarker = marker;
        },

        /**
         * Removes the search result marker from the map if present.
         */
        removeSearchMarker: function () {
            const view = this.getView();
            if (view._searchMarker) {
                view.getOlMap().removeOverlay(view._searchMarker);
                view._searchMarker = null;
            }
        }
    }
});
