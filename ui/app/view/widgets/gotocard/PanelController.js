Ext.define('CMDBuildUI.view.widgets.gotocard.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-gotocard-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        }
    },

    /**
     * @param {CMDBuildUI.view.widgets.gotocard.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var theWidget = view.lookupViewModel().get('theWidget'),
            urlFn,
            objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(theWidget.get('ObjectTypeName'));

        switch (objectType) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                urlFn = CMDBuildUI.util.Navigation.getClassBaseUrl;
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                urlFn = CMDBuildUI.util.Navigation.getProcessBaseUrl;
                break;
            default:
                CMDBuildUI.util.Logger.log(
                    `Unsupported object type: ${objectType}`,
                    CMDBuildUI.util.Logger.levels.error
                );
                return;
        }

        var url = urlFn(theWidget.get('ObjectTypeName'), theWidget.get('ObjId'), null, true);
        if (theWidget.get('IsAttachment')) {
            url += '/attachments';
        }
        CMDBuildUI.util.Utilities.redirectTo(url);
    },

    /**
     * @param {CMDBuildUI.view.widgets.gotocard.Panel} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        view.ownerCt.close();
    }
});
