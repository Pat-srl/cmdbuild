Ext.define('CMDBuildUI.view.administration.content.dms.categorytypes.tabitems.assignedon.View', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.dms.categorytypes.tabitems.assignedon.ViewController',
        'CMDBuildUI.view.administration.content.dms.categorytypes.tabitems.assignedon.ViewModel'
    ],
    alias: 'widget.administration-content-dms-categorytypes-tabitems-assignedon-view',
    controller: 'administration-content-dms-categorytypes-tabitems-assignedon-view',
    viewModel: {
        type: 'administration-content-dms-categorytypes-tabitems-assignedon-view'
    },
    scrollable: 'y',
    items: [{
        xtype: 'container',
        ui: 'messageinfo',  
        margin: 10,      
        hidden: true,
        bind: {
            hidden: '{!defaultCategoryMessage}',
            html: '{defaultCategoryMessage}'
        }
    }, {
        xtype: 'fieldset',
        bind: {
            title: '{classesFieldsetTitle}'
        },
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.classes.title'
        },
        ui: 'administration-formpagination',
        items: [{
            xtype: 'grid',
            headerBorders: false,
            border: false,
            bodyBorder: false,
            rowLines: false,
            sealedColumns: false,
            sortableColumns: false,
            enableColumnHide: false,
            enableColumnMove: false,
            enableColumnResize: false,
            cls: 'administration-reorder-grid',
            bind: {
                store: '{assignedonClassesStore}'
            },
            columns: [{
                flex: 1,
                dataIndex: 'description',
                align: 'left'
            }]
        }]
    }, {
        xtype: 'fieldset',
        bind: {
            title: '{processesFieldsetTitle}'
        },
        ui: 'administration-formpagination',
        items: [{
            xtype: 'grid',
            headerBorders: false,
            border: false,
            bodyBorder: false,
            rowLines: false,
            sealedColumns: false,
            sortableColumns: false,
            enableColumnHide: false,
            enableColumnMove: false,
            enableColumnResize: false,
            cls: 'administration-reorder-grid',
            bind: {
                store: '{assignedonProcessesStore}'
            },
            columns: [{
                flex: 1,
                dataIndex: 'description',
                align: 'left'
            }]
        }]
    }]
});