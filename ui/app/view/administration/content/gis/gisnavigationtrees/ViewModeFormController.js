Ext.define('CMDBuildUI.view.administration.content.gisnavigationtrees.ViewModeFormController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-gisnavigationtrees-viewmodeform',

    control: {
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    onCancelBtnClick: function(button){
        var view = this.getView(); 
        view.up().fireEventArgs('close');
    },

    onSaveBtnClick: function (button) {
        var view = this.getView();
        var store = this.getViewModel().getStore('subclassesStore');
        var record = view.lookupViewModel().get('record');
        var nodes = this.getCheckedRecords(store.getRange());        
        var subclassFilter = [];        
        Ext.Array.forEach(nodes, function (node) {
            subclassFilter.push(node.name);            
            record.set(Ext.String.format('subclass_{0}_description', node.name), node.description);
        });

        record.set('subclassFilter', subclassFilter.length ? subclassFilter.join(',') : '');        
        view.up().fireEventArgs('close');

    },

    privates: {
        getCheckedRecords: function (data) {
            var me = this,
                records = [];

            Ext.Array.forEach(data, function (item) {
                var children = !item.isModel ? item.children : item.get('children');

                // delete item.children || item.data.children;
                if (item.checked) {
                    records.push(Ext.copy(item));
                }
                if (children && children.length) {
                    records = Ext.Array.merge(records, me.getCheckedRecords(children)) || [];
                }
            });
            Ext.Array.forEach(records, function (record) {
                record.children = [];
            });
            return records;
        }
    }

});