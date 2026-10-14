Ext.define('CMDBuildUI.view.events.event.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.events-event-view',

    data: {
        title: null,
        schedulerFieldPopup: false
    },

    formulas: {
        updatePermissions: {
            bind: {
                can_write: '{theEvent._can_write}',
                schedulerFieldPopup: '{schedulerFieldPopup}'
            },
            get: function (data) {
                if (data.schedulerFieldPopup) {
                    this.set("hiddentools", {
                        edit: false,
                        delete: false,
                        open: false
                    });
                }
                this.set("permissions", {
                    delete: data.can_write,
                    edit: data.can_write
                });
            }
        },

        theObject: {
            bind: '{theEvent}',
            get: function (object) {
                return object;
            }
        },

        updateCounter: {
            bind: {
                attachment_count: '{theEvent._attachment_count}',
                email_count: '{theEvent._email_count}',
                notes_count: '{theEvent.notes}'
            },
            get: function (data) {
                this.set('tabcounters.attachments', data._attachment_count);
                this.set('tabcounters.emails', data._email_count);
                this.set('tabcounters.notes', CMDBuildUI.util.Utilities.extractTextFromHTML(data.notes_count || null));
            }
        }
    }

});
