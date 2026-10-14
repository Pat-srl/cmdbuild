Ext.define('CMDBuildUI.view.map.tab.cards.geoattributesGrid.geoattributesGridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.map-tab-cards-geoattributesgrid',

    data: {
        buttonsSaveCancel: {
            hidden: true,
            saveDisabled: true
        },
        geoAttributesCardData: []
    },

    formulas: {
        storeCalculation: {
            bind: '{theObject}',
            get: function (theObject) {
                const me = this;
                if (theObject) {
                    const objectTypeName = theObject.get('_type'),
                        objectTypeInstance = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName),
                        promises = [
                            theObject.getGeoValues(),
                            theObject.getGeoLayers()
                        ];

                    Ext.Deferred.all(promises).then(function (resolved) {
                        const view = me.getView();
                        if (view && !view.destroyed) {
                            const geovalues = resolved[0],
                                geolayers = resolved[1],
                                geoAttributes = Ext.Array.filter(me.get("geoAttributesStore").getRange(), function (item, index, array) {
                                    return Ext.Array.contains(objectTypeInstance.getHierarchy(), item.get("owner_type"));
                                }),
                                d = Ext.Array.map(geoAttributes, function (geoattribute, index, array) {
                                    const i = geovalues.find('ollayername', geoattribute.get('ollayername')),
                                        l = geolayers.find('ollayername', geoattribute.get('ollayername')),
                                        description = geoattribute.get('_description_translation');
                                    var geovalue;

                                    if (i != -1) {
                                        geovalue = geovalues.getAt(i);
                                    } else if (l != -1) {
                                        geovalue = geolayers.getAt(l);
                                    }
                                    if (geovalue) {
                                        geovalue.set('_attr_description', description);
                                        geovalue.set('_can_write', geoattribute.get('writable'));
                                        //the geovalue exists for the given geoAttribute
                                        return geovalue;
                                    } else {
                                        //the geovalue doesn't exists for the given geoAttribute
                                        return geoattribute.createEmptyGeoValue(theObject.getId());
                                    }
                                }, this);

                            me.set("geoAttributesCardData", d);
                        }
                    }, Ext.emptyFn, Ext.emptyFn, this);
                }
            }
        }
    },

    stores: {
        geoAttributesCardStore: {
            model: 'CMDBuildUI.model.gis.GeoValue',
            proxy: 'memory',
            data: '{geoAttributesCardData}'
        }
    }
});
