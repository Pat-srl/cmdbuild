Ext.define('CMDBuildUI.view.joinviews.configuration.items.DataSorting', {
    extend: 'Ext.form.FieldSet',

    requires: [
        'CMDBuildUI.view.joinviews.configuration.items.DataSortingController',
        'CMDBuildUI.view.joinviews.configuration.items.DataSortingModel'
    ],
    alias: 'widget.joinviews-configuration-items-datasorting',
    controller: 'joinviews-configuration-items-datasorting',
    viewModel: {
        type: 'joinviews-configuration-items-datasorting'
    },

    title: CMDBuildUI.locales.Locales.joinviews.datasorting,
    localized: {
        title: 'CMDBuildUI.locales.Locales.joinviews.datasorting'
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    bind: {
        ui: '{fieldsetUi}'
    },

    layout: {
        type: 'vbox',
        align: 'stretch',
        vertical: true
    },

    items: [{
        columnWidth: 0.75,
        items: [{
            xtype: 'components-grid-reorder-grid',
            itemId: 'defaultOrderGrid',
            bind: {
                store: '{theView.sorter}'
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
                dataIndex: 'property',
                align: 'left',
                renderer: function (value) {
                    var allAttriubutesArray = this.lookupViewModel().get('selectablesAtributesData');
                    if (!Ext.isEmpty(allAttriubutesArray)) {
                        var attribute = Ext.Array.findBy(allAttriubutesArray, function (item) {
                            return item.get('name') === value;
                        });
                        if (attribute) {
                            return attribute.get('expr');
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
                        var record = store.findRecord('value', value, 0, false, true);
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
                        }
                    }
                }
            }, {
                xtype: 'actioncolumn',
                minWidth: 150,
                maxWidth: 150,
                bind: {
                    hidden: '{actions.view}'
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
                    getTip: function (v, metadata, record, rowindex, colindex, store) {
                        return CMDBuildUI.locales.Locales.administration.common.actions.moveup;
                    },
                    handler: 'moveUp',
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
                    getTip: function (v, metadata, record, rowindex, colindex, store) {
                        return CMDBuildUI.locales.Locales.administration.common.actions.movedown;
                    },
                    handler: 'moveDown',
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
                    getTip: function (v, metadata, record, rowindex, colindex, store) {
                        return CMDBuildUI.locales.Locales.administration.common.actions.remove;
                    },
                    handler: 'deleteRow',
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
                    hidden: '{actions.view}'
                },
                viewConfig: {
                    markDirty: false
                },
                columns: [{
                    flex: 2,
                    text: '',
                    xtype: 'widgetcolumn',
                    align: 'left',
                    dataIndex: 'property',
                    widget: {
                        xtype: 'groupedcombo',
                        queryMode: 'local',
                        typeAhead: true,
                        itemId: 'defaultOrderAttribute',
                        displayField: '_attributeDescription',
                        valueField: 'name',
                        forceSelection: true,
                        value: '',
                        bind: {
                            value: '{record.property}',
                            store: '{selectablesAttributes}'
                        },
                        listeners: {
                            beforequery: function (queryEv) {
                                var allAttrStore = this.lookupViewModel().get('theView.sorter');
                                if (this.getStore()) {
                                    this.getStore().clearFilter();
                                    if (allAttrStore.getRange()) {
                                        this.getStore().addFilter(function (item) {
                                            return !allAttrStore.findRecord('property', item.get('name'), 0, false, true);
                                        });
                                    }
                                }
                                return true;
                            }
                        }
                    }
                }, {
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
                }, {
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
                        isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                            if (Ext.String.trim(record.get('property')).length) {
                                return false;
                            } else {
                                return true;
                            }
                        },
                        getTip: function (v, metadata, record, rowindex, colindex, store) {
                            return CMDBuildUI.locales.Locales.administration.common.actions.add;
                        }
                    }]
                }]
            }]
        }]
    }]
});