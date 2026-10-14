Ext.define('CMDBuildUI.view.administration.content.gis.gismenus.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-gismenus-view',

    control: {
        '#editBtn': {
            click: 'onEditBtnClick'
        }
    },

    /**
     *
     * @param {*} button
     * @param {*} event
     * @param {*} eOpts
     */
    onEditBtnClick: function (button, event, eOpts) {
        this.getViewModel().setCurrentAction(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    }
});