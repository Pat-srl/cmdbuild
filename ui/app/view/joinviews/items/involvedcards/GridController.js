Ext.define('CMDBuildUI.view.joinviews.items.involvedcards.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.joinviews-items-involvedcards-grid',

    control: {
        'tableview': {
            actionopencard: 'onActionOpenCard'
        }
    },

    /**
    * @param {Ext.data.Model} record
    */
    onActionOpenCard: function (record) {
        var objectName = record.get("typeName"),
            objectId = record.get("id"),
            path;
        switch (record.get("type")) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                path = CMDBuildUI.util.Navigation.getClassBaseUrl(objectName, objectId, null, true);
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                path = CMDBuildUI.util.Navigation.getProcessBaseUrl(objectName, objectId, null, null, true);
                break;
        }
        if (path) {
            CMDBuildUI.util.Utilities.redirectTo(path);
        }
    }

});
