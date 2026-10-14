
(function () {
    var elementId = 'CMDBuildAdministrationContentSchedulesView';
    Ext.define('CMDBuildUI.view.administration.content.schedules.ruledefinitions.View', {
        extend: 'Ext.panel.Panel',

        alias: 'widget.administration-content-schedules-ruledefinitions-view',

        requires: [
            'CMDBuildUI.view.administration.content.schedules.ruledefinitions.ViewController',
            'CMDBuildUI.view.administration.content.schedules.ruledefinitions.ViewModel'
        ],

        controller: 'administration-content-schedules-ruledefinitions-view',
        viewModel: {
            type: 'administration-content-schedules-ruledefinitions-view'
        },
        id: elementId,
        statics: {
            elementId: elementId
        },
        loadMask: true,
        defaults: {
            textAlign: 'left',
            scrollable: true
        },
        layout: 'border',
        items: [{
            xtype: 'administration-content-schedules-ruledefinitions-grid',
            region: 'center',
            bind: {
                hidden: '{isGridHidden}'
            }
        }],
        dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',

            items: [{
                xtype: 'button',
                text: CMDBuildUI.locales.Locales.administration.schedules.addschedule,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.schedules.addschedule'
                },
                ui: 'administration-action-small',
                reference: 'addschedule',
                itemId: 'addschedule',
                autoEl: {
                    'data-testid': 'administration-schedule-toolbar-addScheduleBtn'
                },
                bind: {
                    disabled: '{!toolAction._canAdd}'
                }
            }, {
                xtype: 'textfield',
                name: 'search',
                width: 310,
                emptyText: CMDBuildUI.locales.Locales.administration.schedules.searchschedules,
                localized: {
                    emptyText: 'CMDBuildUI.locales.Locales.administration.schedules.searchschedules'
                },
                cls: 'administration-input',
                reference: 'searchtext',
                itemId: 'searchtext',
                bind: {
                    value: '{search.value}',
                    hidden: '{!canFilter}'
                },
                listeners: {
                    specialkey: 'onSearchSpecialKey'
                },
                triggers: {
                    search: {
                        cls: Ext.baseCSSPrefix + 'form-search-trigger',
                        handler: 'onSearchSubmit',
                        autoEl: {
                            'data-testid': 'administration-schedule-toolbar-form-search-trigger'
                        }
                    },
                    clear: {
                        cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                        handler: 'onSearchClear',
                        autoEl: {
                            'data-testid': 'administration-schedule-toolbar-form-clear-trigger'
                        }
                    }
                },
                autoEl: {
                    'data-testid': 'administration-schedule-toolbar-search-form'
                }
            }, {
                xtype: 'tbfill'
            }, {
                xtype: 'tbtext',
                dock: 'right',
                itemId: 'scheduleGridCounter'
            }]
        }],
        listeners: {
            afterlayout: function (panel) {
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            }
        },

        initComponent: function () {
            var vm = this.getViewModel();
            vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.schedules);
            this.callParent(arguments);
        }
    });
})();