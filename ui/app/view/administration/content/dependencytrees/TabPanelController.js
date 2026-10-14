Ext.define('CMDBuildUI.view.administration.content.dependencytrees.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dependencytrees-tabpanel',
    requires: ['CMDBuildUI.util.administration.helper.TabPanelHelper'],

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChange'
        },
        '#addBtn': {
            click: 'onAddBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.dependencytrees.TabPanel} view
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
                    xtype: 'administration-content-dependencytrees-view'
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
                    xtype: 'administration-content-dependencytrees-permissionstab'
                }
            ],
            1,
            {
                disabled: '{!theSession.rolePrivileges.admin_roles_view || disabledTabs.permissions}',
                hidden: true,
                hidden: '{hideForm}'
            }
        );
        vm.set('activeTab', vm.get('activeTabs.dependencytrees'));
    },

    /**
     * @param {CMDBuildUI.view.administration.content.dependencytrees.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChange: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage(
            'activeTabs.dependencytrees',
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
        this.redirectTo('administration/dependencytrees_empty/true');
    }
});
