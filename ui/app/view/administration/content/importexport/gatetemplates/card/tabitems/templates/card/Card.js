Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.Card', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-content-importexport-gatetemplates-tabitems-templates-card-card',

    requires: [
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.CardController',
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.CardModel',
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.helpers.FieldsetsHelper',
        'CMDBuildUI.util.helper.FormHelper'
    ],
    controller: 'view-administration-content-importexport-gatetemplates-tabitems-templates-card',
    viewModel: {
        type: 'view-administration-content-importexport-gatetemplates-tabitems-templates-card'
    },
    bubbleEvents: [
        'itemupdated',
        'cancelupdating'
    ],
    modelValidation: true,
    config: {
        theGate: null,
        theGateTemplate: null
    },

    bind: {
        theGateTemplate: '{theGateTemplate}'
    },
    hidden: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    scrollable: true,
    ui: 'administration-formpagination',
    items: [
        CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.helpers.FieldsetsHelper.getGeneralPropertiesFieldset(),
        CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.helpers.FieldsetsHelper.getAttributesFieldset(),
        CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.helpers.FieldsetsHelper.getImportCriteriaFieldset()
        // TODO: move notification on GATE when server is done #3672
        // CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.helpers.FieldsetsHelper.getErrorsManagementFieldset()
    ],

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