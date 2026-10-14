Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.domains.Domains', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.processes.tabitems.domains.DomainsController',
        'CMDBuildUI.view.administration.content.processes.tabitems.domains.DomainsModel'
    ],

    alias: 'widget.administration-content-processes-tabitems-domains-domains',
    controller: 'administration-content-processes-tabitems-domains-domains',
    viewModel: {
        type: 'administration-content-processes-tabitems-domains-domains'
    },

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true,
        selected: null
    },

    itemId: 'processDomainsGrid',
    forceFit: true,
    loadMask: true,
    labelWidth: 'auto',

    viewConfig: {
        plugins: [
            {
                ptype: 'gridviewdragdrop',
                dragText: CMDBuildUI.locales.Locales.administration.attributes.strings.draganddrop,
                // TODO: localized not work as expected
                localized: {
                    dragText: 'CMDBuildUI.locales.Locales.administration.attributes.strings.draganddrop'
                },
                containerScroll: true,
                pluginId: 'gridviewdragdrop'
            }
        ]
    },

    plugins: [
        {
            ptype: 'administration-forminrowwidget',
            pluginId: 'administration-forminrowwidget',
            widget: {
                xtype: 'administration-content-domains-tabitems-properties-properties',
                ui: 'administration-tabandtools',
                layout: 'fit',
                paddingBottom: 10,
                autoScroll: false,
                viewModel: {
                    type: 'administration-content-domains-view'
                }
            }
        }
    ],

    autoEl: {
        'data-testid': 'administration-content-processes-tabitems-domains-grid'
    },

    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },

    tbar: [
        {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.administration.domains.texts.adddomain,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.domains.texts.adddomain'
            },
            ui: 'administration-primary-outline-small',
            reference: 'adddomain',
            itemId: 'adddomain',
            autoEl: {
                'data-testid': 'administration-class-toolbar-addDomainBtn'
            },
            bind: {
                disabled: '{!toolAction._canUpdate}'
            }
        },
        {
            xtype: 'localsearchfield',
            gridItemId: '#processDomainsGrid'
        },
        {
            xtype: 'tbfill'
        },
        {
            xtype: 'tool',
            align: 'right',
            itemId: 'editBtn',
            cls: 'administration-tool',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
            viewModel: {},
            hidden: true, // TODO: remove when supported
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
            },
            callback: 'onEditBtnClick',
            autoEl: {
                'data-testid': 'administration-class-domains-tool-editbtn'
            }
        },
        {
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.includeinherited,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.includeinherited'
            },
            labelAlign: 'left',
            labelStyle: 'width:auto',
            labelWidth: false,
            value: true,
            bind: {
                value: '{includeInherited}'
            },
            listeners: {
                change: 'onIncludeInheritedChange'
            }
        }
    ],

    columns: [
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.name,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
            },
            dataIndex: 'name',
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
            text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.source,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.source'
            },
            dataIndex: 'source',
            align: 'left',
            renderer: function (value, cell, record) {
                if (value) {
                    var storeId = record.get('sourceProcess') ? 'processes.Processes' : 'classes.Classes';
                    var sourceRecord = Ext.getStore(storeId).getById(record.get('source'));
                    return sourceRecord && sourceRecord.get('description');
                }
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination'
            },
            dataIndex: 'destination',
            align: 'left',
            renderer: function (value, cell, record) {
                if (value) {
                    var storeId = record.get('destinationProcess') ? 'processes.Processes' : 'classes.Classes';
                    var sourceRecord = Ext.getStore(storeId).getById(record.get('destination'));
                    return sourceRecord && sourceRecord.get('description');
                }
            }
        },
        {
            text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.descriptiondirect,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.descriptiondirect'
            },
            dataIndex: 'descriptionDirect',
            align: 'left'
        },
        {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.descriptioninverse',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.descriptioninverse'
            },
            dataIndex: 'descriptionInverse',
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cardinality,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cardinality'
            },
            dataIndex: 'cardinality',
            align: 'left'
        },
        {
            xtype: 'checkcolumn',
            text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetailshort,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetailshort'
            },
            hidden: true,
            disabled: true,
            dataIndex: 'isMasterDetail',
            align: 'center'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdetail,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdetail'
            },
            hidden: true,
            dataIndex: 'descriptionMasterDetail',
            align: 'left'
        },
        {
            xtype: 'checkcolumn',
            text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.sourceinline,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.sourceinline'
            },
            hidden: true,
            disabled: true,
            dataIndex: 'sourceInline',
            align: 'center'
        },
        {
            xtype: 'checkcolumn',
            text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destinationinline,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destinationinline'
            },
            hidden: true,
            disabled: true,
            dataIndex: 'destinationInline',
            align: 'center'
        },
        //  {
        //     xtype: 'checkcolumn',
        //     text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.link,
        //     localized: {
        //         text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.link'
        //     },
        //     dataIndex: 'link',
        //     align: 'center',
        //     hidden: true,
        //     bind: {
        //         disabled: '{actions.view}'
        //     }
        // },
        {
            xtype: 'checkcolumn',
            text: CMDBuildUI.locales.Locales.administration.common.labels.active,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
            },
            dataIndex: 'active',
            align: 'center',
            disabled: true
        }
    ]
});
