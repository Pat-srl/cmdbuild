Ext.define('CMDBuildUI.view.processes.instances.instance.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.processes-instances-instance-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.card.View} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();

        // read instance parameters from row configuration
        // when view is rendered inside grid row
        if (!view.getShownInPopup()) {
            var config = view.getInitialConfig();
            if (!Ext.isEmpty(config._rowContext)) {
                // get widget record
                var record = config._rowContext.record;
                if (record && record.getData()) {
                    // set view model variable
                    vm.set("objectId", record.get("_id"));
                    vm.set("objectTypeName", record.get("_type"));
                    vm.set("activityId", record.get("_activity_id"));
                }
            }
        }

        function modelLoadSuccess(model) {
            vm.set("objectModel", model);

            // load process instance
            vm.linkTo("theObject", {
                type: model.getName(),
                id: vm.get("objectId")
            });
        }

        if (!(vm.get("theObject") && vm.get("objectModel"))) {
            // get instance model
            CMDBuildUI.util.helper.ModelHelper.getModel(
                CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
                vm.get("objectTypeName")
            ).then(modelLoadSuccess, function () {
                CMDBuildUI.util.Msg.alert('Error', 'Process non found!');
            });
        }

        vm.bind("{theObject}", function () {
            view.removeAll();
            // load activity
            view.loadActivity();
        }, vm, {
            single: true
        });
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.card.View} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        view.loadmask = null;
        if (!view.getShownInPopup()) {
            view.setMinHeight(100);
            view.loadmask = CMDBuildUI.util.Utilities.addLoadMask(view);
        }
    },

    privates: {
        /**
         * Get resource base path for routing.
         * @param {Boolean} includeactivity
         * @return {String}
         */
        getBasePath: function (includeactivity) {
            var vm = this.getViewModel();
            var url = Ext.String.format(
                "processes/{0}/instances/{1}",
                vm.get("objectTypeName"),
                vm.get("objectId")
            );
            if (includeactivity) {
                url = Ext.String.format(
                    "{0}/activities/{1}",
                    url,
                    vm.get("activityId")
                );
            }
            if (CMDBuildUI.util.Navigation._currentcontext.objectType == CMDBuildUI.util.helper.ModelHelper.objecttypes.custompage) {
                url = Ext.String.format(
                    '{0}/{1}/{2}',
                    'custompages',
                    CMDBuildUI.util.Navigation._currentcontext.objectTypeName,
                    url
                );
            }
            return url;
        }
    }
});