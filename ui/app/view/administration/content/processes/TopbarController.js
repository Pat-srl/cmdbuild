Ext.define('CMDBuildUI.view.administration.content.processes.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-processes-topbar',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addprocess': {
            click: 'onAddProcessClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.processes.Topbar} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        view.lookupViewModel().getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.processes);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddProcessClick: function (button, e, eOpts) {
        this.redirectTo('administration/processes', true);
        const vm = Ext.getCmp('administrationNavigationTree').getViewModel();
        vm.set('selected', null);
    }
});
