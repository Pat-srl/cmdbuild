Ext.define('CMDBuildUI.view.views.items.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.views-items-view',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * @param {CMDBuildUI.view.views.items.View} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();

        var config = view.getInitialConfig();
        if (!Ext.isEmpty(config._rowContext)) {
            var record = config._rowContext.record; // get widget record
            if (record && record.getData()) {
                vm.set("objectTypeName", config._rowContext.ownerGrid.getObjectTypeName());
                vm.set("theObject", record);
            }
        }

        CMDBuildUI.util.helper.ModelHelper.getModel(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.view,
            vm.get("objectTypeName")
        ).then(function (model) {
            var items = [];
            items = CMDBuildUI.util.helper.FormHelper.renderForm(model, {
                mode: CMDBuildUI.util.helper.FormHelper.formmodes.read,
                showAsFieldsets: false
            });
            view.removeAll();
            view.add(items);
        });
    }
});
