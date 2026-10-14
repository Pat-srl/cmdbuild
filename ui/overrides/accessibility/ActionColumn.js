Ext.define('Overrides.accessibility.ActionColumn', {
    override: 'Ext.grid.column.Action',

    initComponent: function () {
        this.callParent(arguments);
        this.on('beforerender', this.bindViewRefresh, this);
    },

    bindViewRefresh: function () {
        const grid = this.up('grid');
        if (!grid) return;

        const view = grid.getView();
        if (!view) return;

        /**
         * For every refresh of the grid view, apply accessibility attributes to action column icons
         *
         * This is necessary because action column icons are re-rendered on each refresh, losing any previously set attributes
         */
        view.on('refresh', this.applyAccessibility, this);
    },

    applyAccessibility: function (view) {
        const el = view.getEl();
        if (!el) return;

        el.select('.x-action-col-icon').each(function (iconEl) {
            const dom = iconEl.dom;
            const label = dom.getAttribute('data-qtip') || dom.getAttribute('title') || '';

            if (!dom.hasAttribute('aria-label')) dom.setAttribute('aria-label', label);
            if (!dom.hasAttribute('tabindex')) dom.setAttribute('tabindex', '-1');
            if (!dom.hasAttribute('role')) dom.setAttribute('role', 'button');
            if (!dom.hasAttribute('title')) dom.setAttribute('title', label);
        });
    }
});
