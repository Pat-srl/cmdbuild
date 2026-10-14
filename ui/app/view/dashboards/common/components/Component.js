Ext.define('CMDBuildUI.view.dashboards.common.components.Component', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.dashboards.common.components.ComponentController',
        'CMDBuildUI.view.dashboards.common.components.ComponentModel'
    ],

    controller: 'dashboards-components-component',
    viewModel: {
        type: 'dashboards-components-component'
    },

    statics: {
        defaultComponentHeight: 500
    },

    layout: 'fit',

    ui: 'managementdashboard',
    userCls: Ext.baseCSSPrefix + 'cmdbuild-dashboard-component',
    collapsible: true,
    margin: '15 0 0 15',

    initComponent: function () {
        const height = this.getComponentHeight();
        if (height) {
            this.height = height;
        }
        this.callParent(arguments);
    },

    config: {
        /**
         * @cfg {Boolean} showInPopup
         */
        showInPopup: false,

        /**
         * If `true` the "Open in popup" tool will be shown.
         *
         * @type {Boolean}
         */
        enableToolOpen: true,

        /**
         * List of custom Ext.panel.Tool.
         *
         * @type {Ext.panel.Tool[]}
         */
        customActions: [],

        /**
         * Component configuration.
         *
         * @type {CMDBuildUI.model.dashboards.Component}
         */
        itemDefinition: null
    },

    tools: [
        {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-v', 'solid'),
            itemId: 'menuTool',
            userCls: Ext.baseCSSPrefix + 'management-tool',
            hidden: true
        }
    ],

    listeners: {
        afterrender: 'onComponentAfterRender'
    },

    privates: {
        /**
         * Returns the default height used when the component does not define one.
         * Subclasses can return `null` to let the component adapt to its content.
         *
         * @returns {Number|null}
         */
        getDefaultComponentHeight: function () {
            return CMDBuildUI.view.dashboards.common.components.Component.defaultComponentHeight;
        },

        /**
         * Returns the height of the component.
         *
         * @returns {Number|null}
         */
        getComponentHeight: function () {
            const component = this.getItemDefinition();
            return this.getShowInPopup()
                ? (this.up().getHeight() / 100) * 75
                : parseInt(component.get('height')) || this.getDefaultComponentHeight();
        }
    }
});
