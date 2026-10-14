Ext.define('CMDBuildUI.view.administration.content.schedules.settings.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-schedules-settings-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChage',
            enabletabs: 'onEnableTabs',
            disabletabs: 'onDisableTabs'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.schedules.settings.View} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = view.lookupViewModel();
        vm.set(
            'title',
            Ext.String.format(
                '{0} - {1}',
                CMDBuildUI.locales.Locales.administration.navigation.schedules,
                CMDBuildUI.locales.Locales.administration.navigation.settings
            )
        );

        const parentLookupsStore = Ext.create('Ext.data.Store', {
            fields: ['_id', 'description'],
            data: [],
            proxy: {
                type: 'memory'
            }
        });

        const actions = {
            add: false,
            edit: false,
            view: true
        };

        CMDBuildUI.util.administration.helper.TabPanelHelper.addTab(
            view,
            'generalproperties',
            CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            [
                {
                    xtype: 'administration-content-schedules-settings-tabs-generalproperties-form'
                }
            ],
            0,
            {
                disabled: '{disabledTabs.generalproperties}'
            }
        );

        CMDBuildUI.util.administration.helper.TabPanelHelper.addTab(
            view,
            'statuses',
            CMDBuildUI.locales.Locales.administration.schedules.statuses,
            [
                {
                    xtype: 'administration-content-lookuptypes-tabitems-values-grid-grid',
                    viewModel: {
                        data: {
                            gridItemId: 'CalendarEventStatus',
                            actions: actions,
                            theLookupType:
                                CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName('CalendarEventStatus'),
                            parentLookupsStore: parentLookupsStore
                        }
                    }
                }
            ],
            1,
            {
                disabled: '{disabledTabs.statuses}'
            }
        );

        CMDBuildUI.util.administration.helper.TabPanelHelper.addTab(
            view,
            'categories',
            CMDBuildUI.locales.Locales.administration.schedules.categories,
            [
                {
                    xtype: 'administration-content-lookuptypes-tabitems-values-grid-grid',
                    viewModel: {
                        data: {
                            gridItemId: 'CalendarCategory',
                            actions: actions,
                            theLookupType:
                                CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName('CalendarCategory'),
                            parentLookupsStore: parentLookupsStore
                        }
                    }
                }
            ],
            2,
            {
                disabled: '{disabledTabs.categories}'
            }
        );

        CMDBuildUI.util.administration.helper.TabPanelHelper.addTab(
            view,
            'priorities',
            CMDBuildUI.locales.Locales.administration.schedules.priorities,
            [
                {
                    xtype: 'administration-content-lookuptypes-tabitems-values-grid-grid',
                    viewModel: {
                        data: {
                            gridItemId: 'CalendarPriority',
                            actions: actions,
                            theLookupType:
                                CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName('CalendarPriority'),
                            parentLookupsStore: parentLookupsStore
                        }
                    }
                }
            ],
            3,
            {
                disabled: '{disabledTabs.priorities}'
            }
        );

        CMDBuildUI.util.administration.helper.TabPanelHelper.addTab(
            view,
            'manualschedules',
            CMDBuildUI.locales.Locales.administration.schedules.manualschedules,
            [
                {
                    xtype: 'administration-content-schedules-settings-tabs-manualschedules-form'
                }
            ],
            4,
            {
                disabled: '{disabledTabs.manualschedules}'
            }
        );

        const activeTab = vm.get('activeTabs.dmssettings') || 0;
        view.setActiveTab(activeTab);
    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage(
            'activeTabs.dmssettings',
            this,
            view,
            newtab,
            oldtab,
            eOpts
        );
    },

    /**
     *
     */
    onDisableTabs: function () {
        const view = this.getView();
        const activeTab = view.getActiveTab();
        view.items.each(function (tab) {
            if (tab.reference !== activeTab.reference) {
                tab.setDisabled(true);
            }
        });
    },

    /**
     *
     */
    onEnableTabs: function () {
        const view = this.getView();
        view.items.each(function (tab) {
            tab.setDisabled(false);
        });
    }
});
