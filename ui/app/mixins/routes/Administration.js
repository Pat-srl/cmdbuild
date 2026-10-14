Ext.define('CMDBuildUI.mixins.routes.Administration', {
    imports: ['CMDBuildUI.util.Navigation'],
    mixinId: 'administrationroutes-mixin',

    currentmaincontent: null,
    mixins: ['CMDBuildUI.mixins.routes.administration.Permissions'],

    /**
     * Administration routes
     */
    /**
     * Show administration page
     */
    onBeforeShowAdministration: function (action) {
        var me = this;
        me.getViewModel().set('isAdministrationModule', true);
        CMDBuildUI.util.Navigation.clearCurrentContext();
        CMDBuildUI.util.Logger.log(
            'onBeforeShowAdministration checkSessionValidity',
            CMDBuildUI.util.Logger.levels.debug
        );
        var token = CMDBuildUI.util.helper.SessionHelper.getCurrentSession();
        // TODO: check if token is valid
        if (token) {
            CMDBuildUI.util.Logger.log(
                'onBeforeShowAdministration initialize menu',
                CMDBuildUI.util.Logger.levels.debug
            );
            CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
                CMDBuildUI.util.Logger.log(
                    'onBeforeShowAdministration MenuStoreBuilder initialized',
                    CMDBuildUI.util.Logger.levels.debug
                );
                Ext.getBody().removeCls('management');
                Ext.getBody().removeCls('loginpage');
                Ext.getBody().addCls('administration');
                action.resume();
                var navTree = Ext.getBody().down('#administrationNavigationTree');
                var store = Ext.getStore('administration.MenuAdministration');
                var currentNode = store.findNode('href', Ext.History.getToken());
                if (!currentNode) {
                    currentNode = CMDBuildUI.util.administration.MenuStoreBuilder.getFirstSelectableMenuItem(
                        store.getRootNode().childNodes
                    );
                }

                var vm = navTree.component.getViewModel();
                if (currentNode) {
                    vm.set('selected', currentNode);
                }
            }, true);
        } else {
            CMDBuildUI.util.Logger.log(
                'session token not found, stop current action and redirect to login',
                CMDBuildUI.util.Logger.levels.debug
            );
            action.stop();
            me.redirectTo('login', true);
        }
    },

    /**
     *  redirect to administration and refresh the window
     */
    goToAdministration: function () {
        this.redirectTo('administration', true);
        window.location.reload();
    },
    /**
     * Show administration  main container
     */
    showAdministration: function () {
        CMDBuildUI.util.Navigation.addIntoMainContainer('administration-maincontainer');
        this.redirectToStartingUrl();
    },

    /**
     * Show class add
     */
    showClassAdministrationAdd: function (classType, action) {
        classType = decodeURI(classType);
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-classes-view', {
            viewModel: {
                links: {
                    theObject: {
                        type: 'CMDBuildUI.model.classes.Class',
                        create: true
                    }
                },
                data: {
                    objectType: CMDBuildUI.util.helper.ModelHelper.systemClasses.class,
                    classType: classType,
                    title: CMDBuildUI.locales.Locales.administration.navigation.classes,
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    }
                }
            }
        });
    },

    /**
     * Show class add
     */
    showClassAdministration_empty: function (classType, action) {
        classType = decodeURI(classType);
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-classes-topbar', {
            viewModel: {
                data: {
                    objectType: CMDBuildUI.util.helper.ModelHelper.systemClasses.class,
                    classType: classType,
                    title: CMDBuildUI.locales.Locales.administration.navigation.classes,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.navigation.classes'
                    },
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    }
                }
            }
        });
    },

    /**
     * Show class view
     */
    showClassAdministrationView: function (className, action) {
        className = decodeURI(className);
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-classes-view', {
            viewModel: {
                links: {
                    theObject: {
                        type: 'CMDBuildUI.model.classes.Class',
                        id: className
                    }
                },
                data: {
                    objectTypeName: className,
                    objectType: CMDBuildUI.util.helper.ModelHelper.systemClasses.class,
                    title: CMDBuildUI.locales.Locales.administration.navigation.classes,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show class edit
     */
    showClassAdministrationEdit: function (className) {
        className = decodeURI(className);
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-classes-view', {
            viewModel: {
                links: {
                    theObject: {
                        type: 'CMDBuildUI.model.classes.Class',
                        id: className
                    }
                },
                data: {
                    objectTypeName: className,
                    objectType: CMDBuildUI.util.helper.ModelHelper.systemClasses.class,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit
                }
            }
        });
        return;
    },

    /**
     * Show class attribute edit
     */
    showClassAttributeAdministrationEdit: function (className, attributeName, attributes) {
        this.addIntoAdministrationDetailsWindow('administration-content-classes-tabitems-attributes-card-edit', {
            viewModel: {
                data: {
                    className: decodeURI(className),
                    attributeName: decodeURI(attributeName),
                    attributes: decodeURI(attributes)
                }
            }
        });
    },
    /**
     * Show class attribute view
     */
    showClassAttributeAdministrationView: function (className, attributeName, attributes) {
        this.addIntoAdministrationDetailsWindow('administration-content-classes-tabitems-attributes-card-view', {
            viewModel: {
                data: {
                    className: decodeURI(className),
                    attributeName: decodeURI(attributeName),
                    attributes: decodeURI(attributes)
                }
            }
        });
    },

    /**
     * Show lookup type add
     */
    showLookupTypeAdministrationAdd: function (lookupName) {
        lookupName = decodeURI(lookupName);
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-lookuptypes-view', {
            viewModel: {
                links: {
                    theLookupType: {
                        type: 'CMDBuildUI.model.lookups.LookupType',
                        create: true
                    }
                },
                data: {
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add
                }
            }
        });
    },

    showDMSAdministrationView: function () {
        var dmsEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled);
        var nextUrl;
        var theSession = this.getViewModel().get('theSession');
        if (dmsEnabled) {
            nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getDmsCategoryUrl();
        } else if (theSession.adminCan('admin_sysconfig_view') || theSession.adminCan('admin_sysconfig_modify')) {
            nextUrl = 'administration/setup/documentmanagementsystem';
        } else {
            return;
        }

        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },

    showDMSModelsAdministrationView_empty: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-dms-models-view', {
            viewModel: {
                links: {
                    theModel: {
                        type: 'CMDBuildUI.model.dms.DMSModel',
                        create: true
                    }
                },
                data: {
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.empty,
                    objectType: 'dmsmodel',
                    actions: {
                        view: false,
                        add: false,
                        edit: false,
                        empty: true
                    }
                }
            }
        });
        return;
    },
    showDMSModelsAdministrationView: function (modelName) {
        modelName = modelName ? decodeURI(modelName) : undefined;
        var theModel = {
            type: 'CMDBuildUI.model.dms.DMSModel'
        };

        if (modelName) {
            theModel.id = modelName;
        } else {
            theModel.create = true;
        }
        var actions = {
            view: theModel.create ? false : true,
            add: theModel.create ? true : false,
            edit: false,
            empty: false
        };

        var action = theModel.create
            ? CMDBuildUI.util.administration.helper.FormHelper.formActions.add
            : CMDBuildUI.util.administration.helper.FormHelper.formActions.view;

        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-dms-models-view', {
            viewModel: {
                links: {
                    theModel: theModel
                },
                data: {
                    objectTypeName: modelName,
                    objectType: 'dmsmodel',
                    action: action,
                    actions: actions
                }
            }
        });
    },

    showDMSCategoriesAdministrationView_empty: function (categoryNameHash) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-dms-dmscategorytypes-view',
            {
                viewModel: {
                    data: {
                        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.empty
                    }
                }
            }
        );
        return;
    },

    showDMSCategoriesAdministrationView: function (categoryNameHash) {
        categoryNameHash = categoryNameHash ? decodeURI(categoryNameHash) : undefined;
        var theDMSCategoryType = {
            type: 'CMDBuildUI.model.dms.DMSCategoryType'
        };

        if (categoryNameHash) {
            theDMSCategoryType.id = categoryNameHash;
        } else {
            theDMSCategoryType.create = true;
        }
        var actions = {
            view: theDMSCategoryType.create ? false : true,
            add: theDMSCategoryType.create ? true : false,
            edit: false,
            empty: false
        };

        var action = theDMSCategoryType.create
            ? CMDBuildUI.util.administration.helper.FormHelper.formActions.add
            : CMDBuildUI.util.administration.helper.FormHelper.formActions.view;

        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-dms-dmscategorytypes-view',
            {
                viewModel: {
                    links: {
                        theDMSCategoryType: theDMSCategoryType
                    },
                    data: {
                        objectTypeName: categoryNameHash,
                        objectType: 'dmscatgeorytype',
                        action: action,
                        actions: actions,
                        title: CMDBuildUI.locales.Locales.administration.localizations.lookup
                    }
                }
            }
        );
    },
    /**
     * Show lookup type empty
     */
    showLookupTypeAdministration_empty: function (lookupName) {
        lookupName = decodeURI(lookupName);
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-lookuptypes-topbar', {
            viewModel: {
                type: 'administration-content-lookuptypes-view'
            }
        });
    },

    /**
     * Show lookup type view
     */
    showLookupTypeAdministrationView: function (lookupName) {
        lookupName = decodeURI(lookupName);
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-lookuptypes-view', {
            viewModel: {
                links: {
                    theLookupType: {
                        type: 'CMDBuildUI.model.lookups.LookupType',
                        id: lookupName
                    }
                },
                data: {
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                }
            }
        });
    },

    /**
     * show domain view
     * @param {String} domain
     */
    showDomainAdministrationView: function (domain) {
        domain = decodeURI(domain);
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-domains-view', {
            viewModel: {
                links: {
                    theDomain: {
                        type: 'CMDBuildUI.model.domains.Domain',
                        id: domain
                    }
                },
                data: {
                    objectTypeName: domain,
                    objectType: 'Domain',
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                    title: CMDBuildUI.locales.Locales.administration.localizations.domain
                }
            }
        });
    },

    /**
     * show domain create
     */
    showDomainAdministrationCreate: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-domains-view', {
            viewModel: {
                links: {
                    theDomain: {
                        type: 'CMDBuildUI.model.domains.Domain',
                        create: true
                    }
                },
                data: {
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    },
                    objectType: 'Domain',
                    title: CMDBuildUI.locales.Locales.administration.localizations.domain
                }
            }
        });
    },

    /**
     * show domain empty
     */
    showDomainAdministration_empty: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-domains-topbar', {
            viewModel: {}
        });
    },

    /**
     * show menu add
     */
    showMenuAdministrationAdd: function (device) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-menu-view', {
            viewModel: {
                links: {
                    theMenu: {
                        type: 'CMDBuildUI.model.menu.Menu',
                        create: {
                            device: device
                        }
                    }
                },
                data: {
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    },
                    device: device,
                    action: 'ADD',
                    objectType: 'Menu'
                }
            }
        });
    },
    /**
     * show menu view
     */
    showMenuAdministrationView: function (device, menu) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-menu-view', {
            viewModel: {
                links: {
                    theMenu: {
                        type: 'CMDBuildUI.model.menu.Menu',
                        id: decodeURI(menu)
                    }
                },
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    },
                    device: device,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                    objectType: 'Menu',
                    title: CMDBuildUI.locales.Locales.administration.menus.singular
                }
            }
        });
    },
    showMenuNavigationtreeAdministrationView: function (navigationtreesId) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-menunavigationtree-view', {
            viewModel: {
                links: {
                    theNavigationtree: {
                        type: 'CMDBuildUI.model.administration.AdminNavTree',
                        id: navigationtreesId
                    }
                },

                data: {
                    navigationtreesId: navigationtreesId,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    showMenuNavigationtreeAdministrationView_empty: function (showForm) {
        var hideForm = showForm === 'true' ? false : true;
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-menunavigationtree-view', {
            viewModel: {
                links: {
                    theNavigationtree: {
                        type: 'CMDBuildUI.model.administration.AdminNavTree',
                        create: {
                            type: 'menu'
                        }
                    }
                },

                data: {
                    actions: {
                        view: hideForm ? true : false,
                        edit: false,
                        add: hideForm ? false : true
                    },
                    hideForm: hideForm
                }
            }
        });
    },
    /**
     * show menu empty
     */
    showMenuAdministration_empty: function (device, menu) {
        if (!device) {
            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheMenuUrl(
                null,
                CMDBuildUI.model.menu.Menu.device['default']
            );
            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
        }
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-menus-topbar', {
            viewModel: {
                data: {
                    device: device
                }
            }
        });
    },

    /**
     * show homepage empty
     */
    showHomepageAdministrationView: function (homepageId) {
        var link = {
            theDashboard: {
                type: 'CMDBuildUI.model.homepages.Homepage',
                id: homepageId
            }
        };
        if (!homepageId || homepageId === '_new') {
            delete link.theDashboard.id;
            link.theDashboard.create = true;
        }

        var config = {
            // showCard: true,
            viewModel: {
                data: {
                    homepageId: homepageId,
                    actions: {
                        view: homepageId && homepageId !== '_new' ? true : false,
                        edit: false,
                        add: homepageId === '_new'
                    },
                    action:
                        homepageId && homepageId !== '_new'
                            ? CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                            : CMDBuildUI.util.administration.helper.FormHelper.formActions.add,

                    hideForm: !homepageId
                },
                formulas: {},
                links: link
            }
        };
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-homepages-tabpanel',
            config
        );
    },

    showProcessAdministration_empty: function (process) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-processes-topbar', {
            viewModel: {}
        });
    },

    /**
     * Show process view
     */
    showProcessAdministrationView: function (processName, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-processes-view', {
            viewModel: {
                links: {
                    theProcess: {
                        type: 'CMDBuildUI.model.processes.Process',
                        id: decodeURI(processName)
                    }
                },
                data: {
                    objectTypeName: decodeURI(processName),
                    objectType: 'Process',
                    title: CMDBuildUI.locales.Locales.administration.processes.toolbar.processLabel,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * show process add
     */
    showProcessesAdministrationAdd: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-processes-view', {
            viewModel: {
                links: {
                    theProcess: {
                        type: 'CMDBuildUI.model.processes.Process',
                        create: true
                    }
                },
                data: {
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    },
                    action: 'ADD',
                    objectType: 'Process',
                    title: CMDBuildUI.locales.Locales.administration.processes.toolbar.processLabel
                }
            }
        });
    },

    showReportAdministrationView: function (reportId) {
        var link = {
            theReport: {
                type: 'CMDBuildUI.model.reports.Report',
                id: decodeURI(reportId)
            }
        };
        if (reportId === '_new' || !reportId) {
            delete link.theReport.id;
            link.theReport.create = true;
        }

        var config = {
            singularName: 'report',
            viewModel: {
                data: {
                    reportId: decodeURI(reportId),
                    actions: {
                        view: reportId && reportId !== '_new' ? true : false,
                        edit: false,
                        add: reportId === '_new'
                    },
                    action:
                        reportId && reportId !== '_new'
                            ? CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                            : CMDBuildUI.util.administration.helper.FormHelper.formActions.add,

                    hideForm: !reportId
                },
                formulas: {},
                links: link
            }
        };

        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-reports-tabpanel', config);
    },

    showDashboardAdministrationView: function (dashboardId) {
        var link = {
            theDashboard: {
                type: 'CMDBuildUI.model.dashboards.Dashboard',
                id: dashboardId
            }
        };
        if (!dashboardId || dashboardId === '_new') {
            delete link.theDashboard.id;
            link.theDashboard.create = true;
        }

        var config = {
            // showCard: true,
            viewModel: {
                data: {
                    dashboardId: dashboardId,
                    actions: {
                        view: dashboardId && dashboardId !== '_new' ? true : false,
                        edit: false,
                        add: dashboardId === '_new'
                    },
                    action:
                        dashboardId && dashboardId !== '_new'
                            ? CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                            : CMDBuildUI.util.administration.helper.FormHelper.formActions.add,

                    hideForm: !dashboardId
                },
                formulas: {},
                links: link
            }
        };
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-dashboards-tabpanel',
            config
        );
    },

    showCustompageAdministrationView: function (custompageId) {
        var link = {
            theCustompage: {
                type: 'CMDBuildUI.model.custompages.CustomPage',
                id: decodeURI(custompageId)
            }
        };

        if (!custompageId || custompageId === '_new') {
            delete link.theCustompage.id;
            link.theCustompage.create = true;
        }

        var config = {
            // showCard: true,
            singularName: 'custompage',
            viewModel: {
                data: {
                    custompageId: decodeURI(custompageId),
                    actions: {
                        view: custompageId && custompageId !== '_new' ? true : false,
                        edit: false,
                        add: custompageId === '_new'
                    },
                    action:
                        custompageId && custompageId !== '_new'
                            ? CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                            : CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    hideForm: !custompageId
                },
                formulas: {},
                links: link
            }
        };
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-custompages-tabpanel',
            config
        );
        // {
        //     viewModel: {
        //         links: {
        //             theCustompage: {
        //                 type: 'CMDBuildUI.model.custompages.CustomPage',
        //                     id: decodeURI(custompageId)
        //             }
        //         },

        //         data: {
        //             custompageId: decodeURI(custompageId),
        //                 actions: {
        //                 view: true,
        //                     edit: false,
        //                         add: false
        //             }
        //         }
        //     }
        // });
    },
    showCustomcomponentAdministration_empty: function () {
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getCustomComponentUrl(
            'contextmenu',
            false
        );
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showCustomcomponentAdministrationView_empty: function (componentType, showForm) {
        var hideForm = showForm === 'true' ? false : true;

        if (componentType === 'script') {
            CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
                'administration-content-customcomponents-scripts-view',
                {
                    viewModel: {
                        links: {
                            theCustomcomponent: {
                                type: 'CMDBuildUI.model.customcomponents.Script',
                                create: true
                            }
                        },
                        data: {
                            componentType: decodeURI(componentType),
                            actions: {
                                view: hideForm ? true : false,
                                edit: false,
                                add: hideForm ? false : true
                            },
                            hideForm: hideForm
                        }
                    }
                }
            );
        } else {
            CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
                'administration-content-customcomponents-view',
                {
                    viewModel: {
                        links: {
                            theCustomcomponent: {
                                type: 'CMDBuildUI.model.customcomponents.ContextMenu',
                                create: true
                            }
                        },
                        data: {
                            componentType: decodeURI(componentType),
                            actions: {
                                view: hideForm ? true : false,
                                edit: false,
                                add: hideForm ? false : true
                            },
                            hideForm: hideForm
                        }
                    }
                }
            );
        }
    },

    showCustomcomponentAdministrationView: function (componentType, customcomponentId) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-customcomponents-view', {
            viewModel: {
                links: {
                    theCustomcomponent: {
                        type: 'CMDBuildUI.model.customcomponents.ContextMenu',
                        id: decodeURI(customcomponentId)
                    }
                },

                data: {
                    componentType: componentType,
                    customcomponentId: customcomponentId,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    showCustomcomponentScriptAdministrationView: function (componentType, scriptCode) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-customcomponents-scripts-view',
            {
                viewModel: {
                    links: {
                        theCustomcomponent: {
                            type: 'CMDBuildUI.model.customcomponents.Script',
                            id: decodeURI(scriptCode)
                        }
                    },

                    data: {
                        componentType: componentType,
                        customcomponentId: scriptCode,
                        actions: {
                            view: true,
                            edit: false,
                            add: false
                        }
                    }
                }
            }
        );
    },
    showCustompageAdministrationGeneric_empty: function () {
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getCustomPageUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showUsersAdministrationView_empty: function () {
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getUsersUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showSchedulesAdministrationView_empty: function () {
        var nextUrl;
        nextUrl = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.scheduler.enabled)
            ? (nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getSchedulesUrl())
            : 'administration/schedules/settings';

        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showEmailAdministrationView_empty: function () {
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getEmailAccountsUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showTaskReadEmailAdministrationView_empty: function () {
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getTaskManagerReadEmailsUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showGisAdministrationView_empty: function () {
        var gisEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.enabled);
        var nextUrl;
        var theSession = this.getViewModel().get('theSession');
        if (gisEnabled) {
            nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getGISManageIconsUrl();
        } else if (theSession.adminCan('admin_sysconfig_view') || theSession.adminCan('admin_sysconfig_modify')) {
            nextUrl = 'administration/setup/gis';
        } else {
            return;
        }
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showBimAdministrationView_empty: function () {
        var bimEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.bim.enabled);
        var theSession = this.getViewModel().get('theSession');
        var nextUrl;
        if (bimEnabled) {
            nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getBIMProjectsUrl();
        } else if (theSession.adminCan('admin_sysconfig_view') || theSession.adminCan('admin_sysconfig_modify')) {
            nextUrl = 'administration/setup/bim';
        } else {
            return;
        }
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showLocalizationAdministrationView_empty: function () {
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getLocalizationConfigurationUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showSetupAdministrationView_empty: function () {
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getGeneralOptionsUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showImportExportAdministrationView_empty: function () {
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getImportExportDataTemplatesUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showUsersAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-users-view', {});
    },
    showSchedulesAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-schedules-ruledefinitions-view',
            {}
        );
    },
    showSchedulesSettingsAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-schedules-settings-view',
            {}
        );
    },

    /**
     * Show settings view pages
     */
    showSetupAdministrationView: function (setupPage, action) {
        switch (setupPage) {
            case 'authentication':
                CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
                    'administration-content-setup-elements-authentication',
                    {
                        viewModel: {
                            data: {
                                actions: {
                                    view: true,
                                    edit: false,
                                    add: false
                                }
                            }
                        }
                    }
                );
                break;
            case 'logs':
                CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
                    'administration-content-setup-elements-logs',
                    {
                        viewModel: {
                            data: {
                                actions: {
                                    view: true,
                                    edit: false,
                                    add: false
                                }
                            }
                        }
                    }
                );
                break;
            default:
                CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-setup-view', {
                    viewModel: {
                        data: {
                            currentPage: decodeURI(setupPage),
                            actions: {
                                view: true,
                                edit: false,
                                add: false
                            }
                        }
                    }
                });
                break;
        }
    },

    showGroupsandpermissionsAdministrationView: function (roleId) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-groupsandpermissions-view',
            {
                viewModel: {
                    links: {
                        theGroup: {
                            type: 'CMDBuildUI.model.users.Group',
                            id: decodeURI(roleId)
                        }
                    },
                    data: {
                        objectType: roleId
                    }
                }
            }
        );
    },

    showCloneGroupsandpermissionsAdministrationView: function (roleId) {
        var store = Ext.getStore('groups.Groups');
        var record = store.getById(roleId);
        if (!record) {
            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getPermissionUrl();
            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
        } else {
            var clone = record.clone().getData();
            var viewModel = {
                links: {
                    theGroup: {
                        type: 'CMDBuildUI.model.users.Group',
                        create: clone
                    }
                },
                data: {}
            };
            viewModel.data = {
                isFormHidden: false,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                actions: {
                    view: false,
                    edit: false,
                    add: true
                },
                isCLone: true,
                isCloneOf: roleId
            };

            CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
                'administration-content-groupsandpermissions-view',
                {
                    viewModel: viewModel,
                    showCard: true,
                    hidden: false
                }
            );
        }
    },

    showGroupsandpermissionsAdministration_empty: function (showForm) {
        Ext.ComponentQuery.query('viewport')[0].getViewModel().set('isFormHidden', true);
        var viewModel = {
            links: {
                theGroup: {
                    type: 'CMDBuildUI.model.users.Group',
                    create: true
                }
            },
            data: {}
        };
        if (showForm) {
            viewModel.data = {
                isFormHidden: false,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                actions: {
                    view: false,
                    edit: false,
                    add: true
                }
            };
        }
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-groupsandpermissions-view',
            {
                viewModel: viewModel,
                showCard: showForm ? showForm : false,
                hidden: showForm ? showForm : false
            }
        );
    },

    /**
     * Show email template view pages
     */
    showEmailTemplatesAdministrationView: function (type, templateId, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-emails-templates-view', {
            viewModel: {
                data: {
                    templateType: type,
                    currentPage: decodeURI(templateId),
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show email accounts view pages
     */
    showEmailAccountsAdministrationView: function (emailPage, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-emails-accounts-view', {
            viewModel: {
                data: {
                    currentPage: decodeURI(emailPage),
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show email signatures view pages
     */
    showEmailSignaturesAdministrationView: function (action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-emails-signatures-view', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },
    /**
     * Show email queue page
     */
    showEmailQueueAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-emails-queue-view', {
            viewModel: {}
        });
    },

    /**
     * Show email errors page
     */
    showEmailErrorsAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-emails-errors-view', {
            viewModel: {}
        });
    },
    /**
     * Show localization view pages
     */
    showLocalizationsLocalizationAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-localizations-localization-view',
            {
                viewModel: {
                    data: {
                        actions: {
                            view: true,
                            edit: false,
                            add: false
                        }
                    }
                }
            }
        );
    },

    showNavigationtreeAdministrationView: function (navigationtreesId) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-navigationtrees-view', {
            viewModel: {
                links: {
                    theNavigationtree: {
                        type: 'CMDBuildUI.model.administration.AdminNavTree',
                        id: navigationtreesId
                    }
                },

                data: {
                    navigationtreesId: navigationtreesId,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    showNavigationtreeAdministrationView_empty: function (showForm) {
        var hideForm = showForm === 'true' ? false : true;
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-navigationtrees-view', {
            viewModel: {
                links: {
                    theNavigationtree: {
                        type: 'CMDBuildUI.model.administration.AdminNavTree',
                        create: true
                    }
                },

                data: {
                    actions: {
                        view: hideForm ? true : false,
                        edit: false,
                        add: hideForm ? false : true
                    },
                    hideForm: hideForm
                }
            }
        });
    },

    showDependencyTreeAdministrationView: function (dependencyTreeId) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-dependencytrees-tabpanel', {
            viewModel: {
                links: {
                    theDependencyTree: {
                        type: 'CMDBuildUI.model.dependencytrees.DependencyTree',
                        id: dependencyTreeId
                    }
                },

                data: {
                    dependencyTreesId: dependencyTreeId,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    showDependencyTreeAdministrationView_empty: function (showForm) {
        var hideForm = showForm === 'true' ? false : true;
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-dependencytrees-tabpanel', {
            viewModel: {
                links: {
                    theDependencyTree: {
                        type: 'CMDBuildUI.model.dependencytrees.DependencyTree',
                        create: true
                    }
                },

                data: {
                    actions: {
                        view: hideForm ? true : false,
                        edit: false,
                        add: hideForm ? false : true
                    },
                    hideForm: hideForm
                }
            }
        });
    },

    /**
     * Show localization view pages
     */
    showLocalizationsConfigurationAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-localizations-configuration-view',
            {
                viewModel: {
                    data: {
                        actions: {
                            view: true,
                            edit: false,
                            add: false
                        }
                    }
                }
            }
        );
    },

    /**
     * Show read email task page
     */
    showTaskReadEmailAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-tasks-view', {
            viewModel: {
                taskType: CMDBuildUI.model.tasks.Task.types.emailService,
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    showTaskImportExportAdministrationView: function (type, subType) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-tasks-view', {
            type: type,
            viewModel: {
                data: {
                    subType: subType,
                    taskType: type,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show send email task page
     */
    showTaskSendEmailAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-tasks-view', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },
    /**
     * Show sync event task page
     */
    showTaskSyncEventAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-tasks-view', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show async event task page
     */
    showTaskAsyncEventAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-tasks-view', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show start process task page
     */
    showTaskStartProcessAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-tasks-view', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },
    /**
     * Show wizard task page
     */
    showTaskWizardConnectorAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-tasks-view', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    showViewAdministrationView: function (viewName, type, viewForm) {
        if (viewName === '_new' && !type) {
            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy(
                'href',
                'administration/views/_new/FILTER/false',
                this
            );
            return;
        }

        var link = {
            theViewFilter: {
                type: 'CMDBuildUI.model.views.View',
                id: decodeURI(viewName)
            }
        };
        if (!viewName || viewName === '_new') {
            delete link.theViewFilter.id;
            link.theViewFilter.create = {
                type: type
            };
        }

        var config = {
            viewModel: {
                data: {
                    viewType: type,
                    viewName: viewName,
                    actions: {
                        view: viewName && viewName !== '_new' ? true : false,
                        edit: false,
                        add: viewName === '_new'
                    },
                    action:
                        viewName && viewName !== '_new'
                            ? CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                            : CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    hideForm: viewForm === 'false'
                },
                links: link
            }
        };
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-views-tabpanel', config);
    },

    showViewAdministrationView_empty: function (showForm, viewType) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-views-tabpanel', {
            viewModel: {
                data: {
                    create: true,
                    showForm: showForm,
                    viewType: viewType,
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    }
                }
            }
        });
    },

    showJoinViewAdministrationView_empty: function (showForm) {
        var hideForm = showForm === 'true' ? false : true;
        var viewModel = {
            data: {
                uiContext: 'administration',
                showForm: showForm,
                actions: {
                    view: false,
                    edit: false,
                    add: !hideForm,
                    empty: hideForm
                }
            }
        };

        viewModel.links = {
            theView: {
                type: 'CMDBuildUI.model.views.ConfigurableView',
                create: {
                    type: CMDBuildUI.model.views.View.types.join
                }
            }
        };
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('joinviews-configuration-configuration', {
            viewModel: viewModel
        });
    },

    showJoinViewAdministrationView: function (viewId) {
        var viewModel = {
            data: {
                uiContext: 'administration',
                viewId: viewId,
                actions: {
                    view: true,
                    edit: false,
                    add: false
                }
            }
        };
        CMDBuildUI.model.views.ConfigurableView.load(viewId, {
            scope: this,
            failure: function (record, operation) {
                //do something if the load failed
            },
            success: function (record, operation) {
                //do something if the load succeeded
                viewModel.links = {
                    theView: record
                };

                CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('joinviews-configuration-configuration', {
                    viewModel: viewModel
                });
            },
            callback: function (record, operation, success) {
                //do something whether the load succeeded or failed
            }
        });
    },

    showSearchFilterAdministrationView_empty: function (showForm) {
        var hideForm = showForm === 'true' ? false : true;
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-viewfilters-form', {
            viewModel: {
                links: {
                    theViewFilter: {
                        type: 'CMDBuildUI.model.searchfilters.Searchfilter',
                        create: {
                            shared: true
                        }
                    }
                },

                data: {
                    actions: {
                        view: hideForm ? true : false,
                        edit: false,
                        add: hideForm ? false : true
                    },
                    hideForm: hideForm
                }
            }
        });
    },

    /**
     * Show search filters page
     */
    showSearchFilterAdministrationView: function (searchfilter, showForm) {
        var store = Ext.getStore('searchfilters.Searchfilters');
        var record, recordId;
        var link = {
            theViewFilter: {
                type: 'CMDBuildUI.model.searchfilters.Searchfilter'
            }
        };
        if (store) {
            record = store.findRecord('name', decodeURI(searchfilter));
            if (record) {
                recordId = record.getId();
                link.theViewFilter.id = record.getId();
            }
        }
        if (!recordId || searchfilter === '_new') {
            delete link.theViewFilter.id;
            link.theViewFilter.create = true;
        }

        var config = {
            viewModel: {
                data: {
                    searchfilterName: searchfilter,
                    actions: {
                        view: searchfilter && searchfilter !== '_new' ? true : false,
                        edit: false,
                        add: searchfilter === '_new'
                    },
                    action:
                        searchfilter && searchfilter !== '_new'
                            ? CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                            : CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    hideForm: showForm === 'false'
                },
                links: link
            }
        };
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-searchfilters-tabpanel',
            config
        );
    },
    /**
     * Show GIS Icons management view pages
     */
    showGisManageIconsAdministrationView: function (localizationPage, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-gis-view', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show GIS External Services view pages
     */
    showGisExternalServicesAdministrationView: function (localizationPage, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-gis-externalservices-view',
            {
                viewModel: {
                    data: {
                        actions: {
                            view: true,
                            edit: false,
                            add: false
                        }
                    }
                }
            }
        );
    },

    /**
     * Show GIS Layers Order view pages
     */
    showGisLayersOrderAdministrationView: function (localizationPage, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-gis-layersorder-grid', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show gis menu
     * @param {String} param
     */
    showGisGisMenuAdministrationView: function (param) {
        var add = false,
            view = false;

        var links;
        if (['true', 'false'].indexOf(param) > -1) {
            add = true;
            links = {
                theMenu: {
                    type: 'CMDBuildUI.model.administration.AdminNavTree',
                    create: {
                        group: '_default',
                        type: 'gismenu'
                    }
                }
            };
        } else {
            view = true;
            links = {
                theMenu: {
                    type: 'CMDBuildUI.model.menu.Menu',
                    id: decodeURI(param)
                }
            };
        }
        var config = {
            viewModel: {
                links: links,
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        };
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-gismenus-view', config);
    },

    showGisGisNavigationAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-gisnavigationtrees-view', {
            viewModel: {
                data: {
                    navigationtreesId: 'gisnavigation',
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show GIS Server Layers view pages
     */
    showGisThematismAdministrationView: function (localizationPage, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-gis-thematisms-grid', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show GIS Server Layers view pages
     */
    showBimProjectsAdministrationView: function (localizationPage, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-bim-projects-view', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    showDataETLAdministrationView_empty: function (showForm) {
        var hideForm = showForm === 'true' ? false : true;
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-importexport-datatemplates-view',
            {
                viewModel: {
                    data: {
                        showInMainPanel: true,
                        hideForm: hideForm,
                        action: hideForm
                            ? CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                            : CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                        actions: {
                            view: hideForm ? true : false,
                            edit: false,
                            add: hideForm ? false : true
                        }
                    }
                }
            }
        );
    },
    showDataETLAdministrationView: function (templateId) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-importexport-datatemplates-view',
            {
                viewModel: {
                    data: {
                        showInMainPanel: true,
                        templateId: templateId,
                        hideForm: false,
                        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                        actions: {
                            view: true,
                            edit: false,
                            add: false
                        }
                    }
                }
            }
        );
    },

    showCloneDataETLAdministrationView: function (templateId) {
        var viewModel = {
            data: {
                showInMainPanel: true,
                templateId: templateId,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.clone,
                actions: {
                    view: false,
                    edit: false,
                    add: false,
                    clone: true
                }
            }
        };
        CMDBuildUI.model.importexports.Template.load(templateId, {
            scope: this,
            failure: function (record, operation) {
                //do something if the load failed
            },
            success: function (record, operation) {
                //do something if the load succeeded
                viewModel.data.theGateTemplate = record.copyForClone();

                CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
                    'administration-content-importexport-datatemplates-view',
                    {
                        viewModel: viewModel
                    }
                );
            },
            callback: function (record, operation, success) {
                //do something whether the load succeeded or failed
            }
        });
    },

    showGISETLAdministrationView_empty: function (gateType, showForm) {
        showForm = showForm === 'true' ? true : false;
        var theGate = {
            type: CMDBuildUI.model.importexports.Gate.getModelNameForType(gateType),
            create: {
                type: gateType,
                config: {
                    tag: gateType === 'gis' ? 'cad' : gateType
                }
            }
        };

        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-importexport-gatetemplates-view',
            {
                viewModel: {
                    links: {
                        theGate: theGate
                    },
                    data: {
                        hideForm: !showForm,
                        gateType: gateType,
                        actions: {
                            view: false,
                            edit: false,
                            add: showForm,
                            empty: !showForm
                        }
                    }
                }
            }
        );
    },
    showWebhooksAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-webhooks-panel', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },
    showGISETLAdministrationView: function (gateType, gateCode) {
        var theGate = {
            type: CMDBuildUI.model.importexports.Gate.getModelNameForType(gateType)
        };
        if (Ext.isEmpty(gateCode)) {
            theGate.create = {
                type: gateType
            };
        } else {
            theGate.id = gateCode;
        }
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-importexport-gatetemplates-view',
            {
                viewModel: {
                    links: {
                        theGate: theGate
                    },
                    data: {
                        gateType: gateType,
                        hideForm: false,
                        actions: {
                            view: theGate.id ? true : false,
                            edit: false,
                            add: theGate.id ? false : true
                        }
                    }
                }
            }
        );
    },

    showAdministrationHome: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-home-dashboard');
    },

    showBusDescriptorView_empty: function (showForm) {
        var hideForm = showForm === 'true' ? false : true;
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-bus-descriptors-view', {
            viewModel: {
                links: {
                    theDescriptor: {
                        type: 'CMDBuildUI.model.administration.BusDescriptor',
                        create: true
                    }
                },

                data: {
                    actions: {
                        view: hideForm ? true : false,
                        edit: false,
                        add: hideForm ? false : true
                    },
                    hideForm: hideForm
                }
            }
        });
    },
    showBusDescriptorView: function (descriptorCode) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-bus-descriptors-view', {
            viewModel: {
                links: {
                    theDescriptor: {
                        type: 'CMDBuildUI.model.administration.BusDescriptor',
                        id: descriptorCode
                    }
                },

                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    showBusMessagesView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-bus-messages-view', {});
    },

    showTaskJobRunView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-tasks-jobruns-view', {});
    },

    /**
     * Show plugin manager view
     * @param {String} roleId
     */
    showPluginManagerAdministrationView: function (roleId) {
        let record;
        const store = Ext.getStore('pluginmanager.Plugins');
        if (store) {
            record = store.getById(roleId);
        }

        const config = {
            viewModel: {
                data: {
                    thePlugin: record
                }
            }
        };

        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-pluginmanager-plugin',
            config
        );
    },

    /**
     * Show plugin manager view empty or in add mode
     * @param {Boolean} showForm
     */
    showPluginManagerAdministration_empty: function (showForm) {
        let viewModel = {};

        if (showForm) {
            viewModel = {
                data: {
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add
                }
            };
        }

        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-pluginmanager-view', {
            viewModel: viewModel
        });
    },

    /**
     * Redirect to AI administration view
     */
    showAiAdministrationView_empty: function () {
        const nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getAiConfigurationsUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },

    /**
     * Show AI administration view
     */
    showAiAdministrationView: function () {
        let viewModel = {};

        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-ai-view', {
            viewModel: viewModel
        });
    }
});
