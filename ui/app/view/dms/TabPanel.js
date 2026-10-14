
Ext.define('CMDBuildUI.view.dms.TabPanel', {
    extend: 'CMDBuildUI.components.tab.FormPanel',
    alias: 'widget.dms-tabpanel',

    requires: [
        'CMDBuildUI.view.dms.TabPanelController',
        'CMDBuildUI.view.dms.TabPanelModel'
    ],

    mixins: [
        'CMDBuildUI.mixins.DetailsTabPanel'
    ],

    controller: 'dms-tabpanel',
    viewModel: {
        type: 'dms-tabpanel'
    },

    items: [{
        xtype: 'panel',
        layout: 'fit',
        title: CMDBuildUI.locales.Locales.common.tabs.attachment,
        localized: {
            title: 'CMDBuildUI.locales.Locales.common.tabs.attachment'
        },
        items: [{
            xtype: 'dms-attachment-view',
            itemId: 'dms-attachment-view'
        }]
    }, {
        xtype: 'panel',
        layout: 'fit',
        items: [{
            xtype: 'dms-history-grid'
        }],
        bind: {
            disabled: '{isRecordPhantom}'
        },
        title: CMDBuildUI.locales.Locales.common.tabs.history,
        localized: {
            title: 'CMDBuildUI.locales.Locales.common.tabs.history'
        }
    }, {
        xtype: 'events-grid',
        tabConfig: {
            title: CMDBuildUI.locales.Locales.common.tabs.schedules,
            localized: {
                title: 'CMDBuildUI.locales.Locales.common.tabs.schedules'
            },
            hidden: true,
            bind: {
                hidden: '{schedulesHidden}' //NOTE: the binding '{!dms-tabpanel.DMSClass._hasTriggers}' doesn't work, need to pass trougth dms-tabpanel viewModel
            }
        },
        height: '0',
        bind: {
            height: '{schedulesHeight}',
            store: '{eventsStore}'
        }
    }],

    ui: 'management',
    border: false,
    tabPosition: 'left',
    tabRotation: 0,
    header: false,

    defaults: {
        textAlign: 'left',
        bodyPadding: 10,
        scrollable: true,
        border: false
    },

    layout: 'fit',

    tabtools: Ext.Array.merge(
        CMDBuildUI.view.dms.Util.getTools(),
        CMDBuildUI.view.dms.Util.getHelpTool())

});
