Ext.define('CMDBuildUI.view.dms.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dms-tabpanel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     *
     * @param {Ext.app.View} view
     */
    onBeforeRender: function (view) {
        const height = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.inlinecard.height);
        view.setHeight(view.up('dms-container').getHeight() * 0.5);
    },

    /**
     *
     */
    onEditToolClick: function () {
        const view = this.getView();
        const vm = view.lookupViewModel();
        const gridContainer = view.up('dms-container');
        const record = vm.get('record');
        const title =
            CMDBuildUI.locales.Locales.attachments.editattachment +
            ' ' +
            record.get('_category_description_translation');
        const attachmentsStore = vm.get('attachments');
        const attachmentName = record.get('name');
        const invalidFileNames = Ext.Array.remove(attachmentsStore.collect('name'), attachmentName);

        let theObject;

        if (record.phantom || record.dirty) {
            theObject = record;
        }

        CMDBuildUI.util.Utilities.openPopup(
            'popup-edit-attachment-form',
            title,
            {
                xtype: 'dms-attachment-edit',
                ignoreSchedules: gridContainer.getIgnoreSchedules(),
                asyncStore: gridContainer.getIsAsyncSave() ? attachmentsStore : null,
                invalidFileNames: invalidFileNames,
                currentFileName: attachmentName,
                viewModel: {
                    data: {
                        objectType: vm.get('objectType'),
                        objectTypeName: vm.get('objectTypeName'),
                        objectId: vm.get('objectId'),
                        attachmentId: record.getId(),
                        DMSCategoryTypeName: vm.get('DMSCategoryTypeName'),
                        DMSCategoryValue: record.get('category'),
                        theObject: theObject
                    }
                }
            },
            {
                popupsave: {
                    fn: function () {
                        attachmentsStore.load();
                    },
                    scope: this
                },
                popupcancel: function () {}
            }
        );
    },

    /**
     *
     * @param {Ext.Tool} tool
     * @param {Object} e
     */
    onDeleteToolClick: function (tool, e) {
        const view = this.getView(),
            vm = this.getViewModel();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.attachments.deleteattachment,
            CMDBuildUI.locales.Locales.attachments.deleteattachment_confirmation,
            function (action) {
                if (action === 'yes') {
                    CMDBuildUI.util.helper.FormHelper.startSavingForm();
                    const dmsAttachmentView = view.down('#dms-attachment-view'),
                        gridContainer = view.up('dms-container'),
                        attachmentsStore = vm.get('attachments');

                    if (gridContainer.getIsAsyncSave()) {
                        attachmentsStore.remove(attachmentsStore.getById(vm.get('record._id')));
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    } else {
                        const url = Ext.String.format(
                            '{0}{1}/{2}',
                            CMDBuildUI.util.Config.baseUrl,
                            vm.get('proxyUrl'),
                            vm.get('record._id')
                        );

                        Ext.Ajax.request({
                            url: url,
                            method: 'DELETE',
                            success: function (response) {
                                attachmentsStore.load();

                                const theObject = vm.get('record');
                                if (theObject) {
                                    // execute form trigger
                                    dmsAttachmentView.executeAfterActionFormTriggers(
                                        CMDBuildUI.util.helper.FormHelper.formtriggeractions.afterDelete,
                                        theObject,
                                        dmsAttachmentView.getApiForTrigger(
                                            CMDBuildUI.util.api.Client.getApiForFormAfterDelete()
                                        )
                                    );
                                }
                            },
                            callback: function (options, success, response) {
                                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                            }
                        });
                    }
                }
            },
            this
        );
    },

    /**
     *
     * @param {Ext.Tool} tool
     * @param {Object} e
     */
    onDownloadToolClick: function (tool, e) {
        const vm = this.getViewModel();
        const filename = vm.get('record.name');
        const url = Ext.String.format(
            '{0}{1}/{2}/{3}',
            CMDBuildUI.util.Config.baseUrl,
            vm.get('proxyUrl'), //specificated in CMDBuildUI.view.dms.GridModel
            vm.get('record._id'),
            filename
        );

        CMDBuildUI.util.File.download(url, filename);
    }
});
