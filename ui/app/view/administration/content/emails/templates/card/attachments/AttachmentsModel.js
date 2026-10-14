Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.attachments.AttachmentsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-emails-templates-card-attachments',

    formulas: {
        isDMSEnabled: function () {
            return CMDBuildUI.util.helper.Configurations.getEnabledFeatures().dms;
        }
    }

});