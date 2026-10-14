Ext.define('CMDBuildUI.model.importexports.Template', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: ['CMDBuildUI.validator.TrimPresence'],

    statics: {
        types: {
            import: 'import',
            export: 'export',
            importexport: 'import_export'
        },

        getTemplateTypes: function () {
            return [
                {
                    label: CMDBuildUI.locales.Locales.administration.importexport.texts['import'],
                    value: CMDBuildUI.model.importexports.Template.types['import']
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.importexport.texts['export'],
                    value: CMDBuildUI.model.importexports.Template.types['export']
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.importexport.texts.importexportfile,
                    value: CMDBuildUI.model.importexports.Template.types.importexport
                }
            ];
        },

        getTargetTypes: function (fileFormat) {
            if (['database', 'ifc', 'cad'].indexOf(fileFormat) > -1) {
                return [
                    {
                        label: CMDBuildUI.locales.Locales.administration.localizations['class'],
                        value: CMDBuildUI.model.administration.MenuItem.types.klass
                    },
                    {
                        label: CMDBuildUI.locales.Locales.administration.localizations.domain,
                        value: CMDBuildUI.model.administration.MenuItem.types.domain
                    }
                ];
            }
            return [
                {
                    label: CMDBuildUI.locales.Locales.administration.localizations['class'],
                    value: CMDBuildUI.model.administration.MenuItem.types.klass
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.localizations.process,
                    value: CMDBuildUI.util.helper.ModelHelper.objecttypes.process
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.localizations.domain,
                    value: CMDBuildUI.model.administration.MenuItem.types.domain
                },
                {
                    label: CMDBuildUI.locales.Locales.joinviews.joinview,
                    value: CMDBuildUI.model.administration.MenuItem.types.view
                }
            ];
        },
        fileTypes: {
            csv: 'csv',
            xlsx: 'xlsx',
            xls: 'xls',
            database: 'database',
            ifc: 'ifc',
            cad: 'cad'
        },
        getFileTypes: function (direction) {
            var fileTypes = [
                {
                    label: CMDBuildUI.locales.Locales.administration.importexport.texts.csv,
                    value: CMDBuildUI.model.importexports.Template.fileTypes.csv
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.importexport.texts.xlsx,
                    value: CMDBuildUI.model.importexports.Template.fileTypes.xlsx
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.importexport.texts.xls,
                    value: CMDBuildUI.model.importexports.Template.fileTypes.xls
                }
            ];
            return fileTypes;
        },
        getMergeModes: function () {
            return [
                {
                    label: CMDBuildUI.locales.Locales.administration.importexport.texts.nodelete, // 'No delete'
                    value: CMDBuildUI.model.importexports.Template.missingRecords.nodelete // 'leave_missing'
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.importexport.texts['delete'], // 'Delete'
                    value: CMDBuildUI.model.importexports.Template.missingRecords['delete'] //'delete_missing'
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.importexport.texts.modifycard, // 'Modify card'
                    value: CMDBuildUI.model.importexports.Template.missingRecords.modifycard // 'update_attr_on_missing'
                }
            ];
        },

        getCsvSeparators: function () {
            // used also in app/view/main/header/PreferencesModel.js
            return [
                {
                    value: ',',
                    label: CMDBuildUI.locales.Locales.administration.systemconfig.comma
                },
                {
                    value: ';',
                    label: CMDBuildUI.locales.Locales.administration.systemconfig.semicolon
                },
                {
                    value: '|',
                    label: CMDBuildUI.locales.Locales.administration.systemconfig.pipe
                },
                {
                    value: '\u0009',
                    label: CMDBuildUI.locales.Locales.administration.systemconfig.tab
                }
            ];
        },

        missingRecords: {
            nodelete: 'leave_missing',
            delete: 'delete_missing',
            modifycard: 'update_attr_on_missing',
            nomerge: 'no_merge'
        },
        importModes: {
            add: 'add',
            merge: 'merge'
        }
    },

    isTemplate: true,

    /**
     * This field is not returned by the servers but used for internal purpose
     */
    hasMany: [
        {
            name: 'columns',
            model: 'CMDBuildUI.model.importexports.Attribute',
            persist: true,
            critical: true,
            field: 'columns'
        }
    ],
    fields: [
        {
            name: 'code', // the name of template
            type: 'string',
            validators: ['trimpresence'],
            persist: true,
            critical: true
        },
        {
            name: 'description', // the description of template
            type: 'string',
            validators: ['trimpresence'],
            persist: true,
            critical: true
        },
        {
            name: 'description_composed',
            type: 'string',
            persist: false,
            critical: false,
            calculate: function (data) {
                return Ext.String.format(
                    '{0} - {1}',
                    CMDBuildUI.locales.Locales.administration.navigation.datatemplate,
                    data.description
                );
            }
        },
        {
            name: 'targetType', // it can be class|domain
            validators: ['trimpresence'],
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'targetName', // the name of class/domain
            validators: ['trimpresence'],
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'type', // import/export/both
            type: 'string',
            validators: ['trimpresence'],
            persist: true,
            critical: true
        },
        {
            name: 'active',
            type: 'boolean',
            defaultValue: true,
            persist: true,
            critical: true
        },
        {
            name: 'fileFormat', // csv/xlsx/xls optional
            validators: [],
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'ignoreColumnOrder',
            type: 'boolean',
            persist: true,
            critical: true,
            defaultValue: true
        },
        {
            name: 'useHeader',
            type: 'boolean',
            persist: true,
            critical: true,
            defaultValue: true
        },
        {
            name: 'headerRow', // only if fileFormat  is xlsx/xls
            type: 'string',
            defaultValue: 1,
            persist: true,
            critical: true
        },
        {
            name: 'dataRow', // only if fileFormat  is xlsx/xls
            type: 'string',
            defaultValue: 2,
            persist: true,
            critical: true
        },
        {
            name: 'firstCol', // only if fileFormat  is xlsx/xls
            type: 'number'
            // defaultValue: 1, // unused from 26/06/19
            // persist: true, // unused from 26/06/19
            // critical: true // unused from 26/06/19
        },
        // {
        //     // input type is combo with all attributes in attributes grid
        //     // visible only if
        //     name: 'importKeyAttribute',
        //     type: 'string',
        //     persist: true,
        //     critical: true
        // },
        {
            // input type is combo with all attributes in attributes grid
            // visible only if
            name: 'importKeyAttributes',
            type: 'string',
            persist: true,
            critical: true,
            serialize: function (value, record) {
                if (record.get('_importKeyAttribute').length) {
                    return typeof record.get('_importKeyAttribute') === 'string'
                        ? record.get('_importKeyAttribute').split(',')
                        : record.get('_importKeyAttribute');
                }
                return [];
            }
        },
        {
            // input type is combo with all attributes in attributes grid
            // visible only if
            name: '_importKeyAttribute',
            type: 'string',
            calculate: function (data) {
                return data.importKeyAttributes
                    ? typeof data.importKeyAttributes === 'string'
                        ? data.importKeyAttributes.split(',')
                        : data.importKeyAttributes
                    : [];
            }
        },
        {
            // input type is combo with values: nodelete|delete|modifycard
            // visible only if this.type === inport|importexport
            name: 'mergeMode',
            type: 'string',
            persist: true,
            critical: true,
            defaultValue: 'leave_missing' //'no_merge'
        },
        {
            // input type is combo with all attributes in grid
            // visible only if this.mergeMode === modifycard
            // mandatory if field is visible
            name: 'mergeMode_when_missing_update_attr',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            // input type is textfield
            // it can be active only if attibute is set
            name: 'mergeMode_when_missing_update_value',
            defaultValue: null,
            type: 'auto',
            persist: true,
            critical: true
        },
        {
            // input type is textfield
            // it can be active only if attibute is set
            name: '_mergeMode_when_missing_update_value_description',
            type: 'auto',
            persist: false
        },
        {
            // it can be active only if template type is import || importexport
            name: 'importFilter',
            type: 'string',
            persist: true,
            critical: true,
            defaultValue: '{}'
        },
        {
            // it can be active only if template type is import || importexport
            name: 'exportFilter',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'notificationTemplate',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            // mandatory
            // input type is combo with all email accounts
            name: 'errorAccount',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            // mandatory && visible only if fileFormat is csv
            name: 'csv_separator',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'columns',
            type: 'auto',
            defaultValue: [],
            persist: true,
            critical: true
        },
        {
            // sperimental
            name: 'source',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'selection',
            defaultValue: true,
            type: 'boolean'
        },
        {
            name: '_targetAttributes',
            type: 'auto',
            defaultValue: [],
            persist: false,
            critical: false
        },
        {
            name: '_importMode',
            type: 'auto',
            calculate: function (data) {
                if (data.mergeMode === CMDBuildUI.model.importexports.Template.missingRecords.nomerge) {
                    return CMDBuildUI.model.importexports.Template.importModes.add;
                }
                return CMDBuildUI.model.importexports.Template.importModes.merge;
            }
        },
        {
            name: '_importMergeSkip',
            type: 'boolean',
            persist: true,
            critical: true
        },
        {
            name: 'enableCreate',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'charset',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'dateFormat',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'timeFormat',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            name: 'dateTimeFormat',
            type: 'string',
            persist: true,
            critical: true,
            serialize: function (value, record) {
                return Ext.String.format(
                    '{0} {1}',
                    record.get('dateFormat') ||
                        CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.fields.dateFormat),
                    record.get('timeFormat') ||
                        CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.fields.timeFormat)
                );
            }
        },
        {
            name: 'decimalSeparator',
            type: 'string',
            persist: true,
            critical: true
        },
        {
            // for import csv/xls/xlsx only
            // visible only if type === import && mergeMode === ('delete_missing'|'update_attr_on_missing')
            name: 'handleMissingRecordsOnError',
            type: 'boolean',
            persist: true,
            critical: true,
            defaultValue: false
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
        newRecord._columns = this._columns;
        newRecord.crudState = 'C';
        newRecord.phantom = true;
        newRecord.isClone = true;
        delete newRecord.crudStateWas;
        delete newRecord.previousValues;
        delete newRecord.modified;

        return newRecord;
    },

    /**
     * in some cases when save template from admin, the request url is not correct
     * we need to set the correct url every time wee need to save
     * see issues #4378 and #4700
     * @extends
     */
    load: function () {
        if (CMDBuildUI.util.Ajax.getViewContext() === 'admin') {
            this.getProxy().setUrl('/etl/templates');
        }
        this.callParent(arguments);
    },
    /**
     * in some cases when save template from admin, the request url is not correct
     * we need to set the correct url every time wee need to save
     * see issues #4378 and #4700
     * @extends
     */
    save: function () {
        if (CMDBuildUI.util.Ajax.getViewContext() === 'admin') {
            this.getProxy().setUrl('/etl/templates');
        }
        this.callParent(arguments);
    },

    /**
     * in some cases when save template from admin, the request url is not correct
     * we need to set the correct url every time wee need to save
     * see issues #4378 and #4700
     * @extends
     */
    erase: function () {
        if (CMDBuildUI.util.Ajax.getViewContext() === 'admin') {
            this.getProxy().setUrl('/etl/templates');
        }
        this.callParent(arguments);
    }
});
