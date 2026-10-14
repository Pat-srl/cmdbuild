Ext.define('CMDBuildUI.util.administration.helper.ModelHelper', {
    singleton: true,
    requires: ['CMDBuildUI.util.helper.ModelHelper'],

    parent: CMDBuildUI.util.helper.ModelHelper,

    /**
     * cleans the record as if it were just read by not changing the data
     *
     * @param {Ext.data.Model} model
     *
     * @returns {Ext.data.Model}
     */
    setReadState: function (model) {
        if (model && model.isModel) {
            model.crudState = 'R';
            model.crudStateWas = 'R';
            model.modified = {};
            model.previousValues = {};
            model.phantom = false;
            return model;
        }
        CMDBuildUI.util.Logger.log('setReadState of non model.', CMDBuildUI.util.Logger.levels.error);
        CMDBuildUI.util.Logger.log(model, CMDBuildUI.util.Logger.levels.error);
        return model;
    },

    /**
     *
     * @param {Array} onlyTypes
     */
    getGeoattributeTypes: function (onlyTypes) {
        var items = [
            {
                value: CMDBuildUI.model.map.GeoAttribute.type.geometry,
                label: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.geometry
            },
            {
                value: CMDBuildUI.model.map.GeoAttribute.type.shape,
                label: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.shape
            },
            {
                value: CMDBuildUI.model.map.GeoAttribute.type.geotiff,
                label: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.geotiff
            }
        ];
        if (onlyTypes && onlyTypes.length) {
            return Ext.Array.filter(items, function (item) {
                return onlyTypes.indexOf(item.value) !== -1;
            });
        }
        return items;
    },

    getGeoattributeSubtypes: function () {
        return [
            {
                label: 'LINE',
                value: CMDBuildUI.model.map.GeoAttribute.subtype.linestring
            },
            {
                label: 'POINT',
                value: CMDBuildUI.model.map.GeoAttribute.subtype.point
            },
            {
                label: 'POLYGON',
                value: CMDBuildUI.model.map.GeoAttribute.subtype.polygon
            }
        ];
    },

    getGeoattributesFillTypes: function () {
        return [
            {
                label: 'Solid',
                value: CMDBuildUI.model.gis.GeoStyle.fillType.solid
            },
            {
                label: 'Pattern',
                value: CMDBuildUI.model.gis.GeoStyle.fillType.pattern
            }
        ];
    },

    getGeoattributesFillPatterns: function () {
        const data = [];
        Ext.Object.each(CMDBuildUI.util.helper.MapHelper.patternRegistry, function (id, pattern) {
            data.push(Ext.apply({ id: id }, pattern));
        });
        return data;
    },

    getGeoattributesStrokeStyles: function () {
        return [
            {
                label: 'Dash',
                value: CMDBuildUI.model.map.GeoAttribute.strokeStyle.dash
            },
            {
                label: 'Dashdot',
                value: CMDBuildUI.model.map.GeoAttribute.strokeStyle.dashdot
            },
            {
                label: 'Dot',
                value: CMDBuildUI.model.map.GeoAttribute.strokeStyle.dot
            },
            {
                label: 'Longdash',
                value: CMDBuildUI.model.map.GeoAttribute.strokeStyle.longdash
            },
            {
                label: 'Longdashdot',
                value: CMDBuildUI.model.map.GeoAttribute.strokeStyle.longdashdot
            },
            {
                label: 'Solid',
                value: CMDBuildUI.model.map.GeoAttribute.strokeStyle.solid
            }
        ];
    },

    getCustomUITargetDevices: function () {
        return [
            {
                value: CMDBuildUI.model.custompages.CustomPage.device['default'],
                label: CMDBuildUI.locales.Locales.administration.common.labels.desktop
            },
            {
                value: CMDBuildUI.model.custompages.CustomPage.device.mobile,
                label: CMDBuildUI.locales.Locales.administration.common.labels.mobile
            }
        ];
    },

    getMenuTargetDevices: function () {
        return [
            {
                value: CMDBuildUI.model.menu.Menu.device['default'],
                label: CMDBuildUI.locales.Locales.administration.common.labels.desktop
            },
            {
                value: CMDBuildUI.model.menu.Menu.device.mobile,
                label: CMDBuildUI.locales.Locales.administration.common.labels.mobile
            }
        ];
    },

    getImportModes: function () {
        return [
            {
                value: CMDBuildUI.model.importexports.Template.importModes.add,
                label: CMDBuildUI.locales.Locales.administration.importexport.texts.add
            },
            {
                value: CMDBuildUI.model.importexports.Template.importModes.merge,
                label: CMDBuildUI.locales.Locales.administration.importexport.texts.merge
            }
        ];
    },
    getTaskNotificationMode: function () {
        return [
            {
                label: CMDBuildUI.locales.Locales.administration.tasks.onerrors,
                value: 'on_errors',
                group: ['import', 'export']
            },
            {
                label: CMDBuildUI.locales.Locales.administration.tasks.always,
                value: 'always',
                group: ['import', 'export']
            },
            {
                label: CMDBuildUI.locales.Locales.administration.tasks.never,
                value: 'never',
                group: ['import', 'export']
            }
        ];
    },

    getImportExportSources: function () {
        return [
            {
                label: CMDBuildUI.locales.Locales.administration.tasks.fileonserver,
                value: 'file',
                group: 'importexport'
            },
            {
                label: CMDBuildUI.locales.Locales.administration.tasks.url,
                value: 'url',
                group: 'importexport'
            }
        ];
    },

    getDWGImportSources: function () {
        return [
            {
                label: CMDBuildUI.locales.Locales.administration.tasks.fileonserver,
                value: 'filereader'
            },
            {
                label: CMDBuildUI.locales.Locales.administration.tasks.url,
                value: 'urlreader'
            }
        ];
    },

    getTaskModelNameByType: function (type, subType) {
        var modelName;
        switch (type) {
            case CMDBuildUI.model.tasks.Task.types.import_export:
            case CMDBuildUI.model.tasks.Task.types.export_file:
            case CMDBuildUI.model.tasks.Task.types.import_file:
                modelName = 'CMDBuildUI.model.tasks.TaskImportExport';
                break;
            case CMDBuildUI.model.tasks.Task.types.emailService:
                modelName = 'CMDBuildUI.model.tasks.TaskReadEmail';
                break;
            case CMDBuildUI.model.tasks.Task.types.workflow:
                modelName = 'CMDBuildUI.model.tasks.TaskStartWorkflow';
                break;

            case CMDBuildUI.model.tasks.Task.types.importgis:
                if (subType === 'cad') {
                    modelName = 'CMDBuildUI.model.tasks.TaskImportGisTemplate';
                } else if (subType === 'database') {
                    modelName = 'CMDBuildUI.model.tasks.TaskImportDatabase';
                } else if (subType === 'ifc') {
                    modelName = 'CMDBuildUI.model.tasks.TaskImportIfcTemplate';
                }
                break;
            case CMDBuildUI.model.tasks.Task.types.sendemail:
                modelName = 'CMDBuildUI.model.tasks.TaskSendEmail';
                break;
            case CMDBuildUI.model.tasks.Task.types.waterway:
                modelName = 'CMDBuildUI.model.tasks.TaskWaterWay';
                break;
            default:
                break;
        }
        return modelName;
    },

    getAttriubteGroupingDisplayModes: function () {
        return [
            {
                label: CMDBuildUI.locales.Locales.administration.attributes.texts.attributegroupingopen,
                value: CMDBuildUI.model.AttributeGrouping.displayMode.open
            },
            {
                label: CMDBuildUI.locales.Locales.administration.attributes.texts.attributegroupingclosed,
                value: CMDBuildUI.model.AttributeGrouping.displayMode.closed
            }
        ];
    },

    getDMSCountCheckModes: function (type) {
        return [
            {
                value: CMDBuildUI.model.dms.DMSModel.checkCount.no_check,
                label: CMDBuildUI.locales.Locales.administration.dmsmodels.nocheck
            },
            {
                value: CMDBuildUI.model.dms.DMSModel.checkCount.at_least_number,
                label: CMDBuildUI.locales.Locales.administration.dmsmodels.atleastnumber
            },
            {
                value: CMDBuildUI.model.dms.DMSModel.checkCount.exactly_number,
                label: CMDBuildUI.locales.Locales.administration.dmsmodels.exactlynumber
            },
            {
                value: CMDBuildUI.model.dms.DMSModel.checkCount.max_number,
                label: CMDBuildUI.locales.Locales.administration.dmsmodels.maxnumber
            }
        ];
    },

    getGateTypes: function () {
        return [
            {
                value: CMDBuildUI.model.importexports.Gate.gateType.single,
                label: CMDBuildUI.locales.Locales.administration.gates.singlehandler
            },
            {
                value: CMDBuildUI.model.importexports.Gate.gateType.cad,
                label: CMDBuildUI.locales.Locales.administration.gates.ofcadtype
            }
        ];
    },

    getShapeIncludeOrExcludeData: function () {
        return [
            {
                value: CMDBuildUI.model.importexports.GateGisHandler.includeOrExclude.all,
                label: CMDBuildUI.locales.Locales.administration.gates.all
            },
            {
                value: CMDBuildUI.model.importexports.GateGisHandler.includeOrExclude.include,
                label: CMDBuildUI.locales.Locales.administration.gates.include
            },
            {
                value: CMDBuildUI.model.importexports.GateGisHandler.includeOrExclude.exclude,
                label: CMDBuildUI.locales.Locales.administration.gates.exclude
            }
        ];
    },

    dateFormatsData: function () {
        return [
            {
                label: 'dd/mm/yyyy',
                value: 'd/m/Y'
            },
            {
                label: 'dd-mm-yyyy',
                value: 'd-m-Y'
            },
            {
                label: 'dd.mm.yyyy',
                value: 'd.m.Y'
            },
            {
                label: 'mm/dd/yyyy',
                value: 'm/d/Y'
            },
            {
                label: 'yyyy/mm/dd',
                value: 'Y/m/d'
            },
            {
                label: 'yyyy-mm-dd',
                value: 'Y-m-d'
            }
        ];
    },

    timeFormatsData: function () {
        return [
            {
                value: 'H:i:s',
                label: CMDBuildUI.locales.Locales.main.preferences.twentyfourhourformat
            },
            {
                value: 'h:i:s A',
                label: CMDBuildUI.locales.Locales.main.preferences.twelvehourformat
            }
        ];
    },

    decimalsSeparatorsData: function () {
        return [
            {
                value: ',',
                label: CMDBuildUI.locales.Locales.main.preferences.comma
            },
            {
                value: '.',
                label: CMDBuildUI.locales.Locales.main.preferences.period
            }
        ];
    },

    getCascadeActions: function () {
        return [
            {
                value: CMDBuildUI.model.domains.Domain.cascadeAction.restrict,
                label: CMDBuildUI.locales.Locales.administration.domains.texts.restrict
            },
            {
                value: CMDBuildUI.model.domains.Domain.cascadeAction.deletecard,
                label: CMDBuildUI.locales.Locales.administration.domains.texts._delete
            },
            {
                value: CMDBuildUI.model.domains.Domain.cascadeAction.setnull,
                label: CMDBuildUI.locales.Locales.administration.domains.texts.setnull
            },
            {
                value: CMDBuildUI.model.domains.Domain.cascadeAction.auto,
                label: CMDBuildUI.locales.Locales.administration.tasks.auto
            }
        ];
    },

    getReportFormats: function () {
        return [
            {
                value: 'pdf',
                label: 'PDF'
            },
            {
                value: 'odt',
                label: 'ODT'
            }
        ];
    },

    getSourceTypes: function () {
        return [
            {
                value: CMDBuildUI.model.importexports.GateDatabase.sourceTypes.jdbc,
                label: CMDBuildUI.locales.Locales.administration.tasks.jdbc
            }
        ];
    },
    getJdbcDrivers: function () {
        return [
            {
                value: CMDBuildUI.model.importexports.GateDatabase.jdbcDrivers.oracle,
                label: CMDBuildUI.locales.Locales.administration.tasks.oracle
            },
            {
                value: CMDBuildUI.model.importexports.GateDatabase.jdbcDrivers.postgres,
                label: CMDBuildUI.locales.Locales.administration.tasks.postgres
            },
            {
                value: CMDBuildUI.model.importexports.GateDatabase.jdbcDrivers.mysqlmaria,
                label: CMDBuildUI.locales.Locales.administration.tasks.mysqlmaria
            },
            {
                value: CMDBuildUI.model.importexports.GateDatabase.jdbcDrivers.sqlserver,
                label: CMDBuildUI.locales.Locales.administration.tasks.sqlserver
            }
        ];
    },
    getJdbcDriverAddress: function (driver) {
        switch (driver) {
            case CMDBuildUI.model.importexports.GateDatabase.jdbcDrivers.oracle:
                return 'jdbc:oracle:<DRIVER_TYPE>:@<HOST>:<PORT>/<DATABASE>';
            case CMDBuildUI.model.importexports.GateDatabase.jdbcDrivers.postgres:
                return 'jdbc:postgresql://<HOST>:<PORT>/<DATABASE>';
            case CMDBuildUI.model.importexports.GateDatabase.jdbcDrivers.mysqlmaria:
                return 'jdbc:mariadb://<HOST>:<PORT>/<DATABASE>';
            case CMDBuildUI.model.importexports.GateDatabase.jdbcDrivers.sqlserver:
                return 'jdbc:sqlserver://<HOST>\\SQLEXPRESS;databaseName=<DATABASE>';
            default:
                break;
        }
    },
    ifcAssociationModes: function () {
        return [
            {
                value: 'auto',
                label: CMDBuildUI.locales.Locales.administration.tasks.auto
            },
            {
                value: 'static',
                label: CMDBuildUI.locales.Locales.administration.gates.modestatic
            }
        ];
    },

    getUnitOfMeasures: function () {
        return [
            {
                value: 'AFTER',
                label: CMDBuildUI.locales.Locales.administration.common.strings.after
            },
            {
                value: 'BEFORE',
                label: CMDBuildUI.locales.Locales.administration.common.strings.before
            }
        ];
    },

    getFormulaTypes: function () {
        return [
            {
                value: CMDBuildUI.model.Attribute.formulaTypes.sql,
                label: CMDBuildUI.locales.Locales.administration.common.labels.funktion
            },
            {
                value: CMDBuildUI.model.Attribute.formulaTypes.script,
                label: CMDBuildUI.locales.Locales.administration.customcomponents.strings.script
            }
        ];
    },

    getShowPassword: function () {
        return [
            {
                value: CMDBuildUI.model.Attribute.showPassword.always,
                label: CMDBuildUI.locales.Locales.administration.attributes.strings.allusers
            },
            {
                value: CMDBuildUI.model.Attribute.showPassword.onwriteaccess,
                label: CMDBuildUI.locales.Locales.administration.attributes.strings.onwriteaccess
            },
            {
                value: CMDBuildUI.model.Attribute.showPassword.never,
                label: CMDBuildUI.locales.Locales.administration.attributes.strings.noone
            }
        ];
    },

    getAttributeStringTypes: function () {
        return [
            {
                value: false,
                label: CMDBuildUI.locales.Locales.administration.common.labels.default
            },
            {
                value: true,
                label: CMDBuildUI.locales.Locales.administration.emails.password
            }
        ];
    },

    getExecutionModes: function () {
        return [
            {
                value: 'realtime',
                label: CMDBuildUI.locales.Locales.administration.common.strings.realtime
            },
            {
                value: 'batch',
                label: CMDBuildUI.locales.Locales.administration.common.strings.batch
            }
        ];
    },

    getSearchfieldInGridsOptions: function (withDefault) {
        var data = [
            {
                value: 'true',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.enabled
            },
            {
                value: 'false',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.disabled
            }
        ];

        if (withDefault) {
            data.unshift({
                value: 'null',
                label: CMDBuildUI.locales.Locales.administration.common.labels.default
            });
        }
        return data;
    },

    emailTemplateAttachmentsFromCard: function () {
        return [
            {
                value: 'noone',
                label: CMDBuildUI.locales.Locales.administration.attributes.strings.noone
            },
            {
                value: 'all',
                label: CMDBuildUI.locales.Locales.administration.localizations.all
            },
            {
                value: 'fromFilter',
                label: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromfilter
            }
        ];
    },

    getGatesTypeForReadEmailTask: function () {
        return [
            {
                value: 'cad',
                label: CMDBuildUI.locales.Locales.administration.tasks.importgis
            },
            {
                value: 'database',
                label: CMDBuildUI.locales.Locales.administration.importexport.texts.importdatabase
            },
            {
                value: 'others',
                label: CMDBuildUI.locales.Locales.administration.tasks.others
            }
        ];
    },

    getIncludeAttachmentsData: function () {
        return [
            {
                value: 'attach_all',
                label: CMDBuildUI.locales.Locales.administration.tasks.attachall
            },
            {
                value: 'attach_inline',
                label: CMDBuildUI.locales.Locales.administration.tasks.attachinline
            }
        ];
    },

    getWebhookMethods: function () {
        return [
            {
                value: CMDBuildUI.model.webhooks.Webhook.methods.get,
                label: 'GET'
            },
            {
                value: CMDBuildUI.model.webhooks.Webhook.methods.put,
                label: 'PUT'
            },
            {
                value: CMDBuildUI.model.webhooks.Webhook.methods.post,
                label: 'POST'
            },
            {
                value: CMDBuildUI.model.webhooks.Webhook.methods.delete,
                label: 'DELETE'
            }
        ];
    },

    getWebhookEvents: function (objectType) {
        var data = [
            {
                value: CMDBuildUI.model.webhooks.Webhook.events.create,
                label: CMDBuildUI.locales.Locales.administration.webhooks.aftercreate
            },
            {
                value: CMDBuildUI.model.webhooks.Webhook.events.update,
                label: CMDBuildUI.locales.Locales.administration.webhooks.afterupdate
            },
            {
                value: CMDBuildUI.model.webhooks.Webhook.events.delete,
                label: CMDBuildUI.locales.Locales.administration.webhooks.afterdelete
            }
        ];

        if (objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
            data.push({
                value: CMDBuildUI.model.webhooks.Webhook.events.advance,
                label: CMDBuildUI.locales.Locales.administration.webhooks.afteradvance
            });
        }
        return data;
    }
});
