Ext.define('CMDBuildUI.view.administration.content.tasks.jobruns.viewinrow.viewinrow.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',
    alias: 'widget.administration-content-tasks-jobruns-viewinrow-viewinrow',
    requires: [

    ],
    controller: 'administration-content-tasks-jobruns-viewinrow-viewinrow',
    viewModel: {
        type: 'administration-content-tasks-jobruns-viewinrow-viewinrow'
    },
    config: {

    },

    //hidden: true,

    cls: 'administration',
    ui: 'administration-tabandtools',
    items: [{
            title: CMDBuildUI.locales.Locales.administration.jobruns.generaloproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.jobruns.generaloproperties'
            },
            xtype: "fieldset",
            ui: 'administration-formpagination',
            autoEl: {
                "data-testid": "administration-components-attributes-fieldscontainers-generalproperties"
            },
            items: [{
                xtype: 'administration-content-tasks-jobruns-viewinrow-tabitems-generalproperties'
            }]
        }, {
            xtype: "fieldset",
            ui: 'administration-formpagination',
            title: CMDBuildUI.locales.Locales.administration.jobruns.meta,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.jobruns.meta'
            },
            autoEl: {
                "data-testid": "administration-components-attributes-fieldscontainers-typeproperties"
            },
            items: [{
                xtype: 'administration-content-tasks-jobruns-viewinrow-tabitems-meta'
            }]
        }, {
            xtype: "fieldset",
            ui: 'administration-formpagination',

            title: CMDBuildUI.locales.Locales.administration.jobruns.errors,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.jobruns.errors'
            },
            autoEl: {
                "data-testid": "administration-components-attributes-fieldscontainers-otherproperties"
            },
            items: [{
                xtype: 'administration-content-tasks-jobruns-viewinrow-tabitems-errors'
            }]
        } , {
            xtype: "fieldset",
            ui: 'administration-formpagination',
            title: CMDBuildUI.locales.Locales.administration.jobruns.logs,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.jobruns.logs'
            },
            autoEl: {
                "data-testid": "administration-components-attributes-fieldscontainers-otherproperties"
            },
            items: [{
                xtype: 'administration-content-tasks-jobruns-viewinrow-tabitems-logs'
            }]
        }
    ]
});