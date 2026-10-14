Ext.define('CMDBuildUI.mixins.routes.management.Reports', {
    mixinId: 'managementroutes-reports-mixin',

    /**
     * Before show report
     * 
     * @param {String} reportName
     * @param {Object} action
     */
    onBeforeShowReport: function (reportName, action) {
        reportName = decodeURI(reportName);
        CMDBuildUI.util.helper.ModelHelper.getModel(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.report,
            reportName
        ).then(function (model) {
            action.resume();
        }, function () {
            CMDBuildUI.util.Utilities.redirectTo("management");
            action.stop();
        });
    },

    /**
     * Before show report 
     * 
     * @param {String} reportName
     * @param {Object} action   
     */
    onBeforeShowReportExtension: function (reportName, extension, action) {
        this.onBeforeShowReport(reportName,action);
    },

    /**
     * Show report
     * 
     * @param {Numeric} reportName
     */
    showReport: function (reportName) {
        reportName = decodeURI(reportName);
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);
        CMDBuildUI.util.Navigation.addIntoManagementContainer('reports-container', {
            viewModel: {
                data: {
                    objectTypeName: reportName
                }
            }
        });

        // update current context
        CMDBuildUI.util.Navigation.updateCurrentManagementContext(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.report,
            reportName
        );

        // fire global event objecttypechanged
        Ext.GlobalEvents.fireEventArgs("objecttypechanged", [reportName]);
    },

    /**
     * Show report with defined extension
     * 
     * @param {string} reportName
     * @param {string} extension
     */
    showReportExtension: function (reportName, extension) {
        reportName = decodeURI(reportName);
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);
        CMDBuildUI.util.Navigation.addIntoManagementContainer('reports-container', {
            viewModel: {
                data: {
                    objectTypeName: reportName,
                    extension: extension
                }
            }
        });

        // update current context
        CMDBuildUI.util.Navigation.updateCurrentManagementContext(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.report,
            reportName,
            extension
        );

        // fire global event objecttypechanged
        Ext.GlobalEvents.fireEventArgs("objecttypechanged", [reportName, extension]);
    }
});