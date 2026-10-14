Ext.define('CMDBuildUI.view.fields.schedulerdate.SequenceFieldSetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-schedulerdate-sequencefieldset',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view, eOpts) {
        const me = this;
        const vm = this.getViewModel();

        vm.bind(
            '{theSequence}',
            function (theSequence) {
                let readonly;

                switch (theSequence.get('sequenceParamsEditMode')) {
                    case CMDBuildUI.model.calendar.Sequence.sequenceParamsEditMode.read:
                        readonly = true;
                        break;

                    case CMDBuildUI.model.calendar.Sequence.sequenceParamsEditMode.write:
                        readonly = false;
                        break;
                    default:
                        CMDBuildUI.util.Notifier.showErrorMessage("Can't recognize sequenceParamsEditMode value");
                        break;
                }

                const model = Ext.ClassManager.get('CMDBuildUI.model.calendar.Sequence'),
                    form = CMDBuildUI.util.helper.FormHelper.renderForm(model, {
                        readonly: readonly,
                        linkName: 'theSequence',
                        layout: CMDBuildUI.model.calendar.Sequence.getFormLayout()
                    });

                //special function for the endType lookup
                if (!readonly) {
                    me.formManipulation(form);
                }
                view.down('#form').insert(0, form);
            },
            view,
            {
                single: true
            }
        );
    },

    /**
     *
     */
    recalculateEvents: function () {
        const vm = this.getViewModel();
        const theSequence = vm.get('theSequence');
        const data = theSequence.getData();

        if (Ext.isEmpty(data.notifications___0___template)) {
            delete data.notifications___0___template;
            delete data.notifications___0___delay;
            delete data.notifications___0___content;
        }

        const firstEvent = data.firstEvent;
        const lastEvent = data.lastEvent;
        data.firstEvent = Ext.isDate(firstEvent)
            ? new Date(firstEvent.getTime() - firstEvent.getTimezoneOffset() * 60000)
            : firstEvent;
        data.lastEvent = Ext.isDate(lastEvent)
            ? new Date(lastEvent.getTime() - lastEvent.getTimezoneOffset() * 60000)
            : lastEvent;

        Ext.Ajax.request({
            method: 'POST',
            jsonData: data,
            url: Ext.String.format('{0}/calendar/sequences/_ANY/generate-events', CMDBuildUI.util.Config.baseUrl),
            callback: function (options, success, response) {
                const eventsData = JSON.parse(response.responseText).data;
                //sets the _dirty_recalculate so that the dirty propery can't change no more
                theSequence._dirty_recalculate = true;
                theSequence.events().setData(eventsData);
            }
        });
    },

    /**
     *
     * @param {*} button
     * @param {*} eOpts
     */
    onAddButtonClick: function (button, eOpts) {
        const view = this.getView();
        const vm = view.lookupViewModel();
        const sequence = vm.get('theSequence');
        //generate the new event from the sequence (client side)
        const newEvent = this.generateEvent(sequence);
        const popup = CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.calendar.sequencepaneltitle,
            {
                xtype: 'events-event-create',
                controller: 'fields-schedulerdate-create',
                listeners: {
                    popupclose: function () {
                        popup.close();
                    },
                    popupsave: function (theEvent) {
                        const events = sequence.events();
                        events.add(theEvent);
                        sequence._dirty_create = true;
                        popup.close();
                    }
                },
                viewModel: {
                    data: {
                        theEvent: newEvent
                    }
                }
            }
        );
    },

    privates: {
        /**
         *
         * @param {CMDBuildUI.model.calendar.Sequence} sequence
         * @returns {CMDBuildUI.model.calendar.Event}
         */
        generateEvent: function (sequence) {
            return Ext.create('CMDBuildUI.model.calendar.Event', {
                type: sequence.get('eventType'),
                source: CMDBuildUI.model.calendar.Sequence.source.system,
                begin: sequence.get('firstEvent'),
                date: sequence.get('firstEvent'),
                end: sequence.get('firstEvent'),
                timeZone: sequence.get('timeZone'),
                card: sequence.get('card'),
                description: sequence.get('description'),
                eventEditMode: sequence.get('eventEditMode'),
                job: sequence.get('job'),
                onCardDeleteAction: sequence.get('onCardDeleteAction'),
                status: CMDBuildUI.model.calendar.Event.status.active,
                participants: sequence.get('participants'),
                _participant_users: sequence.get('_participant_users'),
                _participant_groups: sequence.get('_participant_groups'),
                category: sequence.get('category'),
                priority: sequence.get('priority'),

                notifications___0____id: sequence.get('notifications___0____id'),
                notifications___0___template: sequence.get('notifications___0___template'),
                notifications___0___content: sequence.get('notifications___0___content'),
                notifications___0___delay: sequence.get('notifications___0___delay'),

                _can_write: true
                // _notification_reports: sequence.get('_notification_reports'),
                // _notification__report: sequence.get('_notification__report'),
                // _notification__content_preview: sequence.get('_notification__content_preview')
            });
        },

        /**
         *
         * @param {*} form
         */
        formManipulation: function (form) {
            const lookupfield = form.items[0].items[2].items[0].items[0];

            lookupfield.controller = {
                type: 'fields-lookupfield',
                control: {
                    '#combo0': {
                        beforerender: function (combobox, eOpts) {
                            combobox.onStoreLoaded = function (store, records, successful, operation, eOpts) {
                                const filtered = records.filter(function (value, index, records) {
                                    if (value.get('code') != 'auto') {
                                        return true;
                                    }
                                });
                                store.setData(filtered);
                            };
                        }
                    }
                }
            };
        }
    }
});
