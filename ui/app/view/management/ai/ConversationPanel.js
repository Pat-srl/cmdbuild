Ext.define('CMDBuildUI.view.management.ai.ConversationPanel', {
    extend: 'Ext.view.View',
    alias: 'widget.management-ai-conversation',
    controller: 'management-ai-conversation',

    requires: ['CMDBuildUI.view.management.ai.ConversationPanelController'],

    cls: Ext.baseCSSPrefix + 'chat-conversation',
    scrollable: true,
    disableSelection: true,

    bind: {
        store: '{messagesStore}'
    },

    tpl: [
        '<div class="{[Ext.baseCSSPrefix]}conversation-container">',
        '<tpl for=".">',
        '<div class="{[Ext.baseCSSPrefix]}conversation-message {[Ext.baseCSSPrefix]}conversation-message-{type}">',
        '<tpl if="type == \'loading\'">',
        '<div class="ai-typing-indicator" role="status" aria-label="AI is typing">',
        '<span class="x-img fa-2x x-column x-img-default cmdbuildicon-sparkles" aria-hidden="true"></span>',
        '<span class="ai-typing-bubble">',
        '<span class="ai-typing-dot"></span>',
        '<span class="ai-typing-dot"></span>',
        '<span class="ai-typing-dot"></span>',
        '</span>',
        '</div>',
        '</tpl>',
        '<tpl if="type != \'loading\'">',
        '<div class="{[Ext.baseCSSPrefix]}conversation-message-content">',
        '<tpl if="type == \'ai\'">',
        '<p><span class="x-img fa-2x x-column x-img-default cmdbuildicon-sparkles"></span><strong><em>{[CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ai.chatname)]}</em></strong></p>',
        '</tpl>',
        '<span>{message}</span>',
        '<tpl if="type == \'ai\'">',
        '<div class="ai-message-actions">',
        '<button type="button" class="ai-message-action ai-message-copy-action" title="{[CMDBuildUI.locales.Locales.common.actions.copy]}">',
        '<span class="x-fas fa-copy" aria-hidden="true"></span>',
        '</button>',
        '<tpl if="showSendAction">',
        '<button type="button" class="ai-message-action ai-message-send-action" title="{[CMDBuildUI.locales.Locales.widgets.ai.copytoattribute]}">',
        '<span class="x-fas fa-file-import" aria-hidden="true"></span>',
        '</button>',
        '</tpl>',
        '</div>',
        '</tpl>',
        '<tpl if="resources && resources.length">',
        '<p><strong>{[CMDBuildUI.locales.Locales.ai.resources]}</strong></p>',
        '<ul class="resources">',
        '<tpl for="resources">',
        '<tpl if="url">',
        '<li class="document">{label} - <u class="link-url" data-url="{url}">{[CMDBuildUI.locales.Locales.ai.viewresource]}</u></li>',
        '<tpl elseif="documentId">',
        '<li class="document" data-id="{_id}" data-type="{_type}">{fileName} - <u class="link-attachment" data-documentid="{documentId}"  data-filename="{fileName}" data-mimetype="{mimeType}">{[CMDBuildUI.locales.Locales.attachments.fileview]}</u> - <u class="link-card">{[CMDBuildUI.locales.Locales.ai.viewrelatedcard]}</u></li>',
        '<tpl else>',
        '<li class="document" data-id="{_id}" data-type="{_type}">{description} - <u class="link-card">{[CMDBuildUI.locales.Locales.ai.viewrelatedcard]}</u></li>',
        '</tpl>',
        '</tpl>',
        '</ul>',
        '</tpl>',
        '</div>',
        '</tpl>',
        '</div>',
        '</tpl>',
        '</div>'
    ],

    itemSelector: '.' + Ext.baseCSSPrefix + 'conversation-message',

    listeners: {
        afterrender: function (view) {
            view.getEl().on(
                'click',
                function (e) {
                    const url = e.target.getAttribute('data-url');
                    view.fireEvent('openurl', url);
                },
                null,
                {
                    delegate: '.link-url'
                }
            );
            view.getEl().on(
                'click',
                function (e) {
                    const target = e.target;
                    const parentNode = target.parentNode;
                    const type = parentNode.getAttribute('data-type');
                    const id = parentNode.getAttribute('data-id');
                    const documentId = target.getAttribute('data-documentid');
                    const fileName = target.getAttribute('data-filename');
                    const mimeType = target.getAttribute('data-mimetype');
                    view.fireEvent('viewattachment', type, id, documentId, fileName, mimeType);
                },
                null,
                {
                    delegate: '.link-attachment'
                }
            );
            view.getEl().on(
                'click',
                function (e) {
                    const parentNode = e.target.parentNode;
                    const type = parentNode.getAttribute('data-type');
                    const id = parentNode.getAttribute('data-id');
                    view.fireEvent('viewcard', type, id);
                },
                null,
                {
                    delegate: '.link-card'
                }
            );
            view.getEl().on(
                'click',
                function (e) {
                    const action = e.getTarget('.ai-message-action');
                    const record = view.getRecord(e.getTarget(view.itemSelector));
                    if (record) {
                        view.fireEvent(
                            action.classList.contains('ai-message-copy-action') ? 'copyanswer' : 'sendanswer',
                            record,
                            action
                        );
                    }
                },
                null,
                {
                    delegate: '.ai-message-action'
                }
            );
        }
    }
});
