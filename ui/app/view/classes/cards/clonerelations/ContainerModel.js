Ext.define('CMDBuildUI.view.classes.cards.clonerelations.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.classes-cards-clonerelations-container',

    data: {
        storeinfo: {
            data: []
        }
    },

    formulas: {
        updateStoreData: {
            bind: {
                id: '{objectId}',
                name: '{objectTypeName}'
            },
            get: function (data) {
                const me = this;
                const domains = [];
                const item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.name);
                const removedDomains = [];
                const hierarchy = item.getHierarchy();

                Ext.Promise.all([item.getAttributes(), item.getDomains()]).then(function (response) {
                    const itemattributes = response[0].getRange();
                    const itemdomains = response[1].getRange();

                    Ext.Array.each(itemattributes, function (attribute) {
                        if (
                            attribute.get('type') ===
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase()
                        ) {
                            removedDomains.push({
                                domain: attribute.get('domain'),
                                direction: attribute.get('direction')
                            });
                        }
                    });

                    Ext.Array.each(itemdomains, function (d) {
                        // direct domain
                        if (
                            Ext.Array.contains(hierarchy, d.get('source')) &&
                            !Ext.Array.contains(d.get('disabledSourceDescendants'), data.name)
                        ) {
                            addToDomainsList(
                                d,
                                d.get('destination'),
                                d.get('_descriptionDirect_translation') || d.get('descriptionDirect'),
                                d.get('destinationProcess'),
                                'direct'
                            );
                        }

                        // inverse domain
                        if (
                            Ext.Array.contains(hierarchy, d.get('destination')) &&
                            !Ext.Array.contains(d.get('disabledDestinationDescendants'), data.name)
                        ) {
                            addToDomainsList(
                                d,
                                d.get('source'),
                                d.get('_descriptionInverse_translation') || d.get('descriptionInverse'),
                                d.get('sourceProcess'),
                                'inverse'
                            );
                        }
                    });
                    me.set('storeinfo.data', domains);

                    function addToDomainsList(domain, destination, description, destIsProcess, direction) {
                        const matchingArrays = Ext.Array.filter(removedDomains, function (element) {
                            return element.domain === domain.getId() && element.direction === direction;
                        });

                        const cardinality = domain.get('cardinality');
                        let isDisabled = false;
                        if (
                            (cardinality == CMDBuildUI.model.domains.Domain.cardinalities.onetomany &&
                                direction == 'direct') ||
                            cardinality == CMDBuildUI.model.domains.Domain.cardinalities.onetoone ||
                            (cardinality == CMDBuildUI.model.domains.Domain.cardinalities.manytoone &&
                                direction == 'inverse')
                        ) {
                            isDisabled = true;
                        }
                        if (destination && !matchingArrays.length) {
                            // get destination object
                            const destObj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                destination,
                                destIsProcess
                                    ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process
                                    : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass
                            );

                            if (destObj) {
                                // add domain in list
                                domains.push({
                                    domain: domain.getId(),
                                    description: description,
                                    destination: destination,
                                    destinationDescription: destObj.getTranslatedDescription(),
                                    destinationIsProcess: destIsProcess,
                                    direction: direction,
                                    isDisabled: isDisabled,
                                    ignore: null,
                                    migrates: null,
                                    clone: null,
                                    mode: null
                                });
                            }
                        }
                    }
                });
            }
        },

        cloneCheckColumnDisabled: {
            bind: {
                domains: '{storeinfo.data}'
            },
            get: function (data) {
                if (Ext.isEmpty(data.domains) || data.domains.length === 0) {
                    return false;
                }
                let headerDisabled = true;
                Ext.Array.each(data.domains, function (item) {
                    if (item.isDisabled !== true) {
                        headerDisabled = false;
                        return false;
                    }
                });

                return headerDisabled;
            }
        }
    },

    stores: {
        relations: {
            model: 'CMDBuildUI.model.domains.Clone',
            autoDestroy: true,
            data: '{storeinfo.data}',
            groupField: '_type',
            proxy: {
                type: 'memory'
            },
            listeners: {
                update: 'onStoreUpdate'
            }
        }
    }
});
