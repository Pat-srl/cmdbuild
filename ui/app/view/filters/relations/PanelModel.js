Ext.define('CMDBuildUI.view.filters.relations.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.filters-relations-panel',

    data: {
        storeinfo: {
            data: [],
            sorters: []
        }
    },

    formulas: {
        updateStoreData: {
            bind: {
                type: '{objectType}',
                name: '{objectTypeName}',
                filter: '{theFilter}'
            },
            get: function (data) {
                var domains = [];
                var me = this;
                var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.name, data.type);
                var defaultdata = data.filter && data.filter.get("configuration").relation || [];
                item.getDomains().then(function (itemdomains, loaded) {
                    var hierarchy = item.getHierarchy();

                    function addToDomainsList(domain, destination, description, destIsProcess, direction) {
                        if (destination) {
                            // get destination object
                            var destObj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                destination,
                                destIsProcess ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass
                            );

                            var v = Ext.Array.findBy(defaultdata, function (d) {
                                if (domain.getId() === d.domain && direction === d.direction) {
                                    return d;
                                }
                            });

                            if (destObj) {
                                // add domain in list
                                domains.push({
                                    domain: domain.getId(),
                                    description: description,
                                    destination: destination,
                                    destinationDescription: destObj.getTranslatedDescription(),
                                    destinationIsProcess: destIsProcess,
                                    direction: direction,
                                    mode: v && v.type || null,
                                    cards: v && v.cards || [],
                                    filter: v && v.filter || null
                                });
                            }
                        }
                    }

                    itemdomains.each(function (d) {
                        var source = d.get("source");
                        var destination = d.get("destination");
                        // direct domain
                        if (
                            (Ext.Array.contains(hierarchy, source) &&
                                !Ext.Array.contains(d.get("disabledSourceDescendants"), data.name))
                            || (source == CMDBuildUI.util.helper.ModelHelper.systemClasses.class)
                        ) {
                            addToDomainsList(
                                d,
                                destination,
                                d.get("_descriptionDirect_translation") || d.get("descriptionDirect"),
                                d.get("destinationProcess"),
                                "_1"
                            );
                        }

                        // inverse domain
                        if (
                            (Ext.Array.contains(hierarchy, destination) &&
                                !Ext.Array.contains(d.get("disabledDestinationDescendants"), data.name))
                            || (destination == CMDBuildUI.util.helper.ModelHelper.systemClasses.class)
                        ) {
                            addToDomainsList(
                                d,
                                source,
                                d.get("_descriptionInverse_translation") || d.get("descriptionInverse"),
                                d.get("sourceProcess"),
                                "_2"
                            );
                        }

                    });

                    var object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.name, data.type),
                        sorters = CMDBuildUI.util.helper.GridHelper.getStoreSorters(object);

                    me.set("storeinfo.sorters", sorters);
                    me.set("storeinfo.data", domains);
                });
            }
        }
    },

    stores: {
        relations: {
            model: 'CMDBuildUI.model.domains.Filter',
            autoDestroy: true,
            data: '{storeinfo.data}',
            sorters: '{storeinfo.sorters}',
            proxy: {
                type: 'memory'
            }
        },

        selected: {
            model: 'CMDBuildUI.model.domains.Filter',
            autoDestroy: true,
            data: '{storeinfo.data}',
            sorters: '{storeinfo.sorters}',
            proxy: {
                type: 'memory'
            }
        }
    }

});