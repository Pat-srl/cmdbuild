Ext.define('CMDBuildUI.mixins.model.FormTrigger', {
    mixinId: 'model-formtrigger-mixin',

    /**
     * 
     * @param {*} action 
     */
    getFormTriggersForAction: function (action) {
        var triggers = this.formTriggers();
        var filtered = [];
        triggers.each(function (t) {
            if (t.get("active") && t.get(action)) {
                filtered.push(t.get("script"));
            }
        });
        return filtered;
    }
});