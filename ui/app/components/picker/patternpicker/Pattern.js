/**
 * The Pattern picker provides a selectable palette of fill patterns.
 * It works similarly to the standard Sencha Color Picker but is extended
 * to support pattern objects instead of color hex codes.
 *
 * The picker can be rendered inside any container. Each pattern is displayed
 * as a canvas element, showing a preview of the fill style defined by the
 * pattern's drawing function.
 *
 * Typically you will implement a handler or listen to the {@link #event-select}
 * event to be notified when the user selects a pattern.
 *
 * @example
 * Ext.create('CMDBuildUI.components.patternpicker.Pattern', {
 *     value: { id: 'dots', ... },  // initial selected pattern
 *     renderTo: Ext.getBody(),
 *     listeners: {
 *         select: function (picker, pattern) {
 *             console.log('Selected pattern:', pattern);
 *         }
 *     }
 * });
 */
Ext.define('CMDBuildUI.components.picker.patternpicker.Pattern', {
    extend: 'Ext.Component',
    requires: 'Ext.XTemplate',
    alias: 'widget.cmdbuild-pattern',

    focusable: true,

    /**
     * @cfg {String} [componentCls='x-pattern-picker']
     * The CSS class applied to the container element of the picker.
     */
    componentCls: Ext.baseCSSPrefix + 'pattern-picker',

    /**
     * @cfg {String} [selectedCls='x-pattern-picker-selected']
     * The CSS class applied to a selected pattern item.
     */
    selectedCls: Ext.baseCSSPrefix + 'pattern-picker-selected',

    /**
     * @cfg {String} itemCls
     * The CSS class applied to each pattern canvas item.
     */
    itemCls: Ext.baseCSSPrefix + 'pattern-picker-item',

    /**
     * @cfg {Object} value
     * The initial pattern to select. Must match one of the elements in
     * the {@link #patterns} collection.
     */
    value: {},

    /**
     * @cfg {String} clickEvent
     * The DOM event that triggers a pattern selection.
     * Defaults to `'click'`, but may be changed (e.g., `'dblclick'`).
     */
    clickEvent: 'click',

    /**
     * @cfg {Boolean} allowReselect
     * If set to `true`, selecting an already selected pattern will still
     * fire the {@link #event-select} event.
     */
    allowReselect: false,

    /**
     * @property {Object[]} patterns
     * The list of patterns available for selection.
     * Each pattern object must contain:
     *
     * - `id`: unique string identifier
     * - `opts`: configuration for tile size, spacing, color, etc.
     * - `drawTile(ctx, opts)`: a function that draws the tile on a canvas
     *
     * The default set of patterns is taken from:
     * `CMDBuildUI.util.administration.helper.ModelHelper.getGeoattributesFillPatterns()`.
     */
    patterns: CMDBuildUI.util.administration.helper.ModelHelper.getGeoattributesFillPatterns(),

    renderTpl: [
        '<tpl for="patterns">',
        '<canvas id="canvas-{id}" data-id="{id}" class="{parent.itemCls}" width="48" height="48"></canvas>',
        '</tpl>'
    ],

    /**
     * @private
     * Initializes the component and registers the handler (if provided) for the
     * {@link #event-select} event.
     */
    initComponent: function () {
        var me = this;

        me.callParent(arguments);

        if (me.handler) {
            me.on('select', me.handler, me.scope, true);
        }
    },

    /**
     * @private
     * Prepares the data used in the rendering template, exposing the CSS class and
     * pattern list to the XTemplate.
     *
     * @return {Object} Render data object.
     */
    initRenderData: function () {
        const me = this;

        return Ext.apply(me.callParent(), {
            itemCls: me.itemCls,
            patterns: me.patterns
        });
    },

    /**
     * @private
     * Called when the component is rendered. Registers the listener for pattern clicks.
     */
    onRender: function () {
        const me = this;
        const clickEvent = me.clickEvent;

        me.callParent(arguments);

        // Delegate events to canvas elements
        me.mon(me.el, clickEvent, me.handleClick, me, { delegate: 'canvas' });

        // Prevent click bubbling when using custom click events
        if (clickEvent !== 'click') {
            me.mon(me.el, 'click', Ext.emptyFn, me, { delegate: 'canvas', stopEvent: true });
        }
    },

    /**
     * @private
     * After the picker is rendered, this method draws each pattern on its canvas preview.
     * If an initial value is specified, it is selected automatically.
     */
    afterRender: function () {
        const me = this;
        me.callParent(arguments);

        // Draw pattern previews
        me.patterns.forEach(function (pattern) {
            const canvas = document.getElementById('canvas-' + pattern.id);
            if (!canvas || !canvas.getContext) return;

            const tile = document.createElement('canvas');
            const ctx = tile.getContext('2d');

            tile.width = pattern.opts.tileWidth || pattern.opts.spacing;
            tile.height = pattern.opts.tileHeight || pattern.opts.spacing;

            // Let pattern draw itself
            pattern.drawTile(
                ctx,
                Ext.Object.merge(pattern.opts, { color: CMDBuildUI.util.helper.MapHelper.patternDefaultColor })
            );

            // Update picker list
            const previewCtx = canvas.getContext('2d');
            const pat = previewCtx.createPattern(tile, 'repeat');

            previewCtx.save();
            previewCtx.clearRect(0, 0, canvas.width, canvas.height);
            previewCtx.fillStyle = pat;
            previewCtx.fillRect(0, 0, canvas.width, canvas.height);
            previewCtx.restore();
        });

        // Restore the initial selection
        if (me.value) {
            const value = me.value;
            me.value = null;
            me.select(value, true);
        }
    },

    /**
     * @private
     * Handles user interaction when clicking a pattern canvas.
     * Finds the pattern object and triggers selection.
     *
     * @param {Ext.event.Event} event The DOM event object.
     */
    handleClick: function (event) {
        const me = this;

        event.stopEvent();

        if (!me.disabled) {
            const patternId = event.currentTarget.getAttribute('data-id');
            me.select(me.findPattern(patternId));
        }
    },

    /**
     * Selects a pattern in the picker and optionally fires the {@link #event-select} event.
     *
     * @param {Object} pattern The pattern object to select.
     * @param {Boolean} [suppressEvent=false] If true, the select event will not fire.
     */
    select: function (pattern, suppressEvent) {
        const me = this;
        const selectedCls = me.selectedCls;
        let value = me.value;
        let el, item;

        if (!me.rendered) {
            me.value = pattern;
            return;
        }

        if (!value || pattern.id !== value.id || me.allowReselect) {
            el = me.el;

            // Remove previous selection
            if (me.value) {
                item = el.down('#canvas-' + value.id, true);
                Ext.fly(item).removeCls(selectedCls);
            }

            // Add new selection
            item = el.down('#canvas-' + pattern.id, true);
            Ext.fly(item).addCls(selectedCls);
            me.value = pattern;

            if (suppressEvent !== true) {
                me.fireEvent('select', me, pattern);
            }
        }
    },

    /**
     * Clears any user selection and resets the picker state.
     */
    clear: function () {
        const me = this;
        const value = me.value;
        let el;

        if (value && me.rendered) {
            el = me.el.down('#canvas-' + value.id, true);
            Ext.fly(el).removeCls(me.selectedCls);
        }

        me.value = null;
    },

    /**
     * Returns the currently selected pattern object.
     *
     * @return {Object|null} The selected pattern, or null if none is selected.
     */
    getValue: function () {
        return this.value || null;
    },

    /**
     * Searches the available patterns by id.
     *
     * @param {String} id The pattern identifier.
     * @return {Object|null} The matching pattern, or null if not found.
     */
    findPattern: function (id) {
        return (
            Ext.Array.findBy(this.patterns, function (p) {
                return p.id === id;
            }) || null
        );
    }
});
