Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.Card', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-content-lookuptypes-tabitems-values-card',

    requires: [
        'CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.CardController',
        'CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.CardModel',
        'CMDBuildUI.util.administration.helper.FieldsHelper',
        'CMDBuildUI.util.helper.FormHelper'
    ],

    controller: 'administration-content-lookuptypes-tabitems-values-card',
    viewModel: {
        type: 'administration-content-lookuptypes-tabitems-values-card'
    },

    bubbleEvents: [
        'itemupdated',
        'cancelupdating'
    ],

    hidden: true,
    bind: {
        hidden: '{!theValue}',
        userCls: '{formModeCls}' // this is used for hide label localization icon in `view` mode
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    scrollable: true,
    ui: 'administration-formpagination',
    items: [{
        ui: 'administration-formpagination',
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        collapsible: true,
        xtype: "fieldset",
        items: CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.FieldsHelper.getGeneralFields()
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.common.labels.icon,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
        },
        items: CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.FieldsHelper.getIconFields()
    }],

    dockedItems: [{
        dock: 'top',
        xtype: 'container',
        hidden: true,
        bind: {
            hidden: '{!actions.view}'
        },
        items: [{
            xtype: 'components-administration-toolbars-formtoolbar',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                'edit': true,
                'delete': true,
                'activeToggle': true
            }, 'lookupvalue', 'theValue')
        }]
    }, {
        xtype: 'toolbar',
        itemId: 'bottomtoolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveAndAddCancelButtons(true, {}, {
            bind: {
                hidden: '{!toolAction._canAdd || actions.edit}'
            }
        })
    }]
});