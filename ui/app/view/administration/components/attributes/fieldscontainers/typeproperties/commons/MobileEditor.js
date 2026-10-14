Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.commons.MobileEditor', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-attribute-commons-mobileedtor',
    viewModel: {
        type: 'administration-attribute-commons-mobileedtor'
    },
    items: [{
        layout: 'column',
        items: [{
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 1,
            items: [{
                xtype: 'fieldcontainer',
                columnWidth: 0.5,
                layout: 'column',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mobileeditor,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mobileeditor'
                },
                items: [{
                    columnWidth: 1,
                    xtype: 'combobox',
                    name: 'domain',
                    itemId: 'attributedomain',
                    clearFilterOnBlur: true,
                    queryMode: 'local',
                    displayField: 'label',
                    valueField: 'value',
                    forceSelection: true,
                    emptyText: CMDBuildUI.locales.Locales.administration.common.labels.default,
                    localized: {
                        emptyText: 'CMDBuildUI.locales.Locales.administration.common.labels.default'
                    },
                    triggers: {
                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                    },
                    bind: {
                        value: '{theAttribute.mobileEditor}',
                        store: '{mobileEditorsStore}',
                        hidden: '{actions.view}'
                    }
                }, {
                    columnWidth: 1,
                    xtype: 'displayfield',
                    bind: {
                        value: '{theAttribute.mobileEditor}',
                        hidden: '{!actions.view}'
                    },
                    renderer: function (value) {
                        var vm = this.lookupViewModel();
                        var store = vm.get("mobileEditorsStore");
                        if (store) {
                            var record = store.findRecord('value', value);
                            if (record) {
                                return record.get('label');
                            }
                        }
                        if (Ext.isEmpty(value)) {
                            return CMDBuildUI.locales.Locales.administration.common.labels.default;
                        }
                        return value;
                    }
                }]
            }, {
                xtype: 'fieldcontainer',
                columnWidth: 0.5,
                layout: 'column',
                hidden: true,
                bind: {
                    hidden: Ext.String.format('{theAttribute.mobileEditor !== "{0}" && theAttribute.mobileEditor !== "{1}" }', CMDBuildUI.model.Attribute.mobileEditors.qrcode, CMDBuildUI.model.Attribute.mobileEditors.rfid)
                },
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mobileeditorregex,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mobileeditorregex'
                },

                items: [{
                    columnWidth: 1,
                    xtype: 'textfield',
                    name: 'mobileEditorRegex',
                    itemId: 'mobileEditorRegex_input',
                    cls: 'ignore-first-type-rule',
                    bind: {
                        value: '{theAttribute.mobileEditorRegex}',
                        hidden: '{actions.view}'
                    }
                }, {
                    htmlEncode: true,
                    columnWidth: 1,
                    xtype: 'displayfield',
                    bind: {
                        value: '{theAttribute.mobileEditorRegex}',
                        hidden: '{!actions.view}'
                    }
                }],
                listeners: {
                    hide: function () {
                        this.down('#mobileEditorRegex_input').setValue(null);
                    }
                }
            }]
        }]
    }]
});