Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.ViewInRowController',
        'CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.CardModel',
        'Ext.layout.*'
    ],
    autoDestroy: true,
    alias: 'widget.administration-content-dms-dmscategorytypes-tabitems-values-card-viewinrow',
    controller: 'administration-content-dms-dmscategorytypes-tabitems-values-card-viewinrow',
    viewModel: {
        type: 'administration-content-dms-dmscategorytypes-tabitems-values-card'
    },
    cls: 'administration',
    userCls: 'formmode-view',
    ui: 'administration-tabandtools',
    scrollable: 'y',
    config: {
        objectTypeName: null,
        objectId: null,
        shownInPopup: false,
        theValue: null
    },
    minHeight: 200,

    items: [{
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        xtype: "fieldset",
        ui: 'administration-formpagination',
        fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
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
        fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
        items: CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.FieldsHelper.getAttachmentsProperties()

    }, {
        xtype: "fieldset",
        ui: 'administration-formpagination',
        title: CMDBuildUI.locales.Locales.administration.common.labels.icon,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
        },
        fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
        items: CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.FieldsHelper.getIconFields()
    }],
    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools({
        'edit': true,
        'view': true,
        'delete': true,
        'activeToggle': true
    }, 'lookupvalue', 'theValue')

});