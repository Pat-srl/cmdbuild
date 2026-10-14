Ext.define('CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin', {
    mixinId: 'administration-task-allinputs',

    requires: ['CMDBuildUI.util.administration.helper.FormHelper'],

    privates: {
        getRowFieldContainer: function (items, config) {
            var fieldcontainer = Ext.merge(
                {},
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    columnWidth: 1,
                    items: items
                },
                config || {}
            );

            fieldcontainer.items = items;

            return fieldcontainer;
        },
        jdbcFieldsValidation: function (ctx) {
            var form = ctx.up('form');
            if (form) {
                var jdbcUrlIput = form.down('#gateconfig_handlers_0_config_jdbcUrl_input');
                var usernameIput = form.down('#gateconfig_handlers_0_config_jdbcUsername_input');
                var passwordIput = form.down('#gateconfig_handlers_0_config_jdbcPassword_input');
                var allEmpty =
                    Ext.isEmpty(jdbcUrlIput.getValue()) &&
                    Ext.isEmpty(usernameIput.getValue()) &&
                    Ext.isEmpty(passwordIput.getValue());
                if (
                    !allEmpty &&
                    (!Ext.isEmpty(jdbcUrlIput.getValue()) ||
                        !Ext.isEmpty(usernameIput.getValue()) ||
                        !Ext.isEmpty(passwordIput.getValue()))
                ) {
                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(jdbcUrlIput, false);
                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(usernameIput, false);
                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(passwordIput, false);
                } else if (allEmpty) {
                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(jdbcUrlIput, true);
                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(usernameIput, true);
                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(passwordIput, true);
                }
                form.form.checkValidity();
            }
        },
        getNameInput: function (vmKeyObject, attribute) {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput(
                {
                    name: {
                        allowBlank: false,
                        bind: {
                            value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                        }
                    }
                },
                true,
                '[name="description"]'
            );
        },

        getDescriptionInput: function (vmKeyObject, attribute) {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                description: {
                    allowBlank: false,
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    }
                }
            });
        },

        getTypeInput: function (vmKeyObject, attribute, disabled) {
            var config = {};
            config[attribute] = {
                fieldcontainer: {
                    bind: {
                        hidden: '{isTypeFieldHidden}'
                    }
                },
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.type,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.type'
                },
                name: attribute,
                allowBlank: false,
                disabled: disabled,
                bind: {
                    store: '{taskTypesStore}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();

                        if (vm.get(vmKeyObject)._config && vm.get(vmKeyObject)._config.get('type') !== newValue) {
                            vm.get(vmKeyObject)._config.set('type', newValue);
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(attribute, config);
        },

        getTemplateInput: function (vmKeyObject, attribute) {
            var config = {};
            config.template = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.template,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.template'
                },
                name: attribute,
                allowBlank: false,
                displayField: 'description',
                valueField: 'code',

                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                    store: '{allImportExportTemplate}'
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config && vm.get(vmKeyObject)._config.get('template') !== newValue) {
                            vm.get(vmKeyObject)._config.set('template', newValue);
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('template', config);
        },

        getImportExportSourceInput: function (vmKeyObject, attribute) {
            var config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            config[_attribute] = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.source,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.source'
                },
                allowBlank: false,
                name: attribute,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                    store: '{importExportSourcesStore}'
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(_attribute, newValue);
                        }
                    },
                    validator: function (field) {
                        var form = this.up('form');
                        var dependes = form.down('[name="type"]');
                        if (dependes.getValue() === CMDBuildUI.model.tasks.Task.types.import_file) {
                            return this.getValue().length;
                        }
                        return true;
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(_attribute, config);
        },

        getDWGSourceTypeInput: function (vmKeyObject, attribute) {
            var config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            config[_attribute] = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.source,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.source'
                },
                queryMode: 'local',
                allowBlank: false,
                forceSelection: true,
                name: attribute,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                    store: '{importDWGSourcesStore}'
                },
                listeners: {
                    select: function (input, record, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(_attribute) !== record.get('value')) {
                            vm.get(vmKeyObject)._config.set(_attribute, record.get('value'));
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(_attribute, config);
        },
        getDirectoryInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            _config[_attribute] = Ext.merge(
                {},
                {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.directory,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.directory'
                    },
                    allowBlank: false,
                    name: attribute,
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = input.lookupViewModel();
                            if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                                vm.get(vmKeyObject)._config.set(_attribute, newValue);
                            }
                        }
                    }
                },
                config || {}
            );
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(_attribute, _config);
        },

        getURLInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            _config[_attribute] = Ext.merge(
                {},
                {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.url,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.url'
                    },
                    vtype: 'url',
                    name: attribute,
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = input.lookupViewModel();
                            if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                                vm.get(vmKeyObject)._config.set(_attribute, newValue);
                            }
                        }
                    }
                },
                config || {}
            );
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(_attribute, _config);
        },

        getActionAttachmentsModeInput: function (vmKeyObject, configAttribute) {
            var config = {};
            // action_attachments_mode
            var attribute = configAttribute.split('.')[1];
            config[attribute] = {
                fieldcontainer: {
                    bind: {
                        hidden: '{!isAttachmentActive}'
                    },
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.actionattachmentsmode,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.actionattachmentsmode'
                    }
                },

                name: attribute,
                bind: {
                    store: '{actionAttachmentsModeStore}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configAttribute)
                },
                combofield: {
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = input.up('form') ? input.up('form').lookupViewModel() : input.lookupViewModel();
                            vm.set('attachmentModeManager', newValue);
                            if (vm.get(vmKeyObject)._config.get(attribute) !== newValue) {
                                vm.get(vmKeyObject)._config.set(attribute, newValue);
                            }
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(attribute, config);
        },
        getFilenameInput: function (vmKeyObject, attribute) {
            var config = {};
            config.filename = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.filename,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.filename'
                },
                allowBlank: false,
                name: attribute,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get('fileName') !== newValue) {
                            vm.get(vmKeyObject)._config.set('fileName', newValue);
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('filename', config);
        },
        getIFCSourceTypeInput: function (vmKeyObject, configAttribute) {
            var config = {};
            var attribute = configAttribute.split('.')[1];
            config[attribute] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.importexport.ifc.sourcetype,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.importexport.ifc.sourcetype'
                    }
                },

                name: attribute,
                allowBlank: false,
                bind: {
                    store: '{ifcSourceTypes}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configAttribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(attribute) !== newValue) {
                            var handlerType = 'filereader';
                            if (newValue === 'project') {
                                handlerType = 'urlreader';
                            }
                            vm.get(vmKeyObject)._config.set('gateconfig_handlers_0_type', handlerType);
                            vm.get(vmKeyObject)._config.set(attribute, newValue);
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(attribute, config);
        },
        getBimProjectInput: function (vmKeyObject, configAttribute) {
            var config = {};
            var attribute = configAttribute.split('.')[1];
            config[attribute] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.importexport.ifc.project,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.importexport.ifc.project'
                    }
                },

                name: attribute,
                valueField: '_id',
                displayField: 'description',
                // allowBlank: false,
                bind: {
                    store: '{bimProjects}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configAttribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(attribute, newValue);
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(attribute, config);
        },

        getFilepatternInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            _config[_attribute] = Ext.merge(
                {},
                {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.filepattern,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.filepattern'
                    },
                    name: attribute,
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },

                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = input.lookupViewModel();
                            if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                                vm.get(vmKeyObject)._config.set(_attribute, newValue);
                            }
                        }
                    }
                },
                config || {}
            );
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(_attribute, _config);
        },

        getAssociationModeInput: function (vmKeyObject, configAttribute, config) {
            var _config = {};
            var attribute = configAttribute.split('.')[1];
            _config[attribute] = Ext.merge(
                {},
                {
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.gates.associationmode,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.gates.associationmode'
                        }
                    },

                    name: attribute,
                    allowBlank: false,
                    bind: {
                        store: '{associationModeTypes}',
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, configAttribute)
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = input.lookupViewModel();
                            if (
                                vm
                                    .get(vmKeyObject)
                                    ._config.get('gateconfig_handlers_1_config_bimserver_project_master_card_mode') !==
                                newValue
                            ) {
                                vm.set('gateconfig_handlers_1_config_bimserver_project_master_card_mode', newValue);
                                vm.get(vmKeyObject)._config.set(
                                    'gateconfig_handlers_1_config_bimserver_project_master_card_mode',
                                    newValue
                                );
                            }
                        }
                    }
                },
                config || {}
            );
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(attribute, _config);
        },

        getTargetCardIdInput: function () {
            return {
                xtype: 'fieldcontainer',
                itemId: 'targetCardIdContainer',
                layout: 'column',
                columnWidth: 1,
                items: []
            };
        },
        getUrlInput: function (vmKeyObject, attribute) {
            var config = {};
            config.url = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.url,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.url'
                },
                allowBlank: false,
                name: attribute,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get('url') !== newValue) {
                            vm.get(vmKeyObject)._config.set('url', newValue);
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('url', config);
        },

        getBasicCronInput: function (vmKeyObject, attribute) {
            var config = {};
            config.basiccron = {
                allowBlank: false,
                name: attribute,
                bind: {
                    store: '{cronSettingsStore}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (
                            vm.get(vmKeyObject) &&
                            vm.get(vmKeyObject)._config &&
                            vm.get(vmKeyObject)._config.get('cronExpression') !== newValue
                        ) {
                            Ext.asap(function () {
                                vm.get(vmKeyObject)._config.set('cronExpression', newValue);
                            });
                        }
                    }
                }
            };

            var fieldcontainer = Ext.merge(
                {},
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    columnWidth: config.basiccron.columnWidth || 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.cron,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.cron'
                    },
                    items: [],
                    itemId: Ext.String.format('{0}_fieldcontainer', 'basiccron'),
                    allowBlank: config.basiccron.allowBlank
                },
                config.basiccron.fieldcontainer || {}
            );

            delete config.basiccron.fieldcontainer;
            delete config.basiccron.fieldLabel;
            var combo = CMDBuildUI.util.administration.helper.FieldsHelper._getCombofield(
                config.basiccron,
                'basiccron'
            );
            combo.valueNotFoundText = CMDBuildUI.locales.Locales.administration.tasks.strings.advanced;
            fieldcontainer.items.push(combo);

            var displayfield = CMDBuildUI.util.administration.helper.FieldsHelper._getDisplayfield(config.basiccron);
            displayfield.renderer = function (value, input) {
                var store = input.lookupViewModel().get('cronSettingsStore');
                if (store && value) {
                    var record = store.findRecord('value', value);
                    if (record) {
                        return record.get('label');
                    } else {
                        return CMDBuildUI.locales.Locales.administration.tasks.strings.advanced;
                    }
                }
                return value;
            };

            fieldcontainer.items.push(displayfield);

            return fieldcontainer;
        },

        getAdvancedCronInput: function () {
            return {
                xtype: 'administration-content-tasks-card-croneditor-panel'
            };
        },

        getErrorEmailTemplateInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            _config.errorTemplate = Ext.merge(
                {},
                {
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.emailtemplate,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.emailtemplate'
                        }
                    },
                    allowBlank: true,
                    name: attribute,
                    displayField: 'description',
                    valueField: 'name',
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                        store: '{allEmailTemplates}'
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = input.lookupViewModel();
                            if (vm.get(vmKeyObject)._config.get('errorTemplate') !== newValue) {
                                vm.get(vmKeyObject)._config.set('errorTemplate', newValue);
                            }
                        }
                    }
                },
                config || {}
            );
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('errorTemplate', _config);
        },
        getNotificationInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            _config.notificationMode = Ext.merge(
                {},
                {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.notificationmode,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.notificationmode'
                    },
                    allowBlank: false,
                    name: attribute,
                    displayField: 'label',
                    valueField: 'value',
                    queryMode: 'local',
                    bind: {
                        store: '{notificationModesStore}',
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = input.lookupViewModel();
                            if (vm.get(vmKeyObject)._config.get('notificationMode') !== newValue) {
                                vm.get(vmKeyObject)._config.set('notificationMode', newValue);
                            }
                        }
                    }
                },
                config || {}
            );
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('notificationMode', _config);
        },

        getErrorEmailAccountInput: function (vmKeyObject, attribute) {
            var config = {};
            config.errorAccount = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.account,
                emptyText: CMDBuildUI.locales.Locales.administration.common.strings.definedinemailtemplate,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.account',
                    emptyText: 'CMDBuildUI.locales.Locales.administration.common.strings.definedinemailtemplate'
                },
                name: attribute,
                displayField: 'name',
                valueField: 'name',
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                    store: '{allEmailAccounts}'
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get('errorAccount') !== newValue) {
                            vm.get(vmKeyObject)._config.set('errorAccount', newValue);
                        }
                    }
                },
                triggers: {
                    clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('errorAccount', config);
        },

        getEmailTemplateInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            _config[_attribute] = Ext.merge(
                {},
                {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.emailtemplate,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.emailtemplate'
                    },
                    allowBlank: false,
                    name: attribute,
                    displayField: 'description',
                    valueField: 'name',
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                        store: '{allEmailTemplates}'
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = input.lookupViewModel();
                            if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                                vm.get(vmKeyObject)._config.set(_attribute, newValue);
                            }
                        }
                    }
                },
                config || {}
            );
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(_attribute, _config);
        },

        getEmailAccountInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            _config[_attribute] = Ext.merge(
                {},
                {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.fieldlabels.account,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.fieldlabels.account'
                    },
                    name: attribute,
                    displayField: 'name',
                    valueField: 'name',
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                        store: '{allEmailAccounts}'
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = input.lookupViewModel();
                            if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                                vm.get(vmKeyObject)._config.set(_attribute, newValue);
                            }
                        }
                    }
                },
                config || {}
            );

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(_attribute, _config);
        },

        getPostImportActionInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            _config[_attribute] = Ext.merge(
                {},
                {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.postimportaction,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.postimportaction'
                    },
                    name: attribute,
                    allowBlank: false,
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                        store: '{postImportActionsStore}'
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = this.lookupViewModel();
                            if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                                vm.get(vmKeyObject)._config.set(_attribute, newValue);
                            }
                        }
                    }
                },
                config || {}
            );
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(_attribute, _config);
        },

        getTargetDirectoryInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            _config[_attribute] = Ext.merge(
                {},
                {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.directory,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.directory'
                    },
                    allowBlank: false,
                    name: attribute,
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = this.lookupViewModel();
                            if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                                vm.get(vmKeyObject)._config.set(_attribute, newValue);
                            }
                        }
                    }
                },
                config || {}
            );

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(_attribute, _config);
        },
        getFilterTypeInput: function (vmKeyObject, attribute, config) {
            var me = this;
            var _config = {};

            _config.filterType = Ext.merge(
                {},
                {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.filtertype,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.filtertype'
                    },
                    allowBlank: false,
                    name: attribute,
                    // store: CMDBuildUI.model.tasks.TaskReadEmail.filterTypes,
                    bind: {
                        store: '{filterTypesStore}',
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = input.lookupViewModel();
                            var form = vm.getView();
                            var fromRegexInput = form.down('#filterSenderRegex_input');
                            var subjectRegexInput = form.down('#filterSubjectRegex_input');
                            var functionNameInput = form.down('#filter_function_name_input');
                            if (vm.get(vmKeyObject)._config.get('filter_type') !== newValue) {
                                vm.get(vmKeyObject)._config.set('filter_type', newValue);
                            }
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                fromRegexInput,
                                !(form && newValue === 'regex'),
                                form.form
                            );
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                subjectRegexInput,
                                !(form && newValue === 'regex'),
                                form.form
                            );
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                functionNameInput,
                                !(form && newValue === 'function'),
                                form.form
                            );
                        }
                    }
                },
                config || {}
            );
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('filterType', _config);
        },
        getFunctionsInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            _config[_attribute] = Ext.merge(
                {},
                {
                    displayField: 'description',
                    valueField: 'name',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.funktion,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.funktion'
                    },
                    allowBlank: false,
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                        store: '{allFunctionsStore}'
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = this.lookupViewModel();
                            if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                                vm.get(vmKeyObject)._config.set(_attribute, newValue);
                            }
                        }
                    }
                },
                config || {}
            );

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(_attribute, _config);
        },

        getFilterRegexFromInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            _config.filterSenderRegex = Ext.merge(
                {},
                {
                    fieldcontainer: {
                        localized: {
                            // fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description',
                            // labelToolIconQtip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                        },
                        // userCls: 'with-tool',
                        labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('list', 'solid'),
                        labelToolIconQtip: CMDBuildUI.locales.Locales.administration.tasks.regex,
                        labelToolIconClick: 'onSenderRegexClick',
                        hideToolOnViewMode: true,
                        bind: {
                            hidden: '{!isRegex}'
                        }
                    },
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.sender,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.sender'
                    },
                    allowBlank: false,
                    disabled: true,
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    }
                },
                config || {}
            );

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput(
                'filterSenderRegex',
                _config
            );
        },

        getFilterRegexSubjectInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            _config.filterSubjectRegex = Ext.merge(
                {},
                {
                    fieldcontainer: {
                        localized: {
                            // fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description',
                            // labelToolIconQtip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                        },
                        userCls: 'with-tool',
                        labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('list', 'solid'),
                        labelToolIconQtip: CMDBuildUI.locales.Locales.administration.tasks.regex,
                        labelToolIconClick: 'onSubjectRegexClick',
                        hideToolOnViewMode: true
                    },

                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.subject,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.subject'
                    },
                    allowBlank: false,
                    disabled: true,
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    }
                },
                config || {}
            );

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput(
                'filterSubjectRegex',
                _config
            );
        },

        getAttachFileInput: function (vmKeyObject, attribute, config) {
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            var input = Ext.merge(
                {},
                {
                    xtype: 'checkbox',

                    name: name,
                    itemId: Ext.String.format('{0}_input', name),
                    bind: {
                        readOnly: '{actions.view}',
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (checkbox, newValue, oldValue) {
                            var vm = this.lookupViewModel();
                            if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                                vm.get(vmKeyObject)._config.set(_attribute, newValue);
                            }
                        }
                    }
                },
                config || {}
            );
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.attachfile,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.attachfile'
                },
                hidden: true,
                listeners: {
                    afterrender: function (container) {
                        var vm = this.lookupViewModel();
                        vm.bind(
                            {
                                bindTo: {
                                    type: '{theTask.type}',
                                    mode: '{theTask.config.notificationMode}'
                                }
                            },
                            function (data) {
                                if (data.type && data.mode) {
                                    var shouldHide = !(data.type === 'export_file' && data.mode === 'always');
                                    if (shouldHide) {
                                        vm.set('theTask.config._attach_file', false);
                                    }
                                    container.setHidden(shouldHide);
                                }
                            }
                        );
                    }
                },
                items: [input]
            };
            return fieldcontainer;
        },
        getFilterRejectInput: function (vmKeyObject, attribute, config) {
            var me = this;
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            var input = Ext.merge(
                {},
                {
                    xtype: 'checkbox',

                    name: _attribute,
                    itemId: Ext.String.format('{0}_input', _attribute),
                    bind: {
                        readOnly: '{actions.view}',
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (checkbox, newValue, oldValue) {
                            var form = me.getView();
                            var vm = form.getViewModel();
                            if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                                vm.get(vmKeyObject)._config.set(_attribute, newValue);
                            }
                            var folderRejectInput = form.down('#folder_rejected_input');
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                folderRejectInput,
                                !(form && newValue),
                                form.form
                            );
                            checkbox.lookupViewModel().set('isMoveReject', newValue);
                        }
                    }
                },
                config || {}
            );
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.movereject,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.movereject'
                },

                items: [input]
            };
            return fieldcontainer;
        },

        getBodyParsingInput: function (vmKeyObject, attribute, config) {
            var input = Ext.merge(
                {},
                {
                    xtype: 'checkbox',

                    name: name,
                    itemId: Ext.String.format('{0}_input', name),
                    bind: {
                        readOnly: '{actions.view}',
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    }
                },
                config || {}
            );
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.bodyparsing,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.bodyparsing'
                },

                items: [input]
            };
            return fieldcontainer;
        },

        getParsingInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            var configAttribute = attribute.split('.');
            _config[configAttribute[configAttribute.length - 1]] = Ext.merge(
                {},
                {
                    name: attribute,
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = input.lookupViewModel();
                            if (
                                vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !==
                                newValue
                            ) {
                                vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                            }
                        }
                    },
                    displayfield: {
                        htmlEncode: true
                    }
                },
                config || {}
            );
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(
                configAttribute[configAttribute.length - 1],
                _config
            );
        },

        getActionNotificationActiveInput: function (vmKeyObject, attribute, config) {
            var me = this;
            var form = me.getView();
            var configAttribute = attribute.split('.');
            var input = Ext.merge(
                {},
                {
                    xtype: 'checkbox',

                    name: configAttribute[1],
                    itemId: Ext.String.format('{0}_input', configAttribute[1]),
                    bind: {
                        readOnly: '{actions.view}',
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (checkbox, newValue, oldValue) {
                            var vm = form.getViewModel();
                            if (
                                vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !==
                                newValue
                            ) {
                                vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                            }
                            vm.set('isNotificationActive', newValue);
                            var notificationTemplateInput = form.down('#action_notification_template_input');
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                notificationTemplateInput,
                                !(form && newValue),
                                form.form
                            );
                        }
                    }
                },
                config || {}
            );
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.sendnotiifcation,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.sendnotiifcation'
                },

                items: [input]
            };
            return fieldcontainer;
        },

        getActionNotificationAttachmentsActiveInput: function (vmKeyObject, attribute, config) {
            var me = this;
            var form = me.getView();
            var configAttribute = attribute.split('.');
            var input = Ext.merge(
                {},
                {
                    xtype: 'checkbox',
                    name: configAttribute[1],
                    itemId: Ext.String.format('{0}_input', configAttribute[1]),
                    bind: {
                        readOnly: '{actions.view}',
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (checkbox, newValue, oldValue) {
                            var vm = form.getViewModel();
                            if (
                                vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !==
                                newValue
                            ) {
                                vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                            }
                            vm.set('isNotificationAttachmentsActive', newValue);
                            var notificationAttachmentsModeInput = form.down(
                                '#action_notification_attachments_mode_input'
                            );
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                notificationAttachmentsModeInput,
                                !(form && newValue),
                                form.form
                            );
                        }
                    }
                },
                config || {}
            );
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.includeattachments,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.includeattachments'
                },

                items: [input]
            };
            return fieldcontainer;
        },

        getActionNotificationAttachmentsModeInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            _config[_attribute] = Ext.merge(
                {},
                {
                    displayField: 'label',
                    valueField: 'value',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.includeattachmentsmode,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.includeattachmentsmode'
                    },
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                        store: '{includeAttachmentsModeStore}'
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = this.lookupViewModel();
                            if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                                vm.get(vmKeyObject)._config.set(_attribute, newValue);
                            }
                        }
                    }
                },
                config || {}
            );

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(_attribute, _config);
        },

        getSaveAttachmentInput: function (vmKeyObject, attribute, config) {
            var me = this;
            var configAttribute = attribute.split('.');
            var input = Ext.merge(
                {},
                {
                    xtype: 'checkbox',

                    name: configAttribute[1],
                    itemId: Ext.String.format('{0}_input', configAttribute[1]),
                    bind: {
                        readOnly: '{actions.view}',
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (checkbox, newValue, oldValue) {
                            const form = me.getView();
                            const _input = form.down('#action_attachments_category_input');
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                _input,
                                !newValue,
                                _input.up('form')
                            );
                            var vm = form.getViewModel();
                            if (
                                vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !==
                                newValue
                            ) {
                                vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                            }
                            vm.set('isAttachmentActive', newValue);
                        }
                    }
                },
                config || {}
            );
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.saveattachmentsdms,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.saveattachmentsdms'
                },

                items: [input]
            };
            return fieldcontainer;
        },

        getAttachmentCategoryInput: function (vmKeyObject, attribute, config) {
            var me = this;
            // dmsLookupStore
            var _config = {};
            var configAttribute = attribute.split('.');

            _config[configAttribute[configAttribute.length - 1]] = Ext.merge(
                {},
                {
                    displayField: 'description',
                    valueField: '_id',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.category,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.category'
                    },

                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                        store: '{dmsLookupStore}'
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var form = me.getView();
                            var vm = form.getViewModel();

                            if (
                                vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !==
                                newValue
                            ) {
                                vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                            }
                            Ext.asap(function () {
                                input.forceSelection = true;
                            });
                        }
                    },
                    displayfield: {
                        bind: {
                            value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                        },
                        renderer: function (value, input) {
                            var store = input.lookupViewModel().get('dmsLookupStore');
                            if (store && value) {
                                var record = store.findRecord('_id', value);
                                if (record) {
                                    return record.get('description');
                                }
                            }
                            return value;
                        }
                    }
                },
                config || {}
            );

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonGroupedComboInput(
                configAttribute[configAttribute.length - 1],
                _config
            );
        },

        getStartProcessInput: function (vmKeyObject, attribute, config) {
            var me = this;
            var configAttribute = attribute.split('.');
            var input = Ext.merge(
                {},
                {
                    xtype: 'checkbox',
                    name: name,
                    itemId: Ext.String.format('{0}_input', name),
                    bind: {
                        readOnly: '{actions.view}',
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (checkbox, newValue, oldValue) {
                            var form = me.getView();
                            var workflowClassNameInput = form.down('#action_workflow_class_name_input');
                            var workfowAdvanceInput = form.down('#action_workflow_advance_input');
                            var saveAttachementInput = form.down('#action_workflow_attachmentssave_input');
                            var attachmentscategoryInput = form.down('#action_workflow_attachmentscategory_input');
                            var vm = form.getViewModel();

                            if (!newValue) {
                                attachmentscategoryInput.reset();
                                saveAttachementInput.reset();
                                workflowClassNameInput.reset();
                                workfowAdvanceInput.reset();
                            }
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                workflowClassNameInput,
                                !(form && newValue),
                                form.form
                            );
                            if (
                                vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !==
                                newValue
                            ) {
                                vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                            }
                            vm.set('isStartProcessActive', newValue);
                        }
                    }
                },
                config || {}
            );
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.startprocess,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.startprocess'
                },

                items: [input]
            };
            return fieldcontainer;
        },

        getProcessesInput: function (vmKeyObject, attribute, config) {
            var me = this;
            var _config = {};
            var configAttribute = attribute.split('.');

            _config[configAttribute[configAttribute.length - 1]] = Ext.merge(
                {},
                {
                    displayField: 'description',
                    valueField: '_id',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.process,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.process'
                    },
                    allowBlank: true,
                    forceSelection: true,
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                        store: '{processesStore}',
                        disabled: '{comeFromClass}'
                    },
                    listeners: {
                        change: function (combo, newValue, oldValue) {
                            me.getViewModel().set('workflowClassName', newValue);
                        }
                    },
                    displayfield: {
                        xtype: 'displayfieldwithtriggers',
                        bind: {
                            value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                            hideTrigger: Ext.String.format('{!{0}.{1}}', vmKeyObject, attribute)
                        },
                        triggers: {
                            open: {
                                cls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                                handler: function (f, trigger, eOpts) {
                                    var value = f.getValue(),
                                        url =
                                            CMDBuildUI.util.administration.helper.ApiHelper.client.getProcessUrl(value);
                                    CMDBuildUI.util.Utilities.closeAllPopups();
                                    CMDBuildUI.util.Utilities.redirectTo(url);
                                }
                            }
                        }
                    }
                },
                config || {}
            );

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                configAttribute[configAttribute.length - 1],
                _config
            );
        },

        getTaskUserInput: function (vmKeyObject, attribute, config) {
            var me = this;
            var _config = {};
            var configAttribute = attribute.split('.');

            _config[configAttribute[configAttribute.length - 1]] = Ext.merge(
                {},
                {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.jobusername,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.jobusername'
                    },
                    allowBlank: true,
                    name: attribute,
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var form = me.getView();
                            var vm = form.getViewModel();
                            if (
                                vm.get(vmKeyObject) &&
                                vm.get(vmKeyObject)._config &&
                                vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !==
                                    newValue
                            ) {
                                vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                            }
                        }
                    }
                },
                config || {}
            );

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(
                configAttribute[configAttribute.length - 1],
                _config
            );
        },

        getWorkflowAdvanceInput: function (vmKeyObject, attribute, config) {
            var configAttribute = attribute.split('.');
            var input = Ext.merge(
                {},
                {
                    xtype: 'checkbox',
                    name: configAttribute[1],
                    itemId: Ext.String.format('{0}_input', configAttribute[1]),
                    bind: {
                        readOnly: '{actions.view}',
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (checkbox, newValue, oldValue) {
                            var vm = checkbox.lookupViewModel();
                            if (
                                vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !==
                                newValue
                            ) {
                                vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                            }
                        }
                    }
                },
                config || {}
            );
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.advanceworkflow,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.advanceworkflow'
                },

                items: [input]
            };
            return fieldcontainer;
        },

        getWorkflowSaveAttachmentsInput: function (vmKeyObject, attribute, config) {
            var me = this;
            var configAttribute = attribute.split('.');
            var input = Ext.merge(
                {},
                {
                    xtype: 'checkbox',
                    name: configAttribute[1],
                    itemId: Ext.String.format('{0}_input', configAttribute[1]),
                    bind: {
                        readOnly: '{actions.view}',
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (checkbox, newValue, oldValue) {
                            var form = me.getView();
                            var vm = form.getViewModel();
                            var attachmentscategoryInput = form.down('#action_workflow_attachmentscategory_input');
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                attachmentscategoryInput,
                                !(form && newValue),
                                form.form
                            );
                            if (
                                vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !==
                                newValue
                            ) {
                                vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                            }
                        }
                    }
                },
                config || {}
            );
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.saveattachments,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.saveattachments'
                },

                items: [input]
            };
            return fieldcontainer;
        },

        getWorkflowAttachmentCategoryInput: function (vmKeyObject, attribute, config) {
            // dmsLookupStore
            var me = this;
            var configAttribute = attribute.split('.');
            var _config = {};
            _config[configAttribute[configAttribute.length - 1]] = Ext.merge(
                {},
                {
                    displayField: 'description',
                    valueField: '_id',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.category,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.category'
                    },
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                        store: '{processDmsLookupStore}'
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var form = me.getView();
                            var vm = form.getViewModel();
                            if (
                                vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !==
                                newValue
                            ) {
                                vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                            }
                        }
                    }
                },
                config || {}
            );

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                configAttribute[configAttribute.length - 1],
                _config
            );
        },

        getProcessAttributesGrid: function () {
            return {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 1,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.processattributes,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.processattributes'
                },
                items: [
                    {
                        columnWidth: 1,

                        xtype: 'grid',
                        headerBorders: false,
                        border: false,
                        bodyBorder: false,
                        rowLines: false,
                        sealedColumns: false,
                        sortableColumns: false,
                        enableColumnHide: false,
                        enableColumnMove: false,
                        enableColumnResize: false,
                        cls: 'administration-reorder-grid',
                        selModel: {
                            pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                        },
                        reference: 'processAttributesGrid',
                        itemId: 'processAttributesGrid',
                        bind: {
                            store: '{processAttributesMapStore}'
                        },
                        viewConfig: {
                            markDirty: false
                        },
                        plugins: {
                            ptype: 'actionColumnRowEditing',
                            id: 'actionColumnRowEditing',
                            hiddenColumnsOnEdit: ['actionColumnEdit', 'actionColumnCancel'],
                            clicksToEdit: 10,
                            buttonsUi: 'button-like-tool',
                            errorSummary: false,
                            placeholdersButtons: []
                        },
                        listeners: {
                            canceledit: function (grid) {
                                var vm = grid.view.lookupViewModel();
                                vm.set('gridEditing', false);
                            },
                            beforeedit: function (grid, context) {
                                var vm = grid.view.lookupViewModel();
                                if (vm.get('actions.view')) {
                                    return false;
                                }
                                vm.set('gridEditing', true);
                                var allAttributesFiltered = vm.get('allAttributesOfProcessStoreFiltered');
                                allAttributesFiltered.clearFilter();
                                allAttributesFiltered.addFilter(function (item) {
                                    return (
                                        (item.get('type') !== CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.file &&
                                            item.canAdminShow() &&
                                            !vm.get('processAttributesMapStore').findRecord('key', item.get('name'))) ||
                                        item.get('name') === context.record.get('key')
                                    );
                                });
                            },
                            edit: function (tableview, context) {
                                var vm = tableview.view.lookupViewModel();
                                context.record.set(
                                    'description',
                                    vm
                                        .get('allAttributesOfProcessStore')
                                        .findRecord('name', context.record.get('key'))
                                        .get('description')
                                );
                                vm.set('gridEditing', false);
                            }
                        },
                        columns: [
                            {
                                flex: 1,
                                text: CMDBuildUI.locales.Locales.administration.common.labels.name,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                                },
                                dataIndex: 'key',
                                align: 'left',
                                editor: {
                                    xtype: 'combobox',
                                    // height: 19,
                                    // minHeight: 19,
                                    // maxHeight: 19,
                                    padding: 0,
                                    ui: 'reordergrid-editor-combo',
                                    displayField: 'description',
                                    valueField: 'name',
                                    queryMode: 'local',
                                    bind: {
                                        value: '{record.key}',
                                        store: '{allAttributesOfProcessStoreFiltered}'
                                    }
                                },
                                renderer: function (value) {
                                    var vm = this.lookupViewModel();
                                    var store = vm.get('processAttributesMapStore');
                                    if (store) {
                                        var record = store.findRecord('key', value);
                                        if (record) {
                                            return record.get('description');
                                        }
                                    }
                                    return Ext.String.capitalize(value);
                                },
                                variableRowHeight: true
                            },
                            {
                                flex: 1,
                                text: CMDBuildUI.locales.Locales.administration.tasks.value,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.tasks.value'
                                },
                                dataIndex: 'value',
                                align: 'left',
                                // height: 19,
                                editor: {
                                    xtype: 'textfield',
                                    // height: 19,
                                    // minHeight: 19,
                                    // maxHeight: 19,
                                    padding: 0,
                                    ui: 'reordergrid-editor-combo'
                                },
                                variableRowHeight: true
                            },
                            {
                                xtype: 'actioncolumn',
                                itemId: 'actionColumnEdit',
                                bind: {
                                    hidden: '{actions.view}'
                                },
                                hidden: true,
                                width: 30,
                                minWidth: 30, // width property not works. Use minWidth.
                                maxWidth: 30,
                                align: 'center',
                                items: [
                                    {
                                        bind: {
                                            hidden: '{actions.view}'
                                        },
                                        handler: function (grid, rowIndex, colIndex, item, e, record) {
                                            grid.editingPlugin.startEdit(record, 0);
                                        },
                                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                            return CMDBuildUI.locales.Locales.administration.common.actions.edit;
                                        },
                                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                            if (record.get('editing')) {
                                                return CMDBuildUI.util.helper.IconHelper.getIconId('check', 'solid');
                                            }
                                            return CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid');
                                        }
                                    }
                                ]
                            },
                            {
                                xtype: 'actioncolumn',
                                itemId: 'actionColumnCancel',
                                bind: {
                                    hidden: '{actions.view}'
                                },
                                width: 30,
                                minWidth: 30, // width property not works. Use minWidth.
                                maxWidth: 30,
                                align: 'center',
                                items: [
                                    {
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                            return CMDBuildUI.locales.Locales.administration.common.actions.remove;
                                        },
                                        handler: function (grid, rowIndex, colIndex, item, e, record) {
                                            var vm = grid.lookupViewModel().getParent();
                                            grid.getStore().remove(record);
                                            vm.set('allAttributesOfProcessDataFilter', vm._allAttributeFilter());
                                        },
                                        isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                            return !record.get('editing') ? false : true;
                                        },
                                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                            metadata.value = Ext.String.insert(
                                                metadata.value,
                                                Ext.String.format(
                                                    ' data-testid="administration-reordergrid-removeBtn-{0}"',
                                                    rowIndex
                                                ),
                                                -7
                                            );
                                            if (record.get('editing')) {
                                                return CMDBuildUI.util.helper.IconHelper.getIconId(
                                                    'ellipsis-h',
                                                    'solid'
                                                );
                                            }
                                            return CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid');
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ]
            };
        },

        getProcessAttributesGridForm: function () {
            return {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 1,
                fieldLabel: '',
                bind: {
                    hidden: '{actions.view}'
                },
                margin: '20 0 20 0',
                items: [
                    {
                        columnWidth: 1,
                        xtype: 'components-grid-reorder-grid',
                        itemId: 'processAttributesGridForm',
                        bind: {
                            hidden: '{gridEditing}',
                            store: '{newProcessAttributesMapStore}'
                        },
                        viewConfig: {
                            markDirty: false
                        },
                        columns: [
                            {
                                flex: 1,
                                text: '',
                                xtype: 'widgetcolumn',
                                align: 'left',
                                dataIndex: 'key',
                                widget: {
                                    xtype: 'combobox',
                                    queryMode: 'local',
                                    typeAhead: true,
                                    displayField: 'description',
                                    valueField: 'name',
                                    forceSelection: true,
                                    bind: {
                                        value: '{record.key}',
                                        store: '{allAttributesOfProcessStoreFiltered}'
                                    },
                                    listeners: {
                                        expand: function (combo) {
                                            var store = combo.getStore();
                                            var vm = combo.lookupViewModel();
                                            store.clearFilter();
                                            store.addFilter(function (item) {
                                                return (
                                                    item.canAdminShow() &&
                                                    !vm
                                                        .get('processAttributesMapStore')
                                                        .findRecord('key', item.get('name'))
                                                );
                                            });
                                        }
                                    }
                                }
                            },
                            {
                                flex: 1,
                                text: '',
                                xtype: 'widgetcolumn',
                                align: 'left',
                                dataIndex: 'value',
                                widget: {
                                    xtype: 'textfield',
                                    bind: {
                                        value: '{record.value}'
                                    }
                                }
                            },
                            {
                                xtype: 'actioncolumn',
                                width: 30,
                                minWidth: 30, // width property not works. Use minWidth.
                                maxWidth: 30,
                                align: 'center',
                                items: [
                                    {
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                                        disabled: true
                                    }
                                ]
                            },
                            {
                                xtype: 'actioncolumn',
                                width: 30,
                                minWidth: 30, // width property not works. Use minWidth.
                                maxWidth: 30,
                                align: 'center',
                                items: [
                                    {
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
                                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                            return CMDBuildUI.locales.Locales.administration.common.actions.add;
                                        },
                                        handler: function (grid, rowIndex, colIndex, item, e, record) {
                                            var formGridStore = grid.getStore();
                                            var vm = grid.lookupViewModel().getParent();
                                            var gridStore = vm.getStore('processAttributesMapStore');
                                            record.set(
                                                'description',
                                                vm
                                                    .get('allAttributesOfProcessStore')
                                                    .findRecord('name', record.get('key'))
                                                    .get('description')
                                            );
                                            gridStore.add(record);
                                            formGridStore.removeAll();
                                            formGridStore.add(CMDBuildUI.model.base.KeyDescriptionValue.create());
                                            vm.set('allAttributesOfProcessDataFilter', vm._allAttributeFilter());
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ]
            };
        },
        getProcessAttributesGridForWorkflow: function () {
            return {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 1,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.processattributes,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.processattributes'
                },
                items: [
                    {
                        columnWidth: 1,
                        forceFit: true,
                        xtype: 'grid',
                        headerBorders: false,
                        border: false,
                        bodyBorder: false,
                        rowLines: false,
                        sealedColumns: false,
                        sortableColumns: false,
                        enableColumnHide: false,
                        enableColumnMove: false,
                        enableColumnResize: false,
                        cls: 'administration-reorder-grid',
                        selModel: {
                            pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                        },
                        itemId: 'processAttributesForWorkflowGrid',
                        bind: {
                            store: '{processAttributesMapStore}'
                        },
                        viewConfig: {
                            markDirty: false
                        },
                        columns: [
                            {
                                flex: 1,
                                text: CMDBuildUI.locales.Locales.administration.common.labels.name,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                                },
                                dataIndex: 'description',
                                tdCls: 'wrapText',
                                align: 'left',
                                renderer: function (value) {
                                    var vm = this.lookupViewModel();
                                    var store = vm.get('processAttributesMapStore');
                                    if (store) {
                                        var record = store.findRecord('key', value);
                                        if (record) {
                                            return record.get('description');
                                        }
                                    }
                                    return Ext.String.capitalize(value);
                                },
                                variableRowHeight: true
                            },
                            {
                                flex: 1,
                                text: CMDBuildUI.locales.Locales.administration.tasks.value,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.tasks.value'
                                },
                                tdCls: 'wrapText',
                                dataIndex: 'value',
                                align: 'left',
                                variableRowHeight: true,
                                cellWrap: true,
                                renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                                    var me = this;
                                    var renderer = CMDBuildUI.util.helper.GridHelper.getColumn(
                                        record.get('definition'),
                                        {}
                                    ).renderer(value, metaData, record, rowIndex, colIndex, store, view);
                                    if (
                                        value &&
                                        record.get('definition').cmdbuildtype === 'lookupArray' &&
                                        !renderer.replaceAll(',', '').trim().length
                                    ) {
                                        Ext.asap(function () {
                                            me.getView().refresh();
                                        });
                                        return CMDBuildUI.util.Navigation.defaultManagementContentTitle;
                                    }
                                    return CMDBuildUI.util.helper.GridHelper.getColumn(
                                        record.get('definition'),
                                        {}
                                    ).renderer(value, metaData, record, rowIndex, colIndex, store, view);
                                }
                            },
                            {
                                xtype: 'actioncolumn',
                                itemId: 'actionColumnEdit',
                                bind: {
                                    hidden: '{actions.view}'
                                },
                                hidden: true,
                                width: 30,
                                minWidth: 30, // width property not works. Use minWidth.
                                maxWidth: 30,
                                align: 'center',
                                items: [
                                    {
                                        bind: {
                                            hidden: '{actions.view}'
                                        },
                                        handler: function (grid, rowIndex, colIndex, item, e, record) {
                                            var vm = grid.lookupViewModel().getParent();
                                            var keyField = grid.up('fieldset').down('#keyField');
                                            grid.getStore().remove(record);
                                            Ext.asap(function () {
                                                vm.set('keyFieldValue', record.get('key'));
                                                keyField.setValue(record.get('key'));
                                                Ext.asap(function () {
                                                    var valueField = grid.up('fieldset').down('#valueField');
                                                    vm.set('valueFieldValue', record.get('value'));
                                                    valueField.setValue(record.get('value'));
                                                });
                                            });
                                        },
                                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                            return CMDBuildUI.locales.Locales.administration.common.actions.edit;
                                        },
                                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                            if (record.get('editing')) {
                                                return CMDBuildUI.util.helper.IconHelper.getIconId('check', 'solid');
                                            }
                                            return CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid');
                                        }
                                    }
                                ]
                            },
                            {
                                xtype: 'actioncolumn',
                                itemId: 'actionColumnCancel',
                                bind: {
                                    hidden: '{actions.view}'
                                },
                                width: 30,
                                minWidth: 30, // width property not works. Use minWidth.
                                maxWidth: 30,
                                align: 'center',
                                items: [
                                    {
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                            return CMDBuildUI.locales.Locales.administration.common.actions.remove;
                                        },
                                        handler: function (grid, rowIndex, colIndex, item, e, record) {
                                            var vm = grid.lookupViewModel().getParent();
                                            grid.getStore().remove(record);
                                            vm.set('allAttributesOfProcessDataFilter', vm._allAttributeFilter());
                                        },
                                        isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                            return !record.get('editing') ? false : true;
                                        },
                                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                            metadata.value = Ext.String.insert(
                                                metadata.value,
                                                Ext.String.format(
                                                    ' data-testid="administration-reordergrid-removeBtn-{0}"',
                                                    rowIndex
                                                ),
                                                -7
                                            );
                                            if (record.get('editing')) {
                                                return CMDBuildUI.util.helper.IconHelper.getIconId(
                                                    'ellipsis-h',
                                                    'solid'
                                                );
                                            }
                                            return CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid');
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ]
            };
        },

        getProcessAttributesGridFormForWorkflow: function () {
            var panel = {
                hidden: true,
                bind: {
                    hidden: '{actions.view}'
                },
                columnWidth: 1,
                layout: 'column',
                margin: '20 0 20 0',
                itemId: 'attributeForm',
                items: [
                    {
                        margin: '0 20 0 0',
                        flex: 0.5,
                        columnWidth: 0.5,
                        xtype: 'combobox',
                        itemId: 'keyField',
                        queryMode: 'local',
                        typeAhead: true,
                        padding: 0,
                        displayField: 'description',
                        valueField: 'name',
                        forceSelection: true,
                        bind: {
                            store: '{allAttributesOfProcessStoreFiltered}',
                            value: '{keyFieldValue}'
                        },
                        listeners: {
                            change: function (combo, newValue, oldValue) {
                                var vm = combo.up('administration-content-tasks-card').lookupViewModel();
                                var attribute = combo
                                    .lookupViewModel()
                                    .get('allAttributesOfProcessStore')
                                    .findRecord('name', newValue);
                                if (!attribute) {
                                    return;
                                }
                                if (attribute.get('type') === 'text') {
                                    attribute.set('type', 'string');
                                    attribute.set('multiline', false);
                                }
                                var valCol = combo.up('#attributeForm').down('#valueFieldContainer');
                                var newEditor = Ext.create(
                                    Ext.Object.merge(
                                        {},
                                        CMDBuildUI.util.helper.FormHelper.getEditorForField(
                                            CMDBuildUI.util.helper.ModelHelper.getModelFieldFromAttribute(attribute),
                                            {
                                                ignoreUpdateVisibilityToField: true,
                                                ignoreCustomValidator: true,
                                                ignoreAutovalue: true,
                                                forceSelection: true
                                            }
                                        ),
                                        {
                                            padding: 0,
                                            itemId: 'valueField',
                                            maxWidth: '100%',
                                            ignoreCqlFilter: true
                                        }
                                    )
                                );

                                if (newEditor.xtype === 'referencefield') {
                                    var combofield = newEditor.down('referencecombofield');
                                    if (combofield) {
                                        combofield.on('change', function (_input, _newValue, _oldValue) {
                                            vm.set('valueFieldValue', _newValue);
                                        });
                                    }
                                } else if (newEditor.xtype === 'lookupfield') {
                                    newEditor.on('add', function (input) {
                                        var _input = input.down('lookupcombofield');
                                        _input.forceSelection = true;
                                        _input.on('change', function (_input, _newValue, _oldValue) {
                                            vm.set('valueFieldValue', _newValue);
                                        });
                                    });
                                } else {
                                    newEditor.on('change', function (_input, _newValue, _oldValue) {
                                        vm.set('valueFieldValue', _newValue);
                                    });
                                }
                                valCol.removeAll();
                                vm.set('keyFieldValue', null);
                                vm.set('valueFieldValue', null);
                                valCol.add(newEditor);
                            }
                        }
                    },
                    {
                        xtype: 'fieldcontainer',
                        itemId: 'valueFieldContainer',
                        flex: 0.5,
                        columnWidth: 0.5,
                        items: [
                            {
                                xtype: 'displayfield'
                            }
                        ]
                    },
                    {
                        xtype: 'button',
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
                        minWidth: 30,
                        maxWidth: 30,
                        margin: '5 0 0 0',
                        width: 30,
                        ui: 'button-like-tool',
                        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.add, // Add
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.add'
                        },
                        disabled: true,
                        bind: {
                            disabled: '{!valueFieldValue  || !keyFieldValue }'
                        },
                        handler: function (button) {
                            var vm = button.up('administration-content-tasks-card').lookupViewModel();
                            var key = vm.get('keyFieldValue');
                            var value = vm.get('valueFieldValue');

                            var dataGridStore = vm.get('processAttributesMapStore');
                            var field = vm.get('precessFieldsDefinitions').find(function (item) {
                                return item.name === key;
                            });
                            var attribute = vm.get('allAttributesOfProcessStore').findRecord('name', key);
                            var description = attribute.get('description');

                            var definition = field;
                            dataGridStore.add({
                                key: key,
                                value: value,
                                definition: definition,
                                description: description
                            });
                            button.up('#attributeForm').down('#valueFieldContainer').removeAll();
                            vm.set('valueFieldValue', null);
                            vm.set('keyFieldValue', null);

                            // remove value field
                            button.up('#attributeForm').down('#valueFieldContainer').add({
                                xtype: 'displayfield'
                            });

                            var store = vm.get('allAttributesOfProcessStoreFiltered');
                            store.clearFilter();
                            store.addFilter(function (item) {
                                return (
                                    item.get('type') !== CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.file &&
                                    item.canAdminShow() &&
                                    !vm.get('processAttributesMapStore').findRecord('key', item.get('name'))
                                );
                            });
                        }
                    },
                    {
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                        disabled: true,
                        xtype: 'button',
                        minWidth: 30,
                        width: 30,
                        maxWidth: 30,
                        margin: '5 0 0 0',
                        ui: 'button-like-tool'
                    }
                ]
            };
            return panel;
        },

        getDatabaseGatesInput: function (vmKeyObject, configKey) {
            var config = {};
            var attribute = configKey.split('.')[1];
            config[attribute] = {
                fieldcontainer: {
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.databasetemplate,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.databasetemplate'
                    }
                },
                displayField: 'description',
                valueField: 'code',
                disabled: true,
                allowBlank: false,
                bind: {
                    store: '{databaseGatesStore}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(attribute, newValue);
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(attribute, config);
        },

        getSourceTypeInput: function (vmKeyObject, configKey) {
            var config = {};
            var attribute = configKey.split('.')[1];
            config[attribute] = {
                fieldcontainer: {
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.sourcetype,
                    emptyText: CMDBuildUI.locales.Locales.administration.tasks.asdefinedindatabasetemplate,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.sourcetype',
                        emptyText: 'CMDBuildUI.locales.Locales.administration.tasks.asdefinedindatabasetemplate'
                    }
                },
                emptyText: CMDBuildUI.locales.Locales.administration.tasks.asdefinedindatabasetemplate,
                localized: {
                    emptyText: 'CMDBuildUI.locales.Locales.administration.tasks.asdefinedindatabasetemplate'
                },
                disabled: true,
                bind: {
                    store: Ext.String.format('{{0}Store}', attribute),
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(attribute, newValue);
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(attribute, config);
        },

        getJdbcUrlInput: function (vmKeyObject, configKey) {
            var me = this;
            var config = {};
            var attribute = configKey.split('.')[1];
            config[attribute] = {
                fieldcontainer: {
                    columnWidth: 1,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.address,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.address'
                    }
                },
                emptyText: CMDBuildUI.locales.Locales.administration.tasks.asdefinedindatabasetemplate,
                localized: {
                    emptyText: 'CMDBuildUI.locales.Locales.administration.tasks.asdefinedindatabasetemplate'
                },
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(attribute, newValue);
                        }
                        me.jdbcFieldsValidation(this);
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(attribute, config);
        },

        getJdbcUsernameInput: function (vmKeyObject, configKey) {
            var me = this;
            var config = {};
            var attribute = configKey.split('.')[1];
            config[attribute] = {
                fieldcontainer: {
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.username,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.username'
                    }
                },
                emptyText: CMDBuildUI.locales.Locales.administration.tasks.asdefinedindatabasetemplate,
                localized: {
                    emptyText: 'CMDBuildUI.locales.Locales.administration.tasks.asdefinedindatabasetemplate'
                },
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(attribute, newValue);
                        }
                        me.jdbcFieldsValidation(this);
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(attribute, config);
        },

        getJdbcPasswordInput: function (vmKeyObject, configKey) {
            var me = this;
            var config = {};
            var attribute = configKey.split('.')[1];
            config[attribute] = {
                fieldcontainer: {
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.password,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.password'
                    }
                },
                emptyText: CMDBuildUI.locales.Locales.administration.tasks.asdefinedindatabasetemplate,
                localized: {
                    emptyText: 'CMDBuildUI.locales.Locales.administration.tasks.asdefinedindatabasetemplate'
                },
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(attribute, newValue);
                        }
                        me.jdbcFieldsValidation(this);
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(attribute, config, true);
        },

        getErrorTemplateInput: function (vmKeyObject, configKey) {
            var config = {};
            var attribute = configKey.split('.')[1];
            config[attribute] = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.emailtemplate,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.emailtemplate'
                },
                name: attribute,
                displayField: 'description',
                valueField: '_id',
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey),
                    store: '{allEmailTemplates}'
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(attribute, newValue);
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(attribute, config);
        },

        getIfcGateTemplateInput: function (vmKeyObject, configKey) {
            var config = {};
            var attribute = configKey.split('.')[1];
            config[attribute] = {
                fieldcontainer: {
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.ifctemplate,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.ifctemplate'
                    }
                },
                displayField: 'description',
                valueField: 'code',
                disabled: true,
                allowBlank: false,
                bind: {
                    store: '{ifcGatesStore}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(attribute, newValue);
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(attribute, config);
        },
        getNotificationEmailTemplateInput: function (vmKeyObject, configKey) {
            var config = {};
            var attribute = configKey.split('.')[1];
            config[attribute] = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.notificationemailtemplate,
                emptyText: CMDBuildUI.locales.Locales.administration.tasks.definedinetltemplate,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.notificationemailtemplate',
                    emptyText: 'CMDBuildUI.locales.Locales.administration.tasks.definedinetltemplate'
                },
                name: attribute,
                displayField: 'description',
                valueField: 'name',
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey),
                    store: '{notificationEmailTemplates}'
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(attribute, newValue);
                        }
                    }
                },
                triggers: {
                    clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(attribute, config);
        },

        getAttachImportReport: function (vmKeyObject, configKey) {
            var me = this;
            var configAttribute = configKey.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];

            var input = {
                xtype: 'checkbox',

                name: name,
                itemId: Ext.String.format('{0}_input', name),
                bind: {
                    readOnly: '{actions.view}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey)
                },
                listeners: {
                    change: function (checkbox, newValue, oldValue) {
                        var form = me.getView();
                        var vm = form.getViewModel();
                        if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(_attribute, newValue);
                        }
                    }
                }
            };
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.attachimportreport,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.attachimportreport'
                },

                items: [input]
            };
            return fieldcontainer;
        },

        getETLGrid: function (config) {
            return Ext.merge(
                {},
                {
                    xtype: 'components-grid-reorder-grid',
                    itemId: 'importDatabaseGrid',
                    listeners: {
                        datachanged: function () {
                            this.reconfigure();
                        }
                    },
                    columnWidth: 1,
                    columns: [
                        {
                            text: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.classdomain,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.classdomain'
                            },
                            dataIndex: 'targetName',
                            align: 'left',
                            flex: 1
                        },
                        {
                            text: CMDBuildUI.locales.Locales.administration.common.labels.description,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                            },
                            dataIndex: 'description',
                            align: 'left',
                            flex: 1
                        },
                        {
                            text: CMDBuildUI.locales.Locales.administration.tasks.source,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.tasks.source'
                            },
                            dataIndex: 'source',
                            align: 'left',
                            flex: 1
                        },
                        {
                            xtype: 'actioncolumn',
                            minWidth: 150,
                            maxWidth: 150,
                            bind: {
                                hidden: '{actions.view}'
                            },
                            align: 'center',
                            items: [
                                {
                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-up', 'solid'),
                                    getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                        return CMDBuildUI.locales.Locales.administration.common.actions.moveup;
                                    },
                                    handler: function (view, rowIndex, colIndex, item, e, record, row) {
                                        var store = view.getStore();
                                        var current = store.findRecord('_id', record.get('_id'));
                                        var currentIndex = store.findExact('_id', record.get('_id'), 0);
                                        store.remove(current, true);
                                        store.insert(currentIndex - 1, current);
                                        view.ownerGrid.reconfigure();
                                    },
                                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                        if (!record.get('editing')) {
                                            rowIndex =
                                                rowIndex == null
                                                    ? view.getStore().findExact('_id', record.get('id'))
                                                    : rowIndex;
                                            return rowIndex == 0;
                                        } else {
                                            return true;
                                        }
                                    },
                                    getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                        if (record.get('editing')) {
                                            return CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid');
                                        }
                                        return CMDBuildUI.util.helper.IconHelper.getIconId('arrow-up', 'solid');
                                    },
                                    style: {
                                        margin: '10'
                                    }
                                },
                                {
                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-down', 'solid'),
                                    getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                        return CMDBuildUI.locales.Locales.administration.common.actions.movedown;
                                    },
                                    handler: function (view, rowIndex, colIndex, item, e, record, row) {
                                        var store = view.getStore();
                                        var current = store.findRecord('_id', record.get('_id'));
                                        var currentIndex = store.findExact('_id', record.get('_id'), 0);
                                        store.remove(current, true);
                                        store.insert(currentIndex + 1, current);
                                        view.ownerGrid.reconfigure();
                                    },
                                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                        if (!record.get('editing')) {
                                            rowIndex =
                                                rowIndex == null
                                                    ? view.getStore().findExact('_id', record.get('_id'))
                                                    : rowIndex;
                                            return rowIndex >= view.store.getCount() - 1;
                                        } else {
                                            return true;
                                        }
                                    },
                                    getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                        if (record.get('editing')) {
                                            return CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid');
                                        }
                                        return CMDBuildUI.util.helper.IconHelper.getIconId('arrow-down', 'solid');
                                    },
                                    margin: '0 10 0 10'
                                },
                                {
                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                                    getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                        return CMDBuildUI.locales.Locales.administration.common.actions.remove;
                                    },
                                    handler: function (view, rowIndex, colIndex, item, e, record, row) {
                                        var vm = view.lookupViewModel();
                                        view.getStore().remove(record);
                                        var selected = vm.get('theTask')._config.get('etlTemplates').length
                                            ? vm.get('theTask')._config.get('etlTemplates').split(',')
                                            : [];
                                        if (selected.indexOf(record.getId()) !== -1) {
                                            Ext.Array.remove(selected, record.get('_id'));
                                            vm.get('theTask')._config.set('etlTemplates', selected.join(','));
                                        }
                                        view.grid.reconfigure();
                                        view.up('form').down('#addTemplateBtn').setMenuItems();
                                    },
                                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                        return record.get('editing') ? true : false;
                                    },
                                    getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                        if (record.get('editing')) {
                                            return CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid');
                                        }
                                        return CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid');
                                    },
                                    margin: '0 10 0 10'
                                }
                            ]
                        }
                    ],
                    bind: {
                        store: '{selectedImportDatabaseTemplates}'
                    }
                },
                config || {}
            );
        },
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        getETLGateTemplateInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            _config[_attribute] = Ext.merge(
                {},
                {
                    displayField: 'description',
                    valueField: 'code',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.gate,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.gate'
                    },
                    allowBlank: true,
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                        store: '{gisGatesTemplatesStore}'
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = input.lookupViewModel();
                            if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                                vm.get(vmKeyObject)._config.set(_attribute, newValue);
                            }
                        }
                    },
                    triggers: {
                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                    }
                },
                config || {}
            );

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(_attribute, _config);
        },
        getActionGateActiveInput: function (vmKeyObject, attribute, config) {
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            var input = Ext.merge(
                {},
                {
                    xtype: 'checkbox',
                    name: name,
                    itemId: Ext.String.format('{0}_input', name),
                    bind: {
                        readOnly: '{actions.view}',
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (checkbox, newValue, oldValue) {
                            var vm = checkbox.lookupViewModel();
                            if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                                vm.get(vmKeyObject)._config.set(_attribute, newValue);
                            }
                            vm.set('gateActiveManager', newValue);
                        }
                    }
                },
                config || {}
            );
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.executegate,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.executegate'
                },

                items: [input]
            };
            return fieldcontainer;
        },
        getGateTypeInput: function (vmKeyObject, configKey, config) {
            config = config || {};
            var attribute = configKey.split('.')[1];
            config[attribute] = Ext.merge(
                {},
                {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.gatetype,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.gatetype'
                    },
                    name: attribute,
                    displayField: 'label',
                    valueField: 'value',
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey),
                        store: '{gatesTypeStore}'
                    },
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = input.lookupViewModel();
                            if (vm.get(vmKeyObject)._config.get(attribute) !== newValue) {
                                vm.get(vmKeyObject)._config.set(attribute, newValue);
                            }
                        }
                    },
                    triggers: {
                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                    }
                },
                config
            );
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(attribute, config);
        },

        actionGateSourceInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            _config[_attribute] = Ext.merge(
                {},
                {
                    allowBlank: false,
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    value: 'attachment',
                    textfield: {
                        listeners: {
                            change: function (input, newValue, oldValue) {
                                var vm = input.lookupViewModel();
                                if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                                    vm.get(vmKeyObject)._config.set(_attribute, newValue);
                                }
                            }
                        }
                    }
                },
                config || {}
            );

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(_attribute, _config);
        },

        getReplyAggressiveMatching: function (vmKeyObject, attribute, config) {
            var me = this;
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            var input = Ext.merge(
                {},
                {
                    xtype: 'checkbox',
                    name: name,
                    itemId: Ext.String.format('{0}_input', name),
                    bind: {
                        readOnly: '{actions.view}',
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (checkbox, newValue, oldValue) {
                            var form = me.getView();
                            var vm = form.getViewModel();
                            if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                                vm.get(vmKeyObject)._config.set(_attribute, newValue);
                            }
                        }
                    }
                },
                config || {}
            );
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.replyaggressivematching,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.replyaggressivematching'
                },

                items: [input]
            };
            return fieldcontainer;
        },

        getAttachReportInput: function (vmKeyObject, attribute, config) {
            var me = this;
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            var input = Ext.merge(
                {},
                {
                    xtype: 'checkbox',
                    name: name,
                    itemId: Ext.String.format('{0}_input', name),
                    bind: {
                        readOnly: '{actions.view}',
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (checkbox, newValue, oldValue) {
                            var form = me.getView();
                            var vm = form.getViewModel();
                            if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                                vm.get(vmKeyObject)._config.set(_attribute, newValue);
                            }
                            vm.set('attachReport', newValue);
                        }
                    }
                },
                config || {}
            );
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.attachreport,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.attachreport'
                },

                items: [input]
            };
            return fieldcontainer;
        },

        getReportInput: function (vmKeyObject, attribute, disabled) {
            var config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            config[_attribute] = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.report,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.report'
                },
                displayField: 'description',
                valueField: 'code',
                name: _attribute,
                disabled: disabled,
                bind: {
                    store: '{allReportsStore}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                combofield: {
                    listeners: {
                        change: function (input, newValue, oldValue) {
                            var vm = input.lookupViewModel();

                            if (
                                vm.get(vmKeyObject)._config &&
                                vm.get(vmKeyObject)._config.get(_attribute) !== newValue
                            ) {
                                vm.get(vmKeyObject)._config.set(_attribute, newValue);
                            }
                            if (newValue) {
                                var report = this.getStore().findRecord('code', newValue);
                                if (report) {
                                    report.getAttributes().then(function (attributesStore) {
                                        if (!vm.destroyed) {
                                            vm.getParent().set('reportAttributesStore', attributesStore);
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(_attribute, config);
        },
        getReportFormatInput: function (vmKeyObject, attribute, disabled) {
            var config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            config[_attribute] = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.format,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.format'
                },
                name: _attribute,
                disabled: disabled,
                bind: {
                    store: '{reportFormatsStore}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();

                        if (vm.get(vmKeyObject)._config && vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(_attribute, newValue);
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(_attribute, config);
        },

        getWaterwayBusInput: function (vmKeyObject, configKey) {
            var config = {};
            var attribute = configKey.split('.')[1];
            config[attribute] = {
                fieldcontainer: {
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.bus.busdedescriptor,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.bus.busdedescriptor'
                    }
                },
                displayField: 'description',
                valueField: 'code',
                forceSelection: true,
                disabled: true,
                allowBlank: false,
                bind: {
                    store: '{waterwayBusStore}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(attribute, newValue);
                            vm.get(vmKeyObject)._config.set('target', null);
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(attribute, config);
        },
        getWaterwayGateTemplateInput: function (vmKeyObject, configKey) {
            var config = {};
            var attribute = configKey.split('.')[1];
            config[attribute] = {
                fieldcontainer: {
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.gate,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.gate'
                    }
                },
                displayField: '_description',
                valueField: 'code',
                disabled: true,
                allowBlank: false,
                forceSelection: true,
                bind: {
                    store: '{waterwayGatesStore}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(attribute, newValue);
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(attribute, config);
        },
        getEmailContextVariablesGrid: function () {
            return {
                ui: 'administration-formpagination',
                xtype: 'fieldset',

                items: [
                    {
                        xtype: 'grid',
                        headerBorders: false,
                        border: false,
                        bodyBorder: false,
                        rowLines: false,
                        sealedColumns: false,
                        sortableColumns: false,
                        enableColumnHide: false,
                        enableColumnMove: false,
                        enableColumnResize: false,
                        cls: 'administration-reorder-grid',
                        itemId: 'emailContextVariableGrid',
                        selModel: {
                            pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                        },
                        viewConfig: {
                            markDirty: false
                        },
                        plugins: {
                            ptype: 'actionColumnRowEditing',
                            id: 'actionColumnRowEditing',
                            hiddenColumnsOnEdit: ['actionColumnEdit', 'actionColumnCancel'],
                            clicksToEdit: 10,
                            buttonsUi: 'button-like-tool',
                            errorSummary: false,
                            placeholdersButtons: []
                        },
                        controller: {
                            control: {
                                '#': {
                                    edit: function (editor, context, eOpts) {
                                        context.record.set('key', editor.editor.items.items[0].getValue());
                                        context.record.set('value', editor.editor.items.items[1].getValue());
                                    },
                                    beforeedit: function (editor, context, eOpts) {
                                        if (editor.view.lookupViewModel().get('actions.view')) {
                                            return false;
                                        }
                                        context.record.previousValues = context.record.getData();

                                        return true;
                                    },
                                    canceledit: function (editor, context) {
                                        if (context && context.record) {
                                            var previousKey = context.record.previousValues.key;
                                            var previousValue = context.record.previousValues.value;
                                            if (previousKey) {
                                                context.record.set('key', previousKey);
                                            }
                                            if (previousValue) {
                                                context.record.set('value', previousValue);
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        columnWidth: 1,
                        autoEl: {
                            'data-testid': 'administration-content-importexport-datatemplates-grid'
                        },

                        forceFit: true,
                        loadMask: true,

                        labelWidth: 'auto',
                        bind: {
                            store: '{emailContextVariableStore}'
                        },
                        columns: [
                            {
                                flex: 1,
                                text: CMDBuildUI.locales.Locales.administration.tasks.variable,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.tasks.variable'
                                },
                                dataIndex: 'key',
                                align: 'left',
                                editor: {
                                    xtype: 'textfield',
                                    height: 19,
                                    minHeight: 19,
                                    maxHeight: 19,
                                    padding: 0,
                                    ui: 'reordergrid-editor-combo'
                                }
                            },
                            {
                                text: CMDBuildUI.locales.Locales.administration.tasks.value,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.tasks.value'
                                },
                                flex: 1,
                                dataIndex: 'value',
                                align: 'left',
                                editor: {
                                    xtype: 'textfield',
                                    height: 19,
                                    minHeight: 19,
                                    maxHeight: 19,
                                    padding: 0,
                                    ui: 'reordergrid-editor-combo'
                                }
                            },
                            {
                                xtype: 'actioncolumn',
                                itemId: 'actionColumnEdit',
                                bind: {
                                    hidden: '{actions.view}'
                                },
                                width: 30,
                                minWidth: 30, // width property not works. Use minWidth.
                                maxWidth: 30,
                                align: 'center',
                                items: [
                                    {
                                        handler: function (grid, rowIndex, colIndex, item, e, record) {
                                            grid.editingPlugin.startEdit(record, 1);
                                        },
                                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                            return CMDBuildUI.locales.Locales.administration.common.actions.edit;
                                        },
                                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                            if (record.get('editing')) {
                                                return CMDBuildUI.util.helper.IconHelper.getIconId('check', 'solid');
                                            }
                                            return CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid');
                                        }
                                    }
                                ]
                            },
                            {
                                xtype: 'actioncolumn',
                                itemId: 'actionColumnCancel',
                                bind: {
                                    hidden: '{actions.view}'
                                },
                                width: 30,
                                minWidth: 30, // width property not works. Use minWidth.
                                maxWidth: 30,
                                align: 'center',
                                items: [
                                    {
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                            return CMDBuildUI.locales.Locales.administration.common.actions.remove;
                                        },
                                        handler: function (grid, rowIndex, colIndex) {
                                            var store = grid.getStore();
                                            var record = store.getAt(rowIndex);
                                            store.remove(record);

                                            grid.refresh();
                                        },
                                        isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                            return !record.get('editing') ? false : true;
                                        },
                                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                            metadata.value = Ext.String.insert(
                                                metadata.value,
                                                Ext.String.format(
                                                    ' data-testid="administration-importexport-attribute-removeBtn-{0}"',
                                                    rowIndex
                                                ),
                                                -7
                                            );
                                            if (record.get('editing')) {
                                                return CMDBuildUI.util.helper.IconHelper.getIconId(
                                                    'ellipsis-h',
                                                    'solid'
                                                );
                                            }
                                            return CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid');
                                        }
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        margin: '20 0 20 0',
                        xtype: 'grid',
                        headerBorders: false,
                        border: false,
                        bodyBorder: false,
                        rowLines: false,
                        sealedColumns: false,
                        sortableColumns: false,
                        enableColumnHide: false,
                        enableColumnMove: false,
                        enableColumnResize: false,
                        cls: 'administration-reorder-grid',
                        itemId: 'emailContextVariableGridNew',
                        selModel: {
                            pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                        },
                        layout: 'hbox',
                        autoEl: {
                            'data-testid': 'administration-task-sendemail-emailcontextvariable-grid-newrecord'
                        },

                        forceFit: true,
                        loadMask: true,

                        labelWidth: 'auto',
                        bind: {
                            store: '{newEmailContextVariableStore}',
                            hidden: '{actions.view}'
                        },

                        columns: [
                            {
                                xtype: 'widgetcolumn',
                                dataIndex: 'key',
                                align: 'left',
                                flex: 1,
                                widget: {
                                    xtype: 'textfield',
                                    itemId: 'newMailVariableKey',
                                    height: 19,
                                    minHeight: 19,
                                    maxHeight: 19,
                                    padding: 0,
                                    ui: 'reordergrid-editor-combo',
                                    autoEl: {
                                        'data-testid': 'administration-task-sendemail-emailcontextvariable-key'
                                    }
                                }
                            },
                            {
                                xtype: 'widgetcolumn',
                                dataIndex: 'value',
                                align: 'left',
                                flex: 1,
                                widget: {
                                    xtype: 'textfield',
                                    itemId: 'newMailVariableValue',
                                    height: 19,
                                    minHeight: 19,
                                    maxHeight: 19,
                                    padding: 0,
                                    ui: 'reordergrid-editor-combo',
                                    autoEl: {
                                        'data-testid': 'administration-task-sendemail-emailcontextvariable-value'
                                    }
                                }
                            },
                            {
                                xtype: 'actioncolumn',
                                itemId: 'actionColumnEditNew',
                                width: 30,
                                minWidth: 30, // width property not works. Use minWidth.
                                maxWidth: 30,
                                align: 'center',
                                items: [
                                    {
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                                        disabled: true
                                    }
                                ]
                            },
                            {
                                xtype: 'actioncolumn',
                                itemId: 'actionColumnAddNew',
                                width: 30,
                                minWidth: 30, // width property not works. Use minWidth.
                                maxWidth: 30,
                                align: 'center',
                                items: [
                                    {
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
                                        autoEl: {
                                            'data-testid': 'administration-task-sendemail-emailcontextvariable-addBtn'
                                        },
                                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                            return CMDBuildUI.locales.Locales.administration.common.actions.add;
                                        },
                                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                            metadata.value = Ext.String.insert(
                                                metadata.value,
                                                ' data-testid="administration-task-sendemail-emailcontextvariable-addBtn"',
                                                -7
                                            );
                                            return CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid');
                                        },
                                        handler: function (grid, rowIndex, colIndex) {
                                            var variableKey = grid.up('grid').down('#newMailVariableKey');
                                            var variableValue = grid.up('grid').down('#newMailVariableValue');
                                            if (Ext.isEmpty(variableKey.getValue())) {
                                                variableKey.focus();
                                                return false;
                                            }
                                            if (!variableValue.isValid()) {
                                                variableValue.focus();
                                                return false;
                                            }
                                            Ext.suspendLayouts();
                                            var mainGrid = grid.up('form').down('#emailContextVariableGrid');
                                            var attributeStore = mainGrid.getStore();
                                            var newAttribute = CMDBuildUI.model.base.KeyValue.create({
                                                key: variableKey.getValue(),
                                                value: variableValue.getValue()
                                            });
                                            attributeStore.add(newAttribute);
                                            variableKey.reset();
                                            variableValue.reset();
                                            Ext.resumeLayouts();
                                            mainGrid.getView().refresh();
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ]
            };
        },
        getReportParametersGrid: function (vm, data, theOwnerObject, theOwnerObjectKey) {
            return {
                ui: 'administration-formpagination',
                xtype: 'fieldset',
                items: [
                    {
                        xtype: 'grid',
                        headerBorders: false,
                        border: false,
                        bodyBorder: false,
                        rowLines: false,
                        sealedColumns: false,
                        sortableColumns: false,
                        enableColumnHide: false,
                        enableColumnMove: false,
                        enableColumnResize: false,
                        cls: 'administration-reorder-grid',
                        itemId: 'reportParametersGrid',
                        selModel: {
                            pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
                        },
                        viewConfig: {
                            markDirty: false
                        },
                        plugins: {
                            ptype: 'actionColumnRowEditing',
                            id: 'actionColumnRowEditing',
                            hiddenColumnsOnEdit: ['actionColumnEdit', 'actionColumnCancel'],
                            clicksToEdit: 10,
                            buttonsUi: 'button-like-tool',
                            errorSummary: false
                        },
                        controller: {
                            control: {
                                '#': {
                                    edit: function (editor, context, eOpts) {
                                        context.record.set('value', editor.editor.items.items[1].getValue());
                                    },
                                    beforeedit: function (editor, context, eOpts) {
                                        if (editor.view.lookupViewModel().get('actions.view')) {
                                            return false;
                                        }
                                        context.record.previousValues = context.record.getData();
                                        return true;
                                    },
                                    canceledit: function (editor, context) {
                                        if (context && context.record) {
                                            var previousValue =
                                                context.record.previousValues && context.record.previousValues.value;
                                            if (previousValue) {
                                                context.record.set('value', previousValue);
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        columnWidth: 1,
                        autoEl: {
                            'data-testid': 'administration-content-importexport-datatemplates-grid'
                        },

                        forceFit: true,
                        loadMask: true,

                        labelWidth: 'auto',
                        bind: {
                            store: '{reportParametersStore}'
                        },
                        columns: [
                            {
                                flex: 1,
                                text: CMDBuildUI.locales.Locales.administration.tasks.parameter,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.tasks.parameter'
                                },
                                dataIndex: 'key',
                                align: 'left',
                                editor: {
                                    xtype: 'displayfield',
                                    height: 19,
                                    minHeight: 19,
                                    maxHeight: 19,
                                    padding: 0,
                                    ui: 'reordergrid-editor-combo'
                                }
                            },
                            {
                                text: CMDBuildUI.locales.Locales.administration.tasks.value,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.tasks.value'
                                },
                                flex: 1,
                                dataIndex: 'value',
                                align: 'left',
                                editor: {
                                    xtype: 'textfield',
                                    height: 19,
                                    minHeight: 19,
                                    maxHeight: 19,
                                    padding: 0,
                                    ui: 'reordergrid-editor-combo'
                                }
                            },
                            {
                                xtype: 'actioncolumn',
                                itemId: 'actionColumnEdit',
                                bind: {
                                    hidden: '{actions.view}'
                                },
                                width: 30,
                                minWidth: 30, // width property not works. Use minWidth.
                                maxWidth: 30,
                                align: 'center',
                                items: [
                                    {
                                        handler: function (grid, rowIndex, colIndex, item, e, record) {
                                            grid.editingPlugin.startEdit(record, 1);
                                        },
                                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                            return CMDBuildUI.locales.Locales.administration.common.actions.edit;
                                        },
                                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                            if (record.get('editing')) {
                                                return CMDBuildUI.util.helper.IconHelper.getIconId('check', 'solid');
                                            }
                                            return CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid');
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ]
            };
        },

        getActionNotificationAutogenerateActiveInput: function (vmKeyObject, attribute, config) {
            const me = this;
            const configAttribute = attribute.split('.');
            const _attribute = configAttribute[configAttribute.length - 1];
            const input = Ext.merge(
                {},
                {
                    xtype: 'checkbox',
                    name: _attribute,
                    itemId: Ext.String.format('{0}_input', _attribute),
                    bind: {
                        readOnly: '{actions.view}',
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    },
                    listeners: {
                        change: function (checkbox, newValue, oldValue) {
                            const form = me.getView();
                            const vm = form.getViewModel();
                            if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                                vm.get(vmKeyObject)._config.set(_attribute, newValue);
                            }
                            checkbox.lookupViewModel().set('isIgnoreAutogenerated', newValue);
                        }
                    }
                },
                config || {}
            );
            const fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.ignoreautogenaretedemails,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.ignoreautogenaretedemails'
                },

                items: [input]
            };
            return fieldcontainer;
        },

        getActionNotificationAutogenerateTemplateInput: function (vmKeyObject, configKey) {
            var config = {};
            var attribute = configKey.split('.')[1];
            config[attribute] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.autogeneratedemailsnotification,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.autogeneratedemailsnotification'
                    },
                    hidden: true,
                    bind: {
                        hidden: Ext.String.format('{!{0}.config.action_notification_autogenerated_active}', vmKeyObject)
                    }
                },
                name: attribute,
                displayField: 'description',
                valueField: 'name',
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey),
                    store: '{actionNotificationAutogenerateTemplates}'
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(attribute, newValue);
                        }
                    }
                },
                triggers: {
                    clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(attribute, config);
        }
    }
});
