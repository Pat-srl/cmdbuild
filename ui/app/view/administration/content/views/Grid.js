Ext.define('CMDBuildUI.view.administration.content.views.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.views.GridController',
        'CMDBuildUI.view.administration.content.views.GridModel'
    ],

    alias: 'widget.administration-content-views-grid',
    controller: 'administration-content-views-grid',
    viewModel: {
        type: 'administration-content-views-grid'
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
        widget: {
            xtype: 'administration-content-views-card-viewinrow',
            ui: 'administration-tabandtools',
            controller: 'administration-content-views-card-viewinrow',
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
        localized:{
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
        // move all buttons on right side
        xtype: 'tbfill'
    }],
    forceFit: true,
    loadMask: true,
    bind: {
        store: '{viewsStore}'
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
            text: CMDBuildUI.locales.Locales.administration.views.targetclass,
            localized:{
                text: 'CMDBuildUI.locales.Locales.administration.views.targetclass'
            },
            dataIndex: 'target_class',
            align: 'left'
        }
        // {
        //     // TODO: currently not supported
        //     xtype: 'checkcolumn',
        //     text: CMDBuildUI.locales.Locales.administration.common.labels.active,
        //     localized: {
        //         text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
        //     },
        //     dataIndex: 'active',
        //     align: 'center',
        //     disabled: true,
        // }
    ]
});