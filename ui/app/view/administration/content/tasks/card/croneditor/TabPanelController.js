Ext.define('CMDBuildUI.view.administration.content.tasks.card.croneditor.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-tasks-card-croneditor-tabpanel',
    requires: [
        'CMDBuildUI.util.administration.helper.TabPanelHelper'
    ],

    control: {
        '#': {
            beforerender: "onBeforeRender"
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var tabPanelHelper = CMDBuildUI.util.administration.helper.TabPanelHelper;
        tabPanelHelper.addTab(view,
            "minutes",
            CMDBuildUI.locales.Locales.administration.tasks.minutes,
            [{
                xtype: 'administration-content-tasks-card-croneditor-tabs-minutestab'
            }],
            0);
        tabPanelHelper.addTab(view,
            "hours",
            CMDBuildUI.locales.Locales.administration.tasks.hours,
            [{
                xtype: 'administration-content-tasks-card-croneditor-tabs-hourstab'
            }],
            1);
        tabPanelHelper.addTab(view,
            "day",
            CMDBuildUI.locales.Locales.administration.tasks.day,
            [{
                xtype: 'administration-content-tasks-card-croneditor-tabs-daytab'
            }],
            2);
        tabPanelHelper.addTab(view,
            "month",
            CMDBuildUI.locales.Locales.administration.tasks.month,
            [{
                xtype: 'administration-content-tasks-card-croneditor-tabs-monthtab'
            }],
            3);
    }
});