Ext.define('CMDBuildUI.view.filters.attributes.RowModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.filters-attributes-row',

    data: {
        values: {
            attribute: null,
            operator: null,
            typeinput: null,
            currentGroup: null,
            currentUser: null,
            value1: null,
            value2: null
        },
        hiddenfields: {
            typeinput: true,
            value1: true,
            value2: true,
            currentGroup: true,
            currentUser: true
        },
        labels: {}
    },

    formulas: {
        updateProperties: {
            bind: {},
            get: function () {
                if (this.get('values.value1') === CMDBuildUI.model.users.User.myuser) {
                    this.set('values.currentUser', true);
                    this.set('values.value1', null);
                    this.set('hiddenfields.value1', true);
                }
                if (this.get('values.value1') === CMDBuildUI.model.users.Group.mygroup) {
                    this.set('values.currentGroup', true);
                    this.set('values.value1', null);
                    this.set('hiddenfields.value1', true);
                }

            }
        },

        operatorsFilter: {
            bind: {
                attribute: '{values.attribute}'
            },
            get: function (data) {
                if (data.attribute) {
                    var attributes = this.get("allfields");
                    var attribute = attributes && attributes[data.attribute];

                    if (attribute) {
                        return [{
                            property: 'availablefor',
                            filterFn: function (item) {
                                return item.get("availablefor").indexOf(attribute.cmdbuildtype) !== -1;
                            }
                        }];
                    } else if (!attribute && data.attribute) {
                        return [{
                            property: 'availablefor',
                            filterFn: function (item) {
                                return item.get("availablefor").indexOf('ignore') !== -1;
                            }
                        }]
                    }
                }
            }
        }
    },

    stores: {
        operators: {
            source: '{operatorslist}',
            filters: '{operatorsFilter}'
        }
    }
});