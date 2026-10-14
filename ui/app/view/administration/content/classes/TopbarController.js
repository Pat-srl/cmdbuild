Ext.define('CMDBuildUI.view.administration.content.classes.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-classes-topbar',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addclass': {
            click: 'onAddClassClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.content.classes.Topbar} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        view.up('administration-content')
            .getViewModel()
            .set('title', CMDBuildUI.locales.Locales.administration.navigation.classes);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddClassClick: function (button, e, eOpts) {
        this.redirectTo('administration/classes', true);
        const vm = Ext.getCmp('administrationNavigationTree').getViewModel();
        vm.set('selected', null);
    }
});
