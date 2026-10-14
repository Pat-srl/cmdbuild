Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.CardMixin', {

    mixinId: 'administration-importexportgatemixin',

    onEditBtnClick: function () {
        var view = this.getView();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var vm = view.getViewModel();
        var viewModel = {
            links: {
                theGateTemplate: {
                    type: 'CMDBuildUI.model.importexports.GateTemplate',
                    id: view.getViewModel().get('selected._id') || view.getViewModel().get('theGateTemplate._id')
                }
            },
            data: {
                gateType: vm.get('gateType'),
                grid: vm.get('grid') || this.getView().up().grid,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                actions: {
                    edit: true,
                    view: false,
                    add: false
                }
            }
        };

        container.removeAll();
        container.add({
            xtype: 'administration-content-importexport-gatetemplates-tabitems-templates-card-card',
            viewModel: viewModel
        });
    },

    onDeleteBtnClick: function (button) {
        var me = this;
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    var theGateTemplate = me.getViewModel().get('theGateTemplate');
                    var grid = CMDBuildUI.util.Navigation.getMainAdministrationContainer().down('administration-content-importexport-gatetemplates-tabitems-templates-grid');
                    grid.fireEventArgs('removetemplate', [theGateTemplate, grid]);
                    CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                }
            }, this);
    },


    onViewBtnClick: function () {
        var view = this.getView();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var vm = view.getViewModel();
        var viewModel = {
            data: {
                gateType: vm.get('gateType'),
                theGate: vm.get('theGate'),
                theGateTemplate: vm.get('selected') || vm.get('theGateTemplate'),
                grid: vm.get('grid') || this.getView().up().grid,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                actions: {
                    edit: false,
                    view: true,
                    add: false
                }
            }
        };

        container.removeAll();
        container.add({
            xtype: 'administration-content-importexport-gatetemplates-tabitems-templates-card-card',
            viewModel: viewModel
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onActiveToggleBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var theGateTemplate = vm.get('theGateTemplate');
        theGateTemplate.set('active', !theGateTemplate.get('active'));
        theGateTemplate.save({
            success: function (record, operation) {
                view.up('administration-content-importexport-gatetemplates-tabitems-templates-grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [view.up('administration-content-importexport-gatetemplates-tabitems-templates-grid'), record, this]);
            }
        });

    },

    onCloneBtnClick: function () {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var newImportExportTemplate = vm.get('theGateTemplate').copyForClone();
        var viewModel = {
            data: {
                theGate: vm.get('theGate'),
                theGateTemplate: newImportExportTemplate,
                grid: vm.get('grid') || this.getView().up().grid,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                actions: {
                    edit: false,
                    view: false,
                    add: true
                }
            }
        };

        var gateType = vm.get('gateType');
        var xtype, listeners;
        switch (gateType) {
            case 'cad':
                xtype = 'administration-content-importexport-gatetemplates-tabitems-templates-card-card';
                break;
            case 'database':
            case 'ifc':
                xtype = 'administration-content-importexport-datatemplates-card';
                listeners = {
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
                break;
            default:
                break;
        }
        container.removeAll();
        container.add({
            xtype: xtype,
            viewModel: viewModel,
            listeners: listeners
        });
    },

    /**
     *
     * @param {*} row
     * @param {*} record
     * @param {*} element
     * @param {*} rowIndex
     * @param {*} e
     * @param {*} eOpts
     */
    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {
        var view = this.getView(),
            container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);

        var formInRow = view.ownerGrid.getPlugin('administration-forminrowwidget');
        formInRow.removeAllExpanded(record);
        view.setSelection(record);
        var xtype = 'administration-content-importexport-gatetemplates-tabitems-templates-card-card';
        if (record.get('fileFormat') === 'ifc' || record.get('fileFormat') === 'database') {
            xtype = 'administration-content-importexport-datatemplates-card';
        }
        container.removeAll();
        container.add({
            xtype: xtype,
            viewModel: {
                links: {
                    theGateTemplate: {
                        type: 'CMDBuildUI.model.importexports.GateTemplate',
                        id: record.get('_id')
                    }
                },
                data: {
                    grid: view.ownerGrid,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    }
                }
            }
        });
    },
    privates: {

    }
});