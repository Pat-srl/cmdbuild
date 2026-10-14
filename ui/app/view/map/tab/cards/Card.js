Ext.define('CMDBuildUI.view.map.tab.cards.Card', {
    extend: 'Ext.panel.Panel',

    requires: ['CMDBuildUI.view.map.tab.cards.CardController'],

    alias: 'widget.map-tab-cards-card',

    controller: 'map-tab-cards-card',

    autoScroll: true,
    items: [
        {
            title: CMDBuildUI.locales.Locales.gis.geographicalAttributes,
            xtype: 'fieldset',
            ui: 'formpagination',
            localized: {
                title: 'CMDBuildUI.locales.Locales.gis.geographicalAttributes'
            },
            items: [
                {
                    xtype: 'map-tab-cards-geoattributesgrid'
                }
            ]
        }
    ]
});
