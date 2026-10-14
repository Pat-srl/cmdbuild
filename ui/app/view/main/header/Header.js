Ext.define('CMDBuildUI.view.main.header.Header', {
    extend: 'Ext.toolbar.Toolbar',

    requires: ['CMDBuildUI.view.main.header.HeaderController', 'CMDBuildUI.view.main.header.HeaderModel'],

    xtype: 'main-header-header',
    controller: 'main-header-header',
    viewModel: {
        type: 'main-header-header'
    },

    ui: 'main-header',
    padding: '10px 15px',
    border: '0 0 1 0',

    // add data-testid attribute to element
    autoEl: {
        'data-testid': 'main-header-header'
    },

    bind: {
        hidden: '{!isAuthenticated}'
    },

    items: [
        {
            xtype: 'main-header-logowhite',
            height: 30,
            cls: 'logo',
            itemId: 'cmdbuildLogo',
            autoEl: {
                'data-testid': 'header-cmdbuildlogo'
            }
        },
        {
            xtype: 'tbspacer'
        },
        {
            xtype: 'container',
            reference: 'companylogocontainer',
            itemId: 'companylogocontainer',
            bind: {
                hidden: '{companylogoinfo.hidden}'
            }
        },
        {
            xtype: 'tbspacer',
            hidden: true,
            bind: {
                hidden: '{companylogoinfo.hidden}'
            }
        },
        {
            xtype: 'tbtext',
            itemId: 'instanceName',
            cls: 'instancename',
            ui: 'main-header',
            autoEl: {
                'data-testid': 'header-instancename'
            },
            bind: {
                text: '{instancename}'
            }
        },
        {
            xtype: 'tbfill'
        },
        {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('exclamation-triangle', 'solid'),
            ui: 'header',
            tooltipLabel: 'CMDBuildUI.locales.Locales.main.alerts',
            itemId: 'alertBtn',
            cls: 'alertCls',
            autoEl: {
                'data-testid': 'header-alertbtn'
            },
            hidden: true,
            bind: {
                hidden: '{!isAdministrationModule || !isNecessaryReload}'
            }
        },
        {
            iconCls: 'cmdbuildicon-sparkles',
            ui: 'header',
            tooltipLabel: 'CMDBuildUI.locales.Locales.ai.headertooltip',
            itemId: 'aibtn',
            hidden: true,
            bind: {
                hidden: '{!chatBotAiEnabled}',
                text: '{chatBotAiName}'
            },
            autoEl: {
                'data-testid': 'header-ai'
            }
        },
        {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('info-circle', 'solid'),
            ui: 'header',
            tooltipLabel: 'CMDBuildUI.locales.Locales.main.info',
            itemId: 'infobtn',
            autoEl: {
                'data-testid': 'header-infobtn'
            }
        },
        {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('calendar-alt', 'solid'),
            ui: 'header',
            tooltipLabel: 'CMDBuildUI.locales.Locales.calendar.scheduler',
            itemId: 'schedulerbtn',
            hidden: true,
            autoEl: {
                'data-testid': 'header-schedulerbtn'
            },
            bind: {
                hidden: '{calendarbtnhidden || isAdministrationModule}'
            }
        },
        {
            xtype: 'main-header-usermenu',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('users', 'solid'),
            hidden: true,
            autoEl: {
                'data-testid': 'header-usermenu'
            },
            bind: {
                hidden: '{!isAuthenticated}'
            }
        },
        {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('bell', 'regular'),
            ui: 'header',
            tooltipLabel: 'CMDBuildUI.locales.Locales.notifications.label',
            itemId: 'notificationsBtn',
            hidden: true,
            autoEl: {
                'data-testid': 'header-notifications'
            },
            bind: {
                hidden: '{!isAuthenticated}',
                userCls: '{notifications.hasUnread ? "unreadNotificationsMarker" : ""}'
            }
        },
        {
            ui: 'header',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('cog', 'solid'),
            href: '#administration/home',
            tooltipLabel: 'CMDBuildUI.locales.Locales.main.administrationmodule',
            reference: 'administrationbtn',
            itemId: 'administrationbtn',
            hidden: true,
            autoEl: {
                'data-testid': 'header-administration'
            },
            bind: {
                hidden: '{!isAdministrator}'
            }
        },
        {
            ui: 'header',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
            href: '#management',
            tooltipLabel: 'CMDBuildUI.locales.Locales.main.managementmodule',
            reference: 'managementbtn',
            itemId: 'managementbtn',
            hidden: true,
            autoEl: {
                'data-testid': 'header-management'
            },
            bind: {
                hidden: '{!isAdministrationModule}'
            }
        },
        {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sign-out-alt', 'solid'),
            ui: 'header',
            tooltipLabel: 'CMDBuildUI.locales.Locales.main.logout',
            reference: 'logoutbtn',
            itemId: 'logoutbtn',
            hidden: true,
            autoEl: {
                'data-testid': 'header-logout'
            },
            bind: {
                hidden: '{!isAuthenticated}'
            }
        }
    ]
});
