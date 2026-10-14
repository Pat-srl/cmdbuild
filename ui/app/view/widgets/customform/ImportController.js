Ext.define('CMDBuildUI.view.widgets.customform.ImportController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-customform-import',

    control: {
        '#formatCombo': {
            change: 'onFormatComboChange'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#importBtn': {
            click: 'onImportBtnClick'
        }
    },

    /**
     * 
     * @param {Ext.form.field.ComboBox} combo 
     * @param {String} value 
     * @param {Object} eOpts 
     */
    onFormatComboChange: function (combo, value, eOpts) {
        var view = this.getView(),
            filefield = view.down("draganddropfilefield");
        value = value || ['csv', 'xls', 'xlsx'];
        filefield.setAllowedExtensions(value);
        filefield.isValid();
    },

    /**
     * 
     * @param {Ext.button.Button} btn 
     * @param {Object} eOpts 
     */
    onCancelBtnClick: function (btn, eOpts) {
        this.getView().closePopup();
    },

    /**
     * 
     * @param {Ext.button.Button} btn 
     * @param {Object} eOpts 
     */
    onImportBtnClick: function (btn, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        var me = this,
            vm = btn.lookupViewModel(),
            view = me.getView(),
            filefield = view.down("draganddropfilefield"),
            files = filefield.getValue(),
            importmode = vm.get("importmode"),
            attrs = view.lookupReference("attributesGrid"),
            keys = [],
            config = {
                fileFormat: vm.get("format"),
                csv_separator: vm.get("separator"),
                charset: 'UTF8',
                attributes: []
            };

        if (importmode === "merge") {
            if (Ext.isEmpty(attrs.getSelection())) {
                CMDBuildUI.util.Msg.alert(
                    CMDBuildUI.locales.Locales.notifier.error,
                    CMDBuildUI.locales.Locales.widgets.customform.importexport.missingkeyattr
                );
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                return;
            }
            attrs.getSelection().forEach(function (a) {
                keys.push(a.get("name"));
            });
        }

        // add loader
        var loader = CMDBuildUI.util.Utilities.addLoadMask(view);

        // set attributes property
        vm.get("attrs").getRange().forEach(function (attr) {
            config.attributes.push(attr.getData());
        });

        // the field returns an array also if it has only one element
        files.forEach(function (file) {
            CMDBuildUI.util.File.uploadFileWithMetadata(
                "POST",
                CMDBuildUI.util.Config.baseUrl + "/etl/templates/inline/import",
                file.get('file'),
                config,
                {
                    metadataPartName: 'config'
                }
            ).then(function (items) {
                switch (importmode) {
                    case 'add':
                        me.addItemsToStore(view.getGridStore(), items);
                        break;
                    case 'merge':
                        me.mergeItemsInStore(view.getGridStore(), items, keys);
                        break;
                    case 'replace':
                        me.replaceItemsInStore(view.getGridStore(), items);
                        break;
                }
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                view.closePopup();
            }).otherwise(function (error) {
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                CMDBuildUI.util.Utilities.removeLoadMask(loader);
            });
        });
    },

    privates: {
        /**
         * 
         * @param {Ext.data.Store} store 
         * @param {Object[]} items 
         */
        addItemsToStore: function (store, items) {
            store.add(items);
        },

        /**
         * 
         * @param {Ext.data.Store} store 
         * @param {Object[]} items 
         */
        replaceItemsInStore: function (store, items) {
            store.removeAll();
            store.add(items);
        },

        /**
         * 
         * @param {Ext.data.Store} store 
         * @param {Object[]} items 
         */
        mergeItemsInStore: function (store, items, keys) {
            items.forEach(function (item) {
                var recordPosition = store.findBy(function (r) {
                    var notmatch = false;
                    keys.forEach(function (key) {
                        notmatch = notmatch || r.get(key) != item[key];
                    });
                    return !notmatch;
                });

                if (recordPosition !== -1) {
                    var record = store.getAt(recordPosition);
                    record.beginEdit()
                    Ext.Object.each(item, function (key, value) {
                        if (!Ext.Array.contains(['_id', '_type'], key)) {
                            record.set(key, value);
                        }
                    });
                    record.endEdit();
                } else {
                    store.add(item);
                }
            });
        }

    }
});
