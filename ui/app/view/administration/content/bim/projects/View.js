Ext.define('CMDBuildUI.view.administration.content.bim.projects.View', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.bim.projects.ViewController',
        'CMDBuildUI.view.administration.content.bim.projects.ViewModel'
    ],

    alias: 'widget.administration-content-bim-projects-view',
    controller: 'administration-content-bim-projects-view',
    viewModel: {
        type: 'administration-content-bim-projects-view'
    },

    scrollable: true,
    layout: 'border',

    items: [{
        xtype: 'panel',
        region: 'north',
        dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [{
                xtype: 'button',
                text: CMDBuildUI.locales.Locales.administration.bim.addproject,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.bim.addproject'
                },
                ui: 'administration-action-small',
                itemId: 'addproject',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
                autoEl: {
                    'data-testid': 'administration-bim-projects-addLayerBtn'
                },
                bind: {
                    disabled: '{!toolAction._canAdd}'
                }
            }, {
                xtype: 'localsearchfield',
                gridItemId: '#bimProjectsGrid'
            }]
        }]
    },
    {
        xtype: 'grid',
        itemId: 'bimProjectsGrid',
        region: 'center',
        forceFit: true,
        bind: {
            store: '{projects}',
            selection: '{theProject}'
        },
        viewConfig: {
            markDirty: false
        },
        plugins: [{
            ptype: 'administration-forminrowwidget',
            pluginId: 'administration-forminrowwidget',
            widget: {
                xtype: 'administration-content-bim-projects-card-viewinrow',
                autoHeight: true,
                ui: 'administration-tabandtools'
            }
        }],
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
            text: CMDBuildUI.locales.Locales.administration.bim.lastcheckin,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.bim.lastcheckin'
            },
            dataIndex: 'lastCheckin',
            align: 'left',
            renderer: function (value) {
                return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value);
            }
        }, {
            text: CMDBuildUI.locales.Locales.administration.common.labels.active,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
            },
            disabled: true,
            xtype: 'checkcolumn',
            dataIndex: 'active'
        }]
    }],

    initComponent: function () {
        var vm = this.getViewModel();
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.bim.projects);
        this.callParent(arguments);
    }
});