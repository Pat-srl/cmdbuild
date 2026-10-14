Ext.define('CMDBuildUI.view.administration.content.setup.elements.SystemController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-system',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        view.up('administration-content-setup-view').getViewModel().setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigation.system);
    },

    onDropCacheBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + '/system/cache/drop',
            method: 'POST',
            success: function (transport) {
                button.setDisabled(false);
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                CMDBuildUI.util.Notifier.showSuccessMessage(CMDBuildUI.locales.Locales.administration.common.messages.cacheempities, null, 'administration');
            },
            failure: function () {
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            }
        });
    },

    onSystemPreloadBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + '/system/preload',
            method: 'POST',
            success: function (transport) {
                button.setDisabled(false);
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                CMDBuildUI.util.Notifier.showSuccessMessage(CMDBuildUI.locales.Locales.administration.common.messages.systempreloaded, null, 'administration');
            },
            failure: function () {
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            }
        });
    },

    onUnlockAllCardsBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.administration.helper.AjaxHelper.unlockAllCards(button);
    },

    /**
     * 
     * @param {Ext.button.Button} btn 
     * @param {Ext.event.Event} e 
     */
    onViewLogsBtnClick: function (btn, e) {
        CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.administration.systemconfig.viewlogs, {
                xtype: 'administration-content-setup-elements-logviewer'
            }, {}, {
                ui: 'administration-actionpanel'
            }
        );
    },
    onEditLogConfigurationBtnClick: function () {
        CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.administration.systemconfig.editlogconfig, {
                xtype: 'administration-content-setup-elements-editlogconfig'
            }, {}, {
                ui: 'administration-actionpanel'
            }
        );
    },
    onDownloadLogsBtnClick: function () {
        CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.administration.systemconfig.downloadlogs, {
                xtype: 'administration-content-setup-elements-downloadlog'
            }, {}, {
                ui: 'administration-actionpanel'
            }
        );
    }

});