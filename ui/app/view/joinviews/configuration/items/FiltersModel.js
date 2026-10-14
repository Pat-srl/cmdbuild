Ext.define('CMDBuildUI.view.joinviews.configuration.items.FilterModel', {
    extend: 'CMDBuildUI.view.filters.attributes.PanelModel',
    alias: 'viewmodel.joinviews-configuration-items-filters',

    data: {
        allfields: undefined
    },

    formulas: {
        operatorsdata: {
            get: function () {
                var f = CMDBuildUI.model.base.Filter;
                return [
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.overlap,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.overlap
                        ),
                        availablefor: [CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.notoverlap,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.notoverlap
                        ),
                        availablefor: [CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.equal,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.equal
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.tenant
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.notequal,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.notequal
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.tenant
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.null,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.null
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.file,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.link,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.tenant
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.notnull,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.notnull
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.file,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.link,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.tenant
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.greater,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.greater
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.less,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.less
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.between,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.between
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.contain,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.contain
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.link,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.description_contains,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.description_contains
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.description_notcontain,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.description_notcontain
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.description_begin,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.description_begin
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.description_notbegin,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.description_notbegin
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.description_end,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.description_end
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.description_notend,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.description_notend
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.notcontain,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.notcontain
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.link,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.begin,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.begin
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.notbegin,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.notbegin
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.end,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.end
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.notend,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.notend
                        ),
                        availablefor: [
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text
                        ]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.netcontains,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.netcontains
                        ),
                        availablefor: [CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.netcontained,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.netcontained
                        ),
                        availablefor: [CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.netcontainsorequal,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.netcontainsorequal
                        ),
                        availablefor: [CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress]
                    },
                    {
                        value: CMDBuildUI.util.helper.FiltersHelper.operators.netcontainedorequal,
                        label: CMDBuildUI.util.helper.FiltersHelper.getOperatorDescription(
                            CMDBuildUI.util.helper.FiltersHelper.operators.netcontainedorequal
                        ),
                        availablefor: [CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress]
                    }
                ];
            }
        }
    },

    stores: {
        attributeslist: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            sorters: ['label'],
            grouper: {
                property: 'group'
            }
        },
        operatorslist: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{operatorsdata}'
        }
    }
});
