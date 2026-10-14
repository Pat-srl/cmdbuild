Ext.define('CMDBuildUI.view.map.tab.cards.geoattributesGrid.geoserverLayers.GeoserverLayersController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-tab-cards-geoattributesgrid-geoserverlayers',

    control: {
        '#savebtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelbtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {Ext.button.Button} btn 
     * @param {Event} e 
     */
    onSaveBtnClick: function (btn, e) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var form = this.getView(),
            theLayer = this.getViewModel().getData().record,
            jsonData = theLayer.getData();

        for (key in jsonData) {
            if (Ext.String.startsWith(key, "_")) {
                delete jsonData[key];
            }
        }

        var className = theLayer.get('_owner_type'),
            cardId = theLayer.get('_owner_id'),
            url = CMDBuildUI.util.api.Classes.getGeoLayersUrl(className, cardId) + "/" + theLayer.get('_attr');

        theLayer.getProxy().setUrl();
        var loadMask = CMDBuildUI.util.Utilities.addLoadMask(form);

        CMDBuildUI.util.File.uploadFileWithMetadata(
            "PUT",
            url,
            form.down('filefield').fileInputEl.dom.files[0],
            jsonData
        ).then(function (response) {
            form.fireEvent("closepopup", form, response);
            CMDBuildUI.util.helper.FormHelper.endSavingForm();
            CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
        }).otherwise(function (error) {
            if (!error.responseText) {
                error = {
                    responseText: error
                };
            }
            CMDBuildUI.util.Ajax.showMessages(error, {
                hideErrorNotification: false
            });
            CMDBuildUI.util.helper.FormHelper.endSavingForm();
            CMDBuildUI.util.Utilities.removeLoadMask(loadMask);
        });

    },

    /**
     * @param {Ext.button.Button} btn 
     * @param {Event} e 
     */
    onCancelBtnClick: function (btn, e) {
        var form = this.getView();
        form.fireEvent("closepopup", form);
    }

});