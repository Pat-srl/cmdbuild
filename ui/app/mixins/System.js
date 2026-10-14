Ext.define('CMDBuildUI.mixins.System', {
    mixinId: 'system-mixin',

    reloadApp: function () {
        const reloadApp = function () {
            Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Common.getBootStatusUrl(),
                method: 'GET',
                callback: function (records, operation, success) {
                    if (success.status === 200 && success.responseText) {
                        const jsonresponse = Ext.JSON.decode(success.responseText);
                        const status = jsonresponse.status;
                        if (status === 'READY') {
                            window.location.reload();
                        }
                    }

                    setTimeout(function () {
                        reloadApp();
                    }, 2000);
                }
            });
        };

        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.notifier.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousurerebootapplication,
            function (action) {
                if (action === 'yes') {
                    const mainContainer = CMDBuildUI.util.Navigation.getMainContainer();
                    mainContainer.getViewModel().set('disableAlertBtn', true);
                    CMDBuildUI.util.Utilities.addLoadMask(mainContainer);
                    Ext.Ajax.request({
                        method: 'POST',
                        url: CMDBuildUI.util.Config.baseUrl + '/system/restart',
                        callback: function () {
                            reloadApp();
                        }
                    });
                }
            }
        );
    }
});
