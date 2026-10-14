Ext.define('CMDBuildUI.view.boot.patches.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.boot-patches-panel',

    control: {
        '#btnApply': {
            click: 'onBtnApplyClick'
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onBtnApplyClick: function (button, eOpts) {
        var container = CMDBuildUI.util.Navigation.getMainContainer();

        // add load mask
        var loadmask = new Ext.LoadMask({
            target: container
        });
        loadmask.show();

        // apply patches
        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Common.getApplyPatchesUrl(),
            method: 'POST',
            timeout: 1800000 // 30 minutes
        }).then(function (response, opts) {
            window.location.reload();
        });
    }
});
