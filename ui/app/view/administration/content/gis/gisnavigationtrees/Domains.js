Ext.define('CMDBuildUI.view.administration.content.gis.gisnavigationtrees.Domains', {
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.administration.content.gis.gisnavigationtrees.DomainsController',
        'CMDBuildUI.view.administration.content.gis.gisnavigationtrees.DomainsModel'
    ],
    alias: 'widget.administration-content-gis-gisnavigationtrees-domains',
    controller: 'administration-content-gis-gisnavigationtrees-domains',
    viewModel: {
        type: 'administration-content-gis-gisnavigationtrees-domains'
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    layout: {
        type: 'fit'
    },

    items: [
        {
            xtype: 'treepanel',
            // rootVisible: false,
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
                    xtype: 'checkcolumn',
                    text: CMDBuildUI.locales.Locales.administration.bim.multilevel,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.bim.multilevel'
                    },
                    dataIndex: 'showOnlyOne',
                    width: 100,
                    align: 'center',
                    listeners: {
                        beforecheckchange: function (check, rowIndex, checked, record, event, eOpts) {
                            var isEdit = !this.getView().lookupViewModel().get('actions.view');
                            var isRecordChecked = record.get('checked');
                            return isEdit && isRecordChecked;
                        }
                    }
                },
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
                // {
                //     text: 'Direction',
                //     dataIndex: 'direction',
                //     itemId: 'direction',
                //     flex: 0.2,
                //     renderer: function (value, cell, record) {
                //         switch (value) {
                //             case '_1':
                //                 return 'DIRECT';
                //             case '_2':
                //                 return 'INVERSE';
                //         }
                //     }
                // },
                {
                    text: CMDBuildUI.locales.Locales.joinviews.klass,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.joinviews.klass'
                    },
                    dataIndex: 'targetClass',
                    itemId: 'targetClass',
                    align: 'left',
                    flex: 0.2,
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
                    text: CMDBuildUI.locales.Locales.administration.common.strings.filtercql,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.strings.filtercql'
                    },
                    dataIndex: 'filter',
                    flex: 0.2,
                    editor: 'textfield',
                    align: 'left'
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.navigationtrees.fieldlabels.label,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.navigationtrees.fieldlabels.label'
                    },
                    dataIndex: 'description',
                    flex: 0.2,
                    editor: 'textfield',
                    align: 'left'
                },
                {
                    xtype: 'actioncolumn',
                    width: 50,
                    align: 'center',
                    handler: 'onViewModeBtnClick',
                    getClass: function (value, metadata, record, rowIndex, colIndex, store, grid) {
                        return CMDBuildUI.util.helper.IconHelper.getIconId('cog', 'solid');
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                        var targetClass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                            record.get('targetClass') || record.get('domainTargetClass')
                        );
                        if (!record.get('checked') || (targetClass && !targetClass.get('prototype'))) {
                            return true;
                        }
                        return false;
                    }
                }
            ]
        }
    ]
});
