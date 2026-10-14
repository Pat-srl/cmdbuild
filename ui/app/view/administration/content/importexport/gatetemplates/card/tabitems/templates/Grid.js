Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.tabitems.templates.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.tabitems.templates.GridController',
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.tabitems.templates.GridModel'
    ],
    alias: 'widget.administration-content-importexport-gatetemplates-tabitems-templates-grid',
    controller: 'administration-content-importexport-gatetemplates-tabitems-templates-grid',
    viewModel: {
        type: 'administration-content-importexport-gatetemplates-tabitems-templates-grid'
    },
    bind: {
        store: '{allGateTemplates}',
        selection: '{selected}'
    },

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
                    var record = Ext.Array.findBy(
                        CMDBuildUI.model.importexports.Template.getTemplateTypes(),
                        function (item) {
                            return item.value === value;
                        }
                    );
                    if (record && record.value) {
                        return record.label;
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

            expandOnDblClick: false,
            widget: {
                xtype: 'administration-content-importexport-gatetemplates-tabitems-templates-card-viewinrow',
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
        'data-testid': 'administration-content-importexport-gatetemplates-tabitems-templates-grid'
    },
    dockedItems: [
        {
            xtype: 'administration-content-importexport-gatetemplates-tabitems-templates-topbar',
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
    labelWidth: 'auto',

    listeners: {
        savesuccess: function (record, requestMethod) {
            var me = this;
            var vm = me.lookupViewModel();
            var theGate = vm.get('theGate');
            var handler = theGate.handlers().first();
            handler.addTemplate(record.get('code'));
            theGate.save({
                success: function (gate, gateOperation) {
                    var eventToCall = requestMethod === 'PUT' ? 'itemupdated' : 'itemcreated';
                    handler.getTemplates().then(function (templatesStore) {
                        me.lookupViewModel().get('allGateTemplates').setData(templatesStore.getRange());
                        me.getPlugin('administration-forminrowwidget').view.fireEventArgs(eventToCall, [
                            me,
                            record,
                            me
                        ]);
                    });
                }
            });
        },
        removetemplate: function (record, grid) {
            var me = this;
            var vm = this.lookupViewModel();
            grid = vm.get('grid') || grid;
            var theGate = vm.get('theGate');
            var handler = theGate.handlers().first();
            handler.removeTemplate(record.get('code'));
            record.erase({
                success: function (_record, operation) {
                    theGate.save({
                        success: function (gate, gateOperation) {
                            var eventToCall = 'itemremoved';
                            handler.getTemplates().then(function (templatesStore) {
                                grid.lookupViewModel().get('allGateTemplates').setData(templatesStore.getRange());
                                grid.getPlugin('administration-forminrowwidget').view.fireEventArgs(eventToCall, [
                                    grid,
                                    record,
                                    me
                                ]);
                            });
                        }
                    });
                }
            });
        }
    }
});
