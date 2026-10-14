Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-importexport-datatemplates-view',
    data: {
        templateId: null,
        disabledTabs: {
            properties: false,
            permissions: false
        },
        toolbarHiddenButtons: {
            edit: true, // action !== view
            print: true, // action !== view
            disable: true,
            enable: true
        },
        targetName: null,
        filterByTargetName: [],
        toolAction: {
            _canAdd: false,
            _canClone: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        },
        isStandalone: true
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_etl_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canClone', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },
        getToolbarButtons: {
            bind: '{theGateTemplate.active}',
            get: function (get) {
                this.set('toolbarHiddenButtons.edit', !this.get('actions.view'));
                this.set('toolbarHiddenButtons.print', !this.get('actions.view'));
                this.set('toolbarHiddenButtons.disable', true);
                this.set('toolbarHiddenButtons.enable', false);
            }
        },
        updateToolbarButtons: {
            bind: '{theGateTemplate.active}',
            get: function (data) {
                if (data) {
                    this.set('toolbarHiddenButtons.disable', false);
                    this.set('toolbarHiddenButtons.enable', true);
                } else {
                    this.set('toolbarHiddenButtons.disable', true);
                    this.set('toolbarHiddenButtons.enable', false);
                }
            }
        },
        filterByTargetName: {
            bind: '{targetName}',
            get: function (targetName) {
                if (!targetName) {
                    return [];
                }
                return [
                    function (item) {
                        return item.get('targetName') === targetName;
                    }
                ];
            }
        }
    },

    stores: {
        allImportExportTemplates: {
            source: 'importexports.Templates',
            autoload: true,
            autoDestroy: true,
            filters: '{filterByTargetName}'
        }
    }
});
