Ext.define('CMDBuildUI.view.administration.content.schedules.settings.tabs.generalproperties.Form', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.schedules.settings.tabs.generalproperties.FormController',
        'CMDBuildUI.view.administration.content.schedules.settings.tabs.generalproperties.FormModel'
    ],
    alias: 'widget.administration-content-schedules-settings-tabs-generalproperties-form',
    controller: 'administration-content-schedules-settings-tabs-generalproperties-form',
    viewModel: {
        type: 'administration-content-schedules-settings-tabs-generalproperties-form'
    },
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [{
        xtype: 'fieldset',
        ui: 'administration-formpagination',
        style: 'border-top:0!important',
        layout: 'column',
        items: [{
            columnWidth: 0.5,
            items: [{
                /********************* org.cmdbuild.scheduler.enabled **********************/
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                },
                name: 'isEnabled',
                bind: {
                    value: '{enabled}',
                    readOnly: '{actions.view}'
                }
            }]
        }]
    }],

    dockedItems: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'top',
        borderBottom: 0,
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: true // #editBtn set true for show the button           
            },

            /* testId */
            'schedules-settings',

            /* viewModel object needed only for activeTogle */
            'theSetup'
        ),
        bind: {
            hidden: '{formtoolbarHidden}'
        }
    }, {
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
    }]

});