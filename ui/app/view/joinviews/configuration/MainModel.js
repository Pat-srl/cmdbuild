Ext.define('CMDBuildUI.view.joinviews.configuration.MainModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.joinviews-configuration-main',

    data: {
        theView: null,
        uiContext: 'administration',
        showForm: 'true',
        currentStep: 0,
        currentStepWas: 0,
        stepNavigationLocked: true,
        totalStep: 7,
        primaryButtonUi: null,
        secondaryButtonUi: null,
        fieldsetUi: null,
        isPrevDisabled: true,
        isNextDisabled: true,
        actions: {
            view: false,
            edit: false,
            add: true,
            empty: false
        },
        toolAction: {
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        },
        selectedAttributes: []
    },

    formulas: {
        toolsManager: {
            bind: '{theSession.rolePrivileges.admin_views_modify}',
            get: function (canModify) {
                this.set('toolAction._canUpdate', canModify);
                this.set('toolAction._canDelete', canModify);
                this.set('toolAction._canActiveToggle', canModify);
            }
        },

        privilegeModes: function () {
            return [
                {
                    value: CMDBuildUI.model.views.ConfigurableView.userpemissions.default,
                    label: CMDBuildUI.locales.Locales.administration.common.labels.default
                },
                {
                    value: CMDBuildUI.model.views.ConfigurableView.userpemissions.restrict,
                    label: CMDBuildUI.locales.Locales.administration.views.applyuserpermission
                },
                {
                    value: CMDBuildUI.model.views.ConfigurableView.userpemissions.ignore,
                    label: CMDBuildUI.locales.Locales.administration.views.ignoreuserpermision
                }
            ];
        },

        action: {
            bind: {
                theView: '{theView}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}',
                isView: '{actions.view}'
            },
            get: function (data) {
                const configurationView = this.getView().up('joinviews-configuration-configuration');
                if (data.isEdit) {
                    this.set('formModeCls', 'formmode-add');
                    if (configurationView) {
                        const vm = configurationView.getViewModel();
                        vm.set('disabledTabs.properties', false);
                        vm.set('disabledTabs.fieldsmanagement', true);
                        vm.set('disabledTabs.permissions', true);
                    }
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.isAdd) {
                    this.set('formModeCls', 'formmode-add');
                    if (configurationView) {
                        const vm = configurationView.getViewModel();
                        vm.set('disabledTabs.properties', false);
                        vm.set('disabledTabs.fieldsmanagement', true);
                        vm.set('disabledTabs.permissions', true);
                    }
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    this.set('formModeCls', 'formmode-view');
                    if (configurationView) {
                        const vm = configurationView.getViewModel();
                        vm.set('disabledTabs.properties', false);
                        vm.set('disabledTabs.fieldsmanagement', false);
                        vm.set('disabledTabs.permissions', false);
                    }
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
                this.set('actions.empty', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.empty);
            }
        },

        panelTitleManager: {
            bind: {
                description: '{theView.description}',
                currentStep: '{currentStep}',
                totalStep: '{totalStep}'
            },
            get: function (data) {
                const me = this;
                const mainTitle = this.get('isAdministrationModule')
                    ? CMDBuildUI.locales.Locales.administration.navigation.views +
                      ' - ' +
                      CMDBuildUI.locales.Locales.administration.searchfilters.texts.fromjoin
                    : this.get('theView').phantom
                    ? CMDBuildUI.locales.Locales.joinviews.newjoinview
                    : CMDBuildUI.locales.Locales.joinviews.joinview;
                if (data && !me.get('actions.empty')) {
                    const title = Ext.String.format(
                        '{0} {1} {2} - {3} {4} {5} {6} {7}',
                        mainTitle,
                        data.description ? '-' : '',
                        data.description,
                        CMDBuildUI.locales.Locales.administration.tasks.step,
                        data.currentStep + 1,
                        CMDBuildUI.locales.Locales.administration.tasks.of,
                        data.totalStep
                    );
                    me.getParent().set('panelTitle', title);
                } else {
                    me.getParent().set('panelTitle', Ext.String.capitalize(mainTitle));
                }
            }
        },

        uiManager: {
            bind: '{uiContext}',
            get: function (uiContext) {
                this.set(
                    'primaryButtonUi',
                    uiContext === 'administration' ? 'administration-action-small' : 'management-primary-small'
                );
                this.set(
                    'secondaryButtonUi',
                    uiContext === 'administration' ? 'administration-secondary-action-small' : 'secondary-action-small'
                );
                this.set(
                    'fieldsetUi',
                    uiContext === 'administration' ? 'administration-formpagination' : 'formpagination'
                );
            }
        },

        stepManager: {
            bind: {
                stepNavigationLocked: '{stepNavigationLocked}',
                currentStep: '{currentStep}',
                totalStep: '{totalStep}'
            },
            get: function (data) {
                if (data.stepNavigationLocked) {
                    this.set('isPrevDisabled', true);
                    this.set('isNextDisabled', true);
                } else {
                    this.set('isPrevDisabled', data.currentStep === 0);
                    this.set('isNextDisabled', data.currentStep >= this.get('totalStep') - 1);
                }
            }
        },

        attributesStoreManager: {
            bind: '{theView}',
            get: function (theView) {
                if (theView) {
                    const attributesGroupsStore = theView.attributeGroups();
                    const selectedAttributes = theView.attributes().getRange();
                    const gridStore = this.get('allAttributesStore');
                    this.set('attributeGroupsStore', attributesGroupsStore);
                    gridStore.beginUpdate();
                    Ext.Array.forEach(selectedAttributes, function (item) {
                        if (!gridStore.findRecord('expr', item.get('expr'), 0, false, true)) {
                            gridStore.addSorted(item);
                        }
                    });
                    gridStore.endUpdate();
                    this.set('selectedAttributes', selectedAttributes);
                }
            }
        }
    },

    stores: {
        allAttributesStore: {
            fields: [
                '_deepIndex',
                '_attributeDescription',
                'name',
                'description',
                'group',
                'showInGrid',
                'showInReducedGrid'
            ],
            groupField: '_deepIndex',
            sorters: [
                {
                    // Sort by first letter of second word of spirit animal, in descending order
                    sorterFn: function (record1, record2) {
                        let desc1, desc2;
                        if (record1.data._attributeDescription) {
                            desc1 = record1.data._attributeDescription.toUpperCase();
                        } else {
                            desc1 = record1.data.name.toUpperCase();
                        }
                        if (record2.data._attributeDescription) {
                            desc2 = record2.data._attributeDescription.toUpperCase();
                        } else {
                            desc2 = record2.data.name.toUpperCase();
                        }

                        return desc1 > desc2 ? 1 : desc1 === desc2 ? 0 : -1;
                    },
                    direction: 'ASC'
                }
            ],
            listeners: {
                endupdate: 'allAttributesStoreOnEndUpdate'
            }
        },

        attributesSelectedStore: {
            proxy: 'memory'
        },

        // placeholder store needed
        attributeGroupsStore: {},

        privilegeModesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{privilegeModes}'
        }
    }
});
