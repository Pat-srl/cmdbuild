Ext.define('CMDBuildUI.view.main.notifications.MenuListController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.main-notifications-menulist',

    control: {
        '#': {
            itemclick: 'onItemClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.main.notifications.MenuList} list
     * @param {CMDBuildUI.model.messages.Notification} record
     * @param {HTMLElement} item
     * @param {Number} index
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onItemClick: function (list, record, item, index, e, eOpts) {
        if (e.target.getAttribute("role") === 'button') {
            this.redirectTo(record.get('meta').action);
            this.getView().up().hide();
            return false;
        }
    }
});