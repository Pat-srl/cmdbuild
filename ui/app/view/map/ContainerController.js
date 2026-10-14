Ext.define('CMDBuildUI.view.map.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-container',

    listen: {
        global: {
            objectidchanged: 'onObjectIdChanged',
            applythematism: 'onApplyThematism',
            cardcreated: 'onRefreshBtnClick',
            cardupdated: 'onRefreshBtnClick',
            carddeleted: 'onCardDeleted'
        }
    },

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            selectdeselectall: 'onSelectDeselectAllMap'
        }
    },

    /**
     *
     * @param {*} view
     * @param {*} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const me = this;
        const container = view.getCardView();
        const vm = view.lookupViewModel();

        if (container) {
            const refreshBtn = container.down('#refreshBtn');
            if (refreshBtn) {
                refreshBtn.addListener('click', me.onRefreshBtnClick, me);
            }

            if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.navigationTreeEnabled)) {
                const gisNavigation = Ext.create('CMDBuildUI.model.navigationTrees.DomainTree', {
                    _id: "gisnavigation"
                });

                gisNavigation.load({
                    params: {
                        treeMode: 'tree'
                    },
                    callback: function (record, operation, success) {
                        if (success) {
                            vm.set("gisNavigation", record);
                        }
                    }
                });
            }

            vm.bind('{theObjectType}',
                function (item) {
                    if (item) {
                        const addbutton = container.down("#addcardgis");
                        addbutton.setText(CMDBuildUI.locales.Locales.classes.cards.addcard + ' ' + item.getTranslatedDescription());

                        var permission = CMDBuildUI.model.base.Base.permissions.add;
                        if (item.isProcess) {
                            permission = CMDBuildUI.model.base.Base.permissions.start;
                        }

                        const getGeoRecords = function (attrs, item) {
                            const georecords = [];
                            Ext.Array.forEach(attrs, function (r, i, allitems) {
                                if ((r.get("subtype") !== "shape" && r.get("subtype") !== "geotiff") && item.getId() === r.get("owner_type")) {
                                    georecords.push(r);
                                }
                            })
                            return georecords;
                        }

                        const createMenu = function (menu, records, child) {
                            Ext.Array.forEach(records, function (geoattribute, index, allitems) {
                                const menuitem = menu.add({
                                    text: geoattribute.get("text"),
                                    _itemdescription: geoattribute.get("text"),
                                    _geoattribute: geoattribute,
                                    iconCls: CMDBuildUI.model.menu.MenuItem.icons.klass,
                                    disabled: geoattribute.get("writable") ? false : true,
                                    listeners: {
                                        click: {
                                            fn: me.onAddGisButtonClick,
                                            scope: me
                                        }
                                    }
                                });

                                if ((child && !child.get(permission)) || !Ext.Array.contains(geoattribute.get("visibility"), item.getId())) {
                                    menuitem.disable();
                                }
                            });
                        }

                        item.getGeoAttributes().then(function (attributes) {
                            // set geoAttributes store
                            vm.set("geoAttributesStore", attributes);

                            if (item.get("prototype")) {
                                addbutton.setMenu([]);
                                const menu = addbutton.getMenu();
                                item.getChildren(true).forEach(function (child) {
                                    child.getGeoAttributes().then(function (attributes) {
                                        const records = getGeoRecords(attributes.getRange(), child);
                                        if (!Ext.isEmpty(records)) {
                                            createMenu(menu, records, child);
                                            addbutton.enable(true);
                                        }
                                    });
                                });
                            } else if (item.get(permission)) {
                                const records = getGeoRecords(attributes.getRange(), item);
                                if (records.length === 1) {
                                    const geoRecord = records[0];
                                    geoRecord.get("writable") ? addbutton.enable() : addbutton.disable();
                                    addbutton.setText(CMDBuildUI.locales.Locales.classes.cards.addcard + ' ' + geoRecord.get("text"));
                                    addbutton._itemdescription = geoRecord.get("text");
                                    addbutton._geoattribute = geoRecord;
                                    addbutton.addListener("click", me.onAddGisButtonClick, me);
                                } else if (records.length > 1) {
                                    addbutton.setMenu([]);
                                    addbutton.enable();
                                    const menu = addbutton.getMenu();
                                    createMenu(menu, records);
                                }
                            }
                        });
                    }
                });
        }
    },

    /**
     *
     * @param {String} newId
     */
    onObjectIdChanged: function (newId) {
        this.getViewModel().set("objectId", Ext.num(newId));
    },

    /**
     *
     * @param {String} id
     */
    onApplyThematism: function (id) {
        const vm = this.getViewModel(),
            currentThematismId = vm.get("thematismId");
        if (!Ext.isEmpty(currentThematismId) && currentThematismId == id) {
            //if the applied thematism is modifyed by the panel
            this.refreshThematism();
        } else {
            vm.set("thematismId", id);
        }
    },

    /**
     *
     * @param {*} btn
     * @param {*} eOpts
     * @returns
     */
    onAddGisButtonClick: function (btn, eOpts) {
        const me = this,
            viewMap = me.getView().getViewMap(),
            georecord = btn._geoattribute.createEmptyGeoValue(),
            gisButton = btn.getItemId() == "addcardgis" ? btn : btn.up("#addcardgis");

        gisButton.setPressed(true);
        if (gisButton.addGeoAttribute) {
            return;
        }
        gisButton.addGeoAttribute = true;

        viewMap.draw({
            record: georecord,
            listeners: {
                drawend: {
                    fn: function (point) {
                        const popup = CMDBuildUI.util.Navigation.getManagementDetailsWindow() || CMDBuildUI.util.Navigation.getManagementDetailsWindow(true);
                        popup.removeAll();
                        popup.add({
                            xtype: 'classes-cards-card-create',
                            padding: 10,
                            viewModel: {
                                data: {
                                    objectTypeName: btn._geoattribute.get("owner_type"),
                                    action: 'new',
                                    detailsWindowTitle: CMDBuildUI.locales.Locales.classes.cards.addcard + " " + btn._itemdescription
                                }
                            },
                            defaultValuesForCreate: {
                                _point: point
                            },
                            buttons: [{
                                text: CMDBuildUI.locales.Locales.common.actions.cancel,
                                itemId: 'cancelbtn',
                                ui: 'secondary-action-small',
                                handler: function () {
                                    popup.close();
                                    gisButton.setPressed(false);
                                    gisButton.addGeoAttribute = false;
                                }
                            }, {
                                text: CMDBuildUI.locales.Locales.common.actions.save,
                                formBind: true, //only enabled once the form is valid
                                disabled: true,
                                ui: 'management-primary-small',
                                autoEl: {
                                    'data-testid': 'map-card-create-save'
                                },
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.common.actions.save'
                                },
                                handler: function (button, e) {
                                    const cancelBtn = this.up().down("#cancelbtn");
                                    button.showSpinner = true;
                                    CMDBuildUI.util.helper.FormHelper.startSavingForm();
                                    CMDBuildUI.util.Utilities.disableFormButtons([button, cancelBtn]);
                                    popup.down("classes-cards-card-create").getController().saveForm({
                                        failure: function () {
                                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                                            CMDBuildUI.util.Utilities.enableFormButtons([button, cancelBtn]);
                                        }
                                    }).then(function (record) {
                                        if (georecord.get("_type") === "point") {
                                            georecord.set('x', point.x);
                                            georecord.set('y', point.y);
                                        } else {
                                            georecord.set("points", point);
                                        }

                                        Ext.Ajax.request({
                                            url: CMDBuildUI.util.api.Classes.getGeoValuesUrl(
                                                record.get('_type'),
                                                record.get('_id')) + Ext.String.format('/{0}', georecord.get('_attr')),
                                            method: 'PUT',
                                            jsonData: georecord.getJsonData(),
                                            callback: function () {
                                                viewMap.clean(georecord);
                                                me.redirectTo(CMDBuildUI.util.Navigation.getClassBaseUrl(
                                                    record.get("_type"),
                                                    record.getId()
                                                ));
                                                popup.close();
                                                gisButton.setPressed(false);
                                                gisButton.addGeoAttribute = false;
                                                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                                            }
                                        });
                                    }).otherwise(function () {
                                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                                        CMDBuildUI.util.Utilities.enableFormButtons([button, cancelBtn]);
                                    });
                                }
                            }]
                        });
                        popup.on('close', function () {
                            viewMap.clean(georecord);
                            gisButton.setPressed(false);
                            gisButton.addGeoAttribute = false;
                        });
                    },
                    scope: this
                }
            },
            operation: 'add'
        });
    },

    /**
     * Select or deselect all items on map, list tab and navigation tree tab based on the value of select
     * @param {Boolean} select
     */
    onSelectDeselectAllMap: function (select) {
        const view = this.getView();
        const vm = this.getViewModel();
        const viewMap = view.getViewMap();
        const olMap = viewMap.getOlMap();
        const listTab = view.getListTab();
        const navTreeTab = view.getNavigationTreeTab();
        const interaction = viewMap.getOl_interaction_select(olMap);

        vm.set("settingsMap.selectAll", select);

        if (select) {
            const listStore = listTab.getStore();
            const navTreeStore = navTreeTab.getStore();
            const objectTypeName = vm.get("objectTypeName");
            const selected = [];

            Ext.Array.forEach(olMap.getLayers().getArray(), function (layer, index, allLayers) {
                const layerFeatures = viewMap.getOlLayerSource(layer).getFeatures ? viewMap.getOlLayerSource(layer).getFeatures() : [];
                Ext.Array.forEach(layerFeatures, function (feature, ind, allFeature) {
                    if (objectTypeName == feature.values_._geovalue.get("_owner_type")) {
                        selected.push(feature);
                    }
                });
            });

            interaction.dispatchEvent(new ol.interaction.Select.SelectEvent('select', selected, [], {
                selectAll: true
            }));

            listTab.getSelectionModel().select(listStore.getRange(0, listStore.getTotalCount()), true, true);
            Ext.Array.forEach(navTreeStore.getRange(0, navTreeStore.getTotalCount()), function (item, index, allitems) {
                const type = item.get("type");
                if (type == objectTypeName && type != item.get("description")) {
                    navTreeTab.getSelectionModel().select(item, true, true);
                }
            });
        } else {
            interaction.dispatchEvent(new ol.interaction.Select.SelectEvent('select', [], []));
            listTab.getSelectionModel().deselectAll(true);
            navTreeTab.getSelectionModel().deselectAll(true);
        }
    },

    /**
    * This function is responsible for adding and removing olLayers
    * @param {*} layerStore
    * @param {*} filters
    * @param {*} eOpts
    */
    onFilterChange: function (layerStore, filters, eOpts) {
        const view = this.getView(),
            //not filtered records
            dataSource = layerStore.getDataSource(),
            //filtered records
            data = layerStore.getData(),
            viewMap = view.getViewMap(),
            olMap = viewMap.getOlMap();

        var hasRemoved = false,
            hasAdded = false;

        dataSource.getRange().forEach(function (item, index, array) {
            if (data.isItemFiltered(item)) {
                // must remove olLayer. If already removed doesn't break
                viewMap.getController().removeOlLayer(olMap, item.get('ollayername'));
                hasRemoved = true;
            } else {
                if (item.get('type') == CMDBuildUI.model.gis.GeoAttribute.type.geometry) {
                    //add olLayer, doesn't add if already exist
                    if (!view.getOlLayer(olMap, item.get('ollayername'))) {
                        viewMap.addOlLayer(olMap, item);
                        hasAdded = true;
                    }
                }
            }
        });

        if (hasAdded) {
            viewMap.getController().delayloadfeatures(hasRemoved);
        }
    },

    /**
     *
     * @param {Ext.data.ChainedStore} store
     * @param {Ext.data.Model} record
     * @param {String} operation
     * @param {String[]} modifiedFieldNames
     * @param {object} details
     * @param {object} eOpts
     */
    onLayerStoreUpdate: function (store, record, operation, modifiedFieldNames, details, eOpts) {
        if (Ext.Array.contains(modifiedFieldNames, 'checked')) {
            const view = this.getView(),
                olMap = view.getViewMap().getOlMap();
            view.setVisibleOlLayer(olMap, record.get('ollayername'), record.get('checked'));
        }
    },

    /**
     *
     * @param {*} hash
     */
    onHashMapUpdate: function (hash) {
        const vm = this.lookupViewModel(),
            featureStore = vm.get("featureStore"),
            shapefeatureStore = vm.get("shapeFeatureStore"),
            viewMap = this.getViewMap(),
            keyValues = hash.keyValue;

        if (featureStore && shapefeatureStore) {
            const olMap = viewMap.getOlMap();

            for (var key in keyValues) {
                const checked = hash.get(key);

                var geovalueindex = -1;
                do {
                    geovalueindex = featureStore.find('_owner_id', key, ++geovalueindex);
                    if (geovalueindex != -1) {
                        const geovalue = featureStore.getAt(geovalueindex);
                        geovalue.set('checked', checked);
                        if (olMap) {
                            viewMap.setVisibleOlFeature(olMap, geovalue.get('ollayername'), geovalue, 'checked', checked);
                        }
                    }
                } while (geovalueindex != -1);

                var shapefeatureindex = -1;
                do {
                    shapefeatureindex = shapefeatureStore.find('_owner_id', key, ++shapefeatureindex);
                    if (shapefeatureindex != -1) {
                        const shapefeature = shapefeatureStore.getAt(shapefeatureindex);
                        shapefeature.set('checked', checked);
                        if (olMap) {
                            viewMap.setVisibleOlGeoFeature(olMap, '_', shapefeature, CMDBuildUI.model.gis.GeoLayer.checked, checked);
                        }
                    }
                } while (shapefeatureindex != -1);
            }
        }
    },

    privates: {

        /**
         * This function removes the current theThematism and after that sets it again. In this process the thematismId doesn't change
         */
        refreshThematism: function () {
            const vm = this.getViewModel(),
                theThematism = vm.get("theThematism");

            vm.set("theThematism", null);

            Ext.asap(function () {
                theThematism.calculateResults(function () {
                    vm.set("theThematism", theThematism);
                });
            });
        },

        /**
         *
         * @param {*} record
         */
        onRefreshBtnClick: function (record) {
            const view = this.getView(),
                mapView = view.getViewMap(),
                // Used to see if argument is a view or a record
                keepSelection = record && record.xtype ? false : true;

            Ext.GlobalEvents.fireEventArgs("refreshMap", [keepSelection]);
            if (mapView.infoWindow._current) {
                mapView.removeInfoWindow();
            }
        },

        /**
         *
         */
        onCardDeleted: function () {
            const view = this.getView(),
                vm = this.getViewModel(),
                mapView = view.getViewMap();

            vm.set("theObject", null);
            vm.set("objectId", null);
            mapView.infoWindow._current = null;
            mapView.infoWindow._ownerId = null;
            this.onRefreshBtnClick();
        }
    }
});