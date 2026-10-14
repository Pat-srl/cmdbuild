Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-importexport-gatetemplates-topbar',

    control: {
        '#addgate': {
            click: 'onNewBtnClick'
        },
        '#dockedTop': {
            afterrender: 'onDockedAfterRender'
        }
    },

    /**
     * 
     * @param {Ext.button.Button} button
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewBtnClick: function (button, event, eOpts) {
        this.redirectTo(CMDBuildUI.util.administration.helper.ApiHelper.client.getGateTemplateUrl(button.lookupViewModel().get('gateType'), true), true);
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() === event.ENTER) {
            this.onSearchSubmit(field);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.event.Event} event
     */
    onSearchSubmit: function (field, event) {
        var grid = this.getView().up('grid');
        var store = grid.getStore();
        var formInRow = grid.getPlugin('administration-forminrowwidget');
        // removeAllExpanded
        formInRow.removeAllExpanded();
        CMDBuildUI.util.administration.helper.GridHelper.localSearchFilter(store, field.getValue());
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        // clear store filter
        var store = this.getView().up('grid').getStore();
        if (store) {
            CMDBuildUI.util.administration.helper.GridHelper.removeLocalSearchFilter(store);
        }
        // reset input
        field.reset();
    },

    /**
     * 
     * @param {Ext.toolbar.Toolbar} view 
     */
    onDockedAfterRender: function (view) {
        view.add({
            xtype: 'admin-globalsearchfield',
            objectType: 'etlgates',
            subType: view.lookupViewModel().get('gateType')
        });
    }
});