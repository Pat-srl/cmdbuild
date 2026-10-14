Ext.define('CMDBuildUI.view.map.tab.cards.geoattributesGrid.geoattributesGridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-tab-cards-geoattributesgrid',

    /**
     *
     * @param {Ext.view.Table} view
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * @param {Object} item
     * @param {Event} e
     * @param {Ext.data.Model} record
     */
    onAddGeoValue: function (view, rowIndex, colIndex, item, e, record) {
        const me = this,
            vm = this.getViewModel(),
            viewMap = this.getMapView(),
            record_type = record.get('_type');

        if (record_type === CMDBuildUI.model.gis.GeoAttribute.subtype.shape || record_type === CMDBuildUI.model.gis.GeoAttribute.subtype.geotiff) {
            const title = CMDBuildUI.locales.Locales.common.actions.add + ' ' + record.get('text'),
                popup = CMDBuildUI.util.Utilities.openPopup(
                    null,
                    title,
                    {
                        xtype: 'geoserverlayer',
                        viewModel: {
                            data: {
                                record: record
                            }
                        },
                        listeners: {
                            closepopup: function (form, response) {
                                if (response.name) {
                                    me.onViewGeoValue(view, null, null, null, null, response, null);
                                    viewMap.fireEvent("labelvisibilitychange");
                                    record.set('x', response.x);
                                    record.set('y', response.y);
                                }
                                popup.close();
                            }
                        }
                    },
                    null,
                    {
                        width: 400,
                        height: 250
                    });
        } else {
            vm.set("drawmode", true);
            vm.set('buttonsSaveCancel.hidden', false);
            viewMap.draw({
                record: record,
                listeners: {
                    drawend: {
                        fn: function (points) {
                            me.setPoints(record, points);
                            vm.set('buttonsSaveCancel.saveDisabled', false);
                        }
                    }
                },
                operation: 'add'
            });
        }
    },

    /**
     *
     * @param {Ext.view.Table} view
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * @param {Object} item
     * @param {Event} e
     * @param {Ext.data.Model} geovalue
     */
    onEditGeoValue: function (view, rowIndex, colIndex, item, e, geovalue) {
        const me = this,
            vm = this.getViewModel(),
            viewMap = this.getMapView(),
            record_type = geovalue.get('_type');

        if (record_type === CMDBuildUI.model.gis.GeoAttribute.subtype.shape || record_type === CMDBuildUI.model.gis.GeoAttribute.subtype.geotiff) {
            const title = CMDBuildUI.locales.Locales.common.actions.edit + ' ' + geovalue.get('text'),
                popup = CMDBuildUI.util.Utilities.openPopup(
                    null,
                    title,
                    {
                        xtype: 'geoserverlayer',
                        viewModel: {
                            data: {
                                record: geovalue
                            }
                        },
                        listeners: {
                            closepopup: function (form, response) {
                                if (response.name) {
                                    me.onViewGeoValue(view, null, null, null, null, response, null);
                                    viewMap.fireEvent("labelvisibilitychange");
                                    geovalue.set('x', response.x);
                                    geovalue.set('y', response.y);
                                }
                                popup.close();
                            }
                        }
                    },
                    null,
                    {
                        width: 400,
                        height: 250
                    });
        } else {
            vm.set("drawmode", true);
            vm.set('buttonsSaveCancel.hidden', false);
            viewMap.modify({
                record: geovalue,
                listeners: {
                    modifystart: {
                        fn: function (points) {
                            vm.set('buttonsSaveCancel.saveDisabled', false);
                        }
                    },
                    modifyend: {
                        fn: function (points) {
                            me.setPoints(geovalue, points);
                        }
                    }
                },
                operation: 'edit'
            });
        }
    },

    /**
     *
     * @param {Ext.view.Table} view
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * @param {Object} item
     * @param {Event} e
     * @param {Ext.data.Model} geovalue
     */
    onRemoveGeoValue: function (view, rowIndex, colIndex, item, e, geovalue) {
        const vm = this.getViewModel(),
            viewMap = this.getMapView(),
            record_type = geovalue.get('_type'),
            listeners = {
                clear: {
                    fn: function () {
                        geovalue.clearValues();
                    }
                }
            };

        vm.set('buttonsSaveCancel.hidden', false);
        vm.set('buttonsSaveCancel.saveDisabled', false);

        if (record_type !== CMDBuildUI.model.gis.GeoAttribute.subtype.shape && record_type !== CMDBuildUI.model.gis.GeoAttribute.subtype.geotiff) {
            vm.set("drawmode", true);
            viewMap.clear({
                record: geovalue,
                listeners: listeners
            })
        } else {
            viewMap.hideshow({
                record: geovalue,
                show: false,
                listeners: listeners
            })
        }
    },

    /**
     *
     * @param {Ext.view.Table} view
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * @param {Object} item
     * @param {Event} e
     * @param {Ext.data.Model} geovalue
     */
    onViewGeoValue: function (view, rowIndex, colIndex, item, e, geovalue) {
        this.getMapView().getController().animateMap(geovalue)
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelButtonClick: function (button, e, eOpts) {
        const me = this,
            view = this.getView(),
            vm = this.getViewModel(),
            store = view.getStore(),
            viewMap = this.getMapView();

        vm.set('buttonsSaveCancel', {
            hidden: true,
            saveDisabled: true
        });

        store.getRange().forEach(function (item, index, array) {
            const type = item.get('_type');
            if (type !== CMDBuildUI.model.gis.GeoAttribute.subtype.shape && type !== CMDBuildUI.model.gis.GeoAttribute.subtype.geotiff) {
                if (item.dirty) {
                    // Runs when you press delete on a saved item
                    if (!item.hasValues() && (item.getPrevious('x') || (item.getPrevious('points') && item.getPrevious('points')[0].x))) {
                        var values;
                        switch (type) {
                            case 'point':
                                values = [item.getPrevious('x'), item.getPrevious('y')];
                                break;
                            case 'linestring':
                                values = item.getPrevious('points');
                                break;
                            case 'polygon':
                                values = [item.getPrevious('points')];
                                break;
                        }
                        const points = CMDBuildUI.map.util.Util.olCoordinatesToObject(type, values);
                        me.setPoints(item, points);
                    } else if (item.phantom) {
                        // Runs when you press delete on an item you just inserted but not yet saved
                        item.clearValues();
                    }
                }
                viewMap.clean(item);
            } else if (item.getPrevious('x') && !item.hasValues()) {
                viewMap.hideshow({
                    record: item,
                    show: true,
                    listeners: {
                        set: {
                            fn: function () {
                                me.setPoints(item, {
                                    x: item.getPrevious('x'),
                                    y: item.getPrevious('y')
                                });
                            }
                        }
                    }
                });
            }
        });

        vm.set("drawmode", false);
        viewMap.fireEvent('drawmodechange');
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveButtonClick: function (button, e, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        const promises = [],
            view = this.getView(),
            vm = this.getViewModel(),
            store = view.getStore(),
            viewMap = this.getMapView(),
            tabpanel = view.up('map-tab-tabpanel'),
            cancelBtn = view.down("#cancelButton");

        button.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([button, cancelBtn]);

        store.getModifiedRecords().forEach(function (item, index, array) {
            const deferred = new Ext.Deferred();
            if (item.get("_type") === CMDBuildUI.model.gis.GeoAttribute.subtype.shape || item.get("_type") === CMDBuildUI.model.gis.GeoAttribute.subtype.geotiff) {
                if (item.getPrevious('x') && !item.hasValues()) {
                    const name = item.get("name") || item.get("_attr"),
                        url = CMDBuildUI.util.api.Classes.getGeoLayersUrl(
                            item.get('_owner_type'),
                            item.get('_owner_id')) + Ext.String.format('/{0}', name);

                    Ext.Ajax.request({
                        url: url,
                        method: 'DELETE',
                        success: function () {
                            item.set('x', null);
                            item.set('y', null);
                            deferred.resolve();
                        },
                        failure: function () {
                            deferred.reject();
                        }
                    });

                    Ext.Deferred.all([deferred.promise]).then(function () {
                        vm.set('buttonsSaveCancel', {
                            hidden: true,
                            saveDisabled: true
                        });

                        viewMap.fireEvent("labelvisibilitychange");
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    });
                }
            } else {
                const url = CMDBuildUI.util.api.Classes.getGeoValuesUrl(
                    item.get('_owner_type'),
                    item.get('_owner_id')) + Ext.String.format('/{0}', item.get('_attr'));

                //the new records
                if (item.phantom) {
                    if (item.hasValues()) {
                        //creates new geovalues
                        Ext.Ajax.request({
                            url: url,
                            method: 'PUT',
                            jsonData: item.getJsonData(),
                            success: function (response, option) {
                                item.commit(); //should change the id with the new one sent from the server
                                deferred.resolve(item);
                            },
                            failure: function (response, option) {
                                const errorMessage = JSON.parse(response.responseText).messages[0].message;
                                deferred.reject(errorMessage);
                            }
                        });
                        promises.push(deferred.promise);
                    }
                } else {
                    //modify existings geoValues
                    if (item.hasValues()) {
                        Ext.Ajax.request({
                            url: url,
                            method: 'PUT',
                            jsonData: item.getJsonData(),
                            success: function (response, option) {
                                item.commit();
                                deferred.resolve(item);
                            },
                            failure: function (response, option) {
                                const errorMessage = JSON.parse(response.responseText).messages[0].message;
                                deferred.reject(errorMessage);
                            }
                        });
                    } else {
                        //erases existing geovalues
                        Ext.Ajax.request({
                            url: url,
                            method: 'DELETE',
                            success: function (response, option) {
                                item.commit(); //should change the id erasing the old one
                                vm.get("theObject").getGeoValues().then(function (geovalues) {
                                    geovalues.remove(geovalues.getById(item.getId()));
                                });
                                deferred.resolve(item);
                                vm.set("settingsMap.geometryDelete", true);
                            },
                            failure: function (response, option) {
                                const errorMessage = JSON.parse(response.responseText).messages[0].message;
                                deferred.reject(errorMessage);
                            }
                        });
                    }
                    promises.push(deferred.promise);
                }
            }
        }, this);

        Ext.Deferred.all(promises).then(
            //onFulfill
            function (resolved) {
                CMDBuildUI.util.Utilities.enableFormButtons([button, cancelBtn]);
                vm.set('buttonsSaveCancel', {
                    hidden: true,
                    saveDisabled: true
                });
                resolved.forEach(function (item, index, array) {
                    viewMap.clean(item);
                }, this);

                vm.set("drawmode", false);
                viewMap.fireEvent('drawmodechange');
                Ext.GlobalEvents.fireEventArgs("refreshCard", [true]);
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            },

            //onRejected
            function (rejected) {
                CMDBuildUI.util.Utilities.enableFormButtons([button, cancelBtn]);
                CMDBuildUI.util.Logger.log(rejected, CMDBuildUI.util.Logger.levels.error);
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            },
            Ext.emptyFn, this);
    },

    privates: {

        /**
         *
         * @param {Ext.data.Model} record
         * @param {Object} points
         */
        setPoints: function (record, points) {
            const type = record.get("_type");
            switch (type) {
                case 'point':
                case 'shape':
                case 'geotiff':
                    record.set('x', points.x);
                    record.set('y', points.y);
                    break;
                case 'linestring':
                case 'polygon':
                    record.set('points', points);
                    break;
            }
        },

        /**
         *
         * @returns {Ext.tab.Panel}
         */
        getMapView: function () {
            return this.getView().getMapContainerView().getViewMap();
        }
    }

});