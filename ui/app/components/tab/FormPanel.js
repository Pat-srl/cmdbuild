Ext.define('CMDBuildUI.components.tab.FormPanel', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.formtabpanel',

    requires: [
        'CMDBuildUI.components.tab.BarWithTools'
    ],

    ui: 'tabandtools',

    /**
     * @override
     * 
     * @param {*} tabBar 
     * @returns 
     */
    applyTabBar: function (tabBar) {
        var me = this,
            // if we are rendering the tabbar into the panel header, use same alignment
            // as header position, and ignore tabPosition.
            dock = (me.tabBarHeaderPosition != null) ? me.getHeaderPosition() : me.getTabPosition();

        return new CMDBuildUI.components.tab.BarWithTools(Ext.apply({
            ui: me.ui,
            dock: dock,
            tabRotation: me.getTabRotation(),
            vertical: (dock === 'left' || dock === 'right'),
            plain: me.plain,
            tabStretchMax: me.getTabStretchMax(),
            tabPanel: me
        }, tabBar));
    },

    /**
     * @override
     * 
     * @private
     * Tools are a Panel-specific capability.
     * Panel uses initTools. Subclasses may contribute tools by implementing addTools.
     */
    initTools: function () {
        var me = this,
            tools = me.tools,
            len = tools && tools.length,
            i, tool;

        me.tools = []; // empty tools
        me.tabtools = [];

        if (!(tools && tools.length)) {
            tools = [{
                xtype: 'tool'
            }];
        }

        if (len) {
            for (i = 0; i < len; ++i) {
                tool = tools[i];

                if (typeof tool !== 'string' && !tool.isTool) {
                    tool = Ext.apply({}, tool);
                }

                me.tabtools.push(tool);
                tool.toolOwner = me;
                tool.ui = me.ui;
            }
        }

        // Add subclass-specific tools. 
        me.addTools();
    },

    /**
     * @override
     * 
     * Create, hide, or show the header component as appropriate based on the current config.
     * @private
     * @param {Boolean} force True to force the header to be created
     */
    updateHeader: function (force) {
        if (this.tabtools && this.tabtools.length) {
            var tabbar = this.getTabBar();
            if (!tabbar.tools.length) {
                tabbar.add({
                    xtype: 'component',
                    flex: 1
                });
                var tools = this.tabtools;
                for (var i = 0; i < tools.length; i++) {
                    tabbar.addTool(tools[i]);
                }
            }
        }
        this.callParent(arguments);
    },

    /**
     * @override
     * 
     * Add tools to this panel {@link Ext.panel.Header header}
     * 
     *     panel.addTool({
     *         type: 'gear',
     *         handler: function() {
     *             // ....
     *         }
     *     });
     *     
     *     panel.addTool([{
     *         type: 'gear',
     *         handler: 'viewControllerGearMethod'
     *     }, {
     *         type: 'save',
     *         handler: 'viewControllerSaveMethod'
     *     }]);
     *
     * By default the tools will be accessible via keyboard, with the exception of 
     * automatically added collapse/expand and close tools.
     *
     * If you implement keyboard equivalents of your tools' actions elsewhere and do not
     * want the tools to participate in keyboard navigation, you can make them 
     * presentational instead:
     * 
     *     panel.addTool({
     *         type: 'mytool',
     *         focusable: false,
     *         ariaRole: 'presentation'
     *         // ...
     *     });
     * 
     * @param {Object/Object[]/Ext.panel.Tool/Ext.panel.Tool[]} tools The tool or tools to
     * add.
     */
    addTool: function (tools) {
        if (!Ext.isArray(tools)) {
            tools = [tools];
        }

        // eslint-disable-next-line vars-on-top
        var me = this,
            header = me.header,
            len = tools.length,
            curTools = me.tools,
            t, tool;

        if (!header || !header.isHeader) {
            header = null;

            if (!curTools) {
                me.tools = curTools = [];
            }
        }

        for (t = 0; t < len; t++) {
            tool = tools[t];

            if (typeof tool !== 'string' && !tool.isTool) {
                tool = Ext.apply({}, tool);
            }

            tool.toolOwner = me;

            if (header) {
                header.addTool(tool);
            } else {
                // only modify the tools array if the header isn't created,
                // otherwise, defer to the header to manage
                curTools.push(tool);
            }
        }

        me.updateHeader();
    },

    /**
     * @override
     * 
     * @private
     * Helper function for ghost
     */
    ghostTools: function () {
        var tools = [],
            header = this.header,
            headerTools = header ? header.query('tool[hidden=false]') : [],
            t, tLen, tool;

        if (headerTools.length) {
            t = 0;
            tLen = headerTools.length;

            for (; t < tLen; t++) {
                tool = headerTools[t];

                // Some tools can be full components, and copying them into the ghost
                // actually removes them from the owning panel. You could also potentially
                // end up with duplicate DOM ids as well. To avoid any issues we just make
                // a simple bare-minimum clone of each tool for ghosting purposes.
                tools.push({
                    type: tool.type,
                    focusable: false
                });
            }
        } else {
            tools = [{
                type: 'placeholder'
            }];
        }

        return tools;
    },

    listeners: {
        afterrender: function () {
            this.getTabBar().items.each(function (item, index) {
                try {
                    if (!item.destroyed && !item.el.dom.dataset.testid) {
                        item.el.dom.dataset.testid = Ext.String.format("tab_{0}", index);
                    }
                } catch (error) {

                }
            }, this);
        }
    }

});