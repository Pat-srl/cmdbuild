Ext.define('CMDBuildUI.view.fields.schedulerdate.Util', {
    singleton: true,

    /**
     * 
     * @param {*} sequence Ther record to update
     * @param {*} newDate The new date
     * @param {Object} config 
     * @param {Boolean} config.keepgap This config calculates the new lastEvent date starting from the newDate and keeping the old gap. If false the gap is 0;
     */
    updateSequenceDate: function (sequence, newDate, config) {
        config = config || {}
        Ext.applyIf(config, {
            keepgap: false
        })

        var gap = 0
        if (config.keepgap) {
            gap = Ext.Date.diff(
                sequence.get('firstEvent'), //min
                sequence.get('lastEvent'), //max
                Ext.Date.DAY //unit
            )
        }

        const firstEvent = newDate,
            lastEvent = Ext.Date.add(newDate, Ext.Date.DAY, gap);

        sequence.set('firstEvent', firstEvent);
        sequence.set('lastEvent', lastEvent);
    },

    /**
     * 
     * @param {*} record 
     */
    isDirty: function (record) {
        var dirty = true;

        if (record._dirty_delete !== true && record._dirty_create !== true && record._dirty_recalculate !== true && record._dirty_edit !== true) {
            dirty = false;
        }
        return dirty;
    }
});