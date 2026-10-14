Ext.define('CMDBuildUI.mixins.routes.management.Homepage', {
    mixinId: 'managementroutes-homepage-mixin',

    /**
     * Before show homepage
     *
     * @param {Object} action
     */
    onBeforeShowHomepage: function (action) {
        const homepageEnabled = CMDBuildUI.util.helper.Configurations.get(
            CMDBuildUI.model.Configuration.homepage.enabled
        );

        if (!homepageEnabled) {
            action.stop();
            this.redirectTo('management', true);
            return;
        }

        action.resume();
    },

    /**
     * Show homepage
     *
     * @param {String} homepageName
     */
    showHomepage: function () {
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);
        CMDBuildUI.util.Navigation.addIntoManagementContainer('homepage-container');

        CMDBuildUI.util.Navigation.updateCurrentManagementContext(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.homepage,
            CMDBuildUI.model.menu.MenuItem.types.homepage
        );

        Ext.GlobalEvents.fireEventArgs('objecttypechanged', [CMDBuildUI.model.menu.MenuItem.types.homepage]);
    }
});
