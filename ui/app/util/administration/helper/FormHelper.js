Ext.define('CMDBuildUI.util.administration.helper.FormHelper', {
    singleton: true,

    fieldDefaults: {
        labelAlign: 'top',
        labelPad: 2,
        labelSeparator: '',
        anchor: '100%',
        width: '100%',
        msgTarget: 'qtip'
    },

    formActions: {
        view: 'VIEW',
        add: 'ADD',
        edit: 'EDIT',
        empty: 'EMPTY',
        clone: 'CLONE'
    },

    /**
     * Get Invalid Fields helper
     *
     * CMDBuildUI.util.administration.helper.FormHelper.getInvalidFields(form)
     *
     * @param {Ext.form.Panel} form
     *
     * @returns {Ext.form.field.Base[]} Array of invalid form fields
     */
    getInvalidFields: function (form) {
        var invalidFields = [];
        Ext.suspendLayouts();
        form.getFields().filterBy(function (field) {
            if (field.validate()) return;
            invalidFields.push(field);
        });
        Ext.resumeLayouts(true);
        return invalidFields;
    },

    /**
     * Get form buttons bar with "Save" and "Cancel" buttons
     *
     * CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(formBind)
     * use formBind = false for ignore form bind
     *
     *
     * @param {Boolean} [formBind=true] shortcut form saveBtn.saveBtn AND saveBtn.disabled
     * @param {Ext.button.Button} [saveBtn] config
     * @param {Ext.button.Button} [cancelBtn] config
     *
     * @returns {Ext.button.Button[]} Array of button items
     */
    getSaveCancelButtons: function (formBind, saveBtn, cancelBtn) {
        formBind = typeof formBind === 'undefined' || formBind === true ? true : false;
        return [
            {
                xtype: 'component',
                flex: 1
            },
            Ext.merge({}, cancelBtn || {}, this.getCancelBtn(cancelBtn)),
            Ext.merge({}, saveBtn || {}, this.getSaveBtn(saveBtn, formBind))
        ];
    },
    /**
     * Get form buttons bar with "Close" button
     *
     * CMDBuildUI.util.administration.helper.FormHelper.getCloseButton()
     * use formBind = false for ignore form bind
     *
     *
     * @param {Boolean} [formBind=true] shortcut form saveBtn.saveBtn AND saveBtn.disabled
     * @param {Ext.button.Button} [saveBtn] config
     * @param {Ext.button.Button} [cancelBtn] config
     *
     * @returns {Ext.button.Button[]} Array of button items
     */
    getCloseButton: function (closeBtn) {
        return [
            {
                xtype: 'component',
                flex: 1
            },
            Ext.merge({}, closeBtn || {}, this.getCloseBtn(closeBtn))
        ];
    },
    /**
     * Get form buttons bar with "Save", "Save and add" and "Cancel" buttons
     *
     * CMDBuildUI.util.administration.helper.FormHelper.getSaveAndAddCancelButtons(ignoreFormBind)
     * use formBind = false for ignore form bind
     *
     * @param {Boolean} [formBind=true]
     *
     * @returns {Array} Array of button items
     */
    getSaveAndAddCancelButtons: function (formBind, saveBtn, saveAndAddBtn, cancelBtn) {
        formBind = typeof formBind === 'undefined' || formBind === true ? true : false;

        return [
            {
                xtype: 'component',
                flex: 1
            },
            Ext.merge({}, cancelBtn || {}, this.getCancelBtn(cancelBtn)),
            Ext.merge({}, saveAndAddBtn || {}, this.getSaveAndAddBtn(saveAndAddBtn, formBind)),
            Ext.merge({}, saveBtn || {}, this.getSaveBtn(saveBtn, formBind))
        ];
    },

    /**
     * Get form buttons bar with "Save", "Save and edit" and "Cancel" buttons
     *
     * CMDBuildUI.util.administration.helper.FormHelper.getSaveAndEditCancelButtons(ignoreFormBind)
     * use formBind = false for ignore form bind
     *
     * @param {Boolean} [formBind=true]
     *
     * @returns {Array} Array of button items
     */
    getSaveAndEditCancelButtons: function (formBind, saveBtn, saveAndEditBtn, cancelBtn) {
        formBind = typeof formBind === 'undefined' || formBind === true ? true : false;

        return [
            {
                xtype: 'component',
                flex: 1
            },
            Ext.merge({}, cancelBtn || {}, this.getCancelBtn(cancelBtn)),
            Ext.merge({}, saveAndEditBtn || {}, this.getSaveAndEditBtn(saveAndEditBtn, formBind)),
            Ext.merge({}, saveBtn || {}, this.getSaveBtn(saveBtn, formBind))
        ];
    },

    /**
     *
     * @param {Object|Ext.button.Button} okProperties
     * @param {Object|Ext.button.Button} closeProperties
     */
    getOkCloseButtons: function (okProperties, closeProperties) {
        var okButton = this.getOkBtn(okProperties);

        var closeButton = this.getCloseBtn(closeProperties);

        return [
            {
                xtype: 'component',
                flex: 1
            },
            closeButton,
            okButton
        ];
    },

    /**
     * Get form buttons bar with "Save", "Save and add" and "Cancel" buttons
     *
     * CMDBuildUI.util.administration.helper.FormHelper.getSaveAndAddCancelButtons(ignoreFormBind)
     * use formBind = false for ignore form bind
     *
     * @param {Boolean} [formBind=true]
     *
     * @returns {Array} Array of button items
     */
    getPrevNextSaveCancelButtons: function (formBind, prevBtn, nextBtn, saveBtn, cancelBtn) {
        formBind = typeof formBind === 'undefined' || formBind === true ? true : false;

        return [
            Ext.merge({}, prevBtn || {}, {
                text: CMDBuildUI.locales.Locales.administration.common.actions.prev,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.common.actions.prev'
                },
                itemId: 'prevBtn',
                ui: 'administration-secondary-action-small'
            }),
            Ext.merge({}, nextBtn || {}, {
                text: CMDBuildUI.locales.Locales.administration.common.actions.next,
                ui: 'administration-secondary-action-small',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.common.actions.next'
                },
                itemId: 'nextBtn'
            }),
            {
                xtype: 'component',
                flex: 1
            },
            Ext.merge({}, cancelBtn || {}, {
                text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
                ui: 'administration-secondary-action-small',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.common.actions.cancel'
                },
                itemId: 'cancelBtn'
            }),
            Ext.merge({}, saveBtn || {}, {
                text: CMDBuildUI.locales.Locales.administration.common.actions.save,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.common.actions.save'
                },
                formBind: formBind,
                disabled: formBind,
                itemId: 'saveBtn',
                ui: 'administration-action-small'
            })
        ];
    },

    /**
     * Open localization pop up
     * @param {String} translationCode the translation code
     * @param {String} [action="EDIT"] open popup in EDIT or VIEW mode.
     * @param {String} localeVmObject
     * @param {Object} vm
     * @param {Boolen} denyAutosave
     * @param {String} [editorType="textfield"]
     *
     * @returns {Ext.panel.Panel} popup panel generated with CMDBuildUI.util.Utilities.openPopup()
     */
    openLocalizationPopup: function (translationCode, action, localeVmObject, vm, denyAutosave, editorType) {
        localeVmObject = localeVmObject || 'theTranslation';
        if (translationCode.length) {
            if (!action) {
                action = CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
            }
            var popupId = Ext.String.format('popup-localization-{0}', translationCode.replace(/.|_| /g, '-'));

            var vmData = {
                action: action,
                actions: {
                    edit: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    add: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    view: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                },
                translationCode: translationCode,
                denyAutosave: denyAutosave
            };

            vmData[localeVmObject] = vm.get(localeVmObject);

            var content = {
                xtype: 'administration-localization-localizecontent',
                scrollable: 'y',
                editorType: editorType || 'textfield',
                theVmObject: localeVmObject,

                viewModel: {
                    data: vmData
                }
            };
            // custom panel listeners
            var listeners = {
                setlocalesstore: function (store) {
                    if (vm && vm.get) {
                        vm.set(localeVmObject ? localeVmObject : 'theTranslation', store);
                        CMDBuildUI.util.Utilities.closePopup(popupId);
                    }
                },
                /**
                 * @param {Ext.panel.Panel} panel
                 * @param {Object} eOpts
                 */
                close: function (panel, eOpts) {
                    CMDBuildUI.util.Utilities.closePopup(popupId);
                }
            };
            // create and open panel
            var popup = CMDBuildUI.util.Utilities.openPopup(
                popupId,
                CMDBuildUI.locales.Locales.administration.common.strings.localization,
                content,
                listeners,
                {
                    ui: 'administration-actionpanel',
                    width: editorType === 'htmleditor' ? '640px' : '450px',
                    height: editorType === 'htmleditor' ? '80%' : '450px',
                    draggable: true
                }
            );

            return popup;
        }
    },

    /**
     *
     * @param {Object} tools
     * @param {Boolean} tools.edit
     * @param {Boolean} tools.view
     * @param {Boolean} tools.open alias of tools.view
     * @param {Boolean} tools.clone
     * @param {Boolean} tools.delete
     * @param {Boolean} tools.activeToggle
     * @param {Boolean} tools.download
     *
     * @param {String} testid
     *
     * @param {String} viewModelKey
     *
     * @param {Object[]|Ext.panel.Tool[]} [beforeTools]
     * @param {Object[]|Ext.panel.Tool[]} [beforeTbfill]
     * @param {Object[]|Ext.panel.Tool[]} [afterTools]
     *
     * @returns {Ext.panel.Tool[]}
     */
    getTools: function (tools, testid, viewModelKey, beforeTbfill, beforeTools, afterTools) {
        var _tools = [
            {
                // it will set the correct heigth
                xtype: 'button',
                itemId: 'spacer',
                style: {
                    visibility: 'hidden'
                }
            }
        ];
        if (beforeTbfill && beforeTbfill.length) {
            _tools = Ext.Array.merge([], beforeTbfill);
        }
        _tools.push({
            xtype: 'tbfill' // it will move the others tools to right
        });

        if (beforeTools && beforeTools.length) {
            _tools = Ext.Array.merge(_tools, beforeTools);
        }

        if (tools.edit) {
            _tools.push(this.getEditTool(tools.edit, testid, viewModelKey));
        }
        if (tools.view || tools.open) {
            _tools.push(this.getViewTool(tools.view || tools.open, testid, viewModelKey));
        }
        if (tools.download) {
            _tools.push(this.getDownloadTool(tools.download, testid, viewModelKey));
        }
        if (tools.clone) {
            _tools.push(this.getCloneTool(tools.clone, testid, viewModelKey));
        }
        if (tools.sql) {
            _tools.push(this.getSqlTool(tools.sql, testid, viewModelKey));
        }
        if (tools.delete) {
            _tools.push(this.getDeleteTool(tools.delete, testid, viewModelKey));
        }
        if (viewModelKey && tools.activeToggle) {
            _tools.push({
                xtype: 'container',
                cls: 'x-tool-administration-tabandtools',
                bind: {
                    hidden: '{!actions.view}'
                },
                items: [
                    this.getEnableTool(tools.activeToggle, testid, viewModelKey),
                    this.getDisableTool(tools.activeToggle, testid, viewModelKey)
                ]
            });
        }
        if (tools.linkToContextTool) {
            _tools.push(this.getLinkToContextTool(testid, viewModelKey));
        }
        if (afterTools && afterTools.length) {
            _tools = Ext.Array.merge(_tools, afterTools);
        }
        return _tools;
    },

    privates: {
        _camelize: function (str) {
            return str.replace(/(?:^\w|[A-Z]|\b\w|\s+)/g, function (match, index) {
                if (+match === 0) return ''; // or if (/\s+/.test(match)) for white spaces
                return index == 0 ? match.toLowerCase() : match.toUpperCase();
            });
        },

        getSaveBtn: function (saveBtn, formBind) {
            return Ext.apply(
                {
                    text: CMDBuildUI.locales.Locales.administration.common.actions.save,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.actions.save'
                    },
                    formBind: (saveBtn && saveBtn.formBind) || formBind,
                    disabled: (saveBtn && saveBtn.disabled) || formBind,
                    autoEl: {
                        'data-testid': Ext.String.format(
                            'administration-forms{0}-saveBtn',
                            saveBtn && saveBtn.testid ? '-' + saveBtn.testid : ''
                        )
                    },
                    itemId: 'saveBtn',
                    ui: 'administration-action-small'
                },
                saveBtn || {}
            );
        },
        getSaveAndAddBtn: function (saveBtn, formBind) {
            return Ext.apply(
                {
                    text: CMDBuildUI.locales.Locales.administration.common.actions.saveandadd,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.actions.saveandadd'
                    },
                    formBind: (saveBtn && saveBtn.formBind) || formBind,
                    disabled: (saveBtn && saveBtn.disabled) || formBind,
                    autoEl: {
                        'data-testid': Ext.String.format(
                            'administration-forms{0}-saveAndAddBtn',
                            saveBtn && saveBtn.testid ? '-' + saveBtn.testid : ''
                        )
                    },
                    itemId: 'saveAndAddBtn',
                    ui: 'administration-primary-outline-small'
                },
                saveBtn || {}
            );
        },
        getSaveAndEditBtn: function (saveBtn, formBind) {
            return Ext.apply(
                {
                    text: CMDBuildUI.locales.Locales.administration.common.actions.saveandedit,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.actions.saveandedit'
                    },
                    formBind: (saveBtn && saveBtn.formBind) || formBind,
                    disabled: (saveBtn && saveBtn.disabled) || formBind,
                    autoEl: {
                        'data-testid': Ext.String.format(
                            'administration-forms{0}-saveAndEditBtn',
                            saveBtn && saveBtn.testid ? '-' + saveBtn.testid : ''
                        )
                    },
                    itemId: 'saveAndEditBtn',
                    ui: 'administration-primary-outline-small'
                },
                saveBtn || {}
            );
        },
        getCancelBtn: function (cancelBtn) {
            return Ext.apply(
                {
                    xtype: 'button',
                    text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
                    ui: 'administration-secondary-action-small',
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.actions.cancel'
                    },
                    autoEl: {
                        'data-testid': Ext.String.format(
                            'administration-forms{0}-cancelBtn',
                            cancelBtn && cancelBtn.testid ? '-' + cancelBtn.testid : ''
                        )
                    },
                    itemId: 'cancelBtn',
                    listeners: (cancelBtn && cancelBtn.listeners) || {
                        mouseover: function () {
                            if (this.up('form')) {
                                var invalidFields = CMDBuildUI.util.administration.helper.FormHelper.getInvalidFields(
                                    this.up('form').form
                                );
                                Ext.Array.forEach(invalidFields, function (field) {
                                    CMDBuildUI.util.Logger.log(
                                        Ext.String.format('{0} is invalid', field.itemId),
                                        CMDBuildUI.util.Logger.levels.debug
                                    );
                                });
                            }
                        }
                    }
                },
                cancelBtn || {}
            );
        },
        getCloseBtn: function (closeProperties) {
            return Ext.applyIf(closeProperties || {}, {
                text: CMDBuildUI.locales.Locales.administration.common.actions.close,
                ui: 'administration-secondary-action-small',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.common.actions.close'
                },
                autoEl: {
                    'data-testid': 'administration-forms-closeBtn'
                },
                itemId: 'closeBtn'
            });
        },

        getOkBtn: function (okProperties) {
            return Ext.Object.merge(
                {},
                {
                    text: CMDBuildUI.locales.Locales.administration.common.actions.ok,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.actions.ok'
                    },
                    formBind: false,
                    disabled: false,
                    itemId: 'okBtn',
                    ui: 'administration-action-small',
                    autoEl: {
                        'data-testid': 'administration-forms-okBtn'
                    }
                },
                okProperties
            );
        },
        getEditTool: function (config, testid, viewModelKey) {
            return Ext.merge(
                {},
                {
                    xtype: 'tool',
                    itemId: 'editBtn',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
                    },
                    cls: 'administration-tool',
                    autoEl: {
                        'data-testid': Ext.String.format('administration-{0}-editBtn', testid)
                    },
                    bind: {
                        hidden: '{!actions.view}',
                        disabled: Ext.String.format(
                            '{{0}{1}_can_modify === false || toolAction._canUpdate === false}',
                            viewModelKey,
                            viewModelKey ? '.' : ''
                        )
                    }
                },
                !Ext.isBoolean(config) ? config : {}
            );
        },

        getViewTool: function (config, testid, viewModelKey) {
            return Ext.merge(
                {},
                {
                    xtype: 'tool',
                    itemId: 'openBtn',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.open,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.open'
                    },
                    cls: 'administration-tool',
                    autoEl: {
                        'data-testid': Ext.String.format('administration-{0}-openBtn', testid)
                    },
                    bind: {
                        hidden: '{hideOpenBtn}',
                        disabled: '{toolAction._canOpen === false}'
                    }
                },
                !Ext.isBoolean(config) ? config : {}
            );
        },

        getCloneTool: function (config, testid, viewModelKey) {
            return Ext.merge(
                {},
                {
                    xtype: 'tool',
                    itemId: 'cloneBtn',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('clone', 'regular'),
                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.clone,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.clone'
                    },
                    cls: 'administration-tool',
                    autoEl: {
                        'data-testid': Ext.String.format('administration-{0}-cloneBtn', testid)
                    },
                    bind: {
                        hidden: '{!actions.view}',
                        disabled: Ext.String.format(
                            '{{0}{1}_can_modify === false || toolAction._canClone === false}',
                            viewModelKey,
                            viewModelKey ? '.' : ''
                        )
                    }
                },
                !Ext.isBoolean(config) ? config : {}
            );
        },

        getDeleteTool: function (config, testid, viewModelKey) {
            return Ext.merge(
                {},
                {
                    xtype: 'tool',
                    itemId: 'deleteBtn',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'regular'),
                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.delete,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.delete'
                    },
                    cls: 'administration-tool',
                    autoEl: {
                        'data-testid': Ext.String.format('administration-{0}-deleteBtn', testid)
                    },
                    bind: {
                        hidden: '{!actions.view}',
                        disabled: Ext.String.format(
                            '{{0}{1}_can_modify === false || toolAction._canDelete === false}',
                            viewModelKey,
                            viewModelKey ? '.' : ''
                        )
                    },
                    listeners: {
                        disable: function (tool) {
                            this.setTooltip(
                                CMDBuildUI.locales.Locales.administration.common.messages.youarenotabletodelete
                            );
                        },
                        enable: function (tool) {
                            this.setTooltip(CMDBuildUI.locales.Locales.administration.common.actions.delete);
                        }
                    }
                },
                !Ext.isBoolean(config) ? config : {}
            );
        },
        getSqlTool: function (config, testid, viewModelKey) {
            return Ext.merge(
                {},
                {
                    xtype: 'tool',
                    itemId: 'sqlBtn',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('database', 'solid'),
                    tooltip: CMDBuildUI.locales.Locales.administration.reports.tooltips.viewsql, // View report sql
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.reports.tooltips.viewsql'
                    },
                    cls: 'administration-tool',
                    autoEl: {
                        'data-testid': Ext.String.format('administration-{0}-sqlBtn', testid)
                    }
                },
                !Ext.isBoolean(config) ? config : {}
            );
        },
        getEnableTool: function (config, testid, viewModelKey) {
            return Ext.merge(
                {},
                {
                    xtype: 'tool',
                    itemId: 'enableBtn',
                    hidden: true,
                    disabled: true,
                    cls: 'administration-tool',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('check-circle', 'regular'),
                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.enable,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.enable'
                    },
                    autoEl: {
                        'data-testid': Ext.String.format('administration-{0}-enableBtn', testid)
                    },
                    bind: {
                        hidden: Ext.String.format('{{0}.{1}}', viewModelKey, config.activeField || 'active'),
                        disabled: Ext.String.format(
                            '{{0}{1}_can_modify === false || toolAction._canActiveToggle === false}',
                            viewModelKey,
                            viewModelKey ? '.' : ''
                        )
                    },
                    listeners: {
                        disable: function (tool) {
                            this.setTooltip(
                                CMDBuildUI.locales.Locales.administration.common.messages.youarenotabletochangeactive
                            );
                        },
                        enable: function (tool) {
                            this.setTooltip(CMDBuildUI.locales.Locales.administration.common.actions.enable);
                        }
                    }
                },
                !Ext.isBoolean(config) ? config : {}
            );
        },

        getDisableTool: function (config, testid, viewModelKey) {
            return Ext.merge(
                {},
                {
                    xtype: 'tool',
                    itemId: 'disableBtn',
                    cls: 'administration-tool',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ban', 'solid'),
                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.disable,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.disable'
                    },
                    hidden: true,
                    autoEl: {
                        'data-testid': Ext.String.format('administration-{0}-disableBtn', testid)
                    },
                    bind: {
                        hidden: Ext.String.format('{!{0}.{1}}', viewModelKey, config.activeField || 'active'),
                        disabled: Ext.String.format(
                            '{{0}{1}_can_modify === false || toolAction._canActiveToggle === false}',
                            viewModelKey,
                            viewModelKey ? '.' : ''
                        )
                    },
                    listeners: {
                        disable: function (tool) {
                            this.setTooltip(
                                CMDBuildUI.locales.Locales.administration.common.messages.youarenotabletochangeactive
                            );
                        },
                        enable: function (tool) {
                            this.setTooltip(CMDBuildUI.locales.Locales.administration.common.actions.disable);
                        }
                    }
                },
                !Ext.isBoolean(config) ? config : {}
            );
        },

        getDownloadTool: function (config, testid, viewModelKey) {
            return Ext.merge(
                {},
                {
                    xtype: 'button',
                    align: 'right',
                    itemId: 'downloadBtn',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('download', 'solid'),
                    cls: 'administration-tool  btn-as-tool',
                    height: '20px',
                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.download,
                    visible: false,
                    autoEl: {
                        'data-testid': Ext.String.format('administration-{0}-downloadBtn', testid)
                    },
                    bind: {
                        hidden: '{!actions.view}',
                        disabled: '{toolAction._canDownload === false}'
                    }
                },
                !Ext.isBoolean(config) ? config : {}
            );
        },

        getLinkToContextTool: function (config, testid, viewModelKey) {
            return Ext.merge(
                {},
                {
                    xtype: 'tool',
                    itemId: 'linkToContextBtn',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sign-in-alt', 'solid'),
                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.viewallitemproperties,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.viewallitemproperties'
                    },
                    cls: 'administration-tool',
                    autoEl: {
                        'data-testid': Ext.String.format('administration-{0}-linkToContextBtn', testid)
                    },
                    hidden: true,
                    bind: {
                        hidden: '{!actions.view}',
                        disabled: '{toolAction._canLinkTo === false}'
                    }
                },
                !Ext.isBoolean(config) ? config : {}
            );
        }
    },

    getDockedTopBar: function (view) {
        return Ext.create('CMDBuildUI.components.administration.toolbars.FormToolbar', {
            dock: 'top',
            padding: '6 0 6 8',
            borderBottom: 0,
            itemId: 'toolbarscontainer',
            style: 'border-bottom-width:0!important',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                {},
                view.getSingularName(),
                view.getTheCurrentObject(),
                [
                    {
                        xtype: 'button',
                        text: CMDBuildUI.locales.Locales.administration[view.getPluralName()].texts[
                            'add' + view.getSingularName()
                        ], // Add customcomponent

                        ui: 'administration-action-small',
                        itemId: 'addBtn',
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
                        autoEl: {
                            'data-testid': Ext.String.format(
                                'administration-{0}-toolbar-addBtn',
                                view.getSingularName()
                            )
                        }
                    },
                    {
                        xtype: 'textfield',
                        name: 'search',
                        width: 250,
                        emptyText:
                            CMDBuildUI.locales.Locales.administration[view.getPluralName()].emptytexts[
                                'search' + view.getPluralName()
                            ], // Search customcomponent...
                        localized: {
                            emptyText: Ext.String.format(
                                'CMDBuildUI.locales.Locales.administration.{0}.emptytexts.search{1}',
                                view.getPluralName(),
                                view.getPluralName()
                            )
                        },
                        cls: 'administration-input',
                        reference: 'searchtext',
                        itemId: 'searchtext',
                        bind: {
                            value: '{search.value}',
                            hidden: '{!canFilter}'
                        },
                        listeners: {
                            specialkey: 'onSearchSpecialKey'
                        },
                        triggers: {
                            search: {
                                cls: Ext.baseCSSPrefix + 'form-search-trigger',
                                handler: 'onSearchSubmit',
                                autoEl: {
                                    'data-testid': Ext.String.format(
                                        'administration-{0}-toolbar-search-form-search-trigger',
                                        view.getSingularName()
                                    )
                                }
                            },
                            clear: {
                                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                handler: 'onSearchClear',
                                autoEl: {
                                    'data-testid': Ext.String.format(
                                        'administration-{0}-toolbar-search-form-clear-trigger',
                                        view.getSingularName()
                                    )
                                }
                            }
                        },
                        autoEl: {
                            'data-testid': Ext.String.format(
                                'administration-{0}-toolbar-search-form',
                                view.getSingularName()
                            )
                        }
                    },
                    {
                        xtype: 'tbfill'
                    }
                ],
                null,
                [
                    {
                        xtype: 'tbtext',
                        hidden: true,
                        bind: {
                            hidden: Ext.String.format('{!{0}.description}', view.getTheCurrentObject()),
                            html: Ext.String.format(
                                '{componentTypeName}: <b data-testid="administration-{0}-description">{{1}.description}</b>',

                                view.getPluralName(),
                                view.getTheCurrentObject()
                            )
                        }
                    }
                ]
            )
        });
    },

    getDockedToolBar: function (view) {
        return Ext.create('CMDBuildUI.components.administration.toolbars.FormToolbar', {
            // xtype: 'components-administration-toolbars-formtoolbar',
            region: 'top',
            borderBottom: 0,
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                {
                    edit: true, // #editBtn set true for show the button
                    delete: true, // #deleteBtn set true for show the button
                    activeToggle: true, // #enableBtn and #disableBtn set true for show the buttons
                    download: true // #downloadBtn set true for show the buttons
                },

                /* testId */
                view.getSingularName(),
                view.getTheCurrentObject(),

                /* add custom tools[] on the left of the bar */
                [],

                /* add custom tools[] before #editBtn*/
                [],

                /* add custom tools[] after at the end of the bar*/
                []
            ),
            bind: {
                hidden: '{formtoolbarHidden}'
            }
        });
    },

    getDockedFormButtons: function (buttonsUtilFormFn) {
        return Ext.create('Ext.toolbar.Toolbar', {
            // xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,

            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper[buttonsUtilFormFn](true)
        });
    },

    /**
     *
     * @param {Ext.form.field.Base} item
     * @returns {String} label
     */
    getFieldcontainerLabel: function (item) {
        var itemUp = item.up('fieldcontainer');
        if (itemUp && itemUp.getFieldLabel) {
            var label = itemUp.getFieldLabel();
            if (!label) {
                return this.getFieldcontainerLabel(itemUp);
            }
            return label;
        } else {
            return item.getFieldLabel();
        }
    },

    /**
     *
     * @param {Ext.form.field.Base[]} invalid
     */
    showInvalidFieldsMessage: function (invalid) {
        var invalidFields = [];
        Ext.Array.forEach(invalid.items, function (item) {
            invalidFields.push(
                item.up
                    ? CMDBuildUI.util.administration.helper.FormHelper.getFieldcontainerLabel(item)
                    : Ext.String.capitalize(item.name)
            );
        });
        invalidFields = Ext.Array.unique(invalidFields);
        CMDBuildUI.util.Notifier.showWarningMessage(
            '<strong>Validation Error:</strong></br>' + invalidFields.join('</br>')
        );
    },

    /**
     *
     * @param {Funtion} [cb] callback
     * @param {String} [fnName="clearValue"] clearValue|reset
     */
    getClearComboTrigger: function (cb, fnName) {
        if (Ext.isString(cb)) {
            fnName = cb;
            cb = undefined;
        }
        var hasFnName = Ext.isString(fnName) ? true : false;
        return {
            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
            handler: function (combo, trigger, eOpts) {
                if (combo.isExpanded) {
                    combo.collapse();
                }

                if (hasFnName && combo[fnName]) {
                    combo[fnName].call(combo);
                } else {
                    if (combo.clearValue) {
                        combo.clearValue();
                    } else if (combo.reset) {
                        combo.reset();
                    }
                }

                if (cb && Ext.isFunction(cb)) {
                    Ext.callback(cb, combo, [combo]);
                }
            },
            autoEl: {
                'data-testid': 'administration-input-clear-trigger'
            }
        };
    }
});
