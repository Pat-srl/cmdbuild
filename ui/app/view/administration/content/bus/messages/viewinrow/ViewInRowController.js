Ext.define('CMDBuildUI.view.administration.content.bus.messages.viewinrow.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-bus-messages-viewinrow-viewinrow',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();
        Ext.asap(function () {
            vm.linkTo('theMessage', {
                type: 'CMDBuildUI.model.administration.BusLog',
                id: view.up('grid').getSelection()[0].get('_id')
            });
        });
    }
});
