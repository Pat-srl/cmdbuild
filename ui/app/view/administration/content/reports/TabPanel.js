Ext.define('CMDBuildUI.view.administration.content.reports.TabPanel', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.administration-content-reports-tabpanel',
    controller: 'administration-content-reports-tabpanel',
    viewModel: {
        type: 'administration-content-reports-tabpanel'
    },
    requires: [
        'CMDBuildUI.view.administration.content.reports.TabPanelController',
        'CMDBuildUI.view.administration.content.reports.TabPanelModel',
        'CMDBuildUI.view.administration.content.reports.View',
        'CMDBuildUI.view.administration.content.reports.PermissionsTab'
    ],

    tabPosition: 'top',
    tabRotation: 0,
    cls: 'administration-mainview-tabpanel',
    ui: 'administration-tabandtools',
    scrollable: true,
    forceFit: true,
    layout: 'fit',

    bind: {
        activeTab: '{activeTab}'
    },

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button',
                    text: CMDBuildUI.locales.Locales.administration.reports.texts.addreport, // Add report
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.reports.texts.addreport'
                    },
                    ui: 'administration-action-small',
                    itemId: 'addBtn',
                    autoEl: {
                        'data-testid': 'administration-reports-addBtn'
                    },
                    bind: {
                        disabled: '{!toolAction._canAdd}'
                    }
                },
                {
                    xtype: 'admin-globalsearchfield',
                    objectType: 'reports'
                },
                {
                    xtype: 'tbfill'
                },
                {
                    xtype: 'tbtext',
                    hidden: true,
                    bind: {
                        hidden: '{!theReport.description}',
                        html: '{componentTypeName}: <b data-testid="administration-report-description">{theReport.description}</b>'
                    }
                }
            ]
        }
    ],

    listeners: {
        itemupdated: 'onItemUpdated',
        cancelcreation: 'onCancelCreation',
        cancelupdating: 'onCancelUpdating'
    }
});
