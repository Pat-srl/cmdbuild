Ext.define('CMDBuildUI.view.administration.components.viewfilters.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.administration.components.viewfilters.card.ViewInRowController'
    ],
    alias: 'widget.administration-components-viewfilters-card-viewinrow',
    controller: 'administration-components-viewfilters-card-viewinrow',
    viewModel: {
        type: 'administration-components-viewfilters-card-form'
    },
    config: {
        theViewFilter: null,
        subtype: null,
        objectTypeName: null,
        objectType: null
    },


    cls: 'administration',
    ui: 'administration-tabandtools',
    items: [{
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        xtype: "fieldset",
        ui: 'administration-formpagination',

        items: [{
            xtype: 'administration-components-viewfilters-card-fieldscontainers-generalproperties',
            bind: {
                actions: '{actions}'
            }
        }]
    }],
    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools({
        edit: true,
        view: true,
        delete: true
    }, 'viewfilter', 'theViewFilter')
});