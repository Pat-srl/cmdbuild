Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.ContentManagementFieldsetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-processes-tabitems-properties-fieldsets-contentmanagementfieldsetmodel',
    
    formulas: {        
        modeComboData: function () {
            return [{
                value: 'default',
                label: CMDBuildUI.locales.Locales.administration.common.strings.standard
            }, {
                value: 'custompage',
                label: CMDBuildUI.locales.Locales.administration.common.strings.withcustompage
            }, {
                value: 'view',
                label: CMDBuildUI.locales.Locales.administration.common.strings.withview
            }, {
                value: 'custom',
                label: CMDBuildUI.locales.Locales.administration.common.strings.customroutings
            }];

        },
       
        viewStoreFilter: {
            get: function () {
                var me = this;
                return [function (item) {
                    return item.get('type') === 'FILTER'  && item.get('sourceClassName') == me.get('theObject.name');
                }];
            }
        },

        customStoreData: {
            bind: {
                customRouting: '{theProcess.uiRouting_custom}',
                isPrototype: '{theProcess.prototype}'
            },
            get: function (data) {
                return this.generateRoutingTablesData(data.isPrototype);

            }
        }
    },
    stores: {
        modeComboStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{modeComboData}'
        },
        viewsStore: {
            source: 'views.Views',
            filters: '{viewStoreFilter}'
        },
        custompagesStore: {
            source: 'custompages.CustomPages'            
        },        
        customRoutingsStore: {
            proxy: 'memory',
            fields: ['action', 'default', 'value'],
            data: '{customStoreData}'
        }

    },

    privates: {
        generateRoutingTablesData: function (isPrototype) {            
            var routes = {
                showGrid: 'processes/:processName/instances',
                addProcessInstance: 'processes/:processName/instances/new',
                viewProcessInstance: 'processes/:processName/instances/:idInstance/activities/:activityId/view',  
                viewInRow: 'processes/:processName/instances/:idInstance',                              
                modifyProcessInstance: 'processes/:processName/instances/:idInstance/activities/:activityId/edit',
                notesProcessInstanceTab: 'processes/:processName/instances/:idInstance/activities/:activityId/notes',
                relationsProcessInstanceTab: 'processes/:processName/instances/:idInstance/activities/:activityId/relations',
                historyProcessInstanceTab: 'processes/:processName/instances/:idInstance/activities/:activityId/history',
                emailsProcessInstanceTab: 'processes/:processName/instances/:idInstance/activities/:activityId/emails',
                attachmentsProcessInstanceTab: 'processes/:processName/instances/:idInstance/activities/:activityId/attachments'                
            };

            var currentConfig = this.get('theProcess.uiRouting_custom');
            var storeData = [];
            if (isPrototype) {
                var _route = {
                    action: 'showGrid',
                    'default': routes.showGrid,
                    value: currentConfig && currentConfig.showGrid || null
                };
                storeData.push(_route);
                return storeData;
            }
            Ext.Array.forEach(Ext.Object.getAllKeys(routes), function (route, index) {
                var _route = {
                    action: route,
                    'default': routes[route],
                    value: currentConfig ? currentConfig[route] : null
                };

                storeData.push(_route);
            });

            return storeData;
        }
    }
});