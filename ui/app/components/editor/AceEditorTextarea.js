Ext.define('CMDBuildUI.components.editor.AceEditorTextarea', {
    extend: 'Ext.form.field.Base',
    alias: 'widget.aceeditortextarea',

    fieldSubTpl: [
        '<div id="{aceWrapperDivId}" ',
        'style="min-height: {minHeight}; height: {height}"',
        '<tpl if="fieldStyle"> style="{fieldStyle}"</tpl>',
        '<tpl if="fieldCls"> class="{fieldCls}"</tpl>',
        '>',
        '<div id="{aceDivId}" ',
        'style="min-height: {minHeight}; height: {height}"',
        '></div>',
        '</div>'
    ],

    defaultAceEditorOptions: {
        highlightActiveLine: true,
        showPrintMargin: false,
        mode: 'ace/mode/javascript',
        placeholder: ''
    },

    config: {
        /**
         * @cfg {String} [editorId] The id of the widget.
         */
        editorId: null,

        /**
         * @cfg {Object} [options]
         * The target object on which the widget is called.
         */
        options: {
            placeholder: ''
        },

        allowBlank: false
    },

    publishes: [
        "editorId"
    ],

    vmObjectName: '',
    lastValue: '',
    inputField: null,
    minHeight: '58px',
    allowBlank: false,
    blankText: CMDBuildUI.locales.Locales.administration.common.messages.thisfieldisrequired,
    localized: {
        blankText: 'CMDBuildUI.locales.Locales.administration.common.messages.thisfieldisrequired'
    },

    extraFieldBodyCls: Ext.baseCSSPrefix + 'form-text-wrap-default',
    invalidCls: Ext.baseCSSPrefix + 'form-invalid-ace-field-default',

    /**
     *
     */
    initComponent: function () {
        //this.initAceEditor();
        this.callParent();
        if (this.height) {
            if (!/^\d+(\.\d+)?%$/.test(this.height)) {
                this.height = this.height + 'px';
            }
        }

        ace.config.set("workerPath", "resources/js/aceeditor/");

        this.on('afterrender', this.initAceEditor, this);
    },

    /**
     *
     * @returns
     */
    getAceEditor: function () {
        const me = this;
        if (!me.aceEditor) {
            me.aceEditor = ace.edit(me.getAceDivId());
        }
        return me.aceEditor;
    },

    /**
     *
     */
    initAceEditor: function () {
        const me = this;
        const aceOptions = Ext.apply(
                this.defaultAceEditorOptions,
                this.getOptions()
            );

        me.getAceEditor().getSession().$useWorker = false;
        me.getAceEditor().setTheme('ace/theme/chrome');
        me.getAceEditor().setReadOnly(this.readOnly);
        me.getAceEditor().setOptions(aceOptions);
        // if is in grid
        if (!Ext.isEmpty(me._rowContext)) {
            var record = this._rowContext.record;
            if (record && record.getData()) {
                var value = record.getData()[this.inputField];

                me.getAceEditor().getSession().setValue(value);
            }
        }
        me.getAceEditor().getSession().setMode(aceOptions.mode);
        if (me.value && me.value.length > 0) {
            me.getAceEditor().getSession().setValue(this.value);
        }
        me.getAceEditor().getSession().on('change', function () {
            me.onChange.call(me);
        });
        if (me.localized && me.localized.placeholder) {
            Ext.asap(function () {
                try {
                    const localizedPlaceholder = eval(me.localized.placeholder);
                    me.getAceEditor().setOption('placeholder', localizedPlaceholder);
                } catch (error) { }
            });
        }
    },

    /**
     *
     */
    onChange: function () {
        this.lastValue = this.value;
        this.value = this.getAceEditor().getSession().getValue();
        this.isCodeValid();
        this.fireEvent('change', this, this.value, this.lastValue);
        this.callParent([this.value, this.lastValue]);
        this.isCodeValid();
        const vm = this.getViewModel() || this.up().up().getViewModel();
        if (!vm && this.$widgetRecord) {
            this.$widgetRecord.set(this.inputField, this.value);
        } else if (vm && vm.get(this.vmObjectName)) {
            vm.get(this.vmObjectName).set(this.inputField, this.value);
        }
    },

    /**
     *
     * @returns
     */
    isCodeValid: function () {
        const annotations = this.getAceEditor().getSession().getAnnotations();
        if (annotations.length > 0) {
            this.fireEvent('annotation', annotations);
        }
        Ext.Array.each(annotations, function (annotation) {
            if (annotation.type == 'info') {
                this.fireEvent('infoannotation');
                return false;
            }
        }, this);

        Ext.Array.each(annotations, function (annotation) {
            if (annotation.type == 'error') {
                this.fireEvent('errorannotation');
                return false;
            }
        }, this);

        Ext.Array.each(annotations, function (annotation) {
            if (annotation.type == 'warning') {
                this.fireEvent('worningannotation');
                return false;
            }
        }, this);
        return this;
    },

    /**
     *
     * @returns
     */
    getValue: function () {
        return this.value;
    },

    /**
     *
     * @returns
     */
    getSubmitValue: function () {
        return this.value;
    },

    /**
     *
     * @param {*} fieldData
     * @returns
     */
    getSubTplData: function (fieldData) {
        fieldData.aceWrapperDivId = this.getAceWrapperDivId();
        fieldData.aceDivId = this.getAceDivId();
        fieldData.height = this.height || this.minHeight;
        fieldData.minHeight = this.minHeight;
        fieldData.fieldCls = this.fieldCls;

        return fieldData;
    },

    /**
     *
     * @returns
     */
    getAceWrapperDivId: function () {
        return this.getId() + '-aceDivWrapperId';
    },

    /**
     *
     * @returns
     */
    getAceWrapperEl: function () {
        return Ext.get(this.getAceWrapperDivId());
    },

    /**
     *
     * @returns
     */
    getAceDivId: function () {
        return this.getId() + '-aceDivId';
    },

    /**
     *
     * @param {*} value
     * @returns
     */
    getErrors: function (value) {
        const errors = this.callParent([value]);

        if ((!this.allowBlank || !this.getAllowBlank()) && (!this.value || this.value.length == 0) && !this.getAceEditor().getReadOnly()) {
            errors.push(this.blankText);
        }

        if (errors && errors.length > 0) {
            this.getAceWrapperEl().addCls(this.invalidCls);
        } else {
            this.getAceWrapperEl().removeCls(this.invalidCls);
        }
        return errors;
    }
});