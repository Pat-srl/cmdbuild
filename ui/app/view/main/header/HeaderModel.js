Ext.define('CMDBuildUI.view.main.header.HeaderModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.main-header-header',

    data: {
        companylogoinfo: {
            hidden: true,
            url: null
        }
    },

    formulas: {
        isAdministrator: {
            bind: {
                privileges: '{theSession.rolePrivileges}',
                isAdministrationModule: '{isAdministrationModule}'
            },
            get: function (data) {
                return data.privileges && data.privileges.admin_access && !data.isAdministrationModule;
            }
        },

        calendarbtnhidden: {
            bind: {
                privileges: '{theSession.rolePrivileges}',
                enabled: '{scheduler.enabled}'
            },
            get: function (data) {
                if (
                    data.enabled &&
                    data.privileges &&
                    (data.privileges.calendar_access || data.privileges.calendar_event_create)
                ) {
                    return false;
                }
                return true;
            }
        }
    }
});
