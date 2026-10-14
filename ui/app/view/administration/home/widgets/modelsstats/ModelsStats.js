Ext.define('CMDBuildUI.view.administration.home.widgets.modelsstats.ModelsStats', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.home.widgets.modelsstats.ModelsStatsController',
        'CMDBuildUI.view.administration.home.widgets.modelsstats.ModelsStatsModel'
    ],

    alias: 'widget.administration-home-widgets-modelsstats-modelsstats',
    controller: 'administration-home-widgets-modelsstats-modelsstats',
    viewModel: {
        type: 'administration-home-widgets-modelsstats-modelsstats'
    },

    title: CMDBuildUI.locales.Locales.administration.home.modelstats,
    localized: {
        title: 'CMDBuildUI.locales.Locales.administration.home.modelstats'
    },

    ui: 'admindashboard',
    items: [],

    tools: [
        {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
            itemId: 'addModelTool',
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.add,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.add'
            },
            hidden: true,
            bind: {
                hidden: '{!theSession.rolePrivileges.admin_all}'
            }
        }
    ],

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            padding: '0 0 40 0',
            items: [
                {
                    xtype: 'tbfill'
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.home.printschema,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.home.printschema'
                    },
                    handler: function (button) {
                        const url = Ext.String.format(
                            '{0}/administration/classes/print_schema/schema.pdf?extension=pdf',
                            CMDBuildUI.util.Config.baseUrl
                        );
                        CMDBuildUI.util.File.download(url, 'pdf');
                    }
                }
            ]
        }
    ]
});
