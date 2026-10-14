Ext.define('Overrides.view.Table', {
    override: 'Ext.view.Table',
    compatibility: ['6.2.0-6.5.0'],

    /**
     * @override
     * 
     * @param {*} store 
     * @param {*} record 
     * @param {*} operation 
     * @param {*} changedFieldNames 
     * @param {*} info 
     * @param {*} allColumns 
     * @returns 
     */
    handleUpdate: function (store, record, operation, changedFieldNames, info, allColumns) {
        var me = this,
            recordIndex = me.store.indexOf(record),
            rowTpl = me.rowTpl,
            markDirty = me.markDirty,
            dirtyCls = me.dirtyCls,
            columnsToUpdate = [],
            hasVariableRowHeight = me.variableRowHeight,
            updateTypeFlags = 0,
            ownerCt = me.ownerCt,
            cellFly = me.cellFly || (me.self.prototype.cellFly = new Ext.dom.Fly()),
            oldItemDom, oldDataRow, newItemDom, newAttrs, attLen, attName, attrIndex,
            overItemCls, columns, column, len, i, cellUpdateFlag, cell, fieldName, value,
            clearDirty, defaultRenderer, scope, elData, emptyValue;

        operation = operation || Ext.data.Model.EDIT;
        clearDirty = operation !== Ext.data.Model.EDIT;

        if (me.viewReady) {
            // Some features might need to know that we're updating
            me.updatingRows = true;

            // Table row being updated
            oldItemDom = me.getNodeByRecord(record);

            // Row might not be rendered due to buffered rendering
            // or being part of a collapsed group...
            if (oldItemDom) {
                // refreshNode can be called on a collapsed placeholder record.
                // Update it from a new rendering.
                if (record.isCollapsedPlaceholder) {
                    Ext.fly(oldItemDom).syncContent(
                        me.createRowElement(record, me.indexOfRow(record))
                    );

                    return;
                }

                overItemCls = me.overItemCls;
                columns = me.ownerCt.getVisibleColumnManager().getColumns();

                // A refreshNode operation must update all columns, and must do a full rerender.
                // Set the flags appropriately.
                if (allColumns) {
                    columnsToUpdate = columns;
                    updateTypeFlags = 1;
                }
                else {
                    // Collect an array of the columns which must be updated.
                    // If the field at this column index was changed, or column has a custom
                    // renderer (which means value could rely on any other changed field)
                    // we include the column.
                    for (i = 0, len = columns.length; i < len; i++) {
                        column = columns[i];

                        // We are not going to update the cell, but we still need
                        // to mark it as dirty.
                        if (column.preventUpdate) {
                            cell = oldItemDom.querySelector(column.getCellSelector());

                            // Mark the field's dirty status if we are configured to do so
                            // (defaults to true)
                            if (cell && !clearDirty && markDirty) {
                                cellFly.attach(cell);

                                if (record.isModified(column.dataIndex)) {
                                    cellFly.addCls(dirtyCls);

                                    if (column.dirtyTextElementId) {
                                        cell.setAttribute('aria-describedby',
                                            column.dirtyTextElementId);
                                    }
                                }
                                else {
                                    cellFly.removeCls(dirtyCls);
                                    cell.removeAttribute('aria-describedby');
                                }
                            }
                        }
                        else {
                            // 0 = Column doesn't need update.
                            // 1 = Column needs update, and renderer has > 1 argument;
                            // We need to render a whole new HTML item.
                            // 2 = Column needs update, but renderer has 1 argument
                            // or column uses an updater.
                            cellUpdateFlag = me.shouldUpdateCell(record, column, changedFieldNames);

                            if (cellUpdateFlag) {
                                // Track if any of the updating columns yields a flag
                                // with the 1 bit set. This means that there is a custom renderer
                                // involved and a new TableView item will need rendering.
                                updateTypeFlags = updateTypeFlags | cellUpdateFlag;

                                columnsToUpdate[columnsToUpdate.length] = column;
                                hasVariableRowHeight = hasVariableRowHeight ||
                                    column.variableRowHeight;
                            }
                        }
                    }
                }

                // Give CellEditors or other transient in-cell items a chance to get out of the way
                // if there are in the cells destined for update.
                if (me.hasListeners.beforeitemupdate) {
                    me.fireEvent(
                        'beforeitemupdate', record, recordIndex, oldItemDom, columnsToUpdate
                    );
                }

                // If there's no data row (some other rowTpl has been used; eg group header)
                // or we have a getRowClass
                // or one or more columns has a custom renderer
                // or there's more than one <TR>, we must use the full render pathway
                // to create a whole new TableView item
                if (me.getRowClass || !me.getRowFromItem(oldItemDom) ||
                    (updateTypeFlags & 1) || (oldItemDom.tBodies[0].childNodes.length > 1)) {

                    elData = oldItemDom._extData;
                    newItemDom =
                        me.createRowElement(record, me.indexOfRow(record), columnsToUpdate);

                    if (Ext.fly(oldItemDom, '_internal').hasCls(overItemCls)) {
                        Ext.fly(newItemDom).addCls(overItemCls);
                    }

                    // Copy new row attributes across. Use IE-specific method if possible.
                    // In IE10, there is a problem where the className will not get updated
                    // in the view, even though the className on the dom element is correct.
                    // See EXTJSIV-9462
                    if (Ext.isIE9m && oldItemDom.mergeAttributes) {
                        oldItemDom.mergeAttributes(newItemDom, true);
                    }
                    else {
                        newAttrs = newItemDom.attributes;
                        attLen = newAttrs.length;

                        for (attrIndex = 0; attrIndex < attLen; attrIndex++) {
                            attName = newAttrs[attrIndex].name;
                            value = newAttrs[attrIndex].value;

                            if (attName !== 'id' && oldItemDom.getAttribute(attName) !== value) {
                                oldItemDom.setAttribute(attName, value);
                            }
                        }
                    }

                    // The element's data is no longer synchronized. We just overwrite it in the DOM
                    if (elData) {
                        elData.isSynchronized = false;
                    }

                    // If we have columns which may *need* updating (think locked side
                    // of lockable grid with all columns unlocked) and the changed record is within
                    // our view, then update the view.
                    if (columns.length && (oldDataRow = me.getRow(oldItemDom))) {
                        me.updateColumns(
                            oldDataRow, newItemDom.querySelector(me.rowSelector), columnsToUpdate,
                            record
                        );
                    }

                    // Loop thru all of rowTpls asking them to sync the content
                    // they are responsible for if any.
                    while (rowTpl) {
                        var notSync = Ext.isArray(changedFieldNames) ? !Ext.Array.contains(changedFieldNames, "_checkAttachment") : true;
                        if (rowTpl.syncContent && notSync) {
                            // *IF* we are selectively updating columns (have been passed
                            // changedFieldNames), then pass the column set, else pass null,
                            // and it will sync all content.
                            // eslint-disable-next-line max-len
                            if (rowTpl.syncContent(oldItemDom, newItemDom, changedFieldNames ? columnsToUpdate : null) === false) {
                                break;
                            }
                        }

                        rowTpl = rowTpl.nextTpl;
                    }
                }
                // No custom renderers found in columns to be updated,
                // we can simply update the existing cells.
                else {
                    // Loop through columns which need updating.
                    for (i = 0, len = columnsToUpdate.length; i < len; i++) {
                        column = columnsToUpdate[i];

                        // The dataIndex of the column is the field name
                        fieldName = column.dataIndex;

                        value = record.get(fieldName);
                        cell = oldItemDom.querySelector(column.getCellSelector());
                        cellFly.attach(cell);

                        // Mark the field's dirty status if we are configured to do so
                        // (defaults to true)
                        if (!clearDirty && markDirty) {
                            if (record.isModified(column.dataIndex)) {
                                cellFly.addCls(dirtyCls);

                                if (column.dirtyTextElementId) {
                                    cell.setAttribute('aria-describedby',
                                        column.dirtyTextElementId);
                                }
                            }
                            else {
                                cellFly.removeCls(dirtyCls);
                                cell.removeAttribute('aria-describedby');
                            }
                        }

                        defaultRenderer = column.usingDefaultRenderer;
                        scope = defaultRenderer ? column : column.scope;

                        // Call the column updater which gets passed the TD element
                        if (column.updater) {
                            Ext.callback(column.updater, scope,
                                [cell, value, record, me, me.dataSource],
                                0, column, ownerCt
                            );
                        }
                        else {
                            if (column.renderer) {
                                value = Ext.callback(
                                    column.renderer, scope,
                                    [value, null, record, 0, 0, me.dataSource, me],
                                    0, column, ownerCt
                                );
                            }

                            emptyValue = value == null || value.length === 0;
                            value = emptyValue ? column.emptyCellText : value;

                            // Update the value of the cell's inner in the best way.
                            // We only use innerHTML of the cell's inner DIV if the renderer
                            // produces HTML. Otherwise we change the value of the single text node
                            // within the inner DIV. The emptyValue may be HTML,
                            // typically defaults to &#160;
                            if (column.producesHTML || emptyValue) {
                                cell.querySelector(me.innerSelector).innerHTML = value;
                            }
                            else {
                                cell.querySelector(me.innerSelector).childNodes[0].data = value;
                            }
                        }

                        // Add the highlight class if there is one
                        if (me.highlightClass) {
                            Ext.fly(cell).addCls(me.highlightClass);

                            // Start up a DelayedTask which will purge the changedCells stack,
                            // removing the highlight class after the expiration time
                            if (!me.changedCells) {
                                me.self.prototype.changedCells = [];

                                me.prototype.clearChangedTask =
                                    new Ext.util.DelayedTask(me.clearChangedCells, me.prototype);

                                me.clearChangedTask.delay(me.unhighlightDelay);
                            }

                            // Post a changed cell to the stack along with expiration time
                            me.changedCells.push({
                                cell: cell,
                                cls: me.highlightClass,
                                expires: Ext.Date.now() + 1000
                            });
                        }
                    }
                }

                // If we have a commit or a reject, some fields may no longer be dirty but may
                // not appear in the modified field names.
                // Remove all the dirty class here to be sure.
                if (clearDirty && markDirty && !record.dirty) {
                    Ext.fly(oldItemDom, '_internal')
                        .select('.' + dirtyCls)
                        .removeCls(dirtyCls)
                        .set({ 'aria-describedby': undefined });
                }

                // Coalesce any layouts which happen due to any itemupdate handlers
                // (eg Widget columns) with the final refreshSize layout.
                if (hasVariableRowHeight) {
                    Ext.suspendLayouts();
                }

                // Since we don't actually replace the row, we need to fire the event
                // with the old row because it's the thing that is still in the DOM
                if (me.hasListeners.itemupdate) {
                    me.fireEvent('itemupdate', record, recordIndex, oldItemDom, me);
                }

                // We only need to update the layout if any of the columns can change
                // the row height.
                if (hasVariableRowHeight) {
                    // Must climb to ownerGrid in case we've only updated one field
                    // in one side of a lockable assembly. ownerGrid is always the topmost
                    // GridPanel.
                    me.ownerGrid.updateLayout();

                    // Ensure any layouts queued by itemupdate handlers
                    // and/or the refreshSize call are executed.
                    Ext.resumeLayouts(true);
                }
            }

            me.updatingRows = false;
        }
    }

});