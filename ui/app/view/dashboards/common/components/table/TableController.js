Ext.define('CMDBuildUI.view.dashboards.common.components.table.TableController', {
    extend: 'CMDBuildUI.view.dashboards.common.components.basechart.BaseChartController',
    alias: 'controller.dashboards-components-table-table',

    /**
     *
     * @param {CMDBuildUI.view.dashboards.components.table.Table} view
     * @param {CMDBuild.model.dashboards.Component} component
     */
    initTableConfig: function (view, component) {
        const me = this;
        component.getSourceAttributes().then(function (attributes) {
            if (!me.destroyed) {
                view.addChart(me.getTableConfig(component, attributes));
            }
        });
    },

    /**
     *
     * @param {CMDBuild.model.dashboards.Component} component
     * @param {Ext.data.Store} attributes
     * @return {Object}
     */
    getTableConfig: function (component, attributes) {
        const me = this;
        const sourcetype = component.get('dataSourceType');
        const columns = [];

        attributes.getRange().forEach(function (attribute) {
            if (attribute.get('name') === component.get('categoryAxisField')) {
                attribute.set(
                    '_description_translation',
                    component.get('_categoryAxisLabel_translation') ||
                        component.get('categoryAxisLabel') ||
                        attribute.get('description') ||
                        attribute.get('name')
                );
            }
            if ((component.get('valueAxisFields') || []).indexOf(attribute.get('name')) > -1) {
                attribute.set(
                    '_description_translation',
                    attribute.get('_description_translation') ||
                        component.get('_valueAxisLabel_translation') ||
                        component.get('valueAxisLabel') ||
                        attribute.get('description') ||
                        attribute.get('name')
                );
            }
            const field = CMDBuildUI.util.helper.ModelHelper.getModelField(attribute.getData());
            const column = field ? CMDBuildUI.util.helper.GridHelper.getColumn(field) : null;
            if (column) {
                if (attribute.get('name') === component.get('labelField')) {
                    column.text =
                        component.get('_labelField_translation') || component.get('labelField') || column.text;
                }
                column.dataIndex = column.attributename;
                columns.push(column);
            }
        });

        if (sourcetype !== CMDBuildUI.model.dashboards.Component.dataSourceTypes.funktion) {
            columns.push({
                xtype: 'actioncolumn',
                minWidth: 30,
                maxWidth: 30,
                hideable: false,
                align: 'center',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                tooltip: CMDBuildUI.locales.Locales.common.actions.open,
                handler: function (grid, rowIndex, colIndex, item, event, record) {
                    let path;
                    switch (sourcetype) {
                        case CMDBuildUI.model.dashboards.Component.dataSourceTypes.klass:
                            path = 'classes/{0}/cards/{1}';
                            break;
                        case CMDBuildUI.model.dashboards.Component.dataSourceTypes.process:
                            path = 'processes/{0}/instances/{1}';
                            break;
                        case CMDBuildUI.model.dashboards.Component.dataSourceTypes.view:
                            path = 'views/{0}/items/{1}';
                            break;
                    }
                    path = Ext.String.format(path, component.get('dataSourceName'), record.get('_id'));
                    me.redirectTo(path);
                }
            });
        }

        return {
            xtype: 'grid',
            itemId: 'table',
            columns: columns,
            viewConfig: { enableTextSelection: true },
            bind: {
                store: '{records}'
            }
        };
    },

    privates: {
        unlimitedPageSize: 999999,

        /**
         *
         * @param {CMDBuildUI.view.dashboards.common.components.table.Table} view
         * @param {CMDBuild.model.dashboards.Component} component
         */
        configureStore: function (view, component) {
            this.callParent(arguments);
            const limit = component.get('dataSourceLimit');
            view.lookupViewModel().set('storedata.pagesize', limit ? limit : this.unlimitedPageSize);
        }
    }
});
