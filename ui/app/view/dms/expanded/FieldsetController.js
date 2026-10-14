Ext.define('CMDBuildUI.view.dms.expanded.FieldsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dms-expanded-fieldset',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    listen: {
        global: {
            updateDataCountStore: 'onUpdateDataCountStore'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.dms.expanded.Fieldset} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = view.lookupViewModel();

        vm.bind('{DMSCategoryType}', function (data) {
            if (data) {
                const record = data.values().findRecord("code", vm.get("dmsCategory")),
                    dmsModel = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(record.get('modelClass'));

                vm.set("basetitle", record.get("_description_translation") || record.get("description"));

                dmsModel.getAttributes().then(function (attrs) {
                    const cols = [];

                    Ext.Array.forEach(attrs.getRange(), function (item, index, allitems) {
                        const field = CMDBuildUI.util.helper.ModelHelper.getModelFieldFromAttribute(item);

                        if (field && field.cmdbuildtype !== CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula) {

                            if (Ext.Array.contains(["FileName", "Version", "Notes"], item.getId())) {
                                field.hidden = false;
                                switch (item.getId()) {
                                    case "FileName":
                                        field.attributeconf._description_translation = CMDBuildUI.locales.Locales.attachments.filename;
                                        break;
                                    case "Version":
                                        field.attributeconf._description_translation = CMDBuildUI.locales.Locales.attachments.version;
                                        field.name = 'version';
                                        break;
                                    case "Notes":
                                        field.attributeconf._description_translation = CMDBuildUI.locales.Locales.common.tabs.notes;
                                        break;
                                }
                            }

                            const col = CMDBuildUI.util.helper.GridHelper.getColumn(field);
                            if (col) {
                                col.hidden = !(item.get("showInGrid") || Ext.Array.contains(["FileName", "Version"], item.getId()));
                                cols.push(col);
                            }
                        }

                    });

                    cols.push({
                        text: CMDBuildUI.locales.Locales.attachments.preview,
                        width: 95,
                        menuDisabled: true,
                        xtype: 'widgetcolumn',
                        widget: {
                            xtype: 'dms-preview',
                            bind: {
                                DMSCategoryType: '{DMSCategoryType}',
                                DMSCategoryTypeValue: '{record.category}',
                                proxyUrl: '{proxyUrl}',
                                attachmentId: '{record._id}',
                                fileName: '{record.name}',
                                fileMimeType: '{record.MimeType}'
                            }
                        },
                        onWidgetAttach: function (col, widget, record) {
                            // Set the current record to the widget
                            const wvm = widget.getViewModel(); // Widget View Model
                            wvm.setData({record: record});
                        },
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.attachments.preview'
                        }
                    })

                    view.add({
                        xtype: 'dms-grid',
                        flex: 1,
                        columns: cols,
                        bind: {
                            store: '{attachmentsCategory}'
                        }
                    });
                });
            }

            vm.bind('{attachmentsCategory}', function (store) {
                vm.set("recordsCount", store.getCount());
            });
        });
    },

    /**
     * Update the number of store elements on header fieldset
     */
    onUpdateDataCountStore: function () {
        const vm = this.getView().lookupViewModel();
        vm.set("recordsCount", vm.get("attachmentsCategory").getCount());
    }

});