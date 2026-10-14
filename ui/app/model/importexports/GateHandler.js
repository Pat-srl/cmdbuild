Ext.define('CMDBuildUI.model.importexports.GateHandler', {
    extend: 'CMDBuildUI.model.base.Base',
    statics: {
        gatetemplatesStore: null,
        type: {
            cad: 'cad',
            script: 'script',
            database: 'database',
            ifc: 'ifc'
        }
    },

    fields: [{
        name: 'type', // cad/script/database/ifc
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: ''
    }, {
        // if type === 'script'
        // pack
        name: 'script',
        type: 'string',
        critical: true,
        persist: true
    }, {
        // if type === 'cad|database|ifc'
        //optional, list of cad import templates (by code)
        name: 'templates',
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: ""
    }],

    proxy: {
        type: 'memory'
    },
    addTemplate: function (templateName) {
        var templates = this.get('templates');
        if (templates.length) {
            var currentTemplates = templates.split(',');
            if (currentTemplates.indexOf(templateName) < 0) {
                currentTemplates.push(templateName);
                this.set('templates', currentTemplates.join(','));
            }
        } else {
            this.set('templates', templateName);
        }
        return this;
    },
    removeTemplate: function (templateName) {
        var templatesList = Ext.Array.remove(this.get('templates').split(','), templateName).join(',');
        this.set('templates', templatesList);
        return this;
    },

    getTemplates: function () {
        var me = this;
        var deferred = new Ext.Deferred();

        this.gatetemplatesStore = this.gatetemplatesStore || Ext.getStore('importexports.GateTemplates');
        var store = this.gatetemplatesStore;
        store.setAdvancedFilter({
            attribute: {
                simple: {
                    attribute: "code",
                    operator: "in",
                    value: me.get('templates').split(',')
                }
            }
        });

        store.remoteSort = false;
        store.on('datachanged', function (_store) {
            if (me.get("templates")) {
                var templates = me.get('templates').split(',');
                _store.each(function (item) {
                    if (templates.indexOf(item.get('code')) > -1) {
                        item.set('index', templates.indexOf(item.get('code')));
                    }
                });
            }

            return _store.getRange();
        });
        store.load({
            single: true,
            callback: function (records, operation, success) {
                if (success) {
                    deferred.resolve(this, true);
                }
            }
        });

        return deferred.promise;

    },

    setTemplates: function (templateCodesArray) {
        var deferred = new Ext.Deferred();
        this.set('templates', templateCodesArray.join(','));
        this.getTemplates().then(function (store) {
            deferred.resolve(this, store);
        });
        return deferred.promise;

    }
});