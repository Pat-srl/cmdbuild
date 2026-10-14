Ext.define('CMDBuildUI.view.administration.content.gis.icon.EditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-gis-icon-edit',
    control: {
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {

        var form = this.getView();
        var theIcon = this.getViewModel().get('theIcon');
        if (form.isValid()) {

            CMDBuildUI.util.Ajax.setActionId('icon.upload');
            var input = this.lookupReference("file").extractFileInput();

            // init formData
            var formData = new FormData();

            // append attachment json data
            var jsonData = Ext.encode(theIcon.getData());
            var fieldName = 'data';
            try {
                formData.append(fieldName, new Blob([jsonData], {
                    type: "application/json"
                }));
            } catch (err) {
                CMDBuildUI.util.Logger.log(
                    "Unable to create attachment Blob FormData entry with type 'application/json', fallback to 'text/plain': " + err,
                    CMDBuildUI.util.Logger.levels.error
                );
                // metadata as 'text/plain' (format compatible with older webviews)
                formData.append(fieldName, jsonData);
            }

            var url = Ext.String.format(
                '{0}/uploads/' + theIcon.get('_id'),
                CMDBuildUI.util.Config.baseUrl
            );

            var method = "PUT";
            CMDBuildUI.util.administration.File.upload(method, formData, input, url, {
                success: function (response) {
                    if (typeof response === 'string') {
                        response = Ext.JSON.decode(response);
                    }
                    var viewports = Ext.ComponentQuery.query('viewport');
                    var grid = viewports[0].down('administration-content-gis-grid');
                    grid.getStore().load({
                        callback: function (records, operation, success) {
                            grid.getView().refresh();
                            var recordIndex = grid.getStore().find('_id', response.data._id);
                            grid.getView().grid.setSelection(recordIndex);
                            form.up("panel").close();
                        }
                    });

                }
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.getView().up('panel').close();
    }

});