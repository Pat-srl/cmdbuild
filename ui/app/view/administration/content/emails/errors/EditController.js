Ext.define('CMDBuildUI.view.administration.content.emails.errors.EditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-errors-edit',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            beforedestroy: 'onBeforeDestroy'
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
        var vm = view.lookupViewModel();
        vm.bind({
            bindTo: '{theEmail}'
        }, function (theEmail) {
            theEmail.beginEdit();
        });
        vm.set('attachmentsSizeLimit.maxAttachmentSize', Number(CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.email.maxattachmentsize)) * 1024 * 1024);
    },

    /**
     * @param {CMDBuildUI.view.emails.email.Edit} view
     * @param {Object} eOpts
     */
    onBeforeDestroy: function (view, eOpts) {
        var theEmail = view.lookupViewModel().get("theEmail");
        if (theEmail.editing) {
            theEmail.cancelEdit();
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
            cancelBtn = view.down("#cancelBtn"),
            attachmentStore = vm.getStore('attachments'),

            allItems = attachmentStore ? attachmentStore.getRange() : [],
            removedItems = attachmentStore ? attachmentStore.removed : [],

            theEmail = vm.get('theEmail');

        theEmail.set("body", theEmail.get("_content_html"));

        btn.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([btn, cancelBtn]);
        theEmail.endEdit();

        // manage removed attachments        
        var emailId = theEmail.get('_id'),
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


        /**
         * This function creates/modify the attachments. Is called after the DELETE operation of attachments
         * @param {String} emailId
         */
        function afterErase(me, emailId) {
            theEmail.getProxy().setUrl(Ext.String.format("{0}/classes/Email/cards/{1}/emails", CMDBuildUI.util.Config.baseUrl, theEmail.get('card')));
            theEmail.saveAttachments(allItems).then(function () {
                CMDBuildUI.util.Utilities.enableFormButtons([btn, cancelBtn]);
                me.getView().fireEvent('saveandsend');
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                view.up().close();
            }, function () {
                CMDBuildUI.util.Utilities.enableFormButtons([btn, cancelBtn]);
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.getView().up("panel").close();
    },

    /**
     *
     * @param {Ext.form.field.ComboBox} combo
     * @param {String} newValue
     * @param {String} oldValue
     */
    onSignatureFieldChange: function (combo, newValue, oldValue) {
        var editor = combo.up().down("cmdbuildhtmleditor");
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
        var vm = this.getViewModel(),
            view = this.getView(),
            maxAttachmentSize = vm.get('attachmentsSizeLimit.maxAttachmentSize'),
            totalAttachmentsSize = 0;
        vm.set('attachmentsTotalCount', store.getCount());
        store.each(function (attachment) {
            totalAttachmentsSize += attachment.get('_file') ? attachment.get('_file').size : attachment.get('Size');
        });
        vm.set('attachmentsSizeLimit.totalAttachmentsSize', totalAttachmentsSize);
        vm.set('attachmentsSizeLimit.error', maxAttachmentSize && totalAttachmentsSize > maxAttachmentSize ? Ext.String.format(CMDBuildUI.locales.Locales.emails.maxsize, Ext.util.Format.fileSize(maxAttachmentSize)) : null);
        Ext.asap(function () {
            view.up().fireEvent('resize');
        });
    }

});