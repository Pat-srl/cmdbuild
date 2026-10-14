
Ext.define('CMDBuildUI.view.events.notes.Notes', {
    extend: 'Ext.form.Panel',
    alias: 'widget.events-notes-notes',
    requires: [
        'CMDBuildUI.view.events.notes.NotesController'
    ],

    controller: 'events-notes-notes',
    viewModel: {
        type: 'notes-panel'
    },

    modelValidation: true,

    config: {
        /**
         * @cfg {Boolean} editMode
         */
        editMode: false,

        /**
         * @cfg {readOnly}
         *
         * Set to `true` to shwow details tabs in read-only mode.
         */
        readOnly: false
    },

    layout: 'fit',

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.notes.edit,
        itemId: 'editbtn',
        ui: 'management-primary-small',
        hidden: true,
        disabled: true,
        bind: {
            hidden: '{hiddenbtns.edit}',
            disabled: '{!permissions.edit}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.notes.edit'
        },
        autoEl: {
            'data-testid': 'notes-panel-editbtn'
        }
    }],

    items: [
        CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
            flex: 1,
            bind: {
                value: '{theEvent.notes}',
                hidden: '{!editmode}'
            }
        }), {
            xtype: 'panel',
            scrollable: true,
            cls: 'x-selectable',
            bind: {
                html: '{theEvent.notes}',
                hidden: '{editmode}'
            }
        }
    ],

    buttons: [ {
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        itemId: 'cancelbtn',
        ui: 'secondary-action-small',
        bind: {
            hidden: '{hiddenbtns.cancel}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        },
        autoEl: {
            'data-testid': 'notes-panel-cancelbtn'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.save,
        itemId: 'savebtn',
        ui: 'management-primary-small',
        formBind: true, //only enabled once the form is valid
        disabled: true,
        bind: {
            hidden: '{hiddenbtns.save}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        },
        autoEl: {
            'data-testid': 'notes-panel-savebtn'
        }
    }]
});
