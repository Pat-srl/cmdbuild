Ext.define('CMDBuildUI.view.dashboards.common.components.text.Text', {
    extend: 'CMDBuildUI.view.dashboards.common.components.Component',

    alias: 'widget.dashboards-components-text-text',

    tools: [],
    layout: 'auto',
    scrollable: 'y',

    initComponent: function () {
        const component = this.getItemDefinition();
        this.setHtml(component.get('text'));
        this.callParent(arguments);
    },

    privates: {
        /**
         * The text component adapts to its content when no height is specified.
         *
         * @returns {null}
         */
        getDefaultComponentHeight: function () {
            return null;
        }
    }
});
