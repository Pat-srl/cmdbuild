Ext.define('CMDBuildUI.view.administration.content.pluginmanager.PluginModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-pluginmanager-plugin',

    data: {
        thePlugin: null,
        pluginInfo: '',
        pluginStatus: '',
        hideButtons: false,
        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
        actions: {
            view: true,
            edit: false,
            add: false
        },
        isPatchesAvailable: false,
        recalculateConfigs: true,
        configsAttributes: {},
        pluginConfigs: {}
    },

    formulas: {
        setActions: {
            bind: '{action}',
            get: function (action) {
                this.set('actions.view', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },

        setPluginConfigs: {
            bind: {
                thePlugin: '{thePlugin}',
                recalculateConfigs: '{recalculateConfigs}'
            },
            get: function (data) {
                const me = this;
                const thePlugin = data.thePlugin;
                const pluginConfigs = {};

                if (data.recalculateConfigs && thePlugin && !Ext.isEmpty(thePlugin.get('configs'))) {
                    me.set('recalculateConfigs', false);
                    Ext.Object.each(thePlugin.get('configs'), function (key, value, myself) {
                        if (key !== '_model') {
                            pluginConfigs[key.replace(/\./g, '__DOT__')] = value;
                        } else {
                            const configsAttributes = {};
                            Ext.Array.forEach(value.attributes, function (item, index, allitems) {
                                configsAttributes[item['_id']] = item;
                            });
                            me.set('configsAttributes', configsAttributes);
                        }
                    });
                    me.set('pluginConfigs', pluginConfigs);
                }
            }
        }
    }
});
