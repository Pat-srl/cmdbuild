/**
 * @file CMDBuildUI.view.widgets.createmodifycard
 * @module CMDBuildUI.view.widgets.createmodifycard
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.view.widgets.createmodifycard.Panel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.widgets.createmodifycard.PanelController',
        'CMDBuildUI.view.widgets.createmodifycard.PanelModel'
    ],

    mixins: ['CMDBuildUI.view.widgets.Mixin', 'CMDBuildUI.mixins.grids.AddButtonMixin'],

    alias: 'widget.widgets-createmodifycard-panel',
    controller: 'widgets-createmodifycard-panel',
    viewModel: {
        type: 'widgets-createmodifycard-panel'
    },

    config: {
        form: null
    },

    /**
     * @constant {Boolean} ReadOnly
     * If True disables create/modify.
     */
    ReadOnly: false,

    /**
     * @constant {String} ClassName
     * Class name to select the card preset from.
     */
    ClassName: null,

    /**
     * @constant {String} ObjId
     * Card Id to modify.
     */
    ObjId: null,

    /**
     * @constant {String} Reference
     * Attribute name reference of the card to modify.
     */
    Reference: null,

    /**
     * @constant {Boolean} Required
     * If True this widget is mandatory.
     */
    Required: null,

    layout: 'fit',
    autoScroll: true,

    tbar: [
        {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.classes.cards.addcard,
            reference: 'addcardbtn',
            itemId: 'addcardbtn',
            ui: 'management-primary',
            disabled: true,
            autoEl: {
                'data-testid': 'selection-popup-addcardbtn'
            },
            bind: {
                text: '{addbtn.text}',
                disabled: '{addbtn.disabled}',
                hidden: '{addbtn.hidden}'
            }
        }
    ],

    /**
     * Removes the form in configuration
     * @ignore
     */
    removeForm: function () {
        const form = this.getForm();

        if (form.isComponent) {
            form.destroy();
            this.setForm(null);
        }
    }
});
