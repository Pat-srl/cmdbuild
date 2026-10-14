/**
 * @file CMDBuildUI.util.api.Client
 * @module CMDBuildUI.util.api.Client
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.api.Client', {
    singleton: true,

    /**
     * @private
     *
     * @property {Ext.data.Model} record
     */
    record: null,

    /********** API GETTERS **********/

    /**
     * Api available for field visibility scripts.
     *
     * @private
     *
     * @returns {Object}
     */
    getApiForFieldVisibility: function () {
        return {
            getValue: this.getValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            getReferenceDescription: this.getReferenceDescription,
            getReferenceCode: this.getReferenceCode,
            getLinkUrl: this.getLinkUrl,
            getLinkLabel: this.getLinkLabel,
            testRegExp: this.testRegExp,
            isDesktop: this.isDesktop,
            isMobile: this.isMobile
        };
    },

    /**
     * Api available for field custom validator scripts.
     *
     * @private
     *
     * @returns {Object}
     */
    getApiForFieldCustomValidator: function () {
        return {
            getValue: this.getValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            getReferenceDescription: this.getReferenceDescription,
            getReferenceCode: this.getReferenceCode,
            getLinkUrl: this.getLinkUrl,
            getLinkLabel: this.getLinkLabel,
            testRegExp: this.testRegExp,
            isDesktop: this.isDesktop,
            isMobile: this.isMobile
        };
    },

    /**
     * Api available for field custom validator scripts.
     *
     * @private
     *
     * @returns {Object}
     */
    getApiForFormAutoValue: function () {
        return {
            getValue: this.getValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            getReferenceDescription: this.getReferenceDescription,
            getReferenceCode: this.getReferenceCode,
            getLinkUrl: this.getLinkUrl,
            getLinkLabel: this.getLinkLabel,
            getRemoteCard: this.getRemoteCard,
            getRemoteProcessInstance: this.getRemoteProcessInstance,
            getRemoteLookupFromCode: this.getRemoteLookupFromCode,
            getFunctionOutputs: this.getFunctionOutputs,
            isDesktop: this.isDesktop,
            isMobile: this.isMobile,
            setValue: this.setValue,
            setHTMLValue: this.setHTMLValue,
            setReferenceValue: this.setReferenceValue,
            setReferenceValueFromRecord: this.setReferenceValueFromRecord,
            setLookupValue: this.setLookupValue,
            setLookupValueFromRecord: this.setLookupValueFromRecord,
            setLinkValue: this.setLinkValue
        };
    },

    /**
     * Api available for field custom validator scripts.
     *
     * @private
     *
     * @returns {Object}
     */
    getApiForFieldAutoValue: function () {
        return {
            getValue: this.getValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            getReferenceDescription: this.getReferenceDescription,
            getReferenceCode: this.getReferenceCode,
            getLinkUrl: this.getLinkUrl,
            getLinkLabel: this.getLinkLabel,
            getRemoteCard: this.getRemoteCard,
            getRemoteProcessInstance: this.getRemoteProcessInstance,
            getRemoteLookupFromCode: this.getRemoteLookupFromCode,
            getFunctionOutputs: this.getFunctionOutputs,
            isDesktop: this.isDesktop,
            isMobile: this.isMobile,
            // following properties are "private"
            _setValue: this.setValue,
            _setHTMLValue: this.setHTMLValue,
            _setReferenceValue: this.setReferenceValue,
            _setReferenceValueFromRecord: this.setReferenceValueFromRecord,
            _setLookupValue: this.setLookupValue,
            _setLookupValueFromRecord: this.setLookupValueFromRecord,
            _setLinkValue: this.setLinkValue
        };
    },

    /**
     * Api available for form before create scritps.
     *
     * @private
     *
     * @returns {Object}
     */
    getApiForFormBeforeCreate: function () {
        return {
            getValue: this.getValue,
            setValue: this.setValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            setLookupValue: this.setLookupValue,
            setLookupValueFromRecord: this.setLookupValueFromRecord,
            getReferenceDescription: this.getReferenceDescription,
            getReferenceCode: this.getReferenceCode,
            setReferenceValue: this.setReferenceValue,
            setReferenceValueFromRecord: this.setReferenceValueFromRecord,
            getLinkUrl: this.getLinkUrl,
            getLinkLabel: this.getLinkLabel,
            setLinkValue: this.setLinkValue,
            getRemoteCard: this.getRemoteCard,
            getRemoteProcessInstance: this.getRemoteProcessInstance,
            getRemoteLookupFromCode: this.getRemoteLookupFromCode,
            getFunctionOutputs: this.getFunctionOutputs,
            getAttachmentOwner: this.getAttachmentOwner,
            updateRecordOnFormTrigger: this.updateRecordOnFormTrigger,
            isDesktop: this.isDesktop,
            isMobile: this.isMobile
        };
    },

    /**
     * Api available for form after create scritps.
     *
     * @private
     *
     * @returns {Object}
     */
    getApiForFormAfterCreate: function () {
        return {
            getValue: this.getValue,
            setValue: this.setValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            setLookupValue: this.setLookupValue,
            setLookupValueFromRecord: this.setLookupValueFromRecord,
            getReferenceDescription: this.getReferenceDescription,
            getReferenceCode: this.getReferenceCode,
            setReferenceValue: this.setReferenceValue,
            setReferenceValueFromRecord: this.setReferenceValueFromRecord,
            getLinkUrl: this.getLinkUrl,
            getLinkLabel: this.getLinkLabel,
            setLinkValue: this.setLinkValue,
            getRemoteCard: this.getRemoteCard,
            getRemoteProcessInstance: this.getRemoteProcessInstance,
            getRemoteLookupFromCode: this.getRemoteLookupFromCode,
            getFunctionOutputs: this.getFunctionOutputs,
            getAttachmentOwner: this.getAttachmentOwner,
            abortProcess: this.abortProcess,
            saveRecord: this.saveRecord,
            updateRecordOnFormTrigger: this.updateRecordOnFormTrigger,
            goToResource: this.goToResource,
            isDesktop: this.isDesktop,
            isMobile: this.isMobile,
            // following properties are "private"
            _abortProcess: this.abortProcess
        };
    },

    /**
     * Api available for form before clone scritps.
     *
     * @private
     *
     * @returns {Object}
     */
    getApiForFormBeforeClone: function () {
        return {
            getValue: this.getValue,
            setValue: this.setValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            setLookupValue: this.setLookupValue,
            setLookupValueFromRecord: this.setLookupValueFromRecord,
            getReferenceDescription: this.getReferenceDescription,
            getReferenceCode: this.getReferenceCode,
            setReferenceValue: this.setReferenceValue,
            setReferenceValueFromRecord: this.setReferenceValueFromRecord,
            getLinkUrl: this.getLinkUrl,
            getLinkLabel: this.getLinkLabel,
            setLinkValue: this.setLinkValue,
            getRemoteCard: this.getRemoteCard,
            getRemoteProcessInstance: this.getRemoteProcessInstance,
            getRemoteLookupFromCode: this.getRemoteLookupFromCode,
            getFunctionOutputs: this.getFunctionOutputs,
            updateRecordOnFormTrigger: this.updateRecordOnFormTrigger,
            isDesktop: this.isDesktop,
            isMobile: this.isMobile
        };
    },

    /**
     * Api available for form after clone scritps.
     *
     * @private
     *
     * @returns {Object}
     */
    getApiForFormAfterClone: function () {
        return {
            getValue: this.getValue,
            setValue: this.setValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            setLookupValue: this.setLookupValue,
            setLookupValueFromRecord: this.setLookupValueFromRecord,
            getReferenceDescription: this.getReferenceDescription,
            getReferenceCode: this.getReferenceCode,
            setReferenceValue: this.setReferenceValue,
            setReferenceValueFromRecord: this.setReferenceValueFromRecord,
            getLinkUrl: this.getLinkUrl,
            getLinkLabel: this.getLinkLabel,
            setLinkValue: this.setLinkValue,
            getRemoteCard: this.getRemoteCard,
            getRemoteProcessInstance: this.getRemoteProcessInstance,
            getRemoteLookupFromCode: this.getRemoteLookupFromCode,
            getFunctionOutputs: this.getFunctionOutputs,
            saveRecord: this.saveRecord,
            updateRecordOnFormTrigger: this.updateRecordOnFormTrigger,
            goToResource: this.goToResource,
            isDesktop: this.isDesktop,
            isMobile: this.isMobile
        };
    },

    /**
     * Api available for form before edit scritps.
     *
     * @private
     *
     * @returns {Object}
     */
    getApiForFormBeforeEdit: function () {
        return {
            getValue: this.getValue,
            setValue: this.setValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            setLookupValue: this.setLookupValue,
            setLookupValueFromRecord: this.setLookupValueFromRecord,
            getReferenceDescription: this.getReferenceDescription,
            getReferenceCode: this.getReferenceCode,
            setReferenceValue: this.setReferenceValue,
            setReferenceValueFromRecord: this.setReferenceValueFromRecord,
            getLinkUrl: this.getLinkUrl,
            getLinkLabel: this.getLinkLabel,
            setLinkValue: this.setLinkValue,
            getRemoteCard: this.getRemoteCard,
            getRemoteProcessInstance: this.getRemoteProcessInstance,
            getRemoteLookupFromCode: this.getRemoteLookupFromCode,
            getFunctionOutputs: this.getFunctionOutputs,
            getAttachmentOwner: this.getAttachmentOwner,
            updateRecordOnFormTrigger: this.updateRecordOnFormTrigger,
            isDesktop: this.isDesktop,
            isMobile: this.isMobile
        };
    },

    /**
     * Api available for form after edit scritps.
     *
     * @private
     *
     * @returns {Object}
     */
    getApiForFormAfterEdit: function () {
        return {
            getValue: this.getValue,
            setValue: this.setValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            setLookupValue: this.setLookupValue,
            setLookupValueFromRecord: this.setLookupValueFromRecord,
            getReferenceDescription: this.getReferenceDescription,
            getReferenceCode: this.getReferenceCode,
            setReferenceValue: this.setReferenceValue,
            setReferenceValueFromRecord: this.setReferenceValueFromRecord,
            getLinkUrl: this.getLinkUrl,
            getLinkLabel: this.getLinkLabel,
            setLinkValue: this.setLinkValue,
            getRemoteCard: this.getRemoteCard,
            getRemoteProcessInstance: this.getRemoteProcessInstance,
            getRemoteLookupFromCode: this.getRemoteLookupFromCode,
            getFunctionOutputs: this.getFunctionOutputs,
            getAttachmentOwner: this.getAttachmentOwner,
            abortProcess: this.abortProcess,
            saveRecord: this.saveRecord,
            updateRecordOnFormTrigger: this.updateRecordOnFormTrigger,
            goToResource: this.goToResource,
            isDesktop: this.isDesktop,
            isMobile: this.isMobile,
            // following properties are "private"
            _abortProcess: this.abortProcess
        };
    },

    /**
     * Api available for form after delete scritps.
     *
     * @private
     *
     * @returns {Object}
     */
    getApiForFormAfterDelete: function () {
        return {
            getValue: this.getValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            setLookupValue: this.setLookupValue,
            setLookupValueFromRecord: this.setLookupValueFromRecord,
            getReferenceDescription: this.getReferenceDescription,
            getReferenceCode: this.getReferenceCode,
            setReferenceValue: this.setReferenceValue,
            setReferenceValueFromRecord: this.setReferenceValueFromRecord,
            getLinkUrl: this.getLinkUrl,
            getLinkLabel: this.getLinkLabel,
            setLinkValue: this.setLinkValue,
            getRemoteCard: this.getRemoteCard,
            getRemoteProcessInstance: this.getRemoteProcessInstance,
            getRemoteLookupFromCode: this.getRemoteLookupFromCode,
            getFunctionOutputs: this.getFunctionOutputs,
            getAttachmentOwner: this.getAttachmentOwner,
            goToResource: this.goToResource,
            isDesktop: this.isDesktop,
            isMobile: this.isMobile
        };
    },

    /**
     * Api available for form custom validator scripts.
     *
     * @private
     *
     * @returns {Object}
     */
    getApiForFormCustomValidator: function () {
        return {
            getValue: this.getValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            getReferenceDescription: this.getReferenceDescription,
            getReferenceCode: this.getReferenceCode,
            getLinkUrl: this.getLinkUrl,
            getLinkLabel: this.getLinkLabel,
            testRegExp: this.testRegExp,
            isDesktop: this.isDesktop,
            isMobile: this.isMobile
        };
    },

    /**
     * Api available for form after edit scritps.
     *
     * @private
     *
     * @returns {Object}
     */
    getApiForContextMenu: function () {
        return {
            updateRecord: this.updateRecord,
            goToResource: this.goToResource,
            openPopup: this.openPopup,
            makeRequest: this.makeRequest,
            refreshGrid: this.refreshGrid,
            getRemoteCard: this.getRemoteCard,
            getRemoteProcessInstance: this.getRemoteProcessInstance,
            getRemoteLookupFromCode: this.getRemoteLookupFromCode,
            getFunctionOutputs: this.getFunctionOutputs,
            abortProcess: this.abortProcess,
            resumeProcess: this.resumeProcess,
            openReport: this.openReport,
            isDesktop: this.isDesktop,
            isMobile: this.isMobile,
            // following properties are "private"
            _abortProcess: this.abortProcess,
            _resumeProcess: this.resumeProcess
        };
    },

    /**
     * Api available for schedules rule definition.
     *
     * @private
     *
     * @returns {Object}
     */
    getApiForSchedules: function () {
        return {
            getValue: this.getValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            getReferenceDescription: this.getReferenceDescription,
            getReferenceCode: this.getReferenceCode,
            testRegExp: this.testRegExp,
            isDesktop: this.isDesktop,
            isMobile: this.isMobile
        };
    },

    /**
     * Add managed rotues to the application.
     *
     * @param {Object} routes
     * @param {Ext.app.ViewController} controller
     *
     * @example
     * CMDBuildUI.util.api.Client.addRoutes({
     *      '/my/custom/route/:param': {
     *          conditions: {
     *              param: "([0-9]+)"
     *          }
     *          before: function(param, action) {
     *              // do something
     *              action.resume();
     *          }
     *          action: function(param) {
     *              // do something
     *          }
     *      }
     * }, this);
     */
    addRoutes: function (routes, controller) {
        // get controller if not passed
        if (!controller) {
            var mainview = Ext.ComponentQuery.query('viewport');
            if (!Ext.isEmpty(mainview)) {
                controller = mainview[0].getController();
            }
        }

        // add routes to controller, if exist
        if (controller) {
            controller.setRoutes(Ext.merge(controller.getRoutes(), routes));
        } else {
            // add routes to temp variable
            var customroutes = CMDBuildUI.util.Navigation._customroutes || {};
            CMDBuildUI.util.Navigation._customroutes = Ext.merge(routes, customroutes);
        }
    },

    /********** API DEFINITION **********/

    /**
     * Get the value of a form attribute.
     * Used only on UI Scripts.
     *
     * @param {String} attribute
     *
     * @returns {Strin|Numeric|Object}
     */
    getValue: function (attribute) {
        if (this.record) {
            return this.record.get(attribute);
        } else {
            CMDBuildUI.util.Logger.log('Record is not evaluated', CMDBuildUI.util.Logger.levels.warn);
        }
    },

    /**
     * Set the value of a form attribute.
     * Used only on UI Scripts.
     *
     * @param {String} attribute
     * @param {Strin|Numeric|Object} value
     *
     * @returns {Strin|Numeric|Object}
     */
    setValue: function (attribute, value) {
        if (this.record) {
            return this.record.set(attribute, value);
        } else {
            CMDBuildUI.util.Logger.log('Record is not evaluated', CMDBuildUI.util.Logger.levels.warn);
        }
    },

    /**
     * Get the code of a lookup form attribute.
     * Used only on UI Scripts.
     *
     * @param {String} attribute
     *
     * @returns {String}
     */
    getLookupCode: function (attribute) {
        if (this.record) {
            return this.record.get('_' + attribute + '_code');
        } else {
            CMDBuildUI.util.Logger.log('Record is not evaluated', CMDBuildUI.util.Logger.levels.warn);
        }
    },

    /**
     * Get the description of a lookup form attribute.
     * Used only on UI Scripts.
     *
     * @param {String} attribute
     *
     * @returns {Strin}
     */
    getLookupDescription: function (attribute) {
        if (this.record) {
            return this.record.get('_' + attribute + '_description_translation');
        } else {
            CMDBuildUI.util.Logger.log('Record is not evaluated', CMDBuildUI.util.Logger.levels.warn);
        }
    },

    /**
     * Set lookup value, code and description.
     * Used only on UI Scripts.
     *
     * @param {String} attribute
     * @param {Number} value
     * @param {String} code
     * @param {String} description
     */
    setLookupValue: function (attribute, value, code, description) {
        this.record.set(attribute, value);
        this.record.set('_' + attribute + '_code', code);
        this.record.set('_' + attribute + '_description_translation', description);
    },

    /**
     * Set lookup value, code and description from lookup value.
     * Used only on UI Scripts.
     *
     * @param {String} attribute
     * @param {CMDBuildUI.model.lookups.Lookup} record
     */
    setLookupValueFromRecord: function (attribute, record) {
        this.record.set(attribute, record.get('_id'));
        this.record.set('_' + attribute + '_code', record.get('code'));
        this.record.set('_' + attribute + '_description_translation', record.get('_description_translation'));
    },

    /**
     * Get the description of a reference form attribute.
     * Used only on UI Scripts.
     *
     * @param {String} attribute
     *
     * @returns {String}
     */
    getReferenceDescription: function (attribute) {
        if (this.record) {
            return this.record.get('_' + attribute + '_description');
        } else {
            CMDBuildUI.util.Logger.log('Record is not evaluated', CMDBuildUI.util.Logger.levels.warn);
        }
    },

    /**
     * Get the code of a reference form attribute.
     * Used only on UI Scripts.
     *
     * @param {String} attribute
     *
     * @returns {String}
     */
    getReferenceCode: function (attribute) {
        if (this.record) {
            return this.record.get('_' + attribute + '_code');
        } else {
            CMDBuildUI.util.Logger.log('Record is not evaluated', CMDBuildUI.util.Logger.levels.warn);
        }
    },

    /**
     * Set reference value, description and code.
     * Used only on UI Scripts.
     *
     * @param {String} attribute
     * @param {Number} value
     * @param {String} description
     * @param {String} code
     */
    setReferenceValue: function (attribute, value, description, code) {
        this.record.set(attribute, value);
        this.record.set('_' + attribute + '_description', description);
        this.record.set('_' + attribute + '_code', code);
    },

    /**
     * Set reference value, description and code from card or process instance.
     * Used only on UI Scripts.
     *
     * @param {String} attribute
     * @param {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} record
     */
    setReferenceValueFromRecord: function (attribute, record) {
        this.record.set(attribute, record.get('_id'));
        this.record.set('_' + attribute + '_description', record.get('Description'));
        this.record.set('_' + attribute + '_code', record.get('Code'));
    },

    /**
     * Get the link url.
     *
     * @param {String} attribute Link attribute name.
     *
     * @returns {String}
     */
    getLinkUrl: function (attribute) {
        if (this.record) {
            return this.record.get(CMDBuildUI.util.helper.ModelHelper.getUrlFieldNameForLinkField(attribute));
        } else {
            CMDBuildUI.util.Logger.log('Record is not evaluated', CMDBuildUI.util.Logger.levels.warn);
        }
    },

    /**
     * Get the link label.
     *
     * @param {String} attribute Link attribute name.
     *
     * @returns {String}
     */
    getLinkLabel: function (attribute) {
        if (this.record) {
            return this.record.get(CMDBuildUI.util.helper.ModelHelper.getLabelFieldNameForLinkField(attribute));
        } else {
            CMDBuildUI.util.Logger.log('Record is not evaluated', CMDBuildUI.util.Logger.levels.warn);
        }
    },

    /**
     * Set the link value from url and label.
     *
     * @param {String} attribute Link attribute name.
     * @param {String} url Link url value.
     * @param {String} label Link label value.
     */
    setLinkValue: function (attribute, url, label) {
        if (this.record) {
            this.record.set(CMDBuildUI.util.helper.ModelHelper.getUrlFieldNameForLinkField(attribute), url);
            this.record.set(CMDBuildUI.util.helper.ModelHelper.getLabelFieldNameForLinkField(attribute), label);
            this.record.set(attribute, CMDBuildUI.util.Utilities.createLinkByUrlAndLabel(url, label));
        } else {
            CMDBuildUI.util.Logger.log('Record is not evaluated', CMDBuildUI.util.Logger.levels.warn);
        }
    },

    /**
     * Set the HTML value for an attribute.
     *
     * @param {String} attribute HTML attribute name.
     * @param {String} value HTML string value.
     */
    setHTMLValue: function (attribute, value) {
        this.record.set(attribute, value);
        this.record.set('_' + attribute + '_html', value);
    },

    /**
     * Abort process record
     *
     * @param {CMDBuildUI.model.processes.Instance} record
     * @returns {Ext.promise.Promise}
     */
    abortProcess: function (record) {
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.helper.FormHelper.startSavingForm();
        CMDBuildUI.util.Ajax.setActionId(CMDBuildUI.util.Ajax.processStatAbort);
        record.erase({
            success: function (record, operation) {
                // fire global card deleted event
                Ext.GlobalEvents.fireEventArgs('processinstanceaborted', [record]);
                deferred.resolve();
            },
            failure: function (record, operation) {
                deferred.reject();
            },
            callback: function (record, operation, success) {
                CMDBuildUI.util.Ajax.setActionId();
                CMDBuildUI.util.helper.FormHelper.endSavingForm();
            }
        });
        return deferred.promise;
    },

    /**
     * Resume process record
     *
     * @param {CMDBuildUI.model.processes.Instance} record
     * @returns {Ext.promise.Promise}
     */
    resumeProcess: function (record) {
        if (record.get('_FlowStatus_code') === 'open.not_running.suspended') {
            CMDBuildUI.util.helper.FormHelper.startSavingForm();
            var deferred = new Ext.Deferred();
            Ext.Ajax.request({
                url: Ext.String.format(
                    '{0}{1}',
                    CMDBuildUI.util.Config.baseUrl,
                    CMDBuildUI.util.api.Processes.getInstanceResumeUrl(record.get('_type'), record.getId())
                ),
                method: 'POST',
                success: function (response) {
                    Ext.GlobalEvents.fireEventArgs('processinstanceresume');
                    deferred.resolve();
                },
                callback: function () {
                    CMDBuildUI.util.helper.FormHelper.endSavingForm();
                }
            });
            return deferred.promise;
        } else {
            CMDBuildUI.util.Notifier.showWarningMessage(CMDBuildUI.locales.Locales.processes.resumeprocesses);
        }
    },

    /**
     * Test a regular expression on value.
     *
     * @param {RegExp} regex
     * @param {String} value
     *
     * @returns {Boolean} `true` if the value match the regular expression.
     *
     * @example
     * CMDBuildUI.util.api.Client.testRegExp(/^{(client|server)+:*.+}$/, variable);
     */
    testRegExp: function (regexp, value) {
        if (!regexp || !(regexp instanceof RegExp)) {
            CMDBuildUI.util.Logger.log('RegExp not valid!', CMDBuildUI.util.Logger.levels.error, null, {
                regexp: regexp
            });
        }
        return regexp.test(value);
    },

    /**
     * Open a resource using routing.
     * Used only on UI Scripts.
     *
     * @param {String} path
     */
    goToResource: function (path) {
        if (path) {
            CMDBuildUI.util.Utilities.redirectTo(path, true);
        } else {
            CMDBuildUI.util.Logger.log('No path defined', CMDBuildUI.util.Logger.levels.warn);
        }
    },

    /**
     * Update a record on form trigger with given data.
     *
     * @param {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} record
     * @param {Object} data
     * @param {Function} callback
     */
    updateRecordOnFormTrigger: function (record, data, callback) {
        data = data || {};
        if (record) {
            record.set(data);
            var params = {};
            if (callback && Ext.isFunction(callback)) {
                params.callback = callback;
            }
            record.save(params);
        } else {
            CMDBuildUI.util.Logger.log('Record is not evaluated', CMDBuildUI.util.Logger.levels.warn);
        }
    },

    /**
     * Update a record with given data.
     *
     * @param {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} record
     * @param {Object} data
     * @param {Function} callback
     */
    updateRecord: function (record, data, callback) {
        data = data || {};
        if (record) {
            for (var key in data) {
                if (
                    data.hasOwnProperty(key) &&
                    (record.getField(key) || key === '_advance' || key === '_activity_id')
                ) {
                    record.set(key, data[key]);
                } else {
                    CMDBuildUI.util.Logger.log(
                        Ext.String.format('Object has not {0} field.', key),
                        CMDBuildUI.util.Logger.levels.warn
                    );
                }
            }
            var params = {};
            if (callback && Ext.isFunction(callback)) {
                params.callback = callback;
            }
            record.save(params);
        } else {
            CMDBuildUI.util.Logger.log('Record is not evaluated', CMDBuildUI.util.Logger.levels.warn);
        }
    },

    /**
     * This callback is displayed as a global member.
     * @callback saveRecordCallback
     * @param {Ext.data.Model[]} callback.records Array of records.
     * @param {Ext.data.operation.Operation} callback.operation The Operation itself.
     * @param {Boolean} callback.success True when operation completed successfully.
     */
    /**
     * Save record.
     * Used only on UI Scripts.
     *
     * @param {saveRecordCallback} callback Function to execute when operation completed.
     */
    saveRecord: function (callback) {
        if (this.record) {
            return this.record.save({
                callback: callback
            });
        } else {
            CMDBuildUI.util.Logger.log('Record is not evaluated', CMDBuildUI.util.Logger.levels.warn);
        }
    },

    /**
     * Refresh grid.
     * Used only on UI Scripts.
     */
    refreshGrid: function () {
        this._grid.getStore().load();
    },

    /**
     * @private
     *
     * @param {String} text
     */
    showMessage: function (text) {
        //TODO: implement
    },

    /**
     * @private
     */
    getCurrentActivityCode: function () {
        //TODO: implement
    },

    /**
     * @private
     */
    openPopup: function () {
        //TODO: implement
    },

    /**
     * @private
     */
    makeRequest: function () {
        //TODO: implement
    },

    /**
     * Get card from the server.
     *
     * @param {String} className
     * @param {Numeric} cardId
     *
     * @returns {Ext.promise.Promise<CMDBuildUI.model.classes.Card>}
     */
    getRemoteCard: function (className, cardId) {
        var deferred = new Ext.Deferred();

        if (cardId) {
            // get model
            CMDBuildUI.util.helper.ModelHelper.getModel(
                CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                className
            ).then(function (model) {
                if (model) {
                    model.load(cardId, {
                        success: function (record, operation) {
                            deferred.resolve(record);
                        },
                        failure: function (record, operation) {
                            CMDBuildUI.util.Logger.log(
                                'Card not found for class ' + className,
                                CMDBuildUI.util.Logger.levels.warn
                            );
                            deferred.reject();
                        }
                    });
                } else {
                    CMDBuildUI.util.Logger.log(
                        'Class model definition for ' + className + ' not found.',
                        CMDBuildUI.util.Logger.levels.warn
                    );
                    deferred.reject();
                }
            });
        } else {
            CMDBuildUI.util.Logger.log('Empty cardId', CMDBuildUI.util.Logger.levels.warn);
            deferred.reject();
        }
        return deferred.promise;
    },

    /**
     * Get process instance from the server.
     *
     * @param {String} processName
     * @param {Numeric} instanceId
     *
     * @returns {Ext.promise.Promise<CMDBuildUI.model.processes.Instance>}
     */
    getRemoteProcessInstance: function (processName, instanceId) {
        var deferred = new Ext.Deferred();

        if (instanceId) {
            // get model
            CMDBuildUI.util.helper.ModelHelper.getModel(
                CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
                processName
            ).then(function (model) {
                if (model) {
                    model.load(instanceId, {
                        success: function (record, operation) {
                            deferred.resolve(record);
                        },
                        failure: function (record, operation) {
                            CMDBuildUI.util.Logger.log(
                                'Instance not found for process ' + processName,
                                CMDBuildUI.util.Logger.levels.warn
                            );
                            deferred.reject();
                        }
                    });
                } else {
                    CMDBuildUI.util.Logger.log(
                        'Instance model definition for ' + processName + ' not found.',
                        CMDBuildUI.util.Logger.levels.warn
                    );
                    deferred.reject();
                }
            });
        } else {
            CMDBuildUI.util.Logger.log('Empty instanceId', CMDBuildUI.util.Logger.levels.warn);
            deferred.reject();
        }
        return deferred.promise;
    },

    /**
     * Get lookup value by code from the server.
     *
     * @param {String} type Lookup type
     * @param {String} code Lookup code.
     *
     * @returns {Ext.promise.Promise<CMDBuildUI.model.lookups.Lookup>}
     */
    getRemoteLookupFromCode: function (type, code) {
        var deferred = new Ext.Deferred();

        if (code) {
            var lt = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(type);
            lt.getLookupValues().then(
                function (values) {
                    var lookupvalue = values.findRecord('code', code, 0, false, true, true);
                    if (lookupvalue) {
                        deferred.resolve(lookupvalue);
                    } else {
                        deferred.reject();
                    }
                },
                function () {
                    deferred.reject();
                }
            );
        } else {
            CMDBuildUI.util.Logger.log('Empty code', CMDBuildUI.util.Logger.levels.warn);
            deferred.reject();
        }
        return deferred.promise;
    },

    /**
     * Get function outputs from the server.
     *
     * @param {String} fnName Function name
     * @param {Object} [params]
     * @param {Object} [model]
     *
     * @returns {Ext.promise.Promise<Ext.data.Model>}
     */
    getFunctionOutputs: function (fnName, params, model) {
        var deferred = new Ext.Deferred();
        // load function results
        Ext.getStore('Functions')
            .getFunctionByName(fnName)
            .then(function (fn) {
                fn.getOutputs(params, model)
                    .then(function (data, meta) {
                        deferred.resolve(data, meta);
                    })
                    .otherwise(function () {
                        deferred.reject();
                    });
            })
            .otherwise(function () {
                deferred.reject();
            });
        return deferred.promise;
    },

    /**
     * Get attachment oner record.
     * Used only on UI Scripts.
     *
     * @returns {Ext.promise.Promise}
     */
    getAttachmentOwner: function () {
        var deferred = new Ext.Deferred();
        if (this._attachmentOwner) {
            switch (this._attachmentOwner.type) {
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                    this.getRemoteCard(this._attachmentOwner.typeName, this._attachmentOwner.id)
                        .then(function (record) {
                            deferred.resolve(record);
                        })
                        .otherwise(function () {
                            deferred.reject();
                        });
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                    this.getRemoteProcessInstance(this._attachmentOwner.typeName, this._attachmentOwner.id)
                        .then(function (record) {
                            deferred.resolve(record);
                        })
                        .otherwise(function () {
                            deferred.reject();
                        });
                    break;
                default:
                    deferred.reject();
            }
        } else {
            deferred.reject();
        }
        return deferred.promise;
    },

    /**
     * Open a report.
     *
     * @param {String} reportName
     * @param {String} extension
     * @param {Object} [defaults] As key the parameter name,
     * as value an object with keys `value` and `editable`.
     * Example `{Date: {value: new Date(), editable: false}}`.
     */
    openReport: function (reportName, extension, defaults) {
        var popup = CMDBuildUI.util.Utilities.openPopup(null, '', {
            xtype: 'reports-container',
            layout: 'fit',
            hideTitle: true,
            viewModel: {
                data: {
                    objectTypeName: reportName,
                    extension: extension,
                    defaults: defaults
                }
            },
            listeners: {
                closeparameterspopup: function (reportcontainer, reportid) {
                    popup.close();
                }
            }
        });
    },

    /**
     * Used to identify the device context.
     *
     * @returns {Boolean} `true` if the user is using desktop interface.
     */
    isDesktop: function () {
        return true;
    },

    /**
     * Used to identify the device context.
     *
     * @returns {Boolean} `true` if the user is using mobile interface.
     */
    isMobile: function () {
        return false;
    }
});
