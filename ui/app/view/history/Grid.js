Ext.define('CMDBuildUI.view.history.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.history.GridController',
        'CMDBuildUI.view.history.GridModel'
    ],

    alias: 'widget.history-grid',
    controller: 'history-grid',
    viewModel: {
        type: 'history-grid'
    },

    statics: {
        only: 'only',
        full: 'full'
    },

    plugins: [{
        ptype: 'rowwidget',
        expandOnDblClick: true,
        removeWidgetOnCollapse: true,
        widget: {
            xtype: 'history-item',
            viewModel: {} // do not remove otherwise the viewmodel will not be initialized
        }
    }],

    config: {
        className: null,
        allowFilter: false,
        showAddButton: false
    },

    forceFit: true,
    loadMask: true,
    sortableColumns: false,

    columns: [{
        text: CMDBuildUI.locales.Locales.history.type,
        dataIndex: '_historyType',
        align: 'left',
        width: 60,
        maxWidth: 60,
        menuDisabled: true,
        renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
            switch (value) {
                case 'card':
                    return Ext.String.format('<span class="{0}" data-qtip="{1}"></span>', CMDBuildUI.util.helper.IconHelper.getIconId('file', 'regular'), CMDBuildUI.locales.Locales.common.tabs.card);
                case 'reference':
                    return Ext.String.format('<span class="{0}" data-qtip="{1}"></span>', CMDBuildUI.util.helper.IconHelper.getIconId('file-alt', 'regular'), CMDBuildUI.locales.Locales.history.referencerelation);
                case 'relation':
                    return Ext.String.format('<span class="{0}" data-qtip="{1}"></span>', CMDBuildUI.util.helper.IconHelper.getIconId('list', 'solid'), CMDBuildUI.locales.Locales.history.otherrelations);
                case 'system':
                    return Ext.String.format('<span class="{0}" data-qtip="{1}"></span>', CMDBuildUI.util.helper.IconHelper.getIconId('cog', 'solid'), CMDBuildUI.locales.Locales.history.system);
                default:
                    break;
            }
            return value;
        },
        hidden: false,
        localized: {
            text: 'CMDBuildUI.locales.Locales.history.type'
        }
    }, {
        text: CMDBuildUI.locales.Locales.history.begindate,
        dataIndex: '_beginDate',
        align: 'left',
        menuDisabled: true,
        flex: 0.5,
        renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
            return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value);
        },
        hidden: false,
        localized: {
            text: 'CMDBuildUI.locales.Locales.history.begindate'
        }
    }, {
        text: CMDBuildUI.locales.Locales.history.enddate,
        dataIndex: '_endDate',
        align: 'left',
        menuDisabled: true,
        flex: 0.5,
        renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
            return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value);
        },
        hidden: false,
        bind: {
            hidden: '{onlyModeActive}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.history.enddate'
        }
    }, {
        text: CMDBuildUI.locales.Locales.history.user,
        dataIndex: '_user',
        align: 'left',
        hidden: false,
        menuDisabled: true,
        flex: 0.8,
        localized: {
            text: 'CMDBuildUI.locales.Locales.history.user'
        }
    }, {
        text: CMDBuildUI.locales.Locales.history.userdescription,
        dataIndex: '__user_description',
        align: 'left',
        hidden: false,
        menuDisabled: true,
        flex: 0.8,
        localized: {
            text: 'CMDBuildUI.locales.Locales.history.userdescription'
        }
    }, {
        text: CMDBuildUI.locales.Locales.history.activityname,
        dataIndex: 'activities',
        align: 'left',
        hidden: true,
        menuDisabled: true,
        flex: 0.8,
        bind: {
            hidden: '{!isProcess || onlyModeActive}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.history.activityname'
        },
        renderer: function (value) {
            return Ext.Array.pluck(value || [], 'description').join(", ");
        }
    }, {
        text: CMDBuildUI.locales.Locales.history.activityperformer,
        dataIndex: 'activities',
        align: 'left',
        hidden: true,
        menuDisabled: true,
        flex: 0.8,
        bind: {
            hidden: '{!isProcess || onlyModeActive}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.history.activityperformer'
        },
        renderer: function (value) {
            return Ext.Array.pluck(value || [], 'performer').join(", ");
        }
    }, {
        text: CMDBuildUI.locales.Locales.history.processstatus,
        dataIndex: '_status_description',
        align: 'left',
        hidden: true,
        menuDisabled: true,
        flex: 0.5,
        bind: {
            hidden: '{!isProcess || onlyModeActive}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.history.processstatus'
        }
    }, {
        text: CMDBuildUI.locales.Locales.history.relation,
        dataIndex: '_description',
        align: 'left',
        hidden: true,
        hideable: false,
        menuDisabled: true,
        flex: 0.8,
        bind: {
            hidden: '{!historyfilter.references && !historyfilter.relations}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.history.relation'
        }
    }, {
        text: CMDBuildUI.locales.Locales.history.fieldname,
        dataIndex: '_fieldName',
        align: 'left',
        hidden: true,
        hideable: false,
        menuDisabled: true,
        flex: 1,
        bind: {
            hidden: '{!onlyModeActive}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.history.fieldname'
        }
    }, {
        text: CMDBuildUI.locales.Locales.history.value,
        dataIndex: '_newValue',
        align: 'left',
        hidden: true,
        hideable: false,
        menuDisabled: true,
        flex: 1,
        bind: {
            hidden: '{!onlyModeActive}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.history.value'
        }
    }],

    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },

    bind: {
        store: '{objects}'
    },

    tbar: [{
        xtype: 'combobox',
        itemId: 'viewMode',
        editable: false,
        fieldLabel: CMDBuildUI.locales.Locales.history.viewmode,
        valueField: 'value',
        displayField: 'description',
        cls: 'management-input',
        bind: {
            value: '{initialValueViewMode}',
            store: '{viewModes}',
            hidden: '{calendarView}'
        },
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.history.viewmode'
        }
    }, {
        xtype: 'button',
        itemId: 'attributesMenu',
        ui: 'management-neutral-action',
        text: '',
        hidden: true,
        bind: {
            text: '{textAttributes}',
            hidden: '{!onlyModeActive}'
        }
    }, {
        xtype: 'button',
        itemId: 'printHistory',
        ui: 'management-neutral-action',
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('print', 'solid'),
        tooltip: CMDBuildUI.locales.Locales.common.grid.printcsv,
        disabled: false,
        bind: {
            disabled: '{disableButtonPrint}'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.common.grid.printcsv'
        }
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'checkboxfield',
        fieldLabel: CMDBuildUI.locales.Locales.common.tabs.card,
        labelWidth: false,
        labelStyle: 'width: auto',
        itemId: 'filtercard',
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.common.tabs.card'
        },
        value: true,
        bind: {
            value: '{historyfilter.cards}',
            disabled: '{!historyfilter.system && !historyfilter.references && !historyfilter.relations}'
        }
    }, {
        xtype: 'checkboxfield',
        fieldLabel: CMDBuildUI.locales.Locales.history.system,
        labelWidth: false,
        labelStyle: 'width: auto',
        itemId: 'filtersystem',
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.history.system'
        },
        bind: {
            value: '{historyfilter.system}',
            disabled: '{!historyfilter.cards && !historyfilter.references && !historyfilter.relations}'
        }
    }, {
        xtype: 'checkboxfield',
        fieldLabel: CMDBuildUI.locales.Locales.history.referencerelation,
        labelWidth: false,
        labelStyle: 'width: auto',
        itemId: 'filterrelations',
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.history.referencerelation'
        },
        bind: {
            value: '{historyfilter.references}',
            disabled: '{(!historyfilter.cards && !historyfilter.system && !historyfilter.relations) || onlyModeActive}',
            hidden: '{calendarView}'
        }
    }, {
        xtype: 'checkboxfield',
        fieldLabel: CMDBuildUI.locales.Locales.history.otherrelations,
        labelWidth: false,
        labelStyle: 'width: auto',
        itemId: 'filterotherrelations',
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.history.otherrelations'
        },
        bind: {
            value: '{historyfilter.relations}',
            disabled: '{(!historyfilter.cards && !historyfilter.system && !historyfilter.references) || onlyModeActive}',
            hidden: '{calendarView}'
        }
    }]
});