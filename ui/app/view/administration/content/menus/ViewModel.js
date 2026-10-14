Ext.define('CMDBuildUI.view.administration.content.menus.ViewModel', {
    extend: 'Ext.app.ViewModel',
    requires: [
        'CMDBuildUI.util.api.Groups',
        'CMDBuildUI.util.MenuStoreBuilder'
    ],
    alias: 'viewmodel.administration-content-menus-view',

    data: {
        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
        actions: {
            edit: false,
            add: false,
            view: true
        },
        device: null,
        newFolderName: '',
        canAddNewFolder: false,
        unusedRoles: [],
        autoLoadRolesStore: null,
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
                canModify: '{theSession.rolePrivileges.admin_menus_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },

        titleManager: {
            bind: {
                device: '{theMenu.device}'
            },
            get: function (data) {
                var title = CMDBuildUI.locales.Locales.administration.menus.singular;
                if (data.device) {
                    title = Ext.String.format('{0} - {1}',
                        CMDBuildUI.locales.Locales.administration.menus.singular,
                        CMDBuildUI.util.administration.helper.RendererHelper.getMenuTargetDevice(data.device)
                    );
                }
                this.getParent().set('title', title);
            }
        },

        theMenuName: {
            bind: '{theMenu.name}',
            get: function (theMenuName) {
                if (theMenuName === '_default') {
                    return CMDBuildUI.locales.Locales.administration.common.strings['default']; // '*Default*';
                }
                return theMenuName;
            }
        },
        unusedRolesDataManager: {
            bind: {
                theMenuDevice: '{theMenu.device}',
                device: '{device}'
            },
            get: function (data) {
                var device = data.theMenuDevice || data.device;
                var me = this;
                CMDBuildUI.util.Stores.loadGroupsStore().then(
                    function (roles) {
                        roles = CMDBuildUI.util.administration.helper.SortHelper.sort(roles, 'description');

                        CMDBuildUI.util.Stores.loadAdministrationMenusStore().then(
                            function (menus) {
                                if (!me.destroyed) {
                                    me.set('menus', menus);
                                    var defaultMenu = Ext.Array.findBy(menus, function (menu) {
                                        return menu.get('group') === '_default' && menu.get('device') === device;
                                    });

                                    var unusedRoles = [];
                                    if (!defaultMenu) {
                                        unusedRoles.push({
                                            value: '_default',
                                            label: CMDBuildUI.locales.Locales.administration.common.strings['default']
                                        });

                                    }
                                    Ext.Array.forEach(roles, function (role) {
                                        var menuForRole = Ext.Array.findBy(menus, function (menu) {
                                            return menu.get('group') === role.get('name') && menu.get('device') === device;
                                        });
                                        if (!menuForRole) {
                                            unusedRoles.push({
                                                value: role.get('name'),
                                                label: role.get('description')
                                            });
                                        }
                                    });
                                    me.set('unusedRoles', unusedRoles);
                                    me.set('autoLoadRolesStore', true);
                                }
                            }
                        );
                    },
                    function () {

                    }
                );
            }
        },
        targetDevices: {
            get: function () {
                return CMDBuildUI.util.administration.helper.ModelHelper.getMenuTargetDevices();
            }
        }

    },

    stores: {

        rolesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{unusedRoles}',
            proxy: {
                type: 'memory'
            }
        },

        deviceTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{targetDevices}',
            proxy: {
                type: 'memory'
            }
        }
    },

    setCurrentAction: function (action) {
        this.set('actions.edit', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        this.set('actions.add', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
        this.set('actions.view', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        this.set('action', action);
    },

    setExistingMenus: function (value) {
        this.set('existingMenu', value);
    }
});