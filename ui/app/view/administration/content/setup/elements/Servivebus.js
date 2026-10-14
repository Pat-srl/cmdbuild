Ext.define('CMDBuildUI.view.administration.content.setup.elements.Servicebus', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.GisController'
    ],

    alias: 'widget.administration-content-setup-elements-servicebus',
    
    controller: 'administration-content-setup-elements-servicebus',
    
    items: [{
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.generals,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.generals'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    /********************* org.cmdbuild.waterway.jobs.enabled **********************/
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.enablebusjobservice,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.enablebusjobservice'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__waterway__DOT__jobs__DOT__enabled}',
                        readOnly: '{actions.view}'
                    }
                }]
            }]
        }]
    }]
});