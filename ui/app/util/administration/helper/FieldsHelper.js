Ext.define('CMDBuildUI.util.administration.helper.FieldsHelper', {
    singleton: true,

    getFillTypeInput: function (config) {
        const display = this._setTestId(
            this._getDisplayfield(config.fillType, {
                displayField: 'label',
                valueField: 'value'
            }),
            'fillType'
        );

        const combo = this._getCombofield(
            Ext.merge({}, config.fillType, {
                columnWidth: 1,
                xtype: 'combobox',
                clearFilterOnBlur: true,
                queryMode: 'local',
                displayField: 'label',
                valueField: 'value',
                name: 'fillType',
                bind: {
                    hidden: '{actions.view}'
                }
            }),
            'fillType'
        );

        return {
            xtype: 'fieldcontainer',
            columnWidth: 0.5,
            layout: 'column',
            fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.filltype,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.filltype'
            },
            items: [combo, display]
        };
    },

    getFillPatternInput: function (config) {
        return Ext.merge(
            {
                columnWidth: 0.5,
                xtype: 'fieldcontainer',
                layout: 'hbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillpattern,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillpattern'
                },
                items: [
                    CMDBuildUI.util.helper.FieldsHelper.getPatternPickerField(
                        Ext.merge({}, config.fillPattern, {
                            columnWidth: 1,
                            alt: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillpattern,
                            localized: {
                                alt: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillpattern'
                            },
                            bind: {
                                hidden: '{actions.view}'
                            }
                        })
                    )
                ]
            },
            config.fillTypeParent
        );
    },

    getFillOpacityInput: function (config) {
        return {
            columnWidth: 0.5,
            xtype: 'fieldcontainer',
            layout: 'hbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillopacity,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillopacity'
            },
            items: [
                CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField(
                    Ext.merge({}, config.fillOpacity, {
                        // columnWidth: 1,
                        flex: 1,
                        padding: '0 15 0 0',
                        name: 'fillOpacity',
                        increment: 0.01,
                        minValue: 0,
                        maxValue: 1,
                        multiplier: 100,
                        inputDecimalPrecision: 0,
                        sliderDecimalPrecision: 2,
                        showPercentage: true
                    })
                )
            ]
        };
    },

    getFillColorInput: function (config) {
        return {
            columnWidth: 0.5,
            xtype: 'fieldcontainer',
            layout: 'column',
            fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor'
            },
            items: [
                CMDBuildUI.util.helper.FieldsHelper.getColorpickerField(
                    Ext.merge({}, config.fillColor, {
                        columnWidth: 1,
                        alt: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor,
                        localized: {
                            alt: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor'
                        },
                        bind: {
                            hidden: '{actions.view}'
                        }
                    })
                )
            ]
        };
    },

    getPointRadiusInput: function (config) {
        return {
            columnWidth: 0.5,
            xtype: 'fieldcontainer',
            layout: 'column',
            fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.pointradius,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.pointradius'
            },
            items: [
                Ext.merge({}, config.pointRadius, {
                    columnWidth: 1,
                    xtype: 'numberfield',
                    minValue: 0,
                    step: 1,
                    decimalPrecision: 0,
                    name: 'pointRadius',
                    bind: {
                        hidden: '{actions.view}'
                    }
                }),
                Ext.merge({}, config.pointRadius, {
                    xtype: 'displayfield',
                    bind: {
                        hidden: '{!actions.view}'
                    }
                })
            ]
        };
    },

    getIconInput: function (config) {
        return {
            columnWidth: 0.5,
            xtype: 'fieldcontainer',
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.icon,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
            },
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                Ext.merge(
                    {},
                    {
                        columnWidth: 1,
                        xtype: 'filefield',

                        emptyText: CMDBuildUI.locales.Locales.administration.common.strings.selectpngfile,
                        localized: {
                            emptyText: 'CMDBuildUI.locales.Locales.administration.common.strings.selectpngfile'
                        },
                        accept: '.png',
                        buttonConfig: {
                            ui: 'administration-secondary-action-small'
                        },
                        bind: {
                            hidden: '{actions.view}'
                        }
                    },
                    config.icon.input
                ),
                Ext.merge(
                    {},
                    {
                        xtype: 'previewimage',
                        hidden: true,
                        imageHeigth: 32,
                        imageWidth: 32,
                        alt: CMDBuildUI.locales.Locales.administration.common.strings.currenticon,
                        localized: {
                            alt: 'CMDBuildUI.locales.Locales.administration.common.strings.currenticon'
                        }
                    },
                    config.icon.preview
                )
            ]
        };
    },

    getIconComboInput: function (config) {
        return {
            columnWidth: 0.5,
            xtype: 'fieldcontainer',
            layout: 'column',
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.icon,
            items: [
                Ext.merge({}, config.icon.input, {
                    flex: 1,
                    xtype: 'combo',
                    columnWidth: 1,
                    emptyText: CMDBuildUI.locales.Locales.administration.common.strings.selectimage,
                    valueField: '_id',
                    displayField: '_description',

                    store: {
                        model: 'CMDBuildUI.model.icons.Icon',
                        autoLoad: true,
                        autoDestroy: true,
                        proxy: {
                            url: Ext.String.format('{0}/uploads/?path=images/gis', CMDBuildUI.util.Config.baseUrl),
                            type: 'baseproxy'
                        },
                        sorters: ['description'],
                        pageSize: 0
                    },
                    bind: {
                        hidden: '{actions.view}'
                    }
                }),
                Ext.merge({}, config.icon.preview, {
                    xtype: 'image',
                    width: 32,
                    maxWidth: 32,
                    maxHeight: 32,
                    height: 'auto',
                    alt: CMDBuildUI.locales.Locales.administration.common.labels.icon,
                    reference: 'currentIconPreview', // TODO remove all reference occurence
                    tooltip: CMDBuildUI.locales.Locales.administration.common.strings.currenticon,
                    config: {
                        theValue: null
                    }
                })
            ]
        };
    },

    getStrokeDashStyleInput: function (config) {
        var display = this._setTestId(
            this._getDisplayfield(config.strokeDashstyle, {
                displayField: 'label',
                valueField: 'value'
            }),
            'strokeDashstyle'
        );

        var combo = this._getCombofield(
            Ext.merge({}, config.strokeDashstyle, {
                columnWidth: 1,
                xtype: 'combobox',
                clearFilterOnBlur: true,
                queryMode: 'local',
                displayField: 'label',
                valueField: 'value',
                name: 'strokeDashstyle',
                bind: {
                    hidden: '{actions.view}'
                }
            }),
            'strokeDashstyle'
        );

        return {
            xtype: 'fieldcontainer',
            columnWidth: 0.5,
            layout: 'column',
            fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokedashstyle,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokedashstyle'
            },
            items: [combo, display]
        };
    },

    getStrokeColorInput: function (config) {
        return {
            columnWidth: 0.5,
            xtype: 'fieldcontainer',

            fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor'
            },
            layout: 'column',
            items: [
                CMDBuildUI.util.helper.FieldsHelper.getColorpickerField(
                    Ext.merge({}, config.strokeColor.input, {
                        columnWidth: 1,
                        alt: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor,
                        localized: {
                            alt: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor'
                        },
                        bind: {
                            hidden: '{actions.view}'
                        }
                    })
                )
            ]
        };
    },

    getStrokeOpacityInput: function (config) {
        return {
            xtype: 'fieldcontainer',
            columnWidth: 0.5,
            layout: 'hbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokeopacity,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokeopacity'
            },
            items: [
                CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField(
                    Ext.merge({}, config.strokeOpacity, {
                        flex: 1,
                        padding: '0 15 0 0',
                        name: 'strokeOpacity',
                        increment: 0.01,
                        minValue: 0,
                        maxValue: 1,
                        multiplier: 100,
                        inputDecimalPrecision: 0,
                        sliderDecimalPrecision: 2,
                        showPercentage: true
                    })
                )
            ]
        };
    },

    getStrokeWidthInput: function (config) {
        var display = this._setTestId(this._getDisplayfield(config.strokeWidth), 'strokeWidth');
        var input = this._getNumberfield(config.strokeWidth, 'strokeWidth');
        var obj = {
            xtype: 'fieldcontainer',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokewidth,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokewidth'
            },
            items: [display, input]
        };
        return obj;
    },

    getClusterDistanceInput: function (config) {
        return {
            xtype: 'fieldcontainer',
            columnWidth: 0.5,
            layout: 'hbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.clusterdistance,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.clusterdistance'
            },
            hidden: true,
            bind: {
                hidden: '{!theGeoAttribute.style.clusterEnable}'
            },
            items: [
                CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField(
                    Ext.merge({}, config.clusterDistance, {
                        flex: 1,
                        name: 'clusterDistance',
                        increment: 1,
                        minValue: 0,
                        maxValue: 200,
                        multiplier: 1,
                        inputDecimalPrecision: 0,
                        sliderDecimalPrecision: 0,
                        showPercentage: false
                    })
                )
            ]
        };
    },

    getFunctionsInput: function (config, propertyName) {
        propertyName = propertyName || 'function';

        var input = this._getCombofield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName], {
            fieldLabel: 'description',
            fieldValue: 'name'
        });
        this._setTestId(display, propertyName);
        return Ext.merge(
            {},
            {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.funktion,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.funktion'
                },
                allowBlank: config[propertyName].allowBlank,
                items: [input, display]
            },
            (config[propertyName] && config[propertyName].fieldcontainer) || {}
        );
    },

    getNameInput: function (config, disabledOnEdit, copyToInput) {
        var propertyName = 'name';
        var input = this._getTextfield(config[propertyName], propertyName, false, disabledOnEdit, copyToInput);
        var display = this._setTestId(this._getDisplayfield(config[propertyName]), propertyName);

        return {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            allowBlank: config[propertyName].allowBlank,
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
            },
            items: [input, display]
        };
    },

    getCodeInput: function (config, disabledOnEdit, copyToInput, mode) {
        var propertyName = 'code';
        var input = this._getTextfield(config[propertyName], propertyName, false, disabledOnEdit, copyToInput);
        var display = this._getDisplayfield(config[propertyName]);
        var inputs = this.getFieldsByMode(mode, input, display);
        this._setTestId(display, propertyName);
        return {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            allowBlank: config[propertyName].allowBlank,
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.code,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.code'
            },
            items: inputs
        };
    },

    getActiveOnSaveInput: function (config, name, disabledOnEdit) {
        var propertyName = name || 'activeonsave';
        var input = this._getCheckboxfield(config[propertyName], propertyName);

        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.tesks.labels.activeonsave,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.tesks.labels.activeonsave'
            },
            items: [input]
        };

        return fieldcontainer;
    },

    getEmailAccountsInput: function (config) {
        var propertyName = 'emailaccounts';
        var input = this._getCombofield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName], {
            fieldLabel: 'name',
            fieldValue: '_id'
        });
        this._setTestId(display, propertyName);
        return {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.tesks.labels.emailaccount,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.tesks.labels.emailaccount'
            },
            items: [input, display]
        };
    },

    getIncomingFolderInput: function (config) {
        var propertyName = 'incomingfolder';
        var input = this._getTextfield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName]);
        this._setTestId(display, propertyName);
        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            allowBlank: config[propertyName].allowBlank,
            fieldLabel: CMDBuildUI.locales.Locales.administration.tesks.labels.incomingfolder,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.tesks.labels.incomingfolder'
            },
            items: [input, display]
        };

        return fieldcontainer;
    },

    getFilterTypeInput: function (config) {
        var propertyName = 'filtertype';
        var input = this._getCombofield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName], {
            fieldLabel: 'label',
            fieldValue: 'value'
        });
        this._setTestId(display, propertyName);
        return {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.tesks.labels.filtertype,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.tesks.labels.filtertype'
            },
            items: [input, display]
        };
    },
    getDescriptionInput: function (config, mode) {
        var propertyName = 'description';
        var input = this._getTextfield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName]);
        this._setTestId(display, propertyName);
        var items = this.getFieldsByMode(mode, input, display);
        var fieldcontainer = Ext.merge(
            {},
            {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                allowBlank: config[propertyName].allowBlank,
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                },
                items: items
            },
            config.description.fieldcontainer || {}
        );

        return fieldcontainer;
    },

    getActiveInput: function (config, name, disabledOnEdit) {
        var propertyName = name || 'active';
        var input = this._getCheckboxfield(config[propertyName], propertyName);

        var fieldcontainer = Ext.merge(
            {},
            {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                },
                items: [input]
            },
            config[propertyName].fieldcontainer ? config[propertyName].fieldcontainer : {}
        );

        return fieldcontainer;
    },

    getReferenceField: function (attributeName, attribute, config, mergeOptionsInput, mergeOptionDisplay) {
        attributeName = attributeName || 'associatedCard';
        var fieldcontainer = Ext.merge(
            {},
            {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: config[attributeName].fieldcontainer.fieldLabel
            },
            config[attributeName].fieldcontainer
        );
        delete config[attributeName].fieldcontainer;
        var input = this._getReferenceField(attributeName, attribute, config[attributeName], mergeOptionsInput);

        var display = this._getDisplayReference(attributeName, attribute, config[attributeName], mergeOptionDisplay);
        fieldcontainer.items = [input, display];
        return fieldcontainer;
    },

    getLastCheckin: function (config) {
        var propertyName = 'lastCheckin';

        var display = this._getDisplayfield(config[propertyName]);
        this._setTestId(display, propertyName);
        delete display.hidden;
        delete display.bind.hidden;
        display.renderer = function (value) {
            return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value);
        };
        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: 'CMDBuildUI.locales.Locales.administration.bim.lastcheckin',
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.bim.lastcheckin'
            },
            bind: {
                hidden: '{actions.add}'
            },
            items: [display]
        };

        return fieldcontainer;
    },

    getGeoServerEnabledInput: function (config, name, disabledOnEdit) {
        var propertyName = name || 'active';
        var input = this._getCheckboxfield(config[propertyName], propertyName);

        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 1,
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.messages.enabled,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.messages.enabled'
            },
            items: [input]
        };

        return fieldcontainer;
    },

    getGeoServerUrlInput: function (config) {
        var propertyName = 'geoserverurl';
        var input = this._getTextfield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName]);
        this._setTestId(display, propertyName);
        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.url',
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.url'
            },
            items: [input, display]
        };

        return fieldcontainer;
    },

    getGeoServerWorkspaceInput: function (config) {
        var propertyName = 'geoserverworkspace';
        var input = this._getTextfield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName]);
        this._setTestId(display, propertyName);
        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.gis.workspace,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.workspace'
            },
            items: [input, display]
        };

        return fieldcontainer;
    },

    getGeoServerAdminUserInput: function (config) {
        var propertyName = 'geoserveradminuser';
        var input = this._getTextfield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName]);
        this._setTestId(display, propertyName);
        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.gis.adminuser,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.adminuser'
            },
            items: [input, display]
        };

        return fieldcontainer;
    },

    getGeoServerAdminPasswordInput: function (config) {
        var propertyName = 'geoserveradminpassword';
        var input = this._getTextfield(config[propertyName], propertyName, true);
        var display = this._getDisplayfield(config[propertyName]);
        this._setTestId(display, propertyName);
        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            reference: 'geoserveradminpassword', // TODO remove all reference occurence
            fieldLabel: CMDBuildUI.locales.Locales.administration.gis.adminpassword,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.adminpassword'
            },
            items: [input, display]
        };

        return fieldcontainer;
    },

    getParentProject: function (config) {
        var propertyName = 'parentId';
        var input = this._getCombofield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName], {
            displayField: 'name',
            valueField: '_id'
        });
        this._setTestId(display, propertyName);
        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.bim.parentproject,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.bim.parentproject'
            },
            items: [input, display]
        };

        return fieldcontainer;
    },

    /**
     *
     * @param {Object} config
     * @returns
     */
    getInitialExpansionDepthInput: function (config) {
        const display = this._setTestId(this._getDisplayfield(config), 'initialExpansionDepth');
        const input = this._getNumberfield(config, 'initialExpansionDepth');

        return {
            xtype: 'fieldcontainer',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.dependencytrees.initialexpansiondepth,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.dependencytrees.initialexpansiondepth'
            },
            items: [display, input]
        };
    },

    privates: {
        /**
         *
         * @param {*} mode
         * @param {*} input
         * @param {*} display
         */
        getFieldsByMode: function (mode, input, display) {
            var items;
            if (!mode) {
                mode = 'both';
            }
            switch (mode) {
                case 'display':
                    items = [display];
                    break;
                case 'edit':
                    items = [input];
                    break;
                default:
                    items = [input, display];
                    break;
            }
            return items;
        },
        /**
         *
         * @param {*} config
         * @param {*} storeKeys
         */
        _getDisplayfield: function (config, storeKeys) {
            var displayfield = Ext.merge(
                {},
                {
                    columnWidth: 1,
                    // hidden: true,
                    minHeight: 40,
                    xtype: 'displayfield',
                    bind: {
                        hidden: '{!actions.view}'
                    }
                },
                config,
                config.displayfield || {}
            );

            var bindedStore = displayfield.store || displayfield.bind.store;
            if (displayfield.store || (displayfield.bind && displayfield.bind.store)) {
                displayfield.renderer = function (value, input) {
                    var store;

                    if (typeof bindedStore === 'object') {
                        store = bindedStore;
                    } else if (typeof bindedStore === 'string') {
                        var vm = input.lookupViewModel();
                        store =
                            vm.getStore(bindedStore.slice(1, -1)) ||
                            vm.get(bindedStore.slice(1, -1)) ||
                            Ext.getStore(bindedStore);
                        if (!store) {
                            vm.bind(
                                {
                                    bindTo: bindedStore,
                                    single: true
                                },
                                function (store) {
                                    var record = store.findRecord(storeKeys.valueField || 'value', value);
                                    if (record) {
                                        input.setValue(record.get(storeKeys.displayField || 'label'));
                                    }
                                }
                            );
                        }
                    }

                    if (storeKeys && store && value) {
                        var record = store.findRecord(storeKeys.valueField, value);
                        if (record) {
                            return record.get(storeKeys.displayField);
                        }
                    }
                    return value;
                };

                delete displayfield.bind.store;
            }

            if (displayfield.unitOfMeasure) {
                displayfield.renderer = function (value, input) {
                    if (value && value.length && !Ext.isEmpty(displayfield.unitOfMeasure)) {
                        var format;
                        if (config.unitOfMeasureLocation === CMDBuildUI.model.Attribute.unitOfMeasureLocations.before) {
                            format = '{1} {0}';
                        } else {
                            format = '{0} {1}';
                        }
                        value = Ext.String.format(format, value, displayfield.unitOfMeasure);
                    }
                    return value;
                };
            }
            return displayfield;
        },
        /**
         *
         * @param {*} config
         * @param {*} name
         * @param {*} password
         * @param {*} disabledOnEdit
         * @param {*} copyToInput
         */
        _getTextfield: function (config, name, password, disabledOnEdit, copyToInput) {
            var inputtype = password ? 'password' : 'text';
            if ((name === 'name' || name === 'code') && !config.vtype && !config.ignoreVtype) {
                config.vtype = 'nameInputValidation';
            }
            this._setTestId(config, name);
            var textfield = Ext.merge(
                {},
                {
                    columnWidth: 1,
                    xtype: password ? 'passwordfield' : 'textfield',
                    itemId: Ext.String.format('{0}_input', name),
                    name: password ? 'cmdbuild_' + name : name,
                    inputType: inputtype,
                    hidden: true,
                    bind: {
                        hidden: '{actions.view}'
                    },
                    listeners: {}
                },
                config,
                config.textfield || {}
            );

            if (disabledOnEdit) {
                textfield = this._setDisabledOnEdit(textfield);
            }
            if (copyToInput) {
                this._afterRenderCopyToInput(textfield, copyToInput);
            }
            return textfield;
        },
        /**
         *
         * @param {*} config
         * @param {*} name
         * @param {*} disabledOnEdit
         */
        _getTextarea: function (config, name, disabledOnEdit) {
            this._setTestId(config, name);
            if (config && config.resizable) {
                delete config.resizable;
            }
            var textarea = Ext.merge(
                {},
                {
                    columnWidth: 1,
                    xtype: 'textarea',
                    itemId: Ext.String.format('{0}_input', name),
                    name: name,
                    bind: {
                        readOnly: '{actions.view}'
                    },
                    height: '100%',
                    listeners: {}
                },
                config
            );

            if (disabledOnEdit) {
                textarea = this._setDisabledOnEdit(textarea);
            }

            return textarea;
        },
        /**
         *
         * @param {*} input
         * @param {*} copyToInput
         */
        _afterRenderCopyToInput: function (input, copyToInput) {
            var me = this;
            input.listeners.afterrender = function (_input) {
                me._copyToInputOnChange(_input, copyToInput);
            };
        },
        /**
         *
         * @param {*} input
         * @param {*} copyToInput
         */
        _copyToInputOnChange: function (input, copyToInput) {
            var me = this;
            input.on('change', function (_input, newVal, oldVal) {
                me.copyTo(_input, newVal, oldVal, copyToInput);
            });
        },
        /**
         *
         * @param {*} attributeName
         * @param {*} attribute
         * @param {*} config
         * @param {*} mergeOption
         */
        _getReferenceField: function (attributeName, attribute, config, mergeOption) {
            var editor = CMDBuildUI.util.helper.FormHelper.getEditorForField(attribute, config);

            var input = Ext.apply(mergeOption, editor);
            return input;
        },
        /**
         *
         * @param {*} attributeName
         * @param {*} attribute
         * @param {*} config
         * @param {*} mergeOption
         */
        _getDisplayReference: function (attributeName, attribute, config, mergeOption) {
            var editor = CMDBuildUI.util.helper.FormHelper.getReadOnlyField(attribute, config.linkName);
            return Ext.merge({}, mergeOption, editor);
        },

        /**
         *
         * @param {*} config
         * @param {*} name
         * @param {*} disabledOnEdit
         * @param {*} multiSelect
         */
        _getCombofield: function (config, name, disabledOnEdit, multiSelect) {
            this._setTestId(config, name);
            var combo = Ext.merge(
                {},
                {
                    columnWidth: 1,
                    xtype: multiSelect ? 'tagfield' : 'combobox',
                    clearFilterOnBlur: true,
                    itemId: Ext.String.format('{0}_input', name),
                    queryMode: 'local',
                    displayField: 'label',
                    valueField: 'value',
                    name: name,
                    hidden: true,
                    bind: {
                        hidden: '{actions.view}'
                    }
                },
                config,
                config.combofield || {}
            );
            if (disabledOnEdit) {
                combo = this._setDisabledOnEdit(combo);
            }
            return combo;
        },

        /**
         *
         * @param {*} config
         * @param {*} name
         * @param {*} disabledOnEdit
         */
        _getGroupedCombofield: function (config, name, disabledOnEdit) {
            this._setTestId(config, name);
            var combo = Ext.merge(
                {},
                {
                    columnWidth: 1,
                    xtype: 'groupedcombo',
                    clearFilterOnBlur: true,
                    itemId: Ext.String.format('{0}_input', name),
                    queryMode: 'local',
                    displayField: 'label',
                    valueField: 'value',
                    // reference: name,
                    name: name,
                    hidden: true,
                    bind: {
                        hidden: '{actions.view}'
                    }
                },
                config,
                config.combofield || {}
            );
            if (disabledOnEdit) {
                combo = this._setDisabledOnEdit(combo);
            }
            return combo;
        },
        /**
         *
         * @param {*} config
         * @param {*} name
         * @param {*} disabledOnEdit
         */
        _getNumberfield: function (config, name, disabledOnEdit) {
            this._setTestId(config, name);
            var numberfield = Ext.merge(
                {},
                {
                    xtype: 'numberfield',
                    minValue: 0,
                    itemId: Ext.String.format('{0}_input', name),
                    step: 1,
                    decimalPrecision: 0,
                    name: name,
                    hidden: true,
                    bind: {
                        hidden: '{actions.view}'
                    }
                },
                config
            );

            if (disabledOnEdit) {
                numberfield = this._setDisabledOnEdit(numberfield);
            }
            if (numberfield.unitOfMeasure) {
                Ext.merge(numberfield, {
                    hideTrigger: false,
                    triggers: {
                        unitOfMesureTrigger: {
                            cls: 'unitOfMesure',
                            hidden: false,
                            hideOnReadOnly: false,
                            readOnly: false,
                            defaultPass: true,
                            handler: function () {
                                return;
                            }
                        },
                        spinner: {
                            hideOnReadOnly: true,
                            readOnly: true
                        }
                    },
                    listeners: {
                        afterRender: function (view, eOpts) {
                            var trigger = view.getTrigger('unitOfMesureTrigger');
                            if (trigger) {
                                var triggerEl = trigger.getEl().dom;
                                triggerEl.setAttribute('unitOfMesure', 'MB');
                            }
                        }
                    }
                });
            }
            return numberfield;
        },
        /**
         *
         * @param {Object} config
         * @param {String} name
         */
        _setTestId: function (config, name) {
            if (!config.autoEl) {
                config.autoEl = {};
            }
            if (!config.autoEl['data-testid'] || config.xtype === 'displayfield') {
                config.autoEl['data-testid'] = Ext.String.format(
                    'administration-{0}-{1}',
                    name,
                    config.xtype === 'displayfield' ? 'display' : 'input'
                );
            }
            return config;
        },
        /**
         *
         * @param {*} input
         */
        _setDisabledOnEdit: function (input) {
            input.listeners = input.listeners || {};
            if (!input.listeners.beforerender) {
                input.listeners.beforerender = function (_input) {
                    const inputVm = _input.lookupViewModel();
                    const isAddOrClone = inputVm.get('actions.add') || inputVm.get('actions.clone');
                    if (!isAddOrClone) {
                        _input.vtype = undefined;
                    }
                    _input.setDisabled(!isAddOrClone);
                };
            }
            return input;
        },
        /**
         *
         * @param {*} config
         * @param {*} name
         * @param {*} readOnlyOnView
         */
        _getCheckboxfield: function (config, name, readOnlyOnView) {
            this._setTestId(config, name);
            var checkbox = Ext.merge(
                {},
                {
                    xtype: 'checkbox',
                    name: name,
                    dataEl: {
                        'data-testid': Ext.String.format('administrtion_{0}_input', name)
                    },
                    itemId: Ext.String.format('{0}_input', name),
                    bind: {
                        disabled: '{actions.view}'
                    }
                },
                config
            );

            return checkbox;
        }
    },
    /**
     *
     * @param {*} config
     * @param {*} name
     * @param {*} disabledOnEdit
     * @param {*} mode
     */
    getAllClassesProcessesViewsInput: function (config, name, disabledOnEdit, mode) {
        var propertyName = name || 'class';
        var fieldcontainer = Ext.merge(
            {},
            {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                items: []
            },
            config[propertyName].fieldcontainer || {}
        );

        delete config[propertyName].fieldLabel;

        var input = Ext.merge(
            {},
            {
                columnWidth: 1,
                xtype: 'allelementscombo',
                name: name,
                bind: {
                    hidden: '{actions.view}'
                },
                itemId: Ext.String.format('{0}_input', name),
                hidden: true
            },
            config[propertyName],
            config[propertyName].combofield
        );
        delete config[propertyName].combofield;

        var hideTrigger, valueField;
        if (config[propertyName].bind && config[propertyName].bind.value) {
            valueField = config[propertyName].bind.value.replace('{', '').replace('}', '');
        } else if (
            !valueField &&
            config[propertyName].displayfield &&
            config[propertyName].displayfield.bind &&
            config[propertyName].displayfield.bind.value
        ) {
            valueField = config[propertyName].displayfield.bind.value.replace('{', '').replace('}', '');
        }
        hideTrigger = Ext.String.format(
            '{!{0} || {0} == "{1}" || {0} == "{2}"}',
            valueField,
            CMDBuildUI.util.helper.ModelHelper.systemClasses.class,
            CMDBuildUI.util.helper.ModelHelper.systemClasses.process
        );
        var displayfieldConfig = Ext.merge({}, config[propertyName], {
            displayfield: {
                xtype: 'displayfieldwithtriggers',
                bind: {
                    hideTrigger: hideTrigger
                },
                triggers: {
                    open: {
                        cls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                        handler: function (f, trigger, eOpts) {
                            var url,
                                value = f.up().down('allelementscombo').getValue(),
                                targetType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(value),
                                target = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(value);
                            switch (targetType) {
                                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                                    url = CMDBuildUI.util.administration.helper.ApiHelper.client.getClassUrl(
                                        target.get('name')
                                    );
                                    break;
                                case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                                    url = CMDBuildUI.util.administration.helper.ApiHelper.client.getProcessUrl(
                                        target.get('name')
                                    );
                                    break;
                                case CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel:
                                    url = CMDBuildUI.util.administration.helper.ApiHelper.client.getDmsModelUrl(
                                        target.get('name')
                                    );
                                    break;
                                default:
                                    return;
                            }
                            CMDBuildUI.util.Utilities.closeAllPopups();
                            CMDBuildUI.util.Utilities.redirectTo(url);
                        }
                    }
                },
                renderer: function (value) {
                    if (!Ext.isEmpty(value)) {
                        var object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(value);
                        if (object) {
                            return object.get('description');
                        }
                    }
                    return value;
                }
            }
        });
        var display = this._getDisplayfield(displayfieldConfig, {
            displayField: 'label',
            valueField: '_id'
        });
        this._setTestId(display, name);
        fieldcontainer.items = this.getFieldsByMode(mode, input, display);
        return fieldcontainer;
    },

    /**
     *
     * @param {*} propertyName
     * @param {*} config
     * @param {*} disabledOnEdit
     * @param {*} mode
     */
    getNoteInput: function (propertyName, config, disabledOnEdit, mode) {
        var fieldcontainer = Ext.merge(
            {},
            {
                xtype: 'fieldcontainer',
                layout: 'column',
                itemId: Ext.String.format('{0}_fieldcontainer', propertyName),
                columnWidth: config[propertyName].columnWidth || 0.5,
                fieldLabel: config[propertyName].fieldLabel,
                allowBlank: config[propertyName].allowBlank,
                resizable: {
                    handles: 's'
                },
                minHeight: 125,
                items: []
            },
            config[propertyName].fieldcontainer || {}
        );
        delete config[propertyName].fieldLabel;
        delete config[propertyName].fieldcontainer;
        if (config[propertyName].localized) {
            delete config[propertyName].localized.fieldLabel;
        }

        var textarea = Ext.merge(
            {},
            {
                columnWidth: 1,
                xtype: 'textarea',
                itemId: Ext.String.format('{0}_input', propertyName),
                name: name,
                bind: {
                    hidden: '{actions.view}'
                },
                resizable: {
                    handles: 's'
                },
                height: '100%',
                listeners: {}
            },
            config[propertyName] || {}
        );
        var display = Ext.merge(
            {},
            {
                columnWidth: 1,
                minHeight: 40,
                xtype: 'displayfield',
                bind: {
                    hidden: '{!actions.view}'
                }
            },
            config[propertyName] || {}
        );
        fieldcontainer.items = this.getFieldsByMode(mode, textarea, display);
        // fieldcontainer.items.push(textarea);
        // fieldcontainer.items.push(display);
        return fieldcontainer;
    },
    /**
     *
     * @param {*} input
     * @param {*} newVal
     * @param {*} oldVal
     * @param {*} copyToInput
     */
    copyTo: function (input, newVal, oldVal, copyToInput) {
        if (input.lookupViewModel().get('actions.add')) {
            var copyTo = input.up('form').down(copyToInput);
            if (copyTo && oldVal === copyTo.getValue()) {
                copyTo.setValue(newVal);
            }
        }
    },

    getDeafultGroupInput: function (config) {
        var propertName = 'defaultGroup';
        var combo = this._getCombofield(
            Ext.merge(
                {},
                {
                    columnWidth: 1,
                    xtype: 'combobox',
                    clearFilterOnBlur: true,
                    queryMode: 'local',
                    displayField: 'description',
                    valueField: '_id',
                    name: propertName,
                    bind: {
                        hidden: '{actions.view}'
                    }
                },
                config[propertName]
            ),
            propertName
        );

        return Ext.merge(
            {},
            {
                xtype: 'fieldcontainer',
                columnWidth: 0.5,
                layout: 'column',
                fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaultgroup,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaultgroup'
                },
                items: [combo]
            },
            config[propertName].fieldcontainer || {}
        );
    },

    getDisplayField: function (propertyName, config) {
        return this._setTestId(this._getDisplayfield(config[propertyName], propertyName), propertyName);
    },

    getValidationRule: function (options) {
        if (!options || !options.vmObjectName || !options.inputField) {
            CMDBuildUI.util.Logger.log(
                'validationRule required input configs are missing',
                CMDBuildUI.util.Logger.levels.error
            );
        }
        var bindingValue = Ext.String.format('{{0}.{1}}', options.vmObjectName, options.inputField);
        return {
            xtype: 'aceeditortextarea',
            allowBlank: true,
            vmObjectName: options.vmObjectName,
            inputField: options.inputField,
            options: {
                readOnly: true
            },
            bind: {
                value: bindingValue,
                readOnly: '{actions.view}',
                config: {
                    options: {
                        readOnly: '{actions.view}'
                    }
                }
            },
            itemId: options.inputField,
            fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.validationrule,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.validationrule'
            },
            minHeight: '85px',
            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('expand', 'solid'),
            labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.expand,
            labelToolIconClick: 'onAceEditorValidationExpand',
            listeners: {
                render: function (element) {
                    var aceEditor = element.getAceEditor();
                    var vm = element.lookupViewModel();
                    vm.bind(
                        {
                            bindTo: {
                                validationRule: bindingValue
                            },
                            single: true
                        },
                        function (data) {
                            if (data.validationRule) {
                                aceEditor.setValue(data.validationRule, -1);
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
                }
            },
            name: 'validationRule',
            width: '95%'
        };
    },

    getCardAutoValue: function (options) {
        if (!options || !options.vmObjectName || !options.inputField) {
            CMDBuildUI.util.Logger.log(
                'autoValue required input configs are missing',
                CMDBuildUI.util.Logger.levels.error
            );
        }
        var bindingValue = Ext.String.format('{{0}.{1}}', options.vmObjectName, options.inputField);
        return {
            xtype: 'aceeditortextarea',
            allowBlank: true,
            vmObjectName: options.vmObjectName,
            inputField: options.inputField,
            options: {
                readOnly: true
            },
            bind: {
                value: bindingValue,
                readOnly: '{actions.view}',
                config: {
                    options: {
                        readOnly: '{actions.view}'
                    }
                }
            },
            itemId: options.inputField,
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.autovalue,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.autovalue'
            },
            minHeight: '85px',
            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('expand', 'solid'),
            labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.expand,
            labelToolIconClick: 'onAceEditorAutoValueExpand',
            listeners: {
                render: function (element) {
                    var aceEditor = element.getAceEditor();
                    var vm = element.lookupViewModel();
                    vm.bind(
                        {
                            bindTo: {
                                autoValue: bindingValue
                            },
                            single: true
                        },
                        function (data) {
                            if (data.autoValue) {
                                aceEditor.setValue(data.autoValue, -1);
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
                }
            },
            name: 'autoValue',
            width: '95%'
        };
    },

    getCommonCheckboxInput: function (name, config, disabledOnEdit) {
        var propertyName = name || 'unnamed';
        var fieldcontainer = Ext.merge(
            {},
            {
                xtype: 'fieldcontainer',
                layout: 'column',
                itemId: Ext.String.format('{0}_fieldcontainer', propertyName),
                columnWidth: config[propertyName].columnWidth || 0.5,
                fieldLabel: config[propertyName].fieldLabel,
                items: []
            },
            config[propertyName].fieldcontainer || {}
        );
        delete config[propertyName].fieldLabel;
        delete config[propertyName].fieldcontainer;
        if (config[propertyName].localized) {
            delete config[propertyName].localized.fieldLabel;
        }
        fieldcontainer.items.push(this._getCheckboxfield(config[propertyName], propertyName));
        return fieldcontainer;
    },

    getCommonTextfieldInput: function (propertyName, config, password) {
        var fieldcontainer = Ext.merge(
            {},
            {
                xtype: 'fieldcontainer',
                layout: 'column',
                itemId: Ext.String.format('{0}_fieldcontainer', propertyName),
                columnWidth: config[propertyName].columnWidth || 0.5,
                fieldLabel: config[propertyName].fieldLabel,
                allowBlank: config[propertyName].allowBlank,
                items: []
            },
            config[propertyName].fieldcontainer || {}
        );
        delete config[propertyName].fieldLabel;
        delete config[propertyName].fieldcontainer;
        if (config[propertyName].localized) {
            delete config[propertyName].localized.fieldLabel;
        }
        if (!config[propertyName].noInputField) {
            fieldcontainer.items.push(this._getTextfield(config[propertyName], propertyName, password));
        }
        if (config[propertyName].noDisplayField === true) {
            return fieldcontainer;
        }
        fieldcontainer.items.push(this._setTestId(this._getDisplayfield(config[propertyName]), propertyName));

        return fieldcontainer;
    },

    getCommonChekboxInput: function (config, name, disabledOnEdit) {
        var propertyName = name || 'check';
        var input = this._getCheckboxfield(config[propertyName], propertyName);

        var fieldcontainer = Ext.merge(
            {},
            {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                itemId: Ext.String.format('{0}_fieldcontainer', propertyName),
                fieldLabel: config[propertyName].fieldLabel,
                items: [input]
            },
            config[propertyName].fieldcontainer ? config[propertyName].fieldcontainer : {}
        );

        return fieldcontainer;
    },

    getCommonTextareaInput: function (propertyName, config, disabledOnEdit) {
        var fieldcontainer = Ext.merge(
            {},
            {
                xtype: 'fieldcontainer',
                layout: 'column',
                itemId: Ext.String.format('{0}_fieldcontainer', propertyName),
                columnWidth: config[propertyName].columnWidth || 0.5,
                fieldLabel: config[propertyName].fieldLabel,
                localized: config[propertyName].localized,
                allowBlank: config[propertyName].allowBlank,
                resizable: {
                    handles: 's'
                },
                items: []
            },
            config[propertyName].fieldcontainer || {}
        );
        delete config[propertyName].fieldLabel;
        delete config[propertyName].fieldcontainer;
        if (config[propertyName].localized) {
            delete config[propertyName].localized.fieldLabel;
        }
        var textarea = this._getTextarea(config[propertyName], propertyName, disabledOnEdit);

        fieldcontainer.items.push(textarea);
        return fieldcontainer;
    },

    getCommonNumberfieldInput: function (propertyName, config) {
        var fieldcontainer = Ext.merge(
            {},
            {
                xtype: 'fieldcontainer',
                layout: 'column',
                itemId: Ext.String.format('{0}_fieldcontainer', propertyName),
                columnWidth: config[propertyName].columnWidth || 0.5,
                fieldLabel: config[propertyName].fieldLabel,
                allowBlank: config[propertyName].allowBlank,
                items: []
            },
            config[propertyName].fieldcontainer || {}
        );
        delete config[propertyName].fieldLabel;
        delete config[propertyName].fieldcontainer;
        if (config[propertyName].localized) {
            delete config[propertyName].localized.fieldLabel;
        }
        fieldcontainer.items.push(this._getNumberfield(config[propertyName], propertyName));
        fieldcontainer.items.push(this._setTestId(this._getDisplayfield(config[propertyName]), propertyName));

        return fieldcontainer;
    },

    /**
     *
     * @param {String} propertyName
     * @param {Object} config
     * @param {Boolean} disabledOnEdit
     * @param {Boolean} onlyCombo if true it return only the combobox otherwise combo+displayfield
     * @param {Boolean} multiSelect if true it use tag field otherwise combobox field
     *
     * @returns {Ext.form.FieldContainer}
     */
    getCommonComboInput: function (propertyName, config, disabledOnEdit, onlyCombo, multiSelect) {
        var fieldcontainer = Ext.merge(
            {},
            {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: config[propertyName].columnWidth || 0.5,
                fieldLabel: config[propertyName].fieldLabel,
                items: [],
                autoEl: {
                    'data-testid': config[propertyName].testid || Ext.String.format('{0}_fieldcontainer', propertyName)
                },
                itemId: Ext.String.format('{0}_fieldcontainer', propertyName),
                allowBlank: config[propertyName].allowBlank
            },
            config[propertyName].fieldcontainer || {}
        );

        var displayConfig = config[propertyName].displayfield || config[propertyName];
        delete config[propertyName].fieldcontainer;
        delete config[propertyName].fieldLabel;
        delete config[propertyName].disabled;
        if (config[propertyName].localized) {
            delete config[propertyName].localized.fieldLabel;
        }
        delete config[propertyName].displayfield;
        fieldcontainer.items.push(this._getCombofield(config[propertyName], propertyName, disabledOnEdit, multiSelect));
        if (!onlyCombo) {
            if (config[propertyName].bind && config[propertyName].bind.disabled) {
                delete config[propertyName].bind.disabled;
            }
            fieldcontainer.items.push(
                this._setTestId(
                    this._getDisplayfield(displayConfig, {
                        displayField: config[propertyName].displayField || 'label',
                        valueField: config[propertyName].valueField || 'value'
                    }),
                    propertyName
                )
            );
        }

        return fieldcontainer;
    },

    /**
     *
     * @param {String} propertyName
     * @param {Object} config
     * @param {Boolean} disabledOnEdit
     * @param {Boolean} onlyCombo if true it return only the combobox otherwise combo+displayfield
     *
     * @returns {Ext.form.FieldContainer}
     */
    getCommonGroupedComboInput: function (propertyName, config, disabledOnEdit, onlyCombo) {
        var fieldcontainer = Ext.merge(
            {},
            {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: config[propertyName].columnWidth || 0.5,
                fieldLabel: config[propertyName].fieldLabel,
                items: [],
                itemId: Ext.String.format('{0}_fieldcontainer', propertyName),
                allowBlank: config[propertyName].allowBlank
            },
            config[propertyName].fieldcontainer || {}
        );

        var displayConfig = config[propertyName].displayfield || config[propertyName];
        delete config[propertyName].fieldcontainer;
        delete config[propertyName].fieldLabel;
        delete config[propertyName].disabled;
        if (config[propertyName].localized) {
            delete config[propertyName].localized.fieldLabel;
        }
        delete config[propertyName].displayfield;
        fieldcontainer.items.push(this._getGroupedCombofield(config[propertyName], propertyName, disabledOnEdit));
        if (!onlyCombo) {
            if (config[propertyName].bind && config[propertyName].bind.disabled) {
                delete config[propertyName].bind.disabled;
            }
            fieldcontainer.items.push(
                this._setTestId(
                    this._getDisplayfield(displayConfig, {
                        displayField: config[propertyName].displayField || 'label',
                        valueField: config[propertyName].valueField || 'value'
                    }),
                    propertyName
                )
            );
        }

        return fieldcontainer;
    },

    /**
     *
     * @param {Ext.form.field.Base} field
     * @param {Boolean} value
     * @param {Ext.form.Panel} form
     */
    setAllowBlank: function (field, value, form) {
        var fieldcontainer = field.up('fieldcontainer');
        field.allowBlank = value;
        if (fieldcontainer) {
            fieldcontainer.allowBlank = value;
        }

        if (value) {
            try {
                field.clearInvalid();
            } catch (error) {}
            if (fieldcontainer && fieldcontainer.labelEl) {
                fieldcontainer.labelEl.dom.innerHTML = fieldcontainer.labelEl.dom.innerHTML.replace(
                    '<span class="required-field-placeholder"> *</span>',
                    '<span class="required-field-placeholder"></span>'
                );
            }
        } else {
            if (fieldcontainer && fieldcontainer.labelEl) {
                fieldcontainer.labelEl.dom.innerHTML = fieldcontainer.labelEl.dom.innerHTML.replace(
                    '<span class="required-field-placeholder"></span>',
                    '<span class="required-field-placeholder"> *</span>'
                );
            }
        }
        if (form && form.form) {
            form.form.checkValidity();
        }
    },
    getFaIconInput: function (viewModelKey, config) {
        config = config || {};
        const allowBlank = Ext.isDefined(config.allowBlank) ? config.allowBlank : true;

        return {
            xtype: 'fieldcontainer',
            itemId: 'faIconInput',
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.menuicon,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.menuicon'
            },
            layout: 'column',
            items: [
                {
                    columnWidth: 1,
                    xtype: 'cmdbuild-fapicker',
                    itemId: 'classIconFont',
                    allowBlank: allowBlank,
                    msgTarget: 'qtip',
                    viewModelKey: viewModelKey,
                    bind: {
                        hidden: '{actions.view}',
                        value: '{' + viewModelKey + '.icon_description}',
                        disabled: '{actions.view}'
                    },
                    triggers: {
                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger(
                            function (combo) {}
                        )
                    }
                },
                {
                    xtype: 'image',
                    autoEl: 'div',
                    alt: CMDBuildUI.locales.Locales.administration.common.labels.iconpreview,
                    localized: {
                        alt: 'CMDBuildUI.locales.Locales.administration.common.labels.iconpreview'
                    },
                    cls: 'fa-2x',
                    minWidth: 32,
                    minHeight: 32,
                    maxWidth: 32,
                    maxHeight: 32,
                    height: 32,
                    width: 32,
                    margin: '0 15 0 0',
                    style: {
                        lineHeight: '32px'
                    },
                    bind: {
                        userCls: '{' + viewModelKey + '.icon_font}'
                    }
                }
            ]
        };
    }
});
