Ext.define('CMDBuildUI.view.notes.Panel', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.notes.PanelController',
        'CMDBuildUI.view.notes.PanelModel'
    ],

    alias: 'widget.notes-panel',
    controller: 'notes-panel',
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

    publishes: ['editMode'],
    bind: {
        editMode: '{editmode}'
    },
    layout: 'fit',
    autoScroll: true,

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.notes.edit,
        itemId: 'editbtn',
        ui: 'management-primary-small',
        hidden: true,
        disabled: true,
        bind: {
            hidden: '{hiddenbtns.edit}',
            disabled: '{!basepermissions.edit}'
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
            hidden: true,
            bind: {
                value: '{theObject.Notes}',
                hidden: '{!editmode}'
            }
        }), {
            xtype: 'panel',
            scrollable: true,
            cls: 'x-selectable',
            hidden: true,
            bind: {
                html: '{theObject.Notes}',
                hidden: '{editmode}'
            }
        }
    ],

    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        itemId: 'cancelbtn',
        ui: 'secondary-action-small',
        hidden: true,
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
        hidden: true,
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