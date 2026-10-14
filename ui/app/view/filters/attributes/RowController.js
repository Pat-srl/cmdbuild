Ext.define('CMDBuildUI.view.filters.attributes.RowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.filters-attributes-row',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#operatorcombo': {
            change: 'onOperatorComboChange'
        },
        '#typecheck': {
            change: 'onTypeCkeckChange'
        },
        '#currentUser': {
            change: 'onCurrentUserCheckChange'
        },
        '#currentGroup': {
            change: 'onCurrentGroupCheckChange'
        },
        '#removebutton': {
            beforerender: 'onRemoveButtonBeforeRender'
            // click event is managed on parent panel
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.filters.attributes.Row} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = view.lookupViewModel();
        // add values fields
        this.addValuesFields(vm.get('values.attribute'));
        if (vm.get('attachments.operator_label')) {
            view.down('#comboboxoperator').setMargin('0 0 0 15');
        }
    },

    /**
     * Executed when changes the value of Operator combobox.
     *
     * @param {Ext.form.field.ComboBox} combo
     * @param {String} newValue
     * @param {String} oldValue
     * @param {Object} eOpts
     */
    onOperatorComboChange: function (combo, newValue, oldValue, eOpts) {
        this.updateTypeFieldVisibility();
        this.updateCurrentUserVisibility();
        this.updateCurrentGroupVisibility();
        this.updateValue1FieldVisibility();
        this.updateValue2FieldVisibility();
    },

    /**
     * Executed when changes the value of Operator combobox.
     *
     * @param {Ext.form.field.Checkbox} check
     * @param {Boolean} newValue
     * @param {Boolean} oldValue
     * @param {Object} eOpts
     */
    onTypeCkeckChange: function (check, newValue, oldValue, eOpts) {
        this.updateCurrentUserVisibility();
        this.updateCurrentGroupVisibility();
        this.updateValue1FieldVisibility();
        this.updateValue2FieldVisibility();
    },

    /**
     * Executed when changes the value current user checkbox.
     *
     * @param {Ext.form.field.Checkbox} check
     * @param {Boolean} newValue
     * @param {Boolean} oldValue
     * @param {Object} eOpts
     */
    onCurrentUserCheckChange: function (check, newValue, oldValue, eOpts) {
        const vm = this.getViewModel();
        vm.set('values.value1', null);
        this.updateValue1FieldVisibility();
        this.updateValue2FieldVisibility();
    },

    /**
     * Executed when changes the value current group checkbox.
     *
     * @param {Ext.form.field.Checkbox} check
     * @param {Boolean} newValue
     * @param {Boolean} oldValue
     * @param {Object} eOpts
     */
    onCurrentGroupCheckChange: function (check, newValue, oldValue, eOpts) {
        const vm = this.getViewModel();
        vm.set('values.value1', null);
        this.updateValue1FieldVisibility();
        this.updateValue2FieldVisibility();
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onRemoveButtonBeforeRender: function (button, eOpts) {
        this.applyUiIfAdmin(button);
    },

    privates: {
        /**
         * Add a container with value fields.
         * @param {String} attributename
         */
        addValuesFields: function (attributename) {
            const view = this.getView();
            const vm = this.getViewModel();
            const container = this.lookupReference('valuescontainer');
            const allattributes = vm.get('allfields');
            var editor, attribute;

            // empty container
            container.removeAll(true);

            if (attributename && allattributes && allattributes[attributename]) {
                attribute = allattributes[attributename];
                attribute = Ext.apply({}, attribute);
                vm.set('attributetype', attribute.cmdbuildtype);

                if ((attribute.type === 'date' || attribute.cmdbuildtype === 'date') && vm.get('values.value1')) {
                    vm.set('values.value1', new Date(vm.get('values.value1')));
                    if (vm.get('values.value2')) {
                        vm.set('values.value2', new Date(vm.get('values.value2')));
                    }
                }

                switch (attribute.cmdbuildtype) {
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.activity:
                        editor = CMDBuildUI.util.helper.FormHelper.getActivityField(
                            CMDBuildUI.util.helper.FormHelper.formmodes.update
                        );
                        if (editor) {
                            editor.fieldLabel = null;
                            editor.hidden = true;
                            editor.bind.hidden = '{hiddenfields.value1}';
                            editor.bind.value = '{values.value1}';
                            editor.bind.disabled = '{displayOnly}';
                            container.add(editor);
                        }
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference:
                        if (view.getAllowCurrentUser() && 'User' === attribute.attributeconf.targetClass) {
                            editor = {
                                xtype: 'bufferedcombo',
                                labelAlign: 'top',
                                valueField: '_id',
                                displayField: 'username',
                                columns: [
                                    {
                                        dataIndex: 'username',
                                        flex: 1
                                    }
                                ],
                                name: 'userId',
                                storealias: 'users',
                                modelname: 'CMDBuildUI.model.users.User',
                                recordLinkName: 'values',
                                bind: {
                                    hidden: '{hiddenfields.value1}',
                                    value: '{values.value1}',
                                    disabled: '{displayOnly}'
                                }
                            };
                        }
                        // show current user or current group
                        else if (view.getAllowCurrentGroup() && 'Group' === attribute.attributeconf.targetClass) {
                            editor = {
                                xtype: 'bufferedcombo',
                                labelAlign: 'top',
                                valueField: '_id',
                                displayField: 'description',
                                columns: [
                                    {
                                        dataIndex: 'description',
                                        flex: 1
                                    }
                                ],
                                name: 'groupId',
                                storealias: 'groups',
                                modelname: 'CMDBuildUI.model.users.Group',
                                recordLinkName: 'values',
                                bind: {
                                    hidden: '{hiddenfields.value1}',
                                    value: '{values.value1}',
                                    disabled: '{displayOnly}'
                                }
                            };
                        }

                        if (editor) {
                            container.add(editor);
                        }

                        container.add({
                            xtype: 'textfield',
                            hidden: true,
                            bind: {
                                value: '{values.referencetext}',
                                hidden: '{hiddenfields.referencetext}',
                                disabled: '{displayOnly}'
                            }
                        });
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray:
                        attribute.cmdbuildtype = 'lookup';
                        container.add({
                            xtype: 'textfield',
                            hidden: true,
                            bind: {
                                value: '{values.referencetext}',
                                hidden: '{hiddenfields.referencetext}',
                                disabled: '{displayOnly}'
                            }
                        });
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup:
                        container.add({
                            xtype: 'textfield',
                            hidden: true,
                            bind: {
                                value: '{values.referencetext}',
                                hidden: '{hiddenfields.referencetext}',
                                disabled: '{displayOnly}'
                            }
                        });
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.tenant:
                        editor = CMDBuildUI.util.helper.FormHelper.getTenantField(
                            CMDBuildUI.util.helper.FormHelper.formmodes.update
                        );
                        container.add(
                            Ext.applyIf(
                                {
                                    hidden: true,
                                    fieldLabel: null,
                                    bind: {
                                        value: '{values.value1}',
                                        hidden: '{hiddenfields.value1}',
                                        disabled: '{displayOnly}'
                                    }
                                },
                                editor
                            )
                        );
                        break;
                    case 'dmscategory':
                        editor = {
                            xtype: 'groupedcombobox',
                            valueField: 'value',
                            displayField: 'label',
                            queryMode: 'local',
                            forceSelection: true,
                            hidden: true,
                            bind: {
                                store: '{dmscategories}',
                                value: '{values.value1}',
                                hidden: '{hiddenfields.value1}',
                                disabled: '{displayOnly}'
                            }
                        };
                        container.add(editor);
                        break;
                }

                if (!editor) {
                    editor = CMDBuildUI.util.helper.FormHelper.getEditorForField(attribute, {
                        ignoreUpdateVisibilityToField: true,
                        ignoreCustomValidator: true,
                        ignoreAutovalue: true
                    });

                    if (editor.xtype === 'threestatecheckboxfield') {
                        editor.xtype = 'checkboxfield';
                    } else if (editor.xtype === 'linkfield') {
                        editor.xtype = 'textfield';
                    }

                    editor.ignoreCqlFilter = true;
                    container.add([
                        Ext.apply(
                            {
                                hidden: true,
                                bind: {
                                    value: '{values.value1}',
                                    hidden: '{hiddenfields.value1}',
                                    disabled: '{displayOnly}'
                                }
                            },
                            editor
                        ),
                        Ext.apply(
                            {
                                hidden: true,
                                bind: {
                                    value: '{values.value2}',
                                    hidden: '{hiddenfields.value2}',
                                    disabled: '{displayOnly}'
                                }
                            },
                            editor
                        )
                    ]);
                }
            } else if (attributename && !attribute) {
                editor = {
                    xtype: 'textfield',
                    hidden: true,
                    bind: {
                        value: '{values.value1}',
                        hidden: '{hiddenfields.value1}',
                        disabled: '{displayOnly}'
                    }
                };
                container.add(editor);
            }
        },

        /**
         * @param {Ext.button.Button} button
         */
        applyUiIfAdmin: function (button) {
            const vm = button.lookupViewModel();
            if (vm && vm.get('isAdministrationModule')) {
                button.ui = 'administration-action';
            }
        },

        /**
         *
         */
        updateTypeFieldVisibility: function () {
            const vm = this.getViewModel();
            const allowInputParameter = this.getView().getAllowInputParameter();
            const operator = vm.get('values.operator');
            var isHidden = true;

            if (
                vm.get('values.typeinput') &&
                (operator === CMDBuildUI.util.helper.FiltersHelper.operators.null ||
                    operator === CMDBuildUI.util.helper.FiltersHelper.operators.notnull)
            ) {
                vm.set('values.typeinput', false);
            }
            if (
                allowInputParameter &&
                operator &&
                operator !== CMDBuildUI.util.helper.FiltersHelper.operators.null &&
                operator !== CMDBuildUI.util.helper.FiltersHelper.operators.notnull
            ) {
                isHidden = false;
            }
            vm.set('hiddenfields.typeinput', isHidden);
        },

        /**
         *
         */
        updateCurrentUserVisibility: function () {
            const vm = this.getViewModel();
            const allowCurrentUser = this.getView().getAllowCurrentUser();
            const operator = vm.get('values.operator');
            const attribute = vm.get('values.attribute');
            const typeinput = vm.get('values.typeinput');
            const attributeData = vm.get('allfields')[attribute];
            var isHidden = true;

            if (
                attribute &&
                allowCurrentUser &&
                (operator === CMDBuildUI.util.helper.FiltersHelper.operators.equal ||
                    operator === CMDBuildUI.util.helper.FiltersHelper.operators.notequal) &&
                attributeData &&
                'User' === attributeData.attributeconf.targetClass &&
                !typeinput
            ) {
                isHidden = false;
            }
            if (isHidden) {
                vm.set('values.currentUser', false);
            }
            vm.set('hiddenfields.currentUser', isHidden);
        },

        /**
         *
         */
        updateCurrentGroupVisibility: function () {
            const vm = this.getViewModel();
            const allowCurrentGroup = this.getView().getAllowCurrentGroup();
            const operator = vm.get('values.operator');
            const attribute = vm.get('values.attribute');
            const typeinput = vm.get('values.typeinput');
            const attributeData = vm.get('allfields')[attribute];
            var isHidden = true;

            if (
                attribute &&
                allowCurrentGroup &&
                (operator === CMDBuildUI.util.helper.FiltersHelper.operators.equal ||
                    operator === CMDBuildUI.util.helper.FiltersHelper.operators.notequal) &&
                attributeData &&
                'Role' === attributeData.attributeconf.targetClass &&
                !typeinput
            ) {
                isHidden = false;
            }
            if (isHidden) {
                vm.set('values.currentGroup', false);
            }
            vm.set('hiddenfields.currentGroup', isHidden);
        },

        /**
         *
         */
        updateValue1FieldVisibility: function () {
            const vm = this.getViewModel();
            const operator = vm.get('values.operator');
            const typeinput = vm.get('values.typeinput');
            const currentGroup = vm.get('values.currentGroup');
            const currentUser = vm.get('values.currentUser');
            var isHidden = true;
            var refTextFiledHidden = true;

            if (
                operator &&
                operator !== CMDBuildUI.util.helper.FiltersHelper.operators.null &&
                operator !== CMDBuildUI.util.helper.FiltersHelper.operators.notnull &&
                !typeinput &&
                !currentGroup &&
                !currentUser
            ) {
                isHidden = false;
            }

            if (
                [
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.activity,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookuparray
                ].indexOf(vm.get('attributetype')) > -1 &&
                CMDBuildUI.util.helper.FiltersHelper.isOperatorForReferenceOrLookupDescription(operator)
            ) {
                refTextFiledHidden = isHidden;
                isHidden = true;
            }
            vm.set('hiddenfields.value1', isHidden);
            vm.set('hiddenfields.referencetext', refTextFiledHidden);
        },

        /**
         *
         */
        updateValue2FieldVisibility: function () {
            const vm = this.getViewModel();
            const operator = vm.get('values.operator');
            const typeinput = vm.get('values.typeinput');
            const currentGroup = vm.get('values.currentGroup');
            const currentUser = vm.get('values.currentUser');
            var isHidden = true;

            if (
                operator &&
                operator === CMDBuildUI.util.helper.FiltersHelper.operators.between &&
                !typeinput &&
                !currentGroup &&
                !currentUser
            ) {
                isHidden = false;
            }
            vm.set('hiddenfields.value2', isHidden);
        }
    }
});
