/**
 * @file CMDBuildUI.util.helper.AttachmentsHelper
 * @module CMDBuildUI.util.helper.AttachmentsHelper
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.helper.AttachmentsHelper', {
    singleton: true,

    /**
     * @property {String} genericPreview
     * The src for a generic preview image.
     */
    genericPreview:
        'data:image/png;base64,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',

    /**
     * Get DMS Category type name for given object type.
     *
     * @param {String} objectTypeName The class/process name
     * @param {String} [objectType] Object type. One of {@link module:CMDBuildUI.util.helper.ModelHelper#objecttypes CMDBuildUI.util.helper.ModelHelper.objecttypes} properties.
     *
     * @returns {String} DMS Category type name
     *
     * @example
     * var categoryTypeName = CMDBuildUI.util.helper.AttachmentsHelper.getCategoryTypeName(
     *      CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
     *      "Employee"
     * );
     */
    getCategoryTypeName: function (objectTypeName, objectType) {
        // get object definition
        const objdefinition = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType);

        // returns object type catgory if defined
        const cagegoryType = objdefinition ? objdefinition.get('dmsCategory') : null;
        if (!Ext.isEmpty(cagegoryType)) {
            return cagegoryType;
        }

        // returns default category
        return CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.category);
    },

    /**
     * Get DMS Category type for given object type.
     *
     * @param {String} objectTypeName The class/process name
     * @param {String} [objectType] Object type. One of {@link module:CMDBuildUI.util.helper.ModelHelper#objecttypes CMDBuildUI.util.helper.ModelHelper.objecttypes} properties.
     *
     * @returns {CMDBuildUI.model.dms.DMSCategoryType} DMS Category type
     *
     * @example
     * var categoryType = CMDBuildUI.util.helper.AttachmentsHelper.getCategoryType(
     *      CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
     *      "Employee"
     * );
     */
    getCategoryType: function (objectTypeName, objectType) {
        // get category type name
        const categoryTypeName = CMDBuildUI.util.helper.AttachmentsHelper.getCategoryTypeName(
            objectTypeName,
            objectType
        );
        return CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(categoryTypeName);
    },

    /**
     * Get the preview image for an attachment. If document has not a preview, this method will
     * returns category image of DMS model's one.
     *
     * @param {String} objectType Target object type. One of {@link module:CMDBuildUI.util.helper.ModelHelper#objecttypes CMDBuildUI.util.helper.ModelHelper.objecttypes} properties.
     * @param {String} objectTypeName Target class/process name.
     * @param {Number|String} objectId Target object id.
     * @param {String} documentId Document id.
     * @param {Number|String} documentCategory Document dms category id.
     *
     * @returns {Ext.promise.Promise<String>} The resolve method has as arugument the image url.
     *
     * @example
     * CMDBuildUI.util.helper.AttachmentsHelper.getPreview(
     *     objectType,
     *     objectTypeName,
     *     objectId,
     *     documentId,
     *     documentCategory
     * ).then(function(src) {
     *     if (src) {
     *         var img = document.getElementById(previewid);
     *         img.src = src;
     * });
     */
    getPreview: function (objectType, objectTypeName, objectId, documentId, documentCategory, recordUrl) {
        const deferred = new Ext.Deferred();
        var urlFn;
        var urlRecord = recordUrl;
        if (!recordUrl) {
            switch (objectType) {
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                    urlFn = CMDBuildUI.util.api.Classes.getAttachments;
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                    urlFn = CMDBuildUI.util.api.Processes.getAttachmentsUrl;
                    break;
            }
            if (urlFn) {
                urlRecord = CMDBuildUI.util.Config.baseUrl + urlFn(objectTypeName, objectId) + '/' + documentId;
            }
        }
        Ext.Ajax.request({
            url: urlRecord + '/preview',
            method: 'GET',
            success: function (response, opts) {
                const responseJson = Ext.decode(response.responseText);
                if (responseJson.data.hasPreview) {
                    deferred.resolve(responseJson.data.dataUrl);
                } else {
                    if (documentCategory) {
                        const object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType);
                        if (object) {
                            const dmsCategoryTypeName =
                                object.get('dmsCategory') ||
                                CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.category);
                            const dmsCategoryType =
                                CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(dmsCategoryTypeName);

                            dmsCategoryType.getCategoryValues().then(function (categoryValues) {
                                const dmsCategory = categoryValues.findRecord('code', documentCategory);

                                if (dmsCategory.get('icon_type') === CMDBuildUI.model.dms.DMSCategory.icontypes.image) {
                                    deferred.resolve(dmsCategory.get('icon_image'));
                                } else {
                                    const dmsModel = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(
                                        dmsCategory.get('modelClass')
                                    );
                                    if (dmsModel.get('_icon')) {
                                        deferred.resolve(dmsModel.get('_iconPath'));
                                    } else {
                                        deferred.resolve(CMDBuildUI.util.helper.AttachmentsHelper.genericPreview);
                                    }
                                }
                            });
                        }
                    } else {
                        deferred.resolve(CMDBuildUI.util.helper.AttachmentsHelper.genericPreview);
                    }
                }
            },
            scope: this
        });
        return deferred.promise;
    },

    /**
     * Render attachment details in modal by an element,
     *
     * @param {HTMLComponent} el HTML element.
     *
     * @example
     * <i onclick="CMDBuildUI.util.helper.AttachmentsHelper.viewFileFieldDetails(this); return false;"></i>
     */
    viewFileFieldDetails: function (el) {
        const parentNode = el.parentNode;
        const objectType = parentNode.getAttribute('data-objecttype');
        const objectTypeName = parentNode.getAttribute('data-objecttypename');
        const objectId = parentNode.getAttribute('data-objectid');
        const documentCategory = parentNode.getAttribute('data-documentcategory');
        const documentId = parentNode.getAttribute('data-documentid');

        const object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType);
        if (object) {
            const dmsCategoryTypeName =
                object.get('dmsCategory') ||
                CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.category);
            const dmsCategoryType = CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(dmsCategoryTypeName);

            dmsCategoryType.getCategoryValues().then(function (categoryValues) {
                const dmsModelName = categoryValues.findRecord('code', documentCategory).get('modelClass');

                CMDBuildUI.util.helper.ModelHelper.getModel(
                    CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                    dmsModelName
                ).then(function (model) {
                    const attachment = Ext.create(model.getName(), {
                        _id: documentId
                    });
                    const url =
                        objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.klass
                            ? CMDBuildUI.util.api.Classes.getAttachments(objectTypeName, objectId)
                            : CMDBuildUI.util.api.Processes.getAttachmentsUrl(objectTypeName, objectId);
                    attachment.getProxy().setUrl(url);
                    attachment.load({
                        success: function (record, operation) {
                            const window = Ext.create('Ext.menu.Menu', {
                                items: [
                                    {
                                        xtype: 'dms-attachment-view',
                                        viewModel: {
                                            data: {
                                                DMSCategoryTypeName: '',
                                                DMSModelClassName: dmsModelName,
                                                record: record
                                            }
                                        }
                                    }
                                ],
                                listeners: {
                                    hide: function (menu, eOpts) {
                                        menu.destroy();
                                    }
                                },
                                floating: true,
                                alignTarget: el,
                                width: parentNode.offsetWidth * 0.75
                            });
                            window.show();
                        }
                    });
                });
            });
        }
    },

    /**
     * Open file field preview.
     *
     * @param {HTMLComponent} el HTML element.
     *
     * @example
     * <span onclick="CMDBuildUI.util.helper.AttachmentsHelper.openFileFieldPreview(this); return false;"></span>
     */
    openFileFieldPreview: function (el) {
        const parentNode = el.parentNode;
        const objectType = parentNode.getAttribute('data-objecttype');
        const objectTypeName = parentNode.getAttribute('data-objecttypename');
        const objectId = parentNode.getAttribute('data-objectid');
        const documentId = parentNode.getAttribute('data-documentid');
        const fileName = parentNode.getAttribute('data-filename');
        const mimeType = parentNode.getAttribute('data-mimetype');
        var attachmentUrlFn;
        var attachmentUrl;

        switch (objectType) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                attachmentUrlFn = CMDBuildUI.util.api.Classes.getAttachments;
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                attachmentUrlFn = CMDBuildUI.util.api.Processes.getAttachmentsUrl;
                break;
        }
        if (attachmentUrlFn) {
            attachmentUrl =
                CMDBuildUI.util.Config.baseUrl + attachmentUrlFn(objectTypeName, objectId) + '/' + documentId;
            this.viewOrDownloadAttachment(attachmentUrl, mimeType, fileName);
        }
    },

    /**
     * View or download the attachment.
     *
     * @param {String} attachmentUrl The full url of the attachemnt.
     * @param {String} mimeType The file mime-type.
     * @param {String} fileName The file name.
     *
     *
     * @example
     * CMDBuildUI.util.helper.AttachmentsHelper.viewOrDownloadAttachment(attachmentUrl, mimeType, fileName);
     */
    viewOrDownloadAttachment: function (attachmentUrl, mimeType, fileName) {
        const fileUrl =
            attachmentUrl +
            '/' +
            encodeURI(fileName)
                .match(/(?:"[^"]*"|^[^"]*$)/)[0]
                .replace(/"/g, '')
                .replace(/\+/g, ' ');

        function openFile(type) {
            const popup = CMDBuildUI.util.Utilities.openPopup(
                null,
                Ext.String.format('{0} - {1}', CMDBuildUI.locales.Locales.attachments.fileview, fileName),
                {
                    xtype: 'dms-file-view',
                    fileUrl: fileUrl,
                    fileType: type,
                    fileName: fileName,
                    closePopup: function () {
                        popup.close();
                    }
                }
            );
        }
        if (Ext.String.startsWith(mimeType, 'image/', true)) {
            openFile(CMDBuildUI.view.dms.file.View.types.image);
        } else if (Ext.String.startsWith(mimeType, 'text/', true)) {
            openFile(CMDBuildUI.view.dms.file.View.types.text);
        } else if (mimeType === 'application/pdf') {
            openFile(CMDBuildUI.view.dms.file.View.types.pdf);
        } else {
            CMDBuildUI.util.File.download(fileUrl, fileName, undefined, undefined, {
                skipUrlEncode: true
            });
        }
    },

    /**
     * Get the extensions allowed
     *
     * @param {String} DMSCategoryTypeName
     * @param {Number} DMSCategoryValue
     * @param {Ext.data.model} DMSModelClass
     * @returns {String[]} The array of allowed extensions.
     * @note Empty array means all extension are allowed
     */
    getAllowedExtensions: function (DMSCategoryTypeName, DMSCategoryValue, DMSModelClass) {
        var allowedExtensionsArray = [];
        if (DMSCategoryTypeName && DMSCategoryValue && DMSModelClass) {
            const lk = CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(DMSCategoryTypeName);

            const lookupValuesStore = lk.values();
            //we suppose that the lookupValues for the lookupType are already loaded.
            //If not make this function asincronous or load lookupValues febore calling this function

            const lookupValue = lookupValuesStore.getById(DMSCategoryValue);
            if (lookupValue) {
                var allowedExtensions = lookupValue.get('allowedExtensions');
                if (Ext.isEmpty(allowedExtensions)) {
                    const modelName = DMSModelClass.objectTypeName;
                    const DMSClass = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(modelName);

                    allowedExtensions = DMSClass.get('allowedExtensions');

                    if (Ext.isEmpty(allowedExtensions)) {
                        allowedExtensions = CMDBuildUI.util.helper.Configurations.get(
                            CMDBuildUI.model.Configuration.dms.allowedextensions
                        );
                    }
                    if (allowedExtensions == '*' || Ext.isEmpty(allowedExtensions)) {
                        return allowedExtensionsArray;
                    } else {
                        allowedExtensionsArray = allowedExtensions.split(',');
                        return allowedExtensionsArray;
                    }
                } else if (allowedExtensions == '*') {
                    return allowedExtensionsArray;
                } else {
                    allowedExtensionsArray = allowedExtensions.split(',');
                    return allowedExtensionsArray;
                }
            } else {
                CMDBuildUI.util.Notifier.showErrorMessage(
                    Ext.String.format(
                        'DMS lookupValue {0} not found for DMS lookupType {1}',
                        DMSCategoryTypeName,
                        DMSCategoryValue
                    )
                );
            }
        }
    }
});
