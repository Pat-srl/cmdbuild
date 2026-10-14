Ext.define('CMDBuildUI.view.emails.email.Edit', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.emails.email.EditController',
        'CMDBuildUI.view.emails.email.EmailModel'
    ],

    mixins: [
        'CMDBuildUI.view.emails.email.Mixin'
    ],

    alias: 'widget.emails-edit',
    controller: 'emails-edit',
    viewModel: {
        type: 'emails-email'
    },

    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.update,

    autoScroll: true

});