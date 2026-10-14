Ext.define('CMDBuildUI.view.fields.groupedcombobox.GroupedComboBox', {
    extend: 'Ext.form.field.ComboBox',
    alias: ['widget.groupedcombo', 'widget.groupedcombobox'],
    grouper: null,

    onBindStore: function (store, initial) {
        var me = this;
        this.callParent(arguments);
        if (store.getGrouper()) {
            this.grouper = store.getGrouper().getProperty();
            if (this.grouper) {
                this.tpl = new Ext.XTemplate(
                    '<tpl for=".">',
                    '<tpl if="this.showHeader(' + me.grouper + ')"><li class="group-header">{' + me.grouper + '}</li></tpl>',
                    '<li role="option" unselectable="on" class="x-boundlist-item">{' + me.getDisplayField() + '}</li>',
                    '</tpl>', {
                        showHeader: function (group) {
                            if (this._currentgroup !== group) {
                                this._currentgroup = group;
                                return true;
                            }
                            return false;
                        }
                    }
                );
            }
        }
    }
});