Ext.define('CMDBuildUI.view.management.chat.UsersListModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.management-chat-userslist',

    data: {
        usersearch: null
    },

    stores: {
        users: {
            type: 'buffered',
            model: 'CMDBuildUI.model.messages.User',
            autoLoad: true,
            autoDestroy: true,
            sorters: [{
                property: 'description',
                direction: 'ASC'
            }]
        }
    }
});