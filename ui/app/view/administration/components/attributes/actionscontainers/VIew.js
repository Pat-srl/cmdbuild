Ext.define('CMDBuildUI.view.administration.components.attributes.actionscontainers.View', {
    extend: 'CMDBuildUI.components.tab.FormPanel',
    requires: [
        'CMDBuildUI.view.administration.components.attributes.actionscontainers.CardController',
        'CMDBuildUI.view.administration.components.attributes.actionscontainers.CardModel',
        'CMDBuildUI.view.administration.components.attributes.fieldscontainers.GeneralProperties',
        'CMDBuildUI.view.administration.components.attributes.fieldscontainers.TypeProperties'
    ],
    alias: 'widget.administration-components-attributes-actionscontainers-view',
   
    controller: 'administration-components-attributes-actionscontainers-card',
    viewModel: {
        type: 'administration-components-attributes-actionscontainers-card'
    },
    config: {
        objectTypeName: null,
        objectType: null,
        objectId: null,
        shownInPopup: false
    },

    scrollable: true,
    cls: 'administration tab-hidden',
    ui: 'administration-tabandtools',
    items: [{
        xtype: 'container',
        items: [{
            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            items: [{
                xtype: 'administration-components-attributes-fieldscontainers-generalproperties'
            }]
        }, {
            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.attributes.titles.typeproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.attributes.titles.typeproperties'
            },
            items: [{
                xtype: 'administration-components-attributes-fieldscontainers-typeproperties'
            }]
        },  {
            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.attributes.titles.otherproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.attributes.titles.otherproperties'
            },
            items: [{
                xtype: 'administration-components-attributes-fieldscontainers-otherproperties'
            }],
            hidden: true,
            bind: {
                hidden: '{isOtherPropertiesHidden}'
            }
        }]
    }],

    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools({
        edit: true, // #editBtn set true for show the button
        clone:  true, // #cloneBtn set true for show the button
        'delete':  true, // #deleteBtn set true for show the button
        activeToggle:  true, // #enableBtn and #disableBtn set true for show the buttons
        download:  false // #downloadBtn set true for show the buttons
    },
        /* testId */
        'attribute',
    
        /* viewModel object needed only for activeTogle */
        'theAttribute'
    )
});