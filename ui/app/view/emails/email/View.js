Ext.define('CMDBuildUI.view.emails.email.View', {
    extend: 'Ext.form.Panel',

    requires: ['CMDBuildUI.view.emails.email.ViewController', 'CMDBuildUI.view.emails.email.EmailModel'],

    alias: 'widget.emails-view',
    controller: 'emails-view',
    viewModel: {
        type: 'emails-email'
    },

    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.read,

    autoScroll: true,

    items: [
        {
            xtype: 'fieldcontainer',
            layout: 'fit',
            fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
            items: [
                {
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            layout: 'fit',
                            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                            fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                            items: [
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.emails.from,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.emails.from'
                                    },
                                    bind: {
                                        value: '{theEmail.from}'
                                    },
                                    renderer: function (data) {
                                        return CMDBuildUI.util.Utilities.transformMajorMinor(data);
                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    label: CMDBuildUI.locales.Locales.emails.to,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.emails.to'
                                    },
                                    bind: {
                                        value: '{theEmail.to}'
                                    },
                                    renderer: function (data) {
                                        return CMDBuildUI.util.Utilities.transformMajorMinor(data);
                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.emails.cc,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.emails.cc'
                                    },
                                    bind: {
                                        value: '{theEmail.cc}'
                                    },
                                    renderer: function (data) {
                                        return CMDBuildUI.util.Utilities.transformMajorMinor(data);
                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.emails.bcc,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.emails.bcc'
                                    },
                                    bind: {
                                        value: '{theEmail.bcc}'
                                    },
                                    renderer: function (data) {
                                        return CMDBuildUI.util.Utilities.transformMajorMinor(data);
                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.emails.subject,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.emails.subject'
                                    },
                                    bind: {
                                        value: '{theEmail.subject}'
                                    },
                                    renderer: function (data) {
                                        return CMDBuildUI.util.helper.FieldsHelper.renderTextField(data);
                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.emails.message,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.emails.message'
                                    },
                                    bind: {
                                        value: '{theEmail._content_html}'
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'container',
                    hidden: true,
                    bind: {
                        hidden: '{hideEmailTplAttachmentsMsg}'
                    },
                    html: CMDBuildUI.locales.Locales.emails.templateattachments,
                    localized: {
                        html: 'CMDBuildUI.locales.Locales.emails.templateattachments'
                    },
                    margin: 10,
                    ui: 'messageinfo'
                },
                {
                    xtype: 'formpaginationfieldset',
                    collapsible: true,
                    ui: 'formpagination',
                    title: CMDBuildUI.locales.Locales.common.tabs.attachments,
                    hidden: true,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.common.tabs.attachments'
                    },
                    bind: {
                        hidden: '{!attachmentsTotalCount}'
                    },
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            layout: 'fit',
                            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                            fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                            hidden: true,
                            bind: {
                                hidden: '{!attachmentsTotalCount}'
                            },
                            listeners: {
                                show: function () {
                                    var popup = this.up('panel');
                                    Ext.asap(function () {
                                        popup.updateLayout();
                                    });
                                }
                            },
                            items: [
                                {
                                    xtype: 'grid',
                                    reference: 'attachmentsgrid',
                                    itemId: 'attachmentsgrid',
                                    forceFit: true,
                                    columns: [
                                        {
                                            text: CMDBuildUI.locales.Locales.attachments.filename,
                                            dataIndex: 'name',
                                            align: 'left',
                                            localized: {
                                                text: 'CMDBuildUI.locales.Locales.attachments.filename'
                                            },
                                            width: '45%'
                                        },
                                        {
                                            text: CMDBuildUI.locales.Locales.attachments.description,
                                            dataIndex: 'description',
                                            align: 'left',
                                            localized: {
                                                text: 'CMDBuildUI.locales.Locales.attachments.description'
                                            },
                                            width: '45%'
                                        },
                                        {
                                            text: CMDBuildUI.locales.Locales.attachments.preview,
                                            width: 95,
                                            menuDisabled: true,
                                            xtype: 'widgetcolumn',
                                            widget: {
                                                xtype: 'dms-preview',
                                                bind: {
                                                    attachmentId: '{record._id}',
                                                    fileName: '{record.name}',
                                                    fileMimeType: '{record.MimeType}',
                                                    proxyUrl: '{attachmentsstore.urlPreview}'
                                                },
                                                alt: CMDBuildUI.locales.Locales.attachments.preview,
                                                localized: {
                                                    alt: 'CMDBuildUI.locales.Locales.attachments.preview'
                                                }
                                            },
                                            localized: {
                                                text: 'CMDBuildUI.locales.Locales.attachments.preview'
                                            }
                                        },
                                        {
                                            xtype: 'actioncolumn',
                                            minWidth: 45,
                                            maxWidth: 45,
                                            menuDisabled: true,
                                            hideable: false,
                                            align: 'center',
                                            items: [
                                                {
                                                    iconCls:
                                                        'attachments-grid-action ' +
                                                        CMDBuildUI.util.helper.IconHelper.getIconId(
                                                            'download',
                                                            'solid'
                                                        ),
                                                    getTip: function () {
                                                        return CMDBuildUI.locales.Locales.attachments.download;
                                                    },
                                                    handler: function (grid, rowIndex, colIndex) {
                                                        var attachmentStore = grid.getStore(),
                                                            record = attachmentStore.getAt(rowIndex),
                                                            url = Ext.String.format(
                                                                '{0}/{1}/{2}',
                                                                attachmentStore.getProxy().getUrl(), // base url
                                                                record.getId(), // attachment id
                                                                record.get('name') // file name
                                                            );
                                                        CMDBuildUI.util.File.download(url, record.get('name'));
                                                    }
                                                }
                                            ]
                                        }
                                    ],
                                    bind: {
                                        store: '{attachments}'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
});
