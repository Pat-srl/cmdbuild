Ext.define('CMDBuildUI.view.administration.content.domains.Topbar', {
    extend: 'Ext.form.Panel',

    requires: ['CMDBuildUI.view.administration.content.domains.TopbarController'],

    alias: 'widget.administration-content-domains-topbar',
    controller: 'administration-content-domains-topbar',

    forceFit: true,
    loadMask: true,

    tbar: [
        {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.administration.domains.toolbar.addBtn.text,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.domains.toolbar.addBtn.text'
            },
            ui: 'administration-action-small',
            itemId: 'adddomain',
            autoEl: {
                'data-testid': 'administration-domain-toolbar-addDomainBtn'
            }
        },
        {
            xtype: 'admin-globalsearchfield',
            objectType: 'domains'
        },
        {
            xtype: 'tbfill'
        },
        {
            xtype: 'tbtext',
            dock: 'right',
            bind: {
                html: '{domainLabel}: <b data-testid="administration-domain-toolbar-domainName">{theDomain.name}</b>'
            }
        }
    ],

    initComponent: function () {
        const vm = this.lookupViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.domains);
        this.callParent(arguments);
    }
});
