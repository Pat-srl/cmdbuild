Ext.define('CMDBuildUI.util.administration.Utilities', {
    requires: [
        'CMDBuildUI.util.Notifier'
    ],

    singleton: true,

    /**
     * Show message when the active property of model is changed
     * @param {Ext.data.Model} record 
     * @param {String} [message] optional full message
     */
    showToggleActiveMessage: function (record, message) {
        var valueString = record.get('active') ? CMDBuildUI.locales.Locales.administration.common.messages.enabled : CMDBuildUI.locales.Locales.administration.common.messages.disabled;

        new Ext.util.DelayedTask(function () {
            message = message || Ext.String.format('{0} {1} {2}.',
                record.get('description'),
                CMDBuildUI.locales.Locales.administration.common.messages.was,
                valueString.toLowerCase());

            CMDBuildUI.util.Notifier.showSuccessMessage(message, null, 'administration');
        }).delay(350);
    }
});