Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.tabitems.templates.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-importexport-gatetemplates-tabitems-templates-topbar',

    control: {
        '#addgatetemplate': {
            click: 'onNewBtnClick'
        }
    },

    /**
     *
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewBtnClick: function (item, event, eOpts) {
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        var vm = this.getViewModel();
        var gateType = vm.get('gateType');
        switch (gateType) {
            case 'cad':
                container.add({
                    xtype: 'administration-content-importexport-gatetemplates-tabitems-templates-card-card',
                    bind: {
                        theGate: '{theGate}'
                    },
                    viewModel: {
                        links: {
                            theGateTemplate: {
                                type: 'CMDBuildUI.model.importexports.GateTemplate',
                                create: true
                            }
                        },
                        data: {
                            grid: item.up('administration-content-importexport-gatetemplates-view').down('administration-content-importexport-gatetemplates-tabitems-templates-grid'),
                            action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                            actions: {
                                view: false,
                                edit: false,
                                add: true
                            }
                        }
                    }
                });
                break;
            case 'database':
            case 'ifc':
                container.add({
                    xtype: 'administration-content-importexport-datatemplates-card',
                    viewModel: {
                        links: {
                            theGateTemplate: {
                                type: 'CMDBuildUI.model.importexports.Template',
                                create: {
                                    type: 'import',
                                    fileFormat: gateType
                                }
                            }
                        },
                        data: {
                            grid: item.up('administration-content-importexport-gatetemplates-view').down('administration-content-importexport-gatetemplates-tabitems-templates-grid'),
                            action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                            typeHidden: true,
                            fileFormatHidden: true,

                            actions: {
                                view: false,
                                edit: false,
                                add: true
                            }
                        }
                    },
                    listeners: {
                        savesuccess: function (record, requestMethod, eOpts) {
                            var me = this;
                            var _vm = this.lookupViewModel();
                            var grid = _vm.get('grid');
                            var theGate = vm.get('theGate');
                            var handler = theGate.handlers().first();
                            handler.addTemplate(record.get('code'));
                            theGate.save({
                                success: function (gate, gateOperation) {
                                    var eventToCall = requestMethod === 'PUT' ? 'itemupdated' : 'itemcreated';
                                    handler.getTemplates().then(function (templatesStore) {
                                        grid.lookupViewModel().get('allGateTemplates').setData(templatesStore.getRange());
                                        grid.getPlugin('administration-forminrowwidget').view.fireEventArgs(eventToCall, [grid, record, me]);
                                    });
                                }
                            });
                        }
                    }
                });
                break;
            default:
                break;
        }


    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() === event.ENTER) {
            this.onSearchSubmit(field);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.event.Event} event
     */
    onSearchSubmit: function (field, event) {
        var grid = this.getView().up('grid');
        var store = grid.getStore();
        var formInRow = grid.getPlugin('administration-forminrowwidget');
        // removeAllExpanded
        formInRow.removeAllExpanded();
        CMDBuildUI.util.administration.helper.GridHelper.localSearchFilter(store, field.getValue());
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        // clear store filter
        var store = this.getView().up('grid').getStore();
        if (store) {
            CMDBuildUI.util.administration.helper.GridHelper.removeLocalSearchFilter(store);
        }
        // reset input
        field.reset();
    }
});