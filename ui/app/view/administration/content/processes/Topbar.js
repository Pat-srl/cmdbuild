Ext.define('CMDBuildUI.view.administration.content.processes.Topbar', {
    extend: 'Ext.form.Panel',

    requires: ['CMDBuildUI.view.administration.content.processes.TopbarController'],

    alias: 'widget.administration-content-processes-topbar',
    controller: 'administration-content-processes-topbar',

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    tbar: [
        {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.administration.processes.toolbar.addProcessBtn.text,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.processes.toolbar.addProcessBtn.text'
            },
            ui: 'administration-action-small',
            reference: 'addprocess',
            itemId: 'addprocess',
            autoEl: {
                'data-testid': 'administration-process-toolbar-addProcessBtn'
            },
            listeners: {
                render: function () {
                    this.setDisabled(!this.lookupViewModel().get('theSession.rolePrivileges.admin_processes_modify'));
                }
            }
        },
        {
            xtype: 'admin-globalsearchfield',
            objectType: 'processes'
        },
        {
            xtype: 'tbfill'
        },
        {
            xtype: 'tbtext',
            dock: 'right',
            bind: {
                html: '{precessLabel}: <b data-testid="administration-process-toolbar-processName">{theProcess.name}</b>'
            }
        }
    ]
});
