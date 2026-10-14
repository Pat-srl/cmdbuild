Ext.define('CMDBuildUI.view.administration.content.dashboards.common.tabpanel.TabPanelBase', {
    extend: 'Ext.tab.Panel',

    controller: 'administration-content-dashboards-common-tabpanel',
    viewModel: {
        type: 'administration-content-dashboards-common-tabpanel'
    },

    config: {
        domainKey: null,
        addButtonText: null,
        addButtonLocaleKey: null,
        addButtonTestId: null,
        extraToolbarItems: null,
        useActiveTabFallback: false
    },

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

    listeners: {
        itemupdated: 'onItemUpdated',
        cancelcreation: 'onCancelCreation',
        cancelupdating: 'onCancelUpdating'
    },

    initComponent: function () {
        var addButtonItem = {
            xtype: 'button',
            text: this.getAddButtonText(),
            localized: {
                text: this.getAddButtonLocaleKey()
            },
            ui: 'administration-action-small',
            reference: 'addEntityBtn',
            itemId: 'addEntityBtn',
            autoEl: {
                'data-testid': this.getAddButtonTestId()
            },
            bind: {
                disabled: '{!toolAction._canAdd}'
            }
        };

        this.dockedItems = [
            {
                xtype: 'toolbar',
                dock: 'top',
                items: [addButtonItem].concat(this.getExtraToolbarItems() || [])
            },
            {
                xtype: 'components-administration-toolbars-formtoolbar',

                cls: 'administration-mainview-tabpanel',
                region: 'north',
                items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                    /* testId */
                    'dashboards',

                    /*viewModel object*/
                    'theDashboard',

                    /*add custom tools[] on the left of the bar*/
                    null,

                    /* add custom tools[] before #editBtn*/
                    null,

                    /* add custom tools[] after at the end of the bar*/
                    [
                        {
                            xtype: 'button',
                            align: 'right',
                            itemId: 'copyFrom',
                            reference: 'copyfrom',
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('clone', 'regular'),
                            cls: 'administration-tool',

                            text: CMDBuildUI.locales.Locales.administration.common.actions.clonefrom,
                            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.clonefrom,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.common.actions.clonefrom',
                                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.clonefrom'
                            },
                            hidden: true,
                            bind: {
                                hidden: '{!actions.add}'
                            },
                            listeners: {
                                afterrender: 'setCopyButton'
                            },
                            menu: {
                                items: []
                            },
                            visible: false,
                            autoEl: {
                                'data-testid': 'administration-dashboards-tool-clone'
                            }
                        }
                    ]
                )
            }
        ];

        this.callParent(arguments);
    }
});
