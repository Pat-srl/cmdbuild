Ext.define('CMDBuildUI.model.importexports.Gate', {
    extend: 'CMDBuildUI.model.base.Base',
    requires: [
        'CMDBuildUI.validator.TrimPresence'
    ],
    statics: {
        gateType: {
            single: 'single',
            cad: 'cad',
            database: 'database',
            ifc: 'ifc',
            gis: 'gis'
        },

        getModelNameForType: function (gateType) {
            var name;
            switch (gateType) {
                case 'ifc':
                    name = 'CMDBuildUI.model.importexports.GateIfc';
                    break;
                case 'cad':
                case 'gis':
                    name = 'CMDBuildUI.model.importexports.GateGis';
                    break;
                case 'database':
                    name = 'CMDBuildUI.model.importexports.GateDatabase';
                    break;
                default:
                    Ext.raise('Wrong gateType!');
                    break;
            }
            return name;
        }
    },
    isGate: true,

    fields: [{
        name: 'code', // the name of template
        type: 'string',
        validators: ['trimpresence'],
        persist: true,
        critical: true
    }, {
        name: 'description', // the description of template
        type: 'string',
        validators: ['trimpresence'],
        persist: true,
        critical: true
    }, {
        name: 'description_composed',
        type: 'string',
        persist: false,
        critical: false,
        calculate: function (data) {
            var type;
            switch (data.config.tag) {
                case CMDBuildUI.model.importexports.Gate.gateType.ifc:
                    type = CMDBuildUI.locales.Locales.administration.navigation.ifcgatetemplate;
                    break;
                case CMDBuildUI.model.importexports.Gate.gateType.cad:
                case CMDBuildUI.model.importexports.Gate.gateType.gis:
                    type = CMDBuildUI.locales.Locales.administration.navigation.gisgatetemplate;
                    break;
                case CMDBuildUI.model.importexports.Gate.gateType.database:
                    type = CMDBuildUI.locales.Locales.administration.navigation.databasegatetemplate;
                    break;
            }
            return Ext.String.format("{0} - {1}", type, data.description);
        }
    }, {
        name: 'allowPublicAccess',
        type: 'boolean',
        persist: true,
        critical: true,
        defaultValue: false
    }, {
        name: 'processingMode',
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'realtime'
    }, {
        name: 'config',
        type: 'auto',
        persist: true,
        critical: true,
        defaultValue: {}
    }, {
        name: 'handlers',
        type: 'auto',
        defaultValue: [],
        persist: true,
        critical: true
    }, {
        name: '_handler_config',
        type: 'auto',
        defaultValue: {}
    }, {
        name: '_handler_type',
        type: 'string',
        mapping: 'config.tag'
    }, {
        name: 'importOn',
        type: 'auto',
        calculate: function (data) {
            var ret = [];
            if (data.config && data.config.showOnClasses && data.config.showOnClasses.length) {
                ret = data.config.showOnClasses.split(',');
            }
            return ret;
        }
    }, {
        name: 'enabled',
        type: 'boolean',
        defaultValue: true,
        persist: true,
        critical: true
    }],
    convertOnSet: false,
    proxy: {
        type: 'baseproxy',
        url: '/etl/gates',
        extraParams: {
            detailed: true
        }
    },

    copyForClone: function () {
        var newRecord = this.clone();
        newRecord.set('_id', undefined);
        newRecord.set('code', Ext.String.format('{0}_{1}', newRecord.get('code'), 'copy'));
        newRecord.set('handlers', this.getAssociatedData().handlers);
        newRecord.set('config', this.getAssociatedData().config);
        newRecord.crudState = "C";
        newRecord.phantom = true;
        newRecord.isClone = true;
        delete newRecord.crudStateWas;
        delete newRecord.previousValues;
        delete newRecord.modified;

        return newRecord;
    },

    save: function () {
        var associated = this.getAssociatedData();
        if (associated.handlers) {
            Ext.Array.forEach(associated.handlers, function (handler, index) {
                delete associated.handlers[index]._id;
                delete associated.handlers[index].script;
            });
            this.set('handlers', associated.handlers);
        }
        if (associated.config) {
            delete associated.config._id;
            this.set('config', associated.config);
        }
        this.callParent(arguments);
    }
});