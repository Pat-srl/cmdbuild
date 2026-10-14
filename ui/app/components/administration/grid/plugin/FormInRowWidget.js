Ext.define('CMDBuildUI.components.administration.grid.plugin.FormInRowWidget', {
    extend: 'Ext.grid.plugin.RowWidget',
    alias: 'plugin.administration-forminrowwidget',

    config: {
        ui: 'default',

        /**
         * @cfg {Boolean} removeWidgetOnCollapse
         * Default to `false`. When `true` the widget will be removed on
         * row collapse.
         */
        removeWidgetOnCollapse: true,
        wasDblClick: undefined
    },

    rowBodyDivCls: 'forminrowwidget',

    /**
     * @override
     *
     * @param {} view
     */
    bindView: function (view) {
        const me = this;
        const listeners = {
            itemclick: me.onItemClick,
            beforeitemdblclick: me.onBeforeItemDblClick,
            togglerow: me.onToggleRow,
            itemupdated: me.onItemUpdated,
            itemremoved: me.onItemRemoved,
            itemcreated: me.onItemCreated,
            scope: me,
            resize: me.onResize
        };
        view.on(listeners);

        this.callParent(arguments);
    },

    /**
     *
     * @param {*} view
     * @param {*} record
     * @param {*} rowIdx
     * @param {*} e
     */
    onToggleRow: function (view, record, rowIdx, e) {
        if (typeof this.getWasDblClick('undefined')) {
            if (view && !view.getSelectionModel().excludeToggleOnColumn) {
                this.toggleRow(rowIdx, record);
            }
        } else if (!this.getWasDblClick()) {
            this.setWasDblClick(undefined);
        } else {
            this.setWasDblClick(false);
        }
    },

    /**
     *
     * @param {*} view
     * @param {*} record
     * @param {*} row
     * @param {*} rowIdx
     * @param {*} e
     */
    onItemClick: function (view, record, row, rowIdx, e) {
        const me = this;
        // We need to delay this activity to allow the user to edit the item when a doubleclick occurs
        new Ext.util.DelayedTask(function () {}).delay(
            200,
            function (view, record, row, rowIdx, e) {
                if (typeof this.getWasDblClick('undefined')) {
                    if (
                        view.up('grid').getSelection().length &&
                        view.up('grid').getSelection()[0].get('_id') !== record.get('_id')
                    ) {
                        view.up('grid').setSelection(record);
                    }
                    if (
                        (view && !view.getSelectionModel().excludeToggleOnColumn) ||
                        (e &&
                            !Ext.Array.contains(
                                Ext.Array.from(view.getSelectionModel().excludeToggleOnColumn),
                                e.position.colIdx
                            ))
                    ) {
                        me.toggleRow(rowIdx, record);
                    }
                } else if (!this.getWasDblClick()) {
                    this.setWasDblClick(undefined);
                } else {
                    this.setWasDblClick(false);
                }
            },
            me,
            arguments
        );
    },

    /**
     *
     */
    onBeforeItemDblClick: function () {
        this.setWasDblClick(true);
    },

    /**
     *
     * @param {*} view
     * @param {*} record
     * @returns
     */
    isRecordNodeExpanded: function (view, record) {
        const me = this;
        try {
            const nodeId = view.getStore().getById(record.get('_id'));
            if (!nodeId) {
                return false;
            }
            const rowNode = view.getNodeByRecord(nodeId);
            if (!rowNode) {
                return false;
            }
            const normalRow = Ext.fly(rowNode);
            return !normalRow.hasCls(me.rowCollapsedCls);
        } catch (e) {
            CMDBuildUI.util.Logger.log('isRecordNodeExpanded node node find error', CMDBuildUI.util.Logger.levels.info);
            return false;
        }
    },

    /**
     *
     * @returns
     */
    getHeaderConfig: function () {
        const me = this;
        const lockable = me.grid.lockable && me.grid;

        return {
            width: me.headerWidth,
            ignoreExport: true,
            lockable: false,
            autoLock: true,
            sortable: false,
            resizable: false,
            draggable: false,
            hideable: false,
            menuDisabled: true,
            selectable: true,
            tdCls: Ext.baseCSSPrefix + 'grid-cell-special',
            innerCls: Ext.baseCSSPrefix + 'grid-cell-inner-row-expander',
            renderer: function () {
                return (
                    '<div class="' + Ext.baseCSSPrefix + 'grid-row-expander" role="presentation" tabIndex="0"></div>'
                );
            },

            // This column always migrates to the locked side if the locked side is visible.
            // It has to report this correctly so that editors can position things correctly
            isLocked: function () {
                return lockable && (lockable.lockedGrid.isVisible() || this.locked);
            },

            // In an editor, this shows nothing.
            editRenderer: function () {
                return '&#160;';
            }
        };
    },

    /**
     * Event for create grid and form in row
     * @param {Ext.grid.Panel} grid
     * @param {Ext.data.Model} record
     */
    onItemCreated: function (grid, record, scope) {
        grid = !grid ? this.grid : grid;
        this.gridReload(grid, scope, function () {
            const store = grid.getStore();
            const storeItem = store.getById(record.getId());
            // NOTE: 17.04.19 F.B. no expand the new row
            // this.view.fireEventArgs('togglerow', [null, _storeItem, _index]);
            try {
                grid.ensureVisible(storeItem);
                grid.setSelection(storeItem);
            } catch (error) {}
        });
    },

    /**
     * Event for remove grid and form in row
     * @param {Ext.grid.Panel} grid
     * @param {Ext.data.Model} record
     */
    onItemRemoved: function (grid, record, scope) {
        const store = grid.getStore();
        const index = store.findExact('_id', record.getId());
        const storeItem = store.getById(record.getId());
        this.view.fireEventArgs('togglerow', [null, storeItem, index]);
        this.gridReload(grid, scope, function () {
            grid.getView().refresh();
        });
    },
    /**
     * Event for update grid and form in row
     * @param {Ext.grid.Panel} grid
     * @param {Ext.data.Model} record
     * @param {Object} scope
     * @param {Boolean} ignoreGridParams
     */
    onItemUpdated: function (grid, record, scope, ignoreGridParams) {
        const me = this;
        const store = grid.getStore();
        const wasExpanded = me.isRecordNodeExpanded(grid.down('tableview'), record);
        let index = store.findExact('_id', record.getId());
        if (index === -1) {
            index = store.findExact('_id', parseInt(record.getId()));
        }
        const storeItem = store.getAt(index);
        if (wasExpanded) {
            me.toggleRow(index, storeItem);
        }
        this.gridReload(grid, scope, function () {
            let index = store.findExact('_id', record.getId());
            if (index === -1) {
                index = store.findExact('_id', parseInt(record.getId()));
            }
            const storeItem = store.getAt(index);
            if (storeItem) {
                grid.setSelection(storeItem);
                me.grid.ensureVisible(storeItem);
                if (wasExpanded) {
                    Ext.asap(function () {
                        me.toggleRow(index, storeItem);
                    });
                }
            }
        });
    },

    privates: {
        /**
         *
         * @param {*} view
         * @param {Integer} width
         * @param {Integer} height
         */
        onResize: function (view, width, height) {
            const me = this;
            const record = view.lookupViewModel().get('selected');
            if (record && me.grid && me.grid.liveRowContexts && me.grid.liveRowContexts[record.internalId]) {
                const widget = me.getWidget(view, record);
                const target = Ext.fly(view.getNode(record)).down(me.rowBodyFeature.innerSelector);
                newWidth = target.getWidth(true) - target.getPadding('lr');
                widget.setWidth(newWidth);
                widget.updateLayout();
            }
            view.updateLayout();
        },

        /**
         * @private
         *
         * @param {*} view
         * @param {*} scope
         * @param {*} cb
         */
        gridReload: function (view, scope, cb) {
            view = !view ? this.grid : view;
            let store = view ? view.getStore() : this.grid.getStore();
            if (store.type === 'chained') {
                store = store.source;
            }
            store.reload({
                callback: function (records, operation, success) {
                    view.getView().refresh();
                    cb();
                },
                scope: scope
            });
        },

        /**
         * @private
         * @override
         *
         */
        addWidget: function (view, record) {
            const me = this;
            let target, width, widget;
            let hasAttach = !!me.onWidgetAttach;
            let isFixedSize = me.isFixedSize;
            let el;
            // If the record is non data (placeholder), or not expanded, return
            if (record.isNonData || !me.recordsExpanded[record.internalId]) {
                return;
            }

            target = Ext.fly(view.getNode(record).querySelector(me.rowBodyFeature.innerSelector));
            width = target.getWidth(true) - target.getPadding('lr');
            widget = me.getWidget(view, record);
            // Might be no widget if we are handling a lockable grid
            // and only one side has a widget definition.
            if (widget) {
                // Bind widget to record unless it has declared a binding
                if (hasAttach) {
                    Ext.callback(me.onWidgetAttach, me.scope, [me, widget, record], 0, me);
                }

                // double check if element dom exist
                // sencha version: el = widget.el || widget.element.dom
                el =
                    widget.el && widget.el.dom
                        ? widget.el
                        : widget.element && widget.element.dom
                        ? widget.element
                        : undefined;

                if (el) {
                    target.dom.appendChild(el.dom);

                    if (!isFixedSize && widget.width !== width) {
                        widget.setWidth(width);
                    } else {
                        widget.updateLayout();
                    }

                    widget.reattachToBody();
                } else {
                    if (!isFixedSize) {
                        widget.width = width;
                    }
                    widget.render(target);
                }

                view.down(me.widget.xtype).fireEventArgs('itemupdated', [view, record]);
                widget.setWidth('100%');
                widget.updateLayout();
                Ext.asap(function () {
                    // HACK: Update layout after the data is inserted
                    widget.updateLayout();
                });
            }

            return widget;
        },

        /**
         * @private
         * @override
         *
         * @param {*} rowIdx
         * @param {*} record
         * @returns
         */
        toggleRow: function (rowIdx, record) {
            // if server fail to get the data and record is udefined
            if (!record) {
                return;
            }
            rowIdx = rowIdx === -1 && record.get('index') ? record.get('index') : rowIdx;

            const me = this;
            // If we are handling a lockable assembly,
            // handle the normal view first
            let view = me.normalView || me.view;

            const rowNode = view.getNodeByRecord(record);
            if (!rowNode) {
                me.recordsExpanded = {};
                return true;
            }
            const normalRow = Ext.fly(rowNode);
            const wasCollapsed = normalRow.hasCls(me.rowCollapsedCls);
            const addOrRemoveCls = wasCollapsed ? 'removeCls' : 'addCls';
            const ownerLockable = me.grid.lockable && me.grid;
            const selmode = me.grid.getSelectionModel().getSelectionMode();
            const isMultiSelect = selmode === 'MULTI';
            let lockedRow, vm;
            let nextBd = normalRow.down(me.rowBodyTrSelector, true);
            let widget = me.getWidget(view, record);

            normalRow[addOrRemoveCls](me.rowCollapsedCls);
            Ext.fly(nextBd)[addOrRemoveCls](me.rowBodyHiddenCls);

            // All layouts must be coalesced.
            // Particularly important for locking assemblies which need
            // to sync row height on the next layout.
            Ext.suspendLayouts();

            // We're expanding
            if (wasCollapsed) {
                // close all expanded records
                me.removeFormInRow(view, record, isMultiSelect, widget);
                widget = me.addFormInrow(view, record, isMultiSelect);
                vm = widget.lookupViewModel();
                // widget.mask();
            } else {
                widget = me.removeFormInRow(view, record, isMultiSelect, widget);
            }
            // Sync the collapsed/hidden classes on the locked side
            if (ownerLockable) {
                // Only attempt to toggle lockable side if it is visible.
                if (ownerLockable.lockedGrid.isVisible()) {
                    view = me.lockedView;

                    // Process the locked side.
                    lockedRow = Ext.fly(view.getNode(rowIdx));
                    // Just because the grid is locked, doesn't mean we'll necessarily have a locked row.
                    if (lockedRow) {
                        lockedRow[addOrRemoveCls](me.rowCollapsedCls);

                        // If there is a template for expander content in the locked side, toggle that side too
                        nextBd = lockedRow.down(me.rowBodyTrSelector, true);
                        Ext.fly(nextBd)[addOrRemoveCls](me.rowBodyHiddenCls);

                        // Pass an array if we're in a lockable assembly.
                        if (wasCollapsed && me.lockedWidget) {
                            widget = [widget, me.addWidget(view, record)];
                        } else {
                            widget = [widget, me.getWidget(view, record)];
                        }
                    }

                    // We're going to need a layout run to synchronize row heights
                    ownerLockable.syncRowHeightOnNextLayout = true;
                }
            }

            if (wasCollapsed) {
                if (me.scrollIntoViewOnExpand) {
                    // We need to delay this activity to allow the user to edit the item when a doubleclick occurs
                    // new Ext.util.DelayedTask(function (_grid, _rowIdx, _widget) {
                    me.view.fireEvent('expandbody', rowNode, record, nextBd, widget);
                    me.grid.ensureVisible(rowIdx);
                    if (widget && widget.isMasked()) {
                        widget.unmask();
                    }
                }
            }

            // Before layouts are resumed, if we have *expanded* the widget row,
            // then ensure bound data is flushed into the widget so that it assumes its final size.
            if (vm) {
                vm.notify();
            }

            Ext.resumeLayouts(true);
        }
    },

    /**
     *
     * @param {*} view
     * @param {*} record
     * @param {*} isMultiSelect
     * @param {*} widget
     * @returns
     */
    removeFormInRow: function (view, record, isMultiSelect, widget) {
        Ext.suspendLayouts();
        const me = this;
        const rowNode = view.getNodeByRecord(record);
        const normalRow = Ext.fly(rowNode);
        const nextBd = normalRow.down(me.rowBodyTrSelector, true);

        delete me.recordsExpanded[record.internalId];
        me.view.fireEvent('collapsebody', rowNode, record, nextBd, widget);
        if (widget && me.getRemoveWidgetOnCollapse()) {
            delete widget._rowContext.widgets[me.getId() + '-' + view.getId()];
            widget.updateLayout();
            widget.destroy();
        }

        // clear selection in grid
        if (isMultiSelect) {
            view.setSelection(null);
        }
        me.grid.ensureVisible(me.grid.getStore().findExact('_id', record.getId()));
        Ext.resumeLayouts();
        return widget;
    },

    /**
     *
     * @param {*} view
     * @param {*} record
     * @param {*} isMultiSelect
     * @param {*} widget
     * @returns
     */
    addFormInrow: function (view, record, isMultiSelect, widget) {
        const me = this;
        me.removeAllExpanded();
        me.recordsExpanded[record.internalId] = record;
        widget = me.addWidget(view, record);

        // set selection in grid
        if (isMultiSelect) {
            view.setSelection(record);
        }
        return widget;
    },

    /**
     *
     * @param {*} exceptRecord
     */
    removeAllExpanded: function (exceptRecord) {
        const me = this;
        Ext.Array.each(Object.keys(me.recordsExpanded), function (key, index) {
            if (!exceptRecord || exceptRecord.get('_id') !== me.recordsExpanded[key].get('_id')) {
                me.toggleRow(me.recordsExpanded[key].rowIdx, me.recordsExpanded[key]);
            }
        });
    }
});
