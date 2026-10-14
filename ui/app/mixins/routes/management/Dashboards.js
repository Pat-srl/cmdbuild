Ext.define('CMDBuildUI.mixins.routes.management.Dashboards', {
    mixinId: 'managementroutes-dashboards-mixin',

    /**
     * Before show dashboard
     * 
     * @param {String} dashboardName
     * @param {Object} action
     */
    onBeforeShowDashboard: function (dashboardName, action) {
        dashboardName = decodeURIComponent(dashboardName)
        var object = CMDBuildUI.util.helper.ModelHelper.getDashboardFromName(dashboardName);

        if (object) {
            action.resume();
        } else {
            CMDBuildUI.util.Utilities.redirectTo("management");
            action.stop();
        }
    },

    /**
     * Show dashboard
     * 
     * @param {String} dashboardName
     */
    showDashboard: function (dashboardName) {
        dashboardName = decodeURIComponent(dashboardName)
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);
        CMDBuildUI.util.Navigation.addIntoManagementContainer('dashboards-container', {
            viewModel: {
                data: {
                    objectTypeName: dashboardName
                }
            }
        });

        CMDBuildUI.util.Navigation.updateCurrentManagementContext(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.dashboard,
            dashboardName
        );

        // fire global event objecttypechanged
        Ext.GlobalEvents.fireEventArgs("objecttypechanged", [dashboardName]);
    }
});