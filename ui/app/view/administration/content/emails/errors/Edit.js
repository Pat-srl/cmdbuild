Ext.define('CMDBuildUI.view.administration.content.emails.errors.Edit', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.errors.EditController',
        'CMDBuildUI.view.administration.content.emails.errors.EmailModel'
    ],

    mixins: [
        'CMDBuildUI.view.administration.content.emails.errors.EditMixin'
    ],

    alias: 'widget.administration-content-emails-errors-edit',
    controller: 'administration-content-emails-errors-edit',
    viewModel: {
        type: 'administration-content-emails-errors-edit'
    },

    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.update,

    autoScroll: true

});