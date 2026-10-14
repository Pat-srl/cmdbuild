Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.AttachmentsFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-classes-tabitems-properties-fieldsets-attachmentsfieldset',
    ui: 'administration-formpagination',

    items: [
        {
            xtype: 'fieldset',
            collapsible: true,
            layout: 'column',
            title: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.ClassAttachments, // Class Attachments
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.ClassAttachments'
            },
            ui: 'administration-formpagination',
            items: [
                {
                    xtype: 'fieldcontainer',
                    columnWidth: 1,
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            items: [
                                {
                                    xtype: 'fieldcontainer',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.dmscategories.dmscategory, // Category Lookup
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.dmscategories.dmscategory'
                                    },
                                    items: [
                                        {
                                            /********************* Category Lookup **********************/
                                            // create / edit
                                            xtype: 'combobox',
                                            typeAhead: true,
                                            queryMode: 'local',
                                            displayField: 'name',
                                            valueField: 'name',
                                            emptyText: CMDBuildUI.locales.Locales.administration.common.labels.default,
                                            localized: {
                                                emptyText:
                                                    'CMDBuildUI.locales.Locales.administration.common.labels.default'
                                            },
                                            name: 'attachmentTypeLookup',
                                            hidden: true,
                                            bind: {
                                                store: '{dmsCategoryTypesStore}',
                                                value: '{theObject.dmsCategory}',
                                                hidden: '{actions.view}'
                                            },
                                            triggers: {
                                                clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                            }
                                        },
                                        {
                                            // view
                                            xtype: 'displayfieldwithtriggers',
                                            name: 'attachmentTypeLookup',
                                            hidden: true,
                                            bind: {
                                                value: '{theObject.dmsCategory}',
                                                hidden: '{!actions.view}',
                                                hideTrigger: '{!theObject.dmsCategory || pluginMode}'
                                            },
                                            triggers: {
                                                open: {
                                                    cls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'external-link-alt',
                                                        'solid'
                                                    ),
                                                    handler: function (f, trigger, eOpts) {
                                                        const value = f.lookupViewModel().get('theObject.dmsCategory'),
                                                            url =
                                                                CMDBuildUI.util.administration.helper.ApiHelper.client.getDmsCategoryUrl(
                                                                    CMDBuildUI.util.Utilities.stringToHex(value)
                                                                );
                                                        CMDBuildUI.util.Utilities.closeAllPopups();
                                                        CMDBuildUI.util.Utilities.redirectTo(url);
                                                    }
                                                }
                                            },
                                            renderer: function (value) {
                                                if (!Ext.isEmpty(value)) {
                                                    const store = Ext.getStore('dms.DMSCategoryTypes');
                                                    const record = store.findRecord('name', value);
                                                    if (record) {
                                                        return record.get('description');
                                                    }
                                                }
                                                return (
                                                    value ||
                                                    CMDBuildUI.locales.Locales.administration.common.labels.default
                                                );
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'fieldcontainer',
                    columnWidth: 1,
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            /********************* Inline notes **********************/
                            // create / edit / view
                            xtype: 'checkbox',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.attachmentsinline,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.attachmentsinline'
                            },
                            name: 'attachmentsInline',
                            hidden: true,
                            bind: {
                                value: '{theObject.attachmentsInline}',
                                readOnly: '{actions.view}',
                                hidden: '{!theObject}'
                            }
                        },
                        {
                            /********************* Closed inline notes **********************/
                            // create / edit / view
                            columnWidth: 0.5,
                            style: {
                                paddingLeft: '15px'
                            },
                            xtype: 'checkbox',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.classes.fieldlabels.attachmentsinlineclosed,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.attachmentsinlineclosed'
                            },
                            name: 'attachmentsInlineClosed',
                            hidden: true,
                            bind: {
                                value: '{theObject.attachmentsInlineClosed}',
                                readOnly: '{actions.view}',
                                hidden: '{!theObject}',
                                disabled: '{checkboxAttachmentsInlineClosed.disabled}'
                            }
                        }
                    ]
                }
            ]
        }
    ]
});
