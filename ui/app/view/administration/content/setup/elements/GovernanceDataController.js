Ext.define('CMDBuildUI.view.administration.content.setup.elements.GovernanceDataController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-governancedata',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#editBtn': {
            click: 'onEditBtnCLick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#showOnlySelected': {
            change: 'onShowOnlySelectedChange'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.custompages.datasetmobile.items.ObjectsChoice} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        const me = this;
        const vm = this.getViewModel();

        vm.bind('{theSetup}', function (theSetup) {
            if (theSetup) {
                const classes = CMDBuildUI.model.setup.GovernanceData.create({
                    name: CMDBuildUI.locales.Locales.administration.navigation.classes,
                    description_translation: CMDBuildUI.locales.Locales.administration.navigation.classes,
                    isPrincipalFolder: true,
                    leaf: false,
                    expanded: true,
                    children: me.getTreeNodes(
                        Ext.getStore('classes.Classes').getRange(),
                        CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                        'Class'
                    )
                });

                const processes = CMDBuildUI.model.setup.GovernanceData.create({
                    name: CMDBuildUI.locales.Locales.administration.navigation.processes,
                    description_translation: CMDBuildUI.locales.Locales.administration.navigation.processes,
                    isPrincipalFolder: true,
                    leaf: false,
                    expanded: true,
                    children: me.getTreeNodes(
                        Ext.getStore('processes.Processes').getRange(),
                        CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
                        'Activity'
                    )
                });

                const root = vm.get('treeStore').getRootNode();
                root.removeAll();
                root.appendChild(classes);
                root.appendChild(processes);
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnCLick: function (btn, e, eOpts) {
        var vm = btn.lookupViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        vm.set('disabledTabs.log', true);
        vm.set('disabledTabs.retention', true);
        vm.set('disabledTabs.audit', true);
        vm.set('disabledTabs.governancedata', false);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (btn, e, eOpts) {
        this.redirectTo('administration/setup/logs', true);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        const vm = button.lookupViewModel();
        const governanceData = {};

        Ext.getBody().mask(CMDBuildUI.locales.Locales.administration.common.messages.saving);

        Ext.Array.forEach(vm.get('treeStore').getRange(), function (item, index, allitems) {
            const entityValues = [];
            Ext.Array.forEach(['list', 'read', 'create', 'update', 'delete'], function (property, ind, allproperties) {
                if (item.get(property)) {
                    entityValues.push(property.charAt(0).toUpperCase());
                }
            });

            if (!Ext.isEmpty(entityValues)) {
                governanceData[item.get('name')] = entityValues;
            }
        });

        CMDBuildUI.util.administration.helper.ConfigHelper.setConfig(
            'org__DOT__cmdbuild__DOT__activitylog__DOT__entries',
            Ext.encode(governanceData)
        ).always(function () {
            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
            vm.set('disabledTabs.log', false);
            vm.set('disabledTabs.retention', false);
            vm.set('disabledTabs.audit', false);
            if (Ext.getBody().isMasked()) {
                Ext.getBody().unmask();
            }
        });
    },

    /**
     *
     * @param {Ext.Button.button} button
     * @param {Boolean} pressed
     * @param {Object} eOpts
     */
    /**
     *
     * @param {Ext.form.field.Field} field
     * @param {Object} newValue
     * @param {Object} oldValue
     * @param {Object} eOpts
     */
    onShowOnlySelectedChange: function (field, newValue, oldValue, eOpts) {
        const store = this.getViewModel().get('treeStore');
        const root = store.getRoot();

        if (newValue) {
            const visibleNodes = {};

            root.cascadeBy(function (node) {
                if (
                    node.isLeaf() &&
                    (node.get('list') ||
                        node.get('read') ||
                        node.get('create') ||
                        node.get('update') ||
                        node.get('delete'))
                ) {
                    visibleNodes[node.id] = true;

                    let parent = node.parentNode;
                    while (parent) {
                        visibleNodes[parent.id] = true;
                        parent = parent.parentNode;
                    }
                }
            });

            store.filterBy(function (node) {
                return !!visibleNodes[node.id];
            });
        } else {
            store.clearFilter();
        }
    },

    privates: {
        /**
         *
         * @param {Array} records
         * @param {String} objectType
         * @param {String} parentname
         * @returns
         */
        getTreeNodes: function (records, objectType, parentname) {
            const me = this;
            const output = [];
            const activityLogEntries = Ext.decode(
                this.getViewModel().get('theSetup')['org__DOT__cmdbuild__DOT__activitylog__DOT__entries']
            );
            const frecords = Ext.Array.filter(records, function (item) {
                return item.get('parent') === parentname;
            });

            frecords.forEach(function (record, i) {
                const treeItem = CMDBuildUI.model.setup.GovernanceData.create({
                    objectType: objectType,
                    name: record.get('name'),
                    description: record.get('description'),
                    description_translation: record.get('_description_translation') || record.get('description'),
                    prototype: record.get('prototype'),
                    mode: null,
                    leaf: true
                });

                const exist = activityLogEntries[record.get('name')];
                if (exist) {
                    treeItem.set('list', exist.includes('L'));
                    treeItem.set('read', exist.includes('R'));
                    treeItem.set('create', exist.includes('C'));
                    treeItem.set('update', exist.includes('U'));
                    treeItem.set('delete', exist.includes('D'));
                }

                if (record.get('prototype')) {
                    treeItem.set('leaf', false);
                    treeItem.set('expanded', true);
                    treeItem.set('children', me.getTreeNodes(records, objectType, record.get('name')));
                }

                output.push(treeItem);
            });

            return output;
        }
    }
});
