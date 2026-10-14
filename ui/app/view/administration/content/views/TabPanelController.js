Ext.define('CMDBuildUI.view.administration.content.views.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-views-tabpanel',
    requires: ['CMDBuildUI.util.administration.helper.TabPanelHelper'],

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: function (view) {
                view.getDockedItems().forEach(function (e) {
                    if (!e.items.length) {
                        view.removeDocked(e);
                    }
                });
            },
            tabchange: 'onTabChage'
        },
        '#addBtn': {
            click: 'onAddBtnClick'
        }
    },

    /**
     * @param {CCMDBuildUI.view.administration.content.views.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        if (!vm.get('hideForm')) {
            var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
            tabPanelHelper.addTab(
                view,
                'properties',
                CMDBuildUI.locales.Locales.administration.common.strings.properties,
                [
                    {
                        xtype: 'administration-content-views-card-form'
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
                        xtype: 'administration-content-views-card-permissions'
                    }
                ],
                1,
                {
                    disabled: '{disabledTabs.permissions}',
                    hidden: '{hideForm}'
                }
            );
            vm.set('activeTab', vm.get('activeTabs.views'));
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.custompages.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage(
            'activeTabs.views',
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
        var vm = button.lookupViewModel(),
            type = vm.get('theViewFilter.type');
        this.redirectTo(Ext.String.format('administration/views/_new/{0}/true', type), true);
    }
});
