Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.attachments.Filter', {
    extend: 'CMDBuildUI.view.filters.attachments.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.templates.card.attachments.FilterController',
        'CMDBuildUI.view.administration.content.emails.templates.card.attachments.FilterModel'
    ],

    alias: 'widget.administration-content-emails-templates-card-attachments-filter',
    controller: 'administration-content-emails-templates-card-attachments-filter',
    viewModel: {
        type: 'administration-content-emails-templates-card-attachments-filter'
    },
    isDms: true
});