Ext.define('CMDBuildUI.view.administration.navigation.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-navigation-container',

    control: {
        '#navSearchField': {
            select: 'onNavSearchFieldSelect'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.fields.groupedcombobox.GroupedComboBox} combo
     * @param {Ext.data.Model} record
     * @param {Object} eOpts
     */
    onNavSearchFieldSelect: function (combo, record, eOpts) {
        if (record) {
            const view = this.getView();
            const tree = view.down('#navigation-tree');
            const tree_store = tree.getStore();
            const node = tree_store.findNode('id', record.get('id'));
            const menu_item = node.getRefOwner();
            menu_item.expand();
            setTimeout(function() {
                tree.setSelection(node);
                const node_el = tree.getItem(node).el;
                view.scrollTo(node_el.getX(), node_el.getY())
            }, 250);
            combo.setValue();
        }
    },
});
