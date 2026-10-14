(function () {
    var ID_ATTRIBUTE = '_id';

    Ext.define('CMDBuildUI.model.base.Base', {
        extend: 'Ext.data.Model',

        statics: {
            permissions: {
                add: '_can_create',
                clone: '_can_clone',
                delete: '_can_delete',
                edit: '_can_update',
                start: '_can_start',
                relgraph: '_relgraph_access',
                print: '_can_print',
                search: '_can_search'
            }
        },

        requires: [
            'CMDBuildUI.proxy.BaseProxy'
        ],

        fields: [{
            name: ID_ATTRIBUTE,
            type: 'string',
            persist: false
            // critical: true // This field is allways sent to server even if it has hot changed
        }],
        idProperty: ID_ATTRIBUTE,

        /**
         * @return {Numeric|String} Record id. Can be different from the value returned by this.getId() function.
         */
        getRecordId: function () {
            throw "The function getRecordId is not defined for the model " + Ext.ClassManager.getName(this);
        },
        /**
         * @return {Numeric|String} Record type. Can be different from the value returned by this.get("_type") function.
         */
        getRecordType: function () {
            throw "The function getRecordType is not defined for the model " + Ext.ClassManager.getName(this);
        },

        /**
         * Return 
         * @return {Object}
         */
        getCleanData: function () {
            var objectdata = {};
            var me = this;
            me.getFields().forEach(function (f) {
                // extract only model field 
                objectdata[f.name] = me.get(f.name);
            });
            return objectdata;
        },

        /**
         * @param {Object} newdata
         * @return {Object} Changes
         */
        updateDataFromObject: function (newdata) {
            // get changes
            var olddata = this.getCleanData();
            var changed = CMDBuildUI.util.Utilities.getObjectChanges(newdata, olddata);
            // start edit 
            this.beginEdit();
            // update changed attributes
            for (var attr in changed) {
                this.set(attr, changed[attr]);
            }
            // end edit
            this.endEdit();
            return changed;
        },

        /**
         * @return {Object} An object with original data for changed fields
         */
        getOriginalDataForChangedFields: function () {
            var data = {};
            for (var k in this.modified) {
                data[k] = this.previousValues[k];
            }
            return data;
        }
    });
})();
