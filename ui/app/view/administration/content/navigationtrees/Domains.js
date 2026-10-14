Ext.define('CMDBuildUI.view.administration.content.navigationtrees.Domains', {
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.administration.content.navigationtrees.DomainsController',
        'CMDBuildUI.view.administration.content.navigationtrees.DomainsModel'
    ],
    alias: 'widget.administration-content-navigationtrees-domains',
    controller: 'administration-content-navigationtrees-domains',
    viewModel: {
        type: 'administration-content-navigationtrees-domains'
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    layout: {
        type: 'fit'
    },

    items: [
        {
            xtype: 'treepanel',
            autoScroll: true,
            folderSort: true,
            itemId: 'domainstree',
            viewConfig: {
                markDirty: false,
                animate: false
            },
            bind: {
                store: '{treeStore}'
            },
            ui: 'administration-navigation-tree',
            plugins: {
                pluginId: 'cellediting',
                ptype: 'cellediting',
                clicksToEdit: 1,
                listeners: {
                    beforeedit: function (editor, context) {
                        if (editor.view.lookupViewModel().get('actions.view') || !context.record.get('checked')) {
                            return false;
                        }
                    }
                }
            },
            columns: [
                {
                    xtype: 'treecolumn',
                    flex: 0.4,
                    itemId: 'text',
                    dataIndex: 'text',
                    text: CMDBuildUI.locales.Locales.administration.domains.domain,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.domains.domain'
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.joinviews.klass,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.joinviews.klass'
                    },
                    dataIndex: 'targetClass',
                    itemId: 'targetClass',
                    align: 'left',
                    flex: 0.15,
                    editor: {
                        xtype: 'combo',
                        allowBlank: false,
                        editable: false,
                        valueField: 'value',
                        displayField: 'label',
                        queryMode: 'local',
                        store: {
                            model: 'CMDBuildUI.model.base.ComboItem',
                            data: [],
                            proxy: 'memory',
                            sorter: ['label'],
                            autoDestroy: true
                        }
                    },
                    renderer: function (value) {
                        if (value) {
                            const record = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(value);
                            return record.getTranslatedDescription();
                        }
                        return value;
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.common.strings.filtercql,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.strings.filtercql'
                    },
                    dataIndex: 'filter',
                    flex: 0.4,
                    editor: 'textfield',
                    align: 'left'
                },
                {
                    xtype: 'checkcolumn',
                    text: CMDBuildUI.locales.Locales.administration.common.strings.recursive,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.strings.recursive'
                    },
                    dataIndex: 'recursionEnabled',
                    width: 100,
                    align: 'center',
                    renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                        if (record.get('domain')) {
                            const domain = Ext.getStore('domains.Domains').findRecord('name', record.get('domain'));
                            if (domain && domain.get('source') === domain.get('destination')) {
                                return this.defaultRenderer(value, metaData);
                            }
                        }
                        return '';
                    },
                    listeners: {
                        beforecheckchange: function () {
                            return !this.getView().lookupViewModel().get('actions.view');
                        }
                    }
                }
            ]
        }
    ]
});
