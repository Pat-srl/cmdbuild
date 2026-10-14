Ext.define('CMDBuildUI.view.bim.xeokit.Mixin', {
    mixinId: 'bim-mixin',

    /**
     * 
     * @returns the container view
     */
    getContainer: function () {
        return this.up("bim-xeokit-container");
    },

    /**
     * Remove word ifc from text and separate words
     * 
     * @param {String} text 
     * @returns {String}
     */
    cleanEntityType: function (text) {
        if (Ext.String.startsWith(text, "ifc", true)) {
            text = text.slice(3);
        }
        text = text.replace(/([a-z])([A-Z])/g, "$1 $2");
        return text;
    }

});