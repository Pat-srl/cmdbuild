Ext.define('CMDBuildUI.util.administration.helper.GridHelper', {
    singleton: true,
    /**
     * @private
     */
    privates: {
        /**
         * @cfg {String[]}
         */
        filterOnly: null,
        /**
         * @cfg {String}
         */
        searchTerm: null,
        /**
         *
         * @param {Ext.data.Model} record
         */
        localFilter: function (record) {
            var me = CMDBuildUI.util.administration.helper.GridHelper;
            var data = record.getData();
            var result = false;
            Ext.Array.forEach(Ext.Object.getKeys(data), function (key) {
                if ((me.filterOnly && key.indexOf(me.filterOnly)) > -1 || !me.filterOnly) {
                    if (typeof data[key] !== 'object' && !Ext.isEmpty(String(data[key]))) {
                        if (String(data[key]).toLowerCase().indexOf(me.searchTerm.toLowerCase()) > -1) {
                            result = true;
                        }
                    }
                }
            });

            return result;
        }
    },

    /**
     * Filter grid items.
     * @param {CMDBuildUI.store.Base} store
     * @param {String} searchTerm
     * @param {String[]} filterOnly //Array of fieldnames
     */
    localSearchFilter: function (store, searchTerm, filterOnly) {
        var me = CMDBuildUI.util.administration.helper.GridHelper;
        store.getFilters().remove(me.localFilter);
        me.searchTerm = searchTerm;
        me.filterOnly = filterOnly;
        var filters = store.getFilters();
        filters.add(me.localFilter);
    },
    /**
     *
     * @param {CMDBuildUI.store.Base} store
     */
    removeLocalSearchFilter: function (store) {
        var me = CMDBuildUI.util.administration.helper.GridHelper;
        store.getFilters().remove(me.localFilter);
    },

    getDragAndDropReorderGrid: function (row) {
        return {
            xtype: 'components-grid-reorder-grid',
            bind: {
                store: '{rows}'
            },
            plugins: [],
            viewConfig: {
                markDirty: false,
                variableRowHeight: true,
                getRowClass: function (record, rowIndex, rowParams, store) {
                    return '';
                },
                rowLines: true,
                overItemCls: 'null',
                focusCls: 'null',
                headerBorders: false,
                header: false,
                navigationModel: {}
            },
            selModel: {
                pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
            },
            columns: CMDBuildUI.util.administration.helper.GridHelper.getLayoutDragDropColumns(row),

            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    hidden: true,
                    bind: {
                        hidden: '{actions.view}'
                    },
                    items: [
                        {
                            itemId: 'addrowBtn',
                            text: CMDBuildUI.locales.Locales.administration.forms.addrow,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.forms.addrow'
                            },
                            autoEl: {
                                'data-testid': 'administration-fieldsmanagement-addRowBtn'
                            },
                            ui: 'administration-primary-outline-small'
                        }
                    ]
                }
            ]
        };
    },

    getLayoutDragDropColumns: function (columns) {
        columns.push({
            variableRowHeight: true,
            xtype: 'actioncolumn',
            width: 190,
            bind: {
                hidden: '{actions.view}'
            },
            items: [
                {
                    xtype: 'button',
                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.moveup,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.moveup'
                    },
                    handler: 'moveUp',
                    getClass: function (value, metadata, record, row, col, store) {
                        return Ext.String.format(
                            '{0} administration-fieldsmanagement-{1}-row-{2}',
                            CMDBuildUI.util.helper.IconHelper.getIconId('arrow-up', 'solid'),
                            'moveUpBtn',
                            row
                        );
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                        return rowIndex === 0;
                    }
                },
                {
                    xtype: 'button',
                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.movedown,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.movedown'
                    },
                    handler: 'moveDown',
                    getClass: function (value, metadata, record, row, col, store) {
                        return Ext.String.format(
                            '{0} administration-fieldsmanagement-{1}-row-{2}',
                            CMDBuildUI.util.helper.IconHelper.getIconId('arrow-down', 'solid'),
                            'moveDownBtn',
                            row
                        );
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                        return rowIndex >= view.store.getCount() - 1;
                    }
                },
                {
                    xtype: 'button',
                    tooltip: CMDBuildUI.locales.Locales.administration.forms.addcolumn,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.forms.addcolumn'
                    },
                    handler: 'addColumn',
                    getClass: function (value, metadata, record, row, col, store) {
                        return Ext.String.format(
                            '{0} administration-fieldsmanagement-{1}-row-{2}',
                            CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
                            'addColumnBtn',
                            row
                        );
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                        return record.get('columns').length >= 4;
                    }
                },
                {
                    xtype: 'button',
                    tooltip: CMDBuildUI.locales.Locales.administration.forms.removecolumn,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.forms.removecolumn'
                    },
                    handler: 'removeColumn',
                    getClass: function (value, metadata, record, row, col, store) {
                        return Ext.String.format(
                            '{0} administration-fieldsmanagement-{1}-row-{2}',
                            CMDBuildUI.util.helper.IconHelper.getIconId('minus', 'solid'),
                            'removeColumnBtn',
                            row
                        );
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                        return record.get('columns').length <= 1;
                    }
                },
                {
                    xtype: 'button',
                    itemId: 'columnsWidthBtn',
                    cls: 'administration-tool',
                    tooltip: CMDBuildUI.locales.Locales.administration.forms.columnssize,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.forms.columnssize'
                    },
                    getClass: function (value, metadata, record, row, col, store) {
                        return Ext.String.format(
                            '{0} administration-fieldsmanagement-{1}-row-{2}',
                            CMDBuildUI.util.helper.IconHelper.getIconId('columns', 'solid'),
                            'columnsSizeBtn',
                            row
                        );
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                        return record.get('columns').length <= 1 || record.get('columns').length >= 4;
                    },
                    handler: function (view, rowIndex, colIndex, item, e, record, row) {
                        var ctrl = view.lookupController();
                        var currentSize = record.get('columns').length;
                        Ext.create('Ext.menu.Menu', {
                            record: record,
                            ctrl: ctrl,
                            listeners: {
                                hide: function (menu, eOpts) {
                                    Ext.asap(function () {
                                        menu.destroy();
                                    });
                                }
                            },
                            items: [
                                // 1 column
                                {
                                    text: '100%',
                                    listeners: {
                                        click: ctrl.columnsSizeBtnClick
                                    },
                                    hidden: true,
                                    columnsLength: 1,
                                    value: [1],
                                    cls: 'menu-item-nospace'
                                },
                                // 2 columns
                                {
                                    text: '50%, 50%',
                                    listeners: {
                                        click: ctrl.columnsSizeBtnClick
                                    },
                                    hidden: currentSize !== 2,
                                    columnsLength: 2,
                                    value: [0.5, 0.5],
                                    cls: 'menu-item-nospace'
                                },
                                {
                                    text: '25%, 75%',
                                    listeners: {
                                        click: ctrl.columnsSizeBtnClick
                                    },
                                    hidden: currentSize !== 2,
                                    columnsLength: 2,
                                    value: [0.25, 0.75],
                                    cls: 'menu-item-nospace'
                                },
                                {
                                    text: '75%, 25%',
                                    listeners: {
                                        click: ctrl.columnsSizeBtnClick
                                    },
                                    hidden: currentSize !== 2,
                                    columnsLength: 2,
                                    value: [0.75, 0.25],
                                    cls: 'menu-item-nospace'
                                },
                                // 3 columns
                                {
                                    text: '33%, 33%, 33%',
                                    listeners: {
                                        click: ctrl.columnsSizeBtnClick
                                    },
                                    hidden: currentSize !== 3,
                                    columnsLength: 3,
                                    value: [0.33, 0.33, 0.33],
                                    cls: 'menu-item-nospace'
                                },
                                {
                                    text: '50%, 25%, 25%',
                                    listeners: {
                                        click: ctrl.columnsSizeBtnClick
                                    },
                                    hidden: currentSize !== 3,
                                    columnsLength: 3,
                                    value: [0.5, 0.25, 0.25],
                                    cls: 'menu-item-nospace'
                                },
                                {
                                    text: '25%, 50%, 25%',
                                    listeners: {
                                        click: ctrl.columnsSizeBtnClick
                                    },
                                    hidden: currentSize !== 3,
                                    columnsLength: 3,
                                    value: [0.25, 0.5, 0.25],
                                    cls: 'menu-item-nospace'
                                },
                                {
                                    text: '25%, 25%, 50%',
                                    listeners: {
                                        click: ctrl.columnsSizeBtnClick
                                    },
                                    hidden: currentSize !== 3,
                                    columnsLength: 3,
                                    value: [0.25, 0.25, 0.5],
                                    cls: 'menu-item-nospace'
                                },
                                // 4 columns
                                {
                                    text: '25%, 25%, 25%, 25%',
                                    listeners: {
                                        click: ctrl.columnsSizeBtnClick
                                    },
                                    hidden: true,
                                    columnsLength: 4,
                                    value: [0.25, 0.25, 0.25, 0.25],
                                    cls: 'menu-item-nospace'
                                }
                            ]
                        }).showAt(e.getXY());
                        return false;
                    }
                },
                {
                    xtype: 'button',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.delete,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.delete'
                    },
                    autoEl: {
                        'data-testid': 'administration-fieldsmanagement-deleteRowBtn'
                    },
                    handler: 'deleteRow'
                }
            ]
        });
        return columns;
    },

    getGridFilterPlugin: function (model) {
        return 'gridfilters';
    },

    getViewInRowPlugins: function (model) {
        model = typeof model === 'string' ? eval(model) : model;
        var me = CMDBuildUI.util.administration.helper.GridHelper,
            config = {
                ifBuffered: model.isBuffered || false,
                widgetType: model.viewInRowWidgetType || 'forminrowwidget',
                vmObjectName: model.vmObjectName || 'theObject',
                pluralObjectTypeName: model.pluralObjectTypeName || 'modules'
            };

        return {
            ptype: config.widgetType,
            pluginId: config.widgetType,
            scrollIntoViewOnExpand: true,
            removeWidgetOnCollapse: true,
            widget: me.getViewInRowWidget(config.vmObjectName, config.pluralObjectTypeName)
        };
    },

    /**
     * get viewinrow widget
     *
     * @param {String} mainVmObject
     * @param {String} pluralModule
     *
     * @returns {Object}
     */
    getViewInRowWidget: function (mainVmObject, pluralModule) {
        var widget = {
            xtype: Ext.String.format('administration-content-{0}-card-viewinrow', pluralModule),
            ui: 'administration-tabandtools',
            viewModel: {
                data: {
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                }
            },
            bind: {}
        };
        widget.bind[mainVmObject] = '{selected}';
        return widget;
    },

    getColumns: function (model, reduced) {
        var columns = [];
        if (!model) {
            return columns;
        }
        if (typeof model === 'string') {
            try {
                model = eval(model);
            } catch (error) {
                CMDBuildUI.util.Logger.log('unable to eval the model', CMDBuildUI.util.Logger.levels.debug);
            }
        }
        Ext.Array.forEach(model.getFields(), function (field) {
            var showInGrid = (!reduced && field.showInGrid) || (reduced && field.showInReducedGrid);
            if (showInGrid) {
                var column;
                // try {
                //     column = {
                //         text: eval(field.description),
                //         localized: {
                //             text: field.description
                //         },
                //         dataIndex: field.name,
                //         align: field.align || 'left'
                //     };
                // } catch (error) {
                column = {
                    text: field.description,
                    localized: {
                        text: field.localized.description
                    },
                    dataIndex: field.name,
                    align: field.align || 'left'
                };
                // }

                switch (field.type) {
                    case 'boolean':
                        column.xtype = 'checkcolumn';
                        column.disabled = true;
                        column.disabledCls = '';
                        break;

                    default:
                        break;
                }
                if (field.renderer) {
                    column.renderer = field.renderer;
                }
                columns.push(column);
            }
        });
        return columns;
    },

    gridReload: function (ctx, record) {
        var view = ctx.getView();
        var store = view.getStore();
        if (record) {
            var newid = record.getId();
            if (store.isBufferedStore) {
                // update extra params to get new card position
                var extraparams = store.getProxy().getExtraParams();
                extraparams.positionOf = newid;
                extraparams.positionOf_goToPage = false;
                // add event listener. Use event listener instaed of callback
                // otherwise the load listener used within afterLoadWithPosition
                // is called at first load.
                store.on({
                    load: {
                        fn: function () {
                            Ext.asap(function () {
                                CMDBuildUI.util.administration.helper.GridHelper.afterLoadWithPosition(
                                    ctx,
                                    store,
                                    newid
                                );
                            });
                        },
                        scope: this,
                        single: true
                    }
                });
            } else {
                CMDBuildUI.util.administration.helper.GridHelper.afterLoadWithPosition(ctx, store, newid);
            }
        }

        // load store
        store.load();
    },
    /**
     *
     * @param {Ext.data.Store} store
     * @param {Numeric} newid
     */
    afterLoadWithPosition: function (ctx, store, newid) {
        var view = ctx.getView();
        var vm = view.lookupViewModel();
        var record;
        // function to expand row
        function expandRow() {
            view.expandRowAfterLoadWithPosition(store, newid);
            view.ensureVisible(store.findRecord(store.model.idProperty, newid));
            var extraparams = store.getProxy().getExtraParams();
            delete extraparams.positionOf;
            delete extraparams.positionOf_goToPage;
        }

        if (store.isBufferedStore) {
            // check if item is found with filers
            try {
                var metadata = store.getProxy().getReader().metaData;
                if (metadata.positions[newid] && metadata.positions[newid].found) {
                    expandRow();
                } else if (
                    !CMDBuildUI.util.helper.Configurations.get(
                        CMDBuildUI.model.Configuration.common.keepfilteronupdatedcard
                    ) &&
                    !store.getAdvancedFilter().isEmpty()
                ) {
                    var advancedFitler = store.getAdvancedFilter();
                    // clear search query
                    vm.set('search.value', '');
                    advancedFitler.clearQueryFilter();
                    // clear attributes and relations filter
                    var filterslauncher = view.up().lookupReference('filterslauncher');
                    if (filterslauncher) {
                        filterslauncher.clearFilter(true);
                    }
                    // show message to user
                    Ext.asap(function () {
                        CMDBuildUI.util.Notifier.showInfoMessage(CMDBuildUI.locales.Locales.common.grid.filterremoved);
                    });
                    // load store
                    store.on({
                        load: {
                            fn: function () {
                                var meta = store.getProxy().getReader().metaData;
                                if (meta.positions[newid] && meta.positions[newid].found) {
                                    expandRow();
                                } else {
                                    // show not found message to user
                                    Ext.asap(function () {
                                        CMDBuildUI.util.Notifier.showWarningMessage(
                                            CMDBuildUI.locales.Locales.common.grid.itemnotfound
                                        );
                                    });
                                }
                            },
                            scope: this,
                            single: true
                        }
                    });
                    store.load();
                } else {
                    // show not found message to user
                    Ext.asap(function () {
                        CMDBuildUI.util.Notifier.showWarningMessage(
                            CMDBuildUI.locales.Locales.common.grid.itemnotfound
                        );
                    });
                }
            } catch (error) {
                CMDBuildUI.util.Logger.log(
                    'positionOf is not handled in current endpoint',
                    CMDBuildUI.util.Logger.levels.error
                );
                CMDBuildUI.util.Logger.log(
                    'try to expand the record if is present in store',
                    CMDBuildUI.util.Logger.levels.error
                );
                expandRow();
                // record = store.findRecord(store.model.idProperty, newid);
                // view.getPlugin(view.getFormInRowPlugin()).view.fireEventArgs('itemcreated', [view, record, ctx]);
            }
        } else {
            record = store.findRecord(store.model.idProperty, newid);
            if (record) {
                view.getPlugin(view.getFormInRowPlugin()).view.fireEventArgs('itemcreated', [view, record, ctx]);
            }
        }
    }
});
