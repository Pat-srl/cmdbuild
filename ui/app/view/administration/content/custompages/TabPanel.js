Ext.define('CMDBuildUI.view.administration.content.custompages.TabPanel', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.administration-content-custompages-tabpanel',
    controller: 'administration-content-custompages-tabpanel',
    viewModel: {
        type: 'administration-content-custompages-tabpanel'
    },
    requires: [
        'CMDBuildUI.view.administration.content.custompages.TabPanelController',
        'CMDBuildUI.view.administration.content.custompages.TabPanelModel',
        'CMDBuildUI.view.administration.content.custompages.View',
        'CMDBuildUI.view.administration.content.custompages.PermissionsTab'
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

    items: [],

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',

            items: [
                {
                    xtype: 'button',
                    text: CMDBuildUI.locales.Locales.administration.custompages.texts.addcustompage,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.custompages.texts.addcustompage'
                    },
                    ui: 'administration-action-small',
                    reference: 'addCustompage',
                    itemId: 'addBtn',
                    autoEl: {
                        'data-testid': 'administration-custompage-addCustompageBtn'
                    },
                    disabled: true,
                    bind: {
                        disabled: '{!theSession.rolePrivileges.admin_uicomponents_modify}'
                    }
                },
                {
                    xtype: 'admin-globalsearchfield',
                    objectType: 'custompages'
                },
                {
                    xtype: 'tbfill'
                },
                {
                    xtype: 'tbtext',
                    hidden: true,
                    bind: {
                        hidden: '{!theCustompage.description}',
                        html: '{custompageLabel}: <b data-testid="administration-lookuptypes-toolbar-className">{theCustompage.description}</b>'
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
