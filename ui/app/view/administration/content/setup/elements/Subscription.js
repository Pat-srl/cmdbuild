Ext.define('CMDBuildUI.view.administration.content.setup.elements.Subscription', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.SubscriptionController',
        'CMDBuildUI.view.administration.content.setup.elements.CustomerInfo'
    ],

    alias: 'widget.administration-content-setup-elements-subscription',
    viewModel: {},
    controller: 'administration-content-setup-elements-subscription',
    items: []
});
