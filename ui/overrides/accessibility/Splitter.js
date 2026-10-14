Ext.define('Overrides.accessibility.Splitter', {
    override: 'Ext.resizer.Splitter',

    beforeRender: function () {
        var me = this;
        me.callParent(arguments);
        me.ariaRenderAttributes = me.ariaRenderAttributes || {};

        me.ariaRenderAttributes['aria-label'] = me.getAriaLabel();

        me.ariaRenderAttributes['aria-valuemin'] = 0;
        me.ariaRenderAttributes['aria-valuemax'] = me.getMaxValue();
        me.ariaRenderAttributes['aria-valuenow'] = me.getCurrentValue();
    },

    getAriaLabel: function () {
        return this.orientation === 'vertical'
            ? CMDBuildUI.locales.Locales.arialabels.resizepanel.vertically
            : CMDBuildUI.locales.Locales.arialabels.resizepanel.horizontally;
    },

    getMaxValue: function () {
        return this.el ? this.el.dom.parentNode[this.orientation === 'vertical' ? 'offsetWidth' : 'offsetHeight'] : 100;
    },

    getCurrentValue: function () {
        return this.el ? this.el.dom[this.orientation === 'vertical' ? 'offsetLeft' : 'offsetTop'] : 50;
    }
});
