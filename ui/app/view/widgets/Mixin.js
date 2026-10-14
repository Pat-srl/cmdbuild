/**
 * @file CMDBuildUI.view.widgets.Mixin
 * @mixin CMDBuildUI.view.widgets.Mixin
 * @author PAT srl
 * @access public
 *
 * @classdesc
 * <p>
 * This mixin must be used in the main view of a custom widgets.
 * </p>
 * <p>
 * To get the widget definition you can use the method vm.get("theWidget")
 * which returns the instance of {@link CMDBuildUI.model.WidgetDefinition}.
 * </p>
 * <p>
 * To get the target on which the widget is called you can use the method
 * vm.get("theTarget") which returns an instance of {@link CMDBuildUI.model.classes.Card}
 * or {@link CMDBuildUI.model.processes.Instance}.
 * </p>
 */
Ext.define('CMDBuildUI.view.widgets.Mixin', {
    mixinId: 'cmdbuildwidgets-mixin',

    mixins: ['Ext.mixin.Bindable'],

    statics: {
        /**
         * This function must be overrinden as static.
         * Function executed when the form opens.
         *
         * @name onTargetFormOpen
         * @function
         *
         * @param {CMDBuildUI.model.classes.Card|CMDBuild.model.processes.Instance} target
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @param {Object} config
         * @param {String} config.formmode
         * @param {Ext.form.Panel} config.form
         *
         * @returns {Ext.promise.Promise}
         *
         * @example
         * Ext.define('CMDBuildUI.view.widgets.custom.testwidget.Panel', {
         *      extend: 'Ext.panel.Panel',
         *      mixins: [
         *          'CMDBuildUI.view.widgets.Mixin'
         *      ],
         *
         *      statics: {
         *          onTargetFormOpen: function(target, widget, config) {
         *              var deferred = new Ext.Deferred();
         *              console.log("onTargetFormOpen - CMDBuildUI.view.widgets.custom.testwidget.Panel");
         *              deferred.resolve();
         *              return deferred;
         *          }
         *      },
         *
         *      alias: 'widget.widgets-custom-testwidget-panel',
         *
         *      html: 'Hello, World!!'
         * });
         *
         * @memberof CMDBuildUI.view.widgets.Mixin
         * @static
         */
        // onTargetFormOpen: function(target, widget, config) {
        //     var deferred = new Ext.Deferred();
        //     deferred.resolve();
        //     return deferred;
        // }
        /**
         * This function must be overrinden as static.
         * Function executed before save/execute action.
         *
         * @name beforeTargetSave
         * @function
         *
         * @param {CMDBuildUI.model.classes.Card|CMDBuild.model.processes.Instance} target
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @param {Object} config
         * @param {String} config.formmode
         * @param {String} config.action One of `save` and `execute`
         *
         * @returns {Ext.promise.Promise}
         *
         * @example
         * Ext.define('CMDBuildUI.view.widgets.custom.testwidget.Panel', {
         *      extend: 'Ext.panel.Panel',
         *      mixins: [
         *          'CMDBuildUI.view.widgets.Mixin'
         *      ],
         *
         *      statics: {
         *          beforeTargetSave: function(target, widget, config) {
         *              var deferred = new Ext.Deferred();
         *              console.log("beforeTargetSave - CMDBuildUI.view.widgets.custom.testwidget.Panel");
         *              deferred.resolve();
         *              return deferred;
         *          }
         *      },
         *
         *      alias: 'widget.widgets-custom-testwidget-panel',
         *
         *      html: 'Hello, World!!'
         * });
         *
         * @memberof CMDBuildUI.view.widgets.Mixin
         * @static
         */
        // beforeTargetSave: function(target, widget, config) {
        //     var deferred = new Ext.Deferred();
        //     deferred.resolve();
        //     return deferred;
        // }
        /**
         * This function must be overrinden as static.
         * Function executed after save/execute action
         *
         * @name afterTargetSave
         * @function
         *
         * @param {CMDBuildUI.model.classes.Card|CMDBuild.model.processes.Instance} target
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @param {Object} config
         * @param {String} config.formmode
         * @param {String} config.action One of `save` and `execute`
         *
         * @returns {Ext.promise.Promise}
         *
         * @example
         * Ext.define('CMDBuildUI.view.widgets.custom.testwidget.Panel', {
         *      extend: 'Ext.panel.Panel',
         *      mixins: [
         *          'CMDBuildUI.view.widgets.Mixin'
         *      ],
         *
         *      statics: {
         *          afterTargetSave: function(target, widget, config) {
         *              var deferred = new Ext.Deferred();
         *              console.log("afterTargetSave - CMDBuildUI.view.widgets.custom.testwidget.Panel");
         *              deferred.resolve();
         *              return deferred;
         *          }
         *      },
         *
         *      alias: 'widget.widgets-custom-testwidget-panel',
         *
         *      html: 'Hello, World!!'
         * });
         *
         * @memberof CMDBuildUI.view.widgets.Mixin
         * @static
         */
        // afterTargetSave: function(target, widget, config) {
        //     var deferred = new Ext.Deferred();
        //     deferred.resolve();
        //     return deferred;
        // }
        /**
         * This function must be overrinden as static.
         * Function executed before cancel action.
         *
         * @name onTargetCancel
         * @function
         *
         * @param {CMDBuildUI.model.classes.Card|CMDBuild.model.processes.Instance} target
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @param {Object} config
         * @param {String} config.formmode
         *
         * @returns {Ext.promise.Promise}
         *
         * @example
         * Ext.define('CMDBuildUI.view.widgets.custom.testwidget.Panel', {
         *      extend: 'Ext.panel.Panel',
         *      mixins: [
         *          'CMDBuildUI.view.widgets.Mixin'
         *      ],
         *
         *      statics: {
         *          onTargetCancel: function(target, widget, config) {
         *              var deferred = new Ext.Deferred();
         *              console.log("onTargetCancel - CMDBuildUI.view.widgets.custom.testwidget.Panel");
         *              deferred.resolve();
         *              return deferred;
         *          }
         *      },
         *
         *      alias: 'widget.widgets-custom-testwidget-panel',
         *
         *      html: 'Hello, World!!'
         * });
         *
         * @memberof CMDBuildUI.view.widgets.Mixin
         * @static
         */
        // onTargetCancel: function(target, widget, config) {
        //     var deferred = new Ext.Deferred();
        //     deferred.resolve();
        //     return deferred;
        // }
    },

    config: {
        /**
         * The id of the widget. <br />
         * Use `getWidgetId()` to get the value of this property. <br />
         * Use `setWidgetId()` to set the value of this property.
         *
         * @type {String}
         *
         * @memberof CMDBuildUI.view.widgets.Mixin
         */
        widgetId: null,

        /**
         * The target object on which the widget is called. <br />
         * Use `getTarget()` to get the value of this property. <br />
         * Use `setTarget()` to set the value of this property.
         *
         * @type {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance}
         *
         * @memberof CMDBuildUI.view.widgets.Mixin
         */
        target: null,

        /**
         * The target attribute where output will be saved. <br />
         * Use `getOutput()` to get the value of this property. <br />
         * Use `setOutput()` to set the value of this property.
         *
         * @type {String}
         *
         * @memberof CMDBuildUI.view.widgets.Mixin
         */
        output: null,

        /**
         * Form mode. <br />
         * Use `getFormMode()` to get the value of this property. <br />
         * Use `setFormMode()` to set the value of this property.
         *
         * @type {String}
         *
         * @memberof CMDBuildUI.view.widgets.Mixin
         */
        formMode: null,

        /**
         * These are the possible configurations to assign to a widget.
         *
         * @memberof CMDBuildUI.view.widgets.Mixin
         * @property {Boolean} AlwaysEnabled If true this widget is always enabled.
         * @property {Boolean} HideInCreation If True hide the widget on the creation of a new card.
         * @property {Boolean} HideInEdit If True hide the widget when modify a card.
         * @property {Boolean} Inline If True show the widget inline.
         * @property {String} InlineAfter If True show the widget inline after the specified fieldset.
         * @property {String} InlineBefore If True show the widget inline before the specified fieldset.
         * @property {Boolean} InlineClosed If True show the widget inline closed.
         * @property {String} Output The variable where the output of the widget is saved (if inserted in a process).
         * @property {Boolean} Required If True this widget is mandatory.
         * @property {String} ViewRule The variable where insert the script for the view rule of the widget.
         */
        WidgetConfigs: {
            /**
             * @property {Boolean} AlwaysEnabled
             * If true this widget is always enabled.
             */
            AlwaysEnabled: false,

            /**
             * @property {Boolean} HideInCreation
             * If True disable the widget on the creation of a new card.
             */
            HideInCreation: false,

            /**
             * @property {Boolean} HideInEdit
             * If True disable the widget when modify a card.
             */
            HideInEdit: false,

            /**
             * @property {Boolean} Inline
             * If True show the widget inline.
             */
            Inline: false,

            /**
             * @property {Boolean} InlineClosed
             * If True show the widget inline closed.
             */
            InlineClosed: false,

            /**
             * @property {String} InlineAfter
             * If True show the widget inline after the specified fieldset.
             */
            InlineAfter: null,

            /**
             * @property {String} InlineBefore
             * If True show the widget inline before the specified fieldset.
             */
            InlineBefore: null,

            /**
             * @property {String} Output
             * The variable where the output of the widget is saved (if inserted in a process).
             */
            Output: null,

            /**
             * @property {Boolean} Required
             * If True this widget is mandatory.
             */
            Required: false,

            /**
             * @property {String} ViewRule
             * The variable where insert the script for the view rule of the widget.
             */
            ViewRule: null
        }
    },

    publishes: ['target'],

    twoWayBindable: ['target'],

    /**
     * Fired to close popup.
     *
     * @method
     * @listens CMDBuildUI.view.widgets.Mixin:popupclose
     *
     * @memberof CMDBuildUI.view.widgets.Mixin
     */

    /**
     * Close the widget popup.
     *
     * @fires popupclose
     *
     * @memberof CMDBuildUI.view.widgets.Mixin
     */
    closePopup: function () {
        this.fireEvent('popupclose');
    },

    /**
     * Show message for not supported inline widgets:
     * createModifyCard | createReport | manageEmail | presetFromCard | sequenceView | startWorkflow
     *
     * @memberof CMDBuildUI.view.widgets.Mixin
     */
    showNotSupportedInlineMessage: function () {
        const me = this;

        me.removeAll();
        me.getDockedItems().forEach(function (item) {
            me.removeDocked(item);
        });
        me.add({
            xtype: 'panel',
            cls: Ext.baseCSSPrefix + 'selectable',
            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
            html: Ext.String.format('<i>{0}</i>', CMDBuildUI.locales.Locales.widgets.notsupportedinline)
        });
    }
});
