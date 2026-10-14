Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.grid.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.grid.GridController',
        'CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.grid.GridModel',

        // plugins
        'Ext.grid.filters.Filters',
        'CMDBuildUI.components.grid.plugin.FormInRowWidget'
    ],

    alias: 'widget.administration-content-dms-dmscategorytypes-tabitems-values-grid-grid',
    controller: 'administration-content-dms-dmscategorytypes-tabitems-values-grid-grid',
    viewModel: {
        type: 'administration-content-dms-dmscategorytypes-tabitems-values-grid-grid'
    },
    bind: {
        store: '{allValues}',
        selection: '{selected}'
    },
    itemId: 'DMSCategoryValuesGrid',
    listeners: {
        rowdblclick: function (row, record, element, rowIndex, e, eOpts) {
            var container =
                Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
                Ext.create(CMDBuildUI.view.administration.DetailsWindow);
            container.removeAll();
            var proxy = CMDBuildUI.model.dms.DMSCategory.getProxy();
            proxy.setUrl(
                CMDBuildUI.util.administration.helper.ApiHelper.server.getDMSCategoryValuesUrl(record.get('_type'))
            );
            // var parentLookupValues = this.up('administration-content-dms-dmscategorytypes-view').getViewModel().getStore('parentDMSCategoriesStore').getData();
            container.add({
                xtype: 'administration-content-dms-dmscategorytypes-tabitems-values-card',
                viewModel: {
                    links: {
                        theValue: {
                            type: 'CMDBuildUI.model.dms.DMSCategory',
                            id: record.getId()
                        }
                    },
                    data: {
                        actions: {
                            edit: true,
                            add: false,
                            view: false
                        },
                        // parentLookupValues: parentLookupValues,
                        lookupTypeName: record.get('_type'),
                        valueId: record.getId(),
                        values: row.grid.getStore().getRange(),
                        title: Ext.String.format(
                            '{0} - {1} - {2}',
                            record.get('_type'),
                            CMDBuildUI.locales.Locales.administration.lookuptypes.strings.values,
                            record.get('name')
                        ),
                        grid: row.grid
                    }
                }
            });
        }
    },
    columns: [
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.code,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.code'
            },
            dataIndex: 'code',
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.description,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
            },
            dataIndex: 'description',
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.dmsmodels.modelclass,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.dmsmodels.modelclass'
            },
            dataIndex: '_modelClass_description',
            renderer: function (value, cell, record, rowIndex, colIndex, store, grid) {
                if (!value) {
                    CMDBuildUI.util.Stores.load('dms.DMSModels', true).then(function (dmsModels) {
                        Ext.Array.forEach(dmsModels, function (dmsModel) {
                            if (record.get('modelClass') === dmsModel.get('name')) {
                                record.set('_modelClass_description', dmsModel.get('description'));
                            }
                        });
                    });
                }
                return value;
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.active,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
            },
            dataIndex: 'active',
            xtype: 'checkcolumn',
            listeners: {
                beforecheckchange: function () {
                    return false;
                }
            }
        }
    ],

    viewConfig: {
        markDirty: false,
        plugins: [
            {
                ptype: 'gridviewdragdrop',
                dragText: CMDBuildUI.locales.Locales.administration.attributes.strings.draganddrop,
                localized: {
                    dragText: 'CMDBuildUI.locales.Locales.administration.attributes.strings.draganddrop'
                },
                containerScroll: true,
                pluginId: 'gridviewdragdrop'
            }
        ]
    },

    plugins: [
        {
            ptype: 'administration-forminrowwidget',
            pluginId: 'administration-forminrowwidget',
            removeWidgetOnCollapse: true,
            widget: {
                xtype: 'administration-content-dms-dmscategorytypes-tabitems-values-card-viewinrow',
                ui: 'administration-tabandtools',
                autoHeight: true,
                bind: {
                    theValue: '{selected}'
                },
                viewModel: {}
            }
        }
    ],

    autoEl: {
        'data-testid': 'administration-content-dms-dmscategorytypes-tabitems-values-grid'
    },

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true,
        selected: null
    },

    forceFit: true,
    loadMask: true,

    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },
    labelWidth: 'auto',
    tbar: [
        {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.administration.lookuptypes.strings.addvalue,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.lookuptypes.strings.addvalue'
            },
            reference: 'addlookupvalue',
            itemId: 'addlookupvalue',
            ui: 'administration-primary-outline-small',

            bind: {
                disabled: '{!canAdd}',
                hidden: '{newButtonHidden}'
            }
        },
        {
            xtype: 'localsearchfield',
            gridItemId: '#DMSCategoryValuesGrid'
        },
        {
            xtype: 'tbfill'
        },
        {
            xtype: 'button',
            align: 'right',
            itemId: 'printBtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sort', 'solid'),
            cls: 'administration-tool',
            ui: 'button-like-tool',
            tooltip: CMDBuildUI.locales.Locales.administration.lookuptypes.sort,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.lookuptypes.sort'
            },
            menu: {
                items: [
                    {
                        text: CMDBuildUI.locales.Locales.administration.lookuptypes.sortbycodeasc,
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.administration.lookuptypes.sortbycodeasc'
                        },
                        listeners: {
                            click: 'onSortMenuItemClick'
                        },
                        sorting: {
                            attribute: 'code',
                            direction: 'ASC'
                        },
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sort-alpha-down', 'solid'),
                        autoEl: {
                            'data-testid': 'administration-content-dms-dmscategorytypes-tabitems-values-sorter-codeasc'
                        }
                    },
                    {
                        text: CMDBuildUI.locales.Locales.administration.lookuptypes.sortbycodedesc,
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.administration.lookuptypes.sortbycodedesc'
                        },
                        listeners: {
                            click: 'onSortMenuItemClick'
                        },
                        sorting: {
                            attribute: 'code',
                            direction: 'DESC'
                        },
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sort-alpha-up', 'solid'),
                        autoEl: {
                            'data-testid': 'administration-content-dms-dmscategorytypes-tabitems-values-sorter-codedesc'
                        }
                    },
                    {
                        xtype: 'menuseparator'
                    },
                    {
                        text: CMDBuildUI.locales.Locales.administration.lookuptypes.sortbydescriptionasc,
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.administration.lookuptypes.sortbydescriptionasc'
                        },
                        listeners: {
                            click: 'onSortMenuItemClick'
                        },
                        sorting: {
                            attribute: 'description',
                            direction: 'ASC'
                        },
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sort-alpha-down', 'solid'),
                        autoEl: {
                            'data-testid':
                                'administration-content-dms-dmscategorytypes-tabitems-values-sorter-descriptionasc'
                        }
                    },
                    {
                        text: CMDBuildUI.locales.Locales.administration.lookuptypes.sortbydescriptiondesc,
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.administration.lookuptypes.sortbydescriptiondesc'
                        },
                        listeners: {
                            click: 'onSortMenuItemClick'
                        },
                        sorting: {
                            attribute: 'description',
                            direction: 'DESC'
                        },
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sort-alpha-up', 'solid'),
                        autoEl: {
                            'data-testid':
                                'administration-content-dms-dmscategorytypes-tabitems-values-sorter-descriptiondesc'
                        }
                    }
                ]
            },

            autoEl: {
                'data-testid': 'administration-content-dms-dmscategorytypes-tabitems-values-sorter'
            }
        }
    ]
});
