Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.card.FieldsHelper', {
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
                                disabled: '{_is_system}'
                            }
                        }
                    })
                ]
            }
        ];
    },

    getAttachmentsProperties: function () {
        return [
            {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 1,
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('modelClass', {
                        modelClass: {
                            fieldcontainer: {
                                allowBlank: false,
                                fieldLabel: CMDBuildUI.locales.Locales.administration.dmsmodels.modelclass,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dmsmodels.modelclass'
                                }
                            },
                            displayfield: {
                                xtype: 'displayfieldwithtriggers',
                                bind: {
                                    value: '{theValue.modelClass}',
                                    hideTrigger:
                                        '{!theValue.modelClass || theValue.modelClass == "DmsModel" || pluginMode}'
                                },
                                triggers: {
                                    open: {
                                        cls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                                        handler: function (f, trigger, eOpts) {
                                            var value = f.lookupViewModel().get('theValue.modelClass'),
                                                url =
                                                    CMDBuildUI.util.administration.helper.ApiHelper.client.getDmsModelUrl(
                                                        value
                                                    );
                                            CMDBuildUI.util.Utilities.closeAllPopups();
                                            CMDBuildUI.util.Utilities.redirectTo(url);
                                        }
                                    }
                                }
                            },
                            allowBlank: false,
                            valueField: 'name',
                            displayField: 'description',
                            bind: {
                                value: '{theValue.modelClass}',
                                store: '{DMSModelsStore}',
                                disabled: '{_is_system}'
                            }
                        }
                    }),
                    {
                        columnWidth: 0.5,
                        xtype: 'fieldcontainer',
                        items: [
                            // Allowed extensions: campo per gestire le estensioni. Se vuoto verrà usato il default del modello. (emptyText come in settings)
                            {
                                xtype: 'textarea',
                                columnWidth: 0.5,
                                fieldLabel: CMDBuildUI.locales.Locales.administration.dmsmodels.allowedextensions,
                                emptyText:
                                    CMDBuildUI.locales.Locales.administration.dmscategories.allowfiletypesemptyvalue,
                                localized: {
                                    emptyText:
                                        'CMDBuildUI.locales.Locales.administration.dmscategories.allowfiletypesemptyvalue',
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dmsmodels.allowedextensions'
                                },

                                bind: {
                                    value: '{theValue.allowedExtensions}',
                                    readOnly: '{actions.view}',
                                    disabled: '{_is_system}'
                                },
                                resizable: {
                                    handles: 's'
                                },
                                listeners: {
                                    blur: function (textarea, event, eOpts) {
                                        var value = textarea.getValue();
                                        var extensions = value.split(',');
                                        var cleanedExtension = [];
                                        Ext.Array.forEach(extensions, function (item) {
                                            var _value = item.trim();
                                            if (_value && _value.length) {
                                                cleanedExtension.push(_value);
                                            }
                                        });
                                        textarea.setValue(cleanedExtension.join(','));
                                    }
                                }
                            }
                        ]
                    }
                ]
            },
            {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 1,
                items: [
                    // Count check: campo combo con le voci “No check”, “At least one”, “Exactly number”
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('checkCount', {
                        checkCount: {
                            fieldcontainer: {
                                fieldLabel: CMDBuildUI.locales.Locales.administration.dmsmodels.checkcount,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dmsmodels.checkcount'
                                }
                            },
                            emptyText: CMDBuildUI.locales.Locales.administration.dmsmodels.nocheckemptytext,
                            localized: {
                                emptyText: 'CMDBuildUI.locales.Locales.administration.dmsmodels.nocheckemptytext'
                            },
                            bind: {
                                value: '{theValue.checkCount}',
                                store: '{checkCountStore}',
                                disabled: '{_is_system}'
                            },
                            triggers: {
                                clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                            }
                        }
                    }),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('checkCountNumber', {
                        checkCountNumber: {
                            minValue: 1,
                            step: 1,
                            fieldcontainer: {
                                hidden: true,
                                allowBlank: true,
                                fieldLabel: CMDBuildUI.locales.Locales.administration.dmsmodels.checkcountnumber,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.dmsmodels.checkcountnumber'
                                },
                                bind: {
                                    hidden: '{checkCountNumberHidden}'
                                },
                                listeners: {
                                    hide: function (fieldcontainer) {
                                        var form = fieldcontainer.up('form');
                                        var field = fieldcontainer.down('numberfield');
                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                            field,
                                            true,
                                            form
                                        );
                                    },
                                    show: function (fieldcontainer) {
                                        var form = fieldcontainer.up('form');
                                        var field = fieldcontainer.down('numberfield');
                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                            field,
                                            false,
                                            form
                                        );
                                    }
                                }
                            },
                            bind: {
                                value: '{theValue.checkCountNumber}',
                                disabled: '{_is_system}'
                            }
                        }
                    })
                ]
            },
            {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 1,
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('maxFileSize', {
                        maxFileSize: {
                            minValue: 1,
                            step: 1,
                            fieldcontainer: {
                                allowBlank: true,
                                fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.maxfilesize,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.maxfilesize'
                                }
                            },
                            unitOfMeasure: 'MB',
                            emptyText: CMDBuildUI.locales.Locales.administration.dmsmodels.nocheckemptytext,
                            localized: {
                                emptyText: 'CMDBuildUI.locales.Locales.administration.dmsmodels.nocheckemptytext'
                            },
                            bind: {
                                value: '{theValue.maxFileSize}',
                                disabled: '{_is_system}'
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
            }
        ];
    }
});
