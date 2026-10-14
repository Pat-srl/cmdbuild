/**
 * @file CMDBuildUI.view.widgets.customform
 * @module CMDBuildUI.view.widgets.customform
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.view.widgets.customform.Panel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.widgets.customform.PanelController',
        'CMDBuildUI.view.widgets.customform.PanelModel',
        'CMDBuildUI.view.widgets.customform.Utilities'
    ],

    statics: {
        /**
         * Set the output variable with inline widget data.
         *
         * @param {CMDBuildUI.model.classes.Card|CMDBuild.model.processes.Instance} target
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @param {Object} config
         * @param {String} config.formmode
         * @param {String} config.action One of `save` and `execute`
         *
         * @returns {Ext.promise.Promise}
         */
        beforeTargetSave: function (target, widget, config) {
            var deferred = new Ext.Deferred();

            if (widget.get('_inline')) {
                var view = widget.getOwner().down('widgets-customform-panel'),
                    vm = view.lookupViewModel(),
                    store = vm.get('dataStore'),
                    row = vm.get('theRow'),
                    storedata = store ? store.getRange() : [row],
                    rows = CMDBuildUI.view.widgets.customform.Utilities.serialize(widget, storedata);

                view.getTarget().set(view.getOutput(), rows);
            }
            deferred.resolve(true);

            return deferred;
        },

        /**
         * Function executed when the form opens
         *
         * @param {CMDBuildUI.model.classes.Card|CMDBuild.model.processes.Instance} target
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @param {Object} config
         * @param {String} config.formmode
         *
         * @return {Ext.promise.Promise}
         */
        onTargetFormOpen: function (target, widget, config) {
            var deferred = new Ext.Deferred();
            // if form is not in read mode
            // and the ouptut is empty
            if (config.formmode !== CMDBuildUI.util.helper.FormHelper.formmodes.read) {
                // get widget model
                CMDBuildUI.view.widgets.customform.Utilities.getModel(widget).then(function () {
                    var model_attributes =
                        CMDBuildUI.view.widgets.customform.Utilities.getAttributesForModelWidget(widget);

                    /**
                     * Set widget data to target
                     *
                     * @param {Object} data bound data
                     */
                    function setDataToTarget(data) {
                        // load data every time any of the binding attributes will change
                        // or if the output attribute is empty
                        if (
                            (widget.get('RefreshBehaviour') &&
                                widget.get('RefreshBehaviour').toLowerCase() === 'everytime') ||
                            Ext.isEmpty(target.get(widget.get('_output')))
                        ) {
                            // load data
                            CMDBuildUI.view.widgets.customform.Utilities.loadData(
                                widget,
                                target,
                                function (response) {
                                    // serialize results and set in ouput variale
                                    var res = CMDBuildUI.view.widgets.customform.Utilities.serialize(widget, response);
                                    target.set(widget.get('_output'), res);
                                },
                                model_attributes
                            );
                        }
                    }

                    // if data type is function load function data
                    if (widget.get('DataType') && widget.get('DataType').toLowerCase() === 'function') {
                        CMDBuildUI.view.widgets.customform.Utilities.calculateFunctionVariableObject(widget).then(
                            function (functionVariableObject) {
                                //saves the calculated object in the widget due to avoid further calulation
                                widget.set('_functionVariableObject', functionVariableObject);

                                /**
                                 * This code creates a bind with theObject. The widget function results are loaded each time theObject attributes changes.
                                 * Only theObject.attributes on which a theWidget.function has it as parameter causes the recalculation of the function
                                 */
                                // calculate bind variables
                                var binds = {};

                                //creates the bind object
                                for (var key in functionVariableObject) {
                                    var attribute = functionVariableObject[key].attribute;
                                    binds[attribute] = Ext.String.format('{theObject.{0}}', attribute);
                                }

                                //creates the bind
                                widget.getOwner().lookupViewModel().bind(
                                    {
                                        bindTo: binds
                                    },
                                    setDataToTarget
                                );
                            }
                        );
                    } else {
                        //loads the data
                        setDataToTarget();
                    }

                    deferred.resolve();
                });
            } else {
                deferred.resolve();
            }
            return deferred;
        }
    },

    mixins: ['CMDBuildUI.view.widgets.Mixin'],

    alias: 'widget.widgets-customform-panel',
    controller: 'widgets-customform-panel',
    viewModel: {
        type: 'widgets-customform-panel'
    },

    layout: 'fit',

    /**
     * @constant {String} Layout
     * One of `grid` or `form`.
     */
    Layout: null,

    /**
     * @constant {String} RefreshBehaviour
     * One of `everyTime` or `firstTime`.
     * If value is `everyTime` the content is refreshed every time the widget is opened
     * following the modification of a configuration parameter.
     * If value is `firstTime` the content is refreshed only when the widget is opened
     * the first time.
     * Default value is `everyTime`.
     */
    RefreshBehaviour: null,

    /**
     * @constant {Boolean} ReadOnly
     * If True disable all functionalities and makes the data only readable.
     * Default value is False.
     */
    ReadOnly: false,

    /**
     * @constant {Boolean} AddDisabled
     * If True disable add functionality.
     * Default value is False.
     */
    AddDisabled: false,

    /**
     * @constant {Boolean} CloneDisabled
     * If True disable clone functionality.
     * Default value is False.
     */
    CloneDisabled: false,

    /**
     * @constant {Boolean} DeleteDisabled
     * If True disable delete functionality.
     * Default value is False.
     */
    DeleteDisabled: false,

    /**
     * @constant {Boolean} ExportDisabled
     * If True disable export functionality.
     * Default value is False.
     */
    ExportDisabled: false,

    /**
     * @constant {Boolean} ImportDisabled
     * If True disable import functionality.
     * Default value is False.
     */
    ImportDisabled: false,

    /**
     * @constant {Boolean} ModifyDisabled
     * If True disable modify functionality.
     * Default value is False.
     */
    ModifyDisabled: false,

    /**
     * @constant {Boolean} RefreshHidden
     * If True hidden refresh functionality.
     * Default value is False.
     */
    RefreshHidden: false,

    /**
     * @constant {String} ModelType
     * One of `form`, `class`.
     */
    ModelType: null,

    /**
     * @constant {String} ClassModel
     * Name of the class from which take the attributes.
     */
    ClassModel: null,

    /**
     * @constant {String} ClassAttributes
     * List of attributes to be considered separated by commas.
     * Empty or `null` indicates all attributes.
     */
    ClassAttributes: null,

    /**
     * @constant {String} FormModel
     * A list of attributes definition.
     */
    FormModel: null,

    /**
     * @constant {String} DataType
     * One of `raw`, `raw_json`, `raw_text` or `function`.
     */
    DataType: null,

    /**
     * @constant {Object|String} rawData
     * The raw data to serialize.
     */
    rawData: null,

    /**
     * @constant {String} FunctionData
     * The function name from which get the data.
     */
    FunctionData: null,

    /**
     * @constant {String} SerializationType
     * One of `json` or `text`.
     * Default value is `text`.
     */
    SerializationType: null,

    /**
     * @constant {String} KeyValueSeparator
     * The string to use to separate key from value.
     * Default value is `=`.
     */
    KeyValueSeparator: null,

    /**
     * @constant {String} AttributesSeparator
     * The string to use to separate attributes.
     * Default value is `,`.
     */
    AttributesSeparator: null,

    /**
     * @constant {String} RowsSeparator
     * The string to use to separate rows.
     * Default value is `\n`.
     */
    RowsSeparator: null,

    /**
     * @constant {Boolean} DisableCellEditing
     * If True disable editing in a cell on the grid.
     * Default value is False.
     */
    DisableCellEditing: false,

    /**
     * @constant {String} Output
     * The variable where the output of the widget is saved (if inserted in a process).
     */
    Output: null,

    /**
     * @constant {Boolean} Inline
     * If True show the widget inline.
     */
    Inline: false,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{theWidget._inline}'
            },
            items: [
                '->',
                {
                    xtype: 'button',
                    ui: 'secondary-action',
                    reference: 'closebtn',
                    itemId: 'closebtn',
                    text: CMDBuildUI.locales.Locales.common.actions.close,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('check', 'solid'),
                    localize: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.close'
                    },
                    autoEl: {
                        'data-testid': 'widgets-customform-close'
                    }
                }
            ]
        }
    ],

    /**
     * Return the name of the model used by the widget.
     * @return {String}
     * @ignore
     */
    getModelName: function () {
        return 'CMDBuildUI.model.customform.' + this.getWidgetId();
    }
});
