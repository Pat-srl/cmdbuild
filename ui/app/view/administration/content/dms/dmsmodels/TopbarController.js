Ext.define('CMDBuildUI.view.administration.content.dms.models.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dms-models-topbar',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#adddmsmodel': {
            click: 'onAddDMSModelClick'
        }
    },

    onBeforeRender: function (view) {
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigation.dmsmodels);
    },
    onAddDMSModelClick: function () {
        this.redirectTo('administration/dmsmodels', true);
        var vm = Ext.getCmp('administrationNavigationTree').getViewModel();
        vm.set('selected', null);
    }
});
