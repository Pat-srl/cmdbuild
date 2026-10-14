Ext.define('CMDBuildUI.view.administration.content.setup.elements.LoginModules', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.LoginModulesController',
        'CMDBuildUI.view.administration.content.setup.elements.LoginModulesModel'
    ],

    alias: 'widget.administration-content-setup-elements-loginmodules',
    controller: 'administration-content-setup-elements-loginmodules',
    viewModel: {
        type: 'administration-content-setup-elements-loginmodules'
    },
    layout: 'fit',
    scrollable: false,
    forceFit: true,
    items: [{
        xtype: 'grid',
        scrollable: 'y',
        reserveScrollbar: true,
        forceFit: true,
        loadMask: true,
        ui: 'cmdbuildgrouping',
        bind: {
            store: '{loginModulesStore}'
        },
        features: [{
            ftype: 'grouping',
            collapsible: true,
            groupHeaderTpl: [
                '<div>{[this.formatGroupLabel(values)]}</div>',
                {
                    formatGroupLabel: function (data) {
                        return Ext.String.format('{0}: {1}', CMDBuildUI.locales.Locales.administration.systemconfig.module, data.children[0].get('type'));
                    }
                }
            ]
        }],
        viewConfig: {
            markDirty: false
        },

        columns: [{
            flex: 0.4,
            text: CMDBuildUI.locales.Locales.administration.systemconfig.parameter,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.systemconfig.parameter'
            },
            dataIndex: 'description'
        }, {
            flex: 0.4,
            text: CMDBuildUI.locales.Locales.administration.common.labels.code,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.code'
            },
            hidden: true,
            dataIndex: 'key'
        }, {
            flex: 0.2,
            text: CMDBuildUI.locales.Locales.administration.systemconfig.value,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.systemconfig.value'
            },
            dataIndex: 'value'
        }]
    }]
});