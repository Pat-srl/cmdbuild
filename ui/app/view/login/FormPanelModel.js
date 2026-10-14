Ext.define('CMDBuildUI.view.login.FormPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.login-formpanel',

    data: {
        loggedIn: false,
        password: null,
        showErrorMessage: false,
        tenantsone: false,
        tenantsany: false,
        hasRole: false,
        hiddenfields: {},
        lengths: {
            groups: undefined,
            tenants: undefined
        },
        disableLogginButton: false
    },

    formulas: {
        updateFieldsVisibility: {
            bind: {
                loggedIn: '{loggedIn}',
                multiTenantActivationPrivileges: '{theSession.multiTenantActivationPrivileges}',
                groups: '{lengths.groups}',
                tenants: '{lengths.tenants}',
                activeTenants: '{theSession.activeTenants}'
            },
            get: function (data) {
                if (data.loggedIn) {
                    this.set("disabledfields.username", true);
                    this.set("disabledfields.password", true);
                    this.set("hiddenfields.role", data.groups > 1 ? false : true);
                    this.set("hiddenfields.cancelbtn", false);
                } else {
                    this.set("disabledfields.username", false);
                    this.set("disabledfields.password", false);
                    this.set("hiddenfields.role", true);
                    this.set("hiddenfields.cancelbtn", true);
                }
                this.getView().down("#roleField").allowBlank = this.get("hiddenfields.role");
            }
        },

        groupsData: {
            bind: {
                groups: '{theSession.availableRolesExtendedData}'
            },
            get: function (data) {
                const groups = [];
                Ext.Array.each(data.groups, function (item, index) {
                    groups.push({
                        value: item.code,
                        label: item._description_translation || item.description
                    });
                });
                this.set("lengths.groups", groups.length);
                return groups;
            }
        },

        tenantsData: {
            bind: {
                tenants: '{theSession.availableTenantsExtendedData}'
            },
            get: function (data) {
                const tenants = [];
                Ext.Array.each(data.tenants, function (item, index) {
                    tenants.push({
                        value: item.code,
                        label: item.description
                    });
                });
                this.set("lengths.tenants", tenants.length);
                return tenants;
            }
        },

        updateTenantsVisibility: {
            bind: {
                store: '{tenants}',
                privileges: '{theSession.multiTenantActivationPrivileges}',
                hasAvailableTenants: '{theSession.availableTenants.length}',
                hasRole: '{hasRole}'
            },
            get: function (data) {
                if (data.store && data.hasAvailableTenants && data.hasRole) {
                    switch (data.privileges) {
                        case "one":
                            this.set("tenantsone", true);
                            break;
                        case "any":
                            this.set("tenantsany", true);
                            break;
                    }
                }
            }
        },

        tenantLabel: {
            get: function () {
                return CMDBuildUI.util.Utilities.getTenantLabel();
            }
        }
    },

    links: {
        theSession: {
            type: 'CMDBuildUI.model.users.Session',
            create: {
                _id: "current"
            }
        }
    },

    stores: {
        groups: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: "memory"
            },
            autoDestroy: true,
            data: '{groupsData}',
            sorters: ['label']
        },
        tenants: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: "memory"
            },
            autoDestroy: true,
            data: '{tenantsData}'
        }
    }

});