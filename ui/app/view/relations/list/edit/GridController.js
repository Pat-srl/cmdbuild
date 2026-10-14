Ext.define('CMDBuildUI.view.relations.list.edit.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-list-edit-grid',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
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
        }
    },

    /**
     * @param {CMDBuildUI.view.fields.reference.SelectionPopup} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view) {
        const vm = this.getViewModel();
        const objectType = vm.get('objectType');
        const objectTypeName = vm.get('objectTypeName');

        CMDBuildUI.util.helper.ModelHelper.getModel(objectType, objectTypeName).then(function (model) {
            if (vm.get('searchvalue')) {
                view.lookupReference('searchtextinput').focus();
            }
            // model name
            const modelname = CMDBuildUI.util.helper.ModelHelper.getModelName(objectType, objectTypeName);
            vm.set('storeinfo.modelname', modelname);

            // reconfigure table
            view.reconfigure(
                null,
                CMDBuildUI.util.helper.GridHelper.getColumns(model.getFields(), {
                    allowFilter: true,
                    objectType: objectType,
                    objectTypeName: objectTypeName
                })
            );

            // set autoload to true
            vm.set('storeinfo.autoload', true);
        });

        vm.bind(
            {
                bindTo: {
                    theRelation: '{theRelation}',
                    records: '{records}'
                },
                single: true
            },
            function (data) {
                if (!data.theRelation || !data.records) return;

                data.records.on({
                    load: {
                        fn: function (store, records, successful, operation, eOpts) {
                            if (successful) {
                                //get the record index
                                const selrecordindex = data.records.find('_id', data.theRelation.get('_destinationId'));
                                if (selrecordindex !== -1) {
                                    //get the record
                                    const record = data.records.getAt(selrecordindex);
                                    const node = view.getView().getNodeByRecord(record);

                                    if (node) {
                                        const checkcell = node.rows[0].cells[0];
                                        checkcell.classList.remove(CMDBuildUI.view.relations.list.add.Grid.disabledcls);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        );
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
        var vm = this.getViewModel();
        // if there is not record or is not available disallow selection and
        // is not the original destination record of the relation
        if (
            !record ||
            (record.get('_id') != vm.get('theRelation._destinationId') &&
                !record.get('_' + vm.get('theDomain').get('name') + '_available'))
        ) {
            return false;
        }
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
     * @param {Ext.data.Store} store
     */
    onRecordsStoreLoad: function (store) {
        var me = this,
            grid = me.getView(),
            vm = grid.lookupViewModel();

        if (vm.get('firstload')) {
            vm.set('firstload', false);
            var selId = vm.get('theRelation._destinationId');
            if (selId) {
                var metadata = store.getProxy().getReader().metaData,
                    posinfo = metadata.positions[selId];
                if (!posinfo.found) {
                    Ext.asap(function () {
                        CMDBuildUI.util.Msg.confirm(
                            CMDBuildUI.locales.Locales.notifier.attention,
                            CMDBuildUI.locales.Locales.relations.cardnotpresentmessage,
                            function (btnText) {
                                if (btnText.toLowerCase() === 'no') {
                                    grid.up('relations-list-edit-gridcontainer').fireEvent('popupclose');
                                }
                            },
                            me
                        );
                    });
                } else if (!posinfo.pageOffset) {
                    grid.setSelection(store.getById(selId + ''));
                    var extraparams = store.getProxy().getExtraParams();
                    delete extraparams.positionOf;
                    delete extraparams.positionOf_goToPage;
                } else {
                    grid.ensureVisible(posinfo.positionInTable, {
                        focus: true,
                        select: true,
                        callback: function (success, record, node) {
                            grid.setSelection(record);
                            var extraparams = store.getProxy().getExtraParams();
                            delete extraparams.positionOf;
                            delete extraparams.positionOf_goToPage;
                        }
                    });
                }
            }
        }
    }
});
