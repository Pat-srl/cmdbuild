Ext.define('CMDBuildUI.view.administration.content.setup.elements.AuthModulesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-authmodules',

    control: {
        '#authModulesGrid > checkcolumn': {
            beforecheckchange: 'onBeforeCheckChange'
        }
    },


    onBeforeCheckChange: function(grid){
        return false;
    }
    
});
