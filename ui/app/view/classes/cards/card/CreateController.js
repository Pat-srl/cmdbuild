Ext.define('CMDBuildUI.view.classes.cards.card.CreateController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.classes-cards-card-create',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
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
     * @param {CMDBuildUI.view.classes.cards.card.Create} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var me = this;
        var isCloneAction = view.getCloneObject() && vm.get("objectId") ? true : false;

        if (!(vm.get("theObject") && vm.get("objectModel"))) {
            var objectTypeName = vm.get("objectTypeName");
            // get model
            CMDBuildUI.util.helper.ModelHelper
                .getModel('class', objectTypeName)
                .then(function (model) {
                    vm.set("objectModel", model);

                    // create new instance
                    vm.linkTo('theObject', {
                        type: model.getName(),
                        create: Ext.merge({ _type: objectTypeName }, view.getDefaultValuesForCreate())
                    });
                });
        }

        vm.bind({
            bindTo: {
                theObject: '{theObject}',
                objectModel: '{objectModel}'
            }
        }, function (params) {
            if (isCloneAction) {
                params.objectModel.load(vm.get("objectId"), {
                    callback: function (record, operation, success) {
                        if (success) {
                            data = record.getCleanData();
                            delete data._id;
                            for (key in data) {
                                params.theObject.set(key, data[key]);
                            }
                        }
                        Ext.asap(function () {
                            me.linkObject(view);
                            me.initBeforeCloneFormTriggers();
                        });
                    }
                });
            } else {
                Ext.asap(function () {
                    me.linkObject(view);
                    me.initBeforeCreateFormTriggers();
                });
            }
        });
    },

    /**
     * Save button click
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this,
            form = this.getView(),
            saveAndCloseBtn = form.down("#saveandclosebtn"),
            cancelBtn = form.down("#cancelbtn");

        button.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([button, saveAndCloseBtn, cancelBtn]);

        // save data
        me.saveForm({
            failure: function () {
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                CMDBuildUI.util.Utilities.enableFormButtons([button, saveAndCloseBtn, cancelBtn]);
            }
        }).then(function (record) {
            if (form.getRedirectAfterSave()) {
                var url = CMDBuildUI.util.Navigation.getClassBaseUrl(
                    record.get("_type"),
                    record.getId(),
                    'view');
                me.redirectTo(url);
            }
        }).otherwise(function () {
            CMDBuildUI.util.Utilities.enableFormButtons([button, saveAndCloseBtn, cancelBtn]);
        });
    },

    /**
     * Save and close button click
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveAndCloseBtnClick: function (button, e, eOpts) {
        var me = this,
            form = this.getView(),
            saveBtn = form.down("#savebtn"),
            cancelBtn = form.down("#cancelbtn");

        button.showSpinner = true;
        CMDBuildUI.util.Utilities.disableFormButtons([button, saveBtn, cancelBtn]);

        // save data
        me.saveForm({
            failure: function () {
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
                CMDBuildUI.util.Utilities.enableFormButtons([button, saveBtn, cancelBtn]);
            }
        }).then(function (record) {
            if (form.getRedirectAfterSave()) {
                var url = CMDBuildUI.util.Navigation.getClassBaseUrl(
                    record.get("_type"),
                    record.getId());
                me.redirectTo(url);
            }
        }).otherwise(function () {
            CMDBuildUI.util.Utilities.enableFormButtons([button, saveBtn, cancelBtn]);
        });
    },

    /**
     * Close creation window
     */
    onCancelBtnClick: function () {
        this.getView().cancelChanges().then(function (success) {
            // close detail window
            CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
        });
    },

    privates: {
        linkObject: function (view) {
            view.setHtml();
            // get form fields
            view.add(view.getMainPanelForm(view.getDynFormFields()));

            // validate form before edit
            Ext.asap(function () {
                view.isValid();
            });
        },

        /**
         * Initialize before create form triggers.
         */
        initBeforeCreateFormTriggers: function () {
            this.getView().initBeforeActionFormTriggers(
                CMDBuildUI.util.helper.FormHelper.formtriggeractions.beforeInsert,
                CMDBuildUI.util.api.Client.getApiForFormBeforeCreate()
            );
        },
        /**
         * Initialize before create form triggers.
         */
        initBeforeCloneFormTriggers: function () {
            this.getView().initBeforeActionFormTriggers(
                CMDBuildUI.util.helper.FormHelper.formtriggeractions.beforeClone,
                CMDBuildUI.util.api.Client.getApiForFormBeforeClone()
            );
        },

        /**
         * Execute after create form triggers.
         *
         * @param {CMDBuildUI.model.classes.Card} record
         */
        executeAfterCreateFormTriggers: function (record) {
            this.getView().executeAfterActionFormTriggers(
                CMDBuildUI.util.helper.FormHelper.formtriggeractions.afterInsert,
                record,
                CMDBuildUI.util.api.Client.getApiForFormAfterCreate()
            );
        },

        /**
         * Execute after create form triggers.
         *
         * @param {CMDBuildUI.model.classes.Card} record
         */
        executeAfterCloneFormTriggers: function (record) {
            this.getView().executeAfterActionFormTriggers(
                CMDBuildUI.util.helper.FormHelper.formtriggeractions.afterClone,
                record,
                CMDBuildUI.util.api.Client.getApiForFormAfterClone()
            );
        },

        /**
         * Save data
         * @param {function} callback
         * @return {Ext.promise.Promise}
         */
        saveForm: function (callback) {
            var deferred = new Ext.Deferred();

            var me = this,
                form = this.getView();

            if (form.isValid()) {
                /**
                 *
                 * @param {CMDBuildUI.model.classes.Card} record
                 * @param {Ext.data.operation.Write} operation
                 */
                function afterSave(record, operation) {
                    if (!record.getRecordType()) {
                        record.set('_type', form.getObjectTypeName());
                    }

                    if (form.getCloneObject()) {
                        me.executeAfterCloneFormTriggers(record);
                    } else {
                        // execute after create form triggers
                        me.executeAfterCreateFormTriggers(record);
                    }

                    // fire global card created event
                    if (form.getFireGlobalEventsAfterSave()) {
                        Ext.GlobalEvents.fireEventArgs("cardcreated", [record]);
                    }

                    // execute on after save function
                    if (form.getOnAfterSave()) {
                        Ext.callback(form.getOnAfterSave(), null, [record]);
                    }

                    // resolve promise
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    deferred.resolve(record);

                    // Deprecated: execute callback
                    if (Ext.isFunction(callback)) {
                        CMDBuildUI.util.Logger.log("callback on saveForm is deprecated", CMDBuildUI.util.Logger.levels.warn);
                        Ext.callback(callback, me, [record]);
                    }
                }

                /**
                 * Used to reject the deferrend and end saving form status
                 */
                function rejectDeferred() {
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                    deferred.reject();
                }

                // save the object
                CMDBuildUI.util.helper.FormHelper.startSavingForm();
                form.saveObject(callback).then(function (record) {
                    // save sequences
                    var sequences = record.sequences();
                    sequences.setProxy({
                        type: 'baseproxy',
                        url: '/calendar/sequences'
                    });

                    //sets the 'card' field in the schedules rule
                    sequences.getRange().forEach(function (sequence) {
                        sequence.set('card', record.getId());
                    });

                    //makes the delete operation
                    var deleeteoperation = [];
                    Ext.Array.forEach(sequences.getRemovedRecords(), function (item, index, array) {
                        var seqdeff = new Ext.Deferred();
                        item.erase({
                            success: function (record, operation) {
                                seqdeff.resolve();
                            },
                            failure: function (record, operation) {
                                seqdeff.reject();
                            }
                        });

                        deleeteoperation.push(seqdeff.promise)
                    }, this);

                    //makes the create update operation
                    Ext.Deferred.all(deleeteoperation).then(function () {

                        var updateoperation = [];
                        Ext.Array.forEach(sequences.getModifiedRecords(), function (item, index, array) {
                            var seqdeff = new Ext.Deferred();
                            item.save({
                                success: function (record, operation) {
                                    seqdeff.resolve();
                                },
                                failure: function (record, operation) {
                                    seqdeff.reject();
                                }
                            });
                            updateoperation.push(seqdeff.promise)
                        }, this);

                        //callback
                        Ext.Deferred.all(updateoperation).then(function () {
                            afterSave.call(this, record);
                        }, function () {
                            rejectDeferred();
                        }, Ext.emptyFn, this);
                    }, function () {
                        rejectDeferred();
                    }, Ext.emptyFn, this);
                }, function () {
                    rejectDeferred();
                }, Ext.emptyFn, this)
            } else {
                rejectDeferred();
            }

            return deferred.promise;
        }
    }
});
