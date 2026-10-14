Ext.define('CMDBuildUI.view.graph.canvas.bottomMenu.canvasMenuController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.graph-canvas-bottommenu-canvasmenu',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            destroy: 'onDestroy'
        },
        "#enableTooltip": {
            beforerender: 'onBeforeRenderEnableTooltip',
            toggle: 'onEnableDisableTooltipToggle'
        },
        "#enableAllTooltip": {
            beforerender: 'onBeforeRenderEnableAllTooltip',
            toggle: 'onEnableDisableAllTooltipToggle'
        }
    },

    /**
     * @param {Ext.Component} toolbar
     * @param {Object} eOpts
     */
    onBeforeRender: function (toolbar, eOpts) {
        CMDBuildUI.graph.util.canvasMenu._init();
    },

    /**
     * @param {Ext.Component} toolbar
     * @param {Object} eOpts
     */
    onDestroy: function (toolbar, eOpts) {
        CMDBuildUI.graph.util.canvasMenu._reset();
    },

    /**
     * 
     * @param {Ext.Button} button 
     * @param {Object} eOpts
     */
    onBeforeRenderEnableTooltip: function (button, eOpts) {
        const enabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.relgraph.node.tooltipEnabled) && !CMDBuildUI.graph.threejs.SceneUtils.allLabelsEnabled;
        this.setInitialStateOfTooltip(button, enabled);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Boolean} pressed
     * @param {Object} eOpts
     */
    onEnableDisableTooltipToggle: function (button, pressed, eOpts) {
        this.setButtonTooltip(button, pressed);
        CMDBuildUI.graph.threejs.SceneUtils.tooltip.setEnable(pressed);
    },

    /**
     * 
     * @param {Ext.Button} button 
     * @param {Object} eOpts
     */
    onBeforeRenderEnableAllTooltip: function (button, eopts) {
        const enabled = CMDBuildUI.graph.threejs.SceneUtils.allLabelsEnabled;
        this.setInitialStateOfTooltip(button, enabled);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Boolean} pressed
     * @param {Object} eOpts
     */
    onEnableDisableAllTooltipToggle: function (button, pressed, eOptss) {
        this.setButtonTooltip(button, pressed);
        CMDBuildUI.graph.threejs.SceneUtils.enableLabels(pressed);
    },

    privates: {
        /**
         * Set the initial state of the button and its tooltip
         * @param {Ext.Button} button 
         * @param {Boolean} enabled 
         */
        setInitialStateOfTooltip: function (button, enabled) {
            //sets the initial toggle state
            button.toggle(enabled, true);
            this.setButtonTooltip(button, enabled);
        },

        /**
         * Set the tooltip for the button
         * @param {Ext.Button} button 
         * @param {Boolean} active 
         */
        setButtonTooltip: function (button, active) {
            const tooltip = Ext.String.format('{0} {1}',
                active ? CMDBuildUI.locales.Locales.relationGraph.disable : CMDBuildUI.locales.Locales.relationGraph.enable,
                button.getItemId() == "enableTooltip" ? CMDBuildUI.locales.Locales.relationGraph.labelsOnGraph : CMDBuildUI.locales.Locales.relationGraph.allLabelsOnGraph
            );
            button.setTooltip(tooltip);
        }
    }
});