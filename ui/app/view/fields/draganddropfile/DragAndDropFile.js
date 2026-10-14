Ext.define('CMDBuildUI.view.fields.draganddropfile.DragAndDropFile', {
    extend: 'Ext.form.FieldContainer',
    requires: [
        'CMDBuildUI.view.fields.draganddropfile.DragAndDropFileController',
        'CMDBuildUI.view.fields.draganddropfile.DragAndDropFileModel'
    ],

    alias: 'widget.draganddropfilefield',
    controller: 'draganddropfilefield',
    viewModel: {
        type: 'draganddropfilefield'
    },

    /**
     * @cfg {Boolean} allowBlank=true
     * Specify false to validate that the value's length must be > 0.
     */
    allowBlank: true,

    config: {
        /**
         * @cfg {String []} allowedExtensions
         */
        allowedExtensions: null,

        /**
         * @cfg {Boolean} allowMultiUpload=false
         */
        allowMultiUpload: false,

        /**
         * @cfg {Number} maxFileSize
         */
        maxFileSize: null,

        /**
         * @cfg {String []} invalidFileNames
         */
        invalidFileNames: [],

        /**
         * @cfg {String} currentFileName
         */
        currentFileName: null
    },

    /**
     * @property {Boolean} isFieldContainer
     */
    isFieldContainer: true,

    /**
     * @property {Boolean} isFormField
     */
    isFormField: true,

    // class
    cls: Ext.baseCSSPrefix + 'draganddropfilefield',
    clsDragOver: Ext.baseCSSPrefix + 'drag-over',

    // add listeners to element
    listeners: {
        drop: {
            element: 'el',
            fn: 'drop'
        },
        dragstart: {
            element: 'el',
            fn: 'addDropZone'
        },
        dragenter: {
            element: 'el',
            fn: 'addDropZone'
        },
        dragover: {
            element: 'el',
            fn: 'addDropZone'
        },
        dragleave: {
            element: 'el',
            fn: 'removeDropZone'
        },
        dragexit: {
            element: 'el',
            fn: 'removeDropZone'
        }
    },

    // https://fiddle.sencha.com/#fiddle/1103&view/editor
    items: [{
        xtype: 'grid',
        itemId: 'filesgrid',
        hideHeaders: true,
        columns: [{
            dataIndex: 'name',
            flex: 1
        }, {
            dataIndex: 'size',
            flex: .3,
            renderer: Ext.util.Format.fileSize
        }, {
            dataIndex: 'status',
            flex: .8,
            renderer: function (value) {
                var text, icon, cls;
                switch (value) {
                    case CMDBuildUI.model.dms.File.statuses.ready:
                        cls = 'status-ready';
                        text = CMDBuildUI.locales.Locales.attachments.statuses.ready;
                        icon = CMDBuildUI.util.helper.IconHelper.getIconId('check', 'solid')
                        break;
                    case CMDBuildUI.model.dms.File.statuses.empty:
                        cls = 'status-warning';
                        text = CMDBuildUI.locales.Locales.attachments.statuses.empty;
                        icon = CMDBuildUI.util.helper.IconHelper.getIconId('exclamation-triangle', 'solid')
                        break;
                    case CMDBuildUI.model.dms.File.statuses.extensionNotAllowed:
                        cls = 'status-warning';
                        text = CMDBuildUI.locales.Locales.attachments.statuses.extensionNotAllowed;
                        icon = CMDBuildUI.util.helper.IconHelper.getIconId('exclamation-triangle', 'solid')
                        break;
                    case CMDBuildUI.model.dms.File.statuses.error:
                        cls = 'status-error';
                        text = CMDBuildUI.locales.Locales.attachments.statuses.error;
                        icon = CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid')
                        break;
                    case CMDBuildUI.model.dms.File.statuses.fileAlreadyPresent:
                        cls = 'status-error';
                        text = Ext.String.format(CMDBuildUI.locales.Locales.attachments.filealreadyinlist, "");
                        icon = CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid')
                        break;
                    case CMDBuildUI.model.dms.File.statuses.loaded:
                        cls = 'status-done';
                        text = CMDBuildUI.locales.Locales.attachments.statuses.loaded;
                        icon = CMDBuildUI.util.helper.IconHelper.getIconId('check-circle', 'regular')
                        break;
                    case CMDBuildUI.model.dms.File.statuses.tooLarge:
                        cls = 'status-warning';
                        text = CMDBuildUI.locales.Locales.attachments.statuses.toolarge;
                        icon = CMDBuildUI.util.helper.IconHelper.getIconId('exclamation-triangle', 'solid');
                        break;
                    default:
                        text = value;
                }
                return Ext.String.format(
                    '<span class="{0}"><i class="fa {1}"></i> {2}</span>',
                    cls,
                    icon,
                    text
                );
            }
        }, {
            width: 32,
            xtype: 'actioncolumn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
            handler: 'onFilesGridRemoveItem',
            tooltip: CMDBuildUI.locales.Locales.attachments.removefile,
            isActionDisabled: function (view, rowindex, colindex, item, record) {
                return record.get('status') === CMDBuildUI.model.dms.File.statuses.loaded
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.attachments.removefile'
            }
        }],

        viewConfig: {
            emptyText: CMDBuildUI.locales.Locales.attachments.dropfiles,
            localized: {
                emptyText: 'CMDBuildUI.locales.Locales.attachments.dropfiles'
            },
            markDirty: false,
            deferEmptyText: false
        },

        autoEl: {
            'data-testid': 'dragdropfilefield-table'
        },

        listeners: {
            click: {
                element: 'el',
                delegate: 'div.x-grid-empty',
                fn: 'onGridEmptyClick'
            }
        },

        bind: {
            store: '{files}'
        }
    }],

    initComponent: function () {
        this.callParent(arguments);

        // add max size panel
        if (this.getMaxFileSize()) {
            this.insert(0, {
                xtype: 'component',
                html: Ext.String.format(
                    "<small class='x-btn-inner-link-small'>" + CMDBuildUI.locales.Locales.attachments.maxsize + "</small>",
                    this.getMaxFileSize()
                )
            });
        }

        // add file field
        this.fileField = this.add(this.getFileFieldConfig());
    },

    /**
     *
     * @param {String []} newvalue
     * @param {String []} oldvalue
     */
    updateAllowedExtensions: function (newvalue, oldvalue) {
        var me = this;

        if (!Ext.isEmpty(newvalue) && !Ext.isArray(newvalue)) {
            // set allowed extensions as array
            this.setAllowedExtensions(Ext.Array.from(newvalue));
            return;
        }

        // set check extensions variable
        this._checkExtensions = !Ext.isEmpty(newvalue);

        // update files statuses
        if (this.lookupViewModel().get("files")) {
            this.lookupViewModel().get("files").getRange().forEach(function (f) {
                f.set("status", me.getFileStatus(f.get("file")));
            });
        }

        // update accept property in file field
        if (me.fileField) {
            var extensions = [];
            newvalue.forEach(function (ext) {
                extensions.push("." + ext);
            });
            me.fileField.fileInputEl.dom.accept = extensions.join(',');
        }
    },

    /**
     * @return {Boolean}
     */
    checkExtensions: function () {
        return this._checkExtensions;
    },

    /**
     * @return {CMDBuildUI.model.dms.File[]}
     */
    getValue: function () {
        var value = []
        this.lookupViewModel().get("files").getRange().forEach(function (f) {
            if (f.get("status") === CMDBuildUI.model.dms.File.statuses.ready) {
                value.push(f);
            }
        });
        return value;
    },

    /**
     * Returns whether or not the widget value is currently valid by {@link #getErrors validating} the
     * {@link #processRawValue processed raw value} of the widget. **Note**: {@link #disabled} buttons are
     * always treated as valid.
     *
     * @return {Boolean} True if the value is valid, else false
     */
    isValid: function () {
        return this.isDisabled() || this.validateValue();
    },

    /**
     * Uses {@link #getErrors} to build an array of validation errors. If any errors are found, they are passed to
     * {@link #markInvalid} and false is returned, otherwise true is returned.
     *
     * @param {Object} value The value to validate
     * @return {Boolean} True if all validations passed, false if one or more failed
     */
    validateValue: function () {
        var errors = this.getErrors(),
            isValid = Ext.isEmpty(errors);

        this.removeCls(this.clsDragOver);

        if (isValid) {
            this.clearInvalid();
        } else {
            this.markInvalid(errors);
        }
        return isValid;
    },

    /**
     * @param {Object} value The value to validate. The processed raw value will be used if nothing is passed.
     * @return {String[]} Array of any validation errors
     */
    getErrors: function (value) {
        var store = this.lookupViewModel().get("files");
        if (store.getCount()) {
            // check validity
            var haserror = store.findBy(function (record) {
                var status = record.get("status");
                return status === CMDBuildUI.model.dms.File.statuses.extensionNotAllowed ||
                    status === CMDBuildUI.model.dms.File.statuses.error ||
                    status === CMDBuildUI.model.dms.File.statuses.empty ||
                    status === CMDBuildUI.model.dms.File.statuses.tooLarge ||
                    status === CMDBuildUI.model.dms.File.statuses.fileAlreadyPresent
            });
            if (haserror !== -1) {
                return [CMDBuildUI.locales.Locales.attachments.invalidfiles];
            }
        }
        if (!this.allowBlank) {
            // if field is mandatory there must be at least one file with ready status
            if (!store.findRecord('status', CMDBuildUI.model.dms.File.statuses.ready)) {
                return [CMDBuildUI.locales.Locales.errors.fieldrequired];
            }
        }
    },

    /**
     * Returns whether or not the widget value is currently valid by {@link #getErrors validating} the field's current
     * value, and fires the {@link #validitychange} event if the field's validity has changed since the last validation.
     * **Note**: {@link #disabled} fields are always treated as valid.
     *
     * Custom implementations of this method are allowed to have side-effects such as triggering error message display.
     * To validate without side-effects, use {@link #isValid}.
     *
     * @return {Boolean} True if the value is valid, else false
     */
    validate: function () {
        return this.checkValidityChange(this.isValid());
    },

    /**
     *
     * @param {Boolean} isValid
     */
    checkValidityChange: function (isValid) {
        var me = this;

        if (isValid !== me.wasValid) {
            me.wasValid = isValid;
            me.fireEvent('validitychange', me, isValid);
        }
        return isValid;
    },

    /**
     *
     */
    isDirty: function () {
        return false;
    },

    privates: {
        /**
         * @return {Object} Ext.form.field.File configuration
         */
        getFileFieldConfig: function () {
            var multiple = '';
            if (this.getAllowMultiUpload()) {
                multiple = ' multiple ';
            }
            var field = {
                xtype: 'filebutton',
                ui: 'management-primary-outline-small',
                text: CMDBuildUI.locales.Locales.attachments.browse,
                itemId: 'filefield',
                multiple: this.getAllowMultiUpload(),
                afterTpl: [
                    '<input id="{id}-fileInputEl" data-ref="fileInputEl" class="{childElCls} {inputCls}" ',
                    'type="file" size="1" name="{inputName}" unselectable="on" ',
                    '<tpl if="accept != null">accept="{accept}"</tpl>',
                    '<tpl if="tabIndex != null">tabindex="{tabIndex}"</tpl>',
                    multiple,
                    '>'
                ],
                autoEl: {
                    'data-testid': 'dragdropfilefield-file'
                }
            };

            // add accept config if allowed extensions is not empty
            if (!Ext.isEmpty(this.getAllowedExtensions())) {
                var extensions = [];
                this.getAllowedExtensions().forEach(function (ext) {
                    extensions.push("." + ext);
                });
                field.accept = extensions.join(',');
            }

            return field;
        },

        /**
         * Clear any invalid styles/messages for this widget button.
         *
         * **Note**: this method does not cause the Field's {@link #validate} or {@link #isValid} methods to return `true`
         * if the value does not _pass_ validation. So simply clearing a field's errors will not necessarily allow
         * submission of forms submitted with the {@link Ext.form.action.Submit#clientValidation} option set.
         * @private
         */
        clearInvalid: function () {
            var me = this;
            if (!!me.activeError) {
                delete me.activeError;
                delete me.activeErrors;

                this.removeClsWithUI("error")
                this.el.dom.removeAttribute('data-errorqtip');
            }
        },

        /**
         * @method
         * Display one or more error messages associated with this widget.
         *
         * **Note**: this method does not cause the Field's {@link #validate} or
         * {@link #isValid} methods to return `false` if the value does _pass_ validation.
         * So simply marking a Field as invalid will not prevent submission of forms
         * submitted with the {@link Ext.form.action.Submit#clientValidation} option set.
         *
         * @param {String/String[]} errors The validation message(s) to display.
         */
        markInvalid: function (errors) {
            var me = this;
            errors = Ext.Array.from(errors);
            var tpl = this.lookupTpl("activeErrorsTpl");

            this.activeErrors = errors;
            var activeError = me.activeError = tpl.apply({
                fieldLabel: me.fieldLabel,
                errors: errors,
                listCls: Ext.baseCSSPrefix + 'list-plain'
            });

            this.addClsWithUI("error");

            this.el.dom.setAttribute('data-errorqtip', activeError);
        },

        /**
         * @param {File} file
         * @return {String}
         */
        getFileStatus: function (file) {
            var me = this,
                status = CMDBuildUI.model.dms.File.statuses.ready;
            // check extension
            if (me.checkExtensions() || me.getCurrentFileName()) {
                extension = (/(?:\.([^.]+))?$/.exec(file.name)[1] || '').toLowerCase(); // extract extension
                if (me.checkExtensions()) {
                    if (!Ext.Array.contains(Ext.Array.map(me.getAllowedExtensions(), function (ext) {
                        return ext.toLowerCase();
                    }), extension)) {
                        status = CMDBuildUI.model.dms.File.statuses.extensionNotAllowed;
                    }
                }
                //check if new file extension is compatible with old file extension
                if (me.getCurrentFileName()) {
                    extensionCurrentFileName = (/(?:\.([^.]+))?$/.exec(me.getCurrentFileName())[1] || '').toLowerCase();
                    if (extensionCurrentFileName !== extension) {
                        status = CMDBuildUI.model.dms.File.statuses.extensionNotAllowed;
                    }
                }
            }
            // check if file is not empty
            if (file.size === 0) {
                status = CMDBuildUI.model.dms.File.statuses.empty;
            }
            // check file size
            if (me.getMaxFileSize() && (file.size / (1024 * 1024)) > me.getMaxFileSize()) {
                status = CMDBuildUI.model.dms.File.statuses.tooLarge;
            }
            //check file is already present
            if (!Ext.isEmpty(me.getInvalidFileNames()) && Ext.Array.contains(me.getInvalidFileNames(), file.name)) {
                status = CMDBuildUI.model.dms.File.statuses.fileAlreadyPresent;
            }
            return status;
        }
    }
});