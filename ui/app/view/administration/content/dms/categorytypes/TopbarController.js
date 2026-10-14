Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dms-dmscategorytypes-topbar',

    control: {
        '#addDMSCategory': {
            click: 'onAddDMSCatgoryTypeClick'
        }
    },
    onAddDMSCatgoryTypeClick: function () {
        var url = CMDBuildUI.util.administration.helper.ApiHelper.client.getDmsCategoryUrl('');
        this.redirectTo(url, true);
        var vm = Ext.getCmp('administrationNavigationTree').getViewModel();        
        vm.set('selected', null);
    }
});