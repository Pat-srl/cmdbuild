Ext.define('CMDBuildUI.mixins.forms.FormTriggers', {
    mixinId: 'forms-formtriggers-mixin',

    requires: ['CMDBuildUI.util.helper.FormHelper'],

    /**
     * 
     * @param {String[]} triggers 
     * @param {Object} api 
     */
    executeFormTriggers: function (triggers, api) {
        CMDBuildUI.util.helper.FormHelper.executeFormTriggers(triggers, api);
    },

    /**
     * Initialize before action form triggers.
     * 
     * @param {String} action 
     * @param {Object} base_api 
     */
    initBeforeActionFormTriggers: Ext.emptyFn,

    /**
     * Execute after action form triggers.
     * 
     * @param {String} action 
     * @param {CMDBuildUI.model.classes.Card} record
     * @param {Object} base_api 
     */
    executeAfterActionFormTriggers: Ext.emptyFn
});