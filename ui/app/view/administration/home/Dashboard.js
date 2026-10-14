Ext.define('CMDBuildUI.view.administration.home.Dashboard', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.home.DashboardController',
        'CMDBuildUI.view.administration.home.DashboardModel'
    ],

    alias: 'widget.administration-home-dashboard',
    controller: 'administration-home-dashboard',
    viewModel: {
        type: 'administration-home-dashboard'
    },

    bodyPadding: 10,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    scrollable: true,
    items: [
        {
            xtype: 'container',
            itemId: 'expirationWarningBanner',
            ui: 'messagewarning',
            hidden: true,
            bind: {
                hidden: '{isWarningHidden}'
            },
            items: [
                {
                    ui: 'custom',
                    xtype: 'panel',
                    bind: {
                        html: '{warningHtml}'
                    }
                }
            ]
        },
        {
            xtype: 'administration-home-widgets-systemstatus-systemstatuses'
        },
        {
            xtype: 'administration-home-widgets-userstats-stats'
        },
        {
            xtype: 'container',
            layout: 'hbox',
            items: [
                {
                    flex: 1,
                    xtype: 'administration-home-widgets-modelsstats-modelsstats'
                },
                {
                    flex: 1,
                    xtype: 'administration-home-widgets-activerecords-activerecords',
                    margin: 'auto auto auto 10'
                }
            ]
        },
        {
            xtype: 'administration-home-widgets-pluginstatus-pluginstatus',
            margin: 'auto auto 30 auto'
        },
        {
            xtype: 'container',
            layout: 'hbox',
            items: [
                {
                    flex: 1,
                    xtype: 'administration-home-widgets-spaceusage-tablesgrid'
                },
                {
                    xtype: 'administration-home-widgets-taskbytype-taskbytype',
                    margin: 'auto auto auto 10',
                    flex: 1
                }
            ]
        }
    ],

    initComponent: function () {
        var me = this;
        var vm = me.getViewModel();
        vm.getParent().set('title', Ext.String.format('{0} {1}', CMDBuildUI.locales.Locales.administration.home.home));
        me.callParent(arguments);
    }
});
