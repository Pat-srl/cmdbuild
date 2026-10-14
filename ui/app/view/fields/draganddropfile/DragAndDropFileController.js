Ext.define('CMDBuildUI.view.fields.draganddropfile.DragAndDropFileController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.draganddropfilefield',

    control: {
        '#filefield': {
            change: 'onFileFieldChange'
        }
    },

    /**
     * 
     * @param {Event} e 
     */
    addDropZone: function (e) {
        var view = this.getView();
        if (!e.browserEvent.dataTransfer || Ext.Array.from(e.browserEvent.dataTransfer.types).indexOf('Files') === -1) {
            return;
        }
        e.stopEvent();
        view.addCls(view.clsDragOver);
    },

    /**
     * 
     * @param {Event} e 
     */
    removeDropZone: function (e) {
        var el = e.getTarget(),
            view = this.getView(),
            thisEl = view.getEl();
        e.stopEvent();

        if (el === thisEl.dom) {
            view.removeCls(view.clsDragOver);
            return;
        }

        while (el !== thisEl.dom && el && el.parentNode) {
            el = el.parentNode;
        }

        if (el !== thisEl.dom) {
            view.removeCls(view.clsDragOver);
        }
    },

    /**
     * 
     * @param {Event} e 
     */
    drop: function (e) {
        var view = this.getView();
        e.stopEvent();
        this.addFilesToStore(e.browserEvent.dataTransfer.files);
        view.removeCls(view.clsDragOver);
    },

    /**
     * 
     * @param {Ext.view.Table} tableview 
     * @param {Number} rowIndex 
     * @param {Number} colIndex 
     * @param {Object} item 
     * @param {Event} e 
     * @param {Ext.data.Model} record 
     * @param {HTMLElement} row 
     */
    // onFilesGridRemoveItem: function(grid, record, eOpts) {
    onFilesGridRemoveItem: function (tableview, rowIndex, colIndex, item, e, record, row) {
        var store = tableview.lookupViewModel().get("files");
        store.remove(record);
    },

    /**
     * 
     * @param {Ext.form.field.File} field 
     * @param {String} value 
     * @param {Object} eOpts 
     */
    onFileFieldChange: function (field, value, eOpts) {
        if (value) {
            this.addFilesToStore(field.fileInputEl.dom.files);
            field.reset(true);
        }
    },

    /**
     * 
     * @param {Ext.data.Store} store 
     * @param {Object} eOpts 
     */
    onStoreDataChanged: function (store, eOpts) {
        this.getView().validate();
    },

    /**
     * 
     * @param {Event} event 
     * @param {HTMLElement} element 
     * @param {Object} opts 
     */
    onGridEmptyClick: function (event, element, opts) {
        var view = this.getView(),
            vm = view.lookupViewModel();
        if (!vm.get("files").getCount()) {
            document.getElementById(view.fileField.fileInputEl.id).click();
        }
    },

    privates: {
        /**
         * 
         * @param {File[]|File} files 
         */
        addFilesToStore: function (files) {
            var view = this.getView(),
                store = this.getViewModel().get("files");
            Ext.Array.forEach(Ext.Array.from(files), function (file) {
                if (store.findExact("name", file.name) === -1) {
                    if (!view.getAllowMultiUpload()) {
                        store.removeAll();
                    }
                    // add file to store
                    store.add({
                        file: file,
                        name: file.name,
                        size: file.size,
                        status: view.getFileStatus(file)
                    });
                } else {
                    CMDBuildUI.util.Notifier.showWarningMessage(Ext.String.format(
                        CMDBuildUI.locales.Locales.attachments.filealreadyinlist,
                        file.name
                    ));
                }
            });
        }
    }

});