Ext.define('CMDBuildUI.view.administration.components.attributes.fieldsmanagement.group.Group', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.components.attributes.fieldsmanagement.group.GroupController',
        'CMDBuildUI.view.administration.components.attributes.fieldsmanagement.group.GroupModel',
        'CMDBuildUI.util.administration.helper.GridHelper'
    ],
    alias: 'widget.administration-components-attributes-fieldsmanagement-group-group',
    controller: 'administration-components-attributes-fieldsmanagement-group-group',
    viewModel: {
        type: 'administration-components-attributes-fieldsmanagement-group-group'
    },
    config: {
        group: null
    },
    scrollable: 'y',
    items: [CMDBuildUI.util.administration.helper.GridHelper.getDragAndDropReorderGrid([{
        xtype: 'widgetcolumn',
        maxHeight: '100%',
        flex: 1,
        widget: {
            xtype: 'administration-components-attributes-fieldsmanagement-group-form-row'
        },
        variableRowHeight: true
    }])],

    /**
     * 
     * @param {Boolean} hideMask 
     */
    updateGroupAndRefresh: function (hideMask) {
        var view = this.down('components-grid-reorder-grid');
        if (view) {
            var group = view.getStore().getRange();
            this.setGroup(group[0]);
            var panel = view.up('administration-attributes-fieldsmanagement-panel');
            view.up('fieldset').removeGroupGrid(view.getStore(), hideMask);
            if (hideMask) {
                panel.removeMask(hideMask);
            }
        }
    }
});