Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.FieldsHelper', {
    singleton: true,

    /**
     *
     * @param {String} action
     * @returns {Object}
     */
    getPointInputs: function (action) {
        const config = Ext.merge({}, this.fieldsConfig, {
            fillColor: {
                input: {},
                preview: {}
            },
            fillOpacity: {},
            pointRadius: {},
            pointColor: {
                input: {},
                preview: {}
            },
            icon: {
                input: {},
                preview: {}
            },
            strokeDashstyle: {},
            strokeColor: {},
            strokeOpacity: {},
            strokeWidth: {},
            clusterEnable: {},
            clusterDistance: {}
        });

        const container = {
            xtype: 'container',
            bind: {
                hidden: '{!actions.' + action + '}'
            },
            items: Ext.Array.merge(
                [this.getFillFields(config), this.getPointRadiusAndIconFields(config)],
                this.getStrokeFields(config),
                this.getClusterEnableAndDistanceFields(config)
            )
        };
        return container;
    },

    /**
     *
     * @returns {Object}
     */
    getLineInputs: function () {
        const config = Ext.merge({}, this.fieldsConfig, {
            strokeDashstyle: {},
            strokeColor: {},
            strokeOpacity: {},
            strokeWidth: {}
        });
        return {
            xtype: 'container',
            items: this.getStrokeFields(config)
        };
    },

    /**
     *
     * @returns {Object}
     */
    getPolygonInputs: function () {
        const config = Ext.merge({}, this.fieldsConfig, {
            fillType: {},
            fillPattern: {},
            fillColor: {},
            fillOpacity: {},
            strokeDashstyle: {},
            strokeColor: {},
            strokeOpacity: {},
            strokeWidth: {}
        });
        const fillTypeAndPatternFields = this.getFillTypeAndPatternFields(config);
        const fillFields = this.getFillFields(config);
        const strokeFields = this.getStrokeFields(config);

        const fields = Ext.Array.merge([fillTypeAndPatternFields, fillFields], strokeFields);
        return {
            xtype: 'container',
            items: fields
        };
    },

    privates: {
        fieldsConfig: {
            fillType: {
                bind: {
                    value: '{theGeoAttribute.style.fillType}',
                    store: '{fillTypeStore}'
                }
            },
            fillTypeParent: {
                bind: {
                    hidden: '{theGeoAttribute.style.fillType === "solid"}'
                }
            },
            fillPattern: {
                bind: {
                    value: '{theGeoAttribute.style.fillPattern}'
                }
            },
            fillColor: {
                bind: {
                    value: '{theGeoAttribute.style.fillColor}'
                }
            },
            fillOpacity: {
                bind: {
                    value: '{theGeoAttribute.style.fillOpacity}'
                }
            },
            pointRadius: {
                bind: {
                    value: '{theGeoAttribute.style.pointRadius}'
                }
            },
            pointColor: {},
            icon: {
                input: {
                    bind: {
                        value: '{theGeoAttribute._icon}'
                    },
                    tpl: [
                        '<tpl for=".">',
                        '<div class="x-boundlist-item">',
                        '{iconelement} &nbsp;{description}',
                        '</div>',
                        '</tpl>'
                    ],
                    listeners: {
                        change: function (combo, newValue, oldValue) {
                            let iconpath;
                            if (newValue) {
                                iconpath = Ext.String.format(
                                    '{0}/uploads/{1}/download',
                                    CMDBuildUI.util.Config.baseUrl,
                                    newValue
                                );
                            } else {
                                iconpath =
                                    'data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==';
                            }
                            this.up().down('image').setSrc(iconpath);
                        }
                    },
                    triggers: {
                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                    }
                },
                preview: {
                    bind: {
                        src: '{theGeoAttribute._iconPath}',
                        alt: '{theGeoAttribute.description}'
                    },
                    itemId: 'geoAttributeIconPreview',
                    viewModel: {
                        data: {
                            theGeoAttribute: null,
                            vmKey: 'theGeoAttribute'
                        }
                    }
                }
            },
            strokeDashstyle: {
                bind: {
                    value: '{theGeoAttribute.style.strokeDashstyle}',
                    store: '{strokeDashStyleStore}'
                }
            },
            strokeColor: {
                preview: {
                    bind: {
                        style: {
                            color: '{theGeoAttribute.style.strokeColor}'
                        }
                    }
                },
                input: {
                    bind: {
                        value: '{theGeoAttribute.style.strokeColor}'
                    }
                }
            },
            strokeOpacity: {
                bind: {
                    value: '{theGeoAttribute.style.strokeOpacity}'
                }
            },
            strokeWidth: {
                bind: {
                    value: '{theGeoAttribute.style.strokeWidth}'
                }
            },
            clusterEnable: {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.clusterenable,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.clusterenable'
                    }
                },
                bind: {
                    value: '{theGeoAttribute.style.clusterEnable}'
                }
            },
            clusterDistance: {
                bind: {
                    value: '{theGeoAttribute.style.clusterDistance}'
                }
            }
        },

        /**
         *
         * @param {String} config
         * @returns {Object}
         */
        getFillTypeAndPatternFields: function (config) {
            return {
                layout: 'column',
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getFillTypeInput(config),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getFillPatternInput(config)
                ]
            };
        },

        /**
         *
         * @param {String} config
         * @returns {Object}
         */
        getFillFields: function (config) {
            return {
                layout: 'column',
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getFillOpacityInput(config),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getFillColorInput(config)
                ]
            };
        },

        /**
         *
         * @param {String} config
         * @returns {Object}
         */
        getStrokeFields: function (config) {
            return [this.getStrokeStyleAndColorFields(config), this.getStrokeOpacityAndWidthFields(config)];
        },

        /**
         *
         * @param {String} config
         * @returns {Object}
         */
        getStrokeOpacityAndWidthFields: function (config) {
            return {
                layout: 'column',
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getStrokeOpacityInput(config),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getStrokeWidthInput(config)
                ]
            };
        },

        /**
         *
         * @param {String} config
         * @returns {Object}
         */
        getClusterEnableAndDistanceFields: function (config) {
            return {
                layout: 'column',
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput('clusterEnable', config),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getClusterDistanceInput(config)
                ]
            };
        },

        /**
         *
         * @param {String} config
         * @returns {Object}
         */
        getStrokeStyleAndColorFields: function (config) {
            return {
                layout: 'column',
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getStrokeDashStyleInput(config),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getStrokeColorInput(config)
                ]
            };
        },

        /**
         *
         * @param {String} config
         * @returns {Object}
         */
        getPointRadiusAndIconFields: function (config) {
            return {
                layout: 'column',
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getPointRadiusInput(config),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getIconComboInput(config)
                ]
            };
        },

        /**
         *
         * @returns {Object}
         */
        getTypeField: function () {
            return {
                layout: 'column',
                columnWidth: 0.5,
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                        'type',
                        {
                            type: {
                                columnWidth: 1,
                                fieldcontainer: {
                                    allowBlank: false,
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.type
                                },
                                bind: {
                                    store: '{typesStore}',
                                    value: '{theGeoAttribute.type}'
                                }
                            }
                        },
                        true
                    )
                ]
            };
        }
    }
});
