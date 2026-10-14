Ext.define('CMDBuildUI.view.widgets.customform.ImportExportModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.widgets-customform-importexport',

    data: {
        // values
        filename: null,
        format: 'csv',
        separator: ';',
        selected: null,
        importmode: null
    },

    formulas: {
        /**
         * Hide separator
         */
        hideSeparator: {
            bind: {
                format: '{format}'
            },
            get: function (data) {
                return data.format !== 'csv';
            }
        },

        /**
         * Filename placeholder
         */
        filenameEmptyText: {
            bind: {
                format: '{format}'
            },
            get: function (data) {
                return data.format && '*.' + data.format;
            }
        },

        /**
         * Attributes store data
         */
        attributeStoreData: {
            get: function () {
                var view = this.getView(),
                    data = [];
                view.getAttributes().forEach(function (attr) {
                    var attrdata = attr.attributeconf;
                    if (attrdata.type.toLowerCase() === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase()) {
                        attrdata.type = CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey;
                    }
                    data.push(attrdata);
                });
                return data;
            }
        },

        /**
         * Update selection at popup opening
         */
        updateGridSelection: {
            bind: {
                count: '{attrs.totalCount}'
            },
            get: function (data) {
                this.set("selected", this.get("attrs").getRange());
            }
        }
    },

    stores: {
        /**
         * Attributes store
         */
        attrs: {
            fields: ['name', 'description'],
            proxy: 'memory',
            autoDestroy: true,
            data: '{attributeStoreData}'
        },

        /**
         * Formats combo store
         */
        formats: {
            proxy: 'memory',
            fields: ['value'],
            autoDestroy: true,
            data: [{
                value: 'csv'
            }, {
                value: 'xls'
            }, {
                value: 'xlsx'
            }]
        },

        /**
         * Separatros combo store
         */
        separators: {
            proxy: 'memory',
            fields: ['value'],
            autoDestroy: true,
            data: [{
                value: ','
            }, {
                value: ';'
            }, {
                value: '|'
            }]
        },

        /**
         * Import modes
         */
        importmodes: {
            proxy: 'memory',
            fields: ['value', 'label'],
            autoDestroy: true,
            data: [{
                value: 'add',
                label: CMDBuildUI.locales.Locales.widgets.customform.importexport.modeadd
            }, {
                value: 'merge',
                label: CMDBuildUI.locales.Locales.widgets.customform.importexport.modemerge
            }, {
                value: 'replace',
                label: CMDBuildUI.locales.Locales.widgets.customform.importexport.modereplace
            }]
        }
    }

});