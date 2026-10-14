Ext.define('CMDBuildUI.view.main.header.tenants.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.main-header-tenants-container',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#savebutton': {
            click: 'onSaveButtonClick'
        },
        '#cancelbutton': {
            click: 'onCancelButtonClick'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.main.header.tenants.Container} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel(),
            session = CMDBuildUI.util.helper.SessionHelper.getCurrentSession();

        vm.set("tenants.ignoreTenants", session.get("ignoreTenants"));
        vm.set("tenants.canIgnoreTenants", session.get("canIgnoreTenants"));
        vm.set("tenants.isMultitenant", session.get("multiTenantActivationPrivileges") !== "one");
    },

    /**
     * 
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveButtonClick: function (button, e, eOpts) {
        var session = CMDBuildUI.util.helper.SessionHelper.getCurrentSession(),
            gridSelection = Ext.Array.map(this.getView().down("#tenantsGrid").getSelection(), function (item, index, array) {
                return item.get("code");
            });

        if (!Ext.Array.equals(session.get("activeTenants").sort(), gridSelection.sort())) {
            CMDBuildUI.util.Msg.confirm(
                CMDBuildUI.locales.Locales.notifier.attention,
                CMDBuildUI.locales.Locales.main.confirmchangetenants,
                function (btnText) {
                    if (btnText === "yes") {
                        CMDBuildUI.util.helper.SessionHelper.updateActiveTenants(gridSelection);
                        session.save({
                            success: function () {
                                window.location.reload();
                            }
                        });
                    }
                });
        }
    },

    /**
     * 
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelButtonClick: function (button, e, eOpts) {
        this.getView().up().close();
    },

    /**
     * Update the value of counter in grid
     * @param {Ext.data.Store} store 
     * @param {Ext.util.Filter[]} filters 
     * @param {Object} eOpts 
     */
    updateGridCounter: function (store, filters, eOpts) {
        var totalCount = store.getRange().length,
            text = totalCount !== 1 ? CMDBuildUI.locales.Locales.common.grid.rows : CMDBuildUI.locales.Locales.common.grid.row;
        this.getViewModel().set("fields.gridCounterHtml", Ext.String.format("{0} {1}", totalCount, text));
    }

});