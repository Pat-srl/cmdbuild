Ext.define('CMDBuildUI.view.joinviews.configuration.items.Domains', {
    extend: 'Ext.form.FieldSet',

    requires: [
        'CMDBuildUI.view.joinviews.configuration.items.DomainsController',
        'CMDBuildUI.view.joinviews.configuration.items.DomainsModel'
    ],
    alias: 'widget.joinviews-configuration-items-domains',
    controller: 'joinviews-configuration-items-domains',
    viewModel: {
        type: 'joinviews-configuration-items-domains'
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    config: {
        fieldsetUi: null
    },
    bind: {
        title: '{fieldsetTitle}',
        ui: '{fieldsetUi}'
    },
    layout: {
        type: 'fit'
    },

    items: [
        {
            xtype: 'treepanel',
            rootVisible: false,
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
                    text: CMDBuildUI.locales.Locales.administration.domains.domain,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.domains.domain'
                    },
                    dataIndex: 'text',
                    itemId: 'text',
                    flex: 0.4
                },
                //  {
                //     text: 'Direction',
                //     dataIndex: 'direction',
                //     itemId: 'direction',
                //     flex: 0.1,
                //     renderer: function (value, cell, record) {
                //         switch (value) {
                //             case 'direct':
                //                 return 'DIRECT';
                //             case 'inverse':
                //                 return 'INVERSE';

                //         }
                //     }
                // },
                {
                    text: CMDBuildUI.locales.Locales.joinviews.domainalias,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.joinviews.domainalias'
                    },
                    hidden: true,
                    dataIndex: 'domainAlias',
                    flex: 0.15,
                    editor: {
                        xtype: 'textfield',
                        vtype: 'nameInputValidation'
                    },
                    itemId: 'domainAliasColumn',
                    align: 'left'
                },
                {
                    text: CMDBuildUI.locales.Locales.joinviews.klass,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.joinviews.klass'
                    },
                    dataIndex: 'targetType',
                    itemId: 'targetType',
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
                            var record = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(value);
                            return record.getTranslatedDescription();
                        }
                        return value;
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.joinviews.targetalias,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.joinviews.targetalias'
                    },
                    itemId: 'targetAlias',
                    dataIndex: 'targetAlias',
                    flex: 0.15,
                    editor: {
                        xtype: 'textfield',
                        vtype: 'nameInputValidation'
                    },
                    align: 'left'
                },
                {
                    text: CMDBuildUI.locales.Locales.joinviews.jointype,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.joinviews.jointype'
                    },
                    dataIndex: 'joinType',
                    itemId: 'joinType',
                    flex: 0.15,
                    editor: {
                        xtype: 'combo',
                        allowBlank: false,
                        editable: false,
                        valueField: 'value',
                        displayField: 'label',
                        bind: {
                            store: '{joinTypesStore}'
                        }
                    },
                    renderer: function (value) {
                        if (value) {
                            var vm = this.lookupViewModel();
                            var store = vm.get('joinTypesStore');
                            var record = store.findRecord('value', value, 0, false, true);
                            return record.get('label');
                        }
                        return value;
                    },
                    align: 'left'
                }
            ]
        }
    ]
});
