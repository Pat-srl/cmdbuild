Ext.define('CMDBuildUI.view.fields.schedulerdate.SchedulerDateField', {
    extend: 'Ext.form.field.Date',

    alias: 'widget.schedulerdatefield',

    requires: [
        'CMDBuildUI.view.fields.schedulerdate.SchedulerDateFieldController',
        'CMDBuildUI.view.fields.schedulerdate.SchedulerDateFieldModel'
    ],

    controller: 'fields-schedulerdate-schedulerdatefield',

    viewModel: {
        type: 'fields-schedulerdate-schedulerdatefield'
    },

    config: {
        recordLinkName: 'theObject', //the standard value
    },

    /**
     * @property {Boolean} haschanges
     * Used to determine if there are change after the firs evaulation.
     */
    haschanges: false,

    triggers: {
        scheduler: {
            cls: CMDBuildUI.util.helper.IconHelper.getIconId('clock', 'regular'),
            handler: function (view, trigger, eOpts) {
                if (!trigger.el.isMasked()) {
                    const ct = this.lookupController();
                    ct.onTriggerClick(view);
                }
            },
            hidden: true, //the visibility is handled in the viewModel
            disabled: true
        }
    },

    initComponent: function () {
        /**
        * Tells if the value present disables the trigger
        */
        this._disabledFromValue = true;

        /**
         * tells if some condition disables the trigger
         */
        this._disableFromCondition = false;

        /**
         * Used to calculate the _disableFromCondition
         * saves the results for the conditions on triggers
         * {
         *      triggerId: disabled
         * }
         */
        this._disabilityFromCondition = {};

        this._enumConditions = 0;
        this._auxEnumCondition = 0;
        this._conditionchange = false;

        /**
         * Used to count the number of event generation requests currently in progress
         */
        this.generateEventRequests = 0;

        this.callParent(arguments);
    },

    /**
    *
    */
    addConditionalTriggerVisibilityRules: function (trigger) {
        this._enumConditions += 1;
        this._auxEnumCondition = this._enumConditions;

        const me = this,
            vm = this.getViewModel(),
            conditionScript = trigger.get('conditionScript'),
            jsfn = Ext.String.format(
                'function executeTriggerDisability (api) {\n{0}\n}',
                conditionScript
            );

        try {
            eval(jsfn);
        } catch (e) {
            CMDBuildUI.util.Logger.log(
                "Error on context menu function.",
                CMDBuildUI.util.Logger.levels.error,
                null,
                e
            );
            var executeTriggerDisability = function () { return false; };
        }

        const updateTriggerDisability = function (record) {
            const api = Ext.apply({
                record: record,
                mode: me.formmode
            }, CMDBuildUI.util.api.Client.getApiForSchedules());

            var enable = false;
            // use try / catch to manage errors
            try {
                //if the function returns true, the field is visible
                enable = executeTriggerDisability(api);
            } catch (e) {
                CMDBuildUI.util.Logger.log(
                    "Error on execution scheduler condition.",
                    CMDBuildUI.util.Logger.levels.error,
                    null,
                    {
                        fn: executeTriggerDisability
                    }
                );
                enable = false;
            }

            const disable = !enable;
            if (this._disabilityFromCondition[trigger.id] != disable) {
                this._conditionchange = true;

                //assigns the nuew value
                this._disabilityFromCondition[trigger.id] = disable;
            }

            this._auxEnumCondition -= 1;
            if (this._auxEnumCondition == 0) {
                //when enters here, all conditions are evaluated

                //update the visibility of the trigger
                this._updateDisabilityFromcondition();
                this.updateDisability();

                //only the first time enters here. Indicates that the component has evaluated the conditions the first time
                if (!vm.get("initialized")) {
                    vm.set("initialized", true);
                } else if (this._conditionchange) {

                    this.fireEventArgs('changevaluecondition', [this, {
                        origin: 'conditionchange'
                    }]);

                    //could assign only changed triggers and handle only those changes
                }

                //restore full value once is decremented to zero;
                this._auxEnumCondition = this._enumConditions;

                //restore initial value
                this._conditionchange = false;
            }
        }

        //save the trigger function
        trigger.updateTriggerDisability = updateTriggerDisability;

        vm.bind({
            bindTo: '{theObject}',
            deep: true
        }, function (theObject) {
            // apply visibility function
            Ext.callback(updateTriggerDisability, this, [theObject]);
        }, this);

    },

    /**
     * This function tells if the trigger condition is evaluated at least once
     * @param {String} triggerId  the trigger id
     * @returns {boolean} true if the trigger condition has been evaluated once
     */
    isTriggerConditionEvaluated: function (triggerId) {
        return !Ext.isEmpty(this._disabilityFromCondition[triggerId]);
    },

    /**
     *
     * @param {String} triggerId
     * @returns {Boolean} true if the condition is valid, false otherwise
     */
    isTriggerConditionValid: function (triggerId) {
        return !this._disabilityFromCondition[triggerId];
    },

    /**
     *
     * @param {CMDBuildUI.model.calendar.Trigger} trigger
     * @returns true if the trigger has a condition script, false otherwise;
     */
    hasCondition: function (trigger) {
        return Ext.isEmpty(trigger.get('conditionScript')) ? false : true;
    },

    /**
     *
     */
    disableTrigger: function () {
        const trigger = this.getTrigger('scheduler');
        if (trigger.el) {
            trigger.el.mask();
        }
    },

    /**
     *
     */
    enableTrigger: function () {
        const trigger = this.getTrigger('scheduler');
        if (trigger.el) {
            trigger.el.unmask();
        }
    },

    /**
     *
     */
    updateDisability: function () {
        if (this._disabledFromValue || this._disableFromCondition) {
            this.disableTrigger();
        } else {
            this.enableTrigger();
        }
    },

    /**
     * This function checks all the conditions
     * @returns true if one condition is not respected, false otherwise. When false the trigger is disabled
     */
    _updateDisabilityFromcondition: function () {
        for (var trId in this._disabilityFromCondition) {
            if (this._disabilityFromCondition[trId] == false) {
                this._disableFromCondition = false;
                return;
            }
        }
        this._disableFromCondition = true;
    },

    /**
     *
     * @param {Mixed} value
     * @return {Boolean}
     */
    validator: function (value) {
        if (this.generateEventRequests > 0) {
            return CMDBuildUI.locales.Locales.calendar.waiteventgeneration;
        }
        return true;
    }
});