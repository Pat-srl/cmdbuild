Ext.define('CMDBuildUI.view.joinviews.configuration.items.AttributesChoiceController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.joinviews-configuration-items-attributeschoice',

    control: {
        '#': {
            classaliaschange: 'onClassAliasChange',
            domainchange: 'onDomainChange',
            domaincheckchange: 'onDomainCheckChange'
        },
        '#attributegrid': {
            beforedeselect: 'onBeforeDeselect',
            deselect: 'onDeselect',
            beforeselect: 'onBeforeSelect',
            select: 'onSelect'
        },
        '#checkedonly': {
            toggle: 'onToggleButton'
        }
    },

    /**
     * @event
     * @param {Ext.form.field.Text} input
     * @param {String} newValue
     * @param {String} oldValue
     */
    onClassAliasChange: function (input, newValue, oldValue) {
        const grid = this.getView().down('#attributegrid');
        const store = grid.getStore();
        // set new targetAlias and update expr
        store.each(function (item) {
            if (item.get('targetAlias') === oldValue) {
                item.set('targetAlias', newValue);
                item.set('expr', item.get('expr').replace(oldValue, newValue));
            }
        });
    },

    /**
     * @event
     * @param {Ext.data.Model} node
     * @param {Object} context
     * @param {Object} eOpts
     */
    onDomainChange: function (record, context, eOpts) {
        const grid = this.getView().down('#attributegrid');
        const store = grid.getStore();
        const mainView = grid.up('joinviews-configuration-main');

        switch (context.field) {
            case 'targetAlias':
                // set new targetAlias and update expr
                store.each(function (item) {
                    if (item.get('targetAlias') === record.getPrevious('targetAlias')) {
                        item.set('targetAlias', record.get('targetAlias'));
                        item.set(
                            'expr',
                            item.get('expr').replace(record.getPrevious('targetAlias'), record.get('targetAlias'))
                        );
                    }
                });
                break;

            case 'targetType':
                // get all record of targetType class
                const recordsStoreToRemove = store.queryBy(function (item) {
                    if (item.get('targetAlias') === record.getPrevious('targetAlias')) {
                        // remove stored alias of attribute
                        mainView.clearAliasIndex(mainView.aliasType.attribute, record.get('name'));
                        return true;
                    }
                    return false;
                });
                // remove all attributes of targetType class
                store.remove(recordsStoreToRemove.getRange());
                recordsStoreToRemove.destroy();
                this.addTargetAttributes(record, store);
                break;

            default:
                break;
        }

        grid.getView().refresh();
    },

    /**
     *
     * @param {*} node
     * @param {*} ctx
     */
    onDomainCheckChange: function (node, ctx) {
        const vm = this.getViewModel();
        const store = vm.get('allAttributesStore');

        if (node.get('checked')) {
            // add attributes of targetClass to grid
            this.addTargetAttributes(node, store);
        } else {
            const recordsStoreToRemove = store.queryBy(function (item) {
                return item.get('targetAlias') === node.getPrevious('targetAlias');
            });
            store.remove(recordsStoreToRemove.getRange());
            recordsStoreToRemove.destroy();
        }
    },

    /**
     *
     * @param {*} grid
     * @param {*} record
     * @returns
     */
    onBeforeDeselect: function (grid, record) {
        return this.getViewModel().get('actions.view') ? false : true;
    },

    /**
     *
     * @param {*} grid
     * @param {*} record
     * @param {*} rowIndex
     * @param {*} eOpts
     */
    onDeselect: function (grid, record, rowIndex, eOpts) {
        const view = this.getView();
        const mainView = view.up('joinviews-configuration-main');
        const vm = mainView.lookupViewModel();

        vm.get('theView').attributes().remove(record);
        vm.get('attributesSelectedStore').remove(record);
        mainView.clearAliasIndex(mainView.aliasType.attribute, record.get('name'));
        record.set('description', '');
        record.set('name', '');
        record.set('showInGrid', false);
        record.set('showInReducedGrid', false);
        record.set('group', '');

        Ext.asap(function () {
            const gridSelection = grid.getSelection();
            vm.set('selectedAttributes', gridSelection);
            if (gridSelection.length === 0) {
                view.down('#warningattribute').show();
            }
        });
    },

    /**
     *
     * @param {*} grid
     * @param {*} record
     * @returns
     */
    onBeforeSelect: function (grid, record) {
        const vm = this.getViewModel();

        return vm.get('actions.view') &&
            !vm.get('theView.attributes').findRecord('expr', record.get('expr'), 0, false, true)
            ? false
            : true;
    },

    /**
     *
     * @param {*} grid
     * @param {*} record
     * @param {*} rowIndex
     * @param {*} eOpts
     */
    onSelect: function (grid, record, rowIndex, eOpts) {
        const view = this.getView();
        const mainView = view.up('joinviews-configuration-main');
        const vm = mainView.lookupViewModel();
        const isAlredyInSelection = Ext.Array.findBy(vm.get('selectedAttributes'), function (selected) {
            return selected.get('expr') === record.get('expr');
        });
        let attributeName = record.get('expr').split('.')[1];

        if (isAlredyInSelection) {
            mainView.addAliasFromExisisting(mainView.aliasType.attribute, record.get('name'));
        } else {
            if (attributeName == 'IdTenant') {
                attributeName = '_tenant';
            }
            if (attributeName == 'IdClass') {
                attributeName = 'Subtype';
            }
            const nameAliasIndex = mainView.getNewAliasIndex(mainView.aliasType.attribute, attributeName);
            record.set(
                'name',
                Ext.String.format(
                    '{0}{1}',
                    attributeName,
                    nameAliasIndex ? Ext.String.format('_{0}', nameAliasIndex) : ''
                )
            );
        }

        vm.get('attributesSelectedStore').add(record);
        vm.get('theView').attributes().add(record);

        Ext.asap(function () {
            const gridSelection = grid.getSelection();
            vm.set('selectedAttributes', gridSelection);
            if (gridSelection.length !== 0) {
                view.down('#warningattribute').hide();
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Boolean} selected
     * @param {Object} eOpts
     */
    onToggleButton: function (button, selected, eOpts) {
        const grid = this.getView().down('#attributegrid');
        const store = grid.getStore();
        if (selected) {
            store.filter({
                property: 'id',
                operator: 'in',
                value: Ext.Array.pluck(grid.getSelection(), 'id')
            });
        } else {
            store.clearFilter();
        }
    },

    privates: {
        domaintree: null,

        /**
         *
         * @param {*} node
         * @returns
         */
        getDeepIndex: function (node) {
            if (!this.domaintree) {
                this.domaintree = this.getView().up('#joinviews-configuration-main').down('#domainstree');
            }
            let index = '';
            if (!node) {
                return String.fromCharCode(65);
            }
            if (node.parentNode && !node.parentNode.get('root')) {
                index += this.getDeepIndex(node.parentNode);
            }
            try {
                index += String.fromCharCode(
                    66 + Number(this.domaintree.getView().getNodeById(node.internalId).dataset.recordindex)
                );
            } catch (e) {
                index += String.fromCharCode(66);
            }
            return index;
        },

        /**
         *
         * @param {*} node
         * @param {*} store
         */
        addTargetAttributes: function (node, store) {
            const me = this;

            function manageAttributes(attribute, targetAlias, targetType) {
                const expr = Ext.String.format('{0}.{1}', targetAlias, attribute.get('name'));
                const storeAlreadyContain = store.findRecord('expr', expr, 0, false, true);
                const attributeType = attribute.get('type');
                const typeFormula = attributeType === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula;

                if (storeAlreadyContain) {
                    storeAlreadyContain.set('_deepIndex', me.getDeepIndex(node) + '_' + targetAlias);
                    storeAlreadyContain.set('_attributeDescription', attribute.getTranslatedDescription());
                    storeAlreadyContain.set('attributeconf', attribute.getData());
                    storeAlreadyContain.set('cmdbuildtype', attributeType);
                } else if (attribute.canAdminShow() && attribute.get('active') && !typeFormula) {
                    const joinViewAttribute = CMDBuildUI.model.views.JoinViewAttribute.create({
                        _deepIndex: me.getDeepIndex(node) + '_' + targetAlias,
                        targetAlias: targetAlias,
                        targetType: targetType,
                        expr: expr,
                        name: '',
                        description: '',
                        group: '',
                        showInGrid: false,
                        showInReducedGrid: false,
                        _attributeDescription: attribute.getTranslatedDescription(),
                        _select: false,
                        attributeconf: attribute.getData(),
                        cmdbuildtype: attributeType
                    });
                    store.addSorted(joinViewAttribute);
                }
            }

            const targetClass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(node.get('targetType'));
            targetClass.getAttributes().then(function (attributesStore) {
                attributesStore.each(function (attribute) {
                    manageAttributes(attribute, node.get('targetAlias'), node.get('targetType'));
                });
                // Add synthetic Subtype (Subtype) field if targetClass is a superclass
                me.addSubtypeAttributeIfNeeded(node, store, targetClass);
            });

            const domain = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                node.get('domain'),
                CMDBuildUI.util.helper.ModelHelper.objecttypes.domain
            );
            domain.getAttributes().then(function (attributesStore) {
                attributesStore.each(function (attribute) {
                    manageAttributes(attribute, node.get('domainAlias'), node.get('domain'));
                });
            });
        },

        addSubtypeAttributeIfNeeded: function (node, store, targetClass) {
            const me = this;
            const targetTypeName = node.get('targetType');
            const targetAlias = node.get('targetAlias');
            const deepIndex = me.getDeepIndex(node) + '_' + targetAlias;

            function addAttribute(config) {
                const storeAlreadyContain = store.findRecord('expr', config.expr, 0, false, true);
                if (!storeAlreadyContain) {
                    store.addSorted(
                        CMDBuildUI.model.views.JoinViewAttribute.create(
                            Ext.apply(
                                {
                                    _deepIndex: deepIndex,
                                    targetAlias: targetAlias,
                                    targetType: targetTypeName,
                                    name: '',
                                    description: '',
                                    group: '',
                                    showInGrid: false,
                                    showInReducedGrid: false,
                                    _select: false
                                },
                                config
                            )
                        )
                    );
                } else {
                    storeAlreadyContain.set('_deepIndex', deepIndex);
                    storeAlreadyContain.set('_attributeDescription', config._attributeDescription);
                    storeAlreadyContain.set('attributeconf', config.attributeconf);
                    storeAlreadyContain.set('cmdbuildtype', config.cmdbuildtype);
                }
            }

            const classesStore = Ext.getStore('classes.Classes');

            const isLeafClass =
                targetClass.get('prototype') === false &&
                !classesStore.findRecord('parent', targetTypeName, 0, false, true);

            if (!isLeafClass) {
                addAttribute({
                    expr: Ext.String.format('{0}.IdClass', targetAlias),
                    name: 'Subtype',
                    _attributeDescription: CMDBuildUI.locales.Locales.common.grid.subtype,
                    attributeconf: {
                        name: 'Subtype',
                        type: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                        description: CMDBuildUI.locales.Locales.common.grid.subtype,
                        hideInFilter: true,
                        active: true,
                        unique: false,
                        mandatory: false,
                        group: ''
                    },
                    cmdbuildtype: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string
                });
            }

            const multitenantMode = targetClass.get('multitenantMode');
            const multitenantEnabled = CMDBuildUI.util.helper.Configurations.get(
                CMDBuildUI.model.Configuration.multitenant.enabled
            );

            const hasTenant =
                multitenantEnabled &&
                (multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.always ||
                    multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.mixed);

            if (hasTenant) {
                const tenantLabel = CMDBuildUI.util.Utilities.getTenantLabel();

                addAttribute({
                    expr: Ext.String.format('{0}.IdTenant', targetAlias),
                    _attributeDescription: tenantLabel,
                    attributeconf: {
                        name: 'IdTenant',
                        type: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.tenant,
                        description: tenantLabel,
                        active: true,
                        unique: false,
                        mandatory: false,
                        group: ''
                    },
                    cmdbuildtype: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.tenant
                });
            }
        }
    }
});
