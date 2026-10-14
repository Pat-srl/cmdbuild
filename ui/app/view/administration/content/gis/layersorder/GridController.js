Ext.define('CMDBuildUI.view.administration.content.gis.layersorder.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-gis-layersorder-grid',
    control: {
        tableview: {
            beforedrop: 'onBeforeDrop'
        }
    },

    onBeforeDrop: function (node, data, overModel, dropPosition, dropHandlers) {
        var vm = this.getViewModel();
        var view = this.getView();

        var canUpdate = vm.get('theSession.rolePrivileges.admin_gis_modify');
        if(!canUpdate){
            return false;
        }

        var moved = data.records[0].getId();
        var reference = overModel.getId();
        var layers = vm.get('layersStore').getData().getIndices();
        var sortableLayers = [];
        for (var key in layers) {
            if (layers.hasOwnProperty(key)) {
                sortableLayers.push([key, layers[key]]); // each item is an array in format [key, value]
            }
        }

        // sort items by value
        sortableLayers.sort(function (a, b) {
            return a[1] - b[1]; // compare numbers
        });

        var jsonData = [];

        Ext.Array.forEach(sortableLayers, function (val, key) {
            if (moved !== val[0]) {
                if (dropPosition === 'before' && reference === val[0]) {
                    jsonData.push(moved);
                }
                jsonData.push(val[0]);
                if (dropPosition === 'after' && reference === val[0]) {
                    jsonData.push(moved);
                }
            }
        });

        Ext.Ajax.request({
            url: Ext.String.format(
                '{0}/classes/_ANY/geoattributes/order',
                CMDBuildUI.util.Config.baseUrl
            ),
            method: 'POST',
            jsonData: jsonData,
            success: function (response) {
                var res = JSON.parse(response.responseText);
                if (res.success) {
                    view.getView().grid.getStore().load();
                    dropHandlers.processDrop();
                }
            },
            failure: function (response) {
                dropHandlers.cancelDrop();
                view.getView().grid.getStore().load();
            }
        });
    }

});