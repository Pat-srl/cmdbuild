Ext.define('CMDBuildUI.view.dms.history.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dms-history-grid',

    /**
     * 
     * @param {Ext.view.Table} view 
     * @param {Number} rowIndex 
     * @param {Number} colIndex 
     * @param {Object} item 
     * @param {Event} e 
     * @param {Ext.data.Model} record 
     * @param {HTMLElement} row 
     */
    onActionDownload: function (view, rowIndex, colIndex, item, e, record, row) {
        const vm = this.getViewModel(),
            filename = vm.get('record.name'),
            url = Ext.String.format(
                "{0}{1}/{2}/{3}",
                CMDBuildUI.util.Config.baseUrl,
                vm.get('proxyUrl'),
                record.getId(), // attachment id
                filename // filename
            );

        CMDBuildUI.util.File.download(url, filename);
    }

});
