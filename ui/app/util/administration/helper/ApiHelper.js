Ext.define('CMDBuildUI.util.administration.helper.ApiHelper', {
    singleton: true,
    requires: ['CMDBuildUI.util.Config'],

    client: {
        basePath: 'administration',
        getClassUrl: function (className) {
            if (className) {
                return Ext.String.format('{0}/classes/{1}', this.basePath, className);
            }
            return Ext.String.format('{0}/classes_empty', this.basePath);
        },
        getProcessUrl: function (processName) {
            if (processName) {
                return Ext.String.format('{0}/processes/{1}', this.basePath, processName);
            }
            return Ext.String.format('{0}/processes_empty', this.basePath);
        },

        getDomainUrl: function (domainName) {
            if (domainName) {
                return Ext.String.format('{0}/domains/{1}', this.basePath, domainName);
            }
            return Ext.String.format('{0}/domains_empty', this.basePath);
        },
        getCustomPageUrl: function (customPageId, showForm) {
            if (customPageId) {
                return Ext.String.format('{0}/custompages/{1}', this.basePath, customPageId);
            }
            if (!showForm) {
                return Ext.String.format('{0}/custompages', this.basePath);
            }
            return Ext.String.format('{0}/custompages/_new/{1}', this.basePath, showForm);
        },
        getCustomComponentUrl: function (componentType, customPageId) {
            if (customPageId && !isNaN(customPageId)) {
                return Ext.String.format('{0}/customcomponents/{1}/{2}', this.basePath, componentType, customPageId);
            }
            return Ext.String.format(
                '{0}/customcomponents{1}/{2}/{3}',
                this.basePath,
                customPageId ? '' : '_empty',
                componentType,
                customPageId || 'false'
            );
        },
        getDashboardUrl: function (dashboardId) {
            if (dashboardId && !isNaN(dashboardId)) {
                return Ext.String.format('{0}/dashboards/{1}', this.basePath, dashboardId);
            }
            return Ext.String.format('{0}/dashboards', this.basePath);
        },
        getHomepageUrl: function (homepageId) {
            if (homepageId && !isNaN(homepageId)) {
                return Ext.String.format('{0}/homepages/{1}', this.basePath, homepageId);
            }
            return Ext.String.format('{0}/homepages', this.basePath);
        },
        getReportUrl: function (reportId) {
            if (reportId) {
                return Ext.String.format('{0}/reports/{1}', this.basePath, reportId);
            }
            return Ext.String.format('{0}/reports/_new', this.basePath);
        },
        getViewUrl: function (viewId) {
            if (viewId) {
                return Ext.String.format('{0}/views/{1}', this.basePath, viewId);
            }
            return Ext.String.format('{0}/views/_new', this.basePath);
        },
        getJoinViewUrl: function (viewId) {
            if (viewId) {
                return Ext.String.format('{0}/joinviews/{1}', this.basePath, viewId);
            }
            return Ext.String.format('{0}/joinviews_empty/false', this.basePath);
        },
        getTheMenuUrl: function (menuId, device) {
            if (menuId) {
                return Ext.String.format('{0}/menus/{1}/{2}', this.basePath, device, menuId);
            }
            return Ext.String.format('{0}/menus_empty/{1}', this.basePath, device);
        },
        getGisMenuUrl: function (menuId) {
            if (menuId) {
                return Ext.String.format('{0}/gis/gismenu/{1}', this.basePath, menuId);
            }
            return Ext.String.format('{0}/gis/gismenu/false', this.basePath);
        },
        getTheViewFilterUrl: function (filterName, showform) {
            showform = typeof showform === 'undefined' ? false : showform;
            if (filterName) {
                return Ext.String.format('{0}/searchfilters/{1}', this.basePath, filterName);
            }
            return Ext.String.format('{0}/searchfilters/_new/{1}', this.basePath, showform);
        },
        getUsersUrl: function () {
            return Ext.String.format('{0}/users', this.basePath);
        },
        getEmailAccountsUrl: function () {
            return Ext.String.format('{0}/email/accounts', this.basePath);
        },
        getTaskManagerReadEmailsUrl: function () {
            return Ext.String.format('{0}/tasks/reademails', this.basePath);
        },
        getGISManageIconsUrl: function () {
            return Ext.String.format('{0}/gis/manageicons', this.basePath);
        },
        getBIMProjectsUrl: function () {
            return Ext.String.format('{0}/bim/projects', this.basePath);
        },
        getLocalizationConfigurationUrl: function () {
            return Ext.String.format('{0}/localizations/configuration', this.basePath);
        },
        getGeneralOptionsUrl: function () {
            return Ext.String.format('{0}/setup/generaloptions', this.basePath);
        },
        getAiConfigurationsUrl: function () {
            return Ext.String.format('{0}/setup/ai', this.basePath);
        },
        getImportExportDataTemplatesUrl: function (templateId) {
            if (templateId) {
                return Ext.String.format('{0}/importexport/datatemplates/{1}', this.basePath, templateId);
            }
            return Ext.String.format('{0}/importexport/datatemplates_empty/false', this.basePath);
        },
        getCloneImportExportDataTemplatesUrl: function (templateId) {
            return Ext.String.format('{0}/importexport/datatemplates/clone/{1}', this.basePath, templateId);
        },
        getSchedulesUrl: function () {
            return Ext.String.format('{0}/schedules/ruledefinitions', this.basePath);
        },
        getTheLookupTypeUrl: function (lookupHash) {
            if (lookupHash) {
                return Ext.String.format('{0}/lookup_types/{1}', this.basePath, lookupHash);
            }
            return Ext.String.format('{0}/lookup_types_empty', this.basePath);
        },

        getPermissionUrl: function (id) {
            if (id) {
                return Ext.String.format('{0}/groupsandpermissions/{1}', this.basePath, id);
            }
            return Ext.String.format('{0}/groupsandpermissions_empty', this.basePath);
        },

        getTaskReadEmailsUrl: function () {
            return Ext.String.format('{0}/tasks/emailService', this.basePath);
        },

        getTaskConnectorUrl: function () {
            return Ext.String.format('{0}/tasks/etl/database', this.basePath);
        },

        getDmsModelUrl: function (modelName) {
            if (modelName) {
                return Ext.String.format('{0}/dmsmodels/{1}', this.basePath, modelName);
            }
            return Ext.String.format('{0}/dmsmodels_empty', this.basePath);
        },

        getDmsCategoryUrl: function (categoryNameHash) {
            if (typeof categoryNameHash !== 'undefined') {
                return Ext.String.format('{0}/dmscategories/{1}', this.basePath, categoryNameHash);
            }
            return Ext.String.format('{0}/dmscategories_empty', this.basePath);
        },

        getGateTemplateUrl: function (gateType, gateId) {
            if (gateId === true) {
                return Ext.String.format(
                    '{0}/importexport/gatetemplates_empty/{1}/{2}',
                    this.basePath,
                    gateType,
                    gateId
                );
            }

            if (typeof gateId !== 'undefined') {
                return Ext.String.format('{0}/importexport/gatetemplates/{1}/{2}', this.basePath, gateType, gateId);
            }
            return Ext.String.format('{0}/importexport/gatetemplates_empty/{1}/false', this.basePath, gateType);
        },

        getBusDescriptorUrl: function (descriptorCode) {
            if (descriptorCode) {
                return Ext.String.format('{0}/bus/descriptors/{1}', this.basePath, descriptorCode);
            }
            return Ext.String.format('{0}/bus/descriptors_empty/false', this.basePath);
        },

        getPluginManagerUrl: function (pluginCode) {
            if (pluginCode) {
                return Ext.String.format('{0}/pluginmanager/{1}', this.basePath, pluginCode);
            }
            return Ext.String.format('{0}/pluginmanager_empty', this.basePath);
        }
    },

    server: {
        baseUrl: CMDBuildUI.util.Config.baseUrl,
        getAttributeUrl: function (objectName) {
            if (objectName) {
                var objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(objectName);
                if (objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel) {
                    return Ext.String.format('{0}/dms/models/{1}/attributes', this.baseUrl, objectName);
                } else {
                    return Ext.String.format(
                        '{0}/{1}/{2}/attributes',
                        this.baseUrl,
                        Ext.util.Inflector.pluralize(objectType.toLowerCase()),
                        objectName
                    );
                }
            }
        },
        getDropCacheUrl: function () {
            return Ext.String.format('{0}/system/cache/drop', this.baseUrl);
        },
        getLookupValuesUrl: function (lookupType, valueId) {
            if (lookupType) {
                return Ext.String.format(
                    '{0}/lookup_types/{1}/values/{2}',
                    this.baseUrl,
                    CMDBuildUI.util.Utilities.stringToHex(lookupType),
                    valueId
                );
            }
            return Ext.String.format(
                '{0}/lookup_types/{1}/values',
                this.baseUrl,
                CMDBuildUI.util.Utilities.stringToHex(lookupType)
            );
        },

        getAllDmsCategoriesValues: function () {
            return Ext.String.format('{0}/dms/categories/_ALL/values', this.baseUrl);
        },

        getDMSCategoryUrl: function (categoryType) {
            if (categoryType) {
                return Ext.String.format(
                    '{0}/dms/categories/{1}',
                    this.baseUrl,
                    CMDBuildUI.util.Utilities.stringToHex(categoryType)
                );
            }
            return Ext.String.format('{0}/dms/categories', this.baseUrl);
        },
        getDMSCategoryValuesUrl: function (categoryType, valueId) {
            if (categoryType) {
                return Ext.String.format(
                    '{0}/dms/categories/{1}/values/{2}',
                    this.baseUrl,
                    CMDBuildUI.util.Utilities.stringToHex(categoryType),
                    valueId
                );
            }
            return Ext.String.format(
                '{0}/dms/categories/{1}/values',
                this.baseUrl,
                CMDBuildUI.util.Utilities.stringToHex(categoryType)
            );
        },
        getTheMenuUrl: function (name) {
            return Ext.String.format('{0}/menu{1}', this.baseUrl, name ? Ext.String.format('/{0}', name) : '');
        },
        getPermissionUrl: function (id) {
            return Ext.String.format('{0}/roles{1}', this.baseUrl, id ? Ext.String.format('/{0}', id) : '');
        },
        getPermissionFiltersUrl: function (id) {
            return Ext.String.format('{0}/filters', this.getPermissionUrl(id));
        },
        getProcessActivity: function (processName, activityName) {
            if (activityName) {
                return Ext.String.format('{0}/processes/{1}/activities/{2}', this.baseUrl, processName, activityName);
            }
            return Ext.String.format('{0}/processes/{1}/activities', this.baseUrl, processName);
        },

        /**
         * Get class schema for give type
         * @param {String} extension The file extension (PDF|ODT)
         * @param {String} className
         */
        getDownloadSchemaUrl: function (extension, className, objectType) {
            if (!objectType) {
                objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(className);
            }
            var typeBaseUrl;
            switch (objectType) {
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel:
                    typeBaseUrl = 'classes';
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                    typeBaseUrl = 'processes';
                    break;
                default:
                    break;
            }
            var uri = Ext.String.format(
                '{0}/{1}/{2}/print_schema/schema_{2}.{3}?extension={3}',
                CMDBuildUI.util.Config.baseUrl,
                typeBaseUrl,
                className,
                extension
            );
            return encodeURI(uri);
        },

        getSchedulesTriggerUrl: function (triggerId) {
            if (triggerId) {
                return Ext.String.format('{0}/calendar/triggers/{1}', this.baseUrl, triggerId);
            }
            return Ext.String.format('{0}/calendar/triggers', this.baseUrl);
        },
        getUsersUrl: function (userId) {
            if (userId) {
                return Ext.String.format('{0}/users/', this.baseUrl, userId);
            }
            return Ext.String.format('{0}/users', this.baseUrl);
        },
        getRoleGrantsPostUrl: function (role) {
            return Ext.String.format('{0}/roles/{1}/grants/_ANY', this.baseUrl, role);
        },
        getRoleGrantsUrl: function (role) {
            return Ext.String.format('{0}/roles/{1}/grants', this.baseUrl, role);
        }
    }
});
