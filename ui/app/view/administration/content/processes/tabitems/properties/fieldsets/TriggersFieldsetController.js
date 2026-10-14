Ext.define(
    'CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.TriggersFieldsetController',
    {
        extend: 'Ext.app.ViewController',
        alias: 'controller.administration-content-processes-tabitems-properties-fieldsets-triggersfieldset',

        mixins: ['CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.SorterGridsMixin'],

        control: {
            '#': {
                beforerender: 'onBeforeRender'
            }
        },

        /**
         *
         * @param {CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.TriggersFieldset} view
         * @param {Object} eOpts
         */
        onBeforeRender: function (view, eOpts) {
            const vm = view.lookupViewModel();
            vm.bind('{formTriggerCount}', function (formTriggerCount) {
                vm.set(
                    'formTriggerTitle',
                    Ext.String.format(
                        '{0} ({1})',
                        CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.title,
                        formTriggerCount
                    )
                );
            });
        },

        /**
         *
         * @param {*} grid
         * @param {*} rowIndex
         * @param {*} colIndex
         * @returns
         */
        onAddNewTriggerBtn: function (grid, rowIndex, colIndex) {
            const vm = this.getViewModel();
            const mainGrid = this.lookupReference('triggersGrid');
            const triggers = mainGrid.getStore();
            const newTriggerStore = vm.get('formTriggersStoreNew');
            const newTrigger = newTriggerStore.getData().items[0];
            const codeEditorEl = window.newTriggerScript.container;
            if (!newTrigger.get('script')) {
                codeEditorEl.style.border = '1px solid #cf4c35';
                codeEditorEl.dataset.errorqtip = Ext.String.format(
                    '<ul class="x-list-plain"><li>{0}</li></ul>',
                    CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.contextMenus.cantbeempty
                );
                return false;
            }

            Ext.suspendLayouts();
            triggers.add(newTrigger);
            newTriggerStore.removeAll();
            newTriggerStore.add(
                Ext.create('CMDBuildUI.model.FormTrigger', {
                    script: ''
                })
            );
            grid.up('grid').reconfigure(newTriggerStore);
            mainGrid.reconfigure(triggers);
            vm.getParent().set('formTriggerCount', triggers.data.length);
            // Ext.ComponentQuery.query('[name="formTriggerBeforeView"]')[0].setValue(false); // unused for now
            Ext.ComponentQuery.query('[name="formTriggerBeforeInsert"]')[0].setValue(false);
            Ext.ComponentQuery.query('[name="formTriggerBeforeEdit"]')[0].setValue(false);
            Ext.ComponentQuery.query('[name="formTriggerAfterInsertSave"]')[0].setValue(false);
            Ext.ComponentQuery.query('[name="formTriggerAfterInsertExecute"]')[0].setValue(false);
            Ext.ComponentQuery.query('[name="formTriggerAfterEditSave"]')[0].setValue(false);
            Ext.ComponentQuery.query('[name="formTriggerAfterEditExecute"]')[0].setValue(false);
            Ext.ComponentQuery.query('[name="formTriggerAfterDelete"]')[0].setValue(false);
            Ext.ComponentQuery.query('[name="formTriggerActive"]')[0].setValue(true);
            window.newTriggerScript.getSession().setValue('');
            Ext.resumeLayouts();
        },

        /**
         *
         * @param {*} element
         * @param {*} newValue
         * @param {*} oldValue
         * @param {*} eOpts
         */
        formTriggerCheckChange: function (element, newValue, oldValue, eOpts) {
            element
                .up('administration-content-processes-tabitems-properties-properties')
                .lookupViewModel()
                .get('formTriggersStoreNew')
                .getData()
                .items[0].set(element.recordKey, newValue);
        },

        /**
         *
         * @param {*} button
         * @param {*} rowIndex
         * @param {*} colIndex
         */
        onEditBtn: function (button, rowIndex, colIndex) {
            const vm = button.up('administration-content-processes-tabitems-properties-properties').lookupViewModel();

            const grid = this.lookupReference('triggersGrid');
            const store = grid.getStore();
            const theTrigger = CMDBuildUI.util.administration.helper.ModelHelper.setReadState(store.getAt(rowIndex));
            vm.set('theTrigger', theTrigger);
            const formFields = [
                {
                    xtype: 'container',
                    //flex: 2,
                    padding: '0 10 0 10',
                    viewModel: {},
                    items: [
                        {
                            /********************* Triggers **********************/
                            xtype: 'checkboxgroup',
                            userCls: 'hideCellCheboxes',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.classes.strings.executeon,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.strings.executeon'
                            },
                            flex: 2,
                            columns: 1,
                            labelAlign: 'top',
                            vertical: true,
                            viewModel: {},
                            items: [
                                {
                                    boxLabel:
                                        CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets
                                            .formTriggers.inputs.events.values.beforeInsert.label,
                                    localized: {
                                        boxLabel:
                                            'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.beforeInsert.label'
                                    },
                                    bind: {
                                        value: '{theTrigger.beforeInsert}'
                                    }
                                },
                                {
                                    boxLabel:
                                        CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets
                                            .formTriggers.inputs.events.values.beforeEdit.label,
                                    localized: {
                                        boxLabel:
                                            'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.beforeEdit.label'
                                    },
                                    bind: {
                                        value: '{theTrigger.beforeEdit}'
                                    }
                                },
                                {
                                    boxLabel:
                                        CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets
                                            .formTriggers.inputs.events.values.afterInsertSave.label,
                                    localized: {
                                        boxLabel:
                                            'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.afterInsertSave.label'
                                    },
                                    bind: {
                                        value: '{theTrigger.afterInsert}'
                                    }
                                },
                                {
                                    boxLabel:
                                        CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets
                                            .formTriggers.inputs.events.values.afterInsertExecute.label,
                                    localized: {
                                        boxLabel:
                                            'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.afterInsertExecute.label'
                                    },
                                    bind: {
                                        value: '{theTrigger.afterInsertExecute}'
                                    }
                                },
                                {
                                    boxLabel:
                                        CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets
                                            .formTriggers.inputs.events.values.afterEditSave.label,
                                    localized: {
                                        boxLabel:
                                            'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.afterEditSave.label'
                                    },
                                    bind: {
                                        value: '{theTrigger.afterEdit}'
                                    }
                                },
                                {
                                    boxLabel:
                                        CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets
                                            .formTriggers.inputs.events.values.afterEditExecute.label,
                                    localized: {
                                        boxLabel:
                                            'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.afterEditExecute.label'
                                    },
                                    bind: {
                                        value: '{theTrigger.afterEditExecute}'
                                    }
                                },
                                {
                                    boxLabel:
                                        CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets
                                            .formTriggers.inputs.events.values.afterAbort.label,
                                    localized: {
                                        boxLabel:
                                            'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formTriggers.inputs.events.values.afterAbort.label'
                                    },
                                    bind: {
                                        value: '{theTrigger.afterDelete}'
                                    }
                                }
                            ]
                        },
                        {
                            xtype: 'container',
                            //flex: 1,
                            //heigth: '20px',
                            items: [
                                {
                                    /********************* Triggers **********************/
                                    xtype: 'checkboxgroup',
                                    userCls: 'hideCellCheboxes',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                                    },
                                    flex: 1,
                                    columns: 1,
                                    vertical: false,
                                    labelAlign: 'top',
                                    items: [
                                        {
                                            xtype: 'checkbox',
                                            bind: {
                                                value: '{theTrigger.active}'
                                            },
                                            value: theTrigger.get('active')
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ];

            CMDBuildUI.util.administration.helper.AcePopupHelper.getPopup(
                'theTrigger',
                theTrigger,
                'script',
                formFields,
                'popup-edit-formtrigger',
                CMDBuildUI.locales.Locales.administration.common.tooltips.edittrigger
            );
        }
    }
);
