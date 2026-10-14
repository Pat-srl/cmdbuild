Ext.define('CMDBuildUI.view.classes.cards.grid.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.classes-cards-grid-grid',

    data: {
        selection: null
    },

    formulas: {
        canAdd: function (get) {
            // TODO: check permissions
            return true;
        },
        newButtonHidden: function () {
            return !this.getView().getShowAddButton();
        }
    }

});
