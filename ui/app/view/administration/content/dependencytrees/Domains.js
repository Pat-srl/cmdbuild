Ext.define('CMDBuildUI.view.administration.content.dependencytrees.Domains', {
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.administration.content.dependencytrees.DomainsController',
        'CMDBuildUI.view.administration.content.dependencytrees.DomainsModel'
    ],
    alias: 'widget.administration-content-dependencytrees-domains',
    controller: 'administration-content-dependencytrees-domains',
    viewModel: {
        type: 'administration-content-dependencytrees-domains'
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
            ui: 'administration-dependency-tree',
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
                    text: CMDBuildUI.locales.Locales.administration.localizations.class,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.localizations.class'
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
                    xtype: 'actioncolumn',
                    width: 50,
                    align: 'center',
                    handler: 'onNodeSettingsBtnClick',
                    tooltip: CMDBuildUI.locales.Locales.administration.dependencytrees.nodesettings,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.dependencytrees.nodesettings'
                    },
                    getClass: function (value, metadata, record, rowIndex, colIndex, store, grid) {
                        return CMDBuildUI.util.helper.IconHelper.getIconId('cog', 'solid');
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                        return !record.get('checked') || record.get('isRoot') ? true : false;
                    }
                }
            ]
        }
    ]
});
