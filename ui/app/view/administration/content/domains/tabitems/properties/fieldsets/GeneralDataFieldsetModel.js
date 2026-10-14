Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldsetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-domains-tabitems-properties-fieldsets-generaldatafieldset',

    formulas: {
        cascadeActions: function () {
            return CMDBuildUI.util.administration.helper.ModelHelper.getCascadeActions();
        },

        isN1or1N: {
            bind: '{theDomain.cardinality}',
            get: function (cardinality) {
                if (['1:N', 'N:1'].indexOf(cardinality) > -1) {
                    return true;
                } else {
                    this.set('theDomain.isMasterDetail', false);
                    return false;
                }
            }
        },

        getAllClassesProcesses: {
            get: function () {
                const data = [];
                const types = {
                    classes: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.classes, // Classes
                        childrens: Ext.Array.filter(
                            Ext.getStore('classes.Classes').getData().getRange(),
                            function (item) {
                                return item.get('type') === 'standard';
                            }
                        )
                    },
                    processes: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.processes, // Processes
                        childrens: Ext.getStore('processes.Processes').getData().getRange()
                    }
                };

                Object.keys(types).forEach(function (type, typeIndex) {
                    types[type].childrens.forEach(function (value, index) {
                        var item = {
                            group: type,
                            groupLabel: types[type].label,
                            _id: value.get('_id'),
                            label: value.get('description')
                        };
                        data.push(item);
                    });
                });

                data.sort(function (a, b) {
                    const aGroup = a.group.toUpperCase();
                    const bGroup = b.group.toUpperCase();
                    const aLabel = a.label.toUpperCase();
                    const bLabel = b.label.toUpperCase();

                    if (aGroup === bGroup) {
                        return aLabel < bLabel ? -1 : aLabel > bLabel ? 1 : 0;
                    } else {
                        return aGroup < bGroup ? -1 : 1;
                    }
                });
                return data;
            }
        },

        cascadeActionsDirectStoreFilter: {
            bind: {
                bindTo: '{theDomain.cascadeActionDirect}',
                single: true
            },
            get: function (cascadeActionsDirect) {
                if (cascadeActionsDirect !== CMDBuildUI.model.domains.Domain.cascadeAction.auto) {
                    return [
                        function (item) {
                            return item.get('value') !== CMDBuildUI.model.domains.Domain.cascadeAction.auto;
                        }
                    ];
                } else {
                    return [];
                }
            }
        },

        cascadeActionsInverseStoreFilter: {
            bind: {
                bindTo: '{theDomain.cascadeActionInverse}',
                single: true
            },
            get: function (cascadeActionsInverse) {
                if (cascadeActionsInverse !== CMDBuildUI.model.domains.Domain.cascadeAction.auto) {
                    return [
                        function (item) {
                            return item.get('value') !== CMDBuildUI.model.domains.Domain.cascadeAction.auto;
                        }
                    ];
                } else {
                    return [];
                }
            }
        }
    },

    stores: {
        cardinalityStore: {
            autoLoad: true,
            autoDestroy: true,
            fields: ['value', 'label'],
            proxy: {
                type: 'memory'
            },
            data: [
                {
                    value: '1:1',
                    label: '1:1'
                },
                {
                    value: '1:N',
                    label: '1:N'
                },
                {
                    value: 'N:1',
                    label: 'N:1'
                },
                {
                    value: 'N:N',
                    label: 'N:N'
                }
            ]
        },

        getAllStandardClassesAndProcessesStore: {
            data: '{getAllClassesProcesses}',
            proxy: {
                type: 'memory'
            },
            autoDestroy: true
        },

        sourceClassStore: {
            source: '{getAllStandardClassesAndProcessesStore}',
            autoDestroy: true
        },

        destinationClassStore: {
            source: '{getAllStandardClassesAndProcessesStore}',
            autoDestroy: true
        },

        cascadeActionsStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{cascadeActions}',
            autoDestroy: true
        },

        cascadeActionsDirectStore: {
            source: '{cascadeActionsStore}',
            filters: '{cascadeActionsDirectStoreFilter}'
        },

        cascadeActionsInverseStore: {
            source: '{cascadeActionsStore}',
            filters: '{cascadeActionsInverseStoreFilter}'
        }
    }
});
