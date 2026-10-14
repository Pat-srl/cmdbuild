Ext.define('CMDBuildUI.view.main.header.tenants.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.main-header-tenants-container',

    data: {
        tenants: {
            length: 0,
            ignoreTenants: false,
            labelIgnoreTenants: '',
            canIgnoreTenants: false,
            activeTenants: []
        },
        fields: {
            buttonSaveDisabled: false,
            gridCounterHtml: ''
        }
    },

    formulas: {
        tenantsPreferencesData: function () {
            var session = CMDBuildUI.util.helper.SessionHelper.getCurrentSession(),
                tenantsAvailable = session.get("availableTenantsExtendedData"),
                length = tenantsAvailable.length;
            this.set("tenants.length", length);
            this.set("fields.gridCounterHtml", Ext.String.format("{0} {1}", length, CMDBuildUI.locales.Locales.common.grid.rows));
            this.set("tenants.labelIgnoreTenants", Ext.String.format(
                CMDBuildUI.locales.Locales.main.ignoretenants,
                CMDBuildUI.util.Utilities.getTenantLabel()));
            return tenantsAvailable;
        },
        activeTenants: {
            bind: {
                store: '{tenantsPreferences}'
            },
            get: function (data) {
                var activeTenants = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("activeTenants"),
                    tenantsSelected = Ext.Array.filter(data.store.getRange(), function (item, index, array) {
                        return Ext.Array.contains(activeTenants, item.get("code"));
                    });
                this.set("tenants.activeTenants", tenantsSelected);
            }
        }
    },

    stores: {
        tenantsPreferences: {
            proxy: 'memory',
            data: '{tenantsPreferencesData}',
            listeners: {
                filterchange: 'updateGridCounter'
            }
        }
    }

});