Ext.define('CMDBuildUI.view.graph.canvas.topMenu.TopMenuController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.graph-canvas-topmenu-topmenu',

    control: {
        '#reopengraph': {
            click: 'onReopenGraphButtonClick'
        },
        '#chooseNavTree': {
            change: 'onChangeNavTree'
        },
        '#refresh': {
            click: 'onRefreshButtonClick'
        }
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.event.Event} e
     * @param {Ext.Component} owner
     * @param {Object} eOpts
     */
    onReopenGraphButtonClick: function (tool, e, owner, eOpts) {
        const vm = this.getViewModel(),
            selNode = vm.get('selectedNode')[0],
            dataNode = vm
                .get('cy')
                .nodes('#' + selNode.id)
                .data(),
            tmpSelectedNode = {
                type: dataNode.type,
                id: dataNode.id,
                code: dataNode.code,
                description: dataNode.description
            };

        this.graphResetEnvironment(tmpSelectedNode);
    },

    /**
     *
     * @param {Ext.form.field.Field} field
     * @param {Object} newValue
     * @param {Object} oldValue
     * @param {Object} eOpts
     */
    onChangeNavTree: function (field, newValue, oldValue, eOpts) {
        this.onReopenGraphButtonClick();
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Ext.event.Event} e
     * @param {Ext.Component} owner
     * @param {Object} eOpts
     */
    onRefreshButtonClick: function (tool, e, owner, eOpts) {
        const viewContainer = tool.up('graph-graphcontainer'),
            originalNode = {
                type: viewContainer._type, //class name
                id: viewContainer._id, //card id
                code: viewContainer._code,
                description: viewContainer._description
            };

        this.graphResetEnvironment(originalNode);
    },

    privates: {
        /**
         *
         * @param {[Object]} tmpSelectedNode
         */
        graphResetEnvironment: function (tmpSelectedNode) {
            const graphController = this.getView().up('graph-graphcontainer').getController();

            this.getViewModel().set('pointerExternalCanvas', false);
            graphController.resetEnvironment(tmpSelectedNode); //FIXME: no argument in the function required
        }
    }
});
