Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Formula', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-formulafields',

    layout: 'column',
    items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
            'formulaType', {
                formulaType: {
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.formulatype,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.formulatype'
                        }
                    },

                    displayfield: {
                        bind: {
                            value: '{theAttribute._formulaType_description}'
                        },
                        listeners: {
                            afterrender: function(view){
                                var me = this;
                                var vm = me.lookupViewModel();
                                vm.bind({
                                    bindTo: '{theAttribute.formulaType}'
                                }, function (type) {
                                    var store = vm.get('formulaTypesStore');
                                   
                                    if (store && !vm.isDestroyed) {
                                        var record = store.findRecord('value', type);
                                        if (record) {
                                            vm.set('theAttribute._formulaType_description', record.get('label'));
                                        }
                                    }
                                });
                            }
                        }
                    },
                    combofield: {
                        displayField: 'label',
                        valueField: 'value',
                        bind: {
                            store: '{formulaTypesStore}',
                            value: '{theAttribute.formulaType}'
                        }                        
                    }
                }
            }),
        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
            'formulaCodeScript', {
                formulaCodeScript: {
                    fieldcontainer: {
                        hidden: true,
                        bind: {
                            hidden: Ext.String.format('{theAttribute.formulaType !== "{0}"}', CMDBuildUI.model.Attribute.formulaTypes.script)
                        },
                        listeners: {
                            show: function () {                               
                                var form = this.up('administration-attribute-formulafields').up('form');
                                if (form) {
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(this.down('#formulaCodeScript_input'), false, form);                                    
                                }
                            },
                            hide: function () {
                                var form = this.up('administration-attribute-formulafields').up('form');
                                if (form) {
                                    CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(this.down('#formulaCodeScript_input'), true, form);
                                    this.down('#formulaCodeScript_input').setValue(null);
                                }
                            }
                        }
                    },

                    displayfield: {
                        bind: {
                            value: '{theAttribute._formulaCode_description}',
                            fieldLabel: '{formulaCodeLabel}'
                        },
                        listeners: {
                            beforerender: function () {
                                var me = this;
                                var vm = me.lookupViewModel();
                                vm.bind({
                                    formulaCode: '{theAttribute.formulaCode}',
                                    formulaType: '{theAttribute.formulaType}',                                    
                                    scriptsStore: '{scriptsStore}'
                                }, function (data) {
                                    var store = data.scriptsStore;
                                   
                                    if (store && !vm.isDestroyed) {
                                        var record = store.findRecord('name', data.formulaCode);
                                        if (record) {
                                            vm.set('theAttribute._formulaCode_description', record.get('description'));
                                        }
                                    }
                                });

                            }
                        }
                    },
                    combofield: {
                        displayField: 'description',
                        valueField: 'name',
                        bind: {
                            fieldLabel: '{formulaCodeLabel}',
                            value: '{theAttribute.formulaCode}',
                            store: '{scriptsStore}'
                        },
                        forceSelection: true
                    }
                }
            }),
            CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                'formulaCodeSql', {
                    formulaCodeSql: {
                        fieldcontainer: {
                            hidden: true,
                            bind: {                                
                                hidden: Ext.String.format('{theAttribute.formulaType !== "{0}"}', CMDBuildUI.model.Attribute.formulaTypes.sql)
                            },
                            listeners: {
                                show: function () {
                                    var form = this.up('administration-attribute-formulafields').up('form');
                                    if (form) {
                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(this.down('#formulaCodeSql_input'), false, form);                                        
                                    }
                                },
                                hide: function () {
                                    var form = this.up('administration-attribute-formulafields').up('form');
                                    if (form) {
                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(this.down('#formulaCodeSql_input'), true, form);  
                                        this.down('#formulaCodeSql_input').setValue(null);                                      
                                    }
                                }
                            }
                        },
    
                        displayfield: {
                            bind: {
                                value: '{theAttribute._formulaCode_description}',
                                fieldLabel: '{formulaCodeLabel}'
                            },
                            listeners: {
                                beforerender: function () {
                                    var me = this;
                                    var vm = me.lookupViewModel();
                                    vm.bind({
                                        formulaCode: '{theAttribute.formulaCode}',
                                        formulaType: '{theAttribute.formulaType}',
                                        functionsStore: '{functionsStore}'
                                        
                                    }, function (data) {
                                        var store= data.functionsStore;                                        
                                        if (store && !vm.isDestroyed) {
                                            var record = store.findRecord('name', data.formulaCode);
                                            if (record) {
                                                vm.set('theAttribute._formulaCode_description', record.get('description'));
                                            }
                                        }
                                    });
    
                                }
                            }
                        },
                        combofield: {
                            displayField: 'description',
                            valueField: 'name',
                            bind: {
                                fieldLabel: '{formulaCodeLabel}',
                                value: '{theAttribute.formulaCode}',
                                store: '{functionsStore}'
                            },
                            forceSelection: true
                        }
                    }
                })

    ],
    listeners: {
        show: function () {
            var form = this.up('form');
            if (form) {
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(this.down('#formulaType_input'), false, form);
            }
        },
        hide: function () {
            var form = this.up('form');
            if (form) {
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(this.down('#formulaType_input'), true, form);
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(this.down('#formulaCodeSql_input'), true, form);  
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(this.down('#formulaCodeScript_input'), true, form);  
            }
        }
    }
});