/**
 * @file CMDBuildUI.view.widgets.aiassistant
 * @module CMDBuildUI.view.widgets.aiassistant
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.view.widgets.aiassistant.Panel', {
    extend: 'Ext.panel.Panel',

    requires: ['CMDBuildUI.view.widgets.aiassistant.PanelController', 'CMDBuildUI.view.widgets.aiassistant.Chat'],

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,

    mixins: ['CMDBuildUI.view.widgets.Mixin'],

    alias: 'widget.widgets-aiassistant-panel',
    controller: 'widgets-aiassistant-panel',
    viewModel: {
        type: 'widgets-aiassistant-panel'
    },

    statics: {
        widgetDefaults: {
            Prompt: '',
            PromptAuto: false,
            PromptLabel: 'PromptLabel is missing',
            PromptAttributeToCopy: null,
            PromptContext: null
        },

        getWidgetValue: function (widget, name) {
            const value = widget && Ext.isFunction(widget.get) ? widget.get(name) : undefined;
            if (value !== undefined && value !== null) {
                return value;
            }

            return this.widgetDefaults[name];
        }
    },

    /**
     * @constant {Boolean} Inline
     * If True show the widget inline.
     */
    Inline: false,
    // ---------------- PROMPT -------------------------
    /**
     * @constant {String} Prompt
     * The initial prompt for the AI assistant.
     */
    Prompt: '',
    /**
     * @constant {Boolean} PromptAuto
     * Whether to automatically use the initial prompt.
     */
    PromptAuto: false,
    /**
     * @constant {String} PromptLabel
     * Label for the initial prompt.
     */
    PromptLabel: 'Define a PromptLabel(Widget)',
    /**
     * @constant {String} PromptAttributeToCopy
     * Attribute to which the initial prompt can be copied.
     */
    PromptAttributeToCopy: null,
    /**
     * @constant {Boolean} PromptEnableChat
     * Whether to enable chat for the initial prompt.
     */
    PromptEnableChat: true,
    /**
     * @constant {String} PromptContext
     * Context information for the initial prompt.
     */
    PromptContext: null,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            hidden: true,
            ui: 'footer',
            enableFocusableContainer: false,
            bind: {
                hidden: '{theWidget._inline}'
            }
        }
    ]
});
