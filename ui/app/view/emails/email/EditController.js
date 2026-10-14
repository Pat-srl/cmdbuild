Ext.define('CMDBuildUI.view.emails.email.EditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.emails-edit',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            beforedestroy: 'onBeforeDestroy',
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
     * @param {CMDBuildUI.view.emails.email.Edit} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = view.lookupViewModel(),
            email = vm.get('theEmail');

        // card attribute is evaluated on details load
        // then check for its value to know if the email
        // is already loaded or not
        if (email && !(email.phantom || email.get('card'))) {
            email.load({
                callback: function (record, operation, success) {
                    if (vm && !vm.destroyed) {
                        vm.set('emailloaded', true);
                        email.beginEdit();
                    }
                }
            });
        } else {
            vm.set('emailloaded', true);
            email.beginEdit();
        }

        vm.set(
            'attachmentsSizeLimit.maxAttachmentSize',
            Number(CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.email.maxattachmentsize)) *
                1024 *
                1024
        );
    },

    /**
     * @param {CMDBuildUI.view.emails.email.Edit} view
     * @param {Object} eOpts
     */
    onBeforeDestroy: function (view, eOpts) {
        var theEmail = view.lookupViewModel().get('theEmail');
        if (theEmail.editing) {
            theEmail.cancelEdit();
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
     * @param {Ext.form.field.Combobox} combo
     * @param {xt.data.Model/Ext.data.Model[]} record
     * @param {Object} eOpts
     */
    onTemplateComboChange: function (combo, record, eOpts) {
        var view = this.getView();
        view.updateEmailFromTemplate(record);
        view.fetchAttachmentsSizeLimit(record);
    },

    /**
     * @param {Ext.button.Button} btn
     * @param {Object} eOpts
     */
    onSaveBtn: function (btn, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var me = this,
            view = this.getView(),
            vm = view.lookupViewModel(),
            cancelBtn = view.down('#cancelBtn'),
            attachmentStore = vm.getStore('attachments'),
            allItems = attachmentStore ? attachmentStore.getRange() : [],
            removedItems = attachmentStore ? attachmentStore.removed : [],
            theEmail = vm.get('theEmail');

        theEmail.set('body', theEmail.get('_content_html'));

        btn.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([btn, cancelBtn]);

        theEmail.endEdit();

        if (theEmail.phantom) {
            theEmail._attachments = allItems;
            CMDBuildUI.util.helper.FormHelper.endSavingForm();
            view.up().close();
        } else {
            theEmail.save({
                callback: function (email, response, success) {
                    if (view) {
                        var emailId = email.get('_id'),
                            removedItemslength = removedItems.length;

                        removedItems.forEach(function (item) {
                            if (!item.get('newAttachment')) {
                                item.getProxy().setUrl(attachmentStore.getProxy().getUrl());
                                item.erase({
                                    callback: function (record, operation, success) {
                                        --removedItemslength;
                                        if (removedItemslength == 0) {
                                            afterErase(me, emailId);
                                        }
                                    }
                                });
                            } else {
                                --removedItemslength;
                            }
                        });

                        if (removedItemslength == 0) {
                            afterErase(me, emailId);
                        }
                    } else {
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    }
                }
            });
        }

        /**
         * This function creates/modify the attachments. Is called after the DELETE operation of attachments
         * @param {String} emailId
         */
        function afterErase(me, emailId) {
            theEmail.saveAttachments(allItems).then(
                function () {
                    CMDBuildUI.util.Utilities.enableFormButtons([btn, cancelBtn]);
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    view.up().close();
                },
                function () {
                    CMDBuildUI.util.Utilities.enableFormButtons([btn, cancelBtn]);
                }
            );
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
     * @param {Ext.form.field.ComboBox} combo
     * @param {String} newValue
     * @param {String} oldValue
     */
    onSignatureFieldChange: function (combo, newValue, oldValue) {
        var editor = combo.up().down('cmdbuildhtmleditor');
        if (editor) {
            editor.updateSignatureContent();
        }
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
    }
});
