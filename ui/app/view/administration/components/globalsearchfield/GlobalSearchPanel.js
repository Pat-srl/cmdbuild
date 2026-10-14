Ext.define('CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchPanel', {
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchPanelController',
        'CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchPanelModel'
    ],

    alias: 'widget.admin-globalsearchpanel',
    controller: 'administration-components-globalsearchfield-globalsearchpanel',
    viewModel: {
        type: 'administration-components-globalsearchfield-globalsearchpanel'
    },

    viewConfig: {
        getRowClass: function (record) {
            return record.get('depth') > 1 ? 'small-text' : '';
        }
    },

    floating: true,
    width: 300,
    maxHeight: '400',
    itemId: 'resulttreepanel',
    cls: 'treepanel-noexpander',

    rootVisible: false,
    emptyText: CMDBuildUI.locales.Locales.administration.globalsearch.emptyText.noresults,
    localized: {
        emptyText: 'CMDBuildUI.locales.Locales.administration.globalsearch.emptyText.noresults'
    },

    config: {
        objectType: null,
        subType: null
    },

    bind: {
        store: '{searchPanelStore}'
    },

    bbar: [
        '->',
        {
            xtype: 'button',
            ui: 'administration-secondary-action-small',
            text: CMDBuildUI.locales.Locales.administration.common.actions.close,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.close'
            },
            handler: function (button) {
                this.up('#resulttreepanel').destroy();
            }
        }
    ]
});
