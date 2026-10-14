Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.ContentManagementFieldsetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-classes-tabitems-properties-fieldsets-contentmanagementfieldsetmodel',

    data: {
        inputValueColumnFlex: 1,
        displayValueColumnFlex: 1
    },
    formulas: {
        flexManager: {
            bind: '{actions.view}',
            get: function (isView) {
                this.set('inputValueColumnFlex', isView ? 0 : 1);
                this.set('displayValueColumnFlex', !isView ? 0 : 1);
            }
        },
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
                    return item.get('type') === 'FILTER' && item.get('sourceClassName') == me.get('theObject.name');
                }];
            }
        },

        customStoreData: {
            bind: {
                customRouting: '{theObject.uiRouting_custom}',
                isPrototype: '{theObject.prototype}'
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
                showGrid: 'classes/:className/cards',
                addCard: 'classes/:className/cards/new',
                viewCard: 'classes/:className/cards/:idCard/view',
                viewInRow: 'classes/:className/cards/:idCard',
                cloneCard: 'classes/:className/cards/:idCard/clone',
                cloneCardRelations: 'classes/:className/cards/:idCard/clonecardandrelations',
                modifyCard: 'classes/:className/cards/:idCard/edit',
                detailsTab: 'classes/:className/cards/:idCard/details',
                notesTab: 'classes/:className/cards/:idCard/notes',
                relationsTab: 'classes/:className/cards/:idCard/relations',
                historyTab: 'classes/:className/cards/:idCard/history',
                emailsTab: 'classes/:className/cards/:idCard/emails',
                attachmentsTab: 'classes/:className/cards/:idCard/attachments',
                schedulesTab: 'classes/:className/cards/:idCard/schedules'
            };

            var currentConfig = this.get('theObject.uiRouting_custom') || {};
            var storeData = [];
            if (isPrototype) {
                var _route = {
                    action: 'showGrid',
                    'default': routes.showGrid,
                    value: currentConfig.showGrid || null
                };
                storeData.push(_route);
                return storeData;
            }
            Ext.Array.forEach(Ext.Object.getAllKeys(routes), function (route, index) {
                var _route = {
                    action: route,
                    'default': routes[route],
                    value: currentConfig[route] || null
                };

                storeData.push(_route);
            });

            return storeData;
        }
    }
});