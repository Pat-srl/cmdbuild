Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.FieldsHelper', {
    singleton: true,

    requires: ['CMDBuildUI.util.helper.FieldsHelper', 'CMDBuildUI.util.administration.helper.FieldsHelper'],

    getGeneralFields: function () {
        return [
            {
                layout: 'column',
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCodeInput(
                        {
                            code: {
                                vtype: 'lookupnameInputValidation',
                                allowBlank: false,
                                bind: {
                                    value: '{theValue.code}'
                                }
                            }
                        },
                        true, // disabledOnEdit
                        '[name="description"]' // copyToInput
                    ),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                        description: {
                            bind: {
                                value: '{theValue.description}',
                                disabled: '{_is_system}'
                            },
                            allowBlank: false,
                            fieldcontainer: {
                                userCls: 'with-tool',
                                labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                                labelToolIconQtip:
                                    CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                                labelToolIconClick: 'onTranslateClick'
                            }
                        }
                    })
                ]
            },
            {
                layout: 'column',
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                        'parentDescription',
                        {
                            parentDescription: {
                                fieldcontainer: {
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.lookuptypes.strings.parentdescription,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.lookuptypes.strings.parentdescription'
                                    },
                                    bind: {
                                        disabled: '{isTargetTypeDisabled || theLookupType._is_system}'
                                    }
                                },
                                displayField: 'description',
                                valueField: '_id',
                                allowBlank: true,
                                combofield: {
                                    bind: {
                                        value: '{theValue.parent_id}',
                                        disabled: '{theLookupType._is_system}',
                                        store: '{parentLookupsStore}'
                                    }
                                },
                                displayfield: {
                                    bind: {
                                        value: '{theValue._parent_id_description}',
                                        disabled: '{theLookupType._is_system}'
                                    },
                                    renderer: function (value) {
                                        if (!value) {
                                            const vm = this.lookupViewModel();
                                            vm.bind(
                                                {
                                                    bindTo: {
                                                        value: '{theValue.parent_id}',
                                                        store: '{parentLookupsStore}'
                                                    }
                                                },
                                                function (data) {
                                                    if (data.store) {
                                                        const record = data.store.findRecord('_id', data.value);
                                                        if (record) {
                                                            vm.set(
                                                                'theValue._parent_id_description',
                                                                record.get('description')
                                                            );
                                                        }
                                                    }
                                                }
                                            );
                                        }
                                        return value;
                                    }
                                }
                            }
                        },
                        true
                    ),
                    {
                        columnWidth: 0.5,
                        xtype: 'fieldcontainer',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.lookuptypes.strings.textcolor,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.lookuptypes.strings.textcolor'
                        },
                        layout: 'column',
                        msgTarget: 'qtip',
                        items: [
                            CMDBuildUI.util.helper.FieldsHelper.getColorpickerField({
                                columnWidth: 1,
                                alt: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor,
                                localized: {
                                    alt: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor'
                                },
                                bind: {
                                    hidden: '{actions.view}',
                                    value: '{theValue.text_color}',
                                    disabled: '{_is_system}'
                                }
                            })
                        ]
                    }
                ]
            },
            {
                layout: 'column',
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getNoteInput('note', {
                        note: {
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.note,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.note'
                                }
                            },
                            columnWidth: 1,
                            name: 'note',
                            bind: {
                                value: '{theValue.note}',
                                disabled: '{_is_system}'
                            }
                        }
                    })
                ]
            },
            {
                layout: 'column',
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                        active: {
                            bind: {
                                value: '{theValue.active}',
                                disabled: '{theLookupType._is_system}',
                                readOnly: '{actions.view}'
                            }
                        }
                    })
                ]
            }
        ];
    },

    getIconFields: function () {
        return [
            {
                layout: 'column',
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('iconType', {
                        iconType: {
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.icontype,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.icontype'
                                }
                            },
                            name: 'iconType',
                            allowBlank: true,
                            queryMode: 'local',
                            displayField: 'label',
                            valueField: 'value',
                            bind: {
                                value: '{theValue.icon_type}',
                                store: '{iconTypeStore}',
                                disabled: '{_is_system}'
                            }
                        }
                    })
                ]
            },
            {
                layout: 'column',
                bind: {
                    hidden: '{!iconTypeIsImage}'
                },
                hidden: true,
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getIconInput({
                        icon: {
                            input: {
                                itemId: 'lookupValueImage',
                                bind: {
                                    disabled: '{_is_system}'
                                },
                                listeners: {
                                    change: 'onFileChange' // no scope given here
                                }
                            },
                            preview: {
                                src: 'theValue.icon_image',
                                resetKey: 'theValue.icon_image',
                                itemId: 'lookupIconPreview'
                            }
                        }
                    })
                ]
            },
            {
                layout: 'column',
                bind: {
                    hidden: '{!iconTypeIsFont}'
                },
                hidde: true,
                items: [
                    {
                        columnWidth: 0.5,
                        xtype: 'fieldcontainer',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.icon,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
                        },
                        layout: 'column',
                        items: [
                            {
                                columnWidth: 1,
                                xtype: 'cmdbuild-fapicker',
                                itemId: 'lookupValueIconFont',
                                allowBlank: false,
                                msgTarget: 'qtip',
                                bind: {
                                    hidden: '{actions.view}',
                                    value: '{theValue.icon_description}',
                                    disabled: '{_is_system}'
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
                                    userCls: '{theValue.icon_font}'
                                }
                            }
                        ]
                    },
                    CMDBuildUI.util.helper.FieldsHelper.getColorpickerField({
                        itemId: 'lookupValueIconColor',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.iconcolor,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.iconcolor'
                        },
                        columnWidth: 0.5,
                        bind: {
                            hidden: '{actions.view}',
                            value: '{theValue.icon_color}',
                            disabled: '{_is_system}'
                        }
                    })
                ]
            }
        ];
    }
});
