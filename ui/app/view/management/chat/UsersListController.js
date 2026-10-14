Ext.define('CMDBuildUI.view.management.chat.UsersListController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.management-chat-userslist',

    control: {
        '#': {
            itemclick: 'onItemClick'
        },
        '#searchuser': {
            specialkey: 'onSearchUserSpecialKey'
        }
    },

    /**
     * On user click.
     *
     * @param {CMDBuildUI.view.management.chat.UsersList} view
     * @param {CMDBuildUI.model.messages.User} record
     * @param {HTMLElement} item
     * @param {Number} index
     * @param {Event} e
     * @param {Object} eOpts
     */
    onItemClick: function (view, record, item, index, e, eOpts) {
        CMDBuildUI.util.Chat.openChat(record);
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        var value = field.getValue();
        if (value) {
            var vm = field.lookupViewModel(),
                store = vm.get('users');
            if (store) {
                store.getAdvancedFilter().clearAttributesFilter();
                store.getAdvancedFilter().addAttributeFilter('description', CMDBuildUI.util.helper.FiltersHelper.operators.contain, value);
                store.load();
            }
        } else {
            this.onSearchClear(field);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        var vm = field.lookupViewModel(),
            store = vm.get('users');
        if (store) {
            store.getAdvancedFilter().clearAttributesFilter();
            store.load();
            field.reset();
        }
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchUserSpecialKey: function (field, event) {
        if (event.getKey() == event.ENTER) {
            this.onSearchSubmit(field);
        }
    }
});
