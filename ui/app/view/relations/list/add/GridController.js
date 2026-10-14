Ext.define('CMDBuildUI.view.relations.list.add.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-list-add-grid',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            beforeedit: 'onBeforeEdit',
            edit: 'onEdit',
            selectionchange: 'onSelectionChange',
            beforeselect: 'onBeforeSelect'
        },
        '#addcardbtn': {
            beforerender: 'onAddCardBtnBeforeRender'
        },
        '#searchtextinput': {
            specialkey: 'onSearchSpecialKey'
        },
        '#refreshBtn': {
            click: 'onRefreshBtnClick'
        },
        '#checkedonly': {
            toggle: 'onToggleButton'
        }
    },

    /**
     * @param {CMDBuildUI.view.relations.list.add.Grid} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view) {
        var vm = this.getViewModel();

        if (vm.get('searchvalue')) {
            view.lookupReference('searchtextinput').focus();
        }

        // model name
        var modelname = CMDBuildUI.util.helper.ModelHelper.getModelName(vm.get('objectType'), vm.get('objectTypeName'));
        vm.set('storeinfo.modelname', modelname);

        // set autoload to true
        vm.set('storeinfo.autoload', true);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onAddCardBtnBeforeRender: function (button, eOpts) {
        var me = this,
            vm = button.lookupViewModel(),
            theDomain = vm.get('theDomain');
        this.getView().updateAddButton(
            button,
            function (item, event, eOpts) {
                me.onAddCardBtnClick(item, event, eOpts);
            },
            vm.get('objectTypeName'),
            vm.get('objectType'),
            vm.get('relationDirection') === 'direct'
                ? theDomain.get('disabledDestinationDescendants')
                : theDomain.get('disabledSourceDescendants')
        );
    },

    /**
     *
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onAddCardBtnClick: function (item, event, eOpts) {
        var vm = this.getViewModel(),
            grid = this.getView();
        var title = Ext.String.format(
            '{0} {1}',
            CMDBuildUI.locales.Locales.classes.cards.addcard,
            vm.get('objectTypeDescription')
        );
        var popup = CMDBuildUI.util.Utilities.openPopup(null, title, {
            xtype: 'classes-cards-card-create',
            padding: 10,
            fireGlobalEventsAfterSave: false,
            viewModel: {
                data: {
                    objectTypeName: item.objectTypeName
                }
            },
            hideInlineElements: {
                inlineNotes: false
            },
            buttons: [
                {
                    text: CMDBuildUI.locales.Locales.common.actions.cancel,
                    itemId: 'cancelbutton',
                    ui: 'secondary-action-small',
                    autoEl: {
                        'data-testid': 'relations-list-add-grid-create-cancel'
                    },
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
                    },
                    handler: function (button, e) {
                        popup.destroy(true);
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.common.actions.save,
                    formBind: true, //only enabled once the form is valid
                    disabled: true,
                    ui: 'management-primary-small',
                    autoEl: {
                        'data-testid': 'relations-list-add-grid-create-save'
                    },
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.save'
                    },
                    handler: function (button, e) {
                        var form = button.up('form'),
                            cancelButton = form.down('#cancelbutton');
                        if (form.isValid()) {
                            CMDBuildUI.util.helper.FormHelper.startSavingForm();
                            button.showSpinner = true;
                            CMDBuildUI.util.Utilities.disableFormButtons([button, cancelButton]);

                            var object = form.getViewModel().get('theObject');
                            object.save({
                                success: function (record, operation) {
                                    form.getController()
                                        .saveForm({
                                            failure: function () {
                                                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                                                CMDBuildUI.util.Utilities.enableFormButtons([button, cancelButton]);
                                            }
                                        })
                                        .then(function (record) {
                                            grid.selectItemAfterCreation(record);
                                            popup.destroy(true);
                                        });
                                },
                                callback: function (record, operation, success) {
                                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                                    CMDBuildUI.util.Utilities.enableFormButtons([button, cancelButton]);
                                }
                            });
                        }
                    }
                }
            ]
        });
    },

    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        if (vm.get('searchvalue')) {
            var store = vm.get('records');
            if (store) {
                // add filter
                store.getAdvancedFilter().addQueryFilter(vm.get('searchvalue'));
                store.load();
            }
        } else {
            this.onSearchClear(field);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        var store = vm.get('records');
        if (store) {
            // clear store filter
            store.getAdvancedFilter().clearQueryFilter();
            store.load();
            // reset input
            field.reset();
        }
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() == event.ENTER) {
            this.onSearchSubmit(field);
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Ext.event.Event} event
     * @param {Object} e
     */
    onRefreshBtnClick: function (button, event, e) {
        var view = this.getView();
        view.getStore().load();
        view.setSelection();
    },

    /**
     *
     * @param {Ext.grid.plugin.CellEditing} editor
     * @param {Object} context
     * @param {Object} eOpts
     */
    onBeforeEdit: function (editor, context, eOpts) {
        const columnEditor = context.column.getEditor();
        if (
            columnEditor &&
            (columnEditor.getXType() === 'referencecombofield' ||
                columnEditor.getXType() === 'lookuparrayfield' ||
                columnEditor.getXType() === 'referencefield' ||
                columnEditor.getXType() === 'lookupfield')
        ) {
            columnEditor._ownerRecord = context.record;
            // Pass _ownerRecord to combofield
            if (columnEditor.down()) {
                columnEditor.down()._ownerRecord = context.record;
            }
        }
        // prevent edit if row is not selected
        return Ext.Array.contains(context.grid.getSelection(), context.record);
    },

    /**
     *
     * @param {Ext.grid.plugin.CellEditing} editor
     * @param {Object} context
     */
    onEdit: function (editor, context) {
        var cell = context.view.getCellByPosition({
            view: context.view,
            row: context.row,
            column: context.column
        });
        if (context.column.mandatory && Ext.isEmpty(context.record.get(context.column.dataIndex))) {
            cell.addCls(CMDBuildUI.view.relations.list.add.Grid.errorcls);
        } else {
            cell.removeCls(CMDBuildUI.view.relations.list.add.Grid.errorcls);
        }
        this.validateRelAttribtues();
    },

    /**
     *
     * @param {CMDBuildUI.view.relations.list.add.Grid} grid
     * @param {Ext.data.Model[]} selection
     * @param {Object} eOpts
     */
    onSelectionChange: function (selmode, selection, eOpts) {
        var view = this.getView(),
            prevsel = Ext.Array.from(this._prevselection),
            added = Ext.Array.difference(selection, prevsel),
            removed = Ext.Array.difference(prevsel, selection);

        view.getRelationAttributes().forEach(function (relattr) {
            if (relattr.mandatory) {
                // get column position
                var column = view.getVisibleColumns().find(function (c) {
                    return c.dataIndex == relattr.name;
                });
                var colposition = column.getVisibleIndex();

                // add error class
                added.forEach(function (r) {
                    if (Ext.isEmpty(r.get(relattr.name))) {
                        var position = selmode.view.getPosition(r, colposition);
                        var cell = selmode.view.getCellByPosition(position);
                        cell.addCls(CMDBuildUI.view.relations.list.add.Grid.errorcls);
                    }
                });
                // remove error class
                removed.forEach(function (r) {
                    var position = selmode.view.getPosition(r, colposition);
                    var cell = selmode.view.getCellByPosition(position);
                    cell.removeCls(CMDBuildUI.view.relations.list.add.Grid.errorcls);
                });
            }
        });
        this.validateRelAttribtues();
        this._prevselection = selection;
    },

    /**
     * Fired before a record is selected. If any listener returns false, the selection is cancelled.
     *
     * @param {Ext.selection.RowModel} selModel
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onBeforeSelect: function (selModel, record, index, eOpts) {
        // if there is not record or is not available disallow selection
        if (!record || !record.get('_' + this.getViewModel().get('theDomain').get('name') + '_available')) {
            return false;
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Boolean} selected
     * @param {Object} eOpts
     */
    onToggleButton: function (button, selected, eOpts) {
        var view = this.getView(),
            store = view.getStore(),
            advancedFilter = store.getAdvancedFilter(),
            selectedAttributesId = Ext.Array.pluck(view.getSelection(), 'id');
        if (selected) {
            advancedFilter.addAttributeFilter('_id', 'in', selectedAttributesId);
        } else {
            advancedFilter.clearAttributesFilter();
        }
        store.load();
    },

    privates: {
        validateRelAttribtues: function () {
            var isValid = true,
                view = this.getView();

            view.getRelationAttributes().forEach(function (relattr) {
                // update validation to enable / disable button
                view.getSelection().forEach(function (r) {
                    if (relattr.mandatory && Ext.isEmpty(r.get(relattr.name))) {
                        isValid = false;
                    }
                });
            });

            view.lookupViewModel().set('valid.attrs', isValid);
        }
    }
});
