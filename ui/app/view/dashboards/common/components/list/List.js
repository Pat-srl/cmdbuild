const linkIcon = CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid');

Ext.define('CMDBuildUI.view.dashboards.common.components.list.List', {
    extend: 'CMDBuildUI.view.dashboards.common.components.Component',

    requires: [
        'CMDBuildUI.view.dashboards.common.components.list.ListController',
        'CMDBuildUI.view.dashboards.common.components.list.ListModel'
    ],

    alias: 'widget.dashboards-components-list-list',
    controller: 'dashboards-components-list-list',
    viewModel: {
        type: 'dashboards-components-list-list'
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    customActions: [
        {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sync-alt', 'solid'),
            text: CMDBuildUI.locales.Locales.dashboards.tools.reload,
            handler: 'menuItemReloadHandler',
            scope: 'controller'
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
        const list = this.down('#list');
        if (list) {
            list.tpl = new Ext.XTemplate(
                '<tpl for=".">',
                '   <div class="dashboard-list-item {[this.getLinkClass()]}">',
                '       <div class="row-space-between">',
                '           <span class="title">{[this.getValue(values, "titleField")]}</span>',
                '           <tpl if="this.hasValue(values, \'tagField\')">',
                '               <span class="tag">{[this.getValue(values, "tagField")]}</span>',
                '           </tpl>',
                '       </div>',
                '       <tpl if="this.hasValue(values, \'metaField\')">',
                '           <div class="meta">{[this.getValue(values, "metaField")]}</div>',
                '       </tpl>',
                '        <tpl if="this.hasValue(values, \'descriptionField\')">',
                '            <div class="description">{[this.getValue(values, "descriptionField")]}</div>',
                '        </tpl>',
                '   </div>',
                '</tpl>',

                {
                    hasValue: function (values, fieldType) {
                        return this.controller ? this.controller.hasFieldValue(values, fieldType) : false;
                    },
                    getValue: function (values, fieldType) {
                        return this.controller ? this.controller.getFormattedFieldValue(values, fieldType) : '';
                    },
                    hasLink: function () {
                        return this.controller ? this.controller.hasLinkToClass() : false;
                    },
                    getLinkClass: function () {
                        return this.hasLink() ? 'dashboard-list-item-link' : '';
                    }
                }
            );
        }
    },

    items: [
        {
            xtype: 'dataview',
            itemId: 'list',
            cls: 'dashboard-list-view',
            itemSelector: '.dashboard-list-item',
            tpl: '',
            scrollable: true,
            flex: 1,
            bind: {
                store: '{records}',
                maxHeight: '{listMaxHeight}',
                emptyText: '{emptyTextFormatted}'
            },
            listeners: {
                itemclick: 'onListItemClick'
            }
        },
        {
            xtype: 'container',
            layout: {
                type: 'hbox',
                pack: 'end'
            },
            items: [
                {
                    xtype: 'button',
                    itemId: 'showAllButton',
                    margin: '8 0 0 0',
                    text: CMDBuildUI.locales.Locales.dashboards.list.showall,
                    ui: 'management-primary-outline-small',
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.dashboards.list.showall'
                    },
                    bind: {
                        hidden: '{isShowAllButtonHidden}'
                    }
                }
            ]
        }
    ]
});
