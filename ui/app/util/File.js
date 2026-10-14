/**
 * @file CMDBuildUI.util.File
 * @module CMDBuildUI.util.File
 * @author PAT srl
 * @access public
 */

Ext.define('CMDBuildUI.util.File', {
    singleton: true,

    /**
     * Upload a file
     *
     * @deprecated
     * Use {@link CMDBuildUI.util.File#uploadFileWithMetadata CMDBuildUI.util.File.uploadFileWithMetadata()} instead.
     *
     * @param {String} method HTTP method. One of "POST" (for creation) or "PUT" (for edit).
     * @param {FormData} formData
     * @param {DOM} inputFile
     * @param {String} url Url for save the attachemnt.
     * @param {Function|Object} callback
     * @param {Function} callback.success
     * @param {Function} callback.failure
     * @param {Function} callback.callback
     * @param {Object} callback.scope
     * @param {Object} params
     *
     * @example
     * CMDBuildUI.util.File.upload("POST", formData, inputFile, url, {
     *      success: function () {
     *          // success callback
     *      },
     *      failure: function (error) {
     *          // failure callback
     *      }
     * });
     */
    upload: function (method, formData, inputFile, url, callback, params) {
        const xmlhttp = new XMLHttpRequest();
        xmlhttp.withCredentials = true;

        // TODO: show progress bar
        // Uploading progress handler
        // xmlhttp.upload.onprogress = function (e) {
        //     if (e.lengthComputable) {
        //         var percentComplete = (e.loaded / e.total) * 100;
        //         Ext.Viewport.cmdbMask(percentComplete.toFixed(0) + '%');
        //     }
        // };

        // Response handler
        xmlhttp.onreadystatechange = function (e) {
            if (this.readyState === 4) {
                if (Ext.Array.indexOf([200, 201, 204], parseInt(this.status, 10)) !== -1) {
                    if (Ext.isFunction(callback)) {
                        Ext.callback(callback, null, [true, this.responseText, e]);
                    } else if (Ext.isObject(callback)) {
                        if (callback.success) {
                            Ext.callback(callback.success, callback.scope, [this.responseText, e]);
                        }
                        if (callback.callback) {
                            Ext.callback(callback.callback, callback.scope, [true, this.responseText, e]);
                        }
                    }
                } else {
                    if (Ext.isFunction(callback)) {
                        Ext.callback(callback, null, [false, this.responseText, e]);
                    } else if (Ext.isObject(callback)) {
                        if (callback.failure) {
                            Ext.callback(callback.failure, callback.scope, [this.responseText, e]);
                        }
                        if (callback.callback) {
                            Ext.callback(callback.callback, callback.scope, [false, this.responseText, e]);
                        }
                    }
                }
            }
        };

        // Error handler
        xmlhttp.upload.onerror = function () {
            if (Ext.isFunction(callback)) {
                Ext.callback(callback, null, [false, this.responseText]);
            } else if (Ext.isObject(callback)) {
                if (callback.failure) {
                    Ext.callback(callback.failure, callback.scope, [this.responseText]);
                }
                if (callback.callback) {
                    Ext.callback(callback.callback, callback.scope, [false, this.responseText]);
                }
            }
        };

        // update formData
        formData.append('file', inputFile);

        if (!Ext.Object.isEmpty(params)) {
            url += '?' + Ext.Object.toQueryString(params);
        }

        // open form with file using XMLHttpRequest POST request
        xmlhttp.open(
            method,
            CMDBuildUI.util.Utilities.shouldAddAdministrationPrefix(url)
                ? CMDBuildUI.util.Utilities.addAdministrationPrefix(url)
                : url
        );

        // set headers
        xmlhttp.setRequestHeader('CMDBuild-ActionId', CMDBuildUI.util.Ajax.getActionId());
        xmlhttp.setRequestHeader('CMDBuild-RequestId', CMDBuildUI.util.Utilities.generateUUID());

        // finally send
        xmlhttp.send(formData);
    },

    /**
     * Download a file
     *
     * @param {String} url
     * @param {String} [extension] extension || filename
     * @param {Boolean} [hideLoader]
     * @param {Object} [params] Request parameters
     * @param {Object} [config] Configuration
     * @param {Boolean} [config.skipUrlEncode=false] Skip URL encode
     * @param {Ext.Component} [config.applyMaskTo=undefined] component to mask
     *
     * @returns {Ext.promise.Promise<Boolean>}
     *
     * @example
     * var url = Ext.String.format('{0}/downloads/{1}/{2}', CMDBuildUI.util.Config.baseUrl, fileId, fileName);
     * CMDBuildUI.util.File.download(url, "pdf");
     */
    download: function (url, extension, hideLoader, params, config) {
        const deferred = new Ext.Deferred();
        let filename;

        config = config || { skipUrlEncode: false };

        if (!hideLoader) {
            CMDBuildUI.util.Utilities.showLoader(true, config.applyMaskTo);
        }

        if (extension) {
            const isFullname = /\.[0-9a-z]+$/i.exec(extension);
            if (isFullname && isFullname.length) {
                filename = extension;
                extension = isFullname[0].replace('.', '');
            }
        }

        Ext.Ajax.request({
            url: config.skipUrlEncode ? url : encodeURI(url),
            method: 'GET',
            binary: true,
            params: params,
            timeout: CMDBuildUI.util.Config.ajaxTimeout,
            failure: function (error) {
                CMDBuildUI.util.Utilities.showLoader(false, config.applyMaskTo);
                if (error.responseBytes) {
                    const responseText = String.fromCharCode.apply(null, error.responseBytes);
                    if (responseText) {
                        CMDBuildUI.util.Ajax.showMessages({ responseText: responseText }, {});
                    }
                }
                deferred.resolve(false);
            },
            success: function (response) {
                let _filename;
                const requestId =
                    response.request.headers['CMDBuild-RequestId'] || CMDBuildUI.util.Utilities.generateUUID();
                try {
                    // normally this should work
                    let contentDisposition = response.request.xhr.getResponseHeader('Content-Disposition');
                    if (!contentDisposition) {
                        throw new Error('unknow Content-Disposition');
                    }
                    if (!config.skipUrlEncode) {
                        contentDisposition = decodeURI(contentDisposition);
                    }
                    _filename = decodeURI(contentDisposition)
                        .match(/(?:"[^"]*"|^[^"]*$)/)[0]
                        .replace(/"/g, '')
                        .replace(/\+/g, ' ');
                } catch (error) {
                    // in case of CORS error
                    const _extension =
                        extension || /[^/]+$/.exec(response.request.xhr.getResponseHeader('Content-Type'))[0];
                    _filename = filename || Ext.String.format('{0}.{1}', requestId, _extension);
                    if (!config.skipUrlEncode) {
                        _filename = decodeURI(_filename);
                    }
                    CMDBuildUI.util.Logger.log(
                        'Unable to read Content-Disposition header. This may depend by CORS. Filename will be generated.',
                        CMDBuildUI.util.Logger.levels.info
                    );
                }

                const blob = new Blob([response.responseBytes], {
                    type: response.request.xhr.getResponseHeader('Content-Type')
                });

                if (typeof window.navigator.msSaveBlob !== 'undefined') {
                    // IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. These URLs will no longer resolve as the data backing the URL has been freed."
                    window.navigator.msSaveBlob(blob, _filename);
                    if (!hideLoader) {
                        CMDBuildUI.util.Utilities.showLoader(false, config.applyMaskTo);
                    }
                    deferred.resolve(true);
                } else {
                    const URL = window.URL || window.webkitURL;
                    const downloadUrl = URL.createObjectURL(blob);

                    if (_filename) {
                        // use HTML5 a[download] attribute to specify filename
                        const a = document.createElement('a');
                        // safari doesn't support this yet
                        if (typeof a.download === 'undefined') {
                            window.location = downloadUrl;
                        } else {
                            a.href = downloadUrl;
                            a.download = _filename;
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
                        if (!hideLoader) {
                            CMDBuildUI.util.Utilities.showLoader(false, config.applyMaskTo);
                        }
                        deferred.resolve(true);
                    }, 100);
                }
            }
        });
        return deferred.promise;
    },

    /**
     * Download a file from JSON data
     *
     * @param {Object[]} jsonData Array of objects representing the data rows
     * @param {String} extension File extension (csv | xlsx)
     *
     * @returns {void}
     *
     * @example
     * const jsonData = [
     *     { "period": "2025-02", "duration": 392, "id": "extModel947-1" },
     *     { "period": "2025-03", "duration": 210, "id": "extModel947-2" },
     *     { "period": "2025-05", "duration": 549, "id": "extModel947-3" }
     * ];
     * CMDBuildUI.util.File.downloadFromJSON(jsonData, 'csv');
     */
    downloadFromJSON: function (jsonData, extension) {
        if (Ext.isEmpty(jsonData)) {
            CMDBuildUI.util.Notifier.showMessage(CMDBuildUI.locales.Locales.errors.nodatatoexport, {
                title: CMDBuildUI.locales.Locales.notifier.error,
                icon: CMDBuildUI.util.Notifier.icons.error
            });
            return;
        }

        switch (extension.toLowerCase()) {
            case 'csv':
                this._downloadCSVFromJSON(jsonData);
                break;
            default:
                console.error('Unsupported export format: ' + extension);
                break;
        }
    },

    /**
     * Convert JSON data to CSV and trigger download in the browser.
     *
     * @param {Object[]} jsonData Array of objects representing the rows to export.
     *
     * @private
     *
     */
    _downloadCSVFromJSON: function (jsonData) {
        const keys = Object.keys(jsonData[0]);
        const csvRows = [];
        csvRows.push(keys.join(','));

        for (let j = 0; j < jsonData.length; j++) {
            const row = jsonData[j];
            const values = [];

            for (let k = 0; k < keys.length; k++) {
                const key = keys[k];
                let cell = row[key] !== null && row[key] !== undefined ? row[key].toString() : '';
                cell = cell.replace(/"/g, '""');
                values.push('"' + cell + '"');
            }
            csvRows.push(values.join(','));
        }

        const csvString = csvRows.join('\n');

        const blob = new Blob([csvString], { type: 'text/csv;charset=utf-8;' });
        const url = URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = url;
        a.download = 'chart-data.csv';
        document.body.appendChild(a);
        a.click();
        a.remove();

        URL.revokeObjectURL(url);
    },

    /**
     * Upload file with metadata
     *
     * @param {String} method
     * @param {String} url
     * @param {File} file
     * @param {Object} metadata
     * @param {Object} config
     * @param {String} config.filePartName File part name
     * @param {String} config.metadataPartName Metadata part name
     * @param {Object} config.extraParams Params sent in request as query string
     *
     * @returns {Ext.promise.Promise} The promise has as parameter the response of the server.
     *
     * @example
     * CMDBuildUI.util.File.uploadFileWithMetadata(
     *      "POST",
     *      CMDBuildUI.util.Config.baseUrl  + "/etl/templates/inline/import",
     *      file.get('file'),
     *      config, {
     *          metadataPartName: 'config'
     *      }
     * ).then(function (response) {
     *      // success
     * }).otherwise(function (error) {
     *      // failure
     * });
     */
    uploadFileWithMetadata: function (method, url, file, metadata, config) {
        const deferred = new Ext.Deferred();
        const xmlhttp = new XMLHttpRequest();
        const formData = new FormData();

        // use cookie authentication
        xmlhttp.withCredentials = true;

        config = Ext.applyIf(config || {}, { filePartName: 'file', metadataPartName: 'attachment' });

        // Response handler
        xmlhttp.onreadystatechange = function (e) {
            if (this.readyState === 4) {
                if (Ext.Array.indexOf([200, 201, 204], parseInt(this.status, 10)) !== -1) {
                    deferred.resolve(JSON.parse(this.responseText).data);
                } else {
                    CMDBuildUI.util.Ajax.showMessages(this, { hideErrorNotification: false });
                    deferred.reject(Ext.JSON.encode(this.responseText));
                }
            }
        };

        // Error handler
        xmlhttp.upload.onerror = function () {
            deferred.reject(JSON.parse(this.responseText));
        };

        // append file part
        if (file) {
            formData.append(config.filePartName, file);
        }

        // append metadata part
        if (!Ext.isEmpty(metadata)) {
            formData.append(config.metadataPartName, new Blob([Ext.encode(metadata)], { type: 'application/json' }));
        }

        // add extra params
        if (!Ext.Object.isEmpty(config.extraParams)) {
            url += '?' + Ext.Object.toQueryString(config.extraParams);
        }

        // open form with file using XMLHttpRequest POST request
        xmlhttp.open(
            method,
            CMDBuildUI.util.Utilities.shouldAddAdministrationPrefix(url)
                ? CMDBuildUI.util.Utilities.addAdministrationPrefix(url)
                : url,
            true
        );

        // set headers
        xmlhttp.setRequestHeader('CMDBuild-ActionId', CMDBuildUI.util.Ajax.getActionId());
        xmlhttp.setRequestHeader('CMDBuild-RequestId', CMDBuildUI.util.Utilities.generateUUID());

        // finally send
        xmlhttp.send(formData);

        return deferred.promise;
    },

    /**
     * Download a file by base64 content.
     *
     * @param {String} data Base64 data
     * @param {String} strFileName File name
     * @param {String} [strMimeType] File mime type
     *
     * @returns {Blob | Boolean}
     *
     * @example
     * CMDBuildUI.util.File.downloadBase64(
     *      chart.getImage().data,
     *      "My chart"
     * );
     */
    downloadBase64: function (data, strFileName, strMimeType) {
        const self = window; // this script is only for browsers anyway...
        const defaultMime = 'application/octet-stream'; // this default mime also triggers iframe downloads
        let payload = data;
        const url = !strFileName && !strMimeType && payload;
        const anchor = document.createElement('a');
        const toString = function (a) {
            return String(a);
        };
        let mimeType = strMimeType || defaultMime;
        let myBlob = self.Blob || self.MozBlob || self.WebKitBlob || toString;
        let fileName = strFileName || 'download';
        myBlob = myBlob.call ? myBlob.bind(self) : Blob;

        if (String(this) === 'true') {
            //reverse arguments, allowing download.bind(true, "text/xml", "export.xml") to act as a callback
            payload = [payload, mimeType];
            mimeType = payload[0];
            payload = payload[1];
        }

        if (url && url.length < 2048) {
            // if no filename and no mime, assume a url was passed as the only argument
            fileName = url.split('/').pop().split('?')[0];
            anchor.href = url; // assign href prop to temp anchor
            if (anchor.href.indexOf(url) !== -1) {
                // if the browser determines that it's a potentially valid url path:
                const ajax = new XMLHttpRequest();
                ajax.open('GET', url, true);
                ajax.responseType = 'blob';
                ajax.onload = function (e) {
                    download(e.target.response, fileName, defaultMime);
                };
                setTimeout(function () {
                    ajax.send();
                }, 0); // allows setting custom ajax headers using the return:
                return ajax;
            } // end if valid url?
        } // end if url?

        //go ahead and download dataURLs right away
        if (/^data\:[\w+\-]+\/[\w+\-]+[,;]/.test(payload)) {
            if (payload.length > 1024 * 1024 * 1.999 && myBlob !== toString) {
                payload = dataUrlToBlob(payload);
                mimeType = payload.type || defaultMime;
            } else {
                return navigator.msSaveBlob // IE10 can't do a[download], only Blobs:
                    ? navigator.msSaveBlob(dataUrlToBlob(payload), fileName)
                    : saver(payload); // everyone else can save dataURLs un-processed
            }
        } //end if dataURL passed?

        const blob = payload instanceof myBlob ? payload : new myBlob([payload], { type: mimeType });

        function dataUrlToBlob(strUrl) {
            const parts = strUrl.split(/[:;,]/);
            const type = parts[1];
            const decoder = parts[2] == 'base64' ? atob : decodeURIComponent;
            const binData = decoder(parts.pop());
            const mx = binData.length;
            let i = 0;
            let uiArr = new Uint8Array(mx);

            for (i; i < mx; ++i) uiArr[i] = binData.charCodeAt(i);

            return new myBlob([uiArr], { type: type });
        }

        function saver(url, winMode) {
            if ('download' in anchor) {
                //html5 A[download]
                anchor.href = url;
                anchor.setAttribute('download', fileName);
                anchor.className = 'download-js-link';
                anchor.innerHTML = 'downloading...';
                anchor.style.display = 'none';
                document.body.appendChild(anchor);
                setTimeout(function () {
                    anchor.click();
                    document.body.removeChild(anchor);
                    if (winMode === true) {
                        setTimeout(function () {
                            self.URL.revokeObjectURL(anchor.href);
                        }, 250);
                    }
                }, 66);
                return true;
            }

            // handle non-a[download] safari as best we can:
            if (/(Version)\/(\d+)\.(\d+)(?:\.(\d+))?.*Safari\//.test(navigator.userAgent)) {
                url = url.replace(/^data:([\w\/\-\+]+)/, defaultMime);
                if (!window.open(url)) {
                    // popup blocked, offer direct download:
                    if (
                        confirm(
                            'Displaying New Document\n\nUse Save As... to download, then click back to return to this page.'
                        )
                    ) {
                        location.href = url;
                    }
                }
                return true;
            }

            //do iframe dataURL download (old ch+FF):
            const f = document.createElement('iframe');
            document.body.appendChild(f);

            if (!winMode) {
                // force a mime that will download:
                url = 'data:' + url.replace(/^data:([\w\/\-\+]+)/, defaultMime);
            }
            f.src = url;
            setTimeout(function () {
                document.body.removeChild(f);
            }, 333);
        } //end saver

        if (navigator.msSaveBlob) {
            // IE10+ : (has Blob, but not a[download] or URL)
            return navigator.msSaveBlob(blob, fileName);
        }

        if (self.URL) {
            // simple fast and modern way using Blob and URL:
            saver(self.URL.createObjectURL(blob), true);
        } else {
            // handle non-Blob()+non-URL browsers:
            if (typeof blob === 'string' || blob.constructor === toString) {
                try {
                    return saver('data:' + mimeType + ';base64,' + self.btoa(blob));
                } catch (y) {
                    return saver('data:' + mimeType + ',' + encodeURIComponent(blob));
                }
            }

            // Blob but not URL support:
            const reader = new FileReader();
            reader.onload = function (e) {
                saver(this.result);
            };
            reader.readAsDataURL(blob);
        }
        return true;
    }
});
