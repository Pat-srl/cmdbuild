Ext.define('CMDBuildUI.view.administration.components.viewfilters.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.components.viewfilters.GridController',
        'CMDBuildUI.view.administration.components.viewfilters.GridModel'
    ],

    alias: 'widget.administration-components-viewfilters-grid',
    controller: 'administration-components-viewfilters-grid',
    viewModel: {
        type: 'administration-components-viewfilters-grid'
    },
    config: {
        objectType: null
    },
    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },
    plugins: [{
        ptype: 'administration-forminrowwidget',
        pluginId: 'administration-forminrowwidget',
        expandOnDblClick: true,
        selectRowOnExpand: true,
        // removeWidgetOnCollapse: true,
        widget: {
            xtype: 'administration-components-viewfilters-card-viewinrow',
            ui: 'administration-tabandtools',
            controller: 'administration-components-viewfilters-card-viewinrow',
            layout: 'fit',
            paddingBottom: 10,
            heigth: '100%',
            bind: {
                theViewFilter: '{theViewFilter}',
                actions: '{actions}'
            },
            viewModel: {}
        }
    }],

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.searchfilters.texts.addfilter,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.searchfilters.texts.addfilter'
        },
        reference: 'addBtn',
        itemId: 'addBtn',
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
        ui: 'administration-action',

        bind: {
            hidden: '{newButtonHidden}'
        }
    }, {
        xtype: 'tbfill'
    }],
    forceFit: true,
    loadMask: true,
    bind: {
        store: '{viewfiltersStore}'
    },
    columns: [{
        text: CMDBuildUI.locales.Locales.administration.common.labels.name,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
        },
        dataIndex: 'name',
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.labels.description,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
        },
        dataIndex: 'description',
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.targetclass,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.targetclass'
        },
        dataIndex: 'target_class',
        align: 'left'
    }, {
        // TODO: currently not supported by server
        xtype: 'checkcolumn',
        text: CMDBuildUI.locales.Locales.administration.common.labels.active,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
        },
        hidden: true,
        dataIndex: 'active',
        align: 'center',
        disabled: true
    }
    ]
});