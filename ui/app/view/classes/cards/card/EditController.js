Ext.define('CMDBuildUI.view.classes.cards.card.EditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.classes-cards-card-edit',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            beforedestroy: 'onBeforeDestroy'
        },
        '#savebtn': {
            click: 'onSaveBtnClick'
        },
        '#saveandclosebtn': {
            click: 'onSaveAndCloseBtnClick'
        },
        '#cancelbtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.card.EditController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();
        const me = this;

        /**
         * Promise success callback.
         * @param {CMDBuildUI.model.classes.Card} model
         */
        function success(model) {
            vm.set('objectModel', model);

            // set instance to ViewModel
            vm.linkTo('theObject', {
                type: model.getName(),
                id: view.getObjectId() || vm.get('objectId')
            });
        }

        // if the object is not defined on parent
        if (!(vm.get('theObject') && vm.get('objectModel'))) {
            // get model
            CMDBuildUI.util.helper.ModelHelper.getModel(
                'class',
                view.getObjectTypeName() || vm.get('objectTypeName')
            ).then(success);
        }

        // bind theObject to add form
        vm.bind(
            {
                bindTo: {
                    cardmodel: '{theObject._model}',
                    objectmodel: '{objectModel}'
                }
            },
            function (data) {
                if (data.cardmodel && data.objectmodel) {
                    function redirectToView() {
                        const url = CMDBuildUI.util.Navigation.getClassBaseUrl(
                            view.getObjectTypeName() || vm.get('objectTypeName'),
                            view.getObjectId() || vm.get('objectId'),
                            'view'
                        );

                        Ext.asap(function () {
                            me.redirectTo(url);
                        });
                    }
                    if (data.cardmodel[CMDBuildUI.model.base.Base.permissions.edit]) {
                        vm.get('theObject')
                            .addLock()
                            .then(function (success) {
                                if (success) {
                                    me._isLocked = true;
                                    view.setHtml();

                                    // add fields
                                    Ext.asap(function () {
                                        view.add(view.getMainPanelForm(view.getDynFormFields()));
                                    });

                                    // validate form before edit
                                    Ext.asap(function () {
                                        view.isValid();
                                    });
                                } else {
                                    redirectToView();
                                }
                            });
                    } else {
                        redirectToView();
                    }
                }
            }
        );

        this.initBeforeEditFormTriggers();
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        const me = this;
        const form = this.getView();
        const saveAndCloseBtn = form.down('#saveandclosebtn');
        const cancelBtn = form.down('#cancelbtn');

        button.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([button, saveAndCloseBtn, cancelBtn]);

        // save data
        me.saveForm({
            failure: function () {
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                CMDBuildUI.util.Utilities.enableFormButtons([button, saveAndCloseBtn, cancelBtn]);
            }
        })
            .then(function (record) {
                if (form.getRedirectAfterSave()) {
                    const url = CMDBuildUI.util.Navigation.getClassBaseUrl(record.get('_type'), record.getId(), 'view');
                    me.redirectTo(url);
                }
            })
            .otherwise(function () {
                CMDBuildUI.util.Utilities.enableFormButtons([button, saveAndCloseBtn, cancelBtn]);
            });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveAndCloseBtnClick: function (button, e, eOpts) {
        const me = this;
        const form = this.getView();
        const saveBtn = form.down('#savebtn');
        const cancelBtn = form.down('#cancelbtn');

        button.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([button, saveBtn, cancelBtn]);

        // save data
        me.saveForm({
            failure: function () {
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                CMDBuildUI.util.Utilities.enableFormButtons([button, saveBtn, cancelBtn]);
            }
        })
            .then(function (record) {
                if (form.getRedirectAfterSave()) {
                    // close detail window
                    CMDBuildUI.util.Navigation.removeManagementDetailsWindow();

                    const url = CMDBuildUI.util.Navigation.getClassBaseUrl(record.get('_type'), record.getId());
                    me.redirectTo(url);
                }
            })
            .otherwise(function () {
                CMDBuildUI.util.Utilities.enableFormButtons([button, saveBtn, cancelBtn]);
            });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.getView()
            .cancelChanges()
            .then(function (success) {
                const vm = button.lookupViewModel();
                // discard changes
                vm.get('theObject').reject();
                // close detail window
                CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
            });
    },

    /**
     * Unlock card on management details window close.
     * @param {CMDBuildUI.view.classes.cards.card.Edit} view
     * @param {Object} eOpts
     */
    onBeforeDestroy: function (view, eOpts) {
        if (this._isLocked) {
            this.getViewModel().get('theObject').removeLock();
        }
    },

    privates: {
        /**
         * Initialize before create form triggers.
         */
        initBeforeEditFormTriggers: function () {
            this.getView().initBeforeActionFormTriggers(
                CMDBuildUI.util.helper.FormHelper.formtriggeractions.beforeEdit,
                CMDBuildUI.util.api.Client.getApiForFormBeforeEdit()
            );
        },

        /**
         * Execute after create form triggers.
         *
         * @param {CMDBuildUI.model.classes.Card} record
         * @param {Object} originalData
         */
        executeAfterEditFormTriggers: function (record, originalData) {
            record.oldData = originalData;
            if (this.getView()) {
                this.getView().executeAfterActionFormTriggers(
                    CMDBuildUI.util.helper.FormHelper.formtriggeractions.afterEdit,
                    record,
                    CMDBuildUI.util.api.Client.getApiForFormAfterEdit()
                );
            }
        },

        /**
         * Save data
         * @param {function} callback
         * @return {Ext.promise.Promise}
         */
        saveForm: function (callback) {
            const deferred = new Ext.Deferred();
            const me = this;
            const form = this.getView();
            const vm = form.lookupViewModel();

            if (form.isValid()) {
                const theObject = vm.get('theObject');
                const originalData = theObject.getOriginalDataForChangedFields();

                if (!vm.get('classObject.noteInline')) {
                    delete theObject.data.Notes; //resolves issue #1982
                }

                function afterSave(record) {
                    // execute after create form triggers
                    me.executeAfterEditFormTriggers(record, originalData);

                    // fire global card update event
                    if (form.getFireGlobalEventsAfterSave()) {
                        Ext.GlobalEvents.fireEventArgs('cardupdated', [record]);
                    }

                    // execute on after save function
                    if (form.getOnAfterSave()) {
                        Ext.callback(form.getOnAfterSave(), null, [record]);
                    }

                    // resolve promise
                    if (
                        CMDBuildUI.util.Navigation.getCurrentContext().objectType ===
                        CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent
                    ) {
                        CMDBuildUI.util.Navigation.refreshNavigationTree(record);
                    }
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    deferred.resolve(record);

                    // execute callback
                    if (Ext.isFunction(callback)) {
                        CMDBuildUI.util.Logger.log(
                            'callback on saveForm is deprecated',
                            CMDBuildUI.util.Logger.levels.warn
                        );
                        Ext.callback(callback, me, [record]);
                    }
                }

                /**
                 * Used to reject the deferred and end saving form status
                 */
                function rejectDeferred() {
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    deferred.reject();
                }

                // save the object
                CMDBuildUI.util.helper.FormHelper.startSavingForm();
                form.saveObject(callback).then(
                    function (record) {
                        // save sequences
                        const sequences = theObject.sequences();
                        sequences.setProxy({
                            type: 'baseproxy',
                            url: '/calendar/sequences',
                            batchOrder: 'destroy,create,update'
                        });

                        //makes the delete operation
                        const deleeteoperation = [];
                        Ext.Array.forEach(
                            sequences.getRemovedRecords(),
                            function (item, index, array) {
                                const seqdeff = new Ext.Deferred();
                                item.erase({
                                    success: function (record, operation) {
                                        seqdeff.resolve();
                                    },
                                    failure: function (record, operation) {
                                        seqdeff.reject();
                                    }
                                });

                                deleeteoperation.push(seqdeff.promise);
                            },
                            this
                        );

                        //makes the create update operation
                        Ext.Deferred.all(deleeteoperation).then(
                            function () {
                                const updateoperation = [];
                                Ext.Array.forEach(
                                    sequences.getModifiedRecords(),
                                    function (item, index, array) {
                                        const seqdeff = new Ext.Deferred();
                                        item.save({
                                            success: function (record, operation) {
                                                seqdeff.resolve();
                                            },
                                            failure: function (record, operation) {
                                                seqdeff.reject();
                                            }
                                        });
                                        updateoperation.push(seqdeff.promise);
                                    },
                                    this
                                );

                                //callback
                                Ext.Deferred.all(updateoperation).then(
                                    function () {
                                        afterSave.call(this, record);
                                    },
                                    function () {
                                        rejectDeferred();
                                    },
                                    Ext.emptyFn,
                                    this
                                );
                            },
                            function () {
                                rejectDeferred();
                            },
                            Ext.emptyFn,
                            this
                        );
                    },
                    function () {
                        rejectDeferred();
                    },
                    Ext.emptyFn,
                    this
                );
            } else {
                rejectDeferred();
            }

            return deferred.promise;
        }
    }
});
