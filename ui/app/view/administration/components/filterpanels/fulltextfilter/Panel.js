Ext.define('CMDBuildUI.view.administration.components.filterpanels.fulltextfilter.Panel', {
    extend: 'Ext.panel.Panel',
    requires: [
        'CMDBuildUI.view.administration.components.filterpanels.fulltextfilter.PanelController',
        'CMDBuildUI.view.administration.components.filterpanels.fulltextfilter.PanelModel'
    ],

    alias: 'widget.administration-components-filterpanels-fulltextfilter-panel',
    controller: 'administration-components-filterpanels-fulltextfilter-panel',
    viewModel: {
        type: 'administration-components-filterpanels-fulltextfilter-panel'
    },
    items: [{
        xtype: 'panel',
        cls: 'panel-with-gray-background',
        padding: '10 10 10 15',
        layout: 'column',
        items: [{
            xtype: 'textfield',
            columnWidth: 0.5,
            labelAlign: 'top',
            fieldLabel: CMDBuildUI.locales.Locales.administration.searchfilters.texts.writefulltextquery,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.searchfilters.texts.writefulltextquery'
            },
            itemId: 'filterQueryInput',
            bind: {
                value: '{_query}'
            }
        }],
        bind: {
            hidden: '{actions.view}'
        }
    }, {
        xtype: 'panel',

        padding: '10 10 10 15',
        items: [{
            xtype: 'displayfield',
            labelAlign: 'top',
            fieldLabel: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fulltextquery,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.searchfilters.texts.fulltextquery'
            },
            itemId: 'filterQueryInput',
            bind: {
                value: '{_query}'
            }
        }],
        bind: {
            hidden: '{!actions.view}'
        }
    }],

    getQueryData: function () {
        var query = this.getViewModel().get('_query');
        return Ext.String.format(query);
    }

});