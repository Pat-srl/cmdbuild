Ext.define('CMDBuildUI.view.dms.attachment.EditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.dms-attachment-edit',

    data: {
        objectType: undefined,
        objectTypeName: undefined,
        objectId: undefined,
        attachmentId: null,
        DMSCategoryTypeName: undefined,
        DMSCategoryValue: undefined,
        DMSModelClassName: undefined,
        DMSModelClass: undefined,
        DMSWidgets: undefined,
        DMSClass: undefined,
        theObject: undefined
    },

    formulas: {
        DMSCalculations: {
            bind: {
                DMSCategoryTypeName: '{DMSCategoryTypeName}',
                DMSCategoryTypeValue: '{DMSCategoryValue}'
            },
            get: function (data) {
                if (data.DMSCategoryTypeName && data.DMSCategoryTypeValue) {
                    const DMSCategoryType = CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(data.DMSCategoryTypeName);

                    DMSCategoryType.getCategoryValues().then(function (categoryValues) {
                        const r = categoryValues.findRecord('_id', data.DMSCategoryTypeValue);

                        if (r) {
                            const DMSModelClassName = r.get('modelClass') || 'BaseDocument';
                            this.set("DMSModelClassName", DMSModelClassName);
                            this.set("DMSClass", CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(DMSModelClassName));

                            CMDBuildUI.util.helper.ModelHelper.getModel(
                                CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                                DMSModelClassName
                            ).then(function (model) {
                                this.set("DMSModelClass", model);
                            }, Ext.emptyFn, Ext.emptyFn, this);

                        }

                    }, Ext.emptyFn, Ext.emptyFn, this);
                }
            }
        },

        updateTheObject: {
            bind: {
                objectTypeName: '{objectTypeName}',
                objectId: '{objectId}',
                objectType: '{objectType}',
                DMSModelClass: '{DMSModelClass}',
                attachmentId: '{attachmentId}'
            },
            get: function (data) {
                if (!this.get("theObject") && data.objectTypeName && data.objectId && data.objectType && data.DMSModelClass && data.attachmentId) {

                    var proxyUrl;
                    switch (data.objectType) {
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                            proxyUrl = CMDBuildUI.util.api.Classes.getAttachments(data.objectTypeName, data.objectId);
                            break;
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar:
                            proxyUrl = CMDBuildUI.util.api.Calendar.getAttachmentsUrl(data.objectId);
                            break;
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                            proxyUrl = CMDBuildUI.util.api.Processes.getAttachmentsUrl(data.objectTypeName, data.objectId);
                            break;
                        default:
                            CMDBuildUI.util.Notifier.showErrorMessage(Ext.String.format('No attachments url for {0} object', data.objectType));
                            return;
                    }

                    const theObject = Ext.create(data.DMSModelClass.getName(), {
                        _id: data.attachmentId,
                        majorVersion: false
                    });
                    theObject.getProxy().setUrl(proxyUrl);

                    theObject.load({
                        callback: function (record, operation, success) {
                            theObject.isLocked().then(function (locked) {
                                if ((Ext.isBoolean(locked) && locked == false) || (Ext.isObject(locked) && (locked.found == false || (locked.data && locked.data._owned_by_current_session == true)))) {
                                    this.set("theObject", record);
                                } else {
                                    this.getView().closePanel();
                                    var usr;
                                    if (Ext.isObject(locked)) {
                                        usr = locked.user;
                                    }
                                    theObject.showLockMessage(usr);
                                }
                            }, Ext.emptyFn, Ext.emptyFn, this);
                        },
                        scope: this
                    });
                }
            }
        },

        DMSWidgets: {
            bind: '{theObject.widgets}',
            get: function (widgets) {
                return widgets;
            }
        }
    }

});
