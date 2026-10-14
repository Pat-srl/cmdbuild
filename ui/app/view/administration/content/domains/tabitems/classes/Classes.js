Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.classes.Classes', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.domains.tabitems.classes.ClassesController',
        'CMDBuildUI.view.administration.content.domains.tabitems.classes.ClassesModel'
    ],

    alias: 'widget.administration-content-domains-tabitems-domains-classes',
    controller: 'administration-content-domains-tabitems-domains-classes',
    viewModel: {
        type: 'administration-content-domains-tabitems-domains-classes'
    },

    heigth: '500',
    width: '250',
    layout: 'border',
    ui: 'administration-navigation-tree',
    items: [
        {
            xtype: 'components-administration-toolbars-formtoolbar',
            region: 'north',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                {
                    edit: true
                },
                'domains-classes',
                'theDomain'
            )
        },
        {
            xtype: 'treepanel',
            itemId: 'originTree',
            split: true, // enable resizing
            region: 'west',
            layout: 'fit',
            width: '50%',
            viewConfig: {
                markDirty: false
            },
            bind: {
                store: '{originStore}'
            },
            columns: [
                {
                    xtype: 'treecolumn',
                    text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.source,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.source'
                    },
                    dataIndex: 'text',
                    ui: 'administration',
                    flex: 1,
                    align: 'left',
                    renderer: function (val, meta, rec) {
                        if (rec.get('isLayover')) {
                            meta.tdStyle = 'color: gray; font-style: italic;';
                        }
                        return val;
                    }
                },
                {
                    xtype: 'checkcolumn',
                    width: 100,
                    itemId: 'originTreeCheckActive',
                    text: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    bind: {
                        disabled: '{originTreeDisabled}'
                    },
                    dataIndex: 'enabled',
                    renderer: function (value, cell, record, rowIndex, colIndex, store, view) {
                        const config = this.defaultRenderer(value, cell);
                        if (record.get('children') && record.get('children').length) {
                            return '';
                        }
                        return config;
                    }
                }
            ]
        },
        {
            region: 'center', // center region is required, no width/height specified
            xtype: 'treepanel',
            itemId: 'destinationTree',
            layout: 'fit',
            viewConfig: {
                markDirty: false
            },
            ui: 'administration-navigation-tree',
            bind: {
                store: '{destinationStore}'
            },
            columns: [
                {
                    xtype: 'treecolumn',
                    text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination'
                    },
                    dataIndex: 'text',
                    align: 'left',
                    flex: 1
                },
                {
                    xtype: 'checkcolumn',
                    width: 100,
                    itemId: 'destinationTreeCheckActive',
                    text: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    dataIndex: 'enabled',
                    bind: {
                        disabled: '{destinationTreeDisabled}'
                    },
                    renderer: function (value, cell, record, rowIndex, colIndex, store, view) {
                        const config = this.defaultRenderer(value, cell);
                        if (record.get('children') && record.get('children').length) {
                            return;
                        }
                        return config;
                    }
                }
            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(false)
        }
    ]
});
