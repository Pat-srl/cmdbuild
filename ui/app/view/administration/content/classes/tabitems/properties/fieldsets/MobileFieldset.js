Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.MobileFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-classes-tabitems-properties-fieldsets-mobilefieldset',
    viewModel: {
        type: 'administration-content-classes-tabitems-properties-fieldsets-mobilefieldset'
    },

    hidden: true,
    bind: {
        hidden: '{!canShowMobileAttributes}'
    },

    items: [
        {
            xtype: 'fieldset',
            layout: 'column',
            title: CMDBuildUI.locales.Locales.administration.common.labels.mobile,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.labels.mobile'
            },
            ui: 'administration-formpagination',
            collapsible: true,
            collapsed: true,

            items: [
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    columnWidth: 1,
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            layout: 'column',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.barcodesearchattr,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.barcodesearchattr'
                            },
                            items: [
                                {
                                    columnWidth: 1,
                                    xtype: 'combobox',
                                    name: 'domain',
                                    itemId: 'barcodeSearchAttr_input',
                                    clearFilterOnBlur: true,
                                    queryMode: 'local',
                                    displayField: 'description',
                                    valueField: 'name',
                                    forceSelection: true,
                                    triggers: {
                                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                    },
                                    bind: {
                                        value: '{theObject.barcodeSearchAttr}',
                                        store: '{mobileSearchAttributesStore}',
                                        hidden: '{actions.view}'
                                    }
                                },
                                {
                                    columnWidth: 1,
                                    xtype: 'displayfield',
                                    bind: {
                                        value: '{theObject.barcodeSearchAttr}',
                                        hidden: '{!actions.view}'
                                    },
                                    renderer: function (value) {
                                        const vm = this.lookupViewModel();
                                        const store = vm.get('mobileSearchAttributesStore');
                                        if (store) {
                                            const record = store.findRecord('value', value);
                                            if (record) {
                                                return record.get('label');
                                            }
                                        }
                                        return value;
                                    }
                                }
                            ]
                        },
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            layout: 'column',
                            hidden: true,
                            bind: {
                                hidden: '{theObject.barcodeSearchAttr == ""}'
                            },
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.classes.fieldlabels.barcodesearchregex,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.barcodesearchregex'
                            },

                            items: [
                                {
                                    columnWidth: 1,
                                    xtype: 'textfield',
                                    name: 'barcodeSearchRegex',
                                    itemId: 'barcodeSearchRegex_input',
                                    cls: 'ignore-first-type-rule',
                                    bind: {
                                        value: '{theObject.barcodeSearchRegex}',
                                        hidden: '{actions.view}'
                                    }
                                },
                                {
                                    htmlEncode: true,
                                    columnWidth: 1,
                                    xtype: 'displayfield',
                                    bind: {
                                        value: '{theObject.barcodeSearchRegex}',
                                        hidden: '{!actions.view}'
                                    }
                                }
                            ],
                            listeners: {
                                hide: function () {
                                    this.down('#barcodeSearchRegex_input').setValue(null);
                                }
                            }
                        }
                    ]
                }
            ]
        }
    ]
});
