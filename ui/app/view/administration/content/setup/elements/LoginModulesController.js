Ext.define('CMDBuildUI.view.administration.content.setup.elements.LoginModulesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-loginmodules',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();
        vm.bind({
            theSetup: '{theSetup}'
        }, function (data) {
            if (data.theSetup) {             
            }
        });
    }

});