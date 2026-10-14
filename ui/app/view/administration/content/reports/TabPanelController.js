Ext.define('CMDBuildUI.view.administration.content.reports.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-reports-tabpanel',
    requires: ['CMDBuildUI.util.administration.helper.TabPanelHelper'],

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChage'
        },
        '#addBtn': {
            click: 'onAddBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.reports.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();
        const tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
        tabPanelHelper.addTab(
            view,
            'properties',
            CMDBuildUI.locales.Locales.administration.common.strings.properties,
            [
                {
                    xtype: 'administration-content-reports-view'
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
                    xtype: 'administration-content-reports-permissionstab'
                }
            ],
            1,
            {
                disabled: '{disabledTabs.permissions}',
                hidden: '{hideForm}'
            }
        );
        vm.set('activeTab', vm.get('activeTabs.reports'));
    },

    /**
     * @param {CMDBuildUI.view.administration.content.reports.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage(
            'activeTabs.reports',
            this,
            view,
            newtab,
            oldtab,
            eOpts
        );
    },

    /**
     * On add report button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddBtnClick: function (button, e, eOpts) {
        this.redirectTo('administration/reports/_new', true);
    }
});
