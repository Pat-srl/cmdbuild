Ext.define('CMDBuildUI.view.relations.list.add.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.relations-list-add-grid',

    data: {
        searchvalue: null
    },

    formulas: {
        /**
         * Update store info
         */
        updateStoreInfo: {
            bind: {
                objecttypename: '{objectTypeName}',
                originid: '{originId}',
                theDomain: '{theDomain}',
                direction: '{relationDirection}'
            },
            get: function (data) {
                if (data.objecttypename && data.originid) {
                    // store type
                    var object = CMDBuildUI.util.helper.ModelHelper.getClassFromName(data.objecttypename);
                    this.set("storeinfo.proxyurl", CMDBuildUI.util.api.Classes.getCardsUrl(data.objecttypename));

                    // add ecql filter
                    var filters = {};
                    if (this.get("storeinfo.ecqlfilter") && !Ext.Object.isEmpty(this.get("storeinfo.ecqlfilter"))) {
                        filters.ecql = this.get("storeinfo.ecqlfilter");
                    }

                    // add filter for enabled subclasses
                    if (data.theDomain) {
                        var ecqlFilter;

                        if (data.theDomain.get("ecqlSourceFilter") && data.direction === 'inverse') {
                            ecqlFilter = data.theDomain.get("ecqlSourceFilter");
                        } else if (data.theDomain.get('ecqlTargetFilter') && data.direction === 'direct') {
                            ecqlFilter = data.theDomain.get('ecqlTargetFilter');
                        }
                        filters.ecql = CMDBuildUI.util.ecql.Resolver.resolve(ecqlFilter, this.get("theObject"));

                        var disabledDescendants = data.direction === "direct" ?
                            data.theDomain.get("disabledDestinationDescendants") :
                            data.theDomain.get("disabledSourceDescendants");
                        if (!Ext.isEmpty(disabledDescendants)) {
                            filters.attributes = {
                                _type: [{
                                    operator: 'in',
                                    value: data.direction === "direct" ?
                                        data.theDomain.get("destinations") :
                                        data.theDomain.get("sources")
                                }]
                            };
                        }
                    }
                    this.set("storeinfo.advancedfilter", filters);

                    this.set("storeinfo.extraparams", {
                        forDomain_name: data.theDomain.get("name"),
                        forDomain_direction: data.direction,
                        forDomain_originId: data.originid
                    });

                    var sorters = CMDBuildUI.util.helper.GridHelper.getStoreSorters(object, true);
                    this.set("storeinfo.sorters", sorters);
                }
            }
        }
    },

    stores: {
        records: {
            type: 'classes-cards',
            model: '{storeinfo.modelname}',
            proxy: {
                type: 'baseproxy',
                url: '{storeinfo.proxyurl}',
                extraParams: '{storeinfo.extraparams}'
            },
            advancedFilter: '{storeinfo.advancedfilter}',
            sorters: '{storeinfo.sorters}',
            autoLoad: '{storeinfo.autoload}'
        }
    }

});
