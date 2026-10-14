Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.domains.DomainsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-processes-tabitems-domains-domains',
    data: {
        name: 'CMDBuildUI',
        toolbarHiddenButtons: {
            edit: false
        },
        actions: {
            view: true,
            edit: false
        },
        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        },
        includeInherited: true
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_domains_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        }
    }


});