Ext.define('CMDBuildUI.view.fields.lookuparray.FieldController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-lookuparray-field',

    control: {
        '#': {
            change: 'onMainComboChange'
        }
    },

    onMainComboChange: function (combo, newvalue, oldvalue, eOpts) {
        var view = this.getView(),
            object = combo._ownerRecord;

        if (object && object.isModel) {
            var selected = combo.getValueRecords(),
                detailsLookupArray = [];

            Ext.Array.forEach(selected, function (item, index, allitems) {
                var elem = {};
                elem["code"] = item.get("code")
                elem["description"] = item.get("description");
                elem["_description_translation"] = item.get("_description_translation");
                elem["_id"] = item.getId();
                detailsLookupArray.push(elem);
            });

            object.set(Ext.String.format("_{0}_details", view.getName()), detailsLookupArray);
        }
    }

});
