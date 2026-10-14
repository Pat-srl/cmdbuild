Ext.define('CMDBuildUI.view.administration.navigation.TreeItemController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-navigation-tree-item',

    /**
     * Double click event.
     * 
     * @param {Ext.event.Event} event    The Ext.event.Event encapsulating the DOM event.
     * @param {HTMLElement} element      The target of the event.
     * @param {Object} eOpts             The options object passed to Ext.util.Observable.addListener.
     */
    dblClick: function (event, element, eOpts) {
        var isExpandable = (this.view && this.view._expandable)?true:false;
        if(!isExpandable){
            event.stopEvent();
            return false;
        }
        if (!this.view.el.component.isExpanded()) {
            this.view.el.component.expand();
        } else {
            this.view.el.component.collapse();
        }
    }
});
