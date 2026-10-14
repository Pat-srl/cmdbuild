Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: ['CMDBuildUI.view.administration.components.geoattributes.card.ViewInRowController'],

    alias: 'widget.administration-components-geoattributes-card-viewinrow',
    controller: 'administration-components-geoattributes-card-viewinrow',
    viewModel: {
        type: 'administration-components-geoattributes-card-form'
    },

    cls: 'administration',
    ui: 'administration-tabandtools',

    items: [
        {
            xtype: 'fieldset',
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            ui: 'administration-formpagination',
            items: [
                {
                    xtype: 'administration-components-geoattributes-card-fieldscontainers-generalproperties'
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: CMDBuildUI.locales.Locales.administration.geoattributes.strings.specificproperty,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.geoattributes.strings.specificproperty'
            },
            ui: 'administration-formpagination',
            hidden: true,
            itemId: 'typepropertiestab',
            items: [
                {
                    xtype: 'administration-components-geoattributes-card-fieldscontainers-typeproperties'
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: CMDBuildUI.locales.Locales.administration.geoattributes.strings.infowindow,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.geoattributes.strings.infowindow'
            },
            hidden: true,
            itemId: 'infowindowtab',
            ui: 'administration-formpagination',
            items: [
                {
                    xtype: 'administration-components-geoattributes-card-fieldscontainers-infowindow'
                }
            ]
        }
    ],

    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools(
        {
            edit: true,
            view: true,
            delete: true,
            clone: true,
            activeToggle: true
        },
        'geoattribute',
        'theGeoAttribute'
    )
});
