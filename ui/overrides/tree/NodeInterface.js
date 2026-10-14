Ext.define('Overrides.tree.NodeInterface', {
    override: 'Ext.data.NodeInterface',

    /**
     * @override
     * 
     * Creates an object representation of this node including its children.
     * @param {*} writerParam 
     * @returns 
     */
    serialize: function (writerParam) {
        var writer = writerParam || new Ext.data.writer.Json({
            writeAllFields: true
        }),
            result = writer.getRecordData(this),
            childNodes = this.childNodes,
            len = childNodes.length,
            children, i;

        if (len > 0) {
            result[writer.getRootProperty() ? writer.getRootProperty() : 'children'] = children = [];

            for (i = 0; i < len; i++) {
                children.push(childNodes[i].serialize(writer));
            }
        }

        return result;
    }
});