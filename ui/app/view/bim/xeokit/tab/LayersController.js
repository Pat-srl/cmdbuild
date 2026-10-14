Ext.define('CMDBuildUI.view.bim.xeokit.tab.LayersController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bim-xeokit-tab-layers',

    control: {
        '#': {
            objectselected: 'onObjectSelected'
        }
    },

    /**
     * Select on the grid the entity picked in the canvas
     * @param {Object} entity 
     */
    onObjectSelected: function (entity) {
        var view = this.getView(),
            record = null;

        Ext.Array.each(view.getStore().getRange(), function (type, index, allitems) {
            element = Ext.Array.findBy(type.data.children, function (item, index) {
                return entity.id === item.entityId;
            });
            if (element) {
                record = type;
                return false;
            }
        });

        view.ensureVisible(record, {
            select: true
        });
    }

});