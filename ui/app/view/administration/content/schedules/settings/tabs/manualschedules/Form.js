Ext.define('CMDBuildUI.view.administration.content.schedules.settings.tabs.manualschedules.Form', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.schedules.settings.tabs.manualschedules.FormController',
        'CMDBuildUI.view.administration.content.schedules.settings.tabs.manualschedules.FormModel'
    ],
    alias: 'widget.administration-content-schedules-settings-tabs-manualschedules-form',
    controller: 'administration-content-schedules-settings-tabs-manualschedules-form',
    viewModel: {
        type: 'administration-content-schedules-settings-tabs-manualschedules-form'
    },
    layout: {
        type: 'fit'
    },
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [{
        xtype: 'fieldset',
        layout: {
            type: 'fit'
        },
        padding: 0,
        ui: 'administration-formpagination',
        title: CMDBuildUI.locales.Locales.administration.schedules.selectableclasses,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.schedules.selectableclasses'
        },
        items: [{
            xtype: 'grid',
            itemId: 'classesgrid',
            scrollable: 'y',
            width: '100%',

            layout: 'fit',
            bind: {
                store: '{classes}'
            },

            selModel: {
                selType: 'checkboxmodel',
                checkOnly: true,
                mode: 'MULTI'
            },
            columns: [{
                flex: 9,
                text: CMDBuildUI.locales.Locales.administration.home['class'],
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.home.class'
                },
                dataIndex: 'description'
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