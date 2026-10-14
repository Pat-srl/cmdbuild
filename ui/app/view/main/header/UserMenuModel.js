Ext.define('CMDBuildUI.view.main.header.UserMenuModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.main-header-usermenu',

    formulas: {
        // calculate the text to show in button
        text: {
            bind: {
                session: '{theSession}',
                role: '{theSession.role}',
                tenants: '{theSession.activeTenants}'
            },
            get: function (data) {
                if (data.role) {
                    var html = "";
                    if (data.session.get("multigroup")) {
                        html = CMDBuildUI.locales.Locales.main.multigroup;
                    } else {
                        var roles = data.session.get("availableRolesExtendedData");
                        var role = Ext.Array.findBy(roles, function (item, index) {
                            return item.code === data.role;
                        });
                        if (role) {
                            html = role._description_translation || role.description;
                        }
                    }
                    var tenantdescription;
                    if (data.tenants && data.tenants.length === 1) {
                        var tenant = Ext.Array.findBy(data.session.get("availableTenantsExtendedData"), function (item, index) {
                            return item.code == data.tenants[0];
                        });
                        tenantdescription = tenant.description;
                    } else if (data.tenants && data.tenants.length > 1) {
                        tenantdescription = Ext.String.format(
                            CMDBuildUI.locales.Locales.main.multitenant,
                            CMDBuildUI.util.Utilities.getTenantLabel()
                        );
                    }
                    if (tenantdescription) {
                        html += Ext.String.format("<br /><small>{0}</small>", tenantdescription);
                    }
                    return html;
                }
            }
        }
    }

});