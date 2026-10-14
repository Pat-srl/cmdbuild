Ext.define('CMDBuildUI.util.administration.helper.ConfirmMessageHelper', {
    requires: [],

    singleton: true,

    /**
     * Create default confirm message for delete action
     * @param {String} [title]
     * @param {String} [description]
     * @param {Function} callback
     * @param {Object} ctx
     */
    showDeleteItemMessage: function (title, description, callback, ctx) {
        Ext.MessageBox.show({
            title: title || CMDBuildUI.locales.Locales.administration.common.messages.attention,
            message: description || CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            buttons: Ext.Msg.YESNO,
            icon: Ext.Msg.QUESTION,
            buttonText: {
                yes: CMDBuildUI.locales.Locales.administration.common.actions.yes,
                no: CMDBuildUI.locales.Locales.administration.common.actions.no
            },
            fn: function (buttonText) {
                Ext.callback(callback, ctx, [buttonText]);
            }
        });
    }
});
