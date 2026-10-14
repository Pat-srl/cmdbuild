Ext.define('CMDBuildUI.view.administration.content.setup.elements.BimModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-bim',
    data: {
        defaultViewer: 'bimserver'
    },
    formulas: {
        updateDisplayPassword: {
            bind: {
                password: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__bimserver__DOT__password}'
            },
            get: function (data) {
                var hiddenPassword = CMDBuildUI.util.administration.helper.RendererHelper.getDisplayPassword(data.password);
                this.set('hiddenPassword', hiddenPassword);
            }
        },
        viewerManager: {
            bind: {
                viewer: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__viewer}',
                store: '{viewersStore}'
            },
            get: function (data) {
                if (data.viewer && data.store) {
                    this.set('viewerDescription', data.store.findRecord('value', data.viewer).get('label'));
                    this.set('defaultViewer', data.viewer);
                }
            }
        },
        viewers: function () {
            return [{
                value: 'xeokit',
                label: CMDBuildUI.locales.Locales.administration.systemconfig.xeokit
            }];
        }
    },

    stores: {
        viewersStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{viewers}',
            autoLoad: true,
            autoDestroy: true
        }
    }
});