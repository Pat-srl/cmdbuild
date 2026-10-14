Ext.define("Override.data.data.validator.Presence", {
    override: "Ext.data.validator.Presence",

    /**
     * This method was created because in translations
     * message parameter is not in config object
     * but is as parameter, then is not overrided.
     * @return {String} message
     */
    getMessage: function() {
        return this.message;
    }
})