Ext.define('CMDBuildUI.view.widgets.customform.ExportController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-customform-export',

    control: {
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#exportBtn': {
            click: 'onExportBtnClick'
        }
    },

    /**
     *
     * @param {Ext.button.Button} btn
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (btn, eOpts) {
        this.getView().closePopup();
    },

    /**
     *
     * @param {Ext.button.Button} btn
     * @param {Object} eOpts
     */
    onExportBtnClick: function (btn, eOpts) {
        var vm = btn.lookupViewModel(),
            view = this.getView(),
            attrs = view.lookupReference('attributesGrid'),
            config = {
                fileFormat: vm.get('format'),
                csv_separator: vm.get('separator'),
                charset: 'UTF8',
                attributes: []
            },
            data = [];

        // add loader
        var loader = CMDBuildUI.util.Utilities.addLoadMask(view);

        attrs.getSelection().forEach(function (attr) {
            config.attributes.push(attr.getData());
        });

        view.getGridStore()
            .getRange()
            .forEach(function (item) {
                data.push(item.getData());
            });

        var filename = Ext.String.trim(vm.get('filename'));
        if (!Ext.String.endsWith(filename, config.fileFormat, true)) {
            filename += '.' + config.fileFormat;
        }

        this.getExportBlob(config, data)
            .then(function (response) {
                var blob = response.response;
                response.name = filename;

                if (typeof window.navigator.msSaveBlob !== 'undefined') {
                    // IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. These URLs will no longer resolve as the data backing the URL has been freed."
                    window.navigator.msSaveBlob(blob, filename);
                } else {
                    var URL = window.URL || window.webkitURL;
                    var downloadUrl = URL.createObjectURL(blob);

                    if (filename) {
                        // use HTML5 a[download] attribute to specify filename
                        var a = document.createElement('a');
                        // safari doesn't support this yet
                        if (typeof a.download === 'undefined') {
                            window.location = downloadUrl;
                        } else {
                            a.href = downloadUrl;
                            a.download = filename;
                            document.body.appendChild(a);
                            a.click();
                            // remove the link from the DOM
                            a.parentElement.removeChild(a);
                        }
                    } else {
                        window.location = downloadUrl;
                    }

                    setTimeout(function () {
                        URL.revokeObjectURL(downloadUrl); // cleanup
                        view.closePopup();
                    }, 100);
                }
            })
            .otherwise(function (response) {
                CMDBuildUI.util.Utilities.removeLoadMask(loader);
            });
    },

    privates: {
        getExportBlob: function (config, data) {
            var deferred = new Ext.Deferred(),
                xmlhttp = new XMLHttpRequest(),
                formData = new FormData();

            // use cookie authentication
            xmlhttp.withCredentials = true;

            // Response handler
            xmlhttp.onreadystatechange = function (e) {
                if (this.readyState === 4) {
                    if (Ext.Array.indexOf([200, 201, 204], parseInt(this.status, 10)) !== -1) {
                        deferred.resolve(this);
                    } else {
                        deferred.reject(this);
                    }
                }
            };

            // Error handler
            xmlhttp.upload.onerror = function () {
                deferred.reject(this);
            };

            // append config part
            formData.append(
                'config',
                new Blob([Ext.encode(config)], {
                    type: 'application/json'
                })
            );

            // append data part
            formData.append(
                'data',
                new Blob([Ext.encode(data)], {
                    type: 'application/json'
                })
            );

            // open form with file using XMLHttpRequest POST request
            const url = CMDBuildUI.util.Config.baseUrl + '/etl/templates/inline/export';
            xmlhttp.open(
                'POST',
                CMDBuildUI.util.Utilities.shouldAddAdministrationPrefix(url)
                    ? CMDBuildUI.util.Utilities.addAdministrationPrefix(url)
                    : url,
                true
            );

            // set response as binary
            xmlhttp.responseType = 'blob';

            // set headers
            xmlhttp.setRequestHeader('CMDBuild-ActionId', CMDBuildUI.util.Ajax.getActionId());
            xmlhttp.setRequestHeader('CMDBuild-RequestId', CMDBuildUI.util.Utilities.generateUUID());

            // finally send
            xmlhttp.send(formData);

            return deferred.promise;
        }
    }
});
