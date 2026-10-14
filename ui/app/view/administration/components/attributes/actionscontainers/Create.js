Ext.define('CMDBuildUI.view.administration.components.attributes.actionscontainers.Create', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-components-attributes-actionscontainers-create',
    requires: [
        'CMDBuildUI.view.administration.components.attributes.actionscontainers.CardController',
        'CMDBuildUI.view.administration.components.attributes.actionscontainers.CardModel'
    ],
    controller: 'administration-components-attributes-actionscontainers-card',
    viewModel: {
        type: 'administration-components-attributes-actionscontainers-card'
    },
    config: {
        objectType: null,
        objectTypeName: null,
        /**
         * @cfg {Object[]}
         *
         * Can set default values for any of the attributes. An object can be:
         * `{attribute: 'attribute name', value: 'default value', editable: true|false}`
         * used for all attributes or
         * `{domain: 'domain name', value: 'default value', editable: true|false}`
         * used to set default values for references fields.
         */
        defaultValues: null
    },

    modelValidation: true,
    autoScroll: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    bubbleEvents: ['itemcreated', 'cancelcreation'],
    items: [
        {
            hidden: true,
            bind: {
                hidden: '{!formulaWarningMessage}'
            },
            margin: 10,
            xtype: 'container',
            ui: 'messagewarning',
            items: [
                {
                    flex: 1,
                    ui: 'custom',
                    xtype: 'panel',
                    bind: {
                        html: '{formulaWarningMessage}'
                    }
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'textfield',
                            // vtype: 'alphanum',
                            allowBlank: false,
                            vtype: 'attributeNameValidation',
                            maxLength: 20,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                            },
                            name: 'name',
                            autoEl: {
                                'data-testid': 'attribute-name_input'
                            },
                            bind: {
                                disabled: '{actions.edit}',
                                value: '{theAttribute.name}'
                            },
                            listeners: {
                                change: function (input, newVal, oldVal) {
                                    CMDBuildUI.util.administration.helper.FieldsHelper.copyTo(
                                        input,
                                        newVal,
                                        oldVal,
                                        '[name="description"]'
                                    );
                                }
                            }
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'textfield',
                            name: 'description',
                            allowBlank: false,
                            autoEl: {
                                'data-testid': 'attribute-description_input'
                            },
                            bind: {
                                value: '{theAttribute.description}'
                            },
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                            },
                            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                            labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                            labelToolIconClick: 'onTranslateClick'
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'combo',
                            itemId: 'groupfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group'
                            },
                            name: 'group',
                            autoEl: {
                                'data-testid': 'attribute-group_input'
                            },
                            editable: false,
                            forceSelect: true,
                            allowBlank: true,
                            displayField: 'description',
                            valueField: 'name',
                            hidden: true,
                            bind: {
                                value: '{theAttribute.group}',
                                store: '{attributeGroupStore}',
                                hidden: '{isGroupHidden}'
                            },
                            triggers: {
                                clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                            }
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'combo',
                            itemId: 'attributeMode',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mode,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mode'
                            },
                            name: 'mode',
                            autoEl: {
                                'data-testid': 'attribute-mode_input'
                            },
                            clearFilterOnBlur: false,
                            anyMatch: true,
                            autoSelect: true,
                            forceSelection: true,
                            allowBlank: false,
                            displayField: 'label',
                            valueField: 'value',
                            hidden: true,
                            bind: {
                                value: '{theAttribute.mode}',
                                store: '{attributeModeStore}',
                                hidden: Ext.String.format(
                                    '{theAttribute.type == "{0}"}',
                                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula
                                )
                            },

                            renderer: CMDBuildUI.util.administration.helper.RendererHelper.getAttributeMode,
                            /**
                             * Returns whether or not the widget value is currently valid by {@link #getErrors validating} the
                             * {@link #processRawValue processed raw value} of the widget. **Note**: {@link #disabled} buttons are
                             * always treated as valid.
                             *
                             * @return {Boolean} True if the value is valid, else false
                             */
                            isValid: function () {
                                return this.activeErrors && this.activeErrors.length
                                    ? false
                                    : this.validateValue(this.getValue());
                            }
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.25,
                            itemId: 'attributeShowInGrid',
                            xtype: 'checkbox',
                            name: 'showInGrid',
                            autoEl: {
                                'data-testid': 'attribute-showInGrid_input'
                            },
                            hidden: true,
                            bind: {
                                value: '{theAttribute.showInGrid}',
                                fieldLabel: '{showInGridLabel}',
                                hidden: '{showInGridHidden}'
                            },
                            listeners: {
                                hide: function () {
                                    this.setValue(false);
                                }
                            },
                            isValid: function () {
                                if (this.activeErrors && this.activeErrors.length) {
                                    this.displayEl.addCls('x-form-invalid-field');
                                    return false;
                                } else {
                                    this.displayEl.removeCls('x-form-invalid-field');
                                    return this.validateValue(this.getValue());
                                }
                            }
                        },
                        {
                            columnWidth: 0.25,
                            xtype: 'checkbox',
                            itemId: 'attributeShowInReducedGrid',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showinreducedgrid,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showinreducedgrid'
                            },
                            name: 'showInReducedGrid',
                            autoEl: {
                                'data-testid': 'attribute-showInReducedGrid_input'
                            },
                            bind: {
                                value: '{theAttribute.showInReducedGrid}',
                                hidden: Ext.String.format(
                                    '{objectType == "{0}" || objectType == "{1}"}',
                                    CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                                    CMDBuildUI.util.helper.ModelHelper.objecttypes.domain.charAt(0).toUpperCase() +
                                        CMDBuildUI.util.helper.ModelHelper.objecttypes.domain.slice(1)
                                )
                            },
                            hidden: true,
                            listeners: {
                                hide: function () {
                                    this.setValue(false);
                                }
                            },
                            isValid: function () {
                                if (this.activeErrors && this.activeErrors.length) {
                                    this.displayEl.addCls('x-form-invalid-field');
                                    return false;
                                } else {
                                    this.displayEl.removeCls('x-form-invalid-field');
                                    return this.validateValue(this.getValue());
                                }
                            }
                        },
                        {
                            columnWidth: 0.25,
                            xtype: 'checkbox',
                            itemId: 'attributeExcludeFromGrid',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.hideingrid,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.hideingrid'
                            },
                            name: 'excludeFromGrid',
                            autoEl: {
                                'data-testid': 'attribute-excludeFromGrid_input'
                            },
                            bind: {
                                value: '{theAttribute.hideInGrid}',
                                hidden: Ext.String.format(
                                    '{objectType == "{0}" || objectType == "{1}"}',
                                    CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                                    CMDBuildUI.util.helper.ModelHelper.objecttypes.domain.charAt(0).toUpperCase() +
                                        CMDBuildUI.util.helper.ModelHelper.objecttypes.domain.slice(1)
                                )
                            },
                            hidden: true,
                            listeners: {
                                hide: function () {
                                    this.setValue(false);
                                }
                            },
                            isValid: function () {
                                if (this.activeErrors && this.activeErrors.length) {
                                    this.displayEl.addCls('x-form-invalid-field');
                                    return false;
                                } else {
                                    this.displayEl.removeCls('x-form-invalid-field');
                                    return this.validateValue(this.getValue());
                                }
                            }
                        },
                        {
                            columnWidth: 0.25,
                            xtype: 'checkbox',
                            itemId: 'attributeDomainKey',
                            hidden: true,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.domainkey,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.domainkey'
                            },
                            name: 'domainKey',
                            autoEl: {
                                'data-testid': 'attribute-domainKey_input'
                            },
                            bind: {
                                value: '{theAttribute.domainKey}',
                                hidden: '{!isNN}'
                            },
                            listeners: {
                                hide: function () {
                                    this.setValue(false);
                                }
                            },
                            isValid: function () {
                                if (this.activeErrors && this.activeErrors.length) {
                                    this.displayEl.addCls('x-form-invalid-field');
                                    return false;
                                } else {
                                    this.displayEl.removeCls('x-form-invalid-field');
                                    return this.validateValue(this.getValue());
                                }
                            }
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'checkbox',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unique,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unique'
                            },
                            name: 'unique',
                            autoEl: {
                                'data-testid': 'attribute-unique_input'
                            },
                            bind: {
                                value: '{theAttribute.unique}',
                                hidden: Ext.String.format(
                                    '{theAttribute.type == "{0}"}',
                                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula
                                )
                            },
                            listeners: {
                                hide: function () {
                                    this.setValue(false);
                                }
                            },
                            hidden: true
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'checkbox',
                            itemId: 'attributeMandatory',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mandatory,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mandatory'
                            },
                            name: 'mandatory',
                            autoEl: {
                                'data-testid': 'attribute-mandatory_input'
                            },
                            bind: {
                                value: '{theAttribute.mandatory}',
                                hidden: Ext.String.format(
                                    '{isMandatoryHidden || theAttribute.type == "{0}"}',
                                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula
                                )
                            },
                            listeners: {
                                hide: function () {
                                    this.setValue(false);
                                }
                            },
                            hidden: true
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'checkbox',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.hideinfilter,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.hideinfilter'
                            },
                            name: 'hideInFilter',
                            autoEl: {
                                'data-testid': 'attribute-hideInFilter_input'
                            },
                            hidden: true,
                            bind: {
                                hidden: Ext.String.format(
                                    '{objectType == "dmsmodel" || objectType == "Domain" || theAttribute.type == "{0}"}',
                                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula
                                ),
                                value: '{theAttribute.hideInFilter}'
                            }
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'checkbox',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.sortingenabled,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.sortingenabled'
                            },
                            name: 'sortingEnabled',
                            autoEl: {
                                'data-testid': 'attribute-enableSorting_input'
                            },
                            hidden: true,
                            bind: {
                                hidden: Ext.String.format(
                                    '{objectType == "dmsmodel" || objectType == "Domain" || theAttribute.hideInFilter || theAttribute.type == "{0}"}',
                                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula
                                ),
                                value: '{theAttribute.sortingEnabled}'
                            },
                            getErrors: function (value) {
                                if (value) {
                                    return;
                                }
                                var me = this,
                                    errors = [],
                                    vm = me.lookupViewModel(),
                                    attributeOwner = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                        vm.get('objectTypeName')
                                    );
                                if (attributeOwner._defaultOrder) {
                                    var isInDefaultSOrder = attributeOwner._defaultOrder.findRecord(
                                        'attribute',
                                        vm.get('theAttribute.name')
                                    );
                                    if (isInDefaultSOrder) {
                                        errors.push(
                                            CMDBuildUI.locales.Locales.administration.attributes.strings
                                                .mandatoryinactivemessage
                                        );
                                        if (errors.length) {
                                            me.displayEl.addCls('x-form-invalid-field');
                                        } else {
                                            me.displayEl.removeCls('x-form-invalid-field');
                                        }
                                    }
                                }
                                return errors;
                            }
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'checkbox',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                            },
                            name: 'active',
                            autoEl: {
                                'data-testid': 'attribute-active_input'
                            },
                            bind: {
                                value: '{theAttribute.active}',
                                disabled: '{activeInputDisabled}'
                            },
                            listeners: {
                                afterrender: function (input) {
                                    var vm = input.lookupViewModel();
                                    vm.bind(
                                        {
                                            isMandatory: '{theAttribute.mandatory}',
                                            isActive: '{theAttribute.active}'
                                        },
                                        function (data) {
                                            input.getErrors();
                                            input.up('form').isValid();
                                        }
                                    );
                                }
                            },
                            getErrors: function (value) {
                                var me = this,
                                    errors = [],
                                    vm = me.lookupViewModel();
                                if (!vm.get('theAttribute.active') && vm.get('theAttribute.mandatory')) {
                                    errors.push(
                                        CMDBuildUI.locales.Locales.administration.attributes.strings
                                            .mandatoryinactivemessage
                                    );
                                }
                                if (errors.length) {
                                    me.displayEl.addCls('x-form-invalid-field');
                                } else {
                                    me.displayEl.removeCls('x-form-invalid-field');
                                }
                                return errors;
                            }
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'textfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.synctodmsattr,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.synctodmsattr'
                            },
                            name: 'syncToDmsAttr',
                            hidden: true,
                            bind: {
                                hidden: '{objectType !== "dmsmodel"}',
                                value: '{theAttribute.syncToDmsAttr}'
                            }
                        }
                    ]
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.attributes.titles.typeproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.attributes.titles.typeproperties'
            },
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'combo',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.type,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.type'
                            },
                            name: 'type',
                            autoEl: {
                                'data-testid': 'attribute-type_input'
                            },
                            allowBlank: false,
                            queryMode: 'local',
                            displayField: 'label',
                            valueField: 'value',
                            disabled: true,
                            forceSelection: true,
                            bind: {
                                disabled: '{actions.edit}',
                                value: '{theAttribute.type}',
                                store: '{attributetypesStore}'
                            },
                            listeners: {
                                change: function (combo, newValue, oldValue) {
                                    if (oldValue) {
                                        var vm = combo.lookupViewModel();
                                        switch (newValue) {
                                            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text:
                                                if (vm.get('theAttribute.editorType') === 'HTML') {
                                                    vm.set(
                                                        'theAttribute.textContentSecurity',
                                                        CMDBuildUI.model.Attribute.textContentSecurity.html_safe
                                                    );
                                                } else {
                                                    vm.set(
                                                        'theAttribute.textContentSecurity',
                                                        CMDBuildUI.model.Attribute.textContentSecurity.plaintext
                                                    );
                                                }
                                                break;
                                            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string:
                                                vm.set(
                                                    'theAttribute.textContentSecurity',
                                                    CMDBuildUI.model.Attribute.textContentSecurity.plaintext
                                                );
                                                break;
                                            default:
                                                break;
                                        }
                                        if (newValue !== CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference) {
                                            combo.up('form').down('#attributedomain').clearInvalid();
                                            combo.up('form').down('#attributedomain').isValid();
                                        } else {
                                            combo.up('form').down('#attributedomain').isValid();
                                        }

                                        combo.up('form').form.checkValidity();
                                    }
                                }
                            }
                        }
                    ]
                },
                {
                    // If type is biginteger
                    bind: {
                        hidden: '{!types.isBigInteger}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-bigintegerfields'
                },
                {
                    // If type is boolean
                    bind: {
                        hidden: '{!types.isBoolean}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-booleanfields'
                },
                {
                    // If type is date
                    bind: {
                        hidden: '{!types.isDate}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-datefields'
                },
                {
                    // If type is datetime
                    bind: {
                        hidden: '{!types.isDatetime}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-datetimefields'
                },
                {
                    // If type is decimal
                    bind: {
                        hidden: '{!types.isDecimal}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-decimalfields'
                },
                {
                    // If type is double
                    bind: {
                        hidden: '{!types.isDouble}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-doublefields'
                },
                {
                    // If type is file
                    bind: {
                        hidden: '{!types.isFile}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-filefields',
                    listeners: {
                        show: function () {
                            var form = this.up('form');
                            if (form) {
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    this.down('#dmsCategory'),
                                    false,
                                    form
                                );
                            }
                        },
                        hide: function () {
                            var form = this.up('form');
                            if (form) {
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    this.down('#dmsCategory'),
                                    true,
                                    form
                                );
                                this.down('#dmsCategory').setValue(null);
                            }
                        }
                    }
                },
                {
                    // If type is formula
                    bind: {
                        hidden: '{!types.isFormula}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-formulafields'
                },
                {
                    // If type is foreignKey
                    bind: {
                        hidden: '{!types.isForeignkey}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-foreignkeyfields'
                },
                {
                    // If type is integer
                    bind: {
                        hidden: '{!types.isInteger}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-integerfields'
                },
                {
                    // If type is ip address
                    bind: {
                        hidden: '{!types.isIpAddress}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-ipaddressfields'
                },
                {
                    // If type is lookup
                    bind: {
                        hidden: '{!types.isLookup}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-lookupfields'
                },
                {
                    // If type is reference
                    bind: {
                        hidden: '{!types.isReference}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-referencefields'
                },
                {
                    // If type is string
                    bind: {
                        hidden: '{!types.isString}',
                        theAttribute: '{theAttribute}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-stringfields'
                },
                {
                    // If type is text
                    bind: {
                        hidden: '{!types.isText}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-textfields'
                },
                {
                    // If type is time
                    bind: {
                        hidden: '{!types.isTime}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-timefields'
                },
                {
                    // If type is timestamp
                    bind: {
                        hidden: '{!types.isTimestamp}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-timestampfields'
                },
                {
                    // If type is link
                    bind: {
                        hidden: '{!types.isLink}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-linkfields'
                },
                {
                    // If can show mobile attributes
                    bind: {
                        hidden: '{!canShowMobileAttributes}'
                    },
                    hidden: true,
                    xtype: 'administration-attribute-commons-mobileedtor'
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.attributes.titles.otherproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.attributes.titles.otherproperties'
            },
            bind: {
                hidden: '{isOtherPropertiesHidden}'
            },
            items: [
                {
                    layout: 'column',
                    hidden: true,
                    bind: {
                        hidden: '{!theAttribute}'
                    },
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'aceeditortextarea',
                            allowBlank: true,
                            vmObjectName: 'theAttribute',
                            inputField: 'help',
                            itemId: 'attribute-help',
                            autoEl: {
                                'data-testid': 'attribute-help_input'
                            },
                            options: {
                                mode: 'ace/mode/markdown',
                                readOnly: true
                            },
                            bind: {
                                readOnly: '{actions.view}',
                                config: {
                                    options: {
                                        readOnly: '{actions.view}'
                                    }
                                }
                            },
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.help,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.help'
                            },
                            minHeight: '85px',
                            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('expand', 'solid'),
                            labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.expand,
                            labelToolIconClick: 'onAceEditorHelpExpand',
                            listeners: {
                                render: function (element) {
                                    var aceEditor = element.getAceEditor();
                                    var vm = element.lookupViewModel();
                                    vm.bind(
                                        {
                                            bindTo: {
                                                theAttribute: '{theAttribute}'
                                            },
                                            single: true
                                        },
                                        function (data) {
                                            if (data.theAttribute) {
                                                aceEditor.setValue(data.theAttribute.get('help'), -1);
                                            }
                                        }
                                    );
                                    vm.bind(
                                        {
                                            isView: '{actions.view}'
                                        },
                                        function (data) {
                                            aceEditor.setReadOnly(data.isView);
                                        }
                                    );
                                    aceEditor.getSession().on('change', function (event, _editor) {
                                        vm.set('theAttribute.help', _editor.getValue());
                                    });
                                }
                            },
                            name: 'help',
                            width: '95%'
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'aceeditortextarea',
                            itemId: 'attribute-showIf',
                            allowBlank: true,
                            vmObjectName: 'theAttribute',
                            inputField: 'showif',
                            autoEl: {
                                'data-testid': 'attribute-showif_input'
                            },
                            options: {
                                mode: 'ace/mode/javascript',
                                readOnly: true
                            },
                            bind: {
                                readOnly: '{actions.view}',
                                config: {
                                    options: {
                                        readOnly: '{actions.view}'
                                    }
                                }
                            },
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showif,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showif'
                            },
                            minHeight: '85px',
                            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('expand', 'solid'),
                            labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.expand,
                            labelToolIconClick: 'onAceEditorShowIfExpand',
                            listeners: {
                                render: function (element) {
                                    var aceEditor = element.getAceEditor();
                                    var vm = element.lookupViewModel();
                                    vm.bind(
                                        {
                                            bindTo: {
                                                value: '{theAttribute.showIf}'
                                            }
                                        },
                                        function (data) {
                                            if (data.value && data.value !== aceEditor.getValue()) {
                                                aceEditor.setValue(data.value, -1);
                                            }
                                        }
                                    );
                                    vm.bind(
                                        {
                                            isView: '{actions.view}'
                                        },
                                        function (data) {
                                            aceEditor.setReadOnly(data.isView);
                                        }
                                    );
                                    aceEditor.getSession().on('change', function (event, _editor) {
                                        vm.set('theAttribute.showIf', _editor.getValue());
                                    });
                                }
                            },
                            name: 'showIf',
                            width: '95%'
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'checkbox',
                            boxLabel:
                                CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.helpalwaysvisible,
                            localized: {
                                boxLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.helpalwaysvisible'
                            },
                            name: 'helpAlwaysVisible',
                            autoEl: {
                                'data-testid': 'attribute-helpAlwaysVisible_input'
                            },
                            bind: {
                                value: '{theAttribute.helpAlwaysVisible}',
                                hidden: '{!theAttribute.help}'
                            },
                            listeners: {
                                hide: function () {
                                    this.setValue(false);
                                }
                            },
                            hidden: true
                        }
                    ]
                },
                {
                    hidden: true,
                    bind: {
                        hidden: Ext.String.format(
                            '{!theAttribute || theAttribute.type == "{0}" || theAttribute.type == "{1}"}',
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.file,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula
                        )
                    },
                    items: [
                        {
                            layout: 'column',
                            items: [
                                {
                                    columnWidth: 0.5,
                                    xtype: 'aceeditortextarea',
                                    allowBlank: true,
                                    vmObjectName: 'theAttribute',
                                    inputField: 'validationRules',
                                    itemId: 'attribute-validationRules',
                                    autoEl: {
                                        'data-testid': 'attribute-validationRules_input'
                                    },
                                    options: {
                                        mode: 'ace/mode/javascript',
                                        readOnly: true
                                    },
                                    bind: {
                                        readOnly: '{actions.view}',
                                        config: {
                                            options: {
                                                readOnly: '{actions.view}'
                                            }
                                        }
                                    },
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.attributes.fieldlabels
                                            .validationrules,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.validationrules'
                                    },
                                    minHeight: '85px',
                                    labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('expand', 'solid'),
                                    labelToolIconQtip:
                                        CMDBuildUI.locales.Locales.administration.attributes.tooltips.expand,
                                    labelToolIconClick: 'onAceEditorValidationRulesExpand',
                                    listeners: {
                                        render: function (element) {
                                            var aceEditor = element.getAceEditor();
                                            var vm = element.lookupViewModel();
                                            vm.bind(
                                                {
                                                    bindTo: {
                                                        theAttribute: '{theAttribute}'
                                                    },
                                                    single: true
                                                },
                                                function (data) {
                                                    if (data.theAttribute) {
                                                        aceEditor.setValue(
                                                            data.theAttribute.get('validationRules'),
                                                            -1
                                                        );
                                                    }
                                                }
                                            );
                                            vm.bind(
                                                {
                                                    isView: '{actions.view}'
                                                },
                                                function (data) {
                                                    aceEditor.setReadOnly(data.isView);
                                                }
                                            );
                                            aceEditor.getSession().on('change', function (event, _editor) {
                                                vm.set('theAttribute.validationRules', _editor.getValue());
                                            });
                                        }
                                    },
                                    name: 'validationRules',
                                    width: '95%'
                                },
                                {
                                    columnWidth: 0.5,
                                    xtype: 'aceeditortextarea',
                                    allowBlank: true,
                                    vmObjectName: 'theAttribute',
                                    inputField: 'autoValue',
                                    itemId: 'attribute-autoValue',
                                    autoEl: {
                                        'data-testid': 'attribute-autoValue_input'
                                    },
                                    options: {
                                        mode: 'ace/mode/javascript',
                                        readOnly: true
                                    },
                                    bind: {
                                        readOnly: '{actions.view}',
                                        config: {
                                            options: {
                                                readOnly: '{actions.view}'
                                            }
                                        }
                                    },
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.autovalue,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.autovalue'
                                    },
                                    minHeight: '85px',
                                    labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('expand', 'solid'),
                                    labelToolIconQtip:
                                        CMDBuildUI.locales.Locales.administration.attributes.tooltips.expand,
                                    labelToolIconClick: 'onAceEditorAutoValueExpand',
                                    listeners: {
                                        render: function (element) {
                                            var aceEditor = element.getAceEditor();
                                            var vm = element.lookupViewModel();
                                            vm.bind(
                                                {
                                                    bindTo: {
                                                        theAttribute: '{theAttribute}'
                                                    },
                                                    single: true
                                                },
                                                function (data) {
                                                    if (data.theAttribute) {
                                                        aceEditor.setValue(data.theAttribute.get('autoValue'), -1);
                                                    }
                                                }
                                            );
                                            vm.bind(
                                                {
                                                    isView: '{actions.view}'
                                                },
                                                function (data) {
                                                    aceEditor.setReadOnly(data.isView);
                                                }
                                            );
                                            aceEditor.getSession().on('change', function (event, _editor) {
                                                vm.set('theAttribute.autoValue', _editor.getValue());
                                            });
                                        }
                                    },
                                    name: 'autoValue',
                                    width: '95%'
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ],

    buttons: CMDBuildUI.util.administration.helper.FormHelper.getSaveAndAddCancelButtons(
        true,
        {},
        {
            hidden: true,
            bind: {
                hidden: '{actions.edit}'
            }
        }
    )
});
