Ext.define('CMDBuildUI.view.boot.configuredb.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.boot-configuredb-panel',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#testConnectionBtn': {
            click: 'onTestConnectionBtnClick'
        },
        '#configureBtn': {
            click: 'onConfigureBtnClick'
        }
    },

    /**
     *
     * @param {Ext.panel.Panel} panel
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAfterRender: function (panel, e, eOpts) {
        Ext.defer(function () {
            panel.updateLayout();
        }, 50);
    },

    /**
     *
     * @param {Ext.button.Button} btn
     * @param {Event} e
     * @param {Object} eOpts
     */
    onTestConnectionBtnClick: function (btn, e, eOpts) {
        var values = btn.lookupViewModel().get('values');
        var source = null;
        switch (values.configtype) {
            case 'empty':
                source = 'empty.dump.xz';
                break;
            case 'demo':
                source = 'demo.dump.xz';
                break;
            case 'upload':
                source = 'file';
                break;
        }
        var params = {
            'db.url': this.getConnectionUrl(values.dbhost, values.dbport, values.dbname),
            'db.username': values.dbusername,
            'db.password': values.dbpassword,
            'db.admin.username': values.dbadminusername,
            'db.admin.password': values.dbadminpassword,
            'db.source': source
        };
        var url = CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Common.getCheckDBConfigUrl();
        Ext.Ajax.request({
            url: url,
            method: 'POST',
            jsonData: params
        }).then(function () {
            CMDBuildUI.util.Notifier.showSuccessMessage('Connection successful');
        });
    },

    /**
     *
     * @param {Ext.button.Button} btn
     * @param {Event} e
     * @param {Object} eOpts
     */
    onConfigureBtnClick: function (btn, e, eOpts) {
        var form = this.getView();
        var me = this,
            loader;

        function success() {
            CMDBuildUI.util.Notifier.showSuccessMessage('Configuration successful');
            CMDBuildUI.util.Utilities.removeLoadMask(loader);
            me.redirectTo('login');
            window.location.reload();
        }

        function failure(error) {
            if (!error.responseText) {
                error = {
                    responseText: error
                };
            }
            CMDBuildUI.util.Ajax.showMessages(error, {
                hideErrorNotification: false
            });
            CMDBuildUI.util.Utilities.removeLoadMask(loader);
        }

        if (form.isValid()) {
            var values = btn.lookupViewModel().get('values');
            var dbname,
                uploadFile = false;
            // get dbname of upload based on configtype
            switch (values.configtype) {
                case 'empty':
                    dbname = 'empty.dump.xz';
                    break;
                case 'demo':
                    dbname = 'demo.dump.xz';
                    break;
                case 'upload':
                    uploadFile = true;
                    break;
            }

            // prepare parameters
            var params = {
                'db.url': this.getConnectionUrl(values.dbhost, values.dbport, values.dbname),
                'db.username': values.dbusername,
                'db.password': values.dbpassword,
                'db.admin.username': values.dbadminusername,
                'db.admin.password': values.dbadminpassword,
                'db.source': dbname
            };

            // prepare request configuration
            var url = CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Common.getSetDBConfigUrl();
            var method = 'POST';
            CMDBuildUI.util.Ajax.setActionId('configuredb');

            loader = CMDBuildUI.util.Utilities.addLoadMask(form);

            if (uploadFile) {
                var input = form.lookupReference('upload').extractFileInput().files[0];

                // init formData
                var formData = new FormData();

                // append attachment json data
                var jsonData = Ext.encode(params);
                var fieldName = 'configpayload';
                try {
                    formData.append(
                        fieldName,
                        new Blob([jsonData], {
                            type: 'application/json'
                        })
                    );
                } catch (err) {
                    CMDBuildUI.util.Logger.log(
                        "Unable to create attachment Blob FormData entry with type 'application/json', fallback to 'text/plain': " +
                            err,
                        CMDBuildUI.util.Logger.levels.error
                    );
                    // metadata as 'text/plain' (format compatible with older webviews)
                    formData.append(fieldName, jsonData);
                }

                // upload file
                CMDBuildUI.util.File.upload(method, formData, input, url, {
                    timeout: 0,
                    success: success,
                    failure: failure
                });
            } else {
                Ext.Ajax.request({
                    url: url,
                    timeout: 0,
                    method: method,
                    jsonData: params
                }).then(success, failure);
            }
        }
    },

    privates: {
        /**
         * @return {String} Connection url
         */
        getConnectionUrl: function (dbhost, dbport, dbname) {
            return Ext.String.format('jdbc:postgresql://{0}:{1}/{2}', dbhost, dbport, dbname);
        }
    }
});
