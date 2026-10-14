Ext.define('CMDBuildUI.view.events.event.EditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.events-event-edit',

    data: {
        theEvent: undefined
    },

    formulas: {
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
