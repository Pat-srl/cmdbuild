Ext.define('Overrides.promise.Consequence', {
    override: 'Ext.promise.Consequence',

    /**
     * @override
     * Transform and propagate the specified value using the
     * optional callback and propagate the transformed result.
     *
     * @param {Mixed} value Value to transform and/or propagate.
     * @param {Function} [callback] Callback to use to transform the value.
     * @param {Function} deferred Deferred to use to propagate the value, if no callback
     * was specified.
     *  @param {Function} deferredMethod Deferred method to call to propagate the value,
     * if no callback was specified.
     *
     * @private
     */
    propagate: function (value, callback, deferred, deferredMethod) {
        if (Ext.isFunction(callback)) {
            this.schedule(function () {
                try {
                    deferred.resolve(callback(value));
                }
                catch (e) {
                    CMDBuildUI.util.Logger.log("Error on consequence:", CMDBuildUI.util.Logger.levels.error);
                    CMDBuildUI.util.Logger.log(e, CMDBuildUI.util.Logger.levels.error);
                    deferred.reject(e);
                }
            });
        }
        else {
            deferredMethod.call(this.deferred, value);
        }
    }
});