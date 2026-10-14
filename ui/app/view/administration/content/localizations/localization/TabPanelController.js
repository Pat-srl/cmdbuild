Ext.define('CMDBuildUI.view.administration.content.localizations.localization.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-localizations-localization-tabpanel',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChange'
        }
    },

    onBeforeRender: function () {
        const view = this.getView();
        const vm = this.getViewModel();
        let i = 0;
        const tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;

        tabPanelHelper.addTab(
            view,
            'classes',
            CMDBuildUI.locales.Locales.administration.classes.title,
            [
                {
                    xtype: 'administration-content-localizations-localization-tabitems-commongrid',
                    autoScroll: true,
                    section: 'classes'
                }
            ],
            i++,
            {}
        );

        var wfEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled);
        if (wfEnabled) {
            tabPanelHelper.addTab(
                view,
                'processes',
                CMDBuildUI.locales.Locales.administration.processes.title,
                [
                    {
                        xtype: 'administration-content-localizations-localization-tabitems-commongrid',
                        autoScroll: true,
                        section: 'processes'
                    }
                ],
                i++,
                {}
            );
        }

        tabPanelHelper.addTab(
            view,
            'domains',
            CMDBuildUI.locales.Locales.administration.domains.pluralTitle,
            [
                {
                    xtype: 'administration-content-localizations-localization-tabitems-commongrid',
                    autoScroll: true,
                    section: 'domains'
                }
            ],
            i++,
            {}
        );

        tabPanelHelper.addTab(
            view,
            'lookups',
            CMDBuildUI.locales.Locales.administration.lookuptypes.title,
            [
                {
                    xtype: 'administration-content-localizations-localization-tabitems-commongrid',
                    autoScroll: true,
                    section: 'lookuptypes'
                }
            ],
            i++,
            {}
        );

        tabPanelHelper.addTab(
            view,
            'views',
            CMDBuildUI.locales.Locales.administration.navigation.views,
            [
                {
                    xtype: 'administration-content-localizations-localization-tabitems-commongrid',
                    autoScroll: true,
                    section: 'views'
                }
            ],
            i++,
            {}
        );

        tabPanelHelper.addTab(
            view,
            'searchfilters',
            CMDBuildUI.locales.Locales.administration.navigation.searchfilters,
            [
                {
                    xtype: 'administration-content-localizations-localization-tabitems-commongrid',
                    autoScroll: true,
                    section: 'searchfilters'
                }
            ],
            i++,
            {}
        );

        tabPanelHelper.addTab(
            view,
            'dashboards',
            CMDBuildUI.locales.Locales.administration.navigation.dashboards,
            [
                {
                    xtype: 'administration-content-localizations-localization-tabitems-commongrid',
                    autoScroll: true,
                    section: 'dashboards'
                }
            ],
            i++,
            {}
        );

        tabPanelHelper.addTab(
            view,
            'reports',
            CMDBuildUI.locales.Locales.administration.navigation.reports,
            [
                {
                    xtype: 'administration-content-localizations-localization-tabitems-commongrid',
                    autoScroll: true,
                    section: 'reports'
                }
            ],
            i++,
            {}
        );

        tabPanelHelper.addTab(
            view,
            'menu',
            CMDBuildUI.locales.Locales.administration.menus.plural,
            [
                {
                    xtype: 'administration-content-localizations-localization-tabitems-translationsmenutreepanel',
                    autoScroll: true,
                    section: 'menu'
                }
            ],
            i++,
            {}
        );

        tabPanelHelper.addTab(
            view,
            'customcomponents',
            CMDBuildUI.locales.Locales.administration.navigation.customcomponents,
            [
                {
                    xtype: 'administration-content-localizations-localization-tabitems-commongrid',
                    autoScroll: true,
                    section: 'customcomponents'
                }
            ],
            i++,
            {}
        );

        tabPanelHelper.addTab(
            view,
            'groups',
            CMDBuildUI.locales.Locales.administration.users.fieldLabels.groups,
            [
                {
                    xtype: 'administration-content-localizations-localization-tabitems-commongrid',
                    autoScroll: true,
                    section: 'groups'
                }
            ],
            i++,
            {}
        );

        tabPanelHelper.addTab(
            view,
            'notifications',
            CMDBuildUI.locales.Locales.administration.navigation.notifications,
            [
                {
                    xtype: 'administration-content-localizations-localization-tabitems-commongrid',
                    autoScroll: true,
                    section: 'notifications'
                }
            ],
            i++,
            {}
        );

        vm.set('activeTab', 0);
    },

    onEditBtnClick: function (button, e, eOpts) {
        var grid = this.getView().getActiveTab().down();
        var vm = this.getViewModel();
        vm.set('actions.view', false);
        vm.set('actions.edit', true);
        vm.toggleEnableTabs(vm.get('activeTab'));
        grid.getColumns().forEach(function (column) {
            if (!column.locked) {
                column.setEditor(true);
            }
        });
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.event.Event} event
     */
    onKeyUp: function (field, event) {
        var vm = this.getViewModel();
        var searchTerm = '';
        if (field.getValue()) {
            searchTerm = field.getValue();
        }
        var store = this.getView().getActiveTab().down('panel').getStore();
        if (searchTerm) {
            CMDBuildUI.util.administration.helper.GridHelper.localSearchFilter(store, searchTerm);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        // clear store filter
        var store = this.getView().getActiveTab().down('panel').getStore();
        if (store) {
            CMDBuildUI.util.administration.helper.GridHelper.removeLocalSearchFilter(store);
        }
        // reset input
        field.reset();
    },

    /**
     * @param {CMDBuildUI.view.administration.content.groupsandpermissions.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChange: function (view, newtab, oldtab, eOpts) {
        var field = this.getView().lookupReference('localizationsearchtext');
        if (field.getValue()) {
            this.onSearchClear(field);
            CMDBuildUI.util.administration.helper.GridHelper.removeLocalSearchFilter(oldtab.down('panel').getStore());
        }
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage(
            'activeTabs.localizations',
            this,
            view,
            newtab,
            oldtab,
            eOpts
        );
        if (newtab.viewModelKey === 'menu') {
            this.getViewModel().set('canFilter', false);
        } else {
            this.getViewModel().set('canFilter', true);
        }
    },

    onstoreLoaded: function (store, records) {
        var view = this.getView();
        var vm = this.getViewModel();

        var languagesStore = vm.getStore('languages');
        if (languagesStore.isLoaded()) {
            var languagesList = [];

            Ext.Array.forEach(languagesStore.getRange(), function (item) {
                languagesList.push(item.get('code'));
            });
            vm.set('translationsstore.languagesList', languagesList.join(','));
            vm.set('translationsstore.autoload', true);
        }
    }
});
