Ext.define('CMDBuildUI.view.fields.schedulerdate.SchedulerDateFieldController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-schedulerdate-schedulerdatefield',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender',
            change: 'onChange',
            changevaluecondition: 'onValueConditionChange',
            validitychange: 'onValidityChange'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.fields.schedulerdate.SchedulerDateField} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = view.lookupViewModel(),
            recordLinkName = view.getRecordLinkName(),
            fieldName = view.getName();

        vm.bind(
            { theObject: '{theObject}', triggerStore: '{triggerStore}', initialized: '{initialized}' },
            function (data) {
                if (data.theObject && data.triggerStore && data.initialized) {
                    this.loadStores(function (sequenceRecords, triggerRecords) {
                        vm.set('storesloaded', true);
                    });
                }
            },
            this
        );

        vm.bind(
            { record: '{' + recordLinkName + '}', value: '{' + recordLinkName + '.' + fieldName + '}' },
            function (data) {
                if (view.haschanges || data.record.isModified(fieldName)) {
                    view.haschanges = true;
                    view.fireEventArgs('changevaluecondition', [view, { origin: 'valuechange' }]);
                }
            }
        );
    },

    /**
     * On validity change on field
     *
     * @param {CMDBuildUI.view.fields.schedulerdate.SchedulerDateField} view
     * @param {Boolean} isValid
     * @param {Object} eOpts
     */
    onValidityChange: function (view, isValid, eOpts) {
        if (view._disabledFromValue && isValid) {
            this.onChange(view, view.getValue());
        }
    },

    /**
     * On change value event listener
     *
     * @param {CMDBuildUI.view.fields.schedulerdate.SchedulerDateField} view
     * @param {Date} newValue
     * @param {Date} oldValue
     * @param {Object} eOpts
     */
    onChange: function (view, newValue, oldValue, eOpts) {
        if (!view.isValid() || !newValue) {
            view._disabledFromValue = true;
        } else {
            view._disabledFromValue = false;
        }
        view.updateDisability();
    },

    /**
     *
     * @param {[CMDBuildUI.model.calendar.Trigger]} triggerRecords
     * @returns {Boolean} true if the check of the conditions is passed, false otherwise
     */
    conditionCheck: function (triggerRecords) {
        const view = this.getView();
        var conditionValid = true;
        Ext.Array.forEach(
            triggerRecords,
            function (item, index, allItems) {
                if (!conditionValid) return;
                if (view.hasCondition(item)) {
                    if (view.isTriggerConditionEvaluated(item.getId())) {
                        if (!view.isTriggerConditionValid(item.getId())) {
                            conditionValid = false;
                        }
                    } else {
                        conditionValid = false;
                    }
                }
            },
            this
        );
        return conditionValid;
    },

    /**
     *
     * @param {string} value
     * @returns {Boolean} true if value check is passed
     */
    valueCheck: function (value) {
        return value && this.getView().isValid() ? true : false;
    },

    /**
     * @param {CMDBuildUI.view.fields.schedulerdate.SchedulerDateField} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        view.updateDisability();
    },

    /**
     * this function elaborates all the triggers with (value, index, array) 'write' and 'read'
     * @param {*} view
     */
    onTriggerClick: function (view) {
        const me = this;
        this.loadStores(function (sequenceRecords, triggerRecords) {
            this.getViewWriteSequences(false, [], true).then(function (response) {
                me.onTriggerClickHandler(response[0]);
            });
        });
    },

    /**
     *
     * @param {CMDBuildUI.view.fields.schedulerdate.SchedulerDateField} view
     * @param {Object} eOpts contains information about the fired event
     */
    onValueConditionChange: function (view, eOpts) {
        const me = this;
        view.lookupViewModel().bind({ bindTo: '{storesloaded}', single: true }, function () {
            me.onValueConditionChangeHandler(view);
        });
    },
    /**
     *
     * @param {CMDBuildUI.view.fields.schedulerdate.SchedulerDateField} view
     * @param {Object} eOpts
     */
    onValueConditionChangeHandler: function (view, eOpts) {
        const me = this,
            disableFromValue = view._disabledFromValue,
            hasInvalidTriggers = !Ext.isEmpty(this.getInvalidTriggers()),
            validTriggers = this.getValidTriggers(),
            hasValidTriggers = !Ext.isEmpty(validTriggers);

        //If the date is erased --> need to delete all the related "schedules rules"
        //if the conditions on some triggers are not yet valid --> need to delete the related schedules with that triggers
        if (disableFromValue || hasInvalidTriggers) {
            //sequences store
            const vm = this.getViewModel(),
                theObject = vm.get('theObject'),
                sequences = theObject.sequences();

            //removes the hidden "scheduler rules"
            this.getHiddenSequences(false, disableFromValue ? [] : this.getInvalidTriggers()).then(function (response) {
                const hiddenSequences = response[0];
                sequences.remove(hiddenSequences);
            });

            //removes the view write "scheduler rules"
            this.getViewWriteSequences(false, disableFromValue ? null : this.getInvalidTriggers()).then(
                function (response) {
                    const viewWriteSequences = response[0],
                        //sequences that doesn't need user confimation to be changed after date change
                        automaticRecalculation = [],
                        //sequences that need user confirmation to be recalculated
                        askedRecalculation = [];

                    //Split the viewWriteSequences in 2 groups
                    viewWriteSequences.forEach(function (viewWriteSequence) {
                        //split the sequences based on the edit mode
                        if (
                            viewWriteSequence.get('sequenceParamsEditMode') ==
                                CMDBuildUI.model.calendar.Sequence.sequenceParamsEditMode.read &&
                            viewWriteSequence.get('eventEditMode') ==
                                CMDBuildUI.model.calendar.Sequence.sequenceParamsEditMode.read
                        ) {
                            //if both events and sequences are in view mode doesn't need user confirmation
                            automaticRecalculation.push(viewWriteSequence);
                        } else {
                            //otherwise user have to confirm if wants the "schedule rule" to calculate the nuew schedules
                            askedRecalculation.push(viewWriteSequence);
                        }
                    }, this);

                    //removes the sequences in wich the user doesn't have write permission
                    sequences.remove(automaticRecalculation);

                    //ask the user if want to recalculate the sequences based on the new date of (the non automatic ones)
                    if (askedRecalculation.length) {
                        CMDBuildUI.util.Msg.confirm(
                            CMDBuildUI.locales.Locales.calendar.messagetitle,
                            Ext.String.format(
                                (askedRecalculation.length > 1 ?
                                    CMDBuildUI.locales.Locales.calendar.messagebodyplural
                                :   CMDBuildUI.locales.Locales.calendar.messagebodysingular) + '. {1}',
                                askedRecalculation.length,
                                CMDBuildUI.locales.Locales.calendar.messagebodydelete
                            ),
                            function (btnText) {
                                if (btnText === 'yes') {
                                    sequences.remove(askedRecalculation);
                                }
                            },
                            this
                        );
                    }
                }
            );
        }

        //If the date has a new valid value and there are valid triggers (checking their conditions) need to recalculate or create the related "schedules"
        if (!disableFromValue && hasValidTriggers) {
            const value = view.getValue();
            var triggerValue;

            //get the hidden sequences
            this.getHiddenSequences(true, validTriggers).then(function (response) {
                const hiddenSequences = response[0];
                me.addGenerateEventRequests(hiddenSequences.length);

                //manipulate each hiddenSequence
                hiddenSequences.forEach(function (hiddenSequence) {
                    //updates his date
                    if (!Ext.isEmpty(hiddenSequence.trigger.get('delay'))) {
                        triggerValue = me.updateDateFromTrigger(value, hiddenSequence.trigger.parseIsoInterval());
                    } else {
                        triggerValue = value;
                    }
                    CMDBuildUI.view.fields.schedulerdate.Util.updateSequenceDate(hiddenSequence, triggerValue);

                    //generate the events with the new date
                    hiddenSequence.generateEventsfromSequence().then(function (eventsData) {
                        //assign the new events
                        hiddenSequence.dirty = true;
                        hiddenSequence.events().setData(eventsData);
                        me.removeGenerateEventRequest();
                    });
                }, this);
            });

            //get the view write sequences
            this.getViewWriteSequences(true, validTriggers).then(function (response) {
                const viewWriteSequences = response[0];
                const alreadyExisting = response[2];

                //sequences that doesn't need user confimation to be changed after date change
                const automaticRecalculation = [];
                //sequences that need user confirmation to be recalculated
                const askedRecalculation = [];

                //Split the viewWriteSequences in 2 groups
                viewWriteSequences.forEach(function (viewWriteSequence) {
                    //split the sequences based on the edit mode
                    if (
                        viewWriteSequence.get('sequenceParamsEditMode') ==
                            CMDBuildUI.model.calendar.Sequence.sequenceParamsEditMode.read &&
                        viewWriteSequence.get('eventEditMode') ==
                            CMDBuildUI.model.calendar.Sequence.sequenceParamsEditMode.read
                    ) {
                        //if both events and sequences are in view mode doesn't need user confirmation
                        automaticRecalculation.push(viewWriteSequence);
                    } else {
                        //otherwise user have to confirm if wants the "schedule rule" to calculate the nuew schedules
                        if (alreadyExisting.indexOf(viewWriteSequence) != -1) {
                            //only preexisting schedules need confirmation. If a schedules has been generated in this calculation, doesn't need to recalculate eighter ask for it
                            askedRecalculation.push(viewWriteSequence);
                        }
                    }
                }, this);

                me.addGenerateEventRequests(automaticRecalculation.length);

                //recalculate the events schedule (The automatic ones)
                automaticRecalculation.forEach(function (sequence) {
                    //updates his date
                    if (!Ext.isEmpty(sequence.trigger.get('delay'))) {
                        triggerValue = me.updateDateFromTrigger(value, sequence.trigger.parseIsoInterval());
                    } else {
                        triggerValue = value;
                    }
                    CMDBuildUI.view.fields.schedulerdate.Util.updateSequenceDate(sequence, triggerValue, {
                        keepgap: true
                    });

                    //generate the events with the new date
                    sequence.generateEventsfromSequence().then(function (eventsData) {
                        //assign the new events
                        sequence.dirty = true;
                        sequence.events().setData(eventsData);
                        me.removeGenerateEventRequest();
                    });
                });

                //ask the user if want to recalculate the sequences based on the new date of (the non automatic ones)
                if (askedRecalculation.length) {
                    CMDBuildUI.util.Msg.confirm(
                        CMDBuildUI.locales.Locales.calendar.messagetitle,
                        Ext.String.format(
                            (askedRecalculation.length > 1 ?
                                CMDBuildUI.locales.Locales.calendar.messagebodyplural
                            :   CMDBuildUI.locales.Locales.calendar.messagebodysingular) + '. {1}',
                            askedRecalculation.length,
                            CMDBuildUI.locales.Locales.calendar.messagebodyrecalculate
                        ),
                        function (btnText) {
                            if (btnText === 'yes') {
                                me.addGenerateEventRequests(askedRecalculation.length);

                                askedRecalculation.forEach(function (sequence) {
                                    //updates his date
                                    if (!Ext.isEmpty(sequence.trigger.get('delay'))) {
                                        triggerValue = me.updateDateFromTrigger(
                                            value,
                                            sequence.trigger.parseIsoInterval()
                                        );
                                    } else {
                                        triggerValue = value;
                                    }
                                    CMDBuildUI.view.fields.schedulerdate.Util.updateSequenceDate(
                                        sequence,
                                        triggerValue,
                                        { keepgap: true }
                                    );

                                    //generate the events with the new date
                                    sequence.generateEventsfromSequence().then(function (eventsData) {
                                        //assign the new events
                                        sequence.dirty = true;
                                        sequence.events().setData(eventsData);
                                        me.removeGenerateEventRequest();
                                    });
                                }, this);
                            }
                        },
                        this
                    );
                }
            });
        }
    },

    /**
     *
     */
    initViewWriteSequences: function (sequenceRecords, triggerRecords) {
        const me = this;
        const triggersFiltered = triggerRecords.filter(function (value, index, array) {
            if (
                value.get('sequenceParamsEditMode') ==
                    CMDBuildUI.model.calendar.Sequence.sequenceParamsEditMode.write ||
                value.get('sequenceParamsEditMode') == CMDBuildUI.model.calendar.Sequence.sequenceParamsEditMode.read
            ) {
                return true;
            }
        });

        this.createSequencesFromTrigger(sequenceRecords, triggersFiltered, function (sequenceRecords) {
            const sequences = this.getSequences();
            me.addGenerateEventRequests(sequenceRecords.length);

            sequenceRecords.forEach(function (sequenceRecord) {
                const index = sequences.find('_id', sequenceRecord.getId());

                if (index == -1) {
                    //add in the store
                    sequences.add(sequenceRecord);

                    //generate the events related
                    sequenceRecord.generateEventsfromSequence().then(function (eventsData) {
                        //assign the new events
                        sequenceRecord.dirty = true; //assegene as dirty_recalculate
                        sequenceRecord.events().setData(eventsData);
                        me.removeGenerateEventRequest();
                    });
                } else {
                    me.removeGenerateEventRequest();
                }
            }, this);
        });
    },

    /**
     *
     * @param {*} sequenceRecords
     * @param {*} triggerRecords
     */
    initHiddenSequences: function (sequenceRecords, triggerRecords) {
        const me = this;
        const triggersFiltered = triggerRecords.filter(function (value, index, array) {
            if (
                value.get('sequenceParamsEditMode') ==
                CMDBuildUI.model.calendar.Sequence.sequenceParamsEditMode
                    .hidden /*  && view.isTriggerConditionValid(value.getId()) */
            ) {
                return true;
            }
        });

        this.createSequencesFromTrigger(sequenceRecords, triggersFiltered, function (sequenceRecords) {
            const sequences = this.getSequences();
            me.addGenerateEventRequests(sequenceRecords.length);

            sequenceRecords.forEach(function (sequenceRecord) {
                const index = sequences.find('_id', sequenceRecord.getId());

                //if the sequence is new
                if (index == -1) {
                    //add in the store
                    sequences.add(sequenceRecord);

                    //generate the events related
                    sequenceRecord.generateEventsfromSequence().then(function (eventsData) {
                        //assign the new events
                        sequenceRecord.dirty = true; //assegene as dirty_recalculate
                        sequenceRecord.events().setData(eventsData);
                        me.removeGenerateEventRequest();
                    });
                } else {
                    me.removeGenerateEventRequest();
                }
            }, this);
        });
    },

    /**
     *
     * @returns {Array}
     */
    getValidTriggers: function () {
        const view = this.getView();
        const triggers = this.getViewModel().get('triggerStore');
        const validTriggers = triggers.getRange().filter(function (item, index, array) {
            if (view.isTriggerConditionValid(item.getId())) {
                return true;
            }
        }, this);

        return validTriggers;
    },

    /**
     *
     * @returns {Array}
     */
    getInvalidTriggers: function () {
        const view = this.getView();
        const triggers = this.getViewModel().get('triggerStore');
        const invalidTriggers = triggers.getRange().filter(function (item, index, array) {
            if (!view.isTriggerConditionValid(item.getId())) {
                return true;
            }
        }, this);

        return invalidTriggers;
    },

    /**
     * When resolved returns all the sequences wich have a hidden trigger
     * If the sequence is not present, generate it and the resolves the promise
     * @param {Boolean} generate If true generates the missing "schedules rules" calling the server functions; if false returns only the created "schedules rules"; default true;
     * @returns {Ext.Deferred}
     */
    getHiddenSequences: function (generate, someTriggers) {
        generate = generate == false ? false : true;

        const me = this;
        const deferred = new Ext.Deferred();
        const vm = this.getViewModel();
        const view = this.getView();
        const triggers = vm.get('triggerStore');
        const theObject = vm.get('theObject');
        const sequences = theObject.sequences();

        var range;
        if (!Ext.isEmpty(someTriggers)) {
            range = someTriggers;
        } else {
            range = triggers.getRange();
        }

        //filter the triggers from the store and get only the hidden ones
        const hiddenTriggers = range.filter(function (value, index, array) {
            if (
                value.get('sequenceParamsEditMode') == CMDBuildUI.model.calendar.Sequence.sequenceParamsEditMode.hidden
            ) {
                return true;
            }
        });

        const hiddenSequences = [],
            newCreated = [],
            alreadyExisting = [];

        //finds the hidden sequences related to the trigger
        //ASSERT: Each trigger has a sequence at this point
        hiddenTriggers.forEach(function (hiddenTrigger) {
            const foundSequence = sequences.findRecord('trigger', hiddenTrigger.getId());

            if (foundSequence) {
                hiddenSequences.push(foundSequence);
                alreadyExisting.push(foundSequence);
                foundSequence.trigger = hiddenTrigger;
            }
        }, this);

        if (generate) {
            this.createSequencesFromTrigger(hiddenSequences, hiddenTriggers, function (hiddenSequences) {
                me.addGenerateEventRequests(hiddenSequences.length);

                hiddenSequences.forEach(function (hiddenSequence) {
                    var index;
                    if (view.formmode === CMDBuildUI.util.helper.FormHelper.formmodes.create) {
                        index = sequences.find('trigger', hiddenSequence.get('trigger'));
                    } else {
                        index = sequences.find('_id', hiddenSequence.getId());
                    }

                    //if the sequence is new
                    if (index == -1) {
                        //add in the store
                        sequences.add(hiddenSequence);
                        newCreated.push(hiddenSequence);

                        //generate the events related
                        hiddenSequence.generateEventsfromSequence().then(function (eventsData) {
                            //assign the new events
                            hiddenSequence.dirty = true; //assegene as dirty_recalculate
                            hiddenSequence.events().setData(eventsData);
                            me.removeGenerateEventRequest();
                        });
                    } else {
                        me.removeGenerateEventRequest();
                    }
                }, this);

                deferred.resolve([hiddenSequences, newCreated, alreadyExisting]);
            });
        } else {
            deferred.resolve([hiddenSequences, newCreated, alreadyExisting]);
        }

        return deferred;
    },

    /**
     *
     * @param {Boolean} generate
     * @param {Array} someTriggers
     * @param {Boolean} clickOnTrigger indicate if method is call after click on trigger
     * @returns
     */
    getViewWriteSequences: function (generate, someTriggers, clickOnTrigger) {
        generate = generate == false ? false : true;

        const me = this;
        const deferred = new Ext.Deferred();
        const vm = this.getViewModel();
        const view = this.getView();
        const triggers = vm.get('triggerStore');
        const theObject = vm.get('theObject');
        const sequences = theObject.sequences();

        var range;
        if (!Ext.isEmpty(someTriggers)) {
            range = someTriggers;
        } else {
            range = triggers.getRange();
        }

        //filter the triggers from the store and get only the hidden ones
        const viewWriteTriggers = range.filter(function (value, index, array) {
            if (
                value.get('sequenceParamsEditMode') ==
                    CMDBuildUI.model.calendar.Sequence.sequenceParamsEditMode.write ||
                value.get('sequenceParamsEditMode') == CMDBuildUI.model.calendar.Sequence.sequenceParamsEditMode.read
            ) {
                return true;
            }
        });

        const viewWriteSequences = [],
            newCreated = [],
            alreadyExisting = [];

        //finds the hidden sequences related to the trigger
        //ASSERT: Each trigger has a sequence at this point
        viewWriteTriggers.forEach(function (viewWriteTrigger) {
            const foundSequence = sequences.findRecord('trigger', viewWriteTrigger.getId());

            if (foundSequence) {
                viewWriteSequences.push(foundSequence);
                alreadyExisting.push(foundSequence);
                foundSequence.trigger = viewWriteTrigger;
            } else if (clickOnTrigger) {
                generate = true;
            }
        }, this);

        if (generate) {
            this.createSequencesFromTrigger(viewWriteSequences, viewWriteTriggers, function (viewWriteSequences) {
                var numberviewWriteSequences = viewWriteSequences.length;
                me.addGenerateEventRequests(numberviewWriteSequences);

                viewWriteSequences.forEach(function (viewWriteSequence) {
                    var index;
                    if (view.formmode === CMDBuildUI.util.helper.FormHelper.formmodes.create) {
                        index = sequences.find('trigger', viewWriteSequence.get('trigger'));
                    } else {
                        index = sequences.find('_id', viewWriteSequence.getId());
                    }

                    //if the sequence is new
                    if (index == -1) {
                        //add in the store
                        sequences.add(viewWriteSequence);
                        newCreated.push(viewWriteSequence);

                        //generate the events related
                        viewWriteSequence.generateEventsfromSequence().then(function (eventsData) {
                            //assign the new events
                            viewWriteSequence.dirty = true; //assegene as dirty_recalculate
                            viewWriteSequence.events().setData(eventsData);

                            me.removeGenerateEventRequest();
                            if (--numberviewWriteSequences == 0) {
                                deferred.resolve([viewWriteSequences, newCreated, alreadyExisting]);
                            }
                        });
                    } else {
                        me.removeGenerateEventRequest();
                        if (--numberviewWriteSequences == 0) {
                            deferred.resolve([viewWriteSequences, newCreated, alreadyExisting]);
                        }
                    }
                }, this);
            });
        } else {
            deferred.resolve([viewWriteSequences, newCreated, alreadyExisting]);
        }

        return deferred;
    },

    /**
     * If a trigger doesn't have the related sequences, the funcion generates it calling the server
     * @param {*} sequenceRecords  Ther records already in created
     * @param {*} triggerRecords All the trigger regords
     * @param {*} callback
     * @returns {*} The old sequences UNION the new sequences related only to the passed trigger
     */
    createSequencesFromTrigger: function (sequenceRecords, triggerRecords, callback) {
        //TODO: move on trigger model
        const newSequences = [],
            newSequenceRecords = [];
        var callbackCount = triggerRecords.length;

        triggerRecords.forEach(function (trigger) {
            const triggerId = trigger.getId();
            const matchedSequence = Ext.Array.findBy(sequenceRecords, function (item, index) {
                if (item.get('trigger') == triggerId) return true;
                return false;
            });

            if (!matchedSequence) {
                const view = this.getView(),
                    vm = view.lookupViewModel(),
                    theObject = vm.get('theObject'),
                    ownerId = theObject.get('_card') || theObject.getId();

                Ext.Ajax.request({
                    url: Ext.String.format(
                        '{0}/calendar/triggers/{1}/generate-sequence?date={2}',
                        CMDBuildUI.util.Config.baseUrl,
                        triggerId,
                        view.formatDate(view.getValue(), 'Y-m-d')
                    ),
                    method: 'GET',
                    success: function (response, options) {
                        callbackCount--;
                        const data = JSON.parse(response.responseText).data;
                        delete data.notifications;
                        const newSequence = Ext.create('CMDBuildUI.model.calendar.Sequence', data);

                        if (view.formmode !== 'create') {
                            newSequence.set('card', ownerId);
                        }

                        newSequence.trigger = trigger;
                        newSequences.push(newSequence);

                        if (!callbackCount) {
                            callback.call(this, newSequenceRecords.concat(newSequences));
                        }
                    },
                    scope: this
                });
            } else {
                matchedSequence.trigger = trigger;
                newSequenceRecords.push(matchedSequence);
                this.updateSequenceFromTrigger(matchedSequence, trigger);
                callbackCount--;
            }
        }, this);

        if (!callbackCount) {
            callback.call(this, newSequenceRecords.concat(newSequences));
        }
    },

    /**
     * this function calls a callback ensuring that the sequences and the triggers are all loaded
     * @param {*} callback
     */
    loadStores: function (callback) {
        const me = this;
        const view = this.getView();
        const vm = view.lookupViewModel();
        const theObject = vm.get('theObject');

        // /**
        //  * Needed
        //  */
        // //TODO: find a better place to set the proxy
        // var parentModel = Ext.ClassManager.get('CMDBuildUI.model.calendar.Event');
        // parentModel.setProxy({
        //     type: 'memory'
        // })
        if (theObject && typeof theObject.sequences === 'function') {
            const sequences = theObject.sequences();
            const triggers = vm.get('triggerStore');

            Ext.Promise.all([this.sequencesLoad(sequences, theObject), this.triggersLoad(triggers)]).then(
                function (records) {
                    /**
                     * NOTE: to get the records don't use the arguments in this function
                     * records[0] && records[1]
                     */
                    const sequenceRecords = sequences.getRange(), //records[0];
                        triggerRecords = triggers.getRange(); //records[1];

                    callback.call(me, sequenceRecords, triggerRecords);
                }
            );
        }
    },

    /**
     * This function loads the sequences store.
     * @param {*} sequences The store
     * @param {*} theObject
     */
    sequencesLoad: function (sequences, theObject) {
        const deferred = new Ext.Deferred();
        const theObjectId = theObject.get('_card') || theObject.getId();

        //if theObject is not saved on the server
        if (theObject.phantom == true) {
            deferred.resolve([]);
        } else if (!sequences.isLoaded()) {
            sequences.setProxy({
                type: 'baseproxy',
                url: Ext.String.format('/calendar/sequences/by-card/{0}', theObjectId)
            });
            sequences.load({
                params: { detailed: true, includeEvents: true },
                callback: function (records, operations, success) {
                    // callbackFunction.call(this, records);
                    records.forEach(function (item, index, array) {
                        delete item.data.notifications;

                        item.events()
                            .getRange()
                            .forEach(function (item, index, array) {
                                delete item.data.notifications;
                            });
                    });
                    deferred.resolve(records);
                },
                scope: this
            });
        } else {
            const records = sequences.getRange();
            deferred.resolve(records);
            // callbackFunction.call(this, records)
        }
        return deferred.promise;
    },

    /**
     * This function loadts the trigger store
     * @param {*} triggers
     */
    triggersLoad: function (triggers) {
        const deferred = new Ext.Deferred(),
            records = triggers.getRange();
        deferred.resolve(records);

        return deferred.promise;
    },

    /**
     * This function clones the records passed and pushes them as data in a new store with model 'CMDBuildUI.model.calendar.Sequence'
     * @param {*} sequenceRecords This records are tho one stored in theObject.sequence() store
     */
    onTriggerClickHandler: function (sequenceRecords) {
        //creates the new store
        const sequencesClone = Ext.create('Ext.data.Store', {
            model: 'CMDBuildUI.model.calendar.Sequence',
            proxy: { type: 'memory' }
        });
        sequencesClone.suspendEvents();

        const sequenceRecordsClone = [];
        var sequenceClone;
        sequenceRecords.forEach(function (sequenceRecord) {
            //clones the single sequence
            sequenceClone = sequenceRecord.clone();
            sequenceClone.triggerRecord = sequenceRecord.trigger;
            sequenceRecordsClone.push(sequenceClone);

            //get the events related of that sequence
            const events = sequenceRecord.events();

            //clones the events
            var eventsRangeClone = [];
            events.getRange().forEach(function (event) {
                eventsRangeClone.push(event.clone());
            });

            //relates the cloned events in the cloned sequence
            sequenceClone.events().setData(eventsRangeClone);
        });

        //sets the cloned data in the new store
        sequencesClone.setData(sequenceRecordsClone);

        sequencesClone.resumeEvents();
        this.openPopup(sequencesClone);
    },

    /**
     *
     * @param {Ext.data.Store} sequencesClone
     */
    openPopup: function (sequencesClone) {
        const me = this,
            vm = this.getView().lookupViewModel(),
            theObject = vm.get('theObject'),
            sequences = theObject.sequences();

        const popup = CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.calendar.sequencepaneltitle,
            {
                xtype: 'fields-schedulerdate-sequencecontainer',
                viewModel: { data: { sequences: sequencesClone } },

                //this could be punt in sequenceContainer view.
                listeners: {
                    popupclose: function () {
                        // sequences.rejectChanges();
                        popup.close();
                    },
                    popupsave: function () {
                        CMDBuildUI.util.helper.FormHelper.startSavingForm();
                        sequencesClone.getRange().forEach(function (sequencesCloneRecord) {
                            const index = sequences.find('_id', sequencesCloneRecord.getId());

                            //add the record to the store
                            if (index == -1) {
                                sequences.add(sequencesCloneRecord);
                            } else {
                                const recordToReplace = sequences.getAt(index);

                                //updates the record in the original store;
                                me.updateDataFromRecord(recordToReplace, sequencesCloneRecord);

                                //If the cloned record has some events modified, det it as diry
                                CMDBuildUI.view.fields.schedulerdate.Util.isDirty(sequencesCloneRecord) ?
                                    (recordToReplace.dirty = true)
                                :   null;

                                //sets the new records
                                recordToReplace.events().setData(sequencesCloneRecord.events().getData());
                            }
                        });
                        CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        popup.close();
                    }
                }
            }
        );
    },

    privates: {
        /**
         *
         * @returns {Ext.data.Store}
         */
        getSequences: function () {
            return this.getViewModel().get('theObject').sequences();
        },

        /**
         * This function sets attributes in the oldRecord if there are differences with the newRecord.
         * Only
         * @param {Ext.data.Model} oldRecord
         * @param {Ext.data.Model} newRecord
         */
        updateDataFromRecord: function (oldRecord, newRecord) {
            const oldRecordData = oldRecord.getData(),
                toExclude = { events: true };

            for (var attr in oldRecordData) {
                if (!toExclude[attr]) {
                    if (oldRecord.get(attr) != newRecord.get(attr)) oldRecord.set(attr, newRecord.get(attr));
                }
            }
        },

        /**
         *
         * @param {Ext.data.Model} sequence
         * @param {Ext.data.Model} trigger
         */
        updateSequenceFromTrigger: function (sequence, trigger) {
            const params = [
                //TODO: add here other params
                'sequenceParamsEditMode',
                'showGeneratedEventsPreview',
                'eventEditMode'
            ];

            params.forEach(function (param) {
                sequence.set(param, trigger.get(param));
            });
        },

        /**
         *
         * @param {Date} startDate the start date
         * @param {Object} interval the values to calculate the new date
         * @returns {Date}
         */
        updateDateFromTrigger: function (startDate, interval) {
            var typeDate, valueDate;
            Ext.Object.each(interval, function (item, index, allitems) {
                if (interval[item] !== 0 && item !== 'sign') {
                    typeDate =
                        item === 'D' ? Ext.Date.DAY
                        : item === 'M' ? Ext.Date.MONTH
                        : Ext.Date.YEAR;
                    valueDate = interval.sign === '-' ? interval[item] * -1 : interval[item];
                    return false;
                }
            });
            return Ext.Date.add(startDate, typeDate, valueDate);
        },

        /**
         *
         * @param {Number} generateEventRequests
         */
        addGenerateEventRequests: function (generateEventRequests) {
            var view = this.getView();

            if (generateEventRequests > 0) {
                view.generateEventRequests += generateEventRequests;
                view.up('form').getForm().checkValidity();
            }
        },

        /**
         *
         */
        removeGenerateEventRequest: function () {
            var view = this.getView();
            view.generateEventRequests -= 1;

            if (view.generateEventRequests == 0) {
                view.up('form').getForm().checkValidity();
            }
        }
    }
});
