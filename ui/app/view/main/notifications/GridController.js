Ext.define('CMDBuildUI.view.main.notifications.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.main-notifications-grid',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            itemclick: 'onItemClick'
        }
    },

    /**
     * On before render
     * @param {CMDBuildUI.view.main.notifications.Grid} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        view.setEmptyText(CMDBuildUI.locales.Locales.notifications.emptymessage);
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
        if (e.target.getAttribute("role") === 'button' && !e.target.classList.value.includes(CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'))) {
            this.redirectTo(record.get('meta').action);
            this.getView().up().close();
            return false;
        }
    }
});
