Ext.define('CMDBuildUI.model.dms.DMSAttachment', {
    extend: 'CMDBuildUI.model.base.Base',
    statics: {

    },
    fields: [{
        name: '_category_name',
        type: 'string'
    }, {
        name: '_category_description',
        type: 'string'
    }, {
        name: '_category_description_translation',
        type: 'string'
    }],

    proxy: {
        type: 'baseproxy'
    },

    /**
     * Couldn't use mixin lock because the attachments doesn't use the same logics
     */

    /**
     * Override load method to add "includeModel" parameter in request.
     *
     * @param {Object} [options] Options to pass to the proxy.
     *
     * @return {Ext.data.operation.Read} The read operation.
     */
    load: function (options) {
        options = Ext.apply(options || {}, {
            params: {
                includeWidgets: true
            }
        });
        this.callParent([options]);
    },

    /**
     * Check lock on item.
     * 
     * @return {Ext.Deferred}
     */
    isLocked: function (objectType) {
        var deferred = new Ext.Deferred();
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.cardlock.enabled)) {
            var me = this;
            Ext.Ajax.request({
                url: Ext.String.format("{0}/classes/{1}/cards/{2}/lock", CMDBuildUI.util.Config.baseUrl, CMDBuildUI.model.dms.DMSModel.masterParentClass, me.get('_card')),
                method: 'GET',
                success: function (response) {
                    var res = JSON.parse(response.responseText);
                    deferred.resolve(res);
                },
                error: function (response) {
                    deferred.resolve(false);
                }
            });
        } else {
            deferred.resolve(false);
        }
        return deferred.promise;
    },

    /**
     * Check lock on item.
     * 
     * @return {Ext.Deferred}
     */
    addLock: function () {
        var deferred = new Ext.Deferred();
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.cardlock.enabled)) {
            var me = this;
            Ext.Ajax.request({
                url: Ext.String.format("{0}/classes/{1}/cards/{2}/lock", CMDBuildUI.util.Config.baseUrl, CMDBuildUI.model.dms.DMSModel.masterParentClass, me.get('_card')),
                method: 'POST',
                success: function (response) {
                    var res = JSON.parse(response.responseText);
                    if (res.success) {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format("Card {0}-{1} locked.", me.get('_type'), me.getId()),
                            CMDBuildUI.util.Logger.levels.debug,
                            null,
                            res.data
                        );
                        deferred.resolve(res.success);
                    } else {
                        this.showLockMessage(res.user);
                        deferred.resolve(res.success);
                    }
                },
                error: function (response) {
                    deferred.resolve(true);
                }
            });
        } else {
            deferred.resolve(true);
        }
        return deferred.promise;
    },

    /**
     * Remove lock from item.
     * 
     * @return {Ext.Deferred}
     */
    removeLock: function () {
        var deferred = new Ext.Deferred();
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.cardlock.enabled)) {
            var me = this;
            Ext.Ajax.request({
                url: Ext.String.format("{0}/classes/{1}/cards/{2}/lock", CMDBuildUI.util.Config.baseUrl, CMDBuildUI.model.dms.DMSModel.masterParentClass, me.get('_card')),
                method: 'DELETE',
                success: function (response) {
                    var res = JSON.parse(response.responseText);
                    CMDBuildUI.util.Logger.log(
                        Ext.String.format("Card {0}-{1} unlocked.", me.get("_type"), me.getId()),
                        CMDBuildUI.util.Logger.levels.debug,
                        null,
                        res.data
                    );
                    deferred.resolve(res.success);
                }
            });
        } else {
            deferred.resolve(true);
        }
        return deferred.promise;
    },

    showLockMessage: function (userValue) {
        var user = CMDBuildUI.locales.Locales.main.cardlock.someone;
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.cardlock.showuser) && userValue) {
            user = userValue;
        }
        CMDBuildUI.util.Notifier.showWarningMessage(
            Ext.String.format(CMDBuildUI.locales.Locales.main.cardlock.lockedmessage, user)
        );
    },

    /**
     * 
     * @param {File} file 
     * @param {Object} attachmentOwner
     * @param {String} attachmentOwner.type
     * @param {String} attachmentOwner.typeName
     * @param {Numeric} attachmentOwner.id
     */
    saveAttachmentAndSequences: function (file, attachmentOwner) {
        var deferred = new Ext.Deferred();

        var me = this,
            url = this.getProxy().getUrl(),
            method, apis, triggersAction;

        // calculate variables
        if (me.phantom) {
            method = "POST";
            apis = CMDBuildUI.util.api.Client.getApiForFormAfterCreate();
            triggersAction = CMDBuildUI.util.helper.FormHelper.formtriggeractions.afterInsert;
        } else if (me.dirty) {
            method = "PUT";
            url += "/" + me.getId();
            apis = CMDBuildUI.util.api.Client.getApiForFormAfterEdit();
            triggersAction = CMDBuildUI.util.helper.FormHelper.formtriggeractions.afterEdit;
        } else {
            deferred.resolve(true);
            return deferred;
        }

        // add attachemnt owner to apis
        apis._attachmentOwner = attachmentOwner;

        // upload file
        CMDBuildUI.util.File.uploadFileWithMetadata(
            method,
            url,
            file,
            me.getMetadataForSave()
        ).then(function (response) {
            // Add server response data to model and commit changes so that the model is not dirty/phantom
            me.set(response);
            me.commit();
            // execute after action triggers
            me.executeAfterActionFormTriggers(triggersAction, apis);
            // // sync sequences if needed and then resolve the promise
            if (me.sequences && Ext.isFunction(me.sequences) && me.sequences().needsSync) {

                // update card id on sequences
                me.sequences().getRange().forEach(function (item, index, array) {
                    item.set("card", response._card);
                }, this);

                //makes the delete operation
                var deleeteoperation = [];
                Ext.Array.forEach(me.sequences().getRemovedRecords(), function (item, index, array) {
                    var deferred = new Ext.Deferred();
                    item.erase({
                        success: function (record, operation) {
                            deferred.resolve();
                        },
                        failure: function (record, operation) {
                            deferred.reject();
                        }
                    });

                    deleeteoperation.push(deferred.promise)
                }, this);
                //makes the create update operation
                Ext.Deferred.all(deleeteoperation).then(function () {
                    var updateoperation = [];
                    Ext.Array.forEach(me.sequences().getModifiedRecords(), function (item, index, array) {
                        var deferred = new Ext.Deferred();
                        item.save({
                            success: function (record, operation) {
                                deferred.resolve();
                            },
                            failure: function (record, operation) {
                                deferred.reject();
                            }
                        });
                        updateoperation.push(deferred.promise)
                    }, this);
                    Ext.Deferred.all(updateoperation).then(function () {
                        deferred.resolve(true);
                    }, Ext.emptyFn, Ext.emptyFn, this);
                }, Ext.emptyFn, Ext.emptyFn, this);
            } else {
                deferred.resolve(true);
            }
        }, function (err) {
            deferred.reject(err);
        }, Ext.emptyFn, this)

        return deferred;
    },

    privates: {
        /**
         * 
         * @param {Object} metadata 
         * @return {Object} metadata without attributes which starts whit "_"
         */
        getMetadataForSave: function () {
            var data = this.getData();
            for (key in data) {
                if (Ext.String.startsWith(key, "_")) {
                    delete data[key];
                }
            }
            return data;
        },

        /**
         * 
         * @param {String} action 
         * @param {Object} base_api 
         */
        executeAfterActionFormTriggers: function (action, base_api) {
            var type = this.entityName.split(".").pop(),
                dmsmodel = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(type);
            if (dmsmodel) {
                // get form triggers
                var triggers = dmsmodel.getFormTriggersForAction(action);
                if (triggers && triggers.length) {
                    var api = Ext.apply({
                        record: this
                    }, base_api);
                    this.executeFormTriggers(triggers, api);
                }
            }
        },

        /**
         * 
         * @param {String[]} triggers 
         * @param {Object} api 
         */
        executeFormTriggers: function (triggers, api) {
            // execute form triggers
            triggers.forEach(function (triggerfunction) {
                var executeFormTrigger;
                var jsfn = Ext.String.format(
                    'executeFormTrigger = function(api) {\n{0}\n}',
                    triggerfunction
                );
                try {
                    eval(jsfn);
                } catch (e) {
                    CMDBuildUI.util.Logger.log(
                        "Error on trigger function.",
                        CMDBuildUI.util.Logger.levels.error,
                        null,
                        e
                    );
                    executeFormTrigger = Ext.emptyFn;
                }
                // use try / catch to manage errors
                try {
                    executeFormTrigger(api);
                } catch (e) {
                    CMDBuildUI.util.Logger.log(
                        "Error on execution of form trigger.",
                        CMDBuildUI.util.Logger.levels.error,
                        null,
                        {
                            fn: triggerfunction
                        }
                    );
                }
            });
        }
    }
});