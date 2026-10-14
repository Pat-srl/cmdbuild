Ext.define('CMDBuildUI.view.administration.content.dms.models.Topbar', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.dms.models.TopbarController'
    ],

    alias: 'widget.administration-content-dms-models-topbar',
    controller: 'administration-content-dms-models-topbar',

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.dmsmodels.adddmsmodel,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.dmsmodels.adddmsmodel'
        },
        ui: 'administration-action-small',
        reference: 'adddmsmodel',
        itemId: 'adddmsmodel',
        autoEl: {
            'data-testid': 'administration-dms-models-toolbar-addDMSModelBtn'
        },
        listeners: {
            render: function () {
                this.setDisabled(!this.lookupViewModel().get('theSession.rolePrivileges.admin_dms_modify'));
            }
        }
    }, {
        xtype: 'admin-globalsearchfield',
        objectType: 'dmsmodels'
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'tbtext',
        dock: 'right',
        bind: {
            hidden: '{actions.empty}',
            html: '{dmsmodelLabel}: <b data-testid="administration-class-toolbar-className">{theModel.name}</b>'
        }
    }]
});