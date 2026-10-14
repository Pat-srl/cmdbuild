Ext.define('CMDBuildUI.view.administration.content.setup.elements.AuthModules', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.AuthModulesController',
        'CMDBuildUI.view.administration.content.setup.elements.AuthModulesModel'
    ],
    alias: 'widget.administration-content-setup-elements-authmodules',
    controller: 'administration-content-setup-elements-authmodules',
    viewModel: {
        type: 'administration-content-setup-elements-authmodules'
    },
    layout: 'fit',
    /**
     * 
     * org.cmdbuild.auth.default.enabled = true/false (default true, questo e’ il vecchio db auth);


        org.cmdbuild.auth.ldap.enabled = true/false (attiva il repository ldap);


        org.cmdbuild.auth.rsa.enabled = true/false (attiva il login tramite chiave rsa);


        org.cmdbuild.auth.file.enabled = true/false (attiva il login tramite file, usato dalla cli per l’autenticazione in locale);


        org.cmdbuild.auth.header.enabled = true/false (attiva header auth);


        org.cmdbuild.auth.customlogin.enabled = true/false (attiva custom login auth);

     */
    items: [{
        xtype: 'grid',
        itemId: 'authModulesGrid',
        ui: 'cmdbuildgrouping',
        bind: {
            store: '{authModulesStore}'
        },
        scrollable: 'y',
        reserveScrollbar: true,
        forceFit: true,
        loadMask: true,
        viewConfig: {
            markDirty: false
        },
        features: [{
            ftype: 'grouping',
            collapsible: true,
            groupHeaderTpl: [
                '<div>{[this.formatGroupLabel(values)]}</div>',
                {
                    formatGroupLabel: function (data) {
                        if (!data.groupValue) {
                            return CMDBuildUI.locales.Locales.administration.systemconfig.disabled;
                        }
                        return CMDBuildUI.locales.Locales.administration.systemconfig.enabled;

                    }
                }
            ]
        }],
        columns: [{
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.systemconfig.module,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.systemconfig.module'
            },
            dataIndex: 'label'
        }]
    }]
});