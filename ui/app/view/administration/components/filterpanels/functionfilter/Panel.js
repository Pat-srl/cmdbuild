Ext.define('CMDBuildUI.view.administration.components.filterpanels.functionfilter.Panel', {
    extend: 'Ext.panel.Panel',
    requires: [
        'CMDBuildUI.view.administration.components.filterpanels.functionfilter.PanelController',
        'CMDBuildUI.view.administration.components.filterpanels.functionfilter.PanelModel'
    ],

    alias: 'widget.administration-components-filterpanels-functionfilters-panel',
    controller: 'administration-components-filterpanels-functionfilters-panel',
    viewModel: {
        type: 'administration-components-filterpanels-functionfilters-panel'
    },
    items: [{
        cls: 'panel-with-gray-background',
        padding: '10 10 10 15',
        xtype: 'panel',
        layout: 'column',
        defaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
        items: [{
            columnWidth: '0.5',
            xtype: 'fieldcontainer',
            fieldLabel: CMDBuildUI.locales.Locales.administration.searchfilters.texts.chooseafunction,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.searchfilters.texts.chooseafunction'
            },
            items: [{
                width: '100%',
                xtype: 'combo',
                displayField: 'label',
                valueField: 'value',
                itemId: 'filterFunctionCombo',
                bind: {
                    value: '{_function}',
                    store: '{getFunctionsStore}'
                },
                triggers: {
                    clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                }
            }]
        }],
        bind: {
            hidden: '{actions.view}'
        }
    }, {
        xtype: 'panel',
        padding: '10 10 10 15',
        items: [{
            xtype: 'displayfield',
            labelAlign: 'top',
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.funktion,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.funktion'
            },
            bind: {
                value: '{_function}'
            }
        }],
        bind: {
            hidden: '{!actions.view}'
        },
        renderer: function (value) {
            var func = this.lookupViewModel().getStore('getFunctionsStore').findRecord('value', value);
            if (func) {
                return func.get('description');
            }
            return value;
        }
    }],

    getFunctionData: function () {
        var vm = this.lookupViewModel();
        var func = vm.get('_function') || (vm.get('theFilter.configuration.functions') && vm.get('theFilter.configuration.functions').length && vm.get('theFilter.configuration.functions')[0].name);

        if (func) {
            return [{
                name: func
            }];
        }
        return undefined;
    }

});