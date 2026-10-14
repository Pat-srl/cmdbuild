Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.Topbar', {
    extend: 'Ext.form.Panel',

    requires: ['CMDBuildUI.view.administration.content.importexport.datatemplates.TopbarController'],

    alias: 'widget.administration-content-importexport-datatemplates-topbar',
    controller: 'administration-content-importexport-datatemplates-topbar',
    viewModel: {},

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },
    forceFit: true,
    loadMask: true,

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',

            items: [
                {
                    xtype: 'button',
                    text: CMDBuildUI.locales.Locales.administration.importexport.texts.adddatatemplate,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.importexport.texts.adddatatemplate'
                    },
                    ui: 'administration-action-small',
                    reference: 'addtemplate',
                    itemId: 'addtemplate',
                    autoEl: {
                        'data-testid': 'administration-toolbar-addImportExportDataTemplateBtn'
                    },
                    bind: {
                        disabled: '{!toolAction._canAdd}'
                    }
                },
                {
                    xtype: 'admin-globalsearchfield',
                    objectType: 'templates',
                    viewModel: {
                        formulas: {
                            emptyText: function () {
                                if (this.getView().up('administration-content-processes-tabpanel')) {
                                    return CMDBuildUI.locales.Locales.administration.globalsearch.emptyText
                                        .templatesexport;
                                }
                                return CMDBuildUI.locales.Locales.administration.globalsearch.emptyText[
                                    this.getView().getObjectType()
                                ];
                            }
                        }
                    }
                }
            ]
        }
    ]
});
