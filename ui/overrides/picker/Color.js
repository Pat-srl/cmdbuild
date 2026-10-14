Ext.define('Override.picker.Color', {
    override: 'Ext.picker.Color',

    /**
     * @override
     *
     * Selects the specified color in the picker (fires the {@link #event-select} event)
     * @param {String} color A valid 6-digit color hex code (# will be stripped if included)
     * @param {Boolean} [suppressEvent=false] True to stop the select event from firing.
     */
    select: function (color, suppressEvent) {
        var me = this,
            selectedCls = me.selectedCls,
            value = me.value,
            el, item;

        color = color.replace('#', '');

        if (!me.rendered) {
            me.value = color;

            return;
        }

        if (color !== value || me.allowReselect) {
            el = me.el;

            if (me.value) {
                item = el.down('a.color-' + value, true);
                if (item) {
                    Ext.fly(item).removeCls(selectedCls);
                }
            }

            item = el.down('a.color-' + color, true);
            if (item) {
                Ext.fly(item).addCls(selectedCls);
            }
            me.value = color;

            if (suppressEvent !== true) {
                me.fireEvent('select', me, color);
            }
        }
    }
});