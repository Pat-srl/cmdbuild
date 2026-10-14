Ext.define('Overrides.selection.Model', {
    override: 'Ext.selection.Model',

    /**
     * @override
     * 
     * Sets the current selectionMode.
     * @param {String} selMode 'SINGLE', 'MULTI' or 'SIMPLE'.
     */
    setSelectionMode: function (selMode) {
        selMode = selMode ? selMode.toUpperCase() : 'SINGLE';

        // set to mode specified unless it doesnt exist, in that case use single.
        this.selectionMode = this.modes[selMode] ? selMode : 'SINGLE';

        // fire event
        this.fireEvent("selectionmodechange", this, this.selectionMode);
    },

    privates: {

        /**
         * @override
         * 
         * @param {*} record 
         * @param {*} e 
         * @param {*} isSelected 
         */
        selectWithEventMulti: function (record, e, isSelected) {
            var me = this,
                shift = e.shiftKey,
                ctrl = e.ctrlKey,
                start = shift ? (me.getSelectionStart()) : null,
                selected = me.getSelection(),
                len = selected.length,
                toDeselect, i, item;

            if (shift && start) {
                me.selectRange(start, record, ctrl);
            } else if (ctrl && isSelected) {
                if (me.allowDeselect) {
                    me.doDeselect(record, false);
                }
            } else if (ctrl) {
                me.doSelect(record, true, false);
            } else if (isSelected && len > 1) {
                if (me.allowDeselect) {
                    toDeselect = [record];
                    // for (i = 0; i < len; ++i) {
                    //     item = selected[i];
                    //     if (item !== record) {
                    //         toDeselect.push(item);
                    //     }
                    // }
                    me.doDeselect(toDeselect);
                }
            } else if (!isSelected) {
                me.doSelect(record, true);
            }
        }
    }

});