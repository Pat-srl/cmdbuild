Ext.define('CMDBuildUI.view.thematisms.thematism.Row', {
    extend: 'CMDBuildUI.components.tab.FieldSet',

    requires: ['CMDBuildUI.view.thematisms.thematism.RowModel'],

    alias: 'widget.thematisms-thematism-row',
    viewModel: { type: 'thematisms-thematism-row' },

    title: CMDBuildUI.locales.Locales.thematism.defineThematism,
    localized: { title: 'CMDBuildUI.locales.Locales.thematism.defineThematism' },
    collapsible: true,

    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    defaults: {
        xtype: 'fieldcontainer',
        layout: 'column',
        defaults: {
            xtype: 'fieldcontainer',
            columnWidth: 0.5,
            flex: '0.5',
            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
            layout: 'anchor'
        }
    },

    items: [
        {
            items: [
                {
                    items: [
                        {
                            xtype: 'combobox',
                            fieldLabel: CMDBuildUI.locales.Locales.thematism.analysisType,
                            localized: { fieldLabel: 'CMDBuildUI.locales.Locales.thematism.analysisType' },
                            valueField: 'value',
                            displayField: 'label',
                            allowBlank: false,
                            tabIndex: 1,
                            bind: { store: '{analysistypes}', value: '{theThematism.analysistype}' }
                        }
                    ]
                },
                {
                    items: [
                        {
                            xtype: 'combobox',
                            itemId: 'layerCombo',
                            fieldLabel: CMDBuildUI.locales.Locales.thematism.geoAttribute,
                            localized: { fieldLabel: 'CMDBuildUI.locales.Locales.thematism.geoAttribute' },
                            valueField: 'value',
                            displayField: 'label',
                            allowBlank: false,
                            tabIndex: 2,
                            bind: { store: '{geoAttributes}', value: '{theThematism.attribute}' }
                        }
                    ]
                }
            ]
        },
        {
            items: [
                {
                    items: [
                        {
                            xtype: 'combobox',
                            itemId: 'sourceCombo',
                            fieldLabel: CMDBuildUI.locales.Locales.thematism.source,
                            localized: { fieldLabel: 'CMDBuildUI.locales.Locales.thematism.source' },
                            valueField: 'value',
                            displayField: 'label',
                            allowBlank: false,
                            tabIndex: 3,
                            bind: { store: '{sources}', value: '{theThematism.type}' }
                        }
                    ]
                },
                {
                    items: [
                        {
                            xtype: 'combobox',
                            itemId: 'attributeCombo',
                            fieldLabel: CMDBuildUI.locales.Locales.thematism.attribute,
                            localized: { fieldLabel: 'CMDBuildUI.locales.Locales.thematism.attribute' },
                            hidden: true,
                            valueField: 'value',
                            displayField: 'label',
                            allowBlank: false,
                            tabIndex: 4,
                            bind: {
                                hidden: '{hiddenfields.attributecombo}',
                                store: '{attributesstore}',
                                value: '{theThematism.classattribute}'
                            }
                        },
                        {
                            xtype: 'combobox',
                            itemId: 'functionCombo',
                            fieldLabel: CMDBuildUI.locales.Locales.thematism.function,
                            localized: { fieldLabel: 'CMDBuildUI.locales.Locales.thematism.function' },
                            hidden: true,
                            valueField: 'value',
                            displayField: 'label',
                            allowBlank: false,
                            tabIndex: 4,
                            bind: {
                                hidden: '{hiddenfields.functioncombo}',
                                store: '{functionstore}',
                                value: '{theThematism.function}'
                            }
                        }
                    ]
                },
                {
                    items: [
                        {
                            xtype: 'numberfield',
                            fieldLabel: CMDBuildUI.locales.Locales.thematism.segments,
                            localized: { fieldLabel: 'CMDBuildUI.locales.Locales.thematism.segments' },
                            hidden: true,
                            minValue: 0,
                            bind: { value: '{theThematism.segments}', hidden: '{segmentsHidden}' }
                        }
                    ]
                }
            ]
        }
    ]
});
