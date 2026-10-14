Ext.define('CMDBuildUI.view.administration.content.gis.Topbar', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.gis.TopbarController'
    ],

    alias: 'widget.administration-content-gis-topbar',
    controller: 'administration-content-gis-topbar',
    viewModel: {},

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',

        items: [{
                xtype: 'button',
                text: CMDBuildUI.locales.Locales.administration.gis.addicon,
                ui: 'administration-action-small',
                reference: 'addicon',
                itemId: 'addicon',
                autoEl: {
                    'data-testid': 'administration-template-toolbar-addIconBtn'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.gis.addicon'
                }
            },
            {
                xtype: 'textfield',
                name: 'search',
                width: 250,
                enableKeyEvents: true,
                emptyText: CMDBuildUI.locales.Locales.administration.attributes.emptytexts.search,
                localized: {
                    emptyText: 'CMDBuildUI.locales.Locales.administration.attributes.emptytexts.search'
                },
                reference: 'templatessearchtext',
                itemId: 'templatessearchtext',
                cls: 'administration-input',
                bind: {
                    value: '{search.value}',
                    hidden: '{!canFilter}'
                },
                listeners: {
                    keyup: 'onKeyUp'
                },
                triggers: {
                    clear: {
                        cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                        handler: 'onSearchClear'
                    }
                }
            }
        ]
    }]
});