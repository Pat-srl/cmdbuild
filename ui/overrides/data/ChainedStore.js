Ext.define('Override.data.ChainedStore', {
    override: 'Ext.data.ChainedStore',

    /**
     * @override
     * 
     * Finds the index of the first matching Record in this store by a specific field value.
     *
     * When store is filtered, finds records only within filter.
     *
     * **IMPORTANT**
     *
     * **If this store is {@link Ext.data.BufferedStore Buffered}, this can ONLY find records
     * which happen to be cached in the page cache. This will be parts of the dataset around the
     * currently visible zone, or recently visited zones if the pages have not yet been purged from
     * the cache.**
     *
     * @param {String} property The name of the Record field to test.
     * @param {String/RegExp} value Either a string that the field value
     * should begin with, or a RegExp to test against the field.
     * @param {Number} [startIndex=0] The index to start searching at
     * @param {Boolean} [anyMatch=false] True to match any part of the string, not just the
     * beginning.
     * @param {Boolean} [caseSensitive=false] True for case sensitive comparison
     * @param {Boolean} [exactMatch=true] True to force exact match (^ and $ characters
     * added to the regex). Ignored if `anyMatch` is `true`.
     * @return {Number} The matched index or -1
     */
    find: function (property, value, startIndex, anyMatch, caseSensitive, exactMatch) {
        //             exactMatch 
        //  anyMatch    F       T 
        //      F       ^abc    ^abc$ 
        //      T       abc     abc 
        // 
        
        exactMatch = exactMatch || true;
        return this.callParent([property, value, startIndex, anyMatch, caseSensitive, exactMatch]);
    }
});
