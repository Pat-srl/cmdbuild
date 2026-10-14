Ext.define('CMDBuildUI.model.importexports.GateTemplate', {
    extend: 'CMDBuildUI.model.base.Base',
    
    requires: [       
        'CMDBuildUI.validator.TrimPresence'
    ],

    statics: {
        getMergeModes: function () {
            return [{
                label: CMDBuildUI.locales.Locales.administration.importexport.texts.nodelete, // 'No delete'
                value: CMDBuildUI.model.importexports.Template.missingRecords.nodelete // 'leave_missing'
            }, {
                label: CMDBuildUI.locales.Locales.administration.importexport.texts['delete'], // 'Delete'
                value: CMDBuildUI.model.importexports.Template.missingRecords['delete'] //'delete_missing'
            }, {
                label: CMDBuildUI.locales.Locales.administration.importexport.texts.modifycard, // 'Modify card'
                value: CMDBuildUI.model.importexports.Template.missingRecords.modifycard // 'update_attr_on_missing'
            }];
        },

        missingRecords: {
            'nodelete': 'leave_missing',
            'delete': 'delete_missing',
            'modifycard': 'update_attr_on_missing',
            'nomerge': 'no_merge'
        },
        importModes: {
            'add': 'add',
            'merge': 'merge'
        }
    },
    /**
     * This field is not returned by the servers but used for internal purpose
     */
    hasMany: [{
        name: 'columns',
        model: 'CMDBuildUI.model.importexports.GateAttribute',
        persist: true,
        critical: true,
        field: 'columns',
        storeConfig: {
            sorters: [{
                property: 'index',
                direction: 'ASC'
            }]
        }
    }],
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
            name: 'targetType', // it should be class
            validators: ['trimpresence'],
            type: 'string',
            persist: true,
            critical: true,
            defaultValue: 'class'
        }, {
            name: 'targetName', // the name of class
            validators: ['trimpresence'],
            type: 'string',
            persist: true,
            critical: true
        }, {
            name: 'type', // import
            type: 'string',
            validators: ['trimpresence'],
            persist: true,
            critical: true,
            defaultValue: 'import'
        }, {
            name: 'active',
            type: 'boolean',
            defaultValue: true,
            persist: true,
            critical: true
        }, {
            name: 'fileFormat', // it should be dwg
            validators: [],
            type: 'string',
            defaultValue: 'cad',
            persist: true,
            critical: true
        }, {
            // input type is combo with all attributes in attributes grid
            // visible only if
            name: 'importKeyAttributes',
            type: 'string',
            persist: true,
            critical: true,
            serialize: function (value, record) {
                if (record.get('_importKeyAttribute').length) {
                    return (typeof record.get('_importKeyAttribute') === 'string') ? record.get('_importKeyAttribute').split(',') : record.get('_importKeyAttribute');
                }
                return [];
            }
        }, {
            // input type is combo with all attributes in attributes grid
            // visible only if
            name: '_importKeyAttribute',
            type: 'string',
            calculate: function (data) {
                return (data.importKeyAttributes) ? (typeof data.importKeyAttributes === 'string') ? data.importKeyAttributes.split(',') : data.importKeyAttributes : [];
            }

        }, {
            // input type is combo with values: nodelete|delete|modifycard
            // visible only if this.type === inport|importexport
            name: 'mergeMode',
            type: 'string',
            persist: true,
            critical: true,
            defaultValue: 'leave_missing' //'no_merge'
        }, {
            // input type is combo with all attributes in grid
            // visible only if this.mergeMode === modifycard
            // mandatory if field is visible
            name: 'mergeMode_when_missing_update_attr',
            type: 'string',
            persist: true,
            critical: true
        }, {
            // input type is textfield
            // it can be active only if attibute is set        
            name: 'mergeMode_when_missing_update_value',
            defaultValue: null,
            type: 'auto',
            persist: true,
            critical: true
        }, {
            // input type is textfield
            // it can be active only if attibute is set
            name: '_mergeMode_when_missing_update_value_description',
            type: 'auto',
            persist: false
        },
        // TODO: move notification on GATE when server is done #3672
        // {
        //     // mandatory
        //     // input type is combo with all email templates
        //     name: 'errorTemplate',
        //     type: 'string',
        //     validators: ['trimpresence'],
        //     persist: true,
        //     critical: true
        // }, {
        //     name: 'notificationTemplate',
        //     type: 'string',
        //     persist: true,
        //     critical: true
        // }, {
        //     // mandatory
        //     // input type is combo with all email accounts
        //     name: 'errorAccount',
        //     type: 'string',
        //     persist: true,
        //     critical: true
        // }, 

        {

            name: 'columns',
            type: 'auto',
            defaultValue: [],
            persist: true,
            critical: true
        }, {
            // sperimental
            name: 'source',
            type: 'string',
            persist: true,
            critical: true
        }, {
            name: 'selection',
            defaultValue: true,
            type: 'boolean'
        }, {
            name: '_targetAttributes',
            type: 'auto',
            defaultValue: [],
            persist: false,
            critical: false
        }, {
            name: '_importMode',
            type: 'auto',
            calculate: function (data) {
                if (data.mergeMode === CMDBuildUI.model.importexports.Template.missingRecords.nomerge) {
                    return CMDBuildUI.model.importexports.Template.importModes.add;
                }
                return CMDBuildUI.model.importexports.Template.importModes.merge;
            }
        }
    ],
    convertOnSet: false,
    proxy: {
        type: 'baseproxy',
        url: '/etl/templates',
        extraParams: {
            detailed: true
        }
    },

    copyForClone: function () {
        var newRecord = this.clone();
        newRecord.set('_id', undefined);
        newRecord.set('code', Ext.String.format('{0}_{1}', newRecord.get('code'), 'copy')); 
        newRecord.set('description', Ext.String.format('{0}_{1}', newRecord.get('description'), 'copy'));        
        newRecord.set('columns', this.getAssociatedData().columns);
        newRecord._columns =  this.columns();
        newRecord.crudState = "C";
        newRecord.phantom = true;
        newRecord.isClone = true;
        delete newRecord.crudStateWas;
        delete newRecord.previousValues;
        delete newRecord.modified;

        return newRecord;
    },

    save: function (options) {
        var me = this;
        var columns = me.getAssociatedData().columns;
        if(columns){
            Ext.Array.forEach(columns, function (item) {
                if (item.columnName === 'CM_RELATIVE_LOCATION') {
                    item.mode = 'default';
                }
            });
            me.set('columns', columns);    
        }
        me.callParent(arguments);


    }
});