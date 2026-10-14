Ext.define('CMDBuildUI.view.administration.components.filterpanels.relationsfilters.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-filterpanels-relationfilters-panel',

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
                this.populateRelationStore(data);
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
    },

    populateRelationStore: function (data) {
        var domains = [];
        var me = this;
        var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.name, data.type);
        var defaultdata = data.filter && data.filter.get("configuration").relation || [];
        item.getDomains().then(function (itemdomains, loaded) {
            if (!me.destroyed) {
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
                        if (v && v.cards && v.cards.length) {
                            Ext.Array.forEach(v.cards, function (card) {
                                card.id = card.id + ""; // this solve the proble ov numeric id
                            });
                        }
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
                            d.getTranslatedDescriptionDirect(),
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
                            d.getTranslatedDescriptionInverse(),
                            d.get("sourceProcess"),
                            "_2"
                        );
                    }

                });

                // sorters
                var preferences = CMDBuildUI.util.helper.UserPreferences.getGridPreferences(
                    data.type,
                    data.name
                );

                var sorters = [];
                if (preferences && !Ext.isEmpty(preferences.defaultOrder)) {
                    preferences.defaultOrder.forEach(function (o) {
                        sorters.push({
                            property: o.attribute,
                            direction: o.direction === "descending" ? "DESC" : 'ASC'
                        });
                    });
                }
                me.set("storeinfo.sorters", sorters);

                me.set("storeinfo.data", domains);
            }
        });
    }

});
