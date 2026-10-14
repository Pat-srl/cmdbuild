Ext.define('CMDBuildUI.components.editor.HtmlEditor', {
    extend: 'Ext.form.field.HtmlEditor',
    alias: 'widget.cmdbuildhtmleditor',

    allowBlank: true,
    resizable: {
        handles: "s"
    },
    enableResize: true,

    enableSignature: false,
    updateSignature: false,

    /**
     * @override
     * 
     * @returns 
     */
    getToolbarCfg: function () {
        var me = this,
            items = [],
            i,
            tipsEnabled = Ext.quickTipsActive && Ext.tip.QuickTipManager.isEnabled(),
            baseCSSPrefix = Ext.baseCSSPrefix,
            fontSelectItem, undef;

        function btn(id, toggle, handler) {
            return Ext.merge({
                itemId: id,
                cls: baseCSSPrefix + 'btn-icon',
                iconCls: baseCSSPrefix + 'edit-' + id,
                enableToggle: toggle !== false,
                scope: me,
                handler: handler || me.relayBtnCmd,
                clickEvent: 'mousedown',
                tooltip: tipsEnabled ? me.buttonTips[id] : undef,
                overflowText: me.buttonTips[id].title || undef,
                tabIndex: -1
            }, me.buttonDefaults);
        }


        if (me.enableFont) {
            fontSelectItem = Ext.widget('component', {
                itemId: 'fontSelect',
                renderTpl: [
                    '<select id="{id}-selectEl" data-ref="selectEl" class="' + baseCSSPrefix + 'font-select">',
                    '</select>'
                ],
                childEls: ['selectEl'],
                afterRender: function () {
                    me.fontSelect = this.selectEl;
                    Ext.Component.prototype.afterRender.apply(this, arguments);
                },
                onDisable: function () {
                    var selectEl = this.selectEl;
                    if (selectEl) {
                        selectEl.dom.disabled = true;
                    }
                    Ext.Component.prototype.onDisable.apply(this, arguments);
                },
                onEnable: function () {
                    var selectEl = this.selectEl;
                    if (selectEl) {
                        selectEl.dom.disabled = false;
                    }
                    Ext.Component.prototype.onEnable.apply(this, arguments);
                },
                listeners: {
                    change: function () {
                        me.win.focus();
                        me.relayCmd('fontName', me.fontSelect.dom.value);
                        me.deferFocus();
                    },
                    element: 'selectEl'
                }
            });

            items.push(
                fontSelectItem,
                '-'
            );
        }

        if (me.enableFormat) {
            items.push(
                btn('bold'),
                btn('italic'),
                btn('underline')
            );
        }

        if (me.enableFontSize) {
            items.push(
                '-',
                btn('increasefontsize', false, me.adjustFont),
                btn('decreasefontsize', false, me.adjustFont)
            );
        }

        if (me.enableColors) {
            items.push(
                '-', Ext.merge({
                    itemId: 'forecolor',
                    cls: baseCSSPrefix + 'btn-icon',
                    iconCls: baseCSSPrefix + 'edit-forecolor',
                    overflowText: me.buttonTips.forecolor.title,
                    tooltip: tipsEnabled ? me.buttonTips.forecolor || undef : undef,
                    tabIndex: -1,
                    menu: Ext.widget('menu', {
                        plain: true,

                        items: [{
                            xtype: 'colorpicker',
                            allowReselect: true,
                            focus: Ext.emptyFn,
                            value: '000000',
                            plain: true,
                            clickEvent: 'mousedown',
                            handler: function (cp, color) {
                                me.relayCmd('forecolor', Ext.isWebKit || Ext.isIE || Ext.isEdge ? '#' + color : color);
                                this.up('menu').hide();
                            }
                        }]
                    })
                }, me.buttonDefaults), Ext.merge({
                    itemId: 'backcolor',
                    cls: baseCSSPrefix + 'btn-icon',
                    iconCls: baseCSSPrefix + 'edit-backcolor',
                    overflowText: me.buttonTips.backcolor.title,
                    tooltip: tipsEnabled ? me.buttonTips.backcolor || undef : undef,
                    tabIndex: -1,
                    menu: Ext.widget('menu', {
                        plain: true,

                        items: [{
                            xtype: 'colorpicker',
                            focus: Ext.emptyFn,
                            value: 'FFFFFF',
                            plain: true,
                            allowReselect: true,
                            clickEvent: 'mousedown',
                            handler: function (cp, color) {
                                if (Ext.isGecko) {
                                    me.execCmd('useCSS', false);
                                    me.execCmd('hilitecolor', '#' + color);
                                    me.execCmd('useCSS', true);
                                    me.deferFocus();
                                } else {
                                    // eslint-disable-next-line max-len
                                    me.relayCmd(Ext.isOpera ? 'hilitecolor' : 'backcolor', Ext.isWebKit || Ext.isIE || Ext.isEdge || Ext.isOpera ? '#' + color : color);
                                }
                                this.up('menu').hide();
                            }
                        }]
                    })
                }, me.buttonDefaults)
            );
        }

        if (me.enableAlignments) {
            items.push(
                '-',
                btn('justifyleft'),
                btn('justifycenter'),
                btn('justifyright')
            );
        }

        if (me.enableLinks) {
            items.push(
                '-',
                btn('createlink', false, me.createOrEditLink),
                Ext.merge({
                    itemId: 'unlink',
                    cls: baseCSSPrefix + 'edit-unlink',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('unlink', 'solid'),
                    overflowText: CMDBuildUI.locales.Locales.common.editor.unlink,
                    tooltip: {
                        title: CMDBuildUI.locales.Locales.common.editor.unlink,
                        text: CMDBuildUI.locales.Locales.common.editor.unlinkmessage,
                        cls: Ext.baseCSSPrefix + 'html-editor-tip'
                    },
                    scope: me,
                    handler: function (button, event) {
                        me.relayCmd('unlink');
                    },
                    clickEvent: 'mousedown'
                }, me.buttonDefaults)
            );
        }

        if (me.enableLists) {
            items.push(
                '-',
                btn('insertorderedlist'),
                btn('insertunorderedlist')
            );
        }

        if (me.enableSourceEdit) {
            items.push(
                '-',
                btn('sourceedit', true, function () {
                    me.toggleSourceEdit(!me.sourceEditMode);
                })
            );
        }

        if (me.enableSignature) {
            me.buttonTips.signature = {
                title: CMDBuildUI.locales.Locales.common.editor.signature
            };
            items.push(
                '-',
                btn('signature', false, function () {
                    me.addSignature();
                })
            );
        }

        // add clear html button
        me.buttonTips.clearhtml = {
            title: CMDBuildUI.locales.Locales.common.editor.clearhtml
        };
        items.push(
            '-',
            btn('clearhtml', false, me.clearHtml)
        );

        if (me.enableResize) {
            var expandreducebtn;
            if (me.expanded) {
                // configure reduce button
                me.buttonTips.reduceeditor = {
                    title: CMDBuildUI.locales.Locales.common.editor.reduce
                };
                expandreducebtn = btn('reduceeditor', false, me.reduceEditor);
            } else {
                // configure expand button
                me.buttonTips.expandeditor = {
                    title: CMDBuildUI.locales.Locales.common.editor.expand
                };
                expandreducebtn = btn('expandeditor', false, me.expandEditor);
            }

            items.push(
                '-',
                expandreducebtn
            );
        }

        // Everything starts disabled. 
        for (i = 0; i < items.length; i++) {
            if (items[i].itemId !== 'sourceedit') {
                items[i].disabled = true;
            }
        }

        // build the toolbar 
        // Automatically rendered in Component.afterRender's renderChildren call 
        return {
            xtype: 'toolbar',
            defaultButtonUI: me.defaultButtonUI,
            cls: Ext.baseCSSPrefix + 'html-editor-tb',
            enableOverflow: true,
            items: items,

            // stop form submits 
            listeners: {
                click: function (e) {
                    e.preventDefault();
                },
                element: 'el'
            }
        };
    },

    /**
     * 
     */
    clearHtml: function () {
        // sanitize HTML
        this.setValue(sanitizeHtml(this.getValue(), {
            // allow data-type attribute for div and merge with defaults
            // allowed attributes
            allowedAttributes: Ext.apply(sanitizeHtml.defaults.allowedAttributes, {
                div: ['data-type']
            })
        }));
    },

    /**
     * 
     */
    expandEditor: function () {
        var me = this;
        new Ext.Window({
            alwaysOnTop: 1000,
            maximized: true,
            layout: 'fit',
            draggable: false,
            resizable: false,
            maximizable: false,
            closable: false,
            items: [CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
                value: me.getValue(),
                expanded: true,
                ownerEditor: me,
                enableSignature: me.enableSignature
            })],
            listeners: {
                close: function () {
                    me.setValue(this.down("cmdbuildhtmleditor").getValue());
                }
            }
        }).show();
    },

    /**
     * 
     */
    reduceEditor: function () {
        this.up("window").close();
    },

    /**
     * Returns whether or not the field value is currently valid by {@link #getErrors validating} the
     * {@link #processRawValue processed raw value} of the field. **Note**: {@link #disabled} fields are
     * always treated as valid.
     *
     * @return {Boolean} True if the value is valid, else false
     */
    isValid: function () {
        var me = this,
            disabled = me.disabled,
            validate = me.forceValidation || !disabled;

        return validate ? me.validateValue(me.getValue()) : disabled;
    },

    /**
     * Uses {@link #getErrors} to build an array of validation errors. If any errors are found, they are passed to
     * {@link #markInvalid} and false is returned, otherwise true is returned.
     *
     * Previously, subclasses were invited to provide an implementation of this to process validations - from 3.2
     * onwards {@link #getErrors} should be overridden instead.
     *
     * @param {Object} value The value to validate
     * @return {Boolean} True if all validations passed, false if one or more failed
     */
    validateValue: function (value) {
        var me = this,
            errors = me.getErrors(value),
            isValid = Ext.isEmpty(errors);

        if (!me.preventMark) {
            if (isValid) {
                me.clearInvalid();
            } else {
                me.markInvalid(errors);
            }
        }

        return isValid;
    },

    /**
     * @inheritdoc Ext.form.field.Field#markInvalid
     */
    markInvalid: function (errors) {
        // Save the message and fire the 'invalid' event
        var me = this,
            oldMsg = me.getActiveError(),
            active;

        me.setActiveErrors(Ext.Array.from(errors));
        active = me.getActiveError();
        if (oldMsg !== active) {
            me.setError(active);

            if (!me.ariaStaticRoles[me.ariaRole] && me.inputEl) {
                me.inputEl.dom.setAttribute('aria-invalid', true);
            }
        }
    },

    /**
     * Clear any invalid styles/messages for this field.
     *
     * **Note**: this method does not cause the Field's {@link #validate} or {@link #isValid} methods to return `true`
     * if the value does not _pass_ validation. So simply clearing a field's errors will not necessarily allow
     * submission of forms submitted with the {@link Ext.form.action.Submit#clientValidation} option set.
     */
    clearInvalid: function () {
        // Clear the message and fire the 'valid' event
        var me = this,
            hadError = me.hasActiveError();

        delete me.hadErrorOnDisable;

        me.unsetActiveError();

        if (hadError) {
            me.setError('');

            if (!me.ariaStaticRoles[me.ariaRole] && me.inputEl) {
                me.inputEl.dom.setAttribute('aria-invalid', false);
            }
        }
    },

    /**
     * Set the current error state
     * @private
     * @param {String} error The error message to set
     */
    setError: function (error) {
        var me = this,
            msgTarget = me.msgTarget,
            prop;

        if (me.rendered) {
            if (msgTarget === 'title' || msgTarget === 'qtip') {
                prop = msgTarget === 'qtip' ? 'data-errorqtip' : 'title';
                me.getActionEl().dom.setAttribute(prop, error || '');
            } else {
                me.updateLayout();
            }
        }
    },

    /**
     * @private
     * Overrides the method from the Ext.form.Labelable mixin to also add the invalidCls to the inputEl,
     * as that is required for proper styling in IE with nested fields (due to lack of child selector)
     */
    renderActiveError: function () {
        var me = this,
            hasError = me.hasActiveError(),
            invalidCls = 'x-form-trigger-wrap-invalid';

        if (me.containerEl) {
            // Add/remove invalid class
            me.containerEl[hasError ? 'addCls' : 'removeCls']([
                invalidCls, invalidCls + '-' + me.ui
            ]);
        }
        me.mixins.labelable.renderActiveError.call(me);
    },

    /**
     * Validates a value according to the field's validation rules and returns an array of errors
     * for any failing validations. Validation rules are processed in the following order:
     *
     * 1. **Field specific validator**
     *
     *     A validator offers a way to customize and reuse a validation specification.
     *     If a field is configured with a `{@link #validator}`
     *     function, it will be passed the current field value.  The `{@link #validator}`
     *     function is expected to return either:
     *
     *     - Boolean `true`  if the value is valid (validation continues).
     *     - a String to represent the invalid message if invalid (validation halts).
     *
     * 2. **Basic Validation**
     *
     *     If the `{@link #validator}` has not halted validation,
     *     basic validation proceeds as follows:
     *
     *     - `{@link #allowBlank}` : (Invalid message = `{@link #blankText}`)
     *
     *         Depending on the configuration of `{@link #allowBlank}`, a
     *         blank field will cause validation to halt at this step and return
     *         Boolean true or false accordingly.
     *
     *     - `{@link #minLength}` : (Invalid message = `{@link #minLengthText}`)
     *
     *         If the passed value does not satisfy the `{@link #minLength}`
     *         specified, validation halts.
     *
     *     -  `{@link #maxLength}` : (Invalid message = `{@link #maxLengthText}`)
     *
     *         If the passed value does not satisfy the `{@link #maxLength}`
     *         specified, validation halts.
     *
     * 3. **Preconfigured Validation Types (VTypes)**
     *
     *     If none of the prior validation steps halts validation, a field
     *     configured with a `{@link #vtype}` will utilize the
     *     corresponding {@link Ext.form.field.VTypes VTypes} validation function.
     *     If invalid, either the field's `{@link #vtypeText}` or
     *     the VTypes vtype Text property will be used for the invalid message.
     *     Keystrokes on the field will be filtered according to the VTypes
     *     vtype Mask property.
     *
     * 4. **Field specific regex test**
     *
     *     If none of the prior validation steps halts validation, a field's
     *     configured `{@link #regex}` test will be processed.
     *     The invalid message for this test is configured with `{@link #regexText}`
     *
     * @param {Object} value The value to validate. The processed raw value will be used if nothing is passed.
     * @return {String[]} Array of any validation errors
     */
    getErrors: function (value) {
        var me = this,
            errors = me.callParent([value]),
            text = CMDBuildUI.util.Utilities.extractTextFromHTML(value, true),
            trimmed = Ext.String.trim(text);

        // validate mandatory fields
        if (!me.allowBlank && trimmed.length < 1) {
            errors.push(CMDBuildUI.locales.Locales.administration.common.messages.thisfieldisrequired);
        }

        return errors;
    },

    /**
     * Publish the value of this field.
     *
     * @private
     * @override Remove check of field validity
     */
    publishValue: function () {
        var me = this;

        if (me.rendered) {
            me.publishState('value', me.getValue());
        }
    },

    /**
     * @override 
     * Add text area change listener
     * 
     * Toggles the editor between standard and source edit mode.
     * @param {Boolean} [sourceEditMode] True for source edit, false for standard
     * 
     */
    toggleSourceEdit: function (sourceEditMode) {
        var me = this,
            iframe = me.iframeEl,
            textarea = me.textareaEl,
            hiddenCls = Ext.baseCSSPrefix + 'hidden',
            btn = me.getToolbar().getComponent('sourceedit');

        if (!Ext.isBoolean(sourceEditMode)) {
            sourceEditMode = !me.sourceEditMode;
        }

        me.sourceEditMode = sourceEditMode;

        if (btn.pressed !== sourceEditMode) {
            btn.toggle(sourceEditMode);
        }

        if (sourceEditMode) {
            me.disableItems(true);
            me.syncValue();
            textarea.setHeight(iframe.getHeight());
            iframe.addCls(hiddenCls);
            textarea.removeCls(hiddenCls);
            textarea.dom.removeAttribute('tabIndex');
            textarea.focus();
            if (!textarea.events.change) {
                var onChange = function (event) {
                    // set value                    
                    me.setValue(event.target.value);
                };
                textarea.addListener('change', onChange);
                textarea.addListener('keyup', onChange);
            }
            me.inputEl = textarea;
        } else {
            if (me.initialized) {
                me.disableItems(me.readOnly);
            }

            me.pushValue();
            iframe.removeCls(hiddenCls);
            textarea.addCls(hiddenCls);
            textarea.dom.setAttribute('tabIndex', -1);
            me.deferFocus();
            me.inputEl = iframe;
        }

        me.fireEvent('editmodechange', me, sourceEditMode);
        me.updateLayout();
    },

    /**
     * @override
     * 
     * @private
     */
    createLink: function () {
        var me = this;
        CMDBuildUI.util.Msg.prompt(this.createLinkText, '', function (buttonId, url) {
            if (buttonId === 'ok' && url && url !== 'http:/' + '/') {
                me.relayCmd('createlink', url);
            }
        }, this, false, this.defaultLinkValue);
    },

    /**
     * 
     */
    createOrEditLink: function () {
        var win = this.getWin(),
            selection = win.getSelection();
        if (selection && selection.anchorNode) {
            var href = selection.anchorNode.parentNode.href || this.getParentByTagName(selection.anchorNode, 'a').href;
            if (href) {
                CMDBuildUI.util.Msg.prompt(this.createLinkText, '', function (buttonId, url) {
                    if (buttonId === 'ok' && url && url !== 'http:/' + '/') {
                        selection.anchorNode.parentNode.href = url;
                    }
                }, this, false, href);
            } else {
                this.createLink(arguments);
            }
        }
    },

    /**
     * Get parent node for given tagname
     * @param  {Object} node    DOM node
     * @param  {String} tagname HTML tagName
     * @return {Object}         Parent node
     */
    getParentByTagName: function (node, tagname) {
        var parent;
        if (node === null || tagname === '') return;
        parent = node.parentNode;
        tagname = tagname.toUpperCase();

        while (parent.tagName !== "HTML") {
            if (parent.tagName === tagname) {
                return parent;
            }
            parent = parent.parentNode;
        }

        return parent;
    },

    /**
     * 
     */
    addSignature: function () {
        var doc = this.getDoc(),
            currentSignature = doc.querySelectorAll("div[data-type=signature]");

        // remove existing signature
        if (currentSignature.length) {
            currentSignature.forEach(function (s) {
                s.remove();
            });
        }

        this.insertAtCursor('<div data-type="signature"></div><br />');

        if (this.updateSignature) {
            this.updateSignatureContent();
        } else {
            this.syncValue();
        }
    },

    /**
     * 
     */
    updateSignatureContent: function () {
        var vm = this.lookupViewModel(),
            doc = this.getDoc(),
            signatureContainer = doc.querySelector("div[data-type=signature]"),
            selectedSignature = vm.get("theEmail.signature"),
            signatures = vm.get("signatures"),
            signature = selectedSignature ? signatures.getById(selectedSignature) : null,
            text = signature ? signature.get("_content_html_translation") : '';

        if (signatureContainer) {
            signatureContainer.innerHTML = text;
        }

        this.syncValue();
        this.focus();
    },

    /**
     * @override
     * 
     * Add style for signature
     */
    initFrameDoc: function () {
        this.callParent(arguments);
        if (this.enableSignature) {
            this.addSignatureStyle();
        }
    },

    privates: {
        /**
         * 
         */
        addSignatureStyle: function () {
            var doc = this.getDoc(),
                head = doc.head,
                style = doc.createElement('style'),
                css = 'div[data-type=signature]:before { content: "\u270E"; font-size: 20px; color: #aaa; margin-right: 5px; }';

            head.appendChild(style);

            style.type = 'text/css';
            if (style.styleSheet) {
                // This is required for IE8 and below.
                style.styleSheet.cssText = css;
            } else {
                style.appendChild(document.createTextNode(css));
            }
        }
    }
});