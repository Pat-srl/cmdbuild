Ext.define('CMDBuildUI.view.dashboards.common.components.search.popup.ResultsGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.homepage-globalsearch-resultsgrid',

    reference: 'resultsGrid',
    stripeRows: true,
    reserveScrollbar: true,

    listeners: {
        rowdblclick: 'onRowDblClick',
        scope: 'controller'
    },

    initComponent: function () {
        const me = this;
        const columns = me.columns || [];
        const hasTypeColumn = Ext.Array.some(columns, function (column) {
            return column.dataIndex === '_type';
        });
        me.columns = (hasTypeColumn ? [] : [me.getTypeColumnConfig()]).concat(columns, me.getActionColumnsConfig());
        me.callParent(arguments);
    },

    privates: {
        /**
         * @returns {Object}
         */
        getTypeColumnConfig: function () {
            return {
                text: CMDBuildUI.locales.Locales.common.grid.subtype,
                dataIndex: '_type',
                flex: 0.8,
                renderer: function (value) {
                    return Ext.String.htmlEncode(
                        CMDBuildUI.util.helper.ModelHelper.getObjectDescription(value) || value
                    );
                }
            };
        },

        /**
         * Built at runtime: the action column reads `tooltip` in its constructor,
         * before `localized` values are resolved.
         *
         * @returns {Object[]}
         */
        getActionColumnsConfig: function () {
            const locales = CMDBuildUI.locales.Locales;
            const IconHelper = CMDBuildUI.util.helper.IconHelper;
            const defaults = {
                xtype: 'actioncolumn',
                width: 30,
                align: 'center',
                hideable: false,
                menuDisabled: true
            };

            return [
                Ext.apply(
                    {
                        iconCls: IconHelper.getIconId('search', 'solid'),
                        tooltip: locales.classes.cards.opencard,
                        handler: 'onViewCardClick'
                    },
                    defaults
                ),
                Ext.apply(
                    {
                        iconCls: IconHelper.getIconId('external-link-alt', 'solid'),
                        tooltip: locales.relations.opencard,
                        handler: 'onRedirectToCardClick'
                    },
                    defaults
                )
            ];
        }
    }
});
