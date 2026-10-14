Ext.define(
    'CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.ContextMenusFieldsetController',
    {
        extend: 'Ext.app.ViewController',
        alias: 'controller.administration-content-classes-tabitems-properties-fieldsets-contextmenusfieldset',

        mixins: ['CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.SorterGridsMixin'],

        control: {
            '#': {
                beforerender: 'onBeforeRender'
            }
        },

        /**
         *
         * @param {CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.ContextMenusFieldset} view
         * @param {Object} eOpts
         */
        onBeforeRender: function (view, eOpts) {
            const vm = view.lookupViewModel();
            vm.bind('{contextMenuCount}', function (contextMenuCount) {
                vm.set(
                    'contextMenuTitle',
                    Ext.String.format(
                        '{0} ({1})',
                        CMDBuildUI.locales.Locales.administration.common.labels.contextmenuitems,
                        contextMenuCount
                    )
                );
            });
        },

        /**
         *
         * @param {*} view
         * @param {*} rowIndex
         * @param {*} colIndex
         */
        onAddNewContextMenuBtn: function (view, rowIndex, colIndex) {
            const vm = this.getViewModel();
            const contexstMenus = vm.get('theObject.contextMenuItems');
            const newContextMenuStore = vm.get('contextMenuItemsStoreNew');
            const newContextMenu = newContextMenuStore.getData().first();
            // label can't be blank
            if (!newContextMenu.get('label')) {
                view.down('#contextMenuLabel').markInvalid(
                    CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.contextMenus.cantbeempty
                );
                return false;
            }
            // label should be unique
            if (contexstMenus && contexstMenus.findRecord('label', newContextMenu.get('label'))) {
                view.down('#contextMenuLabel').markInvalid(
                    CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.contextMenus
                        .mustbeunique
                );
                return false;
            }
            Ext.suspendLayouts();
            if (newContextMenu.get('type') === 'component') {
                newContextMenu.set('config', newContextMenu.get('script'));
                newContextMenu.set('script', '');
            }
            contexstMenus.add(newContextMenu);
            vm.getParent().set('contextMenuCount', contexstMenus.data.length);
            newContextMenuStore.removeAt(rowIndex);
            const cleanRecord = CMDBuildUI.util.administration.helper.ModelHelper.setReadState(
                Ext.create('CMDBuildUI.model.ContextMenuItem', {
                    script: '',
                    config: ''
                })
            );
            newContextMenuStore.add(cleanRecord);

            view.refresh();
            this.lookupReference('contextMenuGrid').view.grid.getView().refresh();
            window.newContextMenuScriptField.getSession().setValue('');
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
            const grid = this.lookupReference('contextMenuGrid');
            const theContext = CMDBuildUI.util.administration.helper.ModelHelper.setReadState(
                grid.getStore().getAt(rowIndex)
            );
            vm.set('theContext', theContext);

            const formFields = [
                {
                    xtype: 'container',
                    //flex: 2,
                    padding: '0 10 0 10',
                    viewModel: {
                        data: {
                            contextMenuItemTypeStore: Ext.copy(vm.get('contextMenuItemTypeStore'))
                        },
                        stores: {
                            contextMenuComponentStore: Ext.copy(vm.get('contextMenuComponentStore'))
                        }
                    },

                    items: [
                        {
                            xtype: 'combobox',
                            inputField: 'componentId',
                            labelAlign: 'top',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets
                                    .contextMenus.inputs.typeOrGuiCustom.values.component.label,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.processes.properties.form.fieldsets.contextMenus.inputs.typeOrGuiCustom.values.component.label'
                            },
                            editable: false,
                            queryMode: 'local',
                            forceSelection: true,
                            allowBlank: false,
                            displayField: 'description',
                            valueField: 'name',
                            hidden: true,
                            store: vm.get('contextMenuComponentStore'),
                            bind: {
                                value: '{theContext.componentId}',
                                hidden: '{theContext.type !== "component"}'
                            }
                        },
                        {
                            xtype: 'combobox',
                            inputField: 'visibility',
                            labelAlign: 'top',
                            editable: false,
                            forceSelection: true,
                            allowBlank: false,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.processes.fieldlabels.applicability, // Applicability
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.processes.fieldlabels.applicability'
                            },
                            displayField: 'label',
                            valueField: 'value',
                            value: view.grid.getStore().getAt(rowIndex).get('applicability'),
                            store: vm.get('contextMenuApplicabilityStore'),
                            bind: {
                                value: '{theContext.visibility}'
                            }
                        },
                        {
                            /********************* Active **********************/
                            xtype: 'checkbox',
                            labelAlign: 'top',
                            bind: '{theContext.active}',
                            value: view.grid.getStore().getAt(rowIndex).get('active'),
                            boxLabel: CMDBuildUI.locales.Locales.administration.common.labels.active, // Active
                            localized: {
                                boxLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                            }
                        }
                    ]
                }
            ];

            CMDBuildUI.util.administration.helper.AcePopupHelper.getPopup(
                'theContext',
                theContext,
                theContext.get('type') === 'component' ? 'config' : 'script',
                formFields,
                'popup-edit-contextmenu',
                CMDBuildUI.locales.Locales.administration.classes.strings.editcontextmenu,
                null,
                theContext.get('type') === 'separator'
            );
        }
    }
);
