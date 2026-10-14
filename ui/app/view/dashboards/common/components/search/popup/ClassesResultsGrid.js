/**
 * Results of the default search: cards of the configured classes, grouped by class.
 */
Ext.define('CMDBuildUI.view.dashboards.common.components.search.popup.ClassesResultsGrid', {
    extend: 'CMDBuildUI.view.dashboards.common.components.search.popup.ResultsGrid',
    alias: 'widget.homepage-globalsearch-classesresultsgrid',

    config: {
        /**
         * @cfg {Function} searchModel Model of the `Class` superclass: it defines the store
         * and the data columns.
         */
        searchModel: null
    },

    cls: Ext.baseCSSPrefix + 'panel-cmdbuildgrouping',

    features: [
        {
            ftype: 'grouping',
            groupHeaderTpl: '{columnName}: {renderedGroupValue} ({[values.children.length]})',
            hideGroupedHeader: true
        }
    ],

    initComponent: function () {
        const me = this;
        const model = me.getSearchModel();

        me.store = {
            model: model,
            groupField: '_type',
            proxy: {
                type: 'memory'
            }
        };

        me.columns = CMDBuildUI.util.helper.GridHelper.getColumns(model.getFields(), {
            allowFilter: false,
            reducedGrid: false,
            objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
            objectTypeName: 'Class'
        });

        me.callParent(arguments);
    }
});
