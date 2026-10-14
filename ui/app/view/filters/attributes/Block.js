
Ext.define('CMDBuildUI.view.filters.attributes.Block', {
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.filters.attributes.BlockModel'
    ],

    alias: 'widget.filters-attributes-block',
    viewModel: {
        type: 'filters-attributes-block'
    },

    config: {
        /**
         * @cfg {String} operator
         * One of CMDBuildUI.util.helper.FiltersHelper.blocksoperators
         */
        operator: null,

        /**
         * @cfg {Number} level
         */
        level: 0
    },

    ui: 'filters-attributes-block',

    isFilterBlock: true,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [{
        xtype: 'container',
        layout: 'hbox',
        userCls: Ext.baseCSSPrefix + 'filters-attributes-block-operatorct',
        items: [{
            xtype: 'combobox',
            itemId: 'blockoperatorcombo',
            valueField: 'value',
            displayField: 'label',
            queryMode: 'local',
            width: 100,
            bind: {
                value: '{blockoperator}',
                store: '{blockoperators}',
                disabled: '{displayOnly}'
            },
            autoEl: {
                'data-testid': 'filters-attributes-block-blockoperatorcombo'
            }
        }, {
            xtype: 'box',
            flex: 1
        }, {
            xtype: 'tool',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'regular'),
            userCls: Ext.baseCSSPrefix + 'filters-attributes-removebutton',
            itemId: 'removebutton',
            tooltip: CMDBuildUI.locales.Locales.filters.removeblock,
            disabled: true,
            autoEl: {
                'data-testid': 'filters-attributes-block-deletebtn'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.filters.removeblock'
            },
            bind: {
                hidden: '{displayOnly}',
                disabled: '{disableRemoveBtn}'
            }
        }]
    }, {
        xtype: 'container',
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        itemId: 'blockitems',
        userCls: Ext.baseCSSPrefix + 'filters-attributes-block-itemsct'
    }, {
        xtype: 'container',
        userCls: Ext.baseCSSPrefix + 'filters-attributes-block-addct',
        items: [{
            xtype: 'groupedcombo',
            valueField: 'value',
            displayField: 'label',
            queryMode: 'local',
            forceSelection: true,
            itemId: 'attributecombo',
            emptyText: CMDBuildUI.locales.Locales.filters.attribute,
            width: 250,
            localized: {
                emptyText: 'CMDBuildUI.locales.Locales.filters.attribute'
            },
            autoEl: {
                'data-testid': 'filters-attributes-group-attributecombo'
            },
            bind: {
                store: '{attributeslist}',
                disabled: '{displayOnly}'
            },
            getParentBlock: function () {
                return this.up('filters-attributes-block');
            }
        }]
    }],

    constructor: function () {
        this.callParent(arguments);
        var vm = this.lookupViewModel();

        if (this.getOperator()) {
            vm.set('blockoperator', this.getOperator());
        }
        vm.set('disableRemoveBtn', !this.getLevel());

        if (this.getLevel() % 2) {
            this.setUserCls(Ext.baseCSSPrefix + 'filters-attributes-block-gray');
        }
    },

    updateOperator: function (newValue, oldValue) {
        this.lookupViewModel().set('blockoperator', newValue);
    },

    getFilterData: function () {
        var filter = {},
            blockitems = this.down('#blockitems').items.items;

        if (!Ext.isEmpty(blockitems)) {
            var attrs = filter[this.getViewModel().get('blockoperator')] = [];
            blockitems.forEach(function (i) {
                if (i.isFilterAttribute) {
                    attrs.push({
                        simple: i.getFilterData()
                    });
                } else if (i.isFilterBlock) {
                    attrs.push(i.getFilterData());
                }
            });
        }
        return filter;
    }
});
