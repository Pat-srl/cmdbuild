Ext.define(
    'CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.FormWidgetFieldsetController',
    {
        extend: 'Ext.app.ViewController',
        alias: 'controller.administration-content-processes-tabitems-properties-fieldsets-formwidgetfieldset',
        mixins: ['CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.SorterGridsMixin'],

        control: {
            '#': {
                beforerender: 'onBeforeRender'
            }
        },

        /**
         *
         * @param {CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.FormWidgetFieldset} view
         * @param {Object} eOpts
         */
        onBeforeRender: function (view, eOpts) {
            const vm = view.lookupViewModel();
            vm.bind('{formWidgetCount}', function (formWidgetCount) {
                vm.set(
                    'formWidgetTitle',
                    Ext.String.format(
                        '{0} ({1})',
                        CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.formWidgets,
                        formWidgetCount
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
        onAddNewWidgetMenuBtn: function (grid, rowIndex, colIndex) {
            const vm = this.getViewModel();
            const widgets = vm.get('theProcess.widgets');
            const newWidgetStore = vm.get('formWidgetsStoreNew');
            const newWidget = newWidgetStore.getData().first();

            let invalid = false;
            // label can't be blank
            if (!newWidget.get('_label')) {
                grid.down('#widgetLabel').markInvalid(
                    CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.contextMenus.cantbeempty
                );
                invalid = true;
            }

            // type can't be blank
            if (!newWidget.get('_type')) {
                grid.down('#widgetType').markInvalid(
                    CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.contextMenus.cantbeempty
                );
                invalid = true;
            }
            if (invalid) {
                return false;
            }

            Ext.suspendLayouts();
            const uuid = Math.random().toString(36).substring(2) + Math.random().toString(36).substring(2);
            newWidget.set('_id', uuid);
            newWidget.set('WidgetId', uuid);
            newWidget.set('_config', (newWidget.get('_config') + '\nWidgetId="' + uuid + '"').trim());
            const clonedRecord = CMDBuildUI.model.WidgetDefinition.create(newWidget.getData());
            widgets.add(clonedRecord);
            newWidgetStore.rejectChanges();
            grid.refresh();
            vm.getParent().set('formWidgetCount', widgets.data.length);
            this.lookupReference('formWidgetGrid').view.grid.getView().refresh();
            window.newFormWidgetScriptField.getSession().setValue('');
            Ext.resumeLayouts();
        },

        /**
         *
         * @param {*} view
         * @param {*} rowIndex
         * @param {*} colIndex
         */
        onEditBtn: function (view, rowIndex, colIndex) {
            const vm = this.getViewModel();
            const grid = this.lookupReference('formWidgetGrid');
            const theWidget = grid.getStore().getAt(rowIndex);
            vm.set('theWidget', CMDBuildUI.util.administration.helper.ModelHelper.setReadState(theWidget));
            const formFields = [
                {
                    xtype: 'container',
                    //flex: 2,

                    padding: '0 10 0 10',
                    items: [
                        {
                            /********************* Triggers **********************/
                            xtype: 'fieldcontainer',
                            flex: 2,
                            columns: 1,
                            vertical: true,
                            viewModel: {
                                data: {
                                    widgetTypesStore: Ext.copy(vm.get('widgetTypesStore'))
                                }
                            },
                            fieldDefaults: {
                                labelAlign: 'top'
                            },
                            items: [
                                {
                                    xtype: 'textfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                                    },
                                    bind: {
                                        value: '{theWidget._label}'
                                    }
                                },
                                {
                                    xtype: 'combo',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.type,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.type'
                                    },
                                    editable: false,
                                    forceSelection: true,
                                    allowBlank: false,
                                    displayField: 'label',
                                    valueField: 'value',
                                    bind: {
                                        store: '{widgetTypesStore}',
                                        value: '{theWidget._type}'
                                    }
                                },
                                {
                                    xtype: 'checkboxgroup',
                                    userCls: 'hideCellCheboxes',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                                    },
                                    flex: 1,
                                    columns: 1,
                                    vertical: false,
                                    items: [
                                        {
                                            xtype: 'checkbox',
                                            bind: {
                                                value: '{theWidget._active}'
                                            },
                                            value: view.grid.getStore().getAt(rowIndex).get('_active')
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ];

            CMDBuildUI.util.administration.helper.AcePopupHelper.getPopup(
                'theWidget',
                theWidget,
                '_config',
                formFields,
                'popup-edit-fromwidget',
                CMDBuildUI.locales.Locales.administration.classes.strings.editformwidget
            );
        }
    }
);
