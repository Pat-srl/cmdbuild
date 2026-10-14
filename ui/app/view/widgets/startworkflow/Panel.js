/**
 * @file CMDBuildUI.view.widgets.startworkflow
 * @module CMDBuildUI.view.widgets.startworkflow
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.view.widgets.startworkflow.Panel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.widgets.startworkflow.PanelController',
        'CMDBuildUI.view.widgets.startworkflow.PanelModel'
    ],

    mixins: ['CMDBuildUI.view.widgets.Mixin'],

    /**
     * @constant {String} WorkflowCode
     * The name of the starting process.
     */
    WorkflowCode: null,

    /**
     * @constant {String} WorkflowName
     * The name of the starting process. Is an alternative to WorkflowCode.
     */
    WorkflowName: null,

    /**
     * @constant {Boolean} Required
     * If True this widget is mandatory.
     */
    Required: false,

    /**
     * @constant {String} preset
     * The data to parse.
     */
    preset: null,

    /**
     * @constant {String} Output
     * The variable where the output of the widget is saved (if inserted in a process).
     */
    Output: null,

    alias: 'widget.widgets-startworkflow-panel',
    controller: 'widgets-startworkflow-panel',
    viewModel: {
        type: 'widgets-startworkflow-panel'
    },
    layout: 'fit'
});
