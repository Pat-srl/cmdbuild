Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-importexport-gatetemplates-view',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },
    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();

        if(vm.get('theGate') && vm.get('theGate').phantom){
            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
        }
    }
});
