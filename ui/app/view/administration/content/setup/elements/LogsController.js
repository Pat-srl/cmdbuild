Ext.define('CMDBuildUI.view.administration.content.setup.elements.LogsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-logs',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChage'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.setup.elements.Logs} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        view.up('administration-content')
            .getViewModel()
            .set('title', Ext.String.format('{0}', CMDBuildUI.locales.Locales.administration.systemconfig.logs));
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;

        tabPanelHelper.addTab(
            view,
            'log',
            CMDBuildUI.locales.Locales.administration.systemconfig.applicationlogs,
            [
                {
                    xtype: 'administration-content-setup-elements-editlogconfig'
                }
            ],
            0,
            {
                disabled: '{disabledTabs.log}'
            }
        );

        tabPanelHelper.addTab(
            view,
            'retention',
            CMDBuildUI.locales.Locales.administration.systemconfig.logretention,
            [
                {
                    xtype: 'administration-content-setup-elements-logretention'
                }
            ],
            1,
            {
                disabled: '{disabledTabs.retention}'
            }
        );

        tabPanelHelper.addTab(
            view,
            'audit',
            CMDBuildUI.locales.Locales.administration.systemconfig.audit,
            [
                {
                    xtype: 'administration-content-setup-elements-audit'
                }
            ],
            2,
            {
                disabled: '{disabledTabs.audit}'
            }
        );
        tabPanelHelper.addTab(
            view,
            'governancedata',
            CMDBuildUI.locales.Locales.administration.systemconfig.governancedata,
            [
                {
                    xtype: 'administration-content-setup-elements-governancedata'
                }
            ],
            3,
            {
                disabled: '{disabledTabs.governancedata}'
            }
        );
    },

    /**
     * @param {CMDBuildUI.view.administration.content.setup.elements.Logs} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChage: function (view, newtab, oldtab, eOpts) {
        CMDBuildUI.util.administration.helper.TabPanelHelper.onTabChage(
            'activeTabs.logs',
            this,
            view,
            newtab,
            oldtab,
            eOpts
        );
    }
});
