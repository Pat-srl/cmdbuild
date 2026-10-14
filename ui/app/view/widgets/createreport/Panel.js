/**
 * @file CMDBuildUI.view.widgets.createreport
 * @module CMDBuildUI.view.widgets.createreport
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.view.widgets.createreport.Panel', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.widgets.createreport.PanelController',
        'CMDBuildUI.view.widgets.createreport.PanelModel',

        'CMDBuildUI.view.reports.ContainerController'
    ],
    mixins: ['CMDBuildUI.view.widgets.Mixin'],

    alias: 'widget.widgets-createreport-panel',
    controller: 'widgets-createreport-panel',
    viewModel: {
        type: 'widgets-createreport-panel'
    },

    layout: 'fit',

    /**
     * @constant {String} ReportCode
     * Is the code of the report to open.
     */
    ReportCode: null,

    /**
     * @constant {Boolean} ForcePDF
     * Force PDF format for this report.
     */
    ForcePDF: false,

    /**
     * @constant {Boolean} ForceCSV
     * Force CSV format for this report.
     */
    ForceCSV: false,

    /**
     * @constant {String} ReadOnlyAttributes
     * The list of read only attributes separated by comma. E.g. `"Attribute1,Attribute2"`.
     */
    ReadOnlyAttributes: null,

    /**
     * @constant {Boolean} Required
     * If True this widget is mandatory.
     */
    Required: false
});
