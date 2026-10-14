// locazation: ok

Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.GeneralProperties', {
    extend: 'Ext.form.Panel',

    alias: 'widget.administration-components-attributes-fieldscontainers-generalproperties',

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    items: [
        {
            xtype: 'fieldset',
            ui: 'administration-formpagination',
            items: [
                {
                    layout: 'hbox',
                    items: [
                        {
                            flex: 1,
                            items: [
                                {
                                    // view / view in row
                                    columnWidth: 0.5,
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                                    },
                                    name: 'name',
                                    autoEl: {
                                        'data-testid': 'attribute-name_display'
                                    },
                                    bind: {
                                        value: '{theAttribute.name}',
                                        hidden: '{!actions.view}'
                                    }
                                }
                            ]
                        },
                        {
                            columnWidth: 0.5,
                            layout: 'column',
                            flex: 1,
                            // view / viewInRow
                            items: [
                                {
                                    flex: 1,
                                    xtype: 'fieldcontainer',
                                    layout: 'column',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.common.labels.description'
                                    },
                                    labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                                    labelToolIconQtip:
                                        CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                                    labelToolIconClick: 'onTranslateClick',
                                    cls: 'ignore-first-type-rule',
                                    items: [
                                        {
                                            xtype: 'displayfield',
                                            name: 'description',
                                            autoEl: {
                                                'data-testid': 'attribute-description_display'
                                            },
                                            bind: {
                                                value: '{theAttribute.description}',
                                                hidden: '{!actions.view}'
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    layout: 'hbox',
                    flex: 1,
                    items: [
                        {
                            itemId: 'groupfield',
                            columnWidth: 0.5,
                            flex: 1,
                            xtype: 'fieldcontainer',
                            layout: 'column',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group'
                            },
                            items: [
                                {
                                    xtype: 'displayfield',
                                    autoEl: {
                                        'data-testid': 'attribute-group_display'
                                    },
                                    hidden: true,
                                    bind: {
                                        hidden: '{isGroupHidden}',
                                        value: '{theAttribute._group_description}'
                                    }
                                }
                            ]
                        },
                        {
                            flex: 1,
                            items: [
                                {
                                    columnWidth: 0.5,
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mode,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mode'
                                    },
                                    autoEl: {
                                        'data-testid': 'attribute-mode_display'
                                    },
                                    name: 'mode',
                                    hidden: true,
                                    bind: {
                                        hidden: Ext.String.format(
                                            '{theAttribute.type == "{0}"}',
                                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula
                                        ),
                                        value: '{theAttribute.mode}'
                                    },
                                    renderer: CMDBuildUI.util.administration.helper.RendererHelper.getAttributeMode
                                }
                            ]
                        }
                    ]
                },
                {
                    layout: 'column',
                    xtype: 'fieldcontainer',
                    items: [
                        {
                            columnWidth: 0.25,
                            xtype: 'checkbox',
                            autoEl: {
                                'data-testid': 'attribute-showInGrid_display'
                            },
                            name: 'showInGrid',
                            readOnly: true,
                            hidden: true,
                            bind: {
                                fieldLabel: '{showInGridLabel}',
                                value: '{theAttribute.showInGrid}',
                                hidden: '{showInGridHidden}'
                            }
                        },
                        {
                            columnWidth: 0.25,
                            xtype: 'checkbox',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showinreducedgrid,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showinreducedgrid'
                            },
                            autoEl: {
                                'data-testid': 'attribute-showInReducedGrid_display'
                            },
                            hidden: true,
                            name: 'showInReducedGrid',
                            readOnly: true,
                            bind: {
                                value: '{theAttribute.showInReducedGrid}',
                                hidden: Ext.String.format(
                                    '{objectType == "{0}" || objectType == "{1}"}',
                                    CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                                    CMDBuildUI.util.helper.ModelHelper.objecttypes.domain.charAt(0).toUpperCase() +
                                        CMDBuildUI.util.helper.ModelHelper.objecttypes.domain.slice(1)
                                )
                            }
                        },
                        {
                            columnWidth: 0.25,
                            xtype: 'checkbox',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.hideingrid,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.hideingrid'
                            },
                            autoEl: {
                                'data-testid': 'attribute-excludeFromGrid_display'
                            },
                            hidden: true,
                            name: 'excludeFromGrid',
                            readOnly: true,
                            bind: {
                                value: '{theAttribute.hideInGrid}',
                                hidden: Ext.String.format(
                                    '{objectType == "{0}" || objectType == "{1}"}',
                                    CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                                    CMDBuildUI.util.helper.ModelHelper.objecttypes.domain.charAt(0).toUpperCase() +
                                        CMDBuildUI.util.helper.ModelHelper.objecttypes.domain.slice(1)
                                )
                            }
                        },
                        {
                            columnWidth: 0.25,
                            xtype: 'checkbox',
                            itemId: 'attributeDomainKey',
                            hidden: true,
                            readOnly: true,
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
                            autoEl: {
                                'data-testid': 'attribute-unique_display'
                            },
                            name: 'unique',
                            readOnly: true,
                            bind: {
                                value: '{theAttribute.unique}',
                                hidden: Ext.String.format(
                                    '{theAttribute.type == "{0}"}',
                                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula
                                )
                            },
                            hidden: true
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'checkbox',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mandatory,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mandatory'
                            },
                            name: 'mandatory',
                            autoEl: {
                                'data-testid': 'attribute-mandatory_display'
                            },
                            readOnly: true,
                            bind: {
                                value: '{theAttribute.mandatory}',
                                hidden: Ext.String.format(
                                    '{isMandatoryHidden || theAttribute.type == "{0}"}',
                                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula
                                )
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
                                'data-testid': 'attribute-hideInFilter_display'
                            },
                            readOnly: true,
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
                            readOnly: true,
                            hidden: true,
                            bind: {
                                hidden: Ext.String.format(
                                    '{objectType == "dmsmodel" || objectType == "Domain" || theAttribute.hideInFilter || theAttribute.type == "{0}"}',
                                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula
                                ),
                                value: '{theAttribute.sortingEnabled}'
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
                            autoEl: {
                                'data-testid': 'attribute-active_display'
                            },
                            name: 'active',
                            readOnly: true,
                            bind: {
                                value: '{theAttribute.active}'
                            }
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'displayfield',
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
        }
    ]
});
