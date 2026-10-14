Ext.define('CMDBuildUI.view.administration.content.dashboards.common.tabs.PropertiesTabBaseModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dashboards-common-propertiestab',
    data: {
        totalItemsCount: 0,
        modifyPrivilegeKey: 'admin_dashboards_modify', // default, sovrascritto per istanza

        toolbarHiddenButtons: {
            edit: true, // action !== view
            print: true, // action !== view
            disable: true,
            enable: true
        },

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
                rolePrivileges: '{theSession.rolePrivileges}'
            },
            get: function (data) {
                var canModify = !!(data.rolePrivileges && data.rolePrivileges[this.get('modifyPrivilegeKey')] === true);
                this.set('toolAction._canAdd', canModify);
                this.set('toolAction._canUpdate', canModify);
                this.set('toolAction._canDelete', canModify);
                this.set('toolAction._canActiveToggle', canModify);
            }
        },

        getToolbarButtons: {
            bind: '{theDashboard.active}',
            get: function (get) {
                this.set('toolbarHiddenButtons.edit', !this.get('actions.view'));
                this.set('toolbarHiddenButtons.clone', !this.get('actions.view'));
                this.set('toolbarHiddenButtons.print', !this.get('actions.view'));
                this.set('toolbarHiddenButtons.disable', true);
                this.set('toolbarHiddenButtons.enable', false);
            }
        },
        updateToolbarButtons: {
            bind: '{theDashboard.active}',
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
        theDashboardManager: {
            bind: '{theDashboard}',
            get: function (theDashboard) {
                if (theDashboard) {
                    const rawIconFont = theDashboard.get('icon_font');
                    if (rawIconFont) {
                        theDashboard.set(
                            'icon_font',
                            CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(rawIconFont)
                        );

                        theDashboard.set(
                            'icon_description',
                            CMDBuildUI.util.helper.IconHelper.getIconDescription(rawIconFont.split(':')[0])
                        );
                    }
                }
            }
        }
    }
});
