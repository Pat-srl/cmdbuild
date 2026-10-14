Ext.define('CMDBuildUI.view.administration.content.dashboards.common.tabpanel.TabPanelBaseController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dashboards-common-tabpanel',
    requires: ['CMDBuildUI.util.administration.helper.TabPanelHelper'],

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChage'
        },
        '#addEntityBtn': {
            click: 'onNewBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();
        const domainKey = view.domainKey;
        const tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;

        tabPanelHelper.addTab(
            view,
            'properties',
            CMDBuildUI.locales.Locales.administration.common.strings.properties,
            [
                {
                    xtype: Ext.String.format('administration-content-{0}-propertiestab', domainKey)
                }
            ],
            0,
            {
                disabled: '{disabledTabs.properties}',
                hidden: '{hideForm}'
            }
        );

        tabPanelHelper.addTab(
            view,
            'permissions',
            CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.permissions,
            [
                {
                    xtype: Ext.String.format('administration-content-{0}-permissionstab', domainKey)
                }
            ],
            1,
            {
                disabled: '{disabledTabs.permissions}',
                hidden: '{hideForm}'
            }
        );

        Ext.asap(function () {
            if (vm && !vm.isDestroyed) {
                var activeTabValue = vm.get('activeTabs.' + domainKey);
                vm.set('activeTab', view.useActiveTabFallback ? activeTabValue || 0 : activeTabValue);
            }
        });
    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage(
            'activeTabs.' + view.domainKey,
            this,
            view,
            newtab,
            oldtab,
            eOpts
        );
    },

    /**
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewBtnClick: function (item, event, eOpts) {
        this.redirectTo(Ext.String.format('administration/{0}/_new', this.getView().domainKey));
    },

    setCopyButton: function (view) {
        const me = this;
        view.lookupViewModel().bind(
            {
                bindTo: '{theDashboard}'
            },
            function (data) {
                let currentDashboard = view.lookupViewModel().get('theDashboard');
                var copyFromButton = view.up('components-administration-toolbars-formtoolbar').down('#copyFrom');
                copyFromButton.menu.removeAll();

                let callStore, isHomepage;

                if (!currentDashboard.get('group') && currentDashboard.get('group') !== '') {
                    callStore = CMDBuildUI.util.Stores.loadDashboardsStore();
                    isHomepage = false;
                } else {
                    callStore = CMDBuildUI.util.Stores.loadHomepagesStore();
                    isHomepage = true;
                }

                if (!callStore) {
                    return;
                }

                callStore.then(function (dashboards) {
                    dashboards.sort(function (a, b) {
                        return a.get('description').localeCompare(b.get('description'));
                    });
                    Ext.Array.forEach(dashboards, function (dashboard) {
                        let description;
                        if (isHomepage) {
                            if (dashboard.get('name') === '_default') {
                                description = CMDBuildUI.locales.Locales.administration.common.strings['default'];
                            } else {
                                let group = Ext.getStore('groups.Groups').findRecord('name', dashboard.get('name'));
                                description = group.get('description') || dashboard.get('description');
                            }
                        } else {
                            description = dashboard.get('description');
                        }
                        copyFromButton.menu.add({
                            text: description,
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('chart-area', 'solid'),
                            listeners: {
                                click: function () {
                                    me.cloneFrom(dashboard, view, currentDashboard, isHomepage);
                                }
                            }
                        });
                    });
                });
            }
        );
    },

    cloneFrom: function (dashboard, view, currentDashboard, isHomepage) {
        const vm = view.lookupViewModel();
        const data = dashboard.getData({ associated: true });
        const components = data.components || [];
        const rows = (data.layout && data.layout.rows) || [];
        const parameters = (!isHomepage && data.parameters) || [];

        Ext.suspendLayouts();

        currentDashboard.components().loadRawData(components);
        currentDashboard.set('components', components);
        !isHomepage && currentDashboard.set('parameters', parameters);
        currentDashboard.set('layout', data.layout);

        vm.set('componentsData', components);
        vm.set('rowsData', rows);
        !isHomepage && vm.set('globalParametersData', parameters);

        Ext.resumeLayouts(true);
    }
});
