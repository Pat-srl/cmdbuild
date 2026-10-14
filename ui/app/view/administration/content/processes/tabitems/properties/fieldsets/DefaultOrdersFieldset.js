Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.DefaultOrdersFieldset', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.administration-content-processes-tabitems-properties-fieldsets-defaultordersfieldset',
    controller: 'administration-content-processes-tabitems-properties-fieldsets-defaultordersfieldset',
    viewModel: {
        type: 'administration-content-processes-tabitems-properties-fieldsets-defaultordersfieldset'
    },

    items: [{
        xtype: 'fieldset',
        layout: 'column',
        bind: {
            hidden: '{actions.add}'
        },
        title: CMDBuildUI.locales.Locales.administration.classes.strings.datacardsorting, // Data cards sorting
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.classes.strings.datacardsorting'
        },
        collapsible: true,
        ui: 'administration-formpagination',
        items: [{
            columnWidth: 0.75,
            items: [{
                xtype: 'components-grid-reorder-grid',
                itemId: 'defaultOrderGrid',
                bind: {
                    store: '{theProcess.defaultOrder}'
                },
                viewConfig: {
                    markDirty: false
                },
                columns: [{
                    flex: 2,
                    text: CMDBuildUI.locales.Locales.administration.common.strings.attribute, // Attribute
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.strings.attribute'
                    },
                    dataIndex: '_attribute_description',
                    align: 'left',
                    renderer: function (value, cell, record) {
                        var vm = this.lookupViewModel();
                        if (record.get('attribute') === 'IdClass') {
                            record.set('_attribute_description', 'Subtype');
                        } else {
                            if (!value) {
                                vm.bind({
                                    bindTo: {
                                        attributesStore: '{attributesStore}'
                                    },
                                    single: true
                                }, function (data) {
                                    var _record = data.attributesStore.findRecord('name', record.get('attribute'));
                                    if (_record) {
                                        record.set('_attribute_description', _record.get('description'));
                                    }
                                });
                            }
                        }
                        return value;
                    }
                }, {
                    flex: 1,
                    text: CMDBuildUI.locales.Locales.administration.classes.texts.direction,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.classes.texts.direction'
                    },
                    dataIndex: 'direction',
                    align: 'left',
                    renderer: function (value) {
                        var vm = this.lookupViewModel();
                        var store = vm.get('defaultOrderDirectionsStore');
                        if (store) {
                            var record = store.findRecord('value', value);
                            if (record) {
                                return record.get('label');
                            }
                        }
                        return Ext.String.capitalize(value);
                    },
                    height: 19,
                    variableRowHeight: true,
                    editor: {
                        xtype: 'combo',
                        allowBlank: false,
                        editable: false,
                        height: 19,
                        minHeight: 19,
                        maxHeight: 19,
                        displayField: 'label',
                        valueField: 'value',
                        queryMode: 'local',
                        ui: 'reordergrid-editor-combo',
                        bind: {
                            value: '{record.direction}',
                            store: '{defaultOrderDirectionsStore}'
                        },
                        listeners: {
                            focus: function (combo, event, eOpts) {
                                combo.expand();
                            },
                        }
                    }
                }, {
                    xtype: 'actioncolumn',
                    minWidth: 150,
                    maxWidth: 150,
                    bind: {
                        hidden: '{!actions.edit}'
                    },
                    align: 'center',
                    items: [{
                        handler: function (grid, rowIndex, colIndex, item, e, record) {
                            if (record.get('editing')) {
                                record.set('editing', false);
                                record.commit();
                                grid.editingPlugin.completeEdit();
                            } else {
                                grid.editingPlugin.startEdit(record, 1);
                            }
                        },
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.common.actions.edit;
                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            if (record.get('editing')) {
                                return CMDBuildUI.util.helper.IconHelper.getIconId('check', 'solid');
                            }
                            return CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid');
                        }
                    }, {
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-up', 'solid'),
                        handler: 'moveUp',
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.common.actions.moveup;
                        },
                        isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                            if (!record.get('editing')) {
                                rowIndex = rowIndex == null ? view.getStore().findExact('id', record.get('id')) : rowIndex;
                                return rowIndex == 0;
                            } else {
                                return true;
                            }
                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            if (record.get('editing')) {
                                return CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid');
                            }
                            return CMDBuildUI.util.helper.IconHelper.getIconId('arrow-up', 'solid');
                        },
                        style: {
                            margin: '10'
                        }
                    }, {
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-down', 'solid'),
                        handler: 'moveDown',
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.common.actions.movedown;
                        },
                        isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                            if (!record.get('editing')) {
                                rowIndex = rowIndex == null ? view.getStore().findExact('id', record.get('id')) : rowIndex;
                                return rowIndex >= view.store.getCount() - 1;
                            } else {
                                return true;
                            }

                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            if (record.get('editing')) {
                                return CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid');
                            }
                            return CMDBuildUI.util.helper.IconHelper.getIconId('arrow-down', 'solid');
                        },
                        margin: '0 10 0 10'
                    }, {
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                        handler: 'deleteRow',
                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                            return CMDBuildUI.locales.Locales.administration.common.actions.remove;
                        },
                        isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                            if (!record.get('editing')) {
                                return false;
                            } else {
                                return true;
                            }

                        },
                        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                            if (record.get('editing')) {
                                return CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid');
                            }
                            return CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid');
                        },
                        margin: '0 10 0 10'
                    }]
                }]
            }, {
                flex: 1,
                margin: '20 0 20 0',
                items: [{
                    xtype: 'components-grid-reorder-grid',
                    bind: {
                        store: '{defaultOrderStoreNew}',
                        hidden: '{!actions.edit}'
                    },
                    viewConfig: {
                        markDirty: false
                    },
                    columns: [{
                        flex: 2,
                        text: '',
                        xtype: 'widgetcolumn',
                        align: 'left',
                        dataIndex: 'attribute',
                        widget: {
                            xtype: 'combobox',
                            queryMode: 'local',
                            typeAhead: true,
                            itemId: 'defaultOrderAttribute',
                            displayField: 'description',
                            valueField: 'name',
                            value: '',
                            bind: {
                                value: '{record.attribute}',
                                store: '{allAttributesForSorting}'
                            },
                            listeners: {
                                beforequery: function (queryEv) {
                                    var allAttrStore = this.lookupViewModel().get('theProcess.defaultOrder');
                                    if (this.getStore()) {
                                        this.getStore().rejectChanges();
                                        this.getStore().clearFilter();
                                        if (allAttrStore.getRange()) {
                                            this.getStore().addFilter(function (item) {
                                                return !allAttrStore.findRecord('attribute', item.get('name'));
                                            });
                                        }
                                    }
                                    return true;
                                }
                            }
                        }
                    },
                    {
                        flex: 1,
                        text: '',
                        xtype: 'widgetcolumn',
                        align: 'left',
                        dataIndex: 'direction',
                        widget: {
                            xtype: 'combobox',
                            itemId: 'defaultOrderDirection',
                            editable: false,
                            displayField: 'label',
                            valueField: 'value',
                            queryMode: 'local',
                            value: '',
                            bind: {
                                value: '{record.direction}',
                                store: '{defaultOrderDirectionsStore}'
                            },
                            listeners: {
                                change: function (combo, newValue, oldValue, eOpts) {
                                    if (!newValue) {
                                        return false;
                                    }
                                }
                            }
                        }
                    },
                    {
                        xtype: 'actioncolumn',
                        minWidth: 150,
                        maxWidth: 150,

                        align: 'center',
                        items: [{
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                            disabled: true
                        }, {
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                            disabled: true
                        }, {
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid'),
                            disabled: true
                        }, {
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
                            handler: 'onAddNewDefaultOrderBtn',
                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                return CMDBuildUI.locales.Locales.administration.common.actions.add;
                            },
                            isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                                if (Ext.String.trim(record.get('attribute')).length) {
                                    return false;
                                } else {
                                    return true;
                                }
                            }
                        }]
                    }
                    ]
                }]
            }]
        }]
    }]
});