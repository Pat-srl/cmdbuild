Ext.define('CMDBuildUI.graph.util.canvasMenu', {
    singleton: true,

    _canvasMenu: null,

    _init: function () {
        this._canvasMenu = Ext.ComponentQuery.query('graph-canvas-bottommenu-canvasmenu')[0];
    },

    _reset: function () {
        this._canvasMenu = null;
    },

    /**
     * @param {String} id not containing the '#' characther
     * @returns {Ext.component} The component in the topMenu with that id
     */
    getComponent: function (id) {
        var cmp;
        try {
            cmp = this.getCanvasMenu().down(id);
        } catch (err) {
            this._reset();
            this._init();
            cmp = this.getCanvasMenu().down(id);
        } finally {
            return cmp;
        }
    },

    privates: {
        /**
         * 
         * @returns _canvasMenu
         */
        getCanvasMenu: function () {
            return this._canvasMenu;
        }
    }
});