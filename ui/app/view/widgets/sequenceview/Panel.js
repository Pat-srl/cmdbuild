/**
 * @file CMDBuildUI.view.widgets.sequenceview
 * @module CMDBuildUI.view.widgets.sequenceview
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.view.widgets.sequenceview.Panel', {
    extend: 'Ext.form.Panel',

    requires: ['CMDBuildUI.view.widgets.sequenceview.PanelController'],

    alias: 'widget.widgets-sequenceview-panel',
    controller: 'widgets-sequenceview-panel',
    viewModel: {},

    mixins: ['CMDBuildUI.view.widgets.Mixin'],

    /**
     * @constant {String} SequenceId
     * The sequence id to link the sequence of the widget.
     */
    SequenceId: null,

    layout: 'fit',
    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,

    fbar: [
        {
            xtype: 'button',
            ui: 'secondary-action',
            itemId: 'closebtn',
            text: CMDBuildUI.locales.Locales.common.actions.close,
            localized: {
                text: 'CMDBuildUI.locales.Locales.common.actions.close'
            },
            autoEl: {
                'data-testid': 'widgets-sequenceview-close'
            }
        }
    ]
});
