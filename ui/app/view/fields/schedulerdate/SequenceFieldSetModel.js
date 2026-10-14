Ext.define('CMDBuildUI.view.fields.schedulerdate.SequenceFieldSetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.fields-schedulerdate-sequencefieldset',

    data: {
        theSequence: null,
        theTrigger: null
    },

    formulas: {
        recalculateHidden: {
            bind: {
                editMode: '{theSequence.sequenceParamsEditMode}'
            },
            get: function (data) {
                if (data.editMode == CMDBuildUI.model.calendar.Sequence.sequenceParamsEditMode.write) {
                    return false;
                }

                return true;
            }
        },

        addHidden: {
            bind: {
                editMode: '{theSequence.eventEditMode}'
            },
            get: function (data) {
                if (data.editMode == CMDBuildUI.model.calendar.Sequence.sequenceParamsEditMode.write) {
                    return false;
                }

                return true;
            }
        },

        gridHidden: {
            bind: '{theTrigger.eventEditMode}',
            get: function (showGrid) {
                return showGrid === 'hidden';
            }
        },

        endTypeUpdate: {
            bind: {
                frequency: '{theSequence.frequency}',
                theSequence: '{theSequence}'
            },
            get: function (data) {
                if (data.frequency == CMDBuildUI.model.calendar.Trigger.calendarFrequencies.once) {
                    data.theSequence.set('endType', null);
                    data.theSequence.set('endType', CMDBuildUI.model.calendar.Trigger.calendarEndTypes.other);
                }
            }
        },

        /**
         * This is needed because the 'other' configuration is not set always
         */
        sequenceChange: {
            bind: {
                endType: '{theSequence.endType}',
                theSequence: '{theSequence}'
            },
            get: function (data) {
                if (Ext.isEmpty(data.endType)) {
                    data.theSequence.set('endType', CMDBuildUI.model.calendar.Trigger.calendarEndTypes.other);
                }
            }
        }
    }
});
