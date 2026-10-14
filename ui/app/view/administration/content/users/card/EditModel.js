Ext.define('CMDBuildUI.view.administration.content.users.card.EditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.view-administration-content-users-card-edit',
    data: {
        theUser: null,
        rolesData: [],
        tenantsData: [],
        isMultitenantActive: false,
        tenantModeIsDbFunction: false,
        tenantModeIsClass: false,
        toolAction: {
            _canClone: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },

    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_users_modify}',
                canWrite: '{theUser._can_write}'
            },
            get: function (data) {
                this.set('toolAction._canClone', data.canModify === true && data.canWrite);
                this.set('toolAction._canUpdate', data.canModify === true && data.canWrite);
                this.set('toolAction._canDelete', data.canModify === true && data.canWrite);
                this.set('toolAction._canActiveToggle', data.canModify === true && data.canWrite);
            }
        },
        isClone: {
            bind: '{theUser}',
            get: function (theUser) {
                if (theUser) {
                    return theUser.phantom || false;
                }
                return false;
            }
        },
        initialPageManager: {
            bind: {
                initialPage: '{theUser.initialPage}'
            },
            get: function (data) {
                if (data.initialPage) {
                    var initialPage = data.initialPage,
                        initialPageSplip = initialPage.split(':');

                    if (initialPageSplip.length > 1) {
                        var object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                            initialPageSplip[1],
                            initialPageSplip[0]
                        );
                        if (object) {
                            initialPage = object.get('description');
                        }
                        return this.set('theUser._initialPage_description', initialPage);
                    }
                    return this.set('theUser._initialPage_description', initialPage);
                }
            }
        },
        groupsHtml: {
            bind: '{theUser.userGroups}',
            get: function (groups) {
                if (groups && groups.length) {
                    var groupsHtml = '<ul>';
                    groups.forEach(function (group) {
                        groupsHtml += '<li>' + group.description + '</li>';
                    });
                    groupsHtml += '</ul>';
                    return groupsHtml;
                }
                return '<em>' + CMDBuildUI.locales.Locales.administration.users.fieldLabels.nodata + '</em>';
            }
        },
        tenantsHtml: {
            bind: '{theUser.userTenants}',
            get: function (tenants) {
                if (tenants && tenants.length) {
                    var tenantsHtml = '<ul>';
                    tenants.forEach(function (tenant) {
                        tenantsHtml += '<li>' + tenant.description + '</li>';
                    });
                    return (tenantsHtml += '</ul>');
                }
                return '<em>' + CMDBuildUI.locales.Locales.administration.users.fieldLabels.nodata + '</em>';
            }
        },

        panelTitle: {
            bind: '{theUser.username}',
            get: function (username) {
                var title = Ext.String.format(
                    '{0} - {1}',
                    CMDBuildUI.locales.Locales.administration.users.fieldLabels.user,
                    username
                );
                this.getParent().set('title', title);
            }
        },

        getRolesData: {
            bind: '{theUser}',
            get: function (theUser) {
                if (theUser) {
                    var me = this,
                        store = me.getStore('groups'),
                        items = store.getRange(),
                        roles = [];

                    if (!theUser.get('userGroups')) {
                        theUser.set('userGroups', []);
                    }
                    items.forEach(function (role) {
                        if (role.get('active')) {
                            const exist = Ext.Array.findBy(theUser.get('userGroups'), function (userGroup) {
                                return userGroup._id === role.get('_id');
                            });

                            me.getStore('rolesStore').add({
                                description: role.get('description'),
                                _id: role.get('_id'),
                                name: role.get('name'),
                                active: exist ? true : false
                            });
                        }
                    });

                    me.set('theUser.groupsLength', roles.length);
                }
            }
        },

        getTenantsData: {
            bind: '{theUser}',
            get: function (theUser) {
                var me = this;
                if (theUser) {
                    var multitenantConfig = CMDBuildUI.util.helper.Configurations.get(
                        CMDBuildUI.model.Configuration.multitenant.mode
                    );
                    var isMultitenantActive = CMDBuildUI.util.helper.Configurations.get(
                        CMDBuildUI.model.Configuration.multitenant.enabled
                    );
                    me.set('isMultitenantActive', isMultitenantActive);
                    me.set('tenantModeIsDbFunction', isMultitenantActive && multitenantConfig === 'DB_FUNCTION');
                    me.set('tenantModeIsClass', isMultitenantActive && multitenantConfig === 'CMDBUILD_CLASS');

                    if (isMultitenantActive) {
                        me.set('tenantstoreload', true);
                    }
                }
            }
        },

        userGroups: {
            bind: {
                userGroups: '{theUser.userGroups}',
                groupsLength: '{theUser.groupsLength}'
            },

            get: function (data) {
                if (data.userGroups) {
                    return data.userGroups;
                }
            },
            set: function (value) {
                this.set('theUser.userGroups', value);
                this.set('theUser.groupsLength', this.get('theUser.userGroups').length);
            }
        },
        userTenants: {
            bind: {
                userTenants: '{theUser.userTenants}'
            },

            get: function (data) {
                if (data.userTenants) {
                    return data.userTenants;
                }
            },
            set: function (value) {}
        },
        multitenantPrivileges: function () {
            return [
                {
                    value: 'any',
                    label: CMDBuildUI.locales.Locales.administration.common.actions.yes
                },
                {
                    value: 'one',
                    label: CMDBuildUI.locales.Locales.administration.common.actions.no
                }
            ];
        },

        tenantLabel: {
            get: function () {
                return CMDBuildUI.util.Utilities.getTenantLabel();
            }
        }
    },

    stores: {
        tenantStore: {
            proxy: {
                type: 'baseproxy',
                url: '/tenants'
            },
            pageSize: 0,
            listeners: {
                load: 'onTenantStoreLoad'
            },
            autoDestroy: true,
            autoLoad: '{tenantstoreload}'
        },

        multiTenantActivationPrivilegesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{multitenantPrivileges}'
        },
        groups: {
            type: 'chained',
            source: 'groups.Groups',
            sorters: ['description'],
            autoLoad: true,
            autoDestroy: true
        },
        rolesStore: {
            model: 'CMDBuildUI.model.users.UserGroup',
            proxy: {
                type: 'memory'
            },
            sorters: ['description'],
            autoDestroy: true
        },
        activeUserRolesStore: {
            source: '{rolesStore}',
            filters: [
                function (item) {
                    return item.get('active') === true;
                }
            ],
            proxy: {
                type: 'memory'
            },
            sorters: ['description'],
            autoLoad: true,
            autoDestroy: true
        },
        userGroupsStore: {
            proxy: {
                type: 'baseproxy',
                url: '{userGroupProxyUrl}'
            },
            sorters: ['description'],
            pageSize: 0,
            autoLoad: true,
            autoDestroy: true
        },
        tenantsStore: {
            data: '{tenantsData}',
            proxy: {
                type: 'memory'
            },
            sorters: ['description'],
            fields: ['active', 'description', 'name', '_id'],
            pageSize: 0,
            autoLoad: true,
            autoDestroy: true
        },
        userTenantsStore: {
            proxy: {
                type: 'baseproxy',
                url: '{userTenantProxyUrl}'
            },
            sorters: ['description'],
            autoLoad: true,
            pageSize: 0,
            autoDestroy: true
        },
        languages: {
            source: 'localizations.Languages',
            sorters: ['description'],
            autoLoad: true,
            pageSize: 0,
            autoDestroy: true
        },

        getSelectedTenants: {
            data: '{userTenants}',
            proxy: {
                type: 'memory'
            },
            sorters: ['description'],
            autoLoad: true,
            autoDestroy: true,
            filters: [
                function (item) {
                    return item.get('active') === true;
                }
            ]
        }
    }
});
