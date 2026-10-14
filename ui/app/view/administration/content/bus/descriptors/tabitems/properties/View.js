Ext.define('CMDBuildUI.view.administration.content.bus.descriptors.tabitems.properties.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.bus.descriptors.tabitems.properties.ViewController'
    ],

    alias: 'widget.administration-content-bus-descriptors-tabitems-properties-view',
    controller: 'administration-content-bus-descriptors-tabitems-properties-view',
    autoScroll: false,
    modelValidation: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    layout: 'border',
    items: [{
        xtype: 'panel',
        region: 'center',
        scrollable: 'y',
        items: [{
            xtype: 'administration-content-bus-descriptors-tabitems-properties-fieldsets-generaldatafieldset'
        }, {
            xtype: 'administration-content-bus-descriptors-tabitems-properties-fieldsets-paramsfieldset'
        }, {
            xtype: 'administration-content-bus-descriptors-tabitems-properties-fieldsets-configurationfieldset'
        }]
    }],

    dockedItems: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'top',
        borderBottom: 0,
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: {
                    bind: {
                        disabled: '{!toolAction._canUpdate}'
                    }
                },
                activeToggle: {
                    activeField: 'enabled',
                    bind: {
                        disabled: '{!toolAction._canUpdate}'
                    }
                },
                delete: {
                    bind: {
                        disabled: '{!toolAction._canUpdate}'
                    }
                },
                download: {
                    bind: {
                        disabled: '{!toolAction._canDownload}'
                    }
                }
            },

            /* testId */
            'busdescriptor',

            /* viewModel object needed only for activeTogle */
            'theDescriptor',

            /* add custom tools[] on the left of the bar */
            [],

            /* add custom tools[] before #editBtn*/
            [],

            /* add custom tools[] after at the end of the bar*/
            []
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
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveAndEditCancelButtons()
    }]

});