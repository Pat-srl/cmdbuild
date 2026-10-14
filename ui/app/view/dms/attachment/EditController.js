Ext.define('CMDBuildUI.view.dms.attachment.EditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dms-attachment-edit',
    control: {
        '#': {
            beforerender: 'onBeforeRender',
            beforedestroy: 'onBeforeDestroy'
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
            theObject: '{theObject}'
        }, function (data) {
            if (data.theObject) {
                data.theObject.addLock().then(function (success) {
                    if (!this.destroyed && !success) {
                        this.closePanel();
                    }
                }, Ext.emptyFn, Ext.emptyFn, this);
            }
        });

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
                    mode: CMDBuildUI.util.helper.FormHelper.formmodes.update,
                    linkName: 'theObject',
                    showAsFieldsets: true, //important
                    readonly: true,
                    ignoreSchedules: view.getIgnoreSchedules()
                }).then(function (items) {

                    //adds the upload  field
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
                                    allowBlank: true,
                                    allowMultiUpload: false,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.attachments.file'
                                    },
                                    allowedExtensions: CMDBuildUI.util.helper.AttachmentsHelper.getAllowedExtensions(data.DMSCategoryTypeName, data.DMSCategoryValue, data.DMSModelClass),
                                    maxFileSize: view.getMaxFileSize(data.DMSCategoryTypeName, data.DMSCategoryValue, data.DMSModelClass),
                                    invalidFileNames: view.getInvalidFileNames(),
                                    currentFileName: view.getCurrentFileName()
                                }]
                            }, {
                                layout: 'fit',
                                items: [{
                                    xtype: 'checkboxfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.attachments.majorversion,
                                    autoEl: {
                                        'data-testid': 'attachmentform-majorversion'
                                    },
                                    bind: {
                                        value: '{theObject.majorVersion}'
                                    },
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.attachments.majorversion'
                                    }
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
                        CMDBuildUI.util.helper.FormHelper.formtriggeractions.beforeEdit,
                        view.getApiForTrigger(CMDBuildUI.util.api.Client.getApiForFormBeforeEdit())
                    );

                    // execute widgets on target form open
                    CMDBuildUI.util.helper.WidgetsHelper.executeOnTargetFormOpen(
                        theObject,
                        widgets.getRange(), {
                        formmode: view.formmode
                    });
                });
            }
        });
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

            const theObject = vm.get("theObject"),
                widgets = vm.get("DMSWidgets").getRange(),
                field = view.down("#file"),
                files = field.getValue(),
                file = files[0],
                loadmask = CMDBuildUI.util.Utilities.addLoadMask(view);

            var success = 0,
                errors = 0;

            // execute widgets before save actions
            CMDBuildUI.util.helper.WidgetsHelper.executeBeforeTargetSave(
                theObject,
                widgets, {
                formmode: view.formmode
            }).then(function () {
                if (file) {
                    theObject.set("_filedata", file);
                }

                // finish function
                function finish() {
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    // if all files are uploaded
                    if (success) {
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
                    } else if (errors) {
                        CMDBuildUI.util.Utilities.removeLoadMask(loadmask);
                    }
                }

                if (view.isAsyncSave()) {
                    const store = view.getAsyncStore();
                    store.remove(store.getById(theObject.getId()))
                    store.addSorted(theObject);
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    view.up("panel").close();
                } else {
                    theObject.saveAttachmentAndSequences(file ? file.get('file') : null, {
                        type: vm.get("objectType"),
                        typeName: vm.get("objectTypeName"),
                        id: vm.get("objectId")
                    }).then(function () {
                        success++;
                        finish();
                    }).otherwise(function (error) {
                        file ? file.set("status", CMDBuildUI.model.dms.File.statuses.error) : null;
                        errors++;

                        const response = {
                            responseText: error
                        };
                        CMDBuildUI.util.Ajax.showMessages(response, {
                            hideErrorNotification: false
                        });

                        finish();
                    });
                }
            }, function () {
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                CMDBuildUI.util.Utilities.removeLoadMask(loadmask);
            });
        }
    },

    /**
     * 
     * @param {*} button 
     * @param {*} e 
     */
    onCancelButton: function (button, e) {
        const view = this.getView();
        view.closePanel();
    },

    /**
     * Unlock card on management details window close.
     * @param {CMDBuildUI.view.classes.cards.card.Edit} view 
     * @param {Object} eOpts 
     */
    onBeforeDestroy: function (view, eOpts) {
        const theObject = this.getViewModel().get("theObject");
        if (theObject) {
            theObject.removeLock();
        }
    }
});