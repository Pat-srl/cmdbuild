/**
 * @file CMDBuildUI.view.widgets.notewidget
 * @module CMDBuildUI.view.widgets.notewidget
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.view.widgets.notewidget.Panel', {
    extend: 'Ext.form.Panel',

    requires: ['CMDBuildUI.view.widgets.notewidget.PanelController'],
    mixins: ['CMDBuildUI.view.widgets.Mixin'],

    alias: 'widget.widgets-notewidget-panel',
    controller: 'widgets-notewidget-panel',
    viewModel: {
        data: {
            editmode: false,
            notes: null
        }
    },

    /**
     * @constant {Boolean} Inline
     * If True show the widget inline.
     */
    Inline: false,

    /**
     * @constant {Boolean} Required
     * If True this widget is mandatory.
     */
    Required: false,

    layout: 'fit',
    bodyPadding: 15,

    defaults: {
        textAlign: 'left',
        scrollable: true,
        border: false
    },

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{theWidget._inline}'
            },
            items: [
                {
                    xtype: 'tbfill'
                },
                {
                    text: CMDBuildUI.locales.Locales.common.actions.cancel,
                    itemId: 'cancelbtn',
                    ui: 'secondary-action-small',
                    hidden: true,
                    bind: {
                        hidden: '{!editmode}'
                    },
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
                    },
                    autoEl: {
                        'data-testid': 'widgets-notewidget-cancel'
                    }
                },
                {
                    ui: 'secondary-action-small',
                    itemId: 'closebtn',
                    hidden: true,
                    text: CMDBuildUI.locales.Locales.common.actions.close,
                    bind: {
                        hidden: '{editmode}'
                    },
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.close'
                    },
                    autoEl: {
                        'data-testid': 'widgets-notewidget-close'
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.common.actions.save,
                    itemId: 'savebtn',
                    ui: 'management-primary-small',
                    formBind: true, //only enabled once the form is valid
                    disabled: true,
                    hidden: true,
                    bind: {
                        hidden: '{!editmode}'
                    },
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.save'
                    },
                    autoEl: {
                        'data-testid': 'widgets-notewidget-save'
                    }
                }
            ]
        }
    ]
});
