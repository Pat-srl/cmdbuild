/**
 * @file CMDBuildUI.util.Color
 * @module CMDBuildUI.util.Color
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.Color', {
    singleton: true,

    /**
     * Converts a hex color (e.g. '#FF9900' or '#f90') to an rgba() string
     * with the given alpha. Returns '' for falsy/invalid input.
     *
     * @param {String} hex
     * @returns {Array} [r, g, b]
     */
    hexToRgbA: function (hex) {
        if (hex == null) return [0, 0, 0];
        var c;
        if (/^#([A-Fa-f0-9]{3}){1,2}$/.test(hex)) {
            c = hex.substring(1).split('');
            if (c.length == 3) {
                c = [c[0], c[0], c[1], c[1], c[2], c[2]];
            }
            c = '0x' + c.join('');
            return [(c >> 16) & 255, (c >> 8) & 255, c & 255]; // to set the transparency value add the alpha parameter to the returned ones
        }

        CMDBuildUI.util.Logger.log('Color in bad hex sintax', CMDBuildUI.util.Logger.levels.error);
        return [0, 0, 0];
    },

    /**
     * @param {String} hex
     * @param {Number} [amount=0.8]
     * @return {String}
     */
    getLighterColor: function (hex, amount) {
        if (!hex) return hex;
        amount = amount == null ? 0.8 : amount;
        hex = hex.replace('#', '');
        if (hex.length === 3) {
            hex = hex
                .split('')
                .map(function (c) {
                    return c + c;
                })
                .join('');
        }
        var r = parseInt(hex.substr(0, 2), 16);
        var g = parseInt(hex.substr(2, 2), 16);
        var b = parseInt(hex.substr(4, 2), 16);

        function lighten(c) {
            return Math.round(c + (255 - c) * amount);
        }

        return 'rgb(' + lighten(r) + ',' + lighten(g) + ',' + lighten(b) + ')';
    }
});
