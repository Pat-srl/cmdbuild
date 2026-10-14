Ext.define('CMDBuildUI.view.administration.components.localsearchfield.LocalSearchFieldController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-localsearchfield-localsearchfield',

    control: {
        '#': {
            change: 'onChange'
        }
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onChange: function (field, newValue, oldValue) {        
        var grid = Ext.ComponentQuery.query(field.getGridItemId())[0];        
        if (grid) {
            var store = grid.getStore();
            var formInRow = grid.getPlugin('administration-forminrowwidget');
            if (formInRow) {
                // removeAllExpanded
                formInRow.removeAllExpanded();
            }
            if (Ext.isEmpty(newValue)) {
                CMDBuildUI.util.administration.helper.GridHelper.removeLocalSearchFilter(store);
            } else {
                CMDBuildUI.util.administration.helper.GridHelper.localSearchFilter(store, newValue);
            }
        } else {
            CMDBuildUI.util.Logger.log("Unable to fetch the grid associated", CMDBuildUI.util.Logger.levels.debug);
        }
    }

});