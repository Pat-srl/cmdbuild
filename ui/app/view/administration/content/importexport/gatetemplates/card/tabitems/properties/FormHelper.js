Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.properties.FormHelper', {
    singleton: true,

    getGeneralPropertiesFieldset: function () {
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            layout: 'column',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            items: [
                // row
                this.createContainer([
                    //input
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCodeInput(
                        {
                            code: {
                                vtype: 'nameInputValidationWithDash',
                                allowBlank: false,
                                bind: {
                                    value: '{theGate.code}',
                                    disabled: '{actions.edit}'
                                }
                            }
                        },
                        true,
                        '[name="description"]'
                    ),

                    CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                        description: {
                            allowBlank: false,
                            bind: {
                                value: '{theGate.description}'
                            },
                            fieldcontainer: {
                                labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                                labelToolIconQtip:
                                    CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                                labelToolIconClick: 'onTranslateDescriptionClick'
                            }
                        }
                    })
                ]),
                this.createContainer(
                    [
                        CMDBuildUI.util.administration.helper.FieldsHelper.getAllClassesProcessesViewsInput(
                            {
                                shape_import_target_class: {
                                    fieldcontainer: {
                                        allowBlank: false,
                                        fieldLabel:
                                            CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels
                                                .targetclass,
                                        localized: {
                                            fieldLabel:
                                                'CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.targetclass'
                                        }
                                    },
                                    allowBlank: false,
                                    withClasses: true,
                                    bind: {
                                        value: '{theHandler.shape_import_target_class}'
                                    },
                                    combofield: {
                                        listeners: {
                                            change: function (combo, newValue, oldValue) {
                                                if (oldValue) {
                                                    var vm = combo.lookupViewModel();
                                                    var theHandler = vm.get('theHandler');
                                                    theHandler.set('shape_import_target_attr', null);
                                                    theHandler.set('shape_import_key_attr', null);
                                                    theHandler.set('shape_import_key_source', null);
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            'shape_import_target_class'
                        ),
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                            'shape_import_key_attr',
                            {
                                shape_import_key_attr: {
                                    fieldcontainer: {
                                        fieldLabel: CMDBuildUI.locales.Locales.administration.gates.importkeyattributes,
                                        localized: {
                                            fieldLabel:
                                                'CMDBuildUI.locales.Locales.administration.gates.importkeyattributes'
                                        }
                                    },
                                    allowBlank: false,
                                    combofield: {
                                        forceSelection: true,
                                        bind: {
                                            store: '{classAttributesStore}',
                                            value: '{theHandler.shape_import_key_attr}'
                                        }
                                    },
                                    displayfield: {
                                        bind: {
                                            value: '{theHandler._shape_import_key_attr_description}'
                                        },
                                        renderer: function (value) {
                                            var vm = this.lookupViewModel();
                                            if (!value) {
                                                vm.bind(
                                                    {
                                                        bindTo: {
                                                            value: '{theHandler.shape_import_key_attr}',
                                                            store: '{classAttributesStore}'
                                                        },
                                                        single: true
                                                    },
                                                    function (data) {
                                                        var recordLabels = [];
                                                        if (data.store && data.value) {
                                                            Ext.Array.forEach(
                                                                data.value.split(','),
                                                                function (item, index, allitems) {
                                                                    var record = data.store.findRecord('value', item);
                                                                    if (record) {
                                                                        recordLabels.push(record.get('label'));
                                                                    }
                                                                }
                                                            );
                                                        }
                                                        vm.set(
                                                            'theHandler._shape_import_key_attr_description',
                                                            recordLabels.join(', ')
                                                        );
                                                    }
                                                );
                                            }
                                            return value;
                                        }
                                    }
                                }
                            },
                            null,
                            null,
                            true
                        ),
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(
                            'shape_import_key_source',
                            {
                                shape_import_key_source: {
                                    fieldcontainer: {
                                        fieldLabel: CMDBuildUI.locales.Locales.administration.gates.importkeysource,
                                        localized: {
                                            fieldLabel:
                                                'CMDBuildUI.locales.Locales.administration.gates.importkeysource'
                                        }
                                    },
                                    allowBlank: false,
                                    bind: {
                                        value: '{theHandler.shape_import_key_source}'
                                    }
                                }
                            }
                        )
                    ],
                    {
                        bind: {
                            hidden: '{gateType !== "gis" && gateType !== "cad"}'
                        },
                        listeners: this._getShowHideContainerEvents()
                    }
                ),
                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                        active: {
                            allowBlank: false,
                            bind: {
                                value: '{theGate.enabled}' // change to active issue server
                            }
                        }
                    })
                ])
            ]
        };
    },

    getGeoserverDisabledMessage: function () {
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            layout: 'column',
            style: 'border:0!important',
            hidden: true,
            bind: {
                hidden: '{geoserverEnabled}'
            },
            items: [
                {
                    xtype: 'fieldcontainer',
                    items: [
                        {
                            xtype: 'displayfield',
                            bind: {
                                value: '<i>{geoserverDisabledMessage}</i>'
                            }
                        }
                    ]
                }
            ]
        };
    },

    getShapePropertiesFieldset: function () {
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            layout: 'column',
            collapsible: true,
            hidden: true,
            title: CMDBuildUI.locales.Locales.administration.gates.shaperproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.gates.shaperproperties'
            },
            bind: {
                hidden: '{!geoserverEnabled}'
            },
            items: [
                // config handler
                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('shape_import_enabled', {
                        shape_import_enabled: {
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.gates.enableshapeimport,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.gates.enableshapeimport'
                                }
                            },
                            bind: {
                                readOnly: '{actions.view}',
                                value: '{theHandler.shape_import_enabled}' // change to active issue server
                            }
                        }
                    })
                ]),

                this.createContainer(
                    [
                        this.createContainer(
                            [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                                    '_shape_import_include_or_exclude',
                                    {
                                        _shape_import_include_or_exclude: {
                                            columnWidth: 1,
                                            fieldcontainer: {
                                                fieldLabel:
                                                    CMDBuildUI.locales.Locales.administration.gates.importcadlayers,
                                                localized: {
                                                    fieldLabel:
                                                        'CMDBuildUI.locales.Locales.administration.gates.importcadlayers'
                                                }
                                            },
                                            combofield: {
                                                bind: {
                                                    value: '{theHandler._shape_import_include_or_exclude}',
                                                    store: '{shapeImportIncludeOrExludeStore}',
                                                    disabled: '{!geoserverEnabled}'
                                                }
                                            },
                                            displayfield: {
                                                bind: {
                                                    value: '{theHandler._shape_import_include_or_exclude}',
                                                    store: '{shapeImportIncludeOrExludeStore}',
                                                    disabled: '{!geoserverEnabled}'
                                                }
                                            }
                                        }
                                    }
                                )
                            ],
                            {
                                columnWidth: 0.5
                            }
                        ),

                        this.createContainer(
                            [
                                this.createContainer(
                                    [
                                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput(
                                            'shape_import_source_layers_exclude',
                                            {
                                                shape_import_source_layers_exclude: {
                                                    columnWidth: 1,
                                                    fieldcontainer: {
                                                        fieldLabel:
                                                            CMDBuildUI.locales.Locales.administration.gates
                                                                .sourcelayertoexclude,
                                                        localized: {
                                                            fieldLabel:
                                                                'CMDBuildUI.locales.Locales.administration.gates.sourcelayertoexclude'
                                                        },
                                                        allowBlank: true
                                                    },
                                                    emptyText:
                                                        CMDBuildUI.locales.Locales.administration.gates
                                                            .sourcelayertoexcludeempty,
                                                    localized: {
                                                        fieldLabel:
                                                            'CMDBuildUI.locales.Locales.administration.gates.sourcelayertoexclude',
                                                        emptyText:
                                                            'CMDBuildUI.locales.Locales.administration.gates.sourcelayertoexcludeempty'
                                                    },
                                                    allowBlank: true,
                                                    bind: {
                                                        value: '{theHandler.shape_import_source_layers_exclude}',
                                                        disabled: '{!geoserverEnabled}'
                                                    }
                                                }
                                            }
                                        )
                                    ],
                                    {
                                        bind: {
                                            hidden: '{shapeImportExcludeHidden}'
                                        },
                                        hidden: true,
                                        listeners: this._getShowHideContainerEvents()
                                    }
                                ),

                                this.createContainer(
                                    [
                                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput(
                                            'shape_import_source_layers_include',
                                            {
                                                shape_import_source_layers_include: {
                                                    columnWidth: 1,
                                                    fieldcontainer: {
                                                        fieldLabel:
                                                            CMDBuildUI.locales.Locales.administration.gates
                                                                .sourcelayertoinclude,
                                                        localized: {
                                                            fieldLabel:
                                                                'CMDBuildUI.locales.Locales.administration.gates.sourcelayertoinclude'
                                                        },
                                                        allowBlank: true
                                                    },
                                                    emptyText:
                                                        CMDBuildUI.locales.Locales.administration.gates
                                                            .sourcelayertoincludeempty,
                                                    localized: {
                                                        emptyText:
                                                            'CMDBuildUI.locales.Locales.administration.gates.sourcelayertoincludeempty'
                                                    },
                                                    allowBlank: true,
                                                    bind: {
                                                        value: '{theHandler.shape_import_source_layers_include}',
                                                        disabled: '{!geoserverEnabled}'
                                                    },
                                                    listeners: {
                                                        blur: function (textarea, event, eOpts) {
                                                            var value = textarea.getValue();
                                                            var layers = value.split(',');
                                                            var cleanedlayers = [];
                                                            Ext.Array.forEach(layers, function (item) {
                                                                var itemValue = item.trim();
                                                                if (itemValue && itemValue.length) {
                                                                    cleanedlayers.push(itemValue);
                                                                }
                                                            });
                                                            textarea.setValue(cleanedlayers.join(','));
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                    ],
                                    {
                                        hidden: true,
                                        bind: {
                                            hidden: '{shapeImportIncludeHidden}'
                                        },
                                        listeners: this._getShowHideContainerEvents()
                                    }
                                )
                            ],
                            {
                                columnWidth: 0.5
                            }
                        )
                    ],
                    {
                        bind: {
                            hidden: '{!theHandler.shape_import_enabled}'
                        },
                        hidden: true
                    }
                ),

                this.createContainer(
                    [
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                            'shape_import_target_attr',
                            {
                                shape_import_target_attr: {
                                    fieldcontainer: {
                                        fieldLabel: CMDBuildUI.locales.Locales.administration.gates.targetlayer,
                                        localized: {
                                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.gates.targetlayer'
                                        }
                                    },
                                    combofield: {
                                        forceSelection: true,
                                        displayField: 'label',
                                        valueField: 'value',
                                        bind: {
                                            value: '{theHandler.shape_import_target_attr}',
                                            store: '{classGeolayerStore}',
                                            disabled: '{!geoserverEnabled}'
                                        },
                                        listeners: {
                                            show: function () {
                                                this.setValue(this.getValue());
                                            }
                                        }
                                    },
                                    displayfield: {
                                        bind: {
                                            value: '{theHandler._shape_import_target_attr_description}',
                                            disabled: '{!geoserverEnabled}'
                                        }
                                    }
                                }
                            }
                        )
                    ],
                    {
                        bind: {
                            hidden: '{!theHandler.shape_import_enabled}'
                        },
                        hidden: true,
                        listeners: this._getShowHideContainerEvents()
                    }
                )
            ]
        };
    },

    getAssociationPropertiesFieldset: function () {
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            layout: 'column',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.gates.associationproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.gates.associationproperties'
            },
            items: [
                // config handler
                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput(
                        'bimserver_project_has_parent',
                        {
                            bimserver_project_has_parent: {
                                columnWidth: 0.5,
                                fieldcontainer: {
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.gates.hasparent,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.gates.hasparent'
                                    }
                                },
                                bind: {
                                    value: '{theGate.config.bimserver_project_has_parent}',
                                    readOnly: '{actions.view}'
                                },
                                listeners: {
                                    change: function (input, newValue, oldValue) {
                                        var vm = input.lookupViewModel();
                                        if (
                                            vm.get('theGate').getConfig() &&
                                            vm.get('theGate').getConfig().get('bimserver_project_has_parent') !==
                                                newValue
                                        ) {
                                            vm.get('theGate').getConfig().set('bimserver_project_has_parent', newValue);
                                        }
                                    }
                                }
                            }
                        }
                    )
                ]),

                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getAllClassesProcessesViewsInput(
                        {
                            bimserver_project_master_card_target_class: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.gis.associatedclass,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.associatedclass'
                                },
                                allowBlank: false,
                                bind: {
                                    value: '{theGate.config.bimserver_project_master_card_target_class}'
                                },
                                withClasses: true,
                                listeners: {
                                    change: function (input, newValue, oldValue) {
                                        var vm = input.lookupViewModel();
                                        if (
                                            vm.get('theGate').getConfig() &&
                                            vm
                                                .get('theGate')
                                                .getConfig()
                                                .get('bimserver_project_master_card_target_class') !== newValue
                                        ) {
                                            vm.get('theGate')
                                                .getConfig()
                                                .set('bimserver_project_master_card_target_class', newValue);
                                        }
                                    }
                                }
                            }
                        },
                        'bimserver_project_master_card_target_class'
                    )
                ]),

                this.createContainer([
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(
                        'bimserver_project_master_card_key_source',
                        {
                            bimserver_project_master_card_key_source: {
                                fieldcontainer: {
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.gates.sourcepaths,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.gates.sourcepaths'
                                    }
                                },
                                bind: {
                                    value: '{theGate.config.bimserver_project_master_card_key_source}'
                                },
                                listeners: {
                                    change: function (input, newValue, oldValue) {
                                        var vm = input.lookupViewModel();
                                        if (
                                            vm.get('theGate').getConfig() &&
                                            vm
                                                .get('theGate')
                                                .getConfig()
                                                .get('bimserver_project_master_card_key_source') !== newValue
                                        ) {
                                            vm.get('theGate')
                                                .getConfig()
                                                .set('bimserver_project_master_card_key_source', newValue);
                                        }
                                    }
                                }
                            }
                        }
                    ),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(
                        'bimserver_project_master_card_key_attr',
                        {
                            bimserver_project_master_card_key_attr: {
                                fieldcontainer: {
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.gates.attributes,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.gates.attributes'
                                    }
                                },
                                bind: {
                                    value: '{theGate.config.bimserver_project_master_card_key_attr}'
                                },
                                listeners: {
                                    change: function (input, newValue, oldValue) {
                                        var vm = input.lookupViewModel();
                                        if (
                                            vm.get('theGate').getConfig() &&
                                            vm
                                                .get('theGate')
                                                .getConfig()
                                                .get('bimserver_project_master_card_key_attr') !== newValue
                                        ) {
                                            vm.get('theGate')
                                                .getConfig()
                                                .set('bimserver_project_master_card_key_attr', newValue);
                                        }
                                    }
                                }
                            }
                        }
                    )
                ])
            ]
        };
    },

    getDatabasePropertiesFieldset: function () {
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            layout: 'column',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.importexport.texts.databaseconfiguration,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.importexport.texts.databaseconfiguration'
            },
            items: [
                this.createContainer([
                    this.getSourceTypeInput('theGate', 'config.sourceType'),
                    this.getJdbcDriverClassNameInput('theGate', 'config.jdbcDriverClassName')
                ]),
                this.createContainer([this.getJdbcUrlInput('theGate', 'config.jdbcUrl')]),
                this.createContainer([
                    this.getJdbcUsernameInput('theGate', 'config.jdbcUsername'),
                    this.getJdbcPasswordInput('theGate', 'config.jdbcPassword')
                ]),
                this.createContainer(this.getErrorTemplateInput('theGate', 'config.errorTemplate'))
            ]
        };
    },

    createContainer: function (items, config) {
        var container = Ext.merge(
            {},
            {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 1,
                items: items
            },
            config || {}
        );

        return container;
    },

    privates: {
        _setAllowBlank: function (container, value) {
            container.items.each(function (fieldcontainer) {
                if (fieldcontainer) {
                    fieldcontainer.items.each(function (input) {
                        if (
                            ['textfield', 'combo', 'combobox', 'textarea', 'allelementscombo', 'tagfield'].indexOf(
                                input.getXType()
                            ) > -1
                        ) {
                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                input,
                                value,
                                input.up('form')
                            );
                        }
                    });
                }
            });
        },

        _getShowHideContainerEvents: function () {
            var me = this;
            return {
                hide: function (container) {
                    me._setAllowBlank(container, true);
                },
                show: function (container) {
                    me._setAllowBlank(container, false);
                }
            };
        },

        getSourceTypeInput: function (vmKeyObject, configKey) {
            var config = {};
            var attribute = configKey.split('.')[1];
            config[attribute] = {
                fieldcontainer: {
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.sourcetype,
                    allowBlank: false,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.sourcetype'
                    },
                    hidden: true
                },
                disabled: true,
                allowBlank: false,
                bind: {
                    store: Ext.String.format('{{0}Store}', attribute),
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (
                            vm.get(vmKeyObject).getConfig() &&
                            vm.get(vmKeyObject).getConfig().get(attribute) !== newValue
                        ) {
                            vm.get(vmKeyObject).getConfig().set(attribute, newValue);
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(attribute, config);
        },

        getJdbcDriverClassNameInput: function (vmKeyObject, configKey) {
            var config = {};
            var attribute = configKey.split('.')[1];
            config[attribute] = {
                fieldcontainer: {
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.driverclassname,
                    allowBlank: false,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.driverclassname'
                    }
                },
                allowBlank: false,
                bind: {
                    store: Ext.String.format('{{0}Store}', attribute),
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (
                            vm.get(vmKeyObject).getConfig() &&
                            vm.get(vmKeyObject).getConfig().get(attribute) !== newValue
                        ) {
                            vm.get(vmKeyObject).getConfig().set(attribute, newValue);
                            var jdbcAddress =
                                CMDBuildUI.util.administration.helper.ModelHelper.getJdbcDriverAddress(newValue);
                            vm.get(vmKeyObject).getConfig().set('jdbcUrl', jdbcAddress);
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(attribute, config);
        },

        getJdbcUrlInput: function (vmKeyObject, configKey) {
            var config = {};
            var attribute = configKey.split('.')[1];
            config[attribute] = {
                fieldcontainer: {
                    columnWidth: 1,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.address,
                    allowBlank: false,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.address'
                    }
                },
                htmlEncode: true,
                allowBlank: false,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (
                            vm.get(vmKeyObject).getConfig() &&
                            vm.get(vmKeyObject).getConfig().get(attribute) !== newValue
                        ) {
                            vm.get(vmKeyObject).getConfig().set(attribute, newValue);
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(attribute, config);
        },

        getJdbcUsernameInput: function (vmKeyObject, configKey) {
            var config = {};
            var attribute = configKey.split('.')[1];
            config[attribute] = {
                fieldcontainer: {
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.username,
                    allowBlank: false,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.username'
                    }
                },
                allowBlank: false,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (
                            vm.get(vmKeyObject).getConfig() &&
                            vm.get(vmKeyObject).getConfig().get(attribute) !== newValue
                        ) {
                            vm.get(vmKeyObject).getConfig().set(attribute, newValue);
                        }
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
                    allowBlank: false,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.password'
                    }
                },
                displayfield: {
                    bind: {
                        value: '{jdbcPasswordEmptyText}'
                    }
                },
                textfield: {
                    allowBlank: false,
                    bind: {
                        emptyText: '{jdbcPasswordEmptyText}'
                        //value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey)
                    },
                    listeners: {
                        beforerender: function (input) {
                            var vm = input.lookupViewModel();
                            vm.bind('{actions.edit}', function (isEdit) {
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    input,
                                    isEdit,
                                    input.up('form')
                                );
                            });
                        },
                        change: function (input, newValue, oldValue) {
                            var vm = input.lookupViewModel();
                            if (
                                vm.get(vmKeyObject).getConfig() &&
                                vm.get(vmKeyObject).getConfig().get(attribute) !== newValue
                            ) {
                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                    input,
                                    false,
                                    input.up('form')
                                );
                                vm.get(vmKeyObject).getConfig().set(attribute, newValue);
                            }
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(attribute, config, true);
        },

        getErrorTemplateInput: function (vmKeyObject, configKey) {
            var config = {};
            var attribute = configKey.split('.')[1];
            config[attribute] = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.errortemplate,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.errortemplate'
                },
                name: attribute,
                displayField: 'description',
                valueField: '_id',
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, configKey),
                    store: '{allEmailTemplates}'
                },
                combofield: {
                    listeners: {}
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (
                            vm.get(vmKeyObject).getConfig() &&
                            vm.get(vmKeyObject).getConfig().get(attribute) !== newValue
                        ) {
                            vm.get(vmKeyObject).getConfig().set(attribute, newValue);
                        }
                    },
                    afterrender: function (combo) {
                        var vm = combo.lookupViewModel();
                        var sourceStore = vm.getStore('allEmailTemplates').source;
                        if (!sourceStore.isLoading() && !sourceStore.isLoaded()) {
                            sourceStore.load();
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(attribute, config);
        }
    }
});
