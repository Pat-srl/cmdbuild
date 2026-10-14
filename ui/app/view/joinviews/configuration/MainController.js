Ext.define('CMDBuildUI.view.joinviews.configuration.MainController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.joinviews-configuration-main',

    control: {
        '#': {
            attributegruopchanged: 'onAttributeGroupsChanged',
            attributegruopremoved: 'onAttributeGroupsRemoved',
            classchange: 'onClassChange',
            classaliaschange: 'onClassAliasChange',
            domainchange: 'onDomainChange',
            domaincheckchange: 'onDomainCheckChange',
            validitychange: 'onValidityChange'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggle'
        },
        '#disableBtn': {
            click: 'onActiveToggle'
        },
        '#prevBtn': {
            click: 'onPrevBtnClick'
        },
        '#nextBtn': {
            click: 'onNextBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @event
     * @param {Ext.form.Panel} form
     * @param {Boolean} valid
     * @param {Object} eOpts
     */
    onValidityChange: function (form, valid, eOpts) {
        const vm = this.getViewModel();
        if (vm.get('currentStep') !== 5) {
            Ext.asap(function () {
                if (form && !form.destroyed) {
                    vm.set('stepNavigationLocked', !valid);
                }
            });
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, event, eOpts) {
        button.lookupViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, event, eOpts) {
        this.getView().fireEventArgs('deletejoinview', [button]);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onPrevBtnClick: function (button, event, eOpts) {
        const view = this.getView();
        const vm = button.lookupViewModel();
        const activeView = view.items.getAt(vm.get('currentStep'));

        if (!activeView.goingPreviousStep || (activeView.goingPreviousStep && activeView.goingPreviousStep())) {
            vm.set('currentStepWas', vm.get('currentStep'));
            vm.set('currentStep', vm.get('currentStep') - 1);
        } else {
            // TODO show message or something
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onNextBtnClick: function (button, event, eOpts) {
        const view = this.getView();
        const vm = button.lookupViewModel();
        const activeView = view.items.getAt(vm.get('currentStep'));

        if (!activeView.goingNextStep || (activeView.goingNextStep && activeView.goingNextStep())) {
            vm.set('currentStepWas', vm.get('currentStep'));
            vm.set('currentStep', vm.get('currentStep') + 1);
        } else {
            // TODO show message or something
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onActiveToggle: function (button, event, eOpts) {
        const vm = this.getViewModel();
        vm.set('theView.active', !vm.get('theView.active'));
        this.onSaveBtnClick(button);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, event, eOpts) {
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        button.setDisabled(true);
        CMDBuildUI.util.Utilities.showLoader(true);

        const me = this;
        const view = me.getView();
        const vm = view.lookupViewModel();
        const theView = vm.get('theView');
        const joinData = view.getJoinData();
        const attributes = view.getAttributesData();
        const attributeGroups = view.getAttributesGroups();
        const sorter = view.getSorter();
        const filter = view.getFilterData();
        const contextMenuItems = [];

        theView.contextMenuItems().each(function (record, index) {
            const data = record.getData();
            delete data.id;
            delete data._id;
            contextMenuItems.push(data);
        });
        theView.set('join', joinData);
        theView.set('attributes', attributes);
        theView.set('attributeGroups', attributeGroups);
        theView.set('sorter', sorter);
        theView.set('filter', filter);
        theView.set('contextMenuItems', contextMenuItems);
        CMDBuildUI.util.Ajax.setActionId(Ext.String.format('save-{0}-joinview', vm.get('action')));
        theView.save({
            success: function (record, operation) {
                me.saveLocales(Ext.copy(vm), record);
                view.fireEventArgs('saved', [vm.get('action'), record, operation]);
            },
            failure: function (record, operation) {
                if (button && !button.destroyed) {
                    button.setDisabled(false);
                }
            },
            callback: function (record, operation, success) {
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                CMDBuildUI.util.Utilities.showLoader(false);
            }
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, event, eOpts) {
        const me = this;
        const view = me.getView();
        const vm = view.lookupViewModel();
        view.fireEventArgs('cancel', [vm.get('action'), vm.get('theView')]);
    },

    /**
     * @event
     * @param {CMDBuildUI.view.fields.allelementscombo.AllelementsCombo} input
     * @param {String} newClassName
     * @param {String} oldClassName
     */
    onClassChange: function (input, newClassName, oldClassName) {
        const vm = input.lookupViewModel();
        const allAttributesStore = vm.get('allAttributesStore');
        const klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(newClassName);

        if (oldClassName) {
            vm.get('theView').attributes().removeAll();
            allAttributesStore.removeAll();
        }

        if (input.lookupViewModel().get('actions.add')) {
            if (!oldClassName || !vm.get('theView.masterClassAlias')) {
                vm.set('theView.masterClassAlias', newClassName);
            }
        }

        if (klass) {
            const multitenantMode = klass.get('multitenantMode');
            const addFieldMultitenant =
                CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.enabled) &&
                (multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.always ||
                    multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.mixed);

            klass.getAttributes().then(function (classAttributesStore) {
                allAttributesStore.beginUpdate();
                const masterClassAlias = vm.get('theView.masterClassAlias');
                classAttributesStore.each(function (attribute) {
                    const expr = Ext.String.format('{0}.{1}', masterClassAlias, attribute.get('name'));
                    const storeAlreadyContain = allAttributesStore.findRecord('expr', expr, 0, false, true);
                    const tenantAttribute = addFieldMultitenant && attribute.get('name') == 'IdTenant';
                    const attributeDescription = tenantAttribute
                        ? CMDBuildUI.util.Utilities.getTenantLabel()
                        : attribute.getTranslatedDescription();

                    const cmdbuildtype = tenantAttribute
                        ? CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.tenant
                        : attribute.get('type');
                    if (
                        !storeAlreadyContain &&
                        (tenantAttribute ||
                            (attribute.canAdminShow() &&
                                attribute.get('active') &&
                                cmdbuildtype !== CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula))
                    ) {
                        allAttributesStore.addSorted(
                            CMDBuildUI.model.views.JoinViewAttribute.create({
                                _deepIndex: String.fromCharCode(36) + '_' + masterClassAlias,
                                targetAlias: masterClassAlias,
                                targetType: vm.get('theView.masterClass'),
                                expr: expr,
                                name: '',
                                description: '',
                                group: '',
                                showInGrid: false,
                                showInReducedGrid: false,
                                _attributeDescription: attributeDescription,
                                attributeconf: attribute.getData(),
                                cmdbuildtype: cmdbuildtype
                            })
                        );
                    } else if (storeAlreadyContain) {
                        storeAlreadyContain.set(
                            '_deepIndex',
                            String.fromCharCode(36) + '_' + storeAlreadyContain.get('targetAlias')
                        );
                        storeAlreadyContain.set('_attributeDescription', attributeDescription);
                        storeAlreadyContain.set('attributeconf', attribute.getData());
                        storeAlreadyContain.set('cmdbuildtype', cmdbuildtype);
                    }
                });

                const isLeafClass =
                    klass.get('prototype') === false &&
                    Ext.getStore('classes.Classes').findRecord('parent', newClassName, 0, false, true) === null;

                if (!isLeafClass) {
                    const expr = Ext.String.format('{0}.IdClass', masterClassAlias);
                    const storeAlreadyContain = allAttributesStore.findRecord('expr', expr, 0, false, true);
                    const attrconf = {
                        name: 'Subtype',
                        type: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                        description: CMDBuildUI.locales.Locales.common.grid.subtype,
                        hideInFilter: true,
                        active: true,
                        unique: false,
                        mandatory: false,
                        group: ''
                    };

                    if (!storeAlreadyContain) {
                        allAttributesStore.addSorted(
                            CMDBuildUI.model.views.JoinViewAttribute.create({
                                _deepIndex: String.fromCharCode(36) + '_' + masterClassAlias,
                                targetAlias: masterClassAlias,
                                targetType: newClassName,
                                expr: expr,
                                name: 'Subtype',
                                description: '',
                                group: '',
                                showInGrid: false,
                                showInReducedGrid: false,
                                _attributeDescription: CMDBuildUI.locales.Locales.common.grid.subtype,
                                attributeconf: attrconf,
                                cmdbuildtype: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string
                            })
                        );
                    } else {
                        storeAlreadyContain.set('_deepIndex', String.fromCharCode(36) + '_' + masterClassAlias);
                        storeAlreadyContain.set(
                            '_attributeDescription',
                            CMDBuildUI.locales.Locales.common.grid.subtype
                        );
                        storeAlreadyContain.set('attributeconf', attrconf);
                        storeAlreadyContain.set(
                            'cmdbuildtype',
                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string
                        );
                    }
                }

                allAttributesStore.endUpdate();
            });
        }
    },

    /**
     * @event
     * @param {Ext.form.field.Text} input
     * @param {String} newValue
     * @param {String} oldValue
     */
    onClassAliasChange: function (input, newValue, oldValue) {
        this.fireEventToAllItems('classaliaschange', [input, newValue, oldValue]);
    },

    /**
     * @event
     * @param {Ext.data.Model} node
     * @param {Object} context
     */
    onDomainChange: function (node, context) {
        this.fireEventToAllItems('domainchange', [node, context]);
    },

    /**
     * @event
     * @param {Ext.data.Model} node
     * @param {Object} context
     */
    onDomainCheckChange: function (node, context) {
        this.fireEventToAllItems('domaincheckchange', [node, context]);
    },

    /**
     * @event
     * @param {Ext.data.Model} attributeGroup
     */
    onAttributeGroupsChanged: function (attributeGroup) {
        this.fireEventToAllItems('attributegruopchanged', [attributeGroup]);
    },

    /**
     * @event
     * @param {Ext.data.Model} attributeGroup
     */
    onAttributeGroupsRemoved: function (attributeGroup) {
        this.fireEventToAllItems('attributegruopremoved', [attributeGroup]);
    },

    /**
     *
     */
    allAttributesStoreOnEndUpdate: function () {
        const me = this.getView();
        const store = me.getViewModel().get('allAttributesStore');
        if (me.lastAppendedAttributeTimeout) {
            me.lastAppendedAttributeTimeout.cancel();
        }
        me.lastAppendedAttributeTimeout = new Ext.util.DelayedTask(function () {
            if (store.getData() && store.getGroupField()) {
                store.getData().setGrouper(store.getGroupField());
            }
        });
        me.lastAppendedAttributeTimeout.delay(250);
    },

    privates: {
        lastAppendedAttributeTimeout: null,

        /**
         *
         * @param {String} event
         * @param {Array} parameters
         */
        fireEventToAllItems: function (event, parameters) {
            const me = this;
            me.getView().items.each(function (item) {
                if (item.down('#attributesfilterpanel')) {
                    item = item.down('#attributesfilterpanel');
                }
                item.fireEventArgs(event, parameters);
            });
        },

        /**
         *
         * @param {*} vm
         * @param {*} record
         */
        saveLocales: function (vm, record) {
            const descriptionTranslation = vm.get('theDescriptionTranslation');
            if (descriptionTranslation) {
                descriptionTranslation.phantom = false;
                descriptionTranslation.crudState = 'U';
                descriptionTranslation.crudStateWas = 'U';
                delete descriptionTranslation.data._id;
                descriptionTranslation.save();
            }

            record.attributeGroups().each(function (attributeGroup) {
                const translation = vm.get(
                    Ext.String.format(
                        'theGroupingDescriptionTranslation_{0}',
                        CMDBuildUI.util.Utilities.stringToHex(attributeGroup.get('name'))
                    )
                );
                if (translation) {
                    translation.phantom = false;
                    translation.crudState = 'U';
                    translation.crudStateWas = 'U';
                    delete translation.data._id;
                    translation.save();
                }
            });

            record.attributes().each(function (attribute) {
                const translation = vm.get(
                    Ext.String.format('theAttributeDescriptionTranslation_{0}', attribute.get('name'))
                );
                if (translation) {
                    translation.phantom = false;
                    translation.crudState = 'U';
                    translation.crudStateWas = 'U';
                    delete translation.data._id;
                    translation.save();
                }
            });

            record.contextMenuItems().each(function (contextMenu) {
                const deferred = new Ext.Deferred();
                const key = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfClassContextMenuItem(
                    vm.get('theView').get('name'),
                    contextMenu.get('label')
                );
                // save the translation
                const vmObject = vm.get(
                    'theContextMenuTranslation_' + CMDBuildUI.util.Utilities.stringToHex(contextMenu.get('label'))
                );
                if (vmObject) {
                    CMDBuildUI.util.Ajax.setActionId('joinview.contextmenu_translation');
                    vmObject.crudState = 'U';
                    vmObject.crudStateWas = 'U';
                    vmObject.phantom = false;
                    vmObject.set('_id', key);
                    vmObject.save({
                        success: function (translations, operation) {
                            deferred.resolve();
                        }
                    });
                }
                return deferred;
            });
        }
    }
});
