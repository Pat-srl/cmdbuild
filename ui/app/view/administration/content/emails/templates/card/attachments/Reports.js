Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.attachments.Reports', {
    extend: 'Ext.form.FieldContainer',

    requires: [
        'CMDBuildUI.view.administration.content.emails.templates.card.attachments.ReportsController'
    ],

    alias: 'widget.administration-content-emails-templates-card-attachments-reports',
    controller: 'administration-content-emails-templates-card-attachments-reports',
    viewModel: {
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    ui: 'administration-formpagination',
    config: {
        reports: null,
        attachments: null
    },
    layout: 'column',
    items: [
        CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getRow([
            CMDBuildUI.view.administration.content.emails.templates.card.FieldsHelper.getRow([{
                xtype: 'button',
                text: CMDBuildUI.locales.Locales.administration.reports.texts.addreport,
                ui: 'administration-action-small',
                itemId: 'addreport',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.reports.texts.addreport'
                },
                autoEl: {
                    'data-testid': 'administration-template-toolbar-addReportBtn'
                },
                bind: {
                    disabled: '{!toolAction._canAdd}'
                }
            }], {
                hidden: true,
                bind: {
                    hidden: '{actions.view}'
                }
            })]
        )]
});