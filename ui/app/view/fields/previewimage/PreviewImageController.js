Ext.define('CMDBuildUI.view.fields.previewimage.PreviewImageController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-previewimage',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * On before render
     * @param {CMDBuildUI.view.fields.lookup.Lookup} view
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {       
        var vm = view.lookupViewModel();
        var resetKey = view.getResetKey(); 
        vm.set('updatingImage', false);
        vm.bind({
            bindTo: {
                imageKey: Ext.String.format('{{0}}', resetKey)
            }
        }, function (data) {            
            if (!data.imageKey) {
                vm.set('updatingImage', true);
            }
        });
    },

    onRemoveImageBtnClick: function (button, event, eOpts) {
        var view = this.getView();
        var vm = view.lookupViewModel();
        var resetKey = view.getResetKey();        
        vm.set(resetKey, null);
    }

});