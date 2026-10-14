Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.card.Card', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-content-importexport-datatemplates-card',

    requires: [
        'CMDBuildUI.view.administration.content.importexport.datatemplates.card.CardController',
        'CMDBuildUI.view.administration.content.importexport.datatemplates.card.CardModel',
        'CMDBuildUI.util.helper.FormHelper'
    ],

    controller: 'view-administration-content-importexport-datatemplates-card',
    viewModel: {
        type: 'view-administration-content-importexport-datatemplates-card'
    },

    bubbleEvents: [
        'itemupdated',
        'cancelupdating'
    ],
    modelValidation: true,

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    scrollable: true,
    ui: 'administration-formpagination',
    items: [],
    initComponent: function () {
        var me = this;

        // Mostra il loader
        Ext.asap(function () {
            try {
                me.up().mask(CMDBuildUI.locales.Locales.administration.common.messages.loading);
            } catch (error) {
                console.warn('Unable to show load mask:', error);
            }
        });

        // Aggiungi gli items al componente
        me.items = [
            me.getGeneralPropertiesFieldset(),
            me.getAttributesFieldset(),
            me.getImportFilterFieldset(),
            me.getImportCriteriaFieldset(),
            me.getExportCriteriaFieldset(),
            me.getErrorsManagementFieldset()
        ];

        me.callParent(arguments);

        // Rimuovi il loader dopo che il componente è stato renderizzato
        me.on('afterrender', function () {
            Ext.asap(function () {
                me.up().unmask();
            });
        });
    },

    getGeneralPropertiesFieldset: function () {
        return CMDBuildUI.view.administration.content.importexport.datatemplates.card.helpers.FieldsetsHelper.getGeneralPropertiesFieldset();
    },

    getAttributesFieldset: function () {
        return CMDBuildUI.view.administration.content.importexport.datatemplates.card.helpers.FieldsetsHelper.getAttributesFieldset();
    },
    getImportFilterFieldset: function () {
        return CMDBuildUI.view.administration.content.importexport.datatemplates.card.helpers.FieldsetsHelper.getImportFilterFieldset();
    },
    getImportCriteriaFieldset: function () {
        return CMDBuildUI.view.administration.content.importexport.datatemplates.card.helpers.FieldsetsHelper.getImportCriteriaFieldset();
    },

    getExportCriteriaFieldset: function () {
        return CMDBuildUI.view.administration.content.importexport.datatemplates.card.helpers.FieldsetsHelper.getExportCriteriaFieldset();
    },
    getErrorsManagementFieldset: function () {
        return CMDBuildUI.view.administration.content.importexport.datatemplates.card.helpers.FieldsetsHelper.getErrorsManagementFieldset();
    }
});