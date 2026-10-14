
Ext.define('CMDBuildUI.view.administration.components.attributes.fieldsmanagement.Fieldset', {
    extend: 'Ext.form.FieldSet',

    requires: [
        'CMDBuildUI.view.administration.components.attributes.fieldsmanagement.FieldsetController',
        'CMDBuildUI.view.administration.components.attributes.fieldsmanagement.FieldsetModel'
    ],
    alias: 'widget.administration-components-attributes-fieldsmanagement-fieldset',
    controller: 'administration-components-attributes-fieldsmanagement-fieldset',
    viewModel: {
        type: 'administration-components-attributes-fieldsmanagement-fieldset'
    },
    config: {
        group: null       
    },
    style: 'padding-right: 0',
    layout: {
        type: 'hbox',
        align: 'stretch'
    },
    ui: 'administration-formpagination',
    items: [],

    // maxHeight: '450',
    /**
     * For some reason when we remove a row from store the grid re-present the record
     * when new clean record is added.
     * 
     * Trick: we remove group component and add again with fresh data
     * @param {Ext.data.Store} rows 
     */
    removeGroupGrid: function (rows, hideMask) {
        var view = this;
        var panel = view.up('administration-attributes-fieldsmanagement-panel');
        // show the loader
        if(!hideMask){
            panel.addMask();
        }
        // get the current group data
        var group = this.getGroup();

        group.set('rows', rows.getRange());
        view.remove(view.down('administration-components-attributes-fieldsmanagement-group-group'));
        // to prevent multiple page resize and scrollbar moves, add new group 
        // with hidden=true config
        view.insert(0, {
            xtype: 'administration-components-attributes-fieldsmanagement-group-group',
            group: group,            
            region: 'center',
            flex: 0.75,            
            border: 1            
        });
    }
});
