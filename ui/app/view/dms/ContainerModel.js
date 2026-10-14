Ext.define('CMDBuildUI.view.dms.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.dms-container',

    data: {
        DMSCategoryTypeName: undefined,
        DMSCategoryType: undefined,
        proxyUrl: undefined,
        disableRemoteActions: undefined,
        readOnly: false,
        enableExtendedGrid: false,
        disabledbulkactions: true,
        textAlert: null,
        autoLoad: false,
        gridReady: false,
        multiSelectionActive: false
    },

    formulas: {
        DMSCategoryCalculations: {
            bind: {
                objectTypeName: '{objectTypeName}',
                objectType: '{objectType}'
            },
            get: function (data) {
                if (data.objectType && data.objectTypeName) {
                    const klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                            data.objectTypeName,
                            data.objectType
                        ),
                        DMSCategoryTypeName =
                            klass.get('dmsCategory') ||
                            klass.get('attachmentTypeLookup') ||
                            CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.category); //FIXME: In future here should be only attachmentTypeLookup
                    this.set('DMSCategoryTypeName', DMSCategoryTypeName);

                    if (DMSCategoryTypeName) {
                        const DMSCategoryType =
                            CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(DMSCategoryTypeName);
                        if (DMSCategoryType) {
                            DMSCategoryType.getCategoryValues().then(
                                function (categoryValues) {
                                    this.set('DMSCategoryType', DMSCategoryType);
                                },
                                Ext.emptyFn,
                                Ext.emptyFn,
                                this
                            );
                        }
                    }
                }
            }
        },

        proxyUrl: {
            bind: {
                objectTypeName: '{objectTypeName}',
                objectType: '{objectType}',
                objectId: '{objectId}',
                gridReady: '{gridReady}'
            },
            get: function (data) {
                var proxyUrl = '';
                if (data.objectType && data.objectTypeName && data.objectId && data.gridReady) {
                    this.set('autoLoad', true);

                    switch (data.objectType) {
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                            proxyUrl = CMDBuildUI.util.api.Classes.getAttachments(data.objectTypeName, data.objectId);
                            break;
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar:
                            proxyUrl = CMDBuildUI.util.api.Calendar.getAttachmentsUrl(data.objectId);
                            break;
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                            proxyUrl = CMDBuildUI.util.api.Processes.getAttachmentsUrl(
                                data.objectTypeName,
                                data.objectId
                            );
                            break;
                        default:
                            CMDBuildUI.util.Notifier.showErrorMessage(
                                Ext.String.format('No attachments url for {0} object', data.objectType)
                            );
                            return;
                    }
                }
                this.set('disableRemoteActions', !proxyUrl);
                return proxyUrl;
            }
        }
    },

    stores: {
        attachments: {
            model: 'CMDBuildUI.model.dms.DMSAttachment',
            proxy: {
                url: '{proxyUrl}',
                type: 'baseproxy',
                extraParams: {
                    limit: 0,
                    detailed: true
                }
            },
            listeners: {
                load: 'onAttachmentsLoad'
            },
            groupField: '_category_description_translation',
            autoLoad: '{autoLoad}'
        }
    }
});
