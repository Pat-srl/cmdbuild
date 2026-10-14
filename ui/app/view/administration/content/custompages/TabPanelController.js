Ext.define('CMDBuildUI.view.administration.content.custompages.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-custompages-tabpanel',
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
     * @param {CMDBuildUI.view.administration.content.custompages.TabPanel} view
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
                    xtype: 'administration-content-custompages-view'
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
                    xtype: 'administration-content-custompages-permissionstab'
                }
            ],
            1,
            {
                disabled: '{!theSession.rolePrivileges.admin_roles_view || disabledTabs.permissions}',
                hidden: '{hideForm}'
            }
        );
        vm.set('activeTab', vm.get('activeTabs.custompages'));
    },

    /**
     * @param {CMDBuildUI.view.administration.content.custompages.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage(
            'activeTabs.custompages',
            this,
            view,
            newtab,
            oldtab,
            eOpts
        );
    },

    /**
     * On add custompage button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddBtnClick: function (button, e, eOpts) {
        const nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getCustomPageUrl(null, true);
        this.redirectTo(nextUrl);
    }
});
