Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.domains.DomainsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-classes-tabitems-domains-domains',
    data: {
        name: 'CMDBuildUI',
        toolbarHiddenButtons: {
            edit: false
        },
        actions: {
            view: true,
            edit: false
        },
        searchdomain:{
            value: null
        },
        includeInherited: true,
        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
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
    },
    setFormMode: function (_mode) {
        var mode = _mode.toLowerCase();
        switch (mode) {
            case CMDBuildUI.util.administration.helper.FormHelper.formActions.edit:
                this.set('actions.view', false);
                this.set('actions.edit', true);
                this.set('toolbarHiddenButtons.edit', true);
                break;

            default:
                this.set('actions.view', true);
                this.set('actions.edit', false);
                this.set('toolbarHiddenButtons.edit', false);
                break;
        }
    }
});