Ext.define('CMDBuildUI.view.management.ai.ChatModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.management-ai-chat',

    data: {
        loading: false,
        msg: {
            text: null
        }
    },

    formulas: {
        setText: {
            get: function () {
                this.set(
                    'chatTitleHtml',
                    CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ai.chatname)
                );
                this.set(
                    'introPanelHtml',
                    Ext.String.format(
                        '<div class="introText"><h1>{0}</h1><p>{1}</p></div>',
                        CMDBuildUI.locales.Locales.ai.introtexttitle,
                        CMDBuildUI.locales.Locales.ai.introtextbody
                    )
                );
                this.set('loadingText', CMDBuildUI.locales.Locales.ai.loading);
            }
        }
    },

    stores: {
        messagesStore: {
            type: 'chained',
            source: 'ai-messages'
        }
    }
});
