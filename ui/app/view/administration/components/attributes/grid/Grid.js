// localized: ok
Ext.define('CMDBuildUI.view.administration.components.attributes.grid.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        // plugins
        'Ext.grid.filters.Filters',
        'CMDBuildUI.components.grid.plugin.FormInRowWidget',
        'CMDBuildUI.view.administration.components.attributes.grid.GridModel',
        'CMDBuildUI.view.administration.components.attributes.grid.GridController'
    ],
    controller: 'administration-components-attributes-grid-grid',
    viewModel: {
        type: 'administration-components-attributes-grid-grid'
    },

    alias: 'widget.administration-components-attributes-grid-grid',
    bind: {
        store: '{allAttributes}',
        selection: '{selected}'
    },

    itemId: 'attributeGrid',

    gridColumns: [
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.name,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
            },
            dataIndex: 'name',
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
            text: CMDBuildUI.locales.Locales.administration.common.labels.type,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.type'
            },
            dataIndex: '_type_description',
            align: 'left'
        },
        {
            dataIndex: 'showInGrid',
            align: 'center',
            xtype: 'checkcolumn',
            renderer: CMDBuildUI.util.administration.helper.RendererHelper.disabledCheckboxColumn,
            bind: {
                text: '{showInGridText}',
                hidden: '{theDomain.cardinality === "N:N"}'
            },
            listeners: {
                beforecheckchange: function () {
                    return false;
                },
                hide: function (column) {
                    const vm = this.lookupViewModel();
                    if (vm.get('theDomain.cardinality') === 'N:N') {
                        this.hideable = false;
                    }
                }
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showinreducedgrid,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showinreducedgrid'
            },
            dataIndex: 'showInReducedGrid',
            xtype: 'checkcolumn',
            renderer: CMDBuildUI.util.administration.helper.RendererHelper.disabledCheckboxColumn,
            listeners: {
                beforecheckchange: function () {
                    return false;
                }
            },
            hidden: true
        },
        {
            text: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.hideingrid,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.hideingrid'
            },
            dataIndex: 'hideInGrid',
            xtype: 'checkcolumn',
            renderer: CMDBuildUI.util.administration.helper.RendererHelper.disabledCheckboxColumn,
            listeners: {
                beforecheckchange: function () {
                    return false;
                }
            },
            hidden: true
        },
        {
            text: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unique,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unique'
            },
            dataIndex: 'unique',
            align: 'center',
            xtype: 'checkcolumn',
            renderer: CMDBuildUI.util.administration.helper.RendererHelper.disabledCheckboxColumn,
            listeners: {
                beforecheckchange: function () {
                    return false;
                }
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.attributes.texts.mandatory,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.mandatory'
            },
            dataIndex: 'mandatory',
            align: 'center',
            xtype: 'checkcolumn',
            renderer: CMDBuildUI.util.administration.helper.RendererHelper.disabledCheckboxColumn,
            listeners: {
                beforecheckchange: function () {
                    return false;
                }
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.active,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
            },
            dataIndex: 'active',
            align: 'center',
            xtype: 'checkcolumn',
            renderer: CMDBuildUI.util.administration.helper.RendererHelper.disabledCheckboxColumn,
            listeners: {
                beforecheckchange: function () {
                    return false;
                }
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.attributes.texts.editingmode,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.editingmode'
            },
            dataIndex: 'mode',
            align: 'left',
            renderer: CMDBuildUI.util.administration.helper.RendererHelper.getAttributeMode
        },
        {
            text: CMDBuildUI.locales.Locales.administration.attributes.texts.grouping,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.grouping'
            },
            dataIndex: '_group_description',
            align: 'left',
            bind: {
                hidden: '{isOtherPropertiesHidden}'
            }
        },
        {
            xtype: 'checkcolumn',
            dataIndex: 'domainKey',
            align: 'center',
            text: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.domainkey,
            renderer: CMDBuildUI.util.administration.helper.RendererHelper.disabledCheckboxColumn,
            hidden: true,
            bind: {
                hidden: '{isDomainKeyHidden}'
            }
        }
    ],
    getGridColumns: function () {
        return this.gridColumns;
    },
    bufferedRenderer: false,
    reserveScrollbar: true,
    listeners: {
        beforeitemdblclick: function (row, record) {
            const formInRow = row.ownerGrid.getPlugin('administration-forminrowwidget');
            formInRow.removeAllExpanded(record);
            row.setSelection(record);
        },
        rowdblclick: function (row, record, element, rowIndex, e, eOpts) {
            const container =
                Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
                Ext.create(CMDBuildUI.view.administration.DetailsWindow);
            const vm = row.grid.getViewModel();
            container.removeAll();
            const action = vm.get('toolAction._canAdd')
                ? CMDBuildUI.util.administration.helper.FormHelper.formActions.edit
                : CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
            container.add({
                xtype:
                    action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                        ? 'administration-components-attributes-actionscontainers-view'
                        : 'administration-components-attributes-actionscontainers-create',
                viewModel: {
                    data: {
                        theAttribute: record,
                        objectTypeName: vm.get('objectTypeName'),
                        objectType: vm.get('objectType'),
                        attributeName: record.get('name'),
                        attributes: row.grid.getStore().getRange(),
                        title: Ext.String.format(
                            '{0} - {1} {2}',
                            record.get('objectType'),
                            CMDBuildUI.locales.Locales.administration.attributes.attribute,
                            record.get('name') ? '- ' + record.get('name') : ''
                        ),
                        grid: Ext.copy(row.grid),
                        action: action,
                        actions: {
                            edit: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                            view: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                            add: false
                        }
                    }
                }
            });
        }
    },

    viewConfig: {
        plugins: [
            {
                ptype: 'gridviewdragdrop',
                dragText: CMDBuildUI.locales.Locales.administration.attributes.strings.draganddrop,
                // TODO: localized not work as expected
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
            widget: {
                xtype: 'administration-components-attributes-actionscontainers-viewinrow',
                ui: 'administration-tabandtools',
                viewModel: {},
                bind: {
                    pluralObjectType: '{pluralObjectType}',
                    theAttribute: '{selected}'
                },
                autoEl: {
                    'data-testid': 'administration-components-attributes-actionscontainers-viewinrow'
                }
            }
        }
    ],

    autoEl: {
        'data-testid': 'administration-components-attributes-grid'
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
            text: CMDBuildUI.locales.Locales.administration.attributes.texts.addattribute,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.addattribute'
            },
            reference: 'addattribute',
            itemId: 'addattribute',
            ui: 'administration-primary-outline-small',

            bind: {
                disabled: '{!toolAction._canAdd}',
                hidden: '{newButtonHidden}'
            }
        },
        {
            xtype: 'localsearchfield',
            gridItemId: '#attributeGrid'
        },
        {
            xtype: 'tbfill'
        },
        {
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.includeinherited,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.includeinherited'
            },
            labelAlign: 'left',
            labelStyle: 'width:auto',
            labelWidth: false,
            value: true,
            hidden: true,
            bind: {
                hidden: '{isSimpleClass}',
                value: '{includeInherited}'
            },
            listeners: {
                change: 'onIncludeInheritedChange'
            }
        }
    ]
});
