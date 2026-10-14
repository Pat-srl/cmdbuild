Ext.define('CMDBuildUI.model.domains.Clone', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'domain',
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: 'destination',
        type: 'string'
    }, {
        name: 'destinationDescription',
        type: 'string'
    }, {
        name: 'mode',
        type: 'string',
        serialize: function (v, record) {
            if (record.get("ignore")) {
                v = CMDBuildUI.util.helper.FiltersHelper.cloneFilters.ignore;
            } else if (record.get("migrates")) {
                v = CMDBuildUI.util.helper.FiltersHelper.cloneFilters.migrates;
            } else if (record.get("clone")) {
                v = CMDBuildUI.util.helper.FiltersHelper.cloneFilters.clone;
            }
            return v;
        }
    }, {
        name: 'ignore',
        type: 'boolean',
        calculate: function (record) {
            return record.mode === CMDBuildUI.util.helper.FiltersHelper.cloneFilters.ignore;
        }
    }, {
        name: 'migrates',
        type: 'boolean',
        calculate: function (record) {
            return record.mode === CMDBuildUI.util.helper.FiltersHelper.cloneFilters.migrates;
        }
    }, {
        name: 'clone',
        type: 'boolean',
        calculate: function (record) {
            return record.mode === CMDBuildUI.util.helper.FiltersHelper.cloneFilters.clone;
        }
    }, {
        name: 'cards',
        type: 'auto'
    }],

    hasChecks: function () {
        var ignore = this.get('ignore');
        var migrates = this.get('migrates');
        var clone = this.get('clone');

        return ignore || migrates || clone;
    }
});
