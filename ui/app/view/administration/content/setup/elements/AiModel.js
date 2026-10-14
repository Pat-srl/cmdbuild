Ext.define('CMDBuildUI.view.administration.content.setup.elements.AiModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-ai',

    formulas: {
        providerRecord: {
            bind: {
                bindTo: '{theSetup.org__DOT__cmdbuild__DOT__ai__DOT__service__DOT__type}'
            },
            get: function (value) {
                return this.get('aiProviders').findRecord('service', value);
            }
        }
    },

    stores: {
        aiProviders: {
            source: 'pluginmanager.Plugins',
            filters: [
                {
                    property: 'tag',
                    value: 'ai'
                }
            ]
        }
    }
});
