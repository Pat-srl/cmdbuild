Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.attachments.FromCardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-emails-templates-card-attachments-fromcard',

    formulas: {
        attachmentsDataStore: function () {
            return CMDBuildUI.util.administration.helper.ModelHelper.emailTemplateAttachmentsFromCard();
        }
    },

    stores: {
        attachmentsStore: {
            proxy: 'memory',
            data: '{attachmentsDataStore}'
        }
    }

});