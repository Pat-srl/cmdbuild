Ext.define('Overrides.selection.TreeModel', {
    override: 'Ext.selection.TreeModel',

    // Private
    // Called in response to a FocusModel's navigate event when a new record has been navigated to.
    // Event is passed so that shift and ctrl can be handled.
    onNavigate: function (e) {
        // Enforce the ignoreRightMouseSelection setting.
        // Enforce presence of a record.
        // Enforce selection upon click, not mousedown.
        if (!e.record || this.vetoSelection(e.keyEvent) || e.record._childrenloaded instanceof Ext.Deferred) {
            return;
        }

        this.onBeforeNavigate(e);

        // eslint-disable-next-line vars-on-top
        var me = this,
            keyEvent = e.keyEvent,
            // ctrlKey may be set on the event if we want to treat it like a ctrlKey so
            // we don't mutate the original event object
            ctrlKey = keyEvent.ctrlKey || e.ctrlKey,
            recIdx = e.recordIndex,
            record = e.record,
            lastFocused = e.previousRecord,
            isSelected = me.isSelected(record),
            from = (me.selectionStart && me.isSelected(e.previousRecord))
                ? me.selectionStart
                : (me.selectionStart = e.previousRecord),
            fromIdx = e.previousRecordIndex,
            key = keyEvent.getCharCode(),
            isSpace = key === keyEvent.SPACE,
            changedRec = e.record !== e.previousRecord,
            direction;

        direction =
            key === keyEvent.UP || key === keyEvent.PAGE_UP || key === keyEvent.HOME ||
                (key === keyEvent.LEFT && changedRec)
                ? 'up'
                : (key === keyEvent.DOWN || key === keyEvent.PAGE_DOWN ||
                    key === keyEvent.END || (key === keyEvent.RIGHT && changedRec)
                    ? 'down'
                    : null);

        switch (me.selectionMode) {
            case 'MULTI':
                me.setSelectionStart(e.selectionStart);

                if (key === keyEvent.A && ctrlKey) {
                    // Listening to endUpdate on the Collection will be more efficient
                    me.selected.beginUpdate();
                    me.selectRange(0, me.store.getCount() - 1);
                    me.selected.endUpdate();
                }
                else if (isSpace) {
                    // SHIFT+SPACE, select range
                    if (keyEvent.shiftKey) {
                        me.selectRange(from, record, ctrlKey);
                    }
                    else {
                        // SPACE pressed on a selected item: deselect.
                        if (isSelected) {
                            if (me.allowDeselect) {
                                me.doDeselect(record);
                            }
                        }
                        // SPACE on an unselected item: select it
                        // keyEvent.ctrlKey means "keep existing"
                        else {
                            me.doSelect(record, ctrlKey);
                        }
                    }
                }

                // SHIFT-navigate selects intervening rows from the last selected
                // (or last focused) item and target item
                else if (keyEvent.shiftKey && from) {
                    // If we are heading back TOWARDS the start rec - deselect skipped range...
                    if (direction === 'up' && fromIdx <= recIdx) {
                        me.deselectRange(lastFocused, recIdx + 1);
                    }
                    else if (direction === 'down' && fromIdx >= recIdx) {
                        me.deselectRange(lastFocused, recIdx - 1);
                    }

                    // If we are heading AWAY from start point, or no CTRL key,
                    // so just select the range and let the CTRL control "keepExisting"...
                    else if (from !== record) {
                        me.selectRange(from, record, ctrlKey);
                    }

                    me.lastSelected = record;
                }

                else if (key) {
                    if (!ctrlKey) {
                        me.doSelect(record, false);
                    }
                }
                else {
                    me.selectWithEvent(record, keyEvent);
                }

                break;

            case 'SIMPLE':
                if (key === keyEvent.A && ctrlKey) {
                    // Listening to endUpdate on the Collection will be more efficient
                    me.selected.beginUpdate();
                    me.selectRange(0, me.store.getCount() - 1);
                    me.selected.endUpdate();
                }
                else if (isSelected) {
                    me.doDeselect(record);
                }
                else {
                    me.doSelect(record, true);
                }

                break;

            case 'SINGLE':
                // CTRL-navigation does not select
                if (!ctrlKey) {
                    // Arrow movement
                    if (direction) {
                        me.doSelect(record, false);
                    }
                    // Space or click
                    else if (isSpace || !key) {
                        me.selectWithEvent(record, keyEvent);
                    }
                }

                break;
        }

        // selectionStart is a start point for shift/mousedown to create a range from.
        // If the mousedowned record was not already selected, then it becomes the
        // start of any range created from now on.
        // If we drop to no records selected, then there is no range start any more.
        if (!keyEvent.shiftKey && !me.destroyed && me.isSelected(record)) {
            me.selectionStart = record;
            me.selectionStartIdx = recIdx;
        }
    }
})