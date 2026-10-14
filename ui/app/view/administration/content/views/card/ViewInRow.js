Ext.define('CMDBuildUI.view.administration.content.views.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.administration.content.views.card.ViewInRowController'
    ],
    alias: 'widget.administration-content-views-card-viewinrow',
    controller: 'administration-content-views-card-viewinrow',
    viewModel: {
        type: 'administration-content-views-card-form'
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
            xtype: 'administration-content-views-card-fieldscontainers-generalproperties',
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