Ext.define('CMDBuildUI.view.relations.masterdetail.TabModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.relations-masterdetail-tab',

    data: {
        domain: null,
        targetType: null,
        targetTypeName: null,
        storeInfo: {
            autoLoad: false,
            advancedfilter: null
        },
        addbutton: {
            hidden: true,
            text: null,
            disabled: true
        },
        search: {
            value: null
        }
    },

    formulas: {
        /**
         * Updates store configuration depending on type name and domain.
         */
        updateStoreInfo: {
            bind: {
                targettype: '{targetType}',
                targettypename: '{targetTypeName}',
                domain: '{domain}'
            },
            get: function (data) {
                if (data.targettype && data.targettypename && data.domain) {
                    var me = this,
                        view = this.getView();

                    // update tab title
                    this.set("mddescription", data.domain.getTranslatedDescriptionMasterDetail());
                    this.set("mditems", null);

                    CMDBuildUI.util.helper.ModelHelper.getModel(data.targettype, data.targettypename).then(function (model) {
                        var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.targettypename, data.targettype);

                        // set model name
                        me.set("storeInfo.model", model.getName());

                        // set store type
                        var proxytype = 'classes-cards';
                        if (data.targettype === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                            proxytype = 'processes-instances';
                        } else if (!view.getReadOnly()) {
                            me.set("addbutton.hidden", false);
                        }
                        me.set("storeInfo.type", proxytype);

                        // set store proxy
                        var source, destination, direction;
                        if (data.domain.get("cardinality") === CMDBuildUI.model.domains.Domain.cardinalities.onetomany) {
                            source = data.domain.get("destination");
                            destination = data.domain.get("source");
                            direction = "_2";
                        } else {
                            source = data.domain.get("source");
                            destination = data.domain.get("destination");
                            direction = "_1";
                        }

                        var filter;
                        if (data.domain.get('fk_attribute_name')) {
                            filter = {
                                attribute: {
                                    simple: {
                                        attribute: data.domain.get('fk_attribute_name'),
                                        operator: "equal",
                                        value: me.get("objectId")
                                    }
                                }
                            };
                        } else {
                            filter = {
                                relation: [{
                                    domain: data.domain.get('name'),
                                    type: "oneof",
                                    destination: destination,
                                    source: source,
                                    direction: direction,
                                    cards: [{
                                        className: me.get("objectTypeName"),
                                        id: me.get("objectId")
                                    }]
                                }]
                            };
                        }
                        if (data.domain.get("ecqlFilterMasterDetail")) {
                            filter.ecql = data.domain.get("ecqlFilterMasterDetail");
                        }
                        me.set("storeInfo.advancedfilter", filter);

                        // set autoload
                        me.set("storeInfo.autoload", true);

                        // update target type description
                        var addbuttontext = Ext.String.format(
                            '{0} {1}',
                            CMDBuildUI.locales.Locales.relations.adddetail,
                            item.getTranslatedDescription()
                        );
                        me.set("addbutton.text", addbuttontext);
                        me.set("addbutton.disabled", !data.domain.get(CMDBuildUI.model.base.Base.permissions.add));

                        // set sorters
                        var sorters = CMDBuildUI.util.helper.GridHelper.getStoreSorters(item);
                        me.set("storeInfo.sorters", sorters);

                        // fire data changed event
                        view.fireEvent("targetdataupdated", view, view.getTargetTypeObject(), model, data.domain);
                    });
                }
            }
        },

        title: {
            bind: {
                mddescription: '{mddescription}',
                items: '{mditems}'
            },
            get: function (data) {
                var numberItems = Ext.isNumber(data.items) ? Ext.String.format("{0} {1}", data.items, CMDBuildUI.locales.Locales.relations.mditems) : Ext.String.format('<i class="{0} fa-spin"></i>', CMDBuildUI.util.helper.IconHelper.getIconId('spinner', 'solid'));
                return Ext.String.format(
                    "{0} <br /> <small>{1}</small>",
                    data.mddescription,
                    numberItems
                );
            }
        }
    },

    stores: {
        records: {
            type: '{storeInfo.type}',
            model: '{storeInfo.model}',
            autoLoad: '{storeInfo.autoload}',
            autoDestroy: true,
            advancedFilter: '{storeInfo.advancedfilter}',
            sorters: '{storeInfo.sorters}'
        }
    }

});