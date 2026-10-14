Ext.define('CMDBuildUI.view.widgets.aiassistant.ChatModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.widgets-aiassistant-chat',

    data: {
        loading: false,
        msg: {
            text: null
        }
    },

    stores: {
        messagesStore: {
            model: 'CMDBuildUI.model.ai.Message',
            data: [],
            sorters: [
                {
                    property: 'timestamp',
                    direction: 'ASC'
                }
            ],
            pageSize: 0
        }
    }
});
