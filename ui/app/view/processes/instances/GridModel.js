Ext.define('CMDBuildUI.view.processes.instances.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.processes-instances-grid',

    data: {
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
        objectTypeName: null,
        menuType: CMDBuildUI.model.menu.MenuItem.types.process,
        title: null,
        isModelLoaded: false,
        search: {
            disabled: false,
            value: null
        },
        selected: null,
        addbtn: {
            disabled: true,
            hidden: true
        },
        storedata: {},
        statuscombo: {
            hidden: true,
            disabled: true,
            value: null,
            store: {
                autoload: false,
                proxyurl: ''
            }
        }
    },

    formulas: {
        /**
         * Updata view model data
         */
        updateData: {
            bind: {
                typename: '{objectTypeName}',
                modelloaded: '{isModelLoaded}'
            },
            get: function (data) {
                // update translations
                if (data.typename && data.modelloaded) {
                    var process = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(data.typename),
                        modelName = CMDBuildUI.util.helper.ModelHelper.getModelName(
                            CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
                            data.typename
                        ),
                        model = CMDBuildUI.util.helper.ModelHelper.getModelFromName(modelName);
                    this.set('search.disabled', !process.get(CMDBuildUI.model.base.Base.permissions.search));
                    // set model name
                    this.set('storedata.modelname', modelName);

                    this.set('storedata.proxy', model.getProxy().clone());

                    this.set(
                        'title',
                        CMDBuildUI.locales.Locales.processes.workflow + ' ' + process.getTranslatedDescription()
                    );

                    var sorters = CMDBuildUI.util.helper.GridHelper.getStoreSorters(process);
                    this.set('storedata.sorters', sorters);

                    // default filter
                    if (process && process.getCurrentFilter && process.getCurrentFilter()) {
                        this.set('defaultfilter', process.getCurrentFilter());
                    }

                    // add button
                    this.set(
                        'addbtn.text',
                        CMDBuildUI.locales.Locales.processes.startworkflow + ' ' + process.getTranslatedDescription()
                    );
                    this.set('addbtn.hidden', !this.getView().getShowAddButton());
                    // enable or disable print button
                    var canPrint = !Ext.isEmpty(process.get('_can_print')) ? process.get('_can_print') : true;
                    this.set('btnCanPrintDisabled', !canPrint);
                    // filters
                    this.set('allowfilter', this.getView().getAllowFilter());
                }
            }
        },

        updateStatusCombo: {
            bind: {
                allowfilter: '{allowfilter}',
                ismodelloaded: '{isModelLoaded}',
                typename: '{objectTypeName}',
                store: '{statuscombostore}'
            },
            get: function (data) {
                this.set('statuscombo.hidden', !data.allowfilter);

                if (data.ismodelloaded) {
                    var fieldname = CMDBuildUI.model.processes.Process.flowstatus.field;
                    var lt = CMDBuildUI.model.processes.Process.flowstatus.lookuptype;

                    // check if process has custom status attribute
                    var process = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(data.typename);
                    if (process.get('flowStatusAttr')) {
                        var flowStatusAttr = process.get('flowStatusAttr');
                        // get model
                        var modelName = CMDBuildUI.util.helper.ModelHelper.getModelName(
                            CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
                            data.typename
                        );
                        var model = Ext.ClassManager.get(modelName);
                        var field = model.getField(flowStatusAttr);
                        if (field && field.cmdbuildtype === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup) {
                            lt = field.attributeconf.lookupType;
                            fieldname = field.name;

                            //add the ecql filter
                            var filter = field.attributeconf.ecqlFilter;
                            if (filter) {
                                var statuscombostore = data.store;
                                var advancedFilter = statuscombostore.getAdvancedFilter();
                                var ecql = CMDBuildUI.util.ecql.Resolver.resolve(filter);
                                advancedFilter.addEcqlFilter(ecql);
                            }
                        }

                        try {
                            const gridPreferences = CMDBuildUI.util.helper.UserPreferences.getGridPreferences(
                                CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
                                data.typename
                            );
                            this.set('statuscombo.value', gridPreferences.statusFilter);
                        } catch (e) {}
                    } else {
                        try {
                            const gridPreferences = CMDBuildUI.util.helper.UserPreferences.getGridPreferences(
                                CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
                                data.typename
                            );
                            this.set('statuscombo.value', gridPreferences.statusFilter);
                        } catch (e) {
                            let record = this.getView().getOpenRunningStatusValue();
                            if (record) {
                                this.set('statuscombo.value', record.getId());
                            }
                        }
                    }
                    this.set('statuscombo.store.proxyurl', CMDBuildUI.util.api.Lookups.getLookupValues(lt));
                    this.set('statuscombo.store.autoload', true);
                    this.set('statuscombo.disabled', false);
                    this.set('statuscombo.field', fieldname);
                    this.set('statuscombo.lookuptype', lt);
                }
            }
        }
    },

    stores: {
        instances: {
            type: 'processes-instances',
            model: '{storedata.modelname}',
            proxy: '{storedata.proxy}',
            sorters: '{storedata.sorters}',
            autoLoad: false,
            autoDestroy: true
        },

        /**
         * Status combo store definition
         */
        statuscombostore: {
            model: 'CMDBuildUI.model.lookups.Lookup',
            proxy: {
                type: 'baseproxy',
                url: '{statuscombo.store.proxyurl}'
            },
            autoLoad: '{statuscombo.store.autoload}',
            remoteFilter: false,
            autoDestroy: true,
            listeners: {
                load: function (store, records, successful, operation, eOpts) {
                    store.add({
                        _id: '__ALL__',
                        _type: '_FAKELOOKUP_',
                        code: '__ALL__',
                        description: CMDBuildUI.locales.Locales.processes.allstatuses
                    });
                }
            }
        }
    }
});
