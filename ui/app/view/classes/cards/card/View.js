
Ext.define('CMDBuildUI.view.classes.cards.card.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.classes.cards.card.ViewController',
        'CMDBuildUI.view.classes.cards.card.ViewModel',

        'CMDBuildUI.util.helper.FormHelper'
    ],

    mixins: [
        'CMDBuildUI.view.classes.cards.card.Mixin',
        'CMDBuildUI.mixins.forms.FormTriggers'
    ],

    alias: 'widget.classes-cards-card-view',
    controller: 'classes-cards-card-view',
    viewModel: {
        type: 'classes-cards-card-view'
    },

    config: {
        /**
        * @cfg {Boolean} shownInPopup
        * Set to true get inline form.
        */
        shownInPopup: false,

        /**
         * @cfg {Boolean} hideTools
         * Set to true to hide tools.
         */
        hideTools: false
    },

    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    html: CMDBuildUI.util.helper.FormHelper.waitFormHTML,

    bind: {
        title: '{title}'
    },

    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.read,

    tabpaneltools: CMDBuildUI.view.classes.cards.Util.getTools(),

    fieldDefaults: {
        labelAlign: 'top'
    },

    /**
     * Function called when objectTypeName is updated.
     *
     * @param {String} newValue
     * @param {String} oldValue
     */
    updateObjectTypeName: function (newValue, oldValue) {
        this.fireEventArgs("objecttypenamechanged", [this, newValue, oldValue]);
    },

    /**
     * Function called when objectId is updated.
     *
     * @param {Numeric} newValue
     * @param {Numeric} oldValue
     */
    updateObjectId: function (newValue, oldValue) {
        this.fireEventArgs("objectidchanged", [this, newValue, oldValue]);
    }

});
