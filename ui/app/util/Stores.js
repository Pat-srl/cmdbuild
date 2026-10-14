/**
 * @file CMDBuildUI.util.Stores
 * @module CMDBuildUI.util.Stores
 * @author PAT srl
 * @access private
 */
Ext.define('CMDBuildUI.util.Stores', {
    singleton: true,
    // collection of non global stores

    stores: {
        busdescriptors: null
    },

    loaded: {
        classes: false,
        processes: false,
        reports: false,
        dashboards: false,
        views: false,
        searchfilters: false,
        custompages: false,
        customcomponents: false,
        customwidgets: false,
        scripts: false,
        widgets: false,
        menu: false,
        menunavtree: false,
        dependencytree: false,
        administrationmenus: false,
        lookuptypes: false,
        dmsmodels: false,
        categorytypes: true,
        domains: false,
        navtree: false,
        groups: false,
        emailaccounts: false,
        emailtemplates: false,
        importexporttemplates: false,
        etlgates: false,
        busdescriptors: false,
        bim: false,
        pluginmanager: false
    },

    /**
     * Load classes store.
     *
     * @return {Ext.promise.Promise}
     */
    loadClassesStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('classes.Classes');

        // load classes store
        store.load({
            params: {
                detailed: true // load full data
            },
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.classes = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log('Error loading Classes store', CMDBuildUI.util.Logger.levels.error);
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load processes store.
     *
     * @return {Ext.promise.Promise}
     */
    loadProcessesStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('processes.Processes'),
            workflowEnabled = CMDBuildUI.util.helper.Configurations.get('cm_system_workflow_enabled');

        if (workflowEnabled) {
            // load processes store
            store.load({
                params: {
                    detailed: true // load full data
                },
                callback: function (records, operation, success) {
                    if (success) {
                        CMDBuildUI.util.Stores.loaded.processes = true;
                        deferred.resolve(records);
                    } else {
                        CMDBuildUI.util.Logger.log(
                            'Error loading Processes store',
                            CMDBuildUI.util.Logger.levels.error
                        );
                        deferred.resolve([]);
                    }
                }
            });
        } else {
            deferred.resolve([]);
        }

        return deferred.promise;
    },

    /**
     * Load reports store.
     *
     * @return {Ext.promise.Promise}
     */
    loadReportsStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('reports.Reports');

        // load reports store
        store.load({
            params: {
                detailed: true // load full data
            },
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.reports = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log('Error loading Reports store', CMDBuildUI.util.Logger.levels.error);
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load dashboards store.
     *
     * @return {Ext.promise.Promise}
     */
    loadDashboardsStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('dashboards.Dashboards');

        // load dashboards store
        store.load({
            params: {
                detailed: true // load full data
            },
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.dashboards = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log('Error loading Dashboards store', CMDBuildUI.util.Logger.levels.error);
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load homepages store.
     *
     * @return {Ext.promise.Promise}
     */
    loadHomepagesStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('homepages.Homepages');

        // load homepages store
        store.load({
            params: {
                detailed: true // load full data
            },
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.homepages = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log('Error loading Homepages store', CMDBuildUI.util.Logger.levels.error);
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load the current homepage.
     *
     * @returns {Ext.promise.Promise}
     */
    loadCurrentHomepage: function () {
        const deferred = new Ext.Deferred();

        const proxy = Ext.Factory.proxy({
            type: 'baseproxy',
            url: '/homepage/',
            appendId: false,
            model: 'CMDBuildUI.model.homepages.Homepage'
        });

        proxy.read(
            new Ext.data.operation.Read({
                action: 'read',
                callback: function (records, operation, success) {
                    const resultSet = operation.getResultSet();
                    const record = success && resultSet && resultSet.getRecords() ? resultSet.getRecords()[0] : null;

                    if (success) {
                        CMDBuildUI.util.Stores.currentHomepage = record;
                        CMDBuildUI.util.Stores.loaded.homepages = true;
                    } else {
                        CMDBuildUI.util.Logger.log(
                            'Error loading current Homepage',
                            CMDBuildUI.util.Logger.levels.error
                        );
                    }

                    deferred.resolve(record);
                }
            })
        );

        return deferred.promise;
    },

    /**
     * Load views store.
     *
     * @return {Ext.promise.Promise}
     */
    loadViewsStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('views.Views');

        // load views store
        store.load({
            params: {
                detailed: true // load full data
            },
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.views = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log('Error loading Views store', CMDBuildUI.util.Logger.levels.error);
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load searchfilters store.
     *
     * @return {Ext.promise.Promise}
     */
    loadSearchfiltersStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('searchfilters.Searchfilters');

        // load searchfilters store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.searchfilters = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        'Error loading Searchfilters store',
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load custompages store.
     *
     * @return {Ext.promise.Promise}
     */
    loadCustomPagesStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('custompages.CustomPages');

        // load custompages store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.custompages = true;
                    var count = records.length;
                    if (CMDBuildUI.util.Ajax.getViewContext() === 'default' && count) {
                        records.forEach(function (r) {
                            const path = Ext.Loader.getPath(Ext.String.format('CMDBuildUI.{0}', r.get('componentId')));
                            Ext.Loader.loadScript({
                                url: path,
                                onLoad: function () {
                                    --count;
                                    if (!count) {
                                        deferred.resolve(records);
                                    }
                                },
                                onError: function () {
                                    --count;
                                    if (!count) {
                                        deferred.resolve(records);
                                    }
                                },
                                scope: this
                            });
                        });
                    } else {
                        deferred.resolve(records);
                    }
                } else {
                    CMDBuildUI.util.Logger.log('Error loading CustomPages store', CMDBuildUI.util.Logger.levels.error);
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load custom context menu store.
     *
     * @return {Ext.promise.Promise}
     */
    loadCustomContextMenuStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('customcomponents.ContextMenus');

        // load custom context menu store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.customcomponents = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        'Error loading CustomComponents store',
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load custom widgets store.
     *
     * @return {Ext.promise.Promise}
     */
    loadCustomWidgetsStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.StoreManager.get('customcomponents.Widgets');

        // load custom widgets store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    if (CMDBuildUI.util.Ajax.getViewContext() === 'default' && !Ext.isEmpty(records)) {
                        const files = [];
                        records.forEach(function (r) {
                            files.push(Ext.String.format('CMDBuildUI.{0}', r.get('componentId')));
                        });
                        Ext.syncRequire(files);
                    }
                    CMDBuildUI.util.Stores.loaded.customwidgets = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        'Error loading custom widgets store',
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load Widgets store.
     *
     * @return {Ext.promise.Promise}
     */
    loadWidgetsStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('customcomponents.Widgets');

        // load Widgets store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.widgets = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log('Error loading Widgets store', CMDBuildUI.util.Logger.levels.error);
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load Scripts store.
     *
     * @return {Ext.promise.Promise}
     */
    loadScriptsStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('customcomponents.Scripts');

        // load Widgets store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.scripts = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log('Error loading Scripts store', CMDBuildUI.util.Logger.levels.error);
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load menu store.
     *
     * @return {Ext.promise.Promise}
     */
    loadMenuStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('menu.Menu');

        store.setRoot({
            expanded: false
        });

        // load menu store
        store.load(function (records, operation, success) {
            if (success) {
                CMDBuildUI.util.Stores.loaded.menu = true;
                deferred.resolve(records);
            } else {
                CMDBuildUI.util.Logger.log('Error loading Menu items store', CMDBuildUI.util.Logger.levels.error);
                deferred.resolve([]);
            }
        });

        return deferred.promise;
    },

    /**
     * Load menu navigation trees store
     *
     * @return {Ext.promise.Promise}
     */
    loadMenuNavigationTreesStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('menu.NavigationTrees');

        // load menu navigation trees store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.menunavtree = true;
                    /// Temporary until server makes services
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log('Error loading menu NavTree store', CMDBuildUI.util.Logger.levels.error);
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load administration menu store.
     *
     * @return {Ext.promise.Promise}
     */
    loadAdministrationMenusStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.create('Ext.data.Store', {
                model: 'CMDBuildUI.model.menu.Menu',
                pageSize: 0, // disable pagination
                sorters: ['group']
            });

        // load administration menu store
        store.load(function (records, operation, success) {
            if (success) {
                CMDBuildUI.util.Stores.loaded.administrationmenus = true;
                deferred.resolve(records);
            } else {
                CMDBuildUI.util.Logger.log(
                    'Error loading administration Menu items store',
                    CMDBuildUI.util.Logger.levels.error
                );
                deferred.resolve([]);
            }
        });

        return deferred.promise;
    },

    /**
     * Load menu navigation trees store
     *
     * @return {Ext.promise.Promise}
     */
    loadDependencyTreesStore: function () {
        const deferred = new Ext.Deferred();
        const store = Ext.getStore('dependencyTrees.DependencyTrees');
        const dependencyTreeClasses = {};
        const dependencyTreeDescription = {};

        // load menu navigation trees store
        store.load({
            callback: function (records, operation, success) {
                if (CMDBuildUI.util.helper.SessionHelper.isAdministrationModule()) {
                    deferred.resolve(records);
                    return;
                }

                if (success) {
                    let length = records.length;
                    Ext.Array.forEach(records, function (item, index, allitems) {
                        const treeName = item.getId();

                        const associateTreeToClass = function (elem) {
                            const targetClass = elem.targetClass;
                            const allClasses = [targetClass];
                            const obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(targetClass);

                            Ext.Array.forEach(obj.getChildren(), function (childClass) {
                                allClasses.push(childClass.get('name'));
                            });

                            Ext.Array.forEach(allClasses, function (singleClass, i, all) {
                                const dependencytreeClass = dependencyTreeClasses[singleClass];
                                if (!dependencytreeClass) {
                                    dependencyTreeClasses[singleClass] = [treeName];
                                } else if (!dependencytreeClass.includes(treeName)) {
                                    dependencytreeClass.push(treeName);
                                }
                            });

                            length -= 1;
                            const childNodes = elem.nodes;
                            if (!Ext.isEmpty(childNodes)) {
                                length += childNodes.length;
                                Ext.Array.forEach(childNodes, function (node, ind, allnodes) {
                                    associateTreeToClass(node);
                                });
                            } else {
                                if (!length) {
                                    CMDBuildUI.util.Stores.loaded.dependencytree = true;
                                    CMDBuildUI.util.DependencyTree.setDependencyTreeClasses(dependencyTreeClasses);
                                    CMDBuildUI.util.DependencyTree.setDependencyTreeDescription(
                                        dependencyTreeDescription
                                    );
                                    deferred.resolve(records);
                                }
                            }
                        };

                        associateTreeToClass(item.nodes().getRange()[0].getData());

                        dependencyTreeDescription[treeName] =
                            item.get('_description_translation') || item.get('description');
                    });
                } else {
                    CMDBuildUI.util.Logger.log(
                        'Error loading menu DependencyTree store',
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load lookup types store.
     *
     * @return {Ext.promise.Promise}
     */
    loadLookupTypesStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('lookups.LookupTypes');

        // load lookup types store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.lookuptypes = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log('Error loading LookupTypes store', CMDBuildUI.util.Logger.levels.error);
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load dms lookup types store.
     *
     * @return {Ext.promise.Promise}
     */
    loadDMSLookupTypesStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('dms.DMSCategoryTypes');

        // load dms lookup types store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.lookuptypes = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log('Error loading LookupTypes store', CMDBuildUI.util.Logger.levels.error);
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load dms models store.
     *
     * @return {Ext.promise.Promise}
     */
    loadDmsModelsStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('dms.DMSModels');

        // load dms models store
        store.load({
            params: {
                detailed: true // load full data
            },
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.dmsmodels = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log('Error loading DmsModels store', CMDBuildUI.util.Logger.levels.error);
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load dms category types store.
     *
     * @return {Ext.promise.Promise}
     */
    loadDmsCategoryTypesStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('dms.DMSCategoryTypes');

        // load dms category types store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.categorytypes = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        'Error loading DMSCategoryTypes store',
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load domains store.
     *
     * @return {Ext.promise.Promise}
     */
    loadDomainsStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('domains.Domains');

        // load domains store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.domains = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log('Error loading Domains store', CMDBuildUI.util.Logger.levels.error);
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load navigation trees
     *
     * @return {Ext.promise.Promise}
     */
    loadNavigationTreesStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('navigationtrees.NavigationTrees');

        // load navigation tree store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.navtree = true;
                    /// Temporary until server makes services
                    const gisNavigationCallback = function (record, operation, success) {
                        if (success) {
                            store.add([record]);
                        }
                    };
                    for (var i = 0; i < records.length; i++) {
                        if (records[i].get('_id') === 'gisnavigation') {
                            CMDBuildUI.model.navigationTrees.DomainTree.load('gisnavigation', {
                                callback: gisNavigationCallback
                            });
                        }
                    }
                    /// Temporary until server makes services
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log('Error loading NavTree store', CMDBuildUI.util.Logger.levels.error);
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load groups store.
     *
     * @return {Ext.promise.Promise}
     */
    loadGroupsStore: function () {
        const me = this;

        if (me._loadGroupsPromise) {
            return me._loadGroupsPromise;
        }

        const deferred = new Ext.Deferred(),
            store = Ext.getStore('groups.Groups');

        me._loadGroupsPromise = deferred.promise;

        store.load({
            callback: function (records, operation, success) {
                me._loadGroupsPromise = null;
                if (success) {
                    CMDBuildUI.util.Stores.loaded.groups = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log('Error loading Groups store', CMDBuildUI.util.Logger.levels.error);
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load flow statuses store.
     *
     * @return {Ext.promise.Promise}
     */
    loadFlowStatuses: function () {
        // load classes store
        const type = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(
            CMDBuildUI.model.processes.Process.flowstatus.lookuptype
        );
        return type.getLookupValues();
    },

    /**
     * Load email accounts store.
     *
     * @return {Ext.promise.Promise}
     */
    loadEmailAccountsStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('emails.Accounts');

        // load email accounts store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.emailaccounts = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        'Error loading email Accounts store',
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load email templates store.
     *
     * @return {Ext.promise.Promise}
     */
    loadEmailTemplatesStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('emails.Templates');

        // load email templates store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.emailtemplates = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log(
                        'Error loading email Templates store',
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load import export templates store.
     *
     * @return {Ext.promise.Promise}
     */
    loadImportExportTemplatesStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('importexports.Templates');

        // load import exports Templates store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.importexporttemplates = true;
                    deferred.resolve(this.getRange());
                } else {
                    CMDBuildUI.util.Logger.log(
                        'Error loading import/export Templates store',
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load bus descriptors store.
     *
     * @return {Ext.promise.Promise}
     */
    loadBusDescriptorStore: function () {
        const deferred = new Ext.Deferred();
        if (!this.stores.busdescriptors) {
            this.stores.busdescriptors = Ext.create('Ext.data.Store', {
                model: 'CMDBuildUI.model.administration.BusDescriptor',
                pageSize: 0,
                proxy: {
                    type: 'baseproxy',
                    url: '/etl/configs',
                    extrParams: {
                        detailed: true
                    }
                },
                advancedFilter: {
                    attributes: {
                        tag: [
                            {
                                operator: CMDBuildUI.model.base.Filter.operators.null,
                                value: []
                            }
                        ],
                        description: [
                            {
                                operator: CMDBuildUI.model.base.Filter.operators.notnull,
                                value: []
                            }
                        ]
                    }
                }
            });
        }

        // load bus descriptors store
        this.stores.busdescriptors.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.busdescriptors = true;
                    deferred.resolve(this.getRange());
                } else {
                    CMDBuildUI.util.Logger.log(
                        'Error loading bus descriptors store',
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load etl gates store.
     *
     * @returns {Ext.promise.Promise}
     */
    loadETLGatesStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('importexports.Gates');

        // load etl gates store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.etlgates = true;
                    deferred.resolve(this.getRange());
                } else {
                    CMDBuildUI.util.Logger.log(
                        'Error loading ETL Gates Templates store',
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load bim store.
     *
     * @return {Ext.promise.Promise}
     */
    loadBimStore: function () {
        const deferred = new Ext.Deferred(),
            store = Ext.getStore('bim.Projects');

        // load bim store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.bim = true;
                    deferred.resolve(records);
                } else {
                    CMDBuildUI.util.Logger.log('Error loading Bim store', CMDBuildUI.util.Logger.levels.error);
                    deferred.resolve([]);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Load plugin manager store.
     *
     * @return {Ext.promise.Promise}
     */
    loadPluginManagerStore: function () {
        const deferred = new Ext.Deferred();
        const store = Ext.getStore('pluginmanager.Plugins');

        // load plugin manager store
        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    CMDBuildUI.util.Stores.loaded.pluginmanager = true;
                    let count = records.length;
                    if (count > 0) {
                        const resolvePlugins = function () {
                            --count;
                            if (!count) {
                                deferred.resolve(records);
                            }
                        };

                        records.forEach(function (r) {
                            if (r.get('custompage')) {
                                const path = Ext.Loader.getPath(
                                    Ext.String.format('CMDBuildUI.{0}', r.get('custompage').componentId)
                                );
                                Ext.Loader.loadScript({
                                    url: path,
                                    onLoad: function () {
                                        Ext.Loader.onReady(function () {
                                            resolvePlugins();
                                        });
                                    },
                                    onError: function () {
                                        resolvePlugins();
                                    }
                                });
                            } else {
                                resolvePlugins();
                            }
                        });
                    } else {
                        deferred.resolve(records);
                    }
                } else {
                    CMDBuildUI.util.Logger.log(
                        'Error loading plugin manager store',
                        CMDBuildUI.util.Logger.levels.error
                    );
                    deferred.resolve([]);
                }
            }
        });
        return deferred.promise;
    },

    /**
     *
     * @param {String} store the id of the Store
     * @param {Boolean} [soft] if true and store contains data, the data will be resolved without store reload
     * @return {Ext.promise.Promise}
     */
    load: function (store, soft) {
        const deferred = new Ext.Deferred(),
            _store = Ext.getStore(store);

        if (_store && _store.getRange && _store.getRange().length && soft) {
            deferred.resolve(_store.getRange());
        } else {
            switch (store) {
                case 'classes.Classes':
                    CMDBuildUI.util.Stores.loadClassesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'processes.Processes':
                    CMDBuildUI.util.Stores.loadProcessesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'reports.Reports':
                    CMDBuildUI.util.Stores.loadReportsStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'dashboards.Dashboards':
                    CMDBuildUI.util.Stores.loadDashboardsStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'homepages.Homepages':
                    CMDBuildUI.util.Stores.loadHomepagesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'views.Views':
                    CMDBuildUI.util.Stores.loadViewsStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'searchfilters.Searchfilters':
                    CMDBuildUI.util.Stores.loadSearchfiltersStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'custompages.CustomPages':
                    CMDBuildUI.util.Stores.loadCustomPagesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'customcomponents.ContextMenus':
                    CMDBuildUI.util.Stores.loadCustomContextMenuStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'customcomponents.Widgets':
                    CMDBuildUI.util.Stores.loadWidgetsStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'customcomponents.Scripts':
                    CMDBuildUI.util.Stores.loadScriptsStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'menu.Menu':
                    CMDBuildUI.util.Stores.loadMenuStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'menu.NavigationTrees':
                    CMDBuildUI.util.Stores.loadMenuNavigationTreesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'dependencyTrees.DependencyTrees':
                    CMDBuildUI.util.Stores.loadDependencyTreesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'lookups.LookupTypes':
                    CMDBuildUI.util.Stores.loadLookupTypesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'dms.DMSCategoryTypes':
                    CMDBuildUI.util.Stores.loadCategoryTypesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'dms.DMSModels':
                    CMDBuildUI.util.Stores.loadDmsModelsStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'domains.Domains':
                    CMDBuildUI.util.Stores.loadDomainsStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'navigationtrees.NavigationTrees':
                    CMDBuildUI.util.Stores.loadNavigationTreesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'groups.Groups':
                    CMDBuildUI.util.Stores.loadGroupsStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'emails.Accounts':
                    CMDBuildUI.util.Stores.loadEmailAccountsStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'emails.Templates':
                    CMDBuildUI.util.Stores.loadEmailTemplatesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'importexports.Templates':
                    CMDBuildUI.util.Stores.loadImportExportTemplatesStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                case 'bim.Projects':
                    CMDBuildUI.util.Stores.loadBimStore().then(function (data) {
                        deferred.resolve(data);
                    });
                case 'pluginmanager.Plugins':
                    CMDBuildUI.util.Stores.loadPluginManagerStore().then(function (data) {
                        deferred.resolve(data);
                    });
                    break;
                default:
                    CMDBuildUI.util.Logger.log(
                        'Store id not managed by Stores Util',
                        CMDBuildUI.util.Logger.levels.debug
                    );
                    deferred.resolve([]);
                    break;
            }
        }

        return deferred.promise;
    }
});
