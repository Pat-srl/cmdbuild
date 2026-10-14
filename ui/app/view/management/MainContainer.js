Ext.define('CMDBuildUI.view.management.MainContainer', {
    extend: 'Ext.container.Container',

    requires: ['CMDBuildUI.view.management.Content'],

    xtype: 'management-maincontainer',
    layout: 'border',

    items: [
        {
            xtype: 'management-navigation-container',
            split: true, // enable resizing
            region: 'west',
            collapsible: true, // make collapsible
            split: {
                ui: 'management',
                size: 5
            }
        },
        {
            region: 'center', // center region is required, no width/height specified
            xtype: 'management-content'
        },
        {
            xtype: 'management-ai-chat',
            split: true, // enable resizing
            region: 'east',
            hidden: true,
            width: '20%',
            bind: {
                hidden: '{hideChatBot}'
            },
            split: {
                cls: 'aiChat',
                ui: 'management',
                size: 2
            }
        }
    ]
});
