Ext.define('CMDBuildUI.view.emails.email.Create', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.emails.email.CreateController',
        'CMDBuildUI.view.emails.email.EmailModel'
    ],

    mixins: [
        'CMDBuildUI.view.emails.email.Mixin'
    ],

    alias: 'widget.emails-create',
    controller: 'emails-create',
    viewModel: {
        type: 'emails-email'
    },

    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.create,

    autoScroll: true,

    bubbleEvents: [
        'itemcreated'
    ],

    scrollable: true
});