
Ext.define('CMDBuildUI.components.administration.tab.BarWithTools', {
    extend: 'Ext.tab.Bar',
    alias: 'widget.administration',

    requires: [
        'Ext.panel.Tool'
    ],
    config: {
        tools: null
    },

    /**
     * @override
     * 
     */
    initComponent: function () {
        var me = this,
            items = me.items;

        me.tools = me.tools || [];
        me.items = items = (items ? items.slice() : []);

        // Add Tools 
        Ext.Array.push(items, me.tools);
        // Clear the tools so we can have only the instances. Intentional mutation of passed in array 
        // Owning code in Panel uses this array as its public tools property. 
        me.tools.length = 0;
        me.callParent(arguments);
    },

    /**
     * @override
     * 
     * Add a tool to the header
     * @param {Object} tool 
     */
    addTool: function (tool) {
        var me = this;

        // Even though the defaultType is tool, it may be changed, 
        // so let's be safe and forcibly specify tool 
        me.add(Ext.ComponentManager.create(tool, 'tool'));

        me.checkFocusableTools();
    },

    /**
     * @override
     * 
     * @returns 
     */
    checkFocusableTools: function () {
        var me = this,
            tools = me.tools,
            haveFocusableTool, i, len;

        if (me.isAccordionHeader) {
            me.focusableContainer = false;

            return;
        }

        // We only need to enable FocusableContainer behavior when there are focusable tools.
        // For instance, Windows and Accordion panels can have Close tool that is not focusable,
        // in which case there is no sense in making the header behave like focusable container.
        for (i = 0, len = tools.length; i < len; i++) {
            if (tools[i].focusable) {
                haveFocusableTool = true;
                break;
            }
        }

        if (haveFocusableTool) {
            if (!me.initialConfig.hasOwnProperty('focusableContainer') || me.focusableContainer) {
                me.ariaRole = 'toolbar';
                me.focusableContainer = true;

                if (me.rendered) {
                    me.ariaEl.dom.setAttribute('role', 'toolbar');
                    me.initFocusableContainer(true);
                }
            }
        }
        else {
            me.ariaRole = 'presentation';
            me.focusableContainer = false;

            if (me.rendered) {
                me.ariaEl.dom.setAttribute('role', 'presentation');
                me.initFocusableContainer(true);
            }
        }
    }
});
