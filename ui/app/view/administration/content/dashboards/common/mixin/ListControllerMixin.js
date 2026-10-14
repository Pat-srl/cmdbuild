Ext.define('CMDBuildUI.view.administration.content.dashboards.common.mixin.ListControllerMixin', {
    extend: 'Ext.Mixin',

    mixinConfig: {
        id: 'listController'
    },

    // ======================================================
    // ON FIELD CHANGE LISTENERS
    // ======================================================

    onSeeAllElementsChange: function (field, newValue, oldValue) {
        if (!field.hasFocus || newValue === oldValue) {
            return;
        }

        const vm = this.getViewModel();
        const theComponent = vm.get('theComponent');

        if (!theComponent) {
            return;
        }

        theComponent.set('listSearchFilter', null);
    },

    // ======================================================
    // FILTERS LOGIC
    // ======================================================

    onEditFilterBtn: function () {
        this.openFilterPopup(this.getViewModel().get('theComponent'), 'dataSourceName', 'dataSourceFilter');
    },

    onRemoveFilterBtn: function (button, e, eOpts) {
        button.lookupViewModel().get('theComponent').set('dataSourceFilter', '');
    },

    privates: {
        openFilterPopup: function (record, targetField, filterField) {
            const me = this;
            const vm = me.getViewModel();
            const actions = vm.get('actions');
            let recordFilter =
                record.get(filterField) && record.get(filterField).length ? JSON.parse(record.get(filterField)) : {};

            let popuTitle = actions.view
                ? CMDBuildUI.locales.Locales.administration.dashboards.viewfilters
                : CMDBuildUI.locales.Locales.administration.dashboards.editfilters;

            const type = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(record.get(targetField));

            popuTitle = Ext.String.format(popuTitle, type, record.get(targetField));

            const filter = Ext.create('CMDBuildUI.model.base.Filter', {
                name: CMDBuildUI.locales.Locales.filters.newfilter,
                description: CMDBuildUI.locales.Locales.filters.newfilter,
                ownerType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                target: record.get(targetField),
                configuration: recordFilter,
                shared: true
            });

            const viewmodel = {
                data: {
                    objectType: type,
                    objectTypeName: record.get(targetField),
                    theFilter: filter,
                    actions: Ext.copy(actions)
                }
            };
            const attrbutePanel = this.getAttributesFilterTab(viewmodel, record);
            const functionPanel = this.getFunctionFilterTab(viewmodel, record);
            const relationsPanel = this.getRelationsFilterTab(viewmodel, record);
            const attachmentsPanel = this.getDmsFilterTab(viewmodel);

            const listeners = {
                applyfilter: function (panel, filter, eOpts) {
                    me.onApplyFilter(filter);
                    me.popup.close();
                },
                saveandapplyfilter: function (panel, filter, eOpts) {
                    me.onSaveAndApplyFilter(filter);
                    me.popup.close();
                },
                popupclose: function (eOpts) {
                    me.popup.close();
                }
            };

            const dockedItems = [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    hidden: me.getViewModel().get('actions.view'),
                    items: CMDBuildUI.util.administration.helper.FormHelper.getOkCloseButtons(
                        {
                            handler: function (_button) {
                                CMDBuildUI.util.administration.helper.FilterHelper.setRecordFilterFromPanel(
                                    me.popup,
                                    record,
                                    filterField
                                );
                            }
                        },
                        {
                            handler: function () {
                                me.popup.close();
                            }
                        }
                    )
                }
            ];

            const content = {
                xtype: 'tabpanel',
                cls: 'administration',
                ui: 'administration-tabandtools',
                items: [attrbutePanel, relationsPanel, functionPanel],
                dockedItems: dockedItems,
                listeners: listeners
            };

            if (attachmentsPanel) {
                content.items.push(attachmentsPanel);
            }

            me.popup = CMDBuildUI.util.Utilities.openPopup(
                'filterpopup',
                popuTitle,
                content,
                {},
                {
                    ui: 'administration-actionpanel',
                    listeners: {
                        afterrender: function () {
                            const _vm = this.down(
                                'administration-components-filterpanels-relationfilters-panel'
                            ).getViewModel();
                            _vm.populateRelationStore({
                                filter: _vm.get('theFilter'),
                                type: _vm.get('objectType'),
                                name: _vm.get('objectTypeName')
                            });
                        }
                    },
                    viewModel: {
                        data: {
                            index: '0',
                            grid: {},
                            record: record,
                            canedit: true
                        }
                    }
                }
            );
        },

        getAttributesFilterTab: function (viewmodel, record) {
            return {
                xtype: 'administration-components-filterpanels-attributes-panel',
                title: CMDBuildUI.locales.Locales.administration.attributes.attributes,
                allowInputParameter: true,
                allowCurrentUser: true,
                allowCurrentGroup: true,
                viewModel: viewmodel
            };
        },

        getFunctionFilterTab: function (viewmodel, record) {
            return {
                xtype: 'administration-components-filterpanels-functionfilters-panel',
                title: CMDBuildUI.locales.Locales.administration.common.labels.funktion,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.common.labels.funktion'
                },
                viewModel: viewmodel
            };
        },

        getRelationsFilterTab: function (viewmodel, record) {
            return {
                xtype: 'administration-components-filterpanels-relationfilters-panel',
                title: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations,
                viewModel: viewmodel
            };
        },

        getDmsFilterTab: function (viewmodel) {
            viewmodel.data.displayOnly = viewmodel.data.actions.view;
            if (CMDBuildUI.util.helper.Configurations.getEnabledFeatures().dms) {
                return {
                    xtype: 'filters-attachments-panel',
                    reference: 'attachmentspanel',
                    viewModel: viewmodel
                };
            }
        }
    }
});
