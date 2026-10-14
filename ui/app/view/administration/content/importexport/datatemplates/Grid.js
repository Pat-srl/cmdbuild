Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.importexport.datatemplates.GridController',
        'CMDBuildUI.view.administration.content.importexport.datatemplates.GridModel',

        // plugins
        'Ext.grid.filters.Filters',
        'CMDBuildUI.components.grid.plugin.FormInRowWidget'
    ],

    alias: 'widget.administration-content-importexport-datatemplates-grid',
    controller: 'administration-content-importexport-datatemplates-grid',
    viewModel: {
        type: 'administration-content-importexport-datatemplates-grid'
    },
    bind: {
        store: '{allImportExportTemplates}',
        selection: '{selected}'
    },
    itemId: 'importExportGrid',
    reserveScrollbar: true,

    columns: [
        {
            text: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.classdomain,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.classdomain'
            },
            dataIndex: 'targetName',
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.description,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
            },
            dataIndex: 'description',
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.type,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.type'
            },
            dataIndex: 'type',
            align: 'left',
            renderer: function (value) {
                try {
                    var vm = this.lookupViewModel();
                    var store = vm.getStore('templateTypesStore');
                    if (store) {
                        var record = store.findRecord('value', value, false, false, true, true);
                        return record && record.get('label');
                    }
                    return value;
                } catch (e) {
                    return value;
                }
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.fileformat,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.fileformat'
            },
            dataIndex: 'fileFormat',
            align: 'left',
            renderer: function (value) {
                try {
                    var vm = this.lookupViewModel();
                    var store = vm.getStore('fileTypesStore');
                    if (store) {
                        var record = store.findRecord('value', value, false, false, true, true);
                        return record && record.get('label');
                    }
                    return value;
                } catch (e) {
                    return value;
                }
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.active,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
            },
            dataIndex: 'active',
            align: 'center',
            xtype: 'checkcolumn',
            disabled: true
        }
    ],

    plugins: [
        {
            ptype: 'administration-forminrowwidget',
            pluginId: 'administration-forminrowwidget',

            expandOnDblClick: false,
            widget: {
                xtype: 'administration-content-importexport-datatemplates-card-viewinrow',
                ui: 'administration-tabandtools',
                viewModel: {
                    data: {
                        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                        actions: {
                            view: true,
                            edit: false,
                            add: false
                        }
                    }
                },
                bind: {
                    theGateTemplate: '{selected}'
                }
            }
        }
    ],

    autoEl: {
        'data-testid': 'administration-content-importexport-datatemplates-grid'
    },
    dockedItems: [
        {
            xtype: 'administration-content-importexport-datatemplates-topbar',
            dock: 'top',
            borderBottom: 0,
            itemId: 'toolbarscontainer',
            style: 'border-bottom-width:0!important',
            bind: {
                hidden: '{topBarHidden}'
            }
        }
    ],

    forceFit: true,
    loadMask: true,

    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },
    labelWidth: 'auto'
});
