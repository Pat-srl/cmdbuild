Ext.define('Overrides.Base', {
    override: 'Ext.Base',

    /**
     * @override
     *
     * Returns a specified config property value. If the name parameter is not passed,
     * all current configuration options will be returned as key value pairs.
     * @param {String} [name] The name of the config property to get.
     * @param {Boolean} [peek=false] `true` to peek at the raw value without calling the getter.
     * @param {Boolean} [ifInitialized=false] `true` to only return the initialized property
     * value, not the raw config value, and *not* to trigger initialization. Returns
     * `undefined` if the property has not yet been initialized.
     * @return {Object} The config property value.
     */
    getConfig: function (name, peek, ifInitialized) {
        var me = this,
            ret,
            cfg,
            propName;
        if (name) {
            cfg = me.self.$config.configs[name];
            if (cfg) {
                propName = me.$configPrefixed ? cfg.names.internal : name;
                // They only want the fully initialized value, not the initial config,
                //  but only if it's already present on this instance.
                // They don't want to trigger the initGetter.
                // This form is used by Bindable#updatePublishes to initially publish
                // the properties it's being asked make publishable.
                if (ifInitialized) {
                    ret = me.hasOwnProperty(propName) ? me[propName] : null;
                } else if (peek) {
                    // Attempt to return the instantiated property on this instance first.
                    // Only return the config object if it has not yet been pulled through
                    // the applier into the instance.
                    ret = me.hasOwnProperty(propName) ? me[propName] : me.config ? me.config[name] : null;
                } else {
                    ret = me[cfg.names.get]();
                }
            } else {
                ret = me[name];
            }
        } else {
            ret = me.getCurrentConfig();
        }
        return ret;
    }
});
