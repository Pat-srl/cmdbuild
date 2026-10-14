Ext.define('CMDBuildUI.view.filters.relations.Panel', {
    extend: 'Ext.container.Container',

    requires: ['CMDBuildUI.view.filters.relations.PanelController', 'CMDBuildUI.view.filters.relations.PanelModel'],

    alias: 'widget.filters-relations-panel',
    controller: 'filters-relations-panel',
    viewModel: {
        type: 'filters-relations-panel'
    },

    title: CMDBuildUI.locales.Locales.filters.relations,
    layout: 'border',

    items: [
        {
            xtype: 'panel',
            region: 'center',
            layout: 'fit',
            flex: 1,
            items: [
                {
                    xtype: 'grid',
                    forceFit: true,
                    itemId: 'domainsgrid',
                    sortableColumns: false,
                    enableColumnHide: false,
                    viewModel: {},
                    columns: [
                        {
                            text: CMDBuildUI.locales.Locales.filters.description,
                            dataIndex: 'description',
                            align: 'left',
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.filters.description'
                            }
                        },
                        {
                            text: CMDBuildUI.locales.Locales.filters.type,
                            dataIndex: 'destinationDescription',
                            align: 'left',
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.filters.type'
                            }
                        },
                        {
                            text: 'CMDBuildUI.locales.Locales.filters.relations',
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.filters.relations'
                            },
                            columns: [
                                {
                                    xtype: 'checkcolumn',
                                    text: CMDBuildUI.locales.Locales.filters.noone,
                                    dataIndex: 'noone',
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.filters.noone'
                                    },
                                    listeners: {
                                        checkchange: function (column, rowindex, ckecked, record) {
                                            let mode;
                                            if (ckecked) {
                                                mode = CMDBuildUI.util.helper.FiltersHelper.relationstypes.noone;
                                            }
                                            record.set('mode', mode);
                                        }
                                    }
                                },
                                {
                                    xtype: 'checkcolumn',
                                    text: CMDBuildUI.locales.Locales.filters.any,
                                    dataIndex: 'any',
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.filters.any'
                                    },
                                    listeners: {
                                        checkchange: function (column, rowindex, ckecked, record) {
                                            let mode;
                                            if (ckecked) {
                                                mode = CMDBuildUI.util.helper.FiltersHelper.relationstypes.any;
                                            }
                                            record.set('mode', mode);
                                        }
                                    }
                                },
                                {
                                    xtype: 'checkcolumn',
                                    text: CMDBuildUI.locales.Locales.filters.fromselection,
                                    dataIndex: 'oneof',
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.filters.fromselection'
                                    },
                                    listeners: {
                                        checkchange: function (column, rowindex, ckecked, record) {
                                            let mode;
                                            if (ckecked) {
                                                mode = CMDBuildUI.util.helper.FiltersHelper.relationstypes.oneof;
                                            }
                                            record.set('mode', mode);
                                        }
                                    }
                                },
                                {
                                    xtype: 'checkcolumn',
                                    text: CMDBuildUI.locales.Locales.filters.fromfilter,
                                    dataIndex: 'fromfilter',
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.filters.fromfilter'
                                    },
                                    listeners: {
                                        checkchange: function (column, rowindex, ckecked, record) {
                                            let mode;
                                            if (ckecked) {
                                                mode = CMDBuildUI.util.helper.FiltersHelper.relationstypes.fromfilter;
                                            }
                                            record.set('mode', mode);
                                        }
                                    }
                                }
                            ]
                        }
                    ],

                    bind: {
                        store: '{relations}'
                    }
                }
            ]
        },
        {
            xtype: 'panel',
            layout: 'card',
            hidden: true,
            flex: 1,
            region: 'south',
            itemId: 'relselectioncontainer',
            scrollable: true,
            resizable: true
        }
    ],

    localized: {
        title: 'CMDBuildUI.locales.Locales.filters.relations'
    },

    /**
     * @return {Object|Object[]}
     */
    getRelationsData: function () {
        const me = this;
        const vm = me.getViewModel();
        const vc = me.getController();
        const results = [];
        const data = vm.get('relations') ? vm.get('relations').getRange() : [];

        data.forEach(function (d) {
            if (d.get('mode')) {
                const r = {
                    destination: d.get('destination'),
                    direction: d.get('direction'),
                    domain: d.get('domain'),
                    source: vm.get('objectTypeName'),
                    type: d.get('mode')
                };
                switch (r.type) {
                    case CMDBuildUI.util.helper.FiltersHelper.relationstypes.oneof:
                        r.cards = d.get('cards') || [];
                        break;
                    case CMDBuildUI.util.helper.FiltersHelper.relationstypes.fromfilter:
                        const panel = me.down('#' + vc.getFilterPanelId(d));
                        if (panel) {
                            r.filter = panel.getAttributesData();
                        } else {
                            // copies the filter if it has not been modified
                            r.filter = d.get('filter');
                        }
                        break;
                }
                results.push(r);
            }
        });
        return results;
    }
});
