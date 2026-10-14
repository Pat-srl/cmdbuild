/**
 * This class is the controller for the main view for the application. It is specified as
 * the "controller" of the Main view class.
 *
 * TODO - Replace this content of this view to suite the needs of your application.
 */
Ext.define('CMDBuildUI.view.main.MainController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.main',
    mixins: {
        managementroutes: 'CMDBuildUI.mixins.routes.Management',
        adminroutes: 'CMDBuildUI.mixins.routes.Administration'
    },

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    routes: {
        '': {
            action: 'showManagement',
            before: 'onBeforeShowManagement'
        },
        'configuredb': {
            before: 'onBeforeConfigureDB',
            action: 'showConfigureDB'
        },
        'patches': {
            before: 'onBeforeConfigurePatches',
            action: 'showPatches'
        },
        'login': {
            action: 'showLogin',
            before: 'onBeforeShowLogin'
        },
        'logout': {
            action: 'doLogout'
        },
        'gotomamagement': {
            action: 'goToManagement'
        },
        'management': {
            action: 'showManagement',
            before: 'onBeforeShowManagement'
        },
        'gotoadministration': {
            action: 'goToAdministration',
            before: 'adminAccess'
        },
        'administration': {
            action: 'showAdministration',
            before: 'onBeforeShowAdministration'
        },
        'administration/home': {
            action: 'showAdministrationHome',
            before: 'adminAccess'
        },
        'administration/classes': {
            action: 'showClassAdministrationAdd',
            before: 'adminClassAdd'
        },
        'administration/classes_empty': {
            action: 'showClassAdministration_empty',
            before: 'adminClassAccess'
        },
        'administration/classes/:className': {
            action: 'showClassAdministrationView',
            before: 'adminClassAccess'
        },
        'administration/classes/:className/attribute/:attributeName': {
            action: 'showClassAttributeAdministrationView',
            before: 'adminClassAccess'
        },
        'administration/classes/:className/attribute/:attributeName/edit': {
            action: 'showClassAttributeAdministrationEdit',
            before: 'adminClassAccess'
        },
        'administration/lookup_types': {
            action: 'showLookupTypeAdministrationAdd',
            before: 'adminLookupAdd'
        },
        'administration/lookup_types_empty': {
            action: 'showLookupTypeAdministration_empty',
            before: 'adminLookupTypeAccess'
        },
        'administration/lookup_types/:lookupName': {
            action: 'showLookupTypeAdministrationView',
            before: 'adminLookupTypeAccess'
        },
        // TODO permission
        'administration/dms': {
            action: 'showDMSAdministrationView',
            before: 'adminAccess'
        },
        'administration/dmsmodels_empty': {
            action: 'showDMSModelsAdministrationView_empty',
            before: 'adminDMSAccess'
        },
        'administration/dmsmodels': {
            action: 'showDMSModelsAdministrationView',
            before: 'adminDMSAdd'
        },
        'administration/dmsmodels/:modelName': {
            action: 'showDMSModelsAdministrationView',
            before: 'adminDMSAccess'
        },
        'administration/dmscategories_empty': {
            action: 'showDMSCategoriesAdministrationView_empty',
            before: 'adminDMSAccess'
        },
        'administration/dmscategories': {
            action: 'showDMSCategoriesAdministrationView',
            before: 'adminDMSAdd'
        },
        'administration/dmscategories/:categoryTypeHash': {
            action: 'showDMSCategoriesAdministrationView',
            before: 'adminDMSAccess'
        },
        'administration/domains': {
            action: 'showDomainAdministrationCreate',
            before: 'adminDomainAdd'
        },
        'administration/domains_empty': {
            action: 'showDomainAdministration_empty',
            before: 'adminDomainAccess'
        },
        'administration/domains/:domain': {
            action: 'showDomainAdministrationView',
            before: 'adminDomainAccess'
        },
        'administration/menus/:device/:menu': {
            action: 'showMenuAdministrationView',
            before: 'adminMenuAccess',
            conditions: {
                ':device': Ext.String.format(
                    '({0}|{1})',
                    CMDBuildUI.model.menu.Menu.device.mobile,
                    CMDBuildUI.model.menu.Menu.device['default']
                )
            }
        },
        'administration/menus/:device': {
            action: 'showMenuAdministrationAdd',
            before: 'adminMenuAccess',
            conditions: {
                ':device': Ext.String.format(
                    '({0}|{1})',
                    CMDBuildUI.model.menu.Menu.device.mobile,
                    CMDBuildUI.model.menu.Menu.device['default']
                )
            }
        },
        'administration/menus_empty': {
            action: 'showMenuAdministration_empty',
            before: 'adminMenuAccess'
        },
        'administration/menus_empty/:device': {
            action: 'showMenuAdministration_empty',
            before: 'adminMenuAccess',
            conditions: {
                ':device': Ext.String.format(
                    '({0}|{1})',
                    CMDBuildUI.model.menu.Menu.device.mobile,
                    CMDBuildUI.model.menu.Menu.device['default']
                )
            }
        },
        'administration/homepages/:homepageId': {
            action: 'showHomepageAdministrationView',
            before: 'adminHomepagesAccess',
            conditions: {
                ':homepageId': '(_new|[0-9]+)'
            }
        },
        'administration/homepages': {
            action: 'showHomepageAdministrationView',
            before: 'adminHomepagesAccess'
        },
        'administration/menunavigationtrees_empty/:showForm': {
            action: 'showMenuNavigationtreeAdministrationView_empty',
            before: 'adminMenuAccess'
        },
        'administration/menunavigationtrees/:navigationtreeId': {
            action: 'showMenuNavigationtreeAdministrationView',
            before: 'adminMenuAccess'
        },
        'administration/processes': {
            action: 'showProcessesAdministrationAdd',
            before: 'adminProcessAdd'
        },
        'administration/processes_empty': {
            action: 'showProcessAdministration_empty',
            before: 'adminProcessAccess'
        },
        'administration/processes/:process': {
            action: 'showProcessAdministrationView',
            before: 'adminProcessAccess'
        },
        'administration/reports_empty/:showForm': {
            action: 'showReportAdministration_empty',
            before: 'adminReportAccess'
        },
        'administration/reports_empty': {
            action: 'showReportAdministration_empty',
            before: 'adminReportAccess'
        },
        'administration/reports/:reportId': {
            action: 'showReportAdministrationView',
            before: 'adminReportAccess'
        },
        'administration/reports': {
            action: 'showReportAdministrationView',
            before: 'adminReportAccess'
        },
        // 'administration/custompages_empty/:showForm': {
        //     action: 'showCustompageAdministrationGeneric_empty',
        //     before: 'adminUiComponentAccess',
        //     conditions: {
        //         ':showForm': '(true|false)'
        //     }
        // },
        'administration/custompages': {
            action: 'showCustompageAdministrationView',
            before: 'adminUiComponentAccess'
        },
        'administration/custompages/:custompageId': {
            action: 'showCustompageAdministrationView',
            before: 'adminUiComponentAccess',
            conditions: {
                ':custompageId': '(_new|-?[0-9]+)'
            }
        },
        'administration/customcomponents_empty': {
            action: 'showCustomcomponentAdministration_empty',
            before: 'adminUiComponentAccess'
        },
        'administration/customcomponents_empty/:componentType': {
            action: 'showCustomcomponentAdministrationView_empty',
            before: 'adminUiComponentAccess'
        },
        'administration/customcomponents/:componentType/:customcomponentId': {
            action: 'showCustomcomponentAdministrationView',
            before: 'adminUiComponentAccess',
            conditions: {
                ':componentType': '(contextmenu|widget)',
                ':customcomponentId': '(-?[0-9]+)'
            }
        },
        'administration/customcomponents/:componentType/:scriptCode': {
            action: 'showCustomcomponentScriptAdministrationView',
            before: 'adminUiComponentAccess',
            conditions: {
                ':componentType': '(script)'
            }
        },
        'administration/customcomponents_empty/:componentType/:showForm': {
            action: 'showCustomcomponentAdministrationView_empty',
            before: 'adminUiComponentAccess',
            conditions: {
                ':componentType': '(contextmenu|widget|script)',
                ':showForm': '(true|false)'
            }
        },
        'administration/groupsandpermissions_empty/:showForm': {
            action: 'showGroupsandpermissionsAdministration_empty',
            before: 'adminRoleAccess'
        },
        'administration/groupsandpermissions_empty': {
            action: 'showGroupsandpermissionsAdministration_empty',
            before: 'adminRoleAccess'
        },
        'administration/groupsandpermissions/clone/:roleId': {
            action: 'showCloneGroupsandpermissionsAdministrationView',
            before: 'adminRoleAccess'
        },
        'administration/groupsandpermissions/:roleId': {
            action: 'showGroupsandpermissionsAdministrationView',
            before: 'adminRoleAccess'
        },
        'administration/users': {
            action: 'showUsersAdministrationView',
            before: 'adminUserAccess'
        },
        'administration/users_empty': {
            action: 'showUsersAdministrationView_empty',
            before: 'adminUserAccess'
        },
        'administration/setup_empty': {
            action: 'showSetupAdministrationView_empty',
            before: 'adminSetupAccess'
        },
        'administration/setup/:setupPage': {
            action: 'showSetupAdministrationView',
            before: 'adminSetupAccess'
        },
        'administration/webhooks': {
            action: 'showWebhooksAdministrationView',
            before: 'adminSetupAccess'
        },
        'administration/email_empty': {
            action: 'showEmailAdministrationView_empty',
            before: 'adminEmailAccess'
        },
        'administration/notifications/templates/:type': {
            action: 'showEmailTemplatesAdministrationView',
            before: 'adminEmailAccess',
            conditions: {
                ':type': '(all|email|inappnotification|mobilenotification)'
            }
        },
        'administration/email/accounts': {
            action: 'showEmailAccountsAdministrationView',
            before: 'adminEmailAccess'
        },
        'administration/email/signatures': {
            action: 'showEmailSignaturesAdministrationView',
            before: 'adminEmailAccess'
        },
        'administration/email/queue': {
            action: 'showEmailQueueAdministrationView',
            before: 'adminEmailAccess'
        },
        'administration/email/errors': {
            action: 'showEmailErrorsAdministrationView',
            before: 'adminEmailAccess'
        },
        'administration/localizations/localization': {
            action: 'showLocalizationsLocalizationAdministrationView',
            before: 'adminLocalizationAccess'
        },
        'administration/localization_empty': {
            action: 'showLocalizationAdministrationView_empty',
            before: 'adminLocalizationAccess'
        },
        'administration/localizations/configuration': {
            action: 'showLocalizationsConfigurationAdministrationView',
            before: 'adminLocalizationAccess'
        },
        'administration/navigationtrees/:navigationtreeId': {
            action: 'showNavigationtreeAdministrationView',
            before: 'adminNavTreeAccess'
        },
        'administration/navigationtrees_empty/:showForm': {
            action: 'showNavigationtreeAdministrationView_empty',
            before: 'adminNavTreeAccess'
        },
        'administration/dependencytrees/:dependencytreeId': {
            action: 'showDependencyTreeAdministrationView',
            before: 'adminNavTreeAccess'
        },
        'administration/dependencytrees_empty/:showForm': {
            action: 'showDependencyTreeAdministrationView_empty',
            before: 'adminNavTreeAccess'
        },
        'administration/tasks': {
            action: 'showTaskImportExportAdministrationView',
            before: 'adminTaskAccess'
        },
        'administration/tasks/:type': {
            action: 'showTaskImportExportAdministrationView',
            before: 'adminTaskAccess'
        },

        'administration/tasks/:type/:subtype': {
            action: 'showTaskImportExportAdministrationView',
            before: 'adminTaskAccess',
            conditions: {
                ':type': '(etl)',
                ':subtype': '(cad|database|ifc)'
            }
        },

        'administration/searchfilters/:searchfilter/:showForm': {
            action: 'showSearchFilterAdministrationView',
            before: 'adminSearchFilterAccess'
        },
        'administration/searchfilters/:searchfilter': {
            action: 'showSearchFilterAdministrationView',
            before: 'adminSearchFilterAccess'
        },
        'administration/dashboards/:dashboardId': {
            action: 'showDashboardAdministrationView',
            before: 'adminDashboardAccess',
            conditions: {
                ':dashboardId': '(_new|[0-9]+)'
            }
        },
        'administration/dashboards': {
            action: 'showDashboardAdministrationView',
            before: 'adminDashboardAccess'
        },

        'administration/views/:viewName/:type/:showForm': {
            action: 'showViewAdministrationView',
            before: 'adminViewAccess'
        },
        'administration/views/:viewName': {
            action: 'showViewAdministrationView',
            before: 'adminViewAccess'
        },
        'administration/views_empty/:showForm': {
            action: 'showViewAdministrationView_empty',
            before: 'adminViewAccess'
        },
        'administration/joinviews_empty/:showForm': {
            action: 'showJoinViewAdministrationView_empty',
            before: 'adminViewAccess'
        },
        'administration/joinviews/:viewId': {
            action: 'showJoinViewAdministrationView',
            before: 'adminViewAccess'
        },
        'administration/views_empty/:showForm/:viewType': {
            action: 'showViewAdministrationView_empty',
            before: 'adminViewAccess'
        },
        'administration/schedules/ruledefinitions': {
            action: 'showSchedulesAdministrationView',
            before: 'adminScheduleAccess'
        },
        'administration/schedules/settings': {
            action: 'showSchedulesSettingsAdministrationView',
            before: 'adminSetupAccess'
        },
        'administration/schedules_empty': {
            action: 'showSchedulesAdministrationView_empty',
            before: 'adminScheduleAccess'
        },
        'administration/gis_empty': {
            action: 'showGisAdministrationView_empty',
            before: 'adminAccess'
        },
        'administration/gis/manageicons': {
            action: 'showGisManageIconsAdministrationView',
            before: 'adminGisAccess'
        },
        'administration/gis/externalservices': {
            action: 'showGisExternalServicesAdministrationView',
            before: 'adminGisExternalServicesAccess'
        },
        'administration/gis/layersorder': {
            action: 'showGisLayersOrderAdministrationView',
            before: 'adminGisAccess'
        },
        'administration/gis/gismenu/:showForm': {
            action: 'showGisGisMenuAdministrationView',
            before: 'adminGisMenuAccess',
            conditions: {
                ':showForm': '(false|true)'
            }
        },
        'administration/gis/gismenu/:menu': {
            action: 'showGisGisMenuAdministrationView',
            before: 'adminGisMenuAccess'
        },
        'administration/gis/gisnavigation': {
            action: 'showGisGisNavigationAdministrationView',
            before: 'adminGisAccess'
        },
        'administration/gis/thematism': {
            action: 'showGisThematismAdministrationView',
            before: 'adminGisAccess'
        },
        'administration/bim_empty': {
            action: 'showBimAdministrationView_empty',
            before: 'adminAccess'
        },
        'administration/bim/projects': {
            action: 'showBimProjectsAdministrationView',
            before: 'adminBimAccess'
        },
        'administration/importexport_empty': {
            action: 'showImportExportAdministrationView_empty',
            before: 'adminImportExportAccess'
        },
        'administration/importexport/datatemplates_empty/:hideForm': {
            action: 'showDataETLAdministrationView_empty',
            before: 'adminImportExportAccess'
        },
        'administration/importexport/datatemplates': {
            action: 'showDataETLAdministrationView',
            before: 'adminImportExportAccess'
        },
        'administration/importexport/datatemplates/clone/:templateId': {
            action: 'showCloneDataETLAdministrationView',
            before: 'adminImportExportAccess'
        },
        'administration/importexport/datatemplates/:templateId': {
            action: 'showDataETLAdministrationView',
            before: 'adminImportExportAccess'
        },
        'administration/importexport/gatetemplates_empty/:gateType/:showForm?': {
            action: 'showGISETLAdministrationView_empty',
            before: 'adminImportExportAccess'
        },
        'administration/importexport/gatetemplates/:gateType': {
            action: 'showGISETLAdministrationView',
            before: 'adminImportExportAccess'
        },
        'administration/importexport/gatetemplates/:gateType/:gateCode': {
            action: 'showGISETLAdministrationView',
            before: 'adminImportExportAccess'
        },
        'administration/bus/descriptors_empty/:showForm': {
            action: 'showBusDescriptorView_empty',
            before: 'adminBusAccess',
            conditions: {
                ':showForm': '(false|true)'
            }
        },
        'administration/bus/descriptors/:descriptorId': {
            action: 'showBusDescriptorView',
            before: 'adminBusAccess'
        },
        'administration/bus/messages': {
            action: 'showBusMessagesView',
            before: 'adminBusAccess'
        },
        'administration/tasks/jobruns': {
            action: 'showTaskJobRunView',
            before: 'adminTaskAccess'
        },
        'administration/pluginmanager_empty/:showForm': {
            action: 'showPluginManagerAdministration_empty',
            before: 'adminAccess'
        },
        'administration/pluginmanager_empty': {
            action: 'showPluginManagerAdministration_empty',
            before: 'adminAccess'
        },
        'administration/pluginmanager/:roleId': {
            action: 'showPluginManagerAdministrationView',
            before: 'adminAccess'
        },
        'administration/ai_empty': {
            action: 'showAiAdministrationView_empty',
            before: 'adminAccess'
        },
        /* END ADMINISTRATION ROUTES */

        // CLASSES
        'classes/:className/cards': {
            action: 'showCardsGrid',
            before: 'onBeforeShowCardsGrid'
        },
        'classes/:className/cards/:idCard': {
            action: 'showCard',
            before: 'onBeforeShowCard',
            conditions: {
                ':idCard': '([0-9]+)'
            }
        },
        'classes/:className/cards/new': {
            action: 'showCardCreate',
            before: 'onBeforeShowCardWindow'
        },
        'classes/:className/cards/:idCard/:view': {
            action: 'showCardView',
            before: 'onBeforeShowCardWindow',
            conditions: {
                ':view': '(view)'
            }
        },
        'classes/:className/cards/:idCard/:clone': {
            action: 'showCardClone',
            before: 'onBeforeShowCardWindow',
            conditions: {
                ':clone': '(clone)'
            }
        },
        'classes/:className/cards/:idCard/:clonecardandrelations': {
            action: 'showCardCloneandRelations',
            before: 'onBeforeShowCardWindow',
            conditions: {
                ':clonecardandrelations': '(clonecardandrelations)'
            }
        },
        'classes/:className/cards/:idCard/:edit': {
            action: 'showCardEdit',
            before: 'onBeforeShowCardWindow',
            conditions: {
                ':edit': '(edit)'
            }
        },
        'classes/:className/cards/:idCard/:details': {
            action: 'showCardDetails',
            before: 'onBeforeShowCardWindow',
            conditions: {
                ':details': '(details)'
            }
        },
        'classes/:className/cards/:idCard/:notes': {
            action: 'showCardNotes',
            before: 'onBeforeShowCardWindow',
            conditions: {
                ':notes': '(notes)'
            }
        },
        'classes/:className/cards/:idCard/:relations': {
            action: 'showCardRelations',
            before: 'onBeforeShowCardWindow',
            conditions: {
                ':relations': '(relations)'
            }
        },
        'classes/:className/cards/:idCard/:history': {
            action: 'showCardHistory',
            before: 'onBeforeShowCardWindow',
            conditions: {
                ':history': '(history)'
            }
        },
        'classes/:className/cards/:idCard/:emails': {
            action: 'showCardEmails',
            before: 'onBeforeShowCardWindow',
            conditions: {
                ':emails': '(emails)'
            }
        },
        'classes/:className/cards/:idCard/:attachments': {
            action: 'showCardAttachments',
            before: 'onBeforeShowCardWindow',
            conditions: {
                ':attachments': '(attachments)'
            }
        },
        'classes/:className/cards/:idCard/:schedules': {
            action: 'showCardSchedules',
            before: 'onBeforeShowCardWindow',
            conditions: {
                ':schedules': '(schedules)'
            }
        },
        // PROCESSES
        'processes/:processName/instances': {
            action: 'showProcessInstancesGrid',
            before: 'onBeforeShowProcessInstancesGrid'
        },
        'processes/:processName/instances/:new': {
            action: 'showProcessInstanceCreate',
            before: 'onBeforeShowProcessInstanceWindow',
            conditions: {
                ':new': '(new)'
            }
        },
        'processes/:processName/instances/:idInstance': {
            action: 'showProcessInstance',
            before: 'onBeforeShowProcessInstance',
            conditions: {
                ':idInstance': '([0-9]+)'
            }
        },
        'processes/:processName/instances/:idInstance/view': {
            action: 'openProcessInstanceView'
        },
        'processes/:processName/instances/:idInstance/edit': {
            action: 'openProcessInstanceEdit'
        },
        'processes/:processName/instances/:idInstance/activities/:activityId/:view': {
            action: 'showProcessInstanceView',
            before: 'onBeforeShowProcessInstanceWindow',
            conditions: {
                ':view': '(view)'
            }
        },
        'processes/:processName/instances/:idInstance/activities/:activityId/:edit': {
            action: 'showProcessInstanceEdit',
            before: 'onBeforeShowProcessInstanceWindow',
            conditions: {
                ':edit': '(edit)'
            }
        },
        'processes/:processName/instances/:idInstance/activities/:activityId/:notes': {
            action: 'showProcessInstanceNotes',
            before: 'onBeforeShowProcessInstanceWindow',
            conditions: {
                ':notes': '(notes)'
            }
        },
        'processes/:processName/instances/:idInstance/activities/:activityId/:relations': {
            action: 'showProcessInstanceRelations',
            before: 'onBeforeShowProcessInstanceWindow',
            conditions: {
                ':relations': '(relations)'
            }
        },
        'processes/:processName/instances/:idInstance/activities/:activityId/:history': {
            action: 'showProcessInstanceHistory',
            before: 'onBeforeShowProcessInstanceWindow',
            conditions: {
                ':history': '(history)'
            }
        },
        'processes/:processName/instances/:idInstance/activities/:activityId/:emails': {
            action: 'showProcessInstanceEmails',
            before: 'onBeforeShowProcessInstanceWindow',
            conditions: {
                ':emails': '(emails)'
            }
        },
        'processes/:processName/instances/:idInstance/activities/:activityId/:attachments': {
            action: 'showProcessInstanceAttachments',
            before: 'onBeforeShowProcessInstanceWindow',
            conditions: {
                ':attachments': '(attachments)'
            }
        },
        // CUSTOM PAGES
        'custompages/:pageName': {
            action: 'showCustomPage',
            before: 'onBeforeShowCustomPage'
        },
        'custompages/:pageName/:processes/:typename/instances': {
            action: 'showCustomPage',
            before: 'onBeforeShowCustomPage',
            conditions: {
                ':processes': '(processes)'
            }
        },
        'custompages/:pageName/:classes/:typename/cards': {
            action: 'showCustomPage',
            before: 'onBeforeShowCustomPage',
            conditions: {
                ':classes': '(classes)'
            }
        },
        'custompages/:pageName/:classes/:className/cards/:cardId': {
            action: 'showCustomPage',
            before: 'onBeforeShowCpCard',
            conditions: {
                ':cardId': '([0-9]+)',
                ':classes': '(classes)'
            }
        },
        'custompages/:pageName/:processes/:processName/instances/:idInstance': {
            action: 'showCustomPage',
            before: 'onBeforeShowCpProcessInstance',
            conditions: {
                ':idInstance': '([0-9]+)',
                ':processes': '(processes)'
            }
        },
        'custompages/:pageName/classes/:className/cards/:new': {
            action: 'showCpCardAction',
            before: 'onBeforeShowCpCardWindow',
            conditions: {
                ':new': '(new)'
            }
        },
        'custompages/:pageName/classes/:className/cards/:cardId/:action': {
            action: 'showCpCardAction',
            before: 'onBeforeShowCpCardWindow',
            conditions: {
                ':cardId': '([0-9]+)'
            }
        },
        'custompages/:pageName/processes/:processName/instances/:new': {
            action: 'showCpProcessInstanceAction',
            before: 'onBeforeShowCpProcessInstanceWindow',
            conditions: {
                ':new': '(new)'
            }
        },
        'custompages/:pageName/processes/:processName/instances/:idInstance/activities/:activityId/:action': {
            action: 'showCpProcessInstanceAction',
            before: 'onBeforeShowCpProcessInstanceWindow'
        },
        // REPORTS
        'reports/:reportName': {
            action: 'showReport',
            before: 'onBeforeShowReport'
        },
        'reports/:reportName/:extension': {
            action: 'showReportExtension',
            before: 'onBeforeShowReportExtension'
        },
        // VIEWS
        'views/:viewName/:items': {
            action: 'showView',
            before: 'onBeforeShowView',
            conditions: {
                ':items': '(items)'
            }
        },
        'views/:viewName/:events': {
            action: 'showView',
            before: 'onBeforeShowView',
            conditions: {
                ':events': '(events)'
            }
        },
        'views/:viewName/:classes/:className/cards': {
            action: 'showView',
            before: 'onBeforeShowView',
            conditions: {
                ':classes': '(classes)'
            }
        },
        'views/:viewName/:processes/:processName/instances': {
            action: 'showView',
            before: 'onBeforeShowView',
            conditions: {
                ':processes': '(processes)'
            }
        },
        'views/:viewName/:classes/:className/cards/:cardId': {
            action: 'showView',
            before: 'onBeforeShowVwCard',
            conditions: {
                ':classes': '(classes)',
                ':cardId': '([0-9]+)'
            }
        },
        'views/:viewName/:events/:eventId': {
            action: 'showView',
            before: 'onBeforeShowVwEvent',
            conditions: {
                ':events': '(events)',
                ':eventId': '([0-9]+)'
            }
        },
        'views/:viewName/:processes/:processName/instances/:idInstance': {
            action: 'showView',
            before: 'onBeforeShowVwProcess',
            conditions: {
                ':processes': '(processes)',
                ':idInstance': '([0-9]+)'
            }
        },

        //events action
        'views/:viewName/:events/:eventId/:action': {
            action: 'showVwEventAction',
            before: 'onBeforeShowVwEventWindow',
            conditions: {
                ':events': '(events)'
            }
        },
        'views/:viewName/:events/:new': {
            action: 'showVwEventAction',
            before: 'onBeforeShowVwEventWindow',
            conditions: {
                ':events': '(events)',
                ':new': '(new)'
            }
        },

        //class action
        'views/:viewName/:classes/:className/cards/:cardId/:action': {
            action: 'showVwClassAction',
            before: 'onBeforeShowVwClassWindow',
            conditions: {
                ':classes': '(classes)'
            }
        },
        'views/:viewName/:classes/:className/cards/:new': {
            action: 'showVwClassAction',
            before: 'onBeforeShowVwClassWindow',
            conditions: {
                ':classes': '(classes)',
                ':new': '(new)'
            }
        },

        //process action
        'views/:viewName/:processes/:processName/instances/:idInstance/activities/:activityId/:action': {
            action: 'showVwProcessInstanceAction',
            before: 'onBeforeShowVwProcessInstanceWindow',
            conditions: {
                ':processes': '(processes)'
            }
        },
        'views/:viewName/:processes/:processName/instances/:new': {
            action: 'showVwProcessInstanceAction',
            before: 'onBeforeShowVwProcessInstanceWindow',
            conditions: {
                ':processes': '(processes)',
                ':new': '(new)'
            }
        },

        // DASHBOARDS
        'dashboards/:dashboardName': {
            action: 'showDashboard',
            before: 'onBeforeShowDashboard'
        },
        // HOMEPAGE
        'homepage': {
            action: 'showHomepage',
            before: 'onBeforeShowHomepage'
        },
        // EVENTS
        'events': {
            action: 'showEvents',
            before: 'beforeShowEvents'
        },
        'events/:idEvent': {
            action: 'showEvent',
            before: 'beforeShowEvent',
            conditions: {
                ':idEvent': '([0-9]+)'
            }
        },
        'events/:idEvent/view': {
            action: 'showEventView',
            before: 'onBeforeShowEventWindow'
        },
        'events/:idEvent/edit': {
            action: 'showEventEdit',
            before: 'onBeforeShowEventWindow'
        },
        'events/new': {
            action: 'showEventCreate',
            before: 'onBeforeShowEventWindow'
        },
        'events/:idEvent/notes': {
            action: 'showEventNotes',
            before: 'onBeforeShowEventWindow'
        },
        'events/:idEvent/history': {
            action: 'showEventHistory',
            before: 'onBeforeShowEventWindow'
        },
        'events/:idEvent/emails': {
            action: 'showEventEmails',
            before: 'onBeforeShowEventWindow'
        },
        'events/:idEvent/attachments': {
            action: 'showEventAttachments',
            before: 'onBeforeShowEventWindow'
        },

        // NAVIGATION TREE
        'navigation/:navTreeName': {
            action: 'showNavigationTreeContent'
        },

        'navigation/:navTreeName/classes/:className/cards': {
            action: 'navigationCards'
        },

        'navigation/:navTreeName/classes/:className/cards/:cardId': {
            action: 'navigationCardsId',
            conditions: {
                cardId: '([0-9]+)'
            }
        },

        'navigation/:navTreeName/classes/:className/cards/:cardId/:action': {
            action: 'navigationCardsIdAction',
            conditions: {
                cardId: '([0-9]+)',
                action: '(view|edit|clone|clonecardandrelations|details|notes|relations|history|emails|attachments|schedules)'
            }
        },
        'navigation/:navTreeName/classes/:className/cards/:action': {
            action: 'navigationCardsCreate',
            conditions: {
                cardId: '([0-9]+)',
                action: '(new)'
            }
        },
        'offlineMode/:tempId/:DBName': {
            action: 'executeDiffForOfflineData'
        },
        'dependencytree/:dependencyTreeName': {
            action: 'showDependencyTree'
        },
        'dependencytree/:dependencyTreeName/root/:cardId': {
            action: 'showDependencyTreeOnCard'
        }
    },

    init: function () {
        this.callParent(arguments);
        var routes = CMDBuildUI.util.Navigation._customroutes;
        if (routes) {
            CMDBuildUI.util.api.Client.addRoutes(routes, this);
            delete CMDBuildUI.util.Navigation._customroutes;
        }
    },

    onBeforeRender: function () {
        var me = this;
        var currentUrl = Ext.History.getToken();
        if (currentUrl === 'gotoadministration') {
            me.redirectTo('administration', true);
        } else if (currentUrl !== 'login') {
            me.redirectTo('login');
        }
    },

    /**
     *
     * @param {Ext.data.Store} store
     * @param {CMDBuildUI.model.messages.Notification[]} records
     * @param {Boolean} successful
     * @param {Ext.data.operation.Read} operation
     * @param {Object} eOpts
     */
    onNotificationStoreLoad: function (store, records, successful, operation, eOpts) {
        this.checkForUnreadedNotifications(store);
    },

    /**
     *
     * @param {Ext.data.Store} store
     * @param {CMDBuildUI.model.messages.Notification} record
     * @param {String} operation
     * @param {String[]} modifiedFieldNames
     * @param {Object} details
     * @param {Object} eOpts
     */
    onNotificationStoreUpdate: function (store, record, operation, modifiedFieldNames, details, eOpts) {
        this.checkForUnreadedNotifications(store);
    },

    /**
     *
     * @param {Ext.data.Store} store
     * @param {CMDBuildUI.model.messages.Notification[]} records
     * @param {Number} index
     * @param {Boolean} isMove
     * @param {Object} eOpts
     */
    onNotificationStoreRemove: function (store, records, index, isMove, eOpts) {
        this.checkForUnreadedNotifications(store);
    },

    /**
     *
     * @param {String} tempId
     * @param {String} DBName
     */
    executeDiffForOfflineData: function (tempId, DBName) {
        var popup = CMDBuildUI.util.Utilities.openPopup(
            null,
            Ext.String.format(CMDBuildUI.locales.CustomLocales.mobileOfflinePlugin.popuptitle, DBName),
            {
                xtype: 'custompages-diff-panel',
                tempId: tempId,
                DBName: DBName,
                closePopup: function () {
                    popup.destroy();
                }
            }
        );
    },

    privates: {
        /**
         * Check for unreaded notifications
         * @param {Ext.data.Store} store
         */
        checkForUnreadedNotifications: function (store) {
            var vm = this.getViewModel();
            vm.set('notifications.hasUnread', !!store.findRecord('_isNew', true));
            vm.set('notifications.count', store.getCount());
        }
    }
});
