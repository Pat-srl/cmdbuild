Ext.define('CMDBuildUI.view.emails.email.CreateController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.emails-create',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            validitychange: 'onValidityChange'
        },
        '#saveBtn': {
            click: 'onSaveBtn'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#addfileattachment filebutton': {
            click: 'onAddFileAttachmentClick'
        },
        '#addfileattachment': {
            change: 'onAddFileAttachmentChange'
        },
        '#addattachmentsfromdocumentarchive': {
            click: 'onAddAttachmentsFromDocumentArchive'
        },
        '#templatecombo': {
            select: 'onTemplateComboChange'
        },
        '#signaturefield': {
            change: 'onSignatureFieldChange'
        }
    },

    /**
     * @param {CMDBuildUI.view.emails.email.Create} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = view.getViewModel();

        vm.set('disabled.templatechoice', false);
        vm.set(
            'attachmentsSizeLimit.maxAttachmentSize',
            Number(CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.email.maxattachmentsize)) *
                1024 *
                1024
        );
        vm.set('emailloaded', true);

        // set default signature on creation
        vm.bind('{signatures}', function (signatures) {
            if (signatures) {
                function setDefaultSignature() {
                    var defaultSignature = signatures.findRecord('_default', true);
                    if (defaultSignature) {
                        vm.set('theEmail.signature', defaultSignature.getId());
                    }
                }
                if (signatures.isLoaded()) {
                    setDefaultSignature();
                } else {
                    signatures.on({
                        load: {
                            fn: setDefaultSignature,
                            single: true
                        }
                    });
                }
            }
        });

        if (vm.get('isAsync')) {
            vm.bind('{objectTypeName}', function (objectTypeName) {
                if (objectTypeName) {
                    const fileAttributesOfProcess = [];
                    Ext.Array.forEach(
                        CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName).attributes().getRange(),
                        function (item, index, allitems) {
                            if (item.get('type') == CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.file) {
                                fileAttributesOfProcess.push(item.getId());
                            }
                        }
                    );
                    vm.set('fileAttributesOfProcess', fileAttributesOfProcess);
                }
            });
        }
    },

    /**
     *
     * @param {Ext.form.Basic} form
     * @param {Boolean} valid
     * @param {Object} eOpts
     */
    onValidityChange: function (form, valid, eOpts) {
        this.getViewModel().set('formValid', valid);
    },

    /**
     * @param {Ext.form.field.Combobox} combo
     * @param {Ext.data.Model/Ext.data.Model[]} record
     * @param {Object} eOpts
     */
    onTemplateComboChange: function (combo, record, eOpts) {
        const view = this.getView();
        const vm = this.getViewModel();
        const fileAttributesOfProcess = vm.get('fileAttributesOfProcess');
        let isTemplateBlocked = false;

        if (vm.get('isAsync')) {
            const attachments = record.get('data').uploadAttachments;
            if (!(attachments === null || typeof attachments == 'undefined')) {
                isTemplateBlocked = true;
                vm.set('warningMessage', CMDBuildUI.locales.Locales.emails.attachmentsmessage);
            } else {
                const bindedFiles = [];

                Ext.Array.forEach(fileAttributesOfProcess, function (item) {
                    bindedFiles.push('{client:' + item + '}');
                    bindedFiles.push('{server:' + item + '}');
                });

                isTemplateBlocked = Ext.Object.getValues(record.getData()).some(function (value) {
                    if (!Ext.isString(value)) {
                        return false;
                    }

                    const trimmedValue = value.replace(/\s/g, '');
                    return bindedFiles.some(function (singleBind) {
                        return trimmedValue.includes(singleBind);
                    });
                });
                vm.set('warningMessage', CMDBuildUI.locales.Locales.emails.fileattributesmessage);
            }
        }

        if (!isTemplateBlocked) {
            view.updateEmailFromTemplate(record);
            view.fetchAttachmentsSizeLimit(record);
            vm.set('hideWarningMessage', true);
        } else {
            vm.set('hideWarningMessage', false);
        }
    },

    /**
     * @param {Ext.form.field.FileButton} button
     * @param {Object} eOpts
     */
    onAddFileAttachmentClick: function (button, eOpts) {
        // allow multi upload
        button.fileInputEl.dom.setAttribute('multiple', '');
    },

    /**
     * @param {Ext.form.field.File} filefield
     * @param {Object} value
     * @param {Object} eOpts
     */
    onAddFileAttachmentChange: function (filefield, value, eOpts) {
        this.getView().addFileAttachment(filefield);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddAttachmentsFromDocumentArchive: function (button, e, eOpts) {
        this.getView().addDmsAttachment();
    },

    /**
     * @param {Ext.data.Button} btn
     * @param {Object} eOpts
     */
    onSaveBtn: function (btn, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var view = this.getView(),
            vm = view.lookupViewModel(),
            cancelBtn = view.down('#cancelBtn'),
            attachmentStore = vm.getStore('attachments'),
            allItems = attachmentStore ? attachmentStore.getRange() : [],
            theEmail = vm.get('theEmail');

        theEmail.set('body', theEmail.get('_content_html'));
        theEmail.set('status', CMDBuildUI.model.emails.Email.statuses.draft);

        btn.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([btn, cancelBtn]);

        if (vm.get('isAsync')) {
            theEmail._attachments = allItems;
            view.asyncSave(theEmail);
            CMDBuildUI.util.helper.FormHelper.endSavingForm();
            view.up().close();
        } else {
            theEmail.save({
                callback: function (email, response, success) {
                    email.saveAttachments(allItems).then(
                        function () {
                            CMDBuildUI.util.Utilities.enableFormButtons([btn, cancelBtn]);
                            view.fireEvent('itemcreated');
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                            view.up().close();
                        },
                        function () {
                            CMDBuildUI.util.Utilities.enableFormButtons([btn, cancelBtn]);
                        }
                    );
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
    },

    /**
     *
     * @param {Ext.data.Store} store
     * @param {Object} eOpts
     */
    onAttachmentsDatachanged: function (store, eOpts) {
        var vm = this.getViewModel();
        var maxAttachmentSize = vm.get('attachmentsSizeLimit.maxAttachmentSize');
        var totalAttachmentsSize = 0;
        vm.set('attachmentsTotalCount', store.getCount());
        store.each(function (attachment) {
            totalAttachmentsSize += attachment.get('_file') ? attachment.get('_file').size : attachment.get('Size');
        });
        vm.set('attachmentsSizeLimit.totalAttachmentsSize', totalAttachmentsSize);
        vm.set(
            'attachmentsSizeLimit.error',
            maxAttachmentSize && totalAttachmentsSize > maxAttachmentSize
                ? Ext.String.format(
                      CMDBuildUI.locales.Locales.emails.maxsize,
                      Ext.util.Format.fileSize(maxAttachmentSize)
                  )
                : null
        );
    },

    /**
     *
     * @param {Ext.form.field.ComboBox} combo
     * @param {String} newValue
     * @param {String} oldValue
     */
    onSignatureFieldChange: function (combo, newValue, oldValue) {
        var editor = combo.up().down('cmdbuildhtmleditor');
        if (editor) {
            editor.updateSignatureContent();
        }
    }
});
