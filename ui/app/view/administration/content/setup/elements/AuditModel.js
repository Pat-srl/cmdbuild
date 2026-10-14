Ext.define('CMDBuildUI.view.administration.content.setup.elements.AuditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-audit',

    formulas: {
        trackingModes: function () {
            return [
                {
                    value: 'always',
                    label: CMDBuildUI.locales.Locales.administration.systemconfig.always
                },
                {
                    value: 'on_error',
                    label: CMDBuildUI.locales.Locales.administration.systemconfig.onerror
                }
            ];
        }
    },

    stores: {
        trackingModesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{trackingModes}',
            autoDestroy: true,
            pageSize: 0
        }
    }
});
