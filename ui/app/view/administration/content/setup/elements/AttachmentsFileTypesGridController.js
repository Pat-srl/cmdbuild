Ext.define('CMDBuildUI.view.administration.content.setup.elements.AttachmentsFileTypesGridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-attachmentsfiletypesgrid',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addrowBtn': {
            click: 'onAddRowBtnCLick'
        }
    },
    privates: {
        columnMaxWidth: {
            action: 50,
            name: 150,
            extensions: 200
        },
        columnMinWidth: {
            action: 50,
            name: 60,
            extensions: 70,
            mimeTypes: 200
        }
    },
    onBeforeRender: function (view) {
        var columns = [];

        if (view.getFormMode() === CMDBuildUI.util.administration.helper.FormHelper.formActions.view) {
            columns = [
                {
                    text: CMDBuildUI.locales.Locales.administration.common.labels.name,
                    dataIndex: 'name',
                    maxWidth: this.columnMaxWidth.name,
                    minWidth: this.columnMinWidth.name
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.systemconfig.extensions,
                    dataIndex: 'extensions',
                    cellWrap: true,
                    renderer: function (value) {
                        return value.join(', ');
                    },
                    maxWidth: this.columnMaxWidth.extensions,
                    minWidth: this.columnMinWidth.extensions
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.systemconfig.mimetypes,
                    dataIndex: 'mimeTypes',
                    cellWrap: true,
                    renderer: function (value) {
                        return value.join(', ');
                    },
                    minWidth: this.columnMinWidth.mimeTypes
                }
            ];
        } else {
            columns = [
                {
                    xtype: 'widgetcolumn',
                    text: CMDBuildUI.locales.Locales.administration.common.labels.name,
                    dataIndex: 'name',
                    widget: {
                        xtype: 'textfield'
                    },
                    maxWidth: this.columnMaxWidth.name,
                    minWidth: this.columnMinWidth.name
                },
                {
                    xtype: 'widgetcolumn',
                    text: CMDBuildUI.locales.Locales.administration.systemconfig.extensions,
                    dataIndex: '_extensions',
                    widget: {
                        xtype: 'textarea'
                    },
                    maxWidth: this.columnMaxWidth.extensions,
                    minWidth: this.columnMinWidth.extensions
                },
                {
                    xtype: 'widgetcolumn',
                    text: CMDBuildUI.locales.Locales.administration.systemconfig.mimetypes,
                    dataIndex: '_mimeTypes',
                    widget: {
                        xtype: 'textarea'
                    },
                    minWidth: this.columnMinWidth.mimeTypes
                },
                {
                    xtype: 'actioncolumn',
                    minWidth: this.columnMinWidth.action,
                    maxWidth: this.columnMaxWidth.action,
                    bind: {
                        hidden: '{actions.view}'
                    },
                    align: 'center',
                    items: [
                        {
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid'),
                            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                return CMDBuildUI.locales.Locales.administration.common.actions.delete;
                            },
                            handler: 'deleteRow'
                        }
                    ]
                }
            ];
        }
        view.reconfigure(view.getStore(), columns);
    },
    onAddRowBtnCLick: function (button, event, eOpts) {
        var store = this.getView().getStore();
        store.add([CMDBuildUI.model.attachments.AttachmentFileType.create()]);
    },

    deleteRow: function (grid, rowIndex, colIndex, item, event, record, row) {
        var store = record.store;
        store.remove(record);
    }
});
