Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    mixins: ['CMDBuildUI.view.administration.content.importexport.datatemplates.card.CardMixin'],
    requires: ['CMDBuildUI.util.administration.helper.ConfigHelper'],
    alias: 'controller.administration-content-importexport-datatemplates-card-viewinrow',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onViewBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#editExportFilterBtn': {
            click: 'onEditExportFilterBtn'
        },
        '#editImportFilterBtn': {
            click: 'onEditImportFilterBtn'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.importexport.datatemplates.card.ViewInRow} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this;
        var vm = me.getViewModel();
        vm.bind({
            bindTo: '{allSelectedAttributesStore}'
        }, function (store) {
            me.onAllSelectedAttributesStoreDatachanged(store);
        });
        var selected = view._rowContext.record;
        var type = selected.get('type');
        Ext.asap(function () {
            try {
                view.mask(CMDBuildUI.locales.Locales.administration.common.messages.loading);
            } catch (error) {
                CMDBuildUI.util.Logger.log("unable to mask lookup value forminrow", CMDBuildUI.util.Logger.levels.debug);
            }
        });

        vm.linkTo('theGateTemplate', {
            type: 'CMDBuildUI.model.importexports.Template',
            id: selected.get('_id')
        });
        vm.bind({
            bindTo: {
                theGateTemplate: '{theGateTemplate}'
            }
        }, function (data) {
            if (data.theGateTemplate) {
                view.add(CMDBuildUI.view.administration.content.importexport.datatemplates.card.helpers.FieldsetsHelper.getGeneralPropertiesFieldset());
                view.add(CMDBuildUI.view.administration.content.importexport.datatemplates.card.helpers.FieldsetsHelper.getAttributesFieldset());

                if (type === CMDBuildUI.model.importexports.Template.types.import || type === CMDBuildUI.model.importexports.Template.types.importexport) {
                    view.add(CMDBuildUI.view.administration.content.importexport.datatemplates.card.helpers.FieldsetsHelper.getImportFilterFieldset());
                    view.add(CMDBuildUI.view.administration.content.importexport.datatemplates.card.helpers.FieldsetsHelper.getImportCriteriaFieldset());

                }

                if (data.theGateTemplate.targetType === CMDBuildUI.model.administration.MenuItem.types.klass &&
                    (type === CMDBuildUI.model.importexports.Template.types.export ||
                        type === CMDBuildUI.model.importexports.Template.types.importexport)) {
                    view.add(CMDBuildUI.view.administration.content.importexport.datatemplates.card.helpers.FieldsetsHelper.getExportCriteriaFieldset());
                }
                if (data.theGateTemplate.get('fileFormat') !== 'ifc' && data.theGateTemplate.get('fileFormat') !== 'database') {
                    view.add(CMDBuildUI.view.administration.content.importexport.datatemplates.card.helpers.FieldsetsHelper.getErrorsManagementFieldset());
                }
                Ext.Array.forEach(view.down('#importExportAttributeGrid').getColumns(), function (column) {
                    if (column.xtype === 'actioncolumn') {
                        column.destroy();
                    }
                });
                Ext.asap(function () {
                    try {
                        view.setActiveTab(0);
                        view.unmask();
                    } catch (error) {
                        CMDBuildUI.util.Logger.log("unable to unmask view in row", CMDBuildUI.util.Logger.levels.debug);
                    }
                });
            }
        });


    },

    onAfterRender: function (view) {
        var vm = this.getViewModel();
        var selected = view._rowContext.record;

        vm.bind({
            bindTo: {
                theGateTemplate: '{theGateTemplate}'
            }
        }, function (data) {
            if (data.theGateTemplate) {
                Ext.asap(function () {
                    try {
                        view.unmask();
                    } catch (error) {
                        CMDBuildUI.util.Logger.log("unable to unmask view in row", CMDBuildUI.util.Logger.levels.debug);
                    }
                });
                view.setActiveTab(0);
            }
        });
    },

    onImportExportTemplateUpdate: function (v, record) {
        var vm = this.getViewModel();
        var view = this.getView();
        this.linkImportExportTemplate(view, vm);
    },

    linkImportExportTemplate: function (view, vm) {
        var grid = view.up(),
            record = grid.getSelection()[0];
        vm.set("theGateTemplate", record);
    }
});