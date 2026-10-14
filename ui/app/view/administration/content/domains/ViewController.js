Ext.define('CMDBuildUI.view.administration.content.domains.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-domains-view',

    control: {
        '#': {
            afterlayout: 'onAfterLayout'
        }
    },

    /**
     *
     * @param {Ext.container.Container} container
     * @param {Ext.layout.container.Container} layout
     * @param {Object} eOpts
     */
    onAfterLayout: function (container, layout, eOpts) {
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
    }
});
