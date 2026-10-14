Ext.define('CMDBuildUI.view.administration.content.menus.View', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.menus.ViewController',
        'CMDBuildUI.view.administration.content.menus.ViewModel'
    ],

    controller: 'administration-content-menus-view',
    viewModel: {
        type: 'administration-content-menus-view'
    },
    alias: 'widget.administration-content-menu-view',
    defaults: {
        backgrounColor: '#ffffff',
        textAlign: 'left'
    },
    layout: 'border',
    items: [{
        xtype: 'administration-content-menus-topbar',
        region: 'north',
        viewModel: {}
    }, {
        xtype: 'components-administration-toolbars-formtoolbar',

        cls: 'administration-mainview-tabpanel',
        region: 'north',
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: true, // #editBtn set true for show the button
                view: false, // #viewBtn set true for show the button
                clone: false, // #cloneBtn set true for show the button
                'delete': true, // #deleteBtn set true for show the button
                activeToggle: false, // #enableBtn and #disableBtn set true for show the buttons
                download: false // #downloadBtn set true for show the buttons
            },

            /* testId */
            'menu',

            /*viewModel object*/
            'theMenu',

            /*add custom tools[] on the left of the bar*/
            null,

            /* add custom tools[] before #editBtn*/
            null,

            /* add custom tools[] after at the end of the bar*/
            [{
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
                bind: {
                    hidden: '{actions.view}'
                },
                listeners: {
                    afterrender: 'setCopyButton'
                },
                menu: {
                    items: []
                },
                visible: false,
                autoEl: {
                    'data-testid': 'administration-menus-tool-clone'
                }

            }]
        )

    }, {
        xtype: 'administration-content-menus-mainpanel',
        region: 'center',
        hidden: true,
        bind: {
            hidden: '{!theMenu}'
        },
        viewModel: {}
    }],

    listeners: {
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },
    initComponent: function () {
        this.callParent(arguments);
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
    }
});