Ext.define('CMDBuildUI.view.dms.Grid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.dms-grid',

    requires: [
        'CMDBuildUI.view.dms.GridController',
        'CMDBuildUI.view.dms.GridModel',

        // plugins
        'Ext.grid.filters.Filters',
        'CMDBuildUI.components.grid.plugin.FormInRowWidget'
    ],

    mixins: ['CMDBuildUI.mixins.grids.Grid'],

    reserveScrollbar: true,
    controller: 'dms-grid',
    viewModel: {
        type: 'dms-grid'
    },

    ui: 'cmdbuildgrouping',

    features: [
        {
            ftype: 'grouping',
            collapsible: false,
            groupHeaderTpl: [
                '{name:this.formatName} ({children:this.childrenNumber})',
                {
                    formatName: function (name) {
                        if (Ext.isEmpty(name)) {
                            name = CMDBuildUI.locales.Locales.attachments.nocategory;
                        }
                        return name;
                    },
                    childrenNumber: function (children) {
                        return children.length;
                    }
                }
            ],
            depthToIndent: 50,
            enableGroupingMenu: false,
            collapseTip: ''
        }
    ],

    plugins: [
        {
            ptype: 'forminrowwidget',
            expandOnDblClick: false,
            removeWidgetOnCollapse: true,
            widget: CMDBuildUI.util.helper.GridHelper.getFormInRowWidget(
                CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel
            )
        }
    ],

    forceFit: true,

    viewConfig: {
        markDirty: false
    },

    columns: [
        {
            xtype: 'checkcolumn',
            width: 40,
            dataIndex: '_checkAttachment',
            hideable: false,
            menuDisabled: true,
            hidden: true,
            bind: {
                hidden: '{hideCheckColumn}'
            },
            listeners: {
                checkchange: function (column, rowIndex, checked, record, event, eOpts) {
                    var grid = column.ownerCt.ownerCt;
                    grid.ownerCt.fireEvent('changeselection', grid);
                }
            }
        },
        {
            text: CMDBuildUI.locales.Locales.attachments.code,
            dataIndex: 'Code',
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.code'
            },
            renderer: function (value) {
                return CMDBuildUI.util.helper.GridHelper.renderTextColumn(value, false);
            },
            flex: 0.5
        },
        {
            text: CMDBuildUI.locales.Locales.attachments.description,
            dataIndex: 'Description',
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.description'
            },
            renderer: function (value) {
                return CMDBuildUI.util.helper.GridHelper.renderTextColumn(value, false);
            },
            flex: 1
        },
        {
            text: CMDBuildUI.locales.Locales.attachments.filename,
            dataIndex: 'name',
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.filename'
            },
            renderer: function (value) {
                return CMDBuildUI.util.helper.GridHelper.renderTextColumn(value, false);
            },
            flex: 1
        },
        {
            text: CMDBuildUI.locales.Locales.attachments.author,
            dataIndex: 'author',
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.author'
            },
            renderer: function (value) {
                return CMDBuildUI.util.helper.GridHelper.renderTextColumn(value, false);
            },
            flex: 0.5
        },
        {
            text: CMDBuildUI.locales.Locales.attachments.version,
            dataIndex: 'version',
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.version'
            },
            flex: 0.3
        },
        {
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
                wvm.setData({ record: record });
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.preview'
            }
        }
    ]
});
