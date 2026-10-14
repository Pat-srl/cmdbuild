/**
 * @file CMDBuildUI.util.helper.LocalStorageHelper
 * @module CMDBuildUI.util.helper.LocalStorageHelper
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.helper.LocalStorageHelper', {
    singleton: true,

    /**
     * The available keys in local storage.
     *
     * @property {Object} keys
     */
    keys: {
        loginlanguage: 'loginlanguage'
    },

    /**
     * The Local storage store
     *
     * @private
     *
     * @property {Ext.data.Store} _store
     */
    _store: undefined,

    /**
     * Returns the local storage store.
     *
     * @private
     *
     * @returns {Ext.data.Store}
     */
    getStore: function () {
        if (!this._store) {
            this._store = Ext.create('Ext.data.Store', {
                fields: ['key', 'value'],
                proxy: {
                    type: 'localstorage',
                    id: 'cmdbuild'
                }
            });
            this._store.load();
        }
        return this._store;
    },

    /**
     * Get a value saved in local storage.
     *
     * @param {String} key The key of saved property.
     *
     * @returns {*} Returns the saved value.
     */
    get: function (key) {
        var record = this.getStore().findRecord('key', key);
        return record ? record.get('value') : null;
    },

    /**
     * Save a value in local storage.
     *
     * @param {String} key The key for the value to save.
     * @param {*} value The value to save.
     */
    set: function (key, value) {
        var store = this.getStore();
        var record = store.findRecord('key', key);
        if (record) {
            record.set('value', value);
        } else {
            store.add({
                key: key,
                value: value
            });
        }
        store.sync();
    },

    /**
     * Removes a record from the local storage.
     *
     * @param {String} key The key of the value to remove.
     */
    clearKey: function (key) {
        var record = this.getStore().findRecord('key', key);
        if (record) {
            this.getStore().remove(record);
            store.sync();
        }
    }
});
