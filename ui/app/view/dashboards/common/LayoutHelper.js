Ext.define('CMDBuildUI.view.dashboards.common.LayoutHelper', {
    singleton: true,

    xtypeMap: {
        [CMDBuildUI.model.dashboards.Component.componentType.bar]: 'dashboards-components-barchart-barchart',
        [CMDBuildUI.model.dashboards.Component.componentType.pareto]: 'dashboards-components-paretochart-paretochart',
        [CMDBuildUI.model.dashboards.Component.componentType.list]: 'dashboards-components-list-list',
        [CMDBuildUI.model.dashboards.Component.componentType.text]: 'dashboards-components-text-text',
        [CMDBuildUI.model.dashboards.Component.componentType.table]: 'dashboards-components-table-table',
        [CMDBuildUI.model.dashboards.Component.componentType.pie]: 'dashboards-components-piechart-piechart',
        [CMDBuildUI.model.dashboards.Component.componentType.line]: 'dashboards-components-linechart-linechart',
        [CMDBuildUI.model.dashboards.Component.componentType.gauge]: 'dashboards-components-gaugechart-gaugechart',
        [CMDBuildUI.model.dashboards.Component.componentType.tile]: 'dashboards-components-tile-tile',
        [CMDBuildUI.model.dashboards.Component.componentType.calendar]: 'dashboards-components-calendar-calendar'
    },

    buildRowsItems: function (obj) {
        var layout = obj.get('layout');
        if (!layout) return [];

        var me = this;
        var allComponents = obj.components();
        var rows = Ext.isArray(layout.rows) ? layout.rows : [];
        var items = [];

        rows.forEach(function (row) {
            var r = { xtype: 'container', layout: 'hbox', items: [] };
            var columns = Ext.isArray(row.columns) ? row.columns : [];
            var base_width = 1 / columns.length;

            columns.forEach(function (column) {
                var c = {
                    xtype: 'container',
                    layout: { type: 'vbox', align: 'stretch' },
                    flex: column.width || base_width,
                    items: []
                };
                var components = Ext.isArray(column.components) ? column.components : [];

                components.forEach(function (componentId) {
                    var component = allComponents.getById(componentId);
                    if (component.get('active')) {
                        var xtype = me.xtypeMap[component.get('type')];
                        if (xtype) {
                            var hideHeader = component.get('hideComponentHeader');
                            c.items.push({
                                xtype: xtype,
                                title: component.get('_description_translation'),
                                header: hideHeader ? false : undefined,
                                cls: hideHeader ? Ext.baseCSSPrefix + 'dashboard-component-no-header' : '',
                                itemDefinition: component
                            });
                        }
                    }
                });

                r.items.push(c);
            });

            items.push(r);
        });

        return items;
    }
});
