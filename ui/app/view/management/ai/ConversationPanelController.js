Ext.define('CMDBuildUI.view.management.ai.ConversationPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.management-ai-conversation',

    restoreAllActions: function () {
        const actions = this.getView().getEl().dom.querySelectorAll('.ai-message-action-sent');
        actions.forEach(function (action) {
            action.disabled = false;
            action.classList.remove('ai-message-action-sent');
            action.querySelector('span').classList.replace('fa-check-circle', 'fa-file-import');
        });
    }
});
