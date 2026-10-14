Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.NotesFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-classes-tabitems-properties-fieldsets-notesfieldset',

    items: [
        {
            xtype: 'fieldset',
            layout: 'column',
            title: CMDBuildUI.locales.Locales.administration.common.labels.note,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.labels.note'
            },
            ui: 'administration-formpagination',
            collapsible: true,
            items: [
                {
                    columnWidth: 0.5,
                    items: [
                        {
                            /********************* Inline notes **********************/
                            // create / edit / view
                            xtype: 'checkbox',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.noteinline, // Inline notes
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.noteinline'
                            },
                            name: 'noteinline',
                            hidden: true,
                            bind: {
                                value: '{theObject.noteInline}',
                                readOnly: '{actions.view}',
                                hidden: '{!theObject}'
                            }
                        }
                    ]
                },
                {
                    columnWidth: 0.5,
                    style: {
                        paddingLeft: '15px'
                    },
                    items: [
                        {
                            /********************* Closed inline notes **********************/
                            // create / edit / view
                            xtype: 'checkbox',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.noteinlineclosed, // Closed inline notes
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.noteinlineclosed'
                            },
                            name: 'noteinlineclosed',
                            hidden: true,
                            bind: {
                                value: '{theObject.noteInlineClosed}',
                                readOnly: '{actions.view}',
                                hidden: '{!theObject}',
                                disabled: '{checkboxNoteInlineClosed.disabled}'
                            }
                        }
                    ]
                },
                {
                    columnWidth: 1,
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            items: [
                                {
                                    /********************* Show in grid inline notes **********************/
                                    // create / edit / view
                                    xtype: 'checkbox',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showingrid,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showingrid'
                                    },
                                    itemId: 'noteInGrid',
                                    hidden: true,
                                    bind: {
                                        value: '{theObject.noteInGrid}',
                                        readOnly: '{actions.view}',
                                        hidden: '{!theObject.noteInline}'
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
