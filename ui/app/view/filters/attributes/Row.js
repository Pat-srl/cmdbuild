Ext.define('CMDBuildUI.view.filters.attributes.Row', {
    extend: 'Ext.form.FieldContainer',

    requires: ['CMDBuildUI.view.filters.attributes.RowController', 'CMDBuildUI.view.filters.attributes.RowModel'],

    alias: 'widget.filters-attributes-row',
    controller: 'filters-attributes-row',
    viewModel: {
        type: 'filters-attributes-row'
    },

    statics: {
        /**
         * Returns the header for fieldset.
         * @param {Object} config
         * @param {Boolean} config.hideInputLabel
         * @param {Boolean} config.removeRightSpace
         * @returns {Object}
         */
        getHeader: function (config) {
            config = config || {};
            return {
                layout: 'hbox',
                xtype: 'fieldcontainer',
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                items: [
                    {
                        flex: 0.3,
                        xtype: 'fieldcontainer',
                        fieldLabel: CMDBuildUI.locales.Locales.filters.operator
                    },
                    {
                        flex: 0.2,
                        xtype: 'fieldcontainer',
                        fieldLabel: !config.hideInputLabel ? CMDBuildUI.locales.Locales.filters.typeinput : ''
                    },
                    {
                        flex: 0.5,
                        xtype: 'fieldcontainer',
                        fieldLabel: CMDBuildUI.locales.Locales.filters.value,
                        margin: !config.removeRightSpace ? '0 15 0 0' : undefined // use the margin for the alignment
                    }
                ]
            };
        }
    },

    config: {
        /**
         * @cfg {Boolean} allowInputParameter
         */
        allowInputParameter: true,

        /**
         * @cfg {Boolean} allowCurrentUser
         */
        allowCurrentUser: false,

        /**
         * @cfg {Boolean} allowArbitraryAttributeName
         */
        allowArbitraryAttributeName: false,

        /**
         * @cfg {Boolean} allowCurrentGroup
         */
        allowCurrentGroup: false
    },

    isFilterAttribute: true,

    layout: 'hbox',

    fieldBodyCls: Ext.baseCSSPrefix + 'filters-attributes-row-body',
    userCls: Ext.baseCSSPrefix + 'filters-attributes-row',

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,

    defaults: {
        margin: 'auto 10 auto auto',
        layout: 'anchor'
    },

    items: [
        {
            xtype: 'displayfield',
            flex: 0.2,
            margin: '6px 0',
            bind: {
                value: '{values.attribute_description}',
                hidden: '{attachments.operator_label}'
            }
        },
        {
            xtype: 'fieldcontainer',
            flex: 0.3,
            itemId: 'comboboxoperator',
            items: [
                {
                    xtype: 'combobox',
                    valueField: 'value',
                    displayField: 'label',
                    queryMode: 'local',
                    forceSelection: true,
                    reference: 'operatorcombo',
                    itemId: 'operatorcombo',
                    emptyText: CMDBuildUI.locales.Locales.filters.operator,
                    autoEl: {
                        'data-testid': 'filters-attributes-row-operatorcombo'
                    },
                    bind: {
                        store: '{operators}',
                        value: '{values.operator}',
                        disabled: '{displayOnly}'
                    },
                    localized: {
                        emptyText: 'CMDBuildUI.locales.Locales.filters.operator'
                    }
                }
            ]
        },
        {
            xtype: 'fieldcontainer',
            flex: 0.2,
            items: [
                {
                    xtype: 'checkboxfield',
                    hidden: true,
                    reference: 'typecheck',
                    itemId: 'typecheck',
                    boxLabel: CMDBuildUI.locales.Locales.filters.typeinput,
                    autoEl: {
                        'data-testid': 'filters-attributes-row-typecheck'
                    },
                    bind: {
                        value: '{values.typeinput}',
                        hidden: '{hiddenfields.typeinput}',
                        readOnly: '{displayOnly}'
                    },
                    localized: {
                        boxLabel: 'CMDBuildUI.locales.Locales.filters.typeinput'
                    }
                }
            ]
        },
        {
            xtype: 'fieldcontainer',
            flex: 0.5,
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'checkboxfield',
                    hidden: true,
                    reference: 'currentUser',
                    itemId: 'currentUser',
                    margin: 'auto 10px auto auto',
                    boxLabel: CMDBuildUI.locales.Locales.filters.currentuser,
                    autoEl: {
                        'data-testid': 'filters-attributes-row-currentuser'
                    },
                    bind: {
                        value: '{values.currentUser}',
                        hidden: '{hiddenfields.currentUser}',
                        readOnly: '{displayOnly}'
                    },
                    localized: {
                        boxLabel: 'CMDBuildUI.locales.Locales.filters.currentuser'
                    }
                },
                {
                    xtype: 'checkboxfield',
                    hidden: true,
                    reference: 'currentGroup',
                    itemId: 'currentGroup',
                    margin: 'auto 10px auto auto',
                    boxLabel: CMDBuildUI.locales.Locales.filters.currentgroup,
                    autoEl: {
                        'data-testid': 'filters-attributes-row-currentgroup'
                    },
                    bind: {
                        value: '{values.currentGroup}',
                        hidden: '{hiddenfields.currentGroup}',
                        readOnly: '{displayOnly}'
                    },
                    localized: {
                        boxLabel: 'CMDBuildUI.locales.Locales.filters.currentgroup'
                    }
                },
                {
                    xtype: 'fieldcontainer',
                    layout: 'anchor',
                    reference: 'valuescontainer',
                    itemId: 'valuescontainer',
                    flex: 1,
                    autoEl: {
                        'data-testid': 'filters-attributes-row-values'
                    }
                }
            ]
        },
        {
            xtype: 'fieldcontainer',
            reference: 'actionscontainer',
            itemId: 'actionscontainer',
            width: '50px',
            items: [
                {
                    xtype: 'tool',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'regular'),
                    userCls: Ext.baseCSSPrefix + 'filters-attributes-removebutton',
                    itemId: 'removebutton',
                    tooltip: CMDBuildUI.locales.Locales.filters.removeattribute,
                    autoEl: {
                        'data-testid': 'filters-attributes-row-removebutton'
                    },
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.filters.removeattribute'
                    },
                    bind: {
                        hidden: '{displayOnly}'
                    }
                }
            ]
        }
    ],

    /**
     * @return {Object} An object with filter data.
     */
    getFilterData: function () {
        const vm = this.getViewModel();
        const allFields = vm.get('allfields');
        const data = {
            attribute: vm.get('values.attribute'),
            operator: vm.get('values.operator'),
            parameterType: vm.get('values.typeinput')
                ? CMDBuildUI.util.helper.FiltersHelper.parameterstypes.runtime
                : CMDBuildUI.util.helper.FiltersHelper.parameterstypes.fixed,
            value: []
        };

        if (vm.get('values.category')) {
            data.category = vm.get('values.category');
        }

        if (vm.get('values.model')) {
            data.model = vm.get('values.model');
        }

        if (!allFields || !allFields[data.attribute]) {
            data.value.push(vm.get('values.value1'));
        } else if (
            data.parameterType === CMDBuildUI.util.helper.FiltersHelper.parameterstypes.fixed &&
            data.operator !== CMDBuildUI.util.helper.FiltersHelper.operators.null &&
            data.operator !== CMDBuildUI.util.helper.FiltersHelper.operators.notnull
        ) {
            const type = allFields[data.attribute].cmdbuildtype;
            if (vm.get('values.currentGroup')) {
                data.value.push(CMDBuildUI.model.users.Group.mygroup);
            } else if (vm.get('values.currentUser')) {
                data.value.push(CMDBuildUI.model.users.User.myuser);
            } else if (CMDBuildUI.util.helper.FiltersHelper.isOperatorForReferenceOrLookupDescription(data.operator)) {
                data.value.push(vm.get('values.referencetext'));
            } else if (
                vm.get('values.value1') == null &&
                type == CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean
            ) {
                data.value.push(false);
            } else if (
                !Ext.isEmpty(vm.get('values.value1')) &&
                type == CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date
            ) {
                data.value.push(Ext.Date.format(vm.get('values.value1'), 'Y-m-d'));
            } else {
                data.value.push(vm.get('values.value1'));
            }
            // add value2 when operator is `between`
            if (data.operator === CMDBuildUI.util.helper.FiltersHelper.operators.between) {
                data.value.push(vm.get('values.value2'));
            }
        }
        return data;
    }
});
