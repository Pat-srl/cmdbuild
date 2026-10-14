Ext.define('CMDBuildUI.view.widgets.linkcards.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.widgets-linkcards-panel',

    data: {
        objectType: null,
        objectTypeName: null,
        disablegridfilter: false,
        selection: null,
        selectedOnlyPressed: false,
        storeinfo: {
            modelname: undefined,
            storetype: undefined,
            proxyurl: undefined,
            extraparams: undefined,
            advancedfilter: undefined,
            sorters: undefined,
            autoload: undefined
        },
        search: {
            value: null
        },
        firstLoad: true
    },

    formulas: {
        checkedOnlyText: {
            bind: '{selectedOnlyPressed}',
            get: function (selectedOnlyPressed) {
                const checkedIcon = selectedOnlyPressed
                    ? CMDBuildUI.util.helper.IconHelper.getIconId('check-square', 'regular')
                    : CMDBuildUI.util.helper.IconHelper.getIconId('square', 'regular');
                return Ext.String.format(
                    '<div class="{0}"/>  {1}',
                    checkedIcon,
                    CMDBuildUI.locales.Locales.widgets.linkcards.checkedonly
                );
            }
        },

        updateStoreData: {
            bind: {
                model: '{model}',
                objecttype: '{objectType}',
                objecttypename: '{objectTypeName}',
                filter: '{theWidget._Filter_ecql}',
                target: '{theTarget}',
                defaults: '{defaultsLoaded}'
            },
            get: function (data) {
                const model = data.model;

                if (model) {
                    // store type
                    switch (data.objecttype) {
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                            this.set('storeinfo.storetype', 'classes-cards');
                            break;
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                            this.set('storeinfo.storetype', 'processes-instances');
                            break;
                        default:
                            CMDBuildUI.util.Logger.log(
                                'Object type ' + data.objecttype + ' non supported for LinkCard widget.',
                                CMDBuildUI.util.Logger.levels.warn
                            );
                    }

                    // model name
                    this.set('storeinfo.modelname', model.getName());

                    // proxy url
                    this.set('store.proxyurl', model.getProxy().getUrl());

                    // extra params
                    this.set('store.extraparams', model.getProxy().getExtraParams());

                    const advancedFilter = new CMDBuildUI.util.AdvancedFilter();
                    const widget = this.get('theWidget');
                    const ids = [];

                    // filter
                    if (data.filter) {
                        // calculate ecql
                        var ecql = CMDBuildUI.util.ecql.Resolver.resolve(data.filter, data.target);

                        if (ecql) {
                            advancedFilter.addEcqlFilter(ecql);
                        }
                    }

                    if (widget && widget.get('DefaultOnlySelected') == 'true') {
                        // get ids from output variable on target
                        if (data.target) {
                            var output = data.target.get(this.getView().getOutput()) || [];

                            output.forEach(function (i) {
                                ids.push(i._id);
                            });
                        }

                        advancedFilter.addAttributeFilter(
                            '_id',
                            CMDBuildUI.util.helper.FiltersHelper.operators.in,
                            ids
                        );

                        this.set('selectedOnlyPressed', true);
                    }

                    this.set('storeinfo.advancedfilter', advancedFilter);

                    // sorters
                    var sorters = CMDBuildUI.util.helper.GridHelper.getStoreSorters(
                        CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.objecttypename, data.objecttype)
                    );
                    this.set('store.sorters', sorters);
                }
                if (data.defaults) {
                    this.set('store.autoload', true);
                }
            }
        },

        /**
         * Disable button view card/process instance.
         */
        disableViewAction: {
            bind: '{theSession}',
            get: function () {
                return false;
            }
        },

        /**
         * Disable action edit card/process instance.
         */
        disableEditAction: {
            bind: {
                canedit: '{theWidget.AllowCardEditing}',
                objecttype: '{objectType}',
                objecttypename: '{objectTypeName}'
            },
            get: function (data) {
                if (data.objecttypename && data.objecttype) {
                    var obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                        data.objecttypename,
                        data.objecttype
                    );
                    return (
                        !obj.get(CMDBuildUI.model.base.Base.permissions.edit) ||
                        data.canedit === undefined ||
                        data.canedit === null ||
                        data.canedit === '0' ||
                        data.canedit === 0 ||
                        data.canedit === 'false' ||
                        data.canedit === false
                    );
                }
                return true;
            }
        },

        textTogglefilter: {
            bind: {
                disablegridfilter: '{disablegridfilter}'
            },
            get: function (data) {
                if (data.disablegridfilter) {
                    return CMDBuildUI.locales.Locales.widgets.linkcards.togglefilterenabled;
                }
                return CMDBuildUI.locales.Locales.widgets.linkcards.togglefilterdisabled;
            }
        },

        /**
         * Disable toggle filter button.
         */
        disableTogglefilter: {
            bind: {
                disable: '{theWidget.DisableGridFilterToggler}',
                filter: '{theWidget._Filter_ecql}'
            },
            get: function (data) {
                return data.disable || !data.filter ? true : false;
            }
        },

        /**
         * Text refresh selection
         */
        textRefreshselection: {
            bind: {},
            get: function (data) {
                return CMDBuildUI.locales.Locales.widgets.linkcards.refreshselection;
            }
        },

        /**
         * Disable refresh selection button.
         */
        disableRefreshselection: {
            bind: {
                defaultselection: '{theWidget._DefaultSelection_ecql}',
                noselect: '{theWidget.NoSelect}'
            },
            get: function (data) {
                return !data.defaultselection || data.noselect ? true : false;
            }
        },

        /**
         * Get store autoLoad value.
         */
        storeAutoLoad: {
            bind: {
                proxy: '{storeProxy}',
                defaultsLoaded: '{defaultsLoaded}'
            },
            get: function (data) {
                return data.proxy && data.defaultsLoaded;
            }
        }
    },

    stores: {
        // grid data
        gridrows: {
            type: '{storeinfo.storetype}',
            model: '{storeinfo.modelname}',
            proxy: {
                type: 'baseproxy',
                url: '{store.proxyurl}',
                extraParams: '{store.extraparams}'
            },
            advancedFilter: '{storeinfo.advancedfilter}',
            sorters: '{store.sorters}',
            autoLoad: '{store.autoload}',
            autoDestroy: true,
            listeners: {
                prefetch: 'onGridrowsLoad'
            }
        }
    }
});
