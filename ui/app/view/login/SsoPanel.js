Ext.define('CMDBuildUI.view.login.SsoPanel', {
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.login.SsoPanelController',
        'CMDBuildUI.view.login.SsoPanelModel'
    ],

    alias: 'widget.login-ssopanel',
    controller: 'login-ssopanel',
    viewModel: {
        type: 'login-ssopanel'
    },

    hidden: true,

    items: [],
    cls: Ext.baseCSSPrefix + 'mt-2',

    bind: {
        hidden: '{sso.hidden}'
    }
});