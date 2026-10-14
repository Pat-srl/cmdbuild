Ext.define('CMDBuildUI.view.administration.content.bus.descriptors.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-bus-descriptors-view',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#addbtn': {
            click: 'onAddBtnClick'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.administration.content.bus.descriptors.View} view 
     * @param {Object} eOpts 
     */
    onAfterRender: function (view, eOpts) {
        Ext.asap(function () {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        });
    },

    /**
     * 
     * @param {Ext.button.Button} view 
     * @param {Object} eOpts 
     */
    onAddBtnClick: function (view, eOpts) {
        this.redirectTo('administration/bus/descriptors_empty/true', true);
    }
});