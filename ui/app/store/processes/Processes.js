Ext.define('CMDBuildUI.store.processes.Processes', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base',
        'CMDBuildUI.model.processes.Process'
    ],

    alias: 'store.processes',

    model: 'CMDBuildUI.model.processes.Process',

    sorters: ['description'],
    pageSize: 0, // disable pagination
    autoLoad: false,
    filters: [
        function (item) {
            return item.get('name') !== CMDBuildUI.model.processes.Process.masterParentClass;
        }
    ],
    listeners: {
        /**
         * 
         * @param {Ext.data.Store} store 
         * @param {Ext.data.operation.Operation} operation 
         * @param {Object} eOpts 
         */
        beforeload: function (store, operation, eOpts) {
            var wfEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled);
            if (wfEnabled) {
                return true;
            }
            return false;
        }
    }

});