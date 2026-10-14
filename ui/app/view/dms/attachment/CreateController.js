Ext.define('CMDBuildUI.view.dms.attachment.CreateController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dms-attachment-create',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * 
     * @param {*} view 
     * @param {*} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();

        vm.bind({
            DMSCategoryTypeName: '{DMSCategoryTypeName}',
            DMSCategoryValue: '{DMSCategoryValue}',
            DMSModelClass: '{DMSModelClass}',
            DMSClass: '{DMSClass}',
            DMSWidgets: '{DMSWidgets}'
        }, function (data) {
            if (data.DMSModelClass && data.DMSCategoryValue && data.DMSCategoryTypeName && data.DMSClass && data.DMSWidgets) {
                CMDBuildUI.util.helper.FormHelper.renderFormForType(CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                    data.DMSClass.getId(), {
                    mode: CMDBuildUI.util.helper.FormHelper.formmodes.create,
                    linkName: 'theObject',
                    showAsFieldsets: true, //important
                    readonly: true,
                    ignoreSchedules: view.getIgnoreSchedules()
                }).then(function (items) {

                    //enables or disables multi upload whene the DMS Model has some form triggers afterInsert
                    var allowMultiUpload = true;
                    const triggers = data.DMSClass.getFormTriggersForAction(CMDBuildUI.util.helper.FormHelper.formtriggeractions.afterInsert);
                    if (triggers && triggers.length) {
                        allowMultiUpload = false;
                    }

                    //adds the upload field
                    items.push({
                        title: CMDBuildUI.locales.Locales.attachments.file,
                        groupId: CMDBuildUI.locales.Locales.attachments.file,
                        xtype: "formpaginationfieldset",
                        collapsed: false,
                        hidden: false,
                        items: [{
                            xtype: 'container',
                            layout: 'column',
                            defaults: {
                                xtype: "fieldcontainer",
                                columnWidth: 0.5,
                                flex: 0.5,
                                padding: "0 15 0 15",
                                layout: "anchor",
                                minHeight: 1
                            },
                            items: [{
                                layout: 'fit',
                                items: [{
                                    xtype: 'draganddropfilefield',
                                    itemId: 'file',
                                    fieldLabel: CMDBuildUI.locales.Locales.attachments.file,
                                    allowBlank: false,
                                    allowMultiUpload: allowMultiUpload,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.attachments.file'
                                    },
                                    allowedExtensions: CMDBuildUI.util.helper.AttachmentsHelper.getAllowedExtensions(data.DMSCategoryTypeName, data.DMSCategoryValue, data.DMSModelClass),
                                    maxFileSize: view.getMaxFileSize(data.DMSCategoryTypeName, data.DMSCategoryValue, data.DMSModelClass),
                                    invalidFileNames: view.getInvalidFileNames()
                                }]
                            }]
                        }]
                    });

                    const theObject = vm.get("theObject"),
                        formView = view.down('#formpanel'),
                        widgets = data.DMSWidgets,
                        widgetsInline = widgets.query("_inline", true);

                    formView.removeAll(true);

                    if (widgetsInline) {
                        // add inline widgets
                        CMDBuildUI.view.widgets.Launchers.addInlineWidgets(theObject, widgetsInline, view, items);
                    }

                    formView.add(view.getFormItems(items));

                    // init before edit triggers
                    view.initBeforeActionFormTriggers(
                        CMDBuildUI.util.helper.FormHelper.formtriggeractions.beforeInsert,
                        view.getApiForTrigger(CMDBuildUI.util.api.Client.getApiForFormBeforeCreate())
                    );

                    // execute widgets on target form open
                    CMDBuildUI.util.helper.WidgetsHelper.executeOnTargetFormOpen(
                        theObject,
                        widgets.getRange(), {
                        formmode: view.formmode
                    });
                });
            }
        })
    },

    /**
     * 
     * @param {*} button 
     * @param {*} e 
     */
    onSaveButton: function (button, e) {
        const view = this.getView(),
            vm = this.getViewModel();
        if (view.isValid()) {
            CMDBuildUI.util.helper.FormHelper.startSavingForm();
            const field = view.down("#file"),
                theObject = vm.get("theObject"),
                metadata = theObject.getData(),
                files = field.getValue(),
                sequences = theObject.sequences().getRange(),
                categoryDescription = vm.get("DMSCategoryDescription"),
                widgets = vm.get("DMSWidgets").getRange(),
                records = [];

            // execute widgets before save actions
            CMDBuildUI.util.helper.WidgetsHelper.executeBeforeTargetSave(
                theObject,
                widgets, {
                formmode: view.formmode
            }).then(function () {
                // create one record for each file
                files.forEach(function (file) {
                    // create the record
                    // var record = theObject.copy();
                    const record = Ext.create(theObject.$className, Ext.applyIf({
                        _id: null,
                        _filedata: file,
                        _category_description_translation: categoryDescription,
                        _can_update: true,
                        _can_delete: true,
                        name: file.get("name"),
                        version: "1.0"
                    }, metadata));

                    if (sequences.length) {
                        const sequencesCopy = [];
                        // var sequencesCopy = record.sequences();
                        sequences.forEach(function (s) {
                            // create a copy of the schedule
                            const scopy = Ext.create(s.$className, Ext.applyIf({
                                _id: null
                            }, s.getData()));
                            // copy of the events
                            scopy.events().setData(s.events().getRange());
                            // add the copy of the sequence to the array
                            sequencesCopy.push(scopy);
                        });
                        record.sequences().add(sequencesCopy);
                    }
                    records.push(record);
                });

                if (view.isAsyncSave()) {
                    // execute widgets after save actions
                    CMDBuildUI.util.helper.WidgetsHelper.executeAfterTargetSave(
                        theObject,
                        widgets, {
                        formmode: view.formmode
                    }).then(function () {
                        // add records on store
                        view.getAsyncStore().add(records);
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        view.up("panel").close();
                    }, function () {
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    });
                } else {
                    const loadmask = CMDBuildUI.util.Utilities.addLoadMask(view);
                    var success = 0,
                        errors = 0;

                    // finish function
                    const finish = function () {
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        // if all files are uploaded
                        switch (files.length) {
                            case success:
                                // execute widgets after save actions
                                CMDBuildUI.util.helper.WidgetsHelper.executeAfterTargetSave(
                                    theObject,
                                    widgets, {
                                    formmode: view.formmode
                                }).then(function () {
                                    view.up("panel").fireEvent('popupsave');
                                    view.up("panel").close();
                                }, function () {
                                    CMDBuildUI.util.Utilities.removeLoadMask(loadmask);
                                });
                                break;
                            case (success + errors):
                                CMDBuildUI.util.Utilities.removeLoadMask(loadmask);
                                view.isValid();
                                break;
                        }
                    };

                    // in case of many files, to avoid that the saves go in error, we divide the saves into blocks of 10
                    // issue #6358 uploading a lot of documents causes cmdbuild to freeze
                    const tempArray = [],
                        chunk = 10;
                    var i, j;

                    const checkCompleted = function (chunkIndex) {
                        if (tempArray.length - 1 >= chunkIndex) {
                            const nextChunkIndex = chunkIndex + 1;
                            if (tempArray[nextChunkIndex]) {
                                uploadChunk(tempArray[nextChunkIndex], nextChunkIndex);
                            } else {
                                finish();
                            }
                        } else {
                            finish();
                        }
                    };

                    const uploadChunk = function (filesArray, chunkIndex) {
                        var currentCompleted = 0,
                            currentErrors = 0;

                        filesArray.forEach(function (record) {
                            record.saveAttachmentAndSequences(record.get("_filedata").get("file"), {
                                type: vm.get("objectType"),
                                typeName: vm.get("objectTypeName"),
                                id: vm.get("objectId")
                            }).then(function () {
                                success++;
                                currentCompleted++;
                                if (currentCompleted + currentErrors === filesArray.length) {
                                    checkCompleted(chunkIndex);
                                }
                            }).otherwise(function (error) {
                                record.get("_filedata").set("status", CMDBuildUI.model.dms.File.statuses.error);
                                errors++;
                                currentErrors++;

                                const response = {
                                    responseText: error
                                };
                                CMDBuildUI.util.Ajax.showMessages(response, {
                                    hideErrorNotification: false
                                });
                                if (currentCompleted + currentErrors === filesArray.length) {
                                    checkCompleted(chunkIndex);
                                }
                            });
                        });
                    };

                    if (records.length) {
                        for (i = 0, j = records.length; i < j; i += chunk) {
                            tempArray.push(records.slice(i, i + chunk));
                        }
                        uploadChunk(tempArray[0], 0);
                    }
                }
            }, function () {
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            });
        }
    },

    /**
     * 
     * @param {*} button 
     * @param {*} e 
     */
    onCancelButton: function (button, e) {
        const panel = this.getView().up("panel");

        panel.fireEvent('popupcancel');
        panel.close();
    }
});