Ext.define('CMDBuildUI.view.administration.content.pluginmanager.Mixin', {
    mixinId: 'pluginmanager-mixin',
    mixins: ['CMDBuildUI.mixins.System'],

    statics: {
        /**
         * Build the bottom toolbar with Save/Cancel actions for plugin configuration.
         *
         * @returns {Object} Ext toolbar configuration object
         */
        getSaveCancelToolbar: function () {
            return {
                xtype: 'toolbar',
                dock: 'bottom',
                ui: 'footer',
                hidden: true,
                bind: {
                    hidden: '{actions.view || hideButtons}'
                },
                items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
            };
        }
    },

    /**
     * Build plugin configuration fields from plugin configs model
     * @param {Array} configs - Plugin configs object
     */
    buildPluginConfigFields: function (configs) {
        const leftColumns = [];
        const rightColumns = [];
        if (!Ext.Object.isEmpty(configs) && !Ext.isEmpty(configs._model.attributes)) {
            const visibleAttributes = Ext.Array.filter(configs._model.attributes, function (item) {
                return !item.hidden;
            });
            Ext.Array.forEach(visibleAttributes, function (item, index, allitems) {
                const column = {
                    xtype: 'displayfield',
                    fieldLabel: item.name,
                    hidden: true,
                    bind: {
                        hidden: '{actions.edit}',
                        value: '{pluginConfigs.' + item._id.replace(/\./g, '__DOT__') + '}'
                    }
                };

                if (item._description_translation) {
                    column.labelToolIconQtip = item._description_translation;
                    column.labelToolIconCls = CMDBuildUI.util.helper.IconHelper.getIconId('question-circle', 'solid');
                }

                const fieldColumn = Ext.clone(column);

                if (item.mandatory) {
                    fieldColumn.allowBlank = false;
                }

                if (item.password) {
                    column.value = '•••••';
                    delete column.bind.value;
                }

                fieldColumn.bind.hidden = '{actions.view}';
                switch (item.type) {
                    case 'integer':
                        fieldColumn.allowDecimals = false;
                    case 'float':
                        fieldColumn.xtype = 'numberfield';
                        break;
                    case 'select':
                        fieldColumn.xtype = 'combo';
                        const comboStore = [];
                        Ext.Array.forEach(item.options, function (combo, ind, allcombos) {
                            comboStore.push({
                                text: combo
                            });
                        });
                        fieldColumn.store = Ext.create('Ext.data.Store', {
                            fields: ['text'],
                            data: comboStore
                        });
                        fieldColumn.valueField = 'text';
                        break;
                    case 'text':
                        fieldColumn.xtype = 'textareafield';
                        break;
                    case 'string':
                    default:
                        fieldColumn.xtype = 'textfield';
                        if (item.password) {
                            fieldColumn.xtype = 'passwordfield';
                        }
                        break;
                }

                if (index % 2 == 0) {
                    leftColumns.push(column);
                    leftColumns.push(fieldColumn);
                } else {
                    rightColumns.push(column);
                    rightColumns.push(fieldColumn);
                }
            });
        }

        return {
            left: leftColumns,
            right: rightColumns
        };
    },

    /**
     * Build plugin info string for display
     * @param {CMDBuildUI.model.pluginmanager.Plugins} thePlugin
     * @return {String} Formatted plugin info
     */
    buildPluginInfo: function (thePlugin) {
        const expirationDate = thePlugin
            ? Ext.util.Format.date(
                  thePlugin.get('expirationDate'),
                  CMDBuildUI.util.helper.UserPreferences.getDateFormat()
              )
            : null;

        return Ext.String.format(
            '<b>{0}</b> ({1} - V: {2}) - <b>{3}</b>: {4} - <b>{5}</b>: {6}',
            thePlugin.get('description'),
            thePlugin.get('name'),
            thePlugin.get('version'),
            CMDBuildUI.locales.Locales.administration.plugin.fieldlabels.requiredcoreversion,
            thePlugin.get('requiredCoreVersion'),
            CMDBuildUI.locales.Locales.administration.plugin.fieldlabels.expiration,
            expirationDate ? expirationDate : CMDBuildUI.locales.Locales.administration.plugin.fieldlabels.noexpiration
        );
    },

    /**
     * Build plugin status HTML for display
     * @param {CMDBuildUI.model.pluginmanager.Plugins} thePlugin
     * @return {String} Formatted plugin status HTML
     */
    buildPluginStatus: function (thePlugin) {
        let text, cls;
        switch (thePlugin.get('status')) {
            case CMDBuildUI.model.pluginmanager.Plugins.statuses.ready:
                cls = 'ready';
                text = CMDBuildUI.locales.Locales.attachments.statuses.ready;
                break;
            case CMDBuildUI.model.pluginmanager.Plugins.statuses.error:
                cls = 'error';
                text = CMDBuildUI.locales.Locales.attachments.statuses.error;
                break;
        }
        return Ext.String.format(
            '{0} {1}',
            Ext.String.format('<span class="{0}pluginTag">{1}</span>', Ext.baseCSSPrefix, thePlugin.get('tag')),
            Ext.String.format('<span class="{0}pluginTag {1}">{2}</span>', Ext.baseCSSPrefix, cls, text)
        );
    },

    /**
     * Handle reload button click and trigger application reload.
     *
     * @param {Ext.button.Button} button Clicked button instance
     * @param {Event} event Click event object
     * @param {Object} eOpts Additional Ext JS listener options
     */
    onReloadBtnClick: function (button, event, eOpts) {
        this.reloadApp();
    }
});
