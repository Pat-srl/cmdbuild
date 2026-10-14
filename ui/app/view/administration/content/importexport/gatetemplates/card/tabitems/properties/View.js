Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.properties.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.properties.ViewController',
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.properties.ViewModel',
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.properties.FormHelper'
    ],

    alias: 'widget.administration-content-importexport-gatetemplates-card-tabitems-properties-view',

    controller: 'administration-content-importexport-gatetemplates-card-tabitems-properties-view',
    viewModel: {
        type: 'administration-content-importexport-gatetemplates-card-tabitems-properties-view'
    },
    bubbleEvents: [
        'itemupdated',
        'cancelupdating'
    ],
    config: {
        theGate: null
    },

    bind: {
        theGate: '{theGate}',
        userCls: '{formModeCls}'
    },

    hidden: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    scrollable: true,
    ui: 'administration-formpagination',
    items: [
        CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.properties.FormHelper.getGeneralPropertiesFieldset()
    ],

    dockedItems: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        dock: 'top',
        hidden: true,
        bind: {
            hidden: '{!actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: true, // #editBtn set true for show the button
                view: false, // #viewBtn set true for show the button
                'delete': true, // #deleteBtn set true for show the button
                activeToggle: {
                    activeField: 'enabled'
                } // {} // #enableBtn and #disableBtn set true for show the buttons       
            },

            /* testId */
            'importexportgates',

            /* viewModel object needed only for activeTogle */
            'theGate',

            /* add custom tools[] on the left of the bar */
            [],

            /* add custom tools[] before #editBtn*/
            [],

            /* add custom tools[] after at the end of the bar*/
            []
        )
    }, {
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true, {}, {
            listeners: {
                mouseover: function () {
                    var invalidFields = CMDBuildUI.util.administration.helper.FormHelper.getInvalidFields(this.up('form').form);
                    Ext.Array.forEach(invalidFields, function (field) {
                        CMDBuildUI.util.Logger.log(Ext.String.format('{0} is invalid', field.itemId), CMDBuildUI.util.Logger.levels.debug);
                    });
                }
            }
        })
    }],
    initComponent: function () {

        Ext.asap(function () {
            try {
                this.up().mask(CMDBuildUI.locales.Locales.administration.common.messages.loading);
            } catch (error) {

            }
        }, this);
        this.callParent(arguments);
    }

});