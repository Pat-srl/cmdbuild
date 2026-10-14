Ext.define('CMDBuildUI.view.fields.lookup.LookupComboController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-lookupcombofield',

    control: {
        '#': {
            cleartrigger: 'onClearTrigger',
            expand: 'onExpand'
        }
    },

    /**
     * @param {CMDBuildUI.view.fields.Reference} combo
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onClearTrigger: function (combo, trigger, eOpts) {
        combo.clearValue();
        // clear local filter if combo is expanded
        if (combo.isExpanded) {
            combo.doLocalQuery("");
        }
        combo.lastSelectedRecords = [];
        if (combo.hasBindingValue) {
            combo.getBind().value.setValue(null);
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.fields.lookup.LookupCombo} view 
     * @param {Object} eOpts 
     */
    onExpand: function (view, eOpts) {
        var picker = view.getPicker();
        if (picker.getSelectionModel().hasSelection()) {
            var selected = picker.getSelectionModel().getSelection()[0];
            var itemNode = picker.getNode(selected);

            if (itemNode) {
                picker.setScrollY(itemNode.offsetTop);
            }
        }
    }

});