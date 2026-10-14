Ext.define('CMDBuildUI.view.administration.content.dms.models.tabitems.properties.fieldsets.AttachmentsFieldset', {
    extend: 'Ext.panel.Panel',
    requires: ['CMDBuildUI.util.administration.helper.FieldsHelper'],

    alias: 'widget.administration-content-dms-models-tabitems-properties-fieldsets-attachmentsfieldset',
    ui: 'administration-formpagination',

    items: [
        {
            xtype: 'fieldset',
            collapsible: true,
            layout: 'column',
            title: CMDBuildUI.locales.Locales.administration.dmsmodels.modelattachments, // Class Attachments
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.dmsmodels.modelattachments'
            },
            ui: 'administration-formpagination',
            items: [
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    columnWidth: 1,
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'fieldcontainer',
                            resizable: {
                                handles: 's'
                            },
                            minHeight: 125,
                            items: [
                                // Allowed extensions: campo per gestire le estensioni. Se vuoto verrà usato il default del modello. (emptyText come in settings)
                                {
                                    xtype: 'textarea',
                                    columnWidth: 0.5,
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.dmsmodels.allowedextensions,
                                    emptyText:
                                        CMDBuildUI.locales.Locales.administration.dmsmodels.allowedfiletypesemptyvalue,
                                    localized: {
                                        emptyText:
                                            'CMDBuildUI.locales.Locales.administration.dmsmodels.allowedfiletypesemptyvalue',
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.dmsmodels.allowedextensions'
                                    },
                                    minHeight: '100%',
                                    height: '100%',
                                    bind: {
                                        value: '{theModel.allowedExtensions}',
                                        readOnly: '{actions.view || pluginMode}'
                                    },
                                    listeners: {
                                        blur: function (textarea, event, eOpts) {
                                            var value = textarea.getValue();
                                            var extensions = value.split(',');
                                            var cleanedExtension = [];
                                            Ext.Array.forEach(extensions, function (item) {
                                                var value = item.trim();
                                                if (value && value.length) {
                                                    cleanedExtension.push(value);
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
                                allowBlank: false,
                                padding: 0,
                                fieldcontainer: {
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.dmsmodels.checkcount,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dmsmodels.checkcount'
                                    }
                                },
                                bind: {
                                    value: '{theModel.checkCount}',
                                    store: '{checkCountStore}'
                                }
                            }
                        }),
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput(
                            'checkCountNumber',
                            {
                                checkCountNumber: {
                                    padding: 0,
                                    minValue: 0,
                                    step: 1,
                                    fieldcontainer: {
                                        margin: '0 0 0 15',
                                        hidden: true,
                                        allowBlank: true,
                                        fieldLabel:
                                            CMDBuildUI.locales.Locales.administration.dmsmodels.checkcountnumber,
                                        localized: {
                                            fieldLabel:
                                                'CMDBuildUI.locales.Locales.administration.dmsmodels.checkcountnumber'
                                        },
                                        bind: {
                                            hidden: '{checkCountNumberHidden}'
                                        },
                                        listeners: {
                                            hide: function (fieldcontainer) {
                                                var form = fieldcontainer.up('form');
                                                var field = fieldcontainer.down('numberfield');
                                                field.setMinValue(0);
                                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                                    field,
                                                    true,
                                                    form
                                                );
                                            },
                                            show: function (fieldcontainer) {
                                                var form = fieldcontainer.up('form');
                                                var field = fieldcontainer.down('numberfield');
                                                field.setMinValue(1);
                                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                                    field,
                                                    false,
                                                    form
                                                );
                                            }
                                        }
                                    },
                                    bind: {
                                        value: '{theModel.checkCountNumber}'
                                    }
                                }
                            }
                        )
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
                                    },
                                    cls: 'ignore-first-type-rule'
                                },
                                unitOfMeasure: 'MB',
                                cls: 'ignore-first-type-rule',
                                emptyText: CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                                localized: {
                                    emptyText: 'CMDBuildUI.locales.Locales.main.preferences.defaultvalue'
                                },
                                bind: {
                                    value: '{theModel.maxFileSize}',
                                    disabled: '{_is_system}'
                                }
                            }
                        })
                    ]
                }
            ]
        }
    ]
});
