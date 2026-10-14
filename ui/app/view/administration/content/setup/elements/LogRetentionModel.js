Ext.define('CMDBuildUI.view.administration.content.setup.elements.LogRetentionModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-logretention',
    data: {
        mode: 'custom'
    },

    formulas: {
        isDefault: {
            bind: {
                rules: '{theSetup.org__DOT__cmdbuild__DOT__database__DOT__cleanup_rules}',
                defaultMode: '{defaultMode}'
            },
            get: function (data) {
                return data.rules === data.defaultMode;
            }
        },
        // mode: {
        //     bind: '{isDefault}',
        //     get: function (isDefault) {
        //         return isDefault ? 'default' : 'custom';
        //     },
        //     set: function (value) {
        //         if (typeof value != 'string') {
        //             return;
        //         }
        //         value = (value === 'default') ? this.get('defaultMode') : CMDBuildUI.view.administration.content.setup.elements.LogRetention.modes.custom;
        //         this.set('theSetup.org__DOT__cmdbuild__DOT__database__DOT__cleanup_rules', value);
        //     }
        // },
        modesData: {
            get: function () {
                return [
                    {
                        value: 'default',
                        label: CMDBuildUI.locales.Locales.administration.common.labels['default']
                    },
                    {
                        value: 'custom',
                        label: CMDBuildUI.locales.Locales.administration.common.strings.custom
                    }
                ];
            }
        }
    },
    stores: {
        modesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{modesData}'
        }
    }
});
