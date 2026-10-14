
Ext.define('CMDBuildUI.view.fields.formvalidator.Field', {
    extend: 'Ext.form.field.Base',

    requires: [
        'CMDBuildUI.view.fields.formvalidator.FieldController',
        'CMDBuildUI.view.fields.formvalidator.FieldModel'
    ],

    alias: 'widget.formvalidatorfield',
    controller: 'fields-formvalidator-field',
    viewModel: {
        type: 'fields-formvalidator-field'
    },

    config: {
        /**
         * @cfg {String} validationCode
         *
         * The configuation for the validation code.
         */
        validationCode: null,

        /**
         * @cfg {String} linkName
         *
         * The name of the linked object.
         */
        linkName: 'theObject',

        /**
         * @cfg {String} activityLinkName
         *
         * The name of the linked object.
         */
        activityLinkName: 'theActivity',

        /**
         * @cfg {String} formMode
         *
         * Form mode.
         */
        formMode: null,

        /**
         * @cfg {String} errorMessage
         *
         * The error message displayed to the user.
         */
        errorMessage: null,

        /**
         * @cfg {String} calendarMessage
         *
         * The message to display to user when widget calendar isn't set correctly.
         */
        calendarMessage: null,

        /**
         * @cfg {Boolean} showActionButton
         *
         * True if you want to show the action button
         */
        showActionButton: false
    },

    labelableRenderTpl: [
        '<div id="{id}-messageEl" data-ref="messageEl" class="{[Ext.baseCSSPrefix]}form-error-message"></div>'
    ],

    childEls: [
        /**
         * @property {Ext.dom.Element} messageEl
         * The input Element for this Field. Only available after the field has been rendered.
         */
        'messageEl'
    ],

    hidden: true,
    preventMark: true,
    width: '100%',

    bind: {
        value: '{errorMessage}',
        hidden: '{!errorMessage}',
        errorMessage: '{errorMessage}'
    },

    updateErrorMessage: function (newValue, oldValue) {
        if (this.rendered) {
            const value = Ext.isEmpty(newValue) ? '' : newValue;
            const domEl = this.messageEl.dom;
            if (this.getShowActionButton()) {
                domEl.querySelector("#formValidatorFieldText").innerText = value;
            } else {
                domEl.innerHTML = value;
            }
        }
    },

    getErrors: function (value) {
        if (value !== undefined) {
            return value;
        }
        return this.getErrorMessage();
    }
});
