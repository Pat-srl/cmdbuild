Ext.define('CMDBuildUI.view.administration.content.menus.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-menus-topbar',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addBtn': {
            click: 'onAddBtnClick'
        }
    },
    /**
     * Before render
     * @param {CMDBuildUI.view.administration.content.menus.Topbar} view
     */
    onBeforeRender: function (view) {
        var vm = view.getViewModel();
        var title = CMDBuildUI.locales.Locales.administration.menus.plurale;
        var device = vm.get('device');
        if (device) {
            title = Ext.String.format('{0} - {1}',
                CMDBuildUI.locales.Locales.administration.menus.singular,
                CMDBuildUI.util.administration.helper.RendererHelper.getMenuTargetDevice(device)
            );
        }
        vm.getParent().set('title', title);
    },
    /**
     * On add menu button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddBtnClick: function (button, e, eOpts) {
        this.redirectTo(Ext.String.format('administration/menus/{0}', button.lookupViewModel().get('device')), true);       
    }
});