Ext.define('CMDBuildUI.view.dashboards.common.components.ComponentController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dashboards-components-component',

    control: {
        '#menuTool': {
            click: 'onMenuToolClick'
        }
    },

    /**
     * Hides the ellipsis tool if the component does not define any custom action.
     *
     * @param {CMDBuildUI.view.dashboards.common.components.Component} view
     */
    onComponentAfterRender: function (view) {
        const menuTool = view.down('#menuTool');
        if (menuTool) {
            menuTool.setVisible(!!(view.getCustomActions() || []).length);
        }
    },

    /**
     * Click event listener on menu tool click
     *
     * @param {Ext.panel.Tool } tool
     * @param { Ext.event.Event} e
     * @param {Ext.Component} owner
     * @param {Object} eOpts
     */
    onMenuToolClick: function (tool, e, owner, eOpts) {
        let items = [];
        const view = this.getView();
        const me = this;

        if (!view.actionsmenu) {
            const customActions = Ext.Array.map(view.getCustomActions() || [], function (tool) {
                const clonedTool = Ext.apply({}, tool);
                if (clonedTool.scope === 'controller') {
                    clonedTool.scope = me;
                }
                return clonedTool;
            });
            view.actionsmenu = Ext.create('Ext.menu.Menu', {
                items: Ext.Array.merge(items, customActions),
                alwaysOnTop: 1000
            }).showBy(tool, 'tr-br');
        } else {
            view.actionsmenu.show();
        }
    }
});
