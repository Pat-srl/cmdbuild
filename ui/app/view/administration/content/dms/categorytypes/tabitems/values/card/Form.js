Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.Card', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-content-dms-dmscategorytypes-tabitems-values-card',

    requires: [
        'CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.CardController',
        'CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.CardModel',
        'CMDBuildUI.util.administration.helper.FieldsHelper',
        'CMDBuildUI.util.helper.FormHelper'
    ],
    controller: 'administration-content-dms-dmscategorytypes-tabitems-values-card',
    viewModel: {
        type: 'administration-content-dms-dmscategorytypes-tabitems-values-card'
    },
    bubbleEvents: [
        'itemupdated',
        'cancelupdating'
    ],
    // modelValidation: true,
    config: {
        theValue: null
    },
    hidden: true,
    bind: {
        hidden: '{!theValue}',
        userCls: '{formModeCls}' // this is used for hide label localzation icon in `view` mode
    },
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    scrollable: true,
    ui: 'administration-formpagination',
    items: [{
        xtype: 'container',
        hidden: true,
        padding: '0 10 0 10',
        bind: {
            hidden:'{!errorMessage}'
        },
        items: [{
            xtype: 'formvalidatorfield',
            itemId: 'validationField',
            bind: {
                errorMessage: '{errorMessage}'
            }
        }]
    }, {
        ui: 'administration-formpagination',
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        collapsible: true,
        xtype: "fieldset",
        items: CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.FieldsHelper.getGeneralFields()
    }, {
        xtype: 'fieldset',
        collapsible: true,
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.dmsmodels.modelattachments,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.dmsmodels.modelattachments'
        },
        ui: 'administration-formpagination',
        items: CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.FieldsHelper.getAttachmentsProperties()

    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.common.labels.icon,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
        },
        items: CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.FieldsHelper.getIconFields()
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
                hidden: '{_is_system || actions.edit}'
            }
        })
    }],
    initComponent: function () {
        this.callParent(arguments);
        try {
            this.up().mask(CMDBuildUI.locales.Locales.administration.common.messages.loading);
        } catch (error) {

        }

    },
    listeners: {

        afterlayout: function (panel) {
            try {
                panel.up().unmask();
            } catch (error) {}
        }
    }


});