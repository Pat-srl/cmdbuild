Ext.define('CMDBuildUI.view.widgets.Fieldset', {
    extend: 'CMDBuildUI.components.tab.FieldSet',

    mixins: ['CMDBuildUI.view.widgets.ValidationMixin'],

    alias: 'widget.widgets-fieldset',

    collapsible: true,
    cls: Ext.baseCSSPrefix + 'widgetsfieldset',

    listeners: {
        'adderror': function () {
            this.addError(arguments);
        },
        'removeerror': function () {
            this.removeError(arguments);
        }
    },

    /**
     * @override
     */
    getErrorDomElement: function () {
        return this.legend && this.legend.el ? this.legend.el.dom : null;
    }

});