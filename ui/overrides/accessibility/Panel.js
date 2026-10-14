Ext.define('Overrides.accessibility.Panel', {
    override: 'Ext.panel.Panel',

    afterRender: function () {
        this.callParent(arguments);

        if (this.body && this.body.hasCls('x-scroller')) {
            this.body.dom.setAttribute('tabindex', '0');
            this.body.dom.setAttribute('role', 'region');
        }

        if (this.el && this.el.dom) {
            this.el.dom.removeAttribute('aria-readonly');
            this.el.dom.removeAttribute('aria-multiselectable');
        }
    }
});
