Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.attachments.FromCard', {
    extend: 'Ext.form.FieldSet',

    requires: [
        'CMDBuildUI.view.administration.content.emails.templates.card.attachments.FromCardController',
        'CMDBuildUI.view.administration.content.emails.templates.card.attachments.FromCardModel'
    ],

    alias: 'widget.administration-content-emails-templates-card-attachments-fromcard',
    controller: 'administration-content-emails-templates-card-attachments-fromcard',
    viewModel: {
        type: 'administration-content-emails-templates-card-attachments-fromcard'
    },
    title: CMDBuildUI.locales.Locales.administration.emails.fromcard,

    localized: {
        title: 'CMDBuildUI.locales.Locales.administration.emails.fromcard'
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    userCls: 'fieldset-fullwidth',
    ui: 'administration-formpagination',
    items: [
        {
            xtype: 'container',
            columnWidth: 1,
            layout: 'column',
            items: [
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    columnWidth: 1,
                    items: [
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('attachments', {
                            attachments: {
                                fieldcontainer: {}, // config for fieldcontainer
                                fieldLabel:
                                    CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels
                                        .attachments, // the localized object for label of field
                                localized: {
                                    fieldLabel:
                                        'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.attachments'
                                },
                                displayField: 'label',
                                valueField: 'value',
                                bind: {
                                    store: '{attachmentsStore}',
                                    value: '{attachmentsMode}'
                                }
                            }
                        }),
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.filters,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.filters'
                            },
                            bind: {
                                hidden: '{attachmentsMode !== "fromFilter"}'
                            },
                            items: [
                                {
                                    xtype: 'components-administration-toolbars-formtoolbar',
                                    style: 'border:none; margin-top: 5px',
                                    items: [
                                        {
                                            xtype: 'tool',
                                            itemId: 'editFilterBtn',
                                            cls: 'administration-tool margin-right5',
                                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('filter', 'solid'),
                                            tooltip:
                                                CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips
                                                    .filters,
                                            autoEl: {
                                                'data-testid': 'administration-searchfilter-tool-editfilterbtn'
                                            },
                                            bind: {
                                                disabled: '{filterDisabled}'
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
});
